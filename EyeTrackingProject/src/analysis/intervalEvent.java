package analysis;

import java.io.*;
import java.util.ArrayList;

public class intervalEvent extends event{

    public static void processEvent(String inputFile, String outputFile) throws IOException {

        int intervalLengthInMinutes = 2; // <---ENTER THE MINUTES HERE

        int intervalLengthInSeconds = intervalLengthInMinutes * 60;

        String line = null;
        ArrayList<Object> allMouseLeft = new ArrayList<Object>();

        FileWriter fileWriter = new FileWriter(outputFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        bufferedWriter.write("mins   L mouse clicks");
        bufferedWriter.newLine();

        try {
            FileReader fileReader = new FileReader(inputFile);
            int stoppingPoint=intervalLengthInSeconds;
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null && line.isEmpty()==false) {

                String[] lineArray = fixation.lineToArray(line);

                if(lineArray[1].equals("LMouseButton") && lineArray[2].equals("1")){
                    allMouseLeft.add(lineArray);
                    System.out.println(allMouseLeft.size());

                }

                int timestamp=Integer.parseInt(lineArray[0]);
                if (timestamp / 1000 >= stoppingPoint ) {
                    String formatStr="%3d %6d ";
                    String result=String.format(formatStr,
                            stoppingPoint/60,
                            allMouseLeft.size());
                    bufferedWriter.write(result);
                    bufferedWriter.newLine();
                    stoppingPoint+=intervalLengthInSeconds;
                }


            }
            String formatStr="%3d %6d ";
            String result=String.format(formatStr,
                    stoppingPoint/60,
                    allMouseLeft.size());
            bufferedWriter.write(result);
            bufferedWriter.newLine();
            stoppingPoint+=intervalLengthInSeconds;

            bufferedWriter.close();
            bufferedReader.close();

            System.out.println("done writing event data to: " + outputFile);

        }catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + inputFile + "'");
        }catch(IOException ex) {
            System.out.println("Error reading file '" + inputFile + "'");
        }
    }




}
