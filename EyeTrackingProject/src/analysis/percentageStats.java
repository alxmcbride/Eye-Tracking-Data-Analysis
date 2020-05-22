package analysis;

import java.io.*;
import java.util.ArrayList;

/**
 * This class is used to gathers statistics for a set percentage of lines. Given are two options of how to gather them:
 *      1) Expanding Window Interval - uses windowSizeWholePercent, windowSizeDecimalPercent
 *        This option allows for processing a set of raw data for a set percentage of lines, then adds to it the set of raw data for the same percentage of lines
 *       to be processed all together, and continues so forth until the end of the raw data file.
 *
 *      2) Tumbling Window Interval - uses windowSizeWholePercent, windowSizeDecimalPercent
 *         This option allows for processing a set percentage of lines, then processes the next set a set percentage of lines
 *         separately from the previous set, and continues so forth until the end of the raw data file.
 *         
 *      3) Hopping Window - uses windowSizeWholePercent, windowSizeDecimalPercent, hopsizeWholePercent, hopsizeDecimalPercent
 *         This option allows for processing a set of lines every hopSizeWholePercent that have occurred within the last 
 *         windowSizeWholePercent. Example: hopsizeWholePercent = 5%, windowSizeInMinutes = 10% -> every 5% of lines, process lines
 *         that are within the last 10% of lines.
 *         
 *       4) Session Window - - uses windowSizeWholePercent, windowSizeDecimalPercent, maxDuration, maxDurationWholePercent, windowTimestamps
 *         
 *         
 */

public class percentageStats {

    private static double windowSizeWholePercent = 10.0; // <---ENTER THE PERCENTAGE HERE
    private static double windowSizeDecimalPercent= windowSizeWholePercent/100;
    private static String percentageStatsType = "HOP";  //<----- ENTER: "TBM" for Tumbling Window Percentage Statistics
                                                                     // "EXP" for Expanding Window Percentage Statistics
                                                                     // "HOP" for Hopping Window Percentage Statistics
    // Used for Hopping Window Option ONLY
    private static double hopsizeWholePercent = 5.0; // <---ENTER THE PERCENTAGE HERE
    private static double hopsizeDecimalPercent = hopsizeWholePercent/100; //Converting to decimal
    
    
    private static double maxDuration = 10;     // <----ENTER THE MAXIMUM WINDOW DURATION HERE
    private static double maxDurationDecimalPercent = maxDuration/100;
    private static ArrayList<Integer> windowTimestamps = new ArrayList<Integer>();

    // -----------------------------------GENERATING .CSV FILES---------------------------------------------------
   

    public static void getFXDStats(String inputFile, String outputFile, String participant, String visualType) throws IOException {
    	
        //Setting i) file name based on stats type and ii) the file extension
        if (percentageStatsType.equals("TBM")) {
            outputFile = outputFile + "TBM_win_percentage_Results";
        } else if (percentageStatsType.equals("EXP")) {
            outputFile = outputFile + "EXP_win_percentage_Results";
        }else if (percentageStatsType.equals("HOP")){
            outputFile = outputFile + "HOP_win_percentage_Results";
        }else if (percentageStatsType.equals("SES")) {
        	outputFile = outputFile + "SES_win_percentage_Results";
        }else{
            System.out.println(percentageStatsType+" is not an statistics option");
            return;
        }
        
   

        /*    Putting the field names in the output file. In .csv files, we will start out with the fixation
              for participant 1 with the tree visualization, so we will check to see if participant is "p1" and visualType is "tree"
         */

        // Opening writers
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;

         double temp_percent;
         if(percentageStatsType.equals("HOP")){
    		temp_percent = hopsizeWholePercent;  //FOR HOPPING WINDOW ONLY
         }else if (percentageStatsType.equals("SES")) {
        	 temp_percent = 1;
         }else {
    	    temp_percent = windowSizeWholePercent;
         }
    
        String formatFieldNames = "%-12s, %-25s,%-25s, %-25s, %-25s, %-25s, " +
                "%-17s, %-23s, %-23s, %-25s, %-22s ," +
                "%-23s, %-25s, %-25s ,%-20s, " +
                " %-16s, %-9s, " +
                " %-15s, %-15s, %-15s, %-15s, " +
                "%-16s, %-13s, %-20s, %-17s," +
                "%-9s," +
                "%-20s," +
                "%-15s, %-15s, %-15s,";
        


        if(participant.equals("p1") && visualType.equals("tree")) {
            while(temp_percent <= 100.0) {
                fileWriter = new FileWriter(outputFile + "_" + (int) temp_percent + "%.csv"); //remove percent for SES
                bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(String.format(formatFieldNames,
                        "participant", "total fixation duration", "sum fixation duration", "mean fixation duration", "median fixation duration", "SD fixation duration"
                        , "total saccades", "sum saccade length", "mean saccade length", "median saccade length", "SD saccade length"
                        , "sum saccade duration", "mean saccade duration", "median saccade duration", "SD saccade duration"
                        , "scanpath", "fixation to saccade"
                        , "sum abs degree", " mean abs degree", " med abs degree", "SD abs degree"
                        , "sum rel degree", " mean rel degree", " median rel degree", "SD rel degree"
                        , "convex hull"
                        , "left mouse clicks"
                        ,"avg. pupil size left", "avg. pupil size right","avg. pupil size both"));
                bufferedWriter.newLine();
                bufferedWriter.close();
                if(percentageStatsType.equals("HOP")) {
                   temp_percent+= hopsizeWholePercent;
                }else if (percentageStatsType.equals("SES")) {
                	 temp_percent++;
                	 if(temp_percent == 25) {
                		 break;
                	 }
                }else {
                	temp_percent+=windowSizeWholePercent;
                }
            } 
        }
        


        //Writer for temporary file
        String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\tempFile.txt";
        FileWriter tempFileWriter;
        BufferedWriter tempBufferedWriter;


        try {

            //FileReader for inputFile
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //Keeping track of current time interval stopping point
            double stoppingPoint;
            if(percentageStatsType.equals("HOP")){
                stoppingPoint = hopsizeDecimalPercent;
            }else{
                stoppingPoint = windowSizeDecimalPercent;
            }

            double percent;
            if(percentageStatsType.equals("HOP")){
                percent = hopsizeWholePercent;
            }else{
                percent = windowSizeWholePercent;
            }
  
           

            //Holds the lines read from the raw data file by the reader
            ArrayList<String> lines = new ArrayList<String>();

            //Storing line read from the file
            String line = null;

            //Reading from the input file
            while ((line = bufferedReader.readLine()) != null) {

                if (!line.equals("")) {
                    //Reading a line
                    lines.add(line);
                }
            }

            //Getting the number of lines read
            int totalLines = lines.size();

            int numOfLines = (int) Math.floor(totalLines * stoppingPoint);
            int maxNumOfLines = (int) Math.floor(totalLines * maxDurationDecimalPercent);


            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
            int endIndex = 0;
            int startIndex = 0;   //Will change from zero ONLY when gathering tumbling window or hopping window stats

            //For Hopping Window only
            int nextStartIndex=0;
            
            //FOR SESSION WINDOW ONLY
            int avgFixationDuration = 0;
            int firstEventTimestamp = 0;
            int firstEventIndex = 0;
            String firstEvent = "";
            ArrayList<String> eventLines = new ArrayList<String>();
            boolean windowEndFound = false;
            boolean isLastEvent = false;
            int fileCount = 1;
            
            
            //GET AVERAGE FIXATION DURATION OF FIRST 5% OF LINES
            int l;
            for(l=0;l<lines.size();l++) {
            	String[] array=fixation.lineToArray(lines.get(l));
                int timestamp = Integer.parseInt(array[1]);
            	int fixationDuration = Integer.parseInt(array[2]);
            	if(l < numOfLines) {
            	  avgFixationDuration+=fixationDuration;
            	}else {
            		break;
            	}
            } 
            avgFixationDuration /= l;
            
            
            if(percentageStatsType.equals("SES")) {
                for(int m = 0; m< lines.size();m++) {
                	String[] sesStartArray=fixation.lineToArray(lines.get(m));
                	int fixationDuration = Integer.parseInt(sesStartArray[2]);
                	if(fixationDuration > avgFixationDuration) {
                		firstEventIndex = m;
                		startIndex = m + 1;
                		eventLines.add(lines.get(m));
                		endIndex = m + (numOfLines-1); 
                		firstEventTimestamp = Integer.parseInt(sesStartArray[1]);
                		break;
                	}
                }
            }
            


            if(percentageStatsType.equals("SES")) {
                for (int i = startIndex; i < lines.size(); i++) {
                    //If point is found, will go on and process up to intervalToBeProcessed
                    String [] lineArray = fixation.lineToArray(lines.get(i));
                    int timestamp = Integer.parseInt(lineArray[1]);
                    int fixationDuration = Integer.parseInt(lineArray[2]);
                		if(fixationDuration > avgFixationDuration) {
                			if(windowEndFound) {
                				if (i >  endIndex) {
                					firstEventIndex = i;
                					firstEventTimestamp = Integer.parseInt(lineArray[1]);
                					endIndex = firstEventIndex + (numOfLines -1);
                					firstEvent = lines.get(i);
                					isLastEvent = true;
                				}else {
                					eventLines.add(lines.get(i));
                				}
                			}else {
                    		if(i > endIndex) {
                    		  String [] eventArray = fixation.lineToArray(lines.get(endIndex));
                  			  windowTimestamps.add(firstEventTimestamp);;
                  			  windowTimestamps.add(Integer.parseInt(eventArray[1]));
                    		  windowEndFound = true;
                    		}else {
                    			endIndex = i + (numOfLines-1);
                    			if((endIndex+1) - firstEventIndex >= maxNumOfLines) {
                    				windowEndFound = true;
                    				endIndex = firstEventIndex + (maxNumOfLines - 1);
                    				if(endIndex > totalLines) {
                    				   String [] eventArray = fixation.lineToArray(lines.get(totalLines -1));
                    				   windowTimestamps.add(firstEventTimestamp);
                       				   windowTimestamps.add(Integer.parseInt(eventArray[1]));
                    				}else {
                    				   String [] eventArray = fixation.lineToArray(lines.get(endIndex));
                    				   windowTimestamps.add(firstEventTimestamp);
                       				   windowTimestamps.add(Integer.parseInt(eventArray[1]));
                    				}
                    				
                    			}
                    				eventLines.add(lines.get(i));
                    			}
                    		}
                	      }
                		

                       if (isLastEvent || i == totalLines-1) {
                        tempFileWriter = new FileWriter(tempURL);
                        tempBufferedWriter = new BufferedWriter(tempFileWriter);
                            for (int j=0;j<eventLines.size();j++) {
                                tempBufferedWriter.write(eventLines.get(j));
                                tempBufferedWriter.newLine();
                            }
                            tempBufferedWriter.close();
                            fixation.processFixation(tempURL, outputFile+"_"+fileCount+".csv",participant);
                            eventLines.clear();
                            eventLines.add(firstEvent);
                            windowEndFound = false;
                            isLastEvent = false;
                            fileCount++;
                           }
                         }
                     }else{
                    	  endIndex = startIndex + (numOfLines-1);
                          while(stoppingPoint <= 1.0 ) {
                            tempFileWriter = new FileWriter(tempURL);
                            tempBufferedWriter = new BufferedWriter(tempFileWriter);
                            for (int j = startIndex; j <= endIndex; j++) {
                              tempBufferedWriter.write(lines.get(j));
                              tempBufferedWriter.newLine();
                            }
                           tempBufferedWriter.close();
                           fixation.processFixation(tempURL, outputFile + "_" + percent + "%.csv", participant);
                        
                        if(percentageStatsType.equals("HOP")){
                            if(stoppingPoint < windowSizeDecimalPercent){
                                nextStartIndex= endIndex+1;
                            }else {
                                startIndex = nextStartIndex;
                                nextStartIndex = endIndex + 1;
                            }
                        }
                        //if tumbling window, will update the start index to not include previous lines processed
                        else if(percentageStatsType.equals("TBM")){
                            startIndex=endIndex+1;
                        }
                        //Update the stopping point and interval to be processed
                        if(percentageStatsType.equals("HOP")) {
                            stoppingPoint += hopsizeDecimalPercent;
                            percent += hopsizeWholePercent;
                           
                        }else{
                            stoppingPoint += windowSizeDecimalPercent;
                            percent+= windowSizeWholePercent;
                        }

                        endIndex += (numOfLines-1);
                      }
                  }
                
            //Deleting temp file
            File tempFile = new File(tempURL);
            tempFile.deleteOnExit();
            bufferedReader.close();

        } catch (FileNotFoundException ex) { //if file not found
            System.out.println("Unable to open file '" + inputFile + "'");
        } catch (IOException ex) { //if other exception with the file
            System.out.println("Error reading file '" + inputFile + "'");
        }
    }

    public static void getGZDStats(String inputFile, String outputFile) throws IOException {   	
        //Setting i) file name based on stats type and ii) the file extension
        if (percentageStatsType.equals("TBM")) {
            outputFile = outputFile + "TBM_win_percentage_Results";
        } else if (percentageStatsType.equals("EXP")) {
            outputFile = outputFile + "EXP_win_percentage_Results";
        }else if (percentageStatsType.equals("HOP")){
            outputFile = outputFile + "HOP_win_percentage_Results";
        }else if (percentageStatsType.equals("SES")) {
        	outputFile = outputFile + "SES_win_percentage_Results";
        }else{
            System.out.println(percentageStatsType+" is not an statistics option");
            return;
        }


        //Writer for temporary file
        String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\tempFile.txt";
        FileWriter tempFileWriter;
        BufferedWriter tempBufferedWriter;

        try {
            //FileReader for inputFile
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //Holds the lines read from the raw data file by the reader
            ArrayList<String> lines = new ArrayList<String>();

            //Storing line read from the file
            String line = null;

            //Reading from the input file
            while ((line = bufferedReader.readLine()) != null) {

                if (!line.equals("")) {
                    //Reading a line
                    lines.add(line);
                }
            }
            
            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
            int endIndex = 0;
            int startIndex = 0;   //Will change from zero ONLY when gathering tumbling window stats

            //For Hopping Window only
            int nextStartIndex=0;
            
            //SESSION WINDOW ONLY
            int windowEndTimestampCount = 0;
            int fileCount = 1;
          //  double window =  windowTimestamps.get(windowEndTimestampCount);
            
            //Keeping track of current time interval stopping point
            double stoppingPoint;
            if(percentageStatsType.equals("HOP")){
                stoppingPoint = hopsizeDecimalPercent;
            }else if (percentageStatsType.equals("SES")){
            	stoppingPoint = windowTimestamps.get(windowEndTimestampCount);
            }else{
                stoppingPoint = windowSizeDecimalPercent;
            }

            double percent;
            if(percentageStatsType.equals("HOP")){
                percent = hopsizeWholePercent;
            }else{
                percent = (int)windowSizeWholePercent;
            }
            
            //Getting the number of lines read
            int totalLines = lines.size();
            int numOfLines = (int) Math.floor(totalLines * stoppingPoint);
            int value = 0;
            //SESSION WINDOW: gathers data based on timestamps
            if(percentageStatsType.equals("SES")) {
            	for(int i=1; i <= (windowTimestamps.size() / 2);i++) {           		
            		int startTime = windowTimestamps.get(windowEndTimestampCount);
            		windowEndTimestampCount++;
            		int endTime = windowTimestamps.get(windowEndTimestampCount);
                	startIndex = Integer.MIN_VALUE; 
                	endIndex = Integer.MIN_VALUE;
            		for(int j = 0; j < lines.size();j++) {
            			 String[] lineArray = fixation.lineToArray(lines.get(j));
                         int timestamp = Integer.parseInt(lineArray[0]);
                         if(timestamp >= startTime && startIndex == Integer.MIN_VALUE ) {
                        	 startIndex = j ;
                         }else if(startIndex != Integer.MIN_VALUE  && timestamp > endTime || j == (lines.size()-1)) {
                        	 endIndex = j - 1;
                        	 tempFileWriter = new FileWriter(tempURL);
                             tempBufferedWriter = new BufferedWriter(tempFileWriter);
                             for (int k = startIndex; k <= endIndex; k++) {
                                 tempBufferedWriter.write(lines.get(k));
                                 tempBufferedWriter.newLine();
                             }
                                tempBufferedWriter.close();
                             	gaze.processGaze(tempURL, outputFile + "_" + fileCount + ".csv");
                             	fileCount++;
                             	windowEndTimestampCount++;
                             	value = 1;
                             	break;
                           }
            	     }
            	}  
            	
            	//OTHER WINDOW TYPES
           }else {
           	endIndex = startIndex + (numOfLines-1);
            while(stoppingPoint <= 1.0 ) {
              tempFileWriter = new FileWriter(tempURL);
              tempBufferedWriter = new BufferedWriter(tempFileWriter);
              for (int j = startIndex; j <= endIndex; j++) {
                tempBufferedWriter.write(lines.get(j));
                tempBufferedWriter.newLine();
              }
             tempBufferedWriter.close();
             gaze.processGaze(tempURL, outputFile + "_" + percent + "%.csv");
          
          if(percentageStatsType.equals("HOP")){
              if(stoppingPoint < windowSizeDecimalPercent){
                  nextStartIndex= endIndex+1;
              }else {
                  startIndex = nextStartIndex;
                  nextStartIndex = endIndex + 1;
              }
          }
          //if tumbling window, will update the start index to not include previous lines processed
          else if(percentageStatsType.equals("TBM")){
              startIndex=endIndex+1;
          }
          //Update the stopping point and interval to be processed
          if(percentageStatsType.equals("HOP")) {
              stoppingPoint += hopsizeDecimalPercent;
              percent += hopsizeWholePercent;
             
          }else{
              stoppingPoint += windowSizeDecimalPercent;
              percent+= windowSizeWholePercent;
          }

          endIndex += (numOfLines-1);
          }
          }
            //Deleting temp file
            windowTimestamps.clear();
            File tempFile = new File(tempURL);
            tempFile.deleteOnExit();
            bufferedReader.close();
            
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + inputFile + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + inputFile + "'");
        }
    }

    public static void getEVDStats(String inputFile, String outputFile) throws IOException {
        //Setting i) file name based on stats type and ii) the file extension
        if (percentageStatsType.equals("TBM")) {
            outputFile = outputFile + "TBM_win_percentage_Results";
        } else if (percentageStatsType.equals("EXP")) {
            outputFile = outputFile + "EXP_win_percentage_Results";
        }else if (percentageStatsType.equals("HOP")){
            outputFile = outputFile + "HOP_win_percentage_Results";
        }else if (percentageStatsType.equals("SES")) {
        	outputFile = outputFile + "SES_win_percentage_Results";
        }else{
            System.out.println(percentageStatsType+" is not an statistics option");
            return;
        }
        

        //Writer for temporary file
        String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\tempFile.txt";
        FileWriter tempFileWriter;
        BufferedWriter tempBufferedWriter;

        try {
            //FileReader for inputFile
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //Holds the lines read from the raw data file by the reader
            ArrayList<String> lines = new ArrayList<String>();


            //Storing a line read from the file
            String line = null;

            //Reading from the input file
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.equals("")) {
                    //Reading a line
                    lines.add(line);
                }

            }
            bufferedReader.close();

            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
            int endIndex = 0;
            int startIndex = 0;   //Will change from zero ONLY when gathering tumbling window stats

            //For Hopping Window only
            int nextStartIndex=0;
            
            //SESSION WINDOW ONLY
            int windowEndTimestampCount = 0;
            int fileCount = 1;
            //double window =  windowTimestamps.get(windowEndTimestampCount);  UNCOMMENT OUT FOR SES WINDOWS 
            
            
            //Keeping track of current time interval stopping point
            double stoppingPoint;
            if(percentageStatsType.equals("HOP")){
                stoppingPoint = hopsizeDecimalPercent;
            }else if (percentageStatsType.equals("SES")){
            	stoppingPoint = windowTimestamps.get(windowEndTimestampCount);
            }else{
                stoppingPoint = windowSizeDecimalPercent;
            }

            double percent;
            if(percentageStatsType.equals("HOP")){
                percent = hopsizeWholePercent;
            }else{
                percent = (int)windowSizeWholePercent;
            }
            
            //Getting the number of lines read
            int totalLines = lines.size();
            int numOfLines = (int) Math.floor(totalLines * stoppingPoint);
            int value = 0;
            
            //SESSION WINDOW: gathers data based on timestamps
            if(percentageStatsType.equals("SES")) {
            	for(int i=1; i <= (windowTimestamps.size() / 2);i++) {
            		int startTime = windowTimestamps.get(windowEndTimestampCount);
            		windowEndTimestampCount++;
            		int endTime = windowTimestamps.get(windowEndTimestampCount);
                	startIndex = Integer.MIN_VALUE; 
                	endIndex = Integer.MIN_VALUE;
            		for(int j = 0; j < lines.size();j++) {
            			 String[] lineArray = fixation.lineToArray(lines.get(j));
                         int timestamp = Integer.parseInt(lineArray[0]);
                         if(timestamp >= startTime && startIndex == Integer.MIN_VALUE ) {
                        	 startIndex = j;
                         }else if(startIndex != Integer.MIN_VALUE && timestamp > endTime || j == (lines.size()-1)) {
                        	 endIndex = j-1;
                        	 tempFileWriter = new FileWriter(tempURL);
                             tempBufferedWriter = new BufferedWriter(tempFileWriter);
                             for (int k = startIndex; k <= endIndex; k++) {
                                 tempBufferedWriter.write(lines.get(k));
                                 tempBufferedWriter.newLine();
                             }
                                tempBufferedWriter.close();
                             	event.processEvent(tempURL, outputFile + "_" + fileCount + ".csv");
                             	fileCount++;
                             	windowEndTimestampCount++;
                             	value = 1;
                             	break;
                           }
            	     }
            	}    
            
            	//OTHER WINDOW TYPES
            }else {
            	endIndex = startIndex + (numOfLines-1);
                  while(stoppingPoint <= 1.0 ) {
                  tempFileWriter = new FileWriter(tempURL);
                  tempBufferedWriter = new BufferedWriter(tempFileWriter);
                  for (int j = startIndex; j <= endIndex; j++) {
                    tempBufferedWriter.write(lines.get(j));
                    tempBufferedWriter.newLine();
                  }
                 tempBufferedWriter.close();
                 event.processEvent(tempURL, outputFile + "_" + percent + "%.csv");
              
              if(percentageStatsType.equals("HOP")){
                  if(stoppingPoint < windowSizeDecimalPercent){
                      nextStartIndex= endIndex+1;
                  }else {
                      startIndex = nextStartIndex;
                      nextStartIndex = endIndex + 1;
                  }
              }
              //if tumbling window, will update the start index to not include previous lines processed
              else if(percentageStatsType.equals("TBM")){
                  startIndex=endIndex+1;
              }
              //Update the stopping point and interval to be processed
              if(percentageStatsType.equals("HOP")) {
                  stoppingPoint += hopsizeDecimalPercent;
                  percent += hopsizeWholePercent;
                 
              }else{
                  stoppingPoint += windowSizeDecimalPercent;
                  percent+= windowSizeWholePercent;
              }

              endIndex += (numOfLines-1);
              }
            }

            //Deleting temp file
            File tempFile = new File(tempURL);
            tempFile.deleteOnExit();
            bufferedReader.close();

        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + inputFile + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + inputFile + "'");
        }

    }

    /* -----------------------------------------GENERATING .TXT FILES (OLD) ---------------------------------------*/

//    public static void getFXDStats(String inputFile, String outputFile) throws IOException {
//        //Setting i) file name based on stats type and ii) the file extension
//        if (percentageStatsType.equals("TBM")) {
//            outputFile = outputFile + "TBM_window_percent_FXD_Results.txt";
//        } else if (percentageStatsType.equals("EXP")) {
//            outputFile = outputFile + "EXP_window_percent_FXD_Results.txt";
//        }else{
//           System.out.println(percentageStatsType+" is not an statistics option");
//           return;
//        }
//
//        //Writer for the output file
//        FileWriter fileWriter = new FileWriter(outputFile);
//        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
//
//        //Writing the header names to the output file for each column for each statistical result
//        String formatFieldNames = "%-9s %-25s %-25s %-25s %-25s %-25s %-25s %-12s  " +
//                "%-17s %-23s %-23s %-25s %-22s %-20s %-21s  " +
//                "%-23s %-25s %-25s %-20s %-22s %-20s  " +
//                " %-16s  %-9s  " +
//                " %-15s %-15s %-15s %-15s %-15s %-11s   " +
//                "%-16s %-13s %-20s %-17s %-20s %-11s   " +
//                "%-9s";
//        bufferedWriter.write(String.format(formatFieldNames, "percentage", "total fixation duration", "sum fixation duration", "mean fixation duration"
//                , "median fixation duration", "SD fixation duration", "min fixation duration", "max fixation duration"
//                , "total saccades", "sum saccade length", "mean saccade length", "median saccade length", "SD saccade length", "min saccade length", "max saccade length"
//                , "sum saccade duration", "mean saccade duration", "median saccade duration", "SD saccade duration", "min saccade duration", "max saccade duration"
//                , "scanpath", "fixation to saccade", "sum abs degree", " mean abs degree", " med abs degree", "SD abs degree", "min abs degree", "max abs degree"
//                , "sum rel degree", " mean rel degree", " median rel degree", "SD rel degree", "min rel degree", "max rel degree", "convex hull"));
//        bufferedWriter.newLine();
//        bufferedWriter.close();
//
//
//        //Writer for temporary file
//        String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\Percentage Results\\tempFile.txt";
//        FileWriter tempFileWriter;
//        BufferedWriter tempBufferedWriter;
//
//
//        try {
//
//            //FileReader for inputFile
//            FileReader fileReader = new FileReader(inputFile);
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//
//            //Keeping track of current time interval stopping point
//            double stoppingPoint = decimalPercentage;
//            double percent = wholePercentage;
//
//            //Holds the lines read from the raw data file by the reader
//            ArrayList<String> lines = new ArrayList<String>();
//
//            //Storing line read from the file
//            String line = null;
//
//            //Reading from the input file
//            while ((line = bufferedReader.readLine()) != null) {
//
//                if (!line.equals("")) {
//                    //Reading a line
//                    lines.add(line);
//                }
//            }
//
//            //Getting the number of lines read
//            int totalLines = lines.size();
//
//            int numOfLines = (int) Math.floor(totalLines * stoppingPoint);
//
//            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
//            int endIndex = 0;
//            int startIndex = 0;   //Will change from zero ONLY when gathering tumbling window stats
//            while (stoppingPoint <= 0.9) {
//                for (int i = startIndex; i < lines.size(); i++) {
//                    //If point is found, will go on and process up to intervalToBeProcessed
//                    if (i == numOfLines) {
//                        endIndex = i;
//                        tempFileWriter = new FileWriter(tempURL);
//                        tempBufferedWriter = new BufferedWriter(tempFileWriter);
//                        for (int j = startIndex; j <= endIndex; j++) {
//                            tempBufferedWriter.write(lines.get(j));
//                            tempBufferedWriter.newLine();
//                        }
//                        tempBufferedWriter.close();
//                        fixation.processFixation(tempURL, outputFile, percent);
//                        percent += wholePercentage;
//
//                        //if tumbling window, will update the start index to not include previous lines processed
//                        if (percentageStatsType.equals("TBM")) {
//                            startIndex = endIndex + 1;
//                        }
//
//                        //Update the stopping point and interval to be processed
//                        stoppingPoint += decimalPercentage;
//                        numOfLines = (int) Math.floor(totalLines * stoppingPoint);
//
//                    }
//                }
//            }
//
//
//            //Deleting temp file
//            File tempFile = new File(tempURL);
//            tempFile.deleteOnExit();
//            bufferedReader.close();
//
//        } catch (FileNotFoundException ex) { //if file not found
//            System.out.println("Unable to open file '" + inputFile + "'");
//        } catch (IOException ex) { //if other exception with the file
//            System.out.println("Error reading file '" + inputFile + "'");
//        }
//    }
//
//    public static void getGZDStats(String inputFile, String outputFile) throws IOException {
//        //Setting i) file name based on stats type and ii) the file extension
//        if (percentageStatsType.equals("TBM")) {
//            outputFile = outputFile + "TBM_window_percent_GZD_Results.txt";
//        } else if (percentageStatsType.equals("EXP")) {
//            outputFile = outputFile + "EXP_window_percent_GZD_Results.txt";
//        }else{
//            System.out.println(percentageStatsType+"is not an statistics option");
//            return;
//        }
//
//        //Writer for the output file
//        FileWriter fileWriter = new FileWriter(outputFile);
//        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
//
//        //Writing the header names to the output file for each column for each statistical result
//        String formatHeaderNames = "%-12s %-12s %-15s %-15s %-15s";
//        bufferedWriter.write(String.format(formatHeaderNames, "Percentage", "Num of valid recordings", "Avg. pupil size left", "Avg. pupil size right", "Avg. pupil size both"));
//        bufferedWriter.newLine();
//        bufferedWriter.close();
//
//        //Writer for temporary file
//        String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\Percentage Results\\tempFile.txt";
//        FileWriter tempFileWriter;
//        BufferedWriter tempBufferedWriter;
//
//        try {
//            //FileReader for inputFile
//            FileReader fileReader = new FileReader(inputFile);
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//
//
//            //Keeping track of current time interval stopping point
//            double stoppingPoint = decimalPercentage;
//            double percent = wholePercentage;
//
//            //Holds the lines read from the raw data file by the reader
//            ArrayList<String> lines = new ArrayList<String>();
//
//            //Storing line read from the file
//            String line = null;
//
//            //Reading from the input file
//            while ((line = bufferedReader.readLine()) != null) {
//
//                if (!line.equals("")) {
//                    //Reading a line
//                    lines.add(line);
//                }
//            }
//
//            //Getting the number of lines read
//            int totalLines = lines.size();
//
//            int numOfLines = (int) Math.floor(totalLines * stoppingPoint);
//
//            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
//            int endIndex = 0;
//            int startIndex = 0;   //Will change from zero ONLY when gathering tumbling window stats
//            while (stoppingPoint <= 0.9) {
//                for (int i = startIndex; i < lines.size(); i++) {
//                    //If point is found, will go on and process up to intervalToBeProcessed
//                    if (i == numOfLines) {
//                        endIndex = i;
//                        tempFileWriter = new FileWriter(tempURL);
//                        tempBufferedWriter = new BufferedWriter(tempFileWriter);
//                        for (int j = startIndex; j <= endIndex; j++) {
//                            tempBufferedWriter.write(lines.get(j));
//                            tempBufferedWriter.newLine();
//                        }
//                        tempBufferedWriter.close();
//                        gaze.processGaze(tempURL, outputFile, percent);
//                        percent += wholePercentage;
//
//                        //if tumbling window, will update the start index to not include previous lines processed
//                        if (percentageStatsType.equals("TBM")) {
//                            startIndex = endIndex + 1;
//                        }
//
//                        //Update the stopping point and interval to be processed
//                        stoppingPoint += decimalPercentage;
//                        numOfLines = (int) Math.floor(totalLines * stoppingPoint);
//
//                    }
//                }
//            }
//
//
//            //Deleting temp file
//            File tempFile = new File(tempURL);
//            tempFile.deleteOnExit();
//            bufferedReader.close();
//        } catch (FileNotFoundException ex) {
//            System.out.println("Unable to open file '" + inputFile + "'");
//        } catch (IOException ex) {
//            System.out.println("Error reading file '" + inputFile + "'");
//        }
//    }
//
//    public static void getEVDStats(String inputFile, String outputFile) throws IOException {
//        //Setting i) file name based on stats type and ii) the file extension
//        if (percentageStatsType.equals("TBM")) {
//            outputFile = outputFile + "TBM_window_percent_EVD_Results.txt";
//        } else  if (percentageStatsType.equals("EXP")) {
//            outputFile = outputFile + "EXP_window_percent_EVD_Results.txt";
//        }else{
//            System.out.println(percentageStatsType+"is not an statistics option");
//            return;
//        }
//
//        //Writer for the output file
//        FileWriter fileWriter = new FileWriter(outputFile);
//        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
//
//        //Writing the header name for each column to the output file
//        bufferedWriter.write("Percentage  Left mouse clicks");
//        bufferedWriter.newLine();
//        bufferedWriter.close();
//
//        //Writer for temporary file
//        String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\Percentage Results\\tempFile.txt";
//        FileWriter tempFileWriter;
//        BufferedWriter tempBufferedWriter;
//
//        try {
//            //FileReader for inputFile
//            FileReader fileReader = new FileReader(inputFile);
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//
//            //Holds the lines read from the raw data file by the reader
//            ArrayList<String> lines = new ArrayList<String>();
//
//            //Keeping track of current time interval stopping point
//            double stoppingPoint = decimalPercentage;
//            double percent = wholePercentage;
//
//            //Storing a line read from the file
//            String line = null;
//
//            //Reading from the input file
//            while ((line = bufferedReader.readLine()) != null) {
//                if (!line.equals("")) {
//                    //Reading a line
//                    lines.add(line);
//                }
//
//            }
//            bufferedReader.close();
//
//            //Getting the number of lines read
//            int totalLines = lines.size();
//
//            int numOfLines = (int) Math.floor(totalLines * stoppingPoint);
//
//            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
//            int endIndex = 0;
//            int startIndex = 0;   //Will change from zero ONLY when gathering tumbling window stats
//            while (stoppingPoint <= 0.9) {
//                for (int i = startIndex; i < lines.size(); i++) {
//                    //If point is found, will go on and process up to intervalToBeProcessed
//                    if (i == numOfLines) {
//                        endIndex = i;
//                        tempFileWriter = new FileWriter(tempURL);
//                        tempBufferedWriter = new BufferedWriter(tempFileWriter);
//                        for (int j = startIndex; j <= endIndex; j++) {
//                            tempBufferedWriter.write(lines.get(j));
//                            tempBufferedWriter.newLine();
//                        }
//                        tempBufferedWriter.close();
//                        event.processEvent(tempURL, outputFile, percent);
//                        percent += wholePercentage;
//
//                        //if tumbling window, will update the start index to not include previous lines processed
//                        if (percentageStatsType.equals("TBM")) {
//                            startIndex = endIndex + 1;
//                        }
//
//                        //Update the stopping point and interval to be processed
//                        stoppingPoint += decimalPercentage;
//                        numOfLines = (int) Math.floor(totalLines * stoppingPoint);
//
//                    }
//                }
//            }
//
//
//            //Deleting temp file
//            File tempFile = new File(tempURL);
//            tempFile.deleteOnExit();
//            bufferedReader.close();
//
//        } catch (FileNotFoundException ex) {
//            System.out.println("Unable to open file '" + inputFile + "'");
//        } catch (IOException ex) {
//            System.out.println("Error reading file '" + inputFile + "'");
//        }
//
//    }


}
