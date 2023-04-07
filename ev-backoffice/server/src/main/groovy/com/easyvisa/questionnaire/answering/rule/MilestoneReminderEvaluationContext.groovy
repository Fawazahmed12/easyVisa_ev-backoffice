package com.easyvisa.questionnaire.answering.rule

import com.easyvisa.Package
import com.easyvisa.document.DocumentMilestone
import com.easyvisa.questionnaire.model.MilestoneType

class MilestoneReminderEvaluationContext {
    Package aPackage
    DocumentMilestone documentMilestone; // Postgres Data
    MilestoneType milestoneType; // Neo4j Node

    MilestoneReminderEvaluationContext(Package aPackage, DocumentMilestone documentMilestone, MilestoneType milestoneType) {
        this.aPackage = aPackage
        this.documentMilestone = documentMilestone
        this.milestoneType = milestoneType
    }
}
