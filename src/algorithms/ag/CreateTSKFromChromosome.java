package algorithms.ag;

import org.jgap.impl.*;
import org.jgap.*;
import models.tskModel.*;
import models.ConsequentType;
import models.MembershipFunctionType;

/**
 * Clase que construye un TSKModel a partir de cromosomas o FCS
 * @author David Gil Galvan
 */
public class CreateTSKFromChromosome {
    /**
     * Nombre de la clase
     */
    protected static String className="CreateTSKFromChromosome";
    
    /**
     * Metodo que se encarga de crear un TSK Model Gaussiano a partir de un cromosoma     
     * @param chromosome Cromosoma a partir del cual se quiere construir el TSKModel
     * @param tskModel TSK que sirve de guia para generar el TSK Model nuevo
     * @return Devuelve el nuevo TSK Model Gaussiano que corresponde con el cromosoma
     * @throws Exception Se produce una excepcion en el caso que se produzca algun error en el proceso de transformacion
     */
    public static TSKModel createGaussianTSKFromChromosome(Chromosome chromosome, TSKModel tskModel) throws Exception {
        if(!tskModel.getFsetShape().equals(MembershipFunctionType.GAUSSIAN))
            throw new Exception (className+".createGaussianTSKFromChromosome: El modelo TSK es incorrecto. Se necesita un GaussianTSKModel");
        int numRules=tskModel.getNumRules();
        int numConsecuents=tskModel.getNumConsecuents();
        int numAttributes=tskModel.getNumAttributes();
        int numGenesForMF=tskModel.getNumElementsForMF();
        
        // Desviaciones
        double[][] param_a=new double[numRules][numAttributes];
        // Medias
        double[][] param_c=new double[numRules][numAttributes];
        // Consecuentes
        double[][] param_p=new double[numRules][numAttributes+1];
        
        Gene[] genes=(Gene[])chromosome.getGenes();
        for (int i=0;i<numRules;i++){
            for (int j=0;j<numAttributes;j++) {
                for (int k=0;k<numGenesForMF;k++) {
                    
                    if (k==0) { // Desviacion
                        param_a[i][j]=((DoubleGene)genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]).doubleValue();
                    }
                    if (k==1) { // Media
                        param_c[i][j]=((DoubleGene)genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]).doubleValue();
                    }
                }
            }
            // Si es un TSK de grado 0
            if(tskModel.getConsequentType().equals(ConsequentType.SINGLETON)){
                for (int j=0;j<numAttributes;j++) {
                    param_p[i][j]=0;
                }
                param_p[i][numAttributes]=((DoubleGene)genes[((numRules*(numGenesForMF*numAttributes))+(i*numConsecuents))]).doubleValue();
            }
            // Si es un TSK de primer grado
            if(tskModel.getConsequentType().equals(ConsequentType.LINEAR)){
                for (int j=0;j<numConsecuents;j++) {
                    param_p[i][j]=((DoubleGene)genes[((numRules*(numGenesForMF*numAttributes))+(i*numConsecuents)+j)]).doubleValue();
                }
            }
        }        
        // Devuelve el TSK Model Gaussiano 
        return new GaussianTSKModel(tskModel.getTotalModelIdentifier(),tskModel.getNumAttributes(), tskModel.getConsequentType(),tskModel.getFiringType(), param_a, param_c, param_p);
    }
    
    
    /**
     * Metodo que construye un TSKModel Triangular a partir de un cromosoma
     * @param chromosome Cromosoma a partir del cual se quiere construir el TSKModel triangular
     * @param tskModel TSKModel guia
     * @throws java.lang.Exception Devuelve una excepcion en caso que se produzca algun error
     * @return Devuelve el TSKModel Triangular
     */
    public static TSKModel createTriangularTSKFromChromosome(Chromosome chromosome, TSKModel tskModel) throws Exception{
        if(!tskModel.getFsetShape().equals(MembershipFunctionType.TRIANGULAR))
            throw new Exception (className+".createTriangularTSKFromChromosome: El modelo TSK es incorrecto. Se necesita un TriangularTSKModel");
        int numRules=tskModel.getNumRules();
        int numConsecuents=tskModel.getNumConsecuents();
        int numAttributes=tskModel.getNumAttributes();
        int numGenesForMF=tskModel.getNumElementsForMF();
        double[][] param_a=new double[numRules][numAttributes];
        double[][] param_b=new double[numRules][numAttributes];
        double[][] param_c=new double[numRules][numAttributes];
        double[][] param_p=new double[numRules][numAttributes+1];
        
        Gene[] genes=(Gene[])chromosome.getGenes();
        for (int i=0;i<numRules;i++){
            for (int j=0;j<numAttributes;j++) {
                for (int k=0;k<numGenesForMF;k++) {
                    
                    if (k==0) { // a
                        param_a[i][j]=((DoubleGene)genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]).doubleValue();
                    }
                    if (k==1) { // b
                        param_b[i][j]=((DoubleGene)genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]).doubleValue();
                    }
                    if (k==2) { // c
                        param_c[i][j]=((DoubleGene)genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]).doubleValue();
                    }
                }
            }
            if(tskModel.getConsequentType().equals(ConsequentType.SINGLETON)){
                for (int j=0;j<numAttributes;j++) {
                    param_p[i][j]=0;
                }
                param_p[i][numAttributes]=((DoubleGene)genes[((numRules*(numGenesForMF*numAttributes))+(i*numConsecuents))]).doubleValue();
            }
            if(tskModel.getConsequentType().equals(ConsequentType.LINEAR)){
                for (int j=0;j<numConsecuents;j++) {
                    param_p[i][j]=((DoubleGene)genes[((numRules*(numGenesForMF*numAttributes))+(i*numConsecuents)+j)]).doubleValue();
                }
            }
        }
        
        return new TriangularTSKModel(tskModel.getNumAttributes(), tskModel.getConsequentType(),tskModel.getFiringType(), param_a, param_b,param_c, param_p);
       
        
    }
    
    /**
     * Metodo que construye un TSKModel Trapezoidal a partir de un cromosoma
     * @param chromosome Cromosoma a partir del cual se quiere construir el TSKModel trapezoidal
     * @param tskModel TSKModel guia
     * @throws java.lang.Exception Devuelve una excepcion en caso que se produzca algun error
     * @return Devuelve el TSKModel Trapezoidal
     */
    public static TSKModel createTrapezoidalTSKFromChromosome(Chromosome chromosome, TSKModel tskModel) throws Exception{
        if(!tskModel.getFsetShape().equals(MembershipFunctionType.TRAPEZOIDAL))
            throw new Exception (className+".createTrapezoidalTSKFromChromosome: El modelo TSK es incorrecto. Se necesita un TrapezoidalTSKModel");
        int numRules=tskModel.getNumRules();
        int numConsecuents=tskModel.getNumConsecuents();
        int numAttributes=tskModel.getNumAttributes();
        int numGenesForMF=tskModel.getNumElementsForMF();
        double[][] param_a=new double[numRules][numAttributes];
        double[][] param_b=new double[numRules][numAttributes];
        double[][] param_c=new double[numRules][numAttributes];
        double[][] param_d=new double[numRules][numAttributes];
        double[][] param_p=new double[numRules][numAttributes+1];
        
        Gene[] genes=(Gene[])chromosome.getGenes();
        for (int i=0;i<numRules;i++){
            for (int j=0;j<numAttributes;j++) {
                for (int k=0;k<numGenesForMF;k++) {                                        
                    if (k==0) { //a
                        param_a[i][j]=((DoubleGene)genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]).doubleValue();
                    }
                    if (k==1) { //b
                        param_b[i][j]=((DoubleGene)genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]).doubleValue();
                    }
                    if (k==2) { //c
                        param_c[i][j]=((DoubleGene)genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]).doubleValue();
                    }
                    if (k==3) { //d
                        param_d[i][j]=((DoubleGene)genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]).doubleValue();
                    }
                }
            }
            if(tskModel.getConsequentType().equals(ConsequentType.SINGLETON)){
                for (int j=0;j<numAttributes;j++) {
                    param_p[i][j]=0;
                }
                param_p[i][numAttributes]=((DoubleGene)genes[((numRules*(numGenesForMF*numAttributes))+(i*numConsecuents))]).doubleValue();
            }
            if(tskModel.getConsequentType().equals(ConsequentType.LINEAR)){
                for (int j=0;j<numConsecuents;j++) {
                    param_p[i][j]=((DoubleGene)genes[((numRules*(numGenesForMF*numAttributes))+(i*numConsecuents)+j)]).doubleValue();
                }
            }
        }
        
        return new TrapezoidalTSKModel(tskModel.getNumAttributes(), tskModel.getConsequentType(),tskModel.getFiringType(), param_a, param_b,param_c, param_d,param_p);
        
    }                       
}
