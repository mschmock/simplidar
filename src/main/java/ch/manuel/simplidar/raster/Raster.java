//Autor: Manuel Schmocker
//Datum: 19.02.2021
package ch.manuel.simplidar.raster;

import ch.manuel.simplidar.calculation.Point;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class Raster {

    // class attributes
    private int nbCols;
    private int nbRows;
    private int xCorner;        // lower left corner: x-value
    private int yCorner;        // lower left corner: y-value
    private double cellsize;
    private int nodata_val;

    // raster data
    private double[][] raster;

    // CONSTRUCTOR
    public Raster() {

    }

    // PUBLIC FUNCTIONS
    // Instantiating the raster (if data ok)
    public boolean initRaster() {
        if (checkBounds()) {
            this.raster = new double[nbRows][nbCols];
            return true;
        } else {
            return false;
        }
    }

    // set value of element x,y 
    public void setElement(int x, int y, double val) {
        this.raster[x][y] = val;
    }

    // coordinate X für column-index
    public double getCoord_X(int column) {
        return 1.0 * xCorner + cellsize * column;
    }

    // coordinate Y für column-index
    public double getCoord_Y(int row) {
        return 1.0 * yCorner + cellsize * (nbRows - row - 1);
    }

    // get coordinates for triange-points
    public Point getPoint(int cols, int rows) {
        double x = this.getCoord_X(cols);
        double y = this.getCoord_Y(rows);
        double z = this.raster[rows][cols];

        return new Point(x, y, z);
    }

    // get normal of a cluster (by linear regression)
    // A * u = b
    public RealVector normalOfCluster(int x, int y, int size) {
        double[][] AA = new double[size * size][3];
        double[] bb = new double[size * size];
        int nb = 0;

        try {
            for (int i = x; i < (x + size - 1); i++) {
                for (int j = y; j < (j + size - 1); j++) {
                    AA[nb][0] = this.getCoord_X(i);
                    AA[nb][1] = this.getCoord_Y(j);
                    AA[nb][2] = 1.0;
                    bb[nb] = this.raster[i][j];
                    nb++;
                }
                
                
            }
            
            RealMatrix A = new Array2DRowRealMatrix( AA, false );
            RealVector b = new ArrayRealVector( bb, false);
            
            return this.solveSystem(A, b);
        } catch (ArrayIndexOutOfBoundsException ex) {
            return null;
        }
    }

    // SETTER
    public void setNbCols(int number) {
        this.nbCols = number;
    }

    public void setNbRows(int number) {
        this.nbRows = number;
    }

    public void setXLLcorner(int number) {
        this.xCorner = number;
    }

    public void setYLLcorner(int number) {
        this.yCorner = number;
    }

    public void setCellsize(double size) {
        this.cellsize = size;
    }

    public void setNoDataVal(int val) {
        this.nodata_val = val;
    }

    // GETTER
    public int getNbCols() {
        return this.nbCols;
    }

    public int getNbRows() {
        return this.nbRows;
    }

    public int getXLLcorner() {
        return this.xCorner;
    }

    public int getYLLcorner() {
        return this.yCorner;
    }

    public double getCellsize() {
        return this.cellsize;
    }

    public int getNoDataVal() {
        return this.nodata_val;
    }

    // PRIVATE FUNCTIONS
    // check, if bounds are ok
    private boolean checkBounds() {
        if ((this.nbCols > 0) && (this.nbRows > 0)) {
            return true;
        } else {
            return false;
        }
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
    private  RealVector solveSystem(RealMatrix A, RealVector b) {
        RealMatrix ATA = A.transpose().multiply(A);
        RealVector ATb = A.transpose().operate( b );
        
        DecompositionSolver solver = new LUDecomposition( ATA ).getSolver();
        return solver.solve( ATb );

    }
 
}
