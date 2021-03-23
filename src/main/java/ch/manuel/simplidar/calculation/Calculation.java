//Autor: Manuel Schmocker
//Datum: 19.02.2021
package ch.manuel.simplidar.calculation;

import ch.manuel.simplidar.gui.MainFrame;
import ch.manuel.simplidar.raster.RasterManager;
import ch.manuel.utilities.MyUtilities;

public class Calculation {

    // class attributes
    private static String msg;
    private static boolean[][] raster;
    private static int cellsize;
    // size
    private int nbRows;
    private int nbCols;
    private int rowMin;
    private int rowMax;
    private int colMin;
    private int colMax;
    // triangles
    private Triangle triangle1;
    private Triangle triangle2;
    private Triangle triangle3;
    private Triangle triangle4;

    // CONSTURCTOR
    public Calculation() {
        cellsize = 3;
    }

    // PUBLIC FUNCTIONS
    // start calculation: -> simplification
    public void startCalculation() {
        initCalculation();
        startRunner();
    }

    // PRIVATE FUNCTION
    // init variables
    private void initCalculation() {
        nbRows = RasterManager.mainRaster.getNbRows();
        nbCols = RasterManager.mainRaster.getNbCols();
        raster = new boolean[nbRows][nbCols];

        // bounds for runner
        rowMin = cellsize;
        rowMax = nbCols - cellsize;
        colMin = cellsize;
        colMax = nbRows - cellsize;
    }

    // run through raster 
    private void startRunner() {
        cellsize = 3;

        for (int i = rowMin; i < rowMax; i++) {
            for (int j = colMin; j < colMax; j++) {
                createTriangles(i,j);
            }
        }
    }

    // create rectangle 
    private void createTriangles(int i, int j) {
        
        Point p0 = RasterManager.mainRaster.getPoint(j, i);
        Point p1 = RasterManager.mainRaster.getPoint(j + cellsize, i);
        Point p2 = RasterManager.mainRaster.getPoint(j, i + cellsize);
        Point p3 = RasterManager.mainRaster.getPoint(j - cellsize, i);
        Point p4 = RasterManager.mainRaster.getPoint(j, i - cellsize);
        
        // rectangle 1 (1. Quadrant)
        triangle1 = new Triangle(p0, p1, p2);
        // rectangle 2 (2. Quadrant)
        triangle1 = new Triangle(p0, p2, p3);
        // rectangle 3 (3. Quadrant)
        triangle1 = new Triangle(p0, p3, p4);
        // rectangle 4 (4. Quadrant)
        triangle1 = new Triangle(p0, p4, p1);
        
    }

    //***********************************
    public static void testCalc(int x, int y) {

        Point point = RasterManager.mainRaster.getPoint(x - 1, y - 1);

        msg = "X-Koordinate: " + point.getX() + "\n";
        msg += "Y-Koordinate: " + point.getY() + "\n";
        msg += "Z-Koordinate: " + point.getZ() + "\n";

        // test 
        Calculation.createTriangle2();

        double arr[] = {2.0, 2.3, 5, 4.8, 1.11};
        msg += "Std-dev: " + MyUtilities.standardDev(arr) + "\n";

        MainFrame.setText(msg);
    }

    // Test
    private static void createTriangle2() {
        Point p1 = new Point(2, 7, 0);
        Point p2 = new Point(0, 1, 9);
        Point p3 = new Point(2, 0, 9);

        Triangle triangle = new Triangle(p1, p2, p3);
        msg += triangle.showNormal() + "\n";

        msg += "Abstand Punkt:\n";
        Point pP = new Point(5, -3, -4);
        msg += "Berechnet: " + triangle.distPoint(pP) + "\n";

    }

}
