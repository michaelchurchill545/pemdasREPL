package JavaAttempt;

/**
 * Created by michael on 4/18/16.
 */
public class Main {
    /**
     * The program has 0 or more variable declarations with exactly one return statement
     * @param args
     */
    public static void main(String[] args){

       // Parser p = new Parser();
        System.out.println("PEMDAS REPL, Created by Michael Churchill.");

        System.out.println("To declare a variable, the Keyword is 'var' and the syntax is 'var someVariable = aNumberOrAnotherVariable;'.");
        System.out.println("Variables must not contain numbers.");
        System.out.println("To compute a mathematical operation, the keyword is 'return' and the syntax is 'return x+4;'");

        for(String string: args) {
            FiniteStateMachine fsm = new FiniteStateMachine(string);
           // fsm.tokenize(string + "\n");
        }


       // p.lexicallyAnalyze(args);
    }
}
