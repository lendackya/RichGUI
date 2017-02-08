package org.jlab.detector.rich;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Andrew
 */
import org.jlab.ccdb.CcdbPackage;
import org.jlab.ccdb.JDBCProvider;
import org.jlab.ccdb.SQLiteProvider;
import org.jlab.ccdb.MySqlProvider;
import org.jlab.ccdb.Assignment;
import org.jlab.ccdb.*;
import java.util.Vector;


public class CCDB_Reader{

	private String mySQL_ConnectionString = "";
	private JDBCProvider provider;
        

  public static void main (String[] args){
    String pathname = "/test/rich/ca7452";
    CCDB_Reader ccdb = new CCDB_Reader();

		//ccdb.displayRowData(pathname, 0);
		ccdb.displayRowX_ColY(pathname, 0, 1);

  }

	/**
	* Constructor for the CCDB class
	* @param connectionStr the string that corresponds to the connection to the CCDB.
	* This string must either begin with "mysql://" or "sqlite://".
	* An error will be thrown if it does not start with those two cases.
	*/
	public CCDB_Reader(){

		// since this is the reader class, we use this connection string
		this.mySQL_ConnectionString = "mysql://clas12reader@clasdb.jlab.org/clas12";
		this.connect(this.mySQL_ConnectionString);	// connect to the datbase
	}

	// MARK::: Helper Methods

	public void displayTableInformation(String pathname){

        Assignment asgmt = provider.getData(pathname);

        System.out.println("Number of Rows: " + asgmt.getRowCount() );
        System.out.println("Number of Columns: " + asgmt.getColumnCount() );
        System.out.println("Date Created: " + asgmt.getCreated() );
        //System.out.println("ID: " + asgmt.getID() );
        System.out.println("Blob: " + asgmt.getBlob() );
        System.out.println("Type Table Directory: " + asgmt.getTypeTable().getDirectory().getName() );
        System.out.println("Type Table Name: " + asgmt.getTypeTable().getName() );
        System.out.println("Variation Name: " + asgmt.getVariation().getName() );
        System.out.println("Run: " + asgmt.getRun() );
    }

  public void printRootDirectory(){

      System.out.println(this.provider.getRootDir().getComment());
      System.out.println(this.provider.getRootDir().getFullPath());
    }

	private boolean indexOutOfBounds_Rows(String pathname, int rowNum){
			Assignment asgmt = provider.getData(pathname);

			if (rowNum > asgmt.getRowCount() || rowNum < 0){	return true; } // index NOT valid
			else{ return false;} // index valid!
	}

	private boolean indexOutOfBounds_Cols(String pathname, int colNum){
		Assignment asgmt = provider.getData(pathname);

		if (colNum > asgmt.getColumnCount() || colNum < 0){	return true; } // index NOT valid
		else{ return false;} // index valid!
	}

	/**
	 * A method that creates and returns the JDBCProvider needed to connect to the CCDB.
	 *
	 * @param strConnection the string that corresponds to the connection to the CCDB. This string must either begin with "mysql://" or "sqlite://". An error will be thrown if it does not start with those two cases.
	 * @return returns the JDBCProvider corresponding to the connection to the CCDB.
	 */
	public static JDBCProvider getProvider(String strConnection){

		JDBCProvider provider = CcdbPackage.createProvider(strConnection);

		return provider;
	}

	/**
		* Closes the connection to the database
	*/
	public boolean closeConnection(){

		if (this.provider.getIsConnected()){
			this.provider.close();
			System.out.println("Closing Conncection");
			return true;
		}else{
			System.out.println("Connection could not be closed.");
			return false;
		}
	}

	/**
	 * Connects the JDBCProvider to the CCDB.
	 * @param connectionStr the string that corresponds to the connection to the CCDB. This string must either begin with "mysql://" or "sqlite://". An error will be thrown if it does not start with those two cases.
	 * @return returns true if the connection was successfully closed, false otherwise.
	 */
	private boolean connect(String connectionStr){

		this.provider = getProvider(connectionStr);
		this.provider.connect();

		if (this.provider.getIsConnected()){
			System.out.println("Connection to : " + connectionStr + ".");
			return true;
		}else{
			return false;
		}
	}

	// MARK :: Data Retrieval Methods

	/**
	 * A method that displays the data of a specified tablename
	 * @param pathname the name of the table in the database
	*/
	public void displayTableData(String pathname){

		// gets the data from the given tablename
		Assignment asgmt = provider.getData(pathname);

		// gets data represented as number rows and columns
		for(Vector<Double> row : asgmt.getTableDouble()){
				for(Double cell: row){

					System.out.print(cell + " ");
			}
			System.out.println(); //next line after a row
		}
	}

	/**
	 * A method that returns the data of a specified tablename
	 * @param pathname the name of the table in the database
	 * @return returns the data of the specifcied tablename as a Vector<Vector<Double>>
	*/
	public Vector<Vector<Double>> getTableData(String pathname){

		Assignment asgmt = provider.getData(pathname);

		return asgmt.getTableDouble();
	}

	/**
	 * A method that returns the data in a specified row number (0 based)
	 * @param pathname the name of the table in the database
	 * @param rowNum the row of that data that is to be printed
	 *
	 * @return returns the data of the given row as a Vector<Double>
	*/
	public Vector<Double> getRowData(String pathname, int rowNum){
		 Assignment asgmt;

		 if (this.indexOutOfBounds_Rows(pathname, rowNum)){
			 System.out.println("Error. Invalid Row Index. ");
			 return null;
		 }else{
			   asgmt = provider.getData(pathname);
				 Vector<Vector<Double>> data = asgmt.getTableDouble();
				 return data.get(rowNum);
			}
	}

	/**
	 * A method that returns the data in a specified col number (0 based)
	 * @param pathname the name of the table in the database
	 * @param rowNum the row of that data that is to be printed
	 *
	 * @return returns the data of the given row as a Vector<Double>
	*/
	public Vector<Double> getColumnData(String pathname, int colNum){
		 Assignment asgmt;

		 if (this.indexOutOfBounds_Rows(pathname, colNum)){
			 System.out.println("Error. Invalid Row Index. ");
			 return null;
		 }else{
				 asgmt = provider.getData(pathname);
				 Vector<Double> doubleValues = asgmt.getColumnValuesDouble(colNum);
				 return doubleValues;
			}
	}

	/**
	 * A method that displays the data in a specified col number (0 based)
	 * @param pathname the name of the table in the database
	 * @param rowNum the row of that data that is to be printed
	*/
	public void displayColumnData(String pathname, int colNum){

		Vector<Double> col_data = this.getColumnData(pathname, colNum);
		System.out.println("Column #" + colNum);
		for (Double data: col_data){
			System.out.println(data);
		}
	}

	/**
	 * A method that displays the data in a specified row number (0 based)
	 * @param pathname the name of the table in the database
	 * @param rowNum the row of that data that is to be printed
	*/
	public void displayRowData(String pathname, int rowNum){
		Assignment asgmt = this.provider.getData(pathname);
		Vector<Vector<Double>> data = asgmt.getTableDouble();
		System.out.println("Row #" + rowNum);
		System.out.println(data.get(rowNum));
	}

	public Double getRowX_ColY(String pathname, int rowNum, int colNum){

		Vector<Double> rowData = this.getRowData(pathname, rowNum);

		if (rowData != null){ return rowData.get(colNum); }
		else{ return null; }
	}

public void displayRowX_ColY(String pathname, int rowNum, int colNum){

	System.out.println(this.getRowX_ColY(pathname, rowNum, colNum));
}

}

