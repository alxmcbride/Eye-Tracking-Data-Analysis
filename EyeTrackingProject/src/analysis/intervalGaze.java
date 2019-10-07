package analysis;

import java.io.*;
import java.util.ArrayList;

public class intervalGaze extends gaze{

    public static void processGaze(String inputFile, String outputFile) throws IOException {
        int intervalLengthInMinutes = 2; // <---ENTER THE MINUTES HERE

        int intervalLengthInSeconds = intervalLengthInMinutes * 60;

        String line = null;
        ArrayList<Object> allValidData = new ArrayList<Object>();

        FileWriter fileWriter = new FileWriter(outputFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        String formatFieldNames="%-9s %-9s %-15s %-15s %-15s";
        bufferedWriter.write(String.format(formatFieldNames,"mins","# vr","aps le", "aps re","aps both"));
        bufferedWriter.newLine();

        try {
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int stoppingPoint=intervalLengthInSeconds;
            while ((line = bufferedReader.readLine()) != null) {

                String[] lineArray = fixation.lineToArray(line);

                //checking the validity of the recording
                //a code with 0 indicates the eye tracker was confident with this data
                //note that only instances where BOTH pupil sizes are valid will be used in the analysis
                if (lineArray[8].equals("0") && lineArray[15].equals("0")) {
                    double pupilLeft = Double.parseDouble(lineArray[7]);
                    double pupilRight = Double.parseDouble(lineArray[14]);
                    double[] pupilSizes = new double[2];
                    pupilSizes[0] = pupilLeft;
                    pupilSizes[1] = pupilRight;
                    allValidData.add(pupilSizes);
                }
                int timestamp = Integer.parseInt(lineArray[0]);
                if (timestamp / 1000 >= stoppingPoint || lineArray[0].equals(fixation.getFixationCount(inputFile))) {
                    String formatStr="%3d %12d %14f %14f %16f";
                    String result=String.format(formatStr,
                            stoppingPoint/60,
                            allValidData.size(),
                            getAverageOfLeft(allValidData),
                            getAverageOfRight(allValidData),
                            getAverageOfBoth(allValidData));
                    bufferedWriter.write(result);
                    bufferedWriter.newLine();
                    System.out.println(stoppingPoint);
                    stoppingPoint+=intervalLengthInSeconds;

                }
            }

              bufferedWriter.close();
              bufferedReader.close();
            } catch(FileNotFoundException ex){
                System.out.println("Unable to open file '" + inputFile + "'");
            } catch(IOException ex){
                System.out.println("Error reading file '" + inputFile + "'");
            }



        System.out.println("done writing gaze data to: " + outputFile);
        }



}
