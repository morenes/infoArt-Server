package algorithms.anfis;

/**
 *
 * @author Mercedes Valdes Vela
 */
public class MF {
    
    protected static String className="MF";
    
    /**
     * 
     * @param a 
     * @param c 
     * @param x 
     * @return 
     */
    static public final double gauss_MF(double a, double c, double x){
        try {
            
            if (a==0) {
                if (x==c) return 1;
                else return 0;
            }            
            double cal=(Math.exp(-Math.pow((x-c)/a,2)/2));     
            return cal;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    
    /**
     * 
     * @param a 
     * @param b 
     * @param c 
     * @param x 
     * @return 
     */
    static public final double bell_MF(double a, double b, double c, double x) {
        try {
            if (a==0) throw new AnfisException(className+".bell_MF: A tiene el valor 0");
            double tmp=(x-c)/a;
            if (tmp==0) return 1;
            else return (1/(1+Math.pow(Math.pow(tmp,2),b)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Metodo que calcula el grado de pertenencia de un dato en el fuzzy set
     * definido como 'los datos por encima de c'
     * @param a Parametro a de la campana de gauss
     * @param c Parametro c (centro) de la campana de Gauss
     * @param x Dato a calcular su funcion de pertenencia
     * @return
     */
    static public final double above_gauss_MF(double a, double c, double x){
        double mf = 0;
        if(x > c){
            mf = 1- gauss_MF(a, c, x);
        }

        return mf;
    }

    /**
     * Metodo que calcula el grado de pertenencia de un dato en el fuzzy set
     * definido como 'los datos por debajo de c'
     * @param a Parametro a de la campana de gauss
     * @param c Parametro c (centro) de la campana de Gauss
     * @param x Dato a calcular su funcion de pertenencia
     * @return
     */
    static public final double below_gauss_MF(double a, double c, double x){
        double mf = 0;
        if(x < c){
            mf = 1 - gauss_MF(a, c, x);
        }

        return mf;
    }
    
    /**
     * 
     * @param a 
     * @param c 
     * @param x 
     * @return 
     */
    static public final double dGauss_over_a(double a, double c, double x) {
        try {
            
            if (a==0) throw new AnfisException(className+".d_Gauss_over_a: A tiene el valor 0");
            double tmp=(x-c)/a;
            return Math.pow(tmp,2)/a;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * 
     * @param a 
     * @param c 
     * @param x 
     * @return 
     */
    static public final double dGauss_over_c(double a, double c, double x) {
        try {
            if (a==0) throw new AnfisException(className+".d_Gauss_over_c: A tiene el valor 0");
            double tmp=(x-c)/a;
            return tmp/a;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Metodo que calcula la derivada de la campana de gaus below con respecto
     * al parametro c.
     *
     * @param a parametro a de la campana original
     * @param c parametro c de la campana original
     * @param x
     * @return
     */
    static public final double dbelow_Gauss_over_c(double a, double c, double x) {
        if(x < c){
            return -dGauss_over_c(a, c, x);
        }
        else return 0;
    }

        /**
     * Metodo que calcula la derivada de la campana de gaus above con respecto
     * al parametro c.
     *
     * @param a parametro a de la campana original
     * @param c parametro c de la campana original
     * @param x
     * @return
     */
    static public final double dabove_Gauss_over_c(double a, double c, double x) {
        if(x > c){
            return -dGauss_over_c(a, c, x);
        }
        else return 0;
    }

        /**
     * Metodo que calcula la derivada de la campana de gaus below con respecto
     * al parametro c.
     *
     * @param a parametro a de la campana original
     * @param c parametro c de la campana original
     * @param x
     * @return
     */
    static public final double dbelow_Gauss_over_a(double a, double c, double x) {
        if(x < c){
            return -dGauss_over_a(a, c, x);
        }
        else return 0;
    }

        /**
     * Metodo que calcula la derivada de la campana de gaus above con respecto
     * al parametro c.
     *
     * @param a parametro a de la campana original
     * @param c parametro c de la campana original
     * @param x
     * @return
     */
    static public final double dabove_Gauss_over_a(double a, double c, double x) {
        if(x > c){
            return -dGauss_over_a(a, c, x);
        }
        else return 0;
    }
        
    /**
     * 
     * @param a 
     * @param b 
     * @param c 
     * @param x 
     * @return 
     */
    static public final double dBell_over_a(double a, double b, double c, double x) {
        try {
            if (a==0) throw new AnfisException(className+".d_Bell_over_a: A tiene el valor 0");
            double tmp=(x-c)/a, W;
            if (tmp==0) W=1;
            else W= 1/(1+1/Math.pow(Math.pow(tmp,2),b));
            return 2*b*W/a;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
        
    }
    
    /**
     * 
     * @param a 
     * @param b 
     * @param c 
     * @param x 
     * @return 
     */
    static public final double dBell_over_b(double a, double b, double c, double x) {
        try {
            if (a==0) throw new AnfisException(className+".d_Bell_over_b: A tiene el valor 0");
            double tmp=(x-c)/a, W;
            if (tmp==0) W=1;
            else W= 1/(1+1/Math.pow(Math.pow(tmp,2),b));
            if (x-c==0) return 0;
            else return (-W*Math.log(Math.pow(tmp,2)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
        
    }
        
    /**
     * 
     * @param a 
     * @param b 
     * @param c 
     * @param x 
     * @return 
     */
    static public final double dBell_over_c(double a, double b, double c, double x) {
        try {
            if (a==0) throw new AnfisException(className+".d_Bell_over_c: A tiene el valor 0");
            double tmp=(x-c)/a, W;
            if (tmp==0) W=1;
            else W= 1/(1+1/Math.pow(Math.pow(tmp,2),b));
            if (x-c==0) return 0;
            else return (2*b*W/(x-c));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
        
    }
    
    // Numero 0 es sustituido por 0.00000001 ya que si no se produce fallo en el tskModel
    /**
     * 
     * @param a 
     * @param b 
     * @param c 
     * @param x 
     * @return 
     */
    static public final double triangular_MF(double a, double b, double c, double x) {
        if (((b-a)==0)&&((c-b)==0)) return 1;
        if (((b-a)==0)&&((c-b)>0)) return (Math.max(0,(c-x)/(c-b)));
        if (((b-a)>0)&&((c-b)==0)) return (Math.max(0,(x-a)/(b-a)));
        
        return Math.max(0.0, Math.min( (x-a)/(b-a), (c-x)/(c-b) ));
        //return Math.max(0.0, Math.min( (x-a)/(b-a), (c-x)/(c-b) ));
    }
    
    // Numero 0 es sustituido por 0.00000001 ya que si no se produce fallo en el tskModel
    /**
     * 
     * @param a 
     * @param b 
     * @param c 
     * @param d 
     * @param x 
     * @return 
     */
    static public final double trapezoidal_MF(double a, double b, double c, double d, double x) {
        if (((d-c)==0)&&((b-a)==0)) return 1;
        if (((d-c)==0)&&((b-a)>0)) return Math.max(0, Math.min((x-a)/(b-a),1));
        if (((d-c)>0)&&((b-a)==0)) return Math.max(0, Math.min(1,(d-x)/(d-c)));
        return Math.max(0.0, Math.min((x-a)/(b-a),Math.min(1,(d-x)/(d-c))));
    }
    
}

