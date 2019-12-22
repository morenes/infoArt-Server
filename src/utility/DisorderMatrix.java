/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utility;

/**
 * Clase encargada de almacenar la matriz de desorden de un determinado modelo
 * utilizando los datos del fichero contenido en idealMatrix
 * @author Fernando Terroso Saenz
 */
public class DisorderMatrix {

    IdealMatrix idealMatrix;
    int [][] disorderMatrix;
    int numErrors = 0;

    public DisorderMatrix(IdealMatrix pIdealMatrix, int[][] pMatrix){
        idealMatrix = pIdealMatrix;
        disorderMatrix = pMatrix;

        for(int i = 0; i< disorderMatrix.length; i++){
            for(int j = 0; j < disorderMatrix[0].length; j++){
                if(i != j){
                    numErrors += disorderMatrix[i][j];
                }
            }
        }
    }

    public int[][] getDisorderMatrix() {
        return disorderMatrix;
    }

    public IdealMatrix getIdealMatrix() {
        return idealMatrix;
    }

    public int getNumErrors() {
        return numErrors;
    }
    
}
