package JavaAttempt;

/**
 * Created by michael on 5/11/16.
 *
 * The node of a syntax tree contains a value(token), a potential right leaf and a potential left leaf, which also might contain tokens,
 * right leaves and left leaves.
 */
public class Node {
    private Token token;
    private Node leftChild;
    private Node rightChild;

    public Node(Token token) {
        this.token = token;
    }
    public Node getLeftChild() {
        return leftChild;
    }
    public void setLeftChild(Node n) {
        leftChild = n;
    }


    public Node getRightChild() {
        return rightChild;
    }
    public void setRightChild(Node n) {
        rightChild = n;
    }

    public Token getToken() {
        return token;
    }
}