package utility;

import java.util.*;

/**
 * Clase Error que ayuda a calcular los errores cometidos por los algoritmos
 */
public class Error {
    private int n;    
    private double y_mean;
    private double sum_abs_y, std;
    /** 
     * Vector con las salidas originales
     */
    private double y[];
    
    /**
     * Constructor de la clase
     * @param y0 Salidas originales de los datos
     */
    public Error(double y0[]) {
        int i;
        
        y=y0;
        n=y.length;
        
        y_mean=0; sum_abs_y=0;
        for (i=0; i<n; i++) {
            y_mean+=y[i]; sum_abs_y+=Math.abs(y[i]);
        }
        y_mean/=n;
        
        std=0;
        for (i=0; i<n; i++) std+=Math.pow(y[i]-y_mean,2);
        std=Math.sqrt(std/n);
    }

    public double RMSE(double f[]) {
        int i;
        double sum_sqr_diff=0;

        for (i=0; i<n; i++) {
            sum_sqr_diff+=Math.pow(y[i]-f[i],2);
            //System.out.println(sum_sqr_diff + " y:"+y[i]+" f:"+f[i]);
        }

        double rmse = Math.sqrt(sum_sqr_diff/n);
//        System.out.println("RMSE: "+rmse);
        return rmse;
    }
    
    public double RMSE(double f[],boolean imprime) {
        int i;
        double sum_sqr_diff=0;
        
        for (i=0; i<n; i++) {
            sum_sqr_diff+=Math.pow(y[i]-f[i],2);
        }
        
        return (Math.sqrt(sum_sqr_diff/n));
    }

    public double percentageOfMatches(double f[]){
        int numErrors = 0;
        for (int i=0; i<n; i++) {
            int right = (int) Math.round(y[i]);
            int inferred = (int) Math.round(f[i]);

            if(right != inferred){
                numErrors++;
            }
        }
        return 100 -(((double)numErrors*100)/(double) n);

    }
    
    public double ErrorMedio(double f[]) {
        int i;
        double sum_sqr_diff=0;
        
        for (i=0; i<n; i++) {
            if (y[i]!=f[i])
                sum_sqr_diff++;
        }
        
        return (sum_sqr_diff/n);
    }

    public double ErrorMedioContinuo(double f[]) {
        int i;
        double sum_sqr_diff=0;
        double c1=-1.414072134;
        double c2=-0.707036067;
        double c3=0.0;
        double c4=0.707036067;
        double c5=1.414072134;
        for (i=0; i<n; i++) {
            double claseFinal=0;
            double dist=Double.MAX_VALUE;
                if (Math.abs(c1-f[i])<dist) {
                    dist=Math.abs(c1-f[i]);
                    claseFinal=c1;
                }
                if (Math.abs(c2-f[i])<dist) {
                    dist=Math.abs(c2-f[i]);
                    claseFinal=c2;
                }
                if (Math.abs(c3-f[i])<dist) {
                    dist=Math.abs(c3-f[i]);
                    claseFinal=c3;
                }
                if (Math.abs(c4-f[i])<dist) {
                    dist=Math.abs(c4-f[i]);
                    claseFinal=c4;
                }
                if (Math.abs(c5-f[i])<dist) {
                    dist=Math.abs(c5-f[i]);
                    claseFinal=c5;
                }   
            
            if (y[i]!=claseFinal)
                sum_sqr_diff++;
        }
        
        return (sum_sqr_diff/n);
    }
    
    public double J(double f[]) {
        int i;
        double sum_sqr_diff=0;
        
        for (i=0; i<n; i++) {
            sum_sqr_diff+=Math.pow(y[i]-f[i],2);
        }
        
        return (sum_sqr_diff/n);
    }
    
    public double APE(double f[]) {
        int i;
        double sum_abs_diff=0;
        for (i=0; i<n; i++) sum_abs_diff+=Math.abs(y[i]-f[i]);
        return (sum_abs_diff/sum_abs_y);
    }
    
    public double ARV(double f[]) {
        return (Math.pow(NDEI(f),2));
    }
    
    public double NDEI(double f[]) {
        return (RMSE(f)/std);
    }
}













