package sx.distribucionx

import sx.security.User

class AuxiliarSurtido {

    String id

    String nombre

    Date dateCreated

    Surtido surtido

    static constraints = {

    }

     static  mapping = {
        id generator: 'uuid'
    }

}
