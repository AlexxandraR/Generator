package generator;

import java.util.*;

public class Productions {
    private final String leftSide;
    private final Set<String> rightSide;

    public Productions(String leftSide) {
        this.leftSide = leftSide;
        this. rightSide = new LinkedHashSet<>();
    }

    public String getLeftSide() {
        return leftSide;
    }

    public Set<String> getRightSide() {
        return rightSide;
    }

    /** finds the index of a particular production in the set of right sides of rules **/
    public int findIndex(String string){
        int i = 0;
        for( String s : this.rightSide){
            if(Objects.equals(string, s)){
                return i;
            }
            i++;
        }
        return -1;
    }

    /** finds the production in the set of right sides by index **/
    public String findRightSideString(int index){
        int i = 0;
        for(String s : this.rightSide){
            if(i == index){
                return s;
            }
            i++;
        }
        return "";
    }

    public String findSymbolInProduction(int j, int k){
        String word = this.findRightSideString(j);
        int i = 1;
        int start = -1;
        int end = word.indexOf(" ");
        String nonterminal;
        if(i == k && end != -1){
            nonterminal = word.substring(0, end);
            return nonterminal;
        }
        do{
            start = word.indexOf(" ", start+1);
            i++;
        }while(i < k);
        if(start != -1){
            end = word.indexOf(" ", start+1);
            if(end != -1){
                nonterminal = word.substring(start + 1, end);
            }
            else{
                nonterminal = word.substring(start + 1);
            }
        }
        else{
            nonterminal = word;
        }

        return nonterminal;
    }

    public int lengthOfProduction(int j){
        String word = this.findRightSideString(j);
        int count = 1;
        int end = word.indexOf(" ");
        int start;
        if(end != -1){
            do{
                count++;
                start = end;
                end = word.indexOf(" ", start+1);
            }while(end != -1);
        }
        return count;
    }

}
