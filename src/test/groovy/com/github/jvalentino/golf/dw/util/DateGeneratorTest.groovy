package com.github.jvalentino.golf.dw.util

import com.github.jvalentino.golf.dw.util.DateGenerator
import spock.lang.Specification

class DateGeneratorTest extends Specification {

    def "Test DateGenerator"() {
        when:
        Date result = DateGenerator.date()

        then:
        result
    }
}
