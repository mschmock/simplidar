//Autor: Manuel Schmocker
//Datum: 13.02.2021
package ch.manuel.simplidar.raster;

import ch.manuel.simplidar.DataLoader;
import ch.manuel.simplidar.LoaderASC;
import ch.manuel.simplidar.LoaderTiff;
import ch.manuel.simplidar.calculation.RasterAnalyser;
import ch.manuel.simplidar.gui.MainFrame;
import ch.manuel.simplidar.gui.RasterFrame;
import ch.manuel.utilities.MyUtilities;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.io.FilenameUtils;

// Data-holder for rasters
public class RasterManager {

    // raster objects
    public static Raster mainRaster;                // main raster
    // List with rasters --> merge
    private static List<ListElement> listRaster;    // list with rasters
    private static double xMin;                     // bound of list
    private static double xMax;                     // bound of list
    private static double yMin;                     // bound of list
    private static double yMax;                     // bound of list
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
        loadObjAsc.chooseFile();
        boolean isOK;
        isOK = loadObjAsc.getHeader();      // load header
        if (isOK) {
            MainFrame.showRasterValues();       // show header datas
            isOK = mainRaster.initRaster();     // init raster
            if (isOK) {
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
        loadObjTif.chooseFile();
        boolean isOK;
        isOK = loadObjTif.getHeader();      // load header
        if (isOK) {
            MainFrame.showRasterValues();       // show header datas
            isOK = mainRaster.initRaster();     // init raster
            if (isOK) {
                loadObjTif.openFile();          // read data from file
            } else {
                // show text in gui
                MainFrame.setText("Datenfeld Raster kann nicht initialisiert werden");
            }
        }
    }

    // add raster (header info) to fram
    public static void addFileToRaster() {
        // select file
        File file = openFileDialog();
        if(file != null) {
            String extAsc = "asc";
            String extTiff = "tif";
            // prepare raster
            Raster raster = new Raster();

            // open asc-file
            if(extAsc.equals(getFileExtension(file))) {
                LoaderASC loadObjAsc = new LoaderASC(raster);
                loadObjAsc.setFile(file);
                // load header
                boolean isOK;
                isOK = loadObjAsc.getHeader();
                if (isOK) {
                    listRaster.add(new ListElement(raster, file));
                }
            }
            // open tif-file
            if(extTiff.equals(getFileExtension(file))) {
                LoaderTiff loadObjTif = new LoaderTiff(raster);
                loadObjTif.setFile(file);
                // load header
                boolean isOK;
                isOK = loadObjTif.getHeader();
                if (isOK) {
                    listRaster.add(new ListElement(raster, file));
                }
            }
            // calculate bounds
            calcBounds();
            // show files in textarea
            RasterFrame.setText(getFileNames());
        }
    }
    
    public static String getFileNames() {
        String txt = "Geladene Dateien:";
        int nb = getNumberOfRasters();
        for(int i = 0; i < nb; i++) {
            txt += "\n" + (i+1) + ": " + listRaster.get(i).file.getName();
        }
        return txt;
    }
    

    // get number of rasters in list
    public static int getNumberOfRasters() {
        return listRaster.size();
    }

    // get element i in list
    public static Raster getRasterFromList(int index) {
        return listRaster.get(index).raster;
    }

    // get element i in list
    public static File getFileFromList(int index) {
        return listRaster.get(index).file;
    }
    
    public static double getBoundXmin() {
        return xMin;
    }
    public static double getBoundXmax() {
        return xMax;
    }
    public static double getBoundYmin() {
        return yMin;
    }
    public static double getBoundYmax() {
        return yMax;
    }

    // PRIVATE FUNCTIONS
    // open file dialog
    private static File openFileDialog() {
        FileNameExtensionFilter filterA = new FileNameExtensionFilter("ASC Files", "asc");
        FileNameExtensionFilter filterB = new FileNameExtensionFilter("geoTiff-Datei", "tif");
        FileNameExtensionFilter[] filters = {filterA, filterB};
        String defPath = DataLoader.getXMLdata("defaultPath");
        String path = MyUtilities.getOpenFileDialog("Datei öffnen", defPath, filters);

        return (path != null)? new File(path) : null;
    }

    // get extension from file
    private static String getFileExtension(File file) {
        return FilenameUtils.getExtension(file.getName());
    }
    
    // check / recalculate bounds
    private static void calcBounds() {
        int nb = getNumberOfRasters();
        
        if( nb > 0) {
            xMin = listRaster.get(0).raster.getXmin();
            xMax = listRaster.get(0).raster.getXmax();
            yMin = listRaster.get(0).raster.getYmin();
            yMax = listRaster.get(0).raster.getYmax();
            
            if( nb > 1) {
                for( int i = 1; i < nb; i++) {
                    xMin = listRaster.get(i).raster.getXmin() < xMin ? listRaster.get(i).raster.getXmin() : xMin;
                    xMax = listRaster.get(i).raster.getXmax() > xMax ? listRaster.get(i).raster.getXmax() : xMax;
                    yMin = listRaster.get(i).raster.getYmin() < yMin ? listRaster.get(i).raster.getYmin() : yMin;
                    yMax = listRaster.get(i).raster.getYmax() > yMax ? listRaster.get(i).raster.getYmax() : yMax;
                }
            }
        }

    }

    // ******************************************
    // INNER CLASS:
    private static class ListElement {

        // raster objects
        private Raster raster;      // 
        private File file;

        ListElement(Raster raster, File file) {
            this.raster = raster;
            this.file = file;
        }

        // GETTER
        File getFile() {
            return file;
        }

        Raster getRaster() {
            return raster;
        }

    }
}
