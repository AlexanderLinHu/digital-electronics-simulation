package sim.compiler.lexer;
// cspell:disable
public class Token {

    public enum Category {

        //>> Delimiters
        LBRA,           // '{'  // Left brace
        RBRA,           // '}'  // Right brace
        LPAR,           // '('  // Left parenthesis
        RPAR,           // ')'  // Right parenthesis
        LSBR,           // '['  // Left square brace
        RSBR,           // ']'  // Right square brace
        ASSIGN,         // '='
        MINUS,          // '-'
        COMMA,          // ','
        ARROW,          // '-->'
        COLON,          // ':'
        DOUBLE_COLON,   // '::' // Output bus aliasing

    }
}
