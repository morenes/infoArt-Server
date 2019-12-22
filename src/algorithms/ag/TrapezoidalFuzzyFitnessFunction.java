package algorithms.ag;
    
import org.jgap.*;
import models.tskModel.*;

/**
 * Clase que representa la funcion fitness para el TskModel Trapezoidal
 * @author David Gil Galvan
 */
public  class TrapezoidalFuzzyFitnessFunction extends FuzzyFitnessFunction{
    /**
     * Nombre de la clase
     */
    protected String className=this.getClass().getName();  
    
    /**
     * Constructor de la funcion de fitness trapezoidal
     * @param inputData Matriz de datos de entrada
     * @param outputData Vector de datos de salida
     * @param tskModel TskModel guia para crear los demas tskModel
     */
    protected TrapezoidalFuzzyFitnessFunction(double[][] inputData, double[] outputData, TSKModel tskModel) {
        this.inputData=inputData;
        this.outputData=outputData;
        this.tskModel=tskModel;        
    }
  
    /**
     * Metodo que crea el TSKModel Trapezoidal a partir del cromosoma, para asi poder evaluar las salidas
     * @param chromosome Cromosoma del que se desea obtener el tskModel
     * @param tskModel TSKModel guia
     * @throws GAException Devuelve una excepcion en caso que se produzca algun error
     * @return Devuelve el tskModel gaussiano que se corresponde con el cromosoma
     */
    protected  TSKModel createTSKFromChromosome(Chromosome chromosome, TSKModel tskModel) throws GAException {
        try {
            return CreateTSKFromChromosome.createTrapezoidalTSKFromChromosome (chromosome, tskModel);
        } catch (Exception e) {
            throw new GAException(className+".createTSKFromChromosome: No se ha podido crear el model TSK desde un cromosoma."+e.getMessage());
        }
    }
}

