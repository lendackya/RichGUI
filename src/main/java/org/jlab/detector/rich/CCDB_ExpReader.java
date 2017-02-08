/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.detector.rich;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import org.jlab.ccdb.CcdbPackage;
import org.jlab.ccdb.JDBCProvider;
import org.jlab.ccdb.SQLiteProvider;
import org.jlab.ccdb.MySqlProvider;
import org.jlab.ccdb.Assignment;
import org.jlab.ccdb.*;

/**
 *
 * @author Andrew
 */
public class CCDB_ExpReader{
    
    String connectionString = "mysql://clas12reader@clasdb.jlab.org/clas12";
    Double[] data; 
    private JDBCProvider provider; 
    private Assignment asgmt = null;  

    private String path = "/test/rich/exp/"; 
    
    public static void main(String[] args){
    
        CCDB_ExpReader exp = new CCDB_ExpReader(); 
        
        List<Double> data = exp.getData("CA7452", "yeild", 0, 0); 

        for (Double num : data){
        
            System.out.println(num); 
        }  
    }
       
    public CCDB_ExpReader(){
        
        this.provider = CcdbPackage.createProvider(this.connectionString);
        this.provider.connect();
    }
    
    public List<Double> getData(String pmt, String parameter, int hv, int od){
    
        this.asgmt = this.provider.getData("/test/rich/exp/" + pmt);
        
        System.out.println("Retreiving Data from: /test/rich/exp/" + pmt);
        
        if (parameter.equals("mu*eff20")){
            Vector<Double> mu = this.asgmt.getColumnValuesDouble("mu"); 
            Vector<Double> eff20 = this.asgmt.getColumnValuesDouble("eff20"); 
            Vector<Double> mueff20 = new Vector<Double>(); 
              
            // multiply the values to get the parameter
            for (Double my_mu : mu){
                for (Double my_eff20 : eff20){
                
                    mueff20.add(my_mu*my_eff20);
                }
            }
            // split the data up
            Vector<List<Double>> splitData = this.splitIntoOD(mueff20);   
            Vector<List<Double>> hvData = this.splitIntoHV(splitData, od); 
            
            return hvData.get(hv);
        
        }else if (parameter.equals("sc/yield")){
            
            Vector<Double> sc = this.asgmt.getColumnValuesDouble("sc"); 
            Vector<Double> yield = this.asgmt.getColumnValuesDouble("yeild"); 
            Vector<Double> sc_yield = new Vector<Double>(); 
            
            // multiply the values to get the parameter
            for (Double my_sc : sc){
                for (Double my_yield : yield){
                
                    sc_yield.add(my_sc / my_yield);
                }
            }
            // split the data up
            Vector<List<Double>> splitData = this.splitIntoOD(sc_yield);   
            Vector<List<Double>> hvData = this.splitIntoHV(splitData, od); 
            
            return hvData.get(hv);
        
        }else{     
            Vector<Double> data = this.asgmt.getColumnValuesDouble(parameter); 
            Vector<List<Double>> splitData = this.splitIntoOD(data);   
            Vector<List<Double>> hvData = this.splitIntoHV(splitData, od);  
            
            return hvData.get(hv); 
        }
        
                          
    }
        
    private Vector<List<Double>> splitIntoOD(Vector<Double> data){
                
        Vector<List<Double>> splitData = new Vector<List<Double>>(); 
        
        System.out.println(data.size()); 
        
        List<Double> od_13 = data.subList(0, 256);
        List<Double> od_14 = data.subList(256, 512);
        List<Double> od_15 = data.subList(512, data.size());
                   
        splitData.add(od_13);
        splitData.add(od_14);
        splitData.add(od_15);
                
        return splitData;
    }
    
    private Vector<List<Double>> splitIntoHV(Vector<List<Double>> data, int od){
    
        List<Double> odData = data.get(od);
        
        List<Double> hv_1000 = odData.subList(0, 64);
        List<Double> hv_1050 = odData.subList(64, 128);
        List<Double> hv_1075 = odData.subList(128, 192);
        List<Double> hv_1100 = odData.subList(192, odData.size());
                
        Vector<List<Double>> newData = new Vector<List<Double>>(); 
        
        newData.add(hv_1000);
        newData.add(hv_1050); 
        newData.add(hv_1075); 
        newData.add(hv_1100); 
        
        return newData; 
    }
       
}
