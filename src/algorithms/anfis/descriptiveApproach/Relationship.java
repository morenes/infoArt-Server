/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithms.anfis.descriptiveApproach;

import datas.RuleElement;

/**
 * Encapsula la relacion que existe entre un antecedente y otro en caso de que
 * la hubiera.
 *
 * @author Fernando Terroso Saenz
 */
public class Relationship {

    private RelationshipType type;
    private RuleElement element;
    // Posicion 0: media, posicion 1: varianza.
    private double[] values = null;

    public Relationship(RelationshipType pType, Object... pValues){
        type = pType;
        
        switch(type){
            case ABOVE:
            case BELOW:
                element = (RuleElement) pValues[0];
                break;
            case NO_RELATION:
                values = (double[]) pValues[0];
                break;
        }
    }

    public RuleElement getElement() {
        return element;
    }

    public void setElement(RuleElement element) {
        this.element = element;
    }

    public RelationshipType getType() {
        return type;
    }

    public void setType(RelationshipType type) {
        this.type = type;
    }

    public double[] getValues() {
        return values;
    }

    public void setValues(double[] values) {
        this.values = values;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        switch(getType()){
            case ABOVE:
            case BELOW:
                RuleElement e = getElement();
                sb.append(getType());
                sb.append("-->");
                sb.append(e);
                break;
            case NO_RELATION:
                double[] value = getValues();
                sb.append(value[0]);
                sb.append(", ");
                sb.append(value[1]);
                break;
        }
        sb.append("]");
        return sb.toString();
    }
}
