package com.easyvisa.questionnaire.model;

public interface INodeVisitor {
    void visit(Question question);

    void visit(RepeatingQuestionGroup repeatingQuestionGroup);

    void visit(DocumentActionNode documentActionNode);

    void visit(TerminalNode terminalNode);

    void visit(ANDCondition andCondition);

    void visit(Section section);

    void visit(SubSection subSection);

    void visit(PdfField pdfField);

    void visit(FormQuestion formQuestion);

    void visit(Document document);

    void visit(DocumentHelp documentHelp);

    void visit(MilestoneType milestoneType);
}
