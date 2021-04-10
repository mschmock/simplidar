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
    private static int nbRows;
    private static int nbCols;
    // threads
    private Thread t1;
    private Thread t2;
    private Thread t3;
    private Thread t4;
    private CalcRunner runner1;
    private CalcRunner runner2;
    private CalcRunner runner3;
    private CalcRunner runner4;

    // CONSTURCTOR
    public Calculation() {
        cellsize = 5;
        tolerance = 0.1;
        
        runner1 = new Calculation.CalcRunner(1);
        runner2 = new Calculation.CalcRunner(2);
        runner3 = new Calculation.CalcRunner(3);
        runner4 = new Calculation.CalcRunner(4);
    }

    // PUBLIC FUNCTIONS
    // start calculation: -> simplification
    public void startCalculation() {
        initCalculation();
        startThreads();

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
    }
    
    private void startThreads() {
        t1 = new Thread(runner1);
        t2 = new Thread(runner2);
        t3 = new Thread(runner3);
        t4 = new Thread(runner4);
        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }
    



    //***********************************
    private class CalcRunner implements Runnable {
        
        private int indexRunner;
        private int rowMin;
        private int rowMax;
        private int colMin;
        private int colMax;
    
        // CONSTRUCTOR
        CalcRunner(int index) {
            this.indexRunner = index;
            setBounds(indexRunner);
        }
        
        @Override
        public void run() {
            startRunner();
        }
        
        // FUNCTIONS
        // set bounds for runner
        private void setBounds(int index) {
            int rowSplit = nbRows / 2;
            int colSplit = nbCols / 2;
            
            switch(index) {
                case 1:
                    rowMin = cellsize;
                    rowMax = rowSplit;
                    colMin = cellsize;
                    colMax = colSplit;
                    break;
                case 2:
                    rowMin = cellsize;
                    rowMax = rowSplit;
                    colMin = colSplit + cellsize;
                    colMax = nbCols;
                    break;
                case 3:
                    rowMin = rowSplit + cellsize;
                    rowMax = nbRows;
                    colMin = cellsize;
                    colMax = colSplit;
                    break;
                case 4:
                    rowMin = rowSplit + cellsize;
                    rowMax = nbRows;
                    colMin = colSplit + cellsize;
                    colMax = nbCols;
                    break;
            }
        }
        
        // run through raster 
        private void startRunner() {
            PointChecker checkObj;
            

            for (int size = 4; size <= 20; size++) {
                cellsize = size;
                this.setBounds(indexRunner);
                
                for (int i = rowMin; i < rowMax; i++) {
                    for (int j = colMin; j < colMax; j++) {
                        // create triangles on specific index --> PointChecker
                        checkObj = new PointChecker(i, j);
                        checkObj.checkIndexes();
                        if(checkObj.isWithinRange()) {
                            j += cellsize - 1;
                        }
                    }
                    System.out.println("Size: " + size + ", Line " + i);
                }
            }
        }
    
    }
    
    //***********************************
    private class PointChecker {

        // class attributes
        int row;
        int col;
        // indices
        int[][] pointIndT1;
        int[][] pointIndT2;
        int[][] pointIndT3;
        int[][] pointIndT4;
        int[][] edgeIndT1;
        int[][] edgeIndT2;
        int[][] edgeIndT3;
        int[][] edgeIndT4;
        // triangles
        Triangle triangle1;
        Triangle triangle2;
        Triangle triangle3;
        Triangle triangle4;
        // boolean: is within tolerance
        boolean isWithinT1;
        boolean isWithinT2;
        boolean isWithinT3;
        boolean isWithinT4;

        // CONSTRUCTOR
        PointChecker(int row, int col) {
            this.row = row;
            this.col = col;

            // size of index (points inside trinagle)
            // segment has size (n+1) -> Elements (n+1)(n+2)/2 minus edges
            int nbElements = (cellsize + 1) * (cellsize + 2) / 2 - 3;
            pointIndT1 = new int[2][nbElements];
            pointIndT2 = new int[2][nbElements];
            pointIndT3 = new int[2][nbElements];
            pointIndT4 = new int[2][nbElements];
            edgeIndT1 = new int[2][3];
            edgeIndT2 = new int[2][3];
            edgeIndT3 = new int[2][3];
            edgeIndT4 = new int[2][3];

            isWithinT1 = false;
            isWithinT2 = false;
            isWithinT3 = false;
            isWithinT4 = false;
            
            // create indices & triangles
            createEdgeIndices();
            createPointIndices();
            createTriangles();
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
            // check points inside traingle 1
            isWithinT1 = checkPtsInTriangle(triangle1, pointIndT1);
            // check points inside traingle 2
            isWithinT2 = checkPtsInTriangle(triangle2, pointIndT2);

            // if T1 and T2 are within tolerance, T3 and T4 are obsolete
            // if: remove these points
            // else: check T3 and T4
            if (isWithinT1 && isWithinT2) {
                removePoints(pointIndT1);       // remove points in T1
                resetPoints(edgeIndT1);         // reset edges T1
                removePoints(pointIndT1);       // remove points in T2
                resetPoints(edgeIndT1);         // reset edges T2
            } else {
                // check points inside traingle 3
                isWithinT3 = checkPtsInTriangle(triangle3, pointIndT3);
                // check points inside traingle 4
                isWithinT4 = checkPtsInTriangle(triangle4, pointIndT4);

                // if T3 and T4 are within tolerance
                // if: remove these points
                // else: check if one triangle is within range
                if (isWithinT3 && isWithinT4) {
                    removePoints(pointIndT3);       // remove points in T3
                    resetPoints(edgeIndT3);         // reset edges T3
                    removePoints(pointIndT4);       // remove points in T4
                    resetPoints(edgeIndT4);         // reset edges T4
                } else {
                    // check if T1 OR T2 are within tolerance
                    // else: check if T3 OR T4 are within tolerance
                    if (isWithinT1 || isWithinT2) {
                        if (isWithinT1) {
                            removePoints(pointIndT1);       // remove points in T1
                            resetPoints(edgeIndT1);         // reset edges T1
                        } else {
                            removePoints(pointIndT1);       // remove points in T2
                            resetPoints(edgeIndT1);         // reset edges T2
                        }
                    } else if (isWithinT3 || isWithinT4) {
                        if (isWithinT3) {
                            removePoints(pointIndT3);       // remove points in T3
                            resetPoints(edgeIndT3);         // reset edges T3
                        } else {
                            removePoints(pointIndT4);       // remove points in T4
                            resetPoints(edgeIndT4);         // reset edges T4
                        }
                    }
                }
            }
        }

        // test points inside triangle
        private boolean checkPtsInTriangle(Triangle tri, int[][] ind) {
            double dist;

//            tri.printEdges();           //TEST
            int nbElements = (cellsize + 1) * (cellsize + 2) / 2 - 3;

            for (int i = 0; i < nbElements; i++) {

                // check points
                Point pt = RasterManager.mainRaster.getPoint(ind[0][i], ind[1][i]);
//                pt.printPt();     // TEST
                dist = tri.distPoint(pt);

                if (Math.abs(dist) > tolerance) {
//                    System.out.println("dist: " + dist + " in element " + i);     //TEST
                    return false;
                }
            }
//            System.out.println("within distance");        //TEST
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
        
        // GETTER
        private boolean isWithinRange() {
            return isWithinT1 || isWithinT2 || isWithinT3 || isWithinT4;
        }

    }
    
}
