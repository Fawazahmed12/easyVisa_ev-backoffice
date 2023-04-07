package com.easyvisa.questionnaire.repositories;

import com.easyvisa.questionnaire.model.EasyVisaNode;
import org.neo4j.driver.internal.InternalPath;
import org.neo4j.ogm.response.model.QueryResultModel;

import java.util.*;
import java.util.stream.Collectors;

public class EasyVisaNodeHelper {

    //This method will deep copy the result of 'neo4jSession' (i.e)QueryResultModel
    public Iterator<Map<String, Object>> cloneResults(QueryResultModel queryResultModel) {
        List<Map<String, Object>> clonedResultList = new ArrayList<>();
        Map<Object, Object> cloneMap = new HashMap<>();
        Iterator<Map<String, Object>> queryResultIterator = queryResultModel.iterator();
        while (queryResultIterator.hasNext()) {
            Map<String, Object> itemMap = queryResultIterator.next();
            EasyVisaNode formQuestion = (EasyVisaNode) itemMap.get("n");
            InternalPath.SelfContainedSegment[] path = (InternalPath.SelfContainedSegment[]) itemMap.get("path");
            ArrayList<EasyVisaNode> easyVisaNodeArrayList = (ArrayList<EasyVisaNode>) itemMap.get("nodes");

            EasyVisaNode clonedFormSubsection = copy(cloneMap, formQuestion);
            ArrayList<EasyVisaNode> clonedEasyVisaList = copyList(cloneMap, easyVisaNodeArrayList);
            Map<String, Object> clonedItemMap = new HashMap<>();
            clonedItemMap.put("n", clonedFormSubsection);
            clonedItemMap.put("path", path);
            clonedItemMap.put("nodes", clonedEasyVisaList);
            clonedResultList.add(clonedItemMap);
        }
        return clonedResultList.iterator();
    }

    public EasyVisaNode copy(Map<Object, Object> cloneMap, EasyVisaNode source) {
        Object clonedSourceNode = cloneMap.get(source);
        if (clonedSourceNode == null) {
            clonedSourceNode = source.copy();
            cloneMap.put(source, clonedSourceNode);
        }
        return (EasyVisaNode) clonedSourceNode;
    }

    private ArrayList<EasyVisaNode> copyList(Map<Object, Object> cloneMap, List<EasyVisaNode> sourceList) {
        List<EasyVisaNode> clonedEasyVisaList = sourceList.stream()
                .map(easyVisaNode -> copy(cloneMap, easyVisaNode))
                .collect(Collectors.toList());
        return (ArrayList<EasyVisaNode>) clonedEasyVisaList;
    }
}
