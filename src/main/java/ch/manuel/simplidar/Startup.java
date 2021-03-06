//Autor: Manuel Schmocker
//Datum: 13.02.2021


package ch.manuel.simplidar;

import ch.manuel.simplidar.gui.MainFrame;
import ch.manuel.simplidar.raster.DataManager;
import ch.manuel.utilities.MyUtilities;


// Main Class
public class Startup {
    
    // class attributes
    private static MainFrame mainFrame;
    private static DataManager rasterData;
    private static String defaultPath;
    
    public static void main(String[] args) {
        
        // Create Data Object
        rasterData = new DataManager();
        
        // Set Look and Feel
        MyUtilities.setLaF("Windows");
        
        // define default path
        loadDefaultPath();
        
         /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            }
        });
    }
    
    // get default path
    public static String getDefaultPath() {
        return Startup.defaultPath;
    }
    
    // PRIVATE FUNCTIONS
    // load from resources
    private static void loadDefaultPath() {
        Startup.defaultPath = MyUtilities.fileFromRes();
    }
}
