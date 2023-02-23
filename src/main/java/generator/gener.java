package generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class gener {
    public void choose(){
        Random rnd = new Random(System.currentTimeMillis());
        for(int i = 0; i < 20; i++) {
            System.out.println(rnd.nextInt(19) + 1);
        }
    }
}
