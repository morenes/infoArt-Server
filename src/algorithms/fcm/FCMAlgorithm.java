package algorithms.fcm;
import models.fcsModel.*;

/**
 * Implementation algorithm FCM (Fuzzy C-Means)
 * @author David Gil Galvan
 */
public class FCMAlgorithm {
    /**
     * Nombre de la clase
     */
    protected String className=this.getClass().getName();
    /**
     * Umbral que determina cuando finaliza el algoritmo FCM
     */
    protected double epsilon;
    
    /**
     * Numero maximo de iteraciones que se permite al algoritmo FCM antes de finalizar su ejecucion
     */
    protected int maxIteration;
    
    /**
     * Matriz de datos sobre los que opera el FCM
     */
    protected double[][] datas;
    
    /**
     * Metodo que ejecuta el algoritmo FCM
     * @param fcs Fuzzy Cluster Set
     * @param epsilon Umbral que determina cuando finaliza el algoritmo FCM
     * @param maxIteration Numero maximo de iteraciones que se permite al algoritmo FCM antes de finalizar su ejecucion     
     */
    public void optimize(FuzzyClusterSet fcs, double epsilon, int maxIteration) {
        try {
            if (epsilon<0)            
                throw new FCMException(className+".optimize: El valor del epsilon es incorrecto. Debe ser mayor que 0");
            this.epsilon=epsilon;
            if (maxIteration<=0)
                throw new FCMException(className+".optimize: El numero de iteraciones es incorrecto. Debe ser mayor que 0");
            this.maxIteration=maxIteration;
            datas=fcs.getPartitionedData();
            /* Run the updates iteratively */
            int nIterationsF=0;
            
            double performanceNew=Double.POSITIVE_INFINITY;
            double performanceOld=Double.NEGATIVE_INFINITY;                
            ////System.out.println("Empiezo FCM");
            do {                
                performanceOld=performanceNew;
                nIterationsF++;
                // Compute the cluster prototypes (means)                                
                updateCentroids(fcs);
                ////System.out.println("FIN ADAPTO");
                performanceNew=fcs.getPerformance(); 
            } while ((Math.abs(performanceNew-performanceOld)> performanceNew*epsilon )&&(nIterationsF<maxIteration));            

        } catch (Exception e)  {
            System.err.println(className+".optimize: Se ha producido un error al ejecutar el Algoritmo FCM. ");
            e.printStackTrace();
            System.exit(0);
        }        
    }
    
    /**
     * Metodo que recalcula los cluster de centroides         
     * @param fcs Fuzzy Cluster Set sobre el que se recalculan los centroides
     * @throws FCMException Devuelve una excepcion en caso que se produzca algun error
     */
    protected void updateCentroids(FuzzyClusterSet fcs) throws FCMException {
        try {            
            double[][] U=fcs.getUOld();
            int numRows=datas.length;
            int numColum=datas[0].length;
            
            double[] numerator=new double[numColum];
            double[] denominator=new double[numColum];
            double fuzziness=fcs.getFuzziness();
            double[][] centroids=new double[fcs.getNumCluster()][numColum];
            double[][] centroidsAnt=fcs.getCentroids();
            /* For each cluster */
            for (int i=0; i < fcs.getNumCluster(); i++)  {
                
                for (int x=0; x < numColum; x++) {
                    numerator[x]=0;
                    denominator[x]=0;
                }
                boolean salida=false;
                /* Calculate numerator */
                // Si m es ==1
                if (fuzziness==1) {
                    
                    int k=0;
                    // Se comprueba la funcion de pertenencia de cada ejemplo a 
                    // un centroide
                    while ((k < numRows)&&(!salida)) {                        
                        if (U[i][k]!=0) {
                            salida=true;
                        } else k++;
                    }
                }
                // Si existe alg�n U[i][k]!=0 o el m>1 se recalculan los centroides
                // Es decir si ese centroide tiene algun peso sobre los ejemplos
                if ((salida==true)||(fuzziness!=1)) {
                    for (int k=0; k < numRows; k++) {
                        for (int x=0; x < numColum ; x++) {
                            numerator[x] += Math.pow(U[i][k], fuzziness) * datas[k][x];
                        }
                    }
                    
                    /* Calculate denominator */
                    for (int k=0; k < numRows; k++) {
                        for (int x=0; x < numColum; x++) {
                            denominator[x] += Math.pow(U[i][k], fuzziness);
                        }
                    }
                    
                    /* Calculate V */
                    for (int x=0; x < numColum; x++) {
                        centroids[i][x]= numerator[x] / denominator[x];                        
                    }
                }
                // Si no existe U[i][k]!=0
                else { // Los centroides son los mismos que antes
                    for (int x=0; x < numColum; x++) {
                        centroids[i][x]=centroidsAnt[i][x];
                    }
                }
            }  /* endfor: C clusters */
            fcs.setCentroids(centroids);
        } catch (Exception e){
            throw new FCMException(className+".updateCentroids: Se ha producido un error al actualizar los centroides."+e.getMessage());
        }
    }
}