//Autor: Manuel Schmocker
//Datum: 19.02.2021


package ch.manuel.simplidar.calculation;

import ch.manuel.simplidar.gui.MainFrame;
import ch.manuel.simplidar.raster.DataManager;



public class Calculation {
    
    
    public static void testCalc(int x, int y) {
        
        Point point = DataManager.mainRaster.getPoint(x-1, y-1);
        
        String msg =    "X-Koordinate: " + point.getX() + "\n";
        msg +=          "Y-Koordinate: " + point.getY() + "\n";
        msg +=          "Z-Koordinate: " + point.getZ() + "\n";
        
        
        MainFrame.setText(msg);
    }
}
