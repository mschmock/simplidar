//Autor: Manuel Schmocker
//Datum: 13.02.2021

package ch.manuel.simplidar.raster;

import ch.manuel.simplidar.calculation.RasterAnalyser;
import java.util.ArrayList;
import java.util.List;


// Data-holder for rasters
public class RasterManager {

    // raster objects
    public static Raster mainRaster;            // main raster
    private static List<Raster> listRaster;     // Liste mit Gemeinden: Klasse Municipality.java


    
    // rasterAnalyser object
    public static RasterAnalyser analyser;
    
    // Constructor
    public RasterManager() {
        // datas from raster
        mainRaster = new Raster();
        listRaster = new ArrayList<>();
        // analyser
        analyser = new RasterAnalyser();
    }




}
