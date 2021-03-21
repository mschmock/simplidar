//Autor: Manuel Schmocker
//Datum: 02.04.2020

package ch.manuel.simplidar.gui.panels;

import ch.manuel.simplidar.DataLoader;
import ch.manuel.simplidar.geodata.GeoData;
import ch.manuel.simplidar.geodata.Municipality;
import ch.manuel.simplidar.gui.RasterFrame;
import ch.manuel.simplidar.raster.RasterManager;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
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
    // other
    private boolean zoomOnRaster;
    private double zoomLvl;
    private static Municipality selectedMunicip;
        
        
    // Constructor
    public PolygonPanel() {
        super();
        
        // initialisation
        zoomOnRaster = false;
        zoomLvl = 1;
        listPoly = new ArrayList<>();
        mapID = new HashMap<>();
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
        
        // draw selected municipality if selected
        if( PolygonPanel.selectedMunicip != null ) {
            fillMunicip(PolygonPanel.selectedMunicip, g2);
        }
        
        // draw geodata if available
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
    
    // zoom on raster
    public void setZoomOnRaster(boolean bool) {
        this.zoomOnRaster = bool;
    }
    
    public void zoomIn() {
        this.zoomLvl = zoomLvl*1.5;
    }
    public void zoomOut() {
        this.zoomLvl = zoomLvl/1.5;
    }
    

    
    // calculate transformation matrix (translation, scale, mirror...)
    private void calcTransformation() {
        double wd;
        double hg;
        double xMin;
        double yMin;
        
        if(zoomOnRaster) {
            // zoom on raster
            xMin = RasterManager.getBoundXmin();
            yMin = RasterManager.getBoundYmin();
            wd = RasterManager.getBoundXmax() - RasterManager.getBoundXmin();
            hg = RasterManager.getBoundYmax() - RasterManager.getBoundYmin();
        } else {
            // zoom on geodata
            xMin = DataLoader.geoData.getBoundX();
            yMin = DataLoader.geoData.getBoundY();
            wd = DataLoader.geoData.getWidth();
            hg = DataLoader.geoData.getHeight();
        }
        
        
        double ratio1 = (this.getWidth() - (2 * pxBORDER)) / wd;
        double ratio2 = (this.getHeight()- (2 * pxBORDER)) / hg;
        double scaleFact = ( ratio1 < ratio2) ? ratio1 : ratio2;
        
        AffineTransform trans = AffineTransform.getTranslateInstance( -xMin, -yMin );
        AffineTransform scale = AffineTransform.getScaleInstance( scaleFact, scaleFact );
        AffineTransform mirr_y = new AffineTransform( 1, 0, 0, -1, 0, this.getHeight() );
        AffineTransform trans2 = AffineTransform.getTranslateInstance( pxBORDER, -pxBORDER );
        tx.setToIdentity();
        zoomTransformation();
        tx.concatenate(trans2);
        tx.concatenate(mirr_y);
        tx.concatenate(scale);
        tx.concatenate(trans); 
    }
    
    // set zoom transformation
    private void zoomTransformation() {
        double dx = this.getWidth()/2;
        double dy = this.getHeight()/2;
        
        AffineTransform trans10 = AffineTransform.getTranslateInstance( -dx, -dy );
        AffineTransform scale = AffineTransform.getScaleInstance(zoomLvl, zoomLvl );
        AffineTransform trans01 = AffineTransform.getTranslateInstance( dx, dy );
        tx.concatenate(trans01);
        tx.concatenate(scale);
        tx.concatenate(trans10);
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
    
    // draw raster boundaries
    private void drawRasterBounds(Graphics2D g2) {
        int nb = RasterManager.getNumberOfRasters();
        // draw global bound (layer +1)
        if( nb > 1 ) {
            g2.setStroke(new BasicStroke(3));
            g2.setColor( Color.green );
            double x = RasterManager.getBoundXmin();
            double y = RasterManager.getBoundYmin();
            double w = RasterManager.getBoundXmax()- x;
            double h = RasterManager.getBoundYmax()- y;

            Shape shape = this.tx.createTransformedShape( new Rectangle2D.Double(x, y, w, h) );
            g2.draw(shape); 
        }
        // draw each raster shapes (layer 0)
        g2.setStroke(new BasicStroke(1));
        
        for (int i = 0; i < nb; i++) {
            double x = RasterManager.getRasterFromList(i).getXmin();
            double y = RasterManager.getRasterFromList(i).getYmin();
            double w = RasterManager.getRasterFromList(i).getXmax() - x;
            double h = RasterManager.getRasterFromList(i).getYmax() - y;

            Shape shape = this.tx.createTransformedShape( new Rectangle2D.Double(x, y, w, h) );
            
            // filling
            g2.setColor( new Color(255, 0, 0, 40) );
            g2.fill(shape);
            // border
            g2.setColor( Color.red );
            g2.draw(shape);
        }
    }
    
    // fill polygon of municipality i
    private void fillMunicip(Municipality municip, Graphics2D g2) {
        // color
        g2.setColor( Color.lightGray );
        
        // prepare polygons
        List<int[]> listPolyX = municip.getPolyX();
        List<int[]> listPolyY = municip.getPolyY();
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
                RasterFrame.setText("Selected: " + PolygonPanel.mapID.get(i).getName() );
                break;
            }
        }
    }
    
}
