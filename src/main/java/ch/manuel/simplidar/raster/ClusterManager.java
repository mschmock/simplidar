//Autor: Manuel Schmocker
//Datum: 09.03.2021

package ch.manuel.simplidar.raster;

// Data-holder for clusters

import ch.manuel.simplidar.gui.AnalyseFrame;
import static ch.manuel.simplidar.raster.RasterManager.mainRaster;

public class ClusterManager {
    // cluster object
    private static final int size = 10;
    private static int sizeCluX;
    private static int sizeCluY;
    private static Cluster[][] mainCluster;
    private static double maxRoughness;
    
    // CONSTRUCTOR
//    public ClusterManager() {
//        sizeCluX = 1;
//        sizeCluY = 1;
//    }
    
    
    // GETTER
    public static Cluster getElement(int i, int j) {
        return mainCluster[i][j];
    }

    public static int getClusterSizeX() {
        return sizeCluX;
    }

    public static int getClusterSizeY() {
        return sizeCluY;
    }
    
    public static double getMaxRoughness() {
        return maxRoughness;
    }
    
    // PUBLIC FUNCTIONS
    // create data holder for mainCluster
    public static void initClusters() {
        sizeCluX = mainRaster.getNbCols() / size;
        sizeCluY = mainRaster.getNbRows() / size;

        mainCluster = new Cluster[sizeCluX][sizeCluY];
        
    }
    
        // analyse cluster
    public static void analyseCluster() {
        for (int i = 0; i < sizeCluX; i++) {
            for (int j = 0; j < sizeCluY; j++) {
                int colIndex = i*size;
                int rowIndex = j*size;
                // analyse starts with calling the constructor
                mainCluster[i][j] = new Cluster(colIndex, rowIndex, size);
            }
            // update progressbar
            AnalyseFrame.setProgress(100*i/sizeCluX);
        }
        // get max Roughness
        calcMaxRoughness();
        // loading completed: set progressbar back to 0
        AnalyseFrame.setProgress(0);
    }
    
    // PUBLIC FUNCTIONS
    // calculate max roughness
    private static void calcMaxRoughness() {
        maxRoughness = 0.0;
        
        for (int i = 0; i < sizeCluX; i++) {
            for (int j = 0; j < sizeCluY; j++) {
                int colIndex = i*size;
                int rowIndex = j*size;
                // analyse starts with calling the constructor
                double tmpRough = mainCluster[i][j].getRoughness();
                if(tmpRough > maxRoughness ) {
                    maxRoughness = tmpRough;
                }
            }
        }
    }
    
    
    
}
