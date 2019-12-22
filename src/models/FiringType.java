/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

/**
 *
 * @author fernando
 */
public enum FiringType {

    /** Cada regla aporta su grado de disparo */
    EACH_RULE_ITS_FIRE,
    /* La regla con mayor grado es la que aporta la salida */
    STRONGEST_RULE_GETS_ALL;
}
