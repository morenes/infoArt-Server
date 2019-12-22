package algorithms.VariableSelection;


import algorithms.PartitionedSpace;
import models.fcsModel.*;

/**
 * Clase que implementa el algoritmo que determina que variables de entrada son mas adecuadas para aplicar los 
 * algoritmos a partir de un FCS
 * @author David Gil Galvan
 */
public class VariableSelection {
    
    /**
     * Fuzzy Cluster Set
     */
    protected FuzzyClusterSet fcs;
    /**
     * Nombre de la clase
     */
    protected String className=this.getClass().getName();
            
    /**
     * Constructor de la clase
     * @param fcs Fuzzy Cluster Set sobre el que se determinará qué variables se deben seleccionar
     * @param umbral Umbral
     */
    public VariableSelection(FuzzyClusterSet fcs) {
        try {
            this.fcs=fcs;            
            // Por ahora este algoritmo solo esta permitido para la particion de salida
            if (!fcs.getPSpace().equals(PartitionedSpace.O))
                throw new VariableSelectionException(className+".VariableSelection: Solo esta permitido espacio de particion en O");
        } catch (Exception e)  {
            System.err.println(className+".VariableSelection: Se ha producido un error al ejecutar el Algoritmo de Seleccion de Variables. ");
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    /**
     * Metodo que ejecuta el algoritmo que devuelve un vector con la importancia asociada a cada variable
     * para tenerla en cuenta en el resto de algoritmos
     * @return Devuelve la importancia asociada a cada variable de entrada
     * @throws VariableSelectionException Devuelve una excepcion en caso que se produzca algun error     
     */
    public double[] execute() {
        try {
            // Numero de cluster
            int c=fcs.getNumCluster();
            // Datos de entrada
            double[][] inputData=fcs.getInputData();
            // Variables de entrada del conjunto de datos
            int p=inputData[0].length;
            double[][] data=fcs.getPartitionedData();            
            // Vector que contendra la importancia asociada a cada variable de entrada
            double[] vectorPi=new double[p];
            
            for (int j=0;j<p;j++) {
                System.out.println("Funcionando con p:"+j);
                vectorPi[j]=1;
                double max_Tij=Double.NEGATIVE_INFINITY;
                double min_Tij=Double.POSITIVE_INFINITY;
                double max_Tj=Double.NEGATIVE_INFINITY;
                double min_Tj=Double.POSITIVE_INFINITY;
                for (int i=0;i<c;i++) { // Por cada cluster
                    
                    // Esta opcion de ir decrementando el umbral hasta
                    // que haya algun max_Tij y min_Tij es cosa nuestra
                    // Puede dar algun problema
                    
                    double umbralAux=1.0;
                    do {                        
                        for (int k=0;k<data.length;k++) { // Por cada ejemplo
                            System.out.println("Funcionando con K:"+k);
                            double membership=fcs.getMembership(data[k],i);
                            System.out.println("min_Tj_A:"+min_Tj);
                            System.out.println("max_Tj_A:"+max_Tj);
                            System.out.println("UMBRAL_A:"+umbralAux);
                            System.out.println("MEM:"+membership);
                            if (membership>=umbralAux) {                                
                                if (inputData[k][j]>max_Tij) {
                                    max_Tij=inputData[k][j];                                    
                                }
                                if (inputData[k][j]<min_Tij) {
                                    min_Tij=inputData[k][j];                                    
                                }
                            }
                            if (inputData[k][j]>max_Tj)
                                max_Tj=inputData[k][j];
                            if (inputData[k][j]<min_Tj)
                                min_Tj=inputData[k][j];                            
                        }
         
                        umbralAux-=0.1;
                        System.out.println("min_Tj:"+min_Tj);
                        System.out.println("max_Tj:"+max_Tj);
                        System.out.println("UMBRAL:"+umbralAux);
                    } while ((max_Tij==Double.NEGATIVE_INFINITY)||(min_Tij==Double.POSITIVE_INFINITY));
                    System.out.println("UMBRALFIN:"+umbralAux);
                    
                    double division=0;
                    // Si el maximo y el minimo globales son iguales
                    // Si max_Tj==min_Tj y si max_Tij=min_Tij asignarles division=1 es cosa nuestra
                    // Puede dar algun problema
                    if ((max_Tj==min_Tj)) division=1.0;
                    else {                        
                            division=((max_Tij-min_Tij)/(max_Tj-min_Tj));                        
                    }
                    
                    vectorPi[j]=vectorPi[j]*division;
                }
            }
            
            return vectorPi;
        } catch (Exception e)  {
            System.err.println(className+".execute: Se ha producido un error al ejecutar el VariableSelection. ");
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }
    
    
    
}
