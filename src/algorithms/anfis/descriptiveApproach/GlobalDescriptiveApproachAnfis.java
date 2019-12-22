/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithms.anfis.descriptiveApproach;

import algorithms.anfis.Anfis;
import algorithms.anfis.AnfisException;
import algorithms.anfis.AnfisParams;
import algorithms.anfis.MF;
import datas.RuleAccess;
import datas.RuleElement;
import java.util.Arrays;
import java.util.HashMap;
import models.FiringType;
import models.MembershipFunctionType;
import utility.Error;


/**
 * Clase que implementa el algoritmo ANFIS pero enfocado a trabajar en un 
 * entorno totalmente descriptivo en donde los
 *
 * @author Fernando Terroso Saenz
 */
public class GlobalDescriptiveApproachAnfis extends Anfis{

    /* Estructura de las reglas */
    RuleAccess ruleAccess;
    /* Establece las relaciones entre los diferentes antecedentes */
    Dictionary dictionary;
    /* Asocia cada antecedente a una columna en la matriz de ejemplos de entrenamiento */
    HashMap<RuleElement, Integer> indexes;

    public GlobalDescriptiveApproachAnfis(
            RuleAccess pRuleAccess,
            Dictionary pDictionary,
            HashMap<RuleElement, Integer> pIndexes,
            double[][] pInputData,
            double[] pOutputData,
            FiringType pFiringType,
            MembershipFunctionType pFsetShape,
            AnfisParams pAnfisParams){

        ruleAccess = pRuleAccess;
        dictionary = pDictionary;
        indexes = pIndexes;

        mx = pInputData;
        vy = pOutputData;

        numVar = ruleAccess.getNumAntecedents();
        numRule = ruleAccess.getNumRules();
        numMF = ruleAccess.getNumRules();

        param_p = new double[numRule][numVar+1];
        for(int i = 0; i< numRule; i++){
            param_p[i] = Arrays.copyOf(ruleAccess.getConsequents(i), numVar+1);
        }

        create(pFiringType, pFsetShape, pAnfisParams);
    }

    @Override
    protected void calculate_mf(boolean w){
        int i,j;
        for (i=0; i<numMF; i++) {//coincide numMF con el numero de reglas
            RuleElement[] antecedents = ruleAccess.getAntecedents(i);
            for(RuleElement antecedent : antecedents){
                int index = indexes.get(antecedent);
                switch(membership_type){
                    case GAUSSIAN:
                        double value = getGaussMF(antecedent, x[index]);
                        mf[i][index] = value;
                        break;
                    case BELL:
                        break;
                }
            }
        }
    }

    /** Modificacion del paso hacia atras para soportar las especificaciones
     * propias del enfoque totalmente descriptivo.
     * 
     * @throws java.lang.NullPointerException
     * @throws algorithms.anfis.AnfisException
     */

    @Override
    protected void backward_pass() throws NullPointerException, AnfisException{

        if (mx==null || vy==null) throw new NullPointerException("Mx o Vy son nulos");

        int i,j,p, numPat=mx.length;
        double  dMF_da[][]=new double [numRule][numVar],
                dMF_db[][]=new double [numRule][numVar],
                dMF_dc[][]=new double [numRule][numVar];
        double delta_a[][]=new double [numRule][numVar],
                delta_b[][]=new double [numRule][numVar],
                delta_c[][]=new double [numRule][numVar];

        double factor;
        
        double anfis_output[]=new double [numPat];
        Error E=new Error(vy);

        for (p=0; p<numPat; p++) {
            double y = vy[p];
            calculate(L_INPUT, L_F, mx[p], false);

            anfis_output[p]=f;

            for (i=0; i<numRule; i++){
                RuleElement[] antecedents = ruleAccess.getAntecedents(i);
                for(RuleElement antecedent : antecedents){
                    int index = indexes.get(antecedent);
                    switch(membership_type){
                        case GAUSSIAN:
                            dMF_da[i][index]= getDGaussOverA(antecedent, mx[p][index]);
                            dMF_dc[i][index]= getDGaussOverC(antecedent, mx[p][index]);
                            break;
                        case BELL:
                            break;
                    }
                }
            }

            for (i=0; i<numRule; i++){
                for (j=0; j<numVar; j++) {
                    // Este factor es 0 si el número de cluster es 0, ya que la salida de la regla i-esima es igual
                    // a la suma de las salidas de todas las reglas(ya que solo hay una regla)
                    factor = -2*(y-f)*(fi[i]-normTau[i]*f);

                    // Como el factor es cero si el numero de cluster es 1 los delta_a, delta_b y delta_c son 0
                    switch(membership_type){
                        case BELL:
                            delta_b[i][j] += factor*dMF_db[i][j];
                        case GAUSSIAN:
                            delta_a[i][j] += factor*dMF_da[i][j];
                            delta_c[i][j] += factor*dMF_dc[i][j];
                            break;
                    }

                }
            }
        }

        double normAlfa=0;
        for (i=0; i<numRule; i++)
            for (j=0; j<numVar; j++){
                // Al ser todos los delta=0 (si el numero de cluster es 1) la normaAlfa es 0
                normAlfa+=Math.pow(delta_a[i][j],2)+Math.pow(delta_b[i][j],2)+Math.pow(delta_c[i][j],2);
            }
        normAlfa=Math.sqrt(normAlfa);
        if (normAlfa==0) {
            throw new AnfisException("normaAlfa es igual a 0");
        }
        // Al ser la normaAlfa=0 si el numero de cluster=1 la division es infinito y FALLA
        factor= stepSize.getStepSize() / normAlfa;
        for (i=0; i<numRule; i++){
            RuleElement[] antecedents = ruleAccess.getAntecedents(i);
            for(RuleElement antecedent : antecedents){
                int index = indexes.get(antecedent);
                modifyParamAValue(antecedent, factor*delta_a[i][index]);
                modifyParamBValue(antecedent, factor*delta_b[i][index]);
                modifyParamCValue(antecedent, factor*delta_c[i][index]);
            }
        }

        error=E.RMSE(anfis_output);
        stepSize.addError(error);
    }


    private void modifyParamAValue(RuleElement pElement, double pModificator){

        modifyParamValue(pElement, pModificator, 1);
    }

    private void modifyParamBValue(RuleElement pElement, double pModificator){

        modifyParamValue(pElement, pModificator, 1);
    }

    private void modifyParamCValue(RuleElement pElement, double pModificator){

        modifyParamValue(pElement, pModificator, 0);
    }


    private void modifyParamValue(RuleElement pElement, double pModificator, int indexParam){

        Relationship relation = dictionary.getRelation(pElement);
        RuleElement aux = pElement;

        switch(relation.getType()){
            case ABOVE:
            case BELOW:
                aux = relation.getElement();
                relation = dictionary.getRelation(aux);
                break;
        }
        
        double[] value = relation.getValues();

        value[indexParam] = value[indexParam] - pModificator;
        relation.setValues(value);
        dictionary.setRelation(aux, relation);
    }



    /**
     * Metodo que calcula el grado de pertenencia de un ejemplo con respecto al
     * conjunto difuso definido por el antecedente. Dicho conjunto difuso
     * puede ser calculado en funcion de otro antedecente diferente.
     * @param pAntecedent
     * @return
     */
    private double getGaussMF(RuleElement pAntecedent, double x){

        Relationship relation = dictionary.getRelation(pAntecedent);
        double mfVal = 0;

        switch(relation.getType()){
            case NO_RELATION:
                double values[] = relation.getValues();
                mfVal = MF.gauss_MF(values[1], values[0], x);
                break;
            case BELOW:
                RuleElement re = relation.getElement();
                relation = dictionary.getRelation(re);
                values = relation.getValues();
                mfVal = MF.below_gauss_MF(values[1], values[0], x);
                break;
            case ABOVE:
                re = relation.getElement();
                relation = dictionary.getRelation(re);
                values = relation.getValues();
                mfVal = MF.above_gauss_MF(values[1], values[0], x);
                break;
        }

        return mfVal;
    }

    /**
     * Metodo que calcula el grado de pertenencia de un ejemplo con respecto al
     * conjunto difuso definido por la derivada de la funcion de pertenencia
     * original del antecedente. Dicho conjunto difuso
     * puede ser calculado en funcion de otro antedecente diferente.
     * @param pAntecedent
     * @return
     */
    private double getDGaussOverA(RuleElement pAntecedent, double x){

        Relationship relation = dictionary.getRelation(pAntecedent);
        double mfVal = 0;

        switch(relation.getType()){
            case NO_RELATION:
                double values[] = relation.getValues();
                mfVal = MF.dGauss_over_a(values[1], values[0], x);
                break;
            case BELOW:
                RuleElement re = relation.getElement();
                relation = dictionary.getRelation(re);
                values = relation.getValues();
                mfVal = MF.dbelow_Gauss_over_a(values[1], values[0], x);
                break;
            case ABOVE:
                re = relation.getElement();
                relation = dictionary.getRelation(re);
                values = relation.getValues();
                mfVal = MF.dabove_Gauss_over_a(values[1], values[0], x);
                break;
        }

        return mfVal;
    }

        /**
     * Metodo que calcula el grado de pertenencia de un ejemplo con respecto al
     * conjunto difuso definido por la derivada de la funcion de pertenencia
     * original del antecedente. Dicho conjunto difuso
     * puede ser calculado en funcion de otro antedecente diferente.
     * @param pAntecedent
     * @return
     */
    private double getDGaussOverC(RuleElement pAntecedent, double x){

        Relationship relation = dictionary.getRelation(pAntecedent);
        double mfVal = 0;

        switch(relation.getType()){
            case NO_RELATION:
                double values[] = relation.getValues();
                mfVal = MF.dGauss_over_c(values[1], values[0], x);
                break;
            case BELOW:
                RuleElement re = relation.getElement();
                relation = dictionary.getRelation(re);
                values = relation.getValues();
                mfVal = MF.dbelow_Gauss_over_c(values[1], values[0], x);
                break;
            case ABOVE:
                re = relation.getElement();
                relation = dictionary.getRelation(re);
                values = relation.getValues();
                mfVal = MF.dabove_Gauss_over_c(values[1], values[0], x);
                break;
        }

        return mfVal;
    }

   // @Override
    protected void printAntecedentsAndConsequents(){

        System.out.println("\nAntecedentes:");
        System.out.println(dictionary);
        System.out.println("Consecuentes:");
        System.out.println("Param P: " + Arrays.deepToString(param_p));
        System.out.println("");

    }


}
