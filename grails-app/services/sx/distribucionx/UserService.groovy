package sx.distribucionx

import grails.gorm.transactions.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import groovy.sql.Sql
import sx.security.User
import sx.security.UserRole

@Transactional
class UserService {

    @Autowired
    @Qualifier('dataSource')
    dataSource

    def serviceMethod() {}

    def getEmpleadosPuesto(String tipo){
         def sucursal = getSucursalNombre()
         def surtidores = User.where{puesto == tipo && sucursal == sucursal }.list()
        return surtidores
    }



    def getSucursalNombre(){
        def sql = new Sql(dataSource)
        def query = "select s.nombre from app_config a join sucursal s on (s.id=a.sucursal_id)"
        def sucursal = sql.firstRow(query)
        return sucursal.nombre
    }

    def getSucursal(){
        def sql = new Sql(dataSource)
        def query = "select s.nombre from app_config a join sucursal s on (s.id=a.sucursal_id)"
        def sucursal = sql.firstRow(query)
        return sucursal
    }

    def getPlantillaAlmacen(){
        def sucursal = getSucursalNombre()
       def plantilla = User.executeQuery("from User where puesto not in ('CAJERA','CAJERA SUPLENTE','AUXILIAR DE MOSTRADOR') and  puesto not like 'ENCARG%' and sucursal = ? order by puesto",[sucursal])
        return plantilla
    }

    def buscarPorNip(String nip){
            def user = User.findByNip(nip)
            return user
    }

    def getRoles(userId){
        def user = User.get(userId)
        def roles = UserRole.findAllByUser(user)
        return roles
    }


}
