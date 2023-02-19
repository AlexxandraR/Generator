package generator;

import java.util.*;

public class Alphabet {
    private LettersType type;
    private Set<String> alphabet;

    public Alphabet(LettersType type) {
        this.type = type;
        this.alphabet = new LinkedHashSet<>();
    }

    public Set<String> getAlphabet() {
        return alphabet;
    }

    public String getSymbol(int index){
        int i = 1;
        for(String s : this.alphabet){
            if(i == index){
                return s;
            }
            i++;
        }
        return null;
    }

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
