package sx.distribucionx

import grails.gorm.transactions.Transactional
import grails.events.annotation.*

@Transactional
class SurtidoInterceptorService {

    @Subscriber
    void onCrearSurtido(Surtido surtido){

            def entrega = EntregaParcial.get(surtido.origen)
             def entregado = entrega.partidas.sum{it.entregado}
             def cantidad = entrega.partidas.sum{it.cantidad}

             if(entregado >= cantidad){
                 println "Terminand entrega"
                entrega.estado = 'TERMINADA'
             }

    }
}
