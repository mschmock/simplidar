package ch.manuel.simplidar;


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
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DataLoaderTiff{
    private static String file;
    
    public static void openTiff() {
        file = getOpenFileDialog("Datei öffnen", "D:\\Temp\\LIDAR\\LV95");
        readAndDisplayMetadata();
        //readTiff();
    }

    private static void readTiff() {
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
    
    private static void readAndDisplayMetadata() {
        try {

            ImageInputStream iis = ImageIO.createImageInputStream(new File(file));
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            if (readers.hasNext()) {

                // pick the first available ImageReader
                ImageReader reader = readers.next();
                String metaData1 = getNumChannels(reader);
                System.out.println(metaData1);
                
                // attach source to the reader
                reader.setInput(iis, true);

                // read metadata of first image
                IIOMetadata metadata = reader.getImageMetadata(0);

                String[] names = metadata.getMetadataFormatNames();
                int length = names.length;
                for (int i = 0; i < length; i++) {
                    System.out.println( "Format name: " + names[ i ] );
                    displayMetadata(metadata.getAsTree(names[i]));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String getNumChannels(ImageReader reader) {
        try {
            IIOMetadata imageMetadata = reader.getImageMetadata(0);
            if (imageMetadata == null) {
                return "";
            }
            IIOMetadataNode metaTree = (IIOMetadataNode) imageMetadata.getAsTree("javax_imageio_1.0");
            Element numChannelsItem = (Element) metaTree.getElementsByTagName("NumChannels").item(0);
            if (numChannelsItem == null) {
                return "";
            }
            return numChannelsItem.getAttributes().toString();
        } catch (IOException | NegativeArraySizeException e) {
            return "";
        }
    } 
    
//    IIOMetadata metadata = reader.getImageMetadata(imageIndex);
//  IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());
//  IIOMetadataNode extension = findNode(root, "GraphicControlExtension");
//  String attribute = (extension != null) ? extension.getAttribute("delayTime") : null;
    
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
}