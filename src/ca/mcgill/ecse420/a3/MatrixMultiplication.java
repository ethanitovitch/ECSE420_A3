package ca.mcgill.ecse420.a3;

import java.util.Arrays;
import java.util.concurrent.Future;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;

public class MatrixMultiplication {

    private static final int MATRIX_SIZE = 2000;
    public static ExecutorService exec = Executors.newCachedThreadPool();

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // Generate two random matrices, same size
        double[][] a = generateRandomMatrix(MATRIX_SIZE, MATRIX_SIZE);
        double[] b = generateRandomVector(MATRIX_SIZE);

        // Multiply Matrices
        double[] result_s = sequentialMultiplyMatrix(a, b);
        Vector result_p = parallelMultiplyMatrix(a, b);
        System.out.println(Arrays.toString(result_s));
        result_p.print();

        // Get Matrix Multiplication Durations
//		long duration_s = sequentialMultiplyMatrixTimed(a, b);
//		long duration_p = parallelMultiplyMatrixTimed(a, b);
//		System.out.println(duration_s/1000000000.0);
//		System.out.println(duration_p/1000000000.0);
    }


    /**
     * Returns the duration of a sequential matrix multiplication
     * The two matrices are randomly generated
     * @param a is a matrix
     * @param b is a vector
     * @return the duration of method executions
     * */
    public static long sequentialMultiplyMatrixTimed(double[][] a, double[] b) {
        long startTime = System.nanoTime();
        sequentialMultiplyMatrix(a, b);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        return duration;
    }


    /**
     * Returns the duration of a parallel matrix multiplication
     * The two matrices are randomly generated
     * @param a is a matrix
     * @param b is a vector
     * @return the duration of method execution
     * */
    public static long parallelMultiplyMatrixTimed(double[][] a, double[] b) throws ExecutionException, InterruptedException {
        long startTime = System.nanoTime();
        parallelMultiplyMatrix(a,b);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        return duration;
    }


    /**
     * Returns the result of a matrix multiplied by a vector sequentially
     * The matrix and vector are randomly generated
     * @param a is the matrix
     * @param b is the vector
     * @return the result of the multiplication
     * */
    public static double[] sequentialMultiplyMatrix(double[][] a, double[] b)
    {
        double[] result = new double[a[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                result[i] += a[i][j]*b[j];
            }
        }
        return result;
    }


    /**
     * Returns the result of a matrix multiplied by a vector in parallel
     * The matrix and vector are randomly generated
     * @param matrix is the matrix
     * @param vector is the vector
     * @return the result of the multiplication
     * */
    public static Vector parallelMultiplyMatrix(double[][] matrix, double[] vector) throws ExecutionException, InterruptedException {
        Matrix m = new Matrix(matrix);
        Vector v = new Vector(vector);
        Vector a = new Vector(new double[v.getDim()]);
        exec.submit(new MulTask(m, v, a)).get();
        exec.shutdown();
        return a;
    }

    static class AddTask implements Runnable {
        private Vector lhs, rhs, a;
        public AddTask(Vector lhs, Vector rhs, Vector a) {
            this.lhs = lhs; this.rhs = rhs; this.a = a;
        }

        @Override
        public void run() {
            try {
                int n = a.getDim();
                if (n == 1) {
                    a.set(0, lhs.get(0) + rhs.get(0));
                } else {
                    Vector[] ll = lhs.split();
                    Vector[] rr = rhs.split();
                    Vector[] aa = a.split();
                    Future<?> t = exec.submit(new AddTask(ll[0], rr[0], aa[0]));
                    Future<?> b = exec.submit(new AddTask(ll[1], rr[1], aa[1]));
                    t.get();
                    b.get();
                }
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }
        }
    }

    static class MulTask implements Runnable {
        private Matrix m;
        private Vector v;
        private Vector a;
        private Vector lhs;
        private Vector rhs;

        public MulTask(Matrix m, Vector v, Vector a) {
            this.m = m;
            this.v = v;
            this.a = a;
            this.lhs = new Vector(new double[a.getDim()]);
            this.rhs = new Vector(new double[a.getDim()]);
        }

        @Override
        public void run() {
            try {
                if (m.getDim() == 1) {
                    a.set(0, m.get(0,0) * v.get(0));
                } else {
                    Matrix[][] mm = m.split();
                    Vector[] vv= v.split();
                    Vector[] aa = a.split();
                    Vector[] ll = lhs.split();
                    Vector[] rr = rhs.split();

                    Future<?> tl =
                            exec.submit(new MulTask(mm[0][0], vv[0], ll[0]));
                    Future<?> tr =
                            exec.submit(new MulTask(mm[0][1], vv[1], rr[0]));
                    Future<?> bl =
                            exec.submit(new MulTask(mm[1][0], vv[0], ll[1]));
                    Future<?> br =
                            exec.submit(new MulTask(mm[1][1], vv[1], rr[1]));
                    tl.get();
                    tr.get();
                    bl.get();
                    br.get();
                    Future<?> done = exec.submit(new AddTask(lhs, rhs, a));
                    done.get();
                }
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Populates a matrix of given size with randomly generated integers between 0-10.
     * @param numRows number of rows
     * @param numCols number of cols
     * @return matrix
     */
    private static double[][] generateRandomMatrix (int numRows, int numCols) {

        double[][] matrix = new double[numRows][numCols];
        for (int row = 0 ; row < numRows ; row++ ) {
            for (int col = 0 ; col < numCols ; col++ ) {
                matrix[row][col] = (int) (Math.random() * 10.0);
            }
        }
        return matrix;
    }

    /**
     * Populates a row of given size with randomly generated integers between 0-10.
     * @param numRows number of rows
     * @return vector
     */
    private static double[] generateRandomVector (int numRows) {
        double[] vector = new double[numRows];
        for (int row = 0; row < numRows; row++) {
            vector[row] = (int) (Math.random() * 10.0);
        }
        return vector;
    }

}
