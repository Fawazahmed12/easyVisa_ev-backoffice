package com.easyvisa.utils

import org.apache.commons.validator.routines.EmailValidator

import java.text.DecimalFormat

class StringUtils {

    static cleanEmail(String email) {
        String domain = email.tokenize('@').last()
        email.replaceFirst(domain + '$', domain.toLowerCase())
    }

    static String padEasyVisaId(Long id) {
        id.toString().padLeft(10, '0')
    }

    static String toCamelCase(String text) {
        text.replaceAll("(_)([A-Za-z0-9])", { Object[] it -> it[2].toUpperCase() })
    }

    static String formatAsMoney(BigDecimal number, Boolean replaceZeroWithNone = false) {
        if (replaceZeroWithNone && number == 0) {
            'None'
        } else {
            DecimalFormat df = new DecimalFormat(",###")
            "\$" + df.format(number)
        }
    }

    static String stripHtmlTags(String s) {
        s.replaceAll("<(.|\n)*?>", '').replaceAll("&nbsp;", ' ')
    }

    static Boolean isValidEmail(String email) {
        EmailValidator.instance.isValid(email)
    }

    static String isValidEasyVisaId(String str) {
        str.matches(/^[A|C|E|O][0-9]+$/)
    }

    static String textToHTML(String content) {
        content.replaceAll("\n", '<br/>')
    }

    static def asEnum(def enumType, String str) {
        try {
            enumType.valueOf(str)
        }
        catch (Exception e) {
            null
        }
    }
}
