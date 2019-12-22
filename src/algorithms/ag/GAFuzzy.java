package algorithms.ag;

import org.jgap.impl.*;
import org.jgap.*;
import algorithms.ag.*;
import java.util.*;
import models.tskModel.*;
import utility.*;

/**
 * Algoritmo Genetico difuso
 * @author David Gil Galvan
 */
public abstract class GAFuzzy {
    /**
     * Nombre de la clase
     */
    protected String className="GAGFuzzy";
    /**
     * Tamaño de la poblacion
     */
    protected int L;  // Tamano de la poblacion
    /**
     * Numero de etapas
     */
    protected int T; // Numero de etapas
    /**
     * TSKModel inicial
     */
    protected TSKModel tskModel;    
    /**
     * Datos de entrenamiento de entrada
     */
    protected double[][] inputData;
    /** 
     * Datos de entrenamiento de salida
     */
    protected double[] outputData;
    /**
     * Alfa1 para las restricciones en los antecedentes
     */
    protected double alfa1;
    /**
     * Alfa2 para las restricciones en los consecuentes
     */
    protected double alfa2;
    /**
     * Limite superior sobre los genes de los cromosomas
     */
    protected double[] vmin;
    /**
     * Limite inferior sobre los genes de los cromosomas
     */
    protected double[] vmax;
    /**
     * Numero de cromosomas sobre los que se operaron
     */
    protected int nc;
    /**
     * Probabilidad de que se produzca cruce
     */
    protected double probCrossover;
       
    /**
     * Metodo que aplica el algoritmo genetico sobre el tsk dado
     * @param tskModelInitial TSK Inicial
     * @return Devuelve el tsk optimizado mediante el algoritmo genetico
     */
    public TSKModel optimize(TSKModel tskModelInitial) {
        try {            
            this.tskModel=tskModelInitial;
            checkTSKModel();
            
            // Start with a DefaultConfiguration, which comes setup with the
            // most common settings.
            // -------------------------------------------------------------
            Configuration conf = new DefaultConfiguration();
            conf.setPreservFittestIndividual(true);
            // Set the fitness function we want to use.            
            FuzzyFitnessFunction fuzzyFitnessFunction=createFuzzyFitnessFunction(inputData, outputData, tskModel);
            conf.setFitnessFunction(fuzzyFitnessFunction);
            // Establece como se evaluará la función de fitness
            conf.setFitnessEvaluator(new MyFitnessEvaluator());
            // Now we need to tell the Configuration object how we want our
            // Chromosomes to be setup. 
            // --------------------------------------------------------------
            Chromosome chromosome=createInitialChromosome(); // s01
            
            conf.setSampleChromosome(chromosome);
            // Se calculan las restricciones V del cromosoma
            calculateV(chromosome);
            conf.setPreservFittestIndividual(true);
            // Finally, we need to tell the Configuration object how many
            // Chromosomes we want in our population. 
            // ------------------------------------------------------------
            // Tamano de la poblacion
            conf.setPopulationSize(L);
            // Operadores
            conf.addGeneticOperator(new SimpleArithmeticCrossoverOperator());
            conf.addGeneticOperator(new WholeArithmeticCrossoverOperator());
            conf.addGeneticOperator(new HeuristicCrossoverOperator());
            conf.addGeneticOperator(new UniformMutationOperator(vmin, vmax));
            conf.addGeneticOperator(new MultipleUniformMutationOperator(vmin, vmax));
            // Se crea la poblacion inicial a partir del cromosoma
            Population pop=createPopulation(chromosome);
            // Se comprueban las restricciones sobre la poblacion
            checkConstraint(pop);
                        
            MyGenotype population = new MyGenotype(conf,pop, nc,probCrossover);
            // Para cada una de las generaciones
            for (int i=0;i<T;i++) {
                // Se evoluciona la poblacion
                population.evolve();         
                // Se comprueban las restricciones sobre la poblacion
                checkConstraint(population.getPopulation());                                                
            }            
            // Se devuelve el TSKModel correspondiente al mejor cromosoma
            return createTSKFromChromosome(population.getFittestChromosome(),tskModel);            
            
        } catch (Exception e)  {
            System.err.println(className+".optimize: Se ha producido un error al ejecutar el Algoritmo Genetico. ");
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }
    
    /**
     * Metodo que genera un TSK Model desde un cromosoma
     * 
     * @return Devuelve el TSK Model obtenido a partir del cromosoma
     * @param chromosome Cromosoma del que se quiere generar el TSK Model
     * @param tskModel TSK Model inicial que sirve de patron para generar el nuevo tsk a partir del cromosoma
     * @throws algorithms.ag.GAException Excepcion en caso que se produzca algun error
     */
    protected abstract TSKModel createTSKFromChromosome(Chromosome chromosome, TSKModel tskModel) throws GAException;
    
    /**
     * Metodo que comprueba las restricciones especiales y de particion sobre la poblacion      
     * @param population Poblacion sobre la que se comprobaran las restricciones
     * @throws algorithms.ag.GAException Excepcion en caso que se produzca algun error
     */
    protected void checkConstraint(Population population) throws GAException{
        for (int i=0;i<population.size();i++) {                        
            checkConstraintPartition(population.getChromosome(i));
            checkConstraintSearchSpace(population.getChromosome(i));
        }
    }
    
    /**
     * Metodo que crea la poblacion inicial del algoritmo genetico a partir de un cromosoma
     * @param chromosome Chromosoma inicial 
     * @return Devuelve la poblacion generada a partir del cromosoma
     */
    protected Population createPopulation(Chromosome chromosome) {
        Random random=new Random();
        Population population=new Population();
        population.addChromosome(chromosome);        
        for (int i=1;i<L;i++) {            
            Gene[] genes=new Gene[chromosome.size()];
            for(int j=0;j<genes.length;j++) {                
                double val=vmin[j]+((vmax[j]-vmin[j])*random.nextDouble());
                (genes[j]=new DoubleGene(-100,100)).setAllele(val);
            }
            Chromosome chromosomeNew=new Chromosome(genes);
            population.addChromosome(chromosomeNew);            
        }
        return population;
    }
    /**
     * Comprueba que el TSKModel es el apropiado
     * @throws GAException Devuelve una excepcion en caso que se produzca algun error
     */
    protected abstract void checkTSKModel() throws GAException;
    
    /**
     * Crea la funcion fitness apropiada, segun el tsk Model con el que se esta trabajando
     * @param inputData Matriz de datos de entrada
     * @param outputData Vector de datos de salida
     * @param tskModel tskModel que sirve de guia para crear el resto de TSKModel
     * @return Devuelve la Funcion de fitness asociada al model TSKModel
     */
    protected abstract FuzzyFitnessFunction createFuzzyFitnessFunction(double[][] inputData, double[] outputData, TSKModel tskModel);

    /**
     * Metodo que calcula las restricciones espaciales a partir del cromosoma inicial y en funcion del 
     * tskModel con el que se este trabajando
     * @param chromosome Cromosoma inicial
     */
    protected abstract void calculateV(Chromosome chromosome);
    
    /**
     * Metodo que comprueba que el cromosoma cumple las restricciones espaciales
     * @param chromosome Cromosoma sobre el que se quiere determinar si cumple las restricciones espaciales
     * @throws GAException Devuelve una excepción en caso que se produzca algun error
     **/
    protected abstract void checkConstraintSearchSpace(Chromosome chromosome) throws GAException;
    
    /**
     * Metodo que comprueba que el cromosoma cumple las restricciones de particion
     * @param chromosome Cromosoma sobre el que se quiere determinar si cumple las restricciones de particion
     * @throws GAException Devuelve una excepción en caso que se produzca algun error
     **/
    protected abstract void checkConstraintPartition(Chromosome chromosome) throws GAException;
    
    /**
     * Metodo que crea el cromosoma inicial a partir del tskModel
     * @return Devuelve el cromosoma que corresponde con el tskModel
     */
    protected abstract Chromosome createInitialChromosome();
        
}