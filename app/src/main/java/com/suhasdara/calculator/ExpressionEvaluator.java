package com.suhasdara.calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Stack;

class ExpressionEvaluator {
    private static final String ERROR_CONSTANT = "-234698324706982987345873279.493861948369184370918463948";
    static final BigDecimal ERROR_BIGDEC = new BigDecimal(ERROR_CONSTANT);
    private static final MathContext MC_DIV = new MathContext(25, RoundingMode.HALF_EVEN);

    private char previousChar = 0;
    private boolean negSet = false;
    private boolean ignoreOneCloseParen = false;

    private boolean evaluating = false;
    private String lastOp = "";

    private final BigDecimal prevAnswer;

    /*ExpressionEvaluator() {
        this(null);
    }*/

    ExpressionEvaluator(BigDecimal prevAnswer) {
        this.prevAnswer = prevAnswer;
    }

    BigDecimal evaluate(String expression) {
        evaluating = true;

        char[] tokens = expression.toCharArray();

        // Stack for numbers: 'values'
        Stack<BigDecimal> values = new Stack<>();

        // Stack for Operators: 'ops'
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            if(tokens[i] == ' ') {
                continue;
            }

            if ((tokens[i] >= '0' && tokens[i] <= '9') || tokens[i] == '.') {
                StringBuilder buffer = new StringBuilder();

                if(negSet) {
                    buffer.append('-');
                    negSet = false;
                }

                while (i < tokens.length && ((tokens[i] >= '0' && tokens[i] <= '9') || tokens[i] == '.' || tokens[i] == 'E')) {
                    if(tokens[i] == 'E') {
                        buffer.append(tokens[i++]);
                    }
                    buffer.append(tokens[i++]);
                }
                i--;

                if(values.isEmpty() && prevAnswer != null) {
                    values.push(prevAnswer);
                } else {
                    values.push(new BigDecimal(buffer.toString()));
                }
            }

            // Current token is an opening brace, push it to 'ops'
            else if (tokens[i] == '(') {
                ops.push(tokens[i]);
            }

            // Closing brace encountered, solve entire brace
            else if (tokens[i] == ')') {
                if(!ignoreOneCloseParen) {
                    while (ops.peek() != '(') {
                        BigDecimal res = calculateOnce(values, ops);
                        if(res.equals(ERROR_BIGDEC)) {
                            return ERROR_BIGDEC;
                        }

                        values.push(res);
                    }

                    ops.pop(); //remove open brace
                } else {
                    ignoreOneCloseParen = false;
                }
            }

            // Current token is an operator.
            else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/') {
                if(previousChar == '(' && tokens[i] == '-') {
                    ops.pop();
                    ignoreOneCloseParen = true;
                    negSet = true;
                } else {
                    // While top of 'ops' has same or greater precedence to current
                    // token, which is an operator. Apply operator on top of 'ops'
                    // to top two elements in values stack
                    while (!ops.empty() && hasPrecedence(tokens[i], ops.peek())) {
                        BigDecimal res = calculateOnce(values, ops);
                        if(res.equals(ERROR_BIGDEC)) {
                            return ERROR_BIGDEC;
                        }

                        values.push(res);
                    }

                    // Push current token to 'ops'.
                    ops.push(tokens[i]);
                }
            }

            previousChar = tokens[i];
        }

        // System.out.println(values + " " + ops);

        // Entire expression has been parsed at this point, apply remaining
        // ops to remaining values
        while (!ops.empty()) {
            BigDecimal res = calculateOnce(values, ops);
            if(res.equals(ERROR_BIGDEC)) {
                return ERROR_BIGDEC;
            }

            values.push(res);
        }

        // Top of 'values' contains result, return it
        return values.pop();
    }

    // Call only after evaluate, otherwise useless.
    String getLastOperation() {
        if(!evaluating) {
            throw new IllegalStateException("Call evaluate first");
        }

        return lastOp;
    }

    private BigDecimal calculateOnce(Stack<BigDecimal> values, Stack<Character> ops) {
        Character op = ops.pop();
        BigDecimal b = values.pop();
        BigDecimal a = values.pop();

        if(ops.isEmpty()) {
            if(b.compareTo(BigDecimal.ZERO) < 0) {
                lastOp = "" + op + "(" + b.toPlainString() + ")";
            } else {
                lastOp = "" + op + b.toPlainString();
            }
        }

        return applyOp(op, b, a);
    }

    // Returns true if 'op2' has higher or same precedence as 'op1',
    // otherwise returns false.
    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }

        return !((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-'));
    }

    // A utility method to apply an operator 'op' on operands 'a'
    // and 'b'. Return the result.
    private BigDecimal applyOp(char op, BigDecimal b, BigDecimal a) {
        switch (op) {
            case '+':
                return a.add(b);
            case '-':
                return a.subtract(b);
            case '*':
                return a.multiply(b);
            case '/':
                if (b.equals(BigDecimal.ZERO)) {
                    return new BigDecimal(ERROR_CONSTANT);
                }
                return a.divide(b, MC_DIV);
            default:
                return BigDecimal.ZERO;
        }
    }
}
