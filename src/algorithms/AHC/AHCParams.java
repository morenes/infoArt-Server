/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithms.AHC;

import java.io.File;

/**
 * Clase con los principales parámetros de configuración del algoritmo AHC
 *
 * @author Fernando Terroso Saenz
 */
public class AHCParams {

    String XMLPath;
    String executionName;
    String tempFileName = "FCS_AHC_";

    public String getXMLPath() {
        return XMLPath;
    }

    public void setXMLPath(String pXMLPath) {
        this.XMLPath = pXMLPath;
        if(!XMLPath.endsWith(File.separator)){
            XMLPath.concat(File.separator);
        }
    }

    public String getExecutionName() {
        return executionName;
    }

    public void setExecutionName(String executionName) {
        this.executionName = executionName;
    }

    public String getTempFileName() {
        return tempFileName;
    }


    @Override
    public String toString() {
        return "AHCParams{" + "XMLPath=" + XMLPath + "executionName=" + executionName + '}';
    }

    


}
