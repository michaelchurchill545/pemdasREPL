package JavaAttempt;


import java.util.*;

/**
 * Created by michael on 5/5/16.
 * Recursive descent parse tree.
 */
public class SyntaxAnalyzer {
    private Token[] tokens;

    public SyntaxAnalyzer(List<Token> tokens) {
        removeWhiteSpaceTokens(tokens);
        printTokens(tokens);
        this.tokens = tokens.toArray(new Token[tokens.size()]);
        buildSyntaxTree(this.tokens); //PUT THIS BACK

    }

    /**
     * Goal 1: Assign variables
     * Structure: 'var' leftNode '=' rightNode ';'
     * leftNode: Identifier
     * rightNode: ID | NUMBER | Expression
     * <p/>
     * <p/>
     * Goal 2: Return mathematical computations
     * Structure: 'return' EXPRESSION ';'
     * <p/>
     * EXPRESSION = lhs root rhs
     * root: OP | ID
     * lhs:EXPR OP EXPR | '(' EXPR ')' | NUMBER
     * rhs:EXPR OP EXPR | '(' EXPR ')' | ID | NUMBER
     * <p/>
     * EXPRESSION = EXPR OP EXPR | '(' EXPR ')' | ID | NUMBER
     * ID = EXPR | ID | NUMBER
     */
    public void buildSyntaxTree(Token[] t) { //change to private
        Node root = parseGoal(t);
        printTree(root);
        Node computationNodes = parse_Computation(t);
        Evaluator e = new Evaluator(computationNodes);

    }

    /**
     * Creates the root node of potential varDecl or computation syntax trees, and prints out any possible exceptions that may arise during the
     * syntax tree creation process.
     *
     * @param t list of tokens to send to the varDelaration/compute methods
     * @return an empty node if parse_varDecl has, at any point, not recognized the syntax or format of the arrangement of tokens passed in.
     */
    public Node parseGoal(Token[] t) {
        //look for var
        //look for semicolons
        //keep calling vardecls until return

        Node varDecl = parse_Vardecl(t);
        Node compute = parse_Computation(t);

        //iterates through the token array, creates smaller arrays of VarDecls and computations, and evaluates each subarray.
        for(int i= 0;i<t.length;i++){
            if(t[i].getValue().equals(";")){
              Token[] subArrayOfProgram = Arrays.copyOfRange(t, i+1, t.length);
                Node subNode;
                if(subArrayOfProgram[0].getValue().equals("var")){
                    subNode = parse_Vardecl(subArrayOfProgram);
                }else if(subArrayOfProgram[0].getValue().equals("return")){
                    subNode = parse_Computation(subArrayOfProgram);
                }
            }
        }




        if (!varDecl.getToken().getClassification().equals("EXCEPTION")) {
            return varDecl;

        } else if (!compute.getToken().getClassification().equals("EXCEPTION")) {
            return compute;
        } else {
            if (varDecl.getToken().getClassification().equals("EXCEPTION")) {
                System.out.println("Syntax Error: " + varDecl.getToken().getValue());
            } else if (compute.getToken().getClassification().equals("EXCEPTION")) {
                System.out.println("Syntax Error: " + compute.getToken().getValue());
            }
            return null;
        }

    }

    /**
     * Goes through the tokens and checks if the user is declaring a variable.
     *
     * @param tokens an array of tokens to iterate through
     * @return null if the token does not match, returns  a new node if it does match
     */
    private Node parse_Vardecl(Token[] tokens) {

        Node correctAST,
                leftNode,
                rightNode,
                equalsNode;

        //First check symbols
        if (tokens.length == 0 && tokens[0].getValue().equals("var")) {
            return new Node(new Token("EXCEPTION", "'var' expected", false));

        }

        if (!tokens[tokens.length - 1].getValue().equals(";")) {
            return new Node(new Token("EXCEPTION", "; expected", false));

        }

        //Then check tokens
        if (parseLeft_VarDecl(tokens[1]) == null) {
            return new Node(new Token("EXCEPTION", "variable expected", false));

        }
        leftNode = parseLeft_VarDecl(tokens[1]);

        if (parseEquals(tokens[2]) == null) {
            return new Node(new Token("EXCEPTION", "'=' expected", false));
        }

        equalsNode = parseEquals(tokens[2]);

        if (parseRight_VarDecl(tokens[3]) == null) {
            return new Node(new Token("EXCEPTION", "Only Variables or number can be assigned to other variables", false));
        }
        rightNode = parseRight_VarDecl(tokens[3]);


        correctAST = equalsNode;
        correctAST.setLeftChild(leftNode);
        correctAST.setRightChild(rightNode);
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
            return new Node(new Token("EXCEPTION", "'return' expected", false));
        }

        if (!t[t.length - 1].getValue().equals(";")) {
            return new Node(new Token("EXCEPTION", "; expected", false));
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

    private Node parseRight_VarDecl(Token t) {
        Node rightNode = parseVariable(t);
        if (rightNode != null) {
            return rightNode;
        }
        rightNode = parseNumber(t);
        if (rightNode != null) {
            return rightNode;
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
    private Node parseEquals(Token t) {
        if (parseSymbol(t) != null && t.getValue().equals("=")) {
            return new Node(t);
        }
        return null;
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

    private Node parseSymbol(Token t) {
        if (t.getClassification().equals("SYMBOL")) {
            return new Node(t);
        }
        return null;
    }

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
        System.out.println("Printing Syntax Tree: ");
        if (root == null) return;
        Stack<Node> s = new Stack<>();
        Node current = root;
        while (true) {
            if (current != null) {
                if (current.getRightChild() != null) s.push(current.getRightChild());
                s.push(current);
                current = current.getLeftChild();
                continue;
            }
            if (s.isEmpty()) {
                System.out.println();
                return;
            }
            current = s.pop();
            if (current.getRightChild() != null && !s.isEmpty() && current.getRightChild() == s.peek()) {
                s.pop();
                s.push(current);
                current = current.getRightChild();
            } else {
                System.out.print(current.getToken().getValue() + " ");
                current = null;
            }
        }

    }

    /**
     * Takes a tree of variable declarations, iterates through in post-order fashion and creates a complex hashmap of tokens
     * @param root the root node of a tree of var decls.
     * @return a hashmap that maps variables to other variables or variables to numbers.
     */
    private HashMap<Token, Token> treeToMap(Node root){
        HashMap<Token, Token> map = new HashMap<>();//Key = Variable, Value = Variable/Number


        return map;
    }
    public static void printTokens(List<Token> tokens) {
        System.out.println("Tokens without whitespace: ");
        for (Token token : tokens) {
            System.out.println("Index: " + tokens.indexOf(token) + ", Classification: " + token.getClassification() + ", Value: " + token.getValue());
        }
    }
}
