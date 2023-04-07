package com.easyvisa.questionnaire.repositories;

import com.easyvisa.questionnaire.model.*;
import com.easyvisa.questionnaire.util.GraphUtils;
import org.neo4j.driver.internal.InternalPath;
import org.neo4j.ogm.response.model.QueryResultModel;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SectionDAO {
    @Autowired
    Session neo4jSession;

    @Autowired
    FormSubSectionRepository formSubSectionRepository;

    @Autowired
    SectionRepository sectionRepository;

    private Set<FormSubSection> fetchSectionGraph(String questVersion, Set<FormSubSection> formSubSectionSet) {
        String idListStr = formSubSectionSet.stream().map(FormSubSection::getId).map(easyVisaId -> "'" + easyVisaId + "'").collect(Collectors.joining(","));
        Iterator<Map<String, Object>> queryResultIterator = this.fetchFormSubSectionConnectedNodes(questVersion, idListStr);
        Set<FormSubSection> formSubSectionSetWithDetails = new HashSet<>();
        while (queryResultIterator.hasNext()) {
            Map<String, Object> itemMap = queryResultIterator.next();
            FormSubSection formSubSection = (FormSubSection) itemMap.get("n");
            InternalPath.SelfContainedSegment[] paths = (InternalPath.SelfContainedSegment[]) itemMap.get("path");
            ArrayList<EasyVisaNode> easyVisaNodeArrayList = (ArrayList<EasyVisaNode>) itemMap.get("nodes");
            GraphUtils.buildLinks(paths, easyVisaNodeArrayList);
            formSubSectionSetWithDetails.add(formSubSection);
        }
        return formSubSectionSetWithDetails;
    }

    public Section getSectionNode(String questVersion, String sectionId) {
        Section sectionNode = sectionRepository.findByEasyVisaId(questVersion, sectionId);
        return (sectionNode != null) ? (Section) sectionNode.copy() : sectionNode;
    }

    public Section sectionQuestionByBenefitCategoryAndSection(String questVersion, String benefitCategoryId, String sectionId) {
        Section section = (Section) sectionRepository.findByEasyVisaId(questVersion, sectionId).copy();
        List<Map<String, EasyVisaNode>> sectionSubSectionMapList = this.findByBenefitCategoryAndSection(questVersion, benefitCategoryId, sectionId);
        Set<FormSubSection> formSubSectionSet = sectionSubSectionMapList.stream()
                .map(stringEasyVisaNodeMap -> (FormSubSection) stringEasyVisaNodeMap.get("formSubsection"))
                .collect(Collectors.toSet());
        Set<FormSubSection> formSubSectionDetailsSet = fetchSectionGraph(questVersion, formSubSectionSet);
        this.populateChildren(section, sectionSubSectionMapList, formSubSectionDetailsSet);
        return section;
    }


    private List<Map<String, EasyVisaNode>> findByBenefitCategoryAndSection(String questVersion, String benefitCategoryId, String sectionId) {
        List<Map<String, EasyVisaNode>> sectionSubSectionMapList = this.formSubSectionRepository
                .findByBenefitCategoryAndSection(questVersion, benefitCategoryId, sectionId);
        List<Map<String, EasyVisaNode>> clonedSectionSubSectionMapList = this.cloneSectionSubSectionMapList(sectionSubSectionMapList);
        return clonedSectionSubSectionMapList;
    }


    private List<Map<String, EasyVisaNode>> cloneSectionSubSectionMapList(List<Map<String, EasyVisaNode>> sectionSubSectionMapList) {
        EasyVisaNodeHelper easyVisaNodeHelper = new EasyVisaNodeHelper();
        Map<Object, Object> cloneMap = new HashMap<>();
        List<Map<String, EasyVisaNode>> clonedSectionSubSectionMapList = new ArrayList<>();
        sectionSubSectionMapList.stream().forEach(stringEasyVisaNodeMap -> {
            FormSubSection formSubSection = (FormSubSection) stringEasyVisaNodeMap.get("formSubsection");
            SubSection subSection = (SubSection) stringEasyVisaNodeMap.get("subsection");
            Map<String, EasyVisaNode> clonedItemMap = new HashMap<>();
            clonedItemMap.put("formSubsection", easyVisaNodeHelper.copy(cloneMap, formSubSection));
            clonedItemMap.put("subsection", easyVisaNodeHelper.copy(cloneMap, subSection));
            clonedSectionSubSectionMapList.add(clonedItemMap);
        });
        return clonedSectionSubSectionMapList;
    }


    private Iterator<Map<String, Object>> fetchFormSubSectionConnectedNodes(String questVersion, String idListStr) {
        String sectionDetailGraphCQL = "MATCH (n:FormSubSection{questVersion:'questVersionParam'}) WHERE n.easyVisaId IN [idListStr]  MATCH path = (n)-[*..6]->(m) WHERE NOT(m:PdfField) AND NOT(m:FormQuestion) "
                + " UNWIND nodes(path) AS node RETURN n, path, collect(node) AS nodes";
        sectionDetailGraphCQL = sectionDetailGraphCQL.replaceAll("questVersionParam", questVersion);
        sectionDetailGraphCQL = sectionDetailGraphCQL.replaceAll("idListStr", idListStr);
        QueryResultModel queryResultModel = (QueryResultModel) neo4jSession.query(sectionDetailGraphCQL, new HashMap<>());
        Iterator<Map<String, Object>> queryResultIterator = new EasyVisaNodeHelper().cloneResults(queryResultModel);
        return queryResultIterator;
    }

    public List<Section> sectionsByBenefitCategoryAndApplicantType(String questVersion, String benefitCategoryId, String applicantType) {
        List<Section> sectionList = sectionRepository.findByBenefitCategoryId(questVersion, benefitCategoryId, applicantType);
        return Collections.unmodifiableList(sectionList);
    }


    public List<Section> sectionsByForm(String questVersion, String formId) {
        List<Section> sectionList = sectionRepository.sectionsByForm(questVersion, formId);
        return Collections.unmodifiableList(sectionList);
    }


    private void populateChildren(Section section, List<Map<String, EasyVisaNode>> subsectionFormSubsectionMapList,
                                  Set<FormSubSection> formSubSectionDetailsSet) {
        Set<SubSection> subSectionSet = subsectionFormSubsectionMapList.stream()
                .map(stringEasyVisaNodeMap -> (SubSection) stringEasyVisaNodeMap.get("subsection"))
                .collect(Collectors.toSet());

        Map<String, FormSubSection> formSubSectionDetailsMap = formSubSectionDetailsSet.stream()
                .collect(Collectors.toMap(FormSubSection::getId, Function.identity()));

        Map<SubSection, Set<FormSubSection>> subSectionFormSubSectionMap = groupFormSubSectionBySubsection(subsectionFormSubsectionMapList);

        subSectionSet.stream().forEach(subSection -> {
            if (subSectionFormSubSectionMap.containsKey(subSection)) {

                // each form might contribute a set of questions and they may overlap so we need to get unique questions from this set
                Set<FormSubSection> formSubSections = subSectionFormSubSectionMap.get(subSection);
                Set<EasyVisaNode> easyVisaNodes = new HashSet<>();
                formSubSections.stream().forEach(formSubSection -> {
                    if (formSubSectionDetailsMap.containsKey(formSubSection.getId())) {
                        FormSubSection formSubSectionDetail = formSubSectionDetailsMap.get(formSubSection.getId());
                        Set<EasyVisaNodeRelationship> easyVisaNodeRelationships = formSubSectionDetail.getOutgoingLinks();
                        Set<EasyVisaNode> childrenSet =
                                easyVisaNodeRelationships.stream().map(EasyVisaNodeRelationship::getEndNode).collect(Collectors.toSet());
                        easyVisaNodes.addAll(childrenSet);
                    }
                });

                easyVisaNodes.stream().forEach(easyVisaNode -> {
                    subSection.addChild(easyVisaNode);
                });
            }
            section.addChild(subSection);
        });
    }

    private Map<SubSection, Set<FormSubSection>> groupFormSubSectionBySubsection(List<Map<String, EasyVisaNode>> subsectionFormSubsectionMapList) {
        Map<SubSection, Set<FormSubSection>> formSubSectionGroupedBySubsection = new HashMap<>();
        subsectionFormSubsectionMapList.forEach(stringEasyVisaNodeMap -> {
            SubSection subSection = (SubSection) stringEasyVisaNodeMap.get("subsection");
            FormSubSection formSubSection = (FormSubSection) stringEasyVisaNodeMap.get("formSubsection");
            Set<FormSubSection> sectionGroupedFormSubsections;
            if (formSubSectionGroupedBySubsection.containsKey(subSection)) {
                sectionGroupedFormSubsections = formSubSectionGroupedBySubsection.get(subSection);
            } else {
                sectionGroupedFormSubsections = new HashSet<>();
                formSubSectionGroupedBySubsection.put(subSection, sectionGroupedFormSubsections);
            }
            sectionGroupedFormSubsections.add(formSubSection);
        });

        return formSubSectionGroupedBySubsection;
    }

    public Map<String, Set<Section>> fetchFormToSectionListMapper(String questVersion) {
        String formGraphCQL = "MATCH path = (n:Form{questVersion:'questVersionParam'})-[:has]->(section:Section) "
                + " UNWIND nodes(path) AS node RETURN n, path, collect(node) AS nodes";
        formGraphCQL = formGraphCQL.replaceAll("questVersionParam", questVersion);
        QueryResultModel queryResultModel = (QueryResultModel) neo4jSession.query(formGraphCQL, new HashMap<>());
        Iterator<Map<String, Object>> queryResultIterator = new EasyVisaNodeHelper().cloneResults(queryResultModel);
        Map<String, Set<Section>> formToSectionListMapper = new HashMap<>();
        while (queryResultIterator.hasNext()) {
            Map<String, Object> itemMap = queryResultIterator.next();
            Form form = (Form) itemMap.get("n");
            InternalPath.SelfContainedSegment[] paths = (InternalPath.SelfContainedSegment[]) itemMap.get("path");
            ArrayList<EasyVisaNode> easyVisaNodeArrayList = (ArrayList<EasyVisaNode>) itemMap.get("nodes");
            GraphUtils.buildLinksWithCreationOrder(paths, easyVisaNodeArrayList);

            Set<Section> sectionList = formToSectionListMapper.get(form.getId());
            if (sectionList == null) {
                formToSectionListMapper.put(form.getId(), new TreeSet<>(Comparator.comparing(Section::getId)));
            }
            this.extractFormOutgoingLinks(form, formToSectionListMapper);
        }
        return Collections.unmodifiableMap(formToSectionListMapper);
    }

    private void extractFormOutgoingLinks(Form form, Map<String, Set<Section>> formToSectionListMapper) {
        Set<Section> sectionList = formToSectionListMapper.get(form.getId());
        form.getOutgoingLinks().stream().forEach(easyVisaNodeRelationship -> {
            Section section = (Section) easyVisaNodeRelationship.getEndNode().copy();
            sectionList.add(section);
        });
        formToSectionListMapper.put(form.getId(), sectionList);
    }


    public Map<String, Set<String>> buildSectionFormMap(String questVersion) {
        String formSectionGraphCQL = "MATCH (B:BenefitCategory{questVersion:'questVersionParam'})-[:has]->(form:Form)-[relrs:has]->(section:Section) RETURN  section.easyVisaId as sectionId, form.easyVisaId as formId";
        formSectionGraphCQL = formSectionGraphCQL.replaceAll("questVersionParam", questVersion);
        QueryResultModel queryResultModel = (QueryResultModel) neo4jSession.query(formSectionGraphCQL, new HashMap<>());

        Map<String, Set<String>> questionFormMapper = new HashMap<>();
        for (Map<String, Object> itemMap : queryResultModel) {
            String sectionId = (String) itemMap.get("sectionId");
            String formId = (String) itemMap.get("formId");

            Set<String> questionMappedForms = questionFormMapper.computeIfAbsent(sectionId, k -> new HashSet<>());
            questionMappedForms.add(formId);
        }
        return questionFormMapper;
    }

    public List<Map<String, EasyVisaNode>> subsectionByFormAndSection(String questVersion, String formId,String sectionId) {
        List<Map<String, EasyVisaNode>> subSectionList = formSubSectionRepository.findByFormAndSection(questVersion,formId,sectionId);
        return Collections.unmodifiableList(subSectionList);
    }
}
