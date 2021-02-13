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

public class DataLoader {
    
    // class attributes
    private static final Charset utf8 = StandardCharsets.UTF_8;
    private static File ascFile;
    
    // load JSON: geodata (Grenzen)
    private boolean loadASC() {
        
// Error message
        String errMsg = "All OK";
        boolean hasErr = false;
        
        // set bufferedReader
        try {
            FileInputStream fis = new FileInputStream(DataLoader.ascFile);
            InputStreamReader isr = new InputStreamReader(fis, utf8);
            BufferedReader br = new BufferedReader(isr);
            
            // read line by line
            String line;
            int nbLines = 0;
            
            while( (line = br.readLine()) != null ){
                // read line
                line = br.readLine();
                nbLines++;
                
                //process the line
                System.out.println(line);
                
                
            }
            br.close();
                    
        } catch (FileNotFoundException e) {
            hasErr = true;
            errMsg = "Datei nicht gefunden!";
        } catch (IOException e) {
            hasErr = true;
            errMsg = e.getMessage();
        }
        
        // print error-message
        if( hasErr ) {
            MyUtilities.getErrorMsg("Error", errMsg);
            return false;
        } else {
            // no errors loading data
            return true;
        }

    }
    
    // set path to asc-file
    public static void loadAscFile() {
        String path = MyUtilities.getOpenFileDialog(new java.awt.Frame(), "Datei öffnen", "D:\\", "*.asc");
        DataLoader.ascFile = new File( path );
        // check file header
        DataLoader.checkHeader();
    }
    
    // check header of file
    private static void checkHeader() {
        
        // header from file
        String header = "";
        
        // Error message
        String errMsg = "All OK";
        boolean hasErr = false;
        
        // set bufferedReader
        try {
            FileInputStream fis = new FileInputStream(DataLoader.ascFile);
            InputStreamReader isr = new InputStreamReader(fis, utf8);
            BufferedReader br = new BufferedReader(isr);
            
            // read line by line
            String line;
            int nbLines = 0;
            
            // first line
            line = br.readLine();
            header = line;
            
            // line by line
            while( (line != null ) && ( nbLines < 5 ) ){
                // read & process the line
                header += "\n" + br.readLine();
                nbLines++;
            }
            br.close();
                    
        } catch (FileNotFoundException e) {
            hasErr = true;
            errMsg = "Datei nicht gefunden!";
        } catch (IOException e) {
            hasErr = true;
            errMsg = e.getMessage();
        }
        
        // show text in gui
        MainFrame.setText(header);
        
//        // print error-message
//        if( hasErr ) {
//            MyUtilities.getErrorMsg("Error", errMsg);
//            return false;
//        } else {
//            // no errors loading data
//            return true;
//        }
    }
}
