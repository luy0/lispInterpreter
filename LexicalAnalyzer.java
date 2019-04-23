import java.io.*;
import java.util.*;

//*** Token's Type ***
//Terminal: {literalAtom, numericAtom, openParenthesis, closingParenthesis, EOF}
//ERROR
//*** Token's Type ***

public class LexicalAnalyzer {
    private String inString, curToken, tokenValue;
    private int index, preIndex;

    //Initialize
    public void Init() throws Exception {

        Scanner read = new Scanner(System.in);
        //Scanner read = new Scanner(new FileReader("test_1.txt"));
        //Create the inString
        String inStr = "";
        while (read.hasNext()) {
            String inLine = read.nextLine().trim();

            inStr += inLine + " ";
        }
        //System.out.println(inStr);

        inString = inStr;

        //fix for project2 error
        if(inString.length() == 0) {
            System.out.println("ERROR: Empty input string");
            System.exit(0);
        }

        if(index < inString.length() && inString.charAt(index) == ' '
                || inString.charAt(index) == '\n' || inString.charAt(index) == '\r') {
            int nextPosition = getPosition2("", "", inString, index);
            index = nextPosition;
            //System.out.println("index move to: " + index);
        }
        //fix for project2 error

        curToken = getNextToken2(inString, "", index);
        index = getPosition2(curToken, "", inString, index);

        if(curToken.equals("EOF")) {
            tokenValue = "EOF";
        }

        //System.out.println("======= Init Start =======");
        //System.out.println("curToken: " + curToken);
        //System.out.println("tokenVale: " + tokenValue);
        //System.out.println("index: " + index);
        //System.out.println("======== Init End ========");
    }

    //Get current token
    public String GetCurrent() {
        //System.out.println("curToken: " + curToken);
        //System.out.println("tokenValue: " + tokenValue);

        if(curToken.equals("EOF")) {
            tokenValue = "EOF";
        }

        return tokenValue;
    }

    //Move to next
    public void MoveToNext() {
        //inString = inString.substring(index);
        //System.out.println("InString in Move: " + inString);
        //System.out.println("index: " + index);

        //deal with ' ', '\n', and '\r'
        if(index < inString.length() - 1 && inString.charAt(index) == ' '
                || inString.charAt(index) == '\n' || inString.charAt(index) == '\r') {
            int nextPosition = getPosition2("", "", inString, index);
            index = nextPosition;
            //System.out.println("index move to: " + index);
        }

        curToken = getNextToken2(inString, "", index);
        preIndex = index;
        index = getPosition2(curToken, "", inString, index);
        //System.out.println("curToken: " + curToken);
    }

    public String getInvalidStr() {
            //System.out.println(preIndex + " " + index);
            String errorStr = inString.substring(preIndex, index);
            //System.out.println(errorStr);
            return errorStr;
    }

    private String getNextToken2(String inStr, String nextToken, int position) {
        tokenValue = "";
        //System.out.println("getStr: " + inStr + " position:" + position);

        //check empty
        if(inStr.isEmpty()){
            nextToken = "EOF";
            return nextToken;
        }

        if(inStr.charAt(position) == '(') {
            nextToken = "openParenthesis";
            tokenValue = "(";
        } else if(inStr.charAt(position) == ')') {
            nextToken = "closingParenthesis";
            tokenValue = ")";
        } else if(inStr.charAt(position) >= 'A' && inStr.charAt(position) <= 'Z') {
            nextToken = "literalAtom";
            int nextPosition = getPosition2(nextToken, nextToken, inStr, position);
            //System.out.println("literalAtom: " + inStr.substring(position, nextPosition));
            tokenValue = inStr.substring(position, nextPosition);
        } else if(inStr.charAt(position) >= '0' && inStr.charAt(position) <= '9') {
            int originalPosition = position;
            int nextPosition = getPosition2("numericAtom", nextToken, inStr, position);
            boolean valid = true; //check ERROR
            while(position < nextPosition) {
                if(inStr.charAt(position) >= 'A' && inStr.charAt(position) <= 'Z') {
                    valid = false;
                    break;
                }
                position++;
            }


            if(valid) {
                nextToken = "numericAtom";
                tokenValue = inStr.substring(originalPosition, nextPosition);

            } else {
                nextToken = "ERROR";
                tokenValue = "ERROR";
            }

//        } else if(position < inStr.length() && inStr.charAt(position) == ' ' || inStr.charAt(position) ==  '\n'
//                || inStr.charAt(position) == '\r') {
//            int originalPosition = position;
//            int nextPosition = getPosition2("", nextToken, inStr, originalPosition);
//            index = nextPosition;

        } else if(inStr.charAt(position) == ' ' || inStr.charAt(position) ==  '\n' || inStr.charAt(position) == '\r') {
            return nextToken;
            //nextToken = "whiteSpace";
            //System.out.println("white");
        } else {
            //System.out.println("EOF");
            nextToken = "ERROR";
            tokenValue = "ERROR";
        }

        return nextToken;
    }

    private static int getPosition2(String subToken, String token, String inStr, int position) {
        if(subToken == "openParenthesis" || subToken == "closingParenthesis") {
            position++;
        } else if(subToken == "literalAtom") {
            while(position < inStr.length() && ((inStr.charAt(position) >= 'A' && inStr.charAt(position) <= 'Z')
                    || inStr.charAt(position) >= '0' && inStr.charAt(position) <= '9')) {
                position++;

            }
        } else if(subToken == "numericAtom") {
            while(position < inStr.length() && ((inStr.charAt(position) >= 'A' && inStr.charAt(position) <= 'Z')
                    || inStr.charAt(position) >= '0' && inStr.charAt(position) <= '9')) {
                position++;

            }
        } else if(subToken == "ERROR") {
            while(position < inStr.length() && ((inStr.charAt(position) >= 'A' && inStr.charAt(position) <= 'Z')
                    || inStr.charAt(position) >= '0' && inStr.charAt(position) <= '9')) {
                position++;

            }
        } else if(subToken == "whiteSpace") {
            while(position < inStr.length() && inStr.charAt(position) == ' ') {
                position++;
            }
        } else {
            while(position < inStr.length() && (inStr.charAt(position) == ' ' || inStr.charAt(position) == '\n'
                    || inStr.charAt(position) == '\r')) {
                position++;
            }
        }

        return position;
    }
}
