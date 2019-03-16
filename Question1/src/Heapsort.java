import java.util.ArrayList;

public class Heapsort extends AbstractSorting {
    private ArrayList<Integer> arrayList;
    private ArrayList<Integer> heap;

    Heapsort(ArrayList<Integer> arrayList) {
        this.arrayList = new ArrayList<>(arrayList);
        heap = new ArrayList<>();
    }

    private int leftSon(int k) {
        return k * 2;
    }

    private int rightSon(int k) {
        return k * 2 + 1;
    }

    private int parent(int k) {
        return k / 2;
    }

    private void sink(int k, int n) {
        while (leftSon(k) <= n) {
            int j = (leftSon(k) == n || compareElements(heap.get(leftSon(k)), heap.get(rightSon(k))) > 0) ? leftSon(k) : rightSon(k);
            if (compareElements(heap.get(k), heap.get(j)) >= 0)
                break;
            exchangeElements(heap, k, j);
            k = j;
        }
    }

    private void heapify(int n) {
        for (int k = parent(n); k > 0; --k) {
            sink(k, n);
        }
    }

    private void heapsort(int n) {
        heapify(n);
        while (n > 0) {
            exchangeElements(heap, 1, n);
            n -= 1;
            sink(1, n);
        }
    }

    public SortInfo run() throws Exception {
        heap.add(-1);
        for (int x : arrayList) {
            heap.add(x);
        }

        long time = System.currentTimeMillis();
        heapsort(heap.size() - 1);
        time = System.currentTimeMillis() - time;

        for (int i = 0; i < arrayList.size(); ++i) {
            arrayList.set(i, heap.get(i+1));
        }

        if (!checkSorted(arrayList)) {
            throw new Exception("The array is not sorted after heapsort");
        }

        return new SortInfo(arrayList.size(), cntComparisons, cntExchanges, time * 0.001);
    }
}
