package model.mapa;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "id",
    "place",
    "width",
    "height",
    "NEAR",
    "MOVE",
    "MOVE2",
    "FACTOR",
    "MIN_BEA",
    "CONF_ALG",
    "ajuste",
    "beacon"
})
@XmlRootElement(name = "mapaXML")
public class MapaXML {

    protected long id;
    protected int place;
    protected int width;
    protected int height;
    protected int NEAR;
    protected int MOVE;
    protected int MOVE2;
    protected float FACTOR;
    protected int MIN_BEA;
    protected int CONF_ALG;
    @XmlElement(required = true)
    protected List<TipoAjuste> ajuste;
    @XmlElement(required = true)
    protected List<TipoBeacon> beacon;

    /**
     * Obtiene el valor de la propiedad id.
     * 
     */
    public long getId() {
        return id;
    }

    /**
     * Define el valor de la propiedad id.
     * 
     */
    public void setId(long value) {
        this.id = value;
    }

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
     * Obtiene el valor de la propiedad width.
     * 
     */
    public int getWidth() {
        return width;
    }

    /**
     * Define el valor de la propiedad width.
     * 
     */
    public void setWidth(int value) {
        this.width = value;
    }

    /**
     * Obtiene el valor de la propiedad height.
     * 
     */
    public int getHeight() {
        return height;
    }

    /**
     * Define el valor de la propiedad height.
     * 
     */
    public void setHeight(int value) {
        this.height = value;
    }
    public List<TipoBeacon> getBeacon() {
        if (beacon == null) {
            beacon = new ArrayList<TipoBeacon>();
        }
        return this.beacon;
    }

	public int getNEAR() {
		return NEAR;
	}

	public void setNEAR(int nEAR) {
		NEAR = nEAR;
	}

	public int getMOVE() {
		return MOVE;
	}

	public void setMOVE(int mOVE) {
		MOVE = mOVE;
	}

	public int getMOVE2() {
		return MOVE2;
	}

	public void setMOVE2(int mOVE2) {
		MOVE2 = mOVE2;
	}

	public float getFACTOR() {
		return FACTOR;
	}

	public void setFACTOR(float fACTOR) {
		FACTOR = fACTOR;
	}

	public int getMIN_BEA() {
		return MIN_BEA;
	}

	public void setMIN_BEA(int mIN_BEA) {
		MIN_BEA = mIN_BEA;
	}

	public int getCONF_ALG() {
		return CONF_ALG;
	}

	public void setCONF_ALG(int cONF_ALG) {
		CONF_ALG = cONF_ALG;
	}

	public List<TipoAjuste> getAjuste() {
		return ajuste;
	}

	public void setAjuste(List<TipoAjuste> ajuste) {
		this.ajuste = ajuste;
	}
    
    
}
