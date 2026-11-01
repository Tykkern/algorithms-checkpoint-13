package kt13;

import java.util.Arrays;

/**
 * Задача 1. Наибольшее число заявок в одном зале без пересечений.
 */
public final class Bookings {

    private Bookings() {
    }

    /**
     * Жадный выбор: заявки перебираются по возрастанию конца, и берётся каждая,
     * которая начинается не раньше конца последней взятой. Заявка с самым ранним
     * концом оставляет залу больше всего времени, поэтому её всегда можно
     * поставить первой в каком-нибудь оптимальном наборе.
     *
     * Промежутки полуоткрытые, поэтому начало, равное концу, совместимо.
     * Сортировка O(n log n), проход O(n).
     */
    public static int maxCount(int[] left, int[] right) {
        int n = left.length;
        // Конец в старших битах, начало в младших: сортировка long идёт по концу.
        long[] byEnd = new long[n];
        for (int i = 0; i < n; i++)
            byEnd[i] = (long) right[i] << 32 | left[i];
        Arrays.sort(byEnd);

        int count = 0;
        long freeFrom = Long.MIN_VALUE;
        for (long booking : byEnd) {
            int start = (int) booking;
            if (start >= freeFrom) {
                count++;
                freeFrom = booking >> 32;
            }
        }
        return count;
    }
}
