//Autor: Manuel Schmocker
//Datum: 08.03.2021
package ch.manuel.simplidar.gui.panels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

// create legend in  plot (Class ImgPanel.java)
public class Legend {

    // membervariables
    private ImgPanel imgPanel;
    private int colorMode;
    // legend: geometry
    private final int LEN_SEGM = 20;
    private final int OFFS_BORDER = 20;     // distance to border
    private final int TITEL_SIZE = 14;      // font size titel
    // size of panel
    private int panelWidth;
    private int panelHeight;
    // min, max Value for legend
    private double maxValLegend;
    private double minValLegend;
    private String labelText;
    // logarithmic scale
    private boolean isLog;
    // number format
    private NumberFormat formatter;

    // CONSTRUCTOR
    public Legend(ImgPanel imgPanel) {
        this.imgPanel = imgPanel;
        this.colorMode = 0;
        // format
        formatter = new DecimalFormat("###,##0.###");
    }

    // DRAW
    protected void drawLegend(Graphics2D g2) {
        if (this.isLog) {
            drawLegendLog(g2);
        } else {
            drawLegendLin(g2);
        }
    }

    // PUBLIC FUNCTIONS
    // set linear scale from xmin to xmax
    public void setLinearScale(double xMin, double xMax) {
        this.minValLegend = xMin;
        this.maxValLegend = xMax;
        this.isLog = false;
    }

    // set linear scale from xmin to xmax
    public void setLogScale(double xMin, double xMax) {
        this.minValLegend = xMin;
        this.maxValLegend = xMax;
        this.isLog = true;
    }

    // set label text
    public void setTitel(String txt) {
        this.labelText = txt;
    }

    // set color-modes
    public void setColorGray() {
        colorMode = 0;
    }

    public void setColorGreenRed() {
        colorMode = 1;
    }

    public void setColorTheme1() {
        colorMode = 2;
    }

    // GETTER
    /*Mode:
    0:  gray linear
    1:  red to green log
    2:  theme 1
     */
    public Color colorFactory(double val) {
        Color col;
        switch (colorMode) {
            case 0:
                col = this.colorGray(val);
                break;
            case 1:
                col = this.colorGreenRed(val);
                break;
            case 2:
                col = this.colorTheme1(val);
                break;
            default:
                col = Color.WHITE;
        }
        return col;
    }

    // CREATE LEGEND
    // draw log-scale elements
    private void drawLegendLog(Graphics2D g2) {
        // update panel size
        setSize();

        // array annotation, nb of segments
        double[] arrAnnot = new double[]{10, 5, 1, 0.5, 0.1, 0.05, 0.01, 0.005, 0.001};
        int nbSegm = arrAnnot.length;

        // segements
        g2.setStroke(new BasicStroke(6));
        // segment 0 to N
        for (int i = 0; i < nbSegm; i++) {
            g2.setColor(colorFactory(arrAnnot[i]));
            drawSegment(i, g2);
        }

        // text legend
        // segment 0 to N
        for (int i = 0; i < nbSegm; i++) {
            String label = String.valueOf(formatter.format(arrAnnot[i]));
            drawAnnotation(label, i - 1, g2);
        }
        // legend label
        drawLabel(g2);
    }

    // draw linear-scale elements
    private void drawLegendLin(Graphics2D g2) {
        // update panel size
        setSize();

        // nb of segments (fixed)
        int nbSegm = 6;

        // segments
        g2.setStroke(new BasicStroke(6));
        // segment 1 to N
        for (int i = nbSegm; i > 0; i--) {
            double val = this.maxValLegend / nbSegm * i;
            g2.setColor(colorFactory(val));
            drawSegment((nbSegm - i), g2);
        }

        // text legend
        String label;
        for (int i = nbSegm; i > 0; i--) {
            double val = this.maxValLegend / nbSegm * i;
            label = String.valueOf(formatter.format(val));
            drawAnnotation(label, (nbSegm - i - 1), g2);
        }
        // legend label
        drawLabel(g2);
    }

    // COLOR-FACTORIES
    // color-mode gray
    private Color colorGray(double val) {
        float bVal = 1.0f - scaleTo0_1(val);
        return Color.getHSBColor(0.6f, 0.1f, bVal);
    }

    // color-mode green to red
    private Color colorGreenRed(double val) {
        float fraction = scaleTo0_1(val);

        if (fraction >= 0f) {
            float h = fraction > 0.5f ? 0.0f : 0.33f;
//            float s = fraction > 0.5f ? (2f * fraction - 1f) : (-2f * fraction + 1f);
//            float b = fraction > 0.5f ? (1f * fraction) : (-1f * fraction + 1f);
            float s = 1.0f;
            float b = fraction > 0.5f ? (2f * fraction - 1f) : (-2f * fraction + 1f);
            return Color.getHSBColor(h, s, b);
        } else {
            return Color.getHSBColor(0.0f, 0.0f, 1.0f);
        }
    }

    // color-mode angle 0 to 360
    private Color colorTheme1(double val) {
        double rad = val / 180.0 * Math.PI;
        int c1 = Math.round(255.0f * 0.5f * (float) (Math.sin(rad) + 1.0f));
        int c2 = Math.round(255.0f * 0.5f * (float) (Math.cos(rad) + 1.0f));
        return new Color(180, c1, c2);
    }

    // DRAW ELEMENTS
    // legend: draw segment nb
    private void drawSegment(int nb, Graphics2D g2) {

        // positon: right upper corner
        int posX = getPosX0();
        int posY = getPosY0() + TITEL_SIZE + nb * LEN_SEGM;

        g2.drawLine(posX, posY,
                posX, posY + LEN_SEGM);

    }

    // legend: draw text for segment nb
    private void drawAnnotation(String txt, int nb, Graphics2D g2) {
        // rel. offset to segment
        int offsX = 5;
        int offsY = 12;
        // font style
        g2.setColor(Color.black);
        g2.setFont(new Font("Dialog", Font.PLAIN, 11));
        // positon: right upper corner -> minus size of text length
        int strWidth = g2.getFontMetrics().stringWidth(txt);
        int posX = getPosX0() - strWidth - offsX;
        int posY = getPosY0() + offsY + TITEL_SIZE + (nb + 1) * LEN_SEGM;

        g2.drawString(txt, posX, posY);
    }

    // legend: draw legend label
    private void drawLabel(Graphics2D g2) {
        // offset from origin
        int offsX = 0;
        int offsY = -12;

        // set font
        g2.setFont(new Font("Dialog", Font.PLAIN, TITEL_SIZE));
        // white background
        g2.setColor(Color.WHITE);
        int strWidth = g2.getFontMetrics().stringWidth(labelText);
        int posX = getPosX0() - strWidth - offsX;
        int posY = getPosY0() + offsY;
        g2.fillRect(posX - 2, posY + 2, strWidth + 4, TITEL_SIZE + 2);
        // draw text
        g2.setColor(Color.black);
        posY += TITEL_SIZE;
        g2.drawString(labelText, posX, posY);
    }

    // PRIVATE FUNCTIONS
    // recalculate size
    private void setSize() {
        this.panelWidth = imgPanel.getWidth();
        this.panelHeight = imgPanel.getHeight();
    }

    // panel conrner
    private int getPosX0() {
        // positon: right upper corner
        return panelWidth - OFFS_BORDER;
    }

    private int getPosY0() {
        // positon: right upper corner
        return OFFS_BORDER;
    }

    // scale value to interval 0.0 - 1.0
    private float scaleTo0_1(double val) {
        float scaledVal;
        double minVal = this.minValLegend;
        double maxVal = this.maxValLegend;
        // limit input to bounds
        val = val > maxVal ? maxVal : val;
        val = val < minVal ? minVal : val;

        if (isLog) {
            // scale log-scale
            minVal = Math.log(minVal);
            maxVal = Math.log(maxVal);
            scaledVal = (float) (1 / (maxVal - minVal) * Math.log(val) + minVal / (minVal - maxVal));
        } else {
            // scale linear scale
            scaledVal = (float) (1 / (maxVal - minVal) * val + minVal / (minVal - maxVal));
        }

        return scaledVal;
    }
}
