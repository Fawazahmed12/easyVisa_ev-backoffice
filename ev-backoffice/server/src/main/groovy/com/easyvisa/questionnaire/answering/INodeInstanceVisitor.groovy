package com.easyvisa.questionnaire.answering

interface INodeInstanceVisitor {
    void visit(QuestionNodeInstance questionInstance);

    void visit(RepeatingQuestionGroupNodeInstance repeatingQuestionGroupInstance);

    void visit(DocumentActionNodeInstance documentActionInstance);

    void visit(TerminalNodeInstance terminalNodeInstance);

    void visit(SectionNodeInstance sectionInstance);

    void visit(SubSectionNodeInstance subSectionInstance);
}
