package analysis;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class intervalFixation extends fixation {

    public static void processFixation(String inputFile, String outputFile) throws IOException {

        int intervalLengthInMinutes=2; // <---ENTER THE MINUTES HERE

        int intervalLengthInSeconds=intervalLengthInMinutes*60;

        String line = null;
        ArrayList<Integer> allFixationDurations = new ArrayList<Integer>();
        ArrayList<Object> allCoordinates = new ArrayList<Object>();
        List<Point> allPoints = new ArrayList<Point>();
        ArrayList<Object> saccadeDetails = new ArrayList<Object>();

        FileWriter fileWriter = new FileWriter(outputFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);


        //printing field names
       String formatFieldNames="%-9s %-9s %-20s %-15s %-15s %-15s %-12s %-12s  %-6s %-14s %-12s %-12s %-12s %-9s %-21s  %-16s %-16s %-11s"
                 +" %-20s %-16s %-12s   %-16s  %-9s   %-15s %-15s %-15s %-15s %-15s %-11s   %-16s %-13s %-12s %-12s %-12s %-11s   %-9s";
             bufferedWriter.write(String.format(formatFieldNames,"minutes","ttl fxd", "sum fxd","mean xd","med fxd","SD fxd","min fxd","max fxd"
                     ,"scd", "sum scdL","mean scdL","med scdL","SD scdL","min scdL","max scdL"
                     ,"sum scdr" ,"mean scdr", "med scdr","SD scdr", "min scdr", "max scdr"
                     ,"spth","fxd2scd" ,"sum ad"," mean ad"," med ad","SD ad","min ad","max ad"
                     ,"sum rd"," mean rd"," med rd","SD rd","min rd","max rd","cvx hull"));
             bufferedWriter.newLine();
             try {
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int stoppingPoint=intervalLengthInSeconds;
            while((line = bufferedReader.readLine()) != null) {

                String[] lineArray = lineToArray(line);

                //get each fixation's duration
                String eachFixationDuration = lineArray[2];
                int eachDuration = Integer.parseInt(eachFixationDuration);

                //get each fixation's (x,y) coordinates
                String eachFixationX = lineArray[3];
                String eachFixationY = lineArray[4];
                int x = Integer.parseInt(eachFixationX);
                int y = Integer.parseInt(eachFixationY);

                Point eachPoint = new Point(x,y);

                Integer[] eachCoordinate = new Integer[2];
                eachCoordinate[0] = x;
                eachCoordinate[1] = y;

                //get timestamp of each fixation
                int timestamp = Integer.parseInt(lineArray[1]);
                Integer[] eachSaccadeDetail = new Integer[2];
                eachSaccadeDetail[0] = timestamp;
                eachSaccadeDetail[1] = eachDuration;


                allFixationDurations.add(eachDuration);

                allCoordinates.add(eachCoordinate);

                allPoints.add(eachPoint);

                saccadeDetails.add(eachSaccadeDetail);
                if(timestamp/1000 >= stoppingPoint || lineArray[0].equals(getFixationCount(inputFile))){


                    //getting saccade lengths
                    Double[] allSaccadeLengths = saccade.getAllSaccadeLength(allCoordinates);

                    //getting saccade durations
                    ArrayList<Integer> allSaccadeDurations = saccade.getAllSaccadeDurations(saccadeDetails);

                    //getting absolute degrees
                    ArrayList<Double> allAbsoluteDegrees = angle.getAllAbsoluteAngles(allCoordinates);

                    //getting relative degrees
                    ArrayList<Double> allRelativeDegrees = angle.getAllRelativeAngles(allCoordinates);


                    //getting the convex hull using Graham Scan
                    //i.e. Choose point p with smallest y-coordinate.
                    //Sort points by polar angle with p to get simple polygon.
                    //Consider points in order, and discard those that would create a clockwise turn.
                    List<Point> boundingPoints = convexHull.getConvexHull(allPoints);
                    Point2D[] points = listToArray(boundingPoints);

                     String formatStr="%3d %12d %16f %18f %15f %15f %15f %13f  " +
                                      "%4d %14f %13f %12f %12f %10f %13f"+
                                      "%25f %14f %14f %14f %16f %18f  %16f %12f"+
                                      "%15f %14f %14f %14f %16f %16f"+
                                      "%17f %14f %11f %11f %11f %14f %18f";
                     String result=String.format(formatStr,
                                     stoppingPoint/60,
                                            allFixationDurations.size(),
                                            descriptiveStats.getSumOfIntegers(allFixationDurations),
                                            descriptiveStats.getMeanOfIntegers(allFixationDurations),
                                            descriptiveStats.getMedianOfIntegers(allFixationDurations),
                                            descriptiveStats.getStDevOfIntegers(allFixationDurations),
                                            descriptiveStats.getMinOfIntegers(allFixationDurations),
                                            descriptiveStats.getMaxOfIntegers(allFixationDurations),

                                            allSaccadeLengths.length,
                                            descriptiveStats.getSum(allSaccadeLengths),
                                            descriptiveStats.getMean(allSaccadeLengths),
                                            descriptiveStats.getMedian(allSaccadeLengths),
                                            descriptiveStats.getStDev(allSaccadeLengths),
                                            descriptiveStats.getMin(allSaccadeLengths),
                                            descriptiveStats.getMax(allSaccadeLengths),

                                            descriptiveStats.getSumOfIntegers(allSaccadeDurations),
                                            descriptiveStats.getMeanOfIntegers(allSaccadeDurations),
                                            descriptiveStats.getMedianOfIntegers(allSaccadeDurations),
                                            descriptiveStats.getStDevOfIntegers(allSaccadeDurations),
                                            descriptiveStats.getMinOfIntegers(allSaccadeDurations),
                                            descriptiveStats.getMaxOfIntegers(allSaccadeDurations),

                                           getScanpathDuration(allFixationDurations, allSaccadeDurations),
                                           getFixationToSaccadeRatio(allFixationDurations, allSaccadeDurations),

                                           descriptiveStats.getSumOfDoubles(allAbsoluteDegrees),
                                           descriptiveStats.getMeanOfDoubles(allAbsoluteDegrees),
                                           descriptiveStats.getMedianOfDoubles(allAbsoluteDegrees),
                                           descriptiveStats.getStDevOfDoubles(allAbsoluteDegrees),
                                           descriptiveStats.getMinOfDoubles(allAbsoluteDegrees),
                                           descriptiveStats.getMaxOfDoubles(allAbsoluteDegrees),

                                           descriptiveStats.getSumOfDoubles(allRelativeDegrees),
                                           descriptiveStats.getMeanOfDoubles(allRelativeDegrees),
                                           descriptiveStats.getMedianOfDoubles(allRelativeDegrees),
                                           descriptiveStats.getStDevOfDoubles(allRelativeDegrees),
                                           descriptiveStats.getMinOfDoubles(allRelativeDegrees),
                                           descriptiveStats.getMaxOfDoubles(allRelativeDegrees),

                                           convexHull.getPolygonArea(points));
                     bufferedWriter.write(result);
                    bufferedWriter.newLine();
                    System.out.println(stoppingPoint);
                    stoppingPoint+=intervalLengthInSeconds;
                }

            }
          //  bufferedWriter.write("total number of fixations: " + allFixationDurations.size());
          //  bufferedWriter.newLine();




            bufferedWriter.close();
            bufferedReader.close();
            System.out.println("done writing fixation data to: " + outputFile);

        }catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + inputFile + "'");
        }catch(IOException ex) {
            System.out.println("Error reading file '" + inputFile + "'");
        }
    }

}

