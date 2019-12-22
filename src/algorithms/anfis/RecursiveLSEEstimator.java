package algorithms.anfis;

/**
 *
 * @author  Mercedes Valdes Vela
 */
//Implements the LSE Global method in an adaptive manner (example by example)
public class RecursiveLSEEstimator extends LeastSquareEstimator {
    static private final double __BIG_NUMBER = 1e6;
    private double P[][];
    private double theta[], aP[], Pa[];
    private double lambda; //forgetting factor. If 1, then all the data has the same importance.
    
    /**
     * 
     * @param numVar 
     * @param lambda_0 
     */
    public  RecursiveLSEEstimator(int numVar, double lambda_0) {
	lambda=lambda_0;
	P= new double [numVar][numVar];
	theta= new double [numVar];
	Pa= new double [numVar];
	aP= new double [numVar];
	reset();
    }
 
    public void reset() {
      int i,j;
      for (i=0; i<P.length; i++){
	theta[i]=0;
	for (j=0;j<P[i].length;j++)
	  if (i==j) 
	    P[i][j]=__BIG_NUMBER;
	  else P[i][j]=0;	       
      }
    }
		    
    /**
     * 
     * @return 
     */
    public double[] estimated () {
	return (theta);
    }
    
    /**
     * 
     * @param a 
     * @param y 
     */
    public void addPattern (double a[], double y) {
	
	int i,j;
	int rows=P.length,cols=P.length;
	double    aPa=0,at=0;
	for (i=0; i<rows; i++) {
	  Pa[i]=0;
	  aP[i]=0;
	}
	rows=cols=P.length;
	for (i=0; i<rows; i++) {
	    for (j=0; j<cols; j++) {
		Pa[i]+=P[i][j]*a[j];
		aP[j]+=P[i][j]*a[i];
	    }
	    aPa+=a[i]*Pa[i];
	}
	
	for (i=0; i<rows; i++)
	    for (j=0; j<cols; j++)
		P[i][j] = (P[i][j] - Pa[i]*aP[j]/(lambda+aPa))/lambda;
	
	for (i=0; i<a.length; i++)
	    at+=a[i]*theta[i];
	for (i=0; i<rows; i++) {
	  {
	    Pa[i]=0;
	    for (j=0; j<cols; j++)
		Pa[i]+=P[i][j]*a[j];
	  }
	    theta[i]+=(y-at)*Pa[i];
	}
    }
    
}
