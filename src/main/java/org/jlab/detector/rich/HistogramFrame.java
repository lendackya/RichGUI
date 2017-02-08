/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.detector.rich;

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
    
    private String title; 
    
    private H1F histogram; 
    
    
    HistogramFrame(String parameter, List<List<Double>>  data, int hv, int od){
        
        EmbeddedCanvas canvas = new EmbeddedCanvas(); 
            
        this.histogram = new H1F("Testing", 100, 0, 100); 
    
        List<Double> averages = this.findAverages(data);
        
        for (int i = 0; i < averages.size(); i++){ this.histogram.fill(averages.get(i)); }
        
        canvas.draw(this.histogram);
        canvas.getPad(0).setTitle(parameter.toUpperCase());
        this.histogram.setTitleX("Bin");
        this.histogram.setTitleY("Frequency"); 
        this.add(canvas);
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
