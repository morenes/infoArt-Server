package model.mapa;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipoAjuste", propOrder = {
	"id",
    "x",
    "y",
})
public class TipoAjuste {
	protected int id;
    protected int x;
    protected int y;

    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getX() {
        return x;
    }
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


}
