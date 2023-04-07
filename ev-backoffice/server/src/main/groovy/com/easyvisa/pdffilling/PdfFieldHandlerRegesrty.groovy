package com.easyvisa.pdffilling

import com.easyvisa.pdffilling.rules.RepeatPdfHandler
import com.easyvisa.pdffilling.rules.SimplePdfHandler
import com.easyvisa.pdffilling.rules.SplitPdfHandler

class PdfFieldHandlerRegesrty {

    private PdfFieldHandler() {
    }

    private static Map<String, PdfFieldHandler> map = [
            'simple': new SimplePdfHandler(),
            'split' : new SplitPdfHandler(),
            'repeat' : new RepeatPdfHandler()
    ]

    static PdfFieldHandler getHandler(String type) {
        map.get(type)
    }

}
