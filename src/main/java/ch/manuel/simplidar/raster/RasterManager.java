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

    // add raster (header info) to frame
    public static void addFileToRaster() {
        // select file
        File file = openFileDialog();
        if (file != null) {
            String extAsc = "asc";
            String extTiff = "tif";
            // prepare raster
            Raster raster = new Raster();

            // open asc-file
            if (extAsc.equals(getFileExtension(file))) {
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
            if (extTiff.equals(getFileExtension(file))) {
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
        for (int i = 0; i < nb; i++) {
            txt += "\n" + (i + 1) + ": " + listRaster.get(i).file.getName();
        }
        return txt;
    }

    // load data from files
    public static void loadDataFromFiles() {
        boolean isOK = false;
        // check integrity of files
        if (getNumberOfRasters() > 0) {
            isOK = checkIntegrity();
            
            if( isOK ) {
                int nb = getNumberOfRasters();
                for (int i = 0; i < nb; i++) {
                    listRaster.get(i).raster.initRaster();         // init raster
                    
                    String extAsc = "asc";
                    String extTiff = "tif";
                    File file = listRaster.get(i).file;
                    Raster raster = listRaster.get(i).raster;
                    
                    // open asc-file
                    if (extAsc.equals(getFileExtension(file))) {
                        LoaderASC loadObjAsc = new LoaderASC(raster);
                        loadObjAsc.setFile(file);
                        loadObjAsc.openFile();                  // read data from file
                    }
                    // open tif-file
                    if (extTiff.equals(getFileExtension(file))) {
                        LoaderTiff loadObjTif = new LoaderTiff(raster);
                        loadObjTif.setFile(file);
                        loadObjTif.openFile();                  // read data from file
                    }
                }
            }
        }
    }

    // clear list
    public static void clearElements() {
        listRaster.clear();
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
        
        return (path != null) ? new File(path) : null;
    }

    // get extension from file
    private static String getFileExtension(File file) {
        return FilenameUtils.getExtension(file.getName());
    }

    // check / recalculate bounds
    private static void calcBounds() {
        int nb = getNumberOfRasters();
        
        if (nb > 0) {
            xMin = listRaster.get(0).raster.getXmin();
            xMax = listRaster.get(0).raster.getXmax();
            yMin = listRaster.get(0).raster.getYmin();
            yMax = listRaster.get(0).raster.getYmax();
            
            if (nb > 1) {
                for (int i = 1; i < nb; i++) {
                    xMin = listRaster.get(i).raster.getXmin() < xMin ? listRaster.get(i).raster.getXmin() : xMin;
                    xMax = listRaster.get(i).raster.getXmax() > xMax ? listRaster.get(i).raster.getXmax() : xMax;
                    yMin = listRaster.get(i).raster.getYmin() < yMin ? listRaster.get(i).raster.getYmin() : yMin;
                    yMax = listRaster.get(i).raster.getYmax() > yMax ? listRaster.get(i).raster.getYmax() : yMax;
                }
            }
        }
        
    }

    // check integrity of rasters in list
    private static boolean checkIntegrity() {
        String msg = "Pr\u00fcfung der Daten:";
        // all raster same cellsize?
        boolean isOK = true;
        int nb = getNumberOfRasters();
        double cellsize = listRaster.get(0).raster.getCellsize();
        
        for (int i = 0; i < nb; i++) {
            double cs = listRaster.get(i).raster.getCellsize();
            // diff max tolerated: 0.1 mm
            if (Math.abs(cs - cellsize) > 0.0001) {
                isOK = false;
                msg += "\nAbweichende Zellgr\u00f6sse!";
            }
        }

        // check if bounds match eatch other
        boolean isOK2 = true;
        boolean northOK, eastOK, southOK, westOK;
        for (int i = 0; i < nb; i++) {
            // check North
            double north = listRaster.get(i).raster.getXmax();
            northOK = compareBounds(north, i, 1);
            // check East
            double east = listRaster.get(i).raster.getYmax();
            eastOK = compareBounds(east, i, 2);
            // check North
            double south = listRaster.get(i).raster.getXmin();
            southOK = compareBounds(south, i, 3);
            // check North
            double west = listRaster.get(i).raster.getYmin();
            westOK = compareBounds(west, i, 4);
            
            if (!(northOK && eastOK && southOK && westOK)) {
                isOK2 = false;
                msg += "\nL\u00fccken zwischen Raster!";
            }
        }
        // show result
        if (isOK && isOK2) {
            msg += "\nDaten OK!";
        }
        RasterFrame.setText(msg);
        
        return (isOK && isOK2);
    }

    /*
    1: compare with north
    2: compare with east
    3: compare with south
    4: compare with west
     */
    private static boolean compareBounds(double valBound, int i, int face) {
        int nb = getNumberOfRasters();
        double valBound2 = 0;
        boolean boundOK = false;

        // Step 1: check if global bound
        switch (face) {
            case 1:
                valBound2 = RasterManager.xMax;
                break;
            case 2:
                valBound2 = RasterManager.yMax;
                break;
            case 3:
                valBound2 = RasterManager.xMin;
                break;
            case 4:
                valBound2 = RasterManager.yMin;
                break;
        }
        
        if (Math.abs(valBound - valBound2) < 0.0001) {
            boundOK = true;

            // Step 2: if not, check other rasters
        } else {
            for (int j = 0; j < nb; j++) {
                if (i == j) {
                    continue;
                }
                
                switch (face) {
                    case 1:         // north -> compare to south
                        valBound2 = listRaster.get(j).raster.getXmin();
                        break;
                    case 2:         // east -> compare to west
                        valBound2 = listRaster.get(j).raster.getYmin();
                        break;
                    case 3:         // south -> compare to nord
                        valBound2 = listRaster.get(j).raster.getXmax();
                        break;
                    case 4:         // west -> compare to east
                        valBound2 = listRaster.get(j).raster.getYmax();
                        break;
                }
                if (Math.abs(valBound - valBound2) < 0.0001) {
                    System.out.println("Compare raster " + i + " with " +j);
                    System.out.println(i + ": " + valBound + ", j: " + valBound2);
                    boundOK = true;
                    break;
                }
            }
        }
        
        return boundOK;        
    }

    // ******************************************
    // INNER CLASS:
    private static class ListElement {

        // raster objects
        private Raster raster;
        private File file;
        private double offsetX;
        private double offsetY;
        
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
