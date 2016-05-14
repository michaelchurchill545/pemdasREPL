package JavaAttempt;

import java.util.PriorityQueue;
import java.util.Stack;

/**
 * Created by michael on 5/11/16.
 * <p/>
 * The Evaluator class does a post-order traversal of the tree and evaluates the expressions.
 */
public class Evaluator {
    public Evaluator(Node syntaxTreeRoot) {
        postorderIter(syntaxTreeRoot);
    }

    /**
     * Iterates in a post order fashion and evaluates the left child, (sub)root, and then right child.
     *
     *
     *
     * @param root
     */
    public void postorderIter(Node root) {
        if (root == null) return;

        Stack<Node> nodeStack = new Stack<>();
        Node current = root;
        Node op = null;
        Node valLeftMost = null;
        Node valRightMost = null;
        while (true) {
            //iterates down to right child
            if (current != null) {
                if (current.getRightChild() != null) {
                    //pushes the right child on the stack
                    nodeStack.push(current.getRightChild());
                }
                nodeStack.push(current);
                //iterate down to the next left child
                current = current.getLeftChild();
                continue;
            }
            //if there isn't anything left in the stack after calls to push left or right children
            if (nodeStack.isEmpty()) {
                //we're done here
                return;
            }

            //set current to the top of the stack(most likely an operator)
            current = nodeStack.pop();
            //checks if right child exists and is at the top of the stack
            if (current.getRightChild() != null && !nodeStack.isEmpty() && current.getRightChild() == nodeStack.peek()) {
                //remove right child
                nodeStack.pop();
                //push the new one on the stack
                nodeStack.push(current);
                //iterate down to the next right child
                current = current.getRightChild();
            } else {

                System.out.println("Token at the top of the stack: " + current.getToken().getValue()); //testValue
                if(current.getToken().getClassification().equals("OPERATOR")){
                    op = current;
                }else if(valLeftMost==null&&current.getLeftChild()==null && !current.getToken().getClassification().equals("OPERATOR")){
                    valLeftMost = current;
                }else if(valRightMost==null&&valLeftMost!=null&&!current.getToken().getClassification().equals("OPERATOR")){
                    valRightMost = current;
                }

                if(op!=null&&valLeftMost!=null&&valRightMost!=null){
                    nodeStack.push(new Node(compute(valLeftMost, valRightMost, op)));
                    valLeftMost=null;
                    valRightMost=null;
                    op=null;
                }
                current = null;
            }
        }
    }

    /**
     * PARSE ALL NUM VARIABLES TO FLOATS
     *
     * @param value1 the number in the left child node
     * @param value2 the number in the right child node
     * @param operator the operator that is also the sub tree root
     * @return the double value computed.
     */
    private Token compute(Node value1, Node value2, Node operator) {
        double d1 = Double.parseDouble(value1.getToken().getValue());
        double d2 = Double.parseDouble(value2.getToken().getValue());
        System.out.print(d1 + operator.getToken().getValue() + d2 + " evaluated, ");//for testing
        switch (operator.getToken().getValue()) {
            case "^":
                Token valExponent = new Token("NUMBER", String.valueOf(Math.pow(d1, d2)), true);
                System.out.println(valExponent.getValue() + " Returned");
                return valExponent;

            case "*":
                Token valMultiplication = new Token("NUMBER", String.valueOf(d1 * d2), true);
                System.out.println(valMultiplication.getValue() + " Returned");
                return valMultiplication;


            case "/":
                Token valDivision = new Token("NUMBER", String.valueOf(d1/d2), true);
                System.out.println(valDivision.getValue() + " Returned");
                return valDivision;

            case "+":
                Token valAddition = new Token("NUMBER", String.valueOf(d1 + d2), true);
                System.out.println(valAddition.getValue() + " Returned");
                return valAddition;


            case "-":
                Token valSubtraction = new Token("NUMBER", String.valueOf(d1 - d2), true);
                System.out.println(valSubtraction.getValue() + " Returned");
                return valSubtraction;
        }

        throw new RuntimeException(d1 +" " + operator.getToken().getValue() + " " + d2+" could not be computed:");
    }

}
