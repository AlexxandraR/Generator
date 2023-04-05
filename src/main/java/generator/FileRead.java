package generator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileRead {

    /**
     * parses the file into a grammar
     * @param fileName the file from which the grammar is loaded
     * @param grammar variable into which the grammar from the file is saved
     * @return null if everything was loaded correctly and an error message if the loading was not successful
     * @throws IOException if something failed while reading the file
     */
    public String readText(String fileName, Grammar grammar) throws IOException {
        FileReader fReader = null;
        try {
            fReader = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            return "Failed to load file.";
        }
        BufferedReader buffReader = new BufferedReader(fReader);

        int counter = 0;
        String line;
        String word = "";

        int productionCount = 0;

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
                if(!word.contains(" ")){
                    grammar.setS(word);
                    grammar.getN().getAlphabet().add(word);
                }
                else{
                    //System.out.println("A non-terminal symbol cannot contain a space.");
                    return "A non-terminal symbol cannot contain a space.";
                }

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
                        //System.out.println("The grammar is not correct: The terminal symbol cannot be on the left side of the production.");
                        return "The grammar is not correct: The terminal symbol cannot be on the left side of the production.";
                    }
                    if(!word.contains(" ")){
                        grammar.getN().getAlphabet().add(word);
                    }
                    else{
                        //System.out.println("A non-terminal symbol cannot contain a space.");
                        return "A non-terminal symbol cannot contain a space.";
                    }
                }

                /* the line contains an OR rule **/
                else{
                    position = line.indexOf("|");

                }
                if(line.length() > position+2){
                    value = (String) line.subSequence(position+2, line.length());
                    grammar.addToProductions(word, value);
                }
                else{
                    grammar.addToProductions(word, "");
                }
                productionCount++;
            }
        }
        grammar.removeTerminals();
        //System.out.println(productionCount);
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
        return null;

    }

}
