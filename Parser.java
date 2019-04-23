import java.io.FileNotFoundException;
import java.util.HashMap;

public class Parser {
    //variables
    private String res;
    int[] counters = new int[4]; //[0]: '(' in res, [1]: res ')' in res, [2]: literal, [3]: numeric

    //Init
    LexicalAnalyzer LA = new LexicalAnalyzer();
    public static EvalTree d_list = new EvalTree();
    public HashMap<String, Integer> defunMap = new HashMap<>();
    public Parser() throws Exception {
        LA.Init();
        //System.out.println("..... Start to parse .....");
    }

    //check local list and add ')'
    public void localParenthesis(int localList) {
        for(int i = 0; i < localList; i++) {
            counters[1] += 1;
            res += ")";
        }
    }

    //check nil position and add left nil
    public boolean checkLeftNil(boolean nilExist) {
        if(LA.GetCurrent().equals(")") && !LA.GetCurrent().equals("ERROR")) {
            res += "NIL";
            nilExist = false;
        }

        return nilExist;
    }

    //add right nil in list
    public void addRightChild() {
        if(!LA.GetCurrent().equals("ERROR")) {
            res += "NIL";
        }
    }

    //print parseTree
    public void showTree(EvalTree root) {
        if(root.left == null && root.right == null) {
            System.out.print(root.value);
        } else {
            System.out.print("(");

            showTree(root.left);
            System.out.print(" . ");
            showTree(root.right);

            System.out.print(")");
        }
    }

    //list notation
    public void listNotation(EvalTree root) {
        if(root == null) {
            return;
        }

        if(root.left == null && root.right == null) {
            System.out.print(root.value);
            return;
        }

        boolean left = true;
        while (root != null) {
            if(root.left != null || root.right != null) {
                if(left) {
                    System.out.print("(");
                    left = false;
                }

                listNotation(root.left);

                if(root.right != null) {
                    EvalTree temp = root.right;
                    if(temp.left != null || temp.right != null) {
                        System.out.print(" ");
                    }
                }
            } else if(root.value.equals("NIL")) {
                //right leaf node
                System.out.print(")");
            } else {
                System.out.print(" . ");
                System.out.print(root.value + ")");
            }

            root = root.right;
        }
    }

    //Parse Expr
    public void ParseExpr(EvalTree root) {
        //System.out.println("Expr start: " + LA.GetCurrent());
        if(LA.GetCurrent().equals("")) {
            //System.out.println(counters[0] + " " + counters[1]);
            if(counters[0] > counters[1]) {
                System.out.println("ERROR: Invalid Input for lacking ')'");
                System.exit(0);
            } else if(counters[0] < counters[1]) {
                System.out.println("ERROR: Invalid Input for lacking '('");
                System.exit(0);
            } else {
                System.out.println("ERROR: Invalid Input for grammar issue");
                System.exit(0);
            }
        }

        if(LA.GetCurrent().equals(")") && !LA.GetCurrent().equals("ERROR")) {
            System.out.println("ERROR: Invalid Input for lacking '('");
            System.exit(0);

        } else if(LA.GetCurrent().equals("(") && !LA.GetCurrent().equals("ERROR")) {
            //System.out.println("In '(' case: ");
            LA.MoveToNext(); //move to next token
            //System.out.println("current: " + LA.GetCurrent());
            if(LA.GetCurrent().equals("") || (LA.GetCurrent().equals("ERROR") && LA.getInvalidStr().length() == 0)) {
                System.out.println("ERROR: Invalid Input for lacking ')'");
                System.exit(0);
            }

            boolean nilExist = true; //nil exist or not
            //check nil and add left nil
            nilExist = checkLeftNil(nilExist);

            int localList = 0;
            while(!LA.GetCurrent().equals(")") && !LA.GetCurrent().equals("ERROR")) {
                counters[0] += 1;
                res += "(";
                localList++;

                root.left = new EvalTree();
                root.right = new EvalTree();

                ParseExpr(root.left);

                root = root.right;

                //System.out.println("Out inner Expr: " + LA.GetCurrent());
                res += " . ";
            }

            if(LA.GetCurrent().equals("ERROR")) {
                //System.out.println("Check_1");
                String invalidInput = LA.getInvalidStr();
                if(invalidInput.length() != 0) {
                    System.out.println("ERROR: Invalid token " + invalidInput);
                } else {
                    System.out.println("ERROR: Invalid token");
                }

                System.exit(0);
            }

            //add right nil in list
            if(nilExist) {
                addRightChild();
            }

            //check closing parenthesis
            if(localList > 0) {
                localParenthesis(localList);
            }

            //move to next token
            LA.MoveToNext();

//            if(LA.GetCurrent().equals("ERROR")) {
//                System.out.println("ERROR: Invalid token");
//                System.exit(0);
//            }

        } else if(LA.GetCurrent().charAt(0) >= '0' && LA.GetCurrent().charAt(0) <= '9'
                || LA.GetCurrent().charAt(0) >= 'A' && LA.GetCurrent().charAt(0) <= 'Z'
                && !LA.GetCurrent().equals("ERROR")) {
            //System.out.println("In atoms case: " + LA.GetCurrent());
            res += LA.GetCurrent();

            root.value = LA.GetCurrent();

            LA.MoveToNext();

            if(LA.GetCurrent().equals("ERROR")) {
//                if(res.length() != 0) {
//                    System.out.println(res);
//                }
//                System.out.println("ERROR: Invalid token");
//                System.exit(0);
                return;
            }

        } else {
            System.out.println("ERROR: Some ERROR occur");
            System.exit(0);
        }
    }

    //Parse Start
    public void ParseStart() throws FileNotFoundException {
        //System.out.println(LA.GetCurrent());
        //Check empty input
        if(LA.GetCurrent().equals("EOF") ){
            System.out.println("ERROR: Empty input string.");
            System.exit(0);
        }
        //Check ERROR input
//        if(LA.GetCurrent().equals("ERROR")) {
//            System.out.println("ERROR: Invalid Token");
//            System.exit(0);
//        }

        while(!LA.GetCurrent().equals("") && !LA.GetCurrent().equals("EOF")) {
            //Check ERROR input
            if(LA.GetCurrent().equals("ERROR")) {
                //System.out.println("ERROR: Invalid Token");

                String invalidInput = LA.getInvalidStr();
                if(invalidInput.length() != 0) {
                    System.out.println("ERROR: Invalid token " + invalidInput);
                } else {
                    System.out.println("ERROR: Invalid token");
                }

                System.exit(0);
            }

            //System.out.println("*** Start Loop ***");

            //initialize variable start
            res = "";
            for(int i = 0; i < counters.length; i++) {
                counters[i] = 0;
            }
            //initialize variable end

            EvalTree root = new EvalTree();
            ParseExpr(root);

            if(counters[0] != counters[1]) {
                System.out.println("ERROR: Invalid input for '(' and ')'");
            }

            //System.out.println("--- project 2 ---");
            //System.out.println(res);
            //System.out.println("--- project 3 ---");
            //show parseTree
//            showTree(root);
//            System.out.print("\n");
            EvalTree a_list = new EvalTree();
            root = root.evaluate(root, a_list, d_list, defunMap);
            //list notation
            listNotation(root);
            System.out.print("\n");
            //list notation
        }

        //System.out.println("..... End of parse .....");
    }

    public static void main(String[] args) throws Exception {
        Parser parse = new Parser();
        parse.ParseStart();
    }

}
