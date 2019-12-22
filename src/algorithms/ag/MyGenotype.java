package algorithms.ag;

import java.util.*;
import org.jgap.event.*;
import org.jgap.*;
import org.jgap.impl.*;

/**
 * MyGenotype
 * @author David Gil Galvan
 */
public class MyGenotype extends Genotype{

    /**
     * Numero de cromosomas a ser sustituidos en las generaciones sucesivas
     */
    protected int nc;
    /**
     * Probabilidad para que se realice una operacion de cruce
     */
    protected double probCrossover;    

    /**
     * Constructs a new Genotype instance with the given array of
     * Chromosomes and the given active Configuration instance. Note
     * that the Configuration object must be in a valid state
     * when this method is invoked, or a InvalidconfigurationException
     * will be thrown.
     * @param nc Numero de cromosomas sobre los que se aplicaran las operaciones de cruce y mutacion
     * @param probCrossover Probabilidad de que se produzca seleccionados dos cromosomas una operación de cruce, 
     * en caso contrario se realiza una operacion de mutacion
     * @param a_activeConfiguration the current active Configuration object
     * @param a_population The Chromosome population to be managed by this
     * Genotype instance
     * @throws InvalidConfigurationException Excepcion en caso que se produzca cualquier error
     */
    public MyGenotype(Configuration a_activeConfiguration, Population a_population, int nc, double probCrossover)  throws InvalidConfigurationException {
        
        super(a_activeConfiguration, a_population);
        if (nc+1>a_population.size()) throw new InvalidConfigurationException("NC incorrecto");
        this.nc=nc;
        this.probCrossover=probCrossover;
        
    }    
    
    /**
     * Evolves the population of Chromosomes within this Genotype. This will
     * execute all of the genetic operators added to the present active
     * configuration and then invoke the natural selector to choose which
     * chromosomes will be included in the next generation population. Note
     * that the population size not always remains constant (dependent on the
     * NaturalSelectors used!).     
     */
    public synchronized void evolve() {
        
        verifyConfigurationAvailable();
        
        // Adjust population size to configured size (if wanted).
        // ------------------------------------------------------
        
        // Apply certain NaturalSelectors before GeneticOperators will be applied.
        // -----------------------------------------------------------------------
        
        // Execute all of the Genetic Operators.
        // -------------------------------------
        Configuration conf=getConfiguration();
        Vector opCrossOver=new Vector();
        Vector opMutation=new Vector();
        List listGeneticOperator=conf.getGeneticOperators();
        Iterator it=listGeneticOperator.iterator();
        while (it.hasNext()) {
            GeneticOperator go=(GeneticOperator)it.next();
            if ((go instanceof SimpleArithmeticCrossoverOperator) ||
                    (go instanceof WholeArithmeticCrossoverOperator) ||
                    (go instanceof HeuristicCrossoverOperator))
                opCrossOver.add(go);
            if ((go instanceof  UniformMutationOperator)||
                    (go instanceof MultipleUniformMutationOperator))
                opMutation.add(go);
        }
                
        int indice=0;
        
        Population pop=applyNaturalSelectors(nc,new WheelRoulettedSelector());
        
        Chromosome[] populationNew=new Chromosome[m_population.size()];
        
        //utilityAuxiliar.verCromosoma(this.getFittestChromosome());
        
        while (indice<nc) {
            
            Random generator=new Random();
            Vector candidatesChromosomes=new Vector();
            
            // Crossover
            
            double d=generator.nextDouble();
            
            if (d<probCrossover) {
                
                int index1=generator.nextInt(nc);
                int index2=generator.nextInt(nc);                                
                
                candidatesChromosomes.add(pop.getChromosome(index1));
                candidatesChromosomes.add(pop.getChromosome(index2));
                
                int selOperatorCrossover=generator.nextInt(opCrossOver.size());
                
                GeneticOperator go=(GeneticOperator)opCrossOver.get(selOperatorCrossover);
                
                applyGeneticOperator(go,pop,candidatesChromosomes);
                
                populationNew[indice]=(Chromosome)candidatesChromosomes.get(0);
                indice++;
                
                if (indice<nc) {
                    populationNew[indice]=((Chromosome)candidatesChromosomes.get(1));
                    indice++;
                }
                
            } else { // Mutation
                int index1=generator.nextInt(nc);
                
                candidatesChromosomes.add(m_population.getChromosome(index1));
                int selOperatorMutation=generator.nextInt(opMutation.size());
                GeneticOperator go=(GeneticOperator)opMutation.get(selOperatorMutation);
                go.operate(m_population,candidatesChromosomes);
                populationNew[indice]=((Chromosome)candidatesChromosomes.get(0));
                indice++;
            }
        }
        
        Population popEli=applyNaturalSelectors(nc,new InverseWheelRoulettedSelector());
        
        int j=0;
        while ((j<popEli.size())&&(indice<m_population.size()-1)) {
            int i=0;
            
            Gene[] genes1=popEli.getChromosome(j).getGenes();
            boolean salida=false;
            
            while ((i<m_population.size())&&(!salida)) {
                
                Gene[] genes2=m_population.getChromosome(i).getGenes();
                
                int k=0;
                boolean sal=false;
                while ((k<genes1.length)&&(!sal)) {
                    
                    double a1=((DoubleGene)genes1[k]).doubleValue();
                    double a2=((DoubleGene)genes2[k]).doubleValue();
                    if (a1==a2) k++;
                    else sal=true;
                }
                if (sal==true)i++;
                else salida=true;
            }
            
            if (salida==true) {
                
                populationNew[indice]=(Chromosome)m_population.getChromosome(i);
                indice++;
            }
            j++;
        }
        
        if (getConfiguration().isPreserveFittestIndividual()) {
            // Determine the fittest chromosome in the population.
            // ---------------------------------------------------
            Chromosome fittest = null;
            fittest = getPopulation().determineFittestChromosome();
            populationNew[indice]=fittest;
            indice++;
        }
        Random generator=new Random();
        while (indice<(m_population.size())){            
            int next=generator.nextInt(m_population.size());
            populationNew[indice]=m_population.getChromosome(next);
            indice++;
            
        }
                
        // Increase number of generation.
        // ------------------------------
        m_activeConfiguration.incrementGenerationNr();
        // Fire an event to indicate we've performed an evolution.
        // -------------------------------------------------------
        m_activeConfiguration.getEventManager().fireGeneticEvent(
                new GeneticEvent(GeneticEvent.GENOTYPE_EVOLVED_EVENT, this));
        
        setPopulation(new Population(populationNew));
        
    }    
    
    /**
     * Evolves this Genotype the specified number of times. This is
     * equivalent to invoking the standard evolve() method the given number
     * of times in a row.
     *
     * @param a_numberOfEvolutions the number of times to evolve this Genotype
     * before returning     
     */
    public void evolve(int a_numberOfEvolutions) {
        for (int i = 0; i < a_numberOfEvolutions; i++) {
            evolve();
        }
    }
    
    /**
     * Return a string representation of this Genotype instance,
     * useful for display purposes.
     *
     * @return string representation of this Genotype instance     
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < getPopulation().size(); i++) {
            buffer.append(getPopulation().getChromosome(i).toString());
            buffer.append(" [");
            buffer.append(getPopulation().getChromosome(i).getFitnessValue());
            buffer.append(']');
            buffer.append('\n');
        }
        return buffer.toString();
    }
    
    /**
     * Applies all NaturalSelectors registered with the Configuration
     * @param num NC
     * @param selector Operador Selector
     * @return Devuelve la poblacion de tamaño NC 
     */
    protected Population applyNaturalSelectors(int num, INaturalSelector selector) {
        // Process all natural selectors applicable before executing the
        // genetic operators (reproduction, crossing over, mutation...).
        // -------------------------------------------------------------
        
        Population m_new_population;
        m_new_population = new Population(num);
        selector.select(num,getPopulation(), m_new_population);
        selector.empty();
        return m_new_population;
    }
    
    /**
     * Overwritable method that calls a GeneticOperator to operate on a given
     * population and asks him to store the result in the list of chromosomes.
     * Override this method if you want to ensure that a_chromosomes is not
     * part of a_population resp. if you want to use a different list.
     *
     * @param a_operator the GeneticOperator to call
     * @param a_population the Population to use
     * @param a_chromosomes the List of Chromosome objects to return
     */
    protected void applyGeneticOperator(GeneticOperator a_operator,
            Population a_population,
            List a_chromosomes) {
        a_operator.operate(a_population, a_chromosomes);
    }
    
    /**
     * Verifies that a Configuration object has been properly set on this
     * Genotype instance. If not, then an IllegalStateException is thrown.
     * In general, this method should be invoked by any operation on this
     * Genotype that makes use of the Configuration instance.
     */
    private void verifyConfigurationAvailable() {
        if (m_activeConfiguration == null) {
            throw new IllegalStateException(
                    "The active Configuration object must be set on this " +
                    "Genotype prior to invocation of other operations.");
        }
    }
}
