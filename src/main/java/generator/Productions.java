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

}
