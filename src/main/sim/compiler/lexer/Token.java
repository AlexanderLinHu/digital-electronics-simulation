package sim.compiler.lexer;

import sim.compiler.Position;

// cspell:disable
public class Token {

    public enum Category {

        IDENTIFIER, // (LowerCaseAlpha | UpperCaseAlpha | '_') (Digit | LowerCaseAlpha | UpperCaseAlpha | '_' | '-')*

        //>> Delimiters
        LBRA,           // '{'  // Left brace
        RBRA,           // '}'  // Right brace
        LPAR,           // '('  // Left parenthesis
        RPAR,           // ')'  // Right parenthesis
        LSBR,           // '['  // Left square brace
        RSBR,           // ']'  // Right square brace
        ASSIGN,         // '='
        COMMA,          // ','
        ARROW,          // '-->'
        MAPSTO,         // '|->'
        COLON,          // ':'
        DOUBLE_COLON,   // '::' // Output bus aliasing

        // Binary Operators
        LOGOR,
        LOGAND,
        LT,
        LE,
        GT,
        GE,
        PLUS,
        MINUS,
        MULT,
        DIV,
        MODULO,

        //>> Directives
        INCLUDE,

        //>> Types
        DEVICE,
        CIRCUIT,
        CLOCK,
        MAIN,

        //>> Keywords
        INPUT,
        OUTPUT,

        AS,
        INSTANTIATE,
        INSTANTIATE_COUNT,
        INSTANTIATE_LAZY,

        TABLE,
        FUNCTION,
        LAMBDA_FUNCTION,

        FREQUENCY,
        JITTER,
        PHASE,

        //>> Literals
        STRING_LITERAL,
        INT_LITERAL,
        NUMBER_LITERAL,

        //>> Special
        EOF,        // End Of File
        INVALID,    // Used for unrecognized characters, or characters not part of a valid token
    }

    public final Category category;
    public final String data;
    public final Position position;

    public Token(Category category, int lineNum, int colNum) {
        this(category, "", lineNum, colNum);
    }

    public Token(Category category, String data, int lineNum, int colNum) {
        this.category = category;
        this.data = data;
        this.position = new Position(lineNum, colNum);
    }
}
