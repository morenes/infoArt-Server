package algorithms.FACL;

/**
 * Clase que representa el estado actual en que se encuentra el algoritmo FACL.
 * @author David Gil Galvan
 */
public class State {
    
    /** El estado est representado por un array. En nuestro caso este array solo tiene
     * dos valores, el primero sera la media y el segundo la desviaci�n
     */
    protected double[] state;
    /**
     * Constructor de la clase
     * @param state Array que representar� al estado
     */
    public State(double[] state) {
        this.state=state;
    }
    /**
     * Devuelve el estado correspondiente
     * @return Devuelve el estado
     */
    public double[] getState() {
        return state;
    }
    
    /**
     * Establece el nuevo estado
     * @param state Array con el nuevo estado
     */
    public void setState() {
        this.state=state;
    }
}
