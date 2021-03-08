//Autor: Manuel Schmocker
//Datum: 02.04.2020

package ch.manuel.simplidar.gui.panels;

import ch.manuel.simplidar.DataLoader;
import ch.manuel.simplidar.geodata.GeoData;
import ch.manuel.simplidar.geodata.Municipality;
import ch.manuel.simplidar.gui.RasterFrame;
import ch.manuel.simplidar.raster.DataManager;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;


public class PolygonPanel extends JPanel {

    // list polygons
    private static List<Polygon> listPoly; 
    // transformation
    private final AffineTransform tx;
    private static final int pxBORDER = 16;                     // border in pixel
    private static Map<Integer,Municipality> mapID;             // map with id of municipalities
    // network
    private static Municipality selectedMunicip;
    // draw per absolute or per 1'000?
    private static boolean absoluteRes;
        
        
    // Constructor
    public PolygonPanel() {
        super();
        
        // initialisation
        PolygonPanel.absoluteRes = true;
        listPoly = new ArrayList<>();
        mapID = new HashMap<Integer,Municipality>();
        // get polygons from geoData
        initPolygons();
        // init transformation
        this.tx = new AffineTransform();

    }
    
    // initalisation of polygons (border municipalites)
    private void initPolygons() {
        if( DataLoader.geoData != null ) {
            for (int i = 0; i < GeoData.getNbMunicip(); i++) {
                List<int[]> listPolyX = GeoData.getMunicip(i).getPolyX();
                List<int[]> listPolyY = GeoData.getMunicip(i).getPolyY();
                int nb = listPolyX.size();

                for (int j = 0; j < nb; j++) {
                    listPoly.add(new Polygon (  listPolyX.get(j), 
                                                listPolyY.get(j),
                                                listPolyX.get(j).length )
                    );
                    // create map with municipalities
                    mapID.put( listPoly.size()-1, GeoData.getMunicip(i) );
                }
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        if( DataLoader.geoData != null ) {
            // define transformation
            calcTransformation();
            // draw polygons 
            drawBorder( g2 );
            
            // draw raster bounds
            drawRasterBounds( g2 );
        }
    }
    
    // repaint on click
    public void repaintPanel() {
        this.validate();
        this.repaint();
    }
    
    // calculate transformation matrx (translation, scale, mirror...)
    private void calcTransformation() {
        double wd = DataLoader.geoData.getWidth();
        double hg = DataLoader.geoData.getHeight();
        
        double ratio1 = (this.getWidth() - (2 * pxBORDER)) / wd;
        double ratio2 = (this.getHeight()- (2 * pxBORDER)) / hg;
        double scaleFact = ( ratio1 < ratio2) ? ratio1 : ratio2;
        
        AffineTransform trans = AffineTransform.getTranslateInstance( -DataLoader.geoData.getBoundX(), -DataLoader.geoData.getBoundY() );
        AffineTransform scale = AffineTransform.getScaleInstance( scaleFact, scaleFact );
        AffineTransform mirr_y = new AffineTransform( 1, 0, 0, -1, 0, this.getHeight() );
        AffineTransform trans2 = AffineTransform.getTranslateInstance( pxBORDER, -pxBORDER );
        tx.setToIdentity();
        tx.concatenate(trans2);
        tx.concatenate(mirr_y);
        tx.concatenate(scale);
        tx.concatenate(trans);
    }
    
    // draw borders of municipalities
    private void drawBorder(Graphics2D g2) {
        g2.setStroke(new BasicStroke(1));
        g2.setColor( Color.black );
        
        for( int i = 0; i < listPoly.size(); i++ ) {
            Shape shape = this.tx.createTransformedShape( listPoly.get(i) );
            g2.draw(shape);            
        }  
    }
    
    // draw borders of municipalities
    private void drawRasterBounds(Graphics2D g2) {
        g2.setStroke(new BasicStroke(1));
        g2.setColor( Color.red );
        
        double x = DataManager.mainRaster.getXmin();
        double y = DataManager.mainRaster.getYmax();
        double w = DataManager.mainRaster.getXmax() - x;
        double h = y - DataManager.mainRaster.getYmin();
        
        Shape shape = this.tx.createTransformedShape( new Rectangle2D.Double(x, y, w, h) );
        g2.draw(shape);            

    }
    
    // draw infections per municipality
//    private void drawInfections(Graphics2D g2) {
//        
//        Color col;
//        // absolute or relative
//        if( PolygonPanel.absoluteRes ) {
//            // get max value
//            int maxInfect = Municipality.getMaxInfections();
//            // prepare legend
//            legend.setMaxVal( maxInfect );        // max value
//            legend.setLogScale( true );           // logarithmic scale
//            // loop through municipalities
//            for (int i = 0; i < GeoData.getNbMunicip(); i++) {
//                // draw absoute number
//                // choose color from legend
//                col = legend.colorFactory( GeoData.getMunicip(i).getNbInfections() );
//                // fill polygon
//                fillMunicip( g2, i, col );
//            }
//        } else {
//            // get max value
//            float maxInfectPerInhab = Municipality.getMaxInfectPerInhab();
//            // prepare legend
//            legend.setMaxVal( maxInfectPerInhab );      // max value
//            legend.setLogScale( false );                // logarithmic scale
//            // loop through municipalities
//            for (int i = 0; i < GeoData.getNbMunicip(); i++) {
//                // draw per 1000 inhabitants
//                // choose color from legend
//                col = legend.colorFactory( GeoData.getMunicip(i).getNbInfectPerInhab() );
//                // fill polygon
//                fillMunicip( g2, i, col );
//            }
//        }
//        // draw legend
//        legend.drawLegend( g2 );
//        // draw borders on top
//        this.drawBorder(g2);      
//    }
        
    // draw infections per municipality
//    private void drawK0(Graphics2D g2) {
//        
//        Color col;
//        // get max value
//        legend.setMaxVal( Municipality.getMaxK0() );    // max value
//        legend.setLogScale( false );                    // logarithmic scale
//        // loop through municipalities
//        for (int i = 0; i < GeoData.getNbMunicip(); i++) {
//            // choose color from legend
//            col = legend.colorFactory( GeoData.getMunicip(i).getK0() );
//            // fill polygon
//            fillMunicip( g2, i, col );
//        }
//        // draw legend
//        legend.drawLegend( g2 );
//        // draw borders on top
//        this.drawBorder(g2);
//    }
    
    // fill polygon of municipality i
    private void fillMunicip(Graphics2D g2, int i, Color col) {
        // color
        g2.setColor( col );
        
        // prepare polygons
        List<int[]> listPolyX = GeoData.getMunicip(i).getPolyX();
        List<int[]> listPolyY = GeoData.getMunicip(i).getPolyY();
        int nb = listPolyX.size();

        // draw polygons
        for (int j = 0; j < nb; j++) {
            Shape shape = this.tx.createTransformedShape(
                    new Polygon (   listPolyX.get(j), 
                                    listPolyY.get(j),
                                    listPolyX.get(j).length ) );
            g2.fill(shape);
        }
    }
    
    // set index / name for element with click
    public void setNameOnClick(Point p) {
        for( int i = 0; i < listPoly.size(); i++ ) {
            Shape shape = this.tx.createTransformedShape( listPoly.get(i) );
            if( shape.contains(p) ) {
                PolygonPanel.selectedMunicip = PolygonPanel.mapID.get(i);
                RasterFrame.setStatus("Selected: " + PolygonPanel.mapID.get(i).getName() );
                break;
            }
        }
    }
    
    // set draw absolute or per 1'000 inhabitants
    public static void setAbsoluteResultats(boolean bool) {
        PolygonPanel.absoluteRes = bool;
    }
    
    // vue changed
    public static void viewChanged() {
        //legend.resetMaxVal();
    }
}