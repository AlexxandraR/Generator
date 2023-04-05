package generator;

import java.util.*;

public class Productions {
    private final String leftSide;
    private final Set<String> rightSide;

    /**
     * constructor for productions
     * @param leftSide string representing the left side of the rule
     */
    public Productions(String leftSide) {
        this.leftSide = leftSide;
        this. rightSide = new LinkedHashSet<>();
    }

    /**
     * getter for left side of production
     * @return left side string
     */
    public String getLeftSide() {
        return leftSide;
    }

    /**
     * getter for right side of production
     * @return right side string
     */
    public Set<String> getRightSide() {
        return rightSide;
    }

    /**
     * finds the index of a particular production in the set of right sides of rules
     * @param string a particular production
     * @return the index of a particular production
     */
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

    /**
     * finds the production in the set of right sides by index
     * @param index index of particular production
     * @return a particular production
     */
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

    /**
     * finds the string in production j at position k
     * @param j index of production
     * @param k index of symbol in production
     * @return the string in production j at position k
     */
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

    /**
     * finds the rule at index j and returns its length
     * @param j index of production
     * @return length of production
     */
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
