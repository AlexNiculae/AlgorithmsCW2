import java.util.ArrayList;
import java.util.Random;

public class Main {
    private Random random = new Random();
    private DataGraph averageGraph = new DataGraph();
    private final int inputSize[] = {(int)1e5, (int)2e5, (int)5e5, (int)1e6, (int)2e6, (int)5e6, (int)6e6, (int)7e6, (int)8e6, (int)1e7};
    //private final int inputSize[] = {(int)1e4, (int)2e4, (int)5e4, (int)1e5};
    private ArrayList<AverageInfo> averageQuicksortInfo = new ArrayList<>();
    private ArrayList<AverageInfo> averageHeapsortInfo = new ArrayList<>();

    private int randomInt(int low, int high) {
        high += 1;
        return low + random.nextInt(high - low);
    }

    private ArrayList<Integer> getArray(int index) {
        ArrayList<Integer> arr = new ArrayList<>();
        int N = inputSize[index];
        for (int i = 0; i < N; ++i) {
            int x = randomInt(1, (int)1e9);
            arr.add(x);
        }

        return arr;
    }

    private void run(int runIndex) throws Exception {
        DataGraph dataGraph = new DataGraph();

        for (int i = 0; i < inputSize.length; ++i) {
            ArrayList<Integer> arr = getArray(i);

            Quicksort quicksort = new Quicksort(arr);
            SortInfo quicksortInfo = quicksort.run();

            Heapsort heapsort = new Heapsort(arr);
            SortInfo heapsortInfo = heapsort.run();

            dataGraph.addQuicksortInfo(quicksortInfo);
            dataGraph.addHeapsortInfo(heapsortInfo);

            averageQuicksortInfo.get(i).add(quicksortInfo);
            averageHeapsortInfo.get(i).add(heapsortInfo);
        }

        dataGraph.plotRunGraph("Run " + runIndex, "graph" + runIndex);
    }

    private void go() {
        try {
            for (int i = 0; i < inputSize.length; ++i) {
                averageQuicksortInfo.add(new AverageInfo(inputSize[i]));
                averageHeapsortInfo.add(new AverageInfo(inputSize[i]));
            }

            averageGraph = new DataGraph();
            for (int runIndex = 1; runIndex <= 10; ++runIndex) {
                run(runIndex);
                System.out.println("Run " + runIndex + " done.");
            }

            for (int i = 0; i < inputSize.length; ++i) {
                averageQuicksortInfo.get(i).averageValues();
                averageGraph.addAverageQuicksortInfo(averageQuicksortInfo.get(i));

                averageHeapsortInfo.get(i).averageValues();
                averageGraph.addAverageHeapsortInfo(averageHeapsortInfo.get(i));
            }

            averageGraph.plotAverageGraph("Average graph", "AverageGraph");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        Main thisMain = new Main();
        thisMain.go();
    }
}
