package generator;

import java.util.ArrayList;
import java.util.List;

public class Alphabet {
    private LettersType type;
    private List<String> alphabet;

    public Alphabet(LettersType type) {
        this.type = type;
        this.alphabet = new ArrayList<>();
    }

    public List<String> getAlphabet() {
        return alphabet;
    }
}
