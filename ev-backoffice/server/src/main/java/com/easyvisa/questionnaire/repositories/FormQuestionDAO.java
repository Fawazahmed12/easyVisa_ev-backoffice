package com.easyvisa.questionnaire.repositories;

import com.easyvisa.questionnaire.model.EasyVisaNode;
import com.easyvisa.questionnaire.model.FormQuestion;
import com.easyvisa.questionnaire.util.GraphUtils;
import org.neo4j.driver.internal.InternalPath;
import org.neo4j.ogm.response.model.QueryResultModel;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class FormQuestionDAO {

    @Autowired
    Session neo4jSession;

    @Autowired
    FormQuestionRepository formQuestionRepository;


    public Set<FormQuestion> formQuestionsByFormAndQuestionList(String questVersion, String formId, List<String> questionIdList) {
        List<FormQuestion> formQuestionList = formQuestionRepository.findByFormAndQuestionList(questVersion, formId, questionIdList);
        Set<FormQuestion> formQuestionSetWithDetails = this.populateChildren(questVersion, formQuestionList);
        return Collections.unmodifiableSet(formQuestionSetWithDetails);
    }


    private Set<FormQuestion> populateChildren(String questVersion, List<FormQuestion> formQuestionList) {
        String idListStr = formQuestionList.stream()
                .map(FormQuestion::getId)
                .map(easyVisaId -> "'" + easyVisaId + "'")
                .collect(Collectors.joining(","));
        String formQuestionGraphCQL = "MATCH (n:FormQuestion{questVersion:'questVersionParam'}) WHERE n.easyVisaId IN [idListStr]  MATCH path = (n)-[*..6]->(m:PdfField) "
                + " UNWIND nodes(path) AS node RETURN n, path, collect(node) AS nodes";
        formQuestionGraphCQL = formQuestionGraphCQL.replaceAll("questVersionParam", questVersion);
        formQuestionGraphCQL = formQuestionGraphCQL.replaceAll("idListStr", idListStr);
        QueryResultModel queryResultModel = (QueryResultModel) neo4jSession.query(formQuestionGraphCQL, new HashMap<>());
        Iterator<Map<String, Object>> queryResultIterator = new EasyVisaNodeHelper().cloneResults(queryResultModel);
        Set<FormQuestion> formQuestionSetWithDetails = new HashSet<>();
        while (queryResultIterator.hasNext()) {
            Map<String, Object> itemMap = queryResultIterator.next();
            FormQuestion formQuestion = (FormQuestion) itemMap.get("n");
            InternalPath.SelfContainedSegment[] paths = (InternalPath.SelfContainedSegment[]) itemMap.get("path");
            ArrayList<EasyVisaNode> easyVisaNodeArrayList = (ArrayList<EasyVisaNode>) itemMap.get("nodes");
            GraphUtils.buildLinks(paths, easyVisaNodeArrayList);
            formQuestionSetWithDetails.add(formQuestion);
        }
        return formQuestionSetWithDetails;
    }
}
