package com.easyvisa.questionnaire.repositories;

import com.easyvisa.questionnaire.model.*;
import org.neo4j.ogm.response.model.QueryResultModel;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class DocumentDAO {

    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    Session neo4jSession;

    public List<Document> findAllDocuments(String questVersion) {
        List<Document> documentList = documentRepository.getAll(questVersion);
        return documentList;
    }

    public List<Document> documentsByBenefitCategory(String questVersion, String benefitCategoryId, ApplicantType applicantType) {
        List<Map<String, EasyVisaNode>> documentDocHelpMapList = this.findDocumentByBenefitCategoryAndApplicantType(questVersion, benefitCategoryId, applicantType);
        Map<String, Document> documentIdMapper = new HashMap<>();
        documentDocHelpMapList.stream().forEach(stringEasyVisaNodeMap -> {
            DocumentHelp documentHelp = (DocumentHelp) stringEasyVisaNodeMap.get("documentHelp");
            Document document = (Document) stringEasyVisaNodeMap.get("document");
            Document mappedDocumentData = documentIdMapper.get(document.getId());
            if (mappedDocumentData == null) {
                mappedDocumentData = document;
                documentIdMapper.put(document.getId(), document);
            }
            mappedDocumentData.addChild(documentHelp);
        });
        List<Document> documentList = documentIdMapper.values()
                .stream().collect(Collectors.toList());
        return Collections.unmodifiableList(documentList);
    }


    private List<Map<String, EasyVisaNode>> findDocumentByBenefitCategoryAndApplicantType(String questVersion, String benefitCategoryId, ApplicantType applicantType) {
        String documentHelpGraphCQL = "MATCH (b:BenefitCategory{questVersion:'questVersionParam', easyVisaId:'benefitCategoryId'})-[:applicantType]->(document:Document)-[:has]->(documentHelp:DocumentHelp) RETURN document, documentHelp";
        documentHelpGraphCQL = documentHelpGraphCQL.replaceAll("questVersionParam", questVersion);
        documentHelpGraphCQL = documentHelpGraphCQL.replaceAll("benefitCategoryId", benefitCategoryId);
        documentHelpGraphCQL = documentHelpGraphCQL.replaceAll("applicantType", applicantType.name().toLowerCase());
        QueryResultModel queryResultModel = (QueryResultModel) neo4jSession.query(documentHelpGraphCQL, new HashMap<>());
        List<Map<String, EasyVisaNode>> clonedDocumentDocHelpMapList = this.cloneDocumentDocHelpMapList(queryResultModel);
        return clonedDocumentDocHelpMapList;
    }

    private List<Map<String, EasyVisaNode>> cloneDocumentDocHelpMapList(QueryResultModel queryResultModel) {
        EasyVisaNodeHelper easyVisaNodeHelper = new EasyVisaNodeHelper();
        Map<Object, Object> cloneMap = new HashMap<>();
        List<Map<String, EasyVisaNode>> clonedDocumentDocHelpMapList = new ArrayList<>();
        Iterator<Map<String, Object>> queryResultIterator = queryResultModel.iterator();
        while (queryResultIterator.hasNext()) {
            Map<String, Object> itemMap = queryResultIterator.next();
            DocumentHelp documentHelp = (DocumentHelp) itemMap.get("documentHelp");
            Document document = (Document) itemMap.get("document");
            Map<String, EasyVisaNode> clonedItemMap = new HashMap<>();
            clonedItemMap.put("documentHelp", easyVisaNodeHelper.copy(cloneMap, documentHelp));
            clonedItemMap.put("document", easyVisaNodeHelper.copy(cloneMap, document));
            clonedDocumentDocHelpMapList.add(clonedItemMap);
        }
        return clonedDocumentDocHelpMapList;
    }

    public Document getDocumentById(String questVersion, String documentId) {
        return documentRepository.getDocumentById(questVersion, documentId);
    }
}
