package generator;

import java.io.IOException;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.*;

public class Generator {
    private final Grammar grammar;
    private int length;
    private StringBuilder word;
    private final Random rnd;
    private final HashMap<List<Integer>, List<BigInteger>> fValues;
    private final FileWrite fileWriter;

    /**
     * constructor for Generator class
     * @param fileName the name of file where grammar is saved
     * @param beginLength the smallest length of generated word
     * @param endLength the biggest length of generated word
     * @param numberOfRepetitions the number of generated words
     */
    public Generator(String fileName, int beginLength, int endLength, int numberOfRepetitions) throws IOException {
        this.grammar = new Grammar();
        this.word = new StringBuilder();
        this.rnd = new Random(System.currentTimeMillis());
        this.fValues = new HashMap<>();
        this.length = 0;
        this.fileWriter = new FileWrite();

        generate(fileName, beginLength, endLength, numberOfRepetitions);
    }

    /**
     * getter for grammar
     * @return grammar read from file
     */
    public Grammar getGrammar() {
        return grammar;
    }

    /**
     * controls:
     * the loading of the grammar from the file,
     * the modification of the grammar to the desired form,
     * the start of the generator and the saving of the output to the file
     * @param fileName the name of file where grammar is saved
     * @param beginLength the smallest length of generated word
     * @param endLength the biggest length of generated word
     * @param numberOfRepetitions the number of generated words
     */
    private void generate(String fileName, int beginLength, int endLength, int numberOfRepetitions) throws IOException {
        String canContinue = this.readGrammar(fileName);

        this.fileWriter.writeText(this, beginLength, endLength, numberOfRepetitions);

        if(canContinue == null){
            this.grammar.eliminateEpsilonProductions();
            if(this.grammar.containsCyclus()){
                this.grammar.properGrammar();
            }
            boolean continueOrNot = this.grammar.removingRedundantSymbols();

            if(continueOrNot){
                HashMap<String, Integer> counter = new HashMap<>();
                for(int i = 0; i < numberOfRepetitions; i++) {
                    if(endLength < beginLength){
                        this.fileWriter.writeException("Word length generation limits are specified incorrectly.");
                        break;
                    }
                    this.length = rnd.nextInt(endLength - beginLength + 1) + beginLength;
                    //System.out.println(this.length);
                    if (this.length == 0 && this.grammar.getProductions(this.grammar.getS()).findIndex("") != -1) {
                        //System.out.println("This grammar generates a word of length 0.");
                        this.fileWriter.appendText("This grammar generates an empty word.");
                    }
                    else if (this.length == 0 && this.grammar.getProductions(this.grammar.getS()).findIndex("") == -1) {
                        //System.out.println("This grammar does not generate a word of length 0.");
                        this.fileWriter.appendText("This grammar does not generates an empty word.");
                    }
                    else {
                        this.word = g1(this.grammar.getN().getIndex(this.grammar.getS()), this.length);
                        if (this.word != null) {
                            //System.out.println(this.word);
                            this.fileWriter.appendText(this.word.toString());
                            if (counter.containsKey(this.word.toString())) {
                                counter.replace(this.word.toString(), counter.get(this.word.toString()) + 1);
                            }
                            else {
                                counter.put(this.word.toString(), 1);
                            }
                        }
                        else{
                            //System.out.println("This grammar does not produce words of length " + this.length + ".");
                            this.fileWriter.appendText("This grammar does not produce words of length " + this.length + ".");
                        }
                    }
                }
                if (!counter.isEmpty()) {
                    //System.out.println(counter);
                    this.fileWriter.appendStatistics(counter);
                }
            }
            else{
                this.fileWriter.writeException("The grammar generates an empty language, so no word can be derived from the initial symbol.");
            }
        }
        else{
            this.fileWriter.writeException(canContinue);
        }
    }

    /**
     * call the static method readText from class FileRead
     * @param fileName the name of file where grammar is saved
     * @return null if everything was loaded correctly and an error message if the loading was not successful
     * @throws IOException if something failed while reading the file
     */
    private String readGrammar(String fileName) throws IOException {
        FileRead f = new FileRead();
        return f.readText(fileName, this.grammar);
    }

    /**
     * adds all the numbers from the list
     * @param list list of numbers l = [l_1, l_2, ..., l_m]
     * @return the sum of the numbers in the list l
     */
    private BigInteger listSum(List<BigInteger> list){
        BigInteger sum = BigInteger.ZERO;
        for (BigInteger j : list) {
            sum = sum.add(j);
        }
        return sum;
    }

    /**
     * returns a list whose values are the number of strings of length n generated by every possible rule with the left side equal to N_i
     * @param i index of non-terminal
     * @param n length of words
     * @return a list of the number of strings of length n generated by individual rules of non-terminal N_i
     */
    private List<BigInteger> f1(int i, int n){
        int si = this.grammar.getProductions(this.grammar.getN().getSymbol(i-1)).getRightSide().size();
        List<BigInteger> list = new ArrayList<>();
        for(int j = 1; j <= si; j++){
            if(this.fValues.containsKey(new ArrayList<>(Arrays.asList(i, j, 1, n)))){
                list.add(listSum(this.fValues.get(new ArrayList<>(Arrays.asList(i, j, 1, n)))));
            }
            else{
                list.add(listSum(f2(i, j, 1, n)));
            }
        }
        this.fValues.put(new ArrayList<>(Arrays.asList(i, n)), list);
        return list;
    }

    /**
     * calculates the number of symbols in the rule
     * @param word right side of the rule
     * @return the number of symbols in the rule
     */
    public int lengthOfProduction(String word){
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

    /**
     * returns a list whose values specify the number of strings of length n generated by trailing symbols
     * (or suffix) x_ijk. . . x_ijt_ij from the right-hand side of the rule π_ij for each of the possible paths in which
     * n symbols can be divided between the first x_ijk symbol and the remaining symbols
     * @param i index of non-terminal
     * @param j index of rule for non-terminal N_i
     * @param k k takes on values from 0 to the number of symbols in the rule
     * @param n length of words
     * @return a list of the number of strings of length n generated from the suffix x_ijk...x_ijt_ij of the rule π_ij
     * for all admissible prefixes derived from x_ijk
     */
    private List<BigInteger> f2(int i, int j, int k, int n){
        List<BigInteger> list = new ArrayList<>();
        if(n == 0){
            this.fValues.put(new ArrayList<>(Arrays.asList(i, j, k, n)), list);
            return list;
        }
        String xijk = this.grammar.getProductions(i-1).findSymbolInProduction(j-1, k);
        //System.out.println(xijk);
        if(Objects.equals(xijk, "")){
            this.fValues.put(new ArrayList<>(Arrays.asList(i, j, k, n)), list);
            return list;
        }
        int tij = this.lengthOfProduction(this.grammar.getProductions(i-1).findRightSideString(j-1));
        if(this.grammar.getT().getAlphabet().contains(xijk)){
            if(k == tij){
                if(n == 1){
                    list.add(BigInteger.ONE);
                    this.fValues.put(new ArrayList<>(Arrays.asList(i, j, k, n)), list);
                    return list;
                }
                else{
                    list.add(BigInteger.ZERO);
                    this.fValues.put(new ArrayList<>(Arrays.asList(i, j, k, n)), list);
                    return list;
                }
            }
            else{
                if(this.fValues.containsKey(new ArrayList<>(Arrays.asList(i, j, k + 1, n - 1)))){
                    list.add(listSum(this.fValues.get(new ArrayList<>(Arrays.asList(i, j, k + 1, n - 1)))));
                }
                else{
                    list.add(listSum(f2(i, j, k + 1, n - 1)));
                }

                this.fValues.put(new ArrayList<>(Arrays.asList(i, j, k, n)), list);
                return list;
            }
        }
        if(k == tij){
            if(this.fValues.containsKey(new ArrayList<>(Arrays.asList(this.grammar.getN().getIndex(xijk), n)))){
                list.add(listSum(this.fValues.get(new ArrayList<>(Arrays.asList(this.grammar.getN().getIndex(xijk), n)))));
            }
            else{
                list.add(listSum(f1(this.grammar.getN().getIndex(xijk), n)));
            }
            return list;
        }
        else{
            for(int l = 1; l <= n - tij + k; l++){
                if(this.fValues.containsKey(new ArrayList<>(Arrays.asList(this.grammar.getN().getIndex(xijk), l)))){
                    if(this.fValues.containsKey(new ArrayList<>(Arrays.asList(i, j, k + 1, n - l)))){
                        list.add(listSum(this.fValues.get(new ArrayList<>(Arrays.asList(this.grammar.getN().getIndex(xijk), l)))).multiply(listSum(this.fValues.get(new ArrayList<>(Arrays.asList(i, j, k + 1, n - l))))));
                    }
                    else{
                        list.add(listSum(this.fValues.get(new ArrayList<>(Arrays.asList(this.grammar.getN().getIndex(xijk), l)))).multiply(listSum(f2(i, j, k + 1, n - l))));
                    }
                }
                else{
                    if(this.fValues.containsKey(new ArrayList<>(Arrays.asList(i, j, k + 1, n - l)))){
                        list.add(listSum(f1(this.grammar.getN().getIndex(xijk), l)).multiply(listSum(this.fValues.get(new ArrayList<>(Arrays.asList(i, j, k + 1, n - l))))));
                    }
                    else{
                        list.add(listSum(f1(this.grammar.getN().getIndex(xijk), l)).multiply(listSum(f2(i, j, k + 1, n - l))));
                    }
                }
            }
            this.fValues.put(new ArrayList<>(Arrays.asList(i, j, k, n)), list);
            return list;
        }
    }

    /**
     * randomly select an index from list l
     * @param list list of numbers l = [l_1, l_2, ..., l_m]
     * @return randomly selected index of number l_i with probability l_i / sum(l)
     */
    public int choose(List<BigInteger> list){
        List<BigInteger> boundaries = new ArrayList<>();
        boundaries.add(list.get(0));
        for(int i = 1; i < list.size(); i++){
            boundaries.add(boundaries.get(i-1).add(list.get(i)));
        }
        BigInteger rightLimit = boundaries.get(boundaries.size()-1);
        BigInteger randomNumber;
        if(rightLimit.equals(BigInteger.ZERO)){
            return -1;
        }
        BigDecimal tmp = BigDecimal.valueOf(Math.random());
        tmp = tmp.multiply(new BigDecimal(rightLimit));
        randomNumber = tmp.toBigInteger();
        for(int i = 0; i < boundaries.size(); i++){
            if(boundaries.get(i).compareTo(randomNumber) == 1){
                return i+1;
            }
        }
        return -1;
    }

    /**
     * randomly generates a word of length n from non-terminal N_i
     * @param i index of non-terminal
     * @param n length of words
     * @return a uniformly randomly generated string of terminals of length n from a non-terminal N_i
     */
    public StringBuilder g1(int i, int n){
        int r;
        if(this.fValues.containsKey(new ArrayList<>(Arrays.asList(i, n)))){
            r = choose(this.fValues.get(new ArrayList<>(Arrays.asList(i, n))));
        }
        else{
            r = choose(f1(i, n));
        }
        if(r == -1){
            return null;
        }
        return g2(i, r, 1, n);
    }

    /**
     * randomly generates a word of length n from the trailing symbols x_ijk. . . x_ijt_ij
     * taken from the right-hand side of the production π_ij
     * @param i index of non-terminal
     * @param j index of rule for non-terminal N_i
     * @param k k takes on values from 0 to the number of symbols in the rule
     * @param n length of words
     * @return string of length n
     */
    public StringBuilder g2(int i, int j, int k, int n) {
        StringBuilder result = new StringBuilder();
        String xijk = this.grammar.getProductions(i - 1).findSymbolInProduction(j - 1, k);
        long tij = this.grammar.getProductions(i - 1).lengthOfProduction(j - 1);
        if(this.grammar.getT().getAlphabet().contains(xijk)) {
            if (k == tij) {
                return result.append(xijk);
            }
            else{
                result.append(xijk).append(" ").append(g2(i, j, k + 1, n - 1));
                return result;
            }
        }
        if(k == tij){
            return g1(this.grammar.getN().getIndex(xijk), n);
        }
        else{
            int l;
            if(this.fValues.containsKey(new ArrayList<>(Arrays.asList(i, j, k, n)))){
                l = choose(this.fValues.get(new ArrayList<>(Arrays.asList(i, j, k, n))));
            }
            else{
                l = choose(f2(i, j, k, n));
            }
            result.append(g1(this.grammar.getN().getIndex(xijk), l).append(" ").append(g2(i, j, k + 1, n - l)));
        }
        return result;
    }

}
