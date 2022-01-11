/*
MIT License

Copyright (c) 2021 apace100

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package io.github.merchantpug.dieyourway.util;

import java.util.function.BiFunction;

public enum Comparison {

    NONE("", (a, b) -> false),
    EQUAL("==", Double::equals),
    LESS_THAN("<", (a, b) -> a < b),
    GREATER_THAN(">", (a, b) -> a > b),
    LESS_THAN_OR_EQUAL("<=", (a, b) -> a <= b),
    GREATER_THAN_OR_EQUAL(">=", (a, b) -> a >= b),
    NOT_EQUAL("!=", (a, b) -> !a.equals(b));

    private final String comparisonString;
    private final BiFunction<Double, Double, Boolean> comparison;

    Comparison(String comparisonString, BiFunction<Double, Double, Boolean> comparison) {
        this.comparisonString = comparisonString;
        this.comparison = comparison;
    }

    public boolean compare(double a, double b) {
        return comparison.apply(a, b);
    }

    public String getComparisonString() {
        return comparisonString;
    }

    public static Comparison getFromString(String comparisonString) {
        switch(comparisonString) {
            case "==":
                return EQUAL;
            case "<":
                return LESS_THAN;
            case ">":
                return GREATER_THAN;
            case "<=":
                return LESS_THAN_OR_EQUAL;
            case ">=":
                return GREATER_THAN_OR_EQUAL;
            case "!=":
                return NOT_EQUAL;
        }
        return NONE;
    }
}