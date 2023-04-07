import ch.qos.logback.classic.Level
import ch.qos.logback.classic.PatternLayout
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.util.FileSize
import com.easyvisa.logging.BrokenPipeFilter

import grails.util.BuildSettings
import grails.util.Environment
import org.springframework.boot.logging.logback.ColorConverter
import org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter

conversionRule 'clr', ColorConverter
conversionRule 'wex', WhitespaceThrowableProxyConverter


// See http://logback.qos.ch/manual/groovy.html for details on configuration
if ([Environment.DEVELOPMENT, Environment.TEST].contains(Environment.current)) {
    def targetDir = BuildSettings.TARGET_DIR

    appender('STDOUT', ConsoleAppender) {
        encoder(PatternLayoutEncoder) {
            pattern = "%d{yyyy-MM-dd HH:mm:ss} [%thread] %level %logger - %msg%n"
        }
    }

    appender('FULL_STACKTRACE', FileAppender) {
        file = "${targetDir}/stacktrace.log"
        append = true
        encoder(PatternLayoutEncoder) {
            pattern = "%d{yyyy-MM-dd HH:mm:ss} %level %logger - %msg%n"
        }
        filter(BrokenPipeFilter)
    }
    //Grails 4/Gorm 7 should migrate to newer versions of Hibernate/JPA queries (HHH90000022)
    logger('org.hibernate.orm.deprecation', ERROR, ['FULL_STACKTRACE'], false)
    // Logging Transaction boundary
    //logger('org.hibernate.engine.transaction.internal.TransactionImpl', DEBUG, ['STDOUT'], false)

    /*logger 'grails.plugin.springsecurity.web.filter.DebugFilter', DEBUG, ['STDOUT'], false
    logger 'org.springframework.security', DEBUG, ['STDOUT'], false*/

    root(ERROR, ['STDOUT', 'FULL_STACKTRACE'])
} else {
    appender('aws-cloud-watch', io.github.dibog.AwsLogAppender) {
        createLogGroup = false
        queueLength = 200
        groupName = "EasyVisa-${Environment.current.name}"
        streamName = "GrailsApp-${Environment.current.name}"
        dateFormat = 'yyyyMMdd_HHmm'

        layout(PatternLayout) {
            pattern = "%d{yyyyMMdd'T'HHmmss} %thread %level %logger{15} %msg%n"
        }
        filter(BrokenPipeFilter)
    }

    String catalinaBase = System.properties['catalina.base']
    appender('ROLLING', RollingFileAppender) {
        encoder(PatternLayoutEncoder) {
            pattern = "%d{yyyy-MM-dd HH:mm:ss} %thread %level %logger{15} %msg%n"
        }
        rollingPolicy(TimeBasedRollingPolicy) {
            fileNamePattern = "${catalinaBase}/logs/easyvisa-%d{yyyy-MM-dd}.log.gz"
            maxHistory = 90
            totalSizeCap = FileSize.valueOf("1GB")
        }
        filter(BrokenPipeFilter)
    }

    logger('org.hibernate.orm.deprecation', ERROR, ['aws-cloud-watch'], false)
    root(INFO, ['aws-cloud-watch', 'ROLLING'])
}
