# pemdasREPL


When giving commands to the REPL from the command line, the format as follows:

Program: 0 or more var declarations and exactly 1 computation.

variable declaration: "var" variableName "=" numberOrAnotherVariableOrExpression ";"

computation: "return" expression ";"

example of program: 

var test1 = 2; var test2 = test1; return test1 + test2;
Evaluated: 2.0+2.0 Returned: 4.0

Current Functionality:

Error Handling = FUNCTIONAL
Proper Variable Handling: FUNCTIONAL
PEMDAS presedence: FUNCTIONAL
Trig optimization: NOT FUNCTIONAL
Computation handling = FUNCTIONAL
Graphing = NOT FUNCTIONAL
