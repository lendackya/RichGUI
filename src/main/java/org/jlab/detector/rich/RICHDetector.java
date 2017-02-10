/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.detector.rich;

import org.jlab.detector.view.DetectorView2D_ML;
import org.jlab.detector.view.DetectorShape2D;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import org.jlab.groot.graphics.EmbeddedCanvas; 
import org.jlab.detector.base.DetectorType; 
import org.jlab.detector.view.DetectorPane2D;
import java.io.BufferedReader; 
import java.io.FileReader; 
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JProgressBar;
import org.jlab.groot.base.GStyle;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.data.H1F;


/**
 *
 * @author Andrew
 */
public class RICHDetector extends JFrame { 
    
    // Constants used to create MAPMT array
    public static double step1 = 5.3;
    public static double step2 = 20.1;
           
    // Normal Java Variables
    final private Integer[] HV = {1000, 1050, 1075, 1100};
    final private Integer[] OD = {13, 14, 15};
    final private String[] Parameters = {"SC", "MU", "MU*EFF20", "CHI2", "NUAV", "EFF20", "SC/YIELD", "SIGMA"};
       
    // UI Buttons and Labels - Swing
    private JPanel panel; 
    final private JButton genHisto = new JButton("Generate Histogram (All PMTS)");
    final private JButton genGraph = new JButton("Generate Graph for selected PMT");
    final private JComboBox<Integer> HV_List = new JComboBox(HV);
    final private JComboBox<Integer> OD_List = new JComboBox(OD);
    final private JComboBox<Integer> Parameter_List = new JComboBox(Parameters);
    final private JLabel HV_label = new JLabel("HV:");
    final private JLabel OD_label = new JLabel("OD:");
    final private JLabel param_label = new JLabel("Parameter:");
    
    public JTabbedPane tabbedPane;
    private CCDB_ExpReader exp; // database to get experimental values from database 
    private String selectedPMT = "";
    
    JCheckBox showGain_CheckBox = new JCheckBox("Show Gain");
            
    // Custom UI Components - JLab
    final private EmbeddedCanvas canvas = new EmbeddedCanvas(); 
    
    private DetectorView2D_ML dectView;
    private RichDetectorPane2D pane;
    
    private final DeviceNameFile deviceFile = new DeviceNameFile("src/main/resources/gains_inc.txt"); 
    
    // Action Listeners
    ActionListener checkBoxClicked = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JCheckBox checkBox = (JCheckBox) actionEvent.getSource();
            boolean selected = checkBox.isSelected();
            if (selected == true){
                
                int counter = 0; 
                // get all the shapes in the MAPMT layer
                LinkedList<DetectorShape2D> shapes = pane.getAllShapes("MAPMTs");
                  
                double min = dectView.colorAxis.getRange().getMin();
                double max = dectView.colorAxis.getRange().getMax();
                //System.out.println(min); 
                //System.out.println(max); 
                
                Color startCol = new Color(0,255,0);
                Color endCol = new Color(0,0,139);
                Color currCol = startCol; 
                // loop through all the shapes
                for (int i = 0; i < shapes.size(); i++){
                    
                    String name = shapes.get(i).shapeTitle; 
                    
                    DetectorShape2D[][] pixels = pane.getPixelsFor(name); 
                    
                    // FIX ME: This can't be a thing
                    float fraction = ((float)i)/(float)shapes.size(); 
                    int red = (int) (fraction*endCol.getRed() + (1-fraction)*startCol.getRed()); 
                    int green = (int) (fraction*endCol.getGreen()+ (1-fraction)*startCol.getGreen()); 
                    int blue = (int) (fraction*endCol.getBlue()+ (1-fraction)*startCol.getBlue()); 
                    currCol = new Color(red, green, blue); 
                    
                    
                    // loop through the pixels of the PMT and change their color. 
                    for (int j = 0; j < pixels.length; j++){
                        for (int k = 0; k < pixels[j].length; k++){ 
                            pixels[j][k].setColor(red, green, blue);
                        }
                    }
                }
               
                pane.update();
            }else{
                // turn all pmts back to original color
                LinkedList<DetectorShape2D> shapes = pane.getAllShapes("MAPMTs");
                
                for (DetectorShape2D shape : shapes){
                
                    
                    
                    shape.setColor(245, 245, 245);
                }
                dectView.colorAxis.setRange(0.0, 10.0);
                pane.update();     
            }      
        }      
    };  
    
    ActionListener generateGraph = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            
            selectedPMT = dectView.selectedPMT.shapeTitle; 
            if (selectedPMT.equals("") == false){ 
                
                int hv = HV_List.getSelectedIndex(); 
                int od = OD_List.getSelectedIndex();
               
                String parameter = (String) Parameter_List.getSelectedItem();
                parameter = parameter.toLowerCase();
                String pmt = selectedPMT; 
               
                // get the data
                List<Double> data = exp.getData(pmt, parameter, hv, od);
                List<Double> y = new LinkedList<Double>();    
                
                ParameterGraph paraGraph = new ParameterGraph(pmt, parameter, hv, od, data, y); 
                                
                // create a frame to hold the canvas
                JFrame graphFrame = new JFrame(); 
                
                graphFrame.add(paraGraph); 
                graphFrame.setVisible(true);
                graphFrame.setSize(800, 600);
                paraGraph.update(); // update the canvas to show the graph
                paraGraph.setPreferredSize(new Dimension(600,500));
                paraGraph.setVisible(true); 
            }
        }
    };
    
    ActionListener generateHistogram = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            
                
                int hv = HV_List.getSelectedIndex(); 
                int od = OD_List.getSelectedIndex();
                
                String parameter = (String) Parameter_List.getSelectedItem();
                parameter = parameter.toLowerCase();
                String pmt = selectedPMT; 
                 
                List<List<Double>> data = new LinkedList<List<Double>>();
                
                // get all values for the pmt
                // Thread it up!!
                for (int i = 0; i < deviceFile.getNumberOfDevices(); i++){                
                    data.add(exp.getData(deviceFile.getDevices().get(i), parameter, hv, od));
                }
                
                HistogramFrame histo = new HistogramFrame(parameter.toUpperCase(), data, hv, od); 
                
                histo.setSize(800,600);
                histo.setVisible(true);
                
            }
    };
     
    public RICHDetector(){
       
        super(); 
        this.exp = new CCDB_ExpReader(); 
        this.dectView = new DetectorView2D_ML();
        this.pane = new RichDetectorPane2D(this.dectView); 
         
        this.setLayout(new BorderLayout());
        
        // Button and Action Listeners: 
        this.addButtons();
        this.addActionListeners();
   
        // Add pane to the frame
        this.add(this.pane); 
        
        this.initFrameSettings(1100, 800);
       
    }
    
    private void initFrameSettings(int width, int height){
    
        this.pack();
        this.setVisible(true);
        this.setSize(width,height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private void addActionListeners(){
        
        // add action listeners
        this.showGain_CheckBox.addActionListener(checkBoxClicked);
        this.genGraph.addActionListener(generateGraph);  
        this.genHisto.addActionListener(generateHistogram);
    } 
     
    private void addButtons(){
    
        JPanel buttons = new JPanel();
        buttons.add(this.HV_label);
        buttons.add(this.HV_List);
        buttons.add(this.showGain_CheckBox);
        buttons.add(this.OD_label);
        buttons.add(this.OD_List);
        buttons.add(this.param_label);
        buttons.add(this.Parameter_List);
        buttons.add(this.genGraph);
        buttons.add(this.genHisto);
        buttons.setLayout(new FlowLayout());
        this.add(buttons, BorderLayout.PAGE_END);
    }
    
    private String getSelectedPMT(){
        return this.selectedPMT; 
    }
    
    public DetectorView2D_ML getDectectorView(){
        return this.dectView; 
    }
    
    public static void main(String args[]){
    
        RICHDetector dect = new RICHDetector(); 
    }
}

    
   