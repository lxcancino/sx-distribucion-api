package server

class UrlMappings {

    static mappings = {
        /*
        "/"(controller: 'application', action:'index')
        "/api/session"(controller: 'application', action: 'session')
        */
        
        "500"(view: '/error')
        "404"(view: '/notFound')
    }

}
