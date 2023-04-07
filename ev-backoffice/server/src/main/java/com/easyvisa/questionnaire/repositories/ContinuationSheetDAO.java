package com.easyvisa.questionnaire.repositories;

import com.easyvisa.questionnaire.model.ContinuationSheet;
import com.easyvisa.questionnaire.model.EasyVisaNode;
import com.easyvisa.questionnaire.model.Form;
import com.easyvisa.questionnaire.model.Section;
import com.easyvisa.questionnaire.util.GraphUtils;

import grails.plugin.cache.Cacheable;
import org.neo4j.driver.internal.InternalPath;
import org.neo4j.ogm.response.model.QueryResultModel;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ContinuationSheetDAO {

    @Autowired
    Session neo4jSession;

    @Autowired
    ContinuationSheetRepository continuationSheetRepository;

    public List<ContinuationSheet> continuationSheetsByForm(String questVersion, String formId) {
        List<ContinuationSheet> continuationSheetList = continuationSheetRepository.findByForm(questVersion, formId);
        return Collections.unmodifiableList(continuationSheetList);
    }

    public ContinuationSheet continuationSheetById(String questVersion, String continuationSheetId) {
        return continuationSheetRepository.findById(questVersion, continuationSheetId);
    }

    public Map<String, Set<ContinuationSheet>> fetchFormToContinuationSheetListMapper(String questVersion, List<Form> questionnaireFormList) {
        String continuationSheetGraphCQL = "MATCH (n:Form{questVersion:'questVersionParam'}) WHERE n.easyVisaId IN [idListStr] MATCH path = (n)-[:has]->(continuationSheet:ContinuationSheet) "
                + " UNWIND nodes(path) AS node RETURN n, path, collect(node) AS nodes";
        String idListStr = questionnaireFormList.stream().map(Form::getId).map(easyVisaId -> "'" + easyVisaId + "'").collect(Collectors.joining(","));
        continuationSheetGraphCQL = continuationSheetGraphCQL.replaceAll("questVersionParam", questVersion);
        continuationSheetGraphCQL = continuationSheetGraphCQL.replaceAll("idListStr", idListStr);
        QueryResultModel queryResultModel = (QueryResultModel) neo4jSession.query(continuationSheetGraphCQL, new HashMap<>());
        Iterator<Map<String, Object>> queryResultIterator = new EasyVisaNodeHelper().cloneResults(queryResultModel);
        Map<String, Set<ContinuationSheet>> formToContinuationSheetListMapper = new HashMap<>();
        while (queryResultIterator.hasNext()) {
            Map<String, Object> itemMap = queryResultIterator.next();
            Form form = (Form) itemMap.get("n");
            InternalPath.SelfContainedSegment[] paths = (InternalPath.SelfContainedSegment[]) itemMap.get("path");
            ArrayList<EasyVisaNode> easyVisaNodeArrayList = (ArrayList<EasyVisaNode>) itemMap.get("nodes");
            GraphUtils.buildLinksWithCreationOrder(paths, easyVisaNodeArrayList);

            Set<ContinuationSheet> continuationSheetList = formToContinuationSheetListMapper.get(form.getId());
            if (continuationSheetList == null) {
                formToContinuationSheetListMapper.put(form.getId(), new TreeSet<>(Comparator.comparing(ContinuationSheet::getId)));
            }
            this.extractFormOutgoingLinks(form, formToContinuationSheetListMapper);
        }
        return Collections.unmodifiableMap(formToContinuationSheetListMapper);
    }

    private void extractFormOutgoingLinks(Form form, Map<String, Set<ContinuationSheet>> formToContinuationSheetListMapper) {
        Set<ContinuationSheet> continuationSheetList = formToContinuationSheetListMapper.get(form.getId());
        form.getOutgoingLinks().stream().forEach(easyVisaNodeRelationship -> {
            ContinuationSheet continuationSheet = (ContinuationSheet) easyVisaNodeRelationship.getEndNode().copy();
            continuationSheetList.add(continuationSheet);
        });
        formToContinuationSheetListMapper.put(form.getId(), continuationSheetList);
    }



    public List<String> findQuestionnaireContinuationSheetIdList(String questVersion, List<String> questionIdList) {
        List<String> continuationSheetIdList = continuationSheetRepository.findQuestionnaireContinuationSheetIdList(questVersion, questionIdList);
        return continuationSheetIdList;
    }

}
