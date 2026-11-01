package kt13;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Задача 6. Первые k пунктов по расстоянию до начала координат.
 */
public final class ClosestPoints {

    private ClosestPoints() {
    }

    /**
     * Пункты проходят через буфер на 2k индексов. Когда буфер заполнен,
     * быстрым выбором в его начало собираются k лучших, остальные
     * отбрасываются: среди отброшенных нет ни одного из k лучших вообще,
     * ведь k лучших из буфера лучше каждого из них. Отбор стоит O(k) в среднем
     * и случается не чаще одного раза на k новых пунктов, итого O(n).
     * Кроме того, после отбора k-й из лучших становится порогом, и пункты хуже
     * него в буфер даже не попадают.
     *
     * В конце ещё один отбор и сортировка k оставшихся: O(k log k).
     * Дополнительная память: буфер на 2k индексов, то есть O(k).
     *
     * @return индексы выбранных пунктов с 0 в порядке сравнения
     */
    public static int[] select(int[] x, int[] y, int k) {
        int n = x.length;
        if (k < 1 || k > n)
            throw new IllegalArgumentException("k должно быть от 1 до " + n + ", а не " + k + ".");

        int[] buffer = new int[(int) Math.min(n, 2L * k)];
        int size = 0;
        int threshold = -1;
        for (int i = 0; i < n; i++) {
            if (threshold >= 0 && compare(x, y, i, threshold) > 0)
                continue;
            if (size == buffer.length) {
                keepBest(x, y, buffer, size, k);
                size = k;
                threshold = buffer[k - 1];
                if (compare(x, y, i, threshold) > 0)
                    continue;
            }
            buffer[size++] = i;
        }
        keepBest(x, y, buffer, size, k);

        Integer[] best = new Integer[k];
        for (int i = 0; i < k; i++)
            best[i] = buffer[i];
        Arrays.sort(best, (a, b) -> compare(x, y, a, b));

        int[] result = new int[k];
        for (int i = 0; i < k; i++)
            result[i] = best[i];
        return result;
    }

    /**
     * Порядок из условия: x² + y², затем x, затем y, затем номер. Номера
     * разные, поэтому равенства у разных пунктов не бывает. При |x|, |y| <= 10^9
     * сумма квадратов не больше 2 * 10^18 и помещается в long.
     */
    static int compare(int[] x, int[] y, int a, int b) {
        long distanceA = (long) x[a] * x[a] + (long) y[a] * y[a];
        long distanceB = (long) x[b] * x[b] + (long) y[b] * y[b];
        if (distanceA != distanceB)
            return Long.compare(distanceA, distanceB);
        if (x[a] != x[b])
            return Integer.compare(x[a], x[b]);
        if (y[a] != y[b])
            return Integer.compare(y[a], y[b]);
        return Integer.compare(a, b);
    }

    /**
     * Быстрый выбор со случайным опорным элементом: после него первые k ячеек
     * буфера содержат k лучших пунктов, а ячейка k - 1 содержит k-й из них.
     */
    private static void keepBest(int[] x, int[] y, int[] buffer, int size, int k) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int target = k - 1;
        int low = 0;
        int high = size - 1;
        while (low < high) {
            swap(buffer, random.nextInt(low, high + 1), high);
            int pivot = buffer[high];
            int store = low;
            for (int i = low; i < high; i++)
                if (compare(x, y, buffer[i], pivot) < 0)
                    swap(buffer, i, store++);
            swap(buffer, store, high);

            if (store == target)
                return;
            if (target < store)
                high = store - 1;
            else
                low = store + 1;
        }
    }

    private static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }
}
