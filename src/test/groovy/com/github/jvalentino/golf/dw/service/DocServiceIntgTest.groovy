package com.github.jvalentino.golf.dw.service

import com.github.jvalentino.golf.dw.entity.AuthUserDw
import com.github.jvalentino.golf.dw.entity.DocDw
import com.github.jvalentino.golf.dw.repo.AuthUserRepoDw
import com.github.jvalentino.golf.dw.repo.DocRepoDw
import com.github.jvalentino.golf.dw.util.BaseIntg
import com.github.jvalentino.golf.dw.util.DateUtil
import com.github.jvalentino.golf.rest.entity.AuthUser
import com.github.jvalentino.golf.rest.entity.Doc
import com.github.jvalentino.golf.rest.repo.AuthUserRepo
import com.github.jvalentino.golf.rest.repo.DocRepo
import org.springframework.beans.factory.annotation.Autowired

import java.sql.Timestamp

class DocServiceIntgTest extends BaseIntg {

    @Autowired
    DocService docService

    @Autowired
    DocRepo docRepo

    @Autowired
    DocRepoDw docRepoDw

    @Autowired
    AuthUserRepo authUserRepo

    @Autowired
    AuthUserRepoDw authUserRepoDw

    def "Test migrate"() {
        given:
        Date lastRun = DateUtil.toDate('2023-01-02T00:00:00.000+0000')

        and: 'a user'
        AuthUser user = new AuthUser()
        user.with {
            authUserId = 1L
            email = 'a'
            password = 'b'
            salt = 'c'
            firstName = 'd'
            lastName = 'e'
        }
        authUserRepo.save(user)

        AuthUserDw userDw = new AuthUserDw()
        userDw.with {
            authUserId = 1L
            email = 'a'
            password = 'b'
            salt = 'c'
            firstName = 'd'
            lastName = 'e'
        }
        authUserRepoDw.save(userDw)

        and: 'a document that has not changed'
        Doc unchangedSource = new Doc()
        unchangedSource.with {
            docId = 1L
            name = 'a'
            mimeType = 'b'
            createdByUserId = user.authUserId
            updatedByUserId = user.authUserId
            createdDateTime = new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
            updatedDateTime = new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
        }
        docRepo.save(unchangedSource)

        DocDw unchangedDest = new DocDw()
        unchangedDest.with {
            docId = 1L
            name = 'a'
            mimeType = 'b'
            createdByUserId = user.authUserId
            updatedByUserId = user.authUserId
            createdDateTime = new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
            updatedDateTime = new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
        }
        docRepoDw.save(unchangedDest)

        and: 'a document that has changed'
        Doc changedSource = new Doc()
        changedSource.with {
            docId = 2L
            name = 'c'
            mimeType = 'changed'
            createdByUserId = user.authUserId
            updatedByUserId = user.authUserId
            createdDateTime = new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
            updatedDateTime = new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
        }
        docRepo.save(changedSource)

        DocDw changedDest = new DocDw()
        changedDest.with {
            docId = 2L
            name = 'c'
            mimeType = 'd'
            createdByUserId = user.authUserId
            updatedByUserId = user.authUserId
            createdDateTime = new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
            updatedDateTime = new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
        }
        docRepoDw.save(changedDest)

        and: 'a new doc'
        Doc newSource = new Doc()
        newSource.with {
            docId = 3L
            name = 'e'
            mimeType = 'f'
            createdByUserId = user.authUserId
            updatedByUserId = user.authUserId
            createdDateTime = new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
            updatedDateTime = new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
        }
        docRepo.save(newSource)

        when:
        Map<Long, String> results = docService.migrate(lastRun, 1)

        then:
        results[1L] == 'unchanged'
        results[2L] == 'updated'
        results[3L] == 'new'

        and:
        DocDw one = docRepoDw.findById(1L).get()
        one.docId == 1L
        one.name == 'a'
        one.mimeType == 'b'
        one.createdByUserId == user.authUserId
        one.updatedByUserId == user.authUserId
        one.createdDateTime == new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
        one.updatedDateTime == new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)

        and:
        DocDw two = docRepoDw.findById(2L).get()
        two.docId == 2L
        two.name == 'c'
        two.mimeType == 'changed'
        two.createdByUserId == user.authUserId
        two.updatedByUserId == user.authUserId
        two.createdDateTime == new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
        two.updatedDateTime == new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)

        and:
        DocDw three = docRepoDw.findById(3L).get()
        three.docId == 3L
        three.name == 'e'
        three.mimeType == 'f'
        three.createdByUserId == user.authUserId
        three.updatedByUserId == user.authUserId
        three.createdDateTime == new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
        three.updatedDateTime == new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
    }

}
