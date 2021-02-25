//Autor: Manuel Schmocker
//Datum: 13.02.2021
package ch.manuel.simplidar.calculation;

import ch.manuel.simplidar.gui.AnalyseFrame;
import ch.manuel.simplidar.raster.DataManager;
import java.awt.Color;

public class RasterAnalyser implements Runnable {

    @Override
    public void run() {
        // start raster analyse
        startAnalyse();
        // show result in image
        showResult();
        // repaint img
        AnalyseFrame.repaintImg();
    }

    // analyse cluster
    private static void startAnalyse() {
        DataManager.analyseCluster();
    }

    private static void showResult() {
        int sizeX = DataManager.getClusterSizeX();
        int sizeY = DataManager.getClusterSizeY();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {

                int incl = (int) (255 * DataManager.getElement(i, j).getInclination() / (Math.PI));
                int col = new Color(134, incl, 145).getRGB();
                // set pixel color in image
                AnalyseFrame.getImg().setRGB(i, j, col);
            }
        }
    }

}
