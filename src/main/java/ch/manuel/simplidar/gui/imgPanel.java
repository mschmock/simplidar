//Autor: Manuel Schmocker
//Datum: 13.02.2021
package ch.manuel.simplidar.gui;

import java.awt.Graphics;
import javax.swing.JPanel;

public class imgPanel extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(AnalyseFrame.getImg(), 0, 0, null);

    }
}
