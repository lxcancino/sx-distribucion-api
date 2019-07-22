package sx.distribucionx


import grails.rest.*
import grails.converters.*
import org.apache.commons.lang3.exception.ExceptionUtils
import sx.security.User
import sx.security.UserRole



class UserController  {

    UserService userService
    static responseFormats = ['json']
  
    def surtidores(){
        def surtidores = userService.getEmpleadosPuesto('SURTIDOR')
        respond surtidores
    }

    def cortadores(){
        def cortadores = userService.getEmpleadosPuesto('CORTADOR')
        respond cortadores
    }

    def empacadores(){
        def empacadores = userService.getEmpleadosPuesto('EMPACADOR')
        respond empacadores
    }

    def plantilla(){
        def plantilla = userService.getPlantillaAlmacen()
        respond plantilla
    }

    def login(String nip){
        def user = userService.buscarPorNip(nip)
        if (user){
            respond user
        } else {
            return []
        }

        
    }

    def roles(String userId){
        def roles = userService.getRoles(userId)
        respond roles
    }

     def handleException(Exception e) {
        String message = ExceptionUtils.getRootCauseMessage(e)
        log.error(message, ExceptionUtils.getRootCause(e))
        respond([message: message], status: 500)
    }

}
