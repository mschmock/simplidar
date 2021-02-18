//Autor: Manuel Schmocker
//Datum: 13.02.2021


package ch.manuel.simplidar;

// load lidar-datas from asc-file

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

public class DataLoader implements Runnable{
    
    // class attributes
    private static final Charset utf8 = StandardCharsets.UTF_8;
    private static File ascFile;
    private static boolean fileOK;
    
    @Override
    public void run() {
        // load
        DataLoader.loadAscFile();
    }
    
    // set path to asc-file
    public static void selectAscFile() {
        String path = MyUtilities.getOpenFileDialog("Datei öffnen", "");
        if( path != null ) {
            DataLoader.ascFile = new File( path );
            // check file header
            DataLoader.readFile( true );
        }
        // show header datas
        MainFrame.showRasterValues();
    }
    
    // load file
    private static void loadAscFile() {
        // init Raster
        RasterData.initRaster();
        // load file
        DataLoader.readFile( false );
    }
    
    // check header of file
    private static void readFile( boolean onlyHeader ) {
               
        // Error message
        String errMsg = "Alles OK";
        fileOK = true;
        
        // set bufferedReader
        try {
            FileInputStream fis = new FileInputStream(DataLoader.ascFile);
            InputStreamReader isr = new InputStreamReader(fis, utf8);
            BufferedReader br = new BufferedReader(isr);
            
             String line = null;
             int nbLines = 0;
             
            // ____1. ONLY HEADER
            // only header: "onlyHeader == true"
            if( onlyHeader ) {

                // line by line
                while( ((line = br.readLine() ) != null ) && ( nbLines < 5 ) ){

                    // read & process the line
                    readHeader( line );
                    nbLines++;
                }
            
            // ____2. WHOLE FILE    
            // read the whole file
            } else {
                int nbLinesExp = RasterData.getNbRows();
                
                // line by line
                while( (line = br.readLine() ) != null ) {
                    String[] substr;
                    substr = line.split(" ");
                    
                    if( MyUtilities.isNumeric( substr[0] )) {
                        // check expected nb of elements
                        if( substr.length == RasterData.getNbCols() ) {
                            for( int i = 0; i < substr.length; i++) {
                                RasterData.setElement(nbLines, i, Double.parseDouble( substr[i] ));
                            }
                        }
                        // show process
                        MainFrame.setText("Fortschritt: " + (int)(++nbLines*100.0/nbLinesExp) + " %");
                    }
                    
                    if( nbLines != RasterData.getNbRows() ) {
                        // error
                        fileOK = false;
                        errMsg = "Datei ist nicht korrekt formatiert!";
                    }
                        
                }
            }
            
            br.close();
                    
        } catch (FileNotFoundException e) {
            fileOK = false;
            errMsg = "Datei nicht gefunden!";
        } catch (IOException e) {
            fileOK = false;
            errMsg = e.getMessage();
        }
        
        // show text in gui
        MainFrame.setText(errMsg);
    }
    
    // read header
    private static void readHeader(String line) {
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
        System.out.println( line + ": " + line.contains("ncols") );
        if( line.contains("ncols") ) {
            // check value: Numeric int value on 2. position
            int val = testLnInt( line );
            // if value ok -> write to class RasterData
            if( val != 0 ) {  
                RasterData.setNbCols( val );
            }
        }
        // check for "nrows"
        if( line.contains("nrows") ) {
            // check value: Numeric int value on 2. position
            int val = testLnInt( line );
            // if value ok -> write to class RasterData
            if( val != 0 ) {  
                RasterData.setNbRows( val );
            }
        }
        // check for "xllcorner"
        if( line.contains("xllcorner") ) {
            // check value: Numeric int value on 2. position
            int val = testLnInt( line );
            // if value ok -> write to class RasterData
            if( val != 0 ) { 
                RasterData.setXLLcorner( val );
            }
        }
        // check for "yllcorner"
        if( line.contains("yllcorner") ) {
            // check value: Numeric int value on 2. position
            int val = testLnInt( line );
            // if value ok -> write to class RasterData
            if( val != 0 ) { 
                RasterData.setYLLcorner( val );
            }
        }
        // check for "cellsize"
        if( line.contains("cellsize") ) {
            // check value: Numeric int value on 2. position
            double val = testLnDouble( line );
            // if value ok -> write to class RasterData
            if( val != 0 ) { 
                RasterData.setCellsize( val );
            }        
        }
        // check for "nodata_value"
        if( line.contains("nodata_value") ) {
            // check value: Numeric int value on 2. position
            int val = testLnInt( line );
            // if value ok -> write to class RasterData
            if( val != 0 ) { 
                RasterData.setNoDataVal( val );
            }
        }
        
    }
    
    // returns null, if input is not ok
    private static int testLnInt( String str ) {
        String[] substr;
        substr = str.split(" ");
        
        // 2. Element: int with nb of lines
        if( !MyUtilities.isInteger( substr[1] ) ) {
            return 0;
        }
        
        // all OK
        return Integer.parseInt( substr[1] );
    }
    
    // returns null, if input is not ok
    private static double testLnDouble( String str ) {
        String[] substr;
        substr = str.split(" ");
        
        // 2. Element: int with nb of lines
        if( !MyUtilities.isNumeric( substr[1] ) ) {
            return 0;
        }
        
        // all OK
        return Double.parseDouble( substr[1] );
    }
}
