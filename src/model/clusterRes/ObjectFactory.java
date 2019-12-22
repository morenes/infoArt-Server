//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2017.04.28 a las 12:11:45 PM CEST 
//


package model.clusterRes;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the tipos package. 
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
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: tipos
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TipoMember }
     * 
     */
    public TipoMember createTipoMember() {
        return new TipoMember();
    }

    /**
     * Create an instance of {@link TipoCluster }
     * 
     */
    public TipoCluster createTipoCluster() {
        return new TipoCluster();
    }

    /**
     * Create an instance of {@link Clusters }
     * 
     */
    public Clusters createClusters() {
        return new Clusters();
    }

    /**
     * Create an instance of {@link TipoMember.Cluster }
     * 
     */
    public TipoMember.Cluster createTipoMemberCluster() {
        return new TipoMember.Cluster();
    }

    /**
     * Create an instance of {@link TipoCluster.Var }
     * 
     */
    public TipoCluster.Var createTipoClusterVar() {
        return new TipoCluster.Var();
    }

}
