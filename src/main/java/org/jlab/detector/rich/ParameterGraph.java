/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.detector.rich;

import java.util.HashMap;
import java.util.List;
import org.jlab.groot.base.GStyle;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.graphics.EmbeddedCanvas;

/**
 *
 * @author Andrew
 */
public class ParameterGraph extends EmbeddedCanvas {
    
    protected HashMap<Integer, String> hv_hash = new HashMap<Integer, String>(); 
    protected HashMap<Integer, String> od_hash = new HashMap<Integer, String>(); 
        
    public ParameterGraph(String pmt, String parameter, int hv, int od, List<Double> x, List<Double> y){
    
        this.initHashMaps();
        
        // set the style of the graph
        GStyle.getGraphErrorsAttributes().setMarkerStyle(0);
        GStyle.getGraphErrorsAttributes().setMarkerSize(3);
                          
        
        double max = this.getMax(x);
        double[] pixs = this.pixels();
                
        double[] datas = this.convertData(x);
                
        // graph data
        GraphErrors graph = new GraphErrors(parameter, pixs, datas);
        graph.setTitleX("Pixels");
        graph.setTitleY(parameter.toUpperCase());
        graph.setMarkerSize(3);
        graph.setMarkerStyle(0);
       
        this.getPad(0).setTitle(pmt.toUpperCase() + " - " + parameter.toUpperCase() + " at HV: " + hv_hash.get(hv) + " OD: " + od_hash.get(od));
        this.getPad(0).setTitleFontSize(20);
        this.getPad(0).getAxisFrame().getAxisY().setRange(0.0, max + .30*max);
        
        this.draw(graph);
    }
      
    private void initHashMaps(){
    
        this.hv_hash.put(0, "1000 V");
        this.hv_hash.put(1, "1050 V");
        this.hv_hash.put(2, "1075 V");
        this.hv_hash.put(3, "1100 V");
        
        this.od_hash.put(0, "13");
        this.od_hash.put(1, "14");
        this.od_hash.put(2, "15");
    }
    
    private double[] pixels(){
    
        // hold 1 - 64 
        double[] pixs = new double[64];
        for (int i = 0 ; i < 64; i++){pixs[i] = (double) (i + 1); }
        
        return pixs; 
    }
        
    private double[] convertData(List<Double> _x){
    
        double[] datas = new double[_x.size()];
        for (int i = 0; i < _x.size(); i++ ){ datas[i] = _x.get(i); }
        
        return datas; 
    }
   
    private double getMax(List<Double> x){
        double max = Double.NEGATIVE_INFINITY; 
                
        for (Double dat : x){ if (dat > max) { max = dat; } }
              
        return max;
    }
 
    
    
    
}
