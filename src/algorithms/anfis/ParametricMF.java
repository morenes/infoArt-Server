package algorithms.anfis;

/**
 * 
 * @author David Gil Galvan
 */
public abstract class ParametricMF implements FuzzySet{
    /**
     * 
     * @param x 
     * @return 
     */
        public abstract double evaluate(double x);
        public double evaluateNeg(double x){return 1 - evaluate(x);}
        public double evaluateUniv(double x){return 1;}
}
