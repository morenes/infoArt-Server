//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2017.04.28 a las 12:11:45 PM CEST 
//


package model.clusterRes;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="place" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pila_gastada" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="pila_debil" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="cluster" type="{http://www.um.es/as}tipoCluster" maxOccurs="unbounded"/>
 *         &lt;element name="member" type="{http://www.um.es/as}tipoMember" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "place",
    "pilaGastada",
    "pilaDebil",
    "cluster",
    "member"
})
@XmlRootElement(name = "Clusters")
public class Clusters {

    protected int place;
    @XmlElement(name = "pila_gastada", type = Integer.class)
    protected List<Integer> pilaGastada;
    @XmlElement(name = "pila_debil", type = Integer.class)
    protected List<Integer> pilaDebil;
    @XmlElement(required = true)
    protected List<TipoCluster> cluster;
    @XmlElement(required = true)
    protected List<TipoMember> member;

    /**
     * Obtiene el valor de la propiedad place.
     * 
     */
    public int getPlace() {
        return place;
    }

    /**
     * Define el valor de la propiedad place.
     * 
     */
    public void setPlace(int value) {
        this.place = value;
    }

    /**
     * Gets the value of the pilaGastada property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pilaGastada property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPilaGastada().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getPilaGastada() {
        if (pilaGastada == null) {
            pilaGastada = new ArrayList<Integer>();
        }
        return this.pilaGastada;
    }

    /**
     * Gets the value of the pilaDebil property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pilaDebil property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPilaDebil().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getPilaDebil() {
        if (pilaDebil == null) {
            pilaDebil = new ArrayList<Integer>();
        }
        return this.pilaDebil;
    }

    /**
     * Gets the value of the cluster property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cluster property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCluster().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TipoCluster }
     * 
     * 
     */
    public List<TipoCluster> getCluster() {
        if (cluster == null) {
            cluster = new ArrayList<TipoCluster>();
        }
        return this.cluster;
    }

    /**
     * Gets the value of the member property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the member property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMember().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TipoMember }
     * 
     * 
     */
    public List<TipoMember> getMember() {
        if (member == null) {
            member = new ArrayList<TipoMember>();
        }
        return this.member;
    }

}
