//Autor: Manuel Schmocker
//Datum: 09.03.2021

package ch.manuel.simplidar.raster;

// Data-holder for clusters

import ch.manuel.simplidar.gui.AnalyseFrame;
import static ch.manuel.simplidar.raster.RasterManager.mainRaster;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClusterManager {
    // cluster object
    private static final int size = 10;
    private static int sizeCluX;
    private static int sizeCluY;
    private static Cluster[][] mainCluster;
    private static double maxRoughness;
    // thread objects
    private static AnalyserThread thr1;
    private static AnalyserThread thr2;
    private static AnalyserThread thr3;
    private static AnalyserThread thr4;
    
    // CONSTRUCTOR
    public ClusterManager() {
//        sizeCluX = 1;
//        sizeCluY = 1;
        thr1 = new AnalyserThread();
        thr2 = new AnalyserThread();
        thr3 = new AnalyserThread();
        thr4 = new AnalyserThread();
    }
    
    
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
    
    // start the analyse
    public static void startAnalyse() {
        
        int i_min;
        int i_max;
        int j_min;
        int j_max;
        
        // case 1
        i_min = 0;
        i_max = Math.round(sizeCluX / 2.0f);
        j_min = 0;
        j_max = Math.round(sizeCluY / 2.0f);
        Thread t1 = new Thread(thr1);
        thr1.initQuadrant(i_min, i_max, j_min, j_max);
        
        
        // case 2
        i_min = Math.round(sizeCluX / 2.0f);
        i_max = sizeCluX;
        j_min = 0;
        j_max = Math.round(sizeCluY / 2.0f);
        Thread t2 = new Thread(thr2);
        thr2.initQuadrant(i_min, i_max, j_min, j_max);
        
        // case 3
        i_min = 0;
        i_max = Math.round(sizeCluX / 2.0f);
        j_min = Math.round(sizeCluY / 2.0f);
        j_max = sizeCluY;
        Thread t3 = new Thread(thr3);
        thr3.initQuadrant(i_min, i_max, j_min, j_max);
        
        // case 4
        i_min = Math.round(sizeCluX / 2.0f);
        i_max = sizeCluX;
        j_min = Math.round(sizeCluY / 2.0f);
        j_max = sizeCluY;
        Thread t4 = new Thread(thr4);
        thr4.initQuadrant(i_min, i_max, j_min, j_max);
        
        t1.start();
        t2.start();
        t3.start();
        t4.start();
         
        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(ClusterManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        // get max Roughness
        calcMaxRoughness();
        // loading completed: set progressbar back to 0
        AnalyseFrame.setProgress(0);
    }
    
    // PRIVATE FUNCTIONS
    // calculate max roughness
    private static void calcMaxRoughness() {
        maxRoughness = 0.0;
        
        for (int i = 0; i < sizeCluX; i++) {
            for (int j = 0; j < sizeCluY; j++) {
                // analyse starts with calling the constructor
                double tmpRough = mainCluster[i][j].getRoughness();
                if(tmpRough > maxRoughness ) {
                    maxRoughness = tmpRough;
                }
            }
        }
    }
    // status
    private static void setStatus() {
        float mean;
        mean = (thr1.getProgress() + thr2.getProgress() + thr3.getProgress() + thr4.getProgress()) / 4.0f*100f;
 
        // update progressbar
        AnalyseFrame.setProgress((int) mean);
    }
    
    //***********************************************************
    // inner class: thread manager
    private static class AnalyserThread implements Runnable {
        int i_min;
        int i_max;
        int j_min;
        int j_max;
        int progress;
        
        @Override
        public void run() {
            analyseCluster();
        }
        
        // CONSTRUCTOR
        AnalyserThread() {
            this.progress = 0;
        }
        
        // init quadrant
        private void initQuadrant(int i_min, int i_max, int j_min, int j_max) {
            this.i_min = i_min;
            this.i_max = i_max;
            this.j_min = j_min;
            this.j_max = j_max;
        }
        
        // get progress
        private int getProgress() {
            return this.progress;
        }
        
        // analyse cluster
        private void analyseCluster() {
            for (int i = i_min; i < i_max; i++) {
                for (int j = j_min; j < j_max; j++) {
                    int colIndex = i*size;
                    int rowIndex = j*size;
                    // analyse starts with calling the constructor
                    mainCluster[i][j] = new Cluster(colIndex, rowIndex, size);
                }
                // set progress
                progress = Math.round(100.0f*(i-i_min)/(i_max - i_min));
                ClusterManager.setStatus();
            }
        }
            
    }
}
