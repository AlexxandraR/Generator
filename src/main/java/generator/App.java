package generator;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        //for(int i = 0; i < 1; i++){
            //long startTime = System.nanoTime();
            int beginLength =  0;
            int endLength = 0;
            int numberOfRepetitions = 0;
            String file = "";
            try{
                file = args[0];
                beginLength =  Integer.parseInt(args[1]);
                endLength = Integer.parseInt(args[2]);
                numberOfRepetitions = Integer.parseInt(args[3]);
            }catch(NumberFormatException e){
                System.out.println("Illegal argument.");
                return;
            }

            Generator g = new Generator(file, beginLength, endLength, numberOfRepetitions);
            //long elapsedTime = System.nanoTime() - startTime;

            //System.out.println("Time in seconds: " + (double)elapsedTime/1000000000.0);
            //System.out.println((double)elapsedTime/1000000000.0);

        //}
    }
}
