package algorithms.BestCentroids;

import algorithms.AHC.*;
import algorithms.PartitionedSpace;
import algorithms.fcm.*;
import java.io.File;
import models.TCoNormType;
import models.fcsModel.*;
import models.fcsModel.Constants.DistanceType;

/**
 * Clase que implementa el algoritmo que determina los valor adecuados de m(fuzzines), c(numero de centroides) y la matriz de centroides inicial a partir del cual aplicar el algoritmo FCM.
 * Sin embargo esta clase implementa el algoritmo de tal forma que no es necesario aplicar de nuevo FCM sobre el Fuzzy Cluster Set devuelto, ya que a la misma vez que se ejecuta el algoritmo va aplicando el FCM correspondiente.
 * Este algoritmo tiene el problema de que si todos los ejemplos son iguales a los centroides entonces
 * no se puede aplicar correctamente este algoritmo. Ocurre que, aunque se aumente m, el valor de SW y SB
 * permanecen constantes y por lo tanto no se puede encontrar el valor apropiado que que haga que tr(SW+SB)
 * se aproxime a la mitad del intervalo [0,K].
 * Este algoritmo inicialmente elimina los ejemplos iguales para eliminar los problemas que supone tener dos ejemplos iguales.
 * @author David Gil Galvan & Fernando Terroso Saenz
 */
public class BestCentroids {
    
    /**
     * Nombre de la clase
     */
    protected String className=this.getClass().getName();
    
    // Parametros de configuracion del algoritmo
    BestCentroidsParams params;

    public BestCentroids(BestCentroidsParams pParams){
        params = pParams;
    }
    
    /**
     * Metodo que ejecuta el algoritmo. Determina qu� fuzziness, numero de cluster y 
     * la matrices de cluster inicial adecuados para utilizar el algoritmo FCM. Este m�todo ya devuelve 
     * el FCS que se obtiene al aplicar FCM sobre estos parametros adecuados.
     * @return Devuelve el Fuzzy Cluster Set con la mejor ejecucion del algoritmo FCM, es decir con el fuzziness, 
     * el numero de cluster c y matriz de centroides inicial adecuado.
     * @param params Clase con los parametros de BestCentroids
     * @param inData Matriz de datos de entrada
     * @param outData Matriz de datos de salida
     * @param pSpace Espacio de particion de los datos
     * @param distanceType Tipo de distancia
     * @param fi Fi del algoritmo Gustafson-Kessel
     */
    public FuzzyClusterSet execute(
            double[][] inData,
            double[] outData,
            PartitionedSpace pSpace,
            DistanceType distanceType,
            double[] fi,
            String executionName) {

        try {
            int cmax = params.getCmax();
            // Se comprueba que los params.getUmbral()es, params.getAlfa()s y numero de centroides son adecuados
            if ((params.getUmbral()<0) ||(params.getUmbral()>1))
                throw new BestCentroidsException("El valor del params.getUmbral() es incorrecto [0..1]");
            if ((params.getAlfa()<0) ||(params.getAlfa()>1))
                throw new BestCentroidsException("El valor del params.getAlfa() es incorrecto [0..1]");
            
            double[][] datas=partitionedData(inData,outData, pSpace);
            
            if ((cmax>datas.length)||(cmax<1))
                throw new BestCentroidsException("El numero de centroides es incorrecto ");
            if ((params.getEpsilon()<0) ||(params.getEpsilon()>1))
                throw new BestCentroidsException("El valor de params.getEpsilon() es incorrecto [0..1]");
            if (params.getMaxIteration()<1)
                throw new BestCentroidsException("El numero de iteraciones debe ser mayor o igual que 1");
            
            // (CUIDADO)PUEDE SER QUE EL K SEA MUY GRANDE
            // Se calcula el valor de K
            double K=getK(datas);            
            
            //System.out.println("Valor de K es:"+K);
            //System.out.println("Generando los clusters iniciales con m=1");
            FuzzyClusterSet[] initialClustering = getClusteringInitials(executionName,cmax, inData, outData, pSpace, 1.0, distanceType,fi);
            //System.out.println("Clusters iniciales con m=1 generados");
            if (cmax>initialClustering.length) {
                //System.out.println("el valor de cmax se actualiza a cmax=clusteringInitial.length: ("+initialClustering.length+")");
                cmax=initialClustering.length;
            }

            //Array con los fcs optimizados con el m de cada iteracion
            FuzzyClusterSet[] clusteringFinal = initialClustering;

            FuzzyClusterSet fcs_optimum=(FuzzyClusterSet)(initialClustering[cmax-1].clone());
            double scs_min=Double.POSITIVE_INFINITY;

             for (int c_current=1; c_current < cmax; c_current++) {

                // Se inicializan los valores
                boolean finished=false;
                double m_current=1.0;
                double m_optimum=0;
                double m_previus=0;
                double st_optimum=Double.POSITIVE_INFINITY;

                // Valor medio de K
                double middle=K/2.0;

                FCMAlgorithm fcm=new FCMAlgorithm();
                //System.out.println("K:"+K);

                while ((!finished)&&(m_current<20)) {
                    FuzzyClusterSet fcs_initial=initialClustering[cmax-c_current];
                    //System.out.println("NUMERO DE CLUSTER:"+fcs_initial.getNumCluster());
                    FuzzyClusterSet fcs_fcm=(FuzzyClusterSet) fcs_initial.clone();

                    fcs_fcm.setFuzziness(m_current);
                    //System.out.println("EL M ACTUAL ES:"+m_current);
                    // Se ejecuta el algoritmo FCM con el m indicado
                    fcm.optimize(fcs_fcm,params.getEpsilon(), params.getMaxIteration());

                    // Se calculan las matrices de dispersion
                    double[][] sw_current=fcs_fcm.SW();
                    double[][] sb_current=fcs_fcm.SB();

                    // Se calcula la traza de la matriz
                    double scs_current=utility.MatrixOperation.getTrace(utility.MatrixOperation.resta(sw_current, sb_current));
                    //System.out.println("SCT_SUMA:"+utility.MatrixOperation.getTrace(utility.MatrixOperation.sum(sw_current, sb_current)));
                    //System.out.println("SCS_CURRENT:"+scs_current);

                    double val=utility.MatrixOperation.getTrace(utility.MatrixOperation.sum(sw_current, sb_current));
                    //System.out.println("EL PUNTO MEDIO ES:"+middle);
                    //System.out.println("INTERVALO:["+(middle-(middle*params.getAlfa()))+","+(middle+(middle*params.getAlfa()))+"]");
                    //System.out.println("VAL:"+val);

                    if ((val<=(middle+(middle*params.getAlfa())))&&
                            (val>=(middle-(middle*params.getAlfa())))) {
                        st_optimum=utility.MatrixOperation.getTrace(utility.MatrixOperation.sum(sw_current, sb_current));
                        //System.out.println("Numero de centroides de verdad:"+fcs_fcm.getCentroids().length);
                        //System.out.println("ST_OPTIMUM:"+st_optimum);
                        clusteringFinal[cmax-c_current]= (FuzzyClusterSet) fcs_fcm.clone();
                        if(scs_current< scs_min){
                            fcs_optimum= (FuzzyClusterSet) fcs_fcm.clone();
                            scs_min = scs_current;
                        }
                        
                        m_previus=m_optimum;
                        m_optimum=m_current;
                        //System.out.println("M_PREVIUS:"+m_previus);
                        //System.out.println("M_OPTIMUN:"+m_optimum);
                        if ((Math.abs(m_previus-m_optimum))<m_optimum*params.getUmbral()) {
                            // Finaliza la busqueda
                            //System.out.println("FIN");
                            finished=true;
                        }
                    }

                    // Se sigue buscando el m mas adecuado
                    m_current+=0.05;
                    //System.out.println("-----------------");
                }
             }

            //Almacenamos los fcs optimos en formato XML
            for(FuzzyClusterSet fcs : clusteringFinal){
                if(fcs != null){
                    fcs.setSerializationPath(params.getOutputDir());
                    fcs.setPartialModelIdentifier(executionName);
                    //fcs.writeDown();
                }
            }
            
            System.gc();            
            //System.out.println("Fin de la ejecucion de BestCentroids");
            // Devolvemos el clustering con mejor par (m,c)
            return fcs_optimum;
        } catch (Exception e)  {
            //System.out.println("Se ha producido un error al ejecutar Best Centroids");
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Metodo que determina la matriz de cluster inicial adecuada para todos los cluster entre 1 y cmax
     * @param cmax Numero de cluster maximo para el que se desea determinar cual es su matriz de cluster adecuada
     * @param inData Matriz de datos de entrada
     * @param outData Vector de datos de salida
     * @param pSpace Espacio de particionamiento de los datos
     * @param fuzziness Valor de m
     * @param distanceType Tipo de distancia
     * @param fi Fi del algoritmo Gustafson-Kessel
     * @throws BestCentroidsException Devuelve una excepcion en caso que se produzca algun error
     * @return Devuelve un vector con los Fuzzy Cluster Set con m=1 adecuados cuando el numero de cluster varia entre
     * 1 y cmax
     */
    protected FuzzyClusterSet[] getClusteringInitials(
            String executionName,
            int cmax,
            double[][] inData,
            double[] outData,
            PartitionedSpace pSpace,
            double fuzziness,
            DistanceType distanceType,
            double[] fi) throws BestCentroidsException{

        try {

            double[][] partition = partitionedData(inData,outData,pSpace);

            double[][] centroids = removeDuplicates(partition);
            
            //System.out.println("Original:"+partition.length+","+partition[0].length+" Cleaned: "+centroids.length+","+centroids[0].length);
            if(cmax>centroids.length) {
                //System.out.println("Cambio el valor de cmax al tamaño de los centroides");
                cmax=centroids.length;
            }

            AHCParams ahcParams = params.getAhcParams();
            //if (executionName==null) executionName="";
            ahcParams.setExecutionName(executionName);

            String ahcStrDir = ahcParams.getXMLPath();

            FuzzyClusterSet fcsInitial = null;

            // Comprobamos si existe un fichero temporal para recuperar...
            File ahcDir = new File(ahcStrDir);
            if(ahcDir.exists()){
                String[] tempAhcFiles = ahcDir.list();
                String pattern = ahcParams.getTempFileName() + ahcParams.getExecutionName();
                for(String tempAhcFile : tempAhcFiles){
                    if(tempAhcFile.startsWith(pattern)){
                        //System.out.print("Recuperando fichero " + tempAhcFile+"...");
                        fcsInitial = FuzzyClusterSet.createFuzzyClusterSetFromBinary(ahcStrDir+tempAhcFile);
                        //System.out.println("OK!");
                        break;
                    }
                }
            }

            // ...sino empezamos de cero.
            if(fcsInitial == null){
                switch(pSpace){
                    case I:
                        fcsInitial=new FuzzyClusterSetI(executionName,inData,outData,fuzziness, distanceType, centroids,fi, TCoNormType.MAXIMUM);
                        break;
                    case O:
                        fcsInitial=new FuzzyClusterSetO(executionName,inData,outData,fuzziness, distanceType, centroids,fi, TCoNormType.MAXIMUM);
                        break;
                    case IO:
                        fcsInitial=new FuzzyClusterSetIO(executionName,inData,outData,fuzziness, distanceType, centroids,fi, TCoNormType.MAXIMUM);
                        break;
                }
            }

            //System.out.println("El valor de cmax es: "+cmax);
            
            int N=fcsInitial.getCentroids().length;
            //System.out.println("El valor de N es: "+N);
            FuzzyClusterSet[] clusteringInitial=new FuzzyClusterSet[cmax];
            
            // Algoritmo AHC
            AHC ahc=new AHC(params.getAhcParams());
            FuzzyClusterSet fcsNew=ahc.execute((FuzzyClusterSet)fcsInitial.clone(), cmax);
            clusteringInitial[cmax-1]=(FuzzyClusterSet)fcsNew.clone();
            //System.out.println("Inserto el fcsInicial de todos en cmax-1:"+(cmax-1));
            fcsInitial=fcsNew;
            for (int i=2;i<=cmax;i++) {
                
                int c=fcsInitial.getCentroids().length-1;
                // Se llama al algoritmo AHC para determinar cual es la matriz de centroides adecuada cuando m=1 y
                // para el numero de centroides c
                //System.out.println("Ejecuto el AHC con c= "+c);
                fcsNew=ahc.execute((FuzzyClusterSet)fcsInitial.clone(), c);
                //System.out.println("Fin del AHC con c="+c);
                //if (N-i<=params.getCmax()) {
                
                // Solo almacenamos los fcs tal que el numero de cluster se encuentre entre 1 y params.getCmax()
                //System.out.println("Inserto en cmax-i:"+(cmax-i));
                clusteringInitial[cmax-i]=(FuzzyClusterSet)fcsNew.clone();

                fcsInitial=fcsNew;
            }
            //System.out.println("Se han obtenido los clusteringIniciales");
            // Se devuelve el vector con los fcs
            return clusteringInitial;
        } catch (Exception e)  {
            e.printStackTrace();
            throw new BestCentroidsException(className+".getClusteringInitials: Se ha producido un error al obtener los cluster Iniciales."+e.getMessage());
        }
    }
    
    
    
/**
     * Metodo que devuelve la matriz de datos segun del espacio de particionamiento establecido, ya sea
     * entrada, salida o entrada/salida.
     * @param pSpace Espacio de particion
     * @param inData Matriz de datos de entrada
     * @param outData Vector de datos de salida
     * @return Devuelve la matriz de datos segun el espacio del clustering
     * @throws BestCentroidsException Devuelve una excepcion en caso que se produzca algun error
     */
protected double[][] partitionedData(double[][] inData, double[] outData, PartitionedSpace pSpace) throws BestCentroidsException{
    // Si las particiones son en el espacio de entrada
    if (pSpace.equals(PartitionedSpace.I)) {
        int numRows=inData.length;
        int numColum=inData[0].length;
        double[][] matrixI=new double[numRows][numColum];
        for(int i=0; i<numRows; i++){
            for(int j=0; j<numColum; j++){
                matrixI[i][j]=inData[i][j];
            }
        }
        return matrixI;
    }
        
    // Si las particiones son en el espacio de salida
    if (pSpace.equals(PartitionedSpace.O)) {
        double matrixO[][]=new double[outData.length][1];
        for (int i=0;i<outData.length;i++) {
            matrixO[i][0]=outData[i];
        }
        return matrixO;
    }
    // Si las particiones son en el espacio de entrada/salida
    if (pSpace.equals(PartitionedSpace.IO)) {
        int numRows=inData.length;
        int numColum=inData[0].length+1;
        double[][] matrixIO=new double[numRows][numColum];
        for(int i=0; i<numRows; i++){
            for(int j=0; j<numColum-1; j++){
                matrixIO[i][j]=inData[i][j];
            }
        }
        for(int i=0; i<numRows; i++){
            matrixIO[i][numColum-1]=outData[i];
        }
        return matrixIO;
    }
    
    throw new BestCentroidsException("PartitionedSpace not specificied");
}

/**
     * Metodo que devuelve el valor de K
     * @param datas Conjunto de datos
     * @return Devuelve el valor de K
     * @throws BestCentroidsException Devuelve una excepcion en caso que se produzca algun error
     */
protected double getK(double[][] datas) throws BestCentroidsException{
    try {
        
        int numRows=datas.length;
        int numColum=datas[0].length;
        
        double sum;
        
        double[][] trace=new double[numColum][numColum];
        double[] means=new double[numColum];
        for (int i=0;i<numColum;i++) {
            means[i]=calculateMean(datas,i);
        }
        
        for (int i=0;i<numColum;i++) {
            for (int j=0;j<numColum;j++) {
                trace[i][j]=0.0;
            }
        }
        
        for (int i=0;i<numRows;i++) {
            double[][] matrix=new double[numColum][1];
            for (int j=0;j<numColum;j++) {
                matrix[j][0]=datas[i][j]-means[j];
            }
            double[][] matrixT=utility.MatrixOperation.trasponse(matrix);
            trace=utility.MatrixOperation.sum(trace, utility.MatrixOperation.product(matrix, matrixT));
        }
        return utility.MatrixOperation.getTrace(trace);
        
    } catch (Exception e) {
        throw new BestCentroidsException(className+".getK: No se ha podido calcular K."+e.getMessage());
    }
}

/**
     * Metodo que determina la media aritmetica de una columna determinada de una matriz
     * @param datas Matriz de datos
     * @param j Columna sobre la que se desea obtener la media aritmetica
     * @return Devuelve la media aritmetica de la columna indicada
     */
protected double calculateMean(double[][] datas, int j){
    double summation=0;
    int numRows=datas.length;     //n
    for (int i=0;i<numRows;i++) {
        summation+=datas[i][j];
    }
    return (summation/numRows);
}

/**
     * Metodo que se carga los ejemplos duplicados para poder inicializar la matriz de 
     * centroides inicial de forma correcta.
     * @param datas Matriz de datos inicial
     * @return Devuelve la misma matriz de datos, pero eliminando los ejemplos duplicados. Esta
     * matriz sera la matriz de centroides inicial en el proceso de aplicar el algoritmo BestCentroids.
     */
    protected double[][] removeDuplicates(double[][] datas) {
        int filas=0;
        for (int i=0;i<datas.length;i++) {

            for (int j=i+1;j<datas.length;j++) {

                int k=0;
                boolean salida=false;
                if (datas[i][0]!=Double.POSITIVE_INFINITY) {
                    while ((k<datas[0].length)&&(!salida)) {
                        if ((datas[i][k]!=datas[j][k])||(datas[i][k]==Double.POSITIVE_INFINITY))
                            salida=true;
                        else
                            k++;
                    }
                    if (salida==false) {
//                        //System.out.println("Borrado "+Arrays.toString(datas[i])+"\n"+Arrays.toString(datas[j])+"\n-----------");
                        for (k=0;k<datas[0].length;k++) {
                            datas[j][k]=Double.POSITIVE_INFINITY;
                        }
                    }
                }
            }
            if (datas[i][0]!=Double.POSITIVE_INFINITY)
                filas++;
        }
        double[][] centroides=new double[filas][datas[0].length];
        int indice=0;
        for (int i=0;i<datas.length;i++) {
            if (datas[i][0]!=Double.POSITIVE_INFINITY) {
                for (int j=0;j<datas[0].length;j++) {
                    centroides[indice][j]=datas[i][j];
                }
                indice++;
            }
        }

        return centroides;
    }
}