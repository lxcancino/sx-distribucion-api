package sx.distribucionx

import sx.security.User

class Corte {

    String	id

    String clave

    String descripcion

    String origen

    String instruccion

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

    Long factura

    Long venta

    BigDecimal cantidad

    BigDecimal cantidadParcial

    Boolean parcial = false

    Boolean parcializado = false

    Date dateCreated
    Date lastUpdated

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
        auxiliares nullable: true
        origen nullable: true
        instruccion nullable:true
        factura nullable:true
        venta nullable:true
        cantidad nullable:true
        cantidadParcial nullable: true
    }

    static mapping = {
        id generator:'uuid'
        auxiliares cascade: "all-delete-orphan"
        cancelado type:'date'
    }
}
