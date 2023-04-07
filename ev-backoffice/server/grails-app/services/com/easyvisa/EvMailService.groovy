package com.easyvisa

import com.easyvisa.dto.EmailDto
import com.easyvisa.enums.EmailTemplateType
import com.easyvisa.enums.EmailTemplateVariable
import com.easyvisa.enums.TemplateVariableType
import com.easyvisa.questionnaire.util.DateUtil
import com.easyvisa.utils.ExceptionUtils
import com.easyvisa.utils.StringUtils
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional
import grails.gsp.PageRenderer
import grails.plugins.mail.MailService
import groovy.transform.CompileDynamic
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailMessage
import ua_parser.Client

import java.time.LocalDateTime
import java.util.regex.Matcher
import java.util.regex.Pattern

import static com.easyvisa.enums.EmailTemplateType.*

@GrailsCompileStatic
class EvMailService {

    MailService mailService
    PageRenderer groovyPageRenderer
    PackageService packageService
    EmailVariableService emailVariableService
    AlertService alertService
    AsyncService asyncService

    @Value('${frontEndAppURL}')
    String frontEndAppURL
    @Value('${verifyRegistrationLink}')
    String verifyRegistrationLink
    private String registrationEmailBody = '/email/internal/registration'
    @Value('${registration.emailSubject}')
    String registrationEmailSubject

    @Value('${resetPasswordLink}')
    String resetPasswordLink
    private String forgotPasswordEmailBody = '/email/internal/forgotPassword'
    @Value('${forgotPassword.emailSubject}')
    String forgotPasswordEmailSubject

    @Value('${showUsernameLink}')
    String showUsernameLink
    private String forgotUsernameEmailBody = '/email/internal/forgotUsername'
    @Value('${forgotUsername.emailSubject}')
    String forgotUsernameEmailSubject

    @Value('${easyvisa.emailFromName}')
    String emailFromName
    @Value('${easyvisa.emailFromEmail}')
    String emailFromEmail

    @Value('${emailChanged.emailSubject}')
    String emailChangedSubject

    MailMessage sendEmail(EmailDto emailDto) {
        mailService.sendMail {
            multipart emailDto.attachment ? true : false
            to emailDto.toEmail
            replyTo emailDto.replyTo
            from "${emailDto.fromName}<${emailDto.fromEmail}>"
            subject emailDto.emailSubject
            html emailDto.emailBody
            if (emailDto.attachment) {
                attach((emailDto.fileName ?: emailDto.attachment.name), emailDto.attachment)
            }
        }
    }

    EmailDto buildEasyVisaEmailDto(String to, String subject, String body, String source = null, String replyToEmail = null) {
        EmailDto emailDto = new EmailDto()
        emailDto.with {
            toEmail = to
            fromName = source ?: emailFromName
            fromEmail = emailFromEmail
            replyTo = replyToEmail ?: emailFromEmail
            emailSubject = subject
            emailBody = body
        }
        emailDto
    }

    EmailDto buildInviteToColleaguesEmailDto(LegalRepresentative attorney, String to, String subject, String body) {
        EmailDto emailDto = new EmailDto()
        String repEmail = attorney.profile.email
        emailDto.with {
            toEmail = to
            fromName = attorney.profile.fullName
            fromEmail = repEmail
            replyTo = repEmail
            emailSubject = subject
            emailBody = body
        }
        emailDto
    }

    void sendAttorneyRegistrationEmail(Profile user, final RegistrationCode registrationCode) {
        sendEmailAsync({
            final String url = "$frontEndAppURL$verifyRegistrationLink?token=${registrationCode.token}"
            final String emailBody = evaluate(registrationEmailBody, [user: user, url: url])
            final String emailSubject = "${registrationEmailSubject}, ${user.firstName}!"
            sendEmail(buildEasyVisaEmailDto(user.email, emailSubject, emailBody))
        }, "(Re)Send attorney registration email. Profile [${user.id}]")
    }

    void sendForgotpasswordEmail(Profile profile, final RegistrationCode registrationCode) {
        sendEmailAsync({
            sendForgotEmail(registrationCode, profile, forgotPasswordEmailBody, forgotPasswordEmailSubject, resetPasswordLink)
        }, "Send forgot password email. Profile [${profile.id}]")
    }

    void sendForgotUsernameEmail(Profile profile, final RegistrationCode registrationCode) {
        sendEmailAsync({
            sendForgotEmail(registrationCode, profile, forgotUsernameEmailBody, forgotUsernameEmailSubject, showUsernameLink)
        }, "Send forgot username email. Profile [${profile.id}]")
    }

    void sendNewDeviceEmail(Profile profile, Client client, LocalDateTime date) {
        sendEmailAsync({
            String emailBody = alertService.renderTemplate('/email/internal/userNewDevice',
                    [date: DateUtil.fromDateTime(date), os: client.os.family, browser: client.userAgent.family])
            sendEmail(buildEasyVisaEmailDto(profile.email, 'Login from new device', emailBody))
        }, "Send new device. Profile [${profile.id}]")
    }

    Map generateEmailContent(EmailTemplateType emailTemplateType, Map params, Boolean getDefault = false) {
        LegalRepresentative representative = emailVariableService.getLegalRepresentative(params)
        Package packageObj = emailVariableService.getEvPackage(params)

        Boolean useSystemTemplate = getDefault || (!(packageObj || representative))
        if (useSystemTemplate) {
            getRenderedTemplate(null, emailTemplateType, representative, params)
        } else {
            if (packageObj) {
                EmailTemplate template = EmailTemplate.findByAttorneyAndTemplateType(representative, emailTemplateType) ?:
                        EmailTemplate.findByOrganizationAndTemplateType(packageObj?.organization, emailTemplateType)
                getRenderedTemplate(template, emailTemplateType, representative, params)
            } else if (representative) {
                EmailTemplate template = EmailTemplate.findByAttorneyAndTemplateType(representative, emailTemplateType)
                getRenderedTemplate(template, emailTemplateType, representative, params)
            }
        }
    }

    Map getRenderedTemplate(EmailTemplate template, EmailTemplateType emailTemplateType, LegalRepresentative representative, Map params) {
        String subject, content
        if (template) {
            content = template.htmlContent
            subject = template.subject
        } else {
            content = groovyPageRenderer.render(template: emailTemplateType.path, model: params)
            subject = emailTemplateType.subject
        }
        [content         : content,
         subject         : emailTemplateType.subject,
         templateType    : emailTemplateType.name(),
         representativeId: representative?.id]
    }

    @Transactional
    Email saveEmail(String content, String subject, EmailTemplateType templateType, Long packageId, Long representativeId) {
        validateVariables(templateType, subject, content)
        Package aPackage = Package.get(packageId)
        Email email = Email.findByTemplateTypeAndAPackage(templateType, aPackage) ?:
                new Email(templateType: templateType, aPackage: aPackage)
        if (representativeId) {
            email.attorney = LegalRepresentative.get(representativeId)
        }
        email.content = content
        email.subject = subject
        email.save(failOnError: true)
    }

    Email updateEmail(Email email, String content, String subject) {
        if (content) {
            email.content = content
        }
        if (subject) {
            email.subject = subject
        }
        email.save(failOnError: true)
    }

    List<EmailTemplateVariable> getTemplateVariables(String content) {
        List<EmailTemplateVariable> variables = []
        Matcher s = (content =~ /(\|[\w-]+\|)/)
        while (s.find()) {
            String s1 = s.group()
            try {
                variables << EmailTemplateVariable.valueOf(s.group().replaceAll(/\|/, ''))
            }
            catch (Exception e) {
                log.debug("Exception resolving Variable - ${e}")
            }
        }
        variables
    }

    List<String> getUnknownTemplateVariables(EmailTemplateType type, String content) {
        List<EmailTemplateVariable> supportedVariables = EmailTemplateVariable.getEmailTemplateVariables(type)
        List<String> result = []
        Matcher s = (content =~ /(\|[\w-]+\|)/)
        while (s.find()) {
            String variable = s.group().replaceAll(/\|/, '')
            try {
                EmailTemplateVariable emailVariable = EmailTemplateVariable.valueOf(variable)
                if (!supportedVariables.contains(emailVariable)) {
                    result << "|$variable|".toString()
                }
            }
            catch (Exception e) {
                result << "|$variable|".toString()
            }
        }
        result
    }

    @CompileDynamic
    String evaluateTemplate(String content, Map params) {
        List<EmailTemplateVariable> variables = getTemplateVariables(content)
        variables.each { EmailTemplateVariable variable ->
            content = content.replaceAll(Pattern.quote("|${variable.name()}|"), {
                resolveEmailTemplateVariable(variable, params) ?: variable.name()
            })
        }
        content
    }

    @CompileDynamic
    String getVariableValue(EmailTemplateVariable variable, Map params) {
        if (emailVariableService.getPreviewMode(params) && variable.isNotPreviewable) {
            "|${variable.name()}|"
        } else {
            final String methodResolver = StringUtils.toCamelCase(variable.name().toLowerCase())
            emailVariableService."${methodResolver}"(params)
        }
    }

    String resolveEmailTemplateVariable(EmailTemplateVariable variable, Map params) {
        if (variable.type == TemplateVariableType.FRAGMENT) {
            final String fragmentContent = generateEmailContent(variable.templateType, params)['content']
            evaluateTemplate(fragmentContent, params)
        } else {
            getVariableValue(variable, params)
        }
    }

    @Transactional
    EmailTemplate saveEmailTemplate(CreateEmailTemplateCommand emailTemplateCommand) {
        validateVariables(emailTemplateCommand.templateType, emailTemplateCommand.subject, emailTemplateCommand.content)
        EmailTemplate template = EmailTemplate.findByAttorneyAndTemplateType(emailTemplateCommand.attorney, emailTemplateCommand.templateType)
        if (template) {
            template.subject = emailTemplateCommand.subject
            template.content = emailTemplateCommand.content
        } else {
            template = new EmailTemplate(content: emailTemplateCommand.content, subject: emailTemplateCommand.subject, attorney: emailTemplateCommand.attorney, templateType: emailTemplateCommand.templateType)
        }
        template.save(failOnError: true)
    }

    void validateVariables(EmailTemplateType type, String subject, String content) {
        List<String> unknownVariables = getUnknownTemplateVariables(type, subject)
        unknownVariables.addAll(getUnknownTemplateVariables(type, content))
        if (unknownVariables) {
            throw ExceptionUtils.createUnProcessableDataException('emailtemplate.unknown.variables', null, [unknownVariables])
        }
    }

    Email findTemplate(EmailTemplateType templateType, Package aPackage, LegalRepresentative representative) {
        Email.createCriteria().get {
            eq('templateType', templateType)
            if (aPackage) {
                eq('aPackage', aPackage)
            }
            if (representative) {
                eq('attorney', representative)
            }
        } as Email
    }

    Map buildPackageEmailParams(Package aPackage) {
        Map params = [:]
        params = emailVariableService.addPackage(params, aPackage)
        params = emailVariableService.addLegalRepresentative(params, aPackage.attorney)
        params = emailVariableService.addOrganization(params, aPackage.organization)
        params
    }

    void sendEmailByTemplateType(Email email) {
        switch (email.templateType) {
            case NEW_CLIENT: packageService.sendWelcomeEmail(email.aPackage)
                break
            case INVITE_APPLICANT: packageService.sendAllApplicantsInvite(email.aPackage)
                break
            case UPDATED_CLIENT: packageService.sendPackageUpdatedEmail(email.aPackage)
                break
            default: log.debug("No Email defined with Tempalte - ${email.templateType} for Package with id - ${email?.aPackage?.id}")
        }
    }

    void sendAlertEmail(Alert alert) {
        if (alert.body) {
            sendAlert(alert)
        } else {
            sendEmailAsyncDelayed( {
                sendAlert(alert.id)
            }, "Send Alert [${alert.messageType}] to User [${alert.recipient?.id}]")
        }
    }

    private void sendAlert(Long alertId) {
        sendAlert(Alert.get(alertId))
    }

    private void sendAlert(Alert alert) {
        Profile profile = Profile.findByUser(alert.recipient)
        Boolean representative = profile.user.isRepresentative()
        if (!representative || (representative && profile.getEmailPreference(alert.messageType.notificationType))) {
            EmailDto emailDto = new EmailDto()
            emailDto.with {
                toEmail = profile.email
                fromName = alert.source ?: emailFromName
                fromEmail = emailFromEmail
                replyTo = emailFromEmail
                emailSubject = alert.subject
                emailBody = alertService.renderSystemMessageContent(alert)
            }
            sendEmail(emailDto)
        }
    }

    void sendWarningEmail(Warning warning, LegalRepresentative attorney) {
        if (warning.body) {
            sendWarning(attorney, warning)
        } else {
            sendEmailAsyncDelayed({
                sendWarning(attorney.id, warning.id)
            }, "Send Warning [${warning.messageType}] to Attorney [${attorney.id}]")
        }
    }

    private void sendWarning(Long attorneyId, Long warningId) {
        sendWarning(LegalRepresentative.get(attorneyId), Warning.get(warningId))
    }

    private void sendWarning(LegalRepresentative attorney, Warning warning) {
        if (attorney.profile.getEmailPreference(warning.messageType.notificationType)) {
            EmailDto emailDto = new EmailDto()
            emailDto.with {
                toEmail = attorney.attorneyEmail
                fromName = warning.source ?: emailFromName
                fromEmail = emailFromEmail
                replyTo = emailFromEmail
                emailSubject = warning.subject
                emailBody = alertService.renderSystemMessageContent(warning)
            }
            sendEmail(emailDto)
        }
    }

    void sendAddNewEmployeeEmail(Employee employee, RegistrationCode registrationCode, Employee addedBy, Organization organization) {
        sendEmailAsync({
            sendAddNewEmployee(organization, registrationCode, employee, addedBy)
        }, "Send add new employee email for Employee [${employee.id}]")
    }

    private void sendAddNewEmployee(Organization organization, RegistrationCode registrationCode, Employee employee, Employee addedBy) {
        String content = groovyPageRenderer.render(template: NEW_EMPLOYEE_REGISTRATION_INVITE.path)
        String subject = NEW_EMPLOYEE_REGISTRATION_INVITE.subject
        EmailTemplate welComeEmail = EmailTemplate.findByOrganizationAndTemplateType(organization, NEW_EMPLOYEE_REGISTRATION_INVITE)
        if (welComeEmail) {
            content = welComeEmail.htmlContent
            subject = welComeEmail.subject
        }
        Map params = [:]
        params = emailVariableService.addEmployeeRegistrationCode(params, registrationCode)
        params = emailVariableService.addEmployee(params, employee)
        params = emailVariableService.addAdmin(params, addedBy)
        sendEmail(buildEasyVisaEmailDto(employee.profile.email, subject, evaluateTemplate(content, params)))
    }

    void sendPackageTransferredEmail(Package aPackage, LegalRepresentative oldRep, LegalRepresentative newRep) {
        String templatePath = PACKAGE_TRANSFER_SUCCESSFUL.path

        String email = aPackage.client.profile.email
        if (templatePath && email) {
            String content = groovyPageRenderer.render(template: templatePath, model: [packageObj: aPackage,
                                                                                       oldRep    : oldRep, newRep: newRep])
            sendEmail(buildEasyVisaEmailDto(email, PACKAGE_TRANSFER_SUCCESSFUL.subject, content))
        }
    }

    protected String evaluate(String s, Map binding) {
        alertService.renderTemplate(s, binding)
    }

    private void sendEmailAsync(Runnable command, String name) {
        asyncService.runAsync(command, name)
    }

    private void sendEmailAsyncDelayed(Runnable command, String name) {
        asyncService.runAsyncDelayed(command, name)
    }

    private void sendForgotEmail(RegistrationCode registrationCode, Profile profile, String template, String subject, String path) {
        final String url = "$frontEndAppURL$path?token=${registrationCode.token}"
        final String emailBody = evaluate(template, [user: profile, url: url])
        sendEmail(buildEasyVisaEmailDto(profile.email, subject, emailBody))
    }

}
