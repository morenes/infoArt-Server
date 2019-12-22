//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2017.04.28 a las 11:46:32 AM CEST 
//


package model.cluster;

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
 *         &lt;element name="segmentos" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="var_seg" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="clusters" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="factor_tiempo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="factor_audio" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="pilas" type="{http://www.um.es/as}tipoPilas"/>
 *         &lt;element name="representante" type="{http://www.um.es/as}tipoRepresentante" maxOccurs="unbounded"/>
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
    "segmentos",
    "varSeg",
    "clusters",
    "factorTiempo",
    "factorAudio",
    "pilas",
    "representante"
})
@XmlRootElement(name = "Clustering")
public class Clustering {

    protected int place;
    protected int segmentos;
    @XmlElement(name = "var_seg")
    protected int varSeg;
    protected int clusters;
    @XmlElement(name = "factor_tiempo")
    protected int factorTiempo;
    @XmlElement(name = "factor_audio")
    protected int factorAudio;
    @XmlElement(required = true)
    protected TipoPilas pilas;
    @XmlElement(required = true)
    protected List<TipoRepresentante> representante;

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
     * Obtiene el valor de la propiedad segmentos.
     * 
     */
    public int getSegmentos() {
        return segmentos;
    }

    /**
     * Define el valor de la propiedad segmentos.
     * 
     */
    public void setSegmentos(int value) {
        this.segmentos = value;
    }

    /**
     * Obtiene el valor de la propiedad varSeg.
     * 
     */
    public int getVarSeg() {
        return varSeg;
    }

    /**
     * Define el valor de la propiedad varSeg.
     * 
     */
    public void setVarSeg(int value) {
        this.varSeg = value;
    }

    /**
     * Obtiene el valor de la propiedad clusters.
     * 
     */
    public int getClusters() {
        return clusters;
    }

    /**
     * Define el valor de la propiedad clusters.
     * 
     */
    public void setClusters(int value) {
        this.clusters = value;
    }

    /**
     * Obtiene el valor de la propiedad factorTiempo.
     * 
     */
    public int getFactorTiempo() {
        return factorTiempo;
    }

    /**
     * Define el valor de la propiedad factorTiempo.
     * 
     */
    public void setFactorTiempo(int value) {
        this.factorTiempo = value;
    }

    /**
     * Obtiene el valor de la propiedad factorAudio.
     * 
     */
    public int getFactorAudio() {
        return factorAudio;
    }

    /**
     * Define el valor de la propiedad factorAudio.
     * 
     */
    public void setFactorAudio(int value) {
        this.factorAudio = value;
    }

    /**
     * Obtiene el valor de la propiedad pilas.
     * 
     * @return
     *     possible object is
     *     {@link TipoPilas }
     *     
     */
    public TipoPilas getPilas() {
        return pilas;
    }

    /**
     * Define el valor de la propiedad pilas.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPilas }
     *     
     */
    public void setPilas(TipoPilas value) {
        this.pilas = value;
    }

    /**
     * Gets the value of the representante property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the representante property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRepresentante().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TipoRepresentante }
     * 
     * 
     */
    public List<TipoRepresentante> getRepresentante() {
        if (representante == null) {
            representante = new ArrayList<TipoRepresentante>();
        }
        return this.representante;
    }

}
