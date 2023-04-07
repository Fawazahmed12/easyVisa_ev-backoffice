package com.easyvisa.utils

import java.text.NumberFormat

class NumberUtils {

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.US)

    static String formatUSNumber(String number) {
        if (number) {
            return NUMBER_FORMAT.format(number as Long)
        }
        number
    }

    static String formatUSNumber(Long number) {
        if (number) {
            return NUMBER_FORMAT.format(number)
        }
        number
    }

    static String formatMoneyNumber(BigDecimal number) {
        if (number != null) {
            return String.format('%,.2f', number)
        }
        number
    }

    static BigDecimal prepareBigDecimal(BigDecimal number) {
        if (number != null) {
            return number.setScale(2)
        }
        number
    }

}
