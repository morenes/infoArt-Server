package algorithms.ag;
import java.util.*;
import org.jgap.*;
import org.jgap.impl.*;

/**
 * Operador de cruce Whole Arithmetic
 * @author David Gil Galvan
 */
public class WholeArithmeticCrossoverOperator implements GeneticOperator {
    /**
     * Operacion de cruce Whole Arithmetic
     * @param a_candidateChromosomes Lista con dos cromosomas sobre los que se realiza
     * la operacion de cruce
     * @param a_population No se utiliza
     */
    public void operate(Population a_population, java.util.List a_candidateChromosomes) {
        
        // -----------------------------------------------------------
        int nc=a_candidateChromosomes.size();
        Random generator=new Random();
        Vector a_candidateChromosomesAux=new Vector();
                        
        Chromosome chrom1 =(Chromosome) a_candidateChromosomes.get(0);
        Chromosome chrom2 =(Chromosome) a_candidateChromosomes.get(1);
        
        Chromosome firstMate = (Chromosome) chrom1.clone();
        Chromosome secondMate = (Chromosome) chrom2.clone();
        Gene[] firstGenes = firstMate.getGenes();
        Gene[] secondGenes = secondMate.getGenes();
        double r=generator.nextDouble();
        
        // Swap the genes.
        // ---------------
        DoubleGene gene1;
        DoubleGene gene2;
        double firstAllele;
        double secondAllele;
        for (int j = 0; j < firstGenes.length; j++) {
            
            gene1 =(DoubleGene) firstGenes[j];
            
            gene2 =(DoubleGene) secondGenes[j];
            firstAllele = gene1.doubleValue();
            secondAllele=gene2.doubleValue();
            gene1.setAllele(new Double((r*firstAllele)+((1-r)*secondAllele)));
            gene2.setAllele(new Double((r*secondAllele)+((1-r)*firstAllele)));
        }
                        
        a_candidateChromosomesAux.add(firstMate);        
        a_candidateChromosomesAux.add(secondMate);
                                
        for (int j=0;j<a_candidateChromosomesAux.size();j++) {
            a_candidateChromosomes.remove(0);            
        }
        
        for (int j=0;j<a_candidateChromosomesAux.size();j++) {
            a_candidateChromosomes.add(((Chromosome)a_candidateChromosomesAux.get(j)).clone());
        }
        
    }
}

