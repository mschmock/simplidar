//Autor: Manuel Schmocker
//Datum: 13.02.2021


package ch.manuel.simplidar.raster;


/*
Header Format:
    ncols 8750
    nrows 6000
    xllcorner 2628750
    yllcorner 1173000
    cellsize 0.5
    nodata_value 0
*/
public class DataManager {
    
    
    // raster objects
    public static Raster mainRaster;           // main raster
    public static Raster subRaster_1;          // sub raster (merge procedure)
    public static Raster subRaster_2;          // sub raster (merge procedure)
    public static Raster subRaster_3;          // sub raster (merge procedure)
    public static Raster subRaster_4;          // sub raster (merge procedure)
    
    // Constructor
    public DataManager() {
        mainRaster = new Raster();
        subRaster_1 = new Raster();
        subRaster_2 = new Raster();
        subRaster_3 = new Raster();
        subRaster_4 = new Raster();
    }
    


  
    

}
