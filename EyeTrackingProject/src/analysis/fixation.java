package analysis;
/*
 * Copyright (c) 2013, Bo Fu 
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class fixation {
     /*
        Overloaded method for gathering interval statistics or percentage statistics
      */
    public static void processFixation(String inputFile, String outputFile, double interval) throws IOException{
        String line = null;
        ArrayList<Integer> allFixationDurations = new ArrayList<Integer>();
        ArrayList<Object> allCoordinates = new ArrayList<Object>();
        List<Point> allPoints = new ArrayList<Point>();
        ArrayList<Object> saccadeDetails = new ArrayList<Object>();

        FileWriter fileWriter = new FileWriter(outputFile,true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        try {
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
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

            }

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


            //Writing statistical results to the output file
            String formatStr="%6.1f %16d %30f %20f %25f %18f %20f %20f  " +
                    "%20d %20f %13f %20f %20f %20f %20f"+
                    "%25f %14f %20f %25f %25f %20f  %17f %17f"+
                    "%20f %14f %14f %14f %16f %16f"+
                    "%17f %20f %17f %13f %11f %17f %18f";
            String result=String.format(formatStr,
                    interval,
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

            bufferedWriter.close();
            bufferedReader.close();
            System.out.println("done writing fixation data to: " + outputFile);

        }catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + inputFile + "'");
        }catch(IOException ex) {
            System.out.println("Error reading file '" + inputFile + "'");
        }
    }

	
	public static void processFixation(String inputFile, String outputFile) throws IOException{

	         String line = null;
             ArrayList<Integer> allFixationDurations = new ArrayList<Integer>();
             ArrayList<Object> allCoordinates = new ArrayList<Object>();
             List<Point> allPoints = new ArrayList<Point>();
             ArrayList<Object> saccadeDetails = new ArrayList<Object>();

             FileWriter fileWriter = new FileWriter(outputFile,true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            try {
                FileReader fileReader = new FileReader(inputFile);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
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

                }

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

                //Gettting current timestamp
                int saccadeDetailsSize=saccadeDetails.size()-1;
                Integer [] saccadeDetail= (Integer [])saccadeDetails.get(saccadeDetailsSize);
                int interval=saccadeDetail[0];

                //Writing statistical results to the output file
                String formatStr="%3s %16d %30f %20f %25f %18f %20f %20f  " +
                        "%20d %20f %13f %20f %20f %20f %20f"+
                        "%25f %14f %20f %25f %25f %20f  %17f %17f"+
                        "%20f %14f %14f %14f %16f %16f"+
                        "%17f %20f %17f %13f %11f %17f %18f";
                String result=String.format(formatStr,
                        (interval/1000)/60,
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

            bufferedWriter.close();
            bufferedReader.close();
            System.out.println("done writing fixation data to: " + outputFile);

}catch(FileNotFoundException ex) {
        System.out.println("Unable to open file '" + inputFile + "'");
        }catch(IOException ex) {
        System.out.println("Error reading file '" + inputFile + "'");
        }
        }

public static String[] lineToArray(String lineOfData){
		
		StringTokenizer str = new StringTokenizer(lineOfData);
		String[] values = new String[str.countTokens()];

		while(str.hasMoreElements()) {
			for(int i=0; i<values.length; i++){
				values[i] = (String) str.nextElement();
			}
		}
		
		return values;
	}
	
	public static Point2D[] listToArray(List<Point> allPoints){
		Point2D[] points = new Point2D[allPoints.size()];
        for(int i=0; i<points.length; i++){
        	points[i] = allPoints.get(i);
        }
        return points;
	}
	
	public static String getFixationCount(String inputFile) throws IOException {
		File file = new File(inputFile); 
	  
	        RandomAccessFile fileHandler = new RandomAccessFile( file, "r" );
	        long fileLength = file.length() - 1;
	        StringBuilder sb = new StringBuilder();

	        for(long filePointer = fileLength; filePointer != -1; filePointer--){
	            fileHandler.seek( filePointer );
	            int readByte = fileHandler.readByte();

	            if( readByte == 0xA ) {
	                if( filePointer == fileLength ) {
	                    continue;
	                } else {
	                    break;
	                }
	            } else if( readByte == 0xD ) {
	                if( filePointer == fileLength - 1 ) {
	                    continue;
	                } else {
	                    break;
	                }
	            }

	            sb.append( ( char ) readByte );
	        }

	        String lastLine = sb.reverse().toString();
	        
	        //get the first value in the last line, which is the total number of fixations
	        String[] lineArray = lineToArray(lastLine);
            String totalFixations = lineArray[0];
	        
	        return totalFixations;
	 
	}
	

	public static double getScanpathDuration(ArrayList<Integer> allFixationDurations, ArrayList<Integer> allSaccadeDurations){
		double fixationDuration = descriptiveStats.getSumOfIntegers(allFixationDurations);
		double saccadeDuration = descriptiveStats.getSumOfIntegers(allSaccadeDurations);
		return fixationDuration + saccadeDuration;
	}
	
	public static double getFixationToSaccadeRatio(ArrayList<Integer> allFixationDurations, ArrayList<Integer> allSaccadeDurations){
		double fixationDuration = descriptiveStats.getSumOfIntegers(allFixationDurations);
		double saccadeDuration = descriptiveStats.getSumOfIntegers(allSaccadeDurations);
		return fixationDuration/saccadeDuration;
	}
	
}
