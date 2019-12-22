package algorithms.FACL;

import java.util.Hashtable;
/**
 * Clase que representan el conjunto de acciones que se pueden aplicar en el algoritmo por refuerzo 
 * @author David Gil Galvan
 */
public class Actions {
    
    /**
     * Numero total de acciones
     */
    protected int numActions=10;
    /**
     * Probabilidad de utilizar una determinada accion
     */
    protected double[] w=new double[numActions];
    /**
     * Refuerzos primarios para cada accion
     */
    protected double[] r=new double[numActions];
    
    /**
     * Numero de veces que se ha elegido una determinada accion
     */
    protected int[] countActions=new int[numActions];
    /**
     * Elegibilidad de cada accion
     */
    protected double[] elegibilidad=new double[numActions];
    
    /**
     * Constructor
     */
    public Actions() {
        
        w[0]=1/(double)numActions;
        w[1]=1/(double)numActions;
        w[2]=1/(double)numActions;
        w[3]=1/(double)numActions;
        w[4]=1/(double)numActions;
        w[5]=1/(double)numActions;
        w[6]=1/(double)numActions;
        w[7]=1/(double)numActions;
        w[8]=1/(double)numActions;
        w[9]=1/(double)numActions;
        
        elegibilidad[0]=r[0]=+0.13;
        elegibilidad[1]=r[1]=+0.2;
        elegibilidad[2]=r[2]=+0.1;
        elegibilidad[3]=r[3]=-0.1;
        elegibilidad[4]=r[4]=-0.2;
        elegibilidad[5]=r[5]=-0.3;
        elegibilidad[6]=r[6]=+0.25;
        elegibilidad[7]=r[7]=+0.01;
        elegibilidad[8]=r[8]=+0.05;
        elegibilidad[9]=r[9]=+0.41;
               
    }        
    
    /**
     * Devuelve el vector de probabilidades de utilizar las acciones
     * @return Vector de probabilidad de utilizar las acciones
     */
    public double[] getVectorW() {                
        return w;
    }
    /**
     * Establece el nuevo vector de probabilidad de las acciones
     * @w Nuevo vector de probabilidad de las acciones
     */
    public void setVectorW(double[] w) {
        this.w=w;
    }
    
    /**
     * Devuelve el vector con los refuerzos primarios de las acciones
     * @return Devuelve el vector con los refuerzos primarios de las acciones
     */     
    public double[] getPrimaryReinforcements() {        
        return r;
    }
    
    /**
     * Devuelve el refuerzo primario de una determinada accion
     * @param action Accion de la que se quiere obtener el refuerzo primario
     * @return Devuelve el refuerzo primario de la accion se�alada
     */
    public double getPrimaryReinforcement(int action) {        
        return r[action];
    }
    
    /**
     * Se incrementa el numero de veces que una determinada accion ha sido
     * seleccionada
     * @param action Accion que ha sido seleccionada
     */
    public void choosedAction(int action) {
        countActions[action]++;
    }
    
    /**
     * Devuelve el vector que indica el numero de veces que una accion ha sido
     * seleccionada
     * @return Devuelve el vector que indica el numero de veces que una accion
     * ha sido seleccionada
     */
    public int[] getCountActions() {
        return countActions;
    }
    
    /**
     * Devuelve el vector de elegibilidad
     * @return Devuelve el vector de elegilidad
     */
    public double[] getElegibilidad() {
        return elegibilidad;
    }
    
    /**
     * Establece el nuevo vector de elegibilidad
     * @param elegibilidad Vector de elegibilidad
     */
    public void setElegibilidad(double[] elegibilidad) {
        this.elegibilidad=elegibilidad;
    }
    
    /**
     * Numero total de acciones
     * @return Numero total de acciones
     */
    public int getNumActions() {
        return numActions;
    }
}
