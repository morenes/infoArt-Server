package algorithms.anfis;

/**
 * Clase que implementa la membership function Gaussian
 * @author David Gil Galvan
 */
public class GaussianMF extends ParametricMF{
    
    protected double a;
    protected double c;
    
    /**
     * 
     * @param a 
     * @param c 
     */
    public GaussianMF(double a, double c) {
        this.a=a;
        this.c=c;
    }
    
    /**
     * 
     * @param x 
     * @return 
     */
    public double evaluate(double x) {        
        return (Math.exp(-Math.pow((x-c)/a,2)/2));        
    }
    
    /**
     * 
     * @param a 
     * @param c 
     * @param x 
     * @return 
     */
    public double dGauss_over_a(double a, double c, double x) {
        if (a==0) System.out.println("Error dGauss_over_Gauss (a=0)");//Excepcion
        double tmp=(x-c)/a;
        return Math.pow(tmp,2)/a;
    }
    
    /**
     * 
     * @param a 
     * @param c 
     * @param x 
     * @return 
     */
    public double dGauss_over_c(double a, double c, double x) {
        if (a==0) System.out.println("Error dGauss_over_Gauss (a=0)");//Excepcion
        double tmp=(x-c)/a;
        return tmp/a;
    }
}
