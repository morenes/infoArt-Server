package models.fcsModel;
import algorithms.PartitionedSpace;
import java.util.*;
import java.io.*;
import models.ConsequentType;
import models.FiringType;
import models.FuzzyModel;
import models.TCoNormType;
import models.fcsModel.Constants.DistanceType;
import utility.*;

import org.apache.xerces.parsers.DOMParser;

import org.w3c.dom.*;
import models.tskModel.*;

/**
 * Clase abstracta que representa a un Fuzzy Cluster Set
 * @author David Gil Galvan & Fernando Terroso Saenz
 */
public abstract class FuzzyClusterSet extends IODataSet implements Serializable, FuzzyModel{
    
    protected String modelHead = "FCS_";
    
    /* Nombre de la clase */
    protected String className=this.getClass().getName();
    /* Fuzziness */
    protected double fuzziness;
    /* Numero de cluster */
    protected int numCentroids;
    /* Matriz de centroides */
    protected double[][] centroids;
    /* Espacio de particion {I,O,IO} */
    PartitionedSpace pSpace;
    /* Tipo de distancia utilizada {Euclidea, Diagonal. Mahalanobis} */
    protected Constants.DistanceType distanceType;
    /* Tipo de t-conorma a utilizar en la inferencia */
    protected TCoNormType tCoNorm;
    
    public static String dtdPath = "."+File.separator;
    
    public static String xmlPath = "."+File.separator;
    
    protected double[][] UOld=null;
    /* Nombre del dtd del FCS */
    protected static String dtdFuzzyClusterSet="fcsmodel";    
    /* Atributo que indica si se han modificado los centroides del FCS */
    protected boolean changed=true;
    /* Fi del algoritmo Gustafson-Kessel */
    protected double[] fi;

    //Matriz con la pertenencia de los ejemplos a los centroides
    double[][] U = null;
    /**
     * Metodo que incializa los atributos de la clase
     * @param inputData
     * @param outputData
     * @param fuzziness
     * @param distanceType
     * @param pCentroids
     * @param fi
     * @param pTConorm
     * @param pPSpace Tipo del espacio de la particion (I, O, IO)
     */
    protected void init(
            String pModelHead,
            String identifier,
            double[][] inputData,
            double[] outputData,
            double fuzziness,
            DistanceType distanceType,
            double[][] pCentroids,
            double[] fi,
            TCoNormType pTConorm,
            PartitionedSpace pPSpace) {

        try {

            className=this.getClass().getName();
            
            modelHead = pModelHead;
            modelIdentifier = identifier;
            
            this.inputData=inputData;
            this.outputData=outputData;
            
            this.centroids=obtenerCentroidesIniciales(pCentroids);
            this.numCentroids= this.centroids.length;
            if (numCentroids<1)
                throw new FCSException(className+".FuzzyClusterSet: El numero de cluster es incorrecto. Este debe ser mayor o igual que 1.");
            this.pSpace= pPSpace;
            this.tCoNorm = pTConorm;

            if (fuzziness<1)
                throw new FCSException(className+".FuzzyClusterSet: El valor de fuzziness es incorrecto. Este debe ser mayor o igual que 1.");
            this.fuzziness=fuzziness;
            this.distanceType=distanceType;
            this.UOld=generateUOld();
            this.fi=fi;
            checkMatrixCentroids();

        } catch (Exception e) {
            System.err.println(className+".FuzzyClusterSet: Se ha producido un error al crear la instancia." +"\n");
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    /**
     * Metodo que comprueba que la matriz de centroides sea correcta
     * @throws FCSException Devuelve una excepcion en caso que se produzca algun error
     */
    protected void checkMatrixCentroids() throws FCSException{
        if (numCentroids!=centroids.length)
            throw new FCSException(className+".checkMatrixCentroids: La matriz de centroides es incorrecta ya que no coindice con el numero de cluster "+numCentroids+"!="+centroids.length);
        double[][] datas= getPartitionedData();
        if (centroids[0].length!=datas[0].length)
            throw new FCSException(className+".checkMatrixCentroids: La matriz de centroides es incorrecta ya que no coindice con el numero de atributos del espacio de busqueda "+centroids[0].length+"!="+datas[0].length);
    }
    
    /**
     * Calcula la distancia del ejemplo a cada uno de los centroides
     * @param example Ejemplo del que se quiere obtener la distancia
     * @return Devuelve un array de dobles con la distancia a cada uno de los centroides
     * @throws FCSException Devuelve una excepcion en caso que se produzca algún error
     */
    protected double[] distance(double[] example) throws FCSException{
        try {
            switch(this.pSpace){
                case I:
                    if (example.length!=inputData[0].length) {
                        throw new FCSException(className+".distance: Las dimensiones del ejemplo no coinciden con las dimensiones en el espacio de partición");
                    }
                    break;
                case IO:
                    if ((example.length!=inputData[0].length)&&(example.length!=(inputData[0].length+1))) {
                        throw new FCSException(className+".distance: Las dimensiones del ejemplo no coinciden con las dimensiones en el espacio de partición");
                    }
                    break;
                case O:
                    if (example.length!=1) {
                        throw new FCSException(className+".distance: Las dimensiones del ejemplo no coinciden con las dimensiones en el espacio de partición");
                    }
                    break;
            }

            // Obtiene la norma a aplicar            
            double[][] tNorm=null;            
            if (!distanceType.equals(Constants.DistanceType.gustafson_kessel)) {                
                // Como no es GK da lo mismo lo que se le pase                
                double[][] aux=getTNorm(0,0,false);                
                if (pSpace.equals(PartitionedSpace.IO)&&(example.length==inputData[0].length)) {
                    tNorm=new double[aux.length-1][aux[0].length-1];                    
                    for (int k=0;k<aux.length-1;k++) {
                        for (int j=0;j<aux[0].length-1;j++) {
                            tNorm[k][j]=aux[k][j];
                        }
                    }
                } else {                                       
                    tNorm=utility.MatrixOperation.copyMatrix(aux);
                }
            }
            // Vector con las distancias a cada uno de los centroides
            double[] D=new double[numCentroids];
            // Para cada centroide
            for (int i=0; i<numCentroids;i++) {
                double[][] zAux;
                double[][] z=new double[example.length][1];
                
                for (int k=0;k<example.length;k++) {
                    z[k][0]=example[k]-centroids[i][k];
                }
                
                double[][] zTraspt=MatrixOperation.trasponse(z);
                // Obtiene la norma a aplicar
                if (distanceType.equals(Constants.DistanceType.gustafson_kessel)) {
                    double[][] aux=getTNorm(i,fi[i],true);
                    if (pSpace.equals(PartitionedSpace.IO)&&(example.length==inputData[0].length)) {
                        tNorm=new double[aux.length-1][aux[0].length-1];
                        for (int k=0;k<aux.length-1;k++) {
                            for (int j=0;j<aux[0].length-1;j++) {
                                tNorm[k][j]=aux[k][j];
                            }
                        }
                    } else {
                        tNorm=utility.MatrixOperation.copyMatrix(aux);
                    }
                }
                
                double[][] zProduct=MatrixOperation.product(zTraspt,tNorm);
                zAux=MatrixOperation.product(zProduct, z);
                D[i]=Math.sqrt(zAux[0][0]);                
            }
            
            return D;
        } catch (Exception e) {
            throw new FCSException(className+".distance: Se ha producido un error al calcular la distancia."+e.getMessage());
        }
    }
    
    /**
     * Calcula la matriz de distancias de los ejemplos de entrenamiento a cada 
     * uno de los centroides
     * @return Devuelve una matriz de dobles con la distancia a cada uno de 
     * los centroides de cada uno
     * de los ejemplos de enetrenamiento
     * @throws FCSException Devuelve una excepcion en caso que se produzca algun error
     */
    protected double[][] distance() throws FCSException{
        try {
            double[][] datas = getPartitionedData();
            int numRows=datas.length;
            int numColum=datas[0].length;
              
            double[][] D=new double[numCentroids][numRows];
            // Para cada cluster
            double[][] tNorm=null;
            if (!distanceType.equals(Constants.DistanceType.gustafson_kessel)) {
                // Como no es GK da lo mismo lo que se le pase
                   tNorm=getTNorm(0,0,false);
            }
            for (int i=0; i<numCentroids;i++) {
                // Para cada ejemplo
                if (distanceType.equals(Constants.DistanceType.gustafson_kessel)) {
                    tNorm=getTNorm(i,fi[i],true);
                }
                for (int j=0;j<numRows;j++){
                    double[][] z=new double[numColum][1];
                    // Se calcula la distancia al centroide
                    for (int k=0;k<numColum;k++) {
                        z[k][0]=datas[j][k]-centroids[i][k];
                        
                    }
                    
                    double[][] zTraspt=MatrixOperation.trasponse(z);
                    // Obtiene la TNorma a aplicar
                    
                    double[][] zProduct=MatrixOperation.product(zTraspt,tNorm);                    
                    double[][] zAux=MatrixOperation.product(zProduct,z);                    
                    
                    D[i][j]=Math.sqrt(zAux[0][0]);
                    
                }
                
            }
            
            return D;
        } catch (Exception e) {
            throw new FCSException(className+".distance: Se ha producido un error al calcular la matriz de distancias."+e.getMessage());
        }
    }
    
    /**
     * Determina si un ejemplo con un vector de distancia determinado es un centroide
     * @param D Vector de distancias a los centroides del ejemplo
     * @return Devuelve el numero de cluster para el que el ejemplo es centroide. -1 en caso que no sea
     * centroide.
     */
    protected int numCentroids(double[] D ) {
        int i=0;
        int numCen=0;
        while (i < numCentroids) {
            if ( D[i] ==0 ) numCen++;
            i++;
        }
        return numCen;
    }
    
    /**
     * Determina el numero de centroides con distancia 0 a un ejemplo
     * @param k Ejemplo del que se desea conocer el numero de centroides donde la distancia del ejemplo al centroide es 0
     * @param D Vector de distancias a los centroides del ejemplo
     * @return Devuelve el numero de cluster para el que el ejemplo es centroide. 0 en caso que no sea
     * centroide.
     */
    protected int numCentroids(double[][] D, int k) {
        int i=0;
        int numCen=0;
        while (i < numCentroids) {
            if ( D[i][k] ==0 ) numCen++ ;
            i++;
        }
        return numCen;
    }

    /**
     * Obtiene un vector de las pertenencias del ejemplo a cada cluster
     * @param example Ejemplo sobre el que se quiere obtener el vector de pertenencias a cada cluster
     * @return Devuelve el vector de pertenencias del ejemplo a cada cluster
     * @throws FCSException Excepcion en caso que se produzca cualquier error
     */
    public double[] getMembership(double[] example) throws FCSException{
        try {
            
            switch(this.pSpace){
                case I:
                    if (example.length!=inputData[0].length) {
                        throw new FCSException(className+".distance: Las dimensiones del ejemplo no coinciden con las dimensiones en el espacio de partición");
                    }
                    break;
                case IO:
                    if ((example.length!=inputData[0].length)&&(example.length!=(inputData[0].length+1))) {
                        throw new FCSException(className+".distance: Las dimensiones del ejemplo no coinciden con las dimensiones en el espacio de partición");
                    }
                    break;
                case O:
                    if (example.length!=1) {
                        throw new FCSException(className+".distance: Las dimensiones del ejemplo no coinciden con las dimensiones en el espacio de partición");
                    }
                    break;
            }

            double[] Uexample=new double[numCentroids];                        
            double[] Dist=distance(example);            
            /* Special case: If Example is equal to a Cluster Centroid,
            then U=1.0 for that cluster and 0 for all others */
            int numCentroids=numCentroids(Dist);            
            // Si el numero de centroides es mayor que 0
            if (numCentroids>0) {                
                // Si se trata de particiones crisp
                if (fuzziness==1) {
                    
                    Vector Cent=new Vector();
                    for (int i=0; i < this.numCentroids; i++) {
                        if (Dist[i]==0) {
                            Cent.add(new Integer(i));
                        }
                        Uexample[i]=0.0;
                    }
                    //Random rand=new Random();
                    //Integer elegido=(Integer)Cent.get(rand.nextInt(Cent.size()));
                    Integer elegido=(Integer)Cent.get(0);
                    Uexample[elegido.intValue()]=1.0;
                    // Si la particion es difusa
                } else {                    
                    for (int i=0; i < this.numCentroids; i++) {
                        if ( Dist[i]==0)
                            Uexample[i]=1.0/((double)numCentroids);
                        else
                            Uexample[i]=0.0;
                    }
                }
                // Si no existe ningun ejemplo con distancia igual a 0
            } else {
                // Particion es crisp
                if (fuzziness==1) {
                    int max=0;
                    int numCen=1;
                    for (int i=0; i < this.numCentroids; i++) {
                        if (this.numCentroids==1) {
                            Uexample[i]=1;
                        } else{
                            if (Dist[max]>Dist[i]) {
                                max=i;
                                numCen=1;
                            } else {
                                if (Dist[max]==Dist[i])
                                    numCen++;
                            }
                        }
                    }
                    if (this.numCentroids>1) {
                        Vector Cent=new Vector();
                        for (int i=0; i < this.numCentroids; i++) {
                            if (Dist[i]==Dist[max]) {
                                Cent.add(new Integer(i));
                            }
                            Uexample[i]=0.0;
                        }
                        //Random rand=new Random();
                        //Integer elegido=(Integer)Cent.get(rand.nextInt(Cent.size()));
                        Integer elegido=(Integer)Cent.get(0);
                        Uexample[elegido.intValue()]=1.0;
                    }
                } else {
                    /* For each class */                    
                    for (int i=0; i < this.numCentroids; i++) {
                        double summation=0;
                        if (this.numCentroids==1)  {
                            Uexample[i]=1;
                        } else {
                            /* Calculate summation */
                            for (int j=0; j < this.numCentroids; j++) {
                                summation += Math.pow( Dist[i]/ Dist[j], (2.0/ (fuzziness-1)));
                            }
                            
                            /* Weight is 1/sum */
                            double newU=1.0/(double)summation;
                            Uexample[i]=newU;
                        }
                    }                    
                }
            } /* endfor n */
            
            return Uexample;
        } catch (Exception e) {
            throw new FCSException(className+".getMembership: Se ha producido un error al calcular la pertencia de un ejemplo a cada cluster."+e.getMessage());
        }
    }
    
    /**
     * Devuelve la partenencia de un ejemplo a un cluster determinado
     * @return Devuelve la pertenencia del ejemplo al cluster especificado
     * @param example Ejemplo sobre el que se quiere calcular su funcion de pertenencia a un cluster determinado
     * @param cluster Cluster sobre el que se quiere calcular la funcion de pertenencia del ejemplo
     * @throws FCSException Excepcion en caso que se produzca cualquier error
     */
    public double getMembership(double[] example, int cluster) throws FCSException{
        try {

            switch(this.pSpace){
                case I:
                    if (example.length!=inputData[0].length) {
                        throw new FCSException(className+".distance: Las dimensiones del ejemplo no coinciden con las dimensiones en el espacio de partición");
                    }
                    break;
                case IO:
                    if ((example.length!=inputData[0].length)&&(example.length!=(inputData[0].length+1))) {
                        throw new FCSException(className+".distance: Las dimensiones del ejemplo no coinciden con las dimensiones en el espacio de partición");
                    }
                    break;
                case O:
                    if (example.length!=1) {
                        throw new FCSException(className+".distance: Las dimensiones del ejemplo no coinciden con las dimensiones en el espacio de partición");
                    }
                    break;
            }

            double[] Dist=distance(example);
            /* Special case: If Example is equal to a Cluster Centroid,
            then U=1.0 for that cluster and 0 for all others */
            int numCentroids=numCentroids(Dist);
            if (this.numCentroids==1) return 1.0;
            if ( numCentroids>0) {
                if (fuzziness==1) {
                    Vector Cent=new Vector();
                    for (int i=0; i < this.numCentroids; i++) {
                        if (Dist[i]==0) {
                            Cent.add(new Integer(i));
                        }
                    }
                    //Random rand=new Random();
                    //Integer elegido=(Integer)Cent.get(rand.nextInt(Cent.size()));
                    Integer elegido=(Integer)Cent.get(0);
                    if (elegido==cluster) return 1.0;
                    else return 0.0;
                    
                } else {
                    if (Dist[cluster]==0) return 1.0/((double)numCentroids);
                    else return 0.0;
                }
            } else {
                if (fuzziness==1) {
                    int max=0;
                    int numCen=1;
                    for (int i=0; i < this.numCentroids; i++) {
                        if (Dist[max]>Dist[i]) {
                            max=i;
                            numCen=1;
                        } else {
                            if (Dist[max]==Dist[i])
                                numCen++;
                        }
                    }
                    Vector Cent=new Vector();
                    for (int i=0; i < this.numCentroids; i++) {
                        if (Dist[i]==Dist[max]) {
                            Cent.add(new Integer(i));
                        }
                    }
                    //Random rand=new Random();
                    //Integer elegido=(Integer)Cent.get(rand.nextInt(Cent.size()));
                    Integer elegido=(Integer)Cent.get(0);
                    if (elegido==cluster) return 1.0;
                    else return 0.0;
                    
                } else {
                    double summation=0;
                    /* Calculate summation */
                    for (int j=0; j < this.numCentroids; j++) {
                        summation += Math.pow( Dist[cluster]/ Dist[j], (2.0/ (fuzziness-1)));
                    }
                    
                    /* Weight is 1/sum */
                    return (1.0/(double)summation);
                }
            } /* endfor n */
        } catch (Exception e) {
            throw new FCSException(className+".getMembership: Se ha producido un error al calcular la pertencia de un ejemplo a un cluster determinado."+e.getMessage());
        }
    }
    
    /**
     * Devuelve la matriz Tnorma a aplicar en funcion del tipo de distancia {euclidea, diagonal, mahalanobis}
     *
     * @return Devuelve la matriz TNorma a aplicar
     * @param centroid Centroide del que se quiere obtener la Tnorma para el algoritmo GK.
     * Si la distancia no es GK no se utiliza.
     * @param fi Fi del algoritmo GK. Si la distancia no es GK no se utiliza.
     * @param b si es false es porque el valro de fi no tiene importancia (no es GK) si es true si
     * @throws FCSException Devuelve una excepción en caso que se produzca algun error
     */
    public double[][] getTNorm(int centroid, double fi, boolean b) throws FCSException{
        
        switch(distanceType){
            case euclidean:
                return getMatrixIdentity();
            case diagonal:
                return getMatrixVariances();
            case mahalanobis:
                return getMatrixInvCovarianzas();
            case gustafson_kessel:
                return getGK(centroid, fi);
        }
        
        throw new FCSException(className+".getTNorm: PartitionedSpace no ha sido especificado");
    }
    
    /**
     * Metodo que devuelve como T-norma a aplicar la matriz identidad
     * @return Devuelve la matriz identidad
     * @throws FCSException Devuelve una excepcion en caso que se produzca algun error
     */
    protected double[][] getMatrixIdentity() throws FCSException {
        try {
            
            double[][] datas=getPartitionedData();
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
            throw new FCSException(className+".getMatrixIdentity: Se ha producido un error al obtener la matriz identidad."+e.getMessage());
        }
    }
    
    /**
     * Metodo que devuelve como T-norma a aplicar la matriz de varianzas
     * @return Devuelve la matriz de varianzas
     * @throws FCSException Devuelve una excepcion en caso que se produzca algun error
     */
    protected double[][] getMatrixVariances() throws FCSException {
        try {
            double[][] datas=getPartitionedData();
            int numColum=datas[0].length;
            double[][] tNorma =new double[numColum][numColum];
            
            for (int i=0;i<numColum;i++) {
                for(int j=0;j<numColum;j++) {
                    if (i==j) {
                        double mean=calculateMean(datas, j);
                        double variance=calculateVariance(datas,mean, j);
                        if ((Math.pow((1/variance),2))<(0.1*Math.pow(10, -15)))
                            throw new FCSException(className+".getMatrixVariances: Variance of component "+j+" is zero");
                        tNorma[i][j]=Math.pow((1/variance),2);
                    } else {
                        tNorma[i][j]=0;
                    }
                }
            }
            return tNorma;
        } catch (Exception e) {
            throw new FCSException(className+".getMatrixIdentity: Se ha producido un error al obtener la matriz identidad."+e.getMessage());
        }
    }
    
    /**
     * Metodo que devuelve como T-norma a aplicar la matriz de covarianzas
     * @return Devuelve la matriz de covarianzas
     * @throws FCSException Devuelve una excepcion en caso que se produzca algun error
     */
    protected double[][] getMatrixInvCovarianzas() throws FCSException  {
        try {
            double[][] datas=getPartitionedData();
            int numColum=datas[0].length;     //p
            int numRows=datas.length;     //n
            
            double sum;
            
            double[][] tNorm=new double[numColum][numColum];
            double[] means=new double[numColum];
            
            for (int i=0;i<numColum;i++) {
                means[i]=calculateMean(datas,i);
            }
            
            for (int i=0;i<numColum;i++) {
                for (int j=0;j<numColum;j++) {
                    tNorm[i][j]=0.0;
                }
            }
            
            for (int i=0;i<numRows;i++) {
                double[][] matrix=new double[numColum][1];
                for (int j=0;j<numColum;j++) {
                    matrix[j][0]=datas[i][j]-means[j];
                }
                
                double[][] matrixT=utility.MatrixOperation.trasponse(matrix);
                tNorm=utility.MatrixOperation.sum(tNorm, utility.MatrixOperation.product(matrix, matrixT));
            }
            tNorm=utility.MatrixOperation.productMatrixScalar((1.0/(double)numRows),tNorm);
            return utility.MatrixOperation.invert(tNorm);
        } catch (Exception e) {
            throw new FCSException(className+".getMatrixInvCovarianzas: Se ha producido un error al obtener la matriz inversa de covarianzas."+e.getMessage());
        }
    }
    
    /**
     * Metodo que calcula la media sobre una columna de una matriz de valores
     * @param datas Conjunto de datos sobre el que se calculara la media
     * @param j Columna sobre la que se calculara la media
     * @return Devuelve la media obtenida sobre la columna j de los datos
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
     * Metodo que calcula varianza sobre una columna de una matriz de valores y dada la media de esa columna
     * @return Devuelve la varianza obtenida sobre la columna j de los datos
     * @param mean Media de de la columna j de los datos
     * @param datas Conjunto de datos sobre el que se calculara la varianza
     * @param j Columna sobre la que se calculara la varianza y sobre la que se calcula la media
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
     * Devuelve el espacio donde se esta realizando el clustering
     * @return Devuelve el espacio donde se esta realizando el clustering
     */
    public PartitionedSpace getPSpace() {
        return pSpace;
    }
    
    /**
     * Devuelve el tipo de distancia del FCS
     * @return Devuelve el tipo de distancia del FCS
     */
    public Constants.DistanceType getDistanceType() {
        return distanceType;
    }
    
    /**
     * Metodo que establece el valor de m
     * @param fuzziness Valor de m
     */
    public void setFuzziness(double fuzziness) {
        this.fuzziness=fuzziness;
    }
    
    /**
     * Metodo que establece el valor de xmlPath, directorio donde se guardara
     * el XML con el modelo
     * @param xmlPath Nueva ruta del directorio
     */
    public void setSerializationPath(String xmlPath) {
        FuzzyClusterSet.xmlPath = xmlPath;
    }

    public String getSerializationPath(){
        return FuzzyClusterSet.xmlPath;
    }
    /**
     * Metodo que establece el valor de dtdPath, directorio donde se guardara
     * el DTD con el modelo
     * @param dtdPath Nuevo directorio
     */
    public void setDtdPath(String dtdPath) {
        FuzzyClusterSet.dtdPath = dtdPath;
    }

    /**
     * Devuelve el valor de m
     * @return Devuelve el valor de m
     */
    public double getFuzziness() {
        return fuzziness;
    }
    
    /**
     * Metodo que devuelve el numero de cluster. Metodo Adaptador
     * @return Devuelve el numero de cluster
     */
    public int getNumRules() {
        return getNumCluster();
    }
    
    /**
     * Metodo que devuelve el numero de cluster
     * @return Devuelve el numero de cluster
     */
    public int getNumCluster() {
        return numCentroids;
    }
    
    public double[] getTruth(double[] x) {
        try {
            return this.getMembership(x);
        } catch (Exception e) {
            System.out.println(className+".getTruth: Error al calcular getTruth()");
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }
    
    public void mergeCentroids(int centroid1Pos, int centroid2Pos) throws Exception{
        
        double[][] centroidsAux=new double[centroids.length-1][centroids[0].length];
        // Se combinan los dos centroides iniciales en la primera fila de la matriz 
        // de centroides
        for (int j=0;j<centroids[0].length;j++) {
            centroidsAux[0][j]=(centroids[centroid1Pos][j]+centroids[centroid2Pos][j])/2.0;            
        }
        
        // Se completa el resto de la matriz de centroides con el resto de centroides
        // antiguos, sin tener en cuenta los dos centroides que ya han sido seleccionados
        int fila=1;
        for (int i=0;i<centroids.length;i++){                        
            if ((i!=centroid1Pos)&&(i!=centroid2Pos)) {
                System.arraycopy(centroids[i], 0, centroidsAux[fila], 0, centroids[0].length);
                fila++;
            }
        }
        setCentroids(centroidsAux);
        generateU();
    }
    

    protected void generateU() throws FCSException{
        
        double[][] datas=getPartitionedData();
        U=new double[numCentroids][datas.length];
        double[][] D=distance();

        /* For each example in the dataset */
        for ( int k=0; k <datas.length ; k++) {
        /* Special case: If Example is equal to a Cluster Centroid,
        then U=1.0 for that cluster and 0 for all others */
            int nCentroids=numCentroids(D, k);
            if (nCentroids>0) {
                Vector cent=new Vector();
                for (int i=0; i < numCentroids; i++) {
                    if (fuzziness==1) {
                        if (D[i][k]==0) {
                            cent.add(new Integer(i));
                        }
                        U[i][k]=0.0;
                    } else {
                        if ( D[i][k] == 0 ){
                            U[i][k]=1.0/((double)nCentroids);
                        }
                        else
                            U[i][k]=0.0;
                    }
                }
                if (fuzziness==1){
                    Integer elegido=(Integer)cent.get(0);
                    U[elegido.intValue()][k]=1.0;
                }
            } else {
                /* For each class */
                if (fuzziness==1) {
                    int max=0;
                    for (int i=0; i < this.numCentroids; i++) {
                        if (this.numCentroids==1) {
                            U[i][k]=1;
                        } else{
                            if (D[max][k]>D[i][k]) {
                                max=i;
                            }
                        }
                    }

                    if (this.numCentroids>1) {

                        Vector Cent=new Vector();

                        for (int i=0; i < this.numCentroids; i++) {
                            if (D[i][k]==D[max][k]) {
                                Cent.add(new Integer(i));
                            }
                            U[i][k]=0.0;
                        }
                        Integer elegido=(Integer)Cent.get(0);
                        U[elegido.intValue()][k]=1.0;
                    }

                } else {
                    for (int i=0; i < numCentroids; i++) {

                        double summation=0;
                        if (numCentroids==1) {
                            U[i][k]=1;
                        } else{
                            /* Calculate summation */
                            for (int j=0; j < numCentroids; j++) {
                                summation += Math.pow( D[i][k]/ D[j][k] , (2.0/ (fuzziness-1)));
                            }

                            /* Weight is 1/sum */
                            double newU=1.0/(double)summation;

                            U[i][k]=newU;
                        }

                    } /* endfor n */
                }
            }
        }

    }
    
    /**
     * Devuelve la matriz de pertenencia de los ejemplos a cada cluster
     * @return Devuelve la matriz U de pertenencia de los ejemplos a cada cluster
     * @throws FCSException Devuelve una excepcion en caso que se produzca algun error
     */
    public double[][] getU() throws FCSException {

        if(U == null){
            generateU();
        }                          
        return U;
    }
    
    /**
     * Metodo que devuelve la matriz U que hab�a antes de actualizar los centroides
     * @return Devuelve la matriz U que hab�a antes de actualizar los centroides
     */
    public double[][] getUOld() {
        return UOld;
    }
    
    public void setUOld(double[][] UOld) {
        this.UOld=UOld;
    }
    
    /**
     * Establece la matriz de centroides
     * @param centroids Matriz de centroides
     */
    public void setCentroids(double[][] centroids) {
        try {
            UOld=utility.MatrixOperation.copyMatrix(getU());
        } catch (Exception e) {
            System.err.println(this.className+".setCentroids:Error al copiar Matriz");
            System.exit(-1);
        }
        this.centroids=centroids;
        this.numCentroids=centroids.length;
        changed=true;
        
        
    }
    
    /**
     * Metodo que devuelve la matriz de centroides
     * @return Devuelve la matriz de centroides
     */
    public double[][] getCentroids() {
        return centroids;
    }
    
    /**
     * Metodo que calcula el performance del FCS
     * @return Devuelve el performance del FCS
     * @throws FCSException Devuelve una excepcion en caso que se produzca algun error
     */
    public double getPerformance() throws FCSException{
        try {
            int numRows=inputData.length;
            double summation=0;
            double[][] D=distance();
            double[][] U=getU();
            
            
            for (int i=0;i<numCentroids;i++) {
                for (int k=0;k<numRows;k++) {
                    summation+=(Math.pow(U[i][k],fuzziness) * Math.pow(D[i][k],2) );
                }
            }
            return summation;
        } catch (Exception e) {
            throw new FCSException(className+".getPerformance: Se ha producido un error al calcular el performance del FCS."+e.getMessage());
        }
    }
    
    /**
     * Metodo que calcula la fuzzy within-cluster scatter matrix
     * @return Devuelve la fuzzy within-cluster scatter matrix
     * @throws FCSException Devuelve una excepcion en caso que se produzca algún error
     */
    public double[][] SW() throws FCSException {
        try {
            double[][] datas=getPartitionedData();
            int numColum=datas[0].length;     //p
            int numRows=datas.length;     //n
            
            double[][] U=this.getU();
            
            
            double sum;
            
            double[][] sw=new double[numColum][numColum];
            
            for (int i=0;i<numColum;i++) {
                for (int j=0;j<numColum;j++) {
                    sw[i][j]=0.0;
                }
            }
            
            for (int i=0;i<numCentroids;i++) {
                for (int k=0;k<numRows;k++) {
                    double[][] matrix=new double[numColum][1];
                    for (int j=0;j<numColum;j++) {
                        matrix[j][0]=datas[k][j]-centroids[i][j];
                        
                    }
                    double[][] matrixT=utility.MatrixOperation.trasponse(matrix);
                    double[][] aux=utility.MatrixOperation.product(matrix, matrixT);
                    aux=utility.MatrixOperation.productMatrixScalar(Math.pow(U[i][k], fuzziness), aux);
                    sw=utility.MatrixOperation.sum(sw, aux);
                }
            }
            return sw;
        } catch (Exception e) {
            throw new FCSException(className+".SW: Se ha producido un error al obtener Fuzzy within-cluster scatter matrix."+e.getMessage());
        }
    }
    
    /**
     * Metodo que calcula la fuzzy between-cluster scatter matrix
     * @return Devuelve la fuzzy between-cluster scatter matrix
     * @throws FCSException Devuelve una excepcion en caso que se produzca algun error
     */
    public double[][] SB() throws FCSException {
        try {
            
            int numColum=centroids[0].length;
            int numRows=centroids.length;
            double[][] datas=getPartitionedData();
            int N=datas.length;
            double[][] U=this.getU();
            
            
            double sum;
            
            double[][] sb=new double[numColum][numColum];
            double[] means=new double[numColum];
            for (int i=0;i<numColum;i++) {
                means[i]=calculateMean(centroids,i);
            }
            
            for (int i=0;i<numColum;i++) {
                for (int j=0;j<numColum;j++) {
                    sb[i][j]=0.0;
                }
            }
            
            for (int i=0;i<numCentroids;i++) {
                double u=0;
                for (int k=0;k<N;k++) {
                    u+=Math.pow(U[i][k],fuzziness);
                }
                double[][] matrix=new double[numColum][1];
                for (int j=0;j<numColum;j++) {
                    matrix[j][0]=centroids[i][j]-means[j];
                }
                double[][] matrixT=utility.MatrixOperation.trasponse(matrix);
                double[][] aux=utility.MatrixOperation.product(matrix, matrixT);
                aux=utility.MatrixOperation.productMatrixScalar(u, aux);
                sb=utility.MatrixOperation.sum(sb, aux);
            }
            
            return sb;
        } catch (Exception e) {
            throw new FCSException(className+".SB: Se ha producido un error al obtener Fuzzy between-cluster scatter matrix."+e.getMessage());
        }
    }
    
    /**
     * Metodo que devuelve la matriz de datos con la que deben trabajar los algoritmos segun del espacio de trabajo, ya sea
     * entrada, salida o entrada/salida
     * @throws FCSException Devuelve una excepcion en caso que se produzca algun error
     * @return Devuelve la matriz con los datos segun el espacio del clustering
     */
    public abstract double[][] getPartitionedData() throws FCSException;
    
    /**
     * Metodo que devuelve una copia del FCS actual
     * @return Devuelve una copia del FCS actual
     */
    @Override
    public abstract Object clone();
    
    /**
     * Método que se encarga de crear un TSK Model Gaussiano a partir de un FCS

     * @param consequentType Tipo de consecuente: de primer grado o de grado 0
     * @param firingType Tipo de inferencia
     * @return Devuelve el TSK Model Gaussiano correspondiente
     * @throws FCSException Se produce una excepcion en el caso que se produzca algun error en el proceso de generar el TSK Model Gaussiano
     */
    public abstract TSKModel createGaussianTSKFromFCS(ConsequentType consequentType, FiringType firingType) throws FCSException;
    
    /**
     * Metodo que construye un TSKModel Triangular a partir de un FCS
     * @return Devuelve el TSKModel Triangular
     * @param consequentType Tipo de Consecuente
     * @param firingType Tipo de inferencia
     * @param method Metodo para generar el TSKModel
     * @throws FCSException Excepcion en caso que se produzca cualquier error
     */
    public abstract TSKModel createTriangularTSKFromFCS(ConsequentType consequentType, FiringType firingType, byte method) throws FCSException;
    
    /**
     * Metodo que construye un TSKModel Trapezoidal con Consecuente de orden 0 y con espacio de particion del clustering de salida a partir de un FCS
     * @param consequentType Tipo de Consecuente
     * @param firingType Tipo de inferencia
     * @param umbralInf Umbral inferior
     * @param umbralSup Umbral superior
     * @return Devuelve el TSKModel Trapezoidal
     * @throws FCSException Devuelve una excepcion en caso que se produzca algun error
     */
    public abstract TSKModel createTrapezoidalTSKFromFCS(ConsequentType consequentType, FiringType firingType, double umbralSup, double umbralInf) throws FCSException;
    
    /**
     * Devuelve la cabecera XML comun a todos los documentos de este tipo; en la misma incluimos el
     * nombre y localizacion del fichero DTD de definicion de datos
     * @return La cabecera del documento XML
     */
    protected String getXMLHead() {
        
        String head = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        head += "<!DOCTYPE " + getTotalModelIdentifier() + " SYSTEM \"" + dtdPath + dtdFuzzyClusterSet + ".dtd\""+">";
        
        return head;
    }
    
    
    
    
    /**
     * Vuelca a disco en un fichero XML el contenido generado por generateXML
     * @param uri Nombre del fichero donde se volvara el fuzzy cluster set
     * @throws java.io.IOException Si se produce algun error al escribir en disco
     */
    public void writeDown() throws java.io.IOException {
        String uri= getTotalModelIdentifier()+".xml";
        File f = new File(xmlPath);
        if(!f.exists()){
            f.mkdirs();
        }
        uri=uri.substring(6);
        f = new File(xmlPath+uri);
        //System.out.println(xmlPath+uri);
        if (!f.exists()){
        	f.createNewFile();
        }
        PrintWriter writer = new PrintWriter(new FileOutputStream(xmlPath + uri));
        generateXML(writer);
        writer.close();
    }
        
    /**
     * Devuelve en una cadena el documento XML correspondiente a este modelo
     * @return Una cadena con el documento XML correspondiente a este modelo
     * @see mtl.xml.XMLItemImpl
     */
    public String generateXML(PrintWriter writer) throws java.io.IOException {
        String xml = getXMLHead();
        StringBuilder xmlContent = new StringBuilder();
        xml += "\n<FCSMODEL FUZZINESS= \""+fuzziness +"\" PARTIALID=\""+getPartialModelIdentifier()+"\" NUMCLUSTER= \""+
                this.numCentroids+"\" NUMEXAMPLES= \""+inputData.length +"\" NUMATRIBUTES= \""+inputData[0].length+"\" TCONORM= \"" + tCoNorm+"\" PSPACE= ";

        switch(this.pSpace){
            case I:
                xml+="\"INPUT\"";
                break;
            case IO:
                xml+="\"INPUT/OUTPUT\"";
                break;
            case O:
                xml+="\"OUTPUT\"";
                break;
        }
        
        xml += " DISTANCETYPE=";
        
        if (distanceType==models.fcsModel.Constants.DistanceType.euclidean) {
            xml+="\"EUCLIDEAN\"";
        }
        if (distanceType==models.fcsModel.Constants.DistanceType.diagonal) {
            xml+="\"DIAGONAL\"";
        }
        if (distanceType==models.fcsModel.Constants.DistanceType.mahalanobis) {
            xml+="\"MAHALANOBIS\"";
        }
        if (distanceType==models.fcsModel.Constants.DistanceType.mahalanobis) {
            xml+="\"GK\"";
        }
        
        xml +=">\n" +
                "<JAVACLASS>" + getClass().getName() + "</JAVACLASS>\n" +
                "<CENTROIDES>" ;
        for (int i=0;i<centroids.length;i++) {
            xml += "\n<CENTROIDE INDEX= \""+ i +"\">";
            for (int j=0;j<centroids[0].length;j++) {
                xml+="\n<VALUE INDEX= \""+j+"\">"+centroids[i][j]+"</VALUE>";
            }
            xml += "\n</CENTROIDE>";
        }
        xml +="</CENTROIDES>";
        
        if(fi != null){
            xml +="<FIS>";
            for (int i=0;i<fi.length;i++) {
                xml += "\n<FI INDEX= \""+ i +"\">"+fi[i]+"</FI>";
            }
            xml += "</FIS>";
        }
                
        xmlContent.append(xml);                
        xml="";
        xml+="\n<EXAMPLES>";
        for (int i=0; i<inputData.length; i++) {
            xml += "\n<EXAMPLE INDEX= \""+ i +"\">";
            for (int j=0;j<inputData[0].length;j++) {
                xml+= "\n<VALUE INDEX= \""+ j +"\">"+inputData[i][j]+"</VALUE>";
            }
            xml+="\n<VALUEOUTPUT>"+outputData[i]+"</VALUEOUTPUT>\n</EXAMPLE>";
            xmlContent.append(xml);
            xml="";
        } 
        xml += "\n</EXAMPLES>\n";                        
        xml +="\n<PREVIUS_U>";
        for (int i=0; i<UOld.length; i++) {
            xml += "\n<CENTROID_U INDEX= \""+ i +"\">";
            for (int j=0;j<UOld[0].length;j++) {
                xml+= "\n<VALUE INDEX= \""+ j +"\">"+UOld[i][j]+"</VALUE>";
            }
            xml+="\n</CENTROID_U>";
            xmlContent.append(xml);
            xml="";
        }
        
        xmlContent.append("\n</PREVIUS_U>\n</FCSMODEL>\n");
        if (writer!=null){
	        writer.print(xmlContent);
	        writer.flush();        
        }
        return xml;
    }
    
    
    static double fuzzinessF=0;
    static int numClusterF=0;
    static String modelIdentifierF = "";
    static double[][] InputDataF=null;
    static double[] OutputDataF=null;
    static double[][] UOldF=null;
    static PartitionedSpace pSpaceF=null;
    static models.fcsModel.Constants.DistanceType distanceTypeF=null;
    static double[][] centroidsF=null;
    static double[] fiF=null;
    static TCoNormType tCoNormF= null;
    static String classNameF="";
    static int numExamplesF;
    static int numAtributesF;
    
    
    /**
     * Parsing necesario para reconocer el documento XML correspondiente a este modelo (y recrear asi
     * el modelo TSK)
     * @param doc Nodo que esta siendo parseado actualmente del documento XML
     * @return Devuelve el fuzzy cluster set correspondiente al fichero xml
     */
    protected static void parse(Node doc) {
        
        try {

            
            
                switch (doc.getNodeType()) {
                    case Node.ELEMENT_NODE:
                        //System.out.println(doc.getNodeName());
                        if (doc.getNodeName().equals("FCSMODEL")) {
                            
                            NamedNodeMap attributes = doc.getAttributes();
                            Node node = attributes.getNamedItem("FUZZINESS");
                            fuzzinessF= Double.valueOf(node.getNodeValue()).doubleValue();
                            
                            node = attributes.getNamedItem("PARTIALID");
                            modelIdentifierF = node.getNodeValue();
                            
                            node = attributes.getNamedItem("NUMCLUSTER");
                            numClusterF= Integer.valueOf(node.getNodeValue()).intValue();
                            fiF=new double[numClusterF];

                            node = attributes.getNamedItem("NUMEXAMPLES");
                            numExamplesF = Integer.valueOf(node.getNodeValue()).intValue();
                            
                            node = attributes.getNamedItem("NUMATRIBUTES");
                            numAtributesF= Integer.valueOf(node.getNodeValue()).intValue();

                            node = attributes.getNamedItem("TCONORM");
                            tCoNormF= TCoNormType.valueOf(node.getNodeValue());
                            
                            node = attributes.getNamedItem("PSPACE");
                            String pspace=node.getNodeValue();
                            if (pspace.equals("INPUT")) {
                                pSpaceF= PartitionedSpace.I;
                                centroidsF=new double[numClusterF][numAtributesF];
                            }
                            if (pspace.equals("OUTPUT")) {
                                pSpaceF= PartitionedSpace.O;
                                centroidsF=new double[numClusterF][1];
                            }
                            if (pspace.equals("INPUT/OUTPUT")) {
                                pSpaceF= PartitionedSpace.IO;
                                centroidsF=new double[numClusterF][numAtributesF+1];
                            }
                            
                            node = attributes.getNamedItem("DISTANCETYPE");
                            String distance=node.getNodeValue();
                            if (distance.equals("EUCLIDEAN")) {
                                distanceTypeF=models.fcsModel.Constants.DistanceType.euclidean;
                            }
                            if (pspace.equals("DIAGONAL")) {
                                distanceTypeF=models.fcsModel.Constants.DistanceType.diagonal;
                            }
                            if (pspace.equals("MAHALANOBIS")) {
                                distanceTypeF=models.fcsModel.Constants.DistanceType.mahalanobis;
                            }
                            if (pspace.equals("GK")) {
                                distanceTypeF=models.fcsModel.Constants.DistanceType.gustafson_kessel;
                            }
                            InputDataF=new double[numExamplesF][numAtributesF];
                            OutputDataF=new double[numExamplesF];
                            UOldF=new double[numClusterF][numExamplesF];
                            
                        } else {
                            if (doc.getNodeName().equals("JAVACLASS")) {
                                classNameF=doc.getFirstChild().getNodeValue();
                                //System.out.println("NAME:"+className);
                            } else {
                                if (doc.getNodeName().equals("CENTROIDES")) {
                                    NodeList children = doc.getChildNodes();
                                    
                                    for (int k = 0; k < children.getLength(); k++) {
                                        
                                        Node nodoHijo=children.item(k);
                                        if (nodoHijo.getNodeName().equals("CENTROIDE")) {
                                            NamedNodeMap attributes = nodoHijo.getAttributes();
                                            
                                            Node node = attributes.getNamedItem("INDEX");
                                            int valork= Integer.valueOf(node.getNodeValue()).intValue();
                                            NodeList hijos= nodoHijo.getChildNodes();
                                            for (int j = 0; j < hijos.getLength(); j++) {
                                                if (hijos.item(j).getNodeName().equals("VALUE")) {
                                                    NamedNodeMap attributes2=hijos.item(j).getAttributes();
                                                    Node node2=attributes2.getNamedItem("INDEX");
                                                    int valorj= Integer.valueOf(node2.getNodeValue()).intValue();
                                                    centroidsF[valork][valorj]=Double.valueOf(hijos.item(j).getFirstChild().getNodeValue()).doubleValue();                                               
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    if (doc.getNodeName().equals("FIS")) {
                                        NodeList children = doc.getChildNodes();
                                        
                                        for (int k = 0; k < children.getLength(); k++) {
                                            
                                            Node nodoHijo=children.item(k);
                                            if (nodoHijo.getNodeName().equals("FI")) {
                                                NamedNodeMap attributes = nodoHijo.getAttributes();
                                                
                                                Node node = attributes.getNamedItem("INDEX");
                                                int valork= Integer.valueOf(node.getNodeValue()).intValue();
                                                NodeList hijos= nodoHijo.getChildNodes();
                                                for (int j = 0; j < hijos.getLength(); j++) {
                                                    if (hijos.item(j).getNodeName().equals("VALUE")) {
                                                        fiF[valork]=Double.valueOf(hijos.item(j).getFirstChild().getNodeValue()).doubleValue();
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        if (doc.getNodeName().equals("EXAMPLES")) {
                                            NodeList children = doc.getChildNodes();
                                            for (int k = 0; k < children.getLength(); k++) {
                                                Node nodoHijo=children.item(k);
                                                if (nodoHijo.getNodeName().equals("EXAMPLE")) {
                                                    NamedNodeMap attributes = nodoHijo.getAttributes();
                                                    Node node = attributes.getNamedItem("INDEX");
                                                    int valork= Integer.valueOf(node.getNodeValue()).intValue();
                                                    NodeList hijos= nodoHijo.getChildNodes();
                                                    for (int j = 0; j < hijos.getLength(); j++) {
                                                        if (hijos.item(j).getNodeName().equals("VALUE")) {
                                                            NamedNodeMap attributes2=hijos.item(j).getAttributes();
                                                            Node node2=attributes2.getNamedItem("INDEX");
                                                            int valorj= Integer.valueOf(node2.getNodeValue()).intValue();
                                                            InputDataF[valork][valorj]=Double.valueOf(hijos.item(j).getFirstChild().getNodeValue()).doubleValue();
                                                        } else {
                                                            if (hijos.item(j).getNodeName().equals("VALUEOUTPUT")) {
                                                                OutputDataF[valork]=Double.valueOf(hijos.item(j).getFirstChild().getNodeValue()).doubleValue();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            if (doc.getNodeName().equals("PREVIUS_U")) {
                                                NodeList children = doc.getChildNodes();
                                                for (int k = 0; k < children.getLength(); k++) {
                                                    Node nodoHijo=children.item(k);
                                                    if (nodoHijo.getNodeName().equals("CENTROID_U")) {
                                                        NamedNodeMap attributes = nodoHijo.getAttributes();
                                                        Node node = attributes.getNamedItem("INDEX");
                                                        int valork= Integer.valueOf(node.getNodeValue()).intValue();
                                                        NodeList hijos= nodoHijo.getChildNodes();
                                                        for (int j = 0; j < hijos.getLength(); j++) {
                                                            if (hijos.item(j).getNodeName().equals("VALUE")) {
                                                                NamedNodeMap attributes2=hijos.item(j).getAttributes();
                                                                Node node2=attributes2.getNamedItem("INDEX");
                                                                int valorj= Integer.valueOf(node2.getNodeValue()).intValue();
                                                                UOldF[valork][valorj]=Double.valueOf(hijos.item(j).getFirstChild().getNodeValue()).doubleValue();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            
                                        }
                                        
                                    }
                                }
                            }
                        }
                        break;
                }
            
                        
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }        
        NodeList children = doc.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            parse(children.item(i));
        }
    }
    
    /**
     * Recrea a partir de un documento XML la clase que lo genera
     * @param uri URI del documento a parsear
     * @throws Exception Si no se puede recrear la clase
     * @return Devuelve el fuzzy cluster set que se corresponde con el documento xml
     */
    public static FuzzyClusterSet createFuzzyClusterSetFromXML(String uri) throws Exception {


        if(!uri.endsWith(".xml")){
            uri = xmlPath + uri + ".xml";
        }
        DOMParser parser = new DOMParser();
        FuzzyClusterSet f=null;

        parser.parse(uri);

        Document doc = parser.getDocument();
        parse(doc);

        switch(pSpaceF){
            case I:
                f=new FuzzyClusterSetI(modelIdentifierF,InputDataF,OutputDataF, fuzzinessF,distanceTypeF,centroidsF,fiF, tCoNormF);
                f.setUOld(UOldF);
                break;
            case O:
                f=new FuzzyClusterSetO(modelIdentifierF, InputDataF,OutputDataF,fuzzinessF,distanceTypeF,centroidsF,fiF, tCoNormF);
                f.setUOld(UOldF);
                break;
            case IO:
                f=new FuzzyClusterSetIO(modelIdentifierF, InputDataF,OutputDataF,fuzzinessF,distanceTypeF,centroidsF,fiF, tCoNormF);
                f.setUOld(UOldF);
                break;
        }

        return f;
    }
    
    /**
     * Recrea a partir de un documento binario la clase que lo genera
     * @param uri URI del documento a parsear
     * @throws Exception Si no se puede recrear la clase
     * @return Devuelve el fuzzy cluster set que se corresponde con el documento xml
     */
    public static FuzzyClusterSet createFuzzyClusterSetFromBinary(String uri) throws Exception {
        FuzzyClusterSet f=null;
        try {
            FileInputStream fis = new FileInputStream(uri);
            ObjectInputStream in = new ObjectInputStream(fis);
            f = (FuzzyClusterSet) in.readObject();
            in.close();            
            
            System.out.println("FCS recovered from file.");
        } catch (Exception ex) {
            System.out.println("Error while getting FCS.");
            ex.printStackTrace();
        }
        
        return f;
    }
    
    /**
     * METODOS DE XMLITEMIMPL - end */


    
    
    /**
     * Metodo que se encarga de eliminar los centroides que se encuentren duplicados
     * @param datas Matriz de datos
     * @return Devuelve la misma matriz de centroides pero eliminando los centroides duplicados
     */
    protected double[][] obtenerCentroidesIniciales(double[][] datas) {
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
    
    /**
     * A�adido para poder ver los consequentes en I - 9/enero/2007
     * Metodo que devuelve un vector con los consecuentes de los centroides
     * @throws models.fcsModel.FCSException Excepcion
     * @return Devuelve un vector con los consecuentes de los centroides
     */
    public double[] getConsequents() throws FCSException{
        return null;
    }
    
    /**
     * Metodo que devuelve la matriz de covarianzas del algoritmo Gustafson-Kessel
     * @param centroid Centroide del que se desea obtener la matriz de covarianzas del algoritmo Gustafson-Kessel
     * @return Devuelve la matriz de covarianzas del algoritmo Gustafson-Kessel
     * @throws models.fcsModel.FCSException Excepcion
     */
    public double[][] getCovarianceMatrixGK(int centroid) throws FCSException {
        try {
            
            double[][] U=getUOld();
            double[][] datas=getPartitionedData();
            int numRows=datas.length;
            int numColum=datas[0].length;
            
            double[][] F=new double[numColum][numColum];
            // Para cada ejemplo
            double memberTotal=0;
            for (int i=0;i<numRows;i++){
                memberTotal+=Math.pow(U[centroid][i],this.fuzziness);
            }
            
            for (int i=0;i<numRows;i++){
                double[][] z=new double[numColum][1];
                // Se calcula la distancia al centroide
                for (int k=0;k<numColum;k++) {
                    z[k][0]=datas[i][k]-centroids[centroid][k];
                }
                
                double[][] zTraspt=MatrixOperation.trasponse(z);
                double[][] zAux=MatrixOperation.product(z,zTraspt);
                
                
                zAux=utility.MatrixOperation.productMatrixScalar(Math.pow(U[centroid][i],this.fuzziness),zAux);
                F=utility.MatrixOperation.sum(F,zAux);
                
            }
            
            F=utility.MatrixOperation.productMatrixScalar((1.0/memberTotal),F);
            return F;
            
        } catch (Exception e) {
            throw new FCSException(className+".getCovarianceMatrixGK: Se ha producido un error al obtener CoVariance Matrix Gustafson-Kessel."+e.getMessage());
        }
    }
    
    /**
     * Metodo que devuelve la T-Norma del algoritmo Gustafson-Kessel para un centroide en concreto
     * @param centroid Centroide del que se desea obtener la T-Norma
     * @param fi Fi concreto a aplicar para el centroide en concreto
     * @throws models.fcsModel.FCSException Excepcion
     * @return Devuelve la T-Norma del algoritmo Gustafson-Kessel para un centroide en concreto
     */
    protected double[][] getGK(int centroid, double fi) throws FCSException {
        try {
            
            double[][] covarianceMatrixGK=this.getCovarianceMatrixGK(centroid);
            
            Matrix coVarianceMatrixGK=new Matrix(covarianceMatrixGK);
            double result=Math.pow(fi*(utility.Matrix.determinant(coVarianceMatrixGK)),1.0/(this.numCentroids));
            double[][] invertCovarianceMatrixGK=utility.MatrixOperation.invert(covarianceMatrixGK);
            return utility.MatrixOperation.productMatrixScalar(result,invertCovarianceMatrixGK);
            
        } catch (Exception e) {
            throw new FCSException(className+".getGK: Se ha producido un error al obtener T-Norma-i Gustafson-Kessel."+e.getMessage());
        }
    }
    
    /**
     * Metodo que devuelve el Fi de todos los centroides
     * @return Devuelve un vector con el fi de cada uno de los centroide
     */
    public double[] getFi() {
        return fi;
    }

    /**
     * Metodo que inicializa los centroides para el FCS
     * @param inData Conjunto de datos de entrada
     * @param outData Conjunto de datos de salida
     * @param numCluster Numero de cluster
     * @param pSpace Espacio de particion
     * @return Matriz con los centroides para el FCS
     */
    public double[][] initCentroids(double[][] inData, double[] outData, int numCluster, PartitionedSpace pSpace) {

        double[][] centroids = null;
        double max_i,min_i,max,min;

        switch(pSpace){
            case I:
                centroids=new double[numCluster][inData[0].length];
                break;
            case O:
                centroids=new double[numCluster][1];
                break;
            case IO:
                centroids=new double[numCluster][inData[0].length+1];
                break;
        }

        if ((pSpace.equals(PartitionedSpace.I))||(pSpace.equals(PartitionedSpace.IO))) {

            for (int i=0;i<inData[0].length;i++) {
                double summation=0;
                max_i=inData[0][i];
                min_i=inData[0][i];
                for (int j=0;j<inData.length;j++) {
                    summation+=inData[j][i];
                    if (inData[j][i]>max_i) max_i=inData[j][i];
                    if (inData[j][i]<min_i) min_i=inData[j][i];
                }
                centroids[0][i]=(summation/inData.length);


        }}

        if (pSpace.equals(PartitionedSpace.IO)) {
            double summation=0;
            max=outData[0];
            min=outData[0];
            for (int i=0;i<outData.length;i++) {
                summation+=outData[i];
                if (outData[i]>max) max=outData[i];
                if (outData[i]<min) min=outData[i];
            }
            centroids[0][inData[0].length]=(summation/outData.length);

        }

        for (int i=1;i<numCluster;i++) {
            for (int j=0;j<centroids[0].length;j++) {
                if (Math.random()>0.5)
                    centroids[i][j]=centroids[0][j]+(Math.random()*centroids[0][j]);
                else
                    centroids[i][j]=centroids[0][j]-(Math.random()*centroids[0][j]);
            }
        }

        return centroids;
    }
    
    /**
     * Metodo que inicializa los U para el FCS
     * @return Matriz con las pertenencias para el FCS
     * @throws models.fcsModel.FCSException Excepcion
     */
    protected double[][] generateUOld() throws FCSException{
        double[][] datas=getPartitionedData();
        double[][] U=new double[numCentroids][datas.length];
        //double value=1.0/(double)datas.length;
        for (int j=0;j<datas.length;j++) {
            double suma=0;
            for (int i=0;i<numCentroids;i++) {
                double value=Math.random();
                if (suma+value>1.0)
                    value=0;
                U[i][j]=value;
                suma+=value;
            }
            if (suma!=1.0) {
                U[numCentroids-1][j]+=(1.0-suma);
            }
        }
        //System.out.println(utility.MatrixOperation.toStringMatrix(U));
        return U;
    }
    
    @Override
    public String getTotalModelIdentifier() {
        
        StringBuilder verboseIdentifier = new StringBuilder();
        verboseIdentifier.append(modelHead);
        verboseIdentifier.append(modelIdentifier);
        verboseIdentifier.append("_numclus_");
        verboseIdentifier.append(getNumCluster());
        verboseIdentifier.append("_m");
        verboseIdentifier.append(getFuzziness());
        
        return verboseIdentifier.toString();
         
    } 
}
