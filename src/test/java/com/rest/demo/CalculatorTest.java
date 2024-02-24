package com.rest.demo;

import com.rest.demo.Calculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {
    Calculator calculator;

    @BeforeEach
    void setup(){
        calculator = new Calculator();
    }

    @Test
    void testMultiply() {
        assertEquals(20, calculator.multiply(4, 5));
    }

    @Test
    void testDivide(){
        assertEquals(2, calculator.divide(4,2));
    }

}