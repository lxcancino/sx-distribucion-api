package server

class UrlMappings {

    static mappings = {
        /*
        "/"(controller: 'application', action:'index')
        "/api/session"(controller: 'application', action: 'session')
        */
        
        "500"(view: '/error')
        "404"(view: '/notFound')

        // User
        "/surtidores"(controller: "user", action: 'surtidores', method: 'GET')
        "/cortadores"(controller: "user", action: 'cortadores', method: 'GET')
        "/empacadores"(controller: "user", action: 'empacadores', method: 'GET')
        "/plantilla"(controller: "user", action: 'plantilla', method: 'GET')
        "/login"(controller: "user", action: 'login', method: 'GET')
     



        // Surtido
        "/facturas"(controller: "surtido", action: 'facturas', method: 'GET')
        "/pedidos"(controller: "surtido", action: 'pedidos', method: 'GET')
        "/vales"(controller: "surtido", action: 'vales', method: 'GET')
        "/transformaciones"(controller: "surtido", action: 'transformaciones', method: 'GET')
        "/entregaLocal"(controller: "surtido", action: 'entregaLocal', method: 'GET')
        "/entregaEnvio"(controller: "surtido", action: 'entregaEnvio', method: 'GET')
        "/proceso"(controller: "surtido", action: 'enProceso', method: 'GET')
        "/buscador"(controller: "surtido", action: 'buscador', method: 'GET')
        "/buscadorAdmin"(controller: "surtido", action: 'buscadorAdmin', method: 'GET')
         "/buscar"(controller: "surtido", action: 'buscarPorDocumento', method: 'GET')

       
        "/iniciar"(controller: "surtido", action: 'iniciar', method: 'POST')
        "/atender"(controller: "surtido", action: 'atenderSurtido', method: 'POST')
        "/asignacion"(controller: "surtido", action: 'asignacionManual', method: 'POST')
        "/agregarAuxiliar"(controller: "surtido", action: 'agregarAuxiliar', method: 'POST')
        "/detalle"(controller: "surtido", action: 'getOperacion', method: 'GET')



        // Corte
        "/corte"(controller: "corte", action: 'corte', method: 'GET')
        "/empaque"(controller: "corte", action: 'empaque', method: 'GET')
        "/entregaCorte"(controller: "corte", action: 'entregaCorte', method: 'GET')
        "/atenderCorte"(controller: "corte", action: 'atenderCorte', method: 'POST')
        "/entregarCorte"(controller: "corte", action: 'entregarCorte', method: 'POST')
        "/agregarAuxiliarCorte"(controller: "corte", action: 'agregarAuxiliar', method: 'POST')
        "/registrarMesa"(controller: "corte", action: 'registrarMesaDeEmpaque', method: 'POST')
        "/asignarse"(controller: "corte", action: 'asignarseAMesa', method: 'POST')
        "/salir"(controller: "corte", action: 'salirDeMesaDeEmpaque', method: 'POST')
        "/terminarEmpacadoMesa"(controller: "corte", action: 'terminarEmpacadoMesa', method: 'POST')
         "/terminarEmpacado"(controller: "corte", action: 'terminarEmpacado', method: 'POST')
         "/mesas"(controller: "corte", action: 'mesasDisponibles', method: 'GET')
         "/mesa"(controller: "corte", action: 'mesaEmpaque', method: 'GET')
         "/cortesParciales"(controller:"corte",action:"cortesAll", method: 'GET')
         "/parcializarCorte"(controller:"corte", action:"parcializarCorte",method:"POST")

         // Entrega parcial
          "/entregaParcial"(resources: 'entregaParcial',  excludes:['create', 'edit','patch'])
          "/parciales"(controller: "entregaParcial", action: 'parciales', method: 'GET')
          "/entregas"(controller: "entregaParcial", action: 'entregasParciales', method: 'GET')
          "/buscarPendientes"(controller: "entregaParcial", action: 'buscarPendientes', method: 'GET')
          "/buscarParcial"(controller: "entregaParcial", action: 'buscarParcial', method: 'GET')
          "/buscarVentaParcial"(controller: "entregaParcial", action: 'buscarVentaParcial', method: 'GET')
           "/entrega"(controller: "entregaParcial", action: 'entrega', method: 'GET')
           "/detalleParciales"(controller: "entregaParcial", action: 'detalleParciales', method: 'GET')

          


          "/iniciarParcial"(controller: "entregaParcial", action: 'crearEntregaParcial', method: 'POST')
          "/crearSurtido"(controller: "entregaParcial", action: 'crearSurtido', method: 'POST')
          "/agregarSurtido"(controller: "entregaParcial", action: 'agregarSurtidoDet', method: 'POST')
          "/agregarSurtidos"(controller: "entregaParcial", action: 'agregarSurtidos', method: 'POST')

        // Asignacion Actividad
          "/asignarActividad"(controller: "asignacionActividad", action: 'asignarActividad', method: 'POST')
          "/terminarActividad"(controller: "asignacionActividad", action: 'terminarActividad', method: 'POST')
          "/asignacionesFecha"(controller: "asignacionActividad", action: 'asignacionesPorFecha', method: 'GET')
          "/empleadosActividad"(controller: "asignacionActividad", action: 'empleadosActividad', method: 'GET')


        // Administracion

         "/surtidosPeriodo"(controller: "administracion", action: 'surtidosPeriodo', method: 'GET')
         "/reporte"(controller: "administracion", action: 'reporte', method: 'GET')




        

    }

}
