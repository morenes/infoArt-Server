package models.fcsModel;

/**
 * Interfaz FuzzyClusterRule
 * @author David Gil Galvan
 */
public interface FuzzyClusterRule {
    /**
     * Metodo que se encarga de inferir la salida a partir del FCS
     * @param example Ejemplo sobre el que se realizara la inferencia
     * @return Devuelve la inferencia realizada
     * @throws FCSException Excepcion en caso que se produzca cualquier error
     */
    public double makeInference(double[] example) throws FCSException;
}
