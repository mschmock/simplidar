//Autor: Manuel Schmocker
//Datum: 19.02.2021
package ch.manuel.simplidar.calculation;

import ch.manuel.simplidar.gui.MainFrame;
import ch.manuel.simplidar.raster.RasterManager;
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
        cellsize = 6;
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
        int[][] pointIndT1;
        int[][] pointIndT2;
        int[][] pointIndT3;
        int[][] pointIndT4;
        int[][] edgeIndT1;
        int[][] edgeIndT2;
        int[][] edgeIndT3;
        int[][] edgeIndT4;

        // triangles
        private Triangle triangle1;
        private Triangle triangle2;
        private Triangle triangle3;
        private Triangle triangle4;

        PointChecker(int row, int col) {
            this.row = row;
            this.col = col;

            createEdgeIndices();
            createPointIndices();
            createTriangles();

            // size of index (points inside trinagle)
            // edge has size (n+1) -> Elements (n+1)(n+2)/2 minus points
            int nbElements = (cellsize + 1) * (cellsize + 2) / 2 - 3;
            pointIndT1 = new int[2][nbElements];
            pointIndT2 = new int[2][nbElements];
            pointIndT3 = new int[2][nbElements];
            pointIndT4 = new int[2][nbElements];
            edgeIndT1 = new int[2][3];
            edgeIndT2 = new int[2][3];
            edgeIndT3 = new int[2][3];
            edgeIndT4 = new int[2][3];
        }

        // FUNCTIONS
        // p2 ----- p1       p2 ----- p1 
        //  | \ T2  |         | T4  / | 
        //  |   \   |         |   /   | 
        //  | T1  \ |         | /  T3 | 
        // p3 ----- p0       p3 ----- p0   
        // create indices
        private void createEdgeIndices() {
            // edges T1
            edgeIndT1[0][0] = col;
            edgeIndT1[1][0] = row;
            edgeIndT1[0][1] = col - cellsize;
            edgeIndT1[1][1] = row - cellsize;
            edgeIndT1[0][2] = col - cellsize;
            edgeIndT1[1][2] = row;
            // edges T2
            edgeIndT2[0][0] = col;
            edgeIndT2[1][0] = row;
            edgeIndT2[0][1] = col;
            edgeIndT2[1][1] = row - cellsize;
            edgeIndT2[0][2] = col - cellsize;
            edgeIndT2[1][2] = row - cellsize;
            // edges T3
            edgeIndT3[0][0] = col;
            edgeIndT3[1][0] = row;
            edgeIndT3[0][1] = col;
            edgeIndT3[1][1] = row - cellsize;
            edgeIndT3[0][2] = col - cellsize;
            edgeIndT3[1][2] = row;
            // edges T4
            edgeIndT4[0][0] = col;
            edgeIndT4[1][0] = row - cellsize;
            edgeIndT4[0][1] = col - cellsize;
            edgeIndT4[1][1] = row - cellsize;
            edgeIndT4[0][2] = col - cellsize;
            edgeIndT4[1][2] = row;
        }
        
        // create point indices
        private void createPointIndices() {
            int n;
            // indexes for triangle T1
            n = 0;
            for (int i = 0; i < cellsize; i++) {
                for (int j = 0; j <= (cellsize - i); j++) {
                    if (i == 0 && j == 0) continue;
                    if (i == 0 && (j == cellsize)) continue;

                    pointIndT1[0][n] = col + i - cellsize;
                    pointIndT1[1][n] = row - j;
                    n++;
                }
            }
            // indexes for triangle T2
            n = 0;
            for (int i = 0; i < cellsize; i++) {
                for (int j = 0; j <= (cellsize - i); j++) {
                    if (i == 0 && j == 0) continue;
                    if (i == 0 && (j == cellsize)) continue;

                    pointIndT2[0][n] = col - i;
                    pointIndT2[1][n] = row + j - cellsize;
                    n++;
                }
            }
            // indexes for triangle T3
            n = 0;
            for (int i = 0; i < cellsize; i++) {
                for (int j = 0; j <= (cellsize - i); j++) {
                    if (i == 0 && j == 0) continue;
                    if (i == 0 && (j == cellsize)) continue;

                    pointIndT3[0][n] = col - i;
                    pointIndT3[1][n] = row - j;
                    n++;
                }
            }
            // indexes for triangle T4
            n = 0;
            for (int i = 0; i < cellsize; i++) {
                for (int j = 0; j <= (cellsize - i); j++) {
                    if (i == 0 && j == 0) continue;
                    if (i == 0 && (j == cellsize)) continue;

                    pointIndT4[0][n] = col + i - cellsize;
                    pointIndT4[1][n] = row + j - cellsize;
                    n++;
                }
            }
        }
        
        // create triangels
        private void createTriangles() {
            Point p0 = RasterManager.mainRaster.getPoint(col, row);
            Point p1 = RasterManager.mainRaster.getPoint(col, row - cellsize);
            Point p2 = RasterManager.mainRaster.getPoint(col - cellsize, row - cellsize);
            Point p3 = RasterManager.mainRaster.getPoint(col - cellsize, row);

            // rectangle 1 (1. Quadrant)
            triangle1 = new Triangle(p0, p2, p3);
            // rectangle 2 (2. Quadrant)
            triangle2 = new Triangle(p0, p1, p2);
            // rectangle 3 (3. Quadrant)
            triangle3 = new Triangle(p0, p1, p3);
            // rectangle 4 (4. Quadrant)
            triangle4 = new Triangle(p1, p2, p3);
        }

        private void checkIndexes() {
            boolean isWithinTolerance;

            // **********************
            // indexes for triangle 1 (1. Quadrant)


            // check points inside traingle 1
            isWithinTolerance = checkPtsInTriangle(triangle1, pointIndex);
            // if within tolerance -> remove points from boolean array
            if (isWithinTolerance) {
                removePoints(pointIndex);
                resetPoints(edgeIndex);
            }

            // **********************
            // indexes for triangle 2 (2. Quadrant)


            // check points inside traingle 2
            isWithinTolerance = checkPtsInTriangle(triangle2, pointIndex);
            // if within tolerance -> remove points from boolean array
            if (isWithinTolerance) {
                removePoints(pointIndex);
                resetPoints(edgeIndex);
            }

            // **********************
            // indexes for triangle 3 (3. Quadrant)


            // check points inside traingle 3
            isWithinTolerance = checkPtsInTriangle(triangle3, pointIndex);
            // if within tolerance -> remove points from boolean array
            if (isWithinTolerance) {
                removePoints(pointIndex);
                resetPoints(edgeIndex);
            }

            // **********************
            // indexes for triangle 4 (4. Quadrant)

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
//            printIndex(edgeIndex);      //TEST
//            tri.printEdges();           //TEST
            int nbElements = (cellsize + 1) * (cellsize + 2) / 2 - 3;

            for (int i = 0; i < nbElements; i++) {

                // check points
//                System.out.println("Element " + ind[0][i] + " " + ind[1][i]);
                Point pt = RasterManager.mainRaster.getPoint(ind[0][i], ind[1][i]);
//                pt.printPt();
                dist = tri.distPoint(pt);

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
//            for (int i = 0; i < nbElements; i++) {
//                RasterManager.mainRaster.setBoolElement(ind[0][i], ind[1][i], true);
//            }
        }

        // array to string
        private void printIndex(int[][] ind) {
            int nbElements = ind[0].length;
            String msg = "";
            for (int i = 0; i < nbElements; i++) {
                if (i > 0) {
                    msg += " | ";
                }
                msg += ind[0][i] + ", " + ind[1][i];
            }
            System.out.println(msg);
        }

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
