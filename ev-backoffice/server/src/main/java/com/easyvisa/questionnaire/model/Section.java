package com.easyvisa.questionnaire.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity
public class Section extends EasyVisaNode {

    @Property
    private String applicantType;

    @Property
    private String shortName;

    @Property
    private String sectionCompletionRule;

    @Property
    private String sectionCompletionRuleParam;

    @Property
    private String sectionVisibilityRule;

    @Property
    private String sectionVisibilityRuleParam;

    @Property
    private String completionPercentageRule;

    @Property
    private String completionPercentageRuleParam;


    public Section() {
    }

    public String getApplicantType() {
        return applicantType;
    }

    public void setApplicantType(String applicantType) {
        this.applicantType = applicantType;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getSectionCompletionRule() {
        return sectionCompletionRule;
    }

    public void setSectionCompletionRule(String sectionCompletionRule) {
        this.sectionCompletionRule = sectionCompletionRule;
    }

    public String getSectionCompletionRuleParam() {
        return sectionCompletionRuleParam;
    }

    public void setSectionCompletionRuleParam(String sectionCompletionRuleParam) {
        this.sectionCompletionRuleParam = sectionCompletionRuleParam;
    }

    public String getSectionVisibilityRule() {
        return sectionVisibilityRule;
    }

    public void setSectionVisibilityRule(String sectionVisibilityRule) {
        this.sectionVisibilityRule = sectionVisibilityRule;
    }

    public String getSectionVisibilityRuleParam() {
        return sectionVisibilityRuleParam;
    }

    public void setSectionVisibilityRuleParam(String sectionVisibilityRuleParam) {
        this.sectionVisibilityRuleParam = sectionVisibilityRuleParam;
    }

    public String getCompletionPercentageRule() {
        return completionPercentageRule;
    }

    public void setCompletionPercentageRule(String completionPercentageRule) {
        this.completionPercentageRule = completionPercentageRule;
    }

    public String getCompletionPercentageRuleParam() {
        return completionPercentageRuleParam;
    }

    public void setCompletionPercentageRuleParam(String completionPercentageRuleParam) {
        this.completionPercentageRuleParam = completionPercentageRuleParam;
    }

    @Override
    public void accept(INodeVisitor nodeVisitor) {
        nodeVisitor.visit(this);
    }


    @Override
    public EasyVisaNode copy() {
        Section section = new Section();
        this.copyBaseProps(section);
        return section;
    }

    @Override
    protected void copyBaseProps(EasyVisaNode target) {
        super.copyBaseProps(target);
        Section targetSection = (Section) target;
        targetSection.applicantType = this.applicantType;
        targetSection.shortName = this.shortName;
        targetSection.sectionCompletionRule = this.sectionCompletionRule;
        targetSection.sectionCompletionRuleParam = this.sectionCompletionRuleParam;
        targetSection.sectionVisibilityRule = this.sectionVisibilityRule;
        targetSection.sectionVisibilityRuleParam = this.sectionVisibilityRuleParam;
        targetSection.completionPercentageRule = this.completionPercentageRule;
        targetSection.completionPercentageRuleParam = this.completionPercentageRuleParam;
    }
}
