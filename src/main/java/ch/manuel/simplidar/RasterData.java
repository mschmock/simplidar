//Autor: Manuel Schmocker
//Datum: 13.02.2021


package ch.manuel.simplidar;


/*
Header Format:
    ncols 8750
    nrows 6000
    xllcorner 2628750
    yllcorner 1173000
    cellsize 0.5
    nodata_value 0
*/
public class RasterData {
    
    // class attributes
    private static int nbCols;
    private static int nbRows;
    private static int xCorner;
    private static int yCorner;
    private static double cellsize;
    private static int nodata_val;
    
    private static double[][] raster;
    
    
    // Constructor
    public RasterData() {
        
    }
    
    // FUNCTIONS
    public static void initRaster() {
        RasterData.raster = new double[nbRows][nbCols];
    }
    public static void setElement(int x, int y, double val) {
        RasterData.raster[x][y] = val;
    }
  
    
    // SETTER
    public static void setNbCols(int number) {
        RasterData.nbCols = number;
    }
    
    public static void setNbRows(int number) {
        RasterData.nbRows = number;
    }
    
    public static void setXLLcorner(int number) {
        RasterData.xCorner = number;
    }
    
    public static void setYLLcorner(int number) {
        RasterData.yCorner = number;
    }
    
    public static void setCellsize(double size) {
        RasterData.cellsize = size;
    }
    
    public static void setNoDataVal(int val) {
        RasterData.nodata_val = val;
    }
    
    // GETTER
     public static int getNbCols() {
        return RasterData.nbCols;
    }
    public static int getNbRows() {
        return RasterData.nbRows;
    }
    public static int getXLLcorner() {
        return RasterData.xCorner;
    }
    public static int getYLLcorner() {
        return RasterData.yCorner;
    }
    public static double getCellsize() {
        return RasterData.cellsize;
    }
    public static int getNoDataVal() {
        return RasterData.nodata_val;
    }
}
