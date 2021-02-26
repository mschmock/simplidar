//Autor: Manuel Schmocker
//Datum: 13.02.2021
package ch.manuel.simplidar.calculation;

import ch.manuel.simplidar.gui.AnalyseFrame;
import ch.manuel.simplidar.raster.Cluster;
import ch.manuel.simplidar.raster.DataManager;
import java.awt.Color;

public class RasterAnalyser implements Runnable {

    // class attributes
    private boolean isUpToDate;     // normal to cluster (regression)

    @Override
    public void run() {
        // start raster analyse
        startAnalyse();
        // show result in image
        showInclination(true);
        // repaint img
        AnalyseFrame.repaintImg();
    }

    // analyse cluster
    private static void startAnalyse() {
        DataManager.analyseCluster();
    }

    // PUBLIC FUNCTIONS
    public static void updateImg(int selection) {
        switch (selection) {
            case 0:
                showInclination(true);
                break;
            case 1:
                showInclination(false);
                break;
            case 2:
                showOrientation();
                break;
            case 3:
                showRoughness();
                break;
            default:
                showInclination(true);
        }
        // repaint img
        AnalyseFrame.repaintImg();
    }
    // update dynamically 
    public static void updateImg2(int direction) {
        // update sunbeam direction
        Cluster.setSunbeamDirection(direction);
        
        int sizeX = DataManager.getClusterSizeX();
        int sizeY = DataManager.getClusterSizeY();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                // recalculate
                DataManager.getElement(i, j).recalcInclSun();
                
                // inclination from sunbeam s-w
                float incl = 1.0f - DataManager.getElement(i, j).getInclinSunDEG()/ 180.0f;

                // set pixel color in image
                int col = Color.HSBtoRGB(0.3f, 0.8f, incl);
                AnalyseFrame.getImg().setRGB(i, j, col);
            }
        }
        // repaint img
        AnalyseFrame.repaintImg();
    }

    // PRIVATE FUNCTIONS
    // show inclination of cluster element: true -> inclination from vertical
    private static void showInclination(boolean top) {
        int sizeX = DataManager.getClusterSizeX();
        int sizeY = DataManager.getClusterSizeY();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                float incl;
                // inclination from vertical
                if(top) {
                    incl = 1.0f - DataManager.getElement(i, j).getInclinDEG() / 90.0f;
                // inclination from sunbeam s-w
                } else {
                    incl = 1.0f - DataManager.getElement(i, j).getInclinSunDEG()/ 180.0f;
                }

                // set pixel color in image
                int col = Color.HSBtoRGB(0.3f, 0.8f, incl);
                AnalyseFrame.getImg().setRGB(i, j, col);
            }
        }
    }

    private static void showOrientation() {
        int sizeX = DataManager.getClusterSizeX();
        int sizeY = DataManager.getClusterSizeY();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                
//                float degree = DataManager.getElement(i, j).getOrientEG() / 360.0f;

                // set pixel color in image
//                int col = Color.HSBtoRGB(0.3f, 1.0f, 2f * Math.abs(0.5f - degree));
                double rad = DataManager.getElement(i, j).getOrientEG() / 180.0 * Math.PI ;
                int c1 = Math.round( 255.0f * 0.5f * (float)( Math.sin(rad) + 1.0f));
                int c2 = Math.round( 255.0f * 0.5f * (float)( Math.cos(rad) + 1.0f));
                int col = new Color(255, c1, c2).getRGB();
                AnalyseFrame.getImg().setRGB(i, j, col);
            }
        }
    }
    
    private static void showRoughness() {
        
    }
}