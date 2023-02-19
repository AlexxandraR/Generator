package generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Generator {
    private final Grammar grammar;
    private final int length;
    private StringBuilder word;
    private Random rnd;

    public Generator(String fileName, int length) throws IOException {
        this.grammar = new Grammar();
        this.length = length;
        this.word = new StringBuilder();
        this.rnd = new Random(System.currentTimeMillis());

        this.readGrammar(fileName);
        this.grammar.eliminateEpsilonProductions();
        boolean continueOrNot = this.grammar.removingRedundantSymbols();
        if(continueOrNot){
            for(int i = 0; i < 368; i++){
                this.word = g1(this.grammar.getN().getIndex(this.grammar.getS()), this.length);
                //if(this.lenghtOfProduction(String.valueOf(this.word)) == this.length){
                if (this.word != null) {
                    System.out.println(this.word);
                } else {
                    System.out.println("This grammar does not produce words of length " + this.length);
                }
            }
        }
        /*List<Integer> l = this.f1(2, 3);
        for(Integer i : l){
            System.out.println(i);
        }*/


    }

    private void readGrammar(String fileName) throws IOException {
        FileRead f = new FileRead();
        f.readText(fileName, this.grammar);
    }

    private int listSum(List<Integer> list){
        int sum = 0;
        for (int j : list) {
            sum += j;
        }
        return sum;
    }

    private List<Integer> f1(int i, int n){
        /*if(!Objects.equals(this.grammar.getProductions(this.grammar.getN().getSymbol(i)).getLeftSide(), "ε")){
            return new ArrayList<>();
        }*/
        int si = this.grammar.getProductions(this.grammar.getN().getSymbol(i)).getRightSide().size();
        List<Integer> list = new ArrayList<>();
        for(int j = 1; j <= si; j++){
            list.add(listSum(f2(i, j, 1, n)));
        }
        return list;
    }

    public int lenghtOfProduction(String word){
        //String word = this.findRightSideString(j);
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

    private List<Integer> f2(int i, int j, int k, int n){
        List<Integer> list = new ArrayList<>();
        if(n == 0){
            return list;
        }
        String xijk = this.grammar.getProductions(i-1).findSymbolInProduction(j-1, k);
        if(Objects.equals(xijk, "ε")){
            return list;
        }
        int tij = this.lenghtOfProduction(this.grammar.getProductions(i-1).findRightSideString(j-1));
        if(this.grammar.getT().getAlphabet().contains(xijk)){
            if(k == tij){
                if(n == 1){
                    list.add(1);
                    return list;
                }
                else{
                    list.add(0);
                    return list;
                }
            }
            else{
                list.add(listSum(f2(i, j, k + 1, n - 1)));
                return list;
            }
        }
        if(k == tij){
            list.add(listSum(f1(this.grammar.getN().getIndex(xijk), n)));
            return list;
        }
        else{
            for(int l = 1; l <= n - tij + k; l++){
                //wait what???
                //System.out.println("Chyba.");
                list.add(listSum(f1(this.grammar.getN().getIndex(xijk), l)) * listSum(f2(i, j, k + 1, n - l)));
                //list.add(listSum(f2(i, j, k + 1, n - l)));
            }
            return list;
        }
    }

    public int choose1(List<Integer> list){
        Random rnd = new Random(System.currentTimeMillis());
        int max = list.get(0);
        List<Integer> indexes = new ArrayList<>();
        indexes.add(0);
        for (int i = 0; i < list.size(); i++) {
            if (max < list.get(i)) {
                max = list.get(i);
                indexes = new ArrayList<>();
                indexes.add(i);
            } else if (max == list.get(i)) {
                if(!indexes.contains(i)){
                    indexes.add(i);
                }
            }
        }
        if(max == 0){
            return -1;
        }
        return indexes.get(rnd.nextInt(indexes.size())) + 1;
    }

    public int choose(List<Integer> list){
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i) > 0){
                for(int j = 0; j < list.get(i); j++){
                    indexes.add(i);
                }
            }
        }
        /*int max = list.get(0);
        List<Integer> indexes = new ArrayList<>();
        indexes.add(0);
        for (int i = 0; i < list.size(); i++) {
            if (max < list.get(i)) {
                max = list.get(i);
                indexes = new ArrayList<>();
                indexes.add(i);
            } else if (max == list.get(i)) {
                if(!indexes.contains(i)){
                    indexes.add(i);
                }
            }
        }
        if(max == 0){
            return -1;
        }*/
        if(indexes.size() == 0){
            return -1;
        }
        return indexes.get(this.rnd.nextInt(indexes.size())) + 1;
    }

    public StringBuilder g1(int i, int n){
        int r = choose(f1(i, n));
        if(r == -1){
            return null;
        }
        return g2(i, r, 1, n);
    }

    public StringBuilder g2(int i, int j, int k, int n) {
        StringBuilder result = new StringBuilder();
        String xijk = this.grammar.getProductions(i - 1).findSymbolInProduction(j - 1, k);
        int tij = this.grammar.getProductions(i - 1).lenghtOfProduction(j - 1);
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
            int l = choose(f2(i, j, k, n));
            result.append(g1(this.grammar.getN().getIndex(xijk), l).append(" ").append(g2(i, j, k + 1, n - l)));
        }
        return result;
    }

}
