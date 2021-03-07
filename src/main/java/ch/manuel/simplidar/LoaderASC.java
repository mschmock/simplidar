//Autor: Manuel Schmocker
//Datum: 13.02.2021
package ch.manuel.simplidar;

import ch.manuel.simplidar.raster.DataManager;
import ch.manuel.simplidar.gui.MainFrame;
import ch.manuel.utilities.MyUtilities;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import javax.swing.filechooser.FileNameExtensionFilter;

// load lidar-datas from asc-file
public class LoaderASC implements Runnable {

    // class attributes
    private static final Charset utf8 = StandardCharsets.UTF_8;
    private static File ascFile;
    private static int nbRowsHeader;
    // check header & file: -> return true if ok
    private static boolean[] statusHeader;
    private static boolean fileOK;

    // ABSTRACT METHODES
    @Override
    public void run() {
        // load
        LoaderASC.openAscFile();
    }

    // PUBLIC FUNCTIONS
    
    // PRIVATE FUNCTIONS
    // procedure to open file
    public static void openAscFile() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("ASC Files", "asc");
        String defPath = DataLoader.getXMLdata("defaultPath");
        String path = MyUtilities.getOpenFileDialog("Datei öffnen", defPath, filter);
        if (path != null) {
            // set file path
            LoaderASC.ascFile = new File(path);
            // reset file status
            LoaderASC.resetHeaderStatus();
            // check file header
            LoaderASC.readHeader();
        }
        // continue, if header is OK
        if( getHeaderStatus() && fileOK) {
            // init Raster
            boolean isOK = DataManager.mainRaster.initRaster();
            if( isOK ) {
                // show header datas
                MainFrame.showRasterValues();
                // read file
                LoaderASC.readFile();
            } else {
                // show text in gui
                MainFrame.setText("Datenfeld Raster kann nicht initialisiert werden");
            }
        }
    }
    
    // reset header status
    private static void resetHeaderStatus() {
        LoaderASC.statusHeader = new boolean[] {false, false, false, false, false, false};
        fileOK = false;
        nbRowsHeader = 0;
    }
    
    // get status header
    private static boolean getHeaderStatus() {
        return !Arrays.asList(statusHeader).contains(false);
    }
    
    // ____READ FILE: 1. ONLY HEADER
    // check header of file
    private static void readHeader() {
        // status message
        String statusMsg = "Header OK";
        fileOK = true;

        // set bufferedReader
        try {
            FileInputStream fis = new FileInputStream(LoaderASC.ascFile);
            InputStreamReader isr = new InputStreamReader(fis, utf8);
            BufferedReader br = new BufferedReader(isr);

            String line = null;
            int nbLines = 0;

            // line by line
            while (((line = br.readLine()) != null) && (nbLines < 5)) {

                // read & process the line
                readHeaderLine(line);
                nbLines++;
            }
            br.close();
            
            // set number of rows in header
            nbRowsHeader = nbLines;

        } catch (FileNotFoundException e) {
            fileOK = false;
            statusMsg = "Datei nicht gefunden!";
        } catch (IOException e) {
            fileOK = false;
            statusMsg = e.getMessage();
        }
        // calculate bounds from  xllcorner / yllcorner 
        if( fileOK ) {
            DataManager.mainRaster.calcBounds();
        }
        // show text in gui
        MainFrame.setText(statusMsg);
    }

    // ____READ FILE: 2. WHOLE FILE
    // read file
    private static void readFile() {
        // Error message
        String statusMsg = "Alles OK";
        // set bufferedReader
        try {
            FileInputStream fis = new FileInputStream(LoaderASC.ascFile);
            InputStreamReader isr = new InputStreamReader(fis, utf8);
            BufferedReader br = new BufferedReader(isr);
            
            String line = null;
            int nbLines = 0;
  
            // read the whole file
            int nbLinesExp = DataManager.mainRaster.getNbRows();

            // line by line
            while ((line = br.readLine()) != null) {
                String[] substr;
                substr = line.split(" ");
                
                //skip lines of header: non numeric value in pos '0'
                if (MyUtilities.isNumeric(substr[0])) {
                    // check expected nb of elements
                    if (substr.length == DataManager.mainRaster.getNbCols()) {
                        for (int i = 0; i < substr.length; i++) {
                            DataManager.mainRaster.setElement(nbLines, i, Double.parseDouble(substr[i]));
                        }
                    }
                    // show progress
                    MainFrame.setText("Fortschritt: " + (int) (++nbLines * 100.0 / nbLinesExp) + " %");
                }
            }
            
            statusMsg += "\nLinien geladen: " + nbLines;
            br.close();
        
            if (nbLines != DataManager.mainRaster.getNbRows()) {
                // error
                fileOK = false;
                statusMsg = "Datei ist nicht korrekt formatiert!";
            } else {
                statusMsg += "\nLaden abgeschlossen.";
            }

        } catch (FileNotFoundException e) {
            statusMsg = "Datei nicht gefunden!";
        } catch (IOException e) {
            statusMsg = e.getMessage();
        }

        // show text in gui
        MainFrame.setText(statusMsg);
    }

    // read & check line of header
    private static void readHeaderLine(String line) {
        /*
        Header Format:
                ncols 8750
                nrows 6000
                xllcorner 2628750
                yllcorner 1173000
                cellsize 0.5
                nodata_value 0
         */

        // check for "ncols"
        // System.out.println( line + ": " + line.contains("ncols") );
        if (line.contains("ncols")) {
            // check value: Numeric int value on 2. position
            int val = testLnInt(line);
            // if value ok -> write to class RasterData
            if (val != 0) {
                DataManager.mainRaster.setNbCols(val);
                statusHeader[0] = true;
            }
        }
        // check for "nrows"
        if (line.contains("nrows")) {
            // check value: Numeric int value on 2. position
            int val = testLnInt(line);
            // if value ok -> write to class RasterData
            if (val != 0) {
                DataManager.mainRaster.setNbRows(val);
                statusHeader[1] = true;
            }
        }
        // check for "xllcorner"
        if (line.contains("xllcorner")) {
            // check value: Numeric int value on 2. position
            int val = testLnInt(line);
            // if value ok -> write to class RasterData
            if (val != 0) {
                DataManager.mainRaster.setXLLcorner(val);
                statusHeader[2] = true;
            }
        }
        // check for "yllcorner"
        if (line.contains("yllcorner")) {
            // check value: Numeric int value on 2. position
            int val = testLnInt(line);
            // if value ok -> write to class RasterData
            if (val != 0) {
                DataManager.mainRaster.setYLLcorner(val);
                statusHeader[3] = true;
            }
        }
        // check for "cellsize"
        if (line.contains("cellsize")) {
            // check value: Numeric int value on 2. position
            double val = testLnDouble(line);
            // if value ok -> write to class RasterData
            if (val != 0) {
                DataManager.mainRaster.setCellsize(val);
                statusHeader[4] = true;
            }
        }
        // check for "nodata_value"
        if (line.contains("nodata_value")) {
            // check value: Numeric int value on 2. position
            int val = testLnInt(line);
            // if value ok -> write to class RasterData
            if (val != 0) {
                DataManager.mainRaster.setNoDataVal(val);
                statusHeader[5] = true;
            }
        }
    }

    // returns null, if input is not ok
    private static int testLnInt(String str) {
        String[] substr;
        substr = str.split(" ");

        // 2. Element: int with nb of lines
        if (!MyUtilities.isInteger(substr[1])) {
            return 0;
        }

        // all OK
        return Integer.parseInt(substr[1]);
    }

    // returns null, if input is not ok
    private static double testLnDouble(String str) {
        String[] substr;
        substr = str.split(" ");

        // 2. Element: int with nb of lines
        if (!MyUtilities.isNumeric(substr[1])) {
            return 0;
        }

        // all OK
        return Double.parseDouble(substr[1]);
    }
}
