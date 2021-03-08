//Autor: Manuel Schmocker
//Datum: 02.04.2020

package ch.manuel.simplidar.geodata;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//Klasse zum Verwalten der Geodaten (Gemeindegrenzen, Einwohner, Altersklassen)
public class GeoData {
    
// Class attributes
    private static int nbMunicip;                       // Anzahl Gemeinden
    private static int minX;                            // min X-Koordinate   
    private static int minY;                            // min Y-Koordinate
    private static int maxX;                            // max X-Koordinate
    private static int maxY;                            // max Y-Koordinate
    
    private static List<Municipality> listMunicip;              // Liste mit Gemeinden: Klasse Municipality.java
    private static Map<Integer,Municipality> mapID;             // map with id (BFS-ID in Daten)
    
    // Constructor
    public GeoData() {
        listMunicip = new ArrayList<>();
        mapID = new HashMap<Integer,Municipality>();
    }
    
    // Methode
    // Gemeinde hinzufügen
    public void addMunicip( String name) {
        // create new municipality
        listMunicip.add(new Municipality( name ) );
        // update index list
        int lastIndex = GeoData.getLastIndex();
        GeoData.listMunicip.get( lastIndex ).setIndex( lastIndex );
        nbMunicip = listMunicip.size();
    }
    
    // calculate values for bounds: minX, maxX, minY, maxY
    public void calculateBounds() {
        minX = listMunicip.get(0).getMinE();
        maxX = listMunicip.get(0).getMaxE();
        minY = listMunicip.get(0).getMinN();
        maxY = listMunicip.get(0).getMaxN();
        
        if( nbMunicip > 1 ) {
            for (int i = 1; i < nbMunicip; i++) {
                //minX
                minX = ( minX < listMunicip.get(i).getMinE() ) ? minX : listMunicip.get(i).getMinE();
                //maxX
                maxX = ( maxX > listMunicip.get(i).getMaxE() ) ? maxX : listMunicip.get(i).getMaxE();
                //minY
                minY = ( minY < listMunicip.get(i).getMinN() ) ? minY : listMunicip.get(i).getMinN();
                //maxY
                maxY = ( maxY > listMunicip.get(i).getMaxN() ) ? maxY : listMunicip.get(i).getMaxN();
            }
        }
    }  
    
    // SETTER
    // map id with municipality
    public void setID(int id, Municipality obj) {
        mapID.put( id, obj);
        obj.setID(id);
    }
    
    // GETTER
    public static Municipality getLastElement() {
        return GeoData.listMunicip.get(nbMunicip - 1);
    }
    public static Municipality getMunicip(int index) {
        return GeoData.listMunicip.get(index);
    }
    public Municipality getMunicipByID(int id) {
        return mapID.get(id);
    }
    private static int getLastIndex() {
        return GeoData.listMunicip.size() - 1;
    }
    public static int getNbMunicip() {
        return GeoData.nbMunicip;
    }
    public int getBoundX() {
        return GeoData.minX;
    }
    public int getBoundY() {
        return GeoData.minY;
    }
    public int getWidth() {
        return (GeoData.maxX - GeoData.minX);
    }
    public int getHeight() {
        return (GeoData.maxY - GeoData.minY);
    }
    
    // TEST
    public void testprint() {
        for (int i = 0; i < nbMunicip; i++) {
            Municipality tmpMunicip = listMunicip.get(i);
            System.out.println( "Name: " + tmpMunicip.getName() + " - ID: " + tmpMunicip.getID() );
            System.out.println( "Zentrum: " + tmpMunicip.getCenterE() + "E / " + listMunicip.get(i).getCenterN() + "N");
            System.out.println( "___" );
        }
    }
}
