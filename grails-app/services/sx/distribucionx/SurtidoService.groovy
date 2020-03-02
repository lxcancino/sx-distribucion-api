package sx.distribucionx

import grails.gorm.transactions.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import groovy.sql.Sql
import sx.security.User



@Transactional
class SurtidoService {

    @Autowired
    @Qualifier('dataSource')
    def dataSource

     def sql  

    def getFacturas(){
        sql = new Sql(dataSource)     
        def query ="""
            select 
            (select documento from cuenta_por_cobrar c where c.id = v.cuenta_por_cobrar_id) as doc_principal,
            id,documento,nombre,last_updated, tipo as tipo_venta,fecha,update_user as facturo,
            (select documento from cuenta_por_cobrar c where c.id = v.cuenta_por_cobrar_id) as folio_factura,
            (select count(*) from venta_det d join inventario i on (i.id = d.inventario_id) where d.venta_id = v.id ) as partidas,
            (select count(*) from venta_det d join instruccion_corte i on (d.id=i.venta_det_id) where d.venta_id = v.id ) as cortes,
            case when (select count(*) from condicion_de_envio c where c.venta_id = v.id) > 0 then 'ENVIO' else 'LOCAL' end as entrega_local ,
            (select ifnull(sum(d.kilos),0) from venta_det d  where d.venta_id = v.id) as kilos,
            (select ifnull(sum(d.kilos),0) from venta_det d join instruccion_corte n on (n.venta_det_id = d.id) where d.venta_id = v.id) as kilos_corte,
            (select count(*) from venta_det d where d.venta_id = v.id and producto_id in ('402880fc5e4ec411015e4ecc6cc60571','402880fc5e4ec411015e4efa766310b6')) as corte
            from venta v where cuenta_por_cobrar_id is not null and puesto is null and surtido is false and parcial is false
            order by 9 desc
        """
        def facturas = sql.rows(query)
        return facturas
    }


    def getPedidos(){
        sql = new Sql(dataSource) 
         def query = """
            select 
            documento as doc_principal,
            id,documento,nombre,last_updated, tipo as tipo_venta,fecha,update_user as facturo,
            (select documento from cuenta_por_cobrar c where c.id = v.cuenta_por_cobrar_id) as folio_factura,
            (select count(*) from venta_det d  where d.venta_id = v.id ) as partidas,
            (select count(*) from venta_det d join instruccion_corte i on (d.id=i.venta_det_id) where d.venta_id = v.id ) as cortes,
            case when (select count(*) from condicion_de_envio c where c.venta_id = v.id) > 0 then 'ENVIO' else 'LOCAL' end as entrega_local ,
            (select ifnull(sum(d.kilos),0) from venta_det d  where d.venta_id = v.id) as kilos,
            (select ifnull(sum(d.kilos),0) from venta_det d  join instruccion_corte n on (n.venta_det_id = d.id) where d.venta_id = v.id) as kilos_corte,
            puesto
            from venta v where  puesto is not null and surtido is false  
            order by 1 desc
            """
        def pedidos = sql.rows(query)
        return  pedidos
    }

    def getTransformaciones(){
        sql = new Sql(dataSource)
         println "Buscando transformaciones"
         def query = """
                select documento as doc_principal, id,tipo,comentario,comentario as nombre,documento,fecha,
                'LOCAL' as entrega_local,
                (select count(*) from transformacion_det d where d.transformacion_id = t.id) as prods
                from transformacion t where surtido is false and tipo ='TRS' and cancelado is null
                order by 1 desc
         """
         def transformaciones = sql.rows(query)
         return transformaciones
    }

    def getVales(){
        sql = new Sql(dataSource)
       
        def query = """
            select t.documento as doc_principal,t.id , tipo, u.nombre,'ENVIO' as entregaLocal,t.documento,tipo as tipo_de_venta,t.fecha,s.documento as folio_sol,
            (select count(*) from traslado_det d where  d.traslado_id = t.id) as prods,
            (select count(*) from traslado_det d where  d.traslado_id = t.id and cortes_instruccion is not null) as prodsCorte,
            (select count(*) from traslado_det d where  d.traslado_id = t.id and cortes_instruccion is not null) as cortes,
            (select sum(kilos) from traslado_det d where d.traslado_id = t.id) as kilos,
            s.clasificacion_vale as clasificacionVale
            from traslado t join solicitud_de_traslado s on (t.solicitud_de_traslado_id = s.id) join sucursal u on (u.id = s.sucursal_solicita_id)
            where surtido is false and tipo ='TPS'
            order by 1 desc
        """
        def vales = sql.rows(query)
        return vales
    }

    def iniciarSurtidoVenta(String id, String tipo,User asignado, User autorizo){
        sql = new Sql(dataSource) 
        def query = """
            select 
            (select documento from cuenta_por_cobrar c where c.id = v.cuenta_por_cobrar_id) as doc_principal,
            id as origen ,documento,nombre,last_updated, tipo as tipoDeVenta,fecha,update_user as facturoUser,
            (select documento from cuenta_por_cobrar c where c.id = v.cuenta_por_cobrar_id) as folioFac,'EN_SURTIDO' as estado,
            (select count(*) from venta_det d  where d.venta_id = v.id ) as prods,
            (select count(*) from venta_det d join instruccion_corte i on (d.id=i.venta_det_id) where d.venta_id = v.id ) as prodsCorte,
            case when (select count(*) from condicion_de_envio c where c.venta_id = v.id) > 0 then 'ENVIO' else 'LOCAL' end as entregaLocalStr ,
            - (select ifnull(sum(d.kilos),0) from venta_det d  where d.venta_id = v.id) as kilos,
             - (select ifnull(sum(d.kilos),0) from venta_det d join instruccion_corte n on (n.venta_det_id = d.id) where d.venta_id = v.id) as kilosCorte,
            clasificacion_vale as clasificacionVale
            from venta v  where id = ?
        """  
        def maestro = sql.firstRow(query,[id])     
        def facturo = User.findByUsername(maestro.facturoUser)
        def surtido = new Surtido(maestro)
        surtido.facturo = facturo
        surtido.tipo = tipo
        surtido.entregaLocal = maestro.entregaLocalStr == 'LOCAL' ? true : false
        surtido.asignado = asignado
        if(autorizo){
            surtido.autorizo=autorizo
        }
        surtido.inicio = new Date()
        
        def queryCortes = """
            select  
            d.cantidad,
            p.clave,p.descripcion,i.instruccion,d.id as origen,
            (select documento from venta v where v.id = d.venta_id) as venta,
            (select c.documento from venta v join cuenta_por_cobrar c on (c.id = v.cuenta_por_cobrar_id) where v.id = d.venta_id) as factura
            from venta_det d join producto p on (p.id = d.producto_id ) join instruccion_corte i on (i.venta_det_id =d.id) where venta_id = ?
        """
        def partidasCorte = sql.rows(queryCortes,[id])
        partidasCorte.each{part ->
            def corte = new Corte (part)
            corte.estado = 'PENDIENTE'
            surtido.addToCortes(corte)
        }
        surtido.save failOnError:true, flush:true   
        sql.execute('update venta set surtido = true where id = ?',[id])
        return surtido
    }

    def iniciarSurtidoSol(String id, String tipo,User asignado, User autorizo){
        println 'Inciando surtido vale'
        sql = new Sql(dataSource)
        def query ="""
            select s.documento as doc_principal, t.id as origen, u.nombre,'ENVIO' as entregaLocalStr,t.documento,tipo as tipoDeVenta,t.fecha,s.documento as folioFac,
            (select count(*) from traslado_det d where  d.traslado_id = t.id) as prods,
            (select sum(kilos) from traslado_det d where d.traslado_id = t.id) as kilos,
            s.clasificacion_vale as clasificacionVale, 0 as kilosCorte, 0 as prodsCorte,'PENDIENTE' as estado
            from traslado t join solicitud_de_traslado s on (t.solicitud_de_traslado_id = s.id) join sucursal u on (u.id = s.sucursal_solicita_id)
            where t.id = ?
        """
        def vale = sql.firstRow(query,[id])

        def surtido = new Surtido(vale)
        surtido.entregaLocal = vale.entregaLocalStr == 'LOCAL' ? true : false
        surtido.asignado = asignado
         if(autorizo){
            surtido.autorizo=autorizo
        }
        surtido.inicio = new Date()
        surtido.tipo  = tipo

        def queryCortes = """
            select 
            p.clave,p.descripcion,d.cortes_instruccion as instruccion,d.id as origen,
            (select documento from traslado t where t.id =d.traslado_id ) as venta,
            (select documento from traslado t where t.id =d.traslado_id ) as factura
            from traslado_det d join producto p on (p.id = d.producto_id) where d.traslado_id = ?
        """
        def partidasCorte = sql.rows(queryCortes,[id])
        partidasCorte.each{part ->
            def corte = new Corte (part)
            corte.estado = 'PENDIENTE'
            surtido.addToCortes(corte)
        }

        surtido.save failOnError:true, flush:true 
        sql.execute('update traslado set surtido = true where id = ?',[id])  

        return surtido   
    }

    def iniciarSurtidoTrs(String id, String tipo,User asignado,User autorizo ){
        println 'Inciando surtido Transformacion'
        sql = new Sql(dataSource)
        def query = """ 
            select
            documento as doc_principal,
              id as origen,tipo,comentario as nombre,'LOCAL' as entregaLocalStr,documento,tipo as tipoDeVenta,fecha,documento as folioFac,
             0 as kilos, 0 as prods, 0 as kilosCorte, 0 as prodsCorte,'PENDIENTE' as estado,'SIN_VALE' as clasificacionVale
            from transformacion t 
            where t.id = ?
        """
        def trs = sql.firstRow(query,[id])
        println trs
        def surtido = new Surtido(trs)
        surtido.entregaLocal = trs.entregaLocalStr == 'LOCAL' ? true : false
        surtido.asignado = asignado
         if(autorizo){
            surtido.autorizo=autorizo
        }
        surtido.inicio = new Date()
        surtido.save failOnError:true, flush:true 
        sql.execute('update transformacion set surtido = true where id = ?',[id]) 
        
        return surtido 
    }
    
    def buscarPorDocumento(Long folio,String tipo ){
        sql = new Sql(dataSource) 


         switch(tipo) {
           case 'Facturas':
           def query =
            """
                select * from(
                select 
                (select documento from cuenta_por_cobrar c where c.id = v.cuenta_por_cobrar_id) as doc_principal,
                id,documento,nombre,last_updated, tipo as tipo_venta,fecha,update_user as facturo,
                (select documento from cuenta_por_cobrar c where c.id = v.cuenta_por_cobrar_id) as folio_factura,
                (select count(*) from venta_det d join inventario i on (i.id = d.inventario_id) where d.venta_id = v.id ) as partidas,
                (select count(*) from venta_det d join instruccion_corte i on (d.id=i.venta_det_id) where d.venta_id = v.id ) as cortes,
                case when (select count(*) from condicion_de_envio c where c.venta_id = v.id) > 0 then 'ENVIO' else 'LOCAL' end as entrega_local ,
                (select ifnull(sum(d.kilos),0) from venta_det d  where d.venta_id = v.id) as kilos,
                (select ifnull(sum(d.kilos),0) from venta_det d join instruccion_corte n on (n.venta_det_id = d.id) where d.venta_id = v.id) as kilos_corte
                from venta v where cuenta_por_cobrar_id is not null and puesto is null and surtido is false and parcial is false
                ) as x where documento= ? or folio_factura = ?
            """
                def factura = sql.firstRow(query,[folio,folio])
                return factura
           break
           case 'Pedidos':
           def query =
            """
                select 
                documento as doc_principal,
                id,documento,nombre,last_updated, tipo as tipo_venta,fecha,update_user as facturo,
                (select documento from cuenta_por_cobrar c where c.id = v.cuenta_por_cobrar_id) as folio_factura,
                (select count(*) from venta_det d  where d.venta_id = v.id ) as partidas,
                (select count(*) from venta_det d join instruccion_corte i on (d.id=i.venta_det_id) where d.venta_id = v.id ) as cortes,
                case when (select count(*) from condicion_de_envio c where c.venta_id = v.id) > 0 then 'ENVIO' else 'LOCAL' end as entrega_local ,
                (select ifnull(sum(d.kilos),0) from venta_det d  where d.venta_id = v.id) as kilos,
                (select ifnull(sum(d.kilos),0) from venta_det d  join instruccion_corte n on (n.venta_det_id = d.id) where d.venta_id = v.id) as kilos_corte,
                puesto
                from venta v where cuenta_por_cobrar_id is  null and puesto is not null and surtido is false 
                and documento = ?
            """
                def pedido = sql.firstRow(query,[folio])
                return pedido
               
           break
           case 'Vales':
            
             def query =
            """
                select documento as doc_principal,t.id as origen, tipo, u.nombre,'ENVIO' as entregaLocal,t.documento,tipo as tipo_de_venta,t.fecha,s.documento as folio_sol,
                (select count(*) from traslado_det d where  d.traslado_id = t.id) as prods,
                (select count(*) from traslado_det d where  d.traslado_id = t.id and cortes_instruccion is not null) as prodsCorte,
                (select sum(kilos) from traslado_det d where d.traslado_id = t.id) as kilos,
                s.clasificacion_vale as clasificacionVale
                from traslado t join solicitud_de_traslado s on (t.solicitud_de_traslado_id = s.id) join sucursal u on (u.id = s.sucursal_solicita_id)
                where surtido is false and tipo ='TPS'
                and documento = ?
            """
                def  vale = sql.firstRow(query,[folio])
                return vale
               
           break
           case 'Trs':
            def query =
            """
               select documento as doc_principal,id as origen,tipo,comentario,documento,fecha,
                (select count(*) from transformacion_det d where d.transformacion_id = t.id) as prods
                from transformacion t where surtido is false and tipo ='TRS' and cancelado is null
                and documento = ?
            """
                def trs = sql.firstRow(query,[folio])
                return trs
               
           break
           default:
                return null
            break
       }
        
    }

    def getOperacion (String id,tipo) {

         sql = new Sql(dataSource) 

        switch(tipo) {
            case 'FACS':
            println "Buscando Factura"
            def queryMaestro = """
                select 
                (select documento from cuenta_por_cobrar c where c.id = v.cuenta_por_cobrar_id) as doc_principal,
                id,documento,nombre,last_updated, tipo as tipo_venta,fecha,update_user as facturo,
                (select documento from cuenta_por_cobrar c where c.id = v.cuenta_por_cobrar_id) as folio_factura,
                (select count(*) from venta_det d join inventario i on (i.id = d.inventario_id) where d.venta_id = v.id ) as partidas,
                (select count(*) from venta_det d join instruccion_corte i on (d.id=i.venta_det_id) where d.venta_id = v.id ) as cortes,
                case when (select count(*) from condicion_de_envio c where c.venta_id = v.id) > 0 then 'ENVIO' else 'LOCAL' end as entrega_local ,
                (select ifnull(sum(d.kilos),0) from venta_det d  where d.venta_id = v.id) as kilos,
                (select ifnull(sum(d.kilos),0) from venta_det d join instruccion_corte n on (n.venta_det_id = d.id) where d.venta_id = v.id) as kilos_corte
                from venta v 
                where id=?
            """
            def queryPartidas = """
                select clave,descripcion,cantidad,d.kilos 
                from venta_det d join producto p on (d.producto_id = p.id) 
                where venta_id = ? and p.clave not in ('CORTE','MANIOBRA','MANIOBRAF')
            """

            def maestro = sql.firstRow(queryMaestro,[id])

            def partidas = sql.rows(queryPartidas,[id])

            return [maestro: maestro,partidas: partidas]

            break
            
        }
    }

    def getBusqueda(String id, tipo){
         sql = new Sql(dataSource) 
         def queryPartidas = """
                select p.clave, p.descripcion, d.cantidad, d.kilos
                from venta_det d join producto p on (d.producto_id = p.id)  
                where venta_id = ? and p.clave not in ('CORTE','MANIOBRA','MANIOBRAF')
            """
         def partidas = sql.rows(queryPartidas,[id])
         return partidas
    }

   
}
