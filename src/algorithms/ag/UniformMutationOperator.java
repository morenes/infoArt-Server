package algorithms.ag;

import java.util.*;
import org.jgap.*;

/**
 * Operador de mutacion Uniforme
 */
public class UniformMutationOperator implements GeneticOperator{
    
    /** Vector con los limites inferiores para los genes de los cromosomas */
    protected double[] vmin;
    /** Vector con los limites superiores para los genes de los cromosomas */
    protected double[] vmax;
    
    /**
     * Constructor
     * @param vmin Vector con los limites inferiores de los genes de los cromosomas
     * @param vmax Vector con los limites superiores de los genes de los cromosomas
     */    
    public UniformMutationOperator(double[] vmin, double[] vmax) {
        this.vmin=vmin;
        this.vmax=vmax;
    }
    /**
     * Operacion de cruce Multiple Uniform
     * @param a_population No se utiliza
     * @param a_candidateChromosomes Lista con un único cromosoma para mutarlo     
     */
    public void operate(final Population a_population,
            final List a_candidateChromosomes) {
        boolean mutate = false;
        Random generator=new Random();
        Vector a_candidateChromosomesAux=new Vector();
        
        Chromosome chromosome=(Chromosome)a_candidateChromosomes.get(0);
        Chromosome copyOfChromosome = null;
        // ...take a copy of it...
        // -----------------------
        copyOfChromosome = (Chromosome)chromosome.clone();
        // ...add it to the candidate pool...
        // ----------------------------------
        a_candidateChromosomesAux.add(copyOfChromosome);
        
        // ...then mutate all its genes...
        // -------------------------------
        Gene[] genes = copyOfChromosome.getGenes();
        
        // Process all atomic elements in the gene. For a StringGene this
        // would be the length of the string, for an IntegerGene, it is
        // always one element.
        // --------------------------------------------------------------
        mutateGene(genes, generator);
        
        
        for (int j=0;j<a_candidateChromosomesAux.size();j++) {
            a_candidateChromosomes.remove(0);            
        }
        
        for (int j=0;j<a_candidateChromosomesAux.size();j++) {
            a_candidateChromosomes.add(((Chromosome)a_candidateChromosomesAux.get(j)).clone());
        }        
    }    
    
    /**
     * Helper: mutate all atomic elements of a gene
     * @param genes Vector con los genes que se quieren mutar
     * @param a_generator the generator delivering amount of mutation
     */
    protected void mutateGene(Gene[] genes, Random a_generator) {
        int k=a_generator.nextInt(genes.length);
        double valor=vmin[k]+((vmax[k]-vmin[k])*a_generator.nextDouble());
        genes[k].setAllele(new Double(valor));
    }
}
