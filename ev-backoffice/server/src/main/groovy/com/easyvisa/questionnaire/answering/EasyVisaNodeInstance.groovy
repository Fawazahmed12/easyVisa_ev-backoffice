package com.easyvisa.questionnaire.answering

import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.rule.AnswerEvaluationContext
import com.easyvisa.questionnaire.answering.rule.IAnswerVisibilityEvaluator
import com.easyvisa.questionnaire.model.EasyVisaNode

import java.time.LocalDate
import java.util.function.Predicate
import java.util.stream.Collectors

class EasyVisaNodeInstance {


    private String id
    private String name
    private String questVersion
    private String displayText
    private String ruleClassName
    private String ruleParam
    private String visibilityRuleClassName;
    private String visibilityRuleParam;
    private List<EasyVisaNodeInstance> children
    private boolean visibility
    private int order
    private String displayOrderChildren
    private DisplayTextLanguage displayTextLanguage
    private LocalDate currentDate

    protected EasyVisaNodeInstance(EasyVisaNode easyVisaNode,
                                   DisplayTextLanguage displayTextLanguage,
                                   LocalDate currentDate) {
        children = []
        this.id = easyVisaNode.getId()
        this.name = easyVisaNode.getName()
        this.questVersion = easyVisaNode.getQuestVersion()
        this.order = easyVisaNode.getOrder()
        this.displayText = easyVisaNode.getDisplayText()
        this.ruleClassName = easyVisaNode.getRuleClassName()
        this.ruleParam = easyVisaNode.getRuleParam()
        this.visibilityRuleClassName = easyVisaNode.getVisibilityRuleClassName()
        this.visibilityRuleParam = easyVisaNode.getVisibilityRuleParam()
        this.displayOrderChildren = easyVisaNode.getDisplayOrderChildren()
        this.displayTextLanguage = displayTextLanguage
        this.currentDate = currentDate
    }

    void addChild(EasyVisaNodeInstance child) {
        this.children.add(child)
    }

    List<EasyVisaNodeInstance> getChildren() {
        return children
    }

    void setChildren(List<EasyVisaNodeInstance> children) {
        this.children = children
    }

    boolean isVisibility() {
        return this.visibility;
    }

    void setVisibility(boolean visibile) {
        this.visibility = visibile;
    }

    String getDisplayText() {
        return displayText
    }

    void setDisplayText(String displayText) {
        this.displayText = displayText
    }

    String getRuleClassName() {
        return ruleClassName
    }

    void setRuleClassName(String ruleClassName) {
        this.ruleClassName = ruleClassName
    }

    String getRuleParam() {
        return ruleParam
    }

    void setRuleParam(String ruleParam) {
        this.ruleParam = ruleParam
    }

    String getVisibilityRuleClassName() {
        return visibilityRuleClassName
    }

    void setVisibilityRuleClassName(String visibilityRuleClassName) {
        this.visibilityRuleClassName = visibilityRuleClassName
    }

    String getVisibilityRuleParam() {
        return visibilityRuleParam
    }

    void setVisibilityRuleParam(String visibilityRuleParam) {
        this.visibilityRuleParam = visibilityRuleParam
    }

    String getDisplayOrderChildren() {
        return displayOrderChildren
    }

    void setDisplayOrderChildren(String displayOrderChildren) {
        this.displayOrderChildren = displayOrderChildren
    }

    void accept(INodeInstanceVisitor nodeInstanceVisitor) {
        throw new RuntimeException(' No Visit Defined for Base Class')
    }

    EasyVisaNode getDefinitionNode() {
        throw new RuntimeException('Not Implemented')
    }

    List<EasyVisaNodeInstance> matchingInstancesByDefinition(EasyVisaNode easyVisaNode) {
        return children.stream().filter({ easyNodeInstance -> easyNodeInstance.getDefinitionNode() == easyVisaNode }).collect(Collectors.toList())
    }


    void sortChildren() {
        List<EasyVisaNodeInstance> children = this.getChildren()
        List<EasyVisaNodeInstance> orderedInstances =
                children.stream().sorted(Comparator.comparing({ EasyVisaNodeInstance easyVisaNodeInstance -> easyVisaNodeInstance.order })).collect(Collectors.toList())
        this.setChildren(orderedInstances)
        for (EasyVisaNodeInstance childInstance : orderedInstances) {
            childInstance.sortChildren()
        }
    }

    int getOrder() {
        return order
    }

    void setOrder(int order) {
        this.order = order
    }

    boolean hasHideExpression() {
        return this.getDefinitionNode().hasHideExpression()
    }


    String getId() {
        id
    }

    void setId(String id) {
        this.id = id
    }

    String getName() {
        return name
    }


    void setName(String name) {
        this.name = name
    }

    String getQuestVersion() {
        return questVersion
    }

    void setQuestVersion(String questVersion) {
        this.questVersion = questVersion
    }

    DisplayTextLanguage getDisplayTextLanguage() {
        return displayTextLanguage
    }

    LocalDate getCurrentDate() {
        return this.currentDate
    }
/**
     * Any node found in the excludedNodeList will be filtered out.
     * The children will only have elements not found in the excludedNodeList
     * @param excludedNodeList
     */
    void filter(List<String> excludedNodeList) {
        List<EasyVisaNodeInstance> filteredChildren = [];
        this.children.stream().each {
            if (!excludedNodeList.contains(it.id)) {
                filteredChildren.add(it);
            }
            it.filter(excludedNodeList);
        }
        this.setChildren(filteredChildren);
    }


    List<Answer> collectAllValidAnswers(AnswerEvaluationContext answerEvaluationContext, IAnswerVisibilityEvaluator answerVisibilityEvaluator) {
        List<Answer> answerList = [];
        this.collectAnswer(answerEvaluationContext, answerVisibilityEvaluator, answerList);
        return answerList;
    }

    void collectAnswer(AnswerEvaluationContext answerEvaluationContext, IAnswerVisibilityEvaluator answerVisibilityEvaluator, List<Answer> answerList) {
        List<EasyVisaNodeInstance> visibleChildren = this.getChildren().stream()
                .filter { easyVisaNodeInstance -> easyVisaNodeInstance.hasValidEasyVisaInstance(answerEvaluationContext, answerVisibilityEvaluator) }
                .collect(Collectors.toList())
        for (EasyVisaNodeInstance easyVisaNodeInstance : visibleChildren) {
            easyVisaNodeInstance.collectAnswer(answerEvaluationContext, answerVisibilityEvaluator, answerList)
        }
    }

    Boolean hasValidEasyVisaInstance(AnswerEvaluationContext answerEvaluationContext, IAnswerVisibilityEvaluator answerVisibilityEvaluator) {
        return this.isVisibility();
    }


    public List<EasyVisaNodeInstance> flattenCollect(Predicate<EasyVisaNodeInstance> predicate = { EasyVisaNodeInstance easyVisaNodeInstance -> true}) {
        List<EasyVisaNodeInstance> flattenedList = [];
        this.flattenChildren(flattenedList, predicate);
        return flattenedList;
    }

    protected void flattenChildren(List<EasyVisaNodeInstance> flattenedList, Predicate<EasyVisaNodeInstance> predicate) {
        if(!predicate.test(this)){
            return;
        }

        flattenedList.add(this);
        List<EasyVisaNodeInstance> children = this.getChildren();
        for (EasyVisaNodeInstance easyVisaNodeInstance : children) {
            easyVisaNodeInstance.flattenChildren(flattenedList, predicate);
        }
    }
}
