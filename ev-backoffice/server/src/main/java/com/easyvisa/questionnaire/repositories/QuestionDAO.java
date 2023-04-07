package com.easyvisa.questionnaire.repositories;

import com.easyvisa.questionnaire.model.EasyVisaNode;
import com.easyvisa.questionnaire.model.Question;
import com.easyvisa.questionnaire.model.RepeatingQuestionGroup;
import org.neo4j.ogm.response.model.QueryResultModel;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class QuestionDAO {

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    Session neo4jSession;

    public List<Question> findQuestionsOfRepeatingGroupByEasyVisaId(String questVersion, String repeatingGroupId) {
        List<Question> resultItems = questionRepository.findChildrenOfRepeatingGroupByEasyVisaId(questVersion, repeatingGroupId);
        return resultItems;
    }

    public List<Question> findLinkedQuestionsOfRepeatingGroupByEasyVisaId(String questVersion, String repeatingGroupId) {
        List<Question> resultItems = questionRepository.findLinkedChildrenOfRepeatingGroupByEasyVisaId(questVersion, repeatingGroupId);
        return resultItems;
    }

    public Question findByEasyVisaId(String questVersion, String easyVisaId) {
        return (Question) questionRepository.findByEasyVisaId(questVersion, easyVisaId).copy();
    }

    public List<Question>  findQuestionByFormSubsection (String questVersion,String formId,String subsectionId) {
        return questionRepository.findQuestionByFormSubsection(questVersion,formId,subsectionId);
    }

    public List<RepeatingQuestionGroup> findDependentRepeatingGroups(String questVersion, String easyVisaId, String relationType) {
        List<RepeatingQuestionGroup> repeatingQuestionGroupList = new ArrayList<>();
        String repeatingQuestionGroupCQL = "MATCH (question:Question{questVersion:'questVersionParam', easyVisaId:'easyVisaIdParam'})-[rel:relValue]->(repeatingGroup:RepeatingQuestionGroup) " +
                "RETURN repeatingGroup ORDER BY rel.order";
        repeatingQuestionGroupCQL = repeatingQuestionGroupCQL.replaceAll("questVersionParam", questVersion);
        repeatingQuestionGroupCQL = repeatingQuestionGroupCQL.replaceAll("easyVisaIdParam", easyVisaId);
        repeatingQuestionGroupCQL = repeatingQuestionGroupCQL.replaceAll("relValue", relationType);
        QueryResultModel queryResultModel = (QueryResultModel) neo4jSession.query(repeatingQuestionGroupCQL, new HashMap<>());
        Iterator<Map<String, Object>> queryResultIterator = queryResultModel.iterator();
        while (queryResultIterator.hasNext()) {
            Map<String, Object> itemMap = queryResultIterator.next();
            RepeatingQuestionGroup repeatingGroup = (RepeatingQuestionGroup) itemMap.get("repeatingGroup");
            repeatingQuestionGroupList.add(repeatingGroup);
        }
        return repeatingQuestionGroupList;
    }
}
