//Autor: Manuel Schmocker
//Datum: 02.04.2020

package ch.manuel.simplidar.graphics;

import ch.manuel.utilities.MyUtilities;
import java.util.ArrayList;
import java.util.List;

// Klasse zum Verwalten der Daten pro Gemeinde
public class Municipality {
    // Class attributes
    private String name;                        // Name Gemeinde
    private int id;                             // BFS id nummer
    private int index;                          // index in Liste (Class GeoData)
    private int centerN;                        // Zentrum: Koordinate LV 95 N
    private int centerE;                        // Zentrum: Koordinate LV 95 E
    private int minN;                           // Umgrenzung: min Koordinate LV 95 N
    private int maxN;                           // Umgrenzung: max Koordinate LV 95 N
    private int minE;                           // Umgrenzung: min Koordinate LV 95 E
    private int maxE;                           // Umgrenzung: max Koordinate LV 95 E
    // Gemeindegrenzen
    private List<int[]> polyX;                  // Polygone Gemeindegrenze, X-Werte
    private List<int[]> polyY;                  // Polygone Gemeindegrenze, Y-Werte
    
    // CONSTRUCTOR
    public Municipality(String name) {
        this.name = name;
        
        // initialisieren
        polyX = new ArrayList<>();
        polyY = new ArrayList<>();
    }
    
    // SETTER
    public void setIndex(int index) {
        this.index = index;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setID(int id) {
        this.id = id;
    }
    public void setCenterN(int centerN) {
        this.centerN = centerN;
    }
    public void setCenterE(int centerE) {
        this.centerE = centerE;
    }
    public void setMinN(int minN) {
        this.minN = minN;
    }
    public void setMaxN(int maxN) {
        this.maxN = maxN;
    }
    public void setMinE(int minE) {
        this.minE = minE;
    }
    public void setMaxE(int maxE) {
        this.maxE = maxE;
    }

    public boolean setPolygon(String polygon) {
        
        boolean isValid = true;
        
        // READ INPUT: "x1 y1, x2 y2, ..."
        // Gemeinde mit mehreren Polygons:
        // separator: ")), (("
        if( polygon.contains(")), ((") ) {

            String[] polygonSep = polygon.split("\\)\\), \\(\\(");
            // loop through polygons
            for( String poly : polygonSep ) {
                // parse polygon: stringToPoly
                if( !stringToPoly( poly ) ) {
                    isValid = false;
                    break;
                }
            }
        // no separator -> nur ein polygon
        } else {
            // parse polygon: stringToPoly
            isValid = stringToPoly( polygon );
        }
        
        return isValid;
    }
     
    // GETTER
    public int getIndex() {
        return this.index;
    }
    public String getName() {
        return this.name;
    }
    public int getID() {
        return this.id;
    }
    public int getCenterN() {
        return this.centerN;
    }
    public int getCenterE() {
        return this.centerE;
    }
    public int getMinN() {
        return this.minN;
    }
    public int getMaxN() {
        return this.maxN;
    }
    public int getMinE() {
        return this.minE;
    }
    public int getMaxE() {
        return this.maxE;
    }
    public List<int[]> getPolyX() {
        return this.polyX;
    }
    public List<int[]> getPolyY() {
        return this.polyY;
    }
    
    // PRIVATE FUNCTIONS
    private boolean stringToPoly(String polygon) {
            
        boolean isValid = true;
        
        // Trennzeichen für Seen: "), (" -> ignorieren
        if( polygon.contains("), (") ) {
            // nur 1. Element verwenden
            polygon = polygon.split("\\), \\(")[0];
        }
        
        // temp arry with x and y values
        List<Integer> xVals = new ArrayList<>();
        List<Integer> yVals = new ArrayList<>();

        // Split ","
        if( polygon.contains(",") ) {
            String[] points = polygon.split(",");

            // Split " "
            for( String a : points) {
                if( a.contains(" ") ) {
                    String[] b = a.trim().split("\\s+");

                    // only two elements
                    if( b.length != 2 ) {return false;}

                    // test if numeric
                    // x-value
                    if( MyUtilities.isNumeric( b[0] )) {
                        xVals.add((int) Double.parseDouble( b[0] ) );
                    } else {
                        // not valid
                        return false;

                    }
                    // y-value
                    if( MyUtilities.isNumeric( b[1] )) {
                        yVals.add((int) Double.parseDouble( b[1] ) );
                    } else {
                        // not valid
                        return false;
                    }
                } else {
                    // not valid
                    return false;
                }
            }
        } else {
            //not valid
            return false;
        }

        // ok? 
        // store data in member variable
        if( isValid ) {
            this.polyX.add( xVals.stream().mapToInt(Integer::intValue).toArray() );
            this.polyY.add( yVals.stream().mapToInt(Integer::intValue).toArray() );
            //this.polyX = xVals.toArray( new Integer[xVals.size()] );
            //this.polyY = yVals.toArray( new Integer[yVals.size()] );

        }

        return isValid;
    }
    
}
