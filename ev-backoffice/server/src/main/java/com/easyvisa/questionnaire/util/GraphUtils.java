package com.easyvisa.questionnaire.util;

import com.easyvisa.questionnaire.model.EasyVisaNode;
import com.easyvisa.questionnaire.model.EasyVisaNodeRelationship;
import org.neo4j.driver.internal.InternalPath;
import org.neo4j.driver.types.Relationship;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GraphUtils {

    public static void buildLinks(InternalPath.SelfContainedSegment[] paths, List<EasyVisaNode> nodes) {
        Map<Long, EasyVisaNode> nodeMap = nodes.stream().collect(Collectors.toMap(EasyVisaNode::getSystemNodeId, Function.identity()));
        for (InternalPath.SelfContainedSegment path : paths) {
            Relationship relationship = path.relationship();
            Long relId = relationship.id();
            Long startNodeId = relationship.startNodeId();
            Long endNodeId = relationship.endNodeId();
            String linkType = relationship.type();
            if (nodeMap.containsKey(startNodeId) && nodeMap.containsKey(endNodeId)) {
                EasyVisaNode easyVisaNodeStart = nodeMap.get(startNodeId);
                EasyVisaNode easyVisaNodeEnd = nodeMap.get(endNodeId);
                int order = 0;
                if (relationship.containsKey("order")) {
                    order = relationship.get("order").asInt();
                }
                EasyVisaNodeRelationship easyVisaNodeRelationship = new EasyVisaNodeRelationship(relId, easyVisaNodeStart, easyVisaNodeEnd, linkType, order);
                easyVisaNodeStart.getOutgoingLinks().add(easyVisaNodeRelationship);
            }
        }
    }

    public static void buildLinksWithCreationOrder(InternalPath.SelfContainedSegment[] paths, List<EasyVisaNode> nodes) {
        Map<Long, EasyVisaNode> nodeMap = nodes.stream().collect(Collectors.toMap(EasyVisaNode::getSystemNodeId, Function.identity()));
        for (InternalPath.SelfContainedSegment path : paths) {
            Relationship relationship = path.relationship();
            Long relId = relationship.id();
            Long startNodeId = relationship.startNodeId();
            Long endNodeId = relationship.endNodeId();
            String linkType = relationship.type();
            EasyVisaNode easyVisaNodeStart = nodeMap.get(startNodeId);
            EasyVisaNode easyVisaNodeEnd = nodeMap.get(endNodeId);
            int order = easyVisaNodeEnd.getOrder();
            EasyVisaNodeRelationship easyVisaNodeRelationship = new EasyVisaNodeRelationship(relId, easyVisaNodeStart, easyVisaNodeEnd, linkType, order);
            easyVisaNodeStart.getOutgoingLinks().add(easyVisaNodeRelationship);
        }
    }
}
