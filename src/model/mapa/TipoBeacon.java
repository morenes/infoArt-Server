package model.mapa;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipoBeacon", propOrder = {
    "id",
    "x",
    "y",
    "coef"
})
public class TipoBeacon {

    @XmlSchemaType(name = "unsignedByte")
    protected int id;
    protected int x;
    protected int y;
    @XmlElement(required = true)
    protected TipoCoef coef;

    /**
     * Obtiene el valor de la propiedad id.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Define el valor de la propiedad id.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }

    /**
     * Obtiene el valor de la propiedad x.
     * 
     */
    public int getX() {
        return x;
    }

    /**
     * Define el valor de la propiedad x.
     * 
     */
    public void setX(int value) {
        this.x = value;
    }

    /**
     * Obtiene el valor de la propiedad y.
     * 
     */
    public int getY() {
        return y;
    }

    /**
     * Define el valor de la propiedad y.
     * 
     */
    public void setY(int value) {
        this.y = value;
    }

    /**
     * Obtiene el valor de la propiedad coef.
     * 
     * @return
     *     possible object is
     *     {@link TipoCoef }
     *     
     */
    public TipoCoef getCoef() {
        return coef;
    }

    /**
     * Define el valor de la propiedad coef.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoCoef }
     *     
     */
    public void setCoef(TipoCoef value) {
        this.coef = value;
    }

}
