package utility;
/**
 * Clase que se encarga de realizar operaciones sobre matrices y vectores
 */
public class MatrixOperation {
    
    /**
     * Metodo que calcula el producto de dos matrices
     * @param matrixA Matriz original A
     * @param matrixB Matriz original B
     * @return Producto de las dos matrices
     */
    public static double[][] product(double[][] matrixA, double[][] matrixB){
        
        double[][] matrixProduct=new double[matrixA.length][matrixB[0].length];
        for(int i=0; i<matrixA.length; i++){
            for(int j=0; j<matrixB[0].length; j++){
                for(int k=0; k<matrixA[0].length; k++){
                    matrixProduct[i][j]+=matrixA[i][k]*matrixB[k][j];
                }
            }
        }
        
        return matrixProduct;
    }
    
    /**
     * Metodo que calcula la traspuesta de una matriz
     * @param matrix Matriz original
     * @return Devuelve la matriz traspuesta
     */
    public static double[][] trasponse(double[][] matrix){
        int numRows=matrix.length;
        int numColum=matrix[0].length;
        double[][] matrixTrans=new double[numColum][numRows];
        for(int i=0; i<numRows; i++){
            for(int j=0; j<numColum; j++){
                matrixTrans[j][i]=matrix[i][j];
            }
        }
        return matrixTrans;
    }
        
    /**
     * Metodo que calcula la suma de dos matrices
     * @param matrix1 Matriz original 1
     * @param matrix2 Matriz original 2
     * @return Suma de las dos matrices
     * @throws Exception Se produce una excepcion en caso que las dos matrices no tengan la misma dimension
     */
    public static double[][] sum(double[][] matrix1, double[][] matrix2) throws Exception{
        // Si las matrices no tienen la misma dimension se genera una excepcion
        if ((matrix1.length!=matrix2.length)||(matrix1[0].length!=matrix2[0].length))
            throw new Exception("Error Matrix !");
        double[][] matrixSum=new double[matrix1.length][matrix1[0].length];
        for (int i=0;i<matrix1.length;i++) {
            for (int j=0;j<matrix1[0].length;j++) {
                matrixSum[i][j]=matrix1[i][j]+matrix2[i][j];
            }
        }
        return matrixSum;
    }
    
    /**
     * Metodo que calcula la resta de dos matrices
     * @param matrix1 Matriz original 1
     * @param matrix2 Matriz original 2
     * @return Resta de las dos matrices
     * @throws Exception Se produce una excepcion en caso que las dos matrices no tengas la misma dimension
     */
    public static double[][] resta(double[][] matrix1, double[][] matrix2) throws Exception{
        // Si las matrices no tienen la misma dimension se genera una excepcion
        if ((matrix1.length!=matrix2.length)||(matrix1[0].length!=matrix2[0].length))
            throw new Exception("Error Matrix !");
        double[][] matrixSum=new double[matrix1.length][matrix1[0].length];
        for (int i=0;i<matrix1.length;i++) {
            for (int j=0;j<matrix1[0].length;j++) {
                matrixSum[i][j]=matrix1[i][j]-matrix2[i][j];
            }
        }
        return matrixSum;
    }
    
    /**
     * Metodo que calcula el producto de un escalar por una matriz
     * @param scalar Escalar
     * @param matrix Matriz original
     * @return Matriz resultante de multiplicar el escalar por la matriz
     */
    public static double[][] productMatrixScalar(double scalar, double[][] matrix) {
        int numRows=matrix.length;
        int numColum=matrix[0].length;
        double[][] matrixProduc=new double[numColum][numRows];
        for(int i=0; i<numRows; i++){
            for(int j=0; j<numColum; j++){
                matrixProduc[i][j]=matrix[i][j]*scalar;
            }
        }
        return matrixProduc;
    }
        
    /**
     * Metodo que devuelve una cadena de texto con la informacion de la matriz
     * @param matrix Matriz
     * @return Devuelve la cadena de texto con la informacion de la matriz
     */
    public static String toStringMatrix(double[][] matrix) {

        int numRows=matrix.length;
        int numColum=matrix[0].length;
        String texto="\n";
        for(int i=0; i<numRows; i++){
            for(int j=0; j<numColum; j++){

                texto+="\t --"+matrix[i][j]+"--";
            }
            texto+="\n";
        }
        texto+="\n";
        return texto;
    }
    
    /**
     * Metodo que devuelve la informaci�n del vector como una cadena de tecto
     * @param vector Vector
     * @return Devuelve la cadena de texto con la informacion del vector
     */
    public static String toStringVector(double[] vector) {
        int numRows=vector.length;
        
        String texto="\n";
        for(int i=0; i<numRows; i++){
                texto+="\t "+vector[i] +"\n";

        }
        texto+="\n";
        return texto;
    }
    
    /**
     * Metodo que realiza una copia de una matriz
     * @param matrix Matriz original
     * @return Devuelve la copia de la matriz original
     */
    public static double[][] copyMatrix(double[][] matrix) {
        int numRows=matrix.length;
        int numColum=matrix[0].length;
        double[][] matrixCopy=new double[numRows][numColum];
        for (int i=0;i<numRows;i++)
            System.arraycopy(matrix[i], 0, matrixCopy[i], 0, numColum);        
        return matrixCopy;
    }
    
    /**
     * Metodo que devuelve la inversa de una matriz
     * @return Matriz inversa
     * @param matrixOriginal Matriz original
     * @throws Exception Devuelve una excepcion en caso de que se produzca cualquier error
     */
    public static double[][] invert(double[][] matrixOriginal) throws Exception {
        
        double[][] matrix=MatrixOperation.copyMatrix(matrixOriginal);
        int n = matrix.length;
        int row[] = new int[n];
        int col[] = new int[n];
        double temp[] = new double[n];
        int hold , I_pivot , J_pivot;
        double pivot, abs_pivot;
        
        if(matrix[0].length!=n) {
            throw new Exception("Error in Matrix.invert, inconsistent array sizes.");
        }
        // set up row and column interchange vectors
        for(int k=0; k<n; k++) {
            row[k] = k ;
            col[k] = k ;
        }
        // begin main reduction loop
        for(int k=0; k<n; k++) {
            // find largest element for pivot
            pivot = matrix[row[k]][col[k]] ;
            I_pivot = k;
            J_pivot = k;
            for(int i=k; i<n; i++) {
                for(int j=k; j<n; j++) {
                    abs_pivot = Math.abs(pivot) ;
                    if(Math.abs(matrix[row[i]][col[j]]) > abs_pivot) {
                        I_pivot = i ;
                        J_pivot = j ;
                        pivot = matrix[row[i]][col[j]] ;
                    }
                }
            }
            if(Math.abs(pivot) < 1.0E-10) {
                throw new Exception("Matrix is singular !");
            }
            hold = row[k];
            row[k]= row[I_pivot];
            row[I_pivot] = hold ;
            hold = col[k];
            col[k]= col[J_pivot];
            col[J_pivot] = hold ;
            // reduce about pivot
            matrix[row[k]][col[k]] = 1.0 / pivot ;
            for(int j=0; j<n; j++) {
                if(j != k) {
                    matrix[row[k]][col[j]] = matrix[row[k]][col[j]] * matrix[row[k]][col[k]];
                }
            }
            // inner reduction loop
            for(int i=0; i<n; i++) {
                if(k != i) {
                    for(int j=0; j<n; j++) {
                        if( k != j ) {
                            matrix[row[i]][col[j]] = matrix[row[i]][col[j]] - matrix[row[i]][col[k]] *
                                    matrix[row[k]][col[j]] ;
                        }
                    }
                    matrix[row[i]][col [k]] = - matrix[row[i]][col[k]] * matrix[row[k]][col[k]] ;
                }
            }
        }
        // end main reduction loop
        
        // unscramble rows
        for(int j=0; j<n; j++) {
            for(int i=0; i<n; i++) {
                temp[col[i]] = matrix[row[i]][j];
            }
            for(int i=0; i<n; i++) {
                matrix[i][j] = temp[i] ;
            }
        }
        // unscramble columns
        for(int i=0; i<n; i++) {
            for(int j=0; j<n; j++) {
                temp[row[j]] = matrix[i][col[j]] ;
            }
            for(int j=0; j<n; j++) {
                matrix[i][j] = temp[j] ;
            }
        }
        return matrix;
    } // end invert
    
    /**
     * Metodo que devuelve la traza de una matriz cuadrada
     * @param matrix Matriz de la que se quiere obtener la traza
     * @return Devuelve la traza de la matriz, es decir la suma de los valores que se encuentran
     * en la diagonal principal de la matriz
     * @throws Exception Devuelve una excepcion en caso que la matriz no sea cuadrada
     */
    public static double getTrace(double[][] matrix) throws Exception{
         double summation=0;
         int numRows=matrix.length;
         int numColum=matrix[0].length;
         if (numRows!=numColum)
             throw new Exception("Matrix isn't square !");         
        for (int i=0;i<numRows;i++) {
            summation+=matrix[i][i];
            }
            return summation;
    }
}
