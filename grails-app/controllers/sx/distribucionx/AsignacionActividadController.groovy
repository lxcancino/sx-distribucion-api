package sx.distribucionx


import grails.rest.*
import grails.converters.*
import sx.security.User
import org.apache.commons.lang3.exception.ExceptionUtils
import sx.distribucionx.UserService

class AsignacionActividadController extends RestfulController {

    AsignacionActividadService asignacionActividadService

    static responseFormats = ['json']

    AsignacionActividadController() {
        super(AsignacionActividad)
    }

    def asignarActividad(String nip, String actividad, String empleadoId){

        println params

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
            respond new Error(error: true, message: "No tiene el ROL de SUPERVISOR_SURTIDO verifique su NIP ", status: 500)
            return
        }
        if(!actividad || actividad == 'undefined' ){
            respond new Error(error: true, message: 'Seleccione una actividad', status: 500)
            return
        }
        
        if(!empleadoId || empleadoId == 'undefined' ){
            respond new Error(error: true, message: 'Seleccione un empleado', status: 500)
            return
        }
        def empleado = User.get(empleadoId)
        def asignacion = asignacionActividadService.asignarActividad(user, empleado, actividad) 
        respond asignacion
      
    }

    def terminarActividad(String id,  String nip){

        println params
        def asignacion = AsignacionActividad.get(id)
            
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
            respond new Error(error: true, message: "No tiene el ROL de SUPERVISOR_SURTIDO verifique su NIP ", status: 500)
        }
        asignacion.fin= new Date()
        asignacion.termino = user
        asignacion.save failOnError: true, flush:true
        respond asignacion

    }

    def asignacionesPorFecha(PeriodoCommand command){
        def asignaciones = AsignacionActividad.where{ inicio >= command.fechaIni &&  inicio <= command.fechaFin }.list()
        respond asignaciones
    }

    def handleException(Exception e) {
        String message = ExceptionUtils.getRootCauseMessage(e)
        log.error(message, ExceptionUtils.getRootCause(e))
        respond([message: message], status: 500)
    }

    def empleadosActividad() {
        def asignaciones = asignacionActividadService.asignacionesPendientes()

        respond asignaciones
       
    }

    

}

class PeriodoCommand{

    Date fechaIni
    Date fechaFin
    

}
