package com.Aithani.BankingApp.Util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataMaskingUtilTest {
    @Test
    void maskMobile_shouldShowOnlyLastFourDigits() {

        String result = DataMaskingUtil.maskMobile("9876544321");

        assertEquals("******4321", result);
    }
    @Test
    void maskPan_shouldMaskSensitiveCharacters() {

        String result = DataMaskingUtil.maskPan("ABCDE1234F");

        assertEquals("ABC*****4F", result);
    }
}
