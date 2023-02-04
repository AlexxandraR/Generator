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
        this.productions = new LinkedHashSet<>();
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

    /** adding a new production **/
    public void addToProductions(String key, String value) {
        Productions p = null;
        if(!Objects.equals(key, "")) {
            p = getProductions(key);
            if(p == null){
                p = new Productions(key);
                this.productions.add(p);
            }
        }
        if(p != null){
            p.getRightSide().add(value);
        }
    }

    /** get all productions **/
    public Set<Productions> getProductions() {
        return productions;
    }

    public Productions getProductions(int index) {
        int i = 0;
        for(Productions p : productions){
            if(i == index){
                return p;
            }
            i++;
        }
        return null;
    }

    /** get all productions with left side equal to leftSide **/
    public Productions getProductions(String leftSide) {
        for(Productions p : productions){
            if(Objects.equals(p.getLeftSide(), leftSide)){
                return p;
            }
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

    private boolean findSymbolRight(String symbol){
        for(Productions p : this.productions){
            for(String s : p.getRightSide()){
                if(s.contains(symbol)){
                    return true;
                }
            }
        }
        return false;
    }

    private void deleteProductionRight(String symbol){
        int numberOfProductions = productions.size();
        for(int i = 0; i < numberOfProductions; i++){
            Productions p = this.getProductions(i);
            if(p != null){
                int numberOfRightStrings = p.getRightSide().size();
                for(int j = 0; j < numberOfRightStrings; j++) {
                    String s = p.findRightSideString(j);
                    if (s.contains(symbol)) {
                        p.getRightSide().remove(s);
                    }
                }
            }
        }
    }

    /** reformat grammars so that they contain no epsilon rules **/
    public void eliminateEpsilonProductions(){
        Set<String> productionsToAdd;
        Set<String> n_epsilon;
        n_epsilon = epsilonSymbols();
        int numberOfProductions = productions.size();
        for(int i = 0; i < numberOfProductions; i++){
            Productions p = this.getProductions(i);
            if(p !=null){
                int numberOfRightStrings = p.getRightSide().size();
                for(int j = 0; j < numberOfRightStrings; j++){
                    String s = p.findRightSideString(j);
                    if(s.contains("ε") && this.findSymbolRight(p.getLeftSide())){
                        p.getRightSide().remove("ε");
                        if(p.getRightSide().size() == 0){
                            this.deleteProductionRight(p.getLeftSide());
                            this.n.getAlphabet().remove(p.getLeftSide());
                            this.productions.remove(p);
                        }
                    }
                    productionsToAdd = createNewProductions(n_epsilon, p, s);
                    p.getRightSide().addAll(productionsToAdd);
                    numberOfRightStrings = p.getRightSide().size();
                }
            }
        }

        if(n_epsilon.contains(this.s) && findSymbolRight(this.s)){
            String left = "S_new";
            this.getProductions(this.s).getRightSide().remove("ε");
            this.n.getAlphabet().add(left);
            this.addToProductions(left, this.s);
            this.addToProductions(left, "ε");
            this.s = left;
        }

        System.out.print("N_ε: ");

        for(String s : n_epsilon){
            System.out.print(s + ", ");
        }

        System.out.print("\nN: ");
        for(String s : this.n.getAlphabet()){
            System.out.print(s + ", ");
        }
        System.out.print("\nT: ");
        for(String s : this.t.getAlphabet()){
            System.out.print(s + ", ");
        }
        System.out.println("\nS: " + s);
        System.out.println("P: ");

        for(Productions p : productions){
            System.out.print(p.getLeftSide() + " -> ");
            int numberOfRightStrings = p.getRightSide().size();
            for(int j = 0; j < numberOfRightStrings; j++) {
                String s = p.findRightSideString(j);
                if(j == 0){
                    System.out.println(s);
                }
                else{
                    System.out.println("  |  " + s);
                }
            }

        }
    }

    /** generates new productions **/
    public Set<String> generateProductions(int n, int r, List<Integer> indexList, String s, int length) {
        Set<String> productionsToAdd = new HashSet<>();
        StringBuilder newProduction = new StringBuilder();
        generateVariations(new int[r], 0, n - 1, 0, indexList, s, productionsToAdd, length, newProduction);
        return productionsToAdd;
    }

    /** generates all variations of new productions **/
    private void generateVariations(int[] data, int start, int end, int index, List<Integer> indexList, String s, Set<String> productionsToAdd, int length, StringBuilder newProduction) {
        if (index == data.length) {
            newProduction.append(s);
            for(int i = 0; i < data.length; i++){
                newProduction.replace(indexList.get(data[i]) - 2 * i - 1, indexList.get(data[i]) - 2 * i + length, "");
            }
            productionsToAdd.add(newProduction.toString());
            newProduction.delete(0, newProduction.length());
        } else if (start <= end) {
            data[index] = start;
            generateVariations(data, start + 1, end, index + 1, indexList, s, productionsToAdd, length, newProduction);
            generateVariations(data, start + 1, end, index, indexList, s, productionsToAdd, length, newProduction);
        }
    }

    /** creates new productions after deleting epsilon productions **/
    private Set<String> createNewProductions(Set<String> n_epsilon,  Productions p, String s){
        Set<String> productionsToAdd = new HashSet<>();
        List<Integer> index = new ArrayList<>();
        int counter;
        for(String n : n_epsilon){
            counter = 0;
            index.removeAll(index);
            if(s.contains(n)){
                index.add(s.indexOf(n));
                while(index.get(counter) > 0){
                    index.add(s.indexOf(n, index.get(counter) + 1));
                    counter++;
                }
                for(int i = 1; i < index.size(); i++) {
                    productionsToAdd.addAll(generateProductions(index.size() - 1, i, index, s, n.length()));
                }
                if(Objects.equals(p.getLeftSide(), this.s) && n_epsilon.contains(this.s)){
                    productionsToAdd.add("ε");
                }
            }
        }
        return productionsToAdd;
    }

    /** creates a set of non-terminal symbols that can be successively rewritten to an empty word **/
    private Set<String> epsilonSymbols(){
        Set<String> n_epsilon = new HashSet<>();
        n_epsilon.add("ε");
        Set<String> n_e = new HashSet<>(n_epsilon);
        do{
            n_e.addAll(n_epsilon);
            n_epsilon.addAll(findSymbol(n_e));
        }while(n_e.size() != n_epsilon.size());
        n_epsilon.remove("ε");
        return n_epsilon;
    }

    /** searching for non-terminal symbols that have the required characters on the right-hand side of the productions **/
    private Set<String> findSymbol(Set<String> symbols){
        Set<String> n_e = new HashSet<>();
        int counter;
        int productionLength;
        for(Productions p : this.productions){
            counter = 0;
            productionLength = -1;
            for(String symbol : symbols) {
                for(String r : p.getRightSide()){
                    if (r.contains(symbol)){
                        counter++;
                        if(productionLength == -1){
                            productionLength++;
                        }
                        productionLength += r.length();
                    }
                }
                if(counter == productionLength){
                    n_e.add(p.getLeftSide());
                }
                counter = 0;
                productionLength = -1;
            }
        }
        return n_e;
    }

}
