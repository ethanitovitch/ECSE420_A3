package ca.mcgill.ecse420.a3;

public class Matrix {
    int dim;
    double[][] data;
    int rowDisplace, colDisplace;

    public Matrix(double[][] matrix) {
        data = matrix;
        rowDisplace = colDisplace = 0;
        dim = matrix.length;
    }

    private Matrix(double[][] matrix, int x, int y, int d) {
        data = matrix;
        rowDisplace = x;
        colDisplace = y;
        dim = d;
    }

    public double get(int row, int col) {
        return data[row+rowDisplace][col+colDisplace];
    }

    public int getDim() {
        return dim;
    }

    public Matrix[][] split() {
        Matrix[][] result = new Matrix[2][2];
        int newDim = dim / 2;
        result[0][0] = new Matrix(data, rowDisplace, colDisplace, newDim);
        result[0][1] = new Matrix(data, rowDisplace, colDisplace + newDim, newDim);
        result[1][0] = new Matrix(data, rowDisplace + newDim, colDisplace, newDim);
        result[1][1] = new Matrix(data, rowDisplace + newDim, colDisplace+ newDim, newDim);
        return result;
    }
}
