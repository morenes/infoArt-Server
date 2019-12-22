package algorithms.ag;
import java.util.*;
import org.jgap.*;
import org.jgap.impl.*;

/**
 * Operador de Cruce Simple Arithmetic
 * @author David Gil Galvan
 */
public class SimpleArithmeticCrossoverOperator implements GeneticOperator{
    
    /**
     * Operacion de cruce Simple Arithmetic
     * @param a_candidateChromosomes Lista con dos cromosomas sobre los que se realiza
     * la operacion de cruce
     * @param a_population No se utiliza
     */
    public void operate(Population a_population, java.util.List a_candidateChromosomes) {
        
        Random generator=new Random();
        Vector a_candidateChromosomesAux=new Vector();
                        
        Chromosome chrom1 =(Chromosome) a_candidateChromosomes.get(0);
        Chromosome chrom2 =(Chromosome) a_candidateChromosomes.get(1);
                
        Chromosome firstMate = (Chromosome) chrom1.clone();
        Chromosome secondMate = (Chromosome) chrom2.clone();
                        
        Gene[] firstGenes =firstMate.getGenes();        
        Gene[] secondGenes = secondMate.getGenes();
        
        int locus=generator.nextInt(firstGenes.length);
        
        while ((locus<1)&&(locus>firstGenes.length-2))
            locus=generator.nextInt(firstGenes.length);
        
        // Swap the genes.
        // ---------------
        DoubleGene gene1;
        DoubleGene gene2;
        Double firstAllele;
        for (int j = locus; j < firstGenes.length; j++) {
            gene1 =(DoubleGene) firstGenes[j];
            gene2 =(DoubleGene) secondGenes[j];
            firstAllele =(Double) gene1.getAllele();
            gene1.setAllele(gene2.getAllele());
            gene2.setAllele(firstAllele);
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