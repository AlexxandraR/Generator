package generator;

import java.util.*;

public class Grammar {
    private final Alphabet n;
    private final Alphabet t;
    private String s;
    private final Set<Productions> productions;

    public Grammar() {
        this.n = new Alphabet();
        this.t = new Alphabet();
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

    public Set<Productions> getProductions() {
        return productions;
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

    public void removeTerminals(){
        for(int i = 0; i < this.t.getAlphabet().size(); i++){
            String terminal = this.t.getTerminal(i);
            boolean contains = false;
            for(Productions p : this.productions){
                for(String s : p.getRightSide()){
                    StringBuilder sHelp = new StringBuilder();
                    sHelp.append(" ").append(s).append(" ");
                    StringBuilder terminalHelp = new StringBuilder();
                    terminalHelp.append(" ").append(terminal).append(" ");
                    if(sHelp.toString().contains(terminalHelp)){
                        contains = true;
                        break;
                    }
                }
                if(contains){
                    break;
                }
            }
            if(!contains){
                this.t.getAlphabet().remove(terminal);
            }
        }
    }

    private boolean findSymbolRight(String symbol){
        StringBuilder symbolHelp = new StringBuilder();
        symbolHelp.append(" ").append(symbol).append(" ");
        for(Productions p : this.productions){
            for(String s : p.getRightSide()){
                StringBuilder sHelp = new StringBuilder();
                sHelp.append(" ").append(s).append(" ");
                if(sHelp.toString().contains(symbolHelp)){
                    return true;
                }
            }
        }
        return false;
    }

    private void deleteProductionRight(String symbol){
        StringBuilder symbolHelp = new StringBuilder();
        symbolHelp.append(" ").append(symbol).append(" ");
        int numberOfProductions = productions.size();
        for(int i = 0; i < numberOfProductions; i++){
            Productions p = this.getProductions(i);
            if(p != null){
                int numberOfRightStrings = p.getRightSide().size();
                for(int j = 0; j < numberOfRightStrings; j++) {
                    String s = p.findRightSideString(j);
                    StringBuilder sHelp = new StringBuilder();
                    sHelp.append(" ").append(s).append(" ");
                    if(sHelp.toString().contains(symbolHelp)) {
                        p.getRightSide().remove(s);
                        j--;
                        //numberOfProductions--;
                        numberOfRightStrings--;
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

        /*System.out.print("N_ε: ");

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

        }*/
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
        List<Integer> index;
        int counter;
        for(String n : n_epsilon){
            counter = 0;
            index = new ArrayList<>();
            StringBuilder sHelp = new StringBuilder();
            sHelp.append(" ").append(s).append(" ");
            StringBuilder nHelp = new StringBuilder();
            nHelp.append(" ").append(n).append(" ");
            if(sHelp.toString().contains(nHelp)){
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
            if(n_e.size() == 1){
                //System.out.println(n_epsilon.size()-1);
            }
        }while(n_e.size() != n_epsilon.size());
        n_epsilon.remove("ε");
        return n_epsilon;
    }

    /** searching for non-terminal symbols that have the required characters on the right-hand side of the productions **/
    private Set<String> findSymbol(Set<String> symbols){
        Set<String> n_e = new HashSet<>();
        for(Productions p : this.productions){
            for(String symbol : symbols) {
                if(p.getRightSide().contains(symbol)){
                    n_e.add(p.getLeftSide());
                }
            }
        }
        return n_e;
    }

    public boolean removingRedundantSymbols(){
        Set<String> remainingSymbols = deriveTerminals();
        if(!remainingSymbols.contains(this.s)){
            //System.out.println("The grammar generates an empty language, so no word can be derived from the initial symbol.");
            return false;
        }
        else{
            this.deleteNonterminals(remainingSymbols, true);
            remainingSymbols = availableNonterminals();
            this.deleteNonterminals(remainingSymbols, false);
            this.removeTerminals();

            /*System.out.print("\nN: ");
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

            }*/

            return true;
        }
    }

    private Set<String> availableNonterminals(){
        Set<String> v_d = new HashSet<>();
        Set<String> v_d1 = new HashSet<>(v_d);
        v_d.add(this.s);
        do{
            v_d1.addAll(v_d);
            v_d.addAll(findNonterminals(v_d1));
        }while(v_d.size() != v_d1.size());
        /*System.out.println("VD");
        for(String word : v_d){
            System.out.println(word);
        }*/
        return v_d;
    }

    private Set<String> findNonterminals(Set<String> v_d){
        Set<String> v_d1 = new HashSet<>();
        for(String s : v_d){
            Productions production = this.getProductions(s);
            if(production != null){
                for(String word : production.getRightSide()){
                    for(String nonterminal : this.n.getAlphabet()){
                        StringBuilder wordHelp = new StringBuilder();
                        wordHelp.append(" ").append(word).append(" ");
                        StringBuilder nonterminalHelp = new StringBuilder();
                        nonterminalHelp.append(" ").append(nonterminal).append(" ");
                        if(wordHelp.toString().contains(nonterminalHelp)){
                            v_d1.add(nonterminal);
                        }
                    }
                }
            }

        }
        return v_d1;
    }

    private Set<String> deriveTerminals(){
        Set<String> n_t = new HashSet<>();
        Set<String> n_t1 = new HashSet<>(n_t);
        do{
            n_t1.addAll(n_t);
            n_t.addAll(findDeriveTerminals(n_t1));
        }while(n_t.size() != n_t1.size());
        //this.deleteNonterminals(n_t);
        /*System.out.println("NT");
        for(String word : n_t){
            System.out.println(word);
        }*/
        return n_t;
    }

    private Set<String> findDeriveTerminals(Set<String> n_t){
        Set<String> n_t1 = new HashSet<>();
        for(Productions p : this.productions){
            for(String s : p.getRightSide()){
                boolean toAdd = true;
                int begin = 0;
                int end = s.indexOf(" ", begin);
                while(true){
                    if(end == -1){
                        end = s.length();
                    }
                    String word = s.substring(begin, end);
                    if(!this.t.getAlphabet().contains(word) && !n_t.contains(word) && !word.equals("ε")){
                        toAdd = false;
                        break;
                    }
                    if(end == s.length()){
                        break;
                    }
                    begin = end + 1;
                    end = s.indexOf(" ", begin + 1);
                }
                if(toAdd){
                    n_t1.add(p.getLeftSide());
                    break;
                }
            }
        }
        return n_t1;
    }

    private void deleteNonterminals(Set<String> nonterminals, boolean removeFromRight){
        //System.out.println("nadbytocne:");
        for(int i = 0; i < this.n.getAlphabet().size(); i++){
            String symbol = this.n.getTerminal(i);
            if(!nonterminals.contains(symbol)){
                //System.out.println(symbol);
                this.n.getAlphabet().remove(symbol);
                this.productions.remove(this.getProductions(symbol));
                if(removeFromRight){
                    deleteProductionRight(symbol);
                }
            }
        }
    }

    /** vlastna gramatika **/

    public boolean containsCyclus(){
        for(Productions production : this.productions){
            Set<String> n = new HashSet<>();
            n.add(production.getLeftSide());
            Set<String> n_n;
            do{
                n_n = n;
                n = new HashSet<>();
                for(String nont : n_n){
                    /*if(production.getRightSide().contains(nont)){
                        n.add(nont);
                    }*/
                    for(String s : this.n.getAlphabet()){
                        if(this.getProductions(nont).getRightSide().contains(s)){
                            n.add(s);
                        }
                    }
                }
                if(n.contains(production.getLeftSide())){
                    //System.out.println("ano cyklus");
                    return true;
                }
                else{
                    n.addAll(n_n);
                }
            }while(!n.equals(n_n));
        }
        //System.out.println("nie cyklus");
        return false;
    }

    public void ownGrammar(){
        for(String nonterminal : this.n.getAlphabet()){
            Set<String> n = new HashSet<>();
            n.add(nonterminal);
            Set<String> n_n;
            do{
                n_n = n;
                n = new HashSet<>();
                for(String nont : n_n){
                    for(String s : this.n.getAlphabet()){
                        if(this.getProductions(nont).getRightSide().contains(s)){
                            n.add(s);
                        }
                    }
                }
                n.addAll(n_n);
            }while(!n_n.equals(n));
            for (String s : n) {
                //System.out.println(nonterminal + " " + s);
                this.getProductions(nonterminal).getRightSide().remove(s);
                for(String string : this.getProductions(s).getRightSide()){
                    if(!this.n.getAlphabet().contains(string)){
                        this.getProductions(nonterminal).getRightSide().add(string);
                    }
                }
            }
        }
        /*System.out.println("Vlastna gramatika");
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

        }*/
    }

}
