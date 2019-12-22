/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

/**
 *
 * @author fernando
 */
public enum ConsequentType {

    /* Consecuente de orden 0 */
    SINGLETON ("S"),
    /* Consecuente de orden mayor que 0 */
    LINEAR ("L");
    
    private final String ID;
    
    ConsequentType(String ID){
        this.ID = ID;        
    }
    
    public String getID(){
        return ID;
    }

    
}
