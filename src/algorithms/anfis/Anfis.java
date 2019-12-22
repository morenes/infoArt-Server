package algorithms.anfis;
import java.io.*;
import java.util.Arrays;
import models.tskModel.*;
import utility.Error;
import models.ConsequentType;
import models.FiringType;
import models.MembershipFunctionType;

/**
 * Clase encargada de implementar el algoritmo de redes neuronales ANFIS.
 *
 * @author Mercedes Valdes Vela & Fernando Terroso Saenz
 */
public class Anfis {
    
    static public byte L_INPUT=0, L_MF=1, L_TAU=2, L_NORMTAU=3, L_FI=4, L_F=5;
    
    protected MembershipFunctionType membership_type;
    protected int numVar, numRule,numMF;
    protected int numConsequent;
    protected int epochs;

    /**
     * Antecedent parameters
     */
    protected double param_a[][], param_b[][], param_c[][];
    /**
     * Consequent parameters
     */
    protected double param_p[][];
    /**
     * Layers 0, 2, 3, 4
     */
    protected double x[], tau[], normTau[], fi[];
    /**
     * Layer 1
     */
    protected double mf[][];
    /**
     * Layer 5
     */
    protected double f;
    /**
     * Error
     */
    protected double error;
    protected double percentageMatches;
    //private byte consequentEstimationType;
    //private LeastSquareEstimator lse;
    //if consequentEstimationType is GLOBAL_LSE then thsi will be a RecursiveLSEEstimator else it will be a LocalLSEEstimator
    protected RecursiveLSEEstimator lse;
    /**
     * Step Size determinator
     */
    protected StepSize stepSize;
    
    /**
     * Order for the consecuent function
     */
    protected ConsequentType order;

    protected FiringType firingType;
    
    /**
     * Atributos de entrada de los ejemplos de entrenamiento
     */
    protected double mx[][];
    /**
     * Salidas deseadas para cada ejemplo de entrenamiento
     */
    protected double vy[];
    /**
     * Nombre de la clase
     */
    protected String className=this.getClass().getName();    
    protected TSKModel tskModel;
    /**
     * 
     * @param tskModel 
     * @param inputData 
     * @param outputData 
     * @param epochs 
     * @param p 
     * @param q 
     * @param kappa 
     * @param n 
     * @param m 
     */
    public Anfis(){}


    public Anfis(
            double[][] pParam_a,
            double[][] pParam_b,
            double[][] pParam_c,
            double[][] pParam_p,
            double[][] pInputData,
            double[] pOutputData,
            FiringType pFiringType,
            MembershipFunctionType pFsetShape,
            AnfisParams pAnfisParams){

        mx = pInputData;
        vy = pOutputData;

        numVar = pParam_a[0].length;
        numRule = pParam_a.length;
        numMF = pParam_a.length;

        param_a= pParam_a;
        param_c= pParam_c;

        param_p= pParam_p;
        if(param_p == null){
            param_p= new double[numRule][numVar+1];
        }

        param_b= pParam_b;
        if (param_b==null){
            param_b= new double[numRule][numVar];
        }

        create(pFiringType, pFsetShape, pAnfisParams);
    }

    public Anfis(
            TSKModel tskModel,
            double[][] inputData,
            double[] outputData,
            AnfisParams anfisParams) {

        try {

            this.tskModel=tskModel;

            numVar=tskModel.getNumAttributes();
            numRule=tskModel.getNumRules();
            numMF=tskModel.getNumRules();

            param_p=tskModel.getParam_p();
            param_a=tskModel.getParam_a();
            param_b=tskModel.getParam_b();
            if (param_b==null)
                param_b=new double[numRule][numVar];
            param_c=tskModel.getParam_c();

            mx=inputData;
            vy=outputData;

            this.numConsequent= tskModel.getNumConsecuents();

            // Anfis debe tener el mismo tipo de consecuente del TSK que se
            // le pasa como parametro
            anfisParams.setConsequentType(tskModel.getConsequentType());

            create(tskModel.getFiringType(), tskModel.getFsetShape(), anfisParams);

        } catch(Exception e){
            System.err.println("Anfis: Se ha producido al crear la instancia Anfis. " + e.getMessage());
            e.printStackTrace();
        }
    }    
    /**
     * 
     * @param name 
     * @param data_number 
     * @param input_number 
     * @param consequentType 
     * @param firingType 
     * @param fsetShape 
     * @param epochs 
     * @param numDesiredRules 
     * @param p 
     * @param q 
     * @param kappa 
     * @param n 
     * @param m 
     */
    public Anfis(
            String name,
            int data_number,
            int input_number,
            FiringType firingType,
            MembershipFunctionType fsetShape,
            int numDesiredRules,
            AnfisParams params){
     try{

        read_data_from_file(name,data_number,input_number);

        numVar=input_number-1;
        numRule=numDesiredRules;
        numMF=numDesiredRules;

        param_p=new double[numMF][numVar+1];
        param_a=new double[numMF][numVar];
        param_b=new double[numMF][numVar];
        param_c=new double[numMF][numVar];

        create(firingType,fsetShape,params);

        init();
     }   
     catch(FileNotFoundException e) {
            System.out.println("File not found "+ e.getMessage());
            e.printStackTrace();
     }
     catch(IOException e) {
            System.out.println("IO Exception " + e.getMessage());
            e.printStackTrace();
     } 
    }

    /**
     * Metodo que se encarga de inicializar los campos comunes a todos los
     * constructores
     * @param consequentType Tipo de consecuente (LINEAL|SINGLETON)
     * @param firingType Tipo de encendido de las reglas
     * @param fsetShape Tipo de funcion de pertenencia
     * @param params Parametros para el lse (m, n, p, q)
     */
    protected void create(
            FiringType firingType,
            MembershipFunctionType fsetShape,
            AnfisParams params) {

        try{

            epochs = params.getEpochs();

            membership_type = fsetShape;
            order=params.getConsequentType();
            this.firingType = firingType;
            
            if((membership_type.equals(MembershipFunctionType.TRIANGULAR)) &&
                    (membership_type.equals(MembershipFunctionType.TRAPEZOIDAL))){

                throw new AnfisException("El tipo de conjunto difuso es incorrecto");
            }

            stepSize=new StepSize(params.getKappa(), params.getM(), params.getN(), params.getP(), params.getQ());
            lse=new RecursiveLSEEstimator(numRule * (numVar + 1),1);
            mf= new double[numRule][numVar];
            x=new double[numVar];
            tau=new double[numRule];
            normTau=new double [numRule];
            fi=new double[numRule];
            f = -1;
        }
        catch(Exception e){
            System.out.println("Anfis: Se ha producido un error al inicializar los campos basicos de ANFIS.");
            e.printStackTrace();
        }
    }
    //inicializa los valores de a, b,y con valores medios
    protected void init(){
            int i,j,k;
            //normalizacion
            double min[]=new double[numVar];
            double max[]=new double[numVar];
            double min_j,max_j;
            
            for (j=0; j<numVar; j++) {
                min_j=mx[0][j];
                max_j=mx[0][j];
                for (i=0; i<mx.length; i++) {
                    if (mx[i][j]<min_j) min_j=mx[i][j];
                    if (mx[i][j]>max_j) max_j=mx[i][j];
                }
                min[j]=min_j;
                max[j]=max_j;
            }
            for (i=0; i<numMF; i++)
                for (j=0; j<numVar; j++) {
                if (numMF>1)
                    setParamAValue(i, j, (max[j]-min[j])/(2*numMF-2));
                else
                    setParamAValue(i, j, max[j]-min[j]);
                double aux = getParamAValue(i,j);
                setParamCValue(i, j,min[j]+2*aux*i);

                if(membership_type.equals(MembershipFunctionType.BELL))
                    setParamBValue(i, j, 2);
                }
    }
 
     /**
     * 
     * @throws algorithms.anfis.AnfisException 
     * @return 
     */
    public TSKModel createTSKModel() throws AnfisException{
        try {
            String id = "ANFIS";                   
            
            if(tskModel != null){
                id = id+"_"+tskModel.getPartialModelIdentifier();
            }
            
            switch(membership_type){
                case GAUSSIAN:
                    return new GaussianTSKModel(id,numVar,order, firingType, get_param_a(), get_param_c(), get_param_p());
                case BELL:
                    return new BellTSKModel(id,numVar,order, firingType,get_param_a(), get_param_b(), get_param_c(),get_param_p());
                default:
                    return null;
            }

        } catch (Exception e) {
            throw new AnfisException(className+".createTSKModel: Se ha producido un error al crear el TSKModel correspondiente a la ejecución de ANFIS.");
        }
    }
    /**
     * 
     * @param name 
     * @param data_number 
     * @param input_number 
     * @throws java.io.FileNotFoundException 
     * @throws java.io.IOException 
     */
    public void read_data_from_file(String name, int data_number, int input_number)
    throws FileNotFoundException, IOException {
        int i,j;
        StreamTokenizer file = new StreamTokenizer(new InputStreamReader(new FileInputStream(name)));
        mx=new double[data_number][input_number];
        vy=new double[data_number];
        
        for (i=0;i<data_number;i++){
            for (j=0;j<input_number;j++){
                file.nextToken();
                mx[i][j]=file.nval;
            }
            file.nextToken();
            vy[i]=file.nval;
        }
        
    }
    /**
     * 
     * @param e 
     */
    public void set_epochs(int e) {
        if (e<0) e=1000;
        epochs=e;//excepcion si es negativo
    }
    /**
     * 
     * @param kappa 
     * @param m 
     * @param n 
     * @param p 
     * @param q 
     */
    public void  set_step_policy(double kappa, int m, int n, double p, double q) {
        if ((kappa<0)||(m<0)||(n<0)||(p<0)||(q<0))  {kappa=0.1;m=3;n=3;p=0.25;q=0.25;}
        stepSize=new StepSize(kappa, m, n, p, q);
    }
    
    /**
     * 
     * @param lambda 
     */
    public void set_LSE_lambda(double lambda) {
        if ((lambda<0)||(lambda>1)) lambda=0.1;
        lse= new RecursiveLSEEstimator(numRule * (numVar + 1),lambda);
    }
    
    /**
     * 
     * @param o 
     */
    public void set_order(ConsequentType o){
        order=o;
    }
    
    /**
     * 
     * @param m 
     */
    public void set_membership_type(MembershipFunctionType m){
        membership_type=m;
    }
    
    /**
     * 
     * @param s 
     */
    public void setFiringType(FiringType s){
        firingType = s;
    }
    
    
    /**
     * 
     * @return 
     */
    public int get_epochs(){return epochs;}
    
    /**
     * 
     * @return 
     */
    public   StepSize  get_step_policy() { return stepSize;}
        
    /**
     * 
     * @return 
     */
    public ConsequentType get_order(){ return order;}
    
    /**
     * 
     * @return 
     */
    public MembershipFunctionType get_membership_type(){ return membership_type;}
    
    /**
     * 
     * @return 
     */
    public FiringType getFiringType(){ return firingType;}
    
    protected void calculate_mf(boolean p){
        int i,j;
        for (j=0; j<numVar; j++) {
            for (i=0; i<numMF; i++) {//coincide numMF con el numero de reglas

                if(membership_type.equals(MembershipFunctionType.GAUSSIAN)){
                    mf[i][j]=MF.gauss_MF(getParamAValue(i, j), getParamCValue(i,j),x[j]);
                }

                if(membership_type.equals(MembershipFunctionType.BELL))
                    mf[i][j]=MF.bell_MF(getParamAValue(i, j), getParamBValue(i, j), getParamCValue(i,j), x[j]);
            
            }
            
        }
    }
    
    protected void calculate_tau(boolean p) {
        int i,j;
        for (i=0; i<numRule; i++) {
            tau[i]=1;
            for (j=0; j<numVar; j++){
                tau[i]*= mf[i][j];
            }
        }
    }
    
    private void calculate_normTau(boolean p) {
        int    i;
        double den=0;
        for (i=0; i<numRule; i++)
            den+=tau[i];
        
        for (i=0; i<numRule; i++){
            normTau[i]=tau[i]/den;
        }
        
    }

    private void calculate_fi(boolean p) {
        int i,j;
        for (i=0; i<numRule; i++) {

            fi[i]=getParamPValue(i, numVar);
            for (j=0; j<numVar; j++){
               fi[i] += x[j] * getParamPValue(i, j);
            }
            fi[i]*= normTau[i];
        }
    }
    
    private void calculate_f(boolean p) {
        int i;
        f=0;
        for (i=0; i<numRule; i++)
            f+=fi[i];
                
    }
    
    /**
     * 
     * @param from 
     * @param to 
     * @param x0 
     */
    protected void calculate(byte from, byte to, double  x0[], boolean w) {
        switch (from) {            
            case 0: if (to>=Anfis.L_INPUT)   x=x0;                else break;
            case 1: if (to>=Anfis.L_MF)      calculate_mf(w);      else break;
            case 2: if (to>=Anfis.L_TAU)     calculate_tau(w);     else break;
            case 3: if (to>=Anfis.L_NORMTAU) calculate_normTau(w); else break;
            case 4: if (to>=Anfis.L_FI)      calculate_fi(w);      else break;
            case 5: if (to>=Anfis.L_F)       calculate_f(w);       break;
            
        }
    }
    
    protected void forward_pass() throws NullPointerException{
        
        if (mx==null || vy==null) throw (new NullPointerException());
        int p, i, j, numPat=mx.length;
        lse.reset();
        /* pass each param_p to the LSE estimator */
        double  lseParameters[]=new double [numRule*(numVar+1)];
        for (p=0; p<numPat; p++){
            calculate(Anfis.L_INPUT, Anfis.L_NORMTAU, mx[p], false);
            for (i=0; i<numRule; i++) {
                for (j=0; j<numVar; j++){
                    //TODO modificar para strongest rule
                    if((order.equals(ConsequentType.LINEAR))&&(j<numVar)){
                        lseParameters [i*(numVar+1)+j] = normTau[i]*mx[p][j];
                    }
                    else {
                        lseParameters [i*(numVar+1)+j] = 0;
                    }
                }

                lseParameters [i*(numVar+1)+numVar] = normTau[i];
            }
            ((RecursiveLSEEstimator) lse).addPattern(lseParameters, vy[p]);
        }
        
        /* update param_p by means of the LSE estimator */
        double new_p[] =  lse.estimated();
        for (i=0; i<numRule; i++){
            for (j=0; j<numVar+1; j++) {
                setParamPValue(i, j, new_p[i*(numVar+1)+j]);
            }
        }
    }
    
    protected void backward_pass() throws NullPointerException, AnfisException{
        
        if (mx==null || vy==null) throw new NullPointerException("Mx o Vy son nulos");
        
        int i,j,p, numPat=mx.length;
        double  dMF_da[][]=new double [numRule][numVar],
                dMF_db[][]=new double [numRule][numVar],
                dMF_dc[][]=new double [numRule][numVar];
        double delta_a[][]=new double [numRule][numVar],
                delta_b[][]=new double [numRule][numVar],
                delta_c[][]=new double [numRule][numVar];
        
        double anfis_output[]=new double [numPat];
        Error E=new Error(vy);
                        
        for (p=0; p<numPat; p++) {
            double y = vy[p];
            calculate(L_INPUT, L_F, mx[p], true);
            
            anfis_output[p]=f;
            
            for (i=0; i<numRule; i++){
                for (j=0; j<numVar; j++) {
                    if(membership_type.equals(MembershipFunctionType.GAUSSIAN)){
                        dMF_da[i][j]=MF.dGauss_over_a(getParamAValue(i, j),getParamCValue(i,j),mx[p][j]);
                        dMF_dc[i][j]=MF.dGauss_over_c(getParamAValue(i, j),getParamCValue(i,j),mx[p][j]);
                    }
                    
                    if(membership_type.equals(MembershipFunctionType.BELL)){

                        dMF_da[i][j]=MF.dBell_over_a(getParamAValue(i, j), getParamBValue(i, j), getParamCValue(i,j), mx[p][j]);
                        dMF_db[i][j]=MF.dBell_over_b(getParamAValue(i, j), getParamBValue(i, j), getParamCValue(i,j), mx[p][j]);
                        dMF_dc[i][j]=MF.dBell_over_c(getParamAValue(i, j), getParamBValue(i, j), getParamCValue(i,j), mx[p][j]);
                    }
                }
            }
         
            for (i=0; i<numRule; i++){
                for (j=0; j<numVar; j++) {
                    // Este factor es 0 si el número de cluster es 0, ya que la salida de la regla i-esima es igual
                    // a la suma de las salidas de todas las reglas(ya que solo hay una regla)
                    double factor = -2*(y-f)*(fi[i]-normTau[i]*f);

                    // Como el factor es cero si el numero de cluster es 1 los delta_a, delta_b y delta_c son 0
                    delta_a[i][j] += factor*dMF_da[i][j];
                    delta_b[i][j] += factor*dMF_db[i][j];
                    delta_c[i][j] += factor*dMF_dc[i][j];
                }
            }

        }
       
        double normAlfa=0;
        for (i=0; i<numRule; i++)
            for (j=0; j<numVar; j++)
                // Al ser todos los delta=0 (si el numero de cluster es 1) la normaAlfa es 0
                normAlfa+=Math.pow(delta_a[i][j],2)+Math.pow(delta_b[i][j],2)+Math.pow(delta_c[i][j],2);
        normAlfa=Math.sqrt(normAlfa);
        if (normAlfa==0) {
            throw new AnfisException("normaAlfa es igual a 0");
        }
        // Al ser la normaAlfa=0 si el numero de cluster=1 la division es infinito y FALLA
        double factor= stepSize.getStepSize() / normAlfa;
        for (i=0; i<numRule; i++){
            for(j=0; j<numVar; j++) {
                double aux = getParamAValue(i,j) - (factor*delta_a[i][j]);
                setParamAValue(i, j, aux);
                aux = getParamBValue(i,j) - (factor*delta_b[i][j]);
                setParamBValue(i, j, aux);
                aux = getParamCValue(i,j) - (factor*delta_c[i][j]);
                setParamCValue(i, j, aux);
            }
        }
        
        error=E.RMSE(anfis_output);
        percentageMatches = E.percentageOfMatches(anfis_output);
        stepSize.addError(error);      
    }


    public double evaluate(double x[]) {
        calculate(Anfis.L_INPUT, Anfis.L_F, x, false);
        return (f);
    }


    public double learn_epoch() throws AnfisException{

        System.out.println("Forward");
        forward_pass();
        System.out.println("Backward");
        backward_pass();
        
        return error;
    }
    

    public double check_epoch() {
        Error E=new Error(vy);
        double anfis_output[]=new double [vy.length];
        int p;
        for (p=0; p<vy.length; p++) {
            calculate(Anfis.L_INPUT, Anfis.L_F, mx[p], false);
            anfis_output[p]=f;
        }
        return (E.RMSE(anfis_output));
    }
    
    public double check_binary_epoch(double tol) {
        int p,correct=0;
        for (p=0; p<vy.length; p++) {
            calculate(Anfis.L_INPUT, Anfis.L_F, mx[p], false);
            if (Math.abs(vy[p]-f)<=tol) correct++;
        }
        return ((double)correct/vy.length);
    }
    
    public double make_learning() throws AnfisException{
        System.out.println("Iniciando ANFIS...");
        for(int i=0;i<epochs;i++){

            learn_epoch();

            StringBuilder st = new StringBuilder();
            st.append("Error RMSE en la etapa ");
            st.append(i);
            st.append(" con valor: ");
            st.append(error);
            System.out.println(st);
        }        
        return error;
    }

    protected void printAntecedentsAndConsequents(){
        System.out.println("\nReglas: (media, varianza)");

        for(int i= 0; i < param_a.length; i++){
            StringBuilder sb = new StringBuilder();
            sb.append(i);
            sb.append("   ");
            for(int j = 0; j< param_a[0].length; j++){
                sb.append("(");
                sb.append(Math.round(param_c[i][j]*Math.pow(10,3))/Math.pow(10,3));
                sb.append(", ");
                sb.append(Math.round(param_a[i][j]*Math.pow(10,3))/Math.pow(10,3));
                sb.append(")\t");
            }
            sb.append(Arrays.toString(param_p[i]));
            System.out.println(sb.toString());
        }

        System.out.println("");
    }
    
//para probar
    public void run() throws AnfisException{
        //Anfis fichero numExamples numVariables numRules
        for(int i=0;i<get_epochs();i++) {
            System.out.print("\n"+ i + "                 "+ (float)learn_epoch()+ "         -     ");
        }
        System.out.println("\n\n" + this);
    }


    @Override
    public String toString(){
        int i,j;
        String o="begin ANFIS\n";
        for (i=0; i<numRule; i++) {
            o=o+"R[" + i + "]: IF ";
            for (j=0; j<numVar; j++) {
                o=o+"(x" + j + " IS ";
                if(membership_type.equals(MembershipFunctionType.BELL))

                    o=o+"Bell{" + getParamAValue(i, j) + ", " +
                            getParamBValue(i, j) + ", " + getParamCValue(i,j) + "}";
                else if(membership_type.equals(MembershipFunctionType.GAUSSIAN))

                    o=o+"Gaussian{" + getParamAValue(i, j) + ", " + getParamCValue(i,j) + "}";
                else  o=o+"Error: unknown membership function";
                o=o+ ")";
                if (j!= numVar-1) o=o+ " AND "; else o=o+"\n";
            }
            o=o+ "       THEN f" + i + " = ";
            for (j=0; j<numVar; j++)
                if (getParamPValue(i, j)!=0.0)
                    o=o+ getParamPValue(i, j) + " x" + (j+1) + " + ";
            o=o+getParamPValue(i, numVar) + "\n";
        }
        o=o+ "end ANFIS\n";
        return (o);
    }

    /* Metodos que devuelven el valor asociado a una determinada posición en */
    /* el array */
    protected double getParamAValue(int i, int j){
        return param_a[i][j];
    }

    protected double getParamBValue(int i, int j){
        return param_b[i][j];
    }

    protected double getParamCValue(int i, int j){
        return param_c[i][j];
    }

    protected double getParamPValue(int i, int j){
        return param_p[i][j];
    }
    
    /* Metodos que establecen el valor asociado a una determinada posición en */
    /* el array */
    protected void setParamAValue(int i, int j, double value){
        param_a[i][j] = value;
    }

    protected void setParamBValue(int i, int j, double value){
        param_b[i][j] = value;
    }

    protected void setParamCValue(int i, int j, double value){
        param_c[i][j] = value;
    }

    protected void setParamPValue(int i, int j, double value){
        //System.out.println("Old p "+param_p[i][j]+ " New: "+value);
        param_p[i][j] = value;
    }

    /* Metodos publicos de acceso a los arrays enteros */
    public double[][] get_param_a() {
        return param_a;
    }
    
    public double[][] get_param_b() {
        return param_b;
    }
    
    public double[][] get_param_c() {
        return param_c;
    }
    
    public double[][] get_param_p() {
        return param_p;
    }
}
