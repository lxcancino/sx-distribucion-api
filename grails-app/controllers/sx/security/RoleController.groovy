package sx.security


import grails.rest.*
import grails.converters.*

class RoleController extends RestfulController {
    static responseFormats = ['json', 'xml']
    RoleController() {
        super(Role)
    }
}
