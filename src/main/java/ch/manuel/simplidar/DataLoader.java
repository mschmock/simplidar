//Autor: Manuel Schmocker
//Datum: 07.03.2021
package ch.manuel.simplidar;

import ch.manuel.utilities.MyUtilities;
import ch.manuel.simplidar.graphics.GeoData;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

// load app datas (json + xml) from resources
public class DataLoader {

    // Membervariablen
    public static GeoData geoData;
    private static final Charset utf8 = StandardCharsets.UTF_8;
    // files in resources
    private static final String PATH_XML = "/data/appData.xml";
    private static final String PATH_GEODATA = "/data/geodata.json";
    // xml
    private static Document appDataXML;

    // CONSTRUCTOR
    public DataLoader() {
        geoData = new GeoData();
    }
    
    public void loadData() {
        boolean loadingOK = false;

        // load xml
        loadXML();
        // load geodata
        loadingOK = this.loadJSON();               // Grenzen -> geodata.json
        if (loadingOK) {
            // calculate bounds
            DataLoader.geoData.calculateBounds();

            // TEST
            // geoData.testprint();
        }
    }

    // load xml
    private boolean loadXML() {
        boolean hasErr = false;
        String errMsg = "All OK";

        try {
            // get File: appData.xml
            InputStream in = getClass().getResourceAsStream(PATH_XML);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            DataLoader.appDataXML = db.parse(in);
            DataLoader.appDataXML.normalizeDocument();

        } catch (ParserConfigurationException | SAXException | IOException ex) {
            hasErr = true;
            errMsg = "Error loading xml: " + ex.getMessage();
        }
        // print error-message
        if (hasErr) {
            MyUtilities.getErrorMsg("Error", errMsg);
            return false;
        } else {
            // no errors loading data
            return true;
        }
    }

    // load JSON: geodata (Grenzen)
    private boolean loadJSON() {

        // get File: geodata.json
        InputStream in = getClass().getResourceAsStream(PATH_GEODATA);

        // JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        // Error message
        String idName;
        String errMsg = "All OK";
        boolean hasErr = false;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, utf8))) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONArray objList = (JSONArray) obj;

            //Iterate over objects array
            Iterator<JSONObject> iterator = objList.iterator();
            while (iterator.hasNext()) {
                JSONObject tmpObj = iterator.next();
                JSONObject attriObj = (JSONObject) tmpObj.get("attributes");

                idName = tmpObj.get("id").toString();

                // INSERT DATA
                String input;
                geoData.addMunicip(attriObj.get("GMDNAME").toString());       // Gemeindename
                // ID -> in map 
                input = attriObj.get("GMDNR").toString();
                if (MyUtilities.isInteger(input)) {
                    // setID --> map with [id, object]
                    geoData.setID(Integer.parseInt(input), GeoData.getLastElement());
                } else {
                    errMsg = PATH_GEODATA + "\nFehler im Objekt " + idName + ", element: 'GMDNR'";
                    hasErr = true;
                    break;
                }
                // Zentrum: Koordinate LV 95 E
                input = attriObj.get("E_CNTR").toString();
                if (MyUtilities.isInteger(input)) {
                    GeoData.getLastElement().setCenterE(Integer.parseInt(input));
                } else {
                    errMsg = PATH_GEODATA + "\nFehler im Objekt " + idName + ", element: 'E_CNTR'";
                    hasErr = true;
                    break;
                }
                // Zentrum: Koordinate LV 95 N
                input = attriObj.get("N_CNTR").toString();
                if (MyUtilities.isInteger(input)) {
                    GeoData.getLastElement().setCenterN(Integer.parseInt(input));
                } else {
                    errMsg = PATH_GEODATA + "\nFehler im Objekt " + idName + ", element: 'N_CNTR'";
                    hasErr = true;
                    break;
                }
                // Umgrenzung: min Koordinate LV 95 N
                input = attriObj.get("N_MIN").toString();
                if (MyUtilities.isInteger(input)) {
                    GeoData.getLastElement().setMinN(Integer.parseInt(input));
                } else {
                    errMsg = PATH_GEODATA + "\nFehler im Objekt " + idName + ", element: 'N_MIN'";
                    hasErr = true;
                    break;
                }
                // Umgrenzung: max Koordinate LV 95 N
                input = attriObj.get("N_MAX").toString();
                if (MyUtilities.isInteger(input)) {
                    GeoData.getLastElement().setMaxN(Integer.parseInt(input));
                } else {
                    errMsg = PATH_GEODATA + "\nFehler im Objekt " + idName + ", element: 'N_MAX'";
                    hasErr = true;
                    break;
                }
                // Umgrenzung: min Koordinate LV 95 E
                input = attriObj.get("E_MIN").toString();
                if (MyUtilities.isInteger(input)) {
                    GeoData.getLastElement().setMinE(Integer.parseInt(input));
                } else {
                    errMsg = PATH_GEODATA + "\nFehler im Objekt " + idName + ", element: 'E_MIN'";
                    hasErr = true;
                    break;
                }
                // Umgrenzung: max Koordinate LV 95 E
                input = attriObj.get("E_MAX").toString();
                if (MyUtilities.isInteger(input)) {
                    GeoData.getLastElement().setMaxE(Integer.parseInt(input));
                } else {
                    errMsg = PATH_GEODATA + "\nFehler im Objekt " + idName + ", element: 'E_MAX'";
                    hasErr = true;
                    break;
                }
                // Polygon Gemeindegrenze
                if (!GeoData.getLastElement().setPolygon(attriObj.get("POLYGON").toString())) {
                    errMsg = PATH_GEODATA + "\nFehler im Objekt " + idName + ", element: 'POLYGON'";
                    hasErr = true;
                    break;
                }

            }

        } catch (FileNotFoundException e) {
            hasErr = true;
            errMsg = "Datei nicht gefunden!";
        } catch (IOException e) {
            hasErr = true;
            errMsg = e.getMessage();
        } catch (org.json.simple.parser.ParseException e) {
            hasErr = true;
            errMsg = "Fehlerhafte Formatierung JSON in Pos: " + e.getPosition();
        }

        // print error-message
        if (hasErr) {
            MyUtilities.getErrorMsg("Error", errMsg);
            return false;
        } else {
            // no errors loading data
            return true;
        }
    }

    public static String getXMLdata(String tagName) {
        return DataLoader.appDataXML.getElementsByTagName(tagName).item(0).getTextContent();
    }
}
