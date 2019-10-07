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
        FileWriter fileWriter = new FileWriter(outputFile, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        if (isSlidingWindow){
            bufferedWriter.write("Sliding Window Stats");
            bufferedWriter.newLine();
        }else{
            bufferedWriter.write("Extending Window Stats");
            bufferedWriter.newLine();
        }


        String formatFieldNames = "%-9s %-9s %-20s %-15s %-15s %-15s %-12s %-12s  %-6s %-14s %-12s %-12s %-12s %-9s %-21s  %-16s %-16s %-11s"
                + " %-20s %-16s %-12s   %-16s  %-9s   %-15s %-15s %-15s %-15s %-15s %-11s   %-16s %-13s %-12s %-12s %-12s %-11s   %-9s";
        bufferedWriter.write(String.format(formatFieldNames, "minutes", "ttl fxd", "sum fxd", "mean xd", "med fxd", "SD fxd", "min fxd", "max fxd"
                , "scd", "sum scdL", "mean scdL", "med scdL", "SD scdL", "min scdL", "max scdL"
                , "sum scdr", "mean scdr", "med scdr", "SD scdr", "min scdr", "max scdr"
                , "spth", "fxd2scd", "sum ad", " mean ad", " med ad", "SD ad", "min ad", "max ad"
                , "sum rd", " mean rd", " med rd", "SD rd", "min rd", "max rd", "cvx hull"));
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

                //Reading a line and getting timestamp
                lines.add(line);
                String[] lineArray = fixation.lineToArray(line);
                int timestamp = Integer.parseInt(lineArray[1]);

                //Checking if the timestamp is greater or equal to the stopping point
                if (timestamp >= stoppingPoint || lineArray[0].equals(fixation.getFixationCount(inputFile))) {

                    //Writing to output file
                    FileWriter tempFileWriter = new FileWriter(tempURL);
                    BufferedWriter tempBufferedWriter = new BufferedWriter(tempFileWriter);
                    for (String entry : lines) {
                        tempBufferedWriter.write(entry);
                        tempBufferedWriter.newLine();
                    }
                    tempBufferedWriter.close();
                    fixation.processFixation(tempURL, outputFile);
                    stoppingPoint += intervalLengthInMilliseconds;

                    //Used to determine if chosen option is Extending Window or Sliding Window
                    if(isSlidingWindow){
                        lines=new ArrayList<String>(); //makes a new empty list if Sliding Window
                    }
                }

            }
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
        FileWriter fileWriter = new FileWriter(outputFile, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        if (isSlidingWindow){
            bufferedWriter.write("Sliding Window Stats");
            bufferedWriter.newLine();
        }else{
            bufferedWriter.write("Extending Window Stats");
            bufferedWriter.newLine();
        }

        String formatHeaderNames="%-9s %-9s %-15s %-15s %-15s";
        bufferedWriter.write(String.format(formatHeaderNames,"mins","# vr","aps le", "aps re","aps both"));
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
               // System.out.println(line);

                //Reading a line and getting timestamp
                 lines.add(line);
                 String[] lineArray = fixation.lineToArray(line);
                 int timestamp = Integer.parseInt(lineArray[0]);

                 //Checking if the timestamp is greater or equal to the stopping point
                if (timestamp >= stoppingPoint || lineArray[0].equals(fixation.getFixationCount(inputFile))) {
                      System.out.println("\nTrue\n");
                     //Writing to output file
                     FileWriter tempFileWriter = new FileWriter(tempURL);
                     BufferedWriter tempBufferedWriter = new BufferedWriter(tempFileWriter);
                     for (String entry : lines) {
                        tempBufferedWriter.write(entry);
                        tempBufferedWriter.newLine();
                     }
                     tempBufferedWriter.close();
                      gaze.processGaze(tempURL, outputFile);
                     stoppingPoint += intervalLengthInMilliseconds;
//
                    //Used to determine if chosen option is Extending Window or Sliding Window
                    if(isSlidingWindow){
                    lines=new ArrayList<String>(); //makes a new empty list if Sliding Window
                     }
                 }

            }
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
        FileWriter fileWriter = new FileWriter(outputFile, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        if (isSlidingWindow){
            bufferedWriter.write("Sliding Window Stats");
            bufferedWriter.newLine();
        }else{
            bufferedWriter.write("Extending Window Stats");
            bufferedWriter.newLine();
        }

        bufferedWriter.write("minutes   Left mouse clicks");
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
                // System.out.println(line);

                //Reading a line and getting timestamp
                lines.add(line);
                String[] lineArray = fixation.lineToArray(line);
                int timestamp = Integer.parseInt(lineArray[0]);

                //Checking if the timestamp is greater or equal to the stopping point
                if (timestamp >= stoppingPoint || lineArray[0].equals(fixation.getFixationCount(inputFile))) {

                    //Writing to output file
                    FileWriter tempFileWriter = new FileWriter(tempURL);
                    BufferedWriter tempBufferedWriter = new BufferedWriter(tempFileWriter);
                    for (String entry : lines) {
                        tempBufferedWriter.write(entry);
                        tempBufferedWriter.newLine();
                    }
                    tempBufferedWriter.close();
                    event.processEvent(tempURL, outputFile);
                    stoppingPoint += intervalLengthInMilliseconds;
//
                    //Used to determine if chosen option is Extending Window or Sliding Window
                    if(isSlidingWindow){
                        lines=new ArrayList<String>(); //makes a new empty list if Sliding Window
                    }
                }

            }
            System.out.println(fixation.getFixationCount())

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