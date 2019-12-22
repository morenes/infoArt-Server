package models.tskModel;

import models.ConsequentType;
import models.FiringType;
import models.MembershipFunctionType;


/**
 * Clase que representa a un TSKModel de tipo Gaussian, representando los conjuntos
 * difusos con dos puntos {a,c}
 * @author David Gil Galvan
 */
public class GaussianTSKModel extends TSKModel{
    
    private static final String MODEL_HEAD_G = "TSK_Gaussian_";
    
    /**
     * Constructor normal de un modelo TSK con conjuntos difusos gaussianos
     * @param numVar Numero de variables de entrada
     * @param consequentType Tipo de consecuente
     * @param firingType Tipo de inferencia
     * @param param_ap parametros a de las funciones de pertenencia gaussianas
     * @param param_cp parametros c de las funciones de pertenencia gaussianas
     * @param param_pp parametros de los consecuentes
     */
    public GaussianTSKModel (
            String modelIdentifier,
            int numVar,
            ConsequentType consequentType,
            FiringType firingType,
            double[][] param_ap,
            double[][] param_cp,
            double[][] param_pp) {
        
        MODEL_HEAD = MODEL_HEAD_G;
        this.modelIdentifier = modelIdentifier;
        this.attsToInputs=new int[numVar];
        for (int i=0;i<numVar;i++) {
            this.attsToInputs[i]=i;        
        }        
        this.attsToOutput=attsToInputs.length;
        this.fsetShape = MembershipFunctionType.GAUSSIAN;
        this.consequentType = consequentType;
        this.firingType = firingType;
        this.param_a = param_ap;
        this.param_c = param_cp;
        this.param_p = param_pp;
        // Numero de elementos por MF {a,c}
        numElementsForMF=2;    
    }

    /* constructor suponiendo que las reglas llevan alg�n operador difuso*/
    public GaussianTSKModel (
                String modelIdentifier,
                int numVar,
                ConsequentType consequentType,
                FiringType firingType,
                double[][] param_a,
                double[][] param_c,
                double[][] param_p,
                String[][] comp) {
        
        MODEL_HEAD = MODEL_HEAD_G;        
        this.modelIdentifier = modelIdentifier;
        this.attsToInputs=new int[numVar];
        for (int i=0;i<numVar;i++) {
            this.attsToInputs[i]=i;        
        }        
        this.attsToOutput=attsToInputs.length;
        this.fsetShape = MembershipFunctionType.GAUSSIAN;
        this.consequentType = consequentType;
        this.firingType = firingType;
        this.param_a = param_a;        
        this.param_c = param_c;
        this.param_p = param_p; 
        this.complements=comp;
        // Numero de elementos por MF {a,c}
        numElementsForMF=2;
    
    }
    
}
