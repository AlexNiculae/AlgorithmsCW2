import java.util.ArrayList;

public abstract class AbstractSorting {
    int cntComparisons;
    int cntExchanges;

    AbstractSorting() {
        cntComparisons = 0;
        cntExchanges = 0;
    }

    public int compareElements(int a, int b) {
        cntComparisons++;
        return a - b;
    }

    public void exchangeElements(ArrayList<Integer> arr, int i, int j) {
        cntExchanges++;
        int tmp = arr.get(i);
        arr.set(i, arr.get(j));
        arr.set(j, tmp);
    }

    public boolean checkSorted(ArrayList<Integer> arr) {
        for (int i = 0; i < arr.size() - 1; ++i) {
            if (arr.get(i) > arr.get(i+1)) {
                return false;
            }
        }

        return true;
    }

}
