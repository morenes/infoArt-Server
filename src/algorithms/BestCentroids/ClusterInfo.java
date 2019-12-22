/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithms.BestCentroids;

import algorithms.PartitionedSpace;

/**
 * Clase que guarda la información de un cluster a ser generado por Best
 * Centroids. Esta clase es sobre todo utilizada en el paquete test
 *
 * @author Fernado Terroso Saenz
 */
public class ClusterInfo {

    private int[] inputColumns;
    private int outputColumn;
    private int numClusters;
    private PartitionedSpace space;

    public ClusterInfo(){}
    
    public ClusterInfo(
            int[] pInputColumns,
            int pOutputColumn,
            int pNumClusters,
            PartitionedSpace pSpace){

        inputColumns = pInputColumns;
        outputColumn = pOutputColumn;
        numClusters = pNumClusters;
        space = pSpace;

    }

    public int[] getInputColumns() {
        return inputColumns;
    }

    public void setInputColumns(int[] inputColums) {
        this.inputColumns = inputColums;
    }

    public int getNumClusters() {
        return numClusters;
    }

    public void setNumClusters(int numClusters) {
        this.numClusters = numClusters;
    }

    public int getOutputColumn() {
        return outputColumn;
    }

    public void setOutputColumn(int outputColumn) {
        this.outputColumn = outputColumn;
    }

    public PartitionedSpace getSpace() {
        return space;
    }

    public void setSpace(PartitionedSpace space) {
        this.space = space;
    }



    @Override
    public String toString(){

        StringBuilder sb = new StringBuilder();
        sb.append("Cluster:\n\tEspacio:");
        sb.append(space);
        sb.append("\n\tInput: [");
        for(int i : inputColumns){
            sb.append(i);
            sb.append(", ");
        }
        sb.replace(sb.length()-2, sb.length(), "");
        sb.append("]\n\tOutput: ");
        sb.append(outputColumn);
        sb.append("\n\tNumero: ");
        sb.append(numClusters);
        sb.append("\n");

        return sb.toString();
    }

}
