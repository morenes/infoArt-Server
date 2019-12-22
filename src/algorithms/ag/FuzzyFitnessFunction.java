package algorithms.ag;
import org.jgap.impl.*;
import org.jgap.*;
import java.util.*;
import models.tskModel.*;
import utility.*;
import utility.Error;

/**
 * Clase abstracta que implementa la funcion de fitness para el GA difuso
 * @author David Gil Galvan
 */
public abstract class FuzzyFitnessFunction extends FitnessFunction{
    /**
     * Nombre de la clase
     */
    protected String className=this.getClass().getName();
    /**
     * Datos de entrada
     */
    protected double[][] inputData;
    /** 
     * Etiquetas de salida
     */
    protected double[] outputData;
    /**
     * Model TSK desde el que se generará el nuevo tsk para evluar al cromosoma
     */
    protected TSKModel tskModel;
            
    /**
     * Metodo que calcula la funcion de fitness que corresponde con el cromosoma
     * @param a_subject Cromosoma sobre el que se quiere calcular la funcion de fitnes
     * @return Devuelve el valor que proporciona la funcion de fitness sobre el cromosoma
     */
    protected double evaluate(Chromosome a_subject) {
        try {
            
            // Se crea el tsk correspondiente al cromosoma
            TSKModel newTSKModel=createTSKFromChromosome(a_subject,tskModel);
            
            // Se crea una instancia de error;
            Error error=new Error(outputData);
            // 
            double[] outputInfered=new double[outputData.length];
            
            // Se infieren las salidas con el modelo tsk creado
            for (int z=0;z<inputData.length;z++) {
                outputInfered[z]=newTSKModel.makeInference(inputData[z]);
            }                
            return error.J(outputInfered);                
        } catch (Exception e) {
            System.err.println(className+".evaluate: Se ha producido un error al ejecutar al evaluar la función fitness. ");
            e.printStackTrace();
            System.exit(0);
        }
        return -1;
    }
    
    /**
     * Crea un modelo TSK a partir de un cromosoma
     * @param chromosome Cromosoma del que se desea construir su TSKModel asociado
     * @param tskModel TSKModel guia
     * @throws GAException Devuelve una excepcion en caso que se produzca algun error
     * @return Devuelve el TSKModel asociado al cromosoma
     */
    protected abstract TSKModel createTSKFromChromosome(Chromosome chromosome, TSKModel tskModel) throws GAException;
}

