package com.easyvisa.enums

enum Language {

    ARABIC('Arabic - العربية'),
    BENGALI_BANGLA('Bengali/Bangla - বাংলা'),
    CHINESE('Chinese - 普通话/普通話'),
    CHINESE_GAN('Chinese, Gan - 赣语/贛語'),
    CHINESE_HAKKA('Chinese, Hakka - 客家话/客家話'),
    CHINESE_JINYU('Chinese, Jinyu - 晋语/晉語'),
    CHINESE_MIN_NAN('Chinese, Min Nan - 闽语/閩語'),
    CHINESE_WU('Chinese, Wu - 吴语/ 吳語'),
    CHINESE_YUE('Chinese, Yue - 粤语/粵語'),
    CZECH('Czech - čeština, česk   ý jazyk'),
    ENGLISH('English'),
    FILIPINO('Filipino - Wikang Filipino'),
    FRENCH('French - le français'),
    GERMAN('German - Deustsch'),
    GREEK('Greek - Ελληνικά'),
    HINDI('Hindi - मानक हिन्दी'),
    ITALIAN('Italian - italiano'),
    JAPANESE('Japanese -日本語'),
    JAVANESE('Javanese - Basa Jawa'),
    KOREAN('Korean - 한국어'),
    MARATHI('Marathi - मराठी'),
    PERSIAN_FARSI('Persian/Farsi - فارسی'),
    POLISH('Polish - język polski'),
    PORTUGUESE('Portuguese - português'),
    PUNJABI('Punjabi - ਪੰਜਾਬੀ/ پنجابی'),
    ROMANIAN('Romanian - limba română'),
    RUSSIAN('Russian - ру́сский язы́к'),
    SERBO('Serbo-Croatian - srpskohrvatski/hrvatskosrpski српскохрватски/хрватскосрпски'),
    SPANISH('Spanish - español'),
    TELEGU('Telegu - తెలుగు'),
    THAI('Thai - ภาษาไต'),
    TURKISH('Turkish - Türkçe'),
    UKRANIAN("Ukranian - українська мова, ukrayins'ka mova"),
    URDU('Urdu - اُردُو'),
    VIETNAMESE('Vietnamese - Tiếng Việt'),

    final String displayName

    Language(String displayName) {
        this.displayName = displayName
    }

    String getDisplayName() {
        this.displayName
    }
}
