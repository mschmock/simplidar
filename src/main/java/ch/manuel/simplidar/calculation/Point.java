//Autor: Manuel Schmocker
//Datum: 20.02.2021


package ch.manuel.simplidar.calculation;


public class Point {
    // class attributes
    private double val_x;
    private double val_y;
    private double val_z;
    
    // CONSTRUCTOR
    public Point(double x, double y, double z) {
        this.val_x = x;
        this.val_y = y;
        this.val_z = z;
    }
    
    // GETTER
    public double getX() {
        return this.val_x;
    }
    public double getY() {
        return this.val_y;
    }
    public double getZ() {
        return this.val_z;
    }
}
