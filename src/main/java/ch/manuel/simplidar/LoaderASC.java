//Autor: Manuel Schmocker
//Datum: 13.02.2021
package ch.manuel.simplidar;

import ch.manuel.simplidar.gui.MainFrame;
import ch.manuel.simplidar.raster.Raster;
import ch.manuel.utilities.MyUtilities;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileNameExtensionFilter;

// load lidar-datas from asc-file
public class LoaderASC {

    // class attributes
    private static final Charset utf8 = StandardCharsets.UTF_8;
    private File ascFile;
    private final Raster raster;
//    private int nbRowsHeader;
    // load thread and object
    private Thread t1;                  // LOAD file thread
    private LoadThread loader;          // subclass with runnable --> load file
    // check header & file: -> return true if ok
    private boolean[] statusHeader;
    private boolean fileOK;

    // CONSTRUCTOR
    public LoaderASC(Raster raster) {
        this.raster = raster;
        loader = new LoadThread();
    }

    // PUBLIC FUNCTIONS
    // get header informations
    public boolean getHeader() {
        // reset file status
        resetHeaderStatus();
        if (this.ascFile != null) {
            readHeader();
        } else {
            // no file defined
            fileOK = false;
            // show text in gui
            MainFrame.setText("keine Datei ausgew\u00e4hlt");
        }
        return (getHeaderStatus() && fileOK);
    }
    
    // open file in thread
    public void openFile() {
        t1 = new Thread(loader);
        t1.start();
    }
    
    // wait for thread
    public void joinThread() {
        try {
            t1.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(LoaderASC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // set file
    public void setFile(File file) {
        this.ascFile = file;
    }
    
    // open file chooser
    public void chooseFile() {
        openFileDialog();
    }
    
    // PRIVATE FUNCTIONS
    // open file dialog
    private void openFileDialog() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("ASC Files", "asc");
        String defPath = DataLoader.getXMLdata("defaultPath");
        File file = MyUtilities.getOpenFileDialog("Datei \u00d6ffnen", defPath, filter);
        if (file != null) {
            // set file path
            this.ascFile = file;
        }
    }

    // reset header status
    private void resetHeaderStatus() {
        statusHeader = new boolean[]{false, false, false, false, false, false};
        fileOK = false;
//        nbRowsHeader = 0;
    }

    // get status header
    private boolean getHeaderStatus() {
        for(boolean b : statusHeader) if(!b) return false;
        return true;
    }

    // ____READ FILE: 1. ONLY HEADER
    // check header of file
    private void readHeader() {
        // status message
        String statusMsg = "Header OK";
        fileOK = true;

        // set bufferedReader
        try {
            FileInputStream fis = new FileInputStream(ascFile);
            InputStreamReader isr = new InputStreamReader(fis, utf8);
            BufferedReader br = new BufferedReader(isr);

            String line = null;
            int nbLines = 0;

            // line by line
            while (((line = br.readLine()) != null) && (nbLines < 6)) {

                // read & process the line
                readHeaderLine(line);
                nbLines++;
            }
            br.close();

            // set number of rows in header
//            nbRowsHeader = nbLines;

        } catch (FileNotFoundException e) {
            fileOK = false;
            statusMsg = "Datei nicht gefunden!";
        } catch (IOException e) {
            fileOK = false;
            statusMsg = e.getMessage();
        }
        // calculate bounds from  xllcorner / yllcorner 
        if (getHeaderStatus() && fileOK) {
            raster.calcBounds();
        } else {
            statusMsg = "Header nicht korrekt formatiert!";
        }
        // show text in gui
        MainFrame.setText(statusMsg);
    }

    // ____READ FILE: 2. WHOLE FILE
    // read file
    private void readFile() {
        // Error message
        String statusMsg = "Alles OK";
        // set bufferedReader
        try {
            FileInputStream fis = new FileInputStream(ascFile);
            InputStreamReader isr = new InputStreamReader(fis, utf8);
            BufferedReader br = new BufferedReader(isr);

            String line = null;
            int nbLines = 0;

            // read the whole file
            int nbLinesExp = raster.getNbRows();

            // line by line
            while ((line = br.readLine()) != null) {
                String[] substr;
                substr = line.split(" ");

                //skip lines of header: non numeric value in pos '0'
                if (MyUtilities.isNumeric(substr[0])) {
                    // check expected nb of elements
                    if (substr.length == raster.getNbCols()) {
                        for (int i = 0; i < substr.length; i++) {
                            raster.setElement(nbLines, i, Double.parseDouble(substr[i]));
                        }
                    }
                    // show progress
                    MainFrame.setText("Fortschritt: " + (int) (++nbLines * 100.0 / nbLinesExp) + " %");
                }
            }

            statusMsg += "\nLinien geladen: " + nbLines;
            br.close();

            if (nbLines != raster.getNbRows()) {
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
    private void readHeaderLine(String line) {
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
            String[] substr = line.split(" ");
            // if value ok -> write to class RasterData
            if (MyUtilities.isInteger(substr[1])) {
                int val = Integer.parseInt(substr[1]);
                raster.setNbCols(val);
                statusHeader[0] = true;
            }
        }
        // check for "nrows"
        if (line.contains("nrows")) {
            // check value: Numeric int value on 2. position
            String[] substr = line.split(" ");
            // if value ok -> write to class RasterData
            if (MyUtilities.isInteger(substr[1])) {
                int val = Integer.parseInt(substr[1]);
                raster.setNbRows(val);
                statusHeader[1] = true;
            }
        }
        // check for "xllcorner"
        if (line.contains("xllcorner")) {
            // check value: Numeric int value on 2. position
            String[] substr = line.split(" ");
            // if value ok -> write to class RasterData
            if (MyUtilities.isInteger(substr[1])) {
                int val = Integer.parseInt(substr[1]);
                raster.setXLLcorner(val);
                statusHeader[2] = true;
            }
        }
        // check for "yllcorner"
        if (line.contains("yllcorner")) {
            // check value: Numeric int value on 2. position
            String[] substr = line.split(" ");
            // if value ok -> write to class RasterData
            if (MyUtilities.isInteger(substr[1])) {
                int val = Integer.parseInt(substr[1]);
                raster.setYLLcorner(val);
                statusHeader[3] = true;
            }
        }
        // check for "cellsize"
        if (line.contains("cellsize")) {
            // check value: Numeric int value on 2. position
            String[] substr = line.split(" ");
            // if value ok -> write to class RasterData
            if (MyUtilities.isNumeric(substr[1])) {
                double val = Double.parseDouble(substr[1]);
                raster.setCellsize(val);
                statusHeader[4] = true;
            }
        }
        // check for "nodata_value"
        if (line.contains("nodata_value")) {
            // check value: Numeric int value on 2. position
            String[] substr = line.split(" ");
            // if value ok -> write to class RasterData
            if (MyUtilities.isInteger(substr[1])) {
                int val = Integer.parseInt(substr[1]);
                raster.setNoDataVal(val);
                statusHeader[5] = true;
            }
        }
    }

    //***********************************************************
    // inner class with separate thrad
    // loader for asc-file
    private class LoadThread implements Runnable {
        @Override
        public void run() {
            LoaderASC.this.readFile();
        }
    }

}