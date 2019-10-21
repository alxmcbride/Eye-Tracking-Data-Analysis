package analysis;
import java.io.*;
import java.util.ArrayList;

/**
 * This class is used to gathers statistics over a set interval of time. Given are two options of how to gather them:
 *      1) Extending Window Interval
 *        This option allows for processing a set of raw data over a given interval, then adds to it the set of raw data over the next
 *        interval to be processed all together, and continues so forth until the end of the raw data file.
 *
 *      2) Sliding Window Interval
 *         This option allows for processing a set of raw data over a given interval, then processes the next set of raw data over
 *         the next interval separately from the previous set, and continues so forth until the end of the raw data file.
 *
 */

public class intervalStats {

    private static int intervalLengthInMinutes = 2; // <---ENTER THE MINUTES HERE

    private static int intervalLengthInMilliseconds = intervalLengthInMinutes * 60 * 1000; //Converting minutes to seconds

    private static String intervalStatsType = "extending";  //<----- ENTER: "sliding" for Sliding Window Stats    "extending" for Extending Window Stats



    public static void getFXDStats(String inputFile, String outputFile) throws IOException {

        //Setting i) file name based on stats type and ii) the file extension
        //SLD- sliding window interval statistics  EXT- extending window interval statistics
        if (intervalStatsType.equals("sliding")){
            outputFile=outputFile+"FXD_SLD_Results.txt";
        }else if(intervalStatsType.equals("extending")){
            outputFile=outputFile+"FXD_EXT_Results.txt";
        }

        //Writer for the output file
        FileWriter fileWriter=new FileWriter(outputFile);
        BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);

        //Writing the header names to the output file for each column for each statistical result
        String formatFieldNames = "%-9s %-9s %-20s %-15s %-15s %-15s %-12s %-12s  %-6s %-14s %-12s %-12s %-12s %-9s %-21s  %-16s %-16s %-11s"
                + " %-20s %-16s %-12s   %-16s  %-9s   %-15s %-15s %-15s %-15s %-15s %-11s   %-16s %-13s %-12s %-12s %-12s %-11s   %-9s";
        bufferedWriter.write(String.format(formatFieldNames, "minutes", "total fixation duration", "sum fixation duration", "mean fixation duration"
                ,"median fixation duration", "SD fixation duration", "min fixation duration", "max fixation duration"
                , "total saccades", "sum saccade length", "mean saccade length", "median saccade length", "SD saccade length", "min saccade length", "max saccade length"
                , "sum saccade duration", "mean saccade duration", "median saccade duration", "SD saccade duration", "min saccade duration", "max saccade duration"
                , "scanpath", "fixation to saccade", "sum abs degree", " mean abs degree", " med abs degree", "SD abs degree", "min abs degree", "max abs degree"
                , "sum rel degree", " mean rel degree", " median rel degree", "SD rel degree", "min rel degree", "max rel degree", "convex hull"));
        bufferedWriter.newLine();
        bufferedWriter.close();


        //Writer for temporary file
        String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\Interval Results\\tempFile.txt";
        FileWriter tempFileWriter;
        BufferedWriter tempBufferedWriter;


        try {

            //FileReader for inputFile
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //Keeping track of current time interval stopping point
            int stoppingPoint = intervalLengthInMilliseconds;

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

            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
            int endIndex = 0;
            for(String entry : lines){
                String[]lineArray = fixation.lineToArray(entry);
                int timestamp = Integer.parseInt(lineArray[1]);
                if (timestamp == stoppingPoint) {
                    endIndex = lines.indexOf(entry);
                    //    System.out.printf("End: %d ",endIndex);
                    break;
                }else if(timestamp > stoppingPoint) {
                    endIndex = (lines.indexOf(entry))-1;
                    //    System.out.printf("End: %d ",endIndex);
                    break;
                }

            }
            //Holding for the interval being processed
            int intervalToBeProcessed=stoppingPoint;

            //Looking to find the endpoint for the next interval
            stoppingPoint+=intervalLengthInMilliseconds;


            int startIndex=0;   //Will change from zero ONLY when gathering sliding window stats


            for(int i=endIndex+1;i<lines.size();i++){
                String[]lineArray=fixation.lineToArray(lines.get(i));
                int timestamp=Integer.parseInt(lineArray[1]);
                if (timestamp>=stoppingPoint){
                    tempFileWriter = new FileWriter(tempURL);
                    tempBufferedWriter = new BufferedWriter(tempFileWriter);
                    for (int j=startIndex;j<=endIndex;j++) {
                        tempBufferedWriter.write(lines.get(j));
                        //    System.out.println(lines.get(j));
                        tempBufferedWriter.newLine();
                    }
                    tempBufferedWriter.close();
                    fixation.processFixation(tempURL, outputFile,intervalToBeProcessed);
                    if(intervalStatsType.equals("sliding")){
                        startIndex=endIndex+1;
                    }
                    if (timestamp == stoppingPoint) {
                        endIndex = i;
                    }else if(timestamp > stoppingPoint) {
                        endIndex =i-1;
                    }
                    stoppingPoint += intervalLengthInMilliseconds;
                    intervalToBeProcessed+=intervalLengthInMilliseconds;
                }
            }
            tempFileWriter = new FileWriter(tempURL);
            tempBufferedWriter = new BufferedWriter(tempFileWriter);
            for (int j=startIndex;j<lines.size();j++) {
                tempBufferedWriter.write(lines.get(j));
                // System.out.println(lines.get(j));
                tempBufferedWriter.newLine();
            }
            tempBufferedWriter.close();
            String lastline=lines.get(lines.size()-1);
            String [] lastArray=fixation.lineToArray(lastline);
            int lastInterval=Integer.parseInt(lastArray[1]);
            fixation.processFixation(tempURL, outputFile, lastInterval);




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

    public static void getGZDStats(String inputFile, String outputFile) throws IOException{
        //Setting i) file name based on stats type and ii) the file extension
        //SLD- sliding window interval statistics  EXT- extending window interval statistics
        if (intervalStatsType.equals("sliding")){
            outputFile=outputFile+"GZD_SLD_Results.txt";
        }else if(intervalStatsType.equals("extending")){
            outputFile=outputFile+"GZD_EXT_Results.txt";
        }

        //Writer for the output file
        FileWriter fileWriter=new FileWriter(outputFile);
        BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);

        //Writing the header names to the output file for each column for each statistical result
        String formatHeaderNames="%-12s %-12s %-15s %-15s %-15s";
        bufferedWriter.write(String.format(formatHeaderNames,"Minutes","Num of valid recordings","Avg. pupil size left", "Avg. pupil size right","Avg. pupil size both"));
        bufferedWriter.newLine();
        bufferedWriter.close();

        //Writer for temporary file
        String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\Interval Results\\tempFile.txt";
        FileWriter tempFileWriter;
        BufferedWriter tempBufferedWriter;

        try {
            //FileReader for inputFile
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //Keeping track of current time interval stopping point
            int stoppingPoint = intervalLengthInMilliseconds;

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

            int endIndex = 0;
            //Want to get endpoint for the first interval
            for(String entry : lines) {
                String[] lineArray = fixation.lineToArray(entry);
                int timestamp = Integer.parseInt(lineArray[0]);
                if (timestamp == stoppingPoint) {
                    endIndex = lines.indexOf(entry);
                     //  System.out.printf("End: %d\n",endIndex);
                    break;
                } else if (timestamp > stoppingPoint) {
                    endIndex = (lines.indexOf(entry)) - 1;
                    // System.out.printf("End: %d\n",endIndex);
                    break;
                }
            }

            int intervalToBeProcessed=stoppingPoint;
            stoppingPoint+=intervalLengthInMilliseconds;
            int startIndex=0;   //Will used only for the sliding window for shifting the starting point


            for(int i=endIndex+1;i<lines.size();i++){
                String[]lineArray=fixation.lineToArray(lines.get(i));
                int timestamp=Integer.parseInt(lineArray[0]);
                if (timestamp>=stoppingPoint){
                    tempFileWriter = new FileWriter(tempURL);
                    tempBufferedWriter = new BufferedWriter(tempFileWriter);
                    for (int j=startIndex;j<=endIndex;j++) {
                        tempBufferedWriter.write(lines.get(j));
                       // System.out.println(lines.get(j));
                        tempBufferedWriter.newLine();
                    }
                    tempBufferedWriter.close();
                    gaze.processGaze(tempURL, outputFile, intervalToBeProcessed);
                    if (timestamp == stoppingPoint) {
                        endIndex = i;
                    }else if(timestamp > stoppingPoint) {
                        endIndex =i-1;
                    }
                    stoppingPoint += intervalLengthInMilliseconds;
                    intervalToBeProcessed+=intervalLengthInMilliseconds;

                }
            }
            tempFileWriter = new FileWriter(tempURL);
            tempBufferedWriter = new BufferedWriter(tempFileWriter);
            for (int j=startIndex;j<lines.size();j++) {
                tempBufferedWriter.write(lines.get(j));
                tempBufferedWriter.newLine();
            }
            tempBufferedWriter.close();
            String lastline=lines.get(lines.size()-1);
            String [] lastArray=fixation.lineToArray(lastline);
            int lastInterval=Integer.parseInt(lastArray[0]);
            gaze.processGaze(tempURL, outputFile, lastInterval);

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

        //Setting stats type and extension
        if (intervalStatsType.equals("sliding")){
            outputFile=outputFile+"EVD_SLD_Results.txt";
        }else{
            outputFile=outputFile+"EVD_EXT_Results.txt";
        }

        //Writing the header name for each column to the output file
        FileWriter fileWriter=new FileWriter(outputFile);
        BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
        bufferedWriter.write("Minutes   Left mouse clicks");
        bufferedWriter.newLine();
        bufferedWriter.close();

        try {
            //FileReader for inputFile
            System.out.println(inputFile);
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //Holds the lines read from the raw data file by the reader
            ArrayList<String> lines = new ArrayList<String>();

            //Keeping track of current time interval stopping point
            int stoppingPoint = intervalLengthInMilliseconds;

            //Storing a line read from the file
            String line = null;

            //Reading from the input file
            while ((line = bufferedReader.readLine()) != null) {
             if(!line.equals("")) {
                 //Reading a line
                 lines.add(line);
                 System.out.println(line);
             }else if(line.equals("")){
                 break;
             }
            }
            bufferedReader.close();

            //FileWriter for temporary file
            String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\Interval Results\\tempFile.txt";

            //Want to get endpoint for the first interval
            int endIndex = 0;
            for(String entry : lines){
                String[]lineArray = fixation.lineToArray(entry);
                    int timestamp = Integer.parseInt(lineArray[0]);
                    if (timestamp == stoppingPoint) {
                        endIndex = lines.indexOf(entry);
                    //    System.out.printf("End: %d ",endIndex);
                        break;
                    }else if(timestamp > stoppingPoint) {
                        endIndex = (lines.indexOf(entry))-1;
                    //    System.out.printf("End: %d ",endIndex);
                        break;
                    }

                }


            int startIndex=0;   //Will only change for the sliding window for shifting the starting point
            int intervalToBeProcessed=stoppingPoint;
            stoppingPoint+=intervalLengthInMilliseconds;
            FileWriter tempFileWriter;
            BufferedWriter tempBufferedWriter;
            for(int i=endIndex+1;i<lines.size();i++){
                String[]lineArray=fixation.lineToArray(lines.get(i));
                int timestamp=Integer.parseInt(lineArray[0]);
                if (timestamp>=stoppingPoint){
                    tempFileWriter = new FileWriter(tempURL);
                    tempBufferedWriter = new BufferedWriter(tempFileWriter);
                    for (int j=startIndex;j<=endIndex;j++) {
                        tempBufferedWriter.write(lines.get(j));
                    //    System.out.println(lines.get(j));
                        tempBufferedWriter.newLine();
                    }
                    tempBufferedWriter.close();
                    event.processEvent(tempURL, outputFile,intervalToBeProcessed);
                    if(intervalStatsType.equals("sliding")){
                        startIndex=endIndex+1;
                    }
                    if (timestamp == stoppingPoint) {
                        endIndex = i;
                    }else if(timestamp > stoppingPoint) {
                        endIndex =i-1;
                    }
                    stoppingPoint += intervalLengthInMilliseconds;
                    intervalToBeProcessed+=intervalLengthInMilliseconds;
                }
            }
            tempFileWriter = new FileWriter(tempURL);
            tempBufferedWriter = new BufferedWriter(tempFileWriter);
            for (int j=startIndex;j<lines.size();j++) {
                tempBufferedWriter.write(lines.get(j));
               // System.out.println(lines.get(j));
                tempBufferedWriter.newLine();
            }
            tempBufferedWriter.close();
            String lastline=lines.get(lines.size()-1);
            String [] lastArray=fixation.lineToArray(lastline);
            int lastInterval=Integer.parseInt(lastArray[0]);
            event.processEvent(tempURL, outputFile,lastInterval) ;

            //Deleting temp file
//            File tempFile = new File(tempURL);
//            tempFile.deleteOnExit();
//            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + inputFile + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + inputFile + "'");
        }
    }




}