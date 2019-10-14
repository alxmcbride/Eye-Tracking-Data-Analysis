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

    private static boolean isSlidingWindow = false;  //<----- ENTER: true for Sliding Window Stats    false for Extending Window Stats



    public static void getFXDStats(String inputFile, String outputFile) throws IOException {

        //Storing line read from the file
        String line = null;

        //FileWriter for temporary file
        String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\Interval Results\\tempFile.txt";

        //Writing the header name for each column
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        if (isSlidingWindow){
            outputFile=outputFile+"FXD_SLD_Results.txt";
            fileWriter = new FileWriter(outputFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("Sliding Window Stats");
            bufferedWriter.newLine();


        }else{
            outputFile=outputFile+"FXD_EXT_Results.txt";
            fileWriter = new FileWriter(outputFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("Extending Window Stats");
            bufferedWriter.newLine();

        }

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


        try {
            //FileReader for inputFile
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //Holds the lines read from the raw data file by the reader
            ArrayList<String> lines = new ArrayList<String>();

            //Keeping track of current time interval stopping point
            int stoppingPoint = intervalLengthInMilliseconds;

            //Reading from the input file
            while ((line = bufferedReader.readLine()) != null) {

                //Reading a line
                lines.add(line);
            }

            //Want to get endpoint for the first interval
            int endIndex=0;
            for(String entry: lines){
                String[]lineArray=fixation.lineToArray(entry);
                int timestamp=Integer.parseInt(lineArray[1]);
                if (timestamp>=stoppingPoint){
                    endIndex=lines.indexOf(entry);
                    break;
                }
            }

            int startIndex=0;   //Will use only for the sliding window for shifting the starting point
            stoppingPoint+=intervalLengthInMilliseconds;
            FileWriter tempFileWriter;
            BufferedWriter tempBufferedWriter;
            //will check if the next interval goes for the set amount of time
            for(int i=endIndex+1;i<lines.size();i++){
                String[]lineArray=fixation.lineToArray(lines.get(i));
                int timestamp=Integer.parseInt(lineArray[1]);
                //once it reaches the end of the inteval, will write to the temp file
                if (timestamp>=stoppingPoint){
                    tempFileWriter = new FileWriter(tempURL);
                    tempBufferedWriter = new BufferedWriter(tempFileWriter);
                    for (int j=startIndex;j<=endIndex;j++) {
                         tempBufferedWriter.write(lines.get(j));
                         tempBufferedWriter.newLine();
                    }
                     tempBufferedWriter.close();
                    fixation.processFixation(tempURL, outputFile);
                    stoppingPoint += intervalLengthInMilliseconds;
                    if(isSlidingWindow){
                     startIndex=endIndex+1;
                    }
                    endIndex=i;

                }
            }

            //used to write for the very last interval
            tempFileWriter = new FileWriter(tempURL);
            tempBufferedWriter = new BufferedWriter(tempFileWriter);
            for (int j=startIndex;j<lines.size();j++) {
                tempBufferedWriter.write(lines.get(j));
                tempBufferedWriter.newLine();
            }
            tempBufferedWriter.close();
            fixation.processFixation(tempURL, outputFile);




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
        //Storing line read from the file
        String line = null;

        //FileWriter for temporary file
        String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\Interval Results\\tempFile.txt";

        //Writing the header name for each column
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        if (isSlidingWindow){
            outputFile=outputFile+"GZD_SLD_Results.txt";
            fileWriter = new FileWriter(outputFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("Sliding Window Stats");
            bufferedWriter.newLine();


        }else{
            outputFile=outputFile+"GZD_EXT_Results.txt";
            fileWriter = new FileWriter(outputFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("Extending Window Stats");
            bufferedWriter.newLine();

        }

        String formatHeaderNames="%-12s %-12s %-15s %-15s %-15s";
        bufferedWriter.write(String.format(formatHeaderNames,"Minutes","Num of valid recordings","Avg. pupil size left", "Avg. pupil size right","Avg. pupil size both"));
        bufferedWriter.newLine();
        bufferedWriter.close();

        try {
            //FileReader for inputFile
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //Holds the lines read from the raw data file by the reader
            ArrayList<String> lines = new ArrayList<String>();

            //Keeping track of current time interval stopping point
            int stoppingPoint = intervalLengthInMilliseconds;

            //Reading from the input file
            while ((line = bufferedReader.readLine()) != null) {

                //Reading a line
                lines.add(line);
            }

            //Want to get endpoint for the first interval
            int endIndex=0;
            for(String entry: lines){
                String[]lineArray=fixation.lineToArray(entry);
                int timestamp=Integer.parseInt(lineArray[0]);
                if (timestamp>=stoppingPoint){
                    endIndex=lines.indexOf(entry);
                    break;
                }
            }

            int startIndex=0;   //Will used only for the sliding window for shifting the starting point
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
                        tempBufferedWriter.newLine();
                    }
                    tempBufferedWriter.close();
                    gaze.processGaze(tempURL, outputFile);
                    stoppingPoint += intervalLengthInMilliseconds;
                    if(isSlidingWindow){
                        startIndex=endIndex+1;
                    }
                    endIndex=i;

                }
            }
            tempFileWriter = new FileWriter(tempURL);
            tempBufferedWriter = new BufferedWriter(tempFileWriter);
            for (int j=startIndex;j<lines.size();j++) {
                tempBufferedWriter.write(lines.get(j));
                tempBufferedWriter.newLine();
            }
            tempBufferedWriter.close();
            gaze.processGaze(tempURL, outputFile);

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
        String line = null;

        //FileWriter for temporary file
        String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\Interval Results\\tempFile.txt";

        //Writing the header name for each column
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        if (isSlidingWindow){
            outputFile=outputFile+"EVD_SLD_Results.txt";
            fileWriter = new FileWriter(outputFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("Sliding Window Stats");
            bufferedWriter.newLine();


        }else{
            outputFile=outputFile+"EVD_EXT_Results.txt";
            fileWriter = new FileWriter(outputFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("Extending Window Stats");
            bufferedWriter.newLine();

        }

        bufferedWriter.write("Minutes   Left mouse clicks");
        bufferedWriter.newLine();
        bufferedWriter.close();

        try {
            //FileReader for inputFile
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //Holds the lines read from the raw data file by the reader
            ArrayList<String> lines = new ArrayList<String>();

            //Keeping track of current time interval stopping point
            int stoppingPoint = intervalLengthInMilliseconds;

            //Reading from the input file
            while ((line = bufferedReader.readLine()) != null) {

                //Reading a line
                lines.add(line);
            }

            //Want to get endpoint for the first interval
            int endIndex=0;
            for(String entry: lines){
                String[]lineArray=fixation.lineToArray(entry);
                int timestamp=Integer.parseInt(lineArray[0]);
                if (timestamp>=stoppingPoint){
                    endIndex=lines.indexOf(entry);
                    break;
                }
            }

            int startIndex=0;   //Will used only for the sliding window for shifting the starting point
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
                        tempBufferedWriter.newLine();
                    }
                    tempBufferedWriter.close();
                    event.processEvent(tempURL, outputFile);
                    stoppingPoint += intervalLengthInMilliseconds;
                    if(isSlidingWindow){
                        startIndex=endIndex+1;
                    }
                    endIndex=i;

                }
            }
            tempFileWriter = new FileWriter(tempURL);
            tempBufferedWriter = new BufferedWriter(tempFileWriter);
            for (int j=startIndex;j<lines.size();j++) {
                tempBufferedWriter.write(lines.get(j));
                tempBufferedWriter.newLine();
            }
            tempBufferedWriter.close();
            event.processEvent(tempURL, outputFile);

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




}