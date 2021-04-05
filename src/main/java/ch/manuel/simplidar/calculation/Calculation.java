//Autor: Manuel Schmocker
//Datum: 19.02.2021
package ch.manuel.simplidar.calculation;

import ch.manuel.simplidar.gui.MainFrame;
import ch.manuel.simplidar.raster.RasterManager;
import ch.manuel.utilities.MyUtilities;
import java.text.DecimalFormat;

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
        tolerance = 0.2;
    }

    // PUBLIC FUNCTIONS
    // start calculation: -> simplification
    public void startCalculation() {
        initCalculation();
        startRunner();
        
        // print result
        int nbPts = RasterManager.mainRaster.getNumberPoints();
        int nbPixel = RasterManager.mainRaster.getNbPixels();
        DecimalFormat myFormatter = new DecimalFormat("###,###");
        msg = "Initial size: " + myFormatter.format(nbPixel) + "\n";
        msg += "Simp size: " + myFormatter.format(nbPts);
        MainFrame.setText(msg);
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
                checkObj.checkIndexes();
            }
            System.out.println("Line " + i);
        }
    }

    //***********************************
    private class PointChecker {

        // class attributes
        int row;
        int col;
        int[][] pointIndex;
        int[][] edgeIndex;

        // triangles
        private Triangle triangle1;
        private Triangle triangle2;
        private Triangle triangle3;
        private Triangle triangle4;

        PointChecker(int row, int col) {
            this.row = row;
            this.col = col;

            createTriangles();

            // size of index (points inside trinagle)
            // edge has size (n+1) -> Elements (n+1)(n+2)/2 minus points
            int nbElements = (cellsize + 1) * (cellsize + 2) / 2 - 3;
            pointIndex = new int[2][nbElements];
            edgeIndex = new int[2][3];
        }

        //FUNCTIONS
        //      p2
        //   /   |  \
        // p3 - p0 -  p1
        //   \   |  /
        //      p4
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

        private void checkIndexes() {
            int n;
            boolean isWithinTolerance;
            
            // **********************
            // indexes for triangle 1 (1. Quadrant)
            n = 0;
            for (int i = 0; i < cellsize; i++) {
                for (int j = 0; j <= (cellsize - i); j++) {
                    if (i == 0 && j == 0) continue;
                    if (i == 0 && (j == cellsize)) continue;

                    pointIndex[0][n] = col + i;
                    pointIndex[1][n] = row + j;
                    n++;
                }
            }
            edgeIndex[0][0] = col;
            edgeIndex[1][0] = row;
            edgeIndex[0][1] = col + cellsize;
            edgeIndex[1][1] = row;
            edgeIndex[0][2] = col;
            edgeIndex[1][2] = row + cellsize;

            // check points inside traingle 1
            isWithinTolerance = checkPtsInTriangle(triangle1, pointIndex);
            // if within tolerance -> remove points from boolean array
            if (isWithinTolerance) {
                removePoints(pointIndex);
                resetPoints(edgeIndex);
            }
            
            // **********************
            // indexes for triangle 2 (2. Quadrant)
            n = 0;
            for (int i = 0; i < cellsize; i++) {
                for (int j = 0; j <= (cellsize - i); j++) {
                    if (i == 0 && j == 0) continue;
                    if (i == 0 && (j == cellsize)) continue;

                    pointIndex[0][n] = col - i;
                    pointIndex[1][n] = row + j;
                    n++;
                }
            }
            edgeIndex[0][0] = col;
            edgeIndex[1][0] = row;
            edgeIndex[0][1] = col - cellsize;
            edgeIndex[1][1] = row;
            edgeIndex[0][2] = col;
            edgeIndex[1][2] = row + cellsize;

            // check points inside traingle 2
            isWithinTolerance = checkPtsInTriangle(triangle2, pointIndex);
            // if within tolerance -> remove points from boolean array
            if (isWithinTolerance) {
                removePoints(pointIndex);
                resetPoints(edgeIndex);
            }
            
            // **********************
            // indexes for triangle 3 (3. Quadrant)
            n = 0;
            for (int i = 0; i < cellsize; i++) {
                for (int j = 0; j <= (cellsize - i); j++) {
                    if (i == 0 && j == 0) continue;
                    if (i == 0 && (j == cellsize)) continue;

                    pointIndex[0][n] = col - i;
                    pointIndex[1][n] = row - j;
                    n++;
                }
            }
            edgeIndex[0][0] = col;
            edgeIndex[1][0] = row;
            edgeIndex[0][1] = col - cellsize;
            edgeIndex[1][1] = row;
            edgeIndex[0][2] = col;
            edgeIndex[1][2] = row - cellsize;

            // check points inside traingle 3
            isWithinTolerance = checkPtsInTriangle(triangle3, pointIndex);
            // if within tolerance -> remove points from boolean array
            if (isWithinTolerance) {
                removePoints(pointIndex);
                resetPoints(edgeIndex);
            }
            
            // **********************
            // indexes for triangle 4 (4. Quadrant)
            n = 0;
            for (int i = 0; i < cellsize; i++) {
                for (int j = 0; j <= (cellsize - i); j++) {
                    if (i == 0 && j == 0) continue;
                    if (i == 0 && (j == cellsize)) continue;

                    pointIndex[0][n] = col + i;
                    pointIndex[1][n] = row - j;
                    n++;
                }
            }
            edgeIndex[0][0] = col;
            edgeIndex[1][0] = row;
            edgeIndex[0][1] = col + cellsize;
            edgeIndex[1][1] = row;
            edgeIndex[0][2] = col;
            edgeIndex[1][2] = row - cellsize;

            // check points inside traingle 4
            isWithinTolerance = checkPtsInTriangle(triangle4, pointIndex);
            // if within tolerance -> remove points from boolean array
            if (isWithinTolerance) {
                removePoints(pointIndex);
                resetPoints(edgeIndex);
            }

        }

        // test points inside triangle
        private boolean checkPtsInTriangle(Triangle tri, int[][] ind) {
            double dist;
//            System.out.println("Element " + col + " " + row);
            int nbElements = (cellsize + 1) * (cellsize + 2) / 2 - 3;

            for (int i = 0; i < nbElements; i++) {

                // check points
                Point p1 = RasterManager.mainRaster.getPoint(ind[0][i], ind[1][i]);
                dist = tri.distPoint(p1);

                if (Math.abs(dist) > tolerance) {
//                    System.out.println("dist: " + dist + " in element " + i);
                    return false;
                }
            }
//            System.out.println("within distance");
            return true;
        }

        private void removePoints(int[][] ind) {
            int nbElements = (cellsize + 1) * (cellsize + 2) / 2 - 3;

            for (int i = 0; i < nbElements; i++) {
                RasterManager.mainRaster.setBoolElement(ind[0][i], ind[1][i], false);
            }
        }

        private void resetPoints(int[][] ind) {
            int nbElements = 3;
            for (int i = 0; i < nbElements; i++) {
                RasterManager.mainRaster.setBoolElement(ind[0][i], ind[1][i], true);
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
