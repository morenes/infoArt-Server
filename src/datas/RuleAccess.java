/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package datas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * Clase que modela la matriz de reglas del modelo, con sus antecedentes y
 * consecuentes.
 *
 * @author Fernando Terroso Saenz
 */
public class RuleAccess {

    RuleElement[][] ruleAntecedents;
    double[][] ruleConsequent;


    public void parse(String path){
        try{
            File file = new File(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            String line = line = reader.readLine();

            // Leemos la primera linea del fichero
            StringTokenizer token = new StringTokenizer(line, " ");
            int numRules = Integer.valueOf(token.nextToken());
            int numAntecedents = Integer.valueOf(token.nextToken());

            ruleAntecedents = new RuleElement[numRules][numAntecedents];
            ruleConsequent = new double[numRules][numAntecedents + 1];

            //Leemos el resto del fichero con las reglas
            int numRule = 0;
            while(!(line = reader.readLine() ).equals("")){
                token = new StringTokenizer(line, " ");

                //Leemos antecedente(s) de la regla
                for(int i= 0; i< numAntecedents; i++){
                    RuleElement a = RuleElement.valueOf(token.nextToken());
                    ruleAntecedents[numRule][i] = a;
                }

                // Leemos consecuente(s) de la regla
                for(int i= 0; i< numAntecedents+1; i++){
                    Double c = Double.valueOf(token.nextToken());
                    ruleConsequent[numRule][i] = c;
                }

                numRule++;

            }
        }catch( Exception e){
            e.printStackTrace();
            System.out.println("Parse: Error al leer del fichero de reglas");
        }
    }

    public int getNumRules(){
        return ruleAntecedents.length;
    }

    public int getNumAntecedents(){
        return ruleAntecedents[0].length;
    }

    public RuleElement[] getAntecedents(int numRule){
        if(numRule <= getNumRules()){
            return ruleAntecedents[numRule];
        }
        else{
            return null;
        }
    }

    public double[] getConsequents(int numRule){
        if(numRule <= getNumRules()){
            return ruleConsequent[numRule];
        }
        else{
            return null;
        }
    }
}
