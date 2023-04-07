package com.easyvisa.pdffilling

import com.easyvisa.pdffilling.rules.form.CheckboxPdfFieldSetter
import com.easyvisa.pdffilling.rules.form.DropdownPdfFieldSetter
import com.easyvisa.pdffilling.rules.form.IPdfFieldSetter
import com.easyvisa.pdffilling.rules.form.SimplePdfFieldSetter
import com.easyvisa.questionnaire.answering.AnswerValueObject
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.utils.PdfUtils
import groovy.util.logging.Slf4j
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import org.apache.pdfbox.pdmodel.interactive.form.PDField

@Slf4j
abstract class AbstractPdfFieldHandler implements PdfFieldHandler {

    Map<String, IPdfFieldSetter> pdfFieldSetters = ['PDTextField': new SimplePdfFieldSetter(),
                                                    'PDCheckBox' : new CheckboxPdfFieldSetter(),
                                                    'PDComboBox' : new DropdownPdfFieldSetter()]

    protected List<String> setValues(List<String> pdfFields, List<String> values, String filename, PDAcroForm acroForm, PdfFieldContext pdfFieldContext = null) {
        setValues(pdfFields, values, filename, acroForm, false, pdfFieldContext)
    }

    protected List<String> setValues(List<String> pdfFields, List<String> values, String filename, PDAcroForm acroForm, Boolean continuation, PdfFieldContext pdfFieldContext = null) {
        pdfFields.eachWithIndex { String fieldName, int i ->
            String value = values[i]
            if (value == null) {
                value = values[0]
            }
            setValue(fieldName, value, filename, acroForm, continuation, pdfFieldContext)
        }
    }

    protected void setValue(String fieldName, String value, String filename, PDAcroForm acroForm) {
        setValue(fieldName, value, filename, acroForm, false)
    }

    protected void setValue(String fieldName, String value, String filename, PDAcroForm acroForm, Boolean continuation, PdfFieldContext pdfFieldContext = null) {
        if (value) {
            PDField field = acroForm.getField(fieldName)
            if (!field) {
                if (PdfUtils.AUTO_ID == fieldName) {
                    return
                }
                throwException("No field with name [$fieldName] in PDF form [$filename] for value [$value] question [${pdfFieldContext?.pdfFieldDetail?.questionId} + ${pdfFieldContext?.pdfFieldDetail?.questionName}]")
            }
            IPdfFieldSetter fieldSetter = pdfFieldSetters.get(field.class.simpleName)
            if (!fieldSetter) {
                throwException("New way to set value [${value}] in [$fieldName] in PDF form [$filename]")
            }
            String valueToSet = value.replaceAll("\u00A0", ' ')
            try {
                fieldSetter.setValue(field, valueToSet, filename, continuation)
            } catch (Exception e) {
                log.warn("Can't set field value for ${field.fullyQualifiedName} in ${filename}", e)
            }
        }
    }

    protected List<String> getAnswersList(PdfFieldContext pdfFieldContext) {
        getAnswersList(pdfFieldContext.pdfFieldDetail)
    }

    protected List<String> getAnswersList(PdfFieldDetail pdfFieldDetail) {
        pdfFieldDetail.answerValueObjectList.collect() { it.printValue }
    }

    protected List<String> getAnswersList(PdfFieldDetail pdfFieldDetail, Boolean continuation, Integer fileNumber = 0) {
        Integer fileNumToGet = fileNumber
        if (pdfFieldDetail.questionId == PdfUtils.CONTINUATION_HEADER_ID || fileNumber == null) {
            fileNumToGet = 0
        }
        Integer start = getStartPosition(pdfFieldDetail, continuation, fileNumToGet)
        List<AnswerValueObject> valueObjectList = pdfFieldDetail.answerValueObjectList
        Integer maxIndex = valueObjectList.max { it.index }.index
        if (maxIndex) {
            maxIndex++
        } else {
            maxIndex = 0
        }
        Integer answersSize = [valueObjectList.size(), maxIndex].max()
        Integer end = getEndPosition(pdfFieldDetail, continuation, answersSize, fileNumToGet)
        end = [end, answersSize].min()
        List<String> result = []
        Map<Integer, String> valuesMap = valueObjectList.collectEntries { [it.index ?: 0, it.printValue] }
        Integer formFieldCount = pdfFieldDetail.fieldMappingDetail.formFieldCount
        Integer contShift = (pdfFieldDetail.fieldMappingDetail.fieldExpressions.size() - formFieldCount) * fileNumToGet + formFieldCount
        if (start < end) {
            (start..<end).each {
                Integer indexToPopulate = it
                Integer indexToGet = it
                if (continuation) {
                    indexToPopulate -= contShift
                }
                result[indexToPopulate] = valuesMap[indexToGet]
            }
        }
        result
    }

    protected List<String> getPdfFields(PdfFieldContext pdfFieldContext) {
        getPdfFields(pdfFieldContext.pdfFieldDetail)
    }

    protected List<String> getPdfFields(PdfFieldDetail pdfFieldDetail) {
        pdfFieldDetail.fieldMappingDetail.fieldExpressions
    }

    protected List<String> getPdfFields(PdfFieldDetail pdfFieldDetail, Boolean continuation) {
        Integer start = getStartPosition(pdfFieldDetail, continuation)
        Integer end = getEndPosition(pdfFieldDetail, continuation, pdfFieldDetail.fieldMappingDetail.fieldExpressions.size())
        pdfFieldDetail.fieldMappingDetail.fieldExpressions.subList(start, end)
    }

    private Integer getEndPosition(PdfFieldDetail pdfFieldDetail, Boolean continuation, Integer maxListSize, Integer fileNumber = 0) {
        Integer formFieldCount = pdfFieldDetail.fieldMappingDetail.formFieldCount
        if (formFieldCount != null && !continuation) {
            return formFieldCount
        }
        Integer num = fileNumber
        if (num == null) {
            num = 0
        }
        Integer maxPerSheet = (pdfFieldDetail.fieldMappingDetail.fieldExpressions.size() - formFieldCount) * (num + 1) + formFieldCount
        [maxListSize, maxPerSheet].min()
    }

    private Integer getStartPosition(PdfFieldDetail pdfFieldDetail, Boolean continuation, Integer fileNumber = 0) {
        if (continuation) {
            Integer formFieldCount = pdfFieldDetail.fieldMappingDetail.formFieldCount
            Integer num = fileNumber
            if (num == null) {
                num = 0
            }
            Integer shift = (pdfFieldDetail.fieldMappingDetail.fieldExpressions.size() - formFieldCount) * num
            return formFieldCount + shift
        }
        0
    }

    protected void setHandlerHeader(PdfFieldContext pdfFieldContext, List<String> excludeList) {
        pdfFieldContext.pdfFieldDetailList.each {
            if (!excludeList.contains(it.questionId)) {
                setValues(getPdfFields(it, true), getAnswersList(it, true), pdfFieldContext.filename,
                        pdfFieldContext.acroForm)
            }
        }
    }

    protected void throwException(String msg) {
        throw new RuntimeException(msg)
    }

}
