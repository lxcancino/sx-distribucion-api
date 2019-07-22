package sx.distribucionx

import grails.gorm.transactions.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import groovy.sql.Sql
import sx.security.User
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import grails.events.annotation.*

@Transactional
class EntregaParcialService {

   @Autowired
    @Qualifier('dataSource')
    def dataSource

    def sql

    def serviceMethod() {}

    def buscarVentaParcial(folio){

        println "Buscando venta parcial"
        sql = new Sql(dataSource)     
        def query ="""
            select 
            v.id,v.documento,v.nombre,v.last_updated, v.tipo as tipo_venta,v.fecha,v.update_user as facturo,
            c.documento as folio_factura,
            (select count(*) from venta_det d join inventario i on (i.id = d.inventario_id) where d.venta_id = v.id ) as partidas,
            (select count(*) from venta_det d join instruccion_corte i on (d.id=i.venta_det_id) where d.venta_id = v.id ) as cortes,
            case when (select count(*) from condicion_de_envio c where c.venta_id = v.id) > 0 then 'ENVIO' else 'LOCAL' end as entrega_local ,
            (select ifnull(sum(d.kilos),0) from venta_det d  where d.venta_id = v.id) as kilos,
            (select ifnull(sum(d.kilos),0) from venta_det d join instruccion_corte n on (n.venta_det_id = d.id) where d.venta_id = v.id) as kilos_corte
            from venta v join cuenta_por_cobrar c on (v.cuenta_por_cobrar_id = c.id) where 
            v.cuenta_por_cobrar_id is not null and puesto is null and surtido is false and parcial is true 
            and (v.documento = ? or  c.documento = ?)
        """
        def parciales = sql.rows(query,[folio,folio])
        println parciales
        return parciales
    }

    def getParciales(){
        sql = new Sql(dataSource)     
        def query ="""
            select 
            id,documento,nombre,last_updated, tipo as tipo_venta,fecha,update_user as facturo,
            (select documento from cuenta_por_cobrar c where c.id = v.cuenta_por_cobrar_id) as folio_factura,
            (select count(*) from venta_det d join inventario i on (i.id = d.inventario_id) where d.venta_id = v.id ) as partidas,
            (select count(*) from venta_det d join instruccion_corte i on (d.id=i.venta_det_id) where d.venta_id = v.id ) as cortes,
            case when (select count(*) from condicion_de_envio c where c.venta_id = v.id) > 0 then 'ENVIO' else 'LOCAL' end as entrega_local ,
            (select ifnull(sum(d.kilos),0) from venta_det d  where d.venta_id = v.id) as kilos,
            (select ifnull(sum(d.kilos),0) from venta_det d join instruccion_corte n on (n.venta_det_id = d.id) where d.venta_id = v.id) as kilos_corte
            from venta v where cuenta_por_cobrar_id is not null and puesto is null and surtido is false and parcial is true
        """
        def parciales = sql.rows(query)
        return parciales
    }

    def iniciar(String ventaId, User user){
        sql = new Sql(dataSource) 
        def query = """
            select 
            id as venta ,documento,nombre,cliente_id as cliente,last_updated,'FAC' as tipo, tipo as tipoDeVenta,fecha,update_user as facturoUser,
            (select documento from cuenta_por_cobrar c where c.id = v.cuenta_por_cobrar_id) as folioFac,'PENDIENTE' as estado,comentario,
            (select count(*) from venta_det d  where d.venta_id = v.id ) as prods,
            (select count(*) from venta_det d join instruccion_corte i on (d.id=i.venta_det_id) where d.venta_id = v.id ) as prodsCorte,
            case when (select count(*) from condicion_de_envio c where c.venta_id = v.id) > 0 then 'ENVIO' else 'LOCAL' end as entregaLocalStr ,
            clasificacion_vale as clasificacionVale
            from venta v  where  parcial is true and id = ?
        """  
        def maestro = sql.firstRow(query,[ventaId]) 

        def facturo = User.findByUsername(maestro.facturoUser)
        def parcial = new EntregaParcial(maestro)
        parcial.facturo = facturo
        parcial.autorizo = user
        parcial.entregaLocal = maestro.entregaLocalStr == 'LOCAL' ? true : false
        parcial.inicio = new Date()

        def queryPartidas = """
            select  
            d.id as ventaDet,producto_id as producto,p.clave,p.descripcion,d.cantidad,cantidad as saldo
            from venta_det d join producto p on (p.id = d.producto_id ) where venta_id = ?
        """
        def partidas = sql.rows(queryPartidas,[ventaId])
        partidas.each{part ->
            def parcialDet = new EntregaParcialDet(part)
            parcial.addToPartidas(parcialDet)
        }


        parcial.save failOnError:true, flush:true  
        sql.execute('update venta set surtido = true where id = ?',[ventaId])  
        return parcial
    }
    
    @Publisher
    Surtido crearSurtido(def  surtidoJson, User user, User  emp){

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Date fecha = dateFormat.parse(surtidoJson.fecha);

        def facturo = User.get(surtidoJson.facturo.id)

        Surtido surtido = new Surtido()

        surtido.parcial = true
        surtido.estado = 'PENDIENTE'
        surtido.clasificacionVale = 'SIN_VALE'
        surtido.tipo = 'FAC'
        surtido.origen = surtidoJson.id
        surtido.documento = surtidoJson.documento
        surtido.folioFac = surtidoJson.folioFac
        surtido.entregaLocal = surtidoJson.entregaLocal
        surtido.autorizo = user
        surtido.nombre = surtidoJson.nombre
        surtido.tipoDeVenta = surtidoJson.tipoDeVenta
        surtido.fecha =  fecha
        surtido.facturo = facturo
        surtido.asignado = emp
        surtido.prods = surtidoJson.partidas.size()
        surtido.inicio = new Date()
        
        surtidoJson.partidas.each{part ->
            if(part.porEntregar ){
                SurtidoDet det  = new SurtidoDet()
                det.cantidad = part.porEntregar
                det.entregaParcialDet = part.id
                det.clave = part.clave
                det.descripcion = part.descripcion
                surtido.addToParciales(det)
            }
            
        }
        if(surtido.parciales.size() == 0){
            return null
        }

        surtido.save failOnError: true,  flush: true
        return surtido
        
    }

}
