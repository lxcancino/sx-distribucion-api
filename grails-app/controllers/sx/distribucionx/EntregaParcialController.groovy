package sx.distribucionx


import grails.rest.*
import grails.converters.*
import sx.security.User
import org.apache.commons.lang3.exception.ExceptionUtils

class EntregaParcialController extends RestfulController {

    EntregaParcialService entregaParcialService

     

    static responseFormats = ['json']
    EntregaParcialController() {
        super(EntregaParcial)
    }

    def parciales(){
        def parciales = entregaParcialService.getParciales()
        respond parciales
    }

    def entrega(String entregaId) {
        def entrega = EntregaParcial.get(entregaId)
        if(entrega)
            respond entrega
        else
            return [] 
    }

    
    protected EntregaParcial updateResource(EntregaParcial resource) {

        println resource
        //return recepcionDeCompraService.updateRecepcion(resource)

        return[]
    }

    def entregasParciales(){
        def parciales = EntregaParcial.findAllByEstado('PENDIENTE')
        respond parciales
    }

    def buscarVentaParcial() {
        if(!params.folio){
           return []
        }
        def parciales = this.entregaParcialService.buscarVentaParcial(params.folio)
        respond parciales
    }   

    def crearEntregaParcial(){

        println params 

        if( !params.ventaId ) {
            respond new Error(error: true, message: 'Seleccione una venta', status: 500)
            return
        }
         if( !params.userId ) {
            respond new Error(error: true, message: 'No hay usuario registrado', status: 500)
            return
        }
  
        def user = User.get(params.userId)
        if(!user){
            respond new Error(error: true, message: 'Operador no encontrado', status: 500)
            return
        }
        if(!user.getAuthorities().find{it.authority=='ROLE_SUPERVISOR_ENTREGA'}){
            respond new Error(error: true, message: "No tiene el ROL de SUPERVISOR_ENTREGA verifique su NIP ", status: 500)
            return
        }
        def parcial = entregaParcialService.iniciar(params.ventaId , user)
    
        respond parcial
    }

    
    def crearSurtido(){

        // println params
        // println 'Working ...'
        def surtidoJson =  request.JSON

        def emp = User.get(params.empId)


        def user = User.get(params.userId)

 

        if(!user){
            respond new Error(error: true, message: 'Operador no encontrado', status: 500)
            return
        }
        if(!user.getAuthorities().find{it.authority=='ROLE_SUPERVISOR_ENTREGA'}){
            respond new Error(error: true, message: "No tiene el ROL de SUPERVISOR_ENTREGA verifique su NIP ", status: 500)
            return
        }
       

        def surtido = entregaParcialService.crearSurtido(surtidoJson, user, emp)

        if(!surtido){
            return []
        }
        respond (surtido: surtido, partidas: surtido.parciales)

    }

    def agregarSurtidoDet(SurtidoDetCommand command){
        def surtido = command.surtido
        println command.surtido.nombre
        def entregaDet = command.entregaDet
        def surtidoDet = new SurtidoDet(entregaDet.properties)
        surtidoDet.cantidad = command.cantidad
        surtidoDet.entregaParcialDet = entregaDet
        surtidoDet.surtido = surtido
        surtido.addToParciales(surtidoDet)
       surtido.save failOnError:true, flush:true
        respond surtido
    }


    def buscarParcial(Long folio){
        if (folio) {
            def query = EntregaParcial.findAll("from EntregaParcial where folioFac = ? or documento = ?    ",[folio,folio])
            if(query)
                respond query
            else
                return []
        }
        else {
            return []
        }
    }

    def buscarPendientes(EntregaParcial entrega){
        //def entrega = EntregaParcial.get(entregaId)
        if(!entrega){
           respond new Error(error: true, message: 'No existe la entrega', status: 500) 
           return
        }
        def pendientes = entrega.partidas?.findAll({it.pendiente != 0})
        if(!pendientes){
            respond new Error(error: true, message: 'no hay pendientes', status: 500)
            return
        }
        respond pendientes
    }

    def detalleParciales(String id ) {

        def entrega = EntregaParcial.get(id)

        def surtidos = Surtido.findAll("from Surtido where origen = ?",[id])

        respond surtidos

    }

     def handleException(Exception e) {
        String message = ExceptionUtils.getRootCauseMessage(e)
        log.error(message, ExceptionUtils.getRootCause(e))
        respond([message: message], status: 500)
    }
}

class SurtidoDetCommand{
    EntregaParcialDet entregaDet
    Surtido surtido
    BigDecimal cantidad
}

