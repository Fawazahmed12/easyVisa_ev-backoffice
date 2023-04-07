package com.easyvisa.test.jobs

import com.easyvisa.*
import com.easyvisa.enums.CitizenshipStatus
import com.easyvisa.enums.EasyVisaSystemMessageType
import com.easyvisa.enums.EmailTemplateType
import com.easyvisa.enums.ImmigrationBenefitCategory
import com.easyvisa.utils.PackageTestBuilder
import com.easyvisa.utils.TestMockUtils
import groovy.time.TimeCategory
import org.springframework.beans.factory.annotation.Autowired

abstract class TestAbstractReminder extends TestMockUtils {

    @Autowired
    protected EvMailService evMailService
    @Autowired
    protected AlertService alertService
    @Autowired
    protected EmailVariableService emailVariableService
    @Autowired
    protected PackageDocumentService packageDocumentService
    @Autowired
    protected AttorneyService attorneyService
    @Autowired
    protected OrganizationService organizationService
    @Autowired
    protected PackageService packageService
    @Autowired
    protected PackageReminderService packageReminderService
    @Autowired
    protected AdminService adminService
    @Autowired
    PaymentService paymentService
    @Autowired
    ProfileService profileService
    PaymentService paymentServiceMock = Mock(PaymentService)
    @Autowired
    protected TaxService taxService
    protected TaxService taxServiceMock = Mock(TaxService)

    protected AbstractAttorneyNotifications job

    void setup() {
        job = setupJob()
        updateToMock(packageService.accountService, paymentServiceMock, taxServiceMock)
    }

    void cleanup() {
        updateToService(packageService.accountService, paymentServiceMock, taxServiceMock)
    }

    void testJob() {
        given:
        Date curDate = new Date()
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = createLeadPackage()
        PackageTestBuilder testHelperOpen = createOpenPackage()
        PackageTestBuilder testHelperOpenTwoDays = createOpenPackage()

        prepareSuccessJob(testHelper, testHelperOpen, testHelperOpenTwoDays)

        expect:
        runJob()

        List<Alert> alerts
        List<PackageReminder> packRems
        Alert.withNewTransaction {
            alerts = Alert.findAllByRecipient(testHelperOpenTwoDays.aPackage.refresh().client.user)
            packRems = PackageReminder.findAll()
        }

        assertSent(alerts, packRems, testHelperOpenTwoDays, curDate)

        cleanup:
        cleanPackages(testHelper, testHelperOpen, testHelperOpenTwoDays)
    }

    void testJobStoppedReminder() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = createLeadPackage()
        PackageTestBuilder testHelperOpen = createOpenPackage()
        PackageTestBuilder testHelperOpenTwoDays = createOpenPackage()

        prepareSuccessJob(testHelper, testHelperOpen, testHelperOpenTwoDays)

        prepareReminder(testHelperOpenTwoDays, 0, Boolean.TRUE)

        expect:
        runJob()

        List<Alert> alerts
        List<PackageReminder> packRems
        Alert.withNewTransaction {
            alerts = Alert.findAllByRecipient(testHelperOpenTwoDays.aPackage.refresh().client.user)
            packRems = PackageReminder.findAll()
        }

        assert alerts.isEmpty()
        assert 1 == packRems.size()
        PackageReminder reminder = packRems.first()
        assert testHelperOpenTwoDays.aPackage.id == reminder.aPackage.id
        assert emailTemplateType.notificationType == reminder.notificationType
        assert reminder.stopped

        cleanup:
        cleanPackages(testHelper, testHelperOpen, testHelperOpenTwoDays)
    }

    void testJobReminderMore120Days() {
        given:
        Date curDate = new Date()
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = createLeadPackage()
        PackageTestBuilder testHelperOpen = createOpenPackage()
        PackageTestBuilder testHelperOpenTwoDays = createOpenPackage()

        prepareSuccessJob(testHelper, testHelperOpen, testHelperOpenTwoDays, 200)

        expect:
        runJob()

        List<Alert> alerts
        List<PackageReminder> packRems
        Alert.withNewTransaction {
            alerts = Alert.findAllByRecipient(testHelperOpenTwoDays.aPackage.refresh().client.user)
            packRems = PackageReminder.findAll()
            Boolean.TRUE
        }

        if (timedOut) {
            assert alerts.isEmpty()
            assert packRems.isEmpty()
        } else {
            assertSent(alerts, packRems, testHelperOpenTwoDays, curDate)
        }

        cleanup:
        cleanPackages(testHelper, testHelperOpen, testHelperOpenTwoDays)
    }

    void testJobReminderMissedSending() {
        given:
        Date curDate = new Date()
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = createLeadPackage()
        PackageTestBuilder testHelperOpen = createOpenPackage()
        PackageTestBuilder testHelperOpenTwoDays = createOpenPackage()

        prepareSuccessJob(testHelper, testHelperOpen, testHelperOpenTwoDays, 7)

        prepareReminder(testHelperOpenTwoDays, 7)

        expect:
        runJob()

        List<Alert> alerts
        List<PackageReminder> packRems
        Alert.withNewTransaction {
            alerts = Alert.findAllByRecipient(testHelperOpenTwoDays.aPackage.refresh().client.user)
            packRems = PackageReminder.findAll()
        }

        assertSent(alerts, packRems, testHelperOpenTwoDays, curDate)

        cleanup:
        cleanPackages(testHelper, testHelperOpen, testHelperOpenTwoDays)
    }

    void testJobSentYesterday() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = createLeadPackage()
        PackageTestBuilder testHelperOpen = createOpenPackage()
        PackageTestBuilder testHelperOpenTwoDays = createOpenPackage()

        prepareSuccessJob(testHelper, testHelperOpen, testHelperOpenTwoDays)

        Date lastSent = prepareReminder(testHelperOpenTwoDays, 1)

        expect:
        runJob()

        List<Alert> alerts
        List<PackageReminder> packRems
        Alert.withNewTransaction {
            alerts = Alert.findAllByRecipient(testHelperOpenTwoDays.aPackage.refresh().client.user)
            packRems = PackageReminder.findAll()
        }

        assertNoSent(alerts, packRems, testHelperOpenTwoDays, lastSent)


        cleanup:
        cleanPackages(testHelper, testHelperOpen, testHelperOpenTwoDays)
    }

    void testJobSentToday() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = createLeadPackage()
        PackageTestBuilder testHelperOpen = createOpenPackage()
        PackageTestBuilder testHelperOpenTwoDays = createOpenPackage()

        prepareSuccessJob(testHelper, testHelperOpen, testHelperOpenTwoDays)

        Date lastSent = prepareReminder(testHelperOpenTwoDays, 0)

        expect:
        runJob()

        List<Alert> alerts
        List<PackageReminder> packRems
        Alert.withNewTransaction {
            alerts = Alert.findAllByRecipient(testHelperOpenTwoDays.aPackage.refresh().client.user)
            packRems = PackageReminder.findAll()
        }

        assertNoSent(alerts, packRems, testHelperOpenTwoDays, lastSent)

        cleanup:
        cleanPackages(testHelper, testHelperOpen, testHelperOpenTwoDays)
    }

    void testJobCheckAgainstLastReminder() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = createLeadPackage()
        PackageTestBuilder testHelperOpen = createOpenPackage()
        PackageTestBuilder testHelperOpenTwoDays = createOpenPackage()

        prepareSuccessJob(testHelper, testHelperOpen, testHelperOpenTwoDays, 7)

        Date lastSent = prepareReminder(testHelperOpenTwoDays, 1)

        expect:
        runJob()

        List<Alert> alerts
        List<PackageReminder> packRems
        Alert.withNewTransaction {
            alerts = Alert.findAllByRecipient(testHelperOpenTwoDays.aPackage.refresh().client.user)
            packRems = PackageReminder.findAll()
        }

        assertNoSent(alerts, packRems, testHelperOpenTwoDays, lastSent)

        cleanup:
        cleanPackages(testHelper, testHelperOpen, testHelperOpenTwoDays)
    }

    void testJobCheckAgainstNewTriggerDate() {
        given:
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = createLeadPackage()
        PackageTestBuilder testHelperOpen = createOpenPackage()
        PackageTestBuilder testHelperOpenTwoDays = createOpenPackage()

        prepareSuccessJob(testHelper, testHelperOpen, testHelperOpenTwoDays, 1)

        Date lastSent = prepareReminder(testHelperOpenTwoDays, 7)

        expect:
        runJob()

        List<Alert> alerts
        List<PackageReminder> packRems
        Alert.withNewTransaction {
            alerts = Alert.findAllByRecipient(testHelperOpenTwoDays.aPackage.refresh().client.user)
            packRems = PackageReminder.findAll()
        }

        assertNoSent(alerts, packRems, testHelperOpenTwoDays, lastSent)

        cleanup:
        cleanPackages(testHelper, testHelperOpen, testHelperOpenTwoDays)
    }

    void testJobSentByTheReminder() {
        given:
        Date curDate = new Date()
        successPayMock(paymentServiceMock, taxServiceMock)
        PackageTestBuilder testHelper = createLeadPackage()
        PackageTestBuilder testHelperOpen = createOpenPackage()
        PackageTestBuilder testHelperOpenTwoDays = createOpenPackage()

        prepareSuccessJob(testHelper, testHelperOpen, testHelperOpenTwoDays, 7)

        prepareReminder(testHelperOpenTwoDays, 2)

        expect:
        runJob()

        List<Alert> alerts
        List<PackageReminder> packRems
        Alert.withNewTransaction {
            alerts = Alert.findAllByRecipient(testHelperOpenTwoDays.aPackage.refresh().client.user)
            packRems = PackageReminder.findAll()
        }

        assertSent(alerts, packRems, testHelperOpenTwoDays, curDate)

        cleanup:
        cleanPackages(testHelper, testHelperOpen, testHelperOpenTwoDays)
    }

    private Date prepareReminder(testHelperOpenTwoDays, Integer days, Boolean stopped = Boolean.FALSE) {
        Date lastSent = getLastActivityDate(days)
        Package.withNewTransaction {
            new PackageReminder(aPackage: testHelperOpenTwoDays.aPackage, lastSent: lastSent, stopped: stopped,
                    notificationType: emailTemplateType.notificationType)
                    .save(failOnError: true)
        }
        lastSent
    }

    protected abstract EmailTemplateType getEmailTemplateType()

    protected abstract EasyVisaSystemMessageType getAlertType()

    protected abstract AbstractAttorneyNotifications setupJob()

    protected abstract void prepareSuccessJob(PackageTestBuilder testHelper, PackageTestBuilder testHelperOpen,
                                              PackageTestBuilder testHelperOpenTwoDays, Integer lastActivity = null)

    protected abstract Boolean isTimedOut()

    protected abstract ImmigrationBenefitCategory getPrincipalBeneficiaryCategory()

    protected Date getLastActivityDate(Integer minusDays = 2) {
        Integer toMinus = minusDays != null ? minusDays : 2
        use(TimeCategory) {
            new Date().clearTime() - toMinus.days
        }
    }

    protected void createAttorneyNotifications(PackageTestBuilder testHelper, PackageTestBuilder testHelperOpen,
                                               PackageTestBuilder testHelperOpenTwoDays) {
        List<AttorneyNotificationCommand> notifications = createNotificationsCommand()
        attorneyService.updateAttorneyNotifications(testHelperOpenTwoDays.packageLegalRepresentative.refresh(), notifications)
        attorneyService.updateAttorneyNotifications(testHelperOpen.packageLegalRepresentative.refresh(), notifications)
        attorneyService.updateAttorneyNotifications(testHelper.packageLegalRepresentative.refresh(), notifications)
    }

    protected List<AttorneyNotificationCommand> createNotificationsCommand() {
        [new AttorneyNotificationCommand(content: 'content', repeatInterval: 2, templateType: emailTemplateType)]
    }

    protected void runJob() {
        Package.withNewTransaction {
            job.execute()
            Boolean.TRUE
        }
    }

    private PackageTestBuilder createOpenPackage() {
        PackageTestBuilder testHelper = PackageTestBuilder.init([attorneyService: attorneyService,
                                                                 packageService : packageService,
                                                                 adminService   : adminService,
                                                                 profileService : profileService])
        testHelper.buildPetitionerAndBeneficiaryOpenPackage(true, getPrincipalBeneficiaryCategory())
                .buildUsersForPackageApplicants()
    }

    private PackageTestBuilder createLeadPackage() {
        PackageTestBuilder testHelper = PackageTestBuilder.init([attorneyService    : attorneyService,
                                                                 organizationService: organizationService,
                                                                 packageService     : packageService,
                                                                 profileService     : profileService])
        testHelper.buildPetitionerAndBeneficiaryLeadPackage()
                .buildUsersForPackageApplicants()
    }

    private void cleanPackages(PackageTestBuilder testHelper, PackageTestBuilder testHelperOpen, PackageTestBuilder testHelperOpenTwoDays) {
        testHelper.clean()
        testHelperOpen.clean()
        testHelperOpenTwoDays.clean()
    }

    private boolean assertSent(List<Alert> alerts, List<PackageReminder> packRems,
                               PackageTestBuilder testHelperOpenTwoDays, Date curDate) {
        assert 1 == alerts.size()
        assert alertType == alerts.first().messageType
        assert 'content' == alerts.first().body
        assert 1 == packRems.size()
        PackageReminder reminder = packRems.first()
        assert testHelperOpenTwoDays.aPackage.id == reminder.aPackage.id
        assert emailTemplateType.notificationType == reminder.notificationType
        assert curDate < reminder.lastSent
        assert !reminder.stopped
        true
    }

    private boolean assertNoSent(List<Alert> alerts, List<PackageReminder> packRems, PackageTestBuilder testHelperOpenTwoDays, Date lastSent) {
        assert alerts.isEmpty()
        assert 1 == packRems.size()
        PackageReminder reminder = packRems.first()
        assert testHelperOpenTwoDays.aPackage.id == reminder.aPackage.id
        assert emailTemplateType.notificationType == reminder.notificationType
        assert lastSent == reminder.lastSent
        assert !reminder.stopped
        true
    }

}
