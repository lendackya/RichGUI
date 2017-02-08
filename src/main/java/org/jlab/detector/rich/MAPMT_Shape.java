package org.jlab.detector.rich;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.jlab.detector.base.DetectorType;
import org.jlab.detector.view.DetectorShape2D;

/**
 *
 * @author Andrew
 */
public class MAPMT_Shape extends DetectorShape2D{
     
    //private String shapeTitle; 
    public Integer richGridPosition; 
    
    // Constructors: 
    
    public MAPMT_Shape(){
        
        super();     
    }
                 
    public MAPMT_Shape(DetectorType type, int sector, int layer, int component){
    
        super(type, sector, layer, component); 
    }
    
    public MAPMT_Shape(DetectorType type, int sector, int layer, int component, String name){
       
        super(type, sector, layer, component); 
        this.shapeTitle = name;   
    }
    
    public MAPMT_Shape(DetectorType type, int sector, int layer, int component, String name, Integer gridPosition){
       
        super(type, sector, layer, component); 
        this.shapeTitle = name; 
        this.richGridPosition = gridPosition; 
    }
    
    public String getName(){
    
        return this.shapeTitle; 
    }
      
    public void setName(String name){
    
        this.shapeTitle = name;
    }

    
    
    
    
    
    
}
