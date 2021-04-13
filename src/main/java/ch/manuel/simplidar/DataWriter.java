// Autor: Manuel Schmocker
// Datum: 13.04.2021
package ch.manuel.simplidar;

// Class for data export
import ch.manuel.simplidar.raster.RasterManager;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataWriter {

    public static void savePoints() {
        String nameDatei = getSaveFileDialog(new java.awt.Frame(), "Datei speichern", "D:\\", "POINTS.TXT");

        //Speichervorgang wird nur fortgesetzt, wenn der Pfad OK ist
        if (!(nameDatei == null)) {
//            //Endung ergänzen, falls notwendig
//            String endDatei = nameDatei.substring(nameDatei.length() - Structure.FILE_EXT.length(), nameDatei.length());
//            if ( !endDatei.equals(Structure.FILE_EXT) ){
//                nameDatei = nameDatei + Structure.FILE_EXT;
//            }

            DecimalFormat myFormatter = new DecimalFormat("0.000");

            // Textausgabe in die gewählte Datei
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(nameDatei));

                int nbRows = RasterManager.mainRaster.getNbRows();
                int nbCols = RasterManager.mainRaster.getNbCols();

                for (int i = 0; i < nbRows; i++) {
                    for (int j = 0; j < nbCols; j++) {
                        if (RasterManager.mainRaster.getElementBool(i, j)) {
                            double x = RasterManager.mainRaster.getPoint(j, i).getX();
                            double y = RasterManager.mainRaster.getPoint(j, i).getY();
                            double z = RasterManager.mainRaster.getPoint(j, i).getZ();

                            String str = myFormatter.format(x) + "\t"
                                    + myFormatter.format(y) + "\t"
                                    + myFormatter.format(z);
                            // write line
                            bw.write(str);
                            bw.newLine();

                        }

                    }
                }
                bw.close();

            } catch (IOException ex) {
                Logger.getLogger(DataWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // Dialog zum Speichern der Datei (wird von der Methode "saveFile()" aufgerufen
    private static String getSaveFileDialog(java.awt.Frame f, String title, String defDir, String fileType) {
        java.awt.FileDialog fd = new java.awt.FileDialog(f, title, java.awt.FileDialog.SAVE);
        fd.setFile(fileType);
        fd.setDirectory(defDir);
        fd.setLocation(50, 50);
        fd.setVisible(true);
        if (fd.getDirectory() == null) {
            return null;
        } else {
            return fd.getDirectory() + fd.getFile();
        }
    }
}
