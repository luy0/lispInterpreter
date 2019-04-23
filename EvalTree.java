//import com.sun.org.apache.xml.internal.security.Init;
import java.util.*;
import java.util.stream.Collectors;

public class EvalTree {
    //variables
    EvalTree left, right;
    String value;
    HashMap<String, Integer> map = new HashMap<>();
    String[] len2 = new String[]{"ATOM", "INT", "NULL", "CAR", "CDR", "QUOTE"};
    String[] len3 = new String[]{"PLUS", "MINUS", "TIMES", "LESS", "GREATER", "EQ", "CONS"};
    String[] len4 = new String[]{"COND", "DEFUN"};

    List<String> defaultName = Arrays.asList("T", "NIL", "CAR", "CDR", "CONS", "ATOM", "EQ", "NULL", "INT", "PLUS",
                                             "MINUS", "TIMES", "LESS", "GREATER", "COND", "QUOTE", "DEFUN");

    HashMap<String, Integer> defunMap = new HashMap<>();

    //init
    public EvalTree() {
        this.left = null;
        this.right = null;
        this.value = "NIL";

        for(int i = 0; i < len2.length; i++) {
            map.put(len2[i], 2);
        }

        for(int i = 0; i < len3.length; i++) {
            map.put(len3[i], 3);
        }

        for(int i = 0; i < len4.length; i++) {
            map.put(len4[i], 4);
        }
    }

    //get length
    public int getLen(EvalTree root) {
        int len = 0;
        while(root.left != null && root.right != null) {
            root = root.right;
            len += 1;
        }

        //check
        if(!root.value.equals("NIL")) {
            System.out.print("ERROR: Invalid S-express");
            System.out.print("\n");
            System.exit(0);
        }

        return len;
    }

    //function name Check
    public boolean funNameCheck(EvalTree root) {
        boolean valid = true;
        if(defaultName.contains(root.value)) {
            valid = false;
            return valid;
        }

        return valid;
    }

    //check function name is literal
    public boolean literal(EvalTree root) {
        boolean valid = true;
        if(integer(root).value.equals("T")) {
            valid = false;
            return  valid;
        }

        if(!atom(root).value.equals("T")) {
            valid = false;
            return valid;
        }

        return valid;
    }

    //function name check
    public void funNameValid(EvalTree funName) {
        if(!funNameCheck(funName) || !literal(funName)) {
            System.out.printf("ERROR: %s is invalid because it is a default function or invalid parameter name", funName.value);
            System.out.print("\n");
            System.exit(0);
        }
    }

    //check function parameters name
    public void funParaCheck(EvalTree root) {
        Set<String> set = new HashSet<>();

        int size = getLen(root);
        while(size-- > 0) {
            EvalTree currentName = car(root);

            if(set.contains(currentName.value)) {
                System.out.printf("ERROR: %s is invalid because this name has existed", currentName.value);
                System.out.print("\n");
                System.exit(0);
            }

            set.add(currentName.value);

            funNameValid(currentName);

            root = cdr(root);
        }
    }

    //bound: check whether it contains a pair (x . d_list)
    public boolean bound(EvalTree x, EvalTree d_list) {
        boolean pairExist = false;

        if(nullNode(d_list).value.equals("T")) {
            return pairExist;
        }

        if(car(car(d_list)).value.equals(x.value)) {
            pairExist = true;
        } else {
            pairExist = bound(x, cdr(d_list));
        }

        return pairExist;
    }

    //getval
    public EvalTree getval(EvalTree funName, EvalTree d_list) {
        EvalTree res = new EvalTree();

        if(funName.value.equals(car(car(d_list)).value)) {
            res = cdr(car(d_list));
            return res;
        }

        res = getval(funName, cdr(d_list));

        return res;
    }

    //addpairs
    public EvalTree addpairs(EvalTree paraName, EvalTree x, EvalTree a_list) {
        EvalTree res = new EvalTree();

        if(getLen(paraName) != getLen(x)) {
            System.out.println("ERROR: number of parameters mismatch");
            System.exit(0);
        }

        if(nullNode(paraName).value.equals("T")) {
            return a_list;
        }

        res = cons(cons(car(paraName), car(x)), addpairs(cdr(paraName), cdr(x), a_list));

        return res;
    }


    //evlist
    public EvalTree evlist(EvalTree x, EvalTree a_list, EvalTree d_list, HashMap<String, Integer> defunMap) {
        EvalTree evaluation = new EvalTree();
        if(nullNode(x).value.equals("T")) {
            evaluation.value = "NIL";
            return evaluation;
        }

        evaluation = cons(evaluate(car(x), a_list, d_list, defunMap), evlist(cdr(x), a_list, d_list, defunMap));

        return evaluation;
    }

    //apply
    public EvalTree apply(EvalTree funName, EvalTree x, EvalTree a_list, EvalTree d_list, HashMap<String, Integer> defunMap) {
        EvalTree res = new EvalTree();
        EvalTree operation = new EvalTree();
        EvalTree pairs = new EvalTree();

        pairs = addpairs(car(getval(funName, d_list)), x, a_list);
        operation = cdr(getval(funName, d_list));
        
        res = evaluate(operation, pairs, d_list, defunMap);

        return res;
    }

    //*** Lisp functions start ***
    //ATOM
    public EvalTree atom(EvalTree root) {
        if(root == null) {
            System.out.println("ERROR: Invalid ATOM because s1 is null");
            System.exit(0);
        }

        EvalTree node = new EvalTree();
        if(root.left == null && root.right == null) {
            node.value = "T";
        }

        return node;
    }

    //INT
    public EvalTree integer(EvalTree root) {
        if(root == null) {
            System.out.println("ERROR: Invalid INT because s1 is null");
            System.exit(0);
        }

        EvalTree node = new EvalTree();

        if(atom(root).value.equals("T")) {
            char[] ch = root.value.toCharArray();

            if(ch.length == 1 && (ch[0] == '+' || ch[0] == '-')) {
                return  node;
            }

            for(int i = 0; i < ch.length; i++){
                if(i == 0) {
                    if((ch[0] == '+' || ch[0] == '-')) {
                        continue;
                    }

                }

                if(ch[i] >= '0' && ch[i] <= '9'){
                    continue;
                }

                return node;
            }

            node.value = "T";
        }

        return node;
    }

    //CAR
    public EvalTree car(EvalTree root) {
        if(root == null) {
            System.out.println("ERROR: Invalid CAR because s1 is null");
            System.exit(0);
        }

        EvalTree node = new EvalTree();
        if(root.left != null || root.right != null) {
            node = root.left;
        } else {
            System.out.println("ERROR: Invalid CAR because S-expression is not a list");
            System.exit(0);
        }

        return node;
    }

    //CDR
    public EvalTree cdr(EvalTree root) {
        if(root == null) {
            System.out.println("ERROR: Invalid CDR because s1 is null");
            System.exit(0);
        }

        EvalTree node = new EvalTree();
        if(root.left != null || root.right != null) {
            node = root.right;
        } else {
            System.out.println("ERROR: Invalid CDR because S-expression is not a list");
            System.exit(0);
        }

        return node;
    }

    //NULL
    public EvalTree nullNode(EvalTree root) {
        if(root == null) {
            System.out.println("ERROR: Invalid NULL because s1 is null");
            System.exit(0);
        }

        EvalTree node = new EvalTree();
        if(atom(root).value.equals("T")) {
            if(root.value.equals("NIL")) {
                node.value = "T";
            }
        }

        return node;
    }

    //CONS
    public EvalTree cons(EvalTree root1, EvalTree root2) {
        if(root1 == null || root2 == null) {
            System.out.println("ERROR: Invalid CONS because s1 or s2 is null");
            System.exit(0);
        }

        EvalTree node = new EvalTree();
        node.left = root1;
        node.right = root2;

        return node;
    }

    //PLUS
    public EvalTree plus(EvalTree root1, EvalTree root2) {
        if(root1 == null || root2 == null) {
            System.out.println("ERROR: Invalid PLUS because s1 or s2 is null");
            System.exit(0);
        }

        if(!integer(root1).value.equals("T") || !integer(root2).value.equals("T")) {
            System.out.println("ERROR: Invalid PLUS because s1 or s2 is not numeric");
            System.exit(0);
        }

        EvalTree node = new EvalTree();
        int temp = Integer.parseInt(root1.value) + Integer.parseInt(root2.value);
        node.value = Integer.toString(temp);

        return node;
    }

    //MINUS
    public EvalTree minus(EvalTree root1, EvalTree root2) {
        if(root1 == null || root2 == null) {
            System.out.println("ERROR: Invalid MINUS because s1 or s2 is null");
            System.exit(0);
        }

        if(!integer(root1).value.equals("T") || !integer(root2).value.equals("T")) {
            System.out.println("ERROR: Invalid MINUS because s1 or s2 is not numeric");
            System.exit(0);
        }

        EvalTree node = new EvalTree();
        int temp = Integer.parseInt(root1.value) - Integer.parseInt(root2.value);
        node.value = Integer.toString(temp);

        return node;
    }

    //TIMES
    public EvalTree times(EvalTree root1, EvalTree root2) {
        if(root1 == null || root2 == null) {
            System.out.println("ERROR: Invalid TIMES because s1 or s2 is null");
            System.exit(0);
        }

        if(!integer(root1).value.equals("T") || !integer(root2).value.equals("T")) {
            System.out.println("ERROR: Invalid TIMES because s1 or s2 is not numeric");
            System.exit(0);
        }

        EvalTree node = new EvalTree();
        int temp = Integer.parseInt(root1.value) * Integer.parseInt(root2.value);
        node.value = Integer.toString(temp);

        return node;
    }

    //LESS
    public EvalTree less(EvalTree root1, EvalTree root2) {
        if(root1 == null || root2 == null) {
            System.out.println("ERROR: Invalid LESS because s1 or s2 is null");
            System.exit(0);
        }

        if(!integer(root1).value.equals("T") || !integer(root2).value.equals("T")) {
            System.out.println("ERROR: Invalid LESS because s1 or s2 is not numeric");
            System.exit(0);
        }

        EvalTree node = new EvalTree();
        if(Integer.parseInt(root1.value) < Integer.parseInt(root2.value)) {
            node.value = "T";
        }

        return node;
    }

    //GREATER
    public EvalTree greater(EvalTree root1, EvalTree root2) {
        if(root1 == null || root2 == null) {
            System.out.println("ERROR: Invalid GREATER because s1 or s2 is null");
            System.exit(0);
        }

        if(!integer(root1).value.equals("T") || !integer(root2).value.equals("T")) {
            System.out.println("ERROR: Invalid GREATER because s1 or s2 is not numeric");
            System.exit(0);
        }

        EvalTree node = new EvalTree();
        if(Integer.parseInt(root1.value) > Integer.parseInt(root2.value)) {
            node.value = "T";
        }

        return node;
    }

    //EQ
    public EvalTree eq(EvalTree root1, EvalTree root2) {
        if(root1 == null || root2 == null) {
            System.out.println("ERROR: Invalid EQ because s1 or s2 is null");
            System.exit(0);
        }

        if((!integer(root1).value.equals("T") && !atom(root1).value.equals("T")) ||
                (!integer(root2).value.equals("T") && !atom(root2).value.equals("T"))) {
            System.out.println("ERROR: Invalid EQ because s1 or s2 is not an atom");
            System.exit(0);
        }

        EvalTree node = new EvalTree();
        if(root1.value.equals(root2.value)) {
            node.value = "T";
        }

        return node;
    }

    //COND
    public EvalTree cond(EvalTree root, EvalTree a_list, EvalTree d_list, HashMap<String, Integer> defunMap) {
        if(getLen(root) < 2) {
            System.out.printf("ERROR: Eval(COND) undefined because its length < 2", car(root).value);
            System.out.print("\n");
            System.exit(0);
        }

        EvalTree node = new EvalTree();

        while(getLen(root) >= 2) {
            EvalTree condLists = cdr(root); // lists
            EvalTree Si = car(condLists);

            if(getLen(Si) != 2) {
                System.out.printf("ERROR: Invalid eval(%s) because length of Si must be 2", car(root).value);
                System.out.print("\n");
                System.exit(0);
            }

            EvalTree Bi = car(Si), Ei = car(cdr(Si));
            //fix for project 3
            if(!evaluate(Bi, a_list, d_list, defunMap).value.equals("T") &&
                    evaluate(Bi, a_list, d_list, defunMap).left == null && evaluate(Bi, a_list, d_list, defunMap).right == null) {

                root = condLists;

            } else {
                return Ei;
            }
            //fix for project 3

//            //project 3 error occurs
//            if(evaluate(Bi).value.equals("T")) {
//                return Ei;
//            } else {
//                if(evaluate(Bi).left == null && evaluate(Bi).right == null) {
//                    root = condLists;
//                }
//            }
        }

        if(getLen(root) < 2) {
            System.out.printf("ERROR: Eval(COND) undefined because no available Si exists ", car(root).value);
            System.out.print("\n");
            System.exit(0);
        }

        return node;
    }

    public EvalTree defun(EvalTree root, EvalTree a_list, EvalTree d_list, HashMap<String, Integer> defunMap) {
        EvalTree funTemp = cdr(root);
        EvalTree funName = car(funTemp);
        EvalTree funPara = car(cdr(funTemp));
        EvalTree funBody = car(cdr(cdr(funTemp)));

        //check function name valid
        funNameValid(funName);

        //check parameters name valid
        funParaCheck(funPara);

        EvalTree function = cons(funName, cons(funPara, funBody));
        Parser.d_list = cons(function, d_list);

        defunMap.put(funName.value, 5);

        return funName;
    }
    //*** Lisp functions end ***

    //evaluate
    public EvalTree evaluate(EvalTree root, EvalTree a_list, EvalTree d_list, HashMap<String, Integer> defunMap) {
        EvalTree list = new EvalTree();

        if(!atom(root).value.equals("T")) {
            //root is a list
            EvalTree carNode = new EvalTree(), cdrNode = new EvalTree();
            EvalTree s1 = new EvalTree(), s2 = new EvalTree();

            if(getLen(root) >= 3) {
                carNode = car(root);
                cdrNode = cdr(root);
                s1 = car(cdrNode);
                s2 = car(cdr(cdrNode));
            } else if(getLen(root) >= 2) {
                carNode = car(root);
                cdrNode = cdr(root);
                s1 = car(cdrNode);
            } else if(getLen(root) >= 1) {
                carNode = car(root);
            }

            //check function
            String carValue =  carNode.value;
            int validLen;
            if(!carValue.equals("NIL") && map.containsKey(carValue)) {
                validLen = map.get(carValue);
            } else if(defunMap.containsKey(carValue)) {
                validLen = defunMap.get(carValue);
            } else {
                validLen = 5;
            }

            //function selection
            switch(validLen) {
                case 2:
                    if(getLen(root) != 2) {
                        System.out.printf("ERROR: Eval(%s) is undefined because length != 2", carValue);
                        System.out.print("\n");
                        System.exit(0);
                    } else {
                        if(carValue.equals("CAR")) {
                            list = car(evaluate(s1, a_list, d_list, defunMap));
                        } else if(carValue.equals("CDR")) {
                            list = cdr(evaluate(s1, a_list, d_list, defunMap));
                        } else if(carValue.equals("QUOTE")) {
                            list = s1;
                        } else if(carValue.equals("ATOM")) {
                            list = atom(evaluate(s1, a_list, d_list, defunMap));
                        } else if(carValue.equals("INT")) {
                            list = integer(evaluate(s1, a_list, d_list, defunMap));
                        } else if(carValue.equals("NULL")) {
                            list = nullNode(evaluate(s1, a_list, d_list, defunMap));
                        }
                    }
                    break;
                case 3:
                    if(getLen(root) != 3) {
                        System.out.printf("ERROR: Eval(%s) is undefined because length != 3", carValue);
                        System.out.print("\n");
                        System.exit(0);
                    } else {
                        if(carValue.equals("CONS")) {
                            list = cons(evaluate(s1, a_list, d_list, defunMap), evaluate(s2, a_list, d_list, defunMap));
                        } else if(carValue.equals("PLUS")) {
                            list = plus(evaluate(s1, a_list, d_list, defunMap), evaluate(s2, a_list, d_list, defunMap));
                        } else if(carValue.equals("MINUS")) {
                            list = minus(evaluate(s1, a_list, d_list, defunMap), evaluate(s2, a_list, d_list, defunMap));
                        } else if(carValue.equals("TIMES")) {
                            list = times(evaluate(s1, a_list, d_list, defunMap), evaluate(s2, a_list, d_list, defunMap));
                        } else if(carValue.equals("LESS")) {
                            list = less(evaluate(s1, a_list, d_list, defunMap), evaluate(s2, a_list, d_list, defunMap));
                        } else if(carValue.equals("GREATER")) {
                            list = greater(evaluate(s1, a_list, d_list, defunMap), evaluate(s2, a_list, d_list, defunMap));
                        } else if(carValue.equals("EQ")) {
                            list = eq(evaluate(s1, a_list, d_list, defunMap), evaluate(s2, a_list, d_list, defunMap));
                        }
                    }
                    break;
                case 4:
                    if(carValue.equals("COND")) {
                        list = evaluate(cond(root, a_list, d_list, defunMap), a_list, d_list, defunMap);
                    } else if(carValue.equals("DEFUN")) {
                        if(getLen(root) != 4) {
                            System.out.printf("ERROR: Eval(%s) is undefined because length != 4", carValue);
                            System.out.print("\n");
                            System.exit(0);
                        }

                        list = defun(root, a_list, d_list, defunMap);
                    }
                    break;
                case 5:
                    if(bound(carNode, d_list) == false) {
                        System.out.printf("ERROR: Eval(%s) is undefined", carNode.value);
                        System.out.print("\n");
                        System.exit(0);
                    }

                    funNameValid(car(root));

                    list = apply(car(root), evlist(cdr(root), a_list, d_list, defunMap), a_list, d_list, defunMap);
                    break;
                default:
                    System.out.printf("ERROR: Eval(%s) is undefined", root.value);
                    System.out.print("\n");
                    System.exit(0);
            }
        } else {
            if(integer(root).value.equals("T") || root.value.equals("NIL") || root.value.equals("T")) {
                list = root;
            } else if(bound(root, a_list)) {
                list = getval(root, a_list);
            } else {
                System.out.printf("ERROR: Eval(%s) is undefined because s is not INT or NIL or T", root.value);
                System.out.print("\n");
                System.exit(0);
            }
        }

        return list;
    }
}
