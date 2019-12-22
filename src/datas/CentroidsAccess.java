/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package datas;

/**
 * Clase abstracta que define los elementos comunes a todas las clases de acceso
 * a los centroides
 *
 * @author Fernando Terroso Saenz
 */
public abstract class CentroidsAccess {

    private RuleElement[] reglas = {RuleElement.VELz,RuleElement.VELp,RuleElement.ACn,RuleElement.ACz,RuleElement.ACp};

   /**
    * Metodo abstarcto que se encarga de parsear la entrada con los centroides
    * @param path URL con la ruta hacia el fichero de los centroides.
    */
    public abstract void parse(String path);

    public RuleElement[] getRules(){
        return reglas;
    }

}
