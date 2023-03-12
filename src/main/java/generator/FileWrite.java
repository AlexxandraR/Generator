package generator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class FileWrite {
    public void writeText(Generator g, int beginLength, int endLength, int numberOfRepetitions) throws IOException {
        FileWriter output = new FileWriter("output.txt");

        output.write("Grammar:");
        /*output.write("\nN: ");
        for(String s : g.getGrammar().getN().getAlphabet()){
            output.write(s + ", ");
        }*/


        for(String s : g.getGrammar().getT().getAlphabet()){
            output.write("\n%token ");
            output.write(s);
        }
        output.write("\n\n%start " + g.getGrammar().getS());
        output.write("\n%%");

        for(Productions p : g.getGrammar().getProductions()){
            output.write("\n" + p.getLeftSide() + " : ");
            int numberOfRightStrings = p.getRightSide().size();
            for(int j = 0; j < numberOfRightStrings; j++) {
                String s = p.findRightSideString(j);
                if(j == 0){
                    output.write(s);
                }
                else{
                    output.write("\n | " + s);
                }
            }
        }
        output.write("\n%%");

        output.write("\n\nNumber of generated words: ");
        output.write(((Integer)numberOfRepetitions).toString());

        output.write("\nWord lengths are generated from an interval [");
        output.write(((Integer)beginLength).toString());
        output.write(", ");
        output.write(((Integer)endLength).toString());
        output.write("]");

        output.write("\n\nGenerated words:");

        output.close();
    }

    public void writeException(String message) throws IOException {
        FileWriter output = new FileWriter("output.txt");
        output.write(message);
        output.close();
    }

    public void appendText(String message) throws IOException {
        FileWriter output = new FileWriter("output.txt", true);
        output.write("\n" + message);
        output.close();
    }

    public void appendStatistics(HashMap<String, Integer> pair) throws IOException {
        FileWriter output = new FileWriter("output.txt", true);

        output.write("\n\nStatistics:" );
        for(String word : pair.keySet()){
            output.write("\n" + word + ": " + pair.get(word).toString());
        }

        output.close();
    }

}
