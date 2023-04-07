package com.easyvisa.questionnaire.model;

import java.util.List;
import java.util.Map;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity
public class SubSection extends EasyVisaNode {

	@Property
	private String wrapperName;

	@Property
	private String styleClassName;

	public SubSection() {
	}

	public String getWrapperName() {
		return wrapperName;
	}

	public void setWrapperName(String wrapperName) {
		this.wrapperName = wrapperName;
	}

	public String getStyleClassName() {
		return styleClassName;
	}

	public void setStyleClassName(String styleClassName) {
		this.styleClassName = styleClassName;
	}

	@Override
	public void accept(INodeVisitor nodeVisitor) {
		nodeVisitor.visit(this);
	}

	@Override
	public EasyVisaNode copy() {
		SubSection subSection = new SubSection();
		this.copyBaseProps(subSection);
		return subSection;
	}

	@Override
	protected void copyBaseProps(EasyVisaNode target) {
		super.copyBaseProps(target);
		SubSection targetQuestion = (SubSection) target;
		targetQuestion.wrapperName = this.wrapperName;
		targetQuestion.styleClassName = this.styleClassName;
	}

	public void filter(Map<String, List<EasyVisaNode>> dynamicNodeMap) {
		if (dynamicNodeMap.containsKey(this.getId())) {

		}
	}
}
