//Autor: Manuel Schmocker
//Datum: 19.02.2021
package ch.manuel.simplidar.calculation;

import ch.manuel.simplidar.gui.MainFrame;
import ch.manuel.simplidar.raster.RasterManager;
import ch.manuel.utilities.MyUtilities;

public class Calculation {

    // class attributes
    private static String msg;
    private static int cellsize;
    private static double tolerance;
    // size
    private int nbRows;
    private int nbCols;
    private int rowMin;
    private int rowMax;
    private int colMin;
    private int colMax;


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
    
    public void setTolerance(double tolerance) {
        Calculation.tolerance = tolerance;
    }

    // PRIVATE FUNCTION
    // init variables
    private void initCalculation() {
        nbRows = RasterManager.mainRaster.getNbRows();
        nbCols = RasterManager.mainRaster.getNbCols();
        RasterManager.mainRaster.initBoolRaster();

        // bounds for runner
        rowMin = cellsize;
        rowMax = nbCols - cellsize;
        colMin = cellsize;
        colMax = nbRows - cellsize;
    }

    // run through raster 
    private void startRunner() {
        PointChecker checkObj;

        for (int i = rowMin; i < rowMax; i++) {
            for (int j = colMin; j < colMax; j++) {
                // create triangles on specific index --> PointChecker
                checkObj = new PointChecker(i, j);
                checkObj.checkPtsInTriangle();
            }
        }
    }





    //***********************************
    private class PointChecker {
        
        // class attributes
        int row;
        int col;
        int[] indexR;
        int[] indexC;
        
        // triangles
        private Triangle triangle1;
        private Triangle triangle2;
        private Triangle triangle3;
        private Triangle triangle4;
        
        PointChecker(int row, int col) {
            this.row = row;
            this.col = col;
            
            createTriangles();
        }
        
        
        //FUNCTIONS
        private void createTriangles() {
            Point p0 = RasterManager.mainRaster.getPoint(col, row);
            Point p1 = RasterManager.mainRaster.getPoint(col + cellsize, row);
            Point p2 = RasterManager.mainRaster.getPoint(col, row + cellsize);
            Point p3 = RasterManager.mainRaster.getPoint(col - cellsize, row);
            Point p4 = RasterManager.mainRaster.getPoint(col, row - cellsize);

            // rectangle 1 (1. Quadrant)
            triangle1 = new Triangle(p0, p1, p2);
            // rectangle 2 (2. Quadrant)
            triangle2 = new Triangle(p0, p2, p3);
            // rectangle 3 (3. Quadrant)
            triangle3 = new Triangle(p0, p3, p4);
            // rectangle 4 (4. Quadrant)
            triangle4 = new Triangle(p0, p4, p1);
        }
        
        private void createIndexes() {
            // indexes for triangle 1
            for (int i = 0; i < cellsize; i++) {
                for (int j = 0; j <= (cellsize - i); j++) {
                    
                }
            }
        }
        
        private void checkPtsInTriangle() {
            checkTri01();
        }
        
        // test points inside triangle
        private void checkTri01() {
            double dist;
            boolean isWithinTolerance = true;

            loop1:
            for (int i = 0; i < cellsize; i++) {
                for (int j = 0; j <= (cellsize - i); j++) {
                    // check points
                    Point p1 = RasterManager.mainRaster.getPoint(col + i, row + j);
                    dist = triangle1.distPoint(p1);

                    if(dist > tolerance) {
                        isWithinTolerance = false;
                        break loop1;
                    }
                }
            }
            
        }
        
        
        
        
        
    }
            
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
