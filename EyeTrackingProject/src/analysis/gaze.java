package analysis;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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

public class gaze {
	/*
       Overloaded method for gathering interval statistics or percentage statistics
        interval - double for .txt file     String for .csv file (will pass in participant name as interval)
     */
	public static void processGaze(String inputFile, String outputFile) throws IOException{
		String line = null;
		ArrayList<Object> allValidData = new ArrayList<Object>();

		FileWriter fileWriter = new FileWriter(outputFile,true);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		ArrayList<Integer> timestamps= new ArrayList<Integer>();

		try {
			FileReader fileReader = new FileReader(inputFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while((line = bufferedReader.readLine()) != null) {
				String[] lineArray = fixation.lineToArray(line);
//				if(Integer.parseInt(lineArray[0]) < 60000)
//				   System.out.println(lineArray[0]);
				timestamps.add(Integer.parseInt(lineArray[0]));
				//checking the validity of the recording
				//a code with 0 indicates the eye tracker was confident with this data
				//note that only instances where BOTH pupil sizes are valid will be used in the analysis
				if(lineArray[8].equals("0") && lineArray[15].equals("0")){
					double pupilLeft = Double.parseDouble(lineArray[7]);
					double pupilRight = Double.parseDouble(lineArray[14]);
					double[] pupilSizes = new double[2];
					pupilSizes[0] = pupilLeft;
					pupilSizes[1] = pupilRight;
					allValidData.add(pupilSizes);
				  }

			}

             //Writing statistical results to the output file
			//REMOVE "interval" for .csv files
			String formatStr=" %25f, %20f,%20f,";
			String result=String.format(formatStr,
					getAverageOfLeft(allValidData),
					getAverageOfRight(allValidData),
					getAverageOfBoth(allValidData));
			bufferedWriter.write(result);

			/* Missing stats
			    	allValidData.size(),
			 */

			//Always keep
			bufferedWriter.newLine();

			bufferedWriter.close();
			bufferedReader.close();
			System.out.println("done writing gaze data to: " + outputFile);

		}catch(FileNotFoundException ex) {
			System.out.println("Unable to open file '" + inputFile + "'");
		}catch(IOException ex) {
			System.out.println("Error reading file '" + inputFile + "'");
		}
	}

	
	//calculate the average pupil size of the left eye
	public static double getAverageOfLeft(ArrayList<Object> allValidData){
		ArrayList<Double> allLeftSizes = new ArrayList<Double>();
		for (int i=0; i<allValidData.size(); i++){
			double[] eachPair = (double[]) allValidData.get(i);
			double pupilSizeLeft = eachPair[0];
			allLeftSizes.add(pupilSizeLeft);
			
		}
		return descriptiveStats.getMeanOfDoubles(allLeftSizes);
	}

	//calculate the average pupil size of the right eye
	public static double getAverageOfRight(ArrayList<Object> allValidData){
		ArrayList<Double> allRightSizes = new ArrayList<Double>();
		for (int i=0; i<allValidData.size(); i++){
			double[] eachPair = (double[]) allValidData.get(i);
			double pupilSizeRight = eachPair[1];
			allRightSizes.add(pupilSizeRight);
			
		}
		return descriptiveStats.getMeanOfDoubles(allRightSizes);
	}
	
	//computes the average pupil size of both eyes
	public static double getAverageOfBoth(ArrayList<Object> allValidData){
		double averageOfLeft = getAverageOfLeft(allValidData);
		double averageOfRight = getAverageOfRight(allValidData);
		return (averageOfLeft + averageOfRight)/2.0;
	}
}
