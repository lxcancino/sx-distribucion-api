import grails.util.BuildSettings
import grails.util.Environment
import org.springframework.boot.logging.logback.ColorConverter
import org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter
import ch.qos.logback.core.util.FileSize

import java.nio.charset.Charset

conversionRule 'clr', ColorConverter
conversionRule 'wex', WhitespaceThrowableProxyConverter

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        charset = Charset.forName('UTF-8')

        pattern ='%clr(%5p) ' + // Log level
                '%logger{0} ' + // Logger
                '%msg%n' // Message
    }
}

def targetDir = BuildSettings.TARGET_DIR
def USER_HOME = System.getProperty("user.home")
def HOME_DIR = Environment.isDevelopmentMode() ? targetDir : '.'
appender('TASKJOBS', RollingFileAppender) {
    append = false
    encoder(PatternLayoutEncoder) {
        pattern =
                '%d{HH:mm} ' + // Date
                '%5p ' + // Log level
                '%logger{0} ' + // Logger
                '%msg%n' // Message
    }
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${HOME_DIR}/logs/taskjobs-%d{yyyy-MM-dd}.log"
        maxHistory = 5
        totalSizeCap = FileSize.valueOf("1GB")
    }
}


if (Environment.isDevelopmentMode() && targetDir != null) {
    appender("FULL_STACKTRACE", FileAppender) {
        file = "${targetDir}/stacktrace.log"
        append = true
        encoder(PatternLayoutEncoder) {
            pattern = "%level %logger - %msg%n"
        }
    }
    logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false)
    logger("org.springframework.security", OFF, ['STDOUT'], false)
    logger("grails.plugin.springsecurity", OFF, ['STDOUT'], false)
    logger("org.pac4j", OFF, ['STDOUT'], false)

    logger("org.springframework.security", OFF, ['STDOUT'], false)
    logger("grails.plugin.springsecurity", OFF, ['STDOUT'], false)
    logger("grails.views", OFF, ['STDOUT'], false)

    logger("org.pac4j", OFF, ['STDOUT'], false)

    logger("sx.distribucionx.core", DEBUG, ['STDOUT'], false)
    logger("sx.distribucionx.tasks", DEBUG, ['STDOUT', 'TASKJOBS'], false)
} else {
    root(ERROR, ['STDOUT'])
    logger("sx.distribucionx.tasks", INFO, ['TASKJOBS'], false)

}
root(ERROR, ['STDOUT'])




