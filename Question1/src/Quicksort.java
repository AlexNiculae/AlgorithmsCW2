import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Quicksort extends AbstractSorting {
    private ArrayList<Integer> arrayList;
    Quicksort(ArrayList<Integer> arrayList) {
        this.arrayList = new ArrayList<>(arrayList);
    }

    private int quicksortPartition(int left, int right) {
        int pivot = arrayList.get(right);
        int i = left;
        for (int j = left; j < right; ++j) {
            if (compareElements(arrayList.get(j), pivot) < 0) {
                exchangeElements(arrayList, i, j);
                i++;
            }
        }

        exchangeElements(arrayList, i, right);
        return i;
    }

    private void quicksort(int left, int right) {
        Queue<Pair> q = new LinkedList<>();
        q.add(new Pair(left, right));

        while (!q.isEmpty()) {
            Pair segment = q.poll();
            int pos = quicksortPartition(segment.getFirst(), segment.getSecond());
            if (segment.getFirst() <= pos - 1) {
                q.add(new Pair(segment.getFirst(), pos - 1));
            }
            if (pos + 1 <= segment.getSecond()) {
                q.add(new Pair(pos + 1, segment.second));
            }
        }
    }

    private Pair quicksortMiddlePivotPartition(int left, int right) {
        int i = left; int j = right;
        int pivot = arrayList.get((left + right)/2);
        while (i <= j) {
            while (i < right && compareElements(arrayList.get(i), pivot) < 0)
                i++;
            while (left < j && compareElements(pivot, arrayList.get(j)) < 0)
                j--;

            if (i <= j) {
                exchangeElements(arrayList, i, j);
                i++; j--;
            }
        }

        return new Pair(i, j);
    }

    private void quicksortMiddlePivot(int left, int right) {
        Queue<Pair> q = new LinkedList<>();
        q.add(new Pair(left, right));

        while (!q.isEmpty()) {
            Pair segment = q.poll();

            Pair pair = quicksortMiddlePivotPartition(segment.getFirst(), segment.getSecond());
            int i = pair.getFirst(); int j = pair.getSecond();
            if (segment.getFirst() < j) {
                q.add(new Pair(segment.getFirst(), j));
            }
            if (i < segment.getSecond()) {
                q.add(new Pair(i, segment.getSecond()));
            }
        }
    }

    public SortInfo run() throws Exception {
        long time = System.currentTimeMillis();
        quicksort(0, arrayList.size() - 1);
        time = System.currentTimeMillis() - time;

        if (!checkSorted(arrayList)) {
            throw new Exception("The array is not sorted after quicksort");
        }

        return new SortInfo(arrayList.size(), cntComparisons, cntExchanges, time * 0.001);
    }

    private static class Pair {
        private int first;
        private int second;

        Pair(int first, int second) {
            this.first = first;
            this.second =second;
        }

        public int getFirst() {
            return first;
        }

        public int getSecond() {
            return second;
        }
    }
}
