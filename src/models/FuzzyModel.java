package models;

/**
 * Interfaz FuzzyModel
 * @author David Gil Galvan
 */
public interface FuzzyModel {
     
    public String getTotalModelIdentifier();
    public String getPartialModelIdentifier();
    public void setSerializationPath(String xmlPath);
    public void writeDown() throws java.io.IOException;    
    public int getNumRules();
    public double[] getTruth(double[] x);
    public double makeInference(double[] x)throws Exception;
}
