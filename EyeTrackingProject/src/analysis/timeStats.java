package analysis;
import java.io.*;
import java.util.ArrayList;

public class timeStats {
    private static int windowSizeInMinutes = 2; // <---ENTER THE MINUTES HERE

    private static int windowSizeInMilliseconds = windowSizeInMinutes * 60 * 1000; //Converting minutes to milliseconds

    private static String timeStatsType = "HOP";  //<----- ENTER: "EXP" for Expanding Window Interval Stats
                                                                   // "TBM" for Tumbling Window Interval Stats
                                                                  //  "HOP" for Hopping Window

    // For Hopping Window Option ONLY
    private static int hopsizeInMinutes = 1; // <---ENTER THE MINUTES HERE

    private static int hopsizeInMilliseconds = hopsizeInMinutes* 60 * 1000; //Converting minutes to milliseconds


    //-----------------------------------GENERATING .CSV FILES---------------------------------------------------
    /*Note: Make sure to comment out when using .txt methods
          When doing this, want to make sure to run code from p1 to p36(last participant) to keep the order in .csv files.
          If it comes out incorrect for a participant, have to start all over */

    public static void getFXDStats(String inputFile, String outputFile,String participant, String visualType) throws IOException {
        //Setting i) file name based on window type
        if (timeStatsType.equals("TBM")) {
            outputFile = outputFile + "TBM_win_time_Results";
        } else if (timeStatsType.equals("EXP")) {
            outputFile = outputFile + "EXP_win_time_Results";
        }else if (timeStatsType.equals("HOP")) {
            outputFile = outputFile + "HOP_win_time_Results";
        }else{
            System.out.println(timeStatsType+" is not an statistics option");
            return;
        }

        /*Putting the field names in the output file. In .csv files, we will start out with the fixation
         for participant 1 with the tree visualization, so we will check to see if participant is "p1" and visualType is "tree"
         */

        // Opening writers
        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        int temp_time = hopsizeInMinutes;
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
            while(temp_time<=24) {
                fileWriter = new FileWriter(outputFile + "_" + temp_time + "mins.csv");
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
                temp_time += hopsizeInMinutes;
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
            int stoppingPoint;
            if(timeStatsType.equals("HOP")){
                stoppingPoint = hopsizeInMilliseconds;
            }else{
                stoppingPoint = windowSizeInMilliseconds;
            }


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
            //Holding for the window to be currently processed
            int windowToBeProcessed=stoppingPoint;

            //Looking to find the endpoint for the next interval after the current to be processed
            if(timeStatsType.equals("HOP")){
                stoppingPoint += hopsizeInMilliseconds;
            }else{
                stoppingPoint += windowSizeInMilliseconds;
            }



            int startIndex=0;   //Will change from zero ONLY when gathering tumbling window or hopping window stats

            //For Hopping Window only
            int nextStartIndex=0;


            for(int i=endIndex+1;i<lines.size();i++){
                //Looking for the point where the interval ends
                String[]lineArray=fixation.lineToArray(lines.get(i));
                int timestamp=Integer.parseInt(lineArray[1]);

                //If point is found, will go on and process up to intervalToBeProcessed
                if (timestamp>=stoppingPoint){
                    tempFileWriter = new FileWriter(tempURL);
                    tempBufferedWriter = new BufferedWriter(tempFileWriter);
                    for (int j=startIndex;j<=endIndex;j++) {
                        tempBufferedWriter.write(lines.get(j));
                        //System.out.println(lines.get(j));
                        tempBufferedWriter.newLine();
                    }
                    tempBufferedWriter.close();

                    fixation.processFixation(tempURL, outputFile+"_"+(windowToBeProcessed/1000/60)+"mins.csv",participant);

                    if(timeStatsType.equals("HOP")){
                        if( windowToBeProcessed < windowSizeInMilliseconds){
                            nextStartIndex= endIndex+1;
                    }else {
                            startIndex = nextStartIndex;
                            nextStartIndex = endIndex + 1;
                        }
                    }
                    //if tumbling window, will update the start index to not include previous lines processed
                    else if(timeStatsType.equals("TBM")){
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


            //Process the very last set of lines
            tempFileWriter = new FileWriter(tempURL);
            tempBufferedWriter = new BufferedWriter(tempFileWriter);
            for (int j=startIndex;j<lines.size();j++) {
                tempBufferedWriter.write(lines.get(j));
                tempBufferedWriter.newLine();
            }
            tempBufferedWriter.close();
            fixation.processFixation(tempURL, outputFile+"_"+(windowToBeProcessed/1000/60)+"mins.csv",participant);



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
        if (timeStatsType.equals("TBM")) {
            outputFile = outputFile + "TBM_win_time_Results";
        } else if (timeStatsType.equals("EXP")) {
            outputFile = outputFile + "EXP_win_time_Results";
        } else if (timeStatsType.equals("HOP")) {
            outputFile = outputFile + "HOP_win_time_Results";
        } else {
            System.out.println(timeStatsType + " is not an statistics option");
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
            int stoppingPoint;
            if (timeStatsType.equals("HOP")) {
                stoppingPoint = hopsizeInMilliseconds;
            } else {
                stoppingPoint = windowSizeInMilliseconds;
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

            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
            int endIndex = 0;
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
            //Holding for the interval to be currently processed
            int windowToBeProcessed = stoppingPoint;

            //Looking to find the endpoint for the next interval after the current to be processed
            if (timeStatsType.equals("HOP")) {
                stoppingPoint += hopsizeInMilliseconds;
            } else {
                stoppingPoint += windowSizeInMilliseconds;
            }


            int startIndex = 0;   //Will change from zero ONLY when gathering tumbling window stats

            //For Hopping Window only
            int nextStartIndex = 0;

            for (int i = endIndex + 1; i < lines.size(); i++) {
                //Looking for the point where the interval ends
                String[] lineArray = fixation.lineToArray(lines.get(i));
                int timestamp = Integer.parseInt(lineArray[0]);

                //If point is found, will go on and process up to intervalToBeProcessed
                if (timestamp >= stoppingPoint) {
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

    public static void getEVDStats(String inputFile, String outputFile) throws IOException{

        //Setting i) file name based on stats type and ii) the file extension
        if (timeStatsType.equals("TBM")) {
            outputFile = outputFile + "TBM_win_time_Results";
        } else if (timeStatsType.equals("EXP")) {
            outputFile = outputFile + "EXP_win_time_Results";
        }else if (timeStatsType.equals("HOP")) {
            outputFile = outputFile + "HOP_win_time_Results";
        }else{
            System.out.println(timeStatsType+" is not an statistics option");
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
            int stoppingPoint;
            if(timeStatsType.equals("HOP")){
                stoppingPoint = hopsizeInMilliseconds;
            }else{
                stoppingPoint = windowSizeInMilliseconds;
            }

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


            //Getting endpoint(the point that is greater or equal to the current stopping point) for the first interval
            int endIndex = 0;
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
            int windowToBeProcessed=stoppingPoint;

            //Looking to find the endpoint for the next interval after the current to be processed
            if (timeStatsType.equals("HOP")) {
                stoppingPoint += hopsizeInMilliseconds;
            } else {
                stoppingPoint += windowSizeInMilliseconds;
            }



            int startIndex=0;   //Will change from zero ONLY when gathering tumbling window stats
            //For Hopping Window only
            int nextStartIndex = 0;

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
                    event.processEvent(tempURL, outputFile+"_"+(windowToBeProcessed/1000/60)+"mins.csv");

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
                        endIndex =i-1;
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
            for (int j=startIndex;j<lines.size();j++) {
                tempBufferedWriter.write(lines.get(j));
                tempBufferedWriter.newLine();
            }
            tempBufferedWriter.close();
            event.processEvent(tempURL, outputFile+"_"+(windowToBeProcessed/1000/60)+"mins.csv");

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


