package JavaAttempt;

import java.util.List;

/**
 * Created by michael on 5/7/16.
 */
public class Tester {

    public static void main(String[] args) {


        String computeTest1 = "var a =5; var b = 3 + a; var c = a; return a ^( b + (c/12));";
        System.out.println("**********TESTING: "+ computeTest1);

                //"return (17*5)+201;";

        FiniteStateMachine fsm = new FiniteStateMachine(computeTest1);

    }


}
