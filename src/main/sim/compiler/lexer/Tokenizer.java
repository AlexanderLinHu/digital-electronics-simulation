package sim.compiler.lexer;

// cspell:disable

import java.util.Map;

import sim.compiler.CompilerPass;

public class Tokenizer extends CompilerPass {

    private final Scanner scanner;

    public static final String COMMENT_START        = "//";
    public static final String BLOCK_COMMENT_START  = "/*";
    public static final String BLOCK_COMMENT_END    = "*/";


    //*>> ---------------------- Mappings: Source text ==> Token.Category ---------------------- */
    //      All supported tokens (of consistent length) must appear in a map exactly once.       //
    //      Within each map the entries are arranged in the same order the tokens are            //
    //      declared within the Token class.                                                     //

    /**
     * All supported directives
     */
    private static final Map<String, Token.Category> directiveTokens = Map.ofEntries(
        Map.entry("#include", Token.Category.INCLUDE)
    );


    /**
     * Map of all reserved tokens with the form of an identifier.
     * <p>When matching an identifer token if the name is in this map then instead return the corresponding token.
     */
    private static final Map<String, Token.Category> reservedIdentifierTokens = Map.ofEntries(
        // Types
        Map.entry("DEVICE", Token.Category.DEVICE),
        Map.entry("CIRCUIT", Token.Category.CIRCUIT),
        Map.entry("CLOCK", Token.Category.CLOCK),
        Map.entry("MAIN", Token.Category.MAIN),

        // Keywords
        Map.entry("INPUT", Token.Category.INPUT),
        Map.entry("OUTPUT", Token.Category.OUTPUT),
        Map.entry("AS", Token.Category.AS),
        Map.entry("INSTANTIATE", Token.Category.INSTANTIATE),
        Map.entry("INSTANTIATE_COUNT", Token.Category.INSTANTIATE_COUNT),
        Map.entry("INSTANTIATE_LAZY", Token.Category.INSTANTIATE_LAZY),
        Map.entry("TABLE", Token.Category.TABLE),
        Map.entry("FUNCTION", Token.Category.FUNCTION),
        // Map.entry("LAMBDA_FUNCTION", Token.Category.LAMBDA_FUNCTION), // parsed and returned when detected
        Map.entry("FREQUENCY", Token.Category.FREQUENCY),
        Map.entry("JITTER", Token.Category.JITTER),
        Map.entry("PHASE", Token.Category.PHASE)
    );


    /**
     * Map of all tokens consisting of only non-alphanumeric characters. The longest token is 2 characters long
     */
    private static final Map<String, Token.Category> specialCharTokens = Map.ofEntries(
        Map.entry("{", Token.Category.LBRA),
        Map.entry("}", Token.Category.RBRA),
        Map.entry("(", Token.Category.LPAR),
        Map.entry(")", Token.Category.RPAR),
        Map.entry("[", Token.Category.LSBR),
        Map.entry("]", Token.Category.RSBR),
        Map.entry("=", Token.Category.ASSIGN),
        Map.entry(",", Token.Category.COMMA),
        Map.entry("-->", Token.Category.ARROW),
        Map.entry("|->", Token.Category.MAPSTO),
        Map.entry(":", Token.Category.COLON),
        Map.entry("::", Token.Category.DOUBLE_COLON),

        // Binary Operators
        Map.entry("||", Token.Category.LOGOR),
        Map.entry("&&", Token.Category.LOGAND),
        Map.entry("<", Token.Category.LT),
        Map.entry("<=", Token.Category.LE),
        Map.entry(">", Token.Category.GT),
        Map.entry(">=", Token.Category.GE),
        Map.entry("+", Token.Category.PLUS),
        Map.entry("-", Token.Category.MINUS),
        Map.entry("*", Token.Category.MULT),
        Map.entry("/", Token.Category.DIV),
        Map.entry("%", Token.Category.MODULO)
    );

    public Tokenizer(Scanner scanner) {
        this.scanner = scanner;
    }

    /** Returns the path to the source file, uses default file seperator  */
    public String getFilePath() {
        return scanner.getFilePath();
    }

    //*>> --------------------------------- Core functionality --------------------------------- */
    //            Main functionality of this class is the next token method. This method         //
    //               relies on a number of helper methods defined  below this method             //

    public Token nextToken() {
        final int line = scanner.getLine();
        final int column = scanner.getColumn();

        if (!scanner.hasNext()) {
            return new Token(Token.Category.EOF, line, column);
        }

        char currentChar = scanner.next();

        // Skip white space between lexems
        while (Character.isWhitespace(currentChar)) {
            currentChar = scanner.next();
        }

        // Max length for special tokens is 3
        String lookAheadToken = ""  + currentChar + scanner.peek(0) + scanner.peek(1);

        // Check if we have a comment
        if (lookAheadToken.startsWith(COMMENT_START)) {
            while (scanner.hasNext() && scanner.peek(0) != '\n') {
                scanner.next();
            }
            return nextToken();
        }
        if (lookAheadToken.startsWith(BLOCK_COMMENT_START)) {
            char prev = ' ';
            while (scanner.hasNext() && !(("" + prev + scanner.peek(0)).equals("*/"))) {
                prev = scanner.next();
            }
            if (scanner.hasNext()) { // Consume div character (/) for the end of comment, wrapped around if block
                scanner.next();     // incase we reached end of file
            }

            return nextToken();
        }

        // Check if we can match a special char token. By longest matching rule we check the 3 character version first
        if (specialCharTokens.containsKey(lookAheadToken)) {
            scanner.next(); scanner.next(); // We are using the 2nd and 3rd character but haven't consumed it yet
            return new Token(specialCharTokens.get(lookAheadToken), line, column);
        }
        if (specialCharTokens.containsKey(lookAheadToken.substring(0,2))) {
            scanner.next(); // We are using the 2nd character but haven't consumed it yet
            return new Token(specialCharTokens.get(lookAheadToken), line, column);
        }
        if (specialCharTokens.containsKey(String.valueOf(currentChar))) {
            return new Token(specialCharTokens.get(String.valueOf(currentChar)), line, column);
        }


        if (currentChar == '"'){
            return readStringLiteralToken(line, column);
        }

        if (currentChar == '#') {
            return readDirectiveToken(line, column);
        }

        if (Character.isLetter(currentChar) || currentChar == '_') {
            return readIdentifierToken(currentChar, line, column);
        }

        if (Character.isDigit(currentChar)) {
            return readNumberToken(currentChar, line, column);
        }

        // If we reach this point it means the current character is not the start of any recognized token
        String errorMessage = "Lexing error: character (" + currentChar +
                                ") not recognized as the start of any valid token "+line+":"+column;
        error(errorMessage);
        return new Token(Token.Category.INVALID, errorMessage, line, column);
    }


    //*>> ----------------------------------- Helper Methods ----------------------------------- */
    //      Parsing logic for different types of tokens have been factored into their own        //
    //      methods. Methods below should only be called when it is ok to assume the next        //
    //      token must be of that type                                                           //


    /**
     * Assume the next token is a directive, attempt to parse it.
     *
     * @param line the line number where the character of the directive is found
     * @param column the column number where the character of the directive is found
     * @return a token of the corresponding directive, or invalid if its not a recognized directive
     */
    private Token readDirectiveToken(int line, int column) {
        final StringBuilder sb = new StringBuilder("#");

        while (scanner.hasNext() && Character.isLetter(scanner.peek(0))) {
            sb.append(scanner.next());
            if (directiveTokens.containsKey(sb.toString())) {
                return new Token(directiveTokens.get(sb.toString()), line, column);
            }
        }

        String errorMessage = "Unrecognized directive <"+sb+"> at "+line+":"+column;
        error(errorMessage);
        return new Token(Token.Category.INVALID, errorMessage, line, column);
    }

    /**
     * Basic string reader. Continues until another double quote (") is found.
     *
     * <p>Escape characters are not supported, all characters are read and stored as is.
     *
     * @param line the line number where the character of the directive is found
     * @param column the column number where the character of the directive is found
     * @return a {@code STRING_LITERAL} token with its contents in the data field, or invalid token if found EOF
     *          before a closing quote
     */
    private Token readStringLiteralToken(int line, int column) {
        StringBuilder sb = new StringBuilder();

        while (scanner.hasNext()) {
            char c = scanner.next();

            // Terminate String
            if (c == '"') {
                return new Token(Token.Category.STRING_LITERAL, sb.toString(), line, column);
            }

            sb.append(c);
        }

        String errorMessage = "Non-terminated string literal starting at "+line+":"+column;
        error(errorMessage);
        return new Token(Token.Category.INVALID, errorMessage, line, column);
    }


    /**
     * Assume the next token is an identifier, parse it.
     *
     * @param firstChar the first character of the identifier
     * @param line the line number where the character of the directive is found
     * @param column the column number where the character of the directive is found
     * @return if the identifier is a reserved keyword then return the corresponding token, else return
     *          an identifier token whose data field is the name of the identifier
     */
    private Token readIdentifierToken(char firstChar, int line, int column) {
        StringBuilder sb = new StringBuilder(Character.toString(firstChar));

        while (scanner.hasNext() && (Character.isLetterOrDigit(scanner.peek(0))
                                    || scanner.peek(0) == '_'
                                    || scanner.peek(0) == '-')) {
            sb.append(scanner.next());
        }

        String str = sb.toString();
        if (reservedIdentifierTokens.containsKey(str)) {
            return new Token(reservedIdentifierTokens.get(str), line, column);
        }
        return new Token(Token.Category.IDENTIFIER, str, line, column);
    }


    /**
     * Assume the next token is an integer/number, parse it.
     *
     * @param firstDigit the first digit
     * @param line the line number where the character of the directive is found
     * @param column the column number where the character of the directive is found
     * @return an int literal token is parsed an int, if a decimal is fonud in the number then a number literal
     *          token is returned instead. Both will have the value stored in the data field
     */
    private Token readNumberToken(char firstDigit, int line, int column) {
        StringBuilder sb = new StringBuilder();

        sb.append(firstDigit);
        while (scanner.hasNext() && Character.isDigit(scanner.peek(0))) {
            sb.append(scanner.next());
        }
        if (scanner.hasNext() && scanner.peek(0) != '.') { // End of integer
            return new Token(Token.Category.INT_LITERAL, sb.toString(), line, column);
        }

        // We have a decimal point as the next number, continue parsing as a number
        sb.append(scanner.next());
        while (scanner.hasNext() && Character.isDigit(scanner.peek(0))) {
            sb.append(scanner.next());
        }
        return new Token(Token.Category.NUMBER_LITERAL, sb.toString(), line, column);
    }
}
