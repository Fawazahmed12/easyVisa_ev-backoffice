package com.easyvisa.utils

import groovy.util.logging.Slf4j
import org.apache.pdfbox.cos.COSName
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import org.apache.pdfbox.pdmodel.interactive.form.PDComboBox
import org.apache.pdfbox.pdmodel.interactive.form.PDField
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField
import org.apache.pdfbox.pdmodel.interactive.form.PDVariableText

import java.util.regex.Matcher
import java.util.regex.Pattern

@Slf4j
final class PdfUtils {

    static final String CONTINUATION_HEADER_ID = 'header'
    static final String AUTO_ID = 'auto'
    static final String SEE_CONTINUATION = 'See continuation sheet'
    static final String SEE_CONTINUATIONS = 'See continuation sheet(s)'
    static final String ZIP_MIMETYPE = 'application/zip'
    static final String PDF_MIMETYPE = 'application/pdf'

    private static final Pattern DA_NOTATION_PATTERN = Pattern.compile("/([a-zA-Z-]+)\\s+(\\d+\\.?\\d*)\\s.*")

    private PdfUtils() {
    }

    static void setNewValueToComboBox(String value, PDComboBox comboBox, PDField field, String filename) {
        if (value && !comboBox.getOptionsDisplayValues().contains(value)) {
            if (!['N/A', '0', '1'].contains(value)) {
                throw new UnsupportedOperationException("Review value [${value}] to set in dropdown" +
                        " ${field.fullyQualifiedName} in ${filename} form")
            }
            List<String> exportValues = comboBox.getOptionsExportValues()
            List<String> displayValues = comboBox.getOptionsDisplayValues()
            exportValues.add(value)
            displayValues.add(value)
            comboBox.setOptions(exportValues, displayValues)
        }
    }

    static void setSingleField(boolean continuation, PDField field) {
        if (!continuation) {
            PDTextField text = (PDTextField) field
            if (text.isMultiline()) {
                PDRectangle rectangle = text.getWidgets().get(0).getRectangle()
                if (rectangle.getUpperRightY() - rectangle.getLowerLeftY() > 18) {
                    rectangle.setLowerLeftY((rectangle.getUpperRightY() - 18).floatValue())
                }
                text.getWidgets().get(0).setRectangle(rectangle)
                text.setMultiline(false)
            }
        }
    }

    static void checkAutoScale(String filename, String value, PDField field) throws IOException {
        if (field instanceof PDVariableText) {
            PDAcroForm acroForm = field.getAcroForm()
            PDVariableText variableText = (PDVariableText) field
            String defaultAppearance = variableText.getDefaultAppearance()
            Matcher m = DA_NOTATION_PATTERN.matcher(defaultAppearance)
            if (!m.find() || m.groupCount() < 2) {
                throw new UnsupportedOperationException("Review DA notation [${defaultAppearance}] in ${filename}" +
                        " form for ${field.fullyQualifiedName}")
            }
            String fontName = m.group(1)
            String sizeStr = m.group(2)
            float fontSize = Float.valueOf(sizeStr)
            try {
                PDFont fieldFont = acroForm.getDefaultResources().getFont(COSName.getPDFName(fontName))
                float valueWidth = calculateWidth(value.replaceAll('\n', ''), fontSize, fieldFont)
                float valueHeight = calculateHeight(fontSize, fieldFont)
                PDRectangle rectangle = variableText.getWidgets().get(0).getRectangle()
                boolean multiline = false
                if ((variableText instanceof PDTextField) && ((PDTextField) variableText).isMultiline()) {
                    multiline = true
                }
                if (multiline) {
                    checkAutoScaleMultiLine(variableText, defaultAppearance, sizeStr, valueWidth, valueHeight,
                            rectangle, fieldFont, fontSize, value)
                } else {
                    checkAutoScaleSingleLine(field, defaultAppearance, sizeStr, valueWidth, rectangle)
                }
            } catch (Exception e) {
                log.warn("Can't check auto scale necessity for ${field.fullyQualifiedName} in ${filename}", e)
            }
        }
    }

    private static void checkAutoScaleMultiLine(PDVariableText variableText, String defaultAppearance,
                                                String sizeStr, float valueWidth, float valueHeight, PDRectangle rectangle,
                                                PDFont fieldFont, float fontSize, String value) throws IOException {
        float rectHeight = rectangle.getHeight()
        float rectWidth = rectangle.getWidth()
        float multilineWidth = (float) (rectWidth * Double.valueOf(rectangle.getHeight() / valueHeight).intValue())
        if ((multilineWidth - valueWidth) < rectWidth) {
            float adoptedSize = fontSize
            float newMultilineWidth = calculateMultilineFieldWidth(fieldFont, rectWidth, rectHeight, adoptedSize)
            float newWidth = calculateValueWidth(fieldFont, value, rectWidth, adoptedSize)
            while ((newWidth >= newMultilineWidth) && adoptedSize > 0.1) {
                newMultilineWidth = calculateMultilineFieldWidth(fieldFont, rectWidth, rectHeight, adoptedSize)
                newWidth = calculateValueWidth(fieldFont, value, rectWidth, adoptedSize)
                adoptedSize -= 0.1
            }
            variableText.setDefaultAppearance(defaultAppearance.replace(sizeStr, String.valueOf(adoptedSize)))
        }
    }

    private static float calculateValueWidth(PDFont fieldFont, String value, float rectWidth, float fontSize) throws IOException {
        float newWidth = 0
        String[] words = value.split(" ")
        float currentLineWidth = 0
        boolean newLine = false
        for (String word : words) {
            String valueToCheck = word
            if (valueToCheck.contains('\n')) {
                newLine = true
                valueToCheck = valueToCheck.replaceAll('\n', '')
            }
            float wordWidth = calculateWidth(valueToCheck + " ", fontSize, fieldFont)
            currentLineWidth += wordWidth
            if (currentLineWidth >= rectWidth) {
                newWidth += rectWidth
                currentLineWidth = wordWidth
            }
            if (newLine) {
                newLine = false
                newWidth += rectWidth
                currentLineWidth = 0
            }
        }
        return newWidth + currentLineWidth
    }

    private static float calculateMultilineFieldWidth(PDFont fieldFont, float rectWidth, float rectHeight, float fontSize) {
        return rectWidth * Double.valueOf(rectHeight / Math.round(calculateHeight(fontSize, fieldFont))).intValue()
    }

    private static void checkAutoScaleSingleLine(PDField field, String defaultAppearance, String sizeStr,
                                                 float titleWidth, PDRectangle rectangle) {
        if (((titleWidth >= rectangle.getWidth()) && !((field instanceof PDTextField) && ((PDTextField) field).isComb()))) {
            ((PDVariableText) field).setDefaultAppearance(defaultAppearance.replace(sizeStr, "0"))
        }
    }

    private static float calculateHeight(float fontSize, PDFont fieldFont) {
        return fieldFont.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize
    }

    private static float calculateWidth(String value, float fontSize, PDFont fieldFont) throws IOException {
        return fieldFont.getStringWidth(value) / 1000 * fontSize
    }

}
