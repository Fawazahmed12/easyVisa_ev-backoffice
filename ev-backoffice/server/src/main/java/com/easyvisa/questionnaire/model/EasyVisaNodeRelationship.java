package com.easyvisa.questionnaire.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * This is a generic relationship which can take values such as has, yes, no, etc
 * Since it is generic it is not mapped as a RelationShip entity using Spring Neo4j
 */
public class EasyVisaNodeRelationship {

    private Long id;
    private EasyVisaNode startNode;
    private EasyVisaNode endNode;
    private String type;
    private int order;

    private String ruleClassName;

    public EasyVisaNodeRelationship(Long id, EasyVisaNode startNode, EasyVisaNode endNode, String type, int order) {
        this.id = id;
        this.startNode = startNode;
        this.endNode = endNode;
        this.type = type;
        this.order = order;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EasyVisaNode getStartNode() {
        return startNode;
    }

    public void setStartNode(EasyVisaNode startNode) {
        this.startNode = startNode;
    }

    public EasyVisaNode getEndNode() {
        return endNode;
    }

    public void setEndNode(EasyVisaNode endNode) {
        this.endNode = endNode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (other.getClass() != getClass()) {
            return false;
        }

        EasyVisaNodeRelationship otherNode = (EasyVisaNodeRelationship) other;
        boolean isEquals = new EqualsBuilder().append(startNode.getId(), otherNode.startNode.getId()).append(endNode.getId(), otherNode.endNode.getId())
                .append(type, otherNode.type).isEquals();
        return isEquals;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(startNode.getId()).append(endNode.getId()).append(type).hashCode();
    }

    public String getRuleClassName() {
        return ruleClassName;
    }

    public void setRuleClassName(String ruleClassName) {
        this.ruleClassName = ruleClassName;
    }
}
