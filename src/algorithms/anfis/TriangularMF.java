package algorithms.anfis;

/**
 * Clase que implementa la membership function Triangular
 * @author David Gil Galvan
 */
public class TriangularMF extends ParametricMF {
    protected double a;
    protected double b;
    protected double c;
    
    /**
     * 
     * @param a 
     * @param b 
     * @param c 
     */
    public TriangularMF(double a, double b, double c) {
        this.a=a;
        this.b=b;
        this.c=c;
    }    
    // Lo que he modificado en tskModel es q si den=0 devuelva 0
    /**
     * 
     * @param x 
     * @return 
     */
    public double evaluate(double x) {
        if (((b-a)==0)&&((c-b)==0)) {
            if (x==a)return 1;
            else return 0;
        }
         if (((b-a)==0)&&((c-b)>0)) return (Math.max(0,(c-x)/(c-b)));  
         if (((b-a)>0)&&((c-b)==0)) return (Math.max(0,(x-a)/(b-a)));         
                 
         return Math.max(0.0, Math.min( (x-a)/(b-a), (c-x)/(c-b) ));
        
    }
}
