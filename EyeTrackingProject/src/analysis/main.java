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
     * This method is used to get the input file to read from and the
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
		String outputLocation = outputURL + participant + "\\";
		
		//FXD data
		//testing cases where X axis values are the same
		//String fixationData = "fxdSameXValues.txt";
		String treeFixation = participant + ".treeFXD.txt";
		String treeFixationResults = "treeFXDResults.txt";
		String treeFixationInput = inputLocation + treeFixation;
        String treeFixationOutput = outputLocation + treeFixationResults; 
        
        String graphFixation = participant + ".graphFXD.txt";
        String graphFixationResults = "graphFXDResults.txt";
        String graphFixationInput = inputLocation + graphFixation;
        String graphFixationOutput = outputLocation + graphFixationResults;
		
		//EVD data
		String treeEvent = participant + ".treeEVD.txt";
		String treeEventResults = "treeEVDResults.txt";
		String treeEventInput = inputLocation + treeEvent;
        String treeEventOutput = outputLocation + treeEventResults;
        
        String graphEvent = participant + ".graphEVD.txt";
        String graphEventResults = "graphEVDResults.txt";
        String graphEventInput = inputLocation + graphEvent;
        String graphEventOutput = outputLocation + graphEventResults;
;
        
        //GZD data
        String gazeBaseline = participant + "GZD.txt";
        String baselineResults = "baselineResults.txt";
        String baselineInput = inputLocation + gazeBaseline;
        String baselineOutput = outputLocation + baselineResults;
        
        String treeGaze = participant + ".treeGZD.txt";
        String treeGazeResults = "treeGZDResults.txt";
        String treeGazeInput = inputLocation + treeGaze;
        String treeGazeOutput = outputLocation + treeGazeResults;
        
        String graphGaze = participant + ".graphGZD.txt";
        String graphGazeResults = "graphGZDResults.txt";
        String graphGazeInput = inputLocation + graphGaze;
        String graphGazeOutput = outputLocation + graphGazeResults;


         Scanner in = new Scanner(System.in);

          //analyze gaze baseline
//        intervalGaze.processGaze(baselineInput, baselineOutput);
//
//        //analyze tree related data
 //          intervalStats.getFXDStats(treeFixationInput,treeFixationOutput);
 //          intervalStats.getEVDStats(treeEventInput, treeEventOutput);
 //         intervalStats.getGZDStats(treeGazeInput, treeGazeOutput);
////
////      //analyze graph related data
//        intervalFixation.processFixation(graphFixationInput, graphFixationOutput);
 //         intervalStats.getEVDStats(graphEventInput, graphEventOutput);
        intervalGaze.processGaze(graphGazeInput, graphGazeOutput);
	}

}
