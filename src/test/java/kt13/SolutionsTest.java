package kt13;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SolutionsTest {

    private static final Random RANDOM = new Random(1);

    @Nested
    class BookingsTest {

        // Перебор всех подмножеств заявок с проверкой попарной совместимости.
        private int bruteForce(int[] left, int[] right) {
            int n = left.length;
            int best = 0;
            for (int mask = 0; mask < 1 << n; mask++) {
                boolean ok = true;
                for (int i = 0; i < n && ok; i++)
                    for (int j = i + 1; j < n && ok; j++)
                        if ((mask >> i & 1) == 1 && (mask >> j & 1) == 1)
                            ok = right[i] <= left[j] || right[j] <= left[i];
                if (ok)
                    best = Math.max(best, Integer.bitCount(mask));
            }
            return best;
        }

        @Test
        void basicCase() {
            assertEquals(4, Bookings.maxCount(new int[] {1, 3, 0, 5, 8, 5}, new int[] {2, 4, 6, 7, 9, 9}));
        }

        @Test
        void touchingBookingsAreCompatible() {
            assertEquals(3, Bookings.maxCount(new int[] {0, 5, 10}, new int[] {5, 10, 15}));
        }

        @Test
        void longBookingLosesToShortOnes() {
            assertEquals(2, Bookings.maxCount(new int[] {0, 1, 5}, new int[] {10, 4, 9}));
            assertEquals(1, Bookings.maxCount(new int[] {0, 0, 0}, new int[] {3, 3, 3}));
        }

        @Test
        void randomBookingsMatchBruteForce() {
            for (int test = 0; test < 500; test++) {
                int n = 1 + RANDOM.nextInt(10);
                int[] left = new int[n];
                int[] right = new int[n];
                for (int i = 0; i < n; i++) {
                    left[i] = RANDOM.nextInt(15);
                    right[i] = left[i] + 1 + RANDOM.nextInt(6);
                }
                assertEquals(bruteForce(left, right), Bookings.maxCount(left, right));
            }
        }

        @Test
        @Timeout(5)
        void maximalInput() {
            int n = 200_000;
            int[] left = new int[n];
            int[] right = new int[n];
            for (int i = 0; i < n; i++) {
                left[i] = RANDOM.nextInt(1_000_000_000);
                right[i] = left[i] + 1 + RANDOM.nextInt(1_000_000_000 - left[i]);
            }
            assertTrue(Bookings.maxCount(left, right) > 0);

            // Одинаковые заявки на весь диапазон: взять можно только одну.
            Arrays.fill(left, 0);
            Arrays.fill(right, 1_000_000_000);
            assertEquals(1, Bookings.maxCount(left, right));
        }
    }

    @Nested
    class MinMaxStackTest {

        @Test
        void basicCase() {
            MinMaxStack stack = new MinMaxStack(5);
            stack.push(3);
            stack.push(-1);
            stack.push(7);
            assertEquals(-1, stack.min());
            assertEquals(7, stack.max());
            assertEquals(7, stack.pop());
            assertEquals(3, stack.max());
            stack.pop();
            assertEquals(3, stack.min());
            assertEquals(3, stack.max());
        }

        @Test
        void emptyStackThrows() {
            MinMaxStack stack = new MinMaxStack(1);
            assertThrows(NoSuchElementException.class, stack::pop);
            assertThrows(NoSuchElementException.class, stack::min);
            assertThrows(NoSuchElementException.class, stack::max);
        }

        @Test
        void randomOperationsMatchList() {
            MinMaxStack stack = new MinMaxStack(10_000);
            List<Integer> model = new ArrayList<>();
            for (int step = 0; step < 10_000; step++) {
                if (model.isEmpty() || RANDOM.nextInt(3) > 0) {
                    int value = RANDOM.nextInt(21) - 10;
                    stack.push(value);
                    model.add(value);
                } else {
                    assertEquals(model.remove(model.size() - 1), stack.pop());
                }
                if (!model.isEmpty()) {
                    assertEquals(model.stream().min(Integer::compare).get(), stack.min());
                    assertEquals(model.stream().max(Integer::compare).get(), stack.max());
                }
                assertEquals(model.size(), stack.size());
            }
        }

        @Test
        @Timeout(5)
        void maximalInput() {
            int q = 1_000_000;
            MinMaxStack stack = new MinMaxStack(q);
            for (int i = 0; i < q; i++)
                stack.push(i % 2 == 0 ? -1_000_000_000 + i : 1_000_000_000 - i);
            assertEquals(-1_000_000_000, stack.min());
            assertEquals(1_000_000_000 - 1, stack.max());
            while (stack.size() > 1)
                stack.pop();
            assertEquals(-1_000_000_000, stack.max());
        }
    }

    @Nested
    class KthElementTest {

        private void check(int[] a) {
            int[] sorted = Arrays.copyOf(a, a.length);
            Arrays.sort(sorted);
            for (int k = 1; k <= a.length; k++)
                assertEquals(sorted[k - 1], KthElement.select(Arrays.copyOf(a, a.length), k));
        }

        @Test
        void basicCase() {
            assertEquals(3, KthElement.select(new int[] {5, 1, 4, 3, 2}, 3));
            assertEquals(7, KthElement.select(new int[] {7}, 1));
        }

        @Test
        void duplicatesTakeSeparatePositions() {
            check(new int[] {2, 2, 1, 2, 3, 2});
            check(new int[] {4, 4, 4, 4});
        }

        @Test
        void extremeValues() {
            check(new int[] {Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1, Integer.MAX_VALUE, Integer.MIN_VALUE});
        }

        @Test
        void sortedAndReversed() {
            int[] ascending = new int[50];
            for (int i = 0; i < 50; i++)
                ascending[i] = i;
            check(ascending);
            int[] descending = new int[50];
            for (int i = 0; i < 50; i++)
                descending[i] = 50 - i;
            check(descending);
        }

        @Test
        void randomArraysMatchSort() {
            for (int test = 0; test < 300; test++) {
                int n = 1 + RANDOM.nextInt(40);
                int bound = 1 + RANDOM.nextInt(20);
                check(RANDOM.ints(n, -bound, bound).toArray());
            }
        }

        @Test
        void wrongPositionThrows() {
            assertThrows(IllegalArgumentException.class, () -> KthElement.select(new int[] {1, 2}, 0));
            assertThrows(IllegalArgumentException.class, () -> KthElement.select(new int[] {1, 2}, 3));
        }

        @Test
        @Timeout(5)
        void maximalInput() {
            int n = 5_000_000;
            int[] a = RANDOM.ints(n).toArray();
            int[] sorted = Arrays.copyOf(a, n);
            Arrays.sort(sorted);
            assertEquals(sorted[n / 2], KthElement.select(a, n / 2 + 1));

            // Все значения равны: разбиение на две части ушло бы в квадрат.
            Arrays.fill(a, 42);
            assertEquals(42, KthElement.select(a, n));

            for (int i = 0; i < n; i++)
                a[i] = i;
            assertEquals(n - 1, KthElement.select(a, n));
        }
    }

    @Nested
    class OrdersTest {

        // Перебор подмножеств: набор выполним, если заказов со сроком <= t не больше t для каждого t.
        private long bruteForce(int[] deadlines, int[] profits) {
            int n = deadlines.length;
            long best = 0;
            for (int mask = 0; mask < 1 << n; mask++) {
                boolean ok = true;
                for (int t = 1; t <= n && ok; t++) {
                    int count = 0;
                    for (int i = 0; i < n; i++)
                        if ((mask >> i & 1) == 1 && deadlines[i] <= t)
                            count++;
                    ok = count <= t;
                }
                if (!ok)
                    continue;
                long sum = 0;
                for (int i = 0; i < n; i++)
                    if ((mask >> i & 1) == 1)
                        sum += profits[i];
                best = Math.max(best, sum);
            }
            return best;
        }

        @Test
        void basicCase() {
            assertEquals(70, Orders.maxProfit(new int[] {1, 1, 2, 2}, new int[] {20, 10, 40, 30}));
            assertEquals(142, Orders.maxProfit(new int[] {2, 1, 2, 1, 3}, new int[] {100, 19, 27, 25, 15}));
        }

        @Test
        void farDeadlinesTakeEverything() {
            assertEquals(3_000_000_000L, Orders.maxProfit(
                    new int[] {1_000_000_000, 1_000_000_000, 1_000_000_000},
                    new int[] {1_000_000_000, 1_000_000_000, 1_000_000_000}));
        }

        @Test
        void randomOrdersMatchBruteForce() {
            for (int test = 0; test < 500; test++) {
                int n = 1 + RANDOM.nextInt(10);
                int[] deadlines = RANDOM.ints(n, 1, 6).toArray();
                int[] profits = RANDOM.ints(n, 1, 20).toArray();
                assertEquals(bruteForce(deadlines, profits), Orders.maxProfit(deadlines, profits));
            }
        }

        @Test
        @Timeout(5)
        void maximalInput() {
            int n = 500_000;
            int[] deadlines = RANDOM.ints(n, 1, 1_000_000_001).toArray();
            int[] profits = RANDOM.ints(n, 1, 1_000_000_001).toArray();
            assertTrue(Orders.maxProfit(deadlines, profits) > 0);

            // Все сроки 1: выполнить можно один заказ, самый дорогой.
            Arrays.fill(deadlines, 1);
            for (int i = 0; i < n; i++)
                profits[i] = i + 1;
            assertEquals(n, Orders.maxProfit(deadlines, profits));
        }
    }

    @Nested
    class LfuCacheTest {

        // Простая модель: значение, частота и момент последнего использования, вытеснение перебором.
        private static final class NaiveCache {
            private final int capacity;
            private final Map<Integer, long[]> entries = new HashMap<>();
            private long clock;

            NaiveCache(int capacity) {
                this.capacity = capacity;
            }

            int get(int key) {
                long[] entry = entries.get(key);
                if (entry == null)
                    return -1;
                entry[1]++;
                entry[2] = ++clock;
                return (int) entry[0];
            }

            void put(int key, int value) {
                long[] entry = entries.get(key);
                if (entry != null) {
                    entry[0] = value;
                    entry[1]++;
                    entry[2] = ++clock;
                    return;
                }
                if (entries.size() == capacity) {
                    int victim = -1;
                    long[] worst = null;
                    for (Map.Entry<Integer, long[]> e : entries.entrySet()) {
                        long[] v = e.getValue();
                        if (worst == null || v[1] < worst[1] || v[1] == worst[1] && v[2] < worst[2]) {
                            worst = v;
                            victim = e.getKey();
                        }
                    }
                    entries.remove(victim);
                }
                entries.put(key, new long[] {value, 1, ++clock});
            }
        }

        @Test
        void basicCase() {
            LfuCache cache = new LfuCache(2);
            cache.put(1, 1);
            cache.put(2, 2);
            assertEquals(1, cache.get(1));
            cache.put(3, 3);                 // у 2 частота 1, вытесняется он
            assertEquals(-1, cache.get(2));
            assertEquals(3, cache.get(3));
            cache.put(4, 4);                 // у 1 и 3 частота 2, 1 использовался раньше
            assertEquals(-1, cache.get(1));
            assertEquals(3, cache.get(3));
            assertEquals(4, cache.get(4));
        }

        @Test
        void putExistingKeyCountsAsUse() {
            LfuCache cache = new LfuCache(2);
            cache.put(1, 10);
            cache.put(2, 20);
            cache.put(1, 11);                // частота 1 стала 2
            cache.put(3, 30);                // вытесняется 2
            assertEquals(11, cache.get(1));
            assertEquals(-1, cache.get(2));
            assertEquals(30, cache.get(3));
        }

        @Test
        void missingKeyDoesNotChangeState() {
            LfuCache cache = new LfuCache(1);
            cache.put(5, 50);
            assertEquals(-1, cache.get(6));
            assertEquals(50, cache.get(5));
            cache.put(6, 60);
            assertEquals(-1, cache.get(5));
            assertEquals(60, cache.get(6));
        }

        @Test
        void randomOperationsMatchNaiveCache() {
            for (int test = 0; test < 200; test++) {
                int capacity = 1 + RANDOM.nextInt(4);
                LfuCache cache = new LfuCache(capacity);
                NaiveCache model = new NaiveCache(capacity);
                for (int step = 0; step < 200; step++) {
                    int key = RANDOM.nextInt(8);
                    if (RANDOM.nextBoolean()) {
                        assertEquals(model.get(key), cache.get(key));
                    } else {
                        int value = RANDOM.nextInt(100);
                        model.put(key, value);
                        cache.put(key, value);
                    }
                }
            }
        }

        @Test
        @Timeout(5)
        void maximalInput() {
            LfuCache cache = new LfuCache(200_000);
            long sum = 0;
            for (int i = 0; i < 500_000; i++) {
                int key = RANDOM.nextInt(400_000);
                if (i % 2 == 0)
                    cache.put(key, i);
                else
                    sum += cache.get(key);
            }
            assertNotEquals(0, sum);
        }
    }

    @Nested
    class ClosestPointsTest {

        private int[] bruteForce(int[] x, int[] y, int k) {
            Integer[] order = new Integer[x.length];
            for (int i = 0; i < x.length; i++)
                order[i] = i;
            Arrays.sort(order, (a, b) -> {
                long da = (long) x[a] * x[a] + (long) y[a] * y[a];
                long db = (long) x[b] * x[b] + (long) y[b] * y[b];
                if (da != db)
                    return Long.compare(da, db);
                if (x[a] != x[b])
                    return Integer.compare(x[a], x[b]);
                if (y[a] != y[b])
                    return Integer.compare(y[a], y[b]);
                return Integer.compare(a, b);
            });
            int[] result = new int[k];
            for (int i = 0; i < k; i++)
                result[i] = order[i];
            return result;
        }

        @Test
        void basicCase() {
            int[] x = {3, 1, -1, 0, 2};
            int[] y = {3, 0, 0, 2, -2};
            // Расстояния 18, 1, 1, 4, 8; при равных 1 раньше x = -1.
            assertArrayEquals(new int[] {2, 1, 3}, ClosestPoints.select(x, y, 3));
        }

        @Test
        void samePointsGoByNumber() {
            int[] x = {5, 5, 5};
            int[] y = {-5, -5, -5};
            assertArrayEquals(new int[] {0, 1, 2}, ClosestPoints.select(x, y, 3));
        }

        @Test
        void largeCoordinatesDoNotOverflow() {
            int[] x = {1_000_000_000, -1_000_000_000, 0};
            int[] y = {1_000_000_000, 999_999_999, -1_000_000_000};
            assertArrayEquals(new int[] {2, 1, 0}, ClosestPoints.select(x, y, 3));
        }

        @Test
        void randomPointsMatchFullSort() {
            for (int test = 0; test < 500; test++) {
                int n = 1 + RANDOM.nextInt(60);
                int k = 1 + RANDOM.nextInt(n);
                int bound = 1 + RANDOM.nextInt(5);
                int[] x = RANDOM.ints(n, -bound, bound + 1).toArray();
                int[] y = RANDOM.ints(n, -bound, bound + 1).toArray();
                assertArrayEquals(bruteForce(x, y, k), ClosestPoints.select(x, y, k));
            }
        }

        @Test
        @Timeout(5)
        void maximalInput() {
            int n = 2_000_000;
            int k = 100_000;
            int[] x = RANDOM.ints(n, -1_000_000_000, 1_000_000_001).toArray();
            int[] y = RANDOM.ints(n, -1_000_000_000, 1_000_000_001).toArray();
            int[] result = ClosestPoints.select(x, y, k);

            // Полная сортировка двух миллионов пунктов в таймаут не влезает, поэтому
            // проверяем порядок внутри ответа и что лучше последнего ровно k - 1 пунктов.
            for (int i = 1; i < k; i++)
                assertTrue(ClosestPoints.compare(x, y, result[i - 1], result[i]) < 0);
            int better = 0;
            for (int i = 0; i < n; i++)
                if (ClosestPoints.compare(x, y, i, result[k - 1]) < 0)
                    better++;
            assertEquals(k - 1, better);

            // Пункты по убыванию расстояния: порог не отсекает ничего, буфер переполняется постоянно.
            for (int i = 0; i < n; i++) {
                x[i] = n - i;
                y[i] = 0;
            }
            result = ClosestPoints.select(x, y, k);
            assertEquals(n - 1, result[0]);
            assertEquals(n - k, result[k - 1]);
        }
    }
}
