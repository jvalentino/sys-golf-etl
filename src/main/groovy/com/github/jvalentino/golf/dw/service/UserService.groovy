package com.github.jvalentino.golf.dw.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jvalentino.golf.dw.entity.AuthUserDw
import com.github.jvalentino.golf.rest.entity.AuthUser
import com.github.jvalentino.golf.rest.repo.AuthUserRepo
import com.github.jvalentino.golf.dw.repo.AuthUserRepoDw
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

/**
 * Service for dealing with user related data migration
 * @author john.valentino
 */
@CompileDynamic
@Slf4j
@Service
class UserService {

    @Autowired
    AuthUserRepo authUserRepo

    @Autowired
    AuthUserRepoDw authUserRepoDw

    Map<Long, String> migrate(int batchSize=1_000) {
        Map<Long, String> results = [:]

        boolean moreRecords = true
        int start = 0
        while (moreRecords) {
            List<AuthUser> sourceUsers = authUserRepo.findAll(
                    new PageRequest(start, batchSize, Sort.by('authUserId'))).toList()

            List<Long> ids = []
            for (AuthUser user : sourceUsers) {
                ids.add(user.authUserId)
            }

            List<AuthUserDw> destUsers = authUserRepoDw.findAllById(ids)

            if (sourceUsers.size() == 0) {
                moreRecords = false
                break
            }

            this.migrate(sourceUsers, destUsers, results)

            start += batchSize
        }
        results
    }

    protected void migrate(List<AuthUser> sourceUsers, List<AuthUserDw> destUsers, Map<Long, String> results) {
        Map<Long, AuthUserDw> destUserMap = [:]
        for (AuthUserDw user : destUsers) {
            destUserMap[user.authUserId] = user
        }

        for (AuthUser sourceUser : sourceUsers) {
            AuthUserDw destUser = destUserMap[sourceUser.authUserId]

            boolean isNew = false
            if (destUser == null) {
                destUser = new AuthUserDw()
                isNew = true
            }

            // only save if there has been a change

            String source = new ObjectMapper().writeValueAsString(sourceUser)
            String dest = new ObjectMapper().writeValueAsString(destUser)
            boolean different =  dest != source

            if (different) {
                if (isNew) {
                    results[sourceUser.authUserId] = 'new'
                } else {
                    results[sourceUser.authUserId] = 'updated'
                }
                destUser.with {
                    authUserId = sourceUser.authUserId
                    email = sourceUser.email
                    password = sourceUser.password
                    salt = sourceUser.salt
                    firstName = sourceUser.firstName
                    lastName = sourceUser.lastName
                }

                authUserRepoDw.save(destUser)
            } else {
                results[sourceUser.authUserId] = 'unchanged'
            }

            log.info("AuthUser ${sourceUser.authUserId} ${results[sourceUser.authUserId]}")
        }
    }

}
