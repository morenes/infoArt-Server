package models.tskModel;

import models.ConsequentType;
import models.FiringType;
import models.MembershipFunctionType;

/**
 * Clase que representa a un TSKModel de tipo Trapezoidal, representando los conjuntos
 * difusos con cuatro puntos {a,b,c,d}
 * @author David Gil Galvan
 */
public class TrapezoidalTSKModel extends TSKModel{
    
    private static final String MODEL_HEAD_Trp = "TSK_Trapezoidal_";
    
   /**
     * Constructor normal de un modelo TSK con conjuntos difusos trapezoidales
     * @param numVar Numero de variables de entrada
     * @param consequentType Tipo de consecuente
     * @param firingType Tipo de inferencia
     * @param param_a parametros a de las funciones de pertenencia trapezoidal
     * @param param_b parametros d de las funciones de pertenencia trapezoidal
     * @param param_c parametros c de las funciones de pertenencia trapezoidal
     * @param param_d parametros d de las funciones de pertenencia trapezoidal
     * @param param_p parametros de los consecuentes     
     */
    public TrapezoidalTSKModel(
            int numVar,
            ConsequentType consequentType,
            FiringType firingType,
            double[][] param_a,
            double[][] param_b,
            double[][] param_c,
            double[][] param_d,
            double[][] param_p) {

        this.attsToInputs=new int[numVar];
        for (int i=0;i<numVar;i++) {
            this.attsToInputs[i]=i;        
        }        
        this.attsToOutput=attsToInputs.length;
        this.fsetShape = MembershipFunctionType.TRAPEZOIDAL;
        this.consequentType = consequentType;
        this.firingType = firingType;
        this.param_a = param_a;
        this.param_b = param_b;
        this.param_c = param_c;
        this.param_d = param_d;
        this.param_p = param_p;  
        // Numero de elementos por MF {a,b,c,d}
        numElementsForMF=4;
    }    
    public TrapezoidalTSKModel(
            int numVar,
            ConsequentType consequentType,
            FiringType firingType,
            double[][] param_a,
            double[][] param_b,
            double[][] param_c,
            double[][] param_d,
            double[][] param_p, 
            String[][] comp) {
        
        this.attsToInputs=new int[numVar];
        for (int i=0;i<numVar;i++) {
            this.attsToInputs[i]=i;        
        }        
        this.attsToOutput=attsToInputs.length;
        this.fsetShape = MembershipFunctionType.TRAPEZOIDAL;
        this.consequentType = consequentType;
        this.firingType = firingType;
        this.param_a = param_a;
        this.param_b = param_b;
        this.param_c = param_c;
        this.param_d = param_d;
        this.param_p = param_p;  
        this.complements=comp;
        // Numero de elementos por MF {a,b,c,d}
        numElementsForMF=4;
    }    
    
}
