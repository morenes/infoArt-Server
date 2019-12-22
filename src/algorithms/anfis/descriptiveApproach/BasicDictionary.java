/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithms.anfis.descriptiveApproach;

import datas.RuleElement;
import java.util.HashMap;
import java.util.Set;

/**
 * Clase basica de diccionario para Anfis descriptivo
 *
 * @author Fernando Terroso Saenz
 */
public class BasicDictionary implements Dictionary{

    HashMap<RuleElement, Relationship> dictionary;
    
    public BasicDictionary(){
        dictionary = new HashMap<RuleElement, Relationship>();
    }

    public Relationship getRelation(RuleElement pRuleElement) {
        return dictionary.get(pRuleElement);
    }

    public void setRelation(RuleElement pRuleElement, Relationship pRelationship) {
        dictionary.put(pRuleElement, pRelationship);
    }

    public Set<RuleElement> getRuleElements(){
        return dictionary.keySet();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        Set<RuleElement> rElements = dictionary.keySet();
        for(RuleElement rElement : rElements){
            sb.append(rElement);
            sb.append(" ");
            Relationship relation = dictionary.get(rElement);
            sb.append(relation);
            sb.append("\n");
        }

        return sb.toString();
    }
}
