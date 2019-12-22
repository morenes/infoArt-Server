package algorithms.ag;

import java.util.*;
import org.jgap.*;

/**
 * Implementacion del NaturalSelector que modela una ruleta 
 * @author David Gil Galvan
 */
public class InverseWheelRoulettedSelector implements INaturalSelector {

    /**
     * No se utiliza
     */
    public synchronized void empty() {
    }
    
    /**
     * Siempre devuelve true (No se utiliza)
     * @return Devuelve siempre true
     */
    public boolean returnsUniqueChromosomes() {
        return true;
    }
    
    /**
     * Metodo que selecciona nc crosomomas para eliminar de la poblacion actual segun la probabilidad del metodo de la ruleta
     * @param a_howManyToSelect nc cromosomas a seleccionar
     * @param a_from_pop Poblacion desde la que se escogeran los nc cromosomas
     * @param a_to_pop Nueva poblacion donde se insertan los nc cromosomas seleccionados
     */ 
    public synchronized void select(int a_howManyToSelect, Population a_from_pop, Population a_to_pop) {
        
        double[] cumVector = new double[a_from_pop.size()];
        double sumFitness = 0;                        
        
        for(int i=0;i<a_from_pop.size();i++){
            sumFitness = sumFitness + Math.pow((a_from_pop.getChromosome(i).getFitnessValue()),2);            
        }
                                        
        for(int i=0;i<a_from_pop.size();i++){
            cumVector[i] = Math.pow((a_from_pop.getChromosome(i).getFitnessValue()),2)/sumFitness;            
        }
        
        for(int i=0;i<a_howManyToSelect;i++){
            int firstIndex=calculate(cumVector);
            a_to_pop.addChromosome((Chromosome)a_from_pop.getChromosome(firstIndex).clone());
        }                        
    }
        
    /**
     * This method return the index value of the selected element.  
     * @param pRates represents a vector with probabilities for selection.
     * @return the integer value that represents the index of the selected element.
     */     
    protected int calculate(double[] pRates) {
        int vIndex = 0;
        Random generator=new Random();
        double vGenerated = generator.nextDouble();
        double vSum = pRates[0];
        while (vGenerated > vSum && vIndex < pRates.length - 1) {
            vSum += pRates[++vIndex];
        }       
        return vIndex;
    }

}