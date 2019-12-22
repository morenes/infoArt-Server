package model.mapa;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the mapa.tipos package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: mapa.tipos
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MapaXML }
     * 
     */
    public MapaXML createMapa() {
        return new MapaXML();
    }

    /**
     * Create an instance of {@link TipoBeacon }
     * 
     */
    public TipoBeacon createTipoBeacon() {
        return new TipoBeacon();
    }

    /**
     * Create an instance of {@link TipoCoef }
     * 
     */
    public TipoCoef createTipoCoef() {
        return new TipoCoef();
    }

}
