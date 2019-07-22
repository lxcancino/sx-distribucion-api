package sx.distribucionx

import groovy.util.logging.Slf4j

import grails.rest.*
import grails.converters.*
import org.springframework.beans.factory.annotation.Autowired
import sx.security.User
import org.apache.commons.lang3.exception.ExceptionUtils


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
        def trs = surtidoService.getTransformaciones()
       respond trs
    }

   def iniciar(String id, String tipo, String nip){
       if(!nip){
            respond new Error(error: true, message: 'Digite su NIP', status: 500)
            return
       }
       def user = User.findByNip(nip)
       if(!user){
           respond new Error(error: true, message: 'Operador no encontrado', status: 500)
           return
       }
       def surtido 
       switch(tipo){
            case 'FAC':
                surtido =  surtidoService.iniciarSurtidoVenta(id,tipo,user,null)
            break
            case 'PST':
                surtido = surtidoService.iniciarSurtidoVenta(id,tipo,user,null)          
            break
            case 'SOL':
                surtido = surtidoService.iniciarSurtidoSol(id,tipo,user,null)       
            break
            case 'TRS':
                surtido = surtidoService.iniciarSurtidoTrs(id,tipo,user,null)        
            break
            default:
            break
       }

       respond surtido
       
   }

   def asignacionManual(String id, String tipo ,String nip, Long surtidorId) {

       if(!nip && !surtidorId){
          respond new Error(error: true, message: 'Digite su NIP', status: 500)
           return
       }

        if(!nip){
            respond new Error(error: true, message: 'Digite su NIP', status: 500)
            return
        }
        def user = User.findByNip(nip)
        if(!user){
           respond new Error(error: true, message: 'Operador no encontrado', status: 500)
           return
        }
        if(!user.getAuthorities().find{it.authority=='ROLE_SUPERVISOR_SURTIDO'}){
            respond new Error(error: true, message: "Accion no permitida", status: 500)
            return
        }
         def surtidor = User.get(surtidorId)
        if(!surtidor){
            respond new Error(error: true, message: 'Seleccione un surtidor', status: 500)
            return
        }

       def surtido 
       switch(tipo){
            case 'FAC':
                surtido =  surtidoService.iniciarSurtidoVenta(id,tipo,surtidor,user)
            break
            case 'PST':
                surtido = surtidoService.iniciarSurtidoVenta(id,tipo,surtidor,user)          
            break
            case 'SOL':
                surtido = surtidoService.iniciarSurtidoSol(id,tipo,surtidor,user)       
            break
            case 'TRS':
                surtido = surtidoService.iniciarSurtidoTrs(id,tipo,surtidor,user)        
            break
            default:
            break
       }

       respond surtido

   }



   def atenderSurtido(String id, String nip, String accion){

       println params

       def surtido = Surtido.get(id)

       if(!surtido) {
            respond new Error(error: true, message: 'Surtido no localizado', status: 500)
            return
       }

        if(!nip){
            respond new Error(error: true, message: 'Digite su NIP', status: 500)
            return
        }
        def user = User.findByNip(nip)
        if(!user){
           respond new Error(error: true, message: 'Operador no encontrado', status: 500)
           return
        }
        if(accion == 'CERRAR' || accion == 'ENTREGAR'){
            if(!user.getAuthorities().find{it.authority=='ROLE_SURTIDOR'}){
                respond new Error(error: true, message: 'No tiene el ROL de SURTIDOR verifique su NIP', status: 500)
                return
            }
        }
        if(accion == 'REVISAR'  && !user.getAuthorities().find{it.authority=='ROLE_SUPERVISOR_SURTIDO'}){

                    println "Accion no permitida"
                    respond new Error(error: true, message: "Accion no permitida", status: 500)
                return
    }
        switch(accion) {
            case 'CERRAR':
                surtido.cerrado = new Date()
                surtido.cerro = user
                surtido.save failOnError: true, flush:true
            break
            case 'ENTREGAR':
                surtido.entregado = new Date()
                surtido.entrego = user
                surtido.save failOnError: true, flush:true
            break
            case 'REVISAR':
                println "RE¡evisando surtidos" 
                surtido.revisado = new Date()
                surtido.reviso = user
                surtido.save failOnError: true, flush:true
            break
            case 'CANCELAR_ASIGNACION':
                if(!user.getAuthorities().find{it.authority=='ROLE_SUPERVISOR_SURTIDO'}){
                    respond new Error(error: true, message: "No tiene el ROL de SUPERVISOR_ENTREGA verifique su NIP ", status: 500)
                    return
                }
                surtido.inicio = null
                surtido.asignado = null
                surtido.save failOnError: true, flush:true
            break
            default:
            break

        }
        
        respond surtido
   }


   def asignacionManualOld(Surtido surtido,String nip, Long surtidorId){
       if(!nip){
            respond new Error(error: true, message: 'Digite su NIP', status: 500)
            return
        }
        def user = User.findByNip(nip)
        if(!user){
           respond new Error(error: true, message: 'Operador no encontrado', status: 500)
           return
        }
        if(!user.getAuthorities().find{it.authority=='ROLE_SUPERVISOR_SURTIDO'}){
                    respond new Error(error: true, message: "No tiene el ROL de SUPERVISOR_ENTREGA verifique su NIP ", status: 500)
        }

        def surtidor = User.get(surtidorId)
        surtido.asignado = surtidor
        surtido.inicio = new Date()
        surtido.save failOnError: true, flush:true
        respond surtido
   }


    def entregaLocal(){
        def list=Surtido.where{entregaLocal == true && cancelado == null && entregado == null }.list()
        respond list
    }

    def entregaEnvio(){
        def list=Surtido.where{entregaLocal == false && cancelado == null && revisado == null && (tipo == 'FAC' || tipo == 'PST' || tipo == 'SOL')}.list()
        respond list
    }

    def enProceso(){
         def list = Surtido.where{inicio != null && asignado != null && ((entregaLocal == true && entregado == null)||(entregaLocal == false && revisado == null)) }.list()
        respond list
        
    }


     def agregarAuxiliar(Surtido surtido, String nip){
        if(!nip){
            respond new Error(error: true, message: 'Digite su NIP', status: 500)
            return
        }
        def user=User.findByNip(nip)
        if(!user){
            respond new Error(error: true, message: 'Operador no encontrado', status: 500)
            return
        }
        if(!user.getAuthorities().find{it.authority=='ROLE_SURTIDOR'}){
            respond new Error(error: true, message: 'No tiene el ROL de SURTIDOR verifique su NIP ', status: 500)
            return
        }
        if(user==surtido.asignado){
            respond new Error(error: true, message: "El surtido ya esta asignado a: (${user.username}) no puede ser auxiliar", status: 500)   
            return
        }
        if(surtido.auxiliares.find{it.nombre==user.username}){
            respond new Error(error: true, message: "Auxiliar ya asignado al pedido ${surtido.documento} (${user.username})", status: 500)    
            return
        }
        AuxiliarSurtido auxiliar=new AuxiliarSurtido()
        auxiliar.nombre= user.username
        auxiliar.dateCreated= new Date()
        surtido.addToAuxiliares(auxiliar)
        surtido.save flush:true, failOnError:true
        respond surtido
    }

    def buscador(Long folio) { 
        if (folio) {
            def query = Surtido.findAll("from Surtido where folioFac = ? or documento = ?    ",[folio,folio])
            if(query)
                respond query
            else
                return []
        }
        else {
            return []
        }
    }

    def buscarPorDocumento(Long folio, String tipo) {

        println params
        
        def facs= []

         def fac = surtidoService.buscarPorDocumento(folio, tipo)
            
                if(fac){
                    facs.add(fac)
                    respond facs
                }
                else {
                    return[]
                }
                
  
    }

    def getOperacion(String id, String tipo) {
        println params
         def res = surtidoService.getOperacion(id,tipo)
         respond res
        //return []
    }

     def handleException(Exception e) {
        String message = ExceptionUtils.getRootCauseMessage(e)
        log.error(message, ExceptionUtils.getRootCause(e))
        respond([message: message], status: 500)
    }

    

}


public class Error{
    boolean error
    String message
    Integer status
}
