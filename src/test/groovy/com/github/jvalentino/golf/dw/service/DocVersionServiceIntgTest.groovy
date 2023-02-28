package com.github.jvalentino.golf.dw.service

import com.github.jvalentino.golf.dw.entity.AuthUserDw
import com.github.jvalentino.golf.dw.entity.DocDw
import com.github.jvalentino.golf.dw.entity.DocVersionDw
import com.github.jvalentino.golf.dw.repo.AuthUserRepoDw
import com.github.jvalentino.golf.dw.repo.DocRepoDw
import com.github.jvalentino.golf.dw.repo.DocVersionRepoDw
import com.github.jvalentino.golf.dw.util.BaseIntg
import com.github.jvalentino.golf.dw.util.DateUtil
import com.github.jvalentino.golf.rest.entity.AuthUser
import com.github.jvalentino.golf.rest.entity.Doc
import com.github.jvalentino.golf.rest.entity.DocVersion
import com.github.jvalentino.golf.rest.repo.AuthUserRepo
import com.github.jvalentino.golf.rest.repo.DocRepo
import com.github.jvalentino.golf.rest.repo.DocVersionRepo
import org.springframework.beans.factory.annotation.Autowired

import java.sql.Timestamp

class DocVersionServiceIntgTest extends BaseIntg {

    @Autowired
    DocVersionService docVersionService

    @Autowired
    DocVersionRepo docVersionRepo

    @Autowired
    DocVersionRepoDw docVersionRepoDw

    @Autowired
    DocRepo docRepo

    @Autowired
    DocRepoDw docRepoDw

    @Autowired
    AuthUserRepo authUserRepo

    @Autowired
    AuthUserRepoDw authUserRepoDw

    def "test migrate"() {
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

        and: 'a document'
        Doc doc = new Doc()
        doc.with {
            docId = 1L
            name = 'a'
            mimeType = 'b'
            createdByUserId = user.authUserId
            updatedByUserId = user.authUserId
            createdDateTime = new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
            updatedDateTime = new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
        }
        docRepo.save(doc)

        DocDw docDw = new DocDw()
        docDw.with {
            docId = 1L
            name = 'a'
            mimeType = 'b'
            createdByUserId = user.authUserId
            updatedByUserId = user.authUserId
            createdDateTime = new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
            updatedDateTime = new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
        }
        docRepoDw.save(docDw)

        and: 'a version that has not changed'
        DocVersion unchangedSource = new DocVersion()
        unchangedSource.with {
            docVersionId = 1L
            versionNum = 1L
            docId = doc.docId
            data = [0]
            createdDateTime = new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
            createdByUserId = user.authUserId
        }
        docVersionRepo.save(unchangedSource)

        DocVersionDw unchangedDest = new DocVersionDw()
        unchangedDest.with {
            docVersionId = 1L
            versionNum = 1L
            docId = doc.docId
            data = [0]
            createdDateTime = new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
            createdByUserId = user.authUserId
        }
        docVersionRepoDw.save(unchangedDest)

        and: 'a version that has changed'
        DocVersion changedSource = new DocVersion()
        changedSource.with {
            docVersionId = 2L
            versionNum = 1L
            docId = doc.docId
            data = [1]
            createdDateTime = new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
            createdByUserId = user.authUserId
        }
        docVersionRepo.save(changedSource)

        DocVersionDw changedDest = new DocVersionDw()
        changedDest.with {
            docVersionId = 2L
            versionNum = 1L
            docId = doc.docId
            data = [0]
            createdDateTime = new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
            createdByUserId = user.authUserId
        }
        docVersionRepoDw.save(changedDest)

        and: 'a new version'
        DocVersion newSource = new DocVersion()
        newSource.with {
            docVersionId = 3L
            versionNum = 1L
            docId = doc.docId
            data = [2]
            createdDateTime = new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
            createdByUserId = user.authUserId
        }
        docVersionRepo.save(newSource)

        when:
        Map<Long, String> results = docVersionService.migrate(lastRun, 1)

        then:
        results[1L] == 'unchanged'
        results[2L] == 'updated'
        results[3L] == 'new'

        and:
        DocVersionDw one = docVersionRepoDw.findById(1L).get()
        one.docVersionId == 1L
        one.versionNum == 1L
        one.docId == doc.docId
        one.data == [0]
        one.createdDateTime == new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
        one.createdByUserId == user.authUserId

        and:
        DocVersionDw two = docVersionRepoDw.findById(2L).get()
        two.docVersionId == 2L
        two.versionNum == 1L
        two.docId == doc.docId
        two.data == [1]
        two.createdDateTime == new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
        two.createdByUserId == user.authUserId

        and:
        DocVersionDw three = docVersionRepoDw.findById(3L).get()
        three.docVersionId == 3L
        three.versionNum == 1L
        three.docId == doc.docId
        three.data == [2]
        three.createdDateTime == new Timestamp(DateUtil.toDate('2023-01-03T00:00:00.000+0000').time)
        three.createdByUserId == user.authUserId

    }

}
