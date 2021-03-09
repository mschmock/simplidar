//Autor: Manuel Schmocker
//Datum: 13.02.2021


package ch.manuel.simplidar;

import ch.manuel.simplidar.gui.MainFrame;
import ch.manuel.simplidar.raster.RasterManager;
import ch.manuel.utilities.MyUtilities;


// Main Class
public class Startup {
    
    // class attributes
    private static MainFrame mainFrame;
    private static RasterManager manager;
    private static DataLoader appData;
    
    public static void main(String[] args) {
        
        // Create Data Object
        manager = new RasterManager();
        
        // Set Look and Feel
        MyUtilities.setLaF("Windows");
        
        // load app data
        appData = new DataLoader();
        appData.loadData();
        
         /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            }
        });
    }
    
    
    // PRIVATE FUNCTIONS

}
