package models.tskModel;

import algorithms.anfis.*;
import org.w3c.dom.*;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import models.*;
import org.apache.xerces.parsers.DOMParser;
/**
 * Clase del modelo de un Sistema de inferencia difuso de tipo TSK 
 * @author David Gil Galvan
 */
public class TSKModel implements Serializable, FuzzyModel {
         
    protected String MODEL_HEAD = "TSK_";
    protected String modelIdentifier;
        
    /** Atributos de entrada de una instancia o ejemplo */
    protected int[] attsToInputs;
    /** Atributos de salida de una instancia o ejemplo */
    protected int attsToOutput;
    /** Propiedades del Modelo:  parametros de las funciones de pertenencia. Si son gaussianas la formula es Math.exp (-Math.pow((x-c)/a,2)/2
     * Si son campanas generalizadas 1/(1+Math.pow(Math.pow(tmp,2),b)). Ver clase MF*/
    
    /** Vector de parametros a*/
    protected double[][] param_a=null;
    /** Vector de parametros b*/
    protected double[][] param_b=null;
    /** Vector de parametros c*/
    protected double[][] param_c=null;
    /** Vector de parametros d*/
    protected double[][] param_d=null;
    /** Vector de parametros p*/
    protected double[][] param_p=null;
    
    /**Vector de complementos: la posici�n i,j indica si hay alg�n operador difuso acompa�ando al j-�simo  conjunto difuso
     * de la regla i-�sima. Los posibles complementos son: 
     * "univ" (universal): que si acompa�a a un conjunto difuso �ste se anula y su grado de pertenencia  no se tiene en 
     * cuenta para el c�lculo del grado de disparo de la regla correspondiente.
     * "neg" (negaci�n): el resultado de aplicarlo a un conjunto difuso es 1-gradod de disparo del conjunto original */
    protected String complements[][]=null;
    
    /** Propiedades del Modelo: tipo de funcion de pertenencia para los conjuntos difusos de los antecedentes 0 GAUSSIAN, 1 BELL, 2 TRIANGULAR, 3 TRAPEZOIDAL*/
    MembershipFunctionType fsetShape;
    /** Propiedad del Modelo: tipo de consecuente: 0 constante, 1 funci�n lineal de las entradas*/
    ConsequentType consequentType;
    /** Propiedad del Modelo: tipo de inferencia: */
    /* 'FIRING_EACH_RULE_ITS_FIRE' cada regla aporta su grado de disparo, */
    /* 'FIRING_STRONGEST_RULE_GETS_ALL' la regla con mayor grado es la que aporta la salida */
    FiringType firingType;
    
    /** Numero de elementos por los que se representa un conjunto difuso */
    protected int numElementsForMF=-1;
        
    public static String dtdPath = "."+File.separator;
        
    public static String xmlPath = "."+File.separator;
    
    public static String plotPath = "." + File.separator;
        
    /** Nombre del dtd del tskModel */
    protected static String dtdTSKModel="tskmodel";
     
    /**
     * Nombre de la clase
     */
    protected String className=this.getClass().getName();
    
    
    /**
     * Devuelve los parametros a de las funciones de pertenencia
     * @return Devuelve los parametros a de las funciones de pertenencia
     */
    public double[][] getParam_a() {
        return param_a;
    }
    
    /**
     * Establece los parametros a de las funciones de pertenencia
     * @param a parametros a de las funciones de pertenencia
     */
    public void setParam_a(double[][] a) {
        this.param_a = a;
    }
    
    /**
     * Metodo que devuelve los parametros b de las funciones de pertenencia
     * @return Devuelve los parametros b de las funciones de pertenencia
     */
    public double[][] getParam_b() {
        return param_b;
    }
    
    /**
     * Establece los parametros b de las funciones de pertenencia
     * @param b parametros b de las funciones de pertenencia
     */
    public void setParam_b(double[][] b) {
        this.param_b = b;
    }
    
    /**
     * Metodo que devuelve los parametros c de las funciones de pertenencia
     * @return Devuelve los parametros c de las funciones de pertenencia
     */
    public double[][] getParam_c() {
        return param_c;
    }
    
    /**
     * Establece los parametros c de las funciones de pertenencia
     * @param c parametros c de las funciones de pertenencia
     */
    public void setParam_c(double[][] c) {
        this.param_c = c;
    }
    
    /**
     * Metodo que devuelve los parametros d de las funciones de pertenencia
     * @return Devuelve los parametros d de las funciones de pertenencia
     */
    public double[][] getParam_d() {
        return param_d;
    }
    
    /**
     * Establece los parametros d de las funciones de pertenencia
     * @param d parametros d de las funciones de pertenencia
     */
    public void setParam_d(double[][] d) {
        this.param_d = d;
    }
    
    /**
     * Metodo que devuelve los parametros p de las funciones de pertenencia
     * @return Devuelve los parametros p de las funciones de pertenencia
     */
    public double[][] getParam_p() {
        return param_p;
    }
    
    /**
     * Establece los parametros p de las funciones de pertenencia
     * @param p parametros p de las funciones de pertenencia
     */
    public void setParam_p(double[][] p) {
        this.param_p = p;
    }
    
    /**
     * Metodo que devuelve el numero de reglas que tiene codificadas este tskModel
     * @return Numero de reglas del tskModel
     */
    @Override
    public int getNumRules(){
        return param_a.length;
    }
    
    /**
     * Metodo que devuelve el numero de consecuentes que conforman la funcion lineal de la salida
     * @return Devuelve el numero de consecuentes que conforman la funcion lineal de la salida. Si el tskModel
     * es de grado cero entonces devuelve 1 indicando que el consecuente solo esta formado por el termino independiente, en
     * caso contrario devuelve el numero de elementos que compoenen la funcion lineal
     */
    public int getNumConsecuents() {
//        if (consequentType==Constants.SIMPLIFIED_CONSEQUENT) return 1;
        if(consequentType.equals(ConsequentType.SINGLETON)){
            return 1;
        }
        else {
            return (param_p[0].length);
        }
    }
    
    /**
     * Metodo que devuelve el tipo de TSKModel
     * @return Devuelve el tipo de TSKModel, ya sea gaussiano, trapezoidal, triangular o bell
     */
    public MembershipFunctionType getFsetShape(){
        return fsetShape;
    }
    
    /**
     * Metodo que devuelve el tipo de consecuente, es decir TSKModel de primero grado o de grado cero
     * @return Devuelve el tipo de consecuente, es decir TSKModel de primero grado o de grado cero
     */
    public ConsequentType getConsequentType() {
        return consequentType;
    }
    
    /**
     * Metodo que devuelve el numero de atributos que contienen los datos de entrada
     * @return Devuelve el número de atributos que contienen los datos de entrada
     */
    public int getNumAttributes() {
        return attsToInputs.length;
    }
    
    /**
     * Metodo que devuelve el tipo de inferencia
     * @return Devuelve el tipo de inferencia
     */
    public FiringType getFiringType() {
        return firingType;
    }
    
    /**
     * Establece el tipo de inferencia
     * @param firingType Tipo de inferencia
     */
    public void setFiringType(FiringType firingType) {
        this.firingType=firingType;
    }
    
    /**
     * Metodo que devuelve el numero de elementos que definen el conjunto difuso
     * @return Devuelve el numero de elemntos que definen el conjunto difuso
     */
    public int getNumElementsForMF(){
        // Si ya se ha establecido el numero de puntos que definenen el conjunto difuso
        // se devuelve este numero
        if (numElementsForMF!=-1) {
            return numElementsForMF;
        // Si no se establece
        }else {
            switch(fsetShape){
                case GAUSSIAN:
                    numElementsForMF=2;
                    break;
                case BELL:
                case TRIANGULAR:
                    numElementsForMF=3;
                    break;
                case TRAPEZOIDAL:
                    numElementsForMF=4;
                    break;
            }
            return numElementsForMF;
        }
    }
    public int[] getAttsToInputs(){return attsToInputs;};
    /** Atributos de salida de una instancia o ejemplo */
    public int getAttsToOutput(){return attsToOutput;};
    
    /**
     * Metodo que determina la inferencia dado un ejemplo de entrada x
     * @param x Ejemplo
     * @return Devuelve la inferencia producida por el modelo a la entrada del ejemplo x
     */
    private double calculate(double[] x) {
        double[][] mf = new double[param_a.length][param_a[0].length];
        double[] tau = new double[param_a.length];
        double[] normTau = new double[tau.length];
        double[] fi = new double[param_a.length];
        double f;
        int i,j;
        for (j=0; j<param_a[0].length; j++) {//itera sobre las variables 
            for (i=0; i<param_a.length; i++) {//itera sobre las reglas

                switch(fsetShape){
                    case GAUSSIAN:
                        mf[i][j]=MF.gauss_MF(param_a[i][j], param_c[i][j],x[j]);
                        break;
                    case BELL:
                        mf[i][j]=MF.bell_MF(param_a[i][j], param_b[i][j], param_c[i][j], x[j]);
                        break;
                    case TRIANGULAR:
                        mf[i][j]=MF.triangular_MF(param_a[i][j], param_b[i][j], param_c[i][j], x[j]);
                        break;
                    case TRAPEZOIDAL:
                        mf[i][j]=MF.trapezoidal_MF(param_a[i][j], param_b[i][j], param_c[i][j], param_d[i][j], x[j]);
                        break;
                }

                if(complements != null){
                    if (complements[i][j].equalsIgnoreCase("univ")) //operador universal, ignorar el conjunto difuso
                    {
                        mf[i][j]=1;

                    }
                    else if (complements[i][j].equalsIgnoreCase("neg"))//negarlo
                    {
                        mf[i][j]=1-mf[i][j];

                    }
                }
            }
        }


        for (i=0; i<param_a.length; i++) {
            tau[i]=1;
            for (j=0; j<param_a[0].length; j++) {
                tau[i]*= mf[i][j];                
            }
        }


       double den=0;
        
        for (i=0; i<param_a.length; i++) {
            den += tau[i];
        }

        if (den==0) {
            System.out.println("TSKModel.calcula:Den es 0. Division por cero");                      
            return 0;
        }
        for (i=0; i<param_a.length; i++){
            normTau[i]=tau[i]/den;
        }

        if(firingType.equals(FiringType.STRONGEST_RULE_GETS_ALL)){

            int max_index = 0;
            for (i=0; i<param_a.length; i++)
                if (normTau[i] > normTau[max_index]) max_index=i;
            for (i=0; i < param_a.length; i++)
                normTau[i]=0;
            normTau[max_index]=1;
        }
        
        for (i=0; i < param_a.length; i++) {
            fi[i] = param_p[i][param_a[0].length];
            
            for (j=0; j < param_a[0].length; j++) {
                fi[i] += x[j] * param_p[i][j];
            }
            
            fi[i]*= normTau[i];
        }

        f=0;
        for (double aux : fi) {
            f += aux;
        }

        return f;
    }
             
    /**
     * Devuelve la inferencia realizada con el modelo y la instancia pasada como argumento
     * @param example Ejemplo del que se quiere inferir la salida que produce el modelo TSK
     * @return La inferencia realizada con el modelo y la instancia pasada como argumento (RegressionInference)
     */
    @Override
    public double makeInference(double[] example) {
        return calculate(example);
    }
    
    /**
     * Para comprobar si la inferencia es correcta necesitamos saber que valor de la instancia
     * es el que representa la respuesta correcta. Este metodo proporciona dicho valor
     * @param instance La instancia en la que buscamos el valor
     * @return La inferencia realizada (RegressionInference)
     */    
    public double target(double[] instance) {
        double[] target = new double[1];
        for (int i=0; i < target.length; i++)
            target[i] = new Double(instance[attsToOutput]).doubleValue();
        return target[0];
    }
              
    /**
     * Devuelve en una cadena el documento XML correspondiente a este modelo
     * @return Una cadena con el documento XML correspondiente a este modelo
     * @see mtl.xml.XMLItemImpl
     */
    public String generateXML() {
        
        int numReglas=param_p.length;
        String xml = getXMLHead();
        xml += "\n<TSKMODEL PARTIALID=\""+getPartialModelIdentifier()+"\" NUMBEROFRULES= \""+param_a.length +"\" NUMBEROFINPUTS= \""+
                attsToInputs.length+"\" NUMBEROFOUTPUTS= \"1\""+  " CONSEQUENTTYPE="+ ((consequentType.equals(ConsequentType.LINEAR))?"\"TSK\"":"\"SIMPLIFIED\"") +
                " FIRINGTYPE=" +((firingType.equals(FiringType.STRONGEST_RULE_GETS_ALL))?"\" STRONGEST_RULE_GETS_ALL\"":"\"EACH_RULE_ITS_FIRE\"")+
                " MFTYPE=";

        switch(fsetShape){
            case GAUSSIAN:
                xml+="\"GAUSSIAN\"";
                break;
            case BELL:
                xml+="\"BELL\"";
                break;
            case TRIANGULAR:
                xml+="\"TRIANGULAR\"";
                break;
            case TRAPEZOIDAL:
                xml+="\"TRAPEZOIDAL\"";
                break;
        }
        
        xml +=">\n" +
                "<JAVACLASS>" + getClass().getName() + "</JAVACLASS>\n" +
                "<CONTENTS>" ;
        
        for (int i=0;i<attsToInputs.length;i++) {
            xml+="\n<INPUTVARIABLE INDEX= \""+i+"\" NAME= \""+ attsToInputs[i]+"\"> </INPUTVARIABLE>";
        }
        
        xml+="\n<OUTPUTVARIABLE NAME= \""+ attsToOutput+"\"> </OUTPUTVARIABLE>";
        for (int i=0; i<numReglas; i++) {
            xml += "\n<FUZZYRULE INDEX= \""+ i +"\">\n\t<ANTECEDENT>\n";
            for (int j=0;j<attsToInputs.length;j++) {
                xml+= "\t\t<FUZZYPROPOSITION RULE= \""+ i +"\" INPUTVARIABLEINDEX= \""+ j +
                        "\">\n \t\t\t<FUZZYSET>\n";
                if(this.fsetShape.equals(MembershipFunctionType.GAUSSIAN)){
                    xml+= "\t\t\t\t <PARAMETERMF NAME= \"a\">" + param_a[i][j]+"</PARAMETERMF>\n";
                    xml+= "\t\t\t\t <PARAMETERMF NAME= \"c\">" + param_c[i][j]+"</PARAMETERMF>\n";
                } else {
                    if((this.fsetShape.equals(MembershipFunctionType.BELL)) ||(this.fsetShape.equals(MembershipFunctionType.TRIANGULAR))){
                        xml+= " \t\t\t\t<PARAMETERMF NAME= \"a\">" + param_a[i][j]+"</PARAMETERMF>\n";
                        xml+= " \t\t\t\t<PARAMETERMF NAME= \"b\">" + param_b[i][j]+"</PARAMETERMF>\n";
                        xml+= " \t\t\t\t<PARAMETERMF NAME= \"c\">" + param_c[i][j]+"</PARAMETERMF>\n";
                    } else {
                        if(this.fsetShape.equals(MembershipFunctionType.TRAPEZOIDAL)){
                            xml+= " \t\t\t\t<PARAMETERMF NAME= \"a\">" + param_a[i][j]+"</PARAMETERMF>\n";
                            xml+= " \t\t\t\t<PARAMETERMF NAME= \"b\">" + param_b[i][j]+"</PARAMETERMF>\n";
                            xml+= " \t\t\t\t<PARAMETERMF NAME= \"c\">" + param_c[i][j]+"</PARAMETERMF>\n";
                            xml+= " \t\t\t\t<PARAMETERMF NAME= \"d\">" + param_d[i][j]+"</PARAMETERMF>\n";
                        }
                    }
                }
                xml += "\t\t\t</FUZZYSET>\n\t\t</FUZZYPROPOSITION>\n";
                
            }
            
            xml += "\t</ANTECEDENT>\n\t<CONSEQUENT>\n";
            if(consequentType.equals(ConsequentType.LINEAR)){
                for (int j=0;j<attsToInputs.length;j++) {
                    xml += "\t\t<P_VALUE RULE= \""+ i +"\" INPUTVARIABLEINDEX=\""+ j +"\">" ;
                    xml += param_p[i][j]+"</P_VALUE>\n";
                }
                xml+="\t\t<P_VALUE RULE= \""+ i +"\" INPUTVARIABLEINDEX= \"NULL\">" +param_p[i][attsToInputs.length]+"</P_VALUE>\n";
            } else
                xml+="\t\t<P_VALUE RULE= \""+ i +"\" INPUTVARIABLEINDEX= \"NULL\">" +param_p[i][attsToInputs.length]+"</P_VALUE>\n";
            
            xml += "\t</CONSEQUENT>\n</FUZZYRULE>";
        }
        
        xml += "\n</CONTENTS>\n</TSKMODEL>\n";
        
        
        //System.out.println(xml);
        return xml;
    }
      
    // VALORES ESTATICOS NECESARIOS PARA RECUPERAR UN TSK DESDE UN FICHERO
    static String modelIdentifierS = "";
    static String userS;
    static long keyS;
    static int numVarS;
    static ConsequentType consequentTypeS;
    static FiringType firingTypeS;
    static MembershipFunctionType fsetShapeS;
    static double[][] param_aS;
    static double[][] param_bS;
    static double[][] param_cS;
    static double[][] param_dS;
    static double[][] param_pS;
    static int[] attsToInputsS;
    static int attsToOutputS;
    
    static String complementsS[][];
    /**
     * Parsea Documento XML
     * @param doc Documento XML
     */
    protected static void parse(Node doc) {
        try {
            switch (doc.getNodeType()) {
                case Node.ELEMENT_NODE:
                    if (doc.getNodeName().equals("TSKMODEL")) {
                        
                        NamedNodeMap attributes = doc.getAttributes();
                        Node node = attributes.getNamedItem("NUMBEROFRULES");
                        int nRules = Integer.valueOf(node.getNodeValue()).intValue();
                        
                        node = attributes.getNamedItem("PARTIALID");
                        modelIdentifierS = node.getNodeValue();
                                                
                        node = attributes.getNamedItem("NUMBEROFINPUTS");
                        int nInputs = Integer.valueOf(node.getNodeValue()).intValue();
                        attsToInputsS=new int[nInputs];
                        
                        node = attributes.getNamedItem("CONSEQUENTTYPE");
                        param_pS=new double[nRules][nInputs + 1];
                        complementsS = new String[nRules][nInputs];
                        if (node.getNodeValue().equals("SIMPLIFIED"))
                            consequentTypeS = ConsequentType.SINGLETON;
                        else
                            if (node.getNodeValue().equals("TSK"))
                                consequentTypeS = ConsequentType.LINEAR;
                        node = attributes.getNamedItem("FIRINGTYPE");
                        if (node.getNodeValue().equals("STRONGEST_RULE_GETS_ALL"))
                                firingTypeS = FiringType.STRONGEST_RULE_GETS_ALL;
                        else
                            if (node.getNodeValue().equals("EACH_RULE_ITS_FIRE"))
                                firingTypeS = FiringType.EACH_RULE_ITS_FIRE;

                        
                        node = attributes.getNamedItem("MFTYPE");
                        
                        if (node.getNodeValue().equals("GAUSSIAN")) {
                            fsetShapeS = MembershipFunctionType.GAUSSIAN;
                            param_aS = new double[nRules][nInputs];
                            param_cS = new double[nRules][nInputs];
                        } else {
                            if ((node.getNodeValue().equals("BELL"))||(node.getNodeValue().equals("TRIANGULAR"))) {
                                fsetShapeS = MembershipFunctionType.TRIANGULAR;
                                param_aS = new double[nRules][nInputs];
                                param_cS = new double[nRules][nInputs];
                                param_bS = new double[nRules][nInputs];
                            } else {
                                if (node.getNodeValue().equals("TRAPEZOIDAL")) {
                                    fsetShapeS = MembershipFunctionType.TRAPEZOIDAL;
                                    param_aS = new double[nRules][nInputs];
                                    param_cS = new double[nRules][nInputs];
                                    param_bS = new double[nRules][nInputs];
                                    param_dS = new double[nRules][nInputs];
                                }
                            }
                        }
                        
                    } else
                        if (doc.getNodeName().equals("USER")) {
                        userS=doc.getFirstChild().getNodeValue();
                        } else
                            if (doc.getNodeName().equals("KEY")) {
                        keyS=Long.valueOf(doc.getFirstChild().getNodeValue()).longValue();
                            }else
                                if (doc.getNodeName().equals("CONTENTS")) {
                                } else
                                    if (doc.getNodeName().equals("INPUTVARIABLE")) {
                                        NamedNodeMap attributes = doc.getAttributes();
                                        Node name = attributes.getNamedItem("NAME");
                                        Node index = attributes.getNamedItem("INDEX");
                                        attsToInputsS[Integer.valueOf(index.getFirstChild().getNodeValue()).intValue()]=Integer.valueOf(name.getFirstChild().getNodeValue()).intValue();
                                    } else
                                        if (doc.getNodeName().equals("OUTPUTVARIABLE")) {
                                            NamedNodeMap attributes = doc.getAttributes();
                                            Node name = attributes.getNamedItem("NAME");
                                            attsToOutputS=Integer.valueOf(name.getFirstChild().getNodeValue()).intValue();
                                        }
                    
                    if (doc.getNodeName().equals("FUZZYRULE")) {
                    } else
                        if (doc.getNodeName().equals("ANTECEDENT")){
                        } else
                            if (doc.getNodeName().equals("CONSEQUENT")) {
                            } else
                                if (doc.getNodeName().equals("FUZZYPROPOSITION")) {
                        NamedNodeMap attributes = doc.getAttributes();
                        Node node = attributes.getNamedItem("INPUTVARIABLEINDEX");
                                } else
                                    if (doc.getNodeName().equals("FUZZYSET")) {
                                    } else if ( doc.getNodeName().equals("PARAMETERMF")) {
                                        Node fuzzyProp= doc.getParentNode().getParentNode();
                                        int rule= Integer.valueOf(fuzzyProp.getAttributes().getNamedItem("RULE").getNodeValue()).intValue();
                                        int variable= Integer.valueOf(fuzzyProp.getAttributes().getNamedItem("INPUTVARIABLEINDEX").getNodeValue()).intValue();
                                        Node compNode = fuzzyProp.getAttributes().getNamedItem("COMPLEMENT");
//                                        if(compNode != null){
                                            String complement= compNode.getNodeValue();
                                            complementsS[rule][variable]=complement;
//                                        }else{
//                                            complementsS[rule][variable]="no_comp";
//                                        }

                                        if (doc.getAttributes().getNamedItem("NAME").getNodeValue().equals("a")) {
                                            double value= Double.valueOf(doc.getFirstChild().getNodeValue()).doubleValue();
                                            param_aS[rule][variable]= value;
                                        } else
                                            if (doc.getAttributes().getNamedItem("NAME").getNodeValue().equals("b")) {
                                            param_bS[rule][variable]=Double.valueOf(doc.getFirstChild().getNodeValue()).doubleValue();
                                            } else
                                                if (doc.getAttributes().getNamedItem("NAME").getNodeValue().equals("c")) {
                                            param_cS[rule][variable]=Double.valueOf(doc.getFirstChild().getNodeValue()).doubleValue();
                                                }
                                        } else
                                            if (doc.getNodeName().equals("P_VALUE")){
                        NamedNodeMap attributes = doc.getAttributes();
                        String rule =  attributes.getNamedItem("RULE").getNodeValue();
                        String variable = attributes.getNamedItem("INPUTVARIABLEINDEX").getNodeValue();
                        if (variable.equals("NULL")) {
                            double value= Double.valueOf(doc.getFirstChild().getNodeValue()).doubleValue();
                            param_pS[Integer.valueOf(rule).intValue()][param_pS[0].length-1]= value;
                        } else
                            param_pS[Integer.valueOf(rule).intValue()][Integer.valueOf(variable).intValue()]=Double.valueOf(doc.getFirstChild().getNodeValue()).doubleValue();
                                            }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        NodeList children = doc.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            parse(children.item(i));
        }
        
    }
    
    /**
     * Recrea a partir de un documento XML la clase que lo genera
     * @return Devuelve el TSKModel correspondiente al fichero xml
     * @param uri URI del documento a parsear
     * @throws Exception Si no se puede recrear la clase
     */
    public static TSKModel createTSKModelFromXML(String uri) throws Exception {
        String URI = uri + ".xml";
        DOMParser parser = new DOMParser();
        try {
            parser.parse(URI);
            
        } catch (Exception e) {
            System.out.println("No se ha podido parsear el documento de la URI " + URI);
            System.out.println(e);
            e.printStackTrace();
        }
        Document doc = parser.getDocument();
        
        parse(doc);
        
        switch(fsetShapeS){
            case GAUSSIAN:
                return new GaussianTSKModel(modelIdentifierS,attsToInputsS.length, consequentTypeS, firingTypeS, param_aS, param_cS, param_pS, complementsS);
            case BELL:
                return new BellTSKModel(modelIdentifierS,attsToInputsS.length, consequentTypeS, firingTypeS, param_aS, param_bS,param_cS, param_pS, complementsS);
            case TRIANGULAR:
                return new TriangularTSKModel(attsToInputsS.length, consequentTypeS, firingTypeS, param_aS, param_bS, param_cS, param_pS, complementsS);
            case TRAPEZOIDAL:
                return new TrapezoidalTSKModel(attsToInputsS.length, consequentTypeS, firingTypeS, param_aS, param_bS,param_cS, param_dS, param_pS, complementsS);

        }
        
        return null;
    }
    
    /**
     * Devuelve la cabecera XML comun a todos los documentos de este tipo; en la misma incluimos el
     * nombre y localizacion del fichero DTD de definicion de datos
     * @return La cabecera del documento XML
     */
    protected String getXMLHead() {
                
        String head = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        head += "<!DOCTYPE " + getTotalModelIdentifier() + " SYSTEM \"" + "."+dtdPath + dtdTSKModel + ".dtd\">";
        
        return head;
    }
    
    public void setSerializationPath(String pXmlPath){
        xmlPath = pXmlPath;
        
        if(!xmlPath.endsWith(File.separator)){
            xmlPath = xmlPath + File.separator;
        }
    }

    public void setPlotPath(String pPlotPath){
        plotPath = pPlotPath;
        
        if(!plotPath.endsWith(File.separator)){
            plotPath = plotPath + File.separator;
        }
    }
    
    /**
     * Vuelca a disco en un fichero XML el contenido generado por generateXML
     * @param uri URI del documento donde escribir el tskModel
     * @throws java.io.IOException Si se produce algun error al escribir en disco
     */
    @Override
    public void writeDown() throws java.io.IOException {
        PrintWriter writer=null;
        try {
            String uri= xmlPath + getTotalModelIdentifier()+".xml";
            String xml = generateXML();
            
            writer = new PrintWriter(new FileOutputStream(uri));
            writer.print(xml);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer!=null)
                writer.close();
        }
        
    }
    
    /**
     * Vuelca a disco informacion en el formato adecuado para ayudarme 
     * a imprimir graficas en gnuplot seg�n conjuntos difusos correspondientes
     * @param uri Nombre del fichero donde escribir
     * @throws Exception Si se produce alguna excepcion
     */
    public void writeDownGNUPlot() throws Exception {
        
        File file=new File(plotPath);
        if (!file.exists()) {
            boolean created=file.mkdirs();
            if (created==false)
                throw new Exception (className+".writeDownGNUPlot: No se ha podido crear el directorio seleccionado");
        }
        if (this instanceof TrapezoidalTSKModel) {
            throw new Exception (className+".writeDownGNUPlot: No existe metodo para gnuplot para el tskModel Trapezoida");
        } 
        if (this instanceof GaussianTSKModel) {
            writeDownGaussianGNUPLOT();
        }
        if (this instanceof BellTSKModel) {
            writeDownBellGNUPLOT();
        }
        
        if (this instanceof TriangularTSKModel) {
            throw new Exception (className+".writeDownGNUPlot: No existe metodo para gnuplot para el tskModel Triangular");
        }
        
    }        
    
    /**
     * Vuelca a disco informacion en el formato que es adecuado para mi para ayudarme 
     * a imprimir graficas en gnuplot seg�n conjuntos difusos gausianos     
     * @param uri Nombre del fichero donde escribir
     * @throws Exception Si se produce algun error al escribir en disco
     */
    private void writeDownGaussianGNUPLOT() throws Exception {
        PrintWriter writer=null;
        try {

            for (int i=0; i<param_a[0].length; i++){

                String fileName = plotPath + getTotalModelIdentifier()+"_at" + i + ".gp";
                String fileOutputName = getTotalModelIdentifier() + "_at" + i + ".eps";
                
                writer = new PrintWriter(new FileOutputStream(fileName));

                writer.print(getGNUPlotHeader(fileOutputName, i));                                
                writer.println("gauss(x,sigma,mu)= exp(-0.5*((x-mu)/sigma)**2)");

                double max = Double.NEGATIVE_INFINITY;
                for(int j = 0; j< param_c.length; j++){
                    double aux = param_c[j][i] + Math.abs(3 * param_a[j][i]);
                    if(aux > max){
                        max = aux;
                    }
                }

                double min = Double.POSITIVE_INFINITY;
                for(int j = 0; j< param_c.length; j++){
                    double aux = param_c[j][i] - Math.abs(3 * param_a[j][i]);
                    if(aux < min){
                        min = aux;
                    }
                }
                StringBuilder plotPart = new StringBuilder("plot  [");

                plotPart.append(min);
                plotPart.append(":");
                plotPart.append(max);
                plotPart.append("] ");

                writer.print(plotPart.toString());

                //We merge rules with the same gaussian
                Set<Integer> chosen = new HashSet<Integer>();
                Map<String, Double[]> rules = new HashMap<String, Double[]>();
                for(int j = 0; j < param_c.length; j++){
                    if(!chosen.contains(j)){
                        String key = String.valueOf(j+1);
                        for(int z = j+1; z<= param_c.length-1; z++){
                            if((param_c[j][i] == param_c[z][i]) && (param_a[j][i] == param_a[z][i])){
                                key = key + "," + String.valueOf(z+1);
                                chosen.add(z);
                            }
                        }
                        Double values[] = {param_c[j][i], param_a[j][i]};
                        rules.put(key, values);
                        chosen.add(j);
                    }
                }

                Set<String> keys = rules.keySet();
                LinkedList<String> keysList = new LinkedList<String>(keys);
                Collections.sort(keysList);
                
                plotPart = new StringBuilder();
                int addition = 1;
                for(String key : keysList){
                    if((key.contains(","))){
                        plotPart.append("(gauss(x,");
                        plotPart.append(rules.get(key)[1]);
                        plotPart.append(", ");
                        plotPart.append(rules.get(key)[0]);
                        plotPart.append(")) ls ");
                        plotPart.append(param_c.length+addition);
                        plotPart.append(" title \"Rs ");
                        plotPart.append(key);
                        plotPart.append("\", ");
                        addition++;
                    }else{
                        int j = Integer.valueOf(key)-1;
                        if((complements == null) || (!complements[j][i].equals("univ"))){
                            plotPart.append("(gauss(x,");
                            plotPart.append(param_a[j][i]);
                            plotPart.append(", ");
                            plotPart.append(param_c[j][i]);
                            plotPart.append(")) ls ");
                            plotPart.append(key);
                            plotPart.append(" title \"R ");
                            plotPart.append(key);
                            plotPart.append("\", ");
                        }
                    }
                }

                plotPart.replace(plotPart.length()-2, plotPart.length()-1, "");
                writer.println(plotPart.toString());
                
                writer.flush();
                writer.close();
                
                String command1= "gnuplot "+ fileName;
                String command2= "mv "+fileOutputName+" "+plotPath;
                
                Process p = Runtime.getRuntime().exec(command1);
                p.waitFor();                

                p = Runtime.getRuntime().exec(command2);
                p.waitFor();
            }
                      
        } catch (Exception e) {                                ;
            if (writer!=null) {
                writer.close();            
            }
            throw e;
        }
        
    }

   private void writeDownBellGNUPLOT() throws Exception {
        PrintWriter writer=null;
        try {

            for (int i=0; i<param_a[0].length; i++){

                String fileName = plotPath + getTotalModelIdentifier()+"_at" + i + ".gp";
                String fileOutputName = getTotalModelIdentifier() + "_at" + i + ".eps";
                
                writer = new PrintWriter(new FileOutputStream(fileName));

                writer.print(getGNUPlotHeader(fileOutputName, i));
                writer.println("bell(x,a,b,c)= 1/(1+(abs((x-c)/a))**(2*b))");

                double max = Double.NEGATIVE_INFINITY;
                for(int j = 0; j< param_c.length; j++){
                    double aux = param_c[j][i] + Math.abs(3 * param_a[j][i]);

                    if(aux > max){
                        max = aux;
                    }
                }

                double min = Double.POSITIVE_INFINITY;
                for(int j = 0; j< param_c.length; j++){
                    double aux = param_c[j][i] - Math.abs(3 * param_a[j][i]);
                    if(aux < min){
                        min = aux;
                    }
                }
                StringBuilder sb = new StringBuilder("plot  [");

                sb.append(min);
                sb.append(":");
                sb.append(max);
                sb.append("] ");

                writer.print(sb.toString());

                sb = new StringBuilder();
                for(int j = 0; j < param_c.length; j++){
                    sb.append("(bell(x,");
                    sb.append(param_a[j][i]);
                    sb.append(", ");
                    sb.append(param_b[j][i]);
                    sb.append(", ");
                    sb.append(param_c[j][i]);
                    sb.append(")) title \"R ");
                    sb.append(j);
                    sb.append("\", ");
                }
                sb.replace(sb.length()-2, sb.length()-1, "");
                writer.println(sb.toString());

                writer.flush();
                writer.close();
                
                String command1= "gnuplot "+ fileName;
                String command2= "mv "+fileOutputName+" "+plotPath;
                
                Process p = Runtime.getRuntime().exec(command1);
                p.waitFor();                

                p = Runtime.getRuntime().exec(command2);
                p.waitFor();
            }

        } catch (Exception e) {
            if (writer!=null) {
                writer.close();
            }
            throw e;
        }

    }

   private String getGNUPlotHeader(String outputFileName, int attributeNum){

       StringBuilder GNUPlotHeader = new StringBuilder();

       GNUPlotHeader.append("set term postscript eps color blacktext \"Helvetica\" 24\n");
       GNUPlotHeader.append("set terminal postscript enhanced \n");
       GNUPlotHeader.append("set output \"");
       GNUPlotHeader.append(outputFileName);
       GNUPlotHeader.append("\"\n");
       GNUPlotHeader.append("set style fill solid\n");
       GNUPlotHeader.append("set style line 1 lt rgb \"red\" lw 3\n");
       GNUPlotHeader.append("set style line 2 lt rgb \"green\" lw 3\n");
       GNUPlotHeader.append("set style line 3 lt rgb \"cyan\" lw 3\n");
       GNUPlotHeader.append("set style line 4 lt rgb \"blue\" lw 3\n");
       GNUPlotHeader.append("set style line 5 lt rgb \"violet\" lw 3\n");
       GNUPlotHeader.append("set style line 6 lt rgb \"orange\" lw 3\n");
       GNUPlotHeader.append("set style line 7 lt rgb \"#20B2AA\" lw 3\n");
       GNUPlotHeader.append("set style line 8 lt rgb \"#8FBC8F\" lw 3\n");
       GNUPlotHeader.append("set style line 9 lt rgb \"#FF1493\" lw 3\n");
       GNUPlotHeader.append("set style line 10 lt rgb \"#ADFF2F\" lw 3\n");
       GNUPlotHeader.append("set style line 11 lt rgb \"#800000\" lw 3\n");
       GNUPlotHeader.append("set style line 12 lt rgb \"#191970\" lw 3\n");
       GNUPlotHeader.append("set style line 13 lt rgb \"#9ACD32\" lw 3\n");
       GNUPlotHeader.append("set style line 14 lt rgb \"#2F4F4F\" lw 3\n");
       GNUPlotHeader.append("set style line 15 lt rgb \"#DCDCDC\" lw 3\n");

       GNUPlotHeader.append("set yrange [0 : 1.1 ] noreverse nowriteback\n");
       GNUPlotHeader.append("set grid\n");
       GNUPlotHeader.append("set key out top center\n");
       GNUPlotHeader.append("set key box\n");
       GNUPlotHeader.append("set key horiz samplen 1\n");
       GNUPlotHeader.append("set xlabel 'At. ");
       GNUPlotHeader.append(attributeNum);
       GNUPlotHeader.append("'\n");
       GNUPlotHeader.append("set ylabel 'Membership degree'\n");
       GNUPlotHeader.append("set yrange [0 : 1.1 ] noreverse nowriteback\n");

        if(attributeNum== 0){
            GNUPlotHeader.append("set xrange [-1:1]\n");
            GNUPlotHeader.append("set xtics 0.2\n");
        }

       return GNUPlotHeader.toString();
   }
    
    /**
     * Metodo que determina el grado de disparo de cada regla dado un ejemplo de entrada x
     * @param x Ejemplo
     * @return Devuelve la inferencia producida por el modelo a la entrada del ejemplo x
     */
    @Override
    public double[] getTruth(double[] x) {
        double[][] mf = new double[param_a.length][param_a[0].length];
        double[] tau = new double[param_a.length];                
        
        int i,j;
        for (j=0; j<param_a[0].length; j++) {
            for (i=0; i<param_a.length; i++) {

                switch(fsetShape){
                    case GAUSSIAN:
                        mf[i][j]=MF.gauss_MF(param_a[i][j], param_c[i][j],x[j]);
                        break;
                    case BELL:
                        mf[i][j]=MF.bell_MF(param_a[i][j], param_b[i][j], param_c[i][j], x[j]);
                        break;
                    case TRIANGULAR:
                        mf[i][j]=MF.triangular_MF(param_a[i][j], param_b[i][j], param_c[i][j], x[j]);
                        break;
                    case TRAPEZOIDAL:
                        mf[i][j]=MF.trapezoidal_MF(param_a[i][j], param_b[i][j], param_c[i][j], param_d[i][j], x[j]);
                        break;
                }
            }
        }
        
        for (i=0; i<param_a.length; i++) {
            tau[i]=1;
            for (j=0; j<param_a[0].length; j++) {
                tau[i]*= mf[i][j];
            }
        }
        
        return tau;
    }

    public String getTotalModelIdentifier() {
        
        StringBuilder verboseIdentifier = new StringBuilder();
        verboseIdentifier.append(MODEL_HEAD);
        verboseIdentifier.append(consequentType.getID());
        verboseIdentifier.append("_");
        verboseIdentifier.append(modelIdentifier);
        verboseIdentifier.append("_numrules_");
        verboseIdentifier.append(getNumRules());
                
        return verboseIdentifier.toString();
        
    }
    
    public String getPartialModelIdentifier(){
        return modelIdentifier;
    }

    public void setModelIdentifier(String modelIdentifier) {
        this.modelIdentifier = modelIdentifier;
    }
    
    
    
    /***************************************
     * METODOS DE XMLITEMIMPL - end
     ***************************************/   
}
