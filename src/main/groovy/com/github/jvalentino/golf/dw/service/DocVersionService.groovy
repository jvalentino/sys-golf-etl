package com.github.jvalentino.golf.dw.service

import com.github.jvalentino.golf.dw.entity.AuthUserDw
import com.github.jvalentino.golf.rest.entity.DocVersion
import com.github.jvalentino.golf.rest.repo.DocVersionRepo
import com.github.jvalentino.golf.dw.config.CustomObjectMapper
import com.github.jvalentino.golf.dw.entity.DocDw
import com.github.jvalentino.golf.dw.entity.DocVersionDw
import com.github.jvalentino.golf.dw.repo.DocVersionRepoDw
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

/**
 * Used for dealing with DocVersion migration
 * @author john.valentino
 */
@Service
@Slf4j
@CompileDynamic
class DocVersionService {

    @Autowired
    DocVersionRepo docVersionRepo

    @Autowired
    DocVersionRepoDw docVersionRepoDw

    Map<Long, String> migrate(Date lastRun, int batchSize=1_000) {
        Map<Long, String> results = [:]

        boolean moreRecords = true
        int page = 0
        while (moreRecords) {
            Pageable pageableRequest = new PageRequest(page, batchSize, Sort.by('docVersionId'))
            List<DocVersion> sources = docVersionRepo.findUpdatedAfter(lastRun, pageableRequest).toList()

            if (sources.size() == 0) {
                moreRecords = false
                break
            }

            List<Long> ids = this.getIds(sources)
            List<DocDw> dests = docVersionRepoDw.findAllById(ids)
            this.migrate(sources, dests, results)

            page++
        }

        results
    }

    void delete(Date priorTo) {
        docVersionRepo.deletePriorTo(priorTo)
    }

    protected List<Long> getIds(List<DocVersion> sources) {
        List<Long> ids = []

        for (DocVersion source : sources) {
            ids.add(source.docVersionId)
        }

        ids
    }

    protected void migrate(List<DocVersion> sources, List<DocVersionDw> dests, Map<Long, String> results) {
        Map<Long, AuthUserDw> destMap = [:]
        for (DocVersionDw doc : dests) {
            destMap[doc.docVersionId] = doc
        }

        for (DocVersion source : sources) {
            DocVersionDw dest = destMap[source.docVersionId]

            boolean isNew = false
            if (dest == null) {
                dest = new DocVersionDw()
                isNew = true
            }

            // only save if there has been a change

            String sourceJson = new CustomObjectMapper().writeValueAsString(source)
            String destJson = new CustomObjectMapper().writeValueAsString(dest)
            boolean different =  sourceJson != destJson

            if (different) {
                if (isNew) {
                    results[source.docVersionId] = 'new'
                } else {
                    results[source.docVersionId] = 'updated'
                }
                dest.with {
                    docVersionId = source.docVersionId
                    versionNum = source.versionNum
                    docId = source.docId
                    data = source.data
                    createdDateTime = source.createdDateTime
                    createdByUserId = source.createdByUserId
                }

                docVersionRepoDw.save(dest)
            } else {
                results[source.docVersionId] = 'unchanged'
            }

            log.info("DocVersion ${source.docVersionId} ${results[source.docVersionId]}")
        }
    }

}
