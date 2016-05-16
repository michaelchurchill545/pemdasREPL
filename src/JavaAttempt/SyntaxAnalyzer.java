package JavaAttempt;


import java.util.*;

/**
 * Created by michael on 5/5/16.
 * Recursive descent parse tree.
 */
public class SyntaxAnalyzer {
    private Token[] tokens;
    private HashMap<String, Double> varMap;
    Evaluator e;
    List<Node> nodes;

    public SyntaxAnalyzer(List<Token> tokens) {
        removeWhiteSpaceTokens(tokens);
      //  printTokens(tokens);
        this.tokens = tokens.toArray(new Token[tokens.size()]);
        varMap = new HashMap<>();
        e = new Evaluator(varMap);
        nodes = new ArrayList<>();

        nodes = parseProgram(this.tokens);

        for (int i = 0; i < nodes.size() - 1; i++) {
            processVarDecl(nodes.get(i));
         //   printTree(nodes.get(i));
        }

        Node computation = nodes.get(nodes.size() - 1);
      //  printTree(computation);
        e.evalToBeUsed(computation);



    }

    /**
     * Processes all variables when variables point to other variables
     *
     * @param n
     */
    private void processVarDecl(Node n) {

        double val = e.evalToBeUsed(n.getRightChild());
        varMap.put(n.getLeftChild().getToken().getValue(), val);
    }

    /**
     * PROGRAM PRODUCTION: zero (or more) variable declarations, and exactly 1 computation.
     * <p/>
     * The syntax tree and the variable hashmap are sent to the Evaluator for complete computation.
     */
    private List<Node> parseProgram(Token[] t) { //change to private


        int semicolonIndex = 0;
        int i = 0;
        while (t[i].getValue().equals("var")) {
            semicolonIndex = findSemicolonIndex(t, i);
            nodes.add(parse_Vardecl(Arrays.copyOfRange(t, i, semicolonIndex + 1)));

            i = semicolonIndex + 1;
        }
        if (!t[i].getValue().equals("return")) {
            throw new RuntimeException("'return' expected");
        }
        Node computation = parse_Computation(Arrays.copyOfRange(t, i, t.length));
        nodes.add(computation);
        return nodes;

    }


    /**
     * Finds the index of the next semicolon.
     *
     * @param t a given list of tokens
     * @return the index at which the semi colon is found.
     */
    private int findSemicolonIndex(Token[] t, int start) {

        for (int i = start; i < t.length; i++) {
            if (t[i].getValue().equals(";")) {
                return i;
            }
        }
        throw new RuntimeException("; expected");
    }

    /**
     * Goes through the tokens and checks if the user is declaring a variable.
     *
     * @param tokens an array of tokens to iterate through
     * @return null if the token does not match, returns  a new node if it does match
     */
    private Node parse_Vardecl(Token[] tokens) {

        Node correctAST;

        //First check symbols
        if (tokens.length == 0 && tokens[0].getValue().equals("var")) {

            throw new RuntimeException("The Keyword 'var' is expected");
        }

        if (!tokens[tokens.length - 1].getValue().equals(";")) {
            throw new RuntimeException("; expected");

        }

        //Then check tokens
        if (parseLeft_VarDecl(tokens[1]) == null) {
            throw new RuntimeException("A variable is expected");
        }

        if (!parseEquals(tokens[2])) {
            throw new RuntimeException("= expected");
        }

        correctAST = parseExpression(Arrays.copyOfRange(tokens, 1, tokens.length - 1));
        return correctAST;


    }

    /**
     * Goes through to tokens and checks if the user is attempting to compute an expression.
     * <p/>
     * Production: Computation -> "return" expression ";"
     *
     * @return null if the token does not match, returns  a new node if it does match
     */
    private Node parse_Computation(Token[] t) {


        //checks keyword
        if (t.length == 0 || !t[0].getValue().equals("return")) {
            throw new RuntimeException("The Keyword 'return' is expected");
        }

        if (!t[t.length - 1].getValue().equals(";")) {
            throw new RuntimeException("; expected");

        }

        return parseExpression(Arrays.copyOfRange(t, 1, t.length - 1)); //excludes the keyword and semicolon, because they're proved to already be there


    }

    /**
     * ISSUES:
     * -The method only returns the most recursively called node, not building upon the other nodes.
     *
     * @param t a token
     * @return A node with appropriate left and right children
     */
    public Node parseExpression(Token[] t) {
        Node subtreeRoot = null;
        Node leftNode = null;
        Node rightNode = null;


        //checks if first token is a number or a variable
        if (parseNumber(t[0]) != null || parseVariable(t[0]) != null) {
            if (t.length == 1) {
                return new Node(t[0]);
            } else {
                leftNode = new Node(t[0]);
                if (parseOperator(t[1]) != null) {

                    subtreeRoot = new Node(t[1]); //sets operator as root

                    rightNode = parseExpression(Arrays.copyOfRange(t, 2, t.length));//Issue: if there is only one
                } else {
                    return new Node(new Token("EXCEPTION", "expecting operator after: " + t[1].getValue(), false));

                }
            }

            //Check for matching parens
        } else if (t[0].getValue().equals("(")) {//INPUT "((3+5) + 2)"

            int rightParenIndex = findMatchingRightParen(t);
            if (rightParenIndex >= t.length - 1) {
                return parseExpression((Arrays.copyOfRange(t, 1, rightParenIndex)));//
            } else {
                leftNode = parseExpression(Arrays.copyOfRange(t, 1, rightParenIndex));

                if (parseOperator(t[rightParenIndex + 1]) != null) {

                    subtreeRoot = new Node(t[rightParenIndex + 1]); //sets operator as root

                    rightNode = parseExpression(Arrays.copyOfRange(t, rightParenIndex + 2, t.length));//possible ArrayIndexOutOfBounds?
                } else {
                    return new Node(new Token("EXCEPTION", "expecting operator after: " + t[rightParenIndex + 1], false));

                }
            }

            //ADD variable declaration in here to shorten it .


        } else {
            return new Node(new Token("EXCEPTION", "Unknown value: " + t[0].getValue(), false));

        }
        subtreeRoot.setLeftChild(leftNode);
        subtreeRoot.setRightChild(rightNode);
        return subtreeRoot;
    }


    /**
     * Returns the index of the right paren that matches the beginning left paren
     *
     * @param t the list of Tokens
     * @return the index of the right paren
     */
    private int findMatchingRightParen(Token[] t) {
        int i = 0;
        int parenCount = 0;

        while (i < t.length) {
            if (t[i].getValue().equals("(")) {
                parenCount++;
            } else if (t[i].getValue().equals(")")) {
                parenCount--;
                if (parenCount == 0) {
                    return i;
                } else if (parenCount < 0) {
                    throw new RuntimeException("( expected");
                }
            }

            i++;
        }
        throw new RuntimeException(") expected");
    }

    /**
     * Checks if the variable declaration starts with the proper keyword  "var"
     *
     * @param t a token
     * @return null if the token does not match, returns  a new node if it does match
     */
    private Node parseLeft_VarDecl(Token t) {
        Node leftNode = parseVariable(t);
        if (leftNode != null) {
            return leftNode;
        } else {
            return null;
        }
    }


    /**
     * Checks if the token is an '='
     *
     * @param t a token
     * @return null if the token does not match, returns  a new node if it does match
     */
    private boolean parseEquals(Token t) {
        if (parseSymbol(t) != null && t.getValue().equals("=")) {
            return true;
        }
        return false;
    }

    /**
     * Chekcs if the token is a variable
     *
     * @param t a token
     * @return null if the token does not match, returns  a new node if it does match
     */
    private Node parseVariable(Token t) {
        if (t.getClassification().equals("VARIABLE")) {
            return new Node(t);
        }
        return null;
    }

    /**
     * Chekcs if the token is a number
     *
     * @param t a token
     * @return null if the token does not match, returns  a new node if it does match
     */
    private Node parseNumber(Token t) {
        if (t.getClassification().equals("NUMBER")) {
            return new Node(t);
        }
        return null;
    }


    /**
     * Chekcs if the token is an operator
     *
     * @param t a token
     * @return true if an operator and false if not
     */
    private Node parseOperator(Token t) {
        if (t.getClassification().equals("OPERATOR")) {
            return new Node(t);
        }
        return null;
    }


    /**
     * Checks if token is a symbol
     *
     * @param t token to check
     * @return the token if it is a Symbol or null if not.
     */
    private Node parseSymbol(Token t) {
        if (t.getClassification().equals("OPERATOR")) {
            return new Node(t);
        }
        return null;
    }

    /**
     * Iterates through all tokens and removes any with the "WHITESPACE" classifications
     *
     * @param t the list of tokens.
     */
    private void removeWhiteSpaceTokens(List<Token> t) {
        //List<Token> valuesToDelete = new ArrayList<>();
        for (int i = 0; i < t.size(); i++) {
            if (t.get(i).getClassification().equals("WHITESPACE")) {
                t.remove(t.get(i));
            }
        }

    }

    /**
     * iterates through tree and prints out values of tokens stored in each node.
     *
     * @param root the root node of the tree
     */
    private void printTree(Node root) {
       // System.out.println("Printing Syntax Tree: ");
        if (root == null) return;
        Stack<Node> s = new Stack<>();
        Node current = root;
        while (true) {
            if (current != null) {
                if (current.getRightChild() != null) {
                    s.push(current.getRightChild());
                    System.out.println(current.getRightChild().getToken().getValue() + " Pushed");
                }
                s.push(current);
                System.out.println(current.getToken().getValue() + " Pushed");
                current = current.getLeftChild();
                continue;
            }
            if (s.isEmpty()) {
                System.out.println();
                return;
            }
            current = s.pop();
            System.out.println(current.getToken().getValue() + " Popped");
            if (current.getRightChild() != null && !s.isEmpty() && current.getRightChild() == s.peek()) {
                s.pop();
                System.out.println(current.getToken().getValue() + " Popped");
                s.push(current);
                System.out.println(current.getToken().getValue() + " Pushed");
                current = current.getRightChild();
            } else {
                System.out.println(current.getToken().getValue() + " Value Before change");
                current = null;
            }
        }

    }


    public static void printTokens(List<Token> tokens) {
        System.out.println("Tokens without whitespace: ");
//        for (Token token : tokens) {
//            System.out.println("Index: " + tokens.indexOf(token) + ", Classification: " + token.getClassification() + ", Value: " + token.getValue());
//        }
    }
}
