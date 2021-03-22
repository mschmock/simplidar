//Autor: Manuel Schmocker
//Datum: 09.03.2021

package ch.manuel.simplidar.raster;


import ch.manuel.simplidar.gui.AnalyseFrame;
import java.util.logging.Level;
import java.util.logging.Logger;


// Data-holder for clusters
public class ClusterManager {
    // cluster object
    private static final int size = 10;
    private static int sizeCluX;
    private static int sizeCluY;
    private static Cluster[][] mainCluster;
    private static double maxRoughness;
    // thread objects
    private static int progrThr1;
    private static int progrThr2;
    private static int progrThr3;
    private static int progrThr4;
    
    
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
        sizeCluX = RasterManager.mainRaster.getNbCols() / size;
        sizeCluY = RasterManager.mainRaster.getNbRows() / size;

        mainCluster = new Cluster[sizeCluX][sizeCluY];
        
    }
    
    // start the analyse
    public static void startAnalyse() {
        
        int i_min;
        int i_max;
        int j_min;
        int j_max;
        
        // prepare thread 1
        i_min = 0;
        i_max = Math.round(sizeCluX / 2.0f);
        j_min = 0;
        j_max = Math.round(sizeCluY / 2.0f);
        Thread t1 = new Thread(new AnalyserThread(i_min, i_max, j_min, j_max, 1));   
        
        // prepare thread 2
        i_min = Math.round(sizeCluX / 2.0f);
        i_max = sizeCluX;
        j_min = 0;
        j_max = Math.round(sizeCluY / 2.0f);
        Thread t2 = new Thread(new AnalyserThread(i_min, i_max, j_min, j_max, 2)); 
        
        // prepare thread 3
        i_min = 0;
        i_max = Math.round(sizeCluX / 2.0f);
        j_min = Math.round(sizeCluY / 2.0f);
        j_max = sizeCluY;
        Thread t3 = new Thread(new AnalyserThread(i_min, i_max, j_min, j_max, 3)); 
        
        // prepare thread 4
        i_min = Math.round(sizeCluX / 2.0f);
        i_max = sizeCluX;
        j_min = Math.round(sizeCluY / 2.0f);
        j_max = sizeCluY;
        Thread t4 = new Thread(new AnalyserThread(i_min, i_max, j_min, j_max, 4)); 
        
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
    
    // status: mean from 4 threads
    private static void setStatus(int nb, int progress) {
        switch(nb) {
            case 1:
                progrThr1 = progress;
                break;
            case 2:
                progrThr2 = progress;
                break;
            case 3:
                progrThr3 = progress;
                break;
            case 4:
                progrThr4 = progress;
                break;
        }
        float mean;
        mean = (progrThr1 + progrThr2 + progrThr3 + progrThr4) / 4.0f;
 
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
        int threadNb;
        int progress;
        
        @Override
        public void run() {
            analyseCluster();
        }
        
        // CONSTRUCTOR
        AnalyserThread(int i_min, int i_max, int j_min, int j_max, int nb) {
            this.progress = 0;
            this.i_min = i_min;
            this.i_max = i_max;
            this.j_min = j_min;
            this.j_max = j_max;
            this.threadNb = nb;
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
                ClusterManager.setStatus(threadNb, progress);
            }
        }
            
    }
}
