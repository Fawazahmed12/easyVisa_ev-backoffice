package com.easyvisa.questionnaire.answering

import com.easyvisa.enums.DisplayTextLanguage
import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.dto.InputTypeConstant
import com.easyvisa.questionnaire.model.*
import com.easyvisa.questionnaire.services.MetaDataMapper
import com.easyvisa.questionnaire.services.QuestionnaireTranslationService
import com.easyvisa.questionnaire.services.RuleActionHandler
import org.apache.commons.lang.StringUtils

import java.time.LocalDate
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Function
import java.util.stream.Collectors

class AnswerBindingVisitor implements INodeVisitor {

    private EasyVisaNodeInstance latestParent
    private List<Answer> answerList
    private Map<String, Answer> answerByPath
    private SectionNodeInstance sectionInstance

    private Section section
    private SubSection subSection
    private Stack<AtomicInteger> repeatingIndexStack
    private Long packageId
    private Long applicantId
    private DisplayTextLanguage displayTextLanguage
    private LocalDate currentDate

    MetaDataMapper metaDataMapper
    RuleActionHandler ruleActionHandler
    QuestionnaireTranslationService questionnaireTranslationService

    AnswerBindingVisitor(Section section, List<Answer> answers,
                         MetaDataMapper metaDataMapper, RuleActionHandler ruleActionHandler,
                         QuestionnaireTranslationService questionnaireTranslationService,
                         Long packageId, Long applicantId,
                         DisplayTextLanguage displayTextLanguage, LocalDate currentDate) {
        this.displayTextLanguage = displayTextLanguage
        this.currentDate = currentDate
        this.questionnaireTranslationService = questionnaireTranslationService;
        this.latestParent = new SectionNodeInstance(section, this.displayTextLanguage, this.currentDate, this.questionnaireTranslationService)
        answerByPath = answers.stream().collect(Collectors.toMap({ Answer answer -> answer.path }, Function.identity()))
        this.answerList = answers
        this.metaDataMapper = metaDataMapper
        this.ruleActionHandler = ruleActionHandler;
        this.packageId = packageId
        this.applicantId = applicantId
        this.repeatingIndexStack = new Stack<>()
    }


    @Override
    void visit(Section section) {
        this.section = section
        this.sectionInstance = new SectionNodeInstance(section, this.displayTextLanguage, this.currentDate, this.questionnaireTranslationService)
        processChildren(sectionInstance, section)
    }

    @Override
    void visit(SubSection subSection) {
        this.subSection = subSection
        SubSectionNodeInstance subSectionInstance = new SubSectionNodeInstance(subSection, this.displayTextLanguage, this.currentDate, this.questionnaireTranslationService)
        this.latestParent.addChild(subSectionInstance)
        processChildren(subSectionInstance, subSection)
    }


    @Override
    void visit(Question question) {
        Answer answer = this.findAnswer(question)
        Integer repeatingIndex = this.getCurrentRepeatingGroupIndex();
        QuestionNodeInstance questionInstance = new QuestionNodeInstance(question, answer, repeatingIndex,
                this.displayTextLanguage, this.currentDate, this.questionnaireTranslationService)
        this.populateQuestionProperties(questionInstance)
        this.latestParent.addChild(questionInstance)
        Set<EasyVisaNode> children = question.children
        EasyVisaNodeInstance preParent = this.latestParent
        this.latestParent = questionInstance
        for (EasyVisaNode childQuestion : children) {
            childQuestion.accept(this)
        }
        this.latestParent = preParent
    }

    private void populateQuestionProperties(QuestionNodeInstance questionNodeInstance) {
        this.metaDataMapper.populateToolTip(this.answerList, this.packageId, this.applicantId, questionNodeInstance);
        this.metaDataMapper.populateDisplayText(this.answerList, this.packageId, this.applicantId, questionNodeInstance);
        this.metaDataMapper.populateInputSourceType(this.answerList, this.packageId, this.applicantId, questionNodeInstance);
        this.metaDataMapper.populateDynamicAttribute(this.answerList, this.packageId, this.applicantId, questionNodeInstance);
    }

    @Override
    void visit(RepeatingQuestionGroup repeatingQuestionGroup) {
        int repeatGroupInstanceCount = getRepeatingInstanceCount(repeatingQuestionGroup)
        AtomicInteger repeatingGroupCount = new AtomicInteger(0)
        repeatingIndexStack.push(repeatingGroupCount)
        for (int i = 0; i < repeatGroupInstanceCount; i++) {
            RepeatingQuestionGroupNodeInstance repeatingQuestionGroupInstance = new RepeatingQuestionGroupNodeInstance(repeatingQuestionGroup, i, repeatGroupInstanceCount,
                    this.displayTextLanguage, this.currentDate)
            this.populateRepeatingQuestionGroupProperties(repeatingQuestionGroupInstance);
            this.latestParent.addChild(repeatingQuestionGroupInstance)
            processChildren(repeatingQuestionGroupInstance, repeatingQuestionGroup)
            repeatingGroupCount.incrementAndGet()
        }
        this.repeatingIndexStack.pop()
    }

    private void populateRepeatingQuestionGroupProperties(RepeatingQuestionGroupNodeInstance repeatingQuestionGroupInstance) {
        this.metaDataMapper.populateDisplayText(this.answerList, this.packageId, this.applicantId, repeatingQuestionGroupInstance);
        this.metaDataMapper.populateDynamicAttribute(this.answerList, this.packageId, this.applicantId, repeatingQuestionGroupInstance);
    }


    @Override
    void visit(DocumentActionNode documentActionNode) {

    }

    @Override
    void visit(TerminalNode terminalNode) {

    }

    @Override
    void visit(ANDCondition andCondition) {

    }


    @Override
    void visit(PdfField pdfField) {

    }


    @Override
    void visit(FormQuestion formQuestion) {

    }

    @Override
    void visit(Document document) {

    }


    @Override
    void visit(DocumentHelp documentHelp) {

    }

    @Override
    void visit(MilestoneType milestoneType) {

    }

    Answer findAnswer(Question question) {
        String fieldPath = getQuestionPath(question)
        if (this.answerByPath.containsKey(fieldPath)) {
            return this.answerByPath[fieldPath];
        }

        Answer answer = new Answer(packageId: this.packageId,
                applicantId: this.applicantId,
                path: fieldPath)
        if (StringUtils.isNotEmpty(question.defaultValue)) {
            answer.value = question.defaultValue;
        }
        return answer
    }

    /** Exclude Labels from being counted as Question.
     *  Labels wont have any answer or repeating group children **/
    int getRepeatingInstanceCount(RepeatingQuestionGroup repeatingQuestionGroup) {
        List<Question> questionNodeList = repeatingQuestionGroup.getChildren().stream()
                .filter({ childNode -> (childNode instanceof Question) && (((Question) childNode).inputType != InputTypeConstant.LABEL.value) })
                .map({ childNode -> (Question) childNode })
                .collect(Collectors.toList());
        if (questionNodeList.isEmpty()) {
            return 0
        }

        List<Integer> instanceCountList = [];
        for (Question childNode : questionNodeList) {
            String questionPath = getQuestionPath((Question) childNode)
            Set<Answer> totalRepeatingGroupFieldList =
                    answerList.stream().filter({ answer -> answer.getPath().startsWith(questionPath) }).collect(Collectors.toSet())
            instanceCountList.add(totalRepeatingGroupFieldList.size())
        }
        int instanceCount = instanceCountList.sort().reverse()[0];
        return instanceCount
    }

    String getQuestionPath(Question question) {
        List<String> pathFrags = []
        pathFrags.add(section.getId())
        pathFrags.add(subSection.getId())
        pathFrags.add(question.getId())
        Integer repeatingIndex = this.getCurrentRepeatingGroupIndex();
        if (repeatingIndex != null) {
            pathFrags.add(repeatingIndex.toString())
        }
        String fieldPath = pathFrags.stream().collect(Collectors.joining("/"))
        return fieldPath
    }

    SectionNodeInstance getSectionInstance() {
        return this.sectionInstance
    }

    private void processChildren(EasyVisaNodeInstance instance, EasyVisaNode easyVisaNodeDef) {
        Set<EasyVisaNode> children = easyVisaNodeDef.getChildren()
        EasyVisaNodeInstance preParent = this.latestParent
        this.latestParent = instance
        visitChildren(children)
        this.latestParent = preParent
    }

    private void visitChildren(Set<EasyVisaNode> children) {
        for (EasyVisaNode easyVisaNode : children) {
            easyVisaNode.accept(this)
        }
    }

    private getCurrentRepeatingGroupIndex() {
        if (!this.repeatingIndexStack.empty()) {
            Integer repeatingIndex = repeatingIndexStack.peek().get()
            return repeatingIndex;
        }
        return null;
    }

}
