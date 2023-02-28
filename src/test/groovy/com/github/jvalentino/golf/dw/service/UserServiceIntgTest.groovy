package com.github.jvalentino.golf.dw.service

import com.github.jvalentino.golf.dw.entity.AuthUserDw
import com.github.jvalentino.golf.dw.repo.AuthUserRepoDw
import com.github.jvalentino.golf.dw.util.BaseIntg
import com.github.jvalentino.golf.rest.entity.AuthUser
import com.github.jvalentino.golf.rest.repo.AuthUserRepo
import org.springframework.beans.factory.annotation.Autowired

class UserServiceIntgTest extends BaseIntg {

    @Autowired
    UserService userService

    @Autowired
    AuthUserRepo authUserRepo

    @Autowired
    AuthUserRepoDw authUserRepoDw

    def "test migrate"() {
        given: 'that a user has not changed'
        AuthUser unchangedSource = new AuthUser()
        unchangedSource.with {
            authUserId = 1L
            email = 'a'
            password = 'b'
            salt = 'c'
            firstName = 'd'
            lastName = 'e'
        }
        authUserRepo.save(unchangedSource)

        AuthUserDw unchangedDest = new AuthUserDw()
        unchangedDest.with {
            authUserId = 1L
            email = 'a'
            password = 'b'
            salt = 'c'
            firstName = 'd'
            lastName = 'e'
        }
        authUserRepoDw.save(unchangedDest)

        and: 'that a user has had a change'
        AuthUser changedSource = new AuthUser()
        changedSource.with {
            authUserId = 2L
            email = 'f'
            password = 'g'
            salt = 'h'
            firstName = 'i'
            lastName = 'changed'
        }
        authUserRepo.save(changedSource)

        AuthUserDw changedDest = new AuthUserDw()
        changedDest.with {
            authUserId = 2L
            email = 'f'
            password = 'g'
            salt = 'h'
            firstName = 'i'
            lastName = 'j'
        }
        authUserRepoDw.save(changedDest)

        and: 'a new user'
        AuthUser newSource = new AuthUser()
        newSource.with {
            authUserId = 3L
            email = 'k'
            password = 'l'
            salt = 'm'
            firstName = 'n'
            lastName = 'o'
        }
        authUserRepo.save(newSource)

        when:
        Map<Long, String> results = userService.migrate(1)

        then:
        results[1L] == 'unchanged'
        results[2L] == 'updated'
        results[3L] == 'new'

        and:
        AuthUserDw one = authUserRepoDw.findById(1L).get()
        one.authUserId == 1L
        one.email == 'a'
        one.password == 'b'
        one.salt == 'c'
        one.firstName == 'd'
        one.lastName == 'e'

        and:
        AuthUserDw two = authUserRepoDw.findById(2L).get()
        two.authUserId == 2L
        two.email == 'f'
        two.password == 'g'
        two.salt == 'h'
        two.firstName == 'i'
        two.lastName == 'changed'

        and:
        AuthUserDw three = authUserRepoDw.findById(3L).get()
        three.authUserId == 3L
        three.email == 'k'
        three.password == 'l'
        three.salt == 'm'
        three.firstName == 'n'
        three.lastName == 'o'
    }
}
