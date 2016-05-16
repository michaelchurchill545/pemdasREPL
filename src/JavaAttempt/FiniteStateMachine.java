package JavaAttempt;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by michael on 5/8/16.
 * This FSM takes all of the input from a given string and splits recognized sequences of characters into proper tokens to be passed to
 * the syntax analyzer.
 */
public class FiniteStateMachine {
    List<Token> tokens = new ArrayList<>();

    /**
     * CONSTRUCTOR
     *
     * @param argument string argument passed in from command line
     */
    public FiniteStateMachine(String argument) {
        argument = argument + "\n";
        tokens = tokenize(argument);
        SyntaxAnalyzer sa = new SyntaxAnalyzer(tokens);//pipeline

    }

    /**
     * Turns a string argument into a list of tokens determined by these states:
     * <p/>
     * Base state: The starting state, and where the word state and number state transition to if the current char is of the same type as that state.
     * The base state checks the current char and directs it to its proper destination. The base state also appends the current char to a stringbuilder
     * that controls the length of variables, numbers, or symbols that are more than length 1.
     * <p/>
     * Word state: The word state checks if the character being looked at is [a-zA-Z], and chooses to either append the character to the token (if it is
     * a letter) or send the token to the Output Token state (if the current char is not a letter).
     * <p/>
     * Number state: The number state checks if the current char is a number[0-9] or contains a '.'. If the character does match, the char is appended to
     * the token and the pointer is advanced to look at the next char in the string arg. If the char does not match, the state of the token is sent to
     * the output token state.
     * <p/>
     * <p/>
     * OutputToken state: the output token state is where the stringbuilder is sent when the current char looked at is not appended to the string.
     * The string is given a Token classification and a boolean value determining if it is terminal or not, and then packaged as a new Token and added
     * to the list of tokens.
     *
     * @param argument the passed in String argument
     * @return a list of tokens
     */
    public List<Token> tokenize(String argument) {
        String numbers = "0123456789.";
        State currentState = State.base;
        StringBuilder currentToken = new StringBuilder();
        int i = 0;
        while (i < argument.length()) {

            char currentChar = argument.charAt(i);
            // this segment determines which state to transition to depending on currentChar
            if (currentState.equals(State.base)) {
                if (isOperator(currentChar) || isSymbol(currentChar)) {
                    currentState = State.outputToken;
                } else if (currentChar == ' ' || currentChar == '\n') {
                    currentState = State.outputToken;

                } else if (isAlpha(currentChar)) {
                    currentState = State.word;
                } else if (numbers.indexOf(currentChar) >= 0) {
                    currentState = State.number;

                } else {
                    throw new RuntimeException("Can't advance out of base state");
                }
                currentToken.append(currentChar);
                i++;
            } else if (currentState.equals(State.word)) {
                if (!isAlpha(currentChar)) {
                    currentState = State.outputToken;
                } else {
                    currentToken.append(currentChar);
                    i++;
                }

            } else if (currentState.equals(State.number)) {

                if (!isNumber("" + currentChar)) {
                    currentState = State.outputToken;
                } else {
                    currentToken.append(currentChar);
                    i++;
                }

            } else if (currentState.equals(State.outputToken)) {

                String tokenType;
                boolean isTerminal;
                if (currentToken.length() == 1 && isSymbol(currentToken.charAt(0))) {
                    tokenType = "SYMBOL";
                    isTerminal = false;
                } else if (currentToken.toString().equals("var") || currentToken.toString().equals("return") ) {
                    tokenType = "KEYWORD";
                    isTerminal = true;
                }else if(currentToken.toString().equals("sin")||currentToken.toString().equals("cos")||currentToken.toString().equals("tan")){
                    tokenType = "TRIG";
                    isTerminal = false;
                } else if (currentToken.length() == 1 && isOperator(currentToken.charAt(0))) {
                    tokenType = "OPERATOR";
                    isTerminal = false;
                } else if (isNumber(currentToken.toString())) {
                    tokenType = "NUMBER";
                    isTerminal = true;
                } else if (currentToken.toString().matches("\\w+")) {
                    tokenType = "VARIABLE";
                    isTerminal = true;
                } else if (currentToken.length() == 1 && (currentToken.charAt(0) == ' ' || currentToken.charAt(0) == '\n')) {
                    tokenType = "WHITESPACE";
                    isTerminal = true;
                } else {
                    throw new RuntimeException("You broke the system: unknown token: " + currentToken);
                }


                tokens.add(new Token(tokenType, currentToken.toString(), isTerminal));

                currentToken.setLength(0);


                currentState = State.base;
            } else {
                throw new RuntimeException("Error While transitioning states");
            }

        }
        return tokens;
    }

    private boolean isAlpha(char charToCheck) {
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if (alphabet.indexOf(charToCheck) >= 0) {
            return true;
        }
        return false;

    }

    private boolean isSymbol(char charToCheck) {
        String symbols = ";()";
        if (symbols.indexOf(charToCheck) >= 0) {
            return true;
        }
        return false;
    }

    private boolean isNumber(String token) {

        if (token.matches("(\\.|\\d+|\\-)?\\.?\\d+")) {
            return true;
        }
        return false;

    }

    private boolean isOperator(char charToCheck) {
        String operators = "=+-^*/";
        if (operators.indexOf(charToCheck) >= 0) {
            return true;
        }
        return false;
    }


    /**
     * Objects used for finite state machine
     */
    private enum State {
        base, word, number, outputToken, done;

        State() {
            //constructor
        }

    }
}
