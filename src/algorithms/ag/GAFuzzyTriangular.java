package algorithms.ag;

import org.jgap.impl.*;
import org.jgap.*;
import models.tskModel.*;

/**
 * Algoritmo Genetico con funcion de pertenencia Triangular
 * @author David Gil Galvan
 */
public class GAFuzzyTriangular extends GAFuzzy {
    
    /**
     * Nombre de la clase
     */
    protected String className=this.getClass().getName();
    
    /**
     *
     * Constructor del algoritmo genético gaussiano
     * @param L Tamano de la poblacion
     * @param T Numero de generaciones
     * @param alfa1 Umbral para las restricciones espaciales de los antecedes
     * @param alfa2 Umbral para las restricciones espaciales de los consecuentes
     * @param nc Numero de cromosomas a evolucionar en las sucesivas generaciones
     * @param probCrossover Probabilidad de cruzamiento
     * @param inputData Matriz de datos de entrada
     * @param outputData Vector de datos de salida
     */
    public GAFuzzyTriangular(int L, int T, double alfa1, double alfa2, int nc, double probCrossover, double[][] inputData, double[] outputData) {
        this.L=L;
        this.T=T;
        this.alfa1=alfa1;
        this.alfa2=alfa2;
        this.nc=nc;
        this.probCrossover=probCrossover;
        this.inputData=inputData;
        this.outputData=outputData;
    }
    
    /**
     * Comprueba que el TSKModel es del tipo Triangular
     * @throws GAException Devuelve una excepcion en caso que se produzca algun error
     */
    protected void checkTSKModel() throws GAException{
        if (!(tskModel instanceof TriangularTSKModel))
            throw new GAException(className+".checkTSKModel: El modelo TSK es incorrecto");
    }
    
    /**
     * Crea la funcion fitness segun el tsk Model triangular
     * @return Devuelve el TSKModel asociado al cromosoma
     * @param inputData Matriz de datos de entrada
     * @param outputData Vector de datos de salida
     * @param tskModel tskModel que sirve de guia para crear el resto de tskModel
     */
    protected FuzzyFitnessFunction createFuzzyFitnessFunction(double[][] inputData,double[] outputData,TSKModel tskModel) {
        return new TriangularFuzzyFitnessFunction(inputData, outputData, tskModel);
    }
    
    /**
     * Metodo que calcula las restricciones espaciales a partir del cromosoma inicial y en funcion del tskModel con el que se
     * este trabajando
     * @param chromosome Cromosoma inicial desde el que se determinan las restricciones espaciales
     */
    protected void calculateV(Chromosome chromosome) {
        // Calcular el valor de entrada minimo
        double valmin=Double.POSITIVE_INFINITY;
        double valmax=Double.NEGATIVE_INFINITY;
        int numRules=tskModel.getNumRules();
        int numConsecuents=tskModel.getNumConsecuents();
        int numAttributes=inputData[0].length;
        int numGenesForMF=tskModel.getNumElementsForMF();
        
        vmin=new double[numRules*((numGenesForMF*numAttributes)+(numConsecuents))];
        vmax=new double[numRules*((numGenesForMF*numAttributes)+(numConsecuents))];
        DoubleGene[] genes=(DoubleGene[])chromosome.getGenes();
        // Antecedentes
        for (int i=0;i<numRules;i++) {  // Para cada regla
            for (int j=0;j<numAttributes;j++) { // Para cada abc de la regla
                for (int k=0;k<numGenesForMF;k++) { // Para a o b o c
                    valmin=Double.POSITIVE_INFINITY;
                    valmax=Double.NEGATIVE_INFINITY;
                    for (int z=0;z<inputData.length;z++) {
                        if (inputData[z][j]<valmin) {
                            valmin=inputData[z][j];
                        }
                        if (inputData[z][j]>valmax) {
                            valmax=inputData[z][j];
                        }
                    }
                    vmin[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]=genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)].doubleValue()-(valmin*alfa1);
                    vmax[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]=genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)].doubleValue()+(valmax*alfa1);
                }
            }
        }
        
        valmin=Double.POSITIVE_INFINITY;
        valmax=Double.NEGATIVE_INFINITY;
        // Consecuentes
        
        for (int j=0;j<numConsecuents;j++) {
            for (int i=0;i<numRules;i++) { // Para cada regla
                if (genes[((numRules*(numGenesForMF*numAttributes))+(i*numConsecuents)+j)].doubleValue()<valmin)
                    valmin=genes[((numRules*(numGenesForMF*numAttributes))+(i*numConsecuents)+j)].doubleValue();
                if (genes[((numRules*(numGenesForMF*numAttributes))+(i*numConsecuents)+j)].doubleValue()>valmax)
                    valmax=genes[((numRules*(numGenesForMF*numAttributes))+(i*numConsecuents)+j)].doubleValue();
            }
            for (int k=0;k<numRules;k++) {
                vmin[((numRules*(numGenesForMF*numAttributes))+(k*numConsecuents)+j)]=genes[((numRules*(numGenesForMF*numAttributes))+(k*numConsecuents)+j)].doubleValue()-(valmin*alfa2);
                vmax[((numRules*(numGenesForMF*numAttributes))+(k*numConsecuents)+j)]=genes[((numRules*(numGenesForMF*numAttributes))+(k*numConsecuents)+j)].doubleValue()+(valmax*alfa2);
            }
        }
    }
    
    /**
     * Metodo que comprueba que el cromosoma cumple las restricciones espaciales
     * @param chromosome Cromosoma sobre el que se quiere determinar si cumple las restricciones espaciales
     * @throws GAException Devuelve una excepcion en caso que se produzca algun error
     **/
    protected void checkConstraintSearchSpace(Chromosome chromosome) throws GAException {
        try {
            
            Gene[] genes=chromosome.getGenes();
            for (int i=0;i<genes.length;i++) {
                genes[i].setAllele(new Double(Math.max(vmin[i],Math.min(((DoubleGene)genes[i]).doubleValue(),vmax[i]))));
            }
            chromosome.setGenes(genes);
        } catch (Exception e) {
            throw new GAException(className+".checkConstraintSearchSpace: Error al comprobar las restricciones en el espacio de busqueda");
        }
        
    }
    
    /**
     * Metodo que comprueba que el cromosoma cumple las restricciones de particion
     * @param chromosome Cromosoma sobre el que se quiere determinar si cumple las restricciones de particion
     * @throws GAException Devuelve una excepcion en caso que se produzca algun error
     **/
    protected void checkConstraintPartition(Chromosome chromosome) throws GAException{
        try {
            Gene[] genes=chromosome.getGenes();
            int numRules=tskModel.getNumRules();
            int numAttributes=tskModel.getNumAttributes();
            int numGenesForMF=tskModel.getNumElementsForMF();
            for (int i=0;i<numRules;i++) {  // Para cada regla
                for (int j=0;j<numAttributes;j++) {
                    
                    double a=((DoubleGene)genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+0)]).doubleValue();
                    double b=((DoubleGene)genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+1)]).doubleValue();
                    if (a>b) {
                        genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+0)].setAllele(new Double(b));
                        genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+1)].setAllele(new Double(a));
                    }
                    b=((DoubleGene)genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+1)]).doubleValue();
                    double c=((DoubleGene)genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+2)]).doubleValue();
                    if (b>c) {
                        genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+1)].setAllele(new Double(c));
                        genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+2)].setAllele(new Double(b));
                    }
                }
            }
            
            boolean noChanged=false;
            while (noChanged==true) {
                
                noChanged=false;
                int i=0;
                while ((i<numRules)&&(!noChanged)) {
                    //for (int i=0;i<numRules;i++) {  // Para cada regla
                    int j=0;
                    while ((j<numAttributes)&&(!noChanged)) {
                        
                        //for (int i=0;i<numRules;i++) {  // Para cada regla
                        //  for (int j=0;j<numAttributes;j++) { // Para cada abc de la regla
                        double a=((DoubleGene)genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+0)]).doubleValue();
                        double b=((DoubleGene)genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+1)]).doubleValue();
                        double c=((DoubleGene)genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+2)]).doubleValue();
                        int best=-1;
                        double diferencia=Double.POSITIVE_INFINITY;
                        double an;
                        double bn;
                        double cn;
                        
                        for (int k=0;k<numRules;k++) {  // Para cada regla
                            if (i!=k) {
                                
                                an=((DoubleGene)genes[((k*numAttributes*numGenesForMF)+(j*numGenesForMF)+0)]).doubleValue();
                                bn=((DoubleGene)genes[((k*numAttributes*numGenesForMF)+(j*numGenesForMF)+1)]).doubleValue();
                                cn=((DoubleGene)genes[((k*numAttributes*numGenesForMF)+(j*numGenesForMF)+2)]).doubleValue();
                                if ((an>a)&&((an-a)<diferencia)) {
                                    best=k;
                                    diferencia=an-a;
                                }
                            }
                        }
                        
                        if (best!=-1) {
                            an=((DoubleGene)genes[((best*numAttributes*numGenesForMF)+(j*numGenesForMF)+0)]).doubleValue();
                            if(an>c){
                                genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+2)].setAllele(new Double(an));
                                genes[((best*numAttributes*numGenesForMF)+(j*numGenesForMF)+0)].setAllele(new Double(c));
                                noChanged=true;
                            }
                        }
                        j++;
                    }
                    i++;
                    
                }
            }
            chromosome.setGenes(genes);
        } catch (Exception e) {
            throw new GAException(className+".checkConstraintPartition: Error al comprobar las restricciones en las particiones");
        }
    }
    
    /**
     * Metodo que crea el cromosoma inicial a partir del tskModel
     * @return Devuelve el cromosoma que corresponde con el tskModel
     */
    protected Chromosome createInitialChromosome() {
        double[][] param_a=tskModel.getParam_a();
        double[][] param_b=tskModel.getParam_b();
        double[][] param_c=tskModel.getParam_c();
        double[][] param_p=tskModel.getParam_p();
        
        int numRules=tskModel.getNumRules();
        int numConsecuents=tskModel.getNumConsecuents();
        int numAttributes=tskModel.getNumAttributes();
        
        DoubleGene[] genes;
        int numGenesForMF=tskModel.getNumElementsForMF();
        genes=new DoubleGene[(numRules*((numGenesForMF*numAttributes)+(numConsecuents)))];
        int indi=0;
        for (int i=0;i<numRules;i++) {
            for (int j=0;j<numAttributes;j++) {
                for (int k=0;k<numGenesForMF;k++) {
                    if (k==0) {
                        (genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]=new DoubleGene(-100,100)).setAllele(param_a[i][j]);
                    }
                    if (k==1)
                        (genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]=new DoubleGene(-100,100)).setAllele(param_b[i][j]);
                    if (k==2)
                        (genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]=new DoubleGene(-100,100)).setAllele(param_c[i][j]);
                }
            }
            
            if (numConsecuents==1) {
                (genes[((numRules*(numGenesForMF*numAttributes))+(i*numConsecuents))]=new DoubleGene(-100,100)).setAllele(param_p[i][numAttributes]);
                indi++;
            }
            if (numConsecuents>1) {
                for (int j=0;j<numConsecuents;j++) {
                    (genes[((numRules*(numGenesForMF*numAttributes))+(i*numConsecuents)+j)]=new DoubleGene(-100,100)).setAllele(param_p[i][j]);
                    indi++;
                }
            }
        }
        Chromosome chromosome=new Chromosome(genes);
        return chromosome;
    }
    /**
     * Crea un modelo TSK a partir de un cromosoma
     * @param chromosome Cromosoma del que se desea construir su TSKModel asociado
     * @param tskModel TSKModel guia
     * @throws GAException Devuelve una excepcion en caso que se produzca algun error
     * @return Devuelve el TSKModel asociado al cromosoma
     */
    protected  TSKModel createTSKFromChromosome(Chromosome chromosome, TSKModel tskModel) throws GAException {
        try {
            return CreateTSKFromChromosome.createTriangularTSKFromChromosome(chromosome, tskModel);
        } catch (Exception e) {
            throw new GAException(className+".createTSKFromChromosome: No se ha podido crear el model TSK desde un cromosoma."+e.getMessage());
        }
    }
}
