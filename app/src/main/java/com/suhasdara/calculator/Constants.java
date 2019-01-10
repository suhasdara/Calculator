package com.suhasdara.calculator;

class Constants {
    static final char ZERO = '0';
    static final char ONE = '1';
    static final char TW0 = '2';
    static final char THREE = '3';
    static final char FOUR = '4';
    static final char FIVE = '5';
    static final char SIX = '6';
    static final char SEVEN = '7';
    static final char EIGHT = '8';
    static final char NINE = '9';
    static final char DECIMAL = '.';
    
    static final char OPEN = '(';
    static final char CLOSE = ')';
    
    static final char DIVIDE = '÷';   static final char DIV = '/';
    static final char MULTIPLY = '×'; static final char MULT = '*';
    static final char MINUS = '-';
    static final char PLUS = '+';

    static final char EXP = 'E';
    
    static boolean isOperator(char c) {
        return c == DIVIDE || c == MULTIPLY || isNeg(c) || c == PLUS || c == MULT || c == DIV;
    }
    
    static boolean isDecimal(char c) {
        return c == DECIMAL;
    }

    static boolean isNeg(char c) {
        return c == '-';
    }
    
    static boolean isParen(char c) {
        return isOpenParen(c) || isCloseParen(c);
    }
    
    static boolean isOpenParen(char c) {
        return c == '(';
    }
    
    static boolean isCloseParen(char c) {
        return c == ')';
    }

    static boolean isDigit(char c) {
        return c >= ZERO && c <= NINE;
    }

    static boolean isExp(char c) {
        return c == 'E';
    }
}
