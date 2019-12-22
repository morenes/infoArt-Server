package algorithms.FACL;
import models.*;
/**
 * Clase que implementa el algoritmo FACL para estado continuo pero acciones discretas
 * Para ello solo se utiliza un vector w comun a todas las reglas.
 * Falta saber como se inicializan los v.
 * Que elegibilidad y que w iniciales se le ponen a las acciones
 * Distribucion exponencial
 * @author David Gil Galvan
 */
public class FACL {
    
    protected double[] v;   // Vector de Conclusiones
    // Estado anterior 
    protected State stateOld;
    // fuzzyModel
    protected FuzzyModel fuzzyModel;
    // Conjunto de acciones
    protected Actions actions;    
    // Como es el caso discreto solo tenemos un beta y no un array de betas
    protected double beta=0.001;    // Se utiliza en la actualizacion de v        
    protected double gamma=0.9;   // Se utiliza en el calculo del error
    protected double zeta=0.001;    // Se utiliza en el epsilon-greedy
    protected double sp=0.001;      // Se utiliza en el epsilon-greedy
    protected double lambda=0.6; //0.6 0.8 0.9 0 // Se utiliza en la trazabilidad
    protected double lambdaprima=0.8; //0.8 // Se utiliza en la trazabilidad
    protected double kappa=0.1;        // Se utiliza en meta-learning-rule
    protected double omega=0.1;   // Se utiliza en meta-learning-rule
    protected double dseta=0.5;   // Se utiliza en meta-learning-rule
    protected double rtPara_Siguiente_Etapa=0;
    protected double fi_etapa_anterior_aprox=0;
    protected double sigmaEstimated_anterior=0;        
    /** Accion seleccionada */     
    protected int selectedAction=-1;
    /** Etapa actual */
    protected int step=0;
    /** Se utiliza para la distribucion exponencial */
    protected double factorCambio=0;
    
    /** Creates a new instance of FACL V1*/
    public FACL(FuzzyModel fuzzyModel,Actions actions,double gamma,double beta,double zeta,double sp,double lambda,double lambdaprima,double kappa,double omega, double dseta,double[] consecuents,double factorCambio) {
        this.fuzzyModel=fuzzyModel;
        this.actions=actions;
        this.gamma=gamma;
        this.beta=beta;
        this.zeta=zeta;
        this.sp=sp;
        this.lambda=lambda;
        this.lambdaprima=lambdaprima;
        this.kappa=kappa;
        this.omega=omega;
        this.dseta=dseta;
        this.factorCambio=factorCambio;
        
        
        
// INICIALIZAR V        
        //v=new double[consecuents.length];
        v=consecuents;
    }    
    
    public int execute(State stateNew) {
        
        if (step==0) {
            stateOld=stateNew;
            rtPara_Siguiente_Etapa=0;
        }
        System.out.println("ETAPA NUEVA");
        // Estimaci�n de la funci�n de evaluaci�n correspondiente al estado stateNew
        double V_etapa_Anterior_estado_Actual=getV(stateNew);
        System.out.println("V_etapa_Anterior_estado_Actual:"+V_etapa_Anterior_estado_Actual);
        // Estimaci�n de la funci�n de evaluaci�n correspondiente al estado stateOld
        double V_etapa_Anterior_estado_Anterior=getV(stateOld);
        System.out.println("V_ETAPA_ANTERIOR_ESTADO_ANTERIOR:"+V_etapa_Anterior_estado_Anterior);
        // Calculo del error TD
        System.out.println("ESTIMANDO ERROR");
        double error_Etapa_Actual=getErrorEstimated(V_etapa_Anterior_estado_Anterior,V_etapa_Anterior_estado_Actual);
        System.out.println("ERROR_ETAPA_ACTUAL:"+error_Etapa_Actual);
        
        // Se corresponde con la primera etapa donde no se ha seleccionado ninguna
        // accion
        if (selectedAction!=-1) {
            
            updateV_ver2(stateOld,error_Etapa_Actual);            
            updateW_ver2(stateOld,error_Etapa_Actual);
            System.out.println("V ACTUALIZADO:"+utility.MatrixOperation.toStringVector(v));
            System.out.println();
            System.out.println("W ACTUALIZADO:"+utility.MatrixOperation.toStringVector(actions.getVectorW()));
            System.out.println();
        }
        
        // Busqueda greedy seg�n el estado Actual
        selectedAction=epsilon_Greedy(stateNew);
                
        // Se indica cual es la acci�n elegida para incrementar el nt de esa acci�n
        actions.choosedAction(selectedAction);
        
        // Se obtiene la se�al de refuerzo que se obtiene al aplicar la nueva acci�n
        rtPara_Siguiente_Etapa=actions.getPrimaryReinforcement(selectedAction);
        
        // Se indica que el nuevo estado pasar� a ser el antiguo estado
        stateOld=stateNew;
        
        // Se actualiza el valor de beta seg�n Meta Learning Rule V2
        updateBeta(error_Etapa_Actual);
        System.out.println("VALORES NUEVOS DE BETA:"+beta);
                        
        // Se indica cual es el nuevo fi_etapa_Anterior
        fi_etapa_anterior_aprox=getFiAprox(stateNew);
        
        // Se actualiza el sigmaEstimatedAnterior
        sigmaEstimated_anterior=getSigmaEstimated(error_Etapa_Actual);
        
        updateElegibilidad(stateNew,selectedAction);
        System.out.println("ELEGIBILIDAD:"+utility.MatrixOperation.toStringVector(actions.getElegibilidad()));
        System.out.println("ACTION SELECCIONADA:"+selectedAction);
        System.out.println("FIN ETAPA ");
        System.out.println();
        step++;
        return selectedAction;
    }
    /**
     * Metodo que devuelve el valor de V(St)
     * @param state State
     */
    public double getV(State state) {
        
        double[] tau=fuzzyModel.getTruth(state.getState());        
        
        double tempV=0;
        
        for (int i=0;i<fuzzyModel.getNumRules();i++) {
            if (tau[i]!=0) {
                tempV+=(tau[i]*v[i]);            
            }
        }        
        return tempV;
    }    
    /**
     * Metodo que devuelve el error estimado
     * @param VtOld Vt(st)
     * @param VtNew Vt(st+1)
     * @return Devuelve el error estimado
     */
    private double getErrorEstimated(double VtOld,double VtNew) {
        
        System.out.println("VtOld:"+VtOld);
        System.out.println("VtNew:"+VtNew);
                
        return rtPara_Siguiente_Etapa+(gamma*VtNew)-VtOld;
    }
    /**
     * Metodo que actualiza los valores de v segun
     * v(t+1)=v(t)+(Beta*Error*I(t))
     * @param state Estado sobre el que se va actualizar los v
     * @param error Error TD
     */
    private void updateV(State state, double error) {
        double[] tau=fuzzyModel.getTruth(state.getState());
        double sum=0;
        for (int i=0;i<fuzzyModel.getNumRules();i++) {
            if (tau[i]!=0)
                sum+=tau[i];
        }
        for (int i=0;i<fuzzyModel.getNumRules();i++) {
            v[i]=v[i]+(beta*error*sum);
        }
    }
    
    /**
     * Metodo que actualiza los valores de v segun
     */
    private void updateV_ver2(State state, double error) {
        double fi_aprox=this.getFiAprox(state);
        for (int i=0;i<fuzzyModel.getNumRules();i++) {
            v[i]=v[i]+(beta*error*fi_aprox);
        }
    }
    
    private void updateW(State state, double error, int selectedAction) {
        
        double[] w=actions.getVectorW();
        
        double[] tau=fuzzyModel.getTruth(state.getState());
        double sum=0;
        for (int i=0;i<fuzzyModel.getNumRules();i++) {
            if (tau[i]!=0)
                sum+=tau[i];
        }
        w[selectedAction]=w[selectedAction]+(error*sum);
        actions.setVectorW(w);    
    }
    
    private void updateW_ver2(State state, double error) {        
        double[] w=actions.getVectorW();        
        double[] elegibilidad=actions.getElegibilidad();                        
        for (int i=0;i<actions.getNumActions();i++) {            
            w[i]=w[i]+(elegibilidad[i]*error);
        }        
        actions.setVectorW(w);    
    }
    
    /**
     * Metodo epsilon_Greedy
     * @param state
     * @return Devuelve la accion a utilizar 
     */
    private int epsilon_Greedy(State state) {
        
        double[] wprima=actions.getVectorW();                
        double[] tau=fuzzyModel.getTruth(state.getState());
        double[] random=new double[wprima.length];
        for (int i=0;i<wprima.length;i++) {
            double sum=0;        
            for (int j=0;j<fuzzyModel.getNumRules();j++) {
                sum+=(wprima[i]*tau[j]);
            }
            wprima[i]=sum;
            random[i]=getRandomFDistExponencial();
        }
        System.out.println("VECTOR WPRIMA:"+utility.MatrixOperation.toStringVector(wprima));
        System.out.println();
        
        double sf=getSf(wprima,random);
        System.out.println("SF:"+sf);
        //double nt=getRo(state);                
        double bestU=wprima[0]+(sf*random[0]);    
        int selectedAction=0;        
        //System.out.println("RANDOM:"+random);
        System.out.println("TEMPU :"+bestU);
        
        for (int i=1;i<actions.getNumActions();i++) {
            double tempU=wprima[i]+(sf*random[i])+getRo(state,i);            
            System.out.println("TEMPU :"+tempU);
            if (tempU>bestU) {
                bestU=tempU;
                selectedAction=i;
            }
        }
        System.out.println("FIN EPSILONGREEDY");
        System.out.println();
        // Se devuelve la accion seleccionada
        return selectedAction;
    }
    
    private double getSf(double[] w,double[] random) {        
        double max=w[0];
        double min=w[0];
        double randommax=random[0];
        for (int i=1;i<actions.getNumActions();i++) {
            if (w[i]>max)
                max=w[i];
            if (w[i]<min)
                min=w[i];
            if (randommax<random[i])
                randommax=random[i];
        }
        double sf=1;
        if (max==min) sf=1;
        else {
            sf=(sp*((double)max-(double)min))/(double)randommax;
        }
        return sf;
        
    }
    /**
     * Ro=zeta/e^nt
     */
    private double getRo(State state,int action) {
        double nt=0;
        double[] tau=fuzzyModel.getTruth(state.getState());
        int[] countActions=actions.getCountActions();
        // Para cada regla 
        // Se supone que en el caso continuo cada regla tiene su propio
        // countActions. En el caso discreto el countActions es el mismo
        // para todas las reglas. Esto lo he establecido yo, adaptando el caso
        // continuo.
        for (int i=0;i<fuzzyModel.getNumRules();i++) {
            if (tau[i]!=0)
                nt+=tau[i]*(double)countActions[action];            
        }
        return zeta/Math.exp(nt);
    }
    /**
     * Metodo que devuelve debe devolver valores aleatorios segun distribucion 
     * exponencial �Pero valor entre 0 y 1..?
     */
    private double getRandomFDistExponencial() {
        
	double y, x;
        double LIMIT=100;
	y = Math.random()*LIMIT; // y is from <0,1)
               
        x=factorCambio*Math.pow(Math.E,(-factorCambio*y));
        System.out.println("X:"+x);

        return x;
    } // negExp
    
    private double getFiAprox(State state) {
        double[] tau=fuzzyModel.getTruth(state.getState());
        double fiActual=0;
        for (int i=0;i<fuzzyModel.getNumRules();i++) { 
            if (tau[i]!=0)
                fiActual+=tau[i];
        }
        return (fiActual+(gamma*lambda*fi_etapa_anterior_aprox));
    }
    /**
     * Metodo que actualiza la elegibilidad de una acci�n
     * @param state Estado
     * @param selectedAction Accion que ha sido seleccionada
     */
    private void updateElegibilidad(State state,int selectedAction) {        
        double[] elegibilidad=actions.getElegibilidad();
        double[] tau=fuzzyModel.getTruth(state.getState());
        double fiActual=0;
        for (int i=0;i<fuzzyModel.getNumRules();i++) { 
            if (tau[i]!=0)
                fiActual+=tau[i];
        }
        for (int i=0;i<actions.getNumActions();i++) {
            // Si la accion es la que se ha seleccionado
            if (i==selectedAction) {
                elegibilidad[i]=((lambdaprima*elegibilidad[i])+fiActual);
            }
            else {
                elegibilidad[i]=lambdaprima*elegibilidad[i];            
            }
        }
        
        actions.setElegibilidad(elegibilidad);
    }   
    
    /**
     * Metodo que actualiza el valor de beta seg�n la heuristica Delta-Bar-Delta
     * del Meta Learning Rule
     */
    private void updateBeta(double errorEstimated) {
        
        if (sigmaEstimated_anterior*getSigma(errorEstimated)>0) {            
            beta+=kappa;
        }
        else {
            if (sigmaEstimated_anterior*getSigma(errorEstimated)<0) {
                beta+=(-omega*beta);
            }
            // No se actualiza el beta
            else beta=beta;
        }                
    }
    /**
     * Metodo que devuelve el valor de Sigma
     */
    private double getSigma(double errorEstimated) {
        return errorEstimated*fi_etapa_anterior_aprox;
    }
    
    /**
     * Metodo que deveulve el valor de sigma estimado
     */
    private double getSigmaEstimated(double errorEstimated) {
        return (((1-dseta)*getSigma(errorEstimated))+(dseta*sigmaEstimated_anterior));
    }
    
    public double[] getConclusionVector() {
        return v;
    }
    
    public double[] getVectorW() {
        return actions.getVectorW();
    }
    
    public double[] getElegibilidad() {
        return actions.getElegibilidad();
    }

    /**
     * Metodo que vuelca un FCSModel en un fichero XML
     */
    private void volcarModel(FuzzyModel fuzzyModel, String nombre) throws Exception{  
        fuzzyModel.setSerializationPath(nombre);
        fuzzyModel.writeDown();
    }    
}
