package generator;

import java.util.*;

public class Grammar {
    private final Alphabet n;
    private final Alphabet t;
    private String s;
    private final Set<Productions> productions;

    public Grammar() {
        this.n = new Alphabet(LettersType.NONTERMINALS);
        this.t = new Alphabet(LettersType.TERMINALS);
        this.productions = new HashSet<>();
    }

    public Alphabet getN() {
        return this.n;
    }

    public Alphabet getT() {
        return t;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getS() {
        return s;
    }

    public void setProductions(String key, String value) {
        Productions p = findProduction(key);
        if(p != null){
            p.getRightSide().add(value);
        }
    }

    public Set<Productions> getProductions() {
        return productions;
    }

    private Productions findProduction(String key){
        if(!Objects.equals(key, "")) {
            for (Productions p : productions) {
                if (Objects.equals(p.getLeftSide(), key)) {
                    return p;
                }
            }
            Productions production = new Productions(key);
            this.productions.add(production);
            return production;
        }
        return null;
    }

    public Boolean alphabetCheck(String symbol){
        for (String i : this.getT().getAlphabet()) {
            if (Objects.equals(i, symbol)) {
                return true;
            }
        }
        return false;
    }

}
