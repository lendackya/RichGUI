
package org.jlab.detector.rich;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;
import java.io.File;

public class DeviceNameFile{

  public static void main(String[] args){
      //System.out.println("Working Directory = " + System.getProperty("user.dir"));
      DeviceNameFile file = new DeviceNameFile("Text_Files/pmts_db.txt");
      System.out.println(file.numDevices);
      Vector<String> names = file.getDevices();

      for (String name : names){

        System.out.println(name);
      }
  }

  private Vector<String> deviceNames;
  private int numDevices;
  private String filename;

  public DeviceNameFile(String filename){
    File file = new File(filename);

    if (file.canRead() == false){

      System.out.println("Error. File cannot be read.");
    }else {
      this.filename = filename;
      this.initDeviceNames();
      this.initNumDevices();
    }
  }

  private void initDeviceNames(){
    String thisLine = null;
    this.deviceNames = new Vector<String>();
      try{
         // open input stream test.txt for reading purpose.
         FileReader fileReader =  new FileReader(this.filename);
         BufferedReader br = new BufferedReader(fileReader);
         while ((thisLine = br.readLine()) != null) {
            this.deviceNames.add(thisLine);
         }
      }catch(Exception e){
         e.printStackTrace();
      }

  }

  private void initNumDevices(){

    this.numDevices = this.deviceNames.size();
  }

  public Vector<String> getDevices(){

    return this.deviceNames;
  }

  public int getNumberOfDevices(){

    return this.numDevices;
  }

  public String getFilename(){

    return this.filename;
  }

}
