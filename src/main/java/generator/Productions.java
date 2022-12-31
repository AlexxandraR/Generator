package generator;

import java.util.HashSet;
import java.util.Set;

public class Productions {
    private final String leftSide;
    private final Set<String> rightSide;

    public Productions(String leftSide) {
        this.leftSide = leftSide;
        this. rightSide = new HashSet<>();
    }

    public String getLeftSide() {
        return leftSide;
    }

    public Set<String> getRightSide() {
        return rightSide;
    }
}
