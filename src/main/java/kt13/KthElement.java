package kt13;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Задача 3. Значение на позиции k после упорядочивания массива.
 */
public final class KthElement {

    private KthElement() {
    }

    /**
     * Быстрый выбор (quickselect). Массив разбивается вокруг опорного значения
     * на три части: меньше, равно и больше. Позиция k попадает ровно в одну из
     * них; если в среднюю, ответ найден, иначе работа продолжается только в
     * нужной крайней части.
     *
     * Опорный элемент выбирается случайно, поэтому ни один вход не даёт
     * худший случай стабильно: в среднем каждая итерация отрезает постоянную
     * долю диапазона, и суммарная работа n + n/2 + n/4 + ... = O(n).
     * Разбиение на три части нужно для массивов из одинаковых значений:
     * обычное разбиение на две части на них уходит в O(n²).
     *
     * Рекурсии нет, хранятся только границы диапазона: память O(1).
     * Массив меняется на месте.
     *
     * @param k позиция с 1
     */
    public static int select(int[] a, int k) {
        if (k < 1 || k > a.length)
            throw new IllegalArgumentException("Позиция " + k + " вне массива длины " + a.length + ".");

        int target = k - 1;
        int low = 0;
        int high = a.length - 1;
        ThreadLocalRandom random = ThreadLocalRandom.current();

        while (true) {
            int pivot = a[random.nextInt(low, high + 1)];

            // Инвариант: [low, lt) меньше pivot, [lt, i) равны, (gt, high] больше.
            int lt = low;
            int gt = high;
            int i = low;
            while (i <= gt) {
                if (a[i] < pivot)
                    swap(a, lt++, i++);
                else if (a[i] > pivot)
                    swap(a, i, gt--);
                else
                    i++;
            }

            if (target < lt)
                high = lt - 1;
            else if (target > gt)
                low = gt + 1;
            else
                return pivot;
        }
    }

    private static void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }
}
