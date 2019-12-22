/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package datas;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * Clase encargada de filtrar los nombre de los ficheros de un
 * directorio a traves de un patron dado (Ej: *.txt)
 * @author Fernando Terroso Saenz
 */
public class DirFilter implements FilenameFilter{

    private final Pattern pattern;

    public DirFilter(String regex){
        pattern = Pattern.compile(regex);
    }

    public boolean accept(File dir, String name){
        return pattern.matcher(name).matches();
    }

}
