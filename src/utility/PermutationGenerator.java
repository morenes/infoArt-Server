/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utility;

/** Clase encargada de devolver la combinacion sin repeticion 
 * de elementos cogidos de m en m de una coleccion de n elementos.
 * @author Fernando Terroso Saenz
 */
public class PermutationGenerator {

    /* Numero de elementos de la coleccion */
    private int numElements;
    /* Tamaño de la combinacion */
    private int permutationSize;
    /* Array que contiene las diferentes combianciones */
    private int[] elements;

    public PermutationGenerator(int pNumElements, int pPermutationSize) throws PermutationGeneratorException{

        if(pNumElements <= pPermutationSize){
            throw new PermutationGeneratorException("Ilegal params: num. elements: " + pNumElements + " <= permutation size: " + pPermutationSize);
        }
        numElements = pNumElements;
        permutationSize = pPermutationSize;
        initElements();
    }

    private void initElements(){
        elements = new int[permutationSize];

        for(int i = 0; i< permutationSize; i++){
            elements[permutationSize-1-i] = i;
        }
    }

    public int getNumElements() {
        return numElements;
    }

    public int getPermutationSize() {
        return permutationSize;
    }


    public void reset(){

        initElements();
    }

    public int[] next(){
        int [] out = null;
        if(elements != null){
            out = elements.clone();
            elements[0]++;

            // Se ha llegado al final de un bucle
            if(elements[0]>= numElements){
                int index = 0;
                while((index < permutationSize) && ((++elements[index]) > (numElements-1-index))){
                    index++;
                }
                if(index < permutationSize){
                    for(int i = index-1; i >= 0; i--){
                        elements[i] = elements[i+1]+1;
                    }
                }
                else{
                    // Se han devuelto todas las permutaciones posibles
                    elements = null;
                }
            }
        }

        return out;
    }
}
