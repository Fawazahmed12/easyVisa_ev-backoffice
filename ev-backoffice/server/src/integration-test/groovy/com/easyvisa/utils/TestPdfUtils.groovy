package com.easyvisa.utils

import com.easyvisa.Petitioner
import com.easyvisa.questionnaire.answering.AnswerValueObject
import com.easyvisa.questionnaire.answering.ContinuationSheetHeaderInfo
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.google.common.io.Files
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox
import org.apache.pdfbox.pdmodel.interactive.form.PDField
import org.apache.pdfbox.pdmodel.interactive.form.PDVariableText
import org.hamcrest.Matchers

import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

import static org.junit.Assert.*

final class TestPdfUtils {

    private TestPdfUtils() { throw new UnsupportedOperationException() }

    static void writeToFile(InputStream resultPdf, String pdfFolder, String filename) {
        if (pdfFolder) {
            Files.write(((ByteArrayInputStream) resultPdf).getBytes(), Paths.get(pdfFolder, filename).toFile())
            resultPdf.reset()
        }
    }

    static void assertCustomValue(PDAcroForm acroForm, String field, String value) {
        assertPdfValue(value, acroForm.getField(field))
    }

    static List<PdfFieldDetail> assertAllDataPopulated(List<PdfFieldDetail> pdfFieldDetailList, PDAcroForm acroForm) {
        pdfFieldDetailList.each {
            switch (it.fieldMappingDetail.fieldType) {
                case 'repeat': assertRepeatCase(it, acroForm, false)
                    break
                case 'split': assertSplitCase(it, acroForm)
                    break
                default: assertSimpleCase(it, acroForm)
            }
        }
    }

    static void assertRepeatCase(PdfFieldDetail fieldDetail, PDAcroForm acroForm, Boolean continuation) {
        Integer loops = [fieldDetail.answerValueObjectList.size(), fieldDetail.fieldMappingDetail.fieldExpressions.size()].min()
        Integer start = 0
        if (continuation) {
            start = fieldDetail.fieldMappingDetail.formFieldCount
        }
        (start..<loops).each {
            String fieldName = fieldDetail.fieldMappingDetail.fieldExpressions[it]
            if (fieldName != PdfUtils.AUTO_ID) {
                PDField field = acroForm.getField(fieldName)
                int index = it
                assertPdfValue(fieldDetail.answerValueObjectList.find { it.index == index }?.value, field)
            }
        }
    }

    private static void assertSplitCase(PdfFieldDetail fieldDetail, PDAcroForm acroForm) {
        List<AnswerValueObject> answers = fieldDetail.answerValueObjectList
        assertEquals(1, answers.size())
        int qSize = fieldDetail.fieldMappingDetail.fieldExpressions.size() - 1
        def splitList = []
        answers.get(0).value.reverse().each { splitList[qSize--] = it }

        fieldDetail.fieldMappingDetail.fieldExpressions.eachWithIndex { String fieldName, int i ->
            PDField field = acroForm.getField(fieldName)
            def expected = splitList[i]
            if (expected == null) {
                expected = ""
            }
            assertEquals(expected, field.getValueAsString())
        }
    }

    private static void assertSimpleCase(PdfFieldDetail fieldDetail, PDAcroForm acroForm) {
        assertTrue(fieldDetail.answerValueObjectList.size() > 0)
        assertTrue(fieldDetail.fieldMappingDetail.fieldExpressions.size() > 0)
        fieldDetail.fieldMappingDetail.fieldExpressions.eachWithIndex { String entry, int i ->
            PDField field = acroForm.getField(entry)
            String value = fieldDetail.answerValueObjectList[i]?.value
            if (value == null) {
                value = fieldDetail.answerValueObjectList.get(0).value
            }
            assertPdfValue(value, field)
        }
    }

    private static void assertPdfValue(String expectedValue, PDField field) {
        if (field instanceof PDCheckBox) {
            if (expectedValue) {
                assertTrue(((PDCheckBox) field).isChecked())
            } else {
                assertFalse(((PDCheckBox) field).isChecked())
            }
        } else {
            String value = expectedValue
            if (!value) {
                value = ''
            }
            assertEquals(value, field.getValueAsString().replaceAll("[\\[\\]]", ""))
        }
    }

    static void assertPdfResult(def result, String filename, Map<String, String> assertionMap, String pdfFolder) {
        assertEquals(PdfUtils.PDF_MIMETYPE, result.mimetype)
        assertEquals(filename, result.filename)
        assertPdfValues(result['file'], assertionMap, filename, pdfFolder)
    }

    static void assertZipResult(def result, String filename, List<String> pdfNames,
                                List<Map<String, String>> assertionMaps, String pdfFolder) {
        assertNotNull(result.file)
        assertEquals(PdfUtils.ZIP_MIMETYPE, result.mimetype)
        assertThat(result.filename, Matchers.endsWith('.zip'))

        (new ZipInputStream(result.file)).withCloseable {
            ZipEntry entry
            Integer index = 0
            while (entry = it.getNextEntry()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream()
                for (int c = it.read(); c != -1; c = it.read()) {
                    baos.write(c)
                }
                it.closeEntry()
                assertPdfValues(new ByteArrayInputStream(baos.toByteArray()), assertionMaps[index], pdfNames[index], pdfFolder)
                index++
            }
        }
    }

    static void assertPdfValues(InputStream resultPdf, Map<String, String> assertionMap, String fileName, String pdfFolder) {
        assertNotNull(resultPdf)
        writeToFile(resultPdf, pdfFolder, "${fileName}")

        PDDocument.load(resultPdf).withCloseable {
            PDAcroForm acroForm = it.getDocumentCatalog().getAcroForm()
            assertionMap.each {
                assertCustomValue(acroForm, it.key, it.value)
            }
        }
    }

    static void assertPdfFields(InputStream resultPdf, Map<String, PdfAsserts> assertionMap, String fileName, String pdfFolder) {
        assertNotNull(resultPdf)
        writeToFile(resultPdf, pdfFolder, "${fileName}")

        PDDocument.load(resultPdf).withCloseable {
            PDAcroForm acroForm = it.getDocumentCatalog().getAcroForm()
            assertionMap.each {
                assertCustomValue(acroForm, it.key, it.value.fieldValue)
                if (it.value.defaultAppearance) {
                    assertEquals(it.value.defaultAppearance, ((PDVariableText) acroForm.getField(it.key)).defaultAppearance)
                }
            }
        }
    }

    static void assertionMapAdditionalPart(Map<String, String> result, String addPartValue, Petitioner petitioner,
                                           String addPartField, String addPartLNameField, String addPartFNameField,
                                           String addPartMNameField, String addPartANumField) {
        String additionalPart = addPartValue
        String lName = ''
        String fName = ''
        String mName = ''
        String aNumber = ''
        if (petitioner) {
            lName = petitioner.profile.lastName
            fName = petitioner.profile.firstName
            mName = petitioner.profile.middleName
        }
        result.putAll([
                (addPartField)     : additionalPart,
                (addPartLNameField): lName,
                (addPartFNameField): fName,
                (addPartMNameField): mName,
                (addPartANumField) : aNumber
        ])
    }

    static Map<String, String> assertionMapContinuationSheetHeader(ContinuationSheetHeaderInfo continuationSheetHeaderInfo, String page, String part, String item) {
        return [
                'LastName'   : continuationSheetHeaderInfo.lastName,
                'FirstName'  : continuationSheetHeaderInfo.firstName,
                'MiddleName' : continuationSheetHeaderInfo.middleName,
                'AlienNumber': continuationSheetHeaderInfo.alienNumber,
                'Page'       : page,
                'Part'       : part,
                'Item'       : item
        ]
    }

    static Map<String, PdfAsserts> assertionMapPdfAssertsContinuationSheetHeader(Petitioner petitioner, String page,
                                                                                 String part, String item, String da = '') {
        [
                'LastName'   : new PdfAsserts(fieldValue: petitioner.profile.lastName, defaultAppearance: da),
                'FirstName'  : new PdfAsserts(fieldValue: petitioner.profile.firstName, defaultAppearance: da),
                'MiddleName' : new PdfAsserts(fieldValue: petitioner.profile.middleName, defaultAppearance: da),
                'AlienNumber': new PdfAsserts(fieldValue: '', defaultAppearance: da),
                'Page'       : new PdfAsserts(fieldValue: page, defaultAppearance: da),
                'Part'       : new PdfAsserts(fieldValue: part, defaultAppearance: da),
                'Item'       : new PdfAsserts(fieldValue: item, defaultAppearance: da)
        ]
    }

    static class PdfAsserts {
        String fieldValue
        String defaultAppearance
    }
}
