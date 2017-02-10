/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.detector.rich;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.SwingWorker;
import org.jlab.detector.base.DetectorType;
import static org.jlab.detector.rich.RICHDetector.step1;
import org.jlab.detector.view.DetectorPane2D;
import org.jlab.detector.view.DetectorShape2D;
import org.jlab.detector.view.DetectorView2D;
import org.jlab.detector.view.DetectorView2D_ML;

/**
 *
 * @author Andrew
 */
public class RichDetectorPane2D extends DetectorPane2D {
        
    // Databases
    private CCDB_MAPMTs ccdb_calibration = new CCDB_MAPMTs(); 
    private CCDB_ExpReader CCDB_Exp = new CCDB_ExpReader(); 
    
    // Used to get the names of the pmt from a text file
    final private DeviceNameFile deviceFile;   
            
    int numPMTs; 
    final private JLabel db_label = new JLabel("Database: "); 
    
    final private JRadioButton exp = new JRadioButton("Experimental"); 
    final private JRadioButton cc = new JRadioButton("Calibration"); 
    
    private boolean useExpDatabase; 
    private boolean useCcDatabase;
    
    private HashMap<String, DetectorShape2D[][]> pmtToPixels = new HashMap<String, DetectorShape2D[][]>(); 
    
    // Use for later on to run code thread code
    SwingWorker<Boolean, String> workerOne = new SwingWorker<Boolean, String>() {
        @Override
        protected Boolean doInBackground() throws Exception {
             
            System.out.println("Hello From T1");
            double counter1 = 1;
            for (int i = 0; i <= 100; i++){       
                String pmt1 = deviceFile.getDevices().get(i); 
                //gains.put(pmt1, db.getData(pmt1, "gain"));
                //System.out.println(pmt1);
                counter1++;
            }   
          
            return true;
        }
    };
     
    // Action Listeners
    final private ActionListener expButtonClicked = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JRadioButton expButton = (JRadioButton) actionEvent.getSource();
            
            boolean selected = expButton.isSelected();
                    
            if (selected == true){
                
                if (useCcDatabase == true){ useCcDatabase = false; }
                
                useExpDatabase = true; 
                System.out.println("Use CC DB: " + useCcDatabase); 
                System.out.println("Use Exp DB: " + useExpDatabase); 
 
            }else{
                
                     
            }      
        }      
    };
    
    final private ActionListener ccButtonClicked = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JRadioButton expButton = (JRadioButton) actionEvent.getSource();
            
            boolean selected = expButton.isSelected();
                    
            if (selected == true){
                
                if (useExpDatabase == true){ useExpDatabase = false; }
                
                useCcDatabase = true; 
                System.out.println("Use CC DB: " + useCcDatabase); 
                System.out.println("Use Exp DB: " + useExpDatabase); 
            }else{
            
            }      
        }      
    };
    
    public RichDetectorPane2D(DetectorView2D_ML detectView){
        super();
               
        // load file to know what to name pmts in array
        this.deviceFile = new DeviceNameFile("src/main/resources/gains_inc.txt"); 
        this.numPMTs = this.deviceFile.getNumberOfDevices();

        this.remove(this.view2D);
        this.view2D = detectView; 
        this.add(detectView, BorderLayout.CENTER);
        
        // add the shapes and new buttons
        this.initDetector();
        this.initOutline();
        this.initToolbar();

        this.updateBox();  
    }
    
    private void initToolbar(){
        
        // create button group for the radio buttons
        ButtonGroup btnGroup = new ButtonGroup(); 
        btnGroup.add(exp);
        btnGroup.add(cc);
        
        this.toolbarPane.add(db_label);
        this.toolbarPane.add(exp); 
        this.toolbarPane.add(cc);
    }
    
    private void addActionListeners(){
        
        cc.addActionListener(this.ccButtonClicked);
        exp.addActionListener(this.expButtonClicked);  
    }
    
    public CCDB_MAPMTs getCalibrationDatabase(){
    
        return this.ccdb_calibration; 
    }
    
    public CCDB_ExpReader getExperimentalDatabase(){
        return this.CCDB_Exp; 
    }
        
    public boolean useExperimentalDatabse(){ return this.useExpDatabase; }
    
    public boolean useCalibrationDatabase(){ return this.useCcDatabase; }
            
    private void initDetector(){

        Double[] position;
               
        Vector<String> deviceNames = this.deviceFile.getDevices(); 
        
        int counter = 0; 
        Integer pmtLocationCounter = 0; 
        
        for (int pmty=0; pmty<23;pmty+=1) {
 
            HashMap<Integer, Double[]> pmtLocation = new HashMap<Integer, Double[]>(); 
            double pmty1 = (double) pmty;
            for (int pmtx=0;pmtx<(6+pmty);pmtx+=1){
                double pmtx1 =(double) pmtx;
                double x=(-pmty1/2+pmtx1+1/2)*step1*1.7;
                double y=-pmty1*step1*1.8;
                
                //System.out.println(pmtLocationCounter); 
                
                DetectorShape2D shape = new DetectorShape2D(DetectorType.UNDEFINED,pmtx,2,pmty);
                
                shape.shapeTitle = deviceNames.get(pmtLocationCounter);
                
                shape.createBarXY(9,9);                  
                shape.getShapePath().translateXYZ(x,y,0);
                shape.setColor( 245,  245, 245);
                //shape.setColor(pmtLocationCounter % 245, pmtLocationCounter % 245, 0);
                position = new Double[2]; 
                pmtLocationCounter++;   
                position[0] = x;  
                position[1] = y;  
             
                this.getView().addShape("MAPMTs", shape);
                
                for(int pixelx = 0; pixelx < 8; pixelx++){
                    for(int pixely = 0; pixely < 8; pixely++){
                        DetectorShape2D  pixel = new DetectorShape2D(DetectorType.UNDEFINED,(int)(pixelx+x-3.5),1, ( int )(pixely+y-3.5));

                        pixel.createBarXY(1.125, 1.125);                                 
                        pixel.getShapePath().translateXYZ(pixelx+x-3.5,pixely+y-3.5,0);
                     
                        counter++;     
                        this.getView().addShape("Pixels", pixel); 
                    
                    }
                }
            }
        }
    } 
    
    private void initOutline(){
    
        int pixelCounter = 0; 
        int pmtCounter = 0; 
        
        DetectorShape2D[][] pixels; 
        
        for (int pmty=0; pmty<23;pmty+=1) {
            double pmty1 = (double) pmty;
            for (int pmtx=0;pmtx<(6+pmty);pmtx+=1){
                double pmtx1 =(double) pmtx;
                double x=(-pmty1/2+pmtx1+1/2)*step1*1.575;
                double y=-pmty1*step1*1.5;
             
                String pmtName = this.deviceFile.getDevices().get(pmtCounter);                 
                pmtCounter++; 
                
                pixels = new DetectorShape2D[8][8];
                
                for(int pixelx = 0; pixelx < 8; pixelx++){
                    for(int pixely = 0; pixely < 8; pixely++){
                        DetectorShape2D pixel = new DetectorShape2D(DetectorType.UNDEFINED,(int)(pixelx+x-3.5), pixelCounter ,( int )(pixely+y-3.5));
                        pixelCounter++; 
                        pixel.createBarXY(1.125, 1.125);                                 
                        pixel.getShapePath().translateXYZ(pixelx+x-3.5,pixely+y-3.5,0);
                        pixel.setColor(245, 245, 245);
                        
                        pixels[pixelx][pixely] = pixel;
                        
                        this.getView().addShape("Outline", pixel);                   
                    }
                }
                pmtToPixels.put(pmtName, pixels);         
            }
        }
    }
    
    public DetectorShape2D[][] getPixelsFor(String pmtName){
    
        DetectorShape2D[][] pixels = this.pmtToPixels.get(pmtName);
               
        return pixels; 
    }
    
    public LinkedList<DetectorShape2D> getAllShapes(String layer){                      
        return this.view2D.viewLayers.get(layer).getShapes();
    }
    
    public DetectorView2D getDetectorView(){
        return this.view2D;
    }
    
    public ArrayList<JCheckBox> getCheckBoxes(){
    
        return (ArrayList<JCheckBox>) this.checkButtons; 
    }
    
    public void disableCheckBox(String name){
    
        ArrayList<JCheckBox> cb = new ArrayList<JCheckBox>(); 
        System.out.println("Function Entered!\n\n"); 
        System.out.println(cb.size());
        
        for (int i = 0; i < cb.size(); i++){
            System.out.println(cb.get(i).getText());
            if (cb.get(i).getText().equals(name)){
                System.out.println("Click!\n\n"); 
                cb.get(i).doClick();
            }
        }
    }
}
