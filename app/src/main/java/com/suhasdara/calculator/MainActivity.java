package com.suhasdara.calculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {
    Button b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, bDec;
    Button bClose, bOpen, bDiv, bMul, bSub, bAdd, bEq, bSwitch;
    ImageButton bDel;
    Button bC, bCE;
    TextView tNum;
    AutoResizeTextView tEqn;

    StringBuilder num;
    StringBuilder equation;
    boolean currNegFlag;
    boolean answerSet;

    BigDecimal prevAnswer = null;
    String lastOperation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        num = new StringBuilder();
        equation = new StringBuilder();

        b0 = findViewById(R.id.b0);
        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);
        b3 = findViewById(R.id.b3);
        b4 = findViewById(R.id.b4);
        b5 = findViewById(R.id.b5);
        b6 = findViewById(R.id.b6);
        b7 = findViewById(R.id.b7);
        b8 = findViewById(R.id.b8);
        b9 = findViewById(R.id.b9);
        bDec = findViewById(R.id.bDec);
        bDel = findViewById(R.id.bDel);
        bC = findViewById(R.id.bC);
        bCE = findViewById(R.id.bCE);
        bOpen = findViewById(R.id.bOpen);
        bClose = findViewById(R.id.bClose);
        bDiv = findViewById(R.id.bDiv);
        bMul = findViewById(R.id.bMul);
        bSub = findViewById(R.id.bSub);
        bAdd = findViewById(R.id.bAdd);
        bEq = findViewById(R.id.bEq);
        bSwitch = findViewById(R.id.bSwitch);

        tNum = findViewById(R.id.tNum);
        tEqn = findViewById(R.id.tEqn);

        setTextCopy();
        setNumButtonClicks();
        setClearButtonClicks();
        setOpnButtonClicks();
        setSwitchButtonClick();
        setParenButtonClicks();
        setEqualsClick();
    }

    private void setTextCopy() {
        tNum.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("number", num.toString());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(MainActivity.this, "Value copied to clipboard", Toast.LENGTH_LONG).show();
                return true;
            }
        });

        tEqn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("equation", num.toString());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(MainActivity.this, "Equation copied to clipboard", Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

    private void setNumButtonClicks() {
        b0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!(num.length() == 1 && num.toString().equals("0"))){
                    addDigit('0');
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addDigit('1'); } });
        b2.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addDigit('2'); } });
        b3.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addDigit('3'); } });
        b4.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addDigit('4'); } });
        b5.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addDigit('5'); } });
        b6.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addDigit('6'); } });
        b7.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addDigit('7'); } });
        b8.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addDigit('8'); } });
        b9.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addDigit('9'); } });

        bDec.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!num.toString().contains(".")) {
                    addDigit('.');
                } else {
                    Toast.makeText(MainActivity.this, "Invalid format provided: Two decimals", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addDigit(char digit) {
        if(answerSet) {
            equation = new StringBuilder();
            num = new StringBuilder();
            answerSet = false;
            prevAnswer = null;
            currNegFlag = false;
        }

        if((currNegFlag && num.length() == 16) || (!currNegFlag && num.length() == 15)) {
            Toast.makeText(MainActivity.this, "Maximum number of digits reached (15)", Toast.LENGTH_LONG).show();
            return;
        }

        if(equation.length() == 150) {
            Toast.makeText(MainActivity.this, "Maximum number of characters reached (150)", Toast.LENGTH_LONG).show();
            return;
        }

        if(currNegFlag) {
            num.append(digit);
            equation.insert(equation.length() - 1, digit);
        } else {
            if(!endsWithCloseParen(equation.toString())) {
                num.append(digit);
                equation.append(digit);
            } else {
                Toast.makeText(MainActivity.this, "Enter an operation before a digit after close parenthesis", Toast.LENGTH_LONG).show();
            }
        }

        setTextFields();
    }

    private void setClearButtonClicks() {
        bDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delLogic();
            }
        });

        bC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearLogic();
            }
        });

        bCE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerSetClearLogic();
            }
        });
    }

    private void delLogic() {
        if(answerSet) {
            answerSetClearLogic();
            return;
        }

        if(num.length() != 0) {
            num.deleteCharAt(num.length() - 1);
            if(num.length() == 1 && num.charAt(0) == '-') {
                num = new StringBuilder();
            }
        }

        if(equation.length() != 0) {
            if(currNegFlag) {
                equation.deleteCharAt(equation.length() - 2);
                if(num.length() == 0) {
                    equation.delete(equation.length() - 3, equation.length());
                    currNegFlag = false;
                }
            } else {
                equation.deleteCharAt(equation.length() - 1);
            }
        }

        setTextFields();
    }

    private void clearLogic() {
        if(answerSet) {
            answerSetClearLogic();
            return;
        }

        if(currNegFlag) {
            int start = equation.length() - (num.length() + 2);
            equation.delete(start, equation.length());
            currNegFlag = false;
        } else {
            equation.delete(equation.length() - num.length(), equation.length());
        }
        num = new StringBuilder();
        setTextFields();
    }

    private void answerSetClearLogic() {
        equation = new StringBuilder();
        num = new StringBuilder();
        setTextFields();
        answerSet = false;
        prevAnswer = null;
        currNegFlag = false;
    }

    private void setOpnButtonClicks() {
        bMul.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addOperator('×'); } });
        bDiv.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addOperator('÷'); } });
        bAdd.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addOperator('+'); } });
        bSub.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addOperator('-'); } });
    }

    private void addOperator(char operator) {
        if(answerSet) {
            equation = new StringBuilder(num);
            if(currNegFlag) {
                equation.insert(0, '(');
                equation.append(')');
                currNegFlag = false;
            }
            equation.append(operator);

            num = new StringBuilder();
            setTextFields();

            answerSet = false;
            return;
        }

        checkEndingDecimal();

        num = new StringBuilder();

        boolean wasEmpty = false;
        if(equation.length() == 0) {
            equation.append("0");
            wasEmpty = true;
        }

        if(endsWithOpenParen(equation.toString())) {
            Toast.makeText(MainActivity.this, "Enter a digit before an operation after open parenthesis", Toast.LENGTH_LONG).show();
            if(wasEmpty) {
                equation = new StringBuilder();
            }

            return;
        }

        if(endsWithOperation(equation.toString())) {
            equation.setCharAt(equation.length() - 1, operator);
        } else {
            equation.append(operator);
        }

        currNegFlag = false;
        setTextFields();
    }

    private void setSwitchButtonClick() {
        bSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(answerSet) {
                    if(currNegFlag) {
                        num.deleteCharAt(0);
                        equation = new StringBuilder(num);
                        currNegFlag = false;
                        prevAnswer = prevAnswer.negate();
                    } else {
                        num.insert(0, "-");
                        equation = new StringBuilder(num);
                        equation.insert(0, '(');
                        equation.append(')');
                        currNegFlag = true;
                        prevAnswer = prevAnswer.negate();
                    }
                    setTextFields();
                    return;
                }

                if(num.length() != 0) {
                    if(currNegFlag) {
                        num.deleteCharAt(0);
                        equation.delete(equation.length() - (num.length() + 3), equation.length());
                        equation.append(num);
                        currNegFlag = false;
                    } else {
                        currNegFlag = true;
                        equation.delete(equation.length() - num.length(), equation.length());
                        num.insert(0, "-");
                        equation.append("(");
                        equation.append(num);
                        equation.append(")");
                    }
                    setTextFields();
                }
            }
        });
    }

    private void setParenButtonClicks() {
        bOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(answerSet) {
                    equation = new StringBuilder();
                    num = new StringBuilder();
                    answerSet = false;
                    prevAnswer = null;
                    currNegFlag = false;
                }

                String eqn = equation.toString();
                if(eqn.length() == 0) {
                    openParenLogic(false);
                } else {
                    if(endsWithOperation(eqn) || endsWithOpenParen(eqn)) {
                        openParenLogic(false);
                    } else {
                        openParenLogic(true);
                        /*Toast.makeText(MainActivity.this, "Equation currently ends on a non-operation symbol", Toast.LENGTH_LONG).show();*/
                    }
                }
            }
        });

        bClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(answerSet) {
                    equation = new StringBuilder();
                    num = new StringBuilder();
                    answerSet = false;
                    prevAnswer = null;
                    currNegFlag = false;
                }

                int[] parenCounts = countsParens(equation.toString().toCharArray());
                boolean expressionEmpty = endsWithOpenParen(equation.toString());
                boolean fulfilled = parenCounts[0] == parenCounts[1];

                if(!endsWithOperation(equation.toString()) && !expressionEmpty && !fulfilled) {
                    checkEndingDecimal();
                    num = new StringBuilder();
                    equation.append(")");
                    setTextFields();
                }

                if(endsWithOperation(equation.toString())) {
                    Toast.makeText(MainActivity.this, "Equation currently ends on an operation", Toast.LENGTH_LONG).show();
                } else if(expressionEmpty) {
                    Toast.makeText(MainActivity.this, "Parenthesis expression is empty", Toast.LENGTH_LONG).show();
                } else if(fulfilled) {
                    Toast.makeText(MainActivity.this, "Enough closing parentheses already exist", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void openParenLogic(boolean addMult) {
        checkEndingDecimal();
        currNegFlag = false;
        num = new StringBuilder();
        if(addMult) {
            equation.append("×(");
        } else {
            equation.append("(");
        }
        setTextFields();
    }

    private void setEqualsClick() {
        bEq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String equat = equation.toString();

                if(answerSet) {
                    BigDecimal ans = new BigDecimal(prevAnswer.toPlainString(), new MathContext(15, RoundingMode.HALF_EVEN));

                    String PA_Eq = prevAnswer.toPlainString();
                    String PA_Disp = ans.toString();
                    if(currNegFlag) {
                        PA_Eq = "(" + PA_Eq + ")";
                        PA_Disp = "(" + PA_Disp + ")";
                    }

                    String eqn = PA_Eq + lastOperation;
                    String eqDisp = PA_Disp + lastOperation;

                    equation = new StringBuilder(eqDisp);

                    eqn = eqn.replace('×', '*').replace('÷', '/');
                    ExpressionEvaluator evaluator = new ExpressionEvaluator(prevAnswer);
                    BigDecimal answer = evaluator.evaluate(eqn);
                    afterEvaluation(answer, evaluator);

                    tEqn.setText(equation);
                    return;
                }

                if(!equationValid(equat)) {
                    Toast.makeText(MainActivity.this, "Equation is invalid", Toast.LENGTH_LONG).show();
                } else {
                    String eqn = equat.replace('×', '*').replace('÷', '/');
                    if(eqn.length() == 0) {
                        return;
                    }

                    ExpressionEvaluator evaluator = new ExpressionEvaluator(prevAnswer);
                    BigDecimal answer = evaluator.evaluate(eqn);

                    if(answer.equals(ExpressionEvaluator.ERROR_BIGDEC)) {
                        num = new StringBuilder();
                        setTextFields();
                        Toast.makeText(MainActivity.this, "ERROR: Calculation involves division by zero", Toast.LENGTH_LONG).show();
                        return;
                    }

                    afterEvaluation(answer, evaluator);
                }
            }
        });
    }

    private void afterEvaluation(BigDecimal answer, ExpressionEvaluator evaluator) {
        answerSet = true;
        prevAnswer = answer;
        lastOperation = evaluator.getLastOperation().replace('*', '×').replace('/', '÷');
        answer = new BigDecimal(answer.toPlainString(), new MathContext(15, RoundingMode.HALF_EVEN));
        String ans = answer.toString();
        num = new StringBuilder(ans);

        currNegFlag = num.charAt(0) == '-';

        tNum.setText(ans);
    }

    private void checkEndingDecimal() {
        if(num.length() != 0 && num.charAt(num.length() - 1) == '.') {
            addDigit('0');
        }
    }

    private boolean endsWithOperation(String s) {
        if(s.length() == 0) {
            return false;
        }

        char lastChar = s.charAt(s.length() - 1);
        return lastChar == '×' || lastChar == '-' || lastChar == '+' || lastChar == '÷';
    }

    private boolean endsWithOpenParen(String s) {
        return s.length() != 0 && s.charAt(s.length() - 1) == '(';
    }

    private boolean endsWithCloseParen(String s) {
        return s.length() != 0 && s.charAt(s.length() - 1) == ')';
    }

    private int[] countsParens(char exp[]) {
        int[] counts = new int[2];

        for(char c : exp) {
            if (c == '(') {
                counts[0]++;
            } else if (c == ')') {
                counts[1]++;
            }
        }

        return counts;
    }

    private boolean equationValid(String exp) {
        int[] parenCounts = countsParens(exp.toCharArray());
        boolean parenEqual = parenCounts[0] == parenCounts[1];
        boolean opEnd = endsWithOperation(exp);

        return parenEqual && !opEnd;
    }

    private void setTextFields() {
        String eqn = equation.toString();

        tNum.setText(num);
        tEqn.setText(equation);

        if(!equationValid(eqn)) {
            tEqn.setBackgroundColor(getResources().getColor(R.color.very_light_red));
            tEqn.setTextColor(getResources().getColor(android.R.color.black));
        } else {
            tEqn.setBackgroundColor(getResources().getColor(android.R.color.white));
            tEqn.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
    }
}
