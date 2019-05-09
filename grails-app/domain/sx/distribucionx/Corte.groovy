package sx.distribucionx

import sx.security.User

class Corte {

    String	id

    String clave

    String descripcion

    User	cortador

    User	empacador

    User	cancelo

    Date	inicio

    Date	fin

    Date	empacadoInicio

    Date	empacadoFin

    Date	cancelado

    Date	asignado

    Surtido surtido

    String  estado

    List    auxiliares = []

    static belongsTo = [surtido: Surtido]

    static hasMany = [auxiliares:AuxiliarCorte]

    static constraints = {
        cortador nullable: true
        asignado nullable: true
        empacador nullable: true
        cancelo nullable: true
        inicio nullable: true
        fin nullable: true
        empacadoFin nullable: true
        empacadoInicio nullable: true
        cancelado nullable: true
        asignacion nullable: true
        auxiliares nullable: true
    }

    static mapping = {
        auxiliares cascade: "all-delete-orphan"
        cancelado type:'date'
    }
}
