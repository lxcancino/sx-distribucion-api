package sx.distribucionx

import grails.gorm.transactions.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import groovy.sql.Sql


@Transactional
class AsignacionActividadService {

    @Autowired
    @Qualifier('dataSource')
    def dataSource

     def sql  

    def serviceMethod() {

    }

    def  asignarActividad(user, empleado, actividad){

        def instance = new AsignacionActividad()
        instance.asigno = user
        instance.empleado = empleado
        instance.actividad = actividad
        instance.inicio = new Date()
        instance.save failOnError:true, flush:true
        return instance
    }

    def asignacionesPendientes() {

        sql = new Sql(dataSource) 
         def query ="""
                    Select a.*,u.id as userId,u.nombre,u.puesto 
                    from asignacion_actividad a join user u on (a.empleado_id = u.id)
                    where a.fin is null
                    """
        def rows = sql.rows(query)
        return rows
    }

}
