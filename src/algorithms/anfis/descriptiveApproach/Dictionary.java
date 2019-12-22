/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithms.anfis.descriptiveApproach;

import datas.RuleElement;
import java.util.Set;

/**
 * Interfaz que especifica todas las operaciones del diccionario
 *
 * @author Fernando Terroso Saenz
 */
public interface Dictionary {

    public Relationship getRelation(RuleElement pRuleElement);

    public void setRelation(RuleElement pRuleElement, Relationship pRelationship);

    public Set<RuleElement> getRuleElements();

}
