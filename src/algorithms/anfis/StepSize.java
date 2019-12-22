package algorithms.anfis;

public class StepSize {
  double E[];
  int m, n, act, total,
       last_increase, last_decrease;
  double p, q, kappa;
 

    /**
     * 
     * @param kappa0 
     * @param m0 
     * @param n0 
     * @param p0 
     * @param q0 
     */
public StepSize (double kappa0, int m0, int n0, double p0, double q0) {
  act=-1; total=-1; last_decrease=last_increase=0;
  m=m0; n=n0; p=p0; q=q0; kappa=kappa0;
  E= new double [Math.max(m,2*n)+2];
}

    /**
     * 
     * @param i 
     * @return 
     */
public double get_last_E (int i) {
  while (act-i<0) i-=E.length;
  return (E[(act-i)%E.length]);
}

    /**
     * 
     * @param e 
     */
public  void addError(double e) {
  act=(act+1)%E.length; E[act]=e;
  total++;
  if (need_increase()) kappa*=1+p;
  if (need_decrease()) kappa*=1-q;
}

    /**
     * 
     * @return 
     */
public double getStepSize() {
  return (kappa);
}

    /**
     * 
     * @return 
     */
private boolean need_increase() {
  int i;
  if (total-last_increase<m) return(false);
  for (i=0; i<m; i++)
    if (get_last_E(i)>=get_last_E(i+1)) return(false);

  last_increase=total;
  return(true);
}

    /**
     * 
     * @return 
     */
private boolean need_decrease() {
  int i;
  if (total-last_decrease<2*n) return(false);
  for (i=0; i<n; i++)
    if (get_last_E(2*i)>=get_last_E(2*i+1)
	|| get_last_E(2*i+1)<=get_last_E(2*i+2)) return(false);

  last_decrease=total;
  return(true);
}
}
