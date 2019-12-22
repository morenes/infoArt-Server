/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utility;

/**
 * Clase que contiene el numero de ejemplos de cada tipo en un fichero de
 * entrada dado.
 * @author Feranando Terroso Saenz
 */
public class IdealMatrix {

    private int[] matrix;
    private int numInstances = 0;
    private String fileName;

    public IdealMatrix( String pFileName, double[] pOutputs, int numClasses){

        matrix = new int[numClasses];
        for(int i = 0; i < pOutputs.length; i++){

            int out = (int) pOutputs[i];
            matrix[out-1]++;
        }
        numInstances = pOutputs.length;
        fileName = pFileName;
    }

    public int[] getMatrix() {
        return matrix;
    }

    public int getNumInstances() {
        return numInstances;
    }

    public String getFileName() {
        return fileName;
    }

    

}
