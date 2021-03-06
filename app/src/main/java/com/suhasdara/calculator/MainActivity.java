package com.suhasdara.calculator;

import android.animation.Animator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class MainActivity extends AppCompatActivity {
    private Button b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, bDec;
    private Button bClose, bOpen, bDiv, bMul, bSub, bAdd, bEq, bSwitch;
    private ImageButton bDel;
    private Button bC, bCE;
    private TextView tNum;
    private AutoResizeTextView tEqn;
    private FloatingActionButton openMenu, mStore, mRecall, mAdd, mSub, mClear;
    private TextView memory_notif, tStore, tRecall, tAdd, tSub, tClear;
    private View fabBg;

    private Toast currToast = null;

    private StringBuilder num;
    private StringBuilder equation;

    private boolean isFABOpen;

    private boolean currNegFlag;

    private boolean answerSet;
    private BigDecimal prevAnswer = null;
    private String lastOperation = "";

    private BigDecimal memoryValue = null;
    private boolean memoryRecalled;

    private static final int DIGIT_LIMIT = 15;
    private static final int CHAR_LIMIT = 150;
    private static final MathContext MC = new MathContext(DIGIT_LIMIT, RoundingMode.HALF_EVEN);

    private static final String ERROR_STRING = "ERROR";
    private static final String ANSWER_HEAD = "Answer:\n";
    private static final String MEMORY_HEAD = "Memory:\n";

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

        openMenu = findViewById(R.id.openMenu);
        mStore = findViewById(R.id.mStore);
        mRecall = findViewById(R.id.mRecall);
        mAdd = findViewById(R.id.mAdd);
        mSub = findViewById(R.id.mSub);
        mClear = findViewById(R.id.mClear);
        fabBg = findViewById(R.id.fabBGLayout);
        memory_notif = findViewById(R.id.mem_notification);
        tStore = findViewById(R.id.tStore);
        tRecall = findViewById(R.id.tRecall);
        tAdd = findViewById(R.id.tAdd);
        tSub = findViewById(R.id.tSub);
        tClear = findViewById(R.id.tClear);

        setTextCopy();
        setNumButtonClicks();
        setClearButtonClicks();
        setOpnButtonClicks();
        setSwitchButtonClick();
        setParenButtonClicks();
        setEqualsClick();
        setFabClicks();
    }

    private void setTextCopy() {
        tNum.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                cancelToast();
                if(num.length() > 0) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("number", num.toString());
                    clipboard.setPrimaryClip(clip);

                    showToast("Value copied to clipboard");
                    return true;
                }
                return false;
            }
        });

        tEqn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                cancelToast();
                if(equation.length() > 0) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("equation", num.toString());
                    clipboard.setPrimaryClip(clip);

                    showToast("Equation copied to clipboard");
                    return true;
                }
                return false;
            }
        });
    }

    private void setNumButtonClicks() {
        b0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cancelToast();
                if(!(num.length() == 1 && num.toString().equals(String.valueOf(Constants.ZERO)))){
                    addDigit(Constants.ZERO);
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addDigit(Constants.ONE); } });
        b2.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addDigit(Constants.TW0); } });
        b3.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addDigit(Constants.THREE); } });
        b4.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addDigit(Constants.FOUR); } });
        b5.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addDigit(Constants.FIVE); } });
        b6.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addDigit(Constants.SIX); } });
        b7.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addDigit(Constants.SEVEN); } });
        b8.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addDigit(Constants.EIGHT); } });
        b9.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addDigit(Constants.NINE); } });

        bDec.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(answerSet) {
                    addDigit(Constants.ZERO);
                    addDigit(Constants.DECIMAL);
                } else {
                    if(!num.toString().contains(String.valueOf(Constants.DECIMAL))) {
                        if(num.length() == 0) {
                            addDigit(Constants.ZERO);
                        }
                        addDigit(Constants.DECIMAL);
                    } else {
                        showToast("Invalid format provided: Two decimals");
                    }
                }
            }
        });
    }

    private void addDigit(char digit) {
        cancelToast();

        if(answerSet) {
            equation = new StringBuilder();
            num = new StringBuilder();
            answerSet = false;
            prevAnswer = null;
            currNegFlag = false;
            memoryRecalled = false;
        }

        if(memoryRecalled) {
            if(currNegFlag) {
                int start = equation.length() - (num.length() + 2);
                equation.delete(start, equation.length());
            } else {
                equation.delete(equation.length() - num.length(), equation.length());
            }
            num = new StringBuilder();
            currNegFlag = false;
            memoryRecalled = false;
        }

        if(num.toString().contains(String.valueOf(Constants.EXP))) {
            equation.append(Constants.MULTIPLY);
            num = new StringBuilder();
            currNegFlag = false;
        }

        if((currNegFlag && num.length() >= DIGIT_LIMIT + 1) || (!currNegFlag && num.length() >= DIGIT_LIMIT)) {
            showToast("Maximum number of digits reached (" + DIGIT_LIMIT + ")");
            return;
        }

        if(equationLimitReached()) {
            showToast("Maximum number of characters exceeded (" + CHAR_LIMIT + ")");
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
                num.append(digit);
                equation.append(Constants.MULTIPLY);
                equation.append(digit);
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
        cancelToast();

        if(answerSet) {
            showToast("Cannot modify answer. Use 'C' or 'CE' to clear");
            return;
        } else if(memoryRecalled) {
            showToast("Cannot modify recalled value. Use 'C' or 'CE' to clear");
            return;
        } else if(num.toString().contains(String.valueOf(Constants.EXP))) {
            showToast("Cannot modify value containing Exp (E). Use 'C' or 'CE' to clear");
            return;
        }

        if(num.length() != 0) {
            num.deleteCharAt(num.length() - 1);
            if(num.length() == 1 && Constants.isNeg(num.charAt(0))) {
                num = new StringBuilder();
            }
        } else {
            if(equation.length() > 1) {
                num = getNextToken();
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

    private StringBuilder getNextToken() {
        StringBuilder result = new StringBuilder();
        int index = equation.length() - 2;
        index = loopForGetNextToken(result, index);

        if(index >= 1) {
            char curr = equation.charAt(index);
            char prev = equation.charAt(index - 1);
            if((Constants.isNeg(curr) || curr == Constants.PLUS) && Constants.isExp(prev)) {
                result.insert(0, "" + prev + curr);
                index -= 2;
                index = loopForGetNextToken(result, index);
            }
        }

        if(index >= 1 && Constants.isNeg(equation.charAt(index)) && Constants.isOpenParen(equation.charAt(index - 1))) {
            result.insert(0, Constants.MINUS);
            currNegFlag = true;
            equation.append(Constants.CLOSE);
            index -= 2;
        }

        if(index <= 0 && prevAnswer != null) {
            answerSet = true;
        }

        if(memoryValue != null) {
            BigDecimal val = getDisplayAnswer(memoryValue);
            String valStr = elimWeirdScientificNum(val);
            String valNegStr = elimWeirdScientificNum(val.negate());
            if(valStr.equals(result.toString()) || valNegStr.equals(result.toString())) {
                memoryRecalled = true;
            }
        }

        return result;
    }

    private int loopForGetNextToken(StringBuilder result, int index) {
        while(index >= 0 && (Constants.isDigit(equation.charAt(index)) || Constants.isDecimal(equation.charAt(index)))) {
            result.insert(0, equation.charAt(index));
            index--;
        }

        return index;
    }

    private void clearLogic() {
        cancelToast();

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
        memoryRecalled = false;
        setTextFields();
    }

    private void answerSetClearLogic() {
        cancelToast();

        equation = new StringBuilder();
        num = new StringBuilder();
        answerSet = false;
        memoryRecalled = false;
        prevAnswer = null;
        currNegFlag = false;
        setTextFields();
    }

    private void setOpnButtonClicks() {
        bMul.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addOperator(Constants.MULTIPLY); } });
        bDiv.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addOperator(Constants.DIVIDE); } });
        bAdd.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addOperator(Constants.PLUS); } });
        bSub.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { addOperator(Constants.MINUS); } });
    }

    private void addOperator(char operator) {
        cancelToast();
        
        if(answerSet) {
            equation = new StringBuilder(num);
            if(currNegFlag) {
                equation.insert(0, Constants.OPEN);
                equation.append(Constants.CLOSE);
                currNegFlag = false;
            }
            equation.append(operator);

            num = new StringBuilder();

            answerSet = false;
            memoryRecalled = false;
            setTextFields();
            return;
        }

        memoryRecalled = false;

        if(equationLimitReached()) {
            showToast("Maximum number of characters exceeded (" + CHAR_LIMIT + ")");
            return;
        }

        checkEndingDecimal();

        num = new StringBuilder();

        boolean wasEmpty = false;
        if(equation.length() == 0) {
            equation.append(Constants.ZERO);
            wasEmpty = true;
        }

        if(endsWithOpenParen(equation.toString())) {
            showToast("Enter a digit before an operation after open parenthesis");
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
                cancelToast();
                
                if(answerSet) {
                    if(currNegFlag) {
                        num.deleteCharAt(0);
                        equation = new StringBuilder(num);
                        currNegFlag = false;
                        prevAnswer = prevAnswer.negate();
                    } else {
                        num.insert(0, Constants.MINUS);
                        equation = new StringBuilder(num);
                        equation.insert(0, Constants.OPEN);
                        equation.append(Constants.CLOSE);
                        currNegFlag = true;
                        prevAnswer = prevAnswer.negate();
                    }
                    setTextFields();
                    return;
                }

                if(equationLimitReached()) {
                    showToast("Maximum number of characters exceeded (" + CHAR_LIMIT + ")");
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
                        num.insert(0, Constants.MINUS);
                        equation.append(Constants.OPEN);
                        equation.append(num);
                        equation.append(Constants.CLOSE);
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
                cancelToast();
                
                if(answerSet) {
                    equation = new StringBuilder();
                    num = new StringBuilder();
                    answerSet = false;
                    prevAnswer = null;
                    currNegFlag = false;
                }

                memoryRecalled = false;

                if(equationLimitReached()) {
                    showToast("Maximum number of characters exceeded (" + CHAR_LIMIT + ")");
                    return;
                }

                String eqn = equation.toString();
                if(eqn.length() == 0) {
                    openParenLogic(false);
                } else {
                    if(endsWithOperation(eqn) || endsWithOpenParen(eqn)) {
                        openParenLogic(false);
                    } else {
                        openParenLogic(true);
                    }
                }
            }
        });

        bClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelToast();
                
                if(answerSet) {
                    equation = new StringBuilder();
                    num = new StringBuilder();
                    answerSet = false;
                    prevAnswer = null;
                    currNegFlag = false;
                }

                memoryRecalled = false;

                if(equationLimitReached()) {
                    showToast("Maximum number of characters exceeded (" + CHAR_LIMIT + ")");
                    return;
                }

                int[] parenCounts = countsParens(equation.toString().toCharArray());
                boolean fulfilled = parenCounts[0] == parenCounts[1];
                boolean expressionEmpty = endsWithOpenParen(equation.toString());

                if(!endsWithOperation(equation.toString()) && !expressionEmpty && !fulfilled) {
                    checkEndingDecimal();
                    num = new StringBuilder();
                    currNegFlag = false;
                    equation.append(Constants.CLOSE);
                    setTextFields();
                }

                if(endsWithOperation(equation.toString())) {
                    showToast("Equation currently ends on an operation");
                } else if(expressionEmpty) {
                    showToast("Parenthesis expression is empty");
                } else if(fulfilled) {
                    showToast("Could not find matching open parenthesis");
                }
            }
        });
    }

    private void openParenLogic(boolean addMult) {
        checkEndingDecimal();
        currNegFlag = false;
        num = new StringBuilder();
        if(addMult) {
            equation.append(Constants.MULTIPLY);
        }
        equation.append(Constants.OPEN);
        setTextFields();
    }

    private void setEqualsClick() {
        bEq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelToast();
                memoryRecalled = false;

                if(answerSet) {
                    BigDecimal ans = getDisplayAnswer(prevAnswer);

                    String PA_Eq = prevAnswer.toPlainString();
                    String PA_Disp = elimWeirdScientificNum(ans);

                    if(currNegFlag) {
                        PA_Eq = "" + Constants.OPEN + PA_Eq + Constants.CLOSE;
                        PA_Disp = "" + Constants.OPEN + PA_Disp + Constants.CLOSE;
                    }

                    String eqn = PA_Eq + lastOperation;
                    String eqDisp = PA_Disp + lastOperation;

                    equation = new StringBuilder(eqDisp);

                    eqn = eqn.replace(Constants.MULTIPLY, Constants.MULT).replace(Constants.DIVIDE, Constants.DIV);

                    //Division by zero is impossible in this case
                    ExpressionEvaluator evaluator = new ExpressionEvaluator(prevAnswer);
                    BigDecimal answer = evaluator.evaluate(eqn);
                    afterEvaluation(answer, evaluator);

                    tEqn.setText(equation);
                    return;
                }

                checkEndingDecimal();
                String equat = equation.toString();

                if(!equationValid(equat)) {
                    showToast("Equation is invalid");
                } else {
                    String eqn = equat.replace(Constants.MULTIPLY, Constants.MULT).replace(Constants.DIVIDE, Constants.DIV);
                    if(eqn.length() == 0) {
                        return;
                    }

                    ExpressionEvaluator evaluator = new ExpressionEvaluator(prevAnswer);
                    BigDecimal answer = evaluator.evaluate(eqn);

                    if(answer.equals(ExpressionEvaluator.ERROR_BIGDEC)) {
                        num = new StringBuilder();
                        tNum.setText(getFormattedString(new StringBuilder(ERROR_STRING), true));
                        showToast("ERROR: Calculation involves division by zero");
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
        lastOperation = evaluator.getLastOperation().replace(Constants.MULT, Constants.MULTIPLY).replace(Constants.DIV, Constants.DIVIDE);
        answer = getDisplayAnswer(answer);
        String ans = elimWeirdScientificNum(answer);
        num = new StringBuilder(ans);

        currNegFlag = Constants.isNeg(num.charAt(0));

        StringBuilder disp = new StringBuilder(ANSWER_HEAD + ans);
        tNum.setText(getFormattedString(disp, false));
    }

    private void setFabClicks() {
        openMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen) {
                    openFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        fabBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFABMenu();
            }
        });

        mStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoryStoreLogic(true);
            }
        });

        mRecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoryRecallLogic();
            }
        });

        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoryValue = null;
                memory_notif.setVisibility(View.GONE);
                showToast("Cleared memory");
            }
        });

        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(num.length() != 0) {
                    if(memoryValue == null) {
                        memoryStoreLogic(true);
                    } else {
                        BigDecimal toAdd = new BigDecimal(num.toString());
                        memoryValue = memoryValue.add(toAdd);
                        showToast("Current value added to memory");
                    }
                } else {
                    showToast("ERROR: Value field is empty");
                }
            }
        });

        mSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(num.length() != 0) {
                    if(memoryValue == null) {
                        memoryStoreLogic(false);
                    } else {
                        BigDecimal toSub = new BigDecimal(num.toString());
                        memoryValue = memoryValue.subtract(toSub);
                        showToast("Current value subtracted from memory");
                    }
                } else {
                    showToast("ERROR: Value field is empty");
                }
            }
        });
    }

    /* https://stackoverflow.com/a/40647621/9992614 */
    private void openFABMenu(){
        cancelToast();
        
        isFABOpen = true;
        showButton(mStore, tStore);
        showButton(mRecall, tRecall);
        showButton(mAdd, tAdd);
        showButton(mSub, tSub);
        showButton(mClear, tClear);
        fabBg.setVisibility(View.VISIBLE);

        openMenu.animate().rotationBy(135);
        openMenu.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        translateOut(mStore, R.dimen.standard_50);
        translateOut(mRecall, R.dimen.standard_95);
        translateOut(mAdd, R.dimen.standard_140);
        translateOut(mSub, R.dimen.standard_185);
        translateOut(mClear, R.dimen.standard_230);
    }
    
    private void showButton(FloatingActionButton fab, TextView tv) {
        fab.show();
        tv.setVisibility(View.VISIBLE);
    }
    
    private void translateOut(FloatingActionButton fab, int distance) {
        fab.animate().translationX(getResources().getDimension(distance));
    }

    private void closeFABMenu(){
        cancelToast();
        
        isFABOpen = false;
        fabBg.setVisibility(View.GONE);
        openMenu.animate().rotationBy(-135);
        openMenu.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_orange_light)));

        translateIn(mStore);
        translateIn(mRecall);
        translateIn(mAdd);
        translateIn(mSub);
        mClear.animate().translationX(0).setListener(new Animator.AnimatorListener() {
            public void onAnimationStart(Animator animator) {}
            public void onAnimationCancel(Animator animator) {}
            public void onAnimationRepeat(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                if(!isFABOpen) {
                    hideButton(mStore, tStore);
                    hideButton(mRecall, tRecall);
                    hideButton(mAdd, tAdd);
                    hideButton(mSub, tSub);
                    hideButton(mClear, tClear);
                    openMenu.setRotation(0);
                    openMenu.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.holo_orange_light)));
                } else {
                    openMenu.setRotation(135);
                    openMenu.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                }
            }
        });
    }
    
    private void translateIn(FloatingActionButton fab) {
        fab.animate().translationX(0);
    }
    
    private void hideButton(FloatingActionButton fab, TextView tv) {
        fab.hide();
        tv.setVisibility(View.GONE);
    }

    private void memoryStoreLogic(boolean positive) {
        cancelToast();

        if(answerSet) {
            if(positive) {
                memoryValue = prevAnswer;
            } else {
                memoryValue = prevAnswer.negate();
            }
        } else {
            if(num.length() == 0) {
                showToast("ERROR: Value field is empty");
                return;
            }

            if(positive) {
                memoryValue = new BigDecimal(num.toString());
            } else {
                memoryValue = new BigDecimal(num.toString()).negate();
            }
        }
        memory_notif.setVisibility(View.VISIBLE);
        showToast("Saved current value to memory");
    }

    private void memoryRecallLogic() {
        cancelToast();

        if(memoryValue != null) {
            BigDecimal toSet = getDisplayAnswer(memoryValue);
            String dispVal = elimWeirdScientificNum(toSet);

            if(answerSet) {
                answerSetClearLogic();
                num = new StringBuilder(dispVal);
                equation = new StringBuilder(dispVal);
                if(toSet.compareTo(BigDecimal.ZERO) < 0) {
                    equation.insert(0, Constants.OPEN);
                    equation.append(Constants.CLOSE);
                    currNegFlag = true;
                }
            } else {
                if(currNegFlag) {
                    int start = equation.length() - (num.length() + 2);
                    equation.delete(start, equation.length());
                    currNegFlag = false;
                } else {
                    equation.delete(equation.length() - num.length(), equation.length());
                }

                equation.append(dispVal);
                if(toSet.compareTo(BigDecimal.ZERO) < 0) {
                    equation.insert(equation.length() - dispVal.length(), Constants.OPEN);
                    equation.append(Constants.CLOSE);
                    currNegFlag = true;
                }
                num = new StringBuilder(dispVal);
            }
            memoryRecalled = true;
            setTextFields();
        } else {
            showToast("No value stored in memory");
        }
    }

    private void checkEndingDecimal() {
        if(num.length() != 0 && Constants.isDecimal(num.charAt(num.length() - 1))) {
            addDigit(Constants.ZERO);
        }
    }

    private boolean endsWithOperation(String s) {
        if(s.length() == 0) {
            return false;
        }

        return Constants.isOperator(s.charAt(s.length() - 1));
    }

    private boolean endsWithOpenParen(String s) {
        return s.length() != 0 && Constants.isOpenParen(s.charAt(s.length() - 1));
    }

    private boolean endsWithCloseParen(String s) {
        return s.length() != 0 && Constants.isCloseParen(s.charAt(s.length() - 1));
    }

    private int[] countsParens(char exp[]) {
        int[] counts = new int[2];

        for(char c : exp) {
            if (Constants.isOpenParen(c)) {
                counts[0]++;
            } else if (Constants.isCloseParen(c)) {
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

    private boolean equationLimitReached() {
        return (equation.length() >= CHAR_LIMIT) & equationValid(equation.toString());
    }

    private BigDecimal getDisplayAnswer(BigDecimal answer) {
        if(answer.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(answer.toPlainString(), MC).stripTrailingZeros();
    }

    private String elimWeirdScientificNum(BigDecimal answer) {
        String ans = answer.toString();
        String[] tokens = ans.split(String.valueOf(Constants.EXP));
        if(tokens.length == 2) {
            int scale = Integer.parseInt(tokens[1]);
            if(scale >= 0 && scale < DIGIT_LIMIT) {
                ans = answer.toPlainString();
            }
        }
        return ans;
    }

    private void setTextFields() {
        String eqn = equation.toString();

        StringBuilder numDisp = num;
        if(answerSet) {
            numDisp = new StringBuilder("" + ANSWER_HEAD + num);
        } else if(memoryRecalled) {
            numDisp = new StringBuilder("" + MEMORY_HEAD + num);
        }

        if(numDisp == num) {
            tNum.setText(numDisp);
        } else {
            tNum.setText(getFormattedString(numDisp, false));
        }
        tEqn.setText(equation);

        if(equationValid(eqn)) {
            tEqn.setBackgroundColor(getResources().getColor(android.R.color.white));
            tEqn.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            tEqn.setBackgroundColor(getResources().getColor(R.color.very_light_red));
            tEqn.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    private SpannableString getFormattedString(StringBuilder toFormat, boolean errorMessage) {
        SpannableString toRet = new SpannableString(toFormat);
        if(errorMessage) {
            toRet.setSpan(new ForegroundColorSpan(Color.RED), 0, toFormat.length(), 0);
        } else {
            toRet.setSpan(new RelativeSizeSpan(0.6f), 0, toFormat.indexOf("\n"), 0);
        }

        return toRet;
    }

    private void showToast(String text) {
        if(currToast != null) {
            currToast.cancel();
        }
        currToast = Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG);
        currToast.show();
    }

    private void cancelToast() {
        if(currToast != null) {
            currToast.cancel();
            currToast = null;
        }
    }

    @Override
    public void onBackPressed() {
        if(isFABOpen) {
            closeFABMenu();
        } else {
            super.onBackPressed();
        }
    }
}