//Autor: Manuel Schmocker
//Datum: 13.02.2021


package ch.manuel.simplidar;

import ch.manuel.simplidar.gui.MainFrame;
import ch.manuel.utilities.MyUtilities;


// Main Class
public class Startup {
    
    // class attributes
    private static MainFrame mainFrame;
    
    
    public static void main(String[] args) {
        
        // Set Look and Feel
        MyUtilities.setLaF("Windows");
        
         /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            }
        });
        
    }
}
