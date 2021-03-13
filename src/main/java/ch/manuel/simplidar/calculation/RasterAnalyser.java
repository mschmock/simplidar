//Autor: Manuel Schmocker
//Datum: 13.02.2021
package ch.manuel.simplidar.calculation;

import ch.manuel.simplidar.gui.AnalyseFrame;
import ch.manuel.simplidar.gui.panels.Legend;
import ch.manuel.simplidar.raster.Cluster;
import ch.manuel.simplidar.raster.ClusterManager;
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
        AnalyseFrame.repaintImg();
    }

    // PUBLIC FUNCTIONS
    // draw information based on selection
    public static void updateImg(int selection) {
        // select case (drop down in AnalyseFrame.java)
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

        int sizeX = ClusterManager.getClusterSizeX();
        int sizeY = ClusterManager.getClusterSizeY();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                // recalculate
                ClusterManager.getElement(i, j).recalcInclSun();

                // inclination from sunbeam s-w
                float incl = 1.0f - ClusterManager.getElement(i, j).getInclinSunDEG() / 180.0f;

                // set pixel color in image
                int col = Color.HSBtoRGB(0.6f, 0.1f, incl);
                AnalyseFrame.getImg().setRGB(i, j, col);
            }
        }
        // repaint img: without legend -> false
        AnalyseFrame.repaintImg();
    }

    
    // PRIVATE FUNCTIONS
    // analyse cluster
    private static void startAnalyse() {
        ClusterManager.analyseCluster();
    }
    
    // show inclination of cluster element: true -> inclination from vertical
    private static void showInclination(boolean top) {
        // set color-model for legend
        double maxVal = top ? 90.0 : 180.0;
        AnalyseFrame.getLegend().setLinearScale(0, maxVal);
        AnalyseFrame.getLegend().setColorGray();
        AnalyseFrame.getLegend().setActive();
        
        int sizeX = ClusterManager.getClusterSizeX();
        int sizeY = ClusterManager.getClusterSizeY();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                float angle;
                // inclination from vertical
                if (top) {
                    angle = ClusterManager.getElement(i, j).getInclinDEG();
                    // inclination from sunbeam s-w
                } else {
                    angle = ClusterManager.getElement(i, j).getInclinSunDEG();
                }

                // set pixel color in image
                int col = AnalyseFrame.getLegend().colorFactory(angle).getRGB();
                AnalyseFrame.getImg().setRGB(i, j, col);
            }
        }
    }

    // show orientation of cluster: N - O - S - W
    private static void showOrientation() {
        int sizeX = ClusterManager.getClusterSizeX();
        int sizeY = ClusterManager.getClusterSizeY();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {

//                float degree = DataManager.getElement(i, j).getOrientEG() / 360.0f;
                // set pixel color in image
//                int col = Color.HSBtoRGB(0.3f, 1.0f, 2f * Math.abs(0.5f - degree));
                double rad = ClusterManager.getElement(i, j).getOrientEG() / 180.0 * Math.PI;
                int c1 = Math.round(255.0f * 0.5f * (float) (Math.sin(rad) + 1.0f));
                int c2 = Math.round(255.0f * 0.5f * (float) (Math.cos(rad) + 1.0f));
                int col = new Color(255, c1, c2).getRGB();
                AnalyseFrame.getImg().setRGB(i, j, col);
            }
        }
    }

    // show roughness
    private static void showRoughness() {
        int sizeX = ClusterManager.getClusterSizeX();
        int sizeY = ClusterManager.getClusterSizeY();
        double maxRough = ClusterManager.getMaxRoughness();

        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                float rg;

                // roughness
//                rg = (float) (Math.log(DataManager.getElement(i, j).getRoughness()) / Math.log(maxRough) );
                rg = (float) (ClusterManager.getElement(i, j).getRoughness() / maxRough);
                // set pixel color in image
                int col = AnalyseFrame.getLegend().colorFactory(rg).getRGB();
                AnalyseFrame.getImg().setRGB(i, j, col);
            }
        }
    }
}
