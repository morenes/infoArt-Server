/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithms.BestCentroids;

import algorithms.AHC.AHCParams;
import algorithms.PartitionedSpace;
import java.util.List;

/**
 * Clase que encapsula los parametros de entrada de BestCentroids
 * @author Fernando Terroso Saenz
 */
public class BestCentroidsParams {

    /* Umbral para determinar cuando se finaliza la busqueda en el Bestcentroids/ */
    private double umbral;
    /* Umbral para determinar el intervalo alrededor de la mitad de K */
    private double alfa;
    /* Numero maximo de centroides en BestCentroids */
    private int cmax;
    private double epsilon;
    /* Numero maximo de iteraciones del FCM */
    private int maxIteration;
    /* Directorio donde volcar los ficheros XML con los fcs geneerados */
    private String outputDir;
    
    private PartitionedSpace space;
    
    List<ClusterInfo> clusters;

    private AHCParams ahcParams;


    /* Constructor con valores por defecto */
    public BestCentroidsParams(){
        cmax=30;
        umbral=0.05;
        alfa=0.07;
        epsilon=0.0001;
        maxIteration=10;
        outputDir = "";
    }

    /* Constructor con valores personalizados */
    public BestCentroidsParams(
            String pOutputDir,
            double pUmbral,
            double pAlfa,
            int pCmax,
            double pEpsilon,
            int pMaxIteration){

        setOutputDir(pOutputDir);
        cmax=pCmax;
        umbral=pUmbral;
        alfa=pAlfa;
        epsilon=pEpsilon;
        maxIteration=pMaxIteration;
        
    }

    /****** METODOS GET ******/

    public String getOutputDir() {
        return outputDir;
    }

    public double getAlfa() {
        return alfa;
    }

    public int getCmax() {
        return cmax;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public int getMaxIteration() {
        return maxIteration;
    }

    public double getUmbral() {
        return umbral;
    }

    public AHCParams getAhcParams() {
        return ahcParams;
    }

    public List<ClusterInfo> getClusters() {
        return clusters;
    }

    public PartitionedSpace getSpace() {
        return space;
    }    
    
    /****** FIN METODOS GET ******/


    /****** METODOS SET ******/

    public void setOutputDir(String pOutputDir) {
        outputDir = pOutputDir;

        if(!outputDir.endsWith("/")){
            outputDir += "/";
        }
    }

    public void setAlfa(double alfa) {
        this.alfa = alfa;
    }

    public void setCmax(int cmax) {
        this.cmax = cmax;
    }

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public void setMaxIteration(int maxIteration) {
        this.maxIteration = maxIteration;
    }

    public void setUmbral(double umbral) {
        this.umbral = umbral;
    }

    public void setAhcParams(AHCParams ahcParams) {
        this.ahcParams = ahcParams;
    }

    public void setClusters(List<ClusterInfo> clusters) {
        this.clusters = clusters;
    }

    public void setSpace(PartitionedSpace space) {
        this.space = space;
    }        
   
    /****** FIN METODOS SET ******/


    @Override
    public String toString(){
        StringBuilder out = new StringBuilder();
        out.append("--Parametros para Best Centroids--\n");
        out.append("Umbral: ");
        out.append(umbral );
        out.append("\n");
        out.append("Alfa: ");
        out.append(alfa);
        out.append("\n");
        out.append("Cmax: ");
        out.append(cmax);
        out.append("\n");
        out.append("Epsion: ");
        out.append(epsilon);
        out.append("\n");
        out.append("Max. iterations: ");
        out.append(maxIteration);
        out.append("\n");
        out.append(ahcParams);
        out.append("--Fin parametros para Best Centroids--\n");
        return out.toString();
    }
}
