import java.util.Random;

public class Main {
    void run() {
        try {
            (new RandomDotStereogram()).createRandomDotStereogram();
            for (int pairIndex = 5; pairIndex <= 5; ++pairIndex) {
                StereoMatching pair = new StereoMatching(pairIndex);
                pair.runDynamicProgramming();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        Main thisMain = new Main();
        thisMain.run();
    }
}
