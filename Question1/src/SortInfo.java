public class SortInfo {
    private int inputSize;
    private long cntComparisons;
    private long cntExchanges;
    private double sortTime;

    SortInfo(int inputSize, long cntComparisons, long cntExchanges, double sortTime) {
        this.inputSize = inputSize;
        this.cntComparisons = cntComparisons;
        this.cntExchanges = cntExchanges;
        this.sortTime = sortTime;
    }

    public int getInputSize() {
        return inputSize;
    }

    public long getCntComparisons() {
        return cntComparisons;
    }

    public long getCntExchanges() {
        return cntExchanges;
    }

    public double getSortTime() {
        return sortTime;
    }
}
