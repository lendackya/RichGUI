/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.detector.view;

import org.jlab.detector.view.ViewWorld; 
import org.jlab.detector.view.DetectorView2D; 
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener; 
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jlab.detector.base.DetectorDescriptor;
import org.jlab.detector.base.DetectorType;
import org.jlab.detector.decode.DetectorDataDgtz;
import org.jlab.detector.rich.CCDB_MAPMTs;
import org.jlab.detector.rich.RichDetectorPane2D;
import org.jlab.detector.view.DetectorListener;
import org.jlab.detector.view.DetectorShape2D;
import org.jlab.detector.view.DetectorView2D;
import org.jlab.detector.view.ViewWorld;
import org.jlab.groot.base.ColorPalette;
import org.jlab.groot.graphics.GraphicsAxis;
import org.jlab.groot.math.Dimension1D;
import org.jlab.groot.math.Dimension2D;
import org.jlab.utils.groups.IndexedList;

/**
 *
 * @author gavalian
 */
public class DetectorView2D_ML extends DetectorView2D implements MouseListener {

    
    final private boolean isMouseMotionListener = true;
    final private boolean isMouseEventListener = true; 
    
    //final private GraphicsAxis pmtGainKey; 
    final private CCDB_MAPMTs db; 
    
    //public ViewWorld world = new ViewWorld();
    public DetectorShape2D selectedPMT = null; 
  
     
    public DetectorView2D_ML(){
        super();    
        this.addListeners();
        this.db = new CCDB_MAPMTs(); 
        
        //System.out.println(ColorPalette.getColorPallete3DSize());
        //this.drawColorAxis();          
    }
    
    public void addListeners(){
         if(this.isMouseMotionListener == true){ //&& this.isMouseEventListener == true){
            this.addMouseMotionListener(this);
            this.addMouseListener(this);
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
                
        if(this.isMouseEventListener == true) {
            double x = world.getViewX(e.getX());
            double y = world.getViewY(e.getY());
            
            DetectorShape2D selection = null;
            
            for(String layer : this.viewLayerNames){
                if(viewLayers.get(layer).isActive()==true){
                this.viewLayers.get(layer).resetSelection();
                selection = this.viewLayers.get(layer).getShapeByXY( x,y);
                if(selection!=null) {
                    
                    this.selectedPMT = selection; 
                    this.viewLayers.get(layer).setSelected(selection);
                    break;
                }
              }
            } 
            
            if (selection != null){
                this.selectedPMT = selection; 
               // System.out.println(selection.shapeTitle);
                
                // FIX ME: Create a new class that holds this information
                JFrame newFrame = new JFrame(selection.shapeTitle);
                newFrame.setLayout(new GridLayout(5,1));
                newFrame.pack();
                newFrame.setVisible(true);
                newFrame.setSize(200, 200);
                JLabel infoLabel = new JLabel(); 
                infoLabel.setText("Gain: " + this.db.getData(selection.shapeTitle, "gain").toString());
                newFrame.add(infoLabel);
            }
                   
        } 
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
            
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        //System.out.println("mouse moved = " + e.getX() + " " + e.getY());
        
        if(this.isMouseMotionListener==true) {
            double x = world.getViewX(e.getX());
            double y = world.getViewY(e.getY());
            DetectorShape2D selection = null;
            for(String layer : this.viewLayerNames){
                if(viewLayers.get(layer).isActive()==true){
                    this.viewLayers.get(layer).resetSelection();
                    selection = this.viewLayers.get(layer).getShapeByXY( x, y);
                    if(selection!=null) {
                        this.viewLayers.get(layer).setSelected(selection);
                        break;
                    }
                }
            } 
            if(selection!=null){
                if(activeShape!=null){
                    //System.out.println(" compare = " + activeShape.getDescriptor().compare(selection.getDescriptor()));
                    //System.out.println(" active shape = " + selection.getDescriptor());
                    //System.out.println(selection.shapeTitle);
                    
                   // System.out.println("x: " + x + "y: " + y);
                    
                }
                //System.out.println(" SELECTION = " + selection.getDescriptor());
                activeShape = selection;
                repaint();
            }
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Layer class to keep shapes in. it computes it's boundaries automatically;
     */
    public static class DetectorViewLayer2D {
        
        private IndexedList<DetectorShape2D>  shapes  = null;
        private String                     layerName  = "Layer";
        private Dimension2D               boundaries  = new Dimension2D(); 
        private int                     layerOpacity  = 255;
        private DetectorDescriptor      selectedDescriptor = new DetectorDescriptor();
        private boolean                 isLayerActive  = true;
        private Dimension1D             axisRange      = new Dimension1D();
        private boolean                 showHitMap     = false;
        private int                     opacity        = 255;
        private ColorPalette            palette        = new ColorPalette();
        
        public DetectorViewLayer2D() {
            shapes = new IndexedList<DetectorShape2D>(4);
        }
        
        public IndexedList<DetectorShape2D> getShapes(){
            return this.shapes; 
        }
        
        
        /**
         * adding a shape to the layer.
         * @param shape
         * @return 
         */
        public DetectorViewLayer2D addShape(DetectorShape2D shape){
            
            int  type       = shape.getDescriptor().getType().getDetectorId();
            int  sector     = shape.getDescriptor().getSector();
            int  layer      = shape.getDescriptor().getLayer();
            int  component  = shape.getDescriptor().getComponent();
            
            if(shapes.getMap().isEmpty()){
                boundaries.set(
                        shape.getShapePath().point(0).x(),
                        shape.getShapePath().point(0).x(),
                        shape.getShapePath().point(0).y(),
                        shape.getShapePath().point(0).y()
                        );
            }
            
            int npoints = shape.getShapePath().size();
            for(int i = 0; i < npoints; i++){
                boundaries.grow(
                        shape.getShapePath().point(i).x(),
                        shape.getShapePath().point(i).y()
                );
            }
            //boundaries.getDimension(0).addPadding(0.1);
            //boundaries.getDimension(1).addPadding(0.1);
            
            this.shapes.add(shape, type,sector,layer,component);
            //System.out.println(shape.shapeTitle);
            return this;
        }
        
        
        public int     getOpacity(){return opacity;}

        public boolean isActive(){ return this.isLayerActive;}
        public DetectorViewLayer2D  setActive(boolean flag){ isLayerActive = flag;return this;}
        public DetectorViewLayer2D  setOpacity(int op){this.opacity = op;return this;}
        public DetectorViewLayer2D  setShowHitMap(boolean flag){this.showHitMap = flag;return this;}
        
        public String getName(){ return this.layerName;}
        
        public final DetectorViewLayer2D setName(String name){
            this.layerName = name;
            return this;
        }
        
        public void setSelected(DetectorShape2D shape){
            this.selectedDescriptor.copy(shape.getDescriptor());
        }
        
        public void resetSelection(){
            this.selectedDescriptor.setCrateSlotChannel(0, 0, 0);
            this.selectedDescriptor.setSectorLayerComponent(0, 0, 0);
            this.selectedDescriptor.setType(DetectorType.UNDEFINED);
        }
        
        public DetectorShape2D  getShapeByXY(double x, double y){
            for(Map.Entry<Long,DetectorShape2D>  shape : shapes.getMap().entrySet()){
                if(shape.getValue().isContained(x, y)==true) return shape.getValue();
            }
            return null;
        }
        
        public Dimension1D  getAxisRange(){
            int counter = 0;
            for(Map.Entry<Long,DetectorShape2D>  shape : shapes.getMap().entrySet()){
                if(counter==0) axisRange.setMinMax(shape.getValue().getCounter(), shape.getValue().getCounter());
                axisRange.grow(shape.getValue().getCounter());
                //if(shape.getValue().isContained(x, y)==true) return shape.getValue();
            }
            return this.axisRange;
        }
        
        public Dimension2D  getBounds(){
            return this.boundaries;
        }
        /**
         * updating the detector shapes with the data from detector Bank.
         * @param detectorData
         * @param options 
         */
        public void fill(List<DetectorDataDgtz> detectorData, String options){            
            boolean doReset = true;
            if(options.contains("same")==true) doReset = false;
            for(Map.Entry<Long,DetectorShape2D>  shape : shapes.getMap().entrySet()){
                if(doReset==true){ shape.getValue().reset(); }
                
                for(int d = 0 ; d < detectorData.size(); d++){                     
                    DetectorDescriptor dd = detectorData.get(d).getDescriptor();
                    DetectorDescriptor dm = shape.getValue().getDescriptor();
                    if(dd.getType()==dm.getType()&dd.getSector()==dm.getSector()&
                            dd.getLayer()==dm.getLayer()&dd.getComponent()==dm.getComponent()
                            ){                            
                        //System.out.println("COLORING COMPONENT " + shape.getValue().getDescriptor());
                        int cv = shape.getValue().getCounter();
                        shape.getValue().setCounter(cv+1);
                    }
                }
            }
        }
        
        public void drawLayer(Graphics2D g2d, ViewWorld world){
            //System.out.println(" WORLD      = " + d2d);
            //System.out.println(" Layer Boundaries = " + this.boundaries);
            
            //if(this.showHitMap==true){
            //    this.getAxisRange();
            //}
            int counterZero = 0;
            int counterOne  = 0;
            
            for(Map.Entry<Long,DetectorShape2D> entry : this.shapes.getMap().entrySet()){
                DetectorShape2D shape = entry.getValue();
                //System.out.println(" drating shape ----> " + entry.getKey());
                //double x = world.getPointX(shape.getShapePath().point(0).x());
                //double y = world.getPointY(shape.getShapePath().point(0).y());
                //g2d.drawOval( (int) x, (int) y,5,5);
                Color shapeColor = shape.getSwingColorWithAlpha(this.opacity);
                
                if(this.showHitMap==true){
                    if(shape.getCounter()>0){
                        counterOne++;
                    } else {
                        counterZero++;
                    }
                    //Color mapColor = ;//ColorPalette.gaxisRange.getMax();
                    shapeColor = palette.getColor3D(shape.getCounter(),axisRange.getMax(), false);
                    
                    //System.out.println(" AXIS MAX = " + axisRange.getMax() + "  VALUE = " + shape.getCounter());
                }

                if(this.selectedDescriptor.compare(shape.getDescriptor())==true){
                    shape.drawShape(g2d, world, Color.red, Color.black);
                } else {                 
                    shape.drawShape(g2d, world, shapeColor, Color.black);                        
                }
                
                //if(this.showHitMap==true){
                   // System.out.println("Counters Zero = " + counterZero + "  One = "
                   //         + counterOne);
                //}
            }
        }    
    }
    
    @Override
    public void paint(Graphics g){ 

        Long st = System.currentTimeMillis();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = this.getSize().width;
        int h = this.getSize().height;
        
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, w, h);
        
        this.viewBounds.getDimension(0).setMinMax(0, w);
        this.viewBounds.getDimension(1).setMinMax(0, h);
        //g2d.setColor(Color.red);
        //g2d.drawRect(10,10,w-20,h-20);
        
        this.colorAxis.setVertical(true);
        this.colorAxis.setAxisType(GraphicsAxis.AXISTYPE_COLOR);
        this.colorAxis.setDimension(h-20,h-120);
        this.colorAxis.setRange(0.0, 6.0);
        this.colorAxis.drawAxis(g2d, 10, h-20);
        this.drawLayers(g2d);  
    }
    
    
    private void drawColorAxis(){
    
        int w = this.getSize().width; 
        int h = this.getSize().height; 
        
        Graphics g = this.getGraphics(); 
        
        Graphics2D g2d = (Graphics2D) g;
    
        this.colorAxis.setVertical(true);
        this.colorAxis.setAxisType(GraphicsAxis.AXISTYPE_COLOR);
        this.colorAxis.setDimension(h-20,h-120);
        this.colorAxis.setRange(0.0, 6.0);
        this.colorAxis.drawAxis(g2d, 10, h-20);
    }
    
    
    public static void main(String[] argv){
        
    
    }
}
