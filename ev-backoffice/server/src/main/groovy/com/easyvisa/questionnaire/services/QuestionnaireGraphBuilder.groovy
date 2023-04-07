package com.easyvisa.questionnaire.services

import com.easyvisa.questionnaire.model.EasyVisaNode
import com.easyvisa.questionnaire.model.EasyVisaNodeRelationship
import com.easyvisa.questionnaire.model.Section
import org.springframework.stereotype.Component

@Component
public class QuestionnaireGraphBuilder {

    public EasyVisaNode buildQuestionnaireGraph(Section section) {
        section.getChildren().each { subSection ->
            subSection.getChildren().each { easyVisaNode ->
                buildChildren(easyVisaNode)
            }
        }
        return section;
    }

    /**
     * * recursive build
     *
     * @param parentNode
     */
    private void buildChildren(EasyVisaNode parentNode) {
        Set<EasyVisaNodeRelationship> outgoingRelationships = parentNode.getOutgoingLinks();
        outgoingRelationships.each { easyVisaNodeRelationship ->
            EasyVisaNode easyVisaNode = easyVisaNodeRelationship.getEndNode();
            parentNode.addChild(easyVisaNodeRelationship.getType(), easyVisaNode);
            this.buildChildren(easyVisaNode);
        }
    }
}
