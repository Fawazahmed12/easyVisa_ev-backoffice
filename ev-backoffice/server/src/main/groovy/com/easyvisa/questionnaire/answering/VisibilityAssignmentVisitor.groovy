package com.easyvisa.questionnaire.answering

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.answering.rule.Outcome
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.Question
import com.easyvisa.questionnaire.services.RuleActionHandler
import com.easyvisa.questionnaire.services.RuleEvaluator
import groovy.transform.CompileStatic

@CompileStatic
class VisibilityAssignmentVisitor implements INodeInstanceVisitor {

    private SectionNodeInstance sectionInstance
    private Long packageId
    private Long applicantId
    private List<Answer> answerList

    RuleEvaluator ruleEvaluator
    RuleActionHandler ruleActionHandler

    VisibilityAssignmentVisitor(Long packageId, Long applicantId, List<Answer> answerList,
                                RuleEvaluator ruleEvaluator, RuleActionHandler ruleActionHandler) {
        this.packageId = packageId
        this.applicantId = applicantId
        this.answerList = answerList
        this.ruleEvaluator = ruleEvaluator
        this.ruleActionHandler = ruleActionHandler
    }

    @Override
    void visit(QuestionNodeInstance questionInstance) {
        questionInstance.setVisibility(true)
        Outcome outcome = ruleEvaluator.evaluateQuestion(this.answerList, questionInstance)
        NodeRuleEvaluationContext nodeRuleEvaluationContext = nodeRuleEvaluationContext(questionInstance)
        if (outcome.isSuccessfulMatch()) {
            Answer answer = questionInstance.getAnswer()
            String overriddenAnswerValue = ruleActionHandler.determineAnswer(nodeRuleEvaluationContext, answer, outcome)
            answer.setValue(overriddenAnswerValue)
            ruleActionHandler.updateVisibilityOnSuccessfulNodeRule(nodeRuleEvaluationContext)
        }
        ruleActionHandler.updateVisibilityByComputeRule(nodeRuleEvaluationContext)

        Question question = (Question) questionInstance.getDefinitionNode()
        List<EasyVisaNode> children = question.getChildrenByAnswerKey(outcome.getRelationshipType())
        List<EasyVisaNodeInstance> nodeInstances = []
        children.stream().forEach({ easyVisaNode ->
            nodeInstances.addAll(questionInstance.matchingInstancesByDefinition(easyVisaNode))
        })
        nodeInstances.each { easyNodeInstance -> easyNodeInstance.accept(this) }
    }

    @Override
    void visit(RepeatingQuestionGroupNodeInstance repeatingQuestionGroupInstance) {
        if (!this.ruleEvaluator.matchesVisibilityCondition(this.answerList, this.packageId, this.applicantId, repeatingQuestionGroupInstance)) {
            return;
        }

        repeatingQuestionGroupInstance.setVisibility(true)
        ruleActionHandler.updateVisibilityByComputeRule(nodeRuleEvaluationContext(repeatingQuestionGroupInstance))
        if(!repeatingQuestionGroupInstance.isVisibility()) {
            return;
        }

        visitAllChildren(repeatingQuestionGroupInstance)
    }

    @Override
    void visit(DocumentActionNodeInstance documentActionInstance) {

    }

    @Override
    void visit(TerminalNodeInstance terminalNodeInstance) {

    }

    @Override
    void visit(SectionNodeInstance sectionInstance) {
        this.sectionInstance = sectionInstance
        this.sectionInstance.setVisibility(true)
        visitAllChildren(sectionInstance)
    }

    @Override
    void visit(SubSectionNodeInstance subSectionInstance) {
        if (!this.ruleEvaluator.matchesVisibilityCondition(this.answerList, this.packageId, this.applicantId, subSectionInstance)) {
            return;
        }

        subSectionInstance.setVisibility(true)
        ruleActionHandler.updateVisibilityByComputeRule(nodeRuleEvaluationContext(subSectionInstance))
        if(!subSectionInstance.isVisibility()) {
            return;
        }

        visitAllChildren(subSectionInstance)
    }


    private void visitAllChildren(EasyVisaNodeInstance easyNodeInstance) {
        List<EasyVisaNodeInstance> easyVisaNodeInstanceList = easyNodeInstance.getChildren()
        for (EasyVisaNodeInstance easyVisaNodeInstance : easyVisaNodeInstanceList) {
            easyVisaNodeInstance.accept(this)
        }
    }

    SectionNodeInstance getSectionInstance() {
        return sectionInstance
    }

    void setSectionInstance(SectionNodeInstance sectionInstance) {
        this.sectionInstance = sectionInstance
    }

    private NodeRuleEvaluationContext nodeRuleEvaluationContext(EasyVisaNodeInstance easyVisaNodeInstance) {
        return new NodeRuleEvaluationContext(this.answerList, easyVisaNodeInstance, packageId, applicantId)
    }
}
