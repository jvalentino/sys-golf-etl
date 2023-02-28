package com.github.jvalentino.golf.dw

import com.github.jvalentino.golf.dw.GolfEtlApp
import org.springframework.boot.SpringApplication
import spock.lang.Specification

class GolfEtlAppTest extends Specification {

    def setup() {
        GroovyMock(SpringApplication, global:true)
    }

    def "test main"() {
        when:
        GolfEtlApp.main(null)

        then:
        1 * SpringApplication.run(GolfEtlApp, null)
    }

}
