//package analysis;
//import java.io.*;
//import java.util.ArrayList;
//
///**
// * This class is used to gathers statistics over a set interval of time. Given are two options of how to gather them:
// *      1) Expanding Window Interval (EXP)
// *        This option allows for processing a set of raw data over a given interval, then adds to it the set of raw data over the next
// *        interval to be processed all together, and continues so forth until the end of the raw data file.
// *
// *      2) Tumbling Window Interval (TBM)
// *         This option allows for processing a set of raw data over a given interval, then processes the next set of raw data over
// *         the next interval separately from the previous set, and continues so forth until the end of the raw data file.
// *
// */
//
//
//
//
//public class intervalStats {
//
//    private static int intervalLengthInMinutes = 2; // <---ENTER THE MINUTES HERE
//
//    private static int intervalLengthInMilliseconds = intervalLengthInMinutes * 60 * 1000; //Converting minutes to milliseconds
//
//    private static String intervalStatsType = "EXP";  //<----- ENTER: "EXP" for Expanding Window Interval Stats
//                                                                   // "TBM" for Tumbling Window Interval Stats
//                                                                   // HOP for Hopping Window
//
//    //-----------------------------------GENERATING .CSV FILES---------------------------------------------------
//    /*Note: Make sure to comment out when using .txt methods
//          When doing this, want to make sure to run code from p1 to p36(last participant) to keep the order in .csv files.
//          If it comes out incorrect for a participant, have to start all over */
//
//    public static void getFXDStats(String inputFile, String outputFile,String participant) throws IOException {
//        //Setting i) file name based on stats type and ii) the file extension
//        if (intervalStatsType.equals("TBM")) {
//            outputFile = outputFile + "TBM_win_time_Results";
//        } else if (intervalStatsType.equals("EXP")) {
//            outputFile = outputFile + "EXP_win_time_Results";
//        }else if (intervalStatsType.equals("HOP")) {
//            outputFile = outputFile + "HOP_win_time_Results";
//        }else{
//            System.out.println(intervalStatsType+" is not an statistics option");
//            return;
//        }
//
//
//
//        //Writer for temporary file
//        String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\Interval Results\\tempFile.txt";
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
//
//            //Keeping track of current time interval stopping point
//            int stoppingPoint = intervalLengthInMilliseconds;
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
//                if(!line.equals("")) {
//                    //Reading a line
//                    lines.add(line);
//                }
//            }
//
//
//            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
//            int endIndex = 0;
//            for(String entry : lines){
//                String[]lineArray = fixation.lineToArray(entry);
//                int timestamp = Integer.parseInt(lineArray[1]);
//                //when it finds the endpoint, will save it
//                //if the timestamp equals the stopping point, include in the lines to be processed
//                if (timestamp == stoppingPoint) {
//                    endIndex = lines.indexOf(entry);
//                    break;
//                    //if the timestamp equals the stopping point, exclude the lines to be processed
//                }else if(timestamp > stoppingPoint) {
//                    endIndex = (lines.indexOf(entry))-1;
//                    break;
//                }
//
//            }
//            //Holding for the interval to be currently processed
//            int intervalToBeProcessed=stoppingPoint;
//
//            //Looking to find the endpoint for the next interval after the current to be processed
//            stoppingPoint+=intervalLengthInMilliseconds;
//
//
//            int startIndex=0;   //Will change from zero ONLY when gathering tumbling window stats
//
//
//            for(int i=endIndex+1;i<lines.size();i++){
//                //Looking for the point where the interval ends
//                String[]lineArray=fixation.lineToArray(lines.get(i));
//                int timestamp=Integer.parseInt(lineArray[1]);
//
//                //If point is found, will go on and process up to intervalToBeProcessed
//                if (timestamp>=stoppingPoint){
//                    tempFileWriter = new FileWriter(tempURL);
//                    tempBufferedWriter = new BufferedWriter(tempFileWriter);
//                    for (int j=startIndex;j<=endIndex;j++) {
//                        tempBufferedWriter.write(lines.get(j));
//                        tempBufferedWriter.newLine();
//                    }
//                    tempBufferedWriter.close();
//                    fixation.processFixation(tempURL, outputFile+"_"+(intervalToBeProcessed/1000/60)+"mins.csv",participant);
//
//                    //if tumbling window, will update the start index to not include previous lines processed
//                    if(intervalStatsType.equals("TBM")){
//                        startIndex=endIndex+1;
//                    }
//
//                    //Updating the end index
//                    if (timestamp == stoppingPoint) {
//                        endIndex = i;
//                    }else if(timestamp > stoppingPoint) {
//                        endIndex =i-1;
//                    }
//                    //Update the stopping point and interval to be processed
//                    stoppingPoint += intervalLengthInMilliseconds;
//                    intervalToBeProcessed+=intervalLengthInMilliseconds;
//                }
//            }
//
//
//            //Process the very last set of lines
//            tempFileWriter = new FileWriter(tempURL);
//            tempBufferedWriter = new BufferedWriter(tempFileWriter);
//            for (int j=startIndex;j<lines.size();j++) {
//                tempBufferedWriter.write(lines.get(j));
//                tempBufferedWriter.newLine();
//            }
//            tempBufferedWriter.close();
//            String lastline=lines.get(lines.size()-1);
//            String [] lastArray=fixation.lineToArray(lastline);
//            int lastInterval=Integer.parseInt(lastArray[1]);
//            fixation.processFixation(tempURL, outputFile+"_"+(intervalToBeProcessed/1000/60)+"mins.csv",participant);
//
//
//
//            //Deleting temp file
//            File tempFile = new File(tempURL);
//            tempFile.deleteOnExit();
//            bufferedReader.close();
//
//        } catch (FileNotFoundException ex) { //if file not found
//            System.out.println("Unable to open file '" + inputFile + "' -----");
//        } catch (IOException ex) { //if other exception with the file
//            System.out.println("Error reading file '" + inputFile + "'");
//        }
//    }
//
//    public static void getGZDStats(String inputFile, String outputFile,String participant) throws IOException{
//        //Setting i) file name based on stats type and ii) the file extension
//        if (intervalStatsType.equals("TBM")) {
//            outputFile = outputFile + "TBM_win_time_Results";
//        } else if (intervalStatsType.equals("EXP")) {
//            outputFile = outputFile + "EXP_win_time_Results";
//        }else if (intervalStatsType.equals("HOP")) {
//            outputFile = outputFile + "HOP_win_time_Results";
//        }else{
//            System.out.println(intervalStatsType+" is not an statistics option");
//            return;
//        }
//
//
//        //Writer for temporary file
//        String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\Interval Results\\tempFile.txt";
//        FileWriter tempFileWriter;
//        BufferedWriter tempBufferedWriter;
//
//        try {
//            //FileReader for inputFile
//            FileReader fileReader = new FileReader(inputFile);
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//
//            //Keeping track of current time interval stopping point
//            int stoppingPoint = intervalLengthInMilliseconds;
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
//                if(!line.equals("")) {
//                    //Reading a line
//                    lines.add(line);
//                }
//            }
//
//            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
//            int endIndex = 0;
//            for(String entry : lines){
//                String[]lineArray = fixation.lineToArray(entry);
//                int timestamp = Integer.parseInt(lineArray[0]);
//                //when it finds the endpoint, will save it
//                //if the timestamp equals the stopping point, include in the lines to be processed
//                if (timestamp == stoppingPoint) {
//                    endIndex = lines.indexOf(entry);
//                    break;
//                    //if the timestamp equals the stopping point, exclude the lines to be processed
//                }else if(timestamp > stoppingPoint) {
//                    endIndex = (lines.indexOf(entry))-1;
//                    break;
//                }
//
//            }
//            //Holding for the interval to be currently processed
//            int intervalToBeProcessed=stoppingPoint;
//
//            //Looking to find the endpoint for the next interval after the current to be processed
//            stoppingPoint+=intervalLengthInMilliseconds;
//
//
//            int startIndex=0;   //Will change from zero ONLY when gathering tumbling window stats
//
//
//            for(int i=endIndex+1;i<lines.size();i++){
//                //Looking for the point where the interval ends
//                String[]lineArray=fixation.lineToArray(lines.get(i));
//                int timestamp=Integer.parseInt(lineArray[0]);
//
//                //If point is found, will go on and process up to intervalToBeProcessed
//                if (timestamp>=stoppingPoint){
//                    tempFileWriter = new FileWriter(tempURL);
//                    tempBufferedWriter = new BufferedWriter(tempFileWriter);
//                    for (int j=startIndex;j<=endIndex;j++) {
//                        tempBufferedWriter.write(lines.get(j));
//                        tempBufferedWriter.newLine();
//                    }
//                    tempBufferedWriter.close();
//                    gaze.processGaze(tempURL, outputFile+"_"+(intervalToBeProcessed/1000/60)+"mins.csv",participant);
//
//                    //if tumbling window, will update the start index to not include previous lines processed
//                    if(intervalStatsType.equals("TBM")){
//                        startIndex=endIndex+1;
//                    }
//
//                    //Updating the end index
//                    if (timestamp == stoppingPoint) {
//                        endIndex = i;
//                    }else if(timestamp > stoppingPoint) {
//                        endIndex =i-1;
//                    }
//                    //Update the stopping point and interval to be processed
//                    stoppingPoint += intervalLengthInMilliseconds;
//                    intervalToBeProcessed+=intervalLengthInMilliseconds;
//                }
//            }
//
//            //Process the very last set of lines
//            tempFileWriter = new FileWriter(tempURL);
//            tempBufferedWriter = new BufferedWriter(tempFileWriter);
//            for (int j=startIndex;j<lines.size();j++) {
//                tempBufferedWriter.write(lines.get(j));
//                tempBufferedWriter.newLine();
//            }
//            tempBufferedWriter.close();
//            String lastline=lines.get(lines.size()-1);
//            String [] lastArray=fixation.lineToArray(lastline);
//            int lastInterval=Integer.parseInt(lastArray[0]);
//            gaze.processGaze(tempURL, outputFile+"_"+(intervalToBeProcessed/1000/60)+"mins.csv",participant);
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
//    }
//
//    public static void getEVDStats(String inputFile, String outputFile, String participant) throws IOException{
//
//        //Setting i) file name based on stats type and ii) the file extension
//        if (intervalStatsType.equals("TBM")) {
//            outputFile = outputFile + "TBM_win_time_Results";
//        } else if (intervalStatsType.equals("EXP")) {
//            outputFile = outputFile + "EXP_win_time_Results";
//        }else if (intervalStatsType.equals("HOP")) {
//            outputFile = outputFile + "HOP_win_time_Results";
//        }else{
//            System.out.println(intervalStatsType+" is not an statistics option");
//            return;
//        }
//
//
//        //Writer for temporary file
//        String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\Interval Results\\tempFile.txt";
//        FileWriter tempFileWriter;
//        BufferedWriter tempBufferedWriter;
//
//        try {
//            //FileReader for inputFile
//            System.out.println(inputFile);
//            FileReader fileReader = new FileReader(inputFile);
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//
//            //Holds the lines read from the raw data file by the reader
//            ArrayList<String> lines = new ArrayList<String>();
//
//            //Keeping track of current time interval stopping point
//            int stoppingPoint = intervalLengthInMilliseconds;
//
//            //Storing a line read from the file
//            String line = null;
//
//            //Reading from the input file
//            while ((line = bufferedReader.readLine()) != null) {
//                if(!line.equals("")) {
//                    //Reading a line
//                    lines.add(line);
//                }
//
//            }
//            bufferedReader.close();
//
//
//            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
//            int endIndex = 0;
//            for(String entry : lines){
//                String[]lineArray = fixation.lineToArray(entry);
//                int timestamp = Integer.parseInt(lineArray[0]);
//                //when it finds the endpoint, will save it
//                //if the timestamp equals the stopping point, include in the lines to be processed
//                if (timestamp == stoppingPoint) {
//                    endIndex = lines.indexOf(entry);
//                    break;
//                    //if the timestamp equals the stopping point, exclude the lines to be processed
//                }else if(timestamp > stoppingPoint) {
//                    endIndex = (lines.indexOf(entry))-1;
//                    break;
//                }
//
//            }
//            //Holding for the interval to be currently processed
//            int intervalToBeProcessed=stoppingPoint;
//
//            //Looking to find the endpoint for the next interval after the current to be processed
//            stoppingPoint+=intervalLengthInMilliseconds;
//
//
//            int startIndex=0;   //Will change from zero ONLY when gathering tumbling window stats
//
//
//            for(int i=endIndex+1;i<lines.size();i++){
//                //Looking for the point where the interval ends
//                String[]lineArray=fixation.lineToArray(lines.get(i));
//                int timestamp=Integer.parseInt(lineArray[0]);
//
//                //If point is found, will go on and process up to intervalToBeProcessed
//                if (timestamp>=stoppingPoint){
//                    tempFileWriter = new FileWriter(tempURL);
//                    tempBufferedWriter = new BufferedWriter(tempFileWriter);
//                    for (int j=startIndex;j<=endIndex;j++) {
//                        tempBufferedWriter.write(lines.get(j));
//                        tempBufferedWriter.newLine();
//                    }
//                    tempBufferedWriter.close();
//                    event.processEvent(tempURL, outputFile+"_"+(intervalToBeProcessed/1000/60)+"mins.csv",participant);
//
//                    //if tumbling window, will update the start index to not include previous lines processed
//                    if(intervalStatsType.equals("TBM")){
//                        startIndex=endIndex+1;
//                    }
//
//                    //Updating the end index
//                    if (timestamp == stoppingPoint) {
//                        endIndex = i;
//                    }else if(timestamp > stoppingPoint) {
//                        endIndex =i-1;
//                    }
//                    //Update the stopping point and interval to be processed
//                    stoppingPoint += intervalLengthInMilliseconds;
//                    intervalToBeProcessed+=intervalLengthInMilliseconds;
//                }
//            }
//
//            //Process the very last set of lines
//            tempFileWriter = new FileWriter(tempURL);
//            tempBufferedWriter = new BufferedWriter(tempFileWriter);
//            for (int j=startIndex;j<lines.size();j++) {
//                tempBufferedWriter.write(lines.get(j));
//                tempBufferedWriter.newLine();
//            }
//            tempBufferedWriter.close();
//            String lastline=lines.get(lines.size()-1);
//            String [] lastArray=fixation.lineToArray(lastline);
//            int lastInterval=Integer.parseInt(lastArray[0]);
//            event.processEvent(tempURL, outputFile+"_"+(intervalToBeProcessed/1000/60)+"mins.csv",participant);
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
//    }

    /* -----------------------------------------GENERATING .TXT FILES---------------------------------------*/

//    public static void getFXDStats(String inputFile, String outputFile) throws IOException {
//        if (intervalStatsType.equals("TBM")) {
//            outputFile = outputFile + "TBM_window_time_FXD_Results.txt";
//        } else if (intervalStatsType.equals("EXP")) {
//            outputFile = outputFile + "EXP_window_time_FXD_Results.txt";
//        }else if (intervalStatsType.equals("HOP")) {
//            outputFile = outputFile + "HOP_window_time_FXD_Results.txt";
//        }else{
//            System.out.println(intervalStatsType+" is not an statistics option");
//            return;
//        }
//
//        //Writer for the output file
//        FileWriter fileWriter=new FileWriter(outputFile);
//        BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
//
//        //Writing the header names to the output file for each column for each statistical result
//        String formatFieldNames = "%-9s %-25s %-25s %-25s %-25s %-25s %-25s %-12s  " +
//                "%-17s %-23s %-23s %-25s %-22s %-20s %-21s  " +
//                "%-23s %-25s %-25s %-20s %-22s %-20s  " +
//                " %-16s  %-9s  " +
//                " %-15s %-15s %-15s %-15s %-15s %-11s   " +
//                "%-16s %-13s %-20s %-17s %-20s %-11s   " +
//                "%-9s";
//        bufferedWriter.write(String.format(formatFieldNames,
//                "minutes", "total fixation duration", "sum fixation duration", "mean fixation duration" ,"median fixation duration", "SD fixation duration", "min fixation duration", "max fixation duration"
//                , "total saccades", "sum saccade length", "mean saccade length", "median saccade length", "SD saccade length", "min saccade length", "max saccade length"
//                , "sum saccade duration", "mean saccade duration", "median saccade duration", "SD saccade duration", "min saccade duration", "max saccade duration"
//                , "scanpath", "fixation to saccade"
//                ,"sum abs degree", " mean abs degree", " med abs degree", "SD abs degree", "min abs degree", "max abs degree"
//                , "sum rel degree", " mean rel degree", " median rel degree", "SD rel degree", "min rel degree", "max rel degree",
//                 "convex hull"));
//        bufferedWriter.newLine();
//        bufferedWriter.close();
//
//
//        //Writer for temporary file
//        String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\Interval Results\\tempFile.txt";
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
//            int stoppingPoint = intervalLengthInMilliseconds;
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
//                if(!line.equals("")) {
//                    //Reading a line
//                    lines.add(line);
//                }
//            }
//
//            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
//            int endIndex = 0;
//            for(String entry : lines){
//                String[]lineArray = fixation.lineToArray(entry);
//                int timestamp = Integer.parseInt(lineArray[1]);
//                //when it finds the endpoint, will save it
//                //if the timestamp equals the stopping point, include in the lines to be processed
//                if (timestamp == stoppingPoint) {
//                    endIndex = lines.indexOf(entry);
//                    break;
//                    //if the timestamp equals the stopping point, exclude the lines to be processed
//                }else if(timestamp > stoppingPoint) {
//                    endIndex = (lines.indexOf(entry))-1;
//                    break;
//                }
//
//            }
//            //Holding for the interval to be currently processed
//            int intervalToBeProcessed=stoppingPoint;
//
//            //Looking to find the endpoint for the next interval after the current to be processed
//            stoppingPoint+=intervalLengthInMilliseconds;
//
//
//            int startIndex=0;   //Will change from zero ONLY when gathering tumbling window stats
//
//
//            for(int i=endIndex+1;i<lines.size();i++){
//                //Looking for the point where the interval ends
//                String[]lineArray=fixation.lineToArray(lines.get(i));
//                int timestamp=Integer.parseInt(lineArray[1]);
//
//                //If point is found, will go on and process up to intervalToBeProcessed
//                if (timestamp>=stoppingPoint){
//                    tempFileWriter = new FileWriter(tempURL);
//                    tempBufferedWriter = new BufferedWriter(tempFileWriter);
//                    for (int j=startIndex;j<=endIndex;j++) {
//                        tempBufferedWriter.write(lines.get(j));
//                        tempBufferedWriter.newLine();
//                    }
//                    tempBufferedWriter.close();
//                    fixation.processFixation(tempURL, outputFile,(intervalToBeProcessed/60)/1000);
//
//                    //if tumbling window, will update the start index to not include previous lines processed
//                    if(intervalStatsType.equals("TBM")){
//                        startIndex=endIndex+1;
//                    }
//
//                    //Updating the end index
//                    if (timestamp == stoppingPoint) {
//                        endIndex = i;
//                    }else if(timestamp > stoppingPoint) {
//                        endIndex =i-1;
//                    }
//                    //Update the stopping point and interval to be processed
//                    stoppingPoint += intervalLengthInMilliseconds;
//                    intervalToBeProcessed+=intervalLengthInMilliseconds;
//                }
//            }
//
//            //Process the very last set of lines
//            tempFileWriter = new FileWriter(tempURL);
//            tempBufferedWriter = new BufferedWriter(tempFileWriter);
//            for (int j=startIndex;j<lines.size();j++) {
//                tempBufferedWriter.write(lines.get(j));
//                tempBufferedWriter.newLine();
//            }
//            tempBufferedWriter.close();
//            String lastline=lines.get(lines.size()-1);
//            String [] lastArray=fixation.lineToArray(lastline);
//            int lastInterval=Integer.parseInt(lastArray[1]);
//            fixation.processFixation(tempURL, outputFile, (lastInterval/60)/1000);
//
//
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
//
//
//
//    public static void getGZDStats(String inputFile, String outputFile) throws IOException{
//        //Setting i) file name based on stats type and ii) the file extension
//        if (intervalStatsType.equals("TBM")) {
//            outputFile = outputFile + "TBM_window_time_GZD_Results.txt";
//        } else if (intervalStatsType.equals("EXP")) {
//            outputFile = outputFile + "EXP_window_time_GZD_Results.txt";
//        }else if (intervalStatsType.equals("HOP")) {
//            outputFile = outputFile + "HOP_window_time_GZD_Results.txt";
//        }else{
//            System.out.println(intervalStatsType+"is not an statistics option");
//            return;
//        }
//
//        //Writer for the output file
//        FileWriter fileWriter=new FileWriter(outputFile);
//        BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
//
//        //Writing the header names to the output file for each column for each statistical result
//        String formatHeaderNames="%-12s %-12s %-15s %-15s %-15s";
//        bufferedWriter.write(String.format(formatHeaderNames,"Minutes","Num of valid recordings","Avg. pupil size left", "Avg. pupil size right","Avg. pupil size both"));
//        bufferedWriter.newLine();
//        bufferedWriter.close();
//
//        //Writer for temporary file
//        String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\Interval Results\\tempFile.txt";
//        FileWriter tempFileWriter;
//        BufferedWriter tempBufferedWriter;
//
//        try {
//            //FileReader for inputFile
//            FileReader fileReader = new FileReader(inputFile);
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//
//            //Keeping track of current time interval stopping point
//            int stoppingPoint = intervalLengthInMilliseconds;
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
//                if(!line.equals("")) {
//                    //Reading a line
//                    lines.add(line);
//                }
//            }
//
//            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
//            int endIndex = 0;
//            for(String entry : lines){
//                String[]lineArray = fixation.lineToArray(entry);
//                int timestamp = Integer.parseInt(lineArray[0]);
//                //when it finds the endpoint, will save it
//                //if the timestamp equals the stopping point, include in the lines to be processed
//                if (timestamp == stoppingPoint) {
//                    endIndex = lines.indexOf(entry);
//                    break;
//                    //if the timestamp equals the stopping point, exclude the lines to be processed
//                }else if(timestamp > stoppingPoint) {
//                    endIndex = (lines.indexOf(entry))-1;
//                    break;
//                }
//
//            }
//            //Holding for the interval to be currently processed
//            int intervalToBeProcessed=stoppingPoint;
//
//            //Looking to find the endpoint for the next interval after the current to be processed
//            stoppingPoint+=intervalLengthInMilliseconds;
//
//
//            int startIndex=0;   //Will change from zero ONLY when gathering tumbling window stats
//
//
//            for(int i=endIndex+1;i<lines.size();i++){
//                //Looking for the point where the interval ends
//                String[]lineArray=fixation.lineToArray(lines.get(i));
//                int timestamp=Integer.parseInt(lineArray[0]);
//
//                //If point is found, will go on and process up to intervalToBeProcessed
//                if (timestamp>=stoppingPoint){
//                    tempFileWriter = new FileWriter(tempURL);
//                    tempBufferedWriter = new BufferedWriter(tempFileWriter);
//                    for (int j=startIndex;j<=endIndex;j++) {
//                        tempBufferedWriter.write(lines.get(j));
//                        tempBufferedWriter.newLine();
//                    }
//                    tempBufferedWriter.close();
//                    gaze.processGaze(tempURL, outputFile,(intervalToBeProcessed/1000)/60);
//
//                    //if tumbling window, will update the start index to not include previous lines processed
//                    if(intervalStatsType.equals("TBM")){
//                        startIndex=endIndex+1;
//                    }
//
//                    //Updating the end index
//                    if (timestamp == stoppingPoint) {
//                        endIndex = i;
//                    }else if(timestamp > stoppingPoint) {
//                        endIndex =i-1;
//                    }
//                    //Update the stopping point and interval to be processed
//                    stoppingPoint += intervalLengthInMilliseconds;
//                    intervalToBeProcessed+=intervalLengthInMilliseconds;
//                }
//            }
//
//            //Process the very last set of lines
//            tempFileWriter = new FileWriter(tempURL);
//            tempBufferedWriter = new BufferedWriter(tempFileWriter);
//            for (int j=startIndex;j<lines.size();j++) {
//                tempBufferedWriter.write(lines.get(j));
//                tempBufferedWriter.newLine();
//            }
//            tempBufferedWriter.close();
//            String lastline=lines.get(lines.size()-1);
//            String [] lastArray=fixation.lineToArray(lastline);
//            int lastInterval=Integer.parseInt(lastArray[0]);
//            gaze.processGaze(tempURL, outputFile, (lastInterval/1000)/60);
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
//    }
//
//    public static void getEVDStats(String inputFile, String outputFile) throws IOException{
//
//        //Setting i) file name based on stats type and ii) the file extension
//
//        if (intervalStatsType.equals("TBM")) {
//            outputFile = outputFile + "TBM_window_time_EVD_Results.txt";
//        } else if (intervalStatsType.equals("EXP")) {
//            outputFile = outputFile + "EXP_window_time_EVD_Results.txt";
//        }else{
//            System.out.println(intervalStatsType+"is not an statistics option");
//            return;
//        }
//
//        //Writer for the output file
//        FileWriter fileWriter=new FileWriter(outputFile);
//        BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
//
//        //Writing the header name for each column to the output file
//        bufferedWriter.write("Minutes   Left mouse clicks");
//        bufferedWriter.newLine();
//        bufferedWriter.close();
//
//        //Writer for temporary file
//        String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\Interval Results\\tempFile.txt";
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
//            int stoppingPoint = intervalLengthInMilliseconds;
//
//            //Storing a line read from the file
//            String line = null;
//
//            //Reading from the input file
//            while ((line = bufferedReader.readLine()) != null) {
//             if(!line.equals("")) {
//                 //Reading a line
//                 lines.add(line);
//                }
//
//            }
//            bufferedReader.close();
//
//
//            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
//            int endIndex = 0;
//            for(String entry : lines){
//                String[]lineArray = fixation.lineToArray(entry);
//                int timestamp = Integer.parseInt(lineArray[0]);
//                //when it finds the endpoint, will save it
//                //if the timestamp equals the stopping point, include in the lines to be processed
//                if (timestamp == stoppingPoint) {
//                    endIndex = lines.indexOf(entry);
//                    break;
//                    //if the timestamp equals the stopping point, exclude the lines to be processed
//                }else if(timestamp > stoppingPoint) {
//                    endIndex = (lines.indexOf(entry))-1;
//                    break;
//                }
//
//            }
//            //Holding for the interval to be currently processed
//            int intervalToBeProcessed=stoppingPoint;
//
//            //Looking to find the endpoint for the next interval after the current to be processed
//            stoppingPoint+=intervalLengthInMilliseconds;
//
//
//            int startIndex=0;   //Will change from zero ONLY when gathering tumbling window stats
//
//
//            for(int i=endIndex+1;i<lines.size();i++){
//                //Looking for the point where the interval ends
//                String[]lineArray=fixation.lineToArray(lines.get(i));
//                int timestamp=Integer.parseInt(lineArray[0]);
//
//                //If point is found, will go on and process up to intervalToBeProcessed
//                if (timestamp>=stoppingPoint){
//                    tempFileWriter = new FileWriter(tempURL);
//                    tempBufferedWriter = new BufferedWriter(tempFileWriter);
//                    for (int j=startIndex;j<=endIndex;j++) {
//                        tempBufferedWriter.write(lines.get(j));
//                        tempBufferedWriter.newLine();
//                    }
//                    tempBufferedWriter.close();
//                    event.processEvent(tempURL, outputFile,(intervalToBeProcessed/1000)/60);
//
//                    //if tumbling window, will update the start index to not include previous lines processed
//                    if(intervalStatsType.equals("TBM")){
//                        startIndex=endIndex+1;
//                    }
//
//                    //Updating the end index
//                    if (timestamp == stoppingPoint) {
//                        endIndex = i;
//                    }else if(timestamp > stoppingPoint) {
//                        endIndex =i-1;
//                    }
//                    //Update the stopping point and interval to be processed
//                    stoppingPoint += intervalLengthInMilliseconds;
//                    intervalToBeProcessed+=intervalLengthInMilliseconds;
//                }
//            }
//
//            //Process the very last set of lines
//            tempFileWriter = new FileWriter(tempURL);
//            tempBufferedWriter = new BufferedWriter(tempFileWriter);
//            for (int j=startIndex;j<lines.size();j++) {
//                tempBufferedWriter.write(lines.get(j));
//                tempBufferedWriter.newLine();
//            }
//            tempBufferedWriter.close();
//            String lastline=lines.get(lines.size()-1);
//            String [] lastArray=fixation.lineToArray(lastline);
//            int lastInterval=Integer.parseInt(lastArray[0]);
//            event.processEvent(tempURL, outputFile, (lastInterval/1000)/60);
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
//    }



//}