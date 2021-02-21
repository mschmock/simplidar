//Autor: Manuel Schmocker
//Datum: 20.02.2021
package ch.manuel.simplidar.calculation;

import java.text.DecimalFormat;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Triangle {

    // class attributes
    private Point point1;
    private Point point2;
    private Point point3;

    private Vector3D normal;

    // CONSTRUCTOR
    public Triangle(Point p1, Point p2, Point p3) {
        this.point1 = p1;
        this.point2 = p2;
        this.point3 = p3;
        
        // calculate normal
        this.calcNormal();
    }

    // PUBLIC FUNCTIONS
    // get normal vector as string
    public String showNormal() {
        DecimalFormat myFormatter = new DecimalFormat("0.000");
        return this.normal.toString(myFormatter);
    }

    // calculate dist from point p
    // d=|(⃗p−⃗a)⋅⃗n0|
    // ⃗p Vector to point out of plane
    // ⃗a Vector to point inside plane
    public double distPoint(Point p) {
        // vector(⃗p−⃗a):
        double pa1 = p.getX() - point1.getX();
        double pa2 = p.getY() - point1.getY();
        double pa3 = p.getZ() - point1.getZ();

        Vector3D ap = new Vector3D(pa1, pa2, pa3);

        return ap.dotProduct(this.normal);
    }
    
    // PRIVATE FUNCTIONS
    // calculate normal vector to plane
    private void calcNormal() {
        //v1 = p2-p1
        double v11 = point2.getX() - point1.getX();
        double v12 = point2.getY() - point1.getY();
        double v13 = point2.getZ() - point1.getZ();

        Vector3D vec1 = new Vector3D(v11, v12, v13);

        //v1 = p2-p1
        double v21 = point3.getX() - point1.getX();
        double v22 = point3.getY() - point1.getY();
        double v23 = point3.getZ() - point1.getZ();

        Vector3D vec2 = new Vector3D(v21, v22, v23);

        // calculate normal
        this.normal = vec1.crossProduct(vec2).normalize();
    }
    
    
}
