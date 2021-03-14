//Autor: Manuel Schmocker
//Datum: 13.02.2021
package ch.manuel.simplidar.calculation;

import ch.manuel.simplidar.gui.AnalyseFrame;
import ch.manuel.simplidar.gui.panels.Legend;
import ch.manuel.simplidar.raster.Cluster;
import ch.manuel.simplidar.raster.ClusterManager;

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
        // set color-model for legend
        Legend leged = AnalyseFrame.createLegend();
        leged.setLinearScale(0, 180);
        leged.setColorGray();
        leged.setTitel("Winkel, Grad");

        // update sunbeam direction
        Cluster.setSunbeamDirection(direction);

        int sizeX = ClusterManager.getClusterSizeX();
        int sizeY = ClusterManager.getClusterSizeY();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                // recalculate
                ClusterManager.getElement(i, j).recalcInclSun();

                // inclination from sunbeam s-w
                float angle = ClusterManager.getElement(i, j).getInclinSunDEG();

                // set pixel color in image
                int col = leged.colorFactory(angle).getRGB();
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
        Legend leged = AnalyseFrame.createLegend();
        leged.setLinearScale(0, maxVal);
        leged.setColorGray();
        String label = top ? "Neigung der Fläche, Grad" : "Winkel, Grad";
        leged.setTitel(label);

        int sizeX = ClusterManager.getClusterSizeX();
        int sizeY = ClusterManager.getClusterSizeY();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                float angle;
                if (top) {
                    // inclination from vertical
                    angle = ClusterManager.getElement(i, j).getInclinDEG();
                } else {
                    // inclination from sunbeam
                    angle = ClusterManager.getElement(i, j).getInclinSunDEG();
                }

                // set pixel color in image
                int col = leged.colorFactory(angle).getRGB();
                AnalyseFrame.getImg().setRGB(i, j, col);
            }
        }
    }

    // show orientation of cluster: N - O - S - W
    private static void showOrientation() {
        // set color-model for legend
        Legend leged = AnalyseFrame.createLegend();
        leged.setLinearScale(0, 360);
        leged.setColorTheme1();
        leged.setTitel("Orientierung, Grad");

        int sizeX = ClusterManager.getClusterSizeX();
        int sizeY = ClusterManager.getClusterSizeY();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {

                double angle = ClusterManager.getElement(i, j).getOrientEG();
                int col = leged.colorFactory(angle).getRGB();
                AnalyseFrame.getImg().setRGB(i, j, col);
            }
        }
    }

    // show roughness
    private static void showRoughness() {
        // set color-model for legend
        Legend leged = AnalyseFrame.createLegend();
        leged.setLogScale(0.001, 50);
        leged.setColorGreenRed();
        leged.setTitel("Rauigkeit, Meter");

        int sizeX = ClusterManager.getClusterSizeX();
        int sizeY = ClusterManager.getClusterSizeY();
//        double maxRough = ClusterManager.getMaxRoughness();

        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {

                double rg = ClusterManager.getElement(i, j).getRoughness();
                // set pixel color in image
                int col = leged.colorFactory(rg).getRGB();
                AnalyseFrame.getImg().setRGB(i, j, col);
            }
        }
    }
}
