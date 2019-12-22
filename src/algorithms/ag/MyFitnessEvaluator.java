package algorithms.ag;
import org.jgap.*;

/**
 * A implementation of a fitness evaluator.
 * @author David Gil Galvan
 */
public class MyFitnessEvaluator implements FitnessEvaluator {


    /**
     * Compares the first given fitness value with the second and returns true
     * if the second one is greater than the first one. Otherwise returns false
     * @param a_fitness_value1 first fitness value
     * @param a_fitness_value2 second fitness value
     * @return true: second fitness value greater than first          
     */
    public boolean isFitter(double a_fitness_value1, double a_fitness_value2) {        
        return a_fitness_value1 < a_fitness_value2;
    }

}
