package server

class UrlMappings {

    static mappings = {
        /*
        "/"(controller: 'application', action:'index')
        "/api/session"(controller: 'application', action: 'session')
        */
        
        "500"(view: '/error')
        "404"(view: '/notFound')

        // Surtido
        "/facturas"(controller: "surtido", action: 'facturas', method: 'GET')
        "/pedidos"(controller: "surtido", action: 'pedidos', method: 'GET')
        "/vales"(controller: "surtido", action: 'vales', method: 'GET')
        "/transformaciones"(controller: "surtido", action: 'transformaciones', method: 'GET')
    
    }

}
