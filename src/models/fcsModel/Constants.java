package models.fcsModel;

/**
 * Constantes del FCS Model
 * @author David Gil Galvan
 */
public class Constants {
    
    public static enum DistanceType {
        /**
         * Distancia Euclidea
         */
        euclidean,
        /**
         * Distancia Diagonal
         */
        diagonal,
        /**
         * Distancia Mahalanobis
         */
        mahalanobis,
        /**
         * Distancia Gustafson-Kessel
         */
        gustafson_kessel
    };
   /** Metodo A para contruir el TSKModel Triagular */
    public static byte methodA=0;
    /** Metodo B para contruir el TSKModel Triagular */
    public static byte methodB=1;
}
