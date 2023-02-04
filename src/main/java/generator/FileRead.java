package generator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileRead {

    /** parses the file into a grammar **/
    public void readText(String fileName, Grammar grammar) throws IOException {
        FileReader fReader = new FileReader(fileName);
        BufferedReader buffReader = new BufferedReader(fReader);

        int counter = 0;
        String line;
        String word = "";

        while (buffReader.ready()) {
            line = buffReader.readLine();

            /* entry in the alphabet of terminal symbols */
            if(line.contains("%token")){
                word = (String) line.subSequence(7, line.length());
                grammar.getT().getAlphabet().add(word);
            }

            /* entry of the initial symbol also adding in non-terminal symbols **/
            else if(line.contains("%start")){
                word = (String) line.subSequence(7, line.length());
                grammar.setS(word);
                grammar.getN().getAlphabet().add(word);
            }

            /* rule area label */
            else if(line.equals("%%")){
                counter++;
            }

            /* parsing rule area */
            else if(counter == 1){
                String value;
                int position;

                /* the line does not contain an OR rule **/
                if(line.charAt(0) != ' '){
                    position = line.indexOf(":");
                    word = (String) line.subSequence(0, position);
                    if(grammar.alphabetCheck(word)){
                        System.out.println("The grammar is not correct: The terminal symbol cannot be on the left side of the production.");
                        break;
                    }
                    else if(!grammar.getN().getAlphabet().contains(word)) {
                        grammar.getN().getAlphabet().add(word);
                    }

                }

                /* the line contains an OR rule **/
                else{
                    position = line.indexOf("|");

                }
                value = (String) line.subSequence(position+2, line.length());
                grammar.addToProductions(word, value);
            }
            //System.out.println(line);
        }


        /*for (String i : grammar.getT().getAlphabet()){
            System.out.println(i);
        }
        for (String i : grammar.getN().getAlphabet()){
            System.out.println(i);
        }

        System.out.println(grammar.getS());

        for(Productions p : grammar.getProductions()){
            System.out.println(p.getLeftSide() + p.getRightSide());
        }*/

    }

}
