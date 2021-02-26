//Autor: Manuel Schmocker
//Datum: 23.02.2021
package ch.manuel.simplidar.raster;

import ch.manuel.simplidar.calculation.Point;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class Cluster {

    // class attributes
    private Vector3D normal;        // normal to cluster (regression)
    private double inclination;     // Neigung von Horizontale
    private double inclSunbeam;      // Neigung zum Vektor SUNBEAM
    private double orientation;     // angle direction N-E-S-W
    // vector sunbeam
    private static Vector3D sunbeam;
    // raster index
    private int colIndex;
    private int rowIndex;
    private int size;

    // CONSTRUCTOR
    public Cluster(int x, int y, int size) {
        this.colIndex = x;
        this.rowIndex = y;
        this.size = size;
        // sunbeam default
        Cluster.sunbeam = new Vector3D(-1 ,-1, 1).normalize();
        // calculate geometric properties
        this.normalOfCluster();
        this.calcInclination();
        this.calcInclSunbeam();
        this.calcOrientation();
    }

    // GETTER
    public Vector3D getNormal() {
        return this.normal;
    }

    // inclination in degrees (0° = vertical, 0° - 90°)
    public float getInclinDEG() {
        return ((float) (this.inclination / Math.PI)) * 180.0f;
    }
    // inclination in degrees (0° = parallel to beam, 0° - 180°)
    public float getInclinSunDEG() {
        
        return ((float) (this.inclSunbeam / Math.PI)) * 180.0f;
    }

    // orientation (0° = North)
    public float getOrientEG() {
        float deg = ((float) (this.orientation / Math.PI)) * 180.0f;
        if (deg > 90.0) {
            return 450.0f - deg;
        } else {
            return 90.0f - deg;
        }
    }
    // PUBLIC FUNCTIONS
    public static void setSunbeamDirection(int degree) {
        if (degree > 90) {
            degree = 450 - degree;
        } else {
            degree =  90 - degree;
        }
        double x = Math.sin(degree*Math.PI/180.0);
        double y = Math.cos(degree*Math.PI/180.0);
        
        Cluster.sunbeam = new Vector3D(x ,y, 0.75).normalize();
    }
    // recalculate sunbeam incl
    public void recalcInclSun() {
        calcInclSunbeam();
    }
    
    // PRIVATE FUNCTIONS
    // get normal of a cluster (by linear regression)
    // A * u = b
    private void normalOfCluster() {
        double[][] AA = new double[size * size][3];
        double[] bb = new double[size * size];
        int nb = 0;
        int xEnd = colIndex + size - 1;
        int yEnd = rowIndex + size - 1;

        for (int i = colIndex; i < xEnd; i++) {
            for (int j = rowIndex; j < yEnd; j++) {
                Point p = DataManager.mainRaster.getPoint(i, j);
                AA[nb][0] = p.getX();
                AA[nb][1] = p.getY();
                AA[nb][2] = 1.0;
                bb[nb] = p.getZ();
                nb++;
            }
        }

        RealMatrix A = new Array2DRowRealMatrix(AA, false);
        RealVector b = new ArrayRealVector(bb, false);

        RealVector solution = this.solveSystem(A, b);
        // get norm from ax + bx + c = z
        // -ax - bx + 1*z + c = 0
        this.normal = new Vector3D(-solution.getEntry(0), -solution.getEntry(1), 1.0).normalize();
    }

    // solve linear system
    /*
        ax + bx + c = z
        |x0 y0 1|           |z0|
        |x1 y1 1|   |a|     |z1|
        |x2 y2 1| X |b| =   |z2|
        ...         |c|     ...
        |xN yN 1|           |zN|
        
        A * u = b
     */
    private RealVector solveSystem(RealMatrix A, RealVector b) {
        RealMatrix ATA = A.transpose().multiply(A);
        RealVector ATb = A.transpose().operate(b);

        DecompositionSolver solver = new LUDecomposition(ATA).getSolver();
        return solver.solve(ATb);

    }

    // calculate inclination
    private void calcInclination() {
        Vector3D ez = new Vector3D(0, 0, 1);

        inclination = Math.acos(this.normal.dotProduct(ez));
    }
    
    // calculate angle of vector SUNBEAM
    private void calcInclSunbeam() {
        inclSunbeam = Math.acos(this.normal.dotProduct(Cluster.sunbeam));
    }

    // calculate orientation in x - y plane
    private void calcOrientation() {
        double x = this.normal.getX();
        double y = this.normal.getY();
        this.orientation = Math.atan2(y, x);
    }

}
