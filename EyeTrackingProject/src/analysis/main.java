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

import java.io.IOException;
import java.util.Scanner;


public class main {
    /**
     * This method is used to get the input file to /read from and the
     * output files to write to;
     * @param args
     * @throws IOException
     */
	
	public static void main(String args[]) throws IOException{
		
		//specify the location of the raw data files
		String inputURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Participant Data\\";
		//specify the location of the analyzed results 
		String outputURL = "C:\\Users\\alexm\\OneDrive\\Documents\\ComputerScience\\EyeTrackingExp (CECS 497)\\Correct Results\\Interval Results\\";
		//specify the subject, e.g. p1, as analysis is generated per-participant
		String participant = "p1";
		
		String inputLocation = inputURL + participant + "\\";
		String outputLocation = outputURL + participant+"\\";
		
		//FXD data
		//testing cases where X axis values are the same
		//String fixationData = "fxdSameXValues.txt";
		String treeFixation = participant + ".treeFXD.txt";
		String treeFixationInput=inputLocation + treeFixation;

        
        String graphFixation = participant + ".graphFXD.txt";
        String graphFixationInput = inputLocation + graphFixation;
		
		//EVD data
		String treeEvent = participant + ".treeEVD.txt";
		String treeEventInput = inputLocation + treeEvent;
        
        String graphEvent = participant + ".graphEVD.txt";
        String graphEventInput = inputLocation + graphEvent;


        
        //GZD data
        String gazeBaseline = participant + "GZD.txt";
        String baselineInput = inputLocation + gazeBaseline;
        
        String treeGaze = participant + ".treeGZD.txt";
        String treeGazeInput = inputLocation + treeGaze;
        
        String graphGaze = participant + ".graphGZD.txt";
        String graphGazeInput = inputLocation + graphGaze;

        //Baseline Output files
        //For .txt output files - outputLocation + participant + "_filetype_"
        //For .csv output files - outputLocation + "filetype_"

        String baselineOutput=outputLocation + "baseline_";

        //Tree output files
        String treeOutput=outputLocation +"tree_";

        //Graph output files
        String graphOutput=outputLocation +"graph_";


         Scanner in = new Scanner(System.in);

         //i) Percentage Results - use percentageStats.methodName
        //ii) Time Results - use intervalStats.methodName
        //iii) For .csv, add third parameter participant to each call

         //analyze gaze baseline
//        intervalStats.getGZDStats(baselineInput,baselineOutput, participant);
//
//       //analyze tree related data
        intervalStats.getFXDStats(treeFixationInput, treeOutput, participant);
        intervalStats.getEVDStats(treeEventInput, treeOutput, participant);
        intervalStats.getGZDStats(treeGazeInput, treeOutput, participant);
//
//        //analyze graph related data
        intervalStats.getFXDStats(graphFixationInput, graphOutput, participant);
        intervalStats.getEVDStats(graphEventInput, graphOutput, participant);
        intervalStats.getGZDStats(graphGazeInput, graphOutput, participant);
	}

}
