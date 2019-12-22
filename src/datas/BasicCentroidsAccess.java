/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package datas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Acceso a los datos de los centroides de un fichero de texto plano del sistema de ficheros.
 * @author Fernando Terroso Saenz
 */
public class BasicCentroidsAccess extends CentroidsAccess{

    /* Centroides indexados por circuitos (String) y reglas (RuleElement) */
    HashMap<String,HashMap<RuleElement,double[]>> centroids;

    public void parse(String path){
        try{

            File file = new File(path);
            Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            StreamTokenizer fileTokens = new StreamTokenizer(reader);
            fileTokens.resetSyntax();
            fileTokens.eolIsSignificant(true);
            fileTokens.wordChars(' ','~');
            fileTokens.commentChar('#');
            fileTokens.nextToken();

            centroids = new HashMap<String,HashMap<RuleElement,double[]>>();

            // Elementos en el mismo orden en el que estan en el fichero de centroides

             while (fileTokens.ttype != StreamTokenizer.TT_EOF) {
                 /* fileTokens.sval contiene una linea del fichero */
                 if(fileTokens.sval != null){
                    StringTokenizer stringTokens = new StringTokenizer(fileTokens.sval, " ");

                    String nombreCircuito = stringTokens.nextToken();
                    System.out.println(nombreCircuito);

                    HashMap<RuleElement,double[]> centroidesCircuito = new HashMap<RuleElement,double[]>();

                    // Accedemos a cada uno de los elementos de la linea leida
                    for(RuleElement elemento : getRules() ){

                        String aux = stringTokens.nextToken();
                        StringTokenizer auxToken = new StringTokenizer(aux, "|");
                        double media = Double.parseDouble(auxToken.nextToken());
                        double varianza = Double.parseDouble(auxToken.nextToken());
                        System.out.println("\t" + elemento +" (" + media + ", " + varianza + ")");

                        double [] auxArray = {media, varianza};
                        centroidesCircuito.put(elemento, auxArray);
                    }

                    centroids.put(nombreCircuito, centroidesCircuito);

                 }
                 fileTokens.nextToken();
             }
        }catch( Exception e){
            System.out.println("Error al leer del fichero de centroides: " + e.getMessage());
            e.printStackTrace();
        }

    }


        /**
     * Metodo que permite acceder a los datos de los centroides.
     * @param circuitName Nombre del circuito al cual se quiere acceder
     * @param element Tipo de dato a acceder (Ac+, Ac-, V+, etc)
     */
    public double[] getData(String circuitName, RuleElement element){
        HashMap<RuleElement,double[]> centroidesCircuito = centroids.get(circuitName);
        return centroidesCircuito.get(element);
    }

}
