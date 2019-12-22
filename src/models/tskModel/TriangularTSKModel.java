package models.tskModel;

import models.ConsequentType;
import models.FiringType;
import models.MembershipFunctionType;

/**
 * Clase que representa a un TSKModel de tipo Triangular, representando los conjuntos
 * difusos con tres puntos {a,b,c}
 * @author David Gil Galvan
 */
public class TriangularTSKModel extends TSKModel{
    
    private static final String MODEL_HEAD_Tr = "TSK_Triangular_";    
    
    /**
     * Constructor normal de un modelo TSK con conjuntos difusos triangulares
     * @param user Usuario que genera el modelo
     * @param key Clave del experimento que genero el modelo
     * @param numVar Numero de variables de entrada     
     * @param consequentType Tipo de consecuente
     * @param firingType Tipo de inferencia
     * @param param_a parametros a de las funciones de pertenencia triangular
     * @param param_b parametros b de las funciones de pertenencia triangular
     * @param param_c parametros c de las funciones de pertenencia triangular
     * @param param_p parametros de los consecuentes     
     */
    
        public TriangularTSKModel(
                int numVar,
                ConsequentType consequentType,
                FiringType firingType,
                double[][] param_a,
                double[][] param_b,
                double[][] param_c,
                double[][] param_p) {
        this.attsToInputs=new int[numVar];
        for (int i=0;i<numVar;i++) {
            this.attsToInputs[i]=i;        
        }        
        this.attsToOutput=attsToInputs.length;
        this.fsetShape = MembershipFunctionType.TRIANGULAR;
        this.consequentType = consequentType;
        this.firingType = firingType;
        this.param_a = param_a;
        this.param_b = param_b;
        this.param_c = param_c;
        this.param_p = param_p;  
        // Numero de elementos por MF {a,b,c}
        numElementsForMF=3;
    }    
    public TriangularTSKModel(
            int numVar,
            ConsequentType consequentType, 
            FiringType firingType,
            double[][] param_a,
            double[][] param_b,
            double[][] param_c,
            double[][] param_p,
            String[][] comp) {
        this.attsToInputs=new int[numVar];
        for (int i=0;i<numVar;i++) {
            this.attsToInputs[i]=i;        
        }        
        this.attsToOutput=attsToInputs.length;
        this.fsetShape = MembershipFunctionType.TRIANGULAR;
        this.consequentType = consequentType;
        this.firingType = firingType;
        this.param_a = param_a;
        this.param_b = param_b;
        this.param_c = param_c;
        this.param_p = param_p;  
        this.complements=comp;
        // Numero de elementos por MF {a,b,c}
        numElementsForMF=3;
    }    
}
