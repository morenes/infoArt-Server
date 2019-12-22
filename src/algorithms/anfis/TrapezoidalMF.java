package algorithms.anfis;

/**
 * Clase que implementa la membership function Trapezoidal
 * @author David Gil Galvan
 */
public class TrapezoidalMF extends ParametricMF{
    protected double a;
    protected double b;
    protected double c;
    protected double d;
    
    /**
     * 
     * @param a 
     * @param b 
     * @param c 
     * @param d 
     */
    public TrapezoidalMF(double a, double b, double c, double d) {
        this.a=a;
        this.b=b;
        this.c=c;
        this.d=d;
    }    
    // Lo que he modificado en tskModel es q si den=0 devuelva 0
    /**
     * 
     * @param x 
     * @return 
     */
    public double evaluate(double x) {
        if (((d-c)==0)&&((b-a)==0)) {
            if (x==a)return 1;
            else return 0;
        }
        if (((d-c)==0)&&((b-a)>0)) return Math.max(0, Math.min((x-a)/(b-a),1));        
        if (((d-c)>0)&&((b-a)==0)) return Math.max(0, Math.min(1,(d-x)/(d-c)));
        return Math.max(0.0, Math.min((x-a)/(b-a),Math.min(1,(d-x)/(d-c))));        
    }    
}
