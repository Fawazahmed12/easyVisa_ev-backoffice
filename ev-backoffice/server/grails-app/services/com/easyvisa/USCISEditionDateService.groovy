package com.easyvisa


import com.easyvisa.enums.EmailTemplateType
import com.easyvisa.enums.EmailTemplateVariable
import com.easyvisa.enums.EmployeeStatus
import com.easyvisa.questionnaire.QuestionnaireVersion
import com.easyvisa.questionnaire.model.Form
import com.easyvisa.questionnaire.services.QuestionnaireService
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.ExceptionUtils
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.time.LocalDate

@Service
@GrailsCompileStatic
class USCISEditionDateService {

    @Autowired
    QuestionnaireService questionnaireService

    @Autowired
    PackageQuestionnaireVersionService packageQuestionnaireVersionService

    @Autowired
    EmailVariableService emailVariableService

    @Autowired
    EvMailService evMailService

    @Autowired
    AlertService alertService

    @Autowired
    AttorneyService attorneyService

    @Transactional
    void updateUSCISEditionDates(User currentUser, USCISEditionDatesCommand uscisEditionDatesCommand) {
        LegalRepresentative attorney = this.attorneyService.findAttorneyByUser(currentUser.id)
        Employee employee = this.attorneyService.findEmployeeByUser(currentUser.id)
        Organization organization = OrganizationEmployee.findByEmployeeAndStatus(employee, EmployeeStatus.ACTIVE)?.organization
        if (!organization) {
            throw ExceptionUtils.createUnProcessableDataException('employee.not.in.organization')
        }

        List<UscisEditionDate> savedEditionDateList = UscisEditionDate.findAllByOrganization(organization)
        List<String> modifiedUSCISFormList = []
        uscisEditionDatesCommand.uscisEditionDateList.each { USCISEditionDateCommand uscisEditionDateCommand ->

            UscisEditionDate currentUSCISEditionDate = uscisEditionDateCommand.getUSCISEditionDate(currentUser, organization)
            UscisEditionDate savedUSCISEditionDate = savedEditionDateList.find { it.formId == currentUSCISEditionDate.formId }
            if (!savedUSCISEditionDate) {
                currentUSCISEditionDate.save(failOnError: true)
            } else {
                this.addUSCISFormIfEditionDateChanged(currentUSCISEditionDate, savedUSCISEditionDate, modifiedUSCISFormList)
                savedUSCISEditionDate.editionDate = currentUSCISEditionDate.editionDate
                savedUSCISEditionDate.expirationDate = currentUSCISEditionDate.expirationDate
                savedUSCISEditionDate.lastUpdated = new Date()
                savedUSCISEditionDate.updatedBy = currentUser
                savedUSCISEditionDate.save(failOnError: true)
            }
        }

        if (modifiedUSCISFormList.size() != 0) {
            this.sendUSCISFormUpdateNotification(attorney, modifiedUSCISFormList)
        }
    }

    void addUSCISFormIfEditionDateChanged(UscisEditionDate currentUSCISEditionDate, UscisEditionDate savedUSCISEditionDate, List<String> modifiedUSCISFormList) {
        Boolean hasEditionDateChanged = DateUtil.uscisEditionDate(currentUSCISEditionDate.editionDate) != DateUtil.uscisEditionDate(savedUSCISEditionDate.editionDate)
        Boolean hasExpirationDateChanged = DateUtil.uscisEditionDate(currentUSCISEditionDate.expirationDate) != DateUtil.uscisEditionDate(savedUSCISEditionDate.expirationDate)
        if (hasEditionDateChanged || hasExpirationDateChanged) {
            modifiedUSCISFormList.add(currentUSCISEditionDate.formId)
        }
    }

    void sendUSCISFormUpdateNotification(LegalRepresentative attorney, List<String> modifiedUSCISFormList) {
        Map params = emailVariableService.addLegalRepresentative([:], attorney)
        Map emailTemplate = getSanitizedEmailContent(EmailTemplateType.USCIS_FORM_EDITION_UPDATE_TO_APPLICANTS, params)

        params[EmailTemplateVariable.LEGAL_REP_NAME.name()] = attorney.profile.name
        // String body = evMailService.evaluateTemplate(emailTemplate.content, params)
        // alertService.createAlert(alertType, client.user, emailTemplate.attorney.profile.name, body)
    }

    // [content, subject, templateType, representativeId]
    private Map getSanitizedEmailContent(EmailTemplateType templateType, Map params) {
        Map result = evMailService.generateEmailContent(templateType, params)
        String emailContent = result['content'] as String
        result['content'] = emailContent.replaceAll('<br/>', '\n')
        result
    }


    @Transactional
    def getUSCISEditionDates(String sortBy, String order) {
        QuestionnaireVersion questionnaireVersion = this.packageQuestionnaireVersionService.getLatestQuestionnaireVersion()
        List<Form> formList = this.questionnaireService.findAllForms(questionnaireVersion?.questVersion)?.collect {return it; }

        formList.sort { Form form ->
            if(['editionDate','expirationDate'].contains(sortBy)){
                String dateStrValue = form[sortBy]
                return StringUtils.isNotEmpty(dateStrValue) ? DateUtil.pdfLocalDate(dateStrValue) : ""
            }
            return form[sortBy]
        }

        if( order=="desc" ) {
            Collections.reverse(formList)
        }

       def uscisEditionDateList = formList.collect { Form form ->
            return [
                    formId        : form.id,
                    name          : form.name,
                    displayText   : form.displayText,
                    editionDate   : DateUtil.uscisEditionDate(form.editionDate),
                    expirationDate: DateUtil.uscisEditionDate(form.expirationDate)
            ]
        }
        return uscisEditionDateList
    }
}
