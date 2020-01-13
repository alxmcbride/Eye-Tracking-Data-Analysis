//package analysis;
//import java.io.*;
//import weka.core.converters.ArffSaver;
//import weka.core.converters.CSVLoader;
//import weka.core.Instances;
//import weka.filters.Filter;
//import weka.filters.unsupervised.attribute.NumericToNominal;
//
////coverts CSV to Weka accepted Arff files
////needs to include weka.jar in classpath
//
//public class Converter {
//	public static void convertCSVtoARFF(String csvfilename, String arfffilename) {
//	      try {
//	          // load CSV
//	    	  File CSVFile=new File(csvfilename);
//	    	  System.out.println(CSVFile.exists());
//	          CSVLoader loader = new CSVLoader();
//	          loader.setSource(CSVFile);
//	          Instances instances = loader.getDataSet();
//
//	          // convert last feature from numerical to nominal/categorical
//	          //e.g. ontologies (1, 2); visualizations (1,2); overall/correctness/compelteness success from a number (range between 0 and 1) to binary success/failure.
//	          NumericToNominal NtN = new NumericToNominal();
//	          //assumes these to-be-converted features are in locations 2, 3, and last in the csv file
//	  				String[] options = { "-R", "2,3,last" };
//	  				NtN.setOptions(options);
//	          NtN.setInputFormat(instances);
//	          Instances formattedData = Filter.useFilter(instances, NtN);
//
//
//	          // save ARFF
//	          ArffSaver saver = new ArffSaver();
//	          saver.setInstances(formattedData);
//	          saver.setFile(new File(arfffilename));
//	          //saver.setDestination(new File(arfffilename));
//	          saver.writeBatch();
//	      } catch (Exception e) {
//	          e.printStackTrace();
//	      }
//	  }
//
//
//	  public static void main(String[] args)
//	  {
//	    Converter.convertCSVtoARFF("C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\CSV files\\Weka Files"
//	    		+ "\\Feature Set Spreadsheets\\CSV Files\\Tumbling Window by Time Overall searching\\TBM_WIN_by_t_Overall_searching_1.csv",
//	    		"C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\"
//	    		+ "CSV files\\Weka Files\\Feature Set Spreadsheets\\Tumbling_Window_by_Time_Overall_searching_1.arff");
//	  }
//
//
//
//}
