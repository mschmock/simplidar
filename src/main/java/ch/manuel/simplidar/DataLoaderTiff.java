package ch.manuel.simplidar;


import ch.manuel.simplidar.gui.MainFrame;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferFloat;
import java.io.File;
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
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DataLoaderTiff{
    
    // class attributes
    private static String file;
    private static boolean fileOK;
    
    
    public static void openTiff() {
        file = getOpenFileDialog("Datei öffnen", "D:\\Temp\\LIDAR\\LV95");
        
        //readAndDisplayMetadata();
        readTiff();
    }

    // PRIVATE FUNCTIONS
    private static void readTiff() {
        String errMsg = "Alles OK";
        try {
            // check header
            ImageInputStream iis = ImageIO.createImageInputStream(new File(file));
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            if (readers.hasNext()) {
                // pick the first available ImageReader
                ImageReader reader = readers.next();
                // attach source to the reader
                reader.setInput(iis, true);
                
                // size
                System.out.println( "nb cols: " + reader.getHeight(0));
                System.out.println( "Y-Wert: " + reader.getWidth(0));
                
                // read metadata of first image
                IIOMetadata metadata = reader.getImageMetadata(0);

                TIFFDirectory ifd = TIFFDirectory.createFromMetadata​(metadata);
                TIFFField val = ifd.get​TIFFField(33922); // <TIFFField number="33922" name="ModelTiepointTag">
/*              <TIFFDouble value="0.0"/>           Pixel x1
                <TIFFDouble value="0.0"/>           Pixel y1
                <TIFFDouble value="0.0"/>
                <TIFFDouble value="2631000.0"/>     --> X Wert für Pixel x1
                <TIFFDouble value="1172000.0"/>     --> Y-Wert für Pixel y1
                <TIFFDouble value="0.0"/> */

                System.out.println( "X-Wert: " + val.getAsDouble(3));
                System.out.println( "Y-Wert: " + val.getAsDouble(4));

                TIFFField val2 = ifd.get​TIFFField(33550); // <TIFFField number="33550" name="ModelPixelScaleTag">
/*              <TIFFDouble value="0.5"/>       --> Rastergrösse X
                <TIFFDouble value="0.5"/>       --> Rastergrösse Y
                <TIFFDouble value="0.0"/>  */
                
                System.out.println( "cellsize X: " + val2.getAsDouble(0));
                System.out.println( "cellsize Y: " + val2.getAsDouble(1));
                
                // read tiff
                DataBuffer dataBuffer = reader.read(0).getRaster().getDataBuffer();
                
                DataBufferFloat dataBufferFloat = null;
                if (dataBuffer instanceof DataBufferFloat) {
                    dataBufferFloat = (DataBufferFloat)dataBuffer;
                    
                    
                } else {
                    System.out.println("No DataBufferByte");
                }
                
                float data[] = dataBufferFloat.getData();
                
                System.out.println(data[254]);
                
            }
        } catch (IOException e) {
            fileOK = false;
            errMsg = e.getMessage();
                        
        }
        // show text in gui
        MainFrame.setText(errMsg);
    }
    
    
    private static void readTiffalt() {
        BufferedImage image;
        
        try {
            image = ImageIO.read(new File(file));
            System.out.println(image);

            DataBuffer dataBuffer = image.getRaster().getDataBuffer();
            System.out.println( dataBuffer.toString());
            DataBufferFloat dataBufferFloat = null;
            if (dataBuffer instanceof DataBufferFloat)
            {
                dataBufferFloat = (DataBufferFloat)dataBuffer;
            }
            else
            {
                System.out.println("No DataBufferByte");
                return;
            }

            int w = image.getWidth();
            int h = image.getHeight();

            float data[] = dataBufferFloat.getData();
//            float[] pxl = new float[1];
//            System.out.println(image.getRaster().getPixel(1, 1, pxl));

            for (int y=0; y<h; y++)
            {
                for (int x=0; x<w; x++)
                {
                    int index = x + y * w;
//
//                    System.out.println(dataBuffer.getElemFloat(index));
                    float val = data[index];
                    System.out.println("At "+x+" "+y+" index is "+val);

                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DataLoaderTiff.class.getName()).log(Level.SEVERE, null, ex);
        }
        
   
    }
    
    private static void readAndDisplayMetadata() {
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(new File(file));
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
                    System.out.println( "Format name: " + names[ i ] );
                    //displayMetadata(metadata.getAsTree(names[i]));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void displayMetadata(Node root) {
        displayMetadata(root, 0);
    }

   private static void indent(int level) {
        for (int i = 0; i < level; i++)
            System.out.print("    ");
    }
    
    private static void displayMetadata(Node node, int level) {
        // print open tag of element
        indent(level);
        System.out.print("<" + node.getNodeName());
        NamedNodeMap map = node.getAttributes();
        if (map != null) {

            // print attribute values
            int length = map.getLength();
            for (int i = 0; i < length; i++) {
                Node attr = map.item(i);
                System.out.print(" " + attr.getNodeName() +
                                 "=\"" + attr.getNodeValue() + "\"");
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
    
    
    // Dialog zum Speichern der Datei (wird von der Methode "saveFile()" aufgerufen
    private static String getOpenFileDialog(String title, String defDir) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setCurrentDirectory(new File( defDir ));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY );
 
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Tiff Files", "tif"));
//        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("xyz Files", "xyz", "txt"));
 
        fileChooser.setAcceptAllFileFilterUsed(true);
 
        int result = fileChooser.showOpenDialog(null);
 
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        } else {
            return null;
        }
    }
}