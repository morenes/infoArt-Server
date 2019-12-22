/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithms.anfis;

/**
 * Clase encargada de implementar la funcionalidad del anfis descriptivo. En
 * este caso se tiene, para cada variable, los mismos conjuntos difusos para
 * todas las reglas.
 *
 * @author Mercedes Valdés Vela & Fernando Terroso Saenz
 */
import java.util.ArrayList;
import models.FiringType;
import models.MembershipFunctionType;
import models.tskModel.*;

public class DescriptiveAnfis extends Anfis{
    //distribucion de los FS por las reglas
    protected ArrayList<Double> aValues;
    protected ArrayList<Double> bValues;
    protected ArrayList<Double> cValues;

    // Asocia a cada variable dentro de cada regla una posicion en la lista
    // de valores (a|b|c)Values.
    protected int paramAIndexes[][];
    protected int paramBIndexes[][];
    protected int paramCIndexes[][];


    /**
     * Constructor a partir de un TSK dado. Se calcula las funciones de
     * membresía comunes analizando las matrices a, b, c y p almacenadas dentro
     * del TSK.
     * @param tskModel TSK a partir del cual se construirá el AD.
     * @param inputData Datos de entrada de entrenamiento.
     * @param outputData Salidas asociadas con los datos de entrenamiento
     * @param anfisParams Parametros para el LSE (m, n, p, q)
     */
    public DescriptiveAnfis(
            TSKModel tskModel,
            double[][] inputData,
            double[] outputData,
            AnfisParams anfisParams){

        super(tskModel, inputData, outputData, anfisParams);
        aValues = new ArrayList<Double>();
        bValues = new ArrayList<Double>();
        cValues = new ArrayList<Double>();

        paramAIndexes = new int[numRule][numVar];
        paramBIndexes = new int[numRule][numVar];
        paramCIndexes = new int[numRule][numVar];

        setRulesIndex(tskModel);
    }

    /**
     * Constructor para crear el Anfis Descriptivo (AD) a partir de un conjunto
     * de arrays (centroides) ya establecidos como descriptivos.
     * @param pParam_a Parametros descriptivos A (antecedentes)
     * @param pParam_b Parametros descriptivos B (antecedentes)
     * @param pParam_c Parametros descriptivos C (antecedentes)
     * @param pParam_p Parametros descriptivos P (consecuentes)
     * @param pInputData Datos de entrenamiento
     * @param pOutputData Salidas asociadas a los datos de entrenamiento
     * @param pConsequentType Tipo de consecuente (LINEAL|SINGLETON)
     * @param pFiringType Tipo de encendido de las reglas.
     * @param pFsetShape Forma de la funcion de pertenencia
     * @param pAnfisParams Parametros para el lse (m, n, p, q)
     */
    public DescriptiveAnfis(
            double[][] pParam_a,
            double[][] pParam_b,
            double[][] pParam_c,
            double[][] pParam_p,
            double[][] pInputData,
            double[] pOutputData,
            FiringType pFiringType,
            MembershipFunctionType pFsetShape,
            AnfisParams pAnfisParams){

        super(pParam_a,
                pParam_b,
                pParam_c,
                pParam_p,
                pInputData,
                pOutputData,
                pFiringType,
                pFsetShape,
                pAnfisParams);

        aValues = new ArrayList<Double>();
        bValues = new ArrayList<Double>();
        cValues = new ArrayList<Double>();

        paramAIndexes = new int[numRule][numVar];
        paramBIndexes = new int[numRule][numVar];
        paramCIndexes = new int[numRule][numVar];

        getIndirection(pParam_a, paramAIndexes, aValues);
        getIndirection(pParam_c, paramCIndexes, cValues);

        if(pParam_b != null){
            getIndirection(pParam_b, paramBIndexes, bValues);
        }
        else{
            bValues.add(0.0);
        }
    }

    /**
     * Metodo que obtiene las indirecciones de los arrays contenidos en el
     * TSK model a partir del cual se quiere construir el Anfis Descriptivo
     * @param pModel
     */
    private void setRulesIndex(TSKModel pModel){

        getIndirection(pModel.getParam_a(), paramAIndexes, aValues);
        getIndirection(pModel.getParam_c(), paramCIndexes, cValues);

        if(pModel.getParam_b() != null){
            getIndirection(pModel.getParam_b(), paramBIndexes, bValues);
        }
        else{
            bValues.add(0.0);
        }

    }

    /**
     * Metodo que realiza la indireccion necesaria en el Anfis Descriptivo
     * @param pRules Array con los centroides 'en crudo'
     * @param indexes Array donde dejar los indices que apunta a posiciones de values
     * @param values Lista con los diferentes valores contenidos en pRules.
     */
    private void getIndirection(double[][] pRules, int indexes[][], ArrayList<Double> values){

        int size = 0;
        int numRows = pRules.length;
        int numColumns = pRules[0].length;
        for(int i = 0; i< numColumns; i++){
            ArrayList<Double> valuesVar = new ArrayList<Double>();
            for(int j = 0; j< numRows; j++){
                if(valuesVar.contains(pRules[j][i])){
                    indexes[j][i] = size + valuesVar.indexOf(pRules[j][i]);
                }
                else{
                    valuesVar.add(pRules[j][i]);
                    indexes[j][i] = size + valuesVar.size()-1;
                }
            }
            size += valuesVar.size();
            values.addAll(valuesVar);
        }
    }

        /* Metodos que devuelven el valor asociado a una determinada posición en */
    /* el array */
    @Override
    protected double getParamAValue(int i, int j){
        int index = paramAIndexes[i][j];
        return aValues.get(index);
    }

    @Override
    protected double getParamBValue(int i, int j){
        int index = paramBIndexes[i][j];
        return bValues.get(index);
    }

    @Override
    protected double getParamCValue(int i, int j){

        int index = paramCIndexes[i][j];
        return cValues.get(index);
    }

    /* Metodos que establecen el valor asociado a una determinada posición en */
    /* el array */
    @Override
    protected void setParamAValue(int i, int j, double value){
        int index = paramAIndexes[i][j];
        aValues.set(index, value);
    }

    @Override
    protected void setParamBValue(int i, int j, double value){
        int index = paramBIndexes[i][j];
        bValues.set(index, value);
    }

    @Override
    protected void setParamCValue(int i, int j, double value){
        int index = paramCIndexes[i][j];
        cValues.set(index, value);
    }


    /* Metodos publicos de acceso a los arrays enteros */
    @Override
    public double[][] get_param_a() {
        double pa[][] = null;
        if(aValues.size() > 0){
            pa = new double[numRule][numVar];
            for(int i = 0; i< numRule; i++){
                for(int j = 0; j< numVar; j++){
                    int index = paramAIndexes[i][j];
                    pa[i][j] = aValues.get(index);
                }
            }
        }
        return pa;
    }

    @Override
    public double[][] get_param_b() {
        double pb[][] = null;
        if(bValues.size() > 0){
            pb = new double[numRule][numVar];
            for(int i = 0; i< numRule; i++){
                for(int j = 0; j< numVar; j++){
                    int index = paramBIndexes[i][j];
                    pb[i][j] = bValues.get(index);
                }
            }
        }
        return pb;
    }

    @Override
    public double[][] get_param_c() {

        double pc[][] = null;
        if(cValues.size() > 0){
            pc= new double[numRule][numVar];
            for(int i = 0; i< numRule; i++){
                for(int j = 0; j< numVar; j++){
                    int index = paramCIndexes[i][j];
                    pc[i][j] = cValues.get(index);
                }
            }
        }
        return pc;
    }
}
