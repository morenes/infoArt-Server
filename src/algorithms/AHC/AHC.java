package algorithms.AHC;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import models.fcsModel.*;
import models.fcsModel.Constants.DistanceType;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import utility.*;

/**
 * Clase que implementa el algoritmo AHC (Agglomerative Hierarchical Clustering)
 * @author David Gil Galvan
 */
public class AHC {
    /**
     * Nombre de la clase
     */
    protected String className=this.getClass().getName();
    AHCParams params;

    public AHC(AHCParams pParams){
        params = pParams;
    }

    
    /**
     * Metodo que ejecuta el algoritmo AHC (Agglomerative hierarchical clustering)
     * @param fcs Fuzzy Cluster Set inicial a partir del cual se aplica el algoritmo
     * @param c Numero de centroides que se quieren obtener
     * @return Se devuelve el nuevo Fuzzy Cluster Set con el numero de centroides indicado
     */
    public FuzzyClusterSet execute(FuzzyClusterSet fcs, int c) {
        try {
            // Se obtiene el numero de centroides desde el FCS
            double[][] centroids=fcs.getCentroids();
            int N=centroids.length;
            
            //System.out.println("Ejecutando AHC hasta llegar desde N:"+N+" hasta c:"+c);

//            double[][] centroidsNew=null;
            int storeInterval = 50;
            int lastStoredIteration = N;

            // Desde el conjunto inicial de datos que constituyen los centroides iniciales
            // hasta que se obtiene el n�mero de centroides deseados
            for (int t=N;t>c;t--) {
                // Se obtiene la nueva matriz de disimilitud
                //System.out.println("Obteniendo la matrix disimilitud. t = " + t + ", c = " + c);
                double D[][]=getMatrixDissimilarities(fcs);

                int centroidi=0;
                int centroidj=1;
//                // Se determinan que centroides son los mas similares
                for (int i=0;i<D.length;i++) {
                    for (int j=i+1;j<D[0].length;j++) {
                        if (D[centroidi][centroidj]>D[i][j]) {
                            centroidi=i;
                            centroidj=j;
                        }
                    }
                }

                // Se combinan los dos centroides senalados en uno solo en buscando el punto medio
                fcs.mergeCentroids(centroidi, centroidj);

                // Cada storeInterval iteraciones, guardo el modelo temporal.
                if((lastStoredIteration - storeInterval)>= t){
                    // Guardo temporalmente el FCS generado para no perder datos en caso de caida del sistema.
                    try {
                        FileOutputStream fos = new FileOutputStream(params.getXMLPath()+params.getExecutionName()+"_"+t+".dat");
                        ObjectOutputStream out = new ObjectOutputStream(fos);
                        out.writeObject(fcs);
                        out.close();          

                        //System.out.println("AHC's temporal FCS persisted.");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } 
                    

                    lastStoredIteration = t;
                    // Y borro el de la iteracion anterior.
                    //System.out.print("Borrando modelo " + (t+storeInterval)+"...");
                    try{
                        int aux = t+storeInterval;
                        File tempFile = new File(params.getXMLPath()+params.getTempFileName()+params.getExecutionName()+"_"+aux+".xml");
                        if(tempFile.exists()){
                            tempFile.delete();
                            //System.out.println("OK!");
                        }
                        else{
                            //System.out.println("No existe el fichero!");
                        }
                    }catch(Exception e){
                        //System.out.println(e);
                    }
                }
            }
                        
            //System.out.println("TERMINO AHC");
            // Se devuelve el nuevo Fuzzy Cluster Set
            return fcs;
        } catch (Exception e) {
            System.err.print(className+".execute: Se ha producido un error al ejecutar el algoritmo AHC.\n"+e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
       
    
    /**
     * Metodo que obtiene la matriz de disimilitud. Esta matriz se calcula a partir de
     * la distancia euclidea entre los centroides y el numero de ejemplos que formen parte
     * de cada centroide.
     * @param fcs Fuzzy Cluster Set del que se quiere obtener su matriz de disimilitud.
     * @return Devuelve la matriz de disimilitud
     * @throws AHCException Devuelve una excepcion en caso que se produzca algun error
     */
    protected double[][] getMatrixDissimilarities(FuzzyClusterSet fcs) throws AHCException {
        try {
            // Se obtienen los centroides del FCS            
            double[][] centroids=fcs.getCentroids();
            // Se obtiene la matriz de pertenencia del FCS
            double[][] U=fcs.getU();
  
            int numCentroids=fcs.getNumCluster();//centroids.length;
            // N�mero de columnas de la matriz de centroides
            int numColum=centroids[0].length;
            // Futura matriz de disimilitud
            double[][] D=new double[numCentroids][numCentroids];
            double[][] tNorm=null;
            DistanceType distanceType= fcs.getDistanceType();
            
            if (!distanceType.equals(DistanceType.gustafson_kessel)) {
                tNorm=getTNorm(0,fcs);
            }
            // Para todos los centroides
            for (int i=0;i<numCentroids;i++) {
                D[i][i]=0;
                
                for (int j=i+1;j<numCentroids;j++) {
                    int ni=0;
                    int nj=0;
                    
                    // Se obtienen el numero de ejemplos que pertenencen a los cluster Xi y Xj
                    for (int k=0;k<U[0].length;k++) {
                        ni+=U[i][k];
                        nj+=U[j][k];
                    }
                    
                    double[][] z=new double[numColum][1];
                    for (int k=0;k<numColum;k++) {
                        z[k][0]=centroids[i][k]-centroids[j][k];
                    }

                    Array2DRowRealMatrix matrixZ = new Array2DRowRealMatrix(z);
                    RealMatrix transMatrixZ = matrixZ.transpose();
                    
                    // Se obtiene la T-Norma a aplicar
                    if (distanceType.equals(DistanceType.gustafson_kessel)) {
                        tNorm=getTNorm(0,fcs);
                    }
                    
                    
                    RealMatrix zProduct = transMatrixZ.multiply(new Array2DRowRealMatrix(tNorm));
                    RealMatrix zAux = zProduct.multiply(matrixZ);
                    double distance = Math.sqrt(zAux.getEntry(0, 0));
                    
                    if (ni+nj==0) {
                        //System.out.println("AHC.getMatrixDissimilarities: ni+nj es cero.Division por cero");
                        D[i][j]=0;
                        D[j][i]=0;
                    } else {
                        D[i][j]=Math.sqrt(((double)(2*ni*nj))/((double)(ni+nj)))*distance;
                        D[j][i]=Math.sqrt(((double)(2*ni*nj))/((double)(ni+nj)))*distance;
                    }
                    
                }
            }
            // Se devuelve la matriz de disimilitud
            return D;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AHCException(className+".getmatrixDissimilarities: Se ha producido un error al obtener la matriz de disimilitud.\n");
        }
    }
            
    /**
     * Devuelve la matriz T-Norma a aplicar en funcion del tipo de distancia {euclidea, diagonal, mahalanobis,gustafson-kessel}. Actualmente este metodo siempre utiliza la distancia euclidea.
     * @param centroid Centroide sobre el que se quiere obtener la T-Norma si se utiliza la distancia gustafson-kessel
     * @param fcs Fuzzy Cluster Set desde el que se determina la norma a aplicar en funcion del tipo de distancia seleccionada
     * @return Devuelve la matriz T-Norma a aplicar
     * @throws AHCException Devuelve una excepcion en caso que se produzca algun error
     */
    protected double[][] getTNorm(int centroid, FuzzyClusterSet fcs) throws AHCException{
        //models.fcsModel.Constants.DistanceType distanceType=FCS.getDistanceType();
        models.fcsModel.Constants.DistanceType distanceType=models.fcsModel.Constants.DistanceType.euclidean;
        if (distanceType.equals(models.fcsModel.Constants.DistanceType.euclidean)) {
            return getMatrixIdentity(fcs);
        }
        if (distanceType.equals(models.fcsModel.Constants.DistanceType.diagonal)) {
            return getMatrixVariances(fcs);
        }
        if (distanceType.equals(models.fcsModel.Constants.DistanceType.mahalanobis)) {
            return getMatrixInvCovarianzas(fcs);
        }
         if (distanceType.equals(Constants.DistanceType.gustafson_kessel)) {
            return getGK(centroid, fcs);
        }
        throw new AHCException(className+".getTNorm: PartitionedSpace no ha sido especificado");
    }
    
    /**
     * Metodo que devuelve la matriz identidad como T-norma a aplicar
     * @param fcs Fuzzy Cluster Set desde el que determinar el tamano de la matriz cuadrada
     * @return Devuelve la matriz identidad
     * @throws AHCException Devuelve una excepcion en caso que se produzca algun error
     */
    protected double[][] getMatrixIdentity(FuzzyClusterSet fcs) throws AHCException {
        try {
            double[][] datas=fcs.getPartitionedData();
            int numColum=datas[0].length;
            double[][] tNorma=new double[numColum][numColum];
            
            for (int i=0;i<numColum;i++) {
                for(int j=0;j<numColum;j++) {
                    if (i==j) tNorma[i][j]=1;
                    else tNorma[i][j]=0;
                }
            }
            return tNorma;
        } catch (Exception e) {
            throw new AHCException(className+".getMatrixIdentity: Se ha producido un error al obtener la matriz identidad."+e.getMessage());
        }
    }
    
    /**
     * Metodo que devuelve como T-norma a aplicar la matriz de varianzas
     * @param fcs Fuzzy Cluster Set desde el que determinar el tama�o de la matriz cuadrada
     * @return Devuelve la matriz de varianzas
     * @throws AHCException Devuelve una excepcion en caso que se produzca algun error
     */
    protected double[][] getMatrixVariances(FuzzyClusterSet fcs) throws AHCException {
        try {
            double[][] datas=fcs.getPartitionedData();
            int numColum=datas[0].length;
            double[][] tNorma =new double[numColum][numColum];
            
            for (int i=0;i<numColum;i++) {
                for(int j=0;j<numColum;j++) {
                    if (i==j) {
                        double mean=calculateMean(datas, j);
                        double variance=calculateVariance(datas,mean, j);
                        if ((Math.pow((1/variance),2))<(0.1*Math.pow(10, -15)))
                            throw new AHCException(className+".getMatrixVariances: Variance of component "+j+" is zero");
                        tNorma[i][j]=Math.pow((1/variance),2);
                    } else tNorma[i][j]=0;
                }
            }
            return tNorma;
        } catch (Exception e) {
            throw new AHCException(className+".getMatrixIdentity: Se ha producido un error al obtener la matriz identidad."+e.getMessage());
        }
    }
    
    /**
     * Metodo que devuelve como T-norma a aplicar la matriz de covarianzas
     * @param fcs Fuzzy Cluster Set desde el que determinar el tama�o de la matriz cuadrada
     * @return Devuelve la matriz de covarianzas
     * @throws AHCException Devuelve una excepcion en caso que se produzca algun error
     */
    protected double[][] getMatrixInvCovarianzas(FuzzyClusterSet fcs) throws AHCException  {
        try {
            double[][] datas=fcs.getPartitionedData();
            int numColum=datas[0].length;     //p
            int numRows=datas.length;     //n
            
            double sum;
            
            double[][] tNorma=new double[numColum][numColum];
            
            for (int i=0;i<numColum;i++) {
                for (int j=i;j<numColum;j++) {
                    tNorma[i][j] = 0.0;
                    sum = 0.0;
                    
                    double meani=calculateMean(datas, i);
                    double meanj=calculateMean(datas, j);
                    for (int k=0;k<numRows;k++)
                        sum += (datas[k][i]-meani)*(datas[k][j]-meanj);
                    tNorma[i][j] = sum/numRows;
                    tNorma[j][i] = tNorma[i][j];
                }
            }
            return utility.MatrixOperation.invert(tNorma);
        } catch (Exception e) {
            throw new AHCException(className+".getMatrixInvCovarianzas: Se ha producido un error al obtener la matriz inversa de covarianzas."+e.getMessage());
        }
    }
    
    /**
     * Metodo que calcula la media de una columna determinada de una matriz de valores
     * @param datas Conjunto de datos sobre el que se calculara la media
     * @param j Columna sobre la que se calcula la media
     * @return Devuelve la media obtenida sobre la columna j de la matriz de datos
     */
    protected double calculateMean(double[][] datas, int j){
        double summation=0;
        int numRows=datas.length;
        for (int i=0;i<numRows;i++) {
            summation+=datas[i][j];
        }
        return (summation/numRows);
    }
    
    /**
     * Metodo que calcula la varianza sobre una columna determinada de una matriz de valores.
     * @return Devuelve la varianza obtenida sobre la columna j de los datos
     * @param mean Media de la columna de matriz de valores sobre la que se quiere calcular la varianza
     * @param datas Conjunto de datos sobre el que se calcula la varianza
     * @param j Columna sobre la que se calcula la varianza
     */
    protected double calculateVariance(double[][] datas, double mean, int j){
        double summation=0;
        int numRows=datas.length;     //n
        for (int i=0;i<numRows;i++) {
            summation+=Math.pow((datas[i][j]-mean),2);
        }
        return (summation/numRows);
    }
    
    /**
     * Metodo que devuelve la matriz de covarianzas del algoritmo Gustafson-Kessel
     * @return Devuelve la matriz de covarianzas del algoritmo Gustafson-Kessel
     * @param centroid Centroide del que se desea obtener la matriz de covarianzas del algoritmo Gustafson-Kessel
     * @param fcs Fuzzy Cluster Set
     * @throws algorithms.AHC.AHCException Excepcion
     */
    public double[][] getCovarianceMatrixGK(int centroid, FuzzyClusterSet fcs ) throws AHCException {
        try {
            double[][] U=fcs.getU();
            double[][] datas=fcs.getPartitionedData();
            double[][] centroids=fcs.getCentroids();
            int numRows=datas.length;
            int numColum=datas[0].length;
            
            double[][] F=new double[numColum][numColum];
            // Para cada ejemplo
            double memberTotal=0;
            for (int i=0;i<numRows;i++){
                memberTotal+=Math.pow(U[centroid][i],fcs.getFuzziness());
            }
            for (int i=0;i<numRows;i++){
                double[][] z=new double[numColum][1];
                // Se calcula la distancia al centroide
                for (int k=0;k<numColum;k++) {
                    z[k][0]=datas[i][k]-centroids[centroid][k];
                    
                }
                
                double[][] zTraspt=MatrixOperation.trasponse(z);
                double[][] zAux=MatrixOperation.product(z,zTraspt);
                utility.MatrixOperation.productMatrixScalar(Math.pow(U[centroid][i],fcs.getFuzziness()),zAux);
                F=utility.MatrixOperation.sum(F,zAux);
                
            }
            F=utility.MatrixOperation.productMatrixScalar((1.0/memberTotal),F);
            return F;
            
        } catch (Exception e) {
            throw new AHCException(className+".getCovarianceMatrixGK: Se ha producido un error al obtener CoVariance Matrix Gustafson-Kessel."+e.getMessage());
        }
    }
    
    /**
     * Metodo que devuelve la T-Norma del algoritmo Gustafson-Kessel para un centroide en concreto
     * @return Devuelve la T-Norma del algoritmo Gustafson-Kessel para un centroide en concreto
     * @param centroid Centroide del que se desea obtener la T-Norma
     * @param fcs Fuzzy Cluster Set
     * @throws algorithms.AHC.AHCException Excepcion
     */
    protected double[][] getGK(int centroid, FuzzyClusterSet fcs) throws AHCException {
        try {
            double[][] covarianceMatrixGK=this.getCovarianceMatrixGK(centroid,fcs);
            
            Matrix coVarianceMatrixGK=new Matrix(covarianceMatrixGK);
            
            double result=fcs.getFi()[centroid]*Math.pow(utility.Matrix.determinant(coVarianceMatrixGK),fcs.getNumCluster());
            double[][] invertCovarianceMatrixGK=utility.MatrixOperation.invert(covarianceMatrixGK);
            return utility.MatrixOperation.productMatrixScalar(result,invertCovarianceMatrixGK);
        } catch (Exception e) {
            throw new AHCException(className+".getGK: Se ha producido un error al obtener T-Norma-i Gustafson-Kessel."+e.getMessage());
        }        
    }
}