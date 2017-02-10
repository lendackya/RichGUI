/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.detector.rich;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import org.jlab.groot.data.H1F;
import org.jlab.groot.graphics.EmbeddedCanvas;

/**
 *
 * @author Andrew
 */
public class HistogramFrame extends JFrame{
    
    private H1F histogram; 
    protected HashMap<Integer, String> hv_hash = new HashMap<Integer, String>(); 
    protected HashMap<Integer, String> od_hash = new HashMap<Integer, String>(); 
    
    HistogramFrame(String parameter, List<List<Double>>  data, int hv, int od){
        
        System.out.println("Start init Histogram"); 
        EmbeddedCanvas canvas = new EmbeddedCanvas(); 
        this.initHashMaps();
        List<Double> averages = this.findAverages(data);
        
        double min = this.findMin(averages);
        double max = this.findMax(averages);
        
        this.histogram = new H1F(parameter.toUpperCase(), 200, 0, 200); 
       
        for (int i = 0; i < averages.size(); i++){ this.histogram.fill(averages.get(i)); }

        canvas.draw(this.histogram);
        canvas.getPad(0).setTitle(parameter.toUpperCase() + " - HV: " + this.hv_hash.get(hv) + " OD: " + this.hv_hash.get(od) );
        canvas.getPad(0).getAxisFrame().getAxisX().setTitle("Bin");
        canvas.getPad(0).getAxisFrame().getAxisY().setTitle("Frequency");
        this.add(canvas);
        
        System.out.println("Done init Histogram"); 
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
    
    private double findMin(List<Double> data){
        
        double min = Double.POSITIVE_INFINITY; 
        
        for (int i = 0; i < data.size(); i++){
            
            if (data.get(i) > min){ min = data.get(i); }
        }
    
        return min;     
    }
    
    private double findMax(List<Double> data){
    
        double max = Double.NEGATIVE_INFINITY; 
        
        for (int i = 0; i < data.size(); i++){
            
            if (data.get(i) > max){ max = data.get(i); }
        }
    
        return max; 
    }
    
    private List<Double> findAverages(List<List<Double>>  data){
           
        List<Double> averages = new LinkedList<Double>();
        
        for (int i = 0; i < data.size(); i++){
            for (int j = 0; j < data.get(i).size(); j++){
                
                averages.add(this.findAverage(data.get(i))); 
            }
        }
        
        return averages;
    }
    
    private double findAverage(List<Double>  data){
    
        double sum = 0; 
        double average; 
       
        for (int i = 0; i < data.size(); i++){ sum = sum + data.get(i); }
        
        average = sum/data.size(); 
        
        return average;
    }
}
