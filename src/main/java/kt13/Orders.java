package kt13;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * Задача 4. Наибольшая прибыль от заказов со сроками.
 */
public final class Orders {

    private Orders() {
    }

    /**
     * Набор заказов выполним, если для каждого t заказов со сроком не больше t
     * не больше t: тогда их можно расставить по слотам в порядке сроков.
     *
     * Заказы перебираются по возрастанию срока, взятые лежат в куче по
     * прибыли. Очередной заказ сначала берётся. Если взятых стало больше его
     * срока, условие нарушено, и выбрасывается самый дешёвый из взятых.
     * Каждый раз выбрасывается наименьшая прибыль, так что в куче остаётся
     * самый дорогой выполнимый набор среди уже просмотренных заказов.
     *
     * Сроки до 10^9, но слотов больше n не понадобится, поэтому по слотам
     * ничего не перебирается. Сортировка и куча: O(n log n), память O(n).
     */
    public static long maxProfit(int[] deadlines, int[] profits) {
        int n = deadlines.length;
        // Срок в старших битах, прибыль в младших: оба меньше 2^30.
        long[] byDeadline = new long[n];
        for (int i = 0; i < n; i++)
            byDeadline[i] = (long) deadlines[i] << 30 | profits[i];
        Arrays.sort(byDeadline);

        PriorityQueue<Integer> taken = new PriorityQueue<>();
        long total = 0;
        for (long order : byDeadline) {
            int deadline = (int) (order >> 30);
            int profit = (int) (order & ((1 << 30) - 1));
            taken.add(profit);
            total += profit;
            if (taken.size() > deadline)
                total -= taken.poll();
        }
        return total;
    }
}
