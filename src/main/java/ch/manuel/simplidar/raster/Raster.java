//Autor: Manuel Schmocker
//Datum: 19.02.2021
package ch.manuel.simplidar.raster;

import ch.manuel.simplidar.calculation.Point;

// data-holder for raster
public class Raster {

    // class attributes
    private int nbCols;
    private int nbRows;
    private double cellsize;
    private int nodata_val;
    // bounds
    private double xMax;
    private double yMax;
    private double xMin;
    private double yMin;

    // raster data
    private double[][] raster;
    private boolean[][] rasterBool;

    // CONSTRUCTOR
    public Raster() {

    }

    // PUBLIC FUNCTIONS
    // Instantiating the raster (if data ok)
    public boolean initRaster() {
        if (checkBounds()) {
            this.raster = new double[nbRows][nbCols];
            this.rasterBool = null;
            return true;
        } else {
            return false;
        }
    }

    // initialise boolean raster
    public void initBoolRaster() {
        this.rasterBool = new boolean[nbRows][nbCols];
        for (int i = 0; i < nbRows; i++) {
            for (int j = 0; j < nbCols; j++) {
                this.rasterBool[i][j] = true;
            }
        }
    }

    // set value of element x,y 
    public void setElement(int y, int x, double val) {
        this.raster[y][x] = val;
    }

    // set value of boolRaster
    public void setBoolElement(int y, int x, boolean val) {
        this.rasterBool[y][x] = val;
    }

    // coordinate X für column-index
    public double getCoord_X(int column) {
        return 1.0 * xMin + cellsize * column;
    }

    // coordinate Y für column-index
    public double getCoord_Y(int row) {
        return 1.0 * yMin + cellsize * (nbRows - row - 1);
    }

    // get coordinates for triange-points
    public Point getPoint(int cols, int rows) {
        double x = this.getCoord_X(cols);
        double y = this.getCoord_Y(rows);
        double z = this.raster[rows][cols];

        return new Point(x, y, z);
    }
  
    // calculate bounds xMax / yMax from xMin / yMin and cellsize
    public void calcBounds() {
        xMax = xMin + cellsize * nbCols;
        yMax = yMin + cellsize * nbRows;
    }

    // SETTER
    public void setNbCols(int number) {
        this.nbCols = number;
    }

    public void setNbRows(int number) {
        this.nbRows = number;
    }

    public void setCellsize(double size) {
        this.cellsize = size;
    }

    public void setNoDataVal(int val) {
        this.nodata_val = val;
    }

    public void setBounds(double xMin, double xMax, double yMin, double yMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    public void setXLLcorner(double xllcorner) {
        this.xMin = xllcorner;
    }

    public void setYLLcorner(double yllcorner) {
        this.yMin = yllcorner;
    }

    // GETTER
    public int getNbCols() {
        return this.nbCols;
    }

    public int getNbRows() {
        return this.nbRows;
    }

    public double getXmin() {
        return this.xMin;
    }

    public double getXmax() {
        return this.xMax;
    }

    public double getYmin() {
        return this.yMin;
    }

    public double getYmax() {
        return this.yMax;
    }

    public double getCellsize() {
        return this.cellsize;
    }

    public int getNoDataVal() {
        return this.nodata_val;
    }

    public double getElement(int rows, int cols) {
        return this.raster[rows][cols];
    }

    public boolean getElementBool(int rows, int cols) {
        return this.rasterBool[rows][cols];
    }
      
    // get number of valid points in simp raster
    public int getNumberPoints() {
        int count = 0;
        for (int i = 0; i < nbRows; i++) {
            for (int j = 0; j < nbCols; j++) {
                if( this.rasterBool[i][j]) {
                    count++;
                }
            }
        }
        return count;
    }
    
    // get number of pixels
    public int getNbPixels() {
        return nbRows * nbCols;
    }

    // PRIVATE FUNCTIONS
    // check, if bounds are ok
    private boolean checkBounds() {
        return (this.nbCols > 0) && (this.nbRows > 0);
    }

}
