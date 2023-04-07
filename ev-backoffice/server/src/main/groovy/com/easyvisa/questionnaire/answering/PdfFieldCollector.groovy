package com.easyvisa.questionnaire.answering

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.answering.rule.AnswerEvaluationContext
import com.easyvisa.questionnaire.answering.rule.IAnswerVisibilityEvaluator
import com.easyvisa.questionnaire.answering.rule.NodeRuleEvaluationContext
import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.services.MetaDataMapper
import com.easyvisa.questionnaire.services.RuleActionHandler
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired

import java.util.stream.Collectors

//This class should collects all the questions which are visible in the given section..
class PdfFieldCollector {

    private AnswerEvaluationContext answerEvaluationContext;
    private IAnswerVisibilityEvaluator answerVisibilityEvaluator;
    private RuleActionHandler ruleActionHandler;

    PdfFieldCollector(AnswerEvaluationContext answerEvaluationContext,
                      IAnswerVisibilityEvaluator answerVisibilityEvaluator,
                      RuleActionHandler ruleActionHandler){
        this.answerEvaluationContext = answerEvaluationContext;
        this.answerVisibilityEvaluator = answerVisibilityEvaluator;
        this.ruleActionHandler = ruleActionHandler;
    }

    Map<String, PdfFieldDetail> pdfFieldItems; //here key is 'questionId'

    List<PdfFieldDetail> toCollect(SectionNodeInstance sectionInstance) {
        this.pdfFieldItems = new HashMap<>();
        this.visitChildren(sectionInstance);
        List<PdfFieldDetail> pdfFieldDetailList = new ArrayList<>(this.pdfFieldItems.values());
        return pdfFieldDetailList;
    }

    private void addPdfFieldDetail(QuestionNodeInstance questionNodeInstance) {
        String pdfFieldRelationshipValue = this.generatePdfFieldRelationship(questionNodeInstance)
        if (this.pdfFieldItems.containsKey(questionNodeInstance.getId())) {
            PdfFieldDetail existingPdfFieldDetail = this.pdfFieldItems[questionNodeInstance.getId()];
            existingPdfFieldDetail.addAnswer(questionNodeInstance.getAnswer(), questionNodeInstance.dataType, pdfFieldRelationshipValue)
        } else {
            PdfFieldDetail questionPdfFieldDetail = new PdfFieldDetail(questionNodeInstance.getId(), questionNodeInstance.getName())
            questionPdfFieldDetail.addAnswer(questionNodeInstance.getAnswer(), questionNodeInstance.dataType, pdfFieldRelationshipValue)
            this.pdfFieldItems[questionNodeInstance.getId()] = questionPdfFieldDetail;
        }
    }


    private String generatePdfFieldRelationship(QuestionNodeInstance questionNodeInstance) {
        String pdfFieldRelationshipRule = questionNodeInstance.getPdfFieldRelationshipRule();
        String defaultPdfFieldRelationshipValue = null;
        if(StringUtils.isNotEmpty(pdfFieldRelationshipRule)) {
            NodeRuleEvaluationContext nodeRuleEvaluationContext = new NodeRuleEvaluationContext(this.answerEvaluationContext.answerList,
                    questionNodeInstance, this.answerEvaluationContext.packageId, this.answerEvaluationContext.applicantId)
            return this.ruleActionHandler.generatePdfFieldRelationship(pdfFieldRelationshipRule, nodeRuleEvaluationContext)
        }
        return defaultPdfFieldRelationshipValue
    }


    private void visitChildren(EasyVisaNodeInstance parentNode) {
        List<EasyVisaNodeInstance> easyVisaNodeInstanceList = parentNode.getChildren();

        List<EasyVisaNodeInstance> nonRepeatingGroupNodeInstanceList =
                easyVisaNodeInstanceList.stream().filter({ easyVisaNodeInstance -> !(easyVisaNodeInstance instanceof RepeatingQuestionGroupNodeInstance) })
                        .collect(Collectors.toList());
        for (EasyVisaNodeInstance easyVisaNodeInstance : nonRepeatingGroupNodeInstanceList) {
            this.addAnswerItem(easyVisaNodeInstance);
        }

        List<EasyVisaNodeInstance> repeatingGroupNodeInstanceList =
                easyVisaNodeInstanceList.stream().filter({ easyVisaNodeInstance -> (easyVisaNodeInstance instanceof RepeatingQuestionGroupNodeInstance) })
                        .collect(Collectors.toList());
        Map<String, List<EasyVisaNodeInstance>> groupedRepeatingGroupNodeInstanceList =
                repeatingGroupNodeInstanceList.stream().collect(Collectors.groupingBy({ easyVisaNodeInstance -> easyVisaNodeInstance.getDefinitionNode().getName() }));
        Set<String> repeatingQuestionGroupNames = groupedRepeatingGroupNodeInstanceList.keySet();
        for (String repeatingQuestionGroupName : repeatingQuestionGroupNames) {
            List<EasyVisaNodeInstance> nodesInstanceListByGroupName = groupedRepeatingGroupNodeInstanceList.get(repeatingQuestionGroupName);
            for (EasyVisaNodeInstance easyVisaNodeInstance : nodesInstanceListByGroupName) {
                this.addAnswerItem(easyVisaNodeInstance);
            }
        }
    }

    private void addAnswerItem(EasyVisaNodeInstance easyVisaNodeInstance) {
        EasyVisaNode easyVisaNode = easyVisaNodeInstance.getDefinitionNode();
        String nodeInstanceType = easyVisaNode.getClass().getSimpleName();

        switch (nodeInstanceType) {
            case MetaDataMapper.SUBSECTION:
                SubSectionNodeInstance subSectionInstance = (SubSectionNodeInstance) easyVisaNodeInstance;
                this.addSubsectionNodeAnswerItem(subSectionInstance);
                break;

            case MetaDataMapper.QUESTION:
                QuestionNodeInstance questionNodeInstance = (QuestionNodeInstance) easyVisaNodeInstance;
                this.addQuestionNodeAnswerItem(questionNodeInstance);
                break;

            case MetaDataMapper.REPEATING_QUESTION_GROUP:
                RepeatingQuestionGroupNodeInstance repeatingQuestionGroupNodeInstance = (RepeatingQuestionGroupNodeInstance) easyVisaNodeInstance;
                this.addRepeatingGroupAnswerItem(repeatingQuestionGroupNodeInstance);
                break;
        }
    }

    private void addSubsectionNodeAnswerItem(SubSectionNodeInstance subSectionNodeInstance) {
        if (!subSectionNodeInstance.isVisibility()) {
            return;
        }
        this.visitChildren(subSectionNodeInstance);
    }

    private void addQuestionNodeAnswerItem(QuestionNodeInstance questionNodeInstance) {
        Answer answer = questionNodeInstance.getAnswer();
        this.answerVisibilityEvaluator.populateAnswer(this.answerEvaluationContext, questionNodeInstance);
        Boolean hasValidQuestion = this.answerVisibilityEvaluator.evaluate(this.answerEvaluationContext, questionNodeInstance);
        if (hasValidQuestion && Answer.isValidAnswer(answer)) {
            this.addPdfFieldDetail(questionNodeInstance);
            this.visitChildren(questionNodeInstance);
        }
    }

    private void addRepeatingGroupAnswerItem(RepeatingQuestionGroupNodeInstance repeatingQuestionGroupInstance) {
        if (!repeatingQuestionGroupInstance.isVisibility()) {
            return;
        }
        this.visitChildren(repeatingQuestionGroupInstance);
    }
}
