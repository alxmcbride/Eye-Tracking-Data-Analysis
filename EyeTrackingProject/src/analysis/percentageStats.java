package analysis;

import java.io.*;
import java.util.ArrayList;

/**
 * This class is used to gathers statistics for a set percentage of lines. Given are two options of how to gather them:
 *      1) Accumulating Window Interval
 *        This option allows for processing a set of raw data for a set percentage of lines, then adds to it the set of raw data for the same percentage of lines
 *       to be processed all together, and continues so forth until the end of the raw data file.
 *
 *      2) Sliding Window Interval
 *         This option allows for processing a set percentage of lines, then processes the next set a set percentage of lines
 *         separately from the previous set, and continues so forth until the end of the raw data file.
 *
 */

public class percentageStats {

    private static double wholePercentage = 10.0; // <---ENTER THE PERCENTAGE HERE

    private static double decimalPercentage = 0.10;

    private static String percentageStatsType = "sliding";  //<----- ENTER: "sliding" for Sliding Window Percentage Stats   "accumulating" for Accumulating Window
    // Percentage Stats

    public static void getFXDStats(String inputFile, String outputFile) throws IOException {
        //Setting i) file name based on stats type and ii) the file extension
        //SLD- sliding window interval statistics  ACM- accumulating window interval statistics
        if (percentageStatsType.equals("sliding")) {
            outputFile = outputFile + "FXD_SLD_Results.txt";
        } else if (percentageStatsType.equals("accumulating")) {
            outputFile = outputFile + "FXD_ACM_Results.txt";
        }

        //Writer for the output file
        FileWriter fileWriter = new FileWriter(outputFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        //Writing the header names to the output file for each column for each statistical result
        String formatFieldNames = "%-9s %-9s %-20s %-15s %-15s %-15s %-12s %-12s  %-6s %-14s %-12s %-12s %-12s %-9s %-21s  %-16s %-16s %-11s"
                + " %-20s %-16s %-12s   %-16s  %-9s   %-15s %-15s %-15s %-15s %-15s %-11s   %-16s %-13s %-12s %-12s %-12s %-11s   %-9s";
        bufferedWriter.write(String.format(formatFieldNames, "percentage", "total fixation duration", "sum fixation duration", "mean fixation duration"
                , "median fixation duration", "SD fixation duration", "min fixation duration", "max fixation duration"
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
            double stoppingPoint = decimalPercentage;
            double percent = wholePercentage;

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

            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
            int endIndex = 0;
            int startIndex = 0;   //Will change from zero ONLY when gathering sliding window stats
            while (stoppingPoint <= 0.9) {
                for (int i = startIndex; i < lines.size(); i++) {
                    //If point is found, will go on and process up to intervalToBeProcessed
                    if (i == numOfLines) {
                        endIndex = i;
                        tempFileWriter = new FileWriter(tempURL);
                        tempBufferedWriter = new BufferedWriter(tempFileWriter);
                        for (int j = startIndex; j <= endIndex; j++) {
                            tempBufferedWriter.write(lines.get(j));
                            tempBufferedWriter.newLine();
                        }
                        tempBufferedWriter.close();
                        fixation.processFixation(tempURL, outputFile, percent);
                        percent += wholePercentage;

                        //if sliding window, will update the start index to not include previous lines processed
                        if (percentageStatsType.equals("sliding")) {
                            startIndex = endIndex + 1;
                        }

                        //Update the stopping point and interval to be processed
                        stoppingPoint += decimalPercentage;
                        numOfLines = (int) Math.floor(totalLines * stoppingPoint);

                    }
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
        //SLD- sliding window interval statistics  ACM- accumulating window interval statistics
        if (percentageStatsType.equals("sliding")) {
            outputFile = outputFile + "GZD_SLD_Results.txt";
        } else if (percentageStatsType.equals("accumulating")) {
            outputFile = outputFile + "GZD_ACM_Results.txt";
        }

        //Writer for the output file
        FileWriter fileWriter = new FileWriter(outputFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        //Writing the header names to the output file for each column for each statistical result
        String formatHeaderNames = "%-12s %-12s %-15s %-15s %-15s";
        bufferedWriter.write(String.format(formatHeaderNames, "Percentage", "Num of valid recordings", "Avg. pupil size left", "Avg. pupil size right", "Avg. pupil size both"));
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
            double stoppingPoint = decimalPercentage;
            double percent = wholePercentage;

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

            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
            int endIndex = 0;
            int startIndex = 0;   //Will change from zero ONLY when gathering sliding window stats
            while (stoppingPoint <= 0.9) {
                for (int i = startIndex; i < lines.size(); i++) {
                    //If point is found, will go on and process up to intervalToBeProcessed
                    if (i == numOfLines) {
                        endIndex = i;
                        tempFileWriter = new FileWriter(tempURL);
                        tempBufferedWriter = new BufferedWriter(tempFileWriter);
                        for (int j = startIndex; j <= endIndex; j++) {
                            tempBufferedWriter.write(lines.get(j));
                            tempBufferedWriter.newLine();
                        }
                        tempBufferedWriter.close();
                        gaze.processGaze(tempURL, outputFile, percent);
                        percent += wholePercentage;

                        //if sliding window, will update the start index to not include previous lines processed
                        if (percentageStatsType.equals("sliding")) {
                            startIndex = endIndex + 1;
                        }

                        //Update the stopping point and interval to be processed
                        stoppingPoint += decimalPercentage;
                        numOfLines = (int) Math.floor(totalLines * stoppingPoint);

                    }
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

    public static void getEVDStats(String inputFile, String outputFile) throws IOException {
        //Setting i) file name based on stats type and ii) the file extension
        //SLD- sliding window interval statistics  ACM- accumulating window interval statistics
        if (percentageStatsType.equals("sliding")) {
            outputFile = outputFile + "EVD_SLD_Results.txt";
        } else  if (percentageStatsType.equals("accumulating")) {
            outputFile = outputFile + "EVD_ACM_Results.txt";
        }
        //Writer for the output file
        FileWriter fileWriter = new FileWriter(outputFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        //Writing the header name for each column to the output file
        bufferedWriter.write("Percentage  Left mouse clicks");
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

            //Holds the lines read from the raw data file by the reader
            ArrayList<String> lines = new ArrayList<String>();

            //Keeping track of current time interval stopping point
            double stoppingPoint = decimalPercentage;
            double percent = wholePercentage;

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

            //Getting the number of lines read
            int totalLines = lines.size();

            int numOfLines = (int) Math.floor(totalLines * stoppingPoint);

            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
            int endIndex = 0;
            int startIndex = 0;   //Will change from zero ONLY when gathering sliding window stats
            while (stoppingPoint <= 0.9) {
                for (int i = startIndex; i < lines.size(); i++) {
                    //If point is found, will go on and process up to intervalToBeProcessed
                    if (i == numOfLines) {
                        endIndex = i;
                        tempFileWriter = new FileWriter(tempURL);
                        tempBufferedWriter = new BufferedWriter(tempFileWriter);
                        for (int j = startIndex; j <= endIndex; j++) {
                            tempBufferedWriter.write(lines.get(j));
                            tempBufferedWriter.newLine();
                        }
                        tempBufferedWriter.close();
                        event.processEvent(tempURL, outputFile, percent);
                        percent += wholePercentage;

                        //if sliding window, will update the start index to not include previous lines processed
                        if (percentageStatsType.equals("sliding")) {
                            startIndex = endIndex + 1;
                        }

                        //Update the stopping point and interval to be processed
                        stoppingPoint += decimalPercentage;
                        numOfLines = (int) Math.floor(totalLines * stoppingPoint);

                    }
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
}
