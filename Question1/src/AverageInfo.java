import java.util.ArrayList;

public class AverageInfo {
    private int inputSize;
    private double averageOperations;
    private double averageTime;
    private double varianceOperations;
    private double varianceTime;
    private double stdDevOperations;
    private double stdDevTime;
    private int N;
    private ArrayList<SortInfo> arrayList = new ArrayList<>();

    AverageInfo(int inputSize) {
        this.inputSize = inputSize;
        this.averageOperations = 0;
        this.averageTime = 0;
        this.varianceOperations = 0;
        this.varianceTime = 0;
        N = 0;
    }

    public int getInputSize() {
        return inputSize;
    }

    public double getAverageOperations() {
        return averageOperations;
    }

    public double getAverageTime() {
        return averageTime;
    }

    public double getStdDevTime() {
        return stdDevTime;
    }

    public double getStdDevOperations() {
        return stdDevOperations;
    }

    public void add(SortInfo sortInfo) {
        arrayList.add(sortInfo);

        N += 1;
    }

    private void computeAverages() {
        for (SortInfo sortInfo : arrayList) {
            averageOperations += sortInfo.getCntComparisons();
            averageOperations += sortInfo.getCntExchanges();
            averageTime += sortInfo.getSortTime();
        }
        averageOperations /= N;
        averageTime /= N;
    }

    private void computeVariances() {
        for (SortInfo sortInfo : arrayList) {
            varianceOperations += Math.pow((sortInfo.getCntExchanges() + sortInfo.getCntComparisons() - averageOperations), 2);
            varianceTime += Math.pow((sortInfo.getSortTime() - averageTime), 2);
        }

        varianceOperations /= N;
        varianceTime /= N;
    }

    private void computeStandardDeviations() {
        stdDevOperations = Math.sqrt(varianceOperations);
        stdDevTime = Math.sqrt(varianceTime);
    }

    public void averageValues() {
        computeAverages();
        computeVariances();
        computeStandardDeviations();
    }
}
