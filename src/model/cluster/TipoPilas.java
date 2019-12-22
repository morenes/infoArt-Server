//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2017.04.28 a las 11:46:32 AM CEST 
//


package model.cluster;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tipoPilas complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipoPilas">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ruta" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="sensibilidad" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipoPilas", propOrder = {
    "ruta",
    "sensibilidad"
})
public class TipoPilas {

    protected int ruta;
    protected double sensibilidad;

    /**
     * Obtiene el valor de la propiedad ruta.
     * 
     */
    public int getRuta() {
        return ruta;
    }

    /**
     * Define el valor de la propiedad ruta.
     * 
     */
    public void setRuta(int value) {
        this.ruta = value;
    }

    /**
     * Obtiene el valor de la propiedad sensibilidad.
     * 
     */
    public double getSensibilidad() {
        return sensibilidad;
    }

    /**
     * Define el valor de la propiedad sensibilidad.
     * 
     */
    public void setSensibilidad(double value) {
        this.sensibilidad = value;
    }

}
