package analysis;
import java.io.*;
import java.util.ArrayList;

/**
 * This class is used to gathers statistics for a window of lines that is set by windowSizeInMinutes, which a set amount of time
 * Given are four options of how to gather them:
 * 
 *      1) Expanding Window - uses windowSizeInMinutes, windowSizeInMilliseconds
 *         This option allows for processing a set of raw data for a set percentag of lines, 
 *         then adds to it the set of raw data for the same percentage of lines to be processed 
 *         all together, and continues so forth until the end of the raw data file.
 *
 *      2) Tumbling Window - uses windowSizeInMinutes, windowSizeInMilliseconds
 *         This option allows for processing a set percentage of lines, then processes the next set a set percentage of lines
 *         separately from the previous set, and continues so forth until the end of the raw data file.
 *         
 *      3) Hopping Window - uses windowSizeInMinutes, windowSizeInMilliseconds, hopsizeInMinutes, hopsizeInMilliseconds 
 *         This option allows for processing a set of lines every hopSizeInMinutes that have occured within the last 
 *         windowSizeInMinutes. Example: hopsizeInMinutes = 1min, windowSizeInMinutes = 2min -> every minute, process lines
 *         that are within the last two minutes.
 *       
 *      4) Session Window - uses windowSizeInMinutes, windowSizeInMilliseconds, maxDuration, maxDurationInMilliseconds, windowTimestamps

 *         
 *    This code will generate results in a .csv file 
 */

public class timeStats {
	
	  
    private static int windowSizeInMinutes = 2; // <---ENTER THE MINUTES HERE
    private static int windowSizeInMilliseconds = windowSizeInMinutes * 60 * 1000; //Converting minutes to milliseconds
    private static String timeStatsType = "SES";  //<----- ENTER: "TBM" for Tumbling Window Time Statistics
                                                               // "EXP" for Expanding Window Time Statistics
                                                               // "HOP" for Hopping Window Time Statistics
 
    private static int hopsizeInMinutes = 1; // <---ENTER THE MINUTES HERE
    private static int hopsizeInMilliseconds = hopsizeInMinutes * 60 * 1000; //Converting minutes to milliseconds
    
    
    private static int maxDuration = 5;     // <----ENTER THE MAXIMUM WINDOW DURATION HERE
    private static int maxDurationInMilliseconds = maxDuration * 60 * 1000;
    private static ArrayList<Integer> windowTimestamps = new ArrayList<Integer>(); //stores timestamps for processing gaze, event data

  //-----------------------------------GENERATING .CSV FILES---------------------------------------------------

    public static void getFXDStats(String inputFile, String outputFile,String participant, String visualType) throws IOException {
    	
    // Setting the file name based on window type
        if (timeStatsType.equals("TBM")) {
            outputFile = outputFile + "TBM_win_time_Results";
        } else if (timeStatsType.equals("EXP")) {
            outputFile = outputFile + "EXP_win_time_Results";
        }else if (timeStatsType.equals("HOP")) {
            outputFile = outputFile + "HOP_win_time_Results";
        }else if (timeStatsType.equals("SES")) {
            outputFile = outputFile + "SES_win_time_Results";
        }else{
            System.out.println(timeStatsType+" is not an statistics option");
            return;
        }
        
    

        
    // Opening writers
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        
        int temp_time;
        
        if(timeStatsType.equals("HOP") || timeStatsType.equals("SES")){
        	temp_time = hopsizeInMinutes;  //FOR HOPPING WINDOW ONLY   
        }else {
        	temp_time = windowSizeInMinutes;
        }
        
      
    //Formatting the column names for the files
        String formatFieldNames = "%-12s, %-25s,%-25s, %-25s, %-25s, %-25s,   %-17s, %-23s, %-23s, %-25s, %-22s ,  %-23s, %-25s, %-25s ,%-20s, " +
                " %-16s, %-9s,   %-15s, %-15s, %-15s, %-15s,   %-16s, %-13s, %-20s, %-17s,   %-9s   %-20s,  %-15s, %-15s, %-15s,";

    //Only want to write column names to the file when the first participant's tree statistics are generated since file will be empty
        if(participant.equals("p1") && visualType.equals("tree")) {
            while(temp_time<=24) {
            
            	//NAMING FOR OUTPUT .CSV FILES
                fileWriter = new FileWriter(outputFile + "_" + temp_time + "mins.csv");  //remove mins for SESSION WINDOW, then add back
                
                bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(String.format(formatFieldNames,
                        "participant", "total fixation duration", "sum fixation duration", "mean fixation duration", "median fixation duration", "SD fixation duration"
                        , "total saccades", "sum saccade length", "mean saccade length", "median saccade length", "SD saccade length"
                        , "sum saccade duration", "mean saccade duration", "median saccade duration", "SD saccade duration"
                        , "scanpath", "fixation to saccade", "sum abs degree", " mean abs degree", " med abs degree", "SD abs degree"
                        , "sum rel degree", " mean rel degree", " median rel degree", "SD rel degree", "convex hull" , "left mouse clicks"
                        ,"avg. pupil size left", "avg. pupil size right","avg. pupil size both"));
                bufferedWriter.newLine();
                bufferedWriter.close();
                if(timeStatsType.equals("HOP") || timeStatsType.equals("SES")) {
                  temp_time += hopsizeInMinutes;
                }else {
                  temp_time += windowSizeInMinutes;
                }
            }
        }
    

        //Writer for temporary file used to put raw data in intervals
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
                if(!line.equals("")) {
                    //Reading a line
                    lines.add(line);
                }
            }
            
            //Initialize Variables
            int startIndex=0;   //Will change from zero ONLY when gathering tumbling window or hopping window stats
            int endIndex = 0;
            int stoppingPoint = 0;
            
            //FOR HOPPING WINDOW ONLY
            int nextStartIndex=0;
            int windowToBeProcessed;

            //FOR SESSION WINDOW ONLY
            int avgFixationDuration = 0;
            int firstEventTimestamp = 0;
            ArrayList<String> eventLines = new ArrayList<String>();
            boolean windowEndFound = false;
            boolean isLastEvent = false;
            int fileCount = 1;
            String firstEvent = "";

           
            //SES OPTION: GET AVERAGE FIXATION DURATION TO FIND EVENTS
            int l;
            for(l=0;l<lines.size();l++) {
            	String[] array=fixation.lineToArray(lines.get(l));
                int timestamp = Integer.parseInt(array[1]);
            	int fixationDuration = Integer.parseInt(array[2]);
            	if(timestamp < windowSizeInMilliseconds) {
            	  avgFixationDuration+=fixationDuration;
            	}else {
            		break;
            	}
            } 
             avgFixationDuration /= l;
                      
            //Keeping track of current time interval stopping point

            if(timeStatsType.equals("SES")) {
                for(int m = 0; m< lines.size();m++) {
                	String[] sesStartArray=fixation.lineToArray(lines.get(m));
                	int timestamp = Integer.parseInt(sesStartArray[1]);
                	int fixationDuration = Integer.parseInt(sesStartArray[2]);
                	if(fixationDuration > avgFixationDuration) {
                		endIndex=m;
                		eventLines.add(lines.get(m));
                		firstEventTimestamp = timestamp;
                		stoppingPoint = firstEventTimestamp + windowSizeInMilliseconds;  
                		break;
                	}
                }
            }else {
            	if(timeStatsType.equals("HOP")){
                    stoppingPoint = hopsizeInMilliseconds;
            	}else{
                    stoppingPoint = windowSizeInMilliseconds;
            }
            	
            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
            for(String entry : lines){
                String[]lineArray = fixation.lineToArray(entry);
                int timestamp = Integer.parseInt(lineArray[1]);
                //when it finds the endpoint, will save it
                //if the timestamp of the line exactly equals the stopping point, include the line in the lines to be processed
                if (timestamp == stoppingPoint) {
                    endIndex = lines.indexOf(entry);
                    break;
                    //if the timestamp of the line is greater than the stopping point, exclude the line from the lines to be processed
                }else if(timestamp > stoppingPoint) {
                    endIndex = (lines.indexOf(entry))-1;
                    break;
                }
              }
            }
            
            windowToBeProcessed = stoppingPoint;
            
            //Looking to find the endpoint for the next interval after the current to be processed
            if(!(timeStatsType.equals("SES"))){
              if(timeStatsType.equals("HOP")){
                stoppingPoint += hopsizeInMilliseconds;
              }else{
                stoppingPoint += windowSizeInMilliseconds;
              }
            }
            
           
            //Processing the raw data files
           
            for(int i=endIndex+1;i<lines.size();i++){
                //Looking for the point where the interval ends
                String [] lineArray = fixation.lineToArray(lines.get(i));
                int timestamp = Integer.parseInt(lineArray[1]);
                int fixationDuration = Integer.parseInt(lineArray[2]);
                
                //SESSION WINDOW: checks the data point against the average fixation duration
                if(timeStatsType.equals("SES")) {
                	if(fixationDuration > avgFixationDuration) {
                		if(windowEndFound) {
                			if(timestamp > stoppingPoint) {
                				firstEventTimestamp = timestamp;
                				stoppingPoint = firstEventTimestamp + windowSizeInMilliseconds;
                				firstEvent = lines.get(i);
                				isLastEvent= true;
                			}else {
                				eventLines.add(lines.get(i));
                			}
                		}else {
                		   if(timestamp > stoppingPoint) {		
                			  windowTimestamps.add(firstEventTimestamp);
                			  windowTimestamps.add(stoppingPoint);
                			  windowEndFound= true;
                		   }else {
                			stoppingPoint = timestamp + windowSizeInMilliseconds;
                			if(stoppingPoint - firstEventTimestamp >= maxDurationInMilliseconds) {
                				windowEndFound = true;
                				stoppingPoint = firstEventTimestamp + maxDurationInMilliseconds;
                				String[] lastLine = fixation.lineToArray(lines.get(lines.size()-1));
                				if(stoppingPoint < Integer.parseInt(lastLine[1]) ) {
                			    	windowTimestamps.add(firstEventTimestamp);
                			    	windowTimestamps.add(stoppingPoint);
                				}
                			}
                			eventLines.add(lines.get(i));	
                		   }
                		}
                	}
                
                //If point is found, will go on and process up to intervalToBeProces
                if (isLastEvent || i == (lines.size()-1)){
                    tempFileWriter = new FileWriter(tempURL);
                    tempBufferedWriter = new BufferedWriter(tempFileWriter);
                        for (int j=0;j<eventLines.size();j++) {
                            tempBufferedWriter.write(eventLines.get(j));
                            tempBufferedWriter.newLine();
                        }
                        tempBufferedWriter.close();
                        if(i == lines.size() -1) {
                        	windowTimestamps.add(firstEventTimestamp);
                        	windowTimestamps.add(timestamp);
                        }
                        fixation.processFixation(tempURL, outputFile+"_"+fileCount+".csv",participant);
                        windowEndFound = false;
                        isLastEvent = false;
                        fileCount++;
                        eventLines.clear();
                        eventLines.add(firstEvent);
                  }
                
                //USED FOR OTHER WINDOWS
                }else {
                        if(timestamp > stoppingPoint) {
                        tempFileWriter = new FileWriter(tempURL);
                        tempBufferedWriter = new BufferedWriter(tempFileWriter);
                        for (int j=startIndex;j<=endIndex;j++) {
                        tempBufferedWriter.write(lines.get(j));
                        tempBufferedWriter.newLine();
                        }
                       tempBufferedWriter.close();
                       fixation.processFixation(tempURL, outputFile+"_"+(windowToBeProcessed/1000/60)+"mins.csv",participant);
                    
                
                
            


                if(!(timeStatsType.equals("SES"))) {
                  if(timeStatsType.equals("HOP")){
                    if( windowToBeProcessed < windowSizeInMilliseconds){
                         nextStartIndex= endIndex+1;
                    }else {
                         startIndex = nextStartIndex;
                         nextStartIndex = endIndex + 1;
                        }
                    }//if tumbling window, will update the start index to not include previous lines processed
                    else if(timeStatsType.equals("TBM") || timeStatsType.equals("SES")){
                        startIndex=endIndex+1;
                    }

                    //Updating the end index
                    if (timestamp == stoppingPoint) {
                        endIndex = i;
                    }else if(timestamp > stoppingPoint) {
                        endIndex =i-1;
                    }
                    //Update the stopping point and interval to be processed
                    if(timeStatsType.equals("HOP")) {
                        stoppingPoint += hopsizeInMilliseconds;
                        windowToBeProcessed += hopsizeInMilliseconds;
                    }else{
                        stoppingPoint += windowSizeInMilliseconds;
                        windowToBeProcessed += windowSizeInMilliseconds;
                    }
                  }
                }
              }
            }
         
        

            //Process the very last set of lines
            if(!(timeStatsType.equals("SES"))) {
            tempFileWriter = new FileWriter(tempURL);
            tempBufferedWriter = new BufferedWriter(tempFileWriter);
            for (int j=startIndex;j<lines.size();j++) {
                tempBufferedWriter.write(lines.get(j));
                tempBufferedWriter.newLine();
            }
            tempBufferedWriter.close();
            fixation.processFixation(tempURL, outputFile+"_"+(windowToBeProcessed/1000/60)+"mins.csv",participant);
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
        // Setting the file name based on window type
        if (timeStatsType.equals("TBM")) {
            outputFile = outputFile + "TBM_win_time_Results";
        } else if (timeStatsType.equals("EXP")) {
            outputFile = outputFile + "EXP_win_time_Results";
        }else if (timeStatsType.equals("HOP")) {
            outputFile = outputFile + "HOP_win_time_Results";
        }else if (timeStatsType.equals("SES")) {
            outputFile = outputFile + "SES_win_time_Results";
        }else{
            System.out.println(timeStatsType+" is not an statistics option");
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
            bufferedReader.close();

            
            //Initialize Variables
            int startIndex=0;   //Will change from zero ONLY when gathering tumbling window or hopping window stats
            int endIndex = 0;
            int stoppingPoint = 0;
            int windowToBeProcessed;
            
            //FOR HOPPING WINDOW ONLY
            int nextStartIndex=0;
         

            //FOR SESSION WINDOW ONLY
            int windowEndTimestampCount = 0;
            int fileCount = 1;

            //Keeping track of current time interval stopping point

            if(timeStatsType.equals("HOP")){
                stoppingPoint = hopsizeInMilliseconds;
            }else{
                stoppingPoint = windowSizeInMilliseconds;
            }


            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
            for (String entry : lines) {
                String[] lineArray = fixation.lineToArray(entry);
                int timestamp = Integer.parseInt(lineArray[0]);
                //when it finds the endpoint, will save it
                //if the timestamp equals the stopping point, include in the lines to be processed
                if (timestamp == stoppingPoint) {
                    endIndex = lines.indexOf(entry);
                    break;
                    //if the timestamp equals the stopping point, exclude the lines to be processed
                } else if (timestamp > stoppingPoint) {
                    endIndex = (lines.indexOf(entry)) - 1;
                    break;
                }
                

            }
            
            //For Hopping Window only
            windowToBeProcessed = stoppingPoint;
            
           
            //Holding for the interval to be currently processed
            
            //Looking to find the endpoint for the next interval after the current to be processed
            if(timeStatsType.equals("HOP")){
                stoppingPoint += hopsizeInMilliseconds;
            }else{
                stoppingPoint += windowSizeInMilliseconds;
            }

          



            int value = 0;
            
            //SESSION WINDOW: gathers data based on timestamps
            if(timeStatsType.equals("SES")) {
            	for(int i=1; i <= (windowTimestamps.size() / 2);i++) {
            		int startTime = windowTimestamps.get(windowEndTimestampCount);
            		windowEndTimestampCount++;
            		int endTime = windowTimestamps.get(windowEndTimestampCount);
                	startIndex = 0; 
                	endIndex = 0;
            		for(int j = 0; j < lines.size();j++) {
            			 String[] lineArray = fixation.lineToArray(lines.get(j));
                         int timestamp = Integer.parseInt(lineArray[0]);
                         if(timestamp >= startTime && startIndex == 0 ) {
                        	 startIndex = j - value;
                         }else if(startIndex != 0  && timestamp > endTime || j == (lines.size()-1)) {
                        	 if (j == (lines.size()-1)) {
                        		 if(timestamp > endTime) {
                        			 tempFileWriter = new FileWriter(tempURL);
                                     tempBufferedWriter = new BufferedWriter(tempFileWriter);
                                     for (int k = lines.size()-1; k <= lines.size()-1; k++) {
                                         tempBufferedWriter.write(lines.get(k));
                                         tempBufferedWriter.newLine();
                                     }
                                        tempBufferedWriter.close();
                                     	event.processEvent(tempURL, outputFile + "_" + (fileCount + 1) + ".csv");
                        		 }
                        		 
                        		 if(startIndex == Integer.MIN_VALUE) {
                        			 break;
                        		 }
                        	 }
                        	     endIndex = j-1;
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
              for (int i = endIndex + 1; i < lines.size(); i++) {
                //Looking for the point where the interval ends
                String[] lineArray = fixation.lineToArray(lines.get(i));
                int timestamp = Integer.parseInt(lineArray[0]);
                //If point is found, will go on and process up to intervalToBeProcessed
                if (timestamp >= stoppingPoint ) {
                    tempFileWriter = new FileWriter(tempURL);
                    tempBufferedWriter = new BufferedWriter(tempFileWriter);
                    for (int j = startIndex; j <= endIndex; j++) {
                        tempBufferedWriter.write(lines.get(j));
                        tempBufferedWriter.newLine();
                    }
                      tempBufferedWriter.close();
                      gaze.processGaze(tempURL, outputFile + "_" + (windowToBeProcessed / 1000 / 60) + "mins.csv");
                    
                    if (timeStatsType.equals("HOP")) {
                        if (windowToBeProcessed < windowSizeInMilliseconds) {
                            nextStartIndex = endIndex + 1;
                        } else {
                            startIndex = nextStartIndex;
                            nextStartIndex = endIndex + 1;
                        }
                    }
                    //if tumbling window, will update the start index to not include previous lines processed
                    else if (timeStatsType.equals("TBM")) {
                        startIndex = endIndex + 1;
                    }

                    //Updating the end index
                    if (timestamp == stoppingPoint) {
                        endIndex = i;
                    } else if (timestamp > stoppingPoint) {
                        endIndex = i - 1;
                    }
                    //Update the stopping point and interval to be processed
                    if (timeStatsType.equals("HOP")) {
                        stoppingPoint += hopsizeInMilliseconds;
                        windowToBeProcessed += hopsizeInMilliseconds;
                    } else {
                        stoppingPoint += windowSizeInMilliseconds;
                        windowToBeProcessed += windowSizeInMilliseconds;
                    }
                  }
                }
            

            //Process the very last set of lines
            tempFileWriter = new FileWriter(tempURL);
            tempBufferedWriter = new BufferedWriter(tempFileWriter);
            for (int j = startIndex; j < lines.size(); j++) {
            	
                tempBufferedWriter.write(lines.get(j));
                tempBufferedWriter.newLine();
                
            }
            tempBufferedWriter.close();
            gaze.processGaze(tempURL, outputFile + "_" + (windowToBeProcessed / 1000 / 60) + "mins.csv");
            
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

    public static void getEVDStats(String inputFile, String outputFile) throws IOException{

        // Setting the file name based on window type
        if (timeStatsType.equals("TBM")) {
            outputFile = outputFile + "TBM_win_time_Results";
        } else if (timeStatsType.equals("EXP")) {
            outputFile = outputFile + "EXP_win_time_Results";
        }else if (timeStatsType.equals("HOP")) {
            outputFile = outputFile + "HOP_win_time_Results";
        }else if (timeStatsType.equals("SES")) {
            outputFile = outputFile + "SES_win_time_Results";
        }else{
            System.out.println(timeStatsType+" is not an statistics option");
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
                if(!line.equals("")) {
                    //Reading a line
                    lines.add(line);
                }

            }
            bufferedReader.close();
            
            //Initialize Variables
            int startIndex=0;   //Will change from zero ONLY when gathering tumbling window or hopping window stats
            int endIndex = 0;
            int stoppingPoint;
            int windowToBeProcessed;
            
            //FOR HOPPING WINDOW ONLY
            int nextStartIndex=0;
          

            //FOR SESSION WINDOW ONLY
            int windowEndTimestampCount = 0;
            int fileCount = 1;

            //Keeping track of current time interval stopping point

            if(timeStatsType.equals("HOP")){
                stoppingPoint = hopsizeInMilliseconds;
            }else{
                stoppingPoint = windowSizeInMilliseconds;
            }


            for(String entry : lines){
                String[]lineArray = fixation.lineToArray(entry);
                int timestamp = Integer.parseInt(lineArray[0]);
                //when it finds the endpoint, will save it
                //if the timestamp equals the stopping point, include in the lines to be processed
                if (timestamp == stoppingPoint) {
                    endIndex = lines.indexOf(entry);
                    break;
                    //if the timestamp equals the stopping point, exclude the lines to be processed
                }else if(timestamp > stoppingPoint) {
                    endIndex = (lines.indexOf(entry))-1;
                    break;
                }

            }
            //Holding for the interval to be currently processed
            windowToBeProcessed=stoppingPoint;

            //Looking to find the endpoint for the next interval after the current to be processed
            if(timeStatsType.equals("HOP")){
                stoppingPoint += hopsizeInMilliseconds;
            }else{
                stoppingPoint += windowSizeInMilliseconds;
            }

            int value = 0;
            if(timeStatsType.equals("SES")) {
            	for(int i=1; i <= (windowTimestamps.size() / 2);i++) {
            		int startTime = windowTimestamps.get(windowEndTimestampCount);
            		windowEndTimestampCount++;
            		int endTime = windowTimestamps.get(windowEndTimestampCount);
                	startIndex = Integer.MIN_VALUE; ;
                	endIndex = Integer.MIN_VALUE; 
            		for(int j = 0; j < lines.size();j++) {
            			 String[] lineArray = fixation.lineToArray(lines.get(j));
                         int timestamp = Integer.parseInt(lineArray[0]);
                         if(timestamp >= startTime && startIndex == Integer.MIN_VALUE ) {
                        	 startIndex = j;
                         }else if(startIndex != Integer.MIN_VALUE  && timestamp > endTime || j == (lines.size()-1)) {
                        	 if (j == (lines.size()-1)) {
                        		 if(timestamp > endTime) {
                        			 tempFileWriter = new FileWriter(tempURL);
                                     tempBufferedWriter = new BufferedWriter(tempFileWriter);
                                     for (int k = lines.size()-1; k <= lines.size()-1; k++) {
                                         tempBufferedWriter.write(lines.get(k));
                                         tempBufferedWriter.newLine();
                                     }
                                        tempBufferedWriter.close();
                                     	event.processEvent(tempURL, outputFile + "_" + (fileCount + 1) + ".csv");
                        		 }
                        		 
                        		 if(startIndex == Integer.MIN_VALUE) {
                        			 break;
                        		 }
                        	 }
                        		 
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
                             	break;
                           }
            	     }
            	}
            	
            	//OTHER WINDOW TYPES
          }else {
            for(int i=endIndex+1;i<lines.size();i++){
                //Looking for the point where the interval ends
                String[]lineArray=fixation.lineToArray(lines.get(i));
                int timestamp=Integer.parseInt(lineArray[0]);

                //If point is found, will go on and process up to intervalToBeProcessed
                if (timestamp>=stoppingPoint){
                    tempFileWriter = new FileWriter(tempURL);
                    tempBufferedWriter = new BufferedWriter(tempFileWriter);
                    for (int j=startIndex;j<=endIndex;j++) {
                        tempBufferedWriter.write(lines.get(j));
                        tempBufferedWriter.newLine();
                    }
                    tempBufferedWriter.close();
                    event.processEvent(tempURL, outputFile + "_" + (windowToBeProcessed / 1000 / 60) + "mins.csv");

                    if (timeStatsType.equals("HOP")) {
                        if (windowToBeProcessed < windowSizeInMilliseconds) {
                            nextStartIndex = endIndex + 1;
                        } else {
                            startIndex = nextStartIndex;
                            nextStartIndex = endIndex + 1;
                        }
                    }
                    //if tumbling window, will update the start index to not include previous lines processed
                    else if (timeStatsType.equals("TBM")) {
                        startIndex = endIndex + 1;
                    }

                    //Updating the end index
                    if (timestamp == stoppingPoint) {
                        endIndex = i;
                    }else if(timestamp > stoppingPoint) {
                        endIndex = i-1;
                    }
                    
                    //Update the stopping point and interval to be processed
                    if (timeStatsType.equals("HOP")) {
                        stoppingPoint += hopsizeInMilliseconds;
                        windowToBeProcessed += hopsizeInMilliseconds;
                    }else {
                        stoppingPoint += windowSizeInMilliseconds;
                        windowToBeProcessed += windowSizeInMilliseconds;
                    }
                }
            }

            //Process the very last set of lines
            tempFileWriter = new FileWriter(tempURL);
            tempBufferedWriter = new BufferedWriter(tempFileWriter);
            for (int j=startIndex ;j< lines.size() ;j++) {
                tempBufferedWriter.write(lines.get(j));
                tempBufferedWriter.newLine();
            }
            
            tempBufferedWriter.close();
                event.processEvent(tempURL, outputFile + "_" + (windowToBeProcessed / 1000 / 60) + "mins.csv");

          }
            //Deleting temp file
            File tempFile = new File(tempURL);
            tempFile.deleteOnExit();
       
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + inputFile + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + inputFile + "'");
        }
    }

}

//public static void getFXDStatsTXT(String inputFile, String outputFile) throws IOException {
//if (intervalStatsType.equals("TBM")) {
//  outputFile = outputFile + "TBM_window_time_FXD_Results.txt";
//} else if (intervalStatsType.equals("EXP")) {
//  outputFile = outputFile + "EXP_window_time_FXD_Results.txt";
//}else if (intervalStatsType.equals("HOP")) {
//  outputFile = outputFile + "HOP_window_time_FXD_Results.txt";
//}else{
//  System.out.println(intervalStatsType+" is not an statistics option");
//  return;
//}
//
////Writer for the output file
//FileWriter fileWriter=new FileWriter(outputFile);
//BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
//
////Writing the header names to the output file for each column for each statistical result
//String formatFieldNames = "%-9s %-25s %-25s %-25s %-25s %-25s %-25s %-12s  " +
//      "%-17s %-23s %-23s %-25s %-22s %-20s %-21s  " +
//      "%-23s %-25s %-25s %-20s %-22s %-20s  " +
//      " %-16s  %-9s  " +
//      " %-15s %-15s %-15s %-15s %-15s %-11s   " +
//      "%-16s %-13s %-20s %-17s %-20s %-11s   " +
//      "%-9s";
//bufferedWriter.write(String.format(formatFieldNames,
//      "minutes", "total fixation duration", "sum fixation duration", "mean fixation duration" ,"median fixation duration", "SD fixation duration", "min fixation duration", "max fixation duration"
//      , "total saccades", "sum saccade length", "mean saccade length", "median saccade length", "SD saccade length", "min saccade length", "max saccade length"
//      , "sum saccade duration", "mean saccade duration", "median saccade duration", "SD saccade duration", "min saccade duration", "max saccade duration"
//      , "scanpath", "fixation to saccade"
//      ,"sum abs degree", " mean abs degree", " med abs degree", "SD abs degree", "min abs degree", "max abs degree"
//      , "sum rel degree", " mean rel degree", " median rel degree", "SD rel degree", "min rel degree", "max rel degree",
//       "convex hull"));
//bufferedWriter.newLine();
//bufferedWriter.close();
//
//
////Writer for temporary file
//String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\Interval Results\\tempFile.txt";
//FileWriter tempFileWriter;
//BufferedWriter tempBufferedWriter;
//
//
//try {
//
//  //FileReader for inputFile
//  FileReader fileReader = new FileReader(inputFile);
//  BufferedReader bufferedReader = new BufferedReader(fileReader);
//
//  //Keeping track of current time interval stopping point
//  int stoppingPoint = intervalLengthInMilliseconds;
//
//  //Holds the lines read from the raw data file by the reader
//  ArrayList<String> lines = new ArrayList<String>();
//
//  //Storing line read from the file
//  String line = null;
//
//  //Reading from the input file
//  while ((line = bufferedReader.readLine()) != null) {
//
//      if(!line.equals("")) {
//          //Reading a line
//          lines.add(line);
//      }
//  }
//
//  //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
//  int endIndex = 0;
//  for(String entry : lines){
//      String[]lineArray = fixation.lineToArray(entry);
//      int timestamp = Integer.parseInt(lineArray[1]);
//      //when it finds the endpoint, will save it
//      //if the timestamp equals the stopping point, include in the lines to be processed
//      if (timestamp == stoppingPoint) {
//          endIndex = lines.indexOf(entry);
//          break;
//          //if the timestamp equals the stopping point, exclude the lines to be processed
//      }else if(timestamp > stoppingPoint) {
//          endIndex = (lines.indexOf(entry))-1;
//          break;
//      }
//
//  }
//  //Holding for the interval to be currently processed
//  int intervalToBeProcessed=stoppingPoint;
//
//  //Looking to find the endpoint for the next interval after the current to be processed
//  stoppingPoint+=intervalLengthInMilliseconds;
//
//
//  int startIndex=0;   //Will change from zero ONLY when gathering tumbling window stats
//
//
//  for(int i=endIndex+1;i<lines.size();i++){
//      //Looking for the point where the interval ends
//      String[]lineArray=fixation.lineToArray(lines.get(i));
//      int timestamp=Integer.parseInt(lineArray[1]);
//
//      //If point is found, will go on and process up to intervalToBeProcessed
//      if (timestamp>=stoppingPoint){
//          tempFileWriter = new FileWriter(tempURL);
//          tempBufferedWriter = new BufferedWriter(tempFileWriter);
//          for (int j=startIndex;j<=endIndex;j++) {
//              tempBufferedWriter.write(lines.get(j));
//              tempBufferedWriter.newLine();
//          }
//          tempBufferedWriter.close();
//          fixation.processFixation(tempURL, outputFile,(intervalToBeProcessed/60)/1000);
//
//          //if tumbling window, will update the start index to not include previous lines processed
//          if(intervalStatsType.equals("TBM")){
//              startIndex=endIndex+1;
//          }
//
//          //Updating the end index
//          if (timestamp == stoppingPoint) {
//              endIndex = i;
//          }else if(timestamp > stoppingPoint) {
//              endIndex =i-1;
//          }
//          //Update the stopping point and interval to be processed
//          stoppingPoint += intervalLengthInMilliseconds;
//          intervalToBeProcessed+=intervalLengthInMilliseconds;
//      }
//  }
//
//  //Process the very last set of lines
//  tempFileWriter = new FileWriter(tempURL);
//  tempBufferedWriter = new BufferedWriter(tempFileWriter);
//  for (int j=startIndex;j<lines.size();j++) {
//      tempBufferedWriter.write(lines.get(j));
//      tempBufferedWriter.newLine();
//  }
//  tempBufferedWriter.close();
//  String lastline=lines.get(lines.size()-1);
//  String [] lastArray=fixation.lineToArray(lastline);
//  int lastInterval=Integer.parseInt(lastArray[1]);
//  fixation.processFixation(tempURL, outputFile, (lastInterval/60)/1000);
//
//
//
//
//  //Deleting temp file
//  File tempFile = new File(tempURL);
//  tempFile.deleteOnExit();
//  bufferedReader.close();
//
//} catch (FileNotFoundException ex) { //if file not found
//  System.out.println("Unable to open file '" + inputFile + "'");
//} catch (IOException ex) { //if other exception with the file
//  System.out.println("Error reading file '" + inputFile + "'");
//}
//}
//
//
//
//
//public static void getGZDStatsTXT(String inputFile, String outputFile) throws IOException{
////Setting i) file name based on stats type and ii) the file extension
//if (timeStatsType.equals("TBM")) {
//  outputFile = outputFile + "TBM_window_time_GZD_Results.txt";
//} else if (timeStatsType.equals("EXP")) {
//  outputFile = outputFile + "EXP_window_time_GZD_Results.txt";
//}else if (timeStatsType.equals("HOP")) {
//  outputFile = outputFile + "HOP_window_time_GZD_Results.txt";
//}else{
//  System.out.println(intervalStatsType+"is not an statistics option");
//  return;
//}
//
////Writer for the output file
//FileWriter fileWriter=new FileWriter(outputFile);
//BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
//
////Writing the header names to the output file for each column for each statistical result
//String formatHeaderNames="%-12s %-12s %-15s %-15s %-15s";
//bufferedWriter.write(String.format(formatHeaderNames,"Minutes","Num of valid recordings","Avg. pupil size left", "Avg. pupil size right","Avg. pupil size both"));
//bufferedWriter.newLine();
//bufferedWriter.close();
//
////Writer for temporary file
//String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\Interval Results\\tempFile.txt";
//FileWriter tempFileWriter;
//BufferedWriter tempBufferedWriter;
//
//try {
//  //FileReader for inputFile
//  FileReader fileReader = new FileReader(inputFile);
//  BufferedReader bufferedReader = new BufferedReader(fileReader);
//
//  //Keeping track of current time interval stopping point
//  int stoppingPoint = intervalLengthInMilliseconds;
//
//  //Holds the lines read from the raw data file by the reader
//  ArrayList<String> lines = new ArrayList<String>();
//
//  //Storing line read from the file
//  String line = null;
//
//  //Reading from the input file
//  while ((line = bufferedReader.readLine()) != null) {
//
//      if(!line.equals("")) {
//          //Reading a line
//          lines.add(line);
//      }
//  }
//
//  //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
//  int endIndex = 0;
//  for(String entry : lines){
//      String[]lineArray = fixation.lineToArray(entry);
//      int timestamp = Integer.parseInt(lineArray[0]);
//      //when it finds the endpoint, will save it
//      //if the timestamp equals the stopping point, include in the lines to be processed
//      if (timestamp == stoppingPoint) {
//          endIndex = lines.indexOf(entry);
//          break;
//          //if the timestamp equals the stopping point, exclude the lines to be processed
//      }else if(timestamp > stoppingPoint) {
//          endIndex = (lines.indexOf(entry))-1;
//          break;
//      }
//
//  }
//  //Holding for the interval to be currently processed
//  int intervalToBeProcessed=stoppingPoint;
//
//  //Looking to find the endpoint for the next interval after the current to be processed
//  stoppingPoint+=intervalLengthInMilliseconds;
//
//
//  int startIndex=0;   //Will change from zero ONLY when gathering tumbling window stats
//
//
//  for(int i=endIndex+1;i<lines.size();i++){
//      //Looking for the point where the interval ends
//      String[]lineArray=fixation.lineToArray(lines.get(i));
//      int timestamp=Integer.parseInt(lineArray[0]);
//
//      //If point is found, will go on and process up to intervalToBeProcessed
//      if (timestamp>=stoppingPoint){
//          tempFileWriter = new FileWriter(tempURL);
//          tempBufferedWriter = new BufferedWriter(tempFileWriter);
//          for (int j=startIndex;j<=endIndex;j++) {
//              tempBufferedWriter.write(lines.get(j));
//              tempBufferedWriter.newLine();
//          }
//          tempBufferedWriter.close();
//          gaze.processGaze(tempURL, outputFile,(intervalToBeProcessed/1000)/60);
//
//          //if tumbling window, will update the start index to not include previous lines processed
//          if(intervalStatsType.equals("TBM")){
//              startIndex=endIndex+1;
//          }
//
//          //Updating the end index
//          if (timestamp == stoppingPoint) {
//              endIndex = i;
//          }else if(timestamp > stoppingPoint) {
//              endIndex =i-1;
//          }
//          //Update the stopping point and interval to be processed
//          stoppingPoint += intervalLengthInMilliseconds;
//          intervalToBeProcessed+=intervalLengthInMilliseconds;
//      }
//  }
//
//  //Process the very last set of lines
//  tempFileWriter = new FileWriter(tempURL);
//  tempBufferedWriter = new BufferedWriter(tempFileWriter);
//  for (int j=startIndex;j<lines.size();j++) {
//      tempBufferedWriter.write(lines.get(j));
//      tempBufferedWriter.newLine();
//  }
//  tempBufferedWriter.close();
//  String lastline=lines.get(lines.size()-1);
//  String [] lastArray=fixation.lineToArray(lastline);
//  int lastInterval=Integer.parseInt(lastArray[0]);
//  gaze.processGaze(tempURL, outputFile, (lastInterval/1000)/60);
//
//  //Deleting temp file
//  File tempFile = new File(tempURL);
//  tempFile.deleteOnExit();
//  bufferedReader.close();
//
//} catch (FileNotFoundException ex) {
//  System.out.println("Unable to open file '" + inputFile + "'");
//} catch (IOException ex) {
//  System.out.println("Error reading file '" + inputFile + "'");
//}
//}
//
//public static void getEVDStatsTXT(String inputFile, String outputFile) throws IOException{
//
////Setting i) file name based on stats type and ii) the file extension
//
//if (intervalStatsType.equals("TBM")) {
//  outputFile = outputFile + "TBM_window_time_EVD_Results.txt";
//} else if (intervalStatsType.equals("EXP")) {
//  outputFile = outputFile + "EXP_window_time_EVD_Results.txt";
//}else{
//  System.out.println(intervalStatsType+"is not an statistics option");
//  return;
//}
//
////Writer for the output file
//FileWriter fileWriter=new FileWriter(outputFile);
//BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
//
////Writing the header name for each column to the output file
//bufferedWriter.write("Minutes   Left mouse clicks");
//bufferedWriter.newLine();
//bufferedWriter.close();
//
////Writer for temporary file
//String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\Interval Results\\tempFile.txt";
//FileWriter tempFileWriter;
//BufferedWriter tempBufferedWriter;
//
//try {
//  //FileReader for inputFile
//  FileReader fileReader = new FileReader(inputFile);
//  BufferedReader bufferedReader = new BufferedReader(fileReader);
//
//  //Holds the lines read from the raw data file by the reader
//  ArrayList<String> lines = new ArrayList<String>();
//
//  //Keeping track of current time interval stopping point
//  int stoppingPoint = intervalLengthInMilliseconds;
//
//  //Storing a line read from the file
//  String line = null;
//
//  //Reading from the input file
//  while ((line = bufferedReader.readLine()) != null) {
//   if(!line.equals("")) {
//       //Reading a line
//       lines.add(line);
//      }
//
//  }
//  bufferedReader.close();
//
//
//  //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
//  int endIndex = 0;
//  for(String entry : lines){
//      String[]lineArray = fixation.lineToArray(entry);
//      int timestamp = Integer.parseInt(lineArray[0]);
//      //when it finds the endpoint, will save it
//      //if the timestamp equals the stopping point, include in the lines to be processed
//      if (timestamp == stoppingPoint) {
//          endIndex = lines.indexOf(entry);
//          break;
//          //if the timestamp equals the stopping point, exclude the lines to be processed
//      }else if(timestamp > stoppingPoint) {
//          endIndex = (lines.indexOf(entry))-1;
//          break;
//      }
//
//  }
//  //Holding for the interval to be currently processed
//  int intervalToBeProcessed=stoppingPoint;
//
//  //Looking to find the endpoint for the next interval after the current to be processed
//  stoppingPoint+=intervalLengthInMilliseconds;
//
//
//  int startIndex=0;   //Will change from zero ONLY when gathering tumbling window stats
//
//
//  for(int i=endIndex+1;i<lines.size();i++){
//      //Looking for the point where the interval ends
//      String[]lineArray=fixation.lineToArray(lines.get(i));
//      int timestamp=Integer.parseInt(lineArray[0]);
//
//      //If point is found, will go on and process up to intervalToBeProcessed
//      if (timestamp>=stoppingPoint){
//          tempFileWriter = new FileWriter(tempURL);
//          tempBufferedWriter = new BufferedWriter(tempFileWriter);
//          for (int j=startIndex;j<=endIndex;j++) {
//              tempBufferedWriter.write(lines.get(j));
//              tempBufferedWriter.newLine();
//          }
//          tempBufferedWriter.close();
//          event.processEvent(tempURL, outputFile,(intervalToBeProcessed/1000)/60);
//
//          //if tumbling window, will update the start index to not include previous lines processed
//          if(intervalStatsType.equals("TBM")){
//              startIndex=endIndex+1;
//          }
//
//          //Updating the end index
//          if (timestamp == stoppingPoint) {
//              endIndex = i;
//          }else if(timestamp > stoppingPoint) {
//              endIndex =i-1;
//          }
//          //Update the stopping point and interval to be processed
//          stoppingPoint += intervalLengthInMilliseconds;
//          intervalToBeProcessed+=intervalLengthInMilliseconds;
//      }
//  }
//
//  //Process the very last set of lines
//  tempFileWriter = new FileWriter(tempURL);
//  tempBufferedWriter = new BufferedWriter(tempFileWriter);
//  for (int j=startIndex;j<lines.size();j++) {
//      tempBufferedWriter.write(lines.get(j));
//      tempBufferedWriter.newLine();
//  }
//  tempBufferedWriter.close();
//  String lastline=lines.get(lines.size()-1);
//  String [] lastArray=fixation.lineToArray(lastline);
//  int lastInterval=Integer.parseInt(lastArray[0]);
//  event.processEvent(tempURL, outputFile, (lastInterval/1000)/60);
//
//  //Deleting temp file
//  File tempFile = new File(tempURL);
//  tempFile.deleteOnExit();
//  bufferedReader.close();
//
//} catch (FileNotFoundException ex) {
//  System.out.println("Unable to open file '" + inputFile + "'");
//} catch (IOException ex) {
//  System.out.println("Error reading file '" + inputFile + "'");
//}
//}



