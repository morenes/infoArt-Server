/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package datas.preprocess;

import datas.DirFilter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;


/**
 * Clase encargada de preprocesar los datos de ejemplo contenidos en ficheros
 * normales de texto (.txt). Es una simple ayuda que calcula las lineas
 * a eliminar en los ficheros
 *
 * @author Fernando Terroso Saenz
 */
public class FileDataPreProcessor implements DataPreProcessor{


    public void preProcess(DataPreProcessorParams pParams) {
        try{
            File fileData = new File(pParams.getInputDir());

            //Filtro para solo leer los ficheros .txt del directorio de datos de entrada
            DirFilter filter = new DirFilter("\\w+\\.txt");

            for(File file: fileData.listFiles(filter)){

               System.out.println("Preprocesando fichero: " + file.getName());
               BufferedReader bf = new BufferedReader(new FileReader(file));
               String line;
               int numberOfLines = 0;
               int numberOfManauvers = 0;
               boolean in = false;
               List<Integer> positions = new LinkedList<Integer>();
               //Contamos cuantos ejemplos hay de la clase indicada
               System.out.print("Generando intervalos...");
               while ((line = bf.readLine())!=null) {
                   String[] fields = line.split("\t");
                   String manauver = fields[fields.length-1];
                   if(manauver.equals(pParams.getManauverToHandle())){
                       if(!in){
                           positions.add(numberOfLines);
                           in = true;
                       }
                       numberOfManauvers++;
                   }
                   else if(in){
                       in = false;
                       positions.add(numberOfLines-1);
                   }
                   numberOfLines++;
               }
               if(in){
                   positions.add(numberOfLines-1);
               }
              Integer[] positionsArray = new Integer[positions.size()];
              positions.toArray(positionsArray);

              List<Integer> positionsToDelete = new LinkedList<Integer>();

              //Obtenemos los rangos de ejemplos a eliminar
              for(int i=0; i<= positionsArray.length-2; i+=2){
                  int lowBound = positionsArray[i];
                  int upperBound = positionsArray[i+1];
                  long aux = upperBound - lowBound;

                  int middle = ((int)aux/2) + lowBound;

                  double numerator = (double) aux*pParams.getNumberToReach();

                  // Regla de tres
                  aux = Math.round(numerator / (double) numberOfManauvers);

                  long j = Math.round(aux * 0.40);
                  long j2 = Math.round(aux * 0.20);

                  positionsToDelete.add(lowBound + (int)j);
                  positionsToDelete.add(middle - (int) (j2/2));

                  positionsToDelete.add(middle + (int)(j2/2));
                  positionsToDelete.add(upperBound- (int) j);

              }

              bf.close();

              System.out.println("OK!");

              File outputDir = new File(pParams.getOutputDir());
              outputDir.mkdirs();

              String filePath = pParams.getOutputDir() + File.separator + file.getName();
              PrintWriter out = new PrintWriter(filePath);


              bf = new BufferedReader(new FileReader(file));
              int indexBound = 0;
              int boundLow = positionsToDelete.get(indexBound++);
              int boundUp = positionsToDelete.get(indexBound++);

              int index = 0;
              List<String> backUpLow = new LinkedList<String>();
              List<String> backUpUp = new LinkedList<String>();

              System.out.print("Borrando instancias...");
              //Bucle que copia las instancias de un fichero a otro
              while ((line = bf.readLine())!=null) {
                  //Mientras no este en un intervalo de eliminacion, copio
                  if((index < boundLow-2) || (index > boundUp)){
                      if(backUpLow.isEmpty()){
                        out.println(line);
                      }
                      else{
                          String backup1 = backUpLow.get(0);
                          String[] parts1 = backup1.split("\t");

                          String[] parts2 = line.split("\t");

                          parts1[4] = parts2[2];

                          StringBuilder newLine = new StringBuilder();
                          for(String s : parts1){
                              newLine.append(s);
                              newLine.append("\t");
                          }
                          newLine.replace(newLine.length()-1, newLine.length()-1, "");
                          out.println(newLine.toString());

                          backUpLow.remove(0);
                          if(!backUpLow.isEmpty()){
                              String backup2 = backUpLow.get(0);
                              String[] parts3 = backup2.split("\t");

                              parts3[3] = parts2[2];

                              newLine = new StringBuilder();
                              for(String s : parts3){
                                  newLine.append(s);
                                  newLine.append("\t");
                              }
                              newLine.replace(newLine.length()-1, newLine.length()-1, "");
                              backUpLow.set(0, newLine.toString());
                          }

                          if(backUpLow.isEmpty()){
                              for(String nLine : backUpUp){
                                  out.println(nLine);
                              }
                              backUpUp.clear();
                              out.println(line);
                          }
                          else{
                              backUpUp.add(line);
                          }

                      }
                  }
                  if(index == boundUp){
                      if(indexBound < positionsToDelete.size()){
                          boundLow = positionsToDelete.get(indexBound++);
                          boundUp = positionsToDelete.get(indexBound++);
                      }
                  }

                  //Guardamos la ultima de las lineas antes de empezar el borrado
                  if((index == boundLow-1) || (index == boundLow-2)){
                      if(backUpLow.size() > 2){
                        String backup1 = backUpLow.get(0);
                        String[] parts1 = backup1.split("\t");

                        String[] parts3 = line.split("\t");

                        parts1[4] = parts3[2];

                        StringBuilder newLine = new StringBuilder();
                        for(String s : parts1){
                          newLine.append(s);
                          newLine.append("\t");
                        }
                        newLine.replace(newLine.length()-1, newLine.length()-1, "");
                        out.println(newLine.toString());
                        
                        backUpLow.remove(0);

                        String backup2 = backUpLow.get(0);
                        String[] parts2 = backup2.split("\t");

                        parts2[3] = parts3[2];

                        newLine = new StringBuilder();
                        for(String s : parts2){
                            newLine.append(s);
                            newLine.append("\t");
                        }
                        newLine.replace(newLine.length()-1, newLine.length()-1, "");
                        backUpLow.set(0, newLine.toString());

                      } else if(backUpLow.size() == 1){
                          if(!backUpUp.isEmpty()){
                              String backup2 = backUpLow.get(0);
                              String[] parts2 = backup2.split("\t");

                              String[] parts3 = line.split("\t");

                              parts2[4] = parts3[2];

                              StringBuilder newLine = new StringBuilder();
                              for(String s : parts2){
                                  newLine.append(s);
                                  newLine.append("\t");
                              }
                              newLine.replace(newLine.length()-1, newLine.length()-1, "");
                              out.println(newLine.toString());
                              backUpLow.remove(0);

                              for(String nLine : backUpUp){
                                  out.println(nLine);
                              }
                              backUpUp.clear();
                          }
                      }

                      backUpLow.add(line);

                  }
                  index++;
              }
              System.out.println("OK!");

              out.close();

            }
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
    }

}


