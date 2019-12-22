package algorithms.anfis;
/**
 * Clase que implementa la membership function Bell-Shaped
 * @author David Gil Galvan
 */
public class BellMF extends ParametricMF {    
    protected double a;
    protected double b;
    protected double c;
    
    /**
     * 
     * @param a 
     * @param b 
     * @param c 
     */
    public BellMF(double a, double b, double c) {
        this.a=a;
        this.b=b;
        this.c=c;
    }
    
    /**
     * 
     * @param x 
     * @return 
     */
    public double evaluate(double x) {
        double tmp=(x-c)/a;
        if (tmp==0) return 1;
        else return (1/(1+Math.pow(Math.pow(tmp,2),b)));                
    }
    
    /**
     * 
     * @param x 
     * @return 
     */
    public double dBell_over_a(double x){
        
        if (a==0) System.out.println("Error dGauss_over_Gauss (a=0)");//Excepcion
        double tmp=(x-c)/a, W;
        if (tmp==0) W=1;
        else W= 1/(1+1/Math.pow(Math.pow(tmp,2),b));
        return 2*b*W/a;
        
    }
    
    /**
     * 
     * @param a 
     * @param b 
     * @param c 
     * @param x 
     * @return 
     */
    public double dBell_over_b(double a, double b, double c, double x){
        
        if (a==0) System.out.println("Error dGauss_over_Gauss (a=0)");//Excepcion
        double tmp=(x-c)/a, W;
        if (tmp==0) W=1;
        else W= 1/(1+1/Math.pow(Math.pow(tmp,2),b));
        if (x-c==0) return 0;
        else return (-W*Math.log(Math.pow(tmp,2)));
        
    }
    
    /**
     * 
     * @param a 
     * @param b 
     * @param c 
     * @param x 
     * @return 
     */
    public double dBell_over_c(double a, double b, double c, double x){
        
        if (a==0) System.out.println("Error dGauss_over_Gauss (a=0)");//Excepcion
        double tmp=(x-c)/a, W;
        if (tmp==0) W=1;
        else W= 1/(1+1/Math.pow(Math.pow(tmp,2),b));
        if (x-c==0) return 0;
        else return (2*b*W/(x-c));
    }
    
}
