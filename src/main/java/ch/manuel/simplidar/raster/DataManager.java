//Autor: Manuel Schmocker
//Datum: 13.02.2021
package ch.manuel.simplidar.raster;

/*
Header Format:
    ncols 8750
    nrows 6000
    xllcorner 2628750
    yllcorner 1173000
    cellsize 0.5
    nodata_value 0
 */
public class DataManager {

    // raster objects
    public static Raster mainRaster;           // main raster
    public static Raster subRaster_1;          // sub raster (merge procedure)
    public static Raster subRaster_2;          // sub raster (merge procedure)
    public static Raster subRaster_3;          // sub raster (merge procedure)
    public static Raster subRaster_4;          // sub raster (merge procedure)

    // cluster object
    private static final int size = 10;
    private static int sizeCluX;
    private static int sizeCluY;
    private static Cluster[][] mainCluster;

    // Constructor
    public DataManager() {
        // datas from raster
        mainRaster = new Raster();
        subRaster_1 = new Raster();
        subRaster_2 = new Raster();
        subRaster_3 = new Raster();
        subRaster_4 = new Raster();
        // cluster for mainRaster

    }
    
    // GETTER
    public static Cluster getElement(int i, int j){
        return mainCluster[i][j];
    }

    // PUBLIC FUNCTIONS
    // create data holder for mainCluster
    public static void initClusters() {
        sizeCluX = mainRaster.getNbCols() / size;
        sizeCluY = mainRaster.getNbRows() / size;

        mainCluster = new Cluster[sizeCluX][sizeCluY];
        mainCluster[0][0] = new Cluster(0,0,10);
    }
}
