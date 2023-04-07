package com.easyvisa

import com.easyvisa.enums.EmailTemplateType
import com.easyvisa.utils.StringUtils
import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException
import org.apache.http.HttpStatus
import org.springframework.context.MessageSource

class EmailController implements IErrorHandler {

    EvMailService evMailService
    MessageSource messageSource
    EmailVariableService emailVariableService

    @SuppressWarnings('FactoryMethodName')
    @Secured([Role.EMPLOYEE])
    def create(EmailCommand emailCommand) {
        Email email = evMailService.saveEmail(emailCommand.content, emailCommand.subject,
                emailCommand.templateType, emailCommand.packageId, emailCommand.representativeId)
        if (emailCommand.sendEmail) {
            evMailService.sendEmailByTemplateType(email)
        }
        render(view: '/email', model: [email: email], status: HttpStatus.SC_CREATED)
    }

    @Secured([Role.EMPLOYEE])
    def update(Long id, EmailCommand emailCommand) {
        Email email = Email.get(id)
        if (email) {
            evMailService.updateEmail(email, emailCommand.content, emailCommand.subject)
            if (emailCommand.sendEmail) {
                evMailService.sendEmailByTemplateType(email)
            }
            render(view: '/email',
                    model: [
                            'email'  : email,
                            'message': messageSource.getMessage('package.applicant.invite.resent', null, request.locale)
                    ], status: HttpStatus.SC_OK)
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'email.not.found.with.id')
        }
    }

    @Secured([Role.EMPLOYEE])
    def read(Long id) {
        Email email = Email.get(id)
        if (email) {
            render(view: '/email', model: [email: email], status: HttpStatus.SC_OK)
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'email.not.found.with.id')
        }
    }

    @Secured([Role.EMPLOYEE])
    def getEmailTemplate(String emailTemplateType, EmailTemplateCommand emailTemplateCommand) {
        EmailTemplateType templateType = EmailTemplateType.values().find { it.name() == emailTemplateType }
        if (templateType) {
            Map params = emailVariableService.addPackage([:], emailTemplateCommand.easyVisaPackage)
            params = emailVariableService.addLegalRepresentative(params, emailTemplateCommand.attorney)
            final Map templateResponse = getSanitizedEmailContent(templateType, params, emailTemplateCommand.defaultTemplate)
            render(templateResponse as JSON)
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'emailtemplate.not.found.with.name')
        }
    }

    @Secured([Role.EMPLOYEE])
    def getEmailTemplates(EmailTemplateCommand emailTemplateCommand) {
        List templateContent = emailTemplateCommand.templateType.collect {
            Map params = emailVariableService.addPackage([:], emailTemplateCommand.easyVisaPackage)
            params = emailVariableService.addLegalRepresentative(params, emailTemplateCommand.attorney)
            getSanitizedEmailContent(it, params, emailTemplateCommand.defaultTemplate)
        }
        render(templateContent as JSON)
    }

    @Secured([Role.EMPLOYEE])
    def preview(Long id) {
        Email email = Email.get(id)
        if (email) {
            Map params = buildEmailParams(email.aPackage, email.attorney, email.organization)
            String previewHtml = evMailService.evaluateTemplate(email.htmlContent, emailVariableService.setPreviewMode(params))
            render([preview: previewHtml] as JSON)
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'email.not.found')
        }
    }

    @Secured([Role.EMPLOYEE])
    def previewTemplate(String emailTemplateType, PreviewEmailTemplateCommand previewEmailTemplateCommand) {
        EmailTemplateType templateType = EmailTemplateType.valueOf(emailTemplateType)
        if (templateType) {
            Map params = buildEmailParams(previewEmailTemplateCommand.easyVisaPackage, previewEmailTemplateCommand.attorney)
            params = emailVariableService.addApplicant(params, previewEmailTemplateCommand.easyVisaPackage.client)
            def feeCharge = previewEmailTemplateCommand.getFeeCharges()
            params['charges'] = feeCharge.charges as List
            params['total'] = feeCharge.total as BigDecimal
            String previewHtml = evMailService.evaluateTemplate(StringUtils.textToHTML(previewEmailTemplateCommand.content), emailVariableService.setPreviewMode(params))
            render([preview: previewHtml, subject: templateType.subject] as JSON)
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'emailtemplate.not.found.with.name')
        }
    }

    @Secured([Role.EMPLOYEE])
    def find(FindEmailCommand emailCommand) {
        Email email = evMailService.findTemplate(emailCommand.templateType, emailCommand.packageObj, emailCommand.representative)
        if (email) {
            render([content         : email.content,
                    subject         : email.subject,
                    templateType    : email.templateType.name(),
                    representativeId: email.attorney?.id,
                    packageId       : email.aPackage?.id,
                    id              : email.id] as JSON)
        } else {
            renderError(HttpStatus.SC_NOT_FOUND, 'email.not.found')
        }

    }

    @Secured([Role.EMPLOYEE])
    def createTemplate(CreateEmailTemplateCommand emailCommand) {
        EmailTemplate template = evMailService.saveEmailTemplate(emailCommand)
        render([content         : template.content,
                subject         : template.subject,
                templateType    : template.templateType.name(),
                representativeId: template.attorney?.id] as JSON)

    }

    private buildEmailParams(Package aPackage, LegalRepresentative representative, Organization organization = null) {
        Map params = emailVariableService.addPackage([:], aPackage)
        params = emailVariableService.addLegalRepresentative(params, representative ?: aPackage?.attorney)
        emailVariableService.addOrganization(params, organization)
    }

    private Map getSanitizedEmailContent(EmailTemplateType templateType, Map params, Boolean defaultTemplate) {

        Map result = evMailService.generateEmailContent(templateType, params, defaultTemplate)
        result['content'] = result['content'].replaceAll('<br/>', '\n')
        result
    }

}
