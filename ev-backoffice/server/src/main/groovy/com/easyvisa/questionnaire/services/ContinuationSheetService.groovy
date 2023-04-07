package com.easyvisa.questionnaire.services

import com.easyvisa.Applicant
import com.easyvisa.Package
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.QuestionnaireVersion
import com.easyvisa.questionnaire.answering.ContinuationSheetHeaderInfo
import com.easyvisa.questionnaire.model.ApplicantType
import com.easyvisa.questionnaire.model.ContinuationSheet
import com.easyvisa.questionnaire.model.Form
import com.easyvisa.questionnaire.repositories.ContinuationSheetDAO
import com.easyvisa.questionnaire.repositories.FormDAO
import grails.gorm.transactions.Transactional
import grails.plugin.cache.Cacheable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ContinuationSheetService {

    @Autowired
    ContinuationSheetDAO continuationSheetDAO;

    @Autowired
    FormDAO formDAO;

    @Autowired
    QuestionnaireService questionnaireService;


    @Cacheable('fetchContinuationSheetsByForm')
    List<ContinuationSheet> fetchContinuationSheetsByForm(String questVersion, String formId) {
        return continuationSheetDAO.continuationSheetsByForm(questVersion, formId);
    }


    ContinuationSheet continuationSheetById(String questVersion, String continuationSheetId) {
        return continuationSheetDAO.continuationSheetById(questVersion, continuationSheetId);
    }

    Map<String, Set<ContinuationSheet>> fetchFormToContinuationSheetListMapper(String questVersion, List<Form> packageFormList) {
         continuationSheetDAO.fetchFormToContinuationSheetListMapper(questVersion, packageFormList)
    }

    @Transactional
    ContinuationSheetHeaderInfo getContinuationSheetHeaderByContinuationSheetId(Package aPackage, String continuationSheetId) {
        QuestionnaireVersion questionnaireVersion = this.questionnaireService.findQuestionnaireVersion(aPackage.id)
        Form form = formDAO.formByContinuationSheet(questionnaireVersion.questVersion, continuationSheetId);
        ContinuationSheetHeaderInfo continuationSheetHeaderInfo = this.getContinuationSheetHeader(aPackage, form);
        return continuationSheetHeaderInfo;
    }

    @Transactional
    ContinuationSheetHeaderInfo getContinuationSheetHeaderByFormId(Package aPackage, String formId) {
        QuestionnaireVersion questionnaireVersion = this.questionnaireService.findQuestionnaireVersion(aPackage.id);
        Form form = formDAO.getFormById(questionnaireVersion.questVersion, formId);
        ContinuationSheetHeaderInfo continuationSheetHeaderInfo = this.getContinuationSheetHeader(aPackage, form);
        return continuationSheetHeaderInfo;
    }


    private ContinuationSheetHeaderInfo getContinuationSheetHeader(Package aPackage, Form form) {
        String ALIEN_NUMBER_FIELD_PATH = 'Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_112';
        //petitionerANumberPath
        Applicant headerApplicant = aPackage.client
        if (ApplicantType.Beneficiary.name() == form.applicantType) {
            headerApplicant = aPackage.getPrincipalBeneficiary()
            ALIEN_NUMBER_FIELD_PATH = 'Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2412';
            //beneficiaryANumberPath
        }

        ContinuationSheetHeaderInfo continuationSheetHeaderInfo;
        Answer.withNewSession {
            Answer alienNumberAnswer = Answer.findByPackageIdAndApplicantIdAndPathIlike(aPackage.id, headerApplicant.id, ALIEN_NUMBER_FIELD_PATH)
            continuationSheetHeaderInfo = new ContinuationSheetHeaderInfo(
                    alienNumber: alienNumberAnswer?.value ?: '',
                    firstName: headerApplicant.profile?.firstName,
                    lastName: headerApplicant.profile?.lastName,
                    middleName: headerApplicant.profile?.middleName
            )
        }
        return continuationSheetHeaderInfo;
    }
}
