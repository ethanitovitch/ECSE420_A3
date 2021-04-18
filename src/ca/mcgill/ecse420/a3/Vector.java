package ca.mcgill.ecse420.a3;

import java.util.Arrays;

public class Vector {
    int dim;
    double[] data;
    int rowDisplace;

    public Vector(double[] vector) {
        data = vector;
        rowDisplace = 0;
        dim = vector.length;
    }

    private Vector(double[] vector, int rowDisplace, int d) {
        data = vector;
        this.rowDisplace = rowDisplace;
        dim = d;
    }

    public double get(int row) {
        return data[row+rowDisplace];
    }

    public void set(int row, double value) {
        data[row+rowDisplace] = value;
    }

    public int getDim() {
        return dim;
    }

    public void print() {
        System.out.println(Arrays.toString(data));
    }

    public Vector[] split() {
        Vector[] result = new Vector[2];
        int newDim = dim / 2;
        result[0] = new Vector(data, rowDisplace, newDim);
        result[1] = new Vector(data, rowDisplace + newDim, newDim);
        return result;
    }
}
