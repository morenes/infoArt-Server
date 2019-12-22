/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package datas.preprocess;

/**
 * Clase que contiene los parametros de preprocesamiento de los datos
 *
 * @author Fernando Terroso Saenz
 */
public class DataPreProcessorParams {

    /* Directorio de donde leer los datos a preprocesar */
    private String inputDir;
    /* Directorio donde dejar los datos ya preprocesados */
    private String outputDir;
    /* Tipo de maniobra a tratar */
    private String manauverToHandle;
    /* Numero de instancias de manauverToHandle que se pretende alcanzar */
    private int numberToReach;

    public DataPreProcessorParams(){}

    public String getInputDir() {
        return inputDir;
    }

    public void setInputDir(String inputDir) {
        this.inputDir = inputDir;
    }

    public String getManauverToHandle() {
        return manauverToHandle;
    }

    public void setManauverToHandle(String manauverToHandle) {
        this.manauverToHandle = manauverToHandle;
    }

    public int getNumberToReach() {
        return numberToReach;
    }

    public void setNumberToReach(int numberToReach) {
        this.numberToReach = numberToReach;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

}
