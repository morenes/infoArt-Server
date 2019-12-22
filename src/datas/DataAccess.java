package datas;
/**
 * Interfaz que seala los metodos necesarios para obtener los datos de entrenamiento 
 * y de evaluacion
 * @author David Gil Galvan
 */
public interface DataAccess {
    /**
     * 
     * @return 
     */
    public double[][] getInputDataLearn();
    
    /**
     * 
     * @return 
     */
    public double[] getOutputDataLearn();
        
    /**
     * 
     * @return 
     */
    public double[][] getInputDataEvaluate();
    
    /**
     * 
     * @return 
     */
    public double[] getOutputDataEvaluate();
    /** 
     * Para obtener las tuplas
     */
    public void parse(String path, double learnRatio, int[] utilizarEntrada, int[] utilizarSalida, int limit) throws Exception;
    public int numExamples(String path) throws Exception;
}
