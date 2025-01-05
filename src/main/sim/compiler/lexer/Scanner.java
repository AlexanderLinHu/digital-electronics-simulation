package sim.compiler.lexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import sim.compiler.StatusCodesCpl;
import sim.exception.StatusRuntimeException;

/**
 * A simple scanner to read through a file character by character.
 */
public class Scanner {

    private final String filePath;
    private final String fileName;

    private final BufferedReader input;

    private LinkedList<Integer> peeked;

    private int line;
    private int column;

    public Scanner(File source) throws FileNotFoundException {
        this.input = new BufferedReader(new FileReader(source));
        this.filePath = source.getPath();
        this.fileName = source.getName();
        this.peeked = new LinkedList<>();
        this.line = 1;
        this.column = 1;
    }

    /**
     * Get the the line number of the scanner's position in the source file
     *
     * @return the current line the scanner is at
     */
    public int getLine() {
        return line;
    }

    /**
     * Get the the column number of the scanner's position in the source file
     *
     * @return the current column the scanner is at
     */

    public int getColumn() {
        return column;
    }

    /**
     * Read the next character in file.
     *
     * @return the character read, as an integer from [0 : 65535], or -1 if end of file is reached
     *          (same as {@code BufferedReader.read()})
     * @throws StatusRuntimeException with status codes, <ul>
     *  <li>{@link StatusCodesCpl#BAD_READ} if an IOException occurs
     * </ul>
     */
    private int read() {
        int i;

        try {
            i = input.read();
        }
        catch (IOException e) {
            throw StatusCodesCpl.runtimeException(StatusCodesCpl.BAD_READ, e, fileName, line, column);
        }

        return i;
    }

    /**
     * Look at the nth next character without consuming it nor any characters preceding it. The character returned by
     * {@code next()} is the same as the one yielded by {@code peek(0)}
     *
     * <p>A null character (\u0000) is returned when EOF is reached. To test whether this is a null character in the
     * file, or an EOF, use {@link #hasNext()}.
     *
     * @param n how many characters ahead of the current one to peek ahead. If negative then it is treated as 0.
     * @return the next character in the input stream
     */
    public char peek(int n) {
        if (peeked.size() == n+1) {
            return (char) (int) peeked.remove(n);
        }

        do {
            peeked.add(read());
        } while (peeked.size() <= n);
        if (peeked.peek() == -1) {
            return '\u0000';
        }

        return (char) (int) peeked.remove(n);
    }

    /**
     * Consume and return the next character
     *
     * @return the next character in the input stream
     *  @throws StatusRuntimeException with status codes, <ul>
     *  <li>{@link StatusCodesCpl#BAD_READ} if at end of file
     * </ul>
     */
    public char next() {
        char p = peek(0);

        if (p == '\n') {
            line++;
            column = 1;
        }
        else {
            column++;
        }

        return p;
    }

    /**
     * Check if there is a next character
     *
     * @return true if there is a next character, false if EOF
     */
    public boolean hasNext() {
        if (peeked.peek() != -1) {
            return true;
        }

        peeked.add(read());

        if (peeked.peek() == -1) {
            return false;
        }
        return true;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }



    /**
     * Close the scanner
     *
     * @throws IOException if an IOException occurs
     */
    public void close() throws IOException {
        input.close();
    }
}
