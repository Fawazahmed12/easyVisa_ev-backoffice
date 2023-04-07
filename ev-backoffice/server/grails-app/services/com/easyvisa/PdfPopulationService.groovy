package com.easyvisa

import com.easyvisa.pdffilling.PdfFieldContext
import com.easyvisa.pdffilling.PdfFieldHandler
import com.easyvisa.pdffilling.PdfFieldHandlerRegesrty
import com.easyvisa.questionnaire.QuestionnaireVersion
import com.easyvisa.questionnaire.answering.FieldMappingDetail
import com.easyvisa.questionnaire.answering.PdfFieldDetail
import com.easyvisa.questionnaire.answering.PdfFieldExpressionInfo
import com.easyvisa.questionnaire.model.ContinuationSheet
import com.easyvisa.questionnaire.model.Form
import com.easyvisa.questionnaire.repositories.FormDAO
import com.easyvisa.questionnaire.services.ContinuationSheetService
import com.easyvisa.questionnaire.services.PdfFieldPrintingParams
import com.easyvisa.questionnaire.services.QuestionnaireService
import com.easyvisa.utils.PdfUtils
import grails.gorm.transactions.Transactional
import org.apache.commons.io.Charsets
import org.apache.pdfbox.multipdf.Overlay
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm
import org.apache.pdfbox.util.Matrix
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.util.ResourceUtils

import java.awt.*
import java.util.List
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

import static com.easyvisa.utils.PdfUtils.PDF_MIMETYPE
import static com.easyvisa.utils.PdfUtils.ZIP_MIMETYPE

@Service
class PdfPopulationService {

    @Autowired
    ContinuationSheetService continuationSheetService

    @Autowired
    PackageQuestionnaireService packageQuestionnaireService

    @Autowired
    QuestionnaireService questionnaireService

    @Autowired
    FormDAO formDAO;

    @Transactional
    Map<String, Object> getPdf(Long packageId, Long applicantId, String formId, String continuationSheetId,
                               Integer continuationFileNumber, Boolean hasFormCompleted = true) {
        PdfFieldPrintingParams pdfFieldPrintingParams = new PdfFieldPrintingParams(packageId:packageId,
                applicantId:applicantId, formId:formId, 'continuationSheetId':continuationSheetId)
        PdfFieldExpressionInfo pdfFieldExpressionInfo =
                packageQuestionnaireService.fetchPdfFieldExpressions(pdfFieldPrintingParams)

        QuestionnaireVersion questionnaireVersion = questionnaireService.findQuestionnaireVersion(packageId)
        Map formsDesc = getFormDesc(questionnaireVersion.questVersion, pdfFieldExpressionInfo, formId, continuationSheetId)
        Map checkedData = getPopulationData(formsDesc.continuation, continuationSheetId, pdfFieldExpressionInfo)

        Integer filesToGenerate = 1
        String filename = formsDesc.filename.replaceAll('.pdf', "#${applicantId}.pdf")
        if (formsDesc.continuation && continuationFileNumber == null
                && !checkedData.populationData[0]?.fieldMappingDetail?.continuationSheetRule) {
            filesToGenerate = checkedData.maxFiles
        }
        if (filesToGenerate > 1) {
            filename = "${UUID.randomUUID().toString()}.zip";
        }

        Boolean hasQuestionnaireFormCompleted = (hasFormCompleted == null) ? true : hasFormCompleted;
        String formName = prepareFormName(questionnaireVersion.questVersion, formsDesc.formname)
        InputStream file = fillInPdf(formName, checkedData.populationData, formsDesc.continuation,
                continuationFileNumber, filesToGenerate, hasQuestionnaireFormCompleted)
        ['filename':filename, 'mimetype':getMimetype(filesToGenerate), 'file':file]
    }

    Map<String, Object> generateFormPdfByApplicants(Long packageId, List<Long> applicantIdList, String formId, Boolean hasFormCompleted) {
        QuestionnaireVersion questionnaireVersion = questionnaireService.findQuestionnaireVersion(packageId)
        List<PdfFieldExpressionInfo> pdfFieldExpressionInfoList = applicantIdList.collect {
            PdfFieldPrintingParams pdfFieldPrintingParams = new PdfFieldPrintingParams(packageId:packageId,
                    applicantId:it, formId:formId)
            PdfFieldExpressionInfo pdfFieldExpressionInfo =
                    packageQuestionnaireService.fetchPdfFieldExpressions(pdfFieldPrintingParams);
            return pdfFieldExpressionInfo;
        };

        Map formsDesc = getFormDesc(questionnaireVersion.questVersion, null, formId, null)

        Integer filesToGenerate = 1
        String filename = formsDesc.filename
        Boolean hasQuestionnaireFormCompleted = (hasFormCompleted == null) ? true : hasFormCompleted;
        String formName = prepareFormName(questionnaireVersion.questVersion, formsDesc.formname)

        List<PdfFieldDetail> populationData = [];
        pdfFieldExpressionInfoList.each {
            populationData.addAll(it.pdfFieldDetailList);
        }

        InputStream file = fillInPdf(formName, populationData, formsDesc.continuation,
                null, filesToGenerate, hasQuestionnaireFormCompleted)

        ['filename':filename, 'mimetype':getMimetype(filesToGenerate), 'file':file]
    }

    Map<String, Object> getBlankFormPdf(String formId, String questVersion) {
        Map formsDesc = getFormDesc(questVersion, null, formId, null)
        Integer filesToGenerate = 1
        Boolean hasFormCompleted = true
        String filename = formsDesc.filename
        String formname = prepareFormName(questVersion, formsDesc.formname)
        InputStream file = fillInPdf(formname, [], false, null, filesToGenerate, hasFormCompleted)
        ['filename':filename, 'mimetype':getMimetype(filesToGenerate), 'file':file]
    }

    private Map getPopulationData(Boolean continuation, String continuationSheetId,
                                  PdfFieldExpressionInfo pdfFieldExpressionInfo) {
        List<PdfFieldDetail> populationData = []
        Integer maxFiles = 1
        if (continuation) {
            pdfFieldExpressionInfo.pdfFieldDetailList.each {
                if (it.fieldMappingDetail.continuationSheetEasyVisaId
                        && continuationSheetId == it.fieldMappingDetail.continuationSheetEasyVisaId) {
                    populationData << it
                    maxFiles = getContinuationMaxFiles(maxFiles, it)
                }
            }
        } else {
            populationData = pdfFieldExpressionInfo.pdfFieldDetailList
        }
        ['maxFiles':maxFiles, 'populationData':populationData]
    }

    private Map getFormDesc(String questVersion, PdfFieldExpressionInfo pdfFieldExpressionInfo,
                            String formId, String continuationSheetId) {
        String filename
        String formname
        Boolean continuation = false
        if (formId) {
            Form form = formDAO.getFormById(questVersion, formId);
            filename = form.getPdfForm()
            formname = filename
        } else {
            pdfFieldExpressionInfo.pdfFieldDetailList.find {
                if (it.fieldMappingDetail.continuationSheetEasyVisaId
                        && (continuationSheetId == it.fieldMappingDetail?.continuationSheetEasyVisaId)) {
                    filename = getPdfName(it.fieldMappingDetail.continuationSheetDisplayName)
                    formname = getPdfName(it.fieldMappingDetail.continuationSheetName)
                    return true
                }
            }
            if (!formname) {
                ContinuationSheet continuationSheet = continuationSheetService.continuationSheetById(questVersion,
                        continuationSheetId)
                filename = getPdfName(continuationSheet.displayName)
                formname = getPdfName(continuationSheet.sheetName)
            }
            continuation = true
        }
        ['continuation':continuation, 'filename':filename, 'formname':formname]
    }

    InputStream fillInPdf(String filename, List<PdfFieldDetail> data, Boolean continuation,
                          Integer contFileNum, Integer filesToGenerate, Boolean hasFormCompleted) {
        if (!filename) {
            throwException("PDF form name is not provided")
        }
        String formLocation = getFormLocation(continuation)
        if (filesToGenerate > 1) {
            return generateMultipleContinuations(filesToGenerate, formLocation, filename, data, continuation,
                    hasFormCompleted)
        } else {
            return generatePdf(formLocation, filename, data, continuation, contFileNum, hasFormCompleted)
        }
    }

    private InputStream generateMultipleContinuations(int filesToGenerate, String formLocation, String filename,
                                                      List<PdfFieldDetail> populationData, boolean continuation,
                                                      boolean hasFormCompleted) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ZipOutputStream zip = new ZipOutputStream(baos, Charsets.UTF_8)
        for (Integer i = 0; i < filesToGenerate; i++) {
            InputStream result = generatePdf(formLocation, filename, populationData, continuation, i, hasFormCompleted)
            zip.putNextEntry(new ZipEntry(filename.replaceAll('.pdf', "_${i + 1}.pdf")))
            zip.write(result.getBytes())
            zip.closeEntry()
            result.close()
        }
        zip.close()
        new ByteArrayInputStream(baos.toByteArray())
    }

    private InputStream generatePdf(String formLocation, String filename, List<PdfFieldDetail> populationData,
                                    boolean continuation, Integer contFileNum, boolean hasFormCompleted) {
        InputStream result = null
        PDDocument.load(ResourceUtils.getFile("classpath:pdf/${formLocation}/${filename}")).withCloseable { doc ->
            PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm()
            List<String> processedContinuations = []
            populationData.each {
                FieldMappingDetail mappingDetail = it.fieldMappingDetail
                String continuationSheetId = mappingDetail.continuationSheetEasyVisaId
                if (!processedContinuations.contains(continuationSheetId)) {
                    PdfFieldHandler handler = PdfFieldHandlerRegesrty.getHandler(mappingDetail.fieldType)

                    handler.populateField(new PdfFieldContext('pdfFieldDetail':it, 'acroForm':acroForm,
                            'pdfFieldDetailList':populationData, 'filename':filename, 'continuation':continuation,
                            continuationFileNumber:contFileNum))
                    if (continuationSheetId) {
                        processedContinuations << continuationSheetId
                    }
                }
            }
            if (!hasFormCompleted) {
                this.addOverlayText(doc)
            }
            result = writeResult(doc)
        }
        result
    }

    private String getMimetype(int filesToGenerate) {
        if (filesToGenerate > 1) {
            return ZIP_MIMETYPE
        }
        PDF_MIMETYPE
    }

    private String getFormLocation(boolean continuation) {
        if (continuation) {
            return 'continuations'
        }
        'forms'
    }

    private String getPdfName(String pdfName) {
        pdfName + '.pdf'
    }

    private String prepareFormName(String questVersion, String formname) {
        "${questVersion}/${formname}"
    }

    private Integer getContinuationMaxFiles(Integer maxFiles, PdfFieldDetail it) {
        Integer filesCount = 1
        Integer maxIndex = it.answerValueObjectList.max { it.index }.index
        if (maxIndex != null) {
            filesCount = maxIndex + 1 - it.fieldMappingDetail.formFieldCount
            filesCount = Math.ceil(filesCount /
            ((it.fieldMappingDetail.fieldExpressions.size() - it.fieldMappingDetail.formFieldCount) ?: 1)) as Integer
        }
        [maxFiles, filesCount].max()
    }

    private InputStream writeResult(PDDocument doc) throws IOException {
        OutputStream result = new ByteArrayOutputStream()
        doc.save(result)
        new ByteArrayInputStream(result.toByteArray())
    }

    private void addOverlayText(PDDocument pdDocument) {
        PDDocument overlayDoc = this.generateOverlayDocument("INCOMPLETE");
        // Using the Overlay object add the map of properties to the PDF
        Overlay overlay = new Overlay();
        overlay.setInputPDF(pdDocument);
        overlay.setDefaultOverlayPDF(overlayDoc)
        overlay.setOverlayPosition(Overlay.Position.FOREGROUND);
        overlay.overlay(new HashMap<Integer, String>())
    }

    private PDDocument generateOverlayDocument(String overlayText) {
        PDDocument pdDocument = new PDDocument(); // Create a Page object
        PDPage pdPage = new PDPage();
        pdDocument.addPage(pdPage); // Add the page to the document and save the document to a desired file.

        int marginTop = 30; // Or whatever margin you want.
        PDFont font = PDType1Font.HELVETICA_BOLD; // Or whatever font you want.
        int fontSize = 60; // Or whatever font size you want.
        float overlayTextWidth = font.getStringWidth(overlayText) / 1000 * fontSize;
        float overlayTextHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
        float overlayTextXPos = (float) ((pdPage.getMediaBox().getWidth() - overlayTextWidth) / 2);
        float overlayTextYPos = (float) ((pdPage.getMediaBox().getHeight() - overlayTextHeight) / 2);

        PDPageContentStream pdPageContentStream = new PDPageContentStream(pdDocument, pdPage);
        // Lets try a different font and size
        pdPageContentStream.beginText();
        pdPageContentStream.setFont(font, fontSize);
        pdPageContentStream.setNonStrokingColor(new Color(255, 0, 0, 120));
        pdPageContentStream.transform(Matrix.getRotateInstance(Math.PI / 6, overlayTextXPos, overlayTextYPos));
        pdPageContentStream.showText(overlayText);
        pdPageContentStream.endText();
        // Once all the content is written, close the stream
        pdPageContentStream.close();
        return pdDocument;
    }

    private void throwException(String msg) {
        log.error(msg)
        throw new RuntimeException(msg)
    }

}
