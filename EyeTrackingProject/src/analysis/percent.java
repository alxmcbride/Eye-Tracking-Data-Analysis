package analysis;

import java.io.*;
import java.util.ArrayList;

public class percent {
    private static double windowSizeWholeValue = 10.0; // <---ENTER THE PERCENTAGE HERE

    private static double windowSizeDecimalValue= windowSizeWholeValue/100;

    private static String percentageStatsType = "HOP";  //<----- ENTER: "TBM" for Tumbling Window Percentage Stats
                                                                     // "EXP" for Expanding Window Percentage Stats
                                                                     //" HOP" for Hopping Window Percentage STATS
    // For Hopping Window Option ONLY
    private static double hopsizeWholeValue = 5.0; // <---ENTER THE PERCENTAGE HERE

    private static double hopsizeDecimalValue = hopsizeWholeValue/100; //Converting to decimal

    //-----------------------------------GENERATING .CSV FILES---------------------------------------------------
    /*Note: Make sure to comment out when using .txt methods
    //      When doing this, want to make sure to run code from p1 to p36(last participant) to keep the order in .csv files.
         If it comes out incorrect for a participant, have to start all over */

    public static void getFXDStats(String inputFile, String outputFile, String participant, String visualType) throws IOException {
        //Setting i) file name based on stats type and ii) the file extension
        if (percentageStatsType.equals("TBM")) {
            outputFile = outputFile + "TBM_win_percentage_Results";
        } else if (percentageStatsType.equals("EXP")) {
            outputFile = outputFile + "EXP_win_percentage_Results";
        }else if (percentageStatsType.equals("HOP")){
            outputFile = outputFile + "HOP_win_percentage_Results";
        }else{
            System.out.println(percentageStatsType+" is not an statistics option");
            return;
        }

            /*Putting the field names in the output file. In .csv files, we will start out with the fixation
         for participant 1 with the tree visualization, so we will check to see if participant is "p1" and visualType is "tree"
         */

        // Opening writers
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        double temp_percent = hopsizeWholeValue;
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
            while(temp_percent<=100.0) {
                System.out.println(temp_percent);
                fileWriter = new FileWriter(outputFile + "_" + temp_percent + "%%.csv");
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
                temp_percent+= hopsizeWholeValue;
            }
        }

        //Writer for temporary file
        String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\Interval Results\\tempFile.txt";
        FileWriter tempFileWriter;
        BufferedWriter tempBufferedWriter;


        try {

            //FileReader for inputFile
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //Keeping track of current time interval stopping point
            double stoppingPoint;
            if(percentageStatsType.equals("HOP")){
                stoppingPoint = hopsizeDecimalValue;
            }else{
                stoppingPoint = (int)windowSizeDecimalValue;
            }

            double percent;
            if(percentageStatsType.equals("HOP")){
                percent = hopsizeWholeValue;
            }else{
                percent = (int)windowSizeWholeValue;
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

            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
            int endIndex = 0;
            int startIndex = 0;   //Will change from zero ONLY when gathering tumbling window or hopping window stats

            //For Hopping Window only
            int nextStartIndex=0;



            while (stoppingPoint <= 1.0) {
                for (int i = startIndex; i < lines.size(); i++) {
                    //If point is found, will go on and process up to intervalToBeProcessed
                    if (i == numOfLines-1) {
                        endIndex = i;
                        tempFileWriter = new FileWriter(tempURL);
                        tempBufferedWriter = new BufferedWriter(tempFileWriter);

                        for (int j = startIndex; j <= endIndex; j++) {
                            tempBufferedWriter.write(lines.get(j));
                            tempBufferedWriter.newLine();
                        }
                        tempBufferedWriter.close();
                        fixation.processFixation(tempURL, outputFile + "_" + percent + "%%.csv", participant);


                        if(percentageStatsType.equals("HOP")){
                            if( stoppingPoint < windowSizeDecimalValue){
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
                        //Update the stopping point and interval to be processed
                        if(percentageStatsType.equals("HOP")) {
                            stoppingPoint += hopsizeDecimalValue;
                            percent += hopsizeWholeValue;
                        }else{
                            stoppingPoint += windowSizeDecimalValue;
                            percent+= windowSizeWholeValue;
                        }
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
        if (percentageStatsType.equals("TBM")) {
            outputFile = outputFile + "TBM_win_percentage_Results";
        } else if (percentageStatsType.equals("EXP")) {
            outputFile = outputFile + "EXP_win_percentage_Results";
        }else if (percentageStatsType.equals("HOP")){
            outputFile = outputFile + "HOP_win_percentage_Results";
        }else{
            System.out.println(percentageStatsType+" is not an statistics option");
            return;
        }


        //Writer for temporary file
        String tempURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\Interval Results\\tempFile.txt";
        FileWriter tempFileWriter;
        BufferedWriter tempBufferedWriter;

        try {
            //FileReader for inputFile
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);


            //Keeping track of current time interval stopping point
            double stoppingPoint;
            if(percentageStatsType.equals("HOP")){
                stoppingPoint = hopsizeDecimalValue;
            }else{
                stoppingPoint = (int)windowSizeDecimalValue;
            }

            double percent;
            if(percentageStatsType.equals("HOP")){
                percent = hopsizeWholeValue;
            }else{
                percent = (int)windowSizeWholeValue;
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

            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
            int endIndex = 0;
            int startIndex = 0;   //Will change from zero ONLY when gathering tumbling window stats

            //For Hopping Window only
            int nextStartIndex=0;

            while (stoppingPoint <= 1.0) {
                for (int i = startIndex; i < lines.size(); i++) {
                    //If point is found, will go on and process up to intervalToBeProcessed
                    if (i == numOfLines-1) {
                        endIndex = i;
                        tempFileWriter = new FileWriter(tempURL);
                        tempBufferedWriter = new BufferedWriter(tempFileWriter);
                        for (int j = startIndex; j <= endIndex; j++) {
                            tempBufferedWriter.write(lines.get(j));
                            tempBufferedWriter.newLine();
                        }
                        tempBufferedWriter.close();
                        gaze.processGaze(tempURL, outputFile + "_" + percent + "%%.csv");
                        if(percentageStatsType.equals("HOP")){
                            if( stoppingPoint < windowSizeDecimalValue){
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
                        //Update the stopping point and interval to be processed
                        if(percentageStatsType.equals("HOP")) {
                            stoppingPoint += hopsizeDecimalValue;
                            percent += hopsizeWholeValue;
                        }else{
                            stoppingPoint += windowSizeDecimalValue;
                            percent+= windowSizeWholeValue;
                        }
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
        if (percentageStatsType.equals("TBM")) {
            outputFile = outputFile + "TBM_win_percentage_Results";
        } else if (percentageStatsType.equals("EXP")) {
            outputFile = outputFile + "EXP_win_percentage_Results";
        }else if (percentageStatsType.equals("HOP")){
            outputFile = outputFile + "HOP_win_percentage_Results";
        }else{
            System.out.println(percentageStatsType+" is not an statistics option");
            return;
        }

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
            double stoppingPoint;
            if(percentageStatsType.equals("HOP")){
                stoppingPoint = hopsizeDecimalValue;
            }else{
                stoppingPoint = (int)windowSizeDecimalValue;
            }

            double percent;
            if(percentageStatsType.equals("HOP")){
                percent = hopsizeWholeValue;
            }else{
                percent = (int)windowSizeWholeValue;
            }

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
            int startIndex = 0;   //Will change from zero ONLY when gathering tumbling window stats

            //For Hopping Window only
            int nextStartIndex=0;

            while (stoppingPoint <= 1.0) {
                for (int i = startIndex; i < lines.size(); i++) {
                    //If point is found, will go on and process up to intervalToBeProcessed
                    if (i == numOfLines-1) {
                        endIndex = i;
                        tempFileWriter = new FileWriter(tempURL);
                        tempBufferedWriter = new BufferedWriter(tempFileWriter);
                        for (int j = startIndex; j <= endIndex; j++) {
                            tempBufferedWriter.write(lines.get(j));
                            tempBufferedWriter.newLine();
                        }
                        tempBufferedWriter.close();
                        event.processEvent(tempURL, outputFile+"_"+percent+"%%.csv");
                        if(percentageStatsType.equals("HOP")){
                            if( stoppingPoint < windowSizeDecimalValue){
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
                        //Update the stopping point and interval to be processed
                        if(percentageStatsType.equals("HOP")) {
                            stoppingPoint += hopsizeDecimalValue;
                            percent += hopsizeWholeValue;
                        }else{
                            stoppingPoint += windowSizeDecimalValue;
                            percent+= windowSizeWholeValue;
                        }
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
