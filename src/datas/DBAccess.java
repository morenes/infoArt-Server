package datas;

import java.sql.*;

import java.io.*;
import java.util.Random;

/**
 * Esta clase obtiene los datos desde una base de datos
 * @author David Gil Galvan
 */
public class DBAccess implements Serializable, DataAccess{
    
    private int id = 0;
    private boolean connected;
    private String clase =this.getClass().getName();
    
    private double[][] InputDataLearn;
    private double[] OutputDataLearn;
    private double[][] InputDataEvaluate;
    private double[] OutputDataEvaluate;
    
    private double[][] allInputData=null;
    private double[] allOutputData=null;
            
    /**
     * 
     * @return 
     */
    public double[][] getInputDataLearn() {
        return InputDataLearn;
    }
            
    /**
     * 
     * @return 
     */
    public double[] getOutputDataLearn() {
        return OutputDataLearn;
    }            
    
    /**
     * 
     * @return 
     */
    public double[][] getInputDataEvaluate() {
        return InputDataEvaluate;
    }
            
    /**
     * 
     * @return 
     */
    public double[] getOutputDataEvaluate() {
        return OutputDataEvaluate;
    }
    
    /**
     * Builds a new instance of DBAccess
     * @param control The associated ControlProtocol object
     */
    public DBAccess() throws Exception {
        try {
            Class.forName(System.getProperty("db_driver"));
        } catch (Exception e) {
            throw new Exception("Unable to load the data base driver!. Property db_drive not set. "+e);
        }
    }
    
    /**
     * Connects to the data base, starting a new session.
     */
    public synchronized Connection connect() throws Exception{
        String user = getUser();
        String passwd = getPasswd();
        
        try {
            return DriverManager.getConnection(System.getProperty("db_connection"), user, passwd);
        } catch (Exception e) {
            throw new Exception(clase+".connect(): Unable to connect to the data base."+e);
        }
    }
    
    /**
     * 
     * @return 
     */
    private String getUser() {
        return System.getProperty("db_user");
    }
    
    /**
     * 
     * @return 
     */
    private String getPasswd() {
        return System.getProperty("db_password");
    }
                        
    /**
     * 
     * @param nameTable 
     * @param trainRatio 
     * @param utilizarEntrada 
     * @param utilizarSalida 
     * @param limit 
     * @throws java.lang.Exception 
     */
    public void parse(String nameTable, double trainRatio, int[] utilizarEntrada, int[] utilizarSalida, int limit) throws Exception {
        Connection connection=null;
        Statement stmt=null;
        try {
                        
            connection=connect();
            stmt = connection.createStatement();
            
            String query2;
            if (limit>1)
                query2= "SELECT * FROM "+nameTable+" LIMIT "+limit;
            else
                query2="SELECT * FROM "+nameTable;
            ResultSet rs2 = stmt.executeQuery(query2);
            boolean hecho=rs2.last();
            if (hecho==false) throw new Exception("IMPOSIBLE REALIZAR OPERACION");
            int filas=rs2.getRow();
            rs2.beforeFirst();
            if (hecho==false) throw new Exception("IMPOSIBLE REALIZAR OPERACION");
                                    
            double[][] allInputDataAux=new double[filas][utilizarEntrada.length];
            double[] allOutputDataAux = new double[filas];
            
            int i=0;
            while (rs2.next()) {
                
                for(int j=0; j<utilizarEntrada.length; j++) {
                    allInputDataAux[i][j] = rs2.getDouble(utilizarEntrada[j]);
                    
                }
                allOutputDataAux[i]= rs2.getDouble(utilizarSalida[0]);
                i++;
            }
            
            allInputData =new double[filas][utilizarEntrada.length];
            allOutputData = new double[filas];
            Random random = new Random();
            
            i=0;
            while (i<allOutputDataAux.length) {
                int which = random.nextInt(allOutputDataAux.length);
                while (allInputDataAux[which][0]==-1)
                    which=(which+1)%allOutputDataAux.length;
                for (int j=0;j<utilizarEntrada.length;j++) {
                    allInputData[i][j]=allInputDataAux[which][j];
                }
                allOutputData[i]=allOutputDataAux[which];
                allInputDataAux[which][0]=-1;
                i++;
                
            }
                        
            int begin=0;
            int end= (int)Math.round((1-trainRatio) * allOutputData.length);
            //int end=(int)((1-trainRatio)*allOutputData.length);
            
            double[][] inputDataEvaluate=new double[end-begin][utilizarEntrada.length];
            double[] outputDataEvaluate=new double[end-begin];
            double[][] inputDataLearn=new double[allInputData.length-(end-begin)][utilizarEntrada.length];
            double[] outputDataLearn=new double[allOutputData.length-(end-begin)];
            
            for (i=begin;i<end;i++) {
                for (int j=0;j<utilizarEntrada.length;j++) {
                    inputDataEvaluate[i-begin][j]=allInputData[i][j];
                }
                outputDataEvaluate[i-begin]=allOutputData[i];
            }
            for (i=0;i<begin;i++) {
                for (int j=0;j<utilizarEntrada.length;j++) {
                    inputDataLearn[i][j]=allInputData[i][j];
                }
                outputDataLearn[i]=allOutputData[i];
            }
            
            for (i=end;i<allOutputData.length;i++) {
                for (int j=0;j<utilizarEntrada.length;j++) {
                    inputDataLearn[begin+i-end][j]=allInputData[i][j];
                }
                outputDataLearn[begin+i-end]=allOutputData[i];
            }
            
            InputDataLearn=inputDataLearn;
            OutputDataLearn=outputDataLearn;
            InputDataEvaluate=inputDataEvaluate;
            OutputDataEvaluate=outputDataEvaluate;
            
            
        } catch (Exception e) {
            try {
                if (connection!=null) connection.close();
                if (stmt!=null) stmt.close();
            } catch (Exception ex) {
            }
            throw new Exception(clase+".getTuplesRandom():Error getting number of conversations" + e);
        }
    }       
    //no probado
        public int numExamples(String nameTable) throws Exception {
        Connection connection=null;
        Statement stmt=null;
        int count=0;
        try {
                        
            connection=connect();
            stmt = connection.createStatement();
            
            String query2= "SELECT count(*) FROM "+nameTable;
            ResultSet rs2 = stmt.executeQuery(query2);
            boolean hecho=rs2.last();
            if (hecho==false) throw new Exception("IMPOSIBLE REALIZAR OPERACION");
            count=rs2.getRow();
            rs2.beforeFirst();
            if (hecho==false) throw new Exception("IMPOSIBLE REALIZAR OPERACION");
            
          } catch (Exception e) {
            try {
                if (connection!=null) connection.close();
                if (stmt!=null) stmt.close();
            } catch (Exception ex) {
            }
            throw new Exception( e);
        }
        return count;
    }
}
        