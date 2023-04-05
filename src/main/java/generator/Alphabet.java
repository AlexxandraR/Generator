package generator;

import java.util.*;

public class Alphabet {
    private final Set<String> alphabet;

    /**
     * constructor for the alphabet class
     */
    public Alphabet() {
        this.alphabet = new LinkedHashSet<>();
    }

    /**
     * getter for alphabet
     * @return alphabet
     */
    public Set<String> getAlphabet() {
        return alphabet;
    }

    /**
     * returns the index of the non-terminal in the alphabet
     * @param word the non-terminal whose index we are looking for
     * @return index in the alphabet
     */
    public int getIndex(String word){
        int i = 1;
        for(String s : this.alphabet){
            if(Objects.equals(s, word)){
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * returns the non-terminal at the index in alphabet
     * @param index the index of the non-terminal we are looking for
     * @return wanted non-terminal
     */
    public String getTerminal(int index) {
        int i = 0;
        for(String word : this.alphabet){
            if(i == index){
                return word;
            }
            i++;
        }
        return "";
    }
}
