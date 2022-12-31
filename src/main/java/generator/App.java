package generator;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        /*FileRead f = new FileRead();
        f.readText();*/
        Generator g = new Generator("grammar1.txt", 2);
    }
}
