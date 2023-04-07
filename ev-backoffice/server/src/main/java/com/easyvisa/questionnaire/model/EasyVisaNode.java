package com.easyvisa.questionnaire.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Property;

import java.util.*;

public class EasyVisaNode {

    @Id
    @GeneratedValue
    private Long systemNodeId;

    @Property(name = "easyVisaId")
    private String id; // We need to manually assign ID - so that RDBMS and Neo4j can correlate shared details

    @Property
    private String name;

    @Property
    private String questVersion;

    @Property
    private Integer order;

    private Set<EasyVisaNode> children;

    @Property
    private String displayText;

    @Property
    private String ruleClassName;

    @Property
    private String ruleParam;

    @Property
    private String visibilityRuleClassName;

    @Property
    private String visibilityRuleParam;

    @Property
    private String displayOrderChildren;

    private Map<String, List<EasyVisaNode>> childrenByAnswer;
    private Set<EasyVisaNodeRelationship> incomingLinks;
    private Set<EasyVisaNodeRelationship> outgoingLinks;
    private boolean visibile;

    public EasyVisaNode() {
        children = new HashSet<>();
        childrenByAnswer = new HashMap<>();
    }

    protected EasyVisaNode(String id, String name, Integer order) {
        this.id = id;
        this.name = name;
        this.order = order;
        children = new HashSet<>();
        childrenByAnswer = new HashMap<>();
    }

    @JsonIgnore
    public Set<EasyVisaNodeRelationship> getIncomingLinks() {
        return incomingLinks;
    }

    public void setIncomingLinks(Set<EasyVisaNodeRelationship> incomingLinks) {
        this.incomingLinks = incomingLinks;
    }

    @JsonIgnore
    public Set<EasyVisaNodeRelationship> getOutgoingLinks() {
        if (outgoingLinks == null) {
            this.outgoingLinks = new TreeSet<>(Comparator.comparingLong(EasyVisaNodeRelationship::getOrder));
        }
        return outgoingLinks;
    }

    public void setOutgoingLinks(Set<EasyVisaNodeRelationship> outgoingLinks) {
        this.outgoingLinks = outgoingLinks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addChild(String key, EasyVisaNode child) {
        String answerValue = EasyVisaNode.normalizeAnswer(key);
        List<EasyVisaNode> childrenAnswerGroup = null;
        if (childrenByAnswer.containsKey(answerValue)) {
            childrenAnswerGroup = childrenByAnswer.get(answerValue);
        } else {
            childrenAnswerGroup = new ArrayList<>();
            childrenByAnswer.put(answerValue, childrenAnswerGroup);
        }
        childrenAnswerGroup.add(child);

        // all children
        this.children.add(child);
    }

    public void addChild(EasyVisaNode child) {
        this.addChild("has", child);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @JsonIgnore
    public Set<EasyVisaNode> getChildren() {
        return children;
    }

    public List<EasyVisaNode> getChildrenByAnswerKey(String answer) {
        String answerValue = EasyVisaNode.normalizeAnswer(answer);
        if (StringUtils.isEmpty(answerValue)) {
            return Collections.emptyList();
        }
        if (!childrenByAnswer.containsKey(answerValue)) {
            return Collections.emptyList();
        }
        return childrenByAnswer.get(answerValue);
    }

    public void setChildren(Set<EasyVisaNode> children) {
        this.children = children;
    }

    public Map<String, List<EasyVisaNode>> getChildrenByAnswer() {
        return childrenByAnswer;
    }

    public void setChildrenByAnswer(Map<String, List<EasyVisaNode>> childrenByAnswer) {
        this.childrenByAnswer = childrenByAnswer;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayOrderChildren() {
        return displayOrderChildren;
    }

    public void setDisplayOrderChildren(String displayOrderChildren) {
        this.displayOrderChildren = displayOrderChildren;
    }

    public void accept(INodeVisitor nodeVisitor) {
        throw new RuntimeException("Not Implemented");
    }

    public Long getSystemNodeId() {
        return systemNodeId;
    }

    public void setSystemNodeId(Long systemNodeId) {
        this.systemNodeId = systemNodeId;
    }

    public String getRuleClassName() {
        return ruleClassName;
    }

    public void setRuleClassName(String ruleClassName) {
        this.ruleClassName = ruleClassName;
    }

    public String getRuleParam() {
        return ruleParam;
    }

    public void setRuleParam(String ruleParam) {
        this.ruleParam = ruleParam;
    }

    public String getVisibilityRuleClassName() {
        return visibilityRuleClassName;
    }

    public void setVisibilityRuleClassName(String visibilityRuleClassName) {
        this.visibilityRuleClassName = visibilityRuleClassName;
    }

    public String getVisibilityRuleParam() {
        return visibilityRuleParam;
    }

    public void setVisibilityRuleParam(String visibilityRuleParam) {
        this.visibilityRuleParam = visibilityRuleParam;
    }

    public String getQuestVersion() {
        return questVersion;
    }

    public void setQuestVersion(String questVersion) {
        this.questVersion = questVersion;
    }

    public EasyVisaNode copy() {
        throw new RuntimeException("Not Implemented");
    }

    public boolean isVisibile() {
        return visibile;
    }

    public void setVisibile(boolean visibile) {
        this.visibile = visibile;
    }

    protected void copyBaseProps(EasyVisaNode target) {
        target.systemNodeId = this.systemNodeId;
        target.id = this.id;
        target.questVersion = this.questVersion;
        target.name = this.name;
        target.order = this.order;
        target.displayText = this.displayText;
        target.ruleClassName = this.ruleClassName;
        target.ruleParam = this.ruleParam;
        target.visibilityRuleClassName = this.visibilityRuleClassName;
        target.visibilityRuleParam = this.visibilityRuleParam;
        target.visibile = this.visibile;
        target.displayOrderChildren = this.displayOrderChildren;
    }

    /**
     * Relationship labels use sometimes use  "answer" value as the releationship label
     * There are some restrictions in relationship label. for example  we cant use '/' , space and some other special characters.
     * so we need to sort of normalize these answer values
     * Example: Parents/Marriage  as  parents_marriage
     * Employed  as  employed
     */
    public static String normalizeAnswer(String answerValue) {
        if (StringUtils.isEmpty(answerValue)) {
            return answerValue;
        }
        answerValue = answerValue.replaceAll("/", "_");// replace '/' as '_'
        answerValue = answerValue.replaceAll(" ", "");//replace empty spaces
        return answerValue.toLowerCase();
    }

    // Function to check String for only Alphabets
    public static Boolean hasValidRelationshipType(String answerValue) {
        return ((answerValue != null) && (!answerValue.equals("")) && (answerValue.matches("^[a-zA-Z]*$")));
    }
}
