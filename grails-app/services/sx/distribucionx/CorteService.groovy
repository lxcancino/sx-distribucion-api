package sx.distribucionx

import grails.gorm.transactions.Transactional
import sx.security.User

@Transactional
class CorteService {

    def serviceMethod() {

    }

    def entregarCorte(Corte corte, String cortadorId){
       // def corte = Corte.get(corteId)
        def cortador = User.get(cortadorId)
        corte.cortador = cortador
        corte.asignado = new Date()
        corte.save failOnError: true, flush:true
        def surtido = corte.surtido
        surtido.asignadoCorte = corte.asignado
        surtido.cortador = corte.cortador
        surtido.save failOnError: true, flush:true

        return corte
    }

    def cancelarAsignacionCorte() {
        
    }

    def agregarEmpacadorMesa(Corte corte, User empacador, MesaEmpaque mesa){
        
        println 'Agregando empacador messa'

            corte.empacador=empacador
            corte.empacadoFin=new Date()
            for(int i=1 ; i<=8 ; i++) {
                def column = "empacador" + i
                if (mesa[column] != null){            
                    def auxiliar= new AuxiliarCorte()
                    auxiliar.corte=corte
                    auxiliar.auxiliarCorte=mesa[column]
                    auxiliar.tipo='EMPACADOR'
                    corte.addToAuxiliares(auxiliar)
                }
            }
            corte.save(flush:true)
            return corte
        
    }

    def cancelarAsignacionCorte(String corteId) {
        
        def corte = Corte.get(corteId)

        corte.cortador = null
        corte.asignado = null
        corte.save failOnError: true, flush:true
        def surtido = corte.surtido
        surtido.asignadoCorte = null
        surtido.cortador = null
        surtido.save failOnError: true, flush:true

    }

}
