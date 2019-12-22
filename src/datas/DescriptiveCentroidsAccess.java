/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package datas;

import algorithms.anfis.descriptiveApproach.BasicDictionary;
import algorithms.anfis.descriptiveApproach.Dictionary;
import algorithms.anfis.descriptiveApproach.Relationship;
import algorithms.anfis.descriptiveApproach.RelationshipType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Clase encargada de leer los centroides en el formato descriptivo.
 *
 * @author Fernando Terroso Saenz
 */
public class DescriptiveCentroidsAccess extends CentroidsAccess{

    private  HashMap<String,Dictionary> dictionaries;

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
            
            dictionaries = new HashMap<String,Dictionary>();

            while (fileTokens.ttype != StreamTokenizer.TT_EOF) {
                if(fileTokens.sval != null){

                    StringTokenizer stringTokens = new StringTokenizer(fileTokens.sval, " ");
                    String nombreCircuito = stringTokens.nextToken();

                    Dictionary dictionary = new BasicDictionary();

                    for(RuleElement elemento : getRules() ){
                        String aux = stringTokens.nextToken();
                        StringTokenizer auxToken = new StringTokenizer(aux, "|");
                        String e = auxToken.nextToken();
                        Relationship relation;

                        if(e.matches("(-){0,1}\\d+.\\d+")){
                            double media = Double.parseDouble(e);
                            e = auxToken.nextToken();
                            double varianza = Double.parseDouble(e);
                            double[] values = {media, varianza};
                            relation = new Relationship(RelationshipType.NO_RELATION, values);
                        }
                        else{
                            RelationshipType type = RelationshipType.valueOf(auxToken.nextToken());
                            RuleElement ruleE = RuleElement.valueOf(e);
                            relation = new Relationship(type, ruleE);
                        }
                        dictionary.setRelation(elemento, relation);
                    }
                    dictionaries.put(nombreCircuito, dictionary);
                }
                fileTokens.nextToken();
            }

        }catch(Exception e){
            System.out.println("Error al parsear centroides descriptivos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    public Dictionary getDictionary(String pCircuitName){
        return dictionaries.get(pCircuitName);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        Set<String> circuits = dictionaries.keySet();

        for(String circuit : circuits){
            Dictionary dictionary = dictionaries.get(circuit);
            sb.append(circuit);
            sb.append("\n");
            sb.append(dictionary);
            sb.append("-------------");
            sb.append("\n");
        }

        return sb.toString();
    }
}
