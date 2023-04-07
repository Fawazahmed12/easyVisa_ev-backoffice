package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AlertService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
public class AlienFianceWarningPrevYearRule extends BaseAlienFianceRule {
    final private static String RULE_NAME = "AlienFianceWarningActionRule";
    final private static String ALIEN_FIANCE_FIELD_PATH = "Sec_1/SubSec_1/Q_13"; // This might follow a convention

    private static String WARNING_TEMPLATE = "Your client [Insert client name] answered 'Yes' to the question: '<span class='warning-question'>Have you had a petition of Alien Fiance(e) approved by the USCIS within the previous two years?</span>'.";

    AlertService alertService;

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    @Override
    public void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this);
    }

    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext nodeRuleEvaluationContext, Answer previousAnswer) {
        this.createPackageWarning(WARNING_TEMPLATE, alertService, nodeRuleEvaluationContext);
    }

    @Override
    String getFieldPath() {
        return ALIEN_FIANCE_FIELD_PATH;
    }
}
