package algorithms.ag;

import org.jgap.impl.*;
import org.jgap.*;
import models.tskModel.*;

/**
 * Algoritmo Genetico con funcion de pertenencia Gaussiana
 * @author David Gil Galvan
 */
public class GAFuzzyGaussian extends GAFuzzy {
    /**
     * Nombre de la clase
     */
    protected String className=this.getClass().getName();
    
    /**
     *
     * Constructor del algoritmo genetico gaussiano
     * @param L Tamano de la poblacion
     * @param T Numero de generaciones
     * @param alfa1 Umbral para las restricciones espaciales de los antecedes
     * @param alfa2 Umbral para las restricciones espaciales de los consecuentes
     * @param nc Numero de cromosomas a evolucionar en las sucesivas generaciones
     * @param probCrossover Probabilidad de cruzamiento
     * @param inputData Matriz de datos de entrada
     * @param outputData Vector de datos de salida
     */
    public GAFuzzyGaussian(int L, int T, double alfa1, double alfa2, int nc, double probCrossover, double[][] inputData, double[] outputData) {
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
     * Comprueba que el TSKModel es del tipo Gaussiano
     * @throws GAException Devuelve una excepcion en caso que se produzca algun error
     */
    protected void checkTSKModel() throws GAException{
        if (!(tskModel instanceof GaussianTSKModel))
            throw new GAException(className+".checkTSKModel: El modelo TSK es incorrecto");
    }
    
    /**
     * Crea la funcion fitness segun el tsk Model gaussiano
     * @param inputData Matriz de datos de entrada
     * @param outputData Vector de datos de salida
     * @param tskModel tskModel que sirve de guia para crear el resto de tskModel
     * @return Devuelve el TSKModel asociado al cromosoma
     */
    protected FuzzyFitnessFunction createFuzzyFitnessFunction(double[][] inputData,double[] outputData,TSKModel tskModel) {
        return new GaussianFuzzyFitnessFunction(inputData, outputData, tskModel);
    }
    
    /**
     * Metodo que calcula las restricciones espaciales a partir del cromosoma inicial y en funcion del tskModel
     * con el que se este trabajando
     * @param chromosome Cromosoma Inicial
     */
    protected void calculateV(Chromosome chromosome) {
        
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
                for (int k=0;k<numGenesForMF;k++) { // Para a o c
                    
                    if (k==1) { //c Media
                        valmin=Double.POSITIVE_INFINITY;
                        valmax=Double.NEGATIVE_INFINITY;
                        double a=((DoubleGene)genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+0)]).doubleValue();
                        double c=((DoubleGene)genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+1)]).doubleValue();
                        for (int z=0;z<inputData.length;z++) {
                            if (Math.abs(inputData[z][j]-c)>valmax) {
                                valmax=Math.abs(inputData[z][j]-c);
                            }
                        }
                        //System.out.println("k:"+((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k));
                        vmin[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]=genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)].doubleValue()-(valmax*alfa1);
                        vmax[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]=genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)].doubleValue()+(valmax*alfa1);
                    }
                    
                    if (k==0) {   // a Variance                        
                        double valTem=genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)].doubleValue();
                        vmin[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]=genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)].doubleValue()-(valTem*alfa1);
                        if (vmin[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]<0)
                            vmin[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]=0.001;
                        vmax[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]=genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)].doubleValue()+(valTem*alfa2);
                        if (vmax[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]<0)
                            vmax[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+k)]=0.001;
                    }
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
     * @param chromosome Cromosoma sobre el que se quiere determinar si cumple las restricciones de partición
     * @throws GAException Devuelve una excepcion en caso que se produzca algun error
     **/
    protected void checkConstraintPartition(Chromosome chromosome) throws GAException{
        try {
            Gene[] genes=chromosome.getGenes();
            int numRules=tskModel.getNumRules();
            int numAttributes=tskModel.getNumAttributes();
            int numGenesForMF=tskModel.getNumElementsForMF();
            
            
            boolean noChanged=false;
            while (noChanged==true) {
                int i=0;
                noChanged=false;
                while ((i<numRules)&&(!noChanged)) {
                    int j=0;
                    while ((j<numAttributes)&&(!noChanged)) {
                        double a=((DoubleGene)genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+0)]).doubleValue();
                        double c=((DoubleGene)genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+1)]).doubleValue();
                        int best=-1;
                        double diferencia=Double.POSITIVE_INFINITY;
                        double an;
                        double bn;
                        double cn;
                        
                        for (int k=0;k<numRules;k++) {  // Para cada regla
                            if (i!=k) {
                                
                                an=((DoubleGene)genes[((k*numAttributes*numGenesForMF)+(j*numGenesForMF)+0)]).doubleValue();
                                cn=((DoubleGene)genes[((k*numAttributes*numGenesForMF)+(j*numGenesForMF)+1)]).doubleValue();
                                
                                if ((cn>c)&&(((cn-3*an)-(c+3*a))<diferencia)) {
                                    best=k;
                                    diferencia=((cn-3*an)-(c+3*a));
                                }
                            }
                        }
                        
                        if (best!=-1) {
                            an=((DoubleGene)genes[((best*numAttributes*numGenesForMF)+(j*numGenesForMF)+0)]).doubleValue();
                            cn=((DoubleGene)genes[((best*numAttributes*numGenesForMF)+(j*numGenesForMF)+1)]).doubleValue();
                            if((cn-3*an)>(c+3*a)){
                                genes[((i*numAttributes*numGenesForMF)+(j*numGenesForMF)+0)].setAllele(new Double(((cn-3*an)-c)/3));
                                genes[((best*numAttributes*numGenesForMF)+(j*numGenesForMF)+0)].setAllele(new Double((cn-(c+3*a))/3));
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
        for (int ii=0;ii<genes.length;ii++) {
            System.out.println(genes[ii]);
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
            return CreateTSKFromChromosome.createGaussianTSKFromChromosome(chromosome, tskModel);
        } catch (Exception e) {
            throw new GAException(className+".createTSKFromChromosome: No se ha podido crear el model TSK desde un cromosoma."+e.getMessage());
        }
    }
    
}
