//Autor: Manuel Schmocker
//Datum: 13.02.2021

package ch.manuel.simplidar.raster;

import ch.manuel.simplidar.LoaderASC;
import ch.manuel.simplidar.LoaderTiff;
import ch.manuel.simplidar.calculation.RasterAnalyser;
import ch.manuel.simplidar.gui.MainFrame;
import java.util.ArrayList;
import java.util.List;


// Data-holder for rasters
public class RasterManager {

    // raster objects
    public static Raster mainRaster;            // main raster
    private static List<Raster> listRaster;     // Liste mit Gemeinden: Klasse Municipality.java

    // rasterAnalyser object
    public static RasterAnalyser analyser;
    
    // CONSTRUCTOR
    public RasterManager() {
        // datas from raster
        mainRaster = new Raster();
        listRaster = new ArrayList<>();
        // analyser
        analyser = new RasterAnalyser();
    }
    
    // PUBLIC FUNCTIONS
    // load raster from asc-file
    public static void loadMainRasterFromAsc() {
        LoaderASC loadObjAsc = new LoaderASC(mainRaster);
        boolean isOK;
        isOK = loadObjAsc.getHeader();      // load header
        if(isOK) {
            MainFrame.showRasterValues();       // show header datas
            isOK = mainRaster.initRaster();     // init raster
            if(isOK) {
                loadObjAsc.openFile();          // read data from file
            } else {
                // show text in gui
                MainFrame.setText("Datenfeld Raster kann nicht initialisiert werden");
            }
        }
    }
    
    // load raster from tiff-file
    public static void loadMainRasterFromTiff() {
        LoaderTiff loadObjTif = new LoaderTiff(mainRaster);
        boolean isOK;
        isOK = loadObjTif.getHeader();      // load header
        if(isOK) {
            MainFrame.showRasterValues();       // show header datas
            isOK = mainRaster.initRaster();     // init raster
            if(isOK) {
                loadObjTif.openFile();          // read data from file
            } else {
                // show text in gui
                MainFrame.setText("Datenfeld Raster kann nicht initialisiert werden");
            }
        }
    }
            
    // PRIVATE FUNCTIONS
     public static void createRaster() {
        listRaster.add( new Raster() );
        
    }
}
