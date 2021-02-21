//Autor: Manuel Schmocker
//Datum: 19.02.2021
package ch.manuel.simplidar.calculation;

import ch.manuel.simplidar.gui.MainFrame;
import ch.manuel.simplidar.raster.DataManager;

public class Calculation {

    // test message
    private static String msg;

    public static void testCalc(int x, int y) {

        Point point = DataManager.mainRaster.getPoint(x - 1, y - 1);

        msg = "X-Koordinate: " + point.getX() + "\n";
        msg += "Y-Koordinate: " + point.getY() + "\n";
        msg += "Z-Koordinate: " + point.getZ() + "\n";

        // test 
        Calculation.createTriangle();

        MainFrame.setText(msg);
    }

    private static void createTriangle() {
        Point p1 = new Point(2, 7, 0);
        Point p2 = new Point(0, 1, 9);
        Point p3 = new Point(2, 0, 9);

        Triangle triangle = new Triangle(p1, p2, p3);
        msg += triangle.showNormal() + "\n";

        msg += "Abstand Punkt:\n";
        Point pP = new Point(5, -3, -4);
        msg += "Berechnet: " + triangle.distPoint(pP);

    }
}
