package sx.distribucionx

import groovy.util.logging.Slf4j

import grails.rest.*
import grails.converters.*
import org.springframework.beans.factory.annotation.Autowired

@Slf4j
class SurtidoController extends RestfulController<Surtido> {
    
    static responseFormats = ['json']

  
    SurtidoService surtidoService

    SurtidoController() {
        super(Surtido)
    }

    def facturas(){ 
        def facturas = surtidoService.getFacturas()
        respond facturas
    }

    def pedidos(){ 
        def facturas = surtidoService.getPedidos()
        respond facturas
    }

    def vales(){ 
        def facturas = surtidoService.getVales()
        respond facturas
    }

    def transformaciones(){ 
        def facturas = surtidoService.getTransformaciones()
        respond facturas
    }

}
