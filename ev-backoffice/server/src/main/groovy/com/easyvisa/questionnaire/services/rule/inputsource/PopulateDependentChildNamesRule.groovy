package com.easyvisa.questionnaire.services.rule.inputsource

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.rule.IDynamicInputDatasourceRule
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.meta.InputSourceType
import com.easyvisa.questionnaire.services.rule.impl.RuleComponentRegistry
import grails.compiler.GrailsCompileStatic
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct


/**
 * Section:Family Information
 * SubSection:Dependents (Children)
 * Applicant Type: Petitioner
 * Question: (Q_1269) Select Child to Auto-Fill data (If child was listed in the previous subsection)
 * Notes: Here we are getting the firstName of its(applicant) children from previous subsection (Children Information)
 **/
@CompileStatic
@Component
class PopulateDependentChildNamesRule implements IDynamicInputDatasourceRule {

    //Given Name (First name)
    private static String FIRSTNAME_FIELD_PATH = 'Sec_familyInformation/SubSec_childrenInformation/Q_1251'
    private static String RULE_NAME = 'PopulateDependentChildNamesRule'
    private static String NONE_VALUE = '--None--'

    @Autowired
    RuleComponentRegistry ruleComponentRegistry

    @PostConstruct
    void register() {
        ruleComponentRegistry.registerDynamicInputRule(RULE_NAME, this)
    }


    @Override
    @GrailsCompileStatic(TypeCheckingMode.SKIP)
    InputSourceType generateInputSourceType(NodeRuleEvaluationContext ruleEvaluationContext) {
        List<Answer> childrenFirstNameAnswerList = Answer.findAllByPackageIdAndApplicantIdAndPathIlike(ruleEvaluationContext.packageId,
                ruleEvaluationContext.applicantId, "${FIRSTNAME_FIELD_PATH}%")

        //EV-3330 Adding a --None-- value to the list to allow the user to add additional child
        List<InputSourceType.ValueMap> values = new ArrayList<>()
        values.add(new InputSourceType.ValueMap(NONE_VALUE))

        if (childrenFirstNameAnswerList.isEmpty()) {
            return new InputSourceType(RULE_NAME, values)
        }

        childrenFirstNameAnswerList.each { childFirstNameAnswer ->
            values.add(new InputSourceType.ValueMap(childFirstNameAnswer.getValue()))
        }

        return new InputSourceType(RULE_NAME, values)
    }

}
