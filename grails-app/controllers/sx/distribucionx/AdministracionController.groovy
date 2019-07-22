package sx.distribucionx


import grails.rest.*
import grails.converters.*
import sx.reports.ReportService
import org.apache.commons.lang3.exception.ExceptionUtils
import sx.security.User

class AdministracionController {
	static responseFormats = ['json']

     ReportService reportService
	
    def index() { }

    def surtidosPeriodo(PeriodoCommand command){
        println "Cargando los surtidos por periodo"
        println command.fechaIni
        println command.fechaFin
        def surtidos = Surtido.where{fecha >= command.fechaIni && fecha <= command.fechaFin}
        respond surtidos
    }
    
    def surtidosPorEmpleado(PeriodoCommand command, User empleado){

    }

    def reporte(){
        println 'Ejecutando Reporte'
        params.FECHA_ENT = ''
        params.SUCURSAL = ''
        def pdf = this.reportService.run('SurtidoPeriodo', params)
        def fileName = "SurtidoPeriodo.pdf"
        respond (file: pdf.toByteArray(), contentType: 'application/pdf', filename: fileName)
        //return []
    }

    /** Metodo para ejecutar reportes **/
    /*
    def recepcionesPorDia(RecepcionesPorFecha command) {
        params.FECHA_ENT = command.fecha
        params.SUCURSAL = command.sucursal ?: '%'
        def pdf = this.reportService.run('RecepDeMercancia', params)
        def fileName = "RecepDeMercancia.pdf"
        render (file: pdf.toByteArray(), contentType: 'application/pdf', filename: fileName)
    }
*/
     def handleException(Exception e) {
        String message = ExceptionUtils.getRootCauseMessage(e)
        log.error(message, ExceptionUtils.getRootCause(e))
        respond([message: message], status: 500)
    }


}
