//Autor: Manuel Schmocker
//Datum: 13.02.2021
package ch.manuel.simplidar.calculation;

import ch.manuel.simplidar.gui.AnalyseFrame;
import ch.manuel.simplidar.raster.Cluster;
import ch.manuel.simplidar.raster.DataManager;
import java.awt.Color;

public class RasterAnalyser implements Runnable {

    // class attributes
    private static boolean isUpToDate;      // normal to cluster (regression)

    
    @Override
    public void run() {
        // start raster analyse
        startAnalyse();
        // show result in image
        showInclination(true);
        // repaint img
        AnalyseFrame.repaintImg(false);
    }

    // PUBLIC FUNCTIONS
    // draw information based on selection
    public static void updateImg(int selection) {
        boolean drawLegend;
        // select case (drop down in AnalyseFrame.java)
        switch (selection) {
            case 0:
                drawLegend = false;
                showInclination(true);
                break;
            case 1:
                drawLegend = false;
                showInclination(false);
                break;
            case 2:
                drawLegend = false;
                showOrientation();
                break;
            case 3:
                drawLegend = true;
                showRoughness();
                break;
            default:
                drawLegend = false;
                showInclination(true);
        }
        // repaint img
        AnalyseFrame.repaintImg(drawLegend);
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
                float incl = 1.0f - DataManager.getElement(i, j).getInclinSunDEG() / 180.0f;

                // set pixel color in image
                int col = Color.HSBtoRGB(0.6f, 0.1f, incl);
                AnalyseFrame.getImg().setRGB(i, j, col);
            }
        }
        // repaint img: without legend -> false
        AnalyseFrame.repaintImg(false);
    }

    
    // PRIVATE FUNCTIONS
        // analyse cluster
    private static void startAnalyse() {
        DataManager.analyseCluster();
    }
    
    // show inclination of cluster element: true -> inclination from vertical
    private static void showInclination(boolean top) {
        int sizeX = DataManager.getClusterSizeX();
        int sizeY = DataManager.getClusterSizeY();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                float incl;
                // inclination from vertical
                if (top) {
                    incl = 1.0f - DataManager.getElement(i, j).getInclinDEG() / 90.0f;
                    // inclination from sunbeam s-w
                } else {
                    incl = 1.0f - DataManager.getElement(i, j).getInclinSunDEG() / 180.0f;
                }

                // set pixel color in image
                int col = Color.HSBtoRGB(0.6f, 0.1f, incl);
                AnalyseFrame.getImg().setRGB(i, j, col);
            }
        }
    }

    // show orientation of cluster: N - O - S - W
    private static void showOrientation() {
        int sizeX = DataManager.getClusterSizeX();
        int sizeY = DataManager.getClusterSizeY();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {

//                float degree = DataManager.getElement(i, j).getOrientEG() / 360.0f;
                // set pixel color in image
//                int col = Color.HSBtoRGB(0.3f, 1.0f, 2f * Math.abs(0.5f - degree));
                double rad = DataManager.getElement(i, j).getOrientEG() / 180.0 * Math.PI;
                int c1 = Math.round(255.0f * 0.5f * (float) (Math.sin(rad) + 1.0f));
                int c2 = Math.round(255.0f * 0.5f * (float) (Math.cos(rad) + 1.0f));
                int col = new Color(255, c1, c2).getRGB();
                AnalyseFrame.getImg().setRGB(i, j, col);
            }
        }
    }

    // show roughness
    private static void showRoughness() {
        int sizeX = DataManager.getClusterSizeX();
        int sizeY = DataManager.getClusterSizeY();
        double maxRough = DataManager.getMaxRoughness();

        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                float rg;

                // roughness
//                rg = (float) (Math.log(DataManager.getElement(i, j).getRoughness()) / Math.log(maxRough) );
                rg = (float) (DataManager.getElement(i, j).getRoughness() / maxRough);
                // set pixel color in image
                int col = Color.HSBtoRGB(0.0f, 1.0f, rg);
                AnalyseFrame.getImg().setRGB(i, j, col);
            }
        }
    }
}
