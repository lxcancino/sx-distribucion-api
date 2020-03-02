package sx.distribucionx


import grails.rest.*
import grails.converters.*
import sx.security.User
import org.apache.commons.lang3.exception.ExceptionUtils

class CorteController extends RestfulController {

    CorteService corteService

    static responseFormats = ['json']
    CorteController() {
        super(Corte)
    }

     def cortesAll() {
        def query = Corte.where{  empacadoFin == null && fin == null && asignado != null && parcial == false }
        def cortes = query.list().findAll{!it.surtido.cancelado && !it.surtido.depurado && it.surtido.entregado==null }
        respond cortes
    }

    def parcializarCorte(String id) {
        def corte = Corte.get(id)
        def surtido = corte.surtido

        println params

        def cortJSON = request.JSON
        def parcial = cortJSON.sum{new BigDecimal(it.cantidad)}
       
        if (!cortJSON) {
            respond new Error(error: true, message: 'Seleccion un cortador', status: 500)
            return
        }

            cortJSON.each{ cor ->
                println cor
                def cortador = User.get(cor.id)
                println cortador.nombre
                def corteParcial = new Corte(corte.properties)
                corteParcial.parcial = true
                corteParcial.asignado = new Date()
                corteParcial.cortador = cortador
                corteParcial.cantidadParcial = new BigDecimal(cor.cantidad)
                corteParcial.inicio = null
                corteParcial.fin = null
                corteParcial.empacadoInicio = null
                corteParcial.empacadoFin = null
                println corteParcial.clave
                surtido.addToCortes(corteParcial)
            }
            corte.parcializado = true
            corte.cantidadParcial = corte.cantidad - parcial

            surtido.save failOnError:true, flush:true
        

        respond corte
    }

    def corte(Long cortadorId){
        println "Cortador ID: "+cortadorId
        def cortador= User.get(cortadorId)
        println "Cortador: "+ cortador
        def query = Corte.where{ cortador == cortador && empacadoFin == null && fin == null }
        def cortes = query.list().findAll{!it.surtido.cancelado && !it.surtido.depurado && it.surtido.entregado==null }
        respond cortes
    }

    def empaque(Long cortadorId){
        println "Cortador ID: "+cortadorId
        def cortador= User.get(cortadorId)
        println "Cortador: "+ cortador
        def query = Corte.where{ cortador == cortador && empacadoFin == null }
        def cortes = query.list().findAll{!it.surtido.cancelado && !it.surtido.depurado && it.surtido.entregado==null }
        respond cortes
    }

    def entregaCorte(){
        def cortes=Corte.findAll("from Corte c where c.inicio is null and c.cortador is null and c.surtido.cancelado=null and fin is null order by c.surtido.documento")
        respond cortes
    }

    def entregarCorte(String corteId, String nip, String cortadorId){
        def adicionales = params.adicionales
        def corte = Corte.get(corteId)
        if(!nip){
            respond new Error(error: true, message: 'Digite su NIP', status: 500)
            return
        }
        def user=User.findByNip(nip)
        if(!user){
            respond new Error(error: true, message: 'Operador no encontrado', status: 500)
            return
        }
          def cortador = User.get(cortadorId)
        if(!cortador){
            respond new Error(error: true, message: 'Seleccione un cortador', status: 500)
            return
        }
         def cte = corteService.entregarCorte(corte, cortadorId)
        if (adicionales) {
            def adicionalesList = adicionales.tokenize(',')   
            adicionalesList.each{
                def adicional = Corte.get(it)
                if (adicional.surtido == corte.surtido ) {
                    corteService.entregarCorte(adicional, cortadorId)
                }
            }
        } 
        respond cte
    }

  

    def atenderCorte(String corteId, String accion, String cortadorId){
        def corte = Corte.get(corteId)
        def cortador = User.get(cortadorId)
        def surtido = corte.surtido
        switch(accion) {
            case 'INICIAR':
                corte.inicio = new Date()
                corte.save failOnError: true, flush:true
                surtido.corteInicio = corte.inicio
                surtido.save failOnError: true, flush:true
            break
            case 'TERMINAR':
                corte.fin = new Date()
                corte.empacadoInicio = new Date()
                corte.save failOnError: true, flush:true
                def cortesPendientes = surtido.cortes.findAll{it.fin == null}
                if(!cortesPendientes){
                    surtido.corteFin = corte.fin
                    surtido.save failOnError: true, flush:true   
                }
            break
            default:
            break
        }       
        respond corte
    }

    def agregarAuxiliar(Corte corte, String nip){
        //Corte corte=Corte.get(corteId)
        def cortador=corte.cortador
        if(!cortador){
            respond new Error(error: true, message: 'No se puede agregar auxiliar, corte no asignado', status: 500)
            return
        }
        if(!nip){
            respond new Error(error: true, message: 'Digite su NIP', status: 500)
            return
        }
        def auxiliar=User.findByNip(nip)
        if(!auxiliar){
            respond new Error(error: true, message: 'Operador no encontrado', status: 500)
            return
        }
        if(!auxiliar.getAuthorities().find{it.authority=='ROLE_CORTADOR'}){
           respond new Error(error: true, message: 'El operador no es CORTADOR', status: 500)
            return
        }
        if(auxiliar.username==cortador.username){
            respond new Error(error: true, message: "El corte ya esta asignado a ${auxiliar.username}", status: 500)
            return
        }
        if(corte.auxiliares.find{it.auxiliarCorte.username==auxiliar.username && it.tipo=='CORTADOR'}){
          respond new Error(error: true, message: "El operador ${auxiliar.username} ya esta registrado como auxiliar de corte ", status: 500)
            return
        }
        AuxiliarCorte auxiliarCorte=new AuxiliarCorte()
        auxiliarCorte.auxiliarCorte= auxiliar
        auxiliarCorte.tipo= 'CORTADOR'
        corte.addToAuxiliares(auxiliarCorte)
        corte.save flush:true,failOnError:true 
        
        respond corte

    }

    def registrarMesaDeEmpaque(String cortadorId){
        def cortador=User.get(cortadorId)
        def fe=new Date() .format('MM/dd/yyyy')
        def fecha= new Date(fe)
        println  fe
        println  fecha
       
        def mesa=MesaEmpaque.findByCortadorAndFecha(cortador,fecha)
        if(mesa){       
             respond new Error(error: true, message: "Ya esta la mesa de empaque registrada para: ${cortador.username} (${fecha.format("dd/MM/yyyy")})", status: 500)
            return []
        }else{
            mesa= new MesaEmpaque()
            mesa.cortador=cortador
            mesa.fecha=fecha
            mesa.save failOnError: true, flush: true 
            respond mesa
        }
    }

    def asignarseAMesa(String mesaId, String nip){
       
        def mesa = MesaEmpaque.get(mesaId)
        if(!mesa){
            respond new Error(error: true, message: "No existe mesa registrada", status: 500)
            return
        }else{
            if(!nip){
                respond new Error(error: true, message: "Digite su NIP", status: 500)
                return
            }
            def surtidor=User.findByNip(nip)
            if(!surtidor){
                respond new Error(error: true, message: "Operador no encontrado", status: 500)
                return
            }
            if(!surtidor.getAuthorities().find{it.authority=='ROLE_EMPACADOR'}){
                respond new Error(error: true, message: "No tiene el ROL de Empacador verifique su NIP ", status: 500)
                return
            }
            for(int i=1 ; i<=8 ; i++) {

                def column = "empacador" + i

                if(mesa[column] == null){

                    switch (i){
                        case 1:
                            mesa.empacador1 = surtidor
                            break
                        case 2:
                            mesa.empacador2 = surtidor
                            break
                        case 3:
                            mesa.empacador3 = surtidor
                            break
                        case 4:
                            mesa.empacador4 = surtidor
                            break
                        case 5:
                            mesa.empacador5 = surtidor
                            break
                        case 6:
                            mesa.empacador6 = surtidor
                            break
                        case 7:
                            mesa.empacador7 = surtidor
                            break
                        case 8:
                            mesa.empacador8 = surtidor
                            break

                        default:
                            break
                    }
                    break
                }else{
                    if(mesa[column]==surtidor){
                        respond new Error(error: true, message: "Operador ya asignado a esta mesa ", status: 500)
                        return
                    }
                }
            }
            mesa.save failOnError: true, flush: true
            respond mesa

        }
        return []
    }

        def salirDeMesaDeEmpaque(String mesaId, String nip){

        def mesa = MesaEmpaque.get(mesaId)

        if(!mesa){
            respond new Error(error: true, message: "No existe mesa registrada para ${cortador.username} del día ${fecha}", status: 500)
            return
        }else{
            
            if(!nip){
                respond new Error(error: true, message: "Digite su NIP", status: 500)
                return
            }
            def surtidor=User.findByNip(nip)

            if(!surtidor){
                respond new Error(error: true, message: "Operador no encontrado", status: 500)
                return
            }
            if(!surtidor.getAuthorities().find{it.authority=='ROLE_EMPACADOR'}){
                respond new Error(error: true, message: "No tiene el ROL de Empacador verifique su NIP ", status: 500)
                return
            }
            for(int i=1 ; i<=9 ; i++) {

                def column = "empacador" + i

                if (i == 9){
                    respond new Error(error: true, message: "Usuario no asignado a esta mesa", status: 500)
                    return
                }

                if (i <= 8 && mesa[column] == surtidor  ) {

                    switch (i) {
                        case 1:
                            mesa.empacador1 = null
                            break
                        case 2:
                            mesa.empacador2 = null
                            break
                        case 3:
                            mesa.empacador3 = null
                            break
                        case 4:
                            mesa.empacador4 = null
                            break
                        case 5:
                            mesa.empacador5 = null
                            break
                        case 6:
                            mesa.empacador6 = null
                            break
                        case 7:
                            mesa.empacador7 = null
                            break
                        case 8:
                            mesa.empacador8 = null
                            break
                        default:
                            break
                    }
                    break
                }                
            }
            mesa.save failOnError: true, flush: true
            respond mesa
        }
    }

    def terminarEmpacadoMesa(String corteId, String nip, String mesaId){

        println params
        def adicionales = params.adicionales
        def corte = Corte.get(corteId)

        if(!nip){
            respond new Error(error: true, message: "Digite su NIP", status: 500)
            return
        }
        def empacador=User.findByNip(nip)
        
        if(!empacador){
            respond new Error(error: true, message: "Operador no encontrado", status: 500)
            return
        }
        if(!empacador.getAuthorities().find{it.authority=='ROLE_EMPACADOR'}){
            respond new Error(error: true, message: "No tiene el ROL de Empacador verifique su NIP ", status: 500)
            return
        }
        def mesa = MesaEmpaque.get(mesaId)
        if(!mesa){
            respond new Error(error: true, message: "No existe mesa registrada", status: 500)
            return
        }
        
        corte = corteService.agregarEmpacadorMesa(corte, empacador, mesa)

          if (adicionales) {
              println 'Si hay adicionales'
            def adicionalesList = adicionales.tokenize(',')   
            adicionalesList.each{
                def adicional = Corte.get(it)   
               if (adicional.surtido == corte.surtido && adicional != corte ) {
                    corteService.agregarEmpacadorMesa(adicional, empacador, mesa)
                }
                
            }
        } else {
            println "No hay adicionales"
        }

        respond corte

        //return []

    }

    def terminarEmpacado(Corte corte, String nip){
        //def corte = Corte.get(corteId)
        def cortador=corte.cortador
        if(!nip){
            respond new Error(error: true, message: "Digita su NIP", status: 500)
            return
        }
        def empacador=User.findByNip(nip)
        if(!empacador){
            respond new Error(error: true, message: "Operador no encontrado", status: 500)
            return
        }
        if(!empacador.getAuthorities().find{it.authority=='ROLE_EMPACADOR'}){
            respond new Error(error: true, message: "No tiene el ROL de Empacador verifique su NIP ", status: 500)
            return
        }
        corte.empacador=empacador
        corte.empacadoFin=new Date()
        corte.save(flush:true)
        respond corte
    }

    def mesasDisponibles() {
        def mesas = MesaEmpaque.findAll("from MesaEmpaque where date(fecha) = date(?)",[new Date()])
        if (mesas) {
            respond mesas
        }
        return []
    }

    def mesaEmpaque(String mesaId) {
        def mesa = MesaEmpaque.get(mesaId)

        def empacadores = []

        for(int i=1 ; i<=8 ; i++) {
            def column = "empacador" + i
             if (mesa[column] != null){
                // println mesa[column]
                 empacadores.add(mesa[column].nombre)
             }
        }

        if (empacadores) {
            respond empacadores
        }

        return []
        /*
        def mesa = MesaEmpaque.get(mesaId)
        if (!mesa) {
            return []
        }
        respond mesa
            */
    }

    def cancelarAsignacionCorte(nip,corteId){
         if(!nip){
            respond new Error(error: true, message: 'Digite su NIP', status: 500)
            return
        }
        def user=User.findByNip(nip)
        if(!user){
            respond new Error(error: true, message: 'Operador no encontrado', status: 500)
            return
        }
        if(!user.getAuthorities().find{it.authority=='ROLE_SUPERVISOR'}){
            respond new Error(error: true, message: "No tiene el ROL de Empacador verifique su NIP ", status: 500)
            return
        }
        def corte = corteService.cancelarAsignacionCorte(corteId)

        respond corte
    }

    
     def handleException(Exception e) {
        String message = ExceptionUtils.getRootCauseMessage(e)
        log.error(message, ExceptionUtils.getRootCause(e))
        respond([message: message], status: 500)
    }

}
