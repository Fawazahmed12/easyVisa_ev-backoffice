package com.easyvisa


import java.util.stream.Collectors

class FormContinuationSheetPrintCommand implements grails.validation.Validateable {

    Long packageId
    List<String> formInfoList // its a combination of formId and applicantId, which is seperated by #
    List<String> continuationSheetInfoList
    // its a combination of continuationSheetId and applicantId, which is seperated by #

    Package getEasyVisaPackage() {
        packageId ? Package.get(packageId) : null
    }

    Integer getNumberOfFilesCount(Map<String, List<Long>> formPrintParams, Map<String, List<Long>> continuationSheetPrintParams) {
        def formInfoList = formPrintParams.keySet().stream().collect(Collectors.toList());
        def continuationSheetInfoList = continuationSheetPrintParams.values().stream().collect(Collectors.toList());
        return [formInfoList, continuationSheetInfoList].flatten().size()
    }


    Map<String, List<Long>> constructFormPrintParams() {
        Map<String, List<Long>> formPrintParams = this.generatePdfInfoMapper(this.formInfoList);
        return formPrintParams;
    }


    Map<String, List<Long>> constructContinuationSheetPrintParams() {
        Map<String, List<Long>> continuationSheetPrintParams = this.generatePdfInfoMapper(this.continuationSheetInfoList);
        return continuationSheetPrintParams;
    }


    private Map<String, List<Long>> generatePdfInfoMapper(List<String> inputPdfInfoList) {
        Map<String, List<Long>> outputPrintParams = new HashMap<>();
        if (inputPdfInfoList?.size() != 0) {
            inputPdfInfoList.inject(outputPrintParams) { accumulator, item ->
                String[] pdfInfoItems = item.split('#')
                String pdfId = pdfInfoItems[0];
                Long applicantId = pdfInfoItems[1] as Long;
                List<Long> applicantIdList = accumulator[pdfId] ?: [];
                applicantIdList.add(applicantId);
                accumulator[pdfId] = applicantIdList
                return accumulator;
            };
        }
        return outputPrintParams;
    }
}
