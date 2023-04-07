package com.easyvisa.test.jobs

import com.easyvisa.*
import com.easyvisa.utils.PackageTestBuilder
import grails.testing.mixin.integration.Integration
import groovy.time.TimeCategory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import spock.lang.Specification

@Integration
class UserDeviceCleanJobSpec extends Specification {

    @Autowired
    private UserService userService
    @Autowired
    private AttorneyService attorneyService
    @Autowired
    private ProfileService profileService

    @Value('${local.server.port}')
    Integer serverPort

    private UserDevicesCleanJob cleanJob

    void setup() {
        cleanJob = new UserDevicesCleanJob()
        cleanJob.userService = userService
    }

    void testClean() {
        given:
        PackageTestBuilder testHelper = PackageTestBuilder.init([serverPort     : serverPort,
                                                                 attorneyService: attorneyService,
                                                                 profileService : profileService])
        testHelper.buildPackageLegalRep()
                .logInPackageLegalRep()
                .logInPackageLegalRep()

        UserDevice userDevice
        User user
        Date expired = new Date()
        use(TimeCategory) {
            expired = expired - 3.weeks
        }
        User.withNewTransaction {
            user = testHelper.packageLegalRepresentative.refresh().user
            userDevice = UserDevice.findAllByUser(user).first()
            UserDevice.executeUpdate("update UserDevice ud set dateCreated = :expired where ud = :ud",
                    [expired: expired, ud: userDevice])
        }

        expect:
        runJob()

        List<UserDevice> devices
        User.withNewTransaction {
            devices = UserDevice.findAllByUser(testHelper.packageLegalRepresentative.refresh().user)
        }
        assert 1 == devices.size()
        assert userDevice.id != devices.first().id

        cleanup:
        testHelper.clean()
    }


    private void runJob() {
        AccountTransaction.withNewTransaction {
            cleanJob.execute()
            Boolean.TRUE
        }
    }
}
