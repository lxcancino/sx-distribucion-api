package sx.distribucionx

import groovy.util.logging.Slf4j

import grails.rest.*
import grails.converters.*

@Slf4j
class SurtidoController extends RestfulController<Surtido> {
    
    static responseFormats = ['json']

    SurtidoController() {
        super(Surtido)
    }
}
