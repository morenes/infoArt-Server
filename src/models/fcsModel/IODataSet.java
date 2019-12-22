package models.fcsModel;


/**
 * IODataSet
 * @author David Gil Galvan
 */
public class IODataSet {
    
    private static final String MODEL_HEAD = "IODATASET_";
    
    protected String modelIdentifier;
    
    /**
     * Datos de entrada
     */
    protected double[][] inputData;
    /**
     * Datos de salida
     */
    protected double[] outputData;
    
    /**
     * Establece los datos de salida
     * @param OutputData Datos de salida
     */
    public void setOutputData(double[] OutputData) {
        this.outputData=OutputData;        
    }
    
    /**
     * Obtiene los datos de salida
     * @return Datos de salida
     */
    public double[] getOutputData() {
        return outputData;
    }
    
    /**
     * Obtiene el tamano del vector de datos de salida
     * @return Devuelve el tamano del vector de datos de salida
     */
    public int getOutputDataLenght() {
        return outputData.length;
    }
    
    /**
     * Establece los datos de entrada
     * @param inputData Matriz de datos de entrada
     */
    public void setInputData(double[][] inputData) {
        this.inputData=inputData;        
    }
    
    /**
     * Obtiene los datos de entrada
     * @return Datos de entrada
     */
    public double[][] getInputData() {
        return inputData;
    }

    /**
     * Obtiene el numero de atributos (columnas) que contiene la matriz de datos de entrada
     * @Devuelve el numero de columnas de la matriz de datos de entrada
     * @return Devuelve el numero de columnas de la matriz de datos de entrada
     */
    public int getInputDataNColumns() {
        return inputData[0].length;
    }
    /**
     * Obtiene el numero de ejemplos(filas) que constituyen la matriz de datos de entrada
     * @Devuelve Devuelve el numero de filas de la matriz de datos de entrada
     * @return Devuelve el numero de filas de la matriz de datos de entrada
     */
    public int getInputDataNRows() {
        return inputData.length;
    }
    
    /**
     * Construye una cadena de texto del vector de salidas para poder mostrarla por pantalla
     * @return Devuelve la cadena de texto correspondiente al vector de salidas
     */
    public String OutputDatatoString(){
        String texto=" ";
        for(int i=0; i<outputData.length; i++){
            texto+="\t "+(double)Math.round(1000*outputData[i])/1000;
        }
        texto+="\n";
        return texto;
    }
    
    /**
     * Construye una cadena de texto de la matriz de entradas para poder mostrarla por pantalla
     * @return Devuelve la cadena de texto correspondiente a la matriz de entradas
     */
    public String InputDatatoString(){
        String texto="\n";
        for(int i=0; i<inputData.length; i++){
            for(int j=0; j<inputData.length; j++){
                texto+="\t "+(double)Math.round(1000*inputData[i][j])/1000;
            }
            texto+="\n";
        }
        texto+="\n";
        return texto;
    }

    public String getTotalModelIdentifier() {
        return MODEL_HEAD+modelIdentifier;
    }
    
    public String getPartialModelIdentifier(){
        return modelIdentifier;
    }
    
    public void setPartialModelIdentifier(String modelIdentifier) {
        this.modelIdentifier = modelIdentifier;
    } 
    
}