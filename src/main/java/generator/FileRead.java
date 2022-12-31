package generator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileRead {

    public void readText(String fileName, Grammar grammar) throws IOException {
        FileReader fReader = new FileReader(fileName);
        BufferedReader buffReader = new BufferedReader(fReader);

        int counter = 0;
        String line;
        String word = "";

        while (buffReader.ready()) {
            line = buffReader.readLine();
            if(line.contains("%token")){
                word = (String) line.subSequence(7, line.length());
                grammar.getT().getAlphabet().add(word);
            }
            else if(line.contains("%start")){
                word = (String) line.subSequence(7, line.length());
                grammar.setS(word);
                grammar.getN().getAlphabet().add(word);
            }
            else if(line.equals("%%")){
                counter++;
            }
            else if(counter == 1){
                String value;
                int position;
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
                else{
                    position = line.indexOf("|");

                }
                value = (String) line.subSequence(position+2, line.length());
                grammar.setProductions(word, value);
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
