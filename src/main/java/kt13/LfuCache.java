package kt13;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Задача 5. Кэш, вытесняющий редко используемые ключи (LFU).
 *
 * Для каждой частоты хранится список ключей с этой частотой в порядке
 * последнего использования (LinkedHashSet помнит порядок добавления).
 * При использовании ключ уходит из списка своей частоты в конец списка
 * частоты на 1 больше: его момент использования самый поздний, поэтому
 * конец списка для него верное место. Вытесняется первый ключ из списка
 * наименьшей частоты.
 *
 * Наименьшая частота меняется предсказуемо: новый ключ делает её равной 1,
 * а при использовании она растёт на 1, только если опустел её список.
 * Все операции над хэш-таблицами: O(1) в среднем.
 */
public final class LfuCache {
    private final int capacity;
    private final HashMap<Integer, Integer> values = new HashMap<>();
    private final HashMap<Integer, Integer> counts = new HashMap<>();
    private final HashMap<Integer, LinkedHashSet<Integer>> byCount = new HashMap<>();
    private int minCount;

    public LfuCache(int capacity) {
        if (capacity < 1)
            throw new IllegalArgumentException("Вместимость должна быть не меньше 1.");
        this.capacity = capacity;
    }

    /** Значение ключа или -1, если ключа нет. Отсутствующий ключ состояние не меняет. */
    public int get(int key) {
        Integer value = values.get(key);
        if (value == null)
            return -1;
        touch(key);
        return value;
    }

    public void put(int key, int value) {
        if (values.containsKey(key)) {
            values.put(key, value);
            touch(key);
            return;
        }

        if (values.size() == capacity) {
            Iterator<Integer> oldest = byCount.get(minCount).iterator();
            int evicted = oldest.next();
            oldest.remove();
            values.remove(evicted);
            counts.remove(evicted);
        }

        values.put(key, value);
        counts.put(key, 1);
        byCount.computeIfAbsent(1, c -> new LinkedHashSet<>()).add(key);
        minCount = 1;
    }

    private void touch(int key) {
        int count = counts.get(key);
        LinkedHashSet<Integer> keys = byCount.get(count);
        keys.remove(key);
        if (keys.isEmpty()) {
            byCount.remove(count);
            if (minCount == count)
                minCount++;
        }
        counts.put(key, count + 1);
        byCount.computeIfAbsent(count + 1, c -> new LinkedHashSet<>()).add(key);
    }
}
