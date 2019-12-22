/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithms.anfis;

import models.ConsequentType;

/** Clase que encapsula todos los parametros de ejecucion del algoritmo Anfis
 *
 * @author Fernando Terroso Saenz
 */
public class AnfisParams {

    /* Numero de epocas en anfis. P.ej 10*/
    private int epochs=100;
    /** P . P.ej 0.5 */
    private double p=0.1;
    /** Q . P.ej 0.6 */
    private double q=0.1;
    /** Kapa . P.ej 0.7 */
    private double kappa=0.7;
    /** nn. P.ej 8 */
    private int n=2;
    /** m. P.ej 10 */
    private int m=4;
    /* Tipo de consequente a utilizar por Anfis */
    private ConsequentType consequentType;

    /**
     * Constructor en donde se especifica el valor de cada uno de los parametros     *
     * @param pEpochs
     * @param pP
     * @param pQ
     * @param pKappa
     * @param pN
     * @param pM
     */
    
    public AnfisParams(
            int pEpochs,
            double pP,
            double pQ,
            double pKappa,
            int pN,
            int pM,
            ConsequentType pConsequentType){

        epochs= pEpochs;
        p = pP;
        q = pQ;
        kappa = pKappa;
        n = pN;
        m = pM;
        consequentType = pConsequentType;

    }

    /**
     * Constructor vacío, los parametros toman el valor por defecto.
     */
    public AnfisParams(){}
    
    
    
    /*** Metodos SET ***/

    public void setEpochs(int epochs) {
        this.epochs = epochs;
    }

    public void setKappa(double kappa) {
        this.kappa = kappa;
    }

    public void setM(int m) {
        this.m = m;
    }

    public void setN(int n) {
        this.n = n;
    }

    public void setP(double p) {
        this.p = p;
    }

    public void setQ(double q) {
        this.q = q;
    }

    public void setConsequentType(ConsequentType consequentType) {
        this.consequentType = consequentType;
    }

    /*** Fin metodos SET ***/


    /*** Metodos GET ***/

    public int getEpochs() {
        return epochs;
    }

    public double getKappa() {
        return kappa;
    }

    public int getM() {
        return m;
    }

    public int getN() {
        return n;
    }

    public double getP() {
        return p;
    }

    public double getQ() {
        return q;
    }

    public ConsequentType getConsequentType() {
        return consequentType;
    }

    /*** Fin metodos GET ***/

    @Override
    public String toString(){
        StringBuffer out = new StringBuffer();
        out.append("--Parametros para ANFIS--\n");
        out.append("Tipo de consecuente: " + consequentType + "\n");
        out.append("Epochs: " + epochs + "\n");
        out.append("Kappa: " + kappa + "\n");
        out.append("M: " + m + "\n");
        out.append("N: " + n + "\n");
        out.append("P: " + p + "\n");
        out.append("Q: " + q + "\n");
        out.append("--Fin parametros para ANFIS--\n");
        return out.toString();
    }


}
