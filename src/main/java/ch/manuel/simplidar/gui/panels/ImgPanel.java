//Autor: Manuel Schmocker
//Datum: 13.02.2021

package ch.manuel.simplidar.gui.panels;

import ch.manuel.simplidar.gui.AnalyseFrame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;


// draw panel with raster-data (RasterAnalyser.java)
public class ImgPanel extends JPanel {
    
    // class attributes
    private static Legend legend;       // drawLegend yes / no
    
    // CONSTRUCTOR
    public ImgPanel() {
        ImgPanel.legend = null;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // draw image
        drawScaledImage(g);
        // draw legend
        if(legend != null) {
            showLegende(g2);
        }
    }
    
    // SETTER
    // set legend for drawing
    public void setLegend(Legend legend) {
        ImgPanel.legend = legend;
    }
    public void resetLegend() {
        ImgPanel.legend = null;
    }
    
    // PRIVATE FUNCTIONS
    // draw legend
    private void showLegende(Graphics2D g2) {
        // draw legend
        legend.drawLegend( g2 );
    }
    
    // scale image
    private void drawScaledImage(Graphics g) {
        int imgWidth = 1;
        int imgHeight = 1;
        try{
            imgWidth = AnalyseFrame.getImg().getWidth(null);
            imgHeight = AnalyseFrame.getImg().getHeight(null);
        } catch(NullPointerException ex) {
            
        }
         
        double imgAspect = (double) imgHeight / imgWidth;
 
        int canvasWidth = this.getWidth();
        int canvasHeight = this.getHeight();
         
        double canvasAspect = (double) canvasHeight / canvasWidth;
 
        int x1 = 0; // top left X position
        int y1 = 0; // top left Y position
        int x2 = 0; // bottom right X position
        int y2 = 0; // bottom right Y position
         
        if (imgWidth < canvasWidth && imgHeight < canvasHeight) {
            // the image is smaller than the canvas
            x1 = (canvasWidth - imgWidth)  / 2;
            y1 = (canvasHeight - imgHeight) / 2;
            x2 = imgWidth + x1;
            y2 = imgHeight + y1;
             
        } else {
            if (canvasAspect > imgAspect) {
                y1 = canvasHeight;
                // keep image aspect ratio
                canvasHeight = (int) (canvasWidth * imgAspect);
                y1 = (y1 - canvasHeight) / 2;
            } else {
                x1 = canvasWidth;
                // keep image aspect ratio
                canvasWidth = (int) (canvasHeight / imgAspect);
                x1 = (x1 - canvasWidth) / 2;
            }
            x2 = canvasWidth + x1;
            y2 = canvasHeight + y1;
        }
 
        g.drawImage(AnalyseFrame.getImg(), x1, y1, x2, y2, 0, 0, imgWidth, imgHeight, null);
    }
}
