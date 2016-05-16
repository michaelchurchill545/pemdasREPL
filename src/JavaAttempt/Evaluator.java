package JavaAttempt;

import clojure.lang.IFn;

import java.util.HashMap;
import java.util.Stack;

/**
 * Created by michael on 5/11/16.
 * <p/>
 * The Evaluator class does a post-order traversal of the tree and evaluates the expressions.
 */
public class Evaluator {
    private HashMap<String, Double> hashMap;

    public Evaluator(HashMap<String, Double> hashMap) {
        this.hashMap = hashMap;


    }
//
//    /**
//     * Iterates in a post order fashion and evaluates the left child, (sub)root, and then right child.
//     */
//    public Node evaluateTree(Node root) {
//        if (root == null) return root;
//
//        Stack<Node> nodeStack = new Stack<>();
//        Node current = root;
//        Node op = null;
//        Node valLeftMost = null;
//        Node valRightMost = null;
//        while (true) {
//            //iterates down to right child
//            if (current != null) {
//                if (current.getRightChild() != null) {
//                    //pushes the right child on the stack
//                    nodeStack.push(current.getRightChild());
//                }
//
//                nodeStack.push(current);
//                //iterate down to the next left child
//                current = current.getLeftChild();
//                continue;
//            }
//            //if there isn't anything left in the stack after calls to push left or right children
//            if (nodeStack.isEmpty()) {
//                //we're done here
//                return root;
//            }
//
//            //set current to the top of the stack(most likely an operator)
//            current = nodeStack.pop();
//            //checks if right child exists and is at the top of the stack
//            if (current.getRightChild() != null && !nodeStack.isEmpty() && current.getRightChild() == nodeStack.peek()) {
//                //remove right child
//                nodeStack.pop();
//                //push the new one on the stack
//                nodeStack.push(current);
//                //iterate down to the next right child
//                current = current.getRightChild();
//            } else {
////
//                System.out.println("Token at the top of the stack: " + current.getToken().getValue()); //testValue
//                if (current.getToken().getClassification().equals("OPERATOR")) {
//                    op = current;
//                } else if (valLeftMost == null && current.getLeftChild() == null && !current.getToken().getClassification().equals("OPERATOR")) {
//                    valLeftMost = current;
//                } else if (valRightMost == null && valLeftMost != null && !current.getToken().getClassification().equals("OPERATOR")) {
//                    valRightMost = current;
//                }
//
//                if (op != null && valLeftMost != null && valRightMost != null) {
//                    nodeStack.push(new Node(compute(valLeftMost, valRightMost, op)));
//                    valLeftMost = null;
//                    valRightMost = null;
//                    op = null;
//                }
//                current = null;
//            }
//        }
//    }

    public double evalToBeUsed(Node current) {

        if (current.getToken().getClassification().equals("OPERATOR")) {
            double leftValue = evalToBeUsed(current.getLeftChild());
            double rightValue = evalToBeUsed(current.getRightChild());
            return Double.parseDouble(compute(leftValue, rightValue, current).getToken().getValue());
        } else if (current.getToken().getClassification().equals("VARIABLE")) {
            String varName = current.getToken().getValue();
            return hashMap.get(varName);

        } else {
            return Double.parseDouble(current.getToken().getValue());
        }
    }
//    }
//    private double checkIfVar(Node n) {
//        if (n.getToken().getClassification().equals("VARIABLE")) {
//            String varName = n.getToken().getValue();
//            return hashMap.get(varName);
//        } else if (n.getToken().getClassification().equals("NUMBER")) {
//            return Double.parseDouble(n.getToken().getValue());
//        }
//        throw new RuntimeException("Variable " + n.getToken().getValue() + " does not exist");
//    }
//

    /**
     * PARSE ALL NUM VARIABLES TO FLOATS
     *
     * @param value1   the number in the left child node
     * @param value2   the number in the right child node
     * @param operator the operator that is also the sub tree root
     * @return the double value computed.
     */
    private Node compute(double value1, double value2, Node operator) {

        System.out.print(value1 + operator.getToken().getValue() + value2 + " evaluated, ");//for testing
        switch (operator.getToken().getValue()) {
            case"sin":

                break;

            case "tan":
                break;
            case "cos":
                break;
            case"cot":
                break;

            case "^":
                Token valExponent = new Token("NUMBER", String.valueOf(Math.pow(value1, value2)), true);
                System.out.println(valExponent.getValue() + " Returned");
                return new Node(valExponent);

            case "*":
                Token valMultiplication = new Token("NUMBER", String.valueOf(value1 * value2), true);
                System.out.println(valMultiplication.getValue() + " Returned");
                return new Node(valMultiplication);


            case "/":
                Token valDivision = new Token("NUMBER", String.valueOf(value1 / value2), true);
                System.out.println(valDivision.getValue() + " Returned");
                return new Node(valDivision);

            case "+":
                Token valAddition = new Token("NUMBER", String.valueOf(value1 + value2), true);
                System.out.println(valAddition.getValue() + " Returned");
                return new Node(valAddition);


            case "-":
                Token valSubtraction = new Token("NUMBER", String.valueOf(value1 - value2), true);
                System.out.println(valSubtraction.getValue() + " Returned");
                return new Node(valSubtraction);

        }

        throw new RuntimeException(value1 + " " + operator.getToken().getValue() + " " + value2 + " could not be computed:");
    }
}


