package sx.security

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
class User implements Serializable {

    private static final long serialVersionUID = 1

    // String id

    String username
    String password
    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired

    String apellidoPaterno
    String apellidoMaterno
    String nombres
    String nombre
    Integer numeroDeEmpleado
    String email
    String sucursal
    String puesto

    Set<Role> getAuthorities() {
        (UserRole.findAllByUser(this) as List<UserRole>)*.role as Set<Role>
    }

    static constraints = {
        password nullable: false, blank: false, password: true
        username nullable: false, blank: false, unique: true

        email nullable:true,email:true
        numeroDeEmpleado nullable:true
        sucursal nullable:true,maxSize:20
        puesto nullable:true,maxSize:30
    }

    static mapping = {
        // id generator:'uuid'
	    password column: '`password`'
    }

    private capitalizarNombre(){
        apellidoPaterno=apellidoPaterno.toUpperCase()
        apellidoMaterno=apellidoMaterno.toUpperCase()
        nombres=nombres.toUpperCase()
        nombre="$nombres $apellidoPaterno $apellidoMaterno"
    }
}
