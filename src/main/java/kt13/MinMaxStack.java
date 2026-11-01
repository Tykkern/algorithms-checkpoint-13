package kt13;

import java.util.NoSuchElementException;

/**
 * Задача 2. Стек с минимумом и максимумом.
 *
 * Вместе с каждым значением хранятся минимум и максимум всего стека на момент
 * его добавления. После POP вершиной становится предыдущий элемент, и его
 * минимум с максимумом снова верны, пересчитывать ничего не нужно.
 *
 * Массивы выделяются сразу на всю вместимость, без расширения по ходу работы,
 * поэтому каждая операция O(1) в худшем случае, а не только в среднем.
 */
public final class MinMaxStack {
    private final int[] values;
    private final int[] mins;
    private final int[] maxs;
    private int size;

    public MinMaxStack(int capacity) {
        values = new int[capacity];
        mins = new int[capacity];
        maxs = new int[capacity];
    }

    public void push(int value) {
        if (size == values.length)
            throw new IllegalStateException("Стек заполнен.");
        values[size] = value;
        mins[size] = size == 0 ? value : Math.min(mins[size - 1], value);
        maxs[size] = size == 0 ? value : Math.max(maxs[size - 1], value);
        size++;
    }

    public int pop() {
        checkNotEmpty();
        return values[--size];
    }

    public int min() {
        checkNotEmpty();
        return mins[size - 1];
    }

    public int max() {
        checkNotEmpty();
        return maxs[size - 1];
    }

    public int size() {
        return size;
    }

    private void checkNotEmpty() {
        if (size == 0)
            throw new NoSuchElementException("Стек пуст.");
    }
}
