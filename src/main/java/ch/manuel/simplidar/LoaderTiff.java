package ch.manuel.simplidar;

import ch.manuel.simplidar.gui.MainFrame;
import ch.manuel.simplidar.raster.Raster;
import ch.manuel.utilities.MyUtilities;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferFloat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.tiff.TIFFDirectory;
import javax.imageio.plugins.tiff.TIFFField;
import javax.imageio.stream.ImageInputStream;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class LoaderTiff {

    // class attributes
    private File tiffFile;
    private final Raster raster;
    // load thread and object
    private Thread t1;                          // LOAD file thread
    private LoaderTiff.LoadThread loader;       // subclass with runnable --> load file
    // check header & file: -> return true if ok
    private boolean fileOK;

    // CONSTRUCTOR
    public LoaderTiff(Raster raster) {
        this.raster = raster;
        loader = new LoaderTiff.LoadThread();
    }

    // PUBLIC FUNCTIONS
    // get header informations
    public boolean getHeader() {
        if (this.tiffFile != null) {
            readHeader();
        } else {
            // no file defined
            fileOK = false;
            // show text in gui
            MainFrame.setText("keine Datei ausgew\u00e4hlt");
        }
        return fileOK;
    }

    // open file in thread
    public void openFile() {
        t1 = new Thread(loader);
        t1.start();
    }
    
    // wait for thread
    public void joinThread() {
        try {
            t1.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(LoaderASC.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // set file
    public void setFile(File file) {
        this.tiffFile = file;
    }

    // open file chooser
    public void chooseFile() {
        openFileDialog();
    }

    // PRIVATE FUNCTIONS
    // open file dialog
    private void openFileDialog() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("geoTiff-Datei", "tif");
        String defPath = DataLoader.getXMLdata("defaultPath");
        File file = MyUtilities.getOpenFileDialog("Datei \u00d6ffnen", defPath, filter);
        if (file != null) {
            // set file path
            this.tiffFile = file;
        }
    }

    // reset header status
    private void resetFileStatus() {
        fileOK = false;
    }

    // ____READ FILE: 1. ONLY HEADER
    // check header of file
    private void readHeader() {
        // status message
        String statusMsg = "Header OK";
        fileOK = true;

        // set ImageIO reader
        try {
            // check header
            ImageInputStream iis = ImageIO.createImageInputStream(tiffFile);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            if (readers.hasNext()) {
                // pick the first available ImageReader
                ImageReader reader = readers.next();
                // attach source to the reader
                reader.setInput(iis, true);

                // size, pixel width, height
                int pixelW = reader.getWidth(0);
                int pixelH = reader.getHeight(0);
                raster.setNbCols(pixelW);
                raster.setNbRows(pixelH);

                // read metadata of first image
                IIOMetadata metadata = reader.getImageMetadata(0);

                // coordinate of reference
                TIFFDirectory ifd = TIFFDirectory.createFromMetadata​(metadata);
                TIFFField val = ifd.get​TIFFField(33922); // <TIFFField number="33922" name="ModelTiepointTag">
/*              <TIFFDouble value="0.0"/>           Pixel x1
                <TIFFDouble value="0.0"/>           Pixel y1
                <TIFFDouble value="0.0"/>
                <TIFFDouble value="2631000.0"/>     --> X Wert für Pixel x1
                <TIFFDouble value="1172000.0"/>     --> Y-Wert für Pixel y1
                <TIFFDouble value="0.0"/> */
                double xMin = 0;
                double yMax = 0;
                if (val != null) {
                    xMin = val.getAsDouble(3);
                    yMax = val.getAsDouble(4);
                } else {
                    fileOK = false;
                    statusMsg = "Datei hat nicht das korrekte Format.";
                }

                // cellsize
                TIFFField val2 = ifd.get​TIFFField(33550); // <TIFFField number="33550" name="ModelPixelScaleTag">
/*              <TIFFDouble value="0.5"/>       --> Rastergrösse X
                <TIFFDouble value="0.5"/>       --> Rastergrösse Y
                <TIFFDouble value="0.0"/>  */
                double cellsize = val2.getAsDouble(0);
                raster.setCellsize(cellsize);
                if (val2.getAsDouble(0) != val2.getAsDouble(1)) {
                    statusMsg += "\nWarnung: Zellgrösse X und Y sind unterschiedlich";
                }

                // set Bounds
                double xMax = xMin + pixelH * cellsize;
                double yMin = yMax - pixelW * cellsize;
                raster.setBounds(xMin, xMax, yMin, yMax);

                // number of bands
                int nbBands = reader.read(0).getRaster().getNumBands();
                if (nbBands != 1) {
                    fileOK = false;
                    statusMsg = "\nDatei darf nur 1 Band enthalten.";
                    statusMsg += "\nAnzahl B\u00e4nder:" + nbBands;
                }

            } else {
                fileOK = false;
                statusMsg = "Datei nicht gefunden!";
            }
        } catch (FileNotFoundException e) {
            fileOK = false;
            statusMsg = "Datei nicht gefunden!";
        } catch (IOException e) {
            fileOK = false;
            statusMsg = e.getMessage();
        }
        // show text in gui
        MainFrame.setText(statusMsg);
    }

    // ____READ FILE: 2. WHOLE FILE
    // read file
    private void readFile() {
        String statusMsg = "Alles OK";

        // set ImageIO reader
        try {
            // check header
            ImageInputStream iis = ImageIO.createImageInputStream(tiffFile);
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            if (readers.hasNext()) {
                // pick the first available ImageReader
                ImageReader reader = readers.next();
                // attach source to the reader
                reader.setInput(iis, true);
                DataBuffer dataBuffer = reader.read(0).getRaster().getDataBuffer();
                DataBufferFloat dataBufferFloat = null;
                if (dataBuffer instanceof DataBufferFloat) {
                    dataBufferFloat = (DataBufferFloat) dataBuffer;

                    // copy data to array
                    int pixelW = reader.getWidth(0);
                    int pixelH = reader.getHeight(0);
                    float data[] = dataBufferFloat.getData();

                    for (int y = 0; y < pixelH; y++) {
                        for (int x = 0; x < pixelW; x++) {
                            raster.setElement(y, x, data[x + y * pixelW]);
                        }
                        // show progress
                        MainFrame.setText("Fortschritt: " + (int) (y * 100.0 / pixelH) + " %");
                    }

                    // status
                    statusMsg += "\nLaden abgeschlossen.";

                } else {
                    fileOK = false;
                    statusMsg = "Datentyp muss Float sein!";
                }
            }
        } catch (IOException e) {
            fileOK = false;
            statusMsg = e.getMessage();

        }
        // show text in gui
        MainFrame.setText(statusMsg);
    }

    // *********************************************
    // inner class with separate thrad
    // loader for tiff-file
    private class LoadThread implements Runnable {

        @Override
        public void run() {
            LoaderTiff.this.readFile();
        }
    }

    // inner class:
    // show tiff tags
    private class TiffTagViewer {

        private void readAndDisplayMetadata() {
            try {
                ImageInputStream iis = ImageIO.createImageInputStream(LoaderTiff.this.tiffFile);
                Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

                if (readers.hasNext()) {

                    // pick the first available ImageReader
                    ImageReader reader = readers.next();

                    // attach source to the reader
                    reader.setInput(iis, true);

                    // read metadata of first image
                    IIOMetadata metadata = reader.getImageMetadata(0);

                    String[] names = metadata.getMetadataFormatNames();
                    int length = names.length;
                    for (int i = 0; i < length; i++) {
                        System.out.println("Format name: " + names[i]);
                        displayMetadata(metadata.getAsTree(names[i]));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void displayMetadata(Node root) {
            displayMetadata(root, 0);
        }

        private void indent(int level) {
            for (int i = 0; i < level; i++) {
                System.out.print("    ");
            }
        }

        private void displayMetadata(Node node, int level) {
            // print open tag of element
            indent(level);
            System.out.print("<" + node.getNodeName());
            NamedNodeMap map = node.getAttributes();
            if (map != null) {

                // print attribute values
                int length = map.getLength();
                for (int i = 0; i < length; i++) {
                    Node attr = map.item(i);
                    System.out.print(" " + attr.getNodeName()
                            + "=\"" + attr.getNodeValue() + "\"");
                }
            }

            Node child = node.getFirstChild();
            if (child == null) {
                // no children, so close element and return
                System.out.println("/>");
                return;
            }

            // children, so close current tag
            System.out.println(">");
            while (child != null) {
                // print children recursively
                displayMetadata(child, level + 1);
                child = child.getNextSibling();
            }

            // print close tag of element
            indent(level);
            System.out.println("</" + node.getNodeName() + ">");
        }
    }
}
