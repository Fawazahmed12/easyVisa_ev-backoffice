package com.easyvisa.enums

enum DisplayTextLanguage {

    EN('English', 'en'),
    ES('Español', 'es'),
    TL('Tagalog', 'tl'),
    ZH('中文', 'zh')

    final String label
    final String languageCode

    DisplayTextLanguage(String label, String languageCode) {
        this.label = label
        this.languageCode = languageCode
    }

    String getLabel() {
        return this.label
    }

    String getLanguageCode() {
        return this.languageCode
    }

    static DisplayTextLanguage getDefaultLanguage() {
        return DisplayTextLanguage.EN
    }

    static DisplayTextLanguage findByLanguageCode(String languageCode) {
        values().find { it.languageCode == languageCode }
    }
}
