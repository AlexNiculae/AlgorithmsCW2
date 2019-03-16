import java.io.IOException;
import java.util.*;

public class StereoMatching {
    private final static double Pd = 0.9;
    private final static double variance = 16;
    private final static double occlusion = (Pd / (1 - Pd)) * (1 / Math.pow((2 * Math.PI * Math.pow(variance, -1)), -1.0/2.0));
    //private final static double occlusion = 0.8;

    private ImageController imageControllerLeft;
    private ImageController imageControllerRight;
    private ImageController imageDisp1;
    private ImageController imageDisp2;

    private int numberOfEpipolarLines;
    private int N, M;
    private int[][] disp1, disp2;

    private double[][] C;
    private int[][] pathTracker;

    private int indexFile;

    StereoMatching(int indexFile) throws Exception {
        this.indexFile = indexFile;

        imageControllerLeft = new ImageController();
        imageControllerLeft.readImage("Pair " + indexFile + "/view1.png");

        imageControllerRight = new ImageController();
        imageControllerRight.readImage("Pair " + indexFile + "/view2.png");
    }

    double c(int x, int y) {
        double zc = 1.0 * (x + y) / 2;
        return (Math.pow(zc - x, 2) + Math.pow(zc - y, 2)) / 32.0;
    }

    int getColorValue(Integer disparity, double coef) {
        if (disparity == null)
            return 0;
        return Math.min(255, (int)(disparity * coef));
    }

    private int getMaxDisparity(int[][] disp) {
        int maxDisparity = 0;
        for (int i = 0; i < numberOfEpipolarLines; ++i) {
            for (int j = 1; j <= N; ++j) {
                maxDisparity = Math.max(maxDisparity, disp[i][j]);
            }
        }

        return maxDisparity;
    }

    private void initialiseData() {
        numberOfEpipolarLines = imageControllerLeft.getHeight();
        N = imageControllerLeft.getWidth();
        M = imageControllerRight.getWidth();

        disp1 = new int[numberOfEpipolarLines][N+1];
        disp2 = new int[numberOfEpipolarLines][M+1];
    }

    private void computeDp(int epipolarLine) {
        C = new double[N+1][M+1];
        pathTracker = new int[N+1][M+1];
        for (int i = 1; i <= N; ++i) {
            C[i][0] = i * occlusion;
            pathTracker[i][0] = 2;
        }
        for (int i = 1; i <= M; ++i) {
            C[0][i] = i * occlusion;
            pathTracker[0][i] = 3;
        }

        for (int i = 1; i <= N; ++i) {
            for (int j = 1; j <= M; ++j) {
                int x = imageControllerLeft.getPixel(epipolarLine, i - 1);
                int y = imageControllerRight.getPixel(epipolarLine, j - 1);
                C[i][j] = Math.min(C[i-1][j-1] + c(x,y), Math.min(C[i-1][j] + occlusion, C[i][j-1] + occlusion));

                if (C[i][j] == C[i-1][j-1] + c(x, y)) {
                    pathTracker[i][j] = 1;
                }
                else if (C[i][j] == C[i-1][j] + occlusion) {
                    pathTracker[i][j] = 2;
                }
                else if (C[i][j] == C[i][j-1] + occlusion) {
                    pathTracker[i][j] = 3;
                }
            }
        }
    }

    void computeBackwardPass(int epipolarLine) {
        int x = N; int y = M;
        while (x != 0 || y != 0) {
            switch (pathTracker[x][y]) {
                case 1:
                    disp1[epipolarLine][x] = Math.abs(x - y);
                    disp2[epipolarLine][y] = Math.abs(x - y);
                    x--; y--;
                    break;
                case 2:
                    disp1[epipolarLine][x] = 0;
                    x--;
                    break;
                case 3:
                    disp2[epipolarLine][y] = 0;
                    y--;
                    break;
            }
        }
    }

    private void computeBackwardPassQueue(int epipolarLine) {
        for (int j = 0; j <= N; ++j) {
            disp1[epipolarLine][j] = 256;
        }
        for (int j = 0; j <= M; ++j) {
            disp2[epipolarLine][j] = 256;
        }

        Queue<Pair> q = new LinkedList<>();
        boolean[][] inQ = new boolean[N+1][M+1];

        q.add(new Pair(N, M));
        inQ[N][M] = true;
        while (!q.isEmpty()) {
            Pair pair = q.poll();
            int x = pair.getFirst(); int y = pair.getSecond();

            if (x > 0 && y > 0 && C[x][y] == C[x-1][y-1] + c(imageControllerLeft.getPixel(epipolarLine, x - 1), imageControllerRight.getPixel(epipolarLine, y - 1))) {
                disp1[epipolarLine][x] = Math.min(disp1[epipolarLine][x], Math.abs(x - y));
                disp2[epipolarLine][y] = Math.min(disp2[epipolarLine][y], Math.abs(x - y));
                if (!inQ[x-1][y-1]) {
                    inQ[x-1][y-1] = true;
                    q.add(new Pair(x - 1, y - 1));
                }
            }

            if (x > 0 && C[x][y] == C[x-1][y] + occlusion) {
                disp1[epipolarLine][x] = Math.min(disp1[epipolarLine][x], 256);
                if (!inQ[x-1][y]) {
                    inQ[x-1][y] = true;
                    q.add(new Pair(x - 1, y));
                }
            }

            if (y > 0 && C[x][y] == C[x][y-1] + occlusion) {
                disp2[epipolarLine][y] = Math.min(disp2[epipolarLine][y], 256);
                if (!inQ[x][y-1]) {
                    inQ[x][y-1] = true;
                    q.add(new Pair(x, y - 1));
                }
            }
        }

        for (int i = 0; i <= N; ++i) {
            if (disp1[epipolarLine][i] == 256) {
                disp1[epipolarLine][i] = 0;
            }
        }

        for (int i = 0; i <= M; ++i) {
            if (disp2[epipolarLine][i] == 256) {
                disp2[epipolarLine][i] = 0;
            }
        }
    }

    private void computeOcclusionFilling(int epipolarLine) {
        int k;

        for (k = 1; k <= N; ++k)
            if (disp1[epipolarLine][k] != 0)
                break;
        for (int i = N - 1; i > k; --i) {
            if (disp1[epipolarLine][i] == 0)
                disp1[epipolarLine][i] = disp1[epipolarLine][i+1];
        }

        for (k = M; k > 0; --k)
            if (disp2[epipolarLine][k] != 0)
                break;
        for (int i = 1; i < k; ++i) {
            if (disp2[epipolarLine][i] == 0)
                disp2[epipolarLine][i] = disp2[epipolarLine][i-1];
        }
    }

    private void computeDisparityMap() {
        int maxDisparity1 = getMaxDisparity(disp1);
        int maxDisparity2 = getMaxDisparity(disp2);

        double coef1 = 255.0 / maxDisparity1; System.out.println(coef1);
        double coef2 = 255.0 / maxDisparity2;

        imageDisp1 = new ImageController();
        imageDisp2 = new ImageController();

        imageDisp1.setImageDimensions(numberOfEpipolarLines, N);
        imageDisp2.setImageDimensions(numberOfEpipolarLines, M);

        for (int i = 0; i < numberOfEpipolarLines; ++i) {
            for (int j = 0; j < N; ++j) {
                int pixelColor = getColorValue(disp1[i][j+1], coef1);
                imageDisp1.setPixel(i, j, pixelColor);
            }
            for (int j = 0; j < M; ++j) {
                int pixelColor = getColorValue(disp2[i][j+1], coef2);
                imageDisp2.setPixel(i, j, pixelColor);
            }
        }
    }

    public void runDynamicProgramming() throws Exception {
        if (imageControllerLeft.getHeight() != imageControllerRight.getHeight()) {
            throw new Exception("The pair of images have different heights");
        }

        initialiseData();
        for (int epipolarLine = 0; epipolarLine < numberOfEpipolarLines; ++epipolarLine) {
            computeDp(epipolarLine);
            computeBackwardPass(epipolarLine);
            //computeOcclusionFilling(epipolarLine);
        }

        computeDisparityMap();
        imageDisp1.writeImage("Pair " + indexFile + "/mydisp1.png");
        imageDisp2.writeImage("Pair " + indexFile + "/mydisp2.png");
    }

    private static class Pair {
        private int first;
        private int second;

        Pair(int first, int second) {
            this.first = first;
            this.second = second;
        }

        public int getFirst() {
            return first;
        }

        public int getSecond() {
            return second;
        }
    }
}