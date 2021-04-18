// Autor: Manuel Schmocker
// Datum: 13.04.2021
package ch.manuel.simplidar;

// Class for data export
import ch.manuel.simplidar.gui.MainFrame;
import ch.manuel.simplidar.raster.RasterManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

// Class: Write data (Points) to file
public class DataWriter implements Runnable {

    // class attributes
    private Thread thr1;
    private static File saveFile;

    @Override
    public void run() {
        startWriting();
    }

    public void savePoints() {
        // save dialog
        openFileDialog();

        // start write thread
        thr1 = new Thread(this);
        thr1.start();
    }

    // PRIVATE FUNCTIONS
    private static void openFileDialog() {
        saveFile = getSaveFileDialog(new java.awt.Frame(), "Datei speichern", "D:\\");
    }

    private static void startWriting() {

        //Speichervorgang wird nur fortgesetzt, wenn der Pfad OK ist
        if (!(saveFile == null)) {
            DecimalFormat myFormatter = new DecimalFormat("0.000");

            // Textausgabe in die gewählte Datei
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(saveFile));

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
                    MainFrame.setText("Save " + Math.round(i * 100f / nbRows) + "%");
                }
                bw.close();

            } catch (IOException ex) {
                Logger.getLogger(DataWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // Dialog zum Speichern der Datei (wird von der Methode "saveFile()" aufgerufen
    private static File getSaveFileDialog(java.awt.Frame f, String title, String defDir) {
        String fileEnding = ".txt";
        java.awt.FileDialog fd = new java.awt.FileDialog(f, title, java.awt.FileDialog.SAVE);
        fd.setFile("*" + fileEnding);
        fd.setDirectory(defDir);
        fd.setLocation(50, 50);
        fd.setVisible(true);
        if (fd.getDirectory() == null) {
            return null;
        } else {
            String fileName = fd.getFile();
            if (!fileName.endsWith(fileEnding)) {
                fileName = fileName + fileEnding;
            }

            return new File(fd.getDirectory(), fileName);
        }
    }

}
