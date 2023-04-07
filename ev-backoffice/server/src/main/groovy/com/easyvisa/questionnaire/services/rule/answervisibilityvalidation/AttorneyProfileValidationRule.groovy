package com.easyvisa.questionnaire.services.rule.answervisibilityvalidation

import com.easyvisa.Address
import com.easyvisa.LegalRepresentative
import com.easyvisa.Package
import com.easyvisa.Profile
import com.easyvisa.enums.Country
import com.easyvisa.enums.State
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.QuestionNodeInstance
import com.easyvisa.questionnaire.answering.rule.IAnswerVisibilityValidationRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.model.RelationshipTypeConstants
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors

/**
 * Section: Admin
 * Question: All the questions
 *
 * This rule applies to all the questions in this section.. By default visibility o this section is always false.
 * But we need to populate its answer value for pdf printing.. so by using this rule we are populating
 * this section answers using its param values
 */

@Component
class AttorneyProfileValidationRule implements IAnswerVisibilityValidationRule {

    private static String RULE_NAME = 'AttorneyProfileValidationRule'

    private static String ORGANIZATION_NAME_PARAM = "ORGANIZATION_NAME_PARAM";
    private static String USCIS_ACCOUNT_NO_PARAM = "USCIS_ACCOUNT_NO_PARAM";
    private static String STATE_CODE_PARAM = "STATE_CODE_PARAM";
    private static String COUNTRY_NAME_PARAM = "COUNTRY_NAME_PARAM";
    private static String ATTORNEY_PARAM = "ATTORNEY_PARAM";
    private static String ATTORNEY_PROFILE_PARAM = "ATTORNEY_PROFILE_PARAM";
    private static String ATTORNEY_ADDRESS_PARAM = "ATTORNEY_ADDRESS_PARAM";
    private static String DOES_ATTORNEY_HAVE_SECONDARY_ADDRESS_PARAM = "DOES_ATTORNEY_HAVE_SECONDARY_ADDRESS_PARAM";
    private static String ATTORNEY_SECONDARY_ADDRESS_PARAM_1 = "ATTORNEY_SECONDARY_ADDRESS_PARAM_1";
    private static String ATTORNEY_SECONDARY_ADDRESS_PARAM_2 = "ATTORNEY_SECONDARY_ADDRESS_PARAM_2";
    private static String EMPTY_PARAM = "EMPTY_PARAM";

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerAnswerVisibilityValidationRule(RULE_NAME, this);
    }


    @Override
    void populatePdfFieldAnswer(NodeRuleEvaluationContext ruleEvaluationContext) {
        QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) ruleEvaluationContext.getEasyVisaNodeInstance()
        String answerVisibilityValidationRuleParam = questionNodeInstance.getAnswerVisibilityValidationRuleParam();
        Package aPackage = Package.get(ruleEvaluationContext.packageId);
        String answerValue = this.getAttorneyAnswerValue(aPackage, answerVisibilityValidationRuleParam);
        Answer answer = questionNodeInstance.getAnswer();
        answer.setValue(answerValue);
    }


    private String getAttorneyAnswerValue(Package aPackage, String paramName) {
        String answerValue = "";
        LegalRepresentative attorney = aPackage.attorney;
        Map<String, String> populateParamNameMap = this.getPopulateParamNameMapper();
        String populateParamType = populateParamNameMap[paramName];

        switch (populateParamType) {
            case ORGANIZATION_NAME_PARAM:
                answerValue = this.populatePracticeValue(aPackage,attorney,paramName)
                break;

            case USCIS_ACCOUNT_NO_PARAM:
                answerValue = (attorney[paramName] ?: '').replaceAll('-', '');
                break;

            case ATTORNEY_PARAM:
                answerValue = attorney[paramName] ?: '';
                break;

            case ATTORNEY_PROFILE_PARAM:
                answerValue = this.populateAttorneyProfileValue(attorney, paramName);
                break;

            case ATTORNEY_ADDRESS_PARAM:
                answerValue = this.populateAttorneyAddressValue(attorney, paramName);
                break;

            case DOES_ATTORNEY_HAVE_SECONDARY_ADDRESS_PARAM:
                answerValue = this.populateAttorneyHavingSecondaryAddressDescValue(attorney, paramName);
                break;

            case ATTORNEY_SECONDARY_ADDRESS_PARAM_1:
                answerValue = this.populateAttorneySecondaryAddressDescValue1(attorney, paramName);
                break;

            case ATTORNEY_SECONDARY_ADDRESS_PARAM_2:
                answerValue = this.populateAttorneySecondaryAddressDescValue2(attorney, paramName);
                break;

            case STATE_CODE_PARAM:
                answerValue = this.populateAttorneyStateValue(attorney, paramName);
                break;

            case COUNTRY_NAME_PARAM:
                answerValue = this.populateAttorneyCountryValue(attorney, paramName);
                break;
        }
        return answerValue;
    }


    private String populateAttorneyProfileValue(LegalRepresentative attorney, String paramName) {
        String answerValue = "";
        Profile attorneyProfile = attorney.profile;
        if (attorneyProfile) {
            String[] ruleParams = paramName.split("/");
            String profileParamName = ruleParams[1];
            answerValue = attorneyProfile[profileParamName] ?: '';
        }
        return answerValue;
    }
    private String populatePracticeValue(Package aPackage,LegalRepresentative attorney, String paramName) {
        String answerValue = "";
        if(aPackage.organization.isSoloPractice()){
            answerValue = this.populateAttorneyProfileValue(attorney,paramName)
        }else if(aPackage.organization.isLawFirm()){
            answerValue = aPackage.organization?.name
        }
        return answerValue;
    }



    private String populateAttorneyAddressValue(LegalRepresentative attorney, String paramName) {
        String answerValue = "";
        Profile attorneyProfile = attorney.profile;
        if (attorneyProfile && attorneyProfile.address) {
            String[] ruleParams = paramName.split("/");
            String addressParamName = ruleParams[2];
            Address attorneyAddress = attorneyProfile.address;
            answerValue = attorneyAddress[addressParamName] ?: '';
        }
        return answerValue;
    }

    private String populateAttorneyHavingSecondaryAddressDescValue(LegalRepresentative attorney, String paramName) {
        String answerValue = "";
        Profile attorneyProfile = attorney.profile;
        if (attorneyProfile && attorneyProfile.address) {
            Address attorneyAddress = attorneyProfile.address;
            answerValue = attorneyAddress.line2 ? RelationshipTypeConstants.YES.value : RelationshipTypeConstants.NO.value;
        }
        return answerValue;
    }


    private String populateAttorneySecondaryAddressDescValue1(LegalRepresentative attorney, String paramName) {
        String answerValue = "";
        Profile attorneyProfile = attorney.profile;
        if (attorneyProfile && attorneyProfile.address) {
            Address attorneyAddress = attorneyProfile.address;
            def descriptionNameHandler = { Matcher matcher, String matchedDescriptionName -> matchedDescriptionName };
            answerValue = this.getSecondaryAddressDescriptionValue(attorneyAddress.line2, descriptionNameHandler);
        }
        return answerValue;
    }


    private String populateAttorneySecondaryAddressDescValue2(LegalRepresentative attorney, String paramName) {
        String answerValue = "";
        Profile attorneyProfile = attorney.profile;
        if (attorneyProfile && attorneyProfile.address) {
            Address attorneyAddress = attorneyProfile.address;
            def descriptionValueHandler = { Matcher matcher, String matchedDescriptionName -> matcher.replaceAll("") };
            answerValue = this.getSecondaryAddressDescriptionValue(attorneyAddress.line2, descriptionValueHandler);
        }
        return answerValue;
    }


    private String getSecondaryAddressDescriptionValue(String line2Value, Closure descriptionNameHandler) {
        String answerValue = "";
        if (!line2Value) {
            return answerValue;
        }

        String floorValue = "floor";
        String floorRegex = String.join("|", ['flr', 'floor', 'flor'].stream().map({ word -> "\\b" + word + "\\b" }).collect(Collectors.toList()));
        Pattern FLOOR_PATTERN = Pattern.compile(floorRegex, Pattern.CASE_INSENSITIVE);
        Matcher floorMatcher = FLOOR_PATTERN.matcher(line2Value);
        if (floorMatcher.find()) {
            return descriptionNameHandler(floorMatcher, floorValue);
        }


        String suiteValue = "suite";
        String suiteRegex = String.join("|", ['sute', 'suite', 'ste'].stream().map({ word -> "\\b" + word + "\\b" }).collect(Collectors.toList()));
        Pattern SUITE_PATTERN = Pattern.compile(suiteRegex, Pattern.CASE_INSENSITIVE)
        Matcher suiteMatcher = SUITE_PATTERN.matcher(line2Value);
        if (suiteMatcher.find()) {
            return descriptionNameHandler(suiteMatcher, suiteValue);
        }

        String apartmentValue = "apartment";
        String apartmentRegex = String.join("|", ['apt', 'apartment', 'apart'].stream().map({ word -> "\\b" + word + "\\b" }).collect(Collectors.toList()));
        Pattern APARTMENT_PATTERN = Pattern.compile(apartmentRegex, Pattern.CASE_INSENSITIVE)
        Matcher apartmentMatcher = APARTMENT_PATTERN.matcher(line2Value);
        if (apartmentMatcher.find()) {
            return descriptionNameHandler(apartmentMatcher, apartmentValue);
        }

        return line2Value;
    }

    private String populateAttorneyCountryValue(LegalRepresentative attorney, String paramName) {
        String answerValue = "";
        Profile attorneyProfile = attorney.profile;
        if (attorneyProfile && attorneyProfile.address) {
            String[] ruleParams = paramName.split("/");
            String addressParamName = ruleParams[2];
            Address attorneyAddress = attorneyProfile.address;
            Country selectedCountry = attorneyAddress[addressParamName];
            answerValue = selectedCountry?.displayName;
        }
        return answerValue;
    }


    private String populateAttorneyStateValue(LegalRepresentative attorney, String paramName) {
        String answerValue = "";
        Profile attorneyProfile = attorney.profile;
        if (attorneyProfile && attorneyProfile.address) {
            String[] ruleParams = paramName.split("/");
            String addressParamName = ruleParams[2];
            Address attorneyAddress = attorneyProfile.address;
            State selectedState = attorneyAddress[addressParamName];
            answerValue = selectedState?.code;
        }
        return answerValue;
    }


    private Map<String, String> getPopulateParamNameMapper() {
        Map<String, String> populateParamNameMap = new HashMap<>();
        populateParamNameMap['officePhone'] = ATTORNEY_PARAM;
        populateParamNameMap['faxNumber'] = ATTORNEY_PARAM;
        populateParamNameMap['extension'] = EMPTY_PARAM; // TODO..
        populateParamNameMap['mobilePhone'] = ATTORNEY_PARAM;
        populateParamNameMap['g28number'] = EMPTY_PARAM; // TODO..
        populateParamNameMap['uscisOnlineAccountNo'] = USCIS_ACCOUNT_NO_PARAM
        populateParamNameMap['stateBarNumber'] = ATTORNEY_PARAM;
        populateParamNameMap['profile/lastName'] = ATTORNEY_PROFILE_PARAM;
        populateParamNameMap['profile/firstName'] = ATTORNEY_PROFILE_PARAM;
        populateParamNameMap['profile/practiceName'] = ORGANIZATION_NAME_PARAM;
        populateParamNameMap['profile/email'] = ATTORNEY_PROFILE_PARAM;
        populateParamNameMap['profile/address/country'] = COUNTRY_NAME_PARAM;
        populateParamNameMap['profile/address/line1'] = ATTORNEY_ADDRESS_PARAM;
        populateParamNameMap['doesAddressHaveSecondaryDesc'] = DOES_ATTORNEY_HAVE_SECONDARY_ADDRESS_PARAM;
        populateParamNameMap['secondaryAdrressDesc'] = ATTORNEY_SECONDARY_ADDRESS_PARAM_1;
        populateParamNameMap['profile/address/line2'] = ATTORNEY_SECONDARY_ADDRESS_PARAM_2;
        populateParamNameMap['profile/address/city'] = ATTORNEY_ADDRESS_PARAM;
        populateParamNameMap['profile/address/state'] = STATE_CODE_PARAM;
        populateParamNameMap['profile/address/province'] = ATTORNEY_ADDRESS_PARAM;
        populateParamNameMap['profile/address/zipCode'] = ATTORNEY_ADDRESS_PARAM;
        populateParamNameMap['profile/address/postalCode'] = ATTORNEY_ADDRESS_PARAM;
        return populateParamNameMap;
    }


    @Override
    Boolean validateAnswerVisibility(NodeRuleEvaluationContext ruleEvaluationContext) {
        return true;
    }
}
