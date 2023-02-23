package generator;

import java.io.IOException;
import java.util.*;

public class Generator {
    private final Grammar grammar;
    private final long length;
    private StringBuilder word;
    private Random rnd;
    private HashMap<List<Long>, List<Long>> fValues;

    public Generator(String fileName, long length) throws IOException {
        this.grammar = new Grammar();
        this.length = length;
        this.word = new StringBuilder();
        this.rnd = new Random(System.currentTimeMillis());
        this.fValues = new HashMap<>();

        this.readGrammar(fileName);
        this.grammar.eliminateEpsilonProductions();
        boolean continueOrNot = this.grammar.removingRedundantSymbols();
        if(continueOrNot){
            HashMap<String, Long> counter = new HashMap<>();
            for(long i = 0; i < 1; i++){
                this.word = g1(this.grammar.getN().getIndex(this.grammar.getS()), this.length);
                if (this.word != null) {
                    System.out.println(this.word);
                    if(counter.containsKey(this.word.toString())){
                        counter.replace(this.word.toString(), counter.get(this.word.toString()) + 1);
                    }
                    else{
                        counter.put(this.word.toString(), 1L);
                    }
                } else {
                    System.out.println("This grammar does not produce words of length " + this.length);
                }
            }
            if(!counter.isEmpty()){
                System.out.println(counter);
            }
        }
        /*List<longeger> l = this.f1(2, 3);
        for(longeger i : l){
            System.out.println
           (i);
        }*/


    }

    private void readGrammar(String fileName) throws IOException {
        FileRead f = new FileRead();
        f.readText(fileName, this.grammar);
    }

    private long listSum(List<Long> list){
        long sum = 0;
        for (long j : list) {
            sum += j;
        }
        return sum;
    }

    private List<Long> f1(long i, long n){
        long si = this.grammar.getProductions(this.grammar.getN().getSymbol((int)i)).getRightSide().size();
        List<Long> list = new ArrayList<>();
        for(long j = 1; j <= si; j++){
            if(this.fValues.containsKey(new ArrayList<>(Arrays.asList(i, j, 1L, n)))){
                list.add(listSum(this.fValues.get(new ArrayList<>(Arrays.asList(i, j, 1L, n)))));
            }
            else{
                list.add(listSum(f2(i, j, 1L, n)));
            }

        }
        this.fValues.put(new ArrayList<>(Arrays.asList(i, n)), list);
        return list;
    }

    public long lenghtOfProduction(String word){
        //String word = this.findRightSideString(j);
        long count = 1;
        long end = word.indexOf(" ");
        long start;
        if(end != -1){
            do{
                count++;
                start = end;
                end = word.indexOf(" ", (int)start+1);

            }while(end != -1);
        }
        return count;
    }

    private List<Long> f2(long i, long j, long k, long n){
        List<Long> list = new ArrayList<>();
        if(n == 0){
            this.fValues.put(new ArrayList<>(Arrays.asList(i, j, k, n)), list);
            return list;
        }
        String xijk = this.grammar.getProductions((int)i-1).findSymbolInProduction((int)j-1, (int)k);
        if(Objects.equals(xijk, "ε")){
            this.fValues.put(new ArrayList<>(Arrays.asList(i, j, k, n)), list);
            return list;
        }
        long tij = this.lenghtOfProduction(this.grammar.getProductions((int)i-1).findRightSideString((int)j-1));
        if(this.grammar.getT().getAlphabet().contains(xijk)){
            if(k == tij){
                if(n == 1){
                    list.add(1L);
                    this.fValues.put(new ArrayList<>(Arrays.asList(i, j, k, n)), list);
                    return list;
                }
                else{
                    list.add(0L);
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
            for(long l = 1; l <= n - tij + k; l++){
                if(this.fValues.containsKey(new ArrayList<>(Arrays.asList(this.grammar.getN().getIndex(xijk), l)))){
                    if(this.fValues.containsKey(new ArrayList<>(Arrays.asList(i, j, k + 1, n - l)))){
                        list.add(listSum(this.fValues.get(new ArrayList<>(Arrays.asList(this.grammar.getN().getIndex(xijk), l)))) * listSum(this.fValues.get(new ArrayList<>(Arrays.asList(i, j, k + 1, n - l)))));
                    }
                    else{
                        list.add(listSum(this.fValues.get(new ArrayList<>(Arrays.asList(this.grammar.getN().getIndex(xijk), l)))) * listSum(f2(i, j, k + 1, n - l)));
                    }
                }
                else{
                    if(this.fValues.containsKey(new ArrayList<>(Arrays.asList(i, j, k + 1, n - l)))){
                        list.add(listSum(f1(this.grammar.getN().getIndex(xijk), l)) * listSum(this.fValues.get(new ArrayList<>(Arrays.asList(i, j, k + 1, n - l)))));
                    }
                    else{
                        list.add(listSum(f1(this.grammar.getN().getIndex(xijk), l)) * listSum(f2(i, j, k + 1, n - l)));
                    }
                }
            }
            this.fValues.put(new ArrayList<>(Arrays.asList(i, j, k, n)), list);
            return list;
        }
    }

    public long choose(List<Long> list){
        List<Long> boundaries = new ArrayList<>();
        boundaries.add(list.get(0));
        for(long i = 1; i < list.size(); i++){
            boundaries.add(boundaries.get((int)i-1) + list.get((int)i));
        }

        //long x = this.rnd.nextLong(boundaries.get(boundaries.size()-1));

        long leftLimit = 0L;
        long rightLimit = boundaries.get(boundaries.size()-1);
        long x = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));

        for(long i = 0; i < boundaries.size(); i++){
            if(boundaries.get((int)i) > x){
                return i+1;
            }
        }
        return -1;

        /*long notZero = 0;
        long ind = -1;
        for(long i = 0; i < list.size(); i++){
            if(list.get(i) != 0){
                notZero += 1;
                ind = i;
            }
        }
        if(notZero == 1){
            return ind+1;
        }
        List<longeger> indexes = new ArrayList<>();
        for (long i = 0; i < list.size(); i++) {
            if(list.get(i) > 0){
                for(long j = 0; j < list.get(i); j++){
                    indexes.add(i);
                }
            }
        }
        if(indexes.size() == 0){
            return -1;
        }
        return indexes.get(this.rnd.nextlong(indexes.size())) + 1;*/
    }

    public StringBuilder g1(long i, long n){
        long r;
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

    public StringBuilder g2(long i, long j, long k, long n) {
        StringBuilder result = new StringBuilder();
        String xijk = this.grammar.getProductions((int)i - 1).findSymbolInProduction((int)j - 1,(int) k);
        long tij = this.grammar.getProductions((int)i - 1).lenghtOfProduction((int)j - 1);
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
            long l;
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
