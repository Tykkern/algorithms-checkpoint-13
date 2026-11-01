package kt13;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Запуск решения по номеру задачи: входные данные читаются из stdin
 * в формате из задания, ответ печатается в stdout.
 *
 * Пример: gradlew run -q --args="2" < input.txt
 */
public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Укажите номер задачи от 1 до 6, входные данные подаются в stdin.");
            System.exit(1);
        }

        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
        solve(Integer.parseInt(args[0]), new FastReader(System.in), out);
        out.flush();
    }

    static void solve(int task, FastReader in, PrintWriter out) throws IOException {
        switch (task) {
            case 1 -> {
                int n = in.nextInt();
                int[] left = new int[n];
                int[] right = new int[n];
                readPairs(in, left, right);
                out.println(Bookings.maxCount(left, right));
            }
            case 2 -> {
                int q = in.nextInt();
                MinMaxStack stack = new MinMaxStack(q);
                for (int i = 0; i < q; i++) {
                    String command = in.next();
                    switch (command) {
                        case "PUSH" -> stack.push(in.nextInt());
                        case "POP" -> stack.pop();
                        case "MIN" -> out.println(stack.min());
                        case "MAX" -> out.println(stack.max());
                        default -> throw unknownCommand(command);
                    }
                }
            }
            case 3 -> {
                int n = in.nextInt();
                int k = in.nextInt();
                out.println(KthElement.select(in.nextInts(n), k));
            }
            case 4 -> {
                int n = in.nextInt();
                int[] deadlines = new int[n];
                int[] profits = new int[n];
                readPairs(in, deadlines, profits);
                out.println(Orders.maxProfit(deadlines, profits));
            }
            case 5 -> {
                LfuCache cache = new LfuCache(in.nextInt());
                int q = in.nextInt();
                for (int i = 0; i < q; i++) {
                    String command = in.next();
                    switch (command) {
                        case "GET" -> out.println(cache.get(in.nextInt()));
                        case "PUT" -> {
                            int key = in.nextInt();
                            cache.put(key, in.nextInt());
                        }
                        default -> throw unknownCommand(command);
                    }
                }
            }
            case 6 -> {
                int n = in.nextInt();
                int k = in.nextInt();
                int[] x = new int[n];
                int[] y = new int[n];
                readPairs(in, x, y);
                for (int point : ClosestPoints.select(x, y, k))
                    out.println(x[point] + " " + y[point] + " " + (point + 1));
            }
            default -> throw new IllegalArgumentException("Нет задачи с номером " + task + ", есть 1-6.");
        }
    }

    // n строк по два числа: первое в first[i], второе в second[i].
    private static void readPairs(FastReader in, int[] first, int[] second) throws IOException {
        for (int i = 0; i < first.length; i++) {
            first[i] = in.nextInt();
            second[i] = in.nextInt();
        }
    }

    private static IOException unknownCommand(String command) {
        return new IOException("Неизвестная команда '" + command + "'.");
    }
}
