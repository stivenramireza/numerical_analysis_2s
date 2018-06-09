package com.example.sacrew.numericov4.utils;

import android.widget.Toast;



public class systemEquationsUtils {
    private double[] substitution(double[][] expandedMatrix, int [] marks){
        double [] result = substitution(expandedMatrix,-1);
        if(result == null) return null;
        double [] clean = new double[result.length];
        for(int i = 0; i< result.length; i++){
            double val = result[i];
            clean[marks[i]] = val;
        }
        return clean;
    }
    private double[] substitution(double [][] expandedMatrix,int basura){//regression
        int n = expandedMatrix.length-1;
        double[] values = new double[n+1];
        if(expandedMatrix[n][n] == 0) {
            //Toast.makeText(getContext(), "Error division 0", Toast.LENGTH_SHORT).show();
            return null;
        }
        double x = expandedMatrix[n][n+1]/expandedMatrix[n][n];

        values[values.length-1] = x;
        for(int i = 0 ; i<n+1 ; i++){
            double sumatoria = 0;
            int auxi = n-i;
            for(int p = auxi + 1; p < n+1; p++ ){
                sumatoria = sumatoria + expandedMatrix[auxi][p]*values[p];
            }
            if(expandedMatrix[auxi][auxi] == 0) {
                //Toast.makeText(getContext(), "Error division 0", Toast.LENGTH_SHORT).show();
                return values;
            }
            values[auxi] = (expandedMatrix[auxi][n+1]-sumatoria)/expandedMatrix[auxi][auxi];

        }

        return values;
    }

    private void swapColumn(int k, int higherColumn, double[][] expandedMatrix, int[] marks){
        if(marks != null) {
            int aux = marks[k];
            marks[k] = marks[higherColumn];
            marks[higherColumn] = aux;
        }
        for(int i =0; i < expandedMatrix.length ; i++){
            double temp = expandedMatrix[i][k];
            expandedMatrix[i][k] = expandedMatrix[i][higherColumn];
            expandedMatrix[i][higherColumn] = temp;
        }
    }
    private void swapRows(int k, int higherRow, double[][] expandedMatrix){
        int length = expandedMatrix.length;
        for(int i = 0; i<= length; i++){
            double aux = expandedMatrix[k][i];
            expandedMatrix[k][i] = expandedMatrix[higherRow][i];
            expandedMatrix[higherRow][i] = aux;
        }
    }
    private double[][] totalPivot(int k, double[][] expandedMAtrix, int[] marks){
        double mayor = 0.0;
        int higherRow= k;
        int higherColumn = k;
        for(int r = k; r< expandedMAtrix.length; r++){
            for(int s = k; s< expandedMAtrix.length; s++){
                if(Math.abs(expandedMAtrix[r][s]) > mayor){
                    mayor = Math.abs(expandedMAtrix[r][s]);
                    higherRow = r;
                    higherColumn = s;
                }
            }
        }
        if(mayor == 0){
            //Toast.makeText(getContext(),  "Error division 0", Toast.LENGTH_SHORT).show();
            return null;
        }else{
            if(higherRow != k)
                swapRows(k,higherRow,expandedMAtrix);
            if(higherColumn != k)
                swapColumn(k,higherColumn,expandedMAtrix,marks);
        }
        return expandedMAtrix;
    }
    public double[] eliminationWithTotalPivot(double [][] expandedMatrix){
        int[] marks = new int[expandedMatrix.length];
        for(int i = 0; i< marks.length; i++)
            marks[i] =i;

        for(int k = 0; k< expandedMatrix.length-1; k++){
            expandedMatrix = totalPivot(k,expandedMatrix, marks);
            if(expandedMatrix == null) return null;
            for (int i = k + 1; i < expandedMatrix.length; i++){
                if(expandedMatrix[k][k] == 0) return null;
                    //Toast.makeText(getContext(),  "Error division 0", Toast.LENGTH_SHORT).show();
                double multiplier = expandedMatrix[i][k] / expandedMatrix[k][k];
                for(int j = k; j < expandedMatrix.length + 1; j++){
                    expandedMatrix[i][j] = expandedMatrix[i][j] - multiplier*expandedMatrix[k][j];
                }
            }
        }
        return substitution(expandedMatrix, marks);
    }


}