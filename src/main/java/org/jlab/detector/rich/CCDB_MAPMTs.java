/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jlab.detector.rich;

import java.util.HashMap;
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
public class CCDB_MAPMTs extends SwingWorker<Boolean, Integer>{
    
    String connectionString = "mysql://clas12reader@clasdb.jlab.org/clas12";
    Double[] data; 
    private JDBCProvider provider; 
    public HashMap<String, Integer> pmtStartingPos = new HashMap<String, Integer>();
    private Assignment asgmt = null;  

       
    public CCDB_MAPMTs(){
        
        this.provider = CcdbPackage.createProvider(this.connectionString);
        this.provider.connect();
        this.initHashMaps();
    }
    
    
    public Double getData(String pmt, String parameter){
    
        this.asgmt = this.provider.getData("/test/rich/mapmts_3");
        
        if (parameter.equals("gain")){
            return this.getPMT_atPixel(pmt, 1, parameter);
        }else{
            return null; 
        }
    }
    
    private void initHashMaps(){
            
        try{
            System.out.println("Here");
            DeviceNameFile pmtNames = new DeviceNameFile("src/main/resources/pmts_db.txt"); 
        
            for (int i = 0; i < pmtNames.getNumberOfDevices(); i++){
                //System.out.println(pmtNames.getDevices().get(i));
                this.pmtStartingPos.put(pmtNames.getDevices().get(i), i*64);
            }
            }catch(Exception e){
                System.out.println(e.getMessage());
        }
   
    }
    
    private Double getPMT_atPixel(String pmtName, Integer pixelNum, String parameter){

	Vector<Double> data = new Vector<Double>();
	Vector<Double> fullData = this.asgmt.getColumnValuesDouble(parameter);

	Integer startingInds = this.pmtStartingPos.get(pmtName);

        //System.out.println("Starting Inds: " + startingInds); 
        
	int counter = 0;
        for (int i = startingInds; i < startingInds + 64; i++){
            //System.out.println(i);
            data.add(fullData.get(i));
        }
	//System.out.println(data.size());
	//System.out.println(data.get(pixelNum - 1));

        return data.get(pixelNum - 1);
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override 
    protected void process(List<Integer> i) {
    
        
    }
}
