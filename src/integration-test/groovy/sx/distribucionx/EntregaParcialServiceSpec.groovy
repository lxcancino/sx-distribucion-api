package sx.distribucionx

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class EntregaParcialServiceSpec extends Specification {

    EntregaParcialService entregaParcialService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new EntregaParcial(...).save(flush: true, failOnError: true)
        //new EntregaParcial(...).save(flush: true, failOnError: true)
        //EntregaParcial entregaParcial = new EntregaParcial(...).save(flush: true, failOnError: true)
        //new EntregaParcial(...).save(flush: true, failOnError: true)
        //new EntregaParcial(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //entregaParcial.id
    }

    void "test get"() {
        setupData()

        expect:
        entregaParcialService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<EntregaParcial> entregaParcialList = entregaParcialService.list(max: 2, offset: 2)

        then:
        entregaParcialList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        entregaParcialService.count() == 5
    }

    void "test delete"() {
        Long entregaParcialId = setupData()

        expect:
        entregaParcialService.count() == 5

        when:
        entregaParcialService.delete(entregaParcialId)
        sessionFactory.currentSession.flush()

        then:
        entregaParcialService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        EntregaParcial entregaParcial = new EntregaParcial()
        entregaParcialService.save(entregaParcial)

        then:
        entregaParcial.id != null
    }
}
