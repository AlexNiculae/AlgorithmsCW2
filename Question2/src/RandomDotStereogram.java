import java.util.Random;

public class RandomDotStereogram {
    private Random random;

    private ImageController A;
    private ImageController B;
    private ImageController L;
    private ImageController R;

    RandomDotStereogram() {
        random = new Random();
    }

    private void createA() {
        A = new ImageController();
        A.setImageDimensions(512, 512);
        for (int i = 0; i < A.getHeight(); ++i) {
            for (int j = 0; j < A.getWidth(); ++j) {
                A.setPixel(i, j, 255 * random.nextInt(2));
            }
        }
    }

    private void createB() {
        B = new ImageController();
        B.setImageDimensions(256, 256);
        for (int i = 0; i < B.getHeight(); ++i) {
            for (int j = 0; j < B.getWidth(); ++j) {
                B.setPixel(i, j, 255 * random.nextInt(2));
            }
        }
    }

    private void createL() {
        L = new ImageController();
        L.setImageDimensions(512, 512);
        for (int i = 0; i < L.getHeight(); ++i) {
            for (int j = 0; j < L.getWidth(); ++j) {
                L.setPixel(i, j, A.getPixel(i, j));
            }
        }
        for (int i = 128; i < 128 + B.getHeight(); ++i) {
            for (int j = 124; j < 124 + B.getWidth(); ++j) {
                L.setPixel(i, j, B.getPixel(i - 128, j - 124));
            }
        }
    }

    private void createR() {
        R = new ImageController();
        R.setImageDimensions(512, 512);
        for (int i = 0; i < R.getHeight(); ++i) {
            for (int j = 0; j < R.getWidth(); ++j) {
                R.setPixel(i, j, A.getPixel(i, j));
            }
        }
        for (int i = 128; i < 128 + B.getHeight(); ++i) {
            for (int j = 132; j < 132 + B.getWidth(); ++j) {
                R.setPixel(i, j, B.getPixel(i - 128, j - 132));
            }
        }
    }

    private void writeImages() throws Exception {
        L.writeImage("Pair 4/view1.png");
        R.writeImage("Pair 4/view2.png");
    }

    public void createRandomDotStereogram() throws Exception {
        createA();
        createB();

        createL();
        createR();

        writeImages();
    }
}
