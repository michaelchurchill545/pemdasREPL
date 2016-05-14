package JavaAttempt;

/**
 * Created by michael on 4/27/16.
 */
public class Token {
    private String classification;
   private  String value;
    private boolean isTerminal;

 //   private int priority;

    public Token(String classification, String value, boolean isTerminal) {
        //this.priority=priority;
        this.classification = classification;
        this.value = value;
        this.isTerminal=isTerminal;
    }
//public int getPriority(){return priority;}
    public String getClassification() {
        return classification;
    }


    public String getValue() {
        return value;
    }

    public boolean isTerminal(){return isTerminal;}
}
