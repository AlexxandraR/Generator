package generator;

import java.io.IOException;

public class Generator {
    Grammar grammar;
    int length;
    StringBuilder word;

    public Generator(String fileName, int length) throws IOException {
        this.grammar = new Grammar();
        this.length = length;
        this.word = new StringBuilder();

        this.readGrammar(fileName);
    }

    private void readGrammar(String fileName) throws IOException {
        FileRead f = new FileRead();
        f.readText(fileName, this.grammar);
    }
}
