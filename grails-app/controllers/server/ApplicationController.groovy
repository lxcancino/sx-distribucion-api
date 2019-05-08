package server

import grails.compiler.GrailsCompileStatic
import grails.core.GrailsApplication

import grails.plugins.*
import groovy.transform.CompileDynamic

@GrailsCompileStatic
class ApplicationController implements PluginManagerAware {

    GrailsApplication grailsApplication
    GrailsPluginManager pluginManager

    def index() {
        [grailsApplication: grailsApplication, pluginManager: pluginManager]
    }

    @CompileDynamic
    def session() {
        Map res = [:]

        Map info = [:]
        info.version = grailsApplication.metadata.getApplicationVersion()
        info.name = grailsApplication.metadata.getApplicationName()
        info.grailsVersion = grailsApplication.metadata.getGrailsVersion()
        info.environment = grailsApplication.metadata.getEnvironment()
        info.dataSourceUrl = grailsApplication.config.dataSource.url
        res.apiInfo = info

        if (isLoggedIn()) {
            res.user = getAuthenticatedUser()
        }
        respond res


    }
}
