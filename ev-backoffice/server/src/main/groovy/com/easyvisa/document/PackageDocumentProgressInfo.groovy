package com.easyvisa.document

import com.easyvisa.Package
import com.easyvisa.dto.PackageDocumentProgressDto
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.DateUtils

class PackageDocumentProgressInfo {

    String documentPanelName;
    int approvedDocumentCount;
    int totalDocumentCount;
    List allDocumentAttachments;

    PackageDocumentProgressInfo(String documentPanelName, int approvedDocumentCount, int totalDocumentCount, List allDocumentAttachments) {
        this.documentPanelName = documentPanelName
        this.approvedDocumentCount = approvedDocumentCount
        this.totalDocumentCount = totalDocumentCount
        this.allDocumentAttachments = allDocumentAttachments
    }


    PackageDocumentProgressDto toPackageDocumentProgressDto(Package aPackage) {
        int percentCompleted = this.getCompletedPercentage();
        PackageDocumentProgressDto documentProgressDto = new PackageDocumentProgressDto()
        documentProgressDto.with {
            name = this.documentPanelName
            packageStatus = aPackage.status.displayName
            percentComplete = percentCompleted
            Date firstDate = aPackage.opened
            elapsedDays = DateUtils.getDays(firstDate)
            dateStarted = formatDate(firstDate)
            //populate statistics for document portal progress
            if (this.allDocumentAttachments && percentCompleted == 100) {
                this.allDocumentAttachments.sort { it['lastUpdated'] }
                elapsedDays = null
                Map lastDocAttachment = this.allDocumentAttachments.last() as Map
                Date lastDate = lastDocAttachment['dateCreated'] as Date
                totalDays = DateUtils.getDays(firstDate, lastDate)
                dateCompleted = formatDate(lastDate)
            }
        }
        documentProgressDto
    }

    int getCompletedPercentage() {
        int percentCompleted = 100
        if (this.totalDocumentCount != 0) {
            BigDecimal completedPercentage = (this.approvedDocumentCount / this.totalDocumentCount) * 100
            percentCompleted = completedPercentage.toDouble().round(2) as int
        }
        return percentCompleted;
    }

    private String formatDate(Date date) {
        date.format(DateUtil.PDF_FORM_DATE_FORMAT)
    }
}
