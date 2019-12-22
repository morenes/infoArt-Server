package datas;

import java.io.*;
import java.util.*;

/**
 * Acceso a los datos a trav�s de un fichero de texto plano del sistema de ficheros.  
 * @author David Gil Galvan
 */
public class FileAccess implements DataAccess {
    
    protected String className=this.getClass().getName();
    private double[][] InputDataLearn;
    private double[] OutputDataLearn;
    private double[][] InputDataEvaluate;
    private double[] OutputDataEvaluate;
    private Vector input;
    private Vector output;
    
    /**     
     * Devuelve el vector con las entradas de los datos de aprendizaje
     * @return Devuelve el vector con las entradas de los datos de aprendizaje
     */  
    public double[][] getInputDataLearn() {
        return InputDataLearn;
    }
    
    /**     
     * Devuelve el vector con las salidas de los datos de aprendizaje
     * @return Devuelve el vector con las salidas de los datos de aprendizaje
     */     
    public double[] getOutputDataLearn() {
        return OutputDataLearn;
    }
    
    /**
     * Devuelve el vector con las entradas de los datos de evaluacion
     * @return Devuelve el vector con las entradas de los datos de evaluacion
     */
    public double[][] getInputDataEvaluate() {
        return InputDataEvaluate;
    }
    
    /**
     * Devuelve el vector con las salidas de los datos de evaluacion
     * @return Devuelve el vector con las salidas de los datos de evaluacion
     */
    public double[] getOutputDataEvaluate() {
        return OutputDataEvaluate;
    }
            
    /** 
     * Metodo que parsea el fichero de texto con los datos y determina que ejemplos son 
     * para entrenamiento y cuales para evaluacion. 
     * @param path Determina la ruta donde se encuentra el fichero con los datos
     * @param learnRatio Parametro que tanto por ciento de los datos son para aprendizaje y que tanto por ciento
     * restante es para evaluacion     
     * @param utilizarEntrada Indica que atributos del fichero se utilizan como entrada
     * @param utilizarSalida Indica que atributo del fichero se utiliza como salida     
     * @param limit Indica el numero de ejemplos a utilizar como conjunto de datos
     * @throws Exception  Excepcion que indica si ha ocurrido algun error en la I/O
     */
    public void parse(
            String path, 
            double learnRatio, 
            int[] utilizarEntrada, 
            int[] utilizarSalida, 
            int limit) throws Exception {
        try {
            
            File file = new File(path);
            Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            StreamTokenizer tokens = new StreamTokenizer(reader);
            tokens.resetSyntax();
            tokens.eolIsSignificant(true);
            tokens.wordChars('!','~');
            tokens.whitespaceChars('\0',' ');
            tokens.whitespaceChars(',',',');
            tokens.whitespaceChars(':',':');
            tokens.whitespaceChars(';',';');
            tokens.commentChar('#');
            tokens.nextToken();
            input=new Vector();
            output=new Vector();
            int numVar=0;
            while ((tokens.ttype != StreamTokenizer.TT_EOF)&&(input.size()<limit)) {
                Vector words = new Vector();
                while (tokens.ttype != StreamTokenizer.TT_EOL) {
                    words.add(tokens.sval);
                    tokens.nextToken();
                }
                if (words.size() > 0) {
                    numVar=utilizarEntrada.length;
                    double[] values = new double[utilizarEntrada.length];
                    int insertar=0;
                    double tag=0;
                    for(int i=0; i<words.size(); i++) {
                        for (int k=0;k<utilizarEntrada.length;k++) {
                            if ((i+1)==utilizarEntrada[k]) {
                                
                                values[insertar] = Double.parseDouble((String)words.get(i));
                                //System.out.println("VAL:"+values[insertar]);
                                insertar++;
                                
                            }
                            
                            if ((i+1)==utilizarSalida[0]) {
                                tag = Double.parseDouble((String)words.get(i));
                                //System.out.println("ETIQUETA:"+tag);
                            }
                        }
                    }
                    input.add(values);
                    output.add(new Double(tag));
                }
                tokens.nextToken();
                
            }            
            //input,output evaluacion
            Vector learnInputS=new Vector();
            Vector learnOutputS=new Vector();
            //chapuza mia
            //randomInstances(learnInputS,learnOutputS,input,output,learnRatio);
           System.out.println("ACCESO SEQUENCIAL");
            sequentialInstances(learnInputS,learnOutputS,input,output,learnRatio);
           //HASTA AQUI
            Enumeration in=learnInputS.elements();
            Enumeration out=learnOutputS.elements();
                        
            InputDataLearn=new double[learnInputS.size()][numVar];
            OutputDataLearn=new double[learnOutputS.size()];
                                    
            int i=0;
            
            while (in.hasMoreElements()) {
                
                double[] values=(double[])in.nextElement();
                
                double tag=((Double)out.nextElement()).doubleValue();
                
                
                for (int j=0;j<values.length;j++) {
                    InputDataLearn[i][j]=values[j];
                }
                
                OutputDataLearn[i]=tag;
                i++;
            }
            out=output.elements();
            in=input.elements();
            
            InputDataEvaluate=new double[input.size()][numVar];
            OutputDataEvaluate=new double[output.size()];
            i=0;
            while (in.hasMoreElements()) {
                double[] values =(double[])in.nextElement();
                double tag=((Double)out.nextElement()).doubleValue();
                
                for (int j=0;j<values.length;j++) {
                    InputDataEvaluate[i][j]=values[j];
                }
                OutputDataEvaluate[i]=tag;
                i++;
            }                                    
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(className+"parse: Error al leer del fichero");
        }
    }

        /**
     * Metodo que parsea un conjunto de ficheros de texto con los datos y los trata como datos de evaluacion
     * o apredizaje en función del parametro @param fileType
     * @param files Los ficheros con los datos
     * @param utilizarEntrada Indica que atributos del fichero se utilizan como entrada
     * @param utilizarSalida Indica que atributo del fichero se utiliza como salida
     * @param fileType Indica si el fichero de entrada es de evaluacion o de aprendizaje
     * @throws Exception  Excepcion que indica si ha ocurrido algun error en la I/O
     */
    public void parse(File[] files, int[] utilizarEntrada, int[] utilizarSalida, FileType fileType) throws Exception {
        try {

            input=new Vector();
            output=new Vector();
            int numVar=0;
            for(File file : files){
                Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                StreamTokenizer tokens = new StreamTokenizer(reader);
                tokens.resetSyntax();
                tokens.eolIsSignificant(true);
                tokens.wordChars('!','~');
                tokens.whitespaceChars('\0',' ');
                tokens.whitespaceChars(',',',');
                tokens.whitespaceChars(':',':');
                tokens.whitespaceChars(';',';');
                tokens.commentChar('#');
                tokens.nextToken();
                while (tokens.ttype != StreamTokenizer.TT_EOF) {
                    Vector words = new Vector();
                    while (tokens.ttype != StreamTokenizer.TT_EOL) {
                        words.add(tokens.sval);
                        tokens.nextToken();
                    }
                    if (words.size() > 0) {
                        numVar=utilizarEntrada.length;
                        double[] values = new double[utilizarEntrada.length];
                        int insertar=0;
                        double tag=0;
                        for(int i=0; i<words.size(); i++) {
                            for (int k=0;k<utilizarEntrada.length;k++) {
                                if ((i+1)==utilizarEntrada[k]) {

                                    values[insertar] = Double.parseDouble((String)words.get(i));
                                    insertar++;

                                }

                                if ((i+1)==utilizarSalida[0]) {
                                    tag = Double.parseDouble((String)words.get(i));
                                }
                            }
                        }
                        input.add(values);
                        output.add(new Double(tag));
                    }
                    tokens.nextToken();
                }
            }

            Enumeration in=input.elements();
            Enumeration out=output.elements();

            double[][] inArray = new double[input.size()][numVar];;
            double[] outArray = new double[output.size()];

            int i = 0;
            while (in.hasMoreElements()) {
                double[] values =(double[])in.nextElement();
                double tag=((Double)out.nextElement()).doubleValue();

                for (int j=0;j<values.length;j++) {
                    inArray[i][j]=values[j];
                }
                outArray[i]=tag;
                i++;
            }

            switch(fileType){
                case TRAIN:
                    InputDataLearn = inArray;
                    OutputDataLearn = outArray;
                    break;
                case EVAL:
                    InputDataEvaluate = inArray;
                    OutputDataEvaluate = outArray;
                    break;
            }

        }catch(Exception e ){
            e.printStackTrace();
            System.out.println(className+"parse: Error al leer del fichero");

        }


    }
    
    
    public int numExamples(String path) throws FileNotFoundException, IOException{
        int count=0;
    	try
	{
                
		java.io.FileReader lectorFE= new java.io.FileReader(path);		
		java.io.BufferedReader bufferEntrada = new java.io.BufferedReader(lectorFE);		
		String linea = bufferEntrada.readLine();
		while (linea != null)
		{
			count++;
			linea = bufferEntrada.readLine();
		}

	}
	catch (java.io.FileNotFoundException e) {
		System.out.println("Hey, ese archivo no existe!\n");
	}
	catch (java.io.IOException e) { 
		System.out.println("Error de E/S!\n");
	}
        return count;
    }
    

    /**
     * Metodo que a partir del el conjunto de ejemplos
     * genera un nuevo conjunto de ejemplos pero de forma aleatoria, de forma
     * que no siguen el mismo orden con el que fueron leidos del fichero
     * @param inNuevo Vector de entrada 
     * @param outNuevo Vector de salida
     */
    public void randomInstances(Vector inNuevo,Vector outNuevo) {
        Random random = new Random();
        input =new Vector();
        output =new Vector();
        while (!inNuevo.isEmpty()) {
            int which = random.nextInt(inNuevo.size());
            input.add(inNuevo.remove(which));
            output.add(outNuevo.remove(which));
        }
    }

    
    /**
     * Metodo que a partir de un el conjunto de ejemplos
     * crea un nuevo conjunto de ejemplos aleatorio constituido por tantos
     * ejemplos como indique el ratio de aprendizaje           
     * @param inLeNuevo 
     * @param outLeNuevo 
     * @param learnRation 
     * @param inNuevo Vector de entrada
     * @param outNuevo Vector de salida
     */
    public void randomInstances(Vector inLeNuevo,Vector outLeNuevo, Vector inNuevo,Vector outNuevo,double learnRation) {        
        Random random = new Random();
        int count = (int)Math.round(learnRation * inNuevo.size());
        for (int i=0; i<count; i++) {
            int which = random.nextInt(inNuevo.size());            
            inLeNuevo.add(inNuevo.remove(which));                        
            outLeNuevo.add(outNuevo.remove(which));
            
        }        
    }
        //chapuza mia
      public void sequentialInstances(Vector inLeNuevo,Vector outLeNuevo, Vector inNuevo,Vector outNuevo,double learnRation) {
        int count = (int)Math.round(learnRation * inNuevo.size());
        for (int i=0; i<count; i++) { 
            inLeNuevo.add(inNuevo.get(i));                        
            outLeNuevo.add(outNuevo.get(i));
        }
        inNuevo.removeAllElements();
        outNuevo.removeAllElements();
    }

    
     
}