//Autor: Manuel Schmocker
//Datum: 19.02.2021
package ch.manuel.simplidar.calculation;

import ch.manuel.simplidar.gui.MainFrame;
import ch.manuel.simplidar.raster.DataManager;
import java.util.Arrays;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Calculation {

    // test message
    private static String msg;

    public static void testCalc(int x, int y) {

        Point point = DataManager.mainRaster.getPoint(x - 1, y - 1);

        msg = "X-Koordinate: " + point.getX() + "\n";
        msg += "Y-Koordinate: " + point.getY() + "\n";
        msg += "Z-Koordinate: " + point.getZ() + "\n";

        // test 
        Calculation.createTriangle();

        DataManager.initClusters();
        Vector3D nClus = DataManager.getElement(0, 0).getNormal();
        msg += "Norm cluster: " + Arrays.toString(nClus.toArray());
        
        
        MainFrame.setText(msg);
    }

    private static void createTriangle() {
        Point p1 = new Point(2, 7, 0);
        Point p2 = new Point(0, 1, 9);
        Point p3 = new Point(2, 0, 9);

        Triangle triangle = new Triangle(p1, p2, p3);
        msg += triangle.showNormal() + "\n";

        msg += "Abstand Punkt:\n";
        Point pP = new Point(5, -3, -4);
        msg += "Berechnet: " + triangle.distPoint(pP) + "\n";

    }
    
//    public static void testSolver() {
//        /*
//        ax + bx + c = z
//        |x0 y0 1|           |z0|
//        |x1 y1 1|   |a|     |z1|
//        |x2 y2 1| X |b| =   |z2|
//        ...         |c|     ...
//        |xN yN 1|           |zN|
//        
//        A * u = b
//        */
//        
//        double subArray[][] = DataManager.mainRaster.getSubarray(0, 0, 10);
//        double arrA[][] = { subArray[0] , subArray[1] , subArray[2]};
//
//        RealMatrix A = new Array2DRowRealMatrix( new double[][] { { 3, 2, -1 }, { 2, -2, 4 }, { -1, 0.5, -1 } }, false );
//        RealVector b = new ArrayRealVector( new double[] { 1, -2, 0 }, false );
//        
//        RealVector x = solveSystem(A, b);
//        
//        System.out.println( x.toString());
//        
//    }
//    
//     solve linear system
//    private static RealVector solveSystem(RealMatrix A, RealVector b) {
//        RealMatrix ATA = A.transpose().multiply(A);
//        RealVector ATb = A.transpose().operate( b );
//        
//        DecompositionSolver solver = new LUDecomposition( ATA ).getSolver();
//        return solver.solve( ATb );
//        DecompositionSolver solver = new LUDecomposition( A ).getSolver();
//        return solver.solve( b );
//    }
}
