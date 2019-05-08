package sx.reports

import grails.plugins.jasper.JasperExportFormat
import grails.plugins.jasper.JasperReportDef
import grails.plugins.jasper.JasperService
import groovy.util.logging.Slf4j

@Slf4j
// @GrailsCompileStatic
class ReportService {

    JasperService jasperService

    ByteArrayOutputStream run(String reportName, Map params ) {
        log.debug("Ejecutando reporte ${reportName} con parametros: ${params}")
        def reportDef= new JasperReportDef(
                name: reportName,
                fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: params
        )
        ByteArrayOutputStream pdfStream = jasperService.generateReport(reportDef)
        return pdfStream
    }

    ByteArrayOutputStream run(String reportName, Map params, Collection data){
        log.debug("Ejecutando reporte ${reportName} con parametros: ${params} y data: ${data}")
        def reportDef=new JasperReportDef(
                name:reportName,
                fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters:params,
                reportData:data
        )
        ByteArrayOutputStream  stream=jasperService.generateReport(reportDef)
        return stream
    }
}
