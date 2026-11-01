package kt13;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Проверка чтения входных данных и формата вывода для каждой задачи.
 */
class MainTest {

    private static String run(int task, String input) throws IOException {
        StringWriter output = new StringWriter();
        PrintWriter out = new PrintWriter(output);
        FastReader in = new FastReader(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        Main.solve(task, in, out);
        out.flush();
        return output.toString().replace("\r\n", "\n");
    }

    @Test
    void task1() throws IOException {
        assertEquals("2\n", run(1, "3\n1 2\n3 4\n0 6\n"));
        assertEquals("2\n", run(1, "2\n0 5\n5 10\n"));
    }

    @Test
    void task2() throws IOException {
        assertEquals("-1\n3\n3\n3\n", run(2, "7\nPUSH 3\nPUSH -1\nMIN\nMAX\nPOP\nMIN\nMAX\n"));
    }

    @Test
    void task3() throws IOException {
        assertEquals("2\n", run(3, "5 2\n5 1 4 3 2\n"));
        assertEquals("-2147483648\n", run(3, "2 1\n2147483647 -2147483648\n"));
    }

    @Test
    void task4() throws IOException {
        assertEquals("70\n", run(4, "4\n1 20\n1 10\n2 40\n2 30\n"));
    }

    @Test
    void task5() throws IOException {
        String input = "2 9\nPUT 1 1\nPUT 2 2\nGET 1\nPUT 3 3\nGET 2\nGET 3\nPUT 4 4\nGET 1\nGET 4\n";
        assertEquals("1\n-1\n3\n-1\n4\n", run(5, input));
    }

    @Test
    void task6() throws IOException {
        assertEquals("-1 0 3\n1 0 2\n0 2 4\n", run(6, "5 3\n3 3\n1 0\n-1 0\n0 2\n2 -2\n"));
    }

    @Test
    void windowsLineEndings() throws IOException {
        assertEquals("1\n", run(5, "1 2\r\nPUT 7 1\r\nGET 7\r\n"));
    }

    @Test
    void unknownCommandThrows() {
        assertThrows(IOException.class, () -> run(2, "1\nPEEK\n"));
    }

    @Test
    void truncatedInputThrows() {
        assertThrows(EOFException.class, () -> run(3, "3 1\n1 2\n"));
    }

    @Test
    void unknownTaskThrows() {
        assertThrows(IllegalArgumentException.class, () -> run(7, ""));
    }
}
