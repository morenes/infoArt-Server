package models.fcsModel;

import algorithms.PartitionedSpace;
import models.ConsequentType;
import models.FiringType;
import models.TCoNormType;
import models.tskModel.*;

/**
 * Clase que representa a un Fuzzy Cluster Set en el espacio de particion O
 * @author David Gil Galvan & Fernando Terroso Saenz
 */
public class FuzzyClusterSetO extends FuzzyClusterSet{
    
    private static final String MODEL_HEAD_O = "FCS_O_";    
    
    /**
     * Nombre de la clase
     */
    protected String className=this.getClass().getName();

    /**
     * Constructor de la clase
     * @param fi Fi de cada uno de los centroides del algoritmo Gustafson-Kessel
     * @param inputData Matriz de datos de entrada
     * @param outputData Vector con los datos de salida
     * @param fuzziness Valor de m
     * @param distanceType Tipo de distancia
     * @param centroids Matriz de centroides
     * @param pTConorm
     */
    public FuzzyClusterSetO(
            String identifier,
            double[][] inputData,
            double[] outputData,
            double fuzziness,
            Constants.DistanceType distanceType,
            double[][] centroids,
            double[] fi,
            TCoNormType pTConorm) {

        init(MODEL_HEAD_O,identifier, inputData, outputData, fuzziness, distanceType, centroids, fi, pTConorm, PartitionedSpace.O);

    }

    public double makeInference(double[] x)throws Exception{
        throw new UnsupportedOperationException();
    }

    /**
     * Constructor de la clase sin especificar los centroides. Estos se generan
     * a partir de un metodo interno de la clase
     * @param fi Fi del algoritmo Gustafson-Kessel
     * @param inputData Matriz de datos de entrada
     * @param outputData Vector con los datos de salida
     * @param fuzziness Valor de m
     * @param distanceType Tipo de distancia
     * @param pTConorm
     * @param numClusters Numero de centroides (clusters) a crear internamente
     */
    public FuzzyClusterSetO(
            String identifier,
            double[][] inputData,
            double[] outputData,
            double fuzziness,
            Constants.DistanceType distanceType,
            double[] fi,
            TCoNormType pTConor,
            int numClusters) {

            double[][] centroidsAux = initCentroids(inputData, outputData, numClusters, PartitionedSpace.O);
            init(MODEL_HEAD_O,identifier, inputData, outputData, fuzziness, distanceType, centroidsAux, fi, pTConor, PartitionedSpace.O);

    }
    
    /**
     * Metodo que devuelve la matriz de datos con la que deben trabajar los algoritmos según del espacio de trabajo,
     * ya sea entrada, salida o entrada/salida
     * @throws FCSException Devuelve una excepcion en caso que se produzca algun error
     * @return Devuelve la matriz con los datos segun el espacio del clustering
     */
    public double[][] getPartitionedData() throws FCSException{
        
        // Si las particiones son en el espacio de salida
        
        double matrixO[][]=new double[outputData.length][1];
        for (int i=0;i<outputData.length;i++) {
            matrixO[i][0]=outputData[i];
        }
        return matrixO;
    }
    
    /**
     * Metodo que devuelve una copia del FCS actual
     * @return Devuelve una copia del FCS actual
     */
    public Object clone() {
        IODataSet io=new IODataSet();
        io.setInputData(this.inputData);
        io.setOutputData(this.outputData);
        return new FuzzyClusterSetO(modelIdentifier, inputData, outputData, fuzziness, distanceType,utility.MatrixOperation.copyMatrix(centroids),fi, tCoNorm);
    }
    
    
    /**
     * Metodo que se encarga de crear un TSK Model Gaussiano a partir de un FCS
     * @return Devuelve el TSK Model Gaussiano correspondiente
     * @param user Usuario
     * @param key Clave
     * @param consequentType Tipo de consecuente: de primer grado o de grado 0
     * @param firingType Tipo de inferencia
     * @throws Exception Se produce una excepcion en el caso que se produzca algun error en el proceso de generar el TSK Model Gaussiano
     */
    public TSKModel createGaussianTSKFromFCS(
            ConsequentType consequentType,
            FiringType firingType) throws FCSException {
        // Si el tipo de consecuente es de grado 0
        if(consequentType.equals(ConsequentType.SINGLETON)){
            return createGaussianSingletonTSKFromFCSO(consequentType,firingType);
        } else {
            throw new FCSException(className+".createGaussianTSKFromFCSI: Metodo no implementado");
        }
    }
    
    /**
     * Metodo que construye un TSKModel Gaussiano con Consecuente de orden 0 y con espacio de partion del clustering de salida
     * @return Devuelve el TSKModel Gaussiano
     * @param user Usuario
     * @param key Clave
     * @param consequentType Tipo de consecuente
     * @param firingType Tipo de inferencia
     * @throws models.fcsModel.FCSException Devuelve una excepcion en caso que se produzca algun error
     */
    private TSKModel createGaussianSingletonTSKFromFCSO(ConsequentType consequentType, FiringType firingType ) throws FCSException{
        
        double[][] centroids=getCentroids();
        
        double[][] param_a=new double[numCentroids][inputData[0].length]; // Desviacion
        double[][] param_c=new double[numCentroids][inputData[0].length]; // Media
        double[][] param_p=new double[numCentroids][inputData[0].length+1];
        double[][] data=getPartitionedData();
        
        for (int i=0;i<centroids.length;i++) {
            
            int max=0;
            // Se establece cual es el ejemplo con mayor pertenencia al centroide i
            for (int k=1;k<inputData.length;k++) {                
                if (getMembership(data[max],i)<getMembership(data[k],i))
                    max=k;
            }
            // para cada atributo se determinan los valores a, c y p
            for (int j=0;j<((inputData[0].length));j++) {
                // Para el punto c de la media se utiliza el atributo del ejemplo con
                // mayor pertenencia
                param_c[i][j]=inputData[max][j];
                // Para el punto a se tiene que la desviacion
                param_a[i][j]=calculateDesviation(inputData[max][j], i,j);
                param_p[i][j]=0;
            }
            // El consecuente es el centroide correspondiente
            param_p[i][inputData[0].length]=this.centroids[i][0];
        }
        
        return new GaussianTSKModel(modelIdentifier, inputData[0].length, consequentType, firingType,param_a,param_c,param_p);
    }
    
    /**
     * Metodo que calcula la desviacion respecto a la media de un atributo determinado
     * @return Devuelve la desviacion sobre el atributo at
     * @param mean Media del atributo correspondiente
     * @param cent Centroide
     * @param at Atributo sobre el que se desea obtener la desviacion
     * @throws models.fcsModel.FCSException Devuelve una excepcion en caso que se produzca algun error
     */
    private  double calculateDesviation(double mean, int cent, int at) throws FCSException{
        double[][] U=getU();
        double[][] datas=getInputData();
        double summation=0;
        for (int i=0;i<datas.length;i++) {
            summation+=(U[cent][i]*Math.pow((datas[i][at]-mean),2));
        }
        return Math.sqrt(summation/datas.length);
    }
    
    /**
     * Metodo que construye un TSKModel Triangular a partir de un FCS
     * @return Devuelve el TSKModel Triangular
     * @param user Usuario
     * @param key Clave
     * @param consequentType Tipo de Consecuente
     * @param firingType Tipo de inferencia
     * @param method Metodo para generar el TSKModel
     * @throws models.fcsModel.FCSException Devuelve una excepcion en caso que se produzca algun error
     */
    @Override
    public  TSKModel createTriangularTSKFromFCS(
            ConsequentType consequentType,
            FiringType firingType,
            byte method) throws FCSException{
        
        if(consequentType.equals(ConsequentType.SINGLETON)){
            if (method==models.fcsModel.Constants.methodA)
                return AcreateTriangularSingletonTSKFromFCSO(consequentType,firingType);
            if (method==models.fcsModel.Constants.methodB)
                return BcreateTriangularSingletonTSKFromFCSO(consequentType,firingType);
        } else {
            throw new FCSException(className+".createTriangularTSKFromFCSI: Metodo no implementado");
        }
        return null;
        
    }
    
    /**
     * Metodo (A) que construye un TSKModel Triangular con Consecuente de orden 0 y con espacio de partion del
     * clustering de salida a partir de un fcs
     * @return Devuelve el TSKModel Triangular
     * @param user Usuario
     * @param key Clave
     * @param consequentType Tipo de Consecuente
     * @param firingType Tipo de inferencia
     * @throws models.fcsModel.FCSException Devuelve una excepcion en caso que se produzca algun error
     */
    private TSKModel AcreateTriangularSingletonTSKFromFCSO(
            ConsequentType consequentType,
            FiringType firingType ) throws FCSException{
        
        double[][] centroids=getCentroids();
        
        double[][] param_a=new double[numCentroids][inputData[0].length];
        double[][] param_b=new double[numCentroids][inputData[0].length];
        double[][] param_c=new double[numCentroids][inputData[0].length];
        double[][] param_p=new double[numCentroids][inputData[0].length+1];
        double[][] data=getPartitionedData();
        
        for (int i=0;i<centroids.length;i++) {
            int max=0;
            for (int k=1;k<inputData.length;k++) {
                if (getMembership(data[max],i)<getMembership(data[k],i))
                    max=k;
            }
            
            for (int j=0;j<((inputData[0].length));j++) {
                
                param_b[i][j]=inputData[max][j];
                param_p[i][j]=0;
                if (centroids.length==1) { // SI SOLO EXISTE UN CENTROIDE
                    
                    double valMin=Double.POSITIVE_INFINITY;
                    double valMax=Double.NEGATIVE_INFINITY;
                    for (int k=0;k<inputData.length;k++) {
                        if (inputData[k][j]>valMax) {
                            valMax=inputData[k][j];
                        }
                        if (inputData[k][j]<valMin) {
                            valMin=inputData[k][j];
                        }
                    }
                    param_a[i][j]=valMin;
                    param_c[i][j]=valMax;
                } else {
                    
                    double[] valOrder=new double[centroids.length];
                    int[] posOrder=new int[centroids.length];
                    for (int z=0;z<centroids.length;z++) {
                        int maxTempo=0;
                        for (int k=1;k<inputData.length;k++) {
                            if (getMembership(data[maxTempo],z)<getMembership(data[k],z))
                                maxTempo=k;
                        }
                        valOrder[z]=inputData[maxTempo][j];
                        posOrder[z]=z;
                    }
                    
                    for (int z=0;z<centroids.length;z++) {
                        for (int k=z+1;k<centroids.length;k++) {
                            if (valOrder[z]>valOrder[k]) {
                                double temp1=valOrder[z];
                                valOrder[z]=valOrder[k];
                                valOrder[k]=temp1;
                                int temp2=posOrder[z];
                                posOrder[z]=posOrder[k];
                                posOrder[k]=temp2;
                            }
                        }
                    }
                    
                    if (posOrder[i]==0) {
                        param_c[i][j]=valOrder[1];
                        param_a[i][j]=param_b[i][j]-(valOrder[1]-param_b[i][j]);
                    } else {
                        if (posOrder[i]==(centroids.length-1)) {
                            param_a[i][j]=valOrder[valOrder.length-1];
                            param_c[i][j]=param_b[i][j]+(param_b[i][j]-valOrder[valOrder.length-1]);
                        } else {
                            param_a[i][j]=valOrder[posOrder[i]-1];
                            param_c[i][j]=valOrder[posOrder[i]+1];
                        }
                    }
                }
            }
            
            param_p[i][inputData[0].length]=this.centroids[i][0];
        }
        
        return new TriangularTSKModel(inputData[0].length,consequentType, firingType,param_a,param_b,param_c,param_p);
    }
    
    /**
     * Metodo (B) que construye un TSKModel Triangular con Consecuente de orden 0 y con espacio de particion del clustering de entrada a partir de un FCS
     * @return Devuelve el TSKModel Triangular
     * @param user Usuario
     * @param key Clave
     * @param consequentType Tipo de Consecuente
     * @param firingType Tipo de inferencia
     * @throws models.fcsModel.FCSException Devuelve una excepcion en caso que se produzca algun error
     */
    private TSKModel BcreateTriangularSingletonTSKFromFCSO(
            ConsequentType consequentType,
            FiringType firingType) throws FCSException{
        
        double[][] centroids=getCentroids();
        double[][] param_a=new double[numCentroids][inputData[0].length];
        double[][] param_b=new double[numCentroids][inputData[0].length];
        double[][] param_c=new double[numCentroids][inputData[0].length];
        double[][] param_p=new double[numCentroids][inputData[0].length+1];
        double[][] data=getPartitionedData();
        
        for (int i=0;i<centroids.length;i++) {
            int max=0;
            for (int k=1;k<inputData.length;k++) {
                if (getMembership(data[max],i)<getMembership(data[k],i))
                    max=k;
            }
            for (int j=0;j<((inputData[0].length));j++) {
                
                param_b[i][j]=inputData[max][j];
                param_p[i][j]=0;
                if (centroids.length==1) { // SI SOLO EXISTE UN CENTROIDE
                    
                    double valMin=Double.POSITIVE_INFINITY;
                    double valMax=Double.NEGATIVE_INFINITY;
                    for (int k=0;k<inputData.length;k++) {
                        if (inputData[k][j]>valMax) {
                            valMax=inputData[k][j];
                        }
                        if (inputData[k][j]<valMin) {
                            valMin=inputData[k][j];
                        }
                    }
                    param_a[i][j]=valMin;
                    param_c[i][j]=valMax;
                } else {
                    double[][] inputData=getInputData();
                    
                    int min=0;
                    for (int k=0;k<inputData.length;k++) {
                        
                        if (((getMembership(data[k]))[i])<((getMembership(data[min]))[i]))
                            min=k;
                        
                    }
                    double distance=Math.abs(this.inputData[max][j]-this.inputData[min][j]);
                    param_a[i][j]=this.inputData[max][j]-distance;
                    param_c[i][j]=this.inputData[max][j]+distance;
                }
                
            }
            
            param_p[i][inputData[0].length]=this.centroids[i][0];
        }
        
        return new TriangularTSKModel(inputData[0].length,consequentType, firingType,param_a,param_b,param_c,param_p);
    }
    
    
    
    /**
     * Metodo que construye un TSKModel Trapezoidal y con espacio de particion del clustering de salida a partir de un FCS
     * @param user Usuario
     * @param key Clave
     * @param consequentType Tipo de Consecuente
     * @param firingType Tipo de inferencia
     * @param umbralInf Umbral Inferior
     * @param umbralSup Umbral Superior
     * @throws FCSException Devuelve una excepcion en caso que se produzca algun error
     * @return Devuelve el TSKModel Trapezoidal
     */
    public TSKModel createTrapezoidalTSKFromFCS(
            ConsequentType consequentType,
            FiringType firingType,
            double umbralSup,
            double umbralInf) throws FCSException {
        // Si el tipo de consecuente es de grado 0
        if(consequentType.equals(ConsequentType.SINGLETON)){
            return createTrapezoidalSingletonTSKFromFCSO(consequentType,firingType, umbralSup, umbralInf);
        } else {
            throw new FCSException(className+".createGaussianTSKFromFCSIO: Metodo no implementado");
        }
    }
    
    /**
     * Metodo que construye un TSKModel Trapezoidal con Consecuente de orden 0 y con espacio de particion del clustering de salida a partir de un FCS
     * @param user Usuario
     * @param key Clave
     * @param consequentType Tipo de Consecuente
     * @param firingType Tipo de inferencia
     * @param umbralInf Umbral Inferior
     * @param umbralSup Umbral Superior
     * @throws FCSException Devuelve una excepcion en caso que se produzca algun error
     * @return Devuelve el TSKModel Trapezoidal
     */
    private TSKModel createTrapezoidalSingletonTSKFromFCSO(
            ConsequentType consequentType,
            FiringType firingType,
            double umbralSup,
            double umbralInf) throws FCSException {

        try {
            double[][] centroids=getCentroids();
            // Parametros del TSK
            double[][] param_a=new double[numCentroids][inputData[0].length];
            double[][] param_b=new double[numCentroids][inputData[0].length];
            double[][] param_c=new double[numCentroids][inputData[0].length];
            double[][] param_d=new double[numCentroids][inputData[0].length];
            double[][] param_p=new double[numCentroids][inputData[0].length+1];
            int p=inputData[0].length;
            // Centroides del Cluster Line
            double[][] v1=new double[numCentroids][p];
            double[][] v2=new double[numCentroids][p];
            
            double[][] data=getPartitionedData();
            // Pertenencia segun el cluster Line
            double[][][] ULine=new double[numCentroids][inputData.length][p];
            // Puede ocurrir que la suma de las pertenencias de un ejemplo a los diferentes
            // cluster sea mayor que uno.
            // Metodo dudoso ya que no se sigue el algoritmo original al aceptar
            // membership menores que uno.
            for (int j=0;j<p;j++) {
                for (int i=0;i<this.numCentroids;i++) {
                    double max_Tij=Double.NEGATIVE_INFINITY;
                    double min_Tij=Double.POSITIVE_INFINITY;
                    
                    // Esta opcion de ir decrementando el umbral hasta
                    // que haya algun max_Tij y min_Tij es cosa nuestra
                    // Puede dar algun problema
                    double umbralAux=umbralSup;
                    do {
                        for (int k=0;k<data.length;k++) {
                            double membership=getMembership(data[k],i);
                            
                            if (membership>=umbralAux) {
                                if (inputData[k][j]>max_Tij)
                                    max_Tij=inputData[k][j];
                                if (inputData[k][j]<min_Tij)
                                    min_Tij=inputData[k][j];
                            }
                        }
                        umbralAux-=0.01;
                    } while ((max_Tij==Double.NEGATIVE_INFINITY)||(min_Tij==Double.POSITIVE_INFINITY));
                    // Se obtiene el cluster Line
                    v1[i][j]=min_Tij;
                    v2[i][j]=max_Tij;
                    
                }
                // Segun el cluster line correspondiente, se determina la pertenencia
                // de cada ejemplo a cada cluster
                for (int i=0;i<numCentroids;i++) {
                    for (int k=0;k<data.length;k++) {
                        if (numCentroids==1) // Si el numero de cluster es 1
                            ULine[i][k][j]=1.0;
                        else {
                            if (this.fuzziness==1) { // Si m es crisp
                                if ((v1[i][j]<=inputData[k][j])&&(inputData[k][j]<=v2[i][j]))
                                    ULine[i][k][j]=1.0;
                                else
                                    ULine[i][k][j]=0.0;
                            } else {
                                if ((v1[i][j]<=inputData[k][j])&&(inputData[k][j]<=v2[i][j]))
                                    ULine[i][k][j]=1.0;
                                else {
                                    double summation1=0;
                                    for (int l=0;l<numCentroids;l++) {
                                        summation1+=Math.pow((Math.abs(inputData[k][j]-v1[i][j]))/(Math.abs(inputData[k][j]-v1[l][j])), (1.0/(fuzziness-1.0)));
                                    }

                                    summation1=1.0/summation1;
                                    double summation2=0;
                                    for (int l=0;l<numCentroids;l++) {
                                        summation2+=Math.pow((Math.abs(inputData[k][j]-v2[i][j]))/(Math.abs(inputData[k][j]-v2[l][j])), (1.0/(fuzziness-1.0)));
                                    }
                                    summation2=1.0/summation2;
                                    
                                    ULine[i][k][j]=Math.max(summation1,summation2);
                                }
                            }
                        }
                    }
                }
            }
           
            // Se construye el TSKModelTrapezoidal
            for (int i=0;i<numCentroids;i++) {
                for (int j=0;j<p;j++) {
                    double max_Tij=Double.NEGATIVE_INFINITY;
                    double min_Tij=Double.POSITIVE_INFINITY;
                    
                    // Esta opcion de ir decrementando el umbral hasta
                    // que haya algun max_Tij y min_Tij es cosa nuestra
                    // Puede dar algun problema
                    double umbralAux=umbralInf;
                    double max=Double.POSITIVE_INFINITY;
                    double min=Double.POSITIVE_INFINITY;
                    do {
                        for (int k=0;k<data.length;k++) {
                            //System.out.println("U:"+ULine[i][k][j]);
                            double membership=ULine[i][k][j];
                            if ((membership<min)&&(inputData[k][j]<min_Tij)&&(inputData[k][j]<=v1[i][j])) {
                                min_Tij=inputData[k][j];
                                min=membership;
                            }
                            if ((membership<max)&&(inputData[k][j]>max_Tij)&&(inputData[k][j]>=v2[i][j])) {
                                max_Tij=inputData[k][j];
                                max=membership;
                            }
                        }

                    } while ((max_Tij==Double.NEGATIVE_INFINITY)||(min_Tij==Double.POSITIVE_INFINITY));
                    param_a[i][j]=min_Tij;
                    param_b[i][j]=v1[i][j];
                    param_c[i][j]=v2[i][j];
                    param_d[i][j]=max_Tij;
                    param_p[i][j]=0;
                }
                
                param_p[i][inputData[0].length]=this.centroids[i][0];
            }
            
            return new TrapezoidalTSKModel(inputData[0].length,consequentType, firingType,param_a,param_b,param_c,param_d,param_p);
        } catch (Exception e){
            throw new FCSException(className+".createTrapezoidalTSKFromFCS: Se ha producido un error al obtener el TSK Model Trapezoidal."+e.getMessage());
        }
    }
    
    /**
     * Metodo que devuelve un vector con los consecuentes de los centroides     
     * @throws models.fcsModel.FCSException Excepcion 
     * @return Devuelve un vector con los consecuentes de los centroides
     */
    public double[] getConsequents() throws FCSException{
        double[][] centroides=obtenerCentroidesIniciales(centroids);
        double[] consequents=new double[centroids.length];
        for (int i=0;i<centroids.length;i++) {
            consequents[i]=centroids[i][centroides[0].length-1];
        }
        return consequents;
    }     
    
}