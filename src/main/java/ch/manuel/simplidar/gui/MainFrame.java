//Autor: Manuel Schmocker
//Datum: 13.02.2021
package ch.manuel.simplidar.gui;

import ch.manuel.simplidar.calculation.Calculation;
import ch.manuel.simplidar.raster.RasterManager;
import java.text.DecimalFormat;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class MainFrame extends javax.swing.JFrame {

    // frame: "analyseFrame"
    private static AnalyseFrame aFrame;         // analyseFrame
    private static RasterFrame rFrame;          // rasterFrame (merge + cut raster)

    // CONSTRUCTOR
    public MainFrame() {
        initComponents();
    }

    // PUBLIC FUNCTIONS
    // set text in field
    public static void setText(String txt) {
        MainFrame.jTextArea1.setText(txt);
    }

    // show raster values
    public static void showRasterValues() {
        DecimalFormat myFormatter = new DecimalFormat("###,###");
        String nbCols = myFormatter.format(RasterManager.mainRaster.getNbCols());
        String nbRows = myFormatter.format(RasterManager.mainRaster.getNbRows());
        String xMin = myFormatter.format(RasterManager.mainRaster.getXmin());
        String xMax = myFormatter.format(RasterManager.mainRaster.getXmax());
        String yMin = myFormatter.format(RasterManager.mainRaster.getYmin());
        String yMax = myFormatter.format(RasterManager.mainRaster.getYmax());
        
        // text raster-data
        String rasterData;
        rasterData = "Anzahl Spalten:\t" + nbCols;
        rasterData += "\nAnzahl Linien:\t" + nbRows;
        rasterData += "\nZellgr\u00f6sse:\t" + String.valueOf(RasterManager.mainRaster.getCellsize());
        rasterData += "\nNoData-Wert:\t" + String.valueOf(String.valueOf(RasterManager.mainRaster.getNoDataVal()));
        rasterData += "\nGrenzen X-Min: " + xMin + " / X-Max: " + xMax;        
        rasterData += "\nGrenzen Y-Min: " + yMin + " / Y-Max: " + yMax;        
        MainFrame.jTextArea2.setText(rasterData);
        
        // set spinner values
        SpinnerModel sm = new SpinnerNumberModel(1, 1, RasterManager.mainRaster.getNbCols(), 1);  //default value,lower bound,upper bound,increment by
        MainFrame.jSpinner21.setModel(sm);
        sm = new SpinnerNumberModel(1, 1, RasterManager.mainRaster.getNbRows(), 1);               //default value,lower bound,upper bound,increment by
        MainFrame.jSpinner22.setModel(sm);
    }
    
    // close RasterFrame
    public static void closeRasterFrame() {
        rFrame.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton2 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jSpinner21 = new javax.swing.JSpinner();
        jSpinner22 = new javax.swing.JSpinner();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem21 = new javax.swing.JMenuItem();
        jMenuItem22 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("simpLIDAR");
        setResizable(false);

        jButton1.setText("Analyse");
        jButton1.setPreferredSize(new java.awt.Dimension(120, 32));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(4);
        jTextArea1.setText("Status...");
        jScrollPane1.setViewportView(jTextArea1);

        jButton2.setText("Daten laden");
        jButton2.setPreferredSize(new java.awt.Dimension(120, 32));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel11.setText("Daten Raster");

        jLabel12.setText("Status");

        jLabel21.setText("Spalte wählen");

        jLabel22.setText("Reihe wählen:");

        jSpinner21.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        jSpinner22.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        jMenu1.setText("Datei");

        jMenuItem11.setText("ASC-File laden");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem11);

        jMenuItem12.setText("geoTiff laden");
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem12);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Werkzeuge");

        jMenuItem21.setText("Rasteranalyse");
        jMenuItem21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem21ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem21);

        jMenuItem22.setText("Raster kombinieren");
        jMenuItem22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem22ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem22);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel21))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                            .addComponent(jScrollPane2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 196, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSpinner21, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jSpinner22, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSpinner21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSpinner22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // to be implemented

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jMenuItem21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem21ActionPerformed
        // show AnalyseFrame
        MainFrame.openAnalyseFrame();
    }//GEN-LAST:event_jMenuItem21ActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        // Load file: Asc
        RasterManager.loadMainRasterFromAsc();
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        // Load file: Tiff
        RasterManager.loadMainRasterFromTiff();
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem22ActionPerformed
        // show RasterFrame
        MainFrame.openRasterFrame();
    }//GEN-LAST:event_jMenuItem22ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JButton jButton1;
    private static javax.swing.JButton jButton2;
    private static javax.swing.JLabel jLabel11;
    private static javax.swing.JLabel jLabel12;
    private static javax.swing.JLabel jLabel21;
    private static javax.swing.JLabel jLabel22;
    private static javax.swing.JMenu jMenu1;
    private static javax.swing.JMenu jMenu2;
    private static javax.swing.JMenuBar jMenuBar1;
    private static javax.swing.JMenuItem jMenuItem11;
    private static javax.swing.JMenuItem jMenuItem12;
    private static javax.swing.JMenuItem jMenuItem21;
    private static javax.swing.JMenuItem jMenuItem22;
    private static javax.swing.JScrollPane jScrollPane1;
    private static javax.swing.JScrollPane jScrollPane2;
    private static javax.swing.JSpinner jSpinner21;
    private static javax.swing.JSpinner jSpinner22;
    private static javax.swing.JTextArea jTextArea1;
    private static javax.swing.JTextArea jTextArea2;
    // End of variables declaration//GEN-END:variables

    // PRIVATE FUNCTIONS
    // open frame "AnalyseFrame"
    private static void openAnalyseFrame() {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                aFrame = new AnalyseFrame();
                aFrame.setVisible(true);
            }
        });
    }
    
    // open frame "AnalyseFrame"
    private static void openRasterFrame() {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                rFrame = new RasterFrame();
                rFrame.setVisible(true);
            }
        });
    }
}
