package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.*
import com.easyvisa.enums.PackageStatus
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.BaseComputeRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.ApplicantType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class AutoSyncUserProfileRule extends BaseComputeRule {

    private static String RULE_NAME = "AutoSyncUserProfileRule";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    ProfileService profileService;

    PackageService packageService

    PackageQuestionnaireService packageQuestionnaireService

    @PostConstruct
    @Override
    void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this);
    }

    @Override
    Outcome evaluateOutcome(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance()
        Answer answer = questionNodeInstance.getAnswer();
        return new Outcome(answer.getValue(), true);
    }


    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance();
        String ruleParam = questionNodeInstance.getDefinitionNode().getRuleParam();
        Answer answer = questionNodeInstance.getAnswer();
        Applicant applicant = Applicant.get(nodeRuleEvaluationContext.applicantId);
        Profile profile = applicant.profile;
        profile[ruleParam] = answer.value ?: '';
        this.profileService.saveProfile(profile);
        if(['firstName','middleName','lastName'].contains(ruleParam)) {
            this.updateNameFieldsInOtherOpenPackages(nodeRuleEvaluationContext);
        }
    }


    void updateNameFieldsInOtherOpenPackages(NodeRuleEvaluationContext nodeRuleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) nodeRuleEvaluationContext.getEasyVisaNodeInstance();
        String fieldName = questionNodeInstance.getDefinitionNode().getRuleParam();
        Answer sourceAnswer = questionNodeInstance.getAnswer();
        Package aPackage = Package.get(nodeRuleEvaluationContext.packageId);
        Applicant sourceApplicant = Applicant.get(nodeRuleEvaluationContext.applicantId);

        List<Package> applicantMatchedPackages = packageService.fetchPackagesByApplicantAndStatus(sourceApplicant, PackageStatus.OPEN)
                .findAll { it.id != aPackage.id };
        applicantMatchedPackages.each {
            String questionFieldPath = this.findQuestionnaireFieldPath(it, sourceApplicant, fieldName);
            this.saveOtherPackageNameFieldAnswer(questionFieldPath, sourceAnswer.value, it.id, sourceApplicant.id);
        }
    }


    private saveOtherPackageNameFieldAnswer(String questionFieldPath, String answerValue, Long packageId, Long applicantId) {
        List<String> pathInfoList = questionFieldPath.split("/");
        Answer nameFieldAnswer = new Answer(packageId: packageId, applicantId: applicantId,
                sectionId: pathInfoList[0], subsectionId: pathInfoList[1], questionId: pathInfoList[2],
                value: answerValue, path: questionFieldPath);
        Boolean evaluateRule = false;
        this.packageQuestionnaireService.saveAnswerAndUpdateSectionCompletionStatus(nameFieldAnswer, evaluateRule);
    }


    private Map<String, String> getApplicantFieldPathMapper() {
        Map<String, String> applicantFieldPathMap = new HashMap<>();
        applicantFieldPathMap["${ApplicantType.Petitioner.name()}_firstName"] = 'Sec_2/SubSec_5/Q_32';
        applicantFieldPathMap["${ApplicantType.Petitioner.name()}_middleName"] = 'Sec_2/SubSec_5/Q_33';
        applicantFieldPathMap["${ApplicantType.Petitioner.name()}_lastName"] = 'Sec_2/SubSec_5/Q_34';
        applicantFieldPathMap["${ApplicantType.Beneficiary.name()}_firstName"] = 'Sec_nameForBeneficiary/SubSec_currentLegalNameForBeneficiary/Q_1901';
        applicantFieldPathMap["${ApplicantType.Beneficiary.name()}_middleName"] = 'Sec_nameForBeneficiary/SubSec_currentLegalNameForBeneficiary/Q_1902';
        applicantFieldPathMap["${ApplicantType.Beneficiary.name()}_lastName"] = 'Sec_nameForBeneficiary/SubSec_currentLegalNameForBeneficiary/Q_1903';
        return applicantFieldPathMap;
    }

    private String findQuestionnaireFieldPath(Package aPackage, Applicant sourceApplicant, String fieldName) {
        Map<String, String> applicantFieldPathMap = this.getApplicantFieldPathMapper();
        ApplicantType applicantType = aPackage.beneficiaries.contains(sourceApplicant) ? ApplicantType.Beneficiary : ApplicantType.Petitioner;
        return applicantFieldPathMap["${applicantType.name()}_${fieldName}"];
    }
}
