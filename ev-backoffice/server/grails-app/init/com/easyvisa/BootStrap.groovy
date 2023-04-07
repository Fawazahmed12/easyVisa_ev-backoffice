package com.easyvisa

import grails.converters.JSON

import java.text.DecimalFormat

@SuppressWarnings(['BuilderMethodWithSideEffects', 'FactoryMethodName', 'DuplicateStringLiteral'])
class BootStrap {

    ProfileService profileService
    StartUpService startUpService
    LoginAttemptCacheService loginAttemptCacheService
    TaxService taxService

    def init = { servletContext ->
        JSON.registerObjectMarshaller(BigDecimal) {
            new DecimalFormat('#0.##').format(it)
        }
        loginAttemptCacheService.init()
        profileService.setBlessedOrgRoles()

        environments {
            development {
                taxService.skipTaxes = true
                startUpService.starUpActions()
            }
            test {
                taxService.skipTaxes = true
                startUpService.starUpActions(Boolean.FALSE)
            }
            production {
                startUpService.starUpActions()
                deleteConfigFile()
            }
            qa {
                taxService.skipTaxes = true
                startUpService.starUpActions()
            }
            aws_development {
                taxService.skipTaxes = true
                startUpService.starUpActions()
            }
        }
    }

    @SuppressWarnings('JavaIoPackageAccess')
    void deleteConfigFile() {
        String configPath = System.properties['local.config.location']
        if (configPath) {
            File configFile = new File(configPath)
            configFile.delete()
        }
    }

    def destroy = {
    }
}
