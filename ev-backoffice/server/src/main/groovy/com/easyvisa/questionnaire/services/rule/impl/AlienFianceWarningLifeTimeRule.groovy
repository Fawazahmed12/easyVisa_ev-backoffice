package com.easyvisa.questionnaire.services.rule.impl

import com.easyvisa.AlertService
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class AlienFianceWarningLifeTimeRule extends BaseAlienFianceRule {

    final private static String RULE_NAME = "AlienFianceWarningLifeTimeRule";
    final private static String ALIEN_FIANCE_FIELD_PATH = "Sec_1/SubSec_1/Q_14"; // This might follow a convention

    private static String WARNING_TEMPLATE = "Your client [Insert client name] answered 'Yes' to the question: '<span class='warning-question'>Have you filed 2 or more petitions for an Alien Fiance(e) (other than for your spouse) in your lifetime?</span>'.";

    AlertService alertService;

    @Autowired
    RuleComponentRegistry ruleComponentRegistry;

    @PostConstruct
    @Override
    public void register() {
        ruleComponentRegistry.registerNodeRules(RULE_NAME, this);
    }

    @Override
    void triggerFormActionOnSuccessfulMatch(NodeRuleEvaluationContext answerContext, Answer previousAnswer) {
        this.createPackageWarning(WARNING_TEMPLATE, alertService, answerContext);
    }

    @Override
    String getFieldPath() {
        return ALIEN_FIANCE_FIELD_PATH;
    }
}
