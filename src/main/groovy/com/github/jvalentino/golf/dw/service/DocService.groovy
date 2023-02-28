package com.github.jvalentino.golf.dw.service

import com.github.jvalentino.golf.rest.entity.Doc
import com.github.jvalentino.golf.rest.repo.DocRepo
import com.github.jvalentino.golf.dw.config.CustomObjectMapper
import com.github.jvalentino.golf.dw.entity.AuthUserDw
import com.github.jvalentino.golf.dw.entity.DocDw
import com.github.jvalentino.golf.dw.repo.DocRepoDw
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

/**
 * Service for handling Doc migration
 * @author john.valentino
 */
@Service
@Slf4j
@CompileDynamic
class DocService {

    @Autowired
    DocRepo docRepo

    @Autowired
    DocRepoDw docRepoDw

    Map<Long, String> migrate(Date lastRun, int batchSize=1_000) {
        Map<Long, String> results = [:]

        boolean moreRecords = true
        int page = 0
        while (moreRecords) {
            Pageable pageableRequest = new PageRequest(page, batchSize, Sort.by('docId'))
            List<Doc> sourceDocs = docRepo.findUpdatedAfter(lastRun, pageableRequest).toList()

            if (sourceDocs.size() == 0) {
                moreRecords = false
                break
            }

            List<Long> ids = this.getIds(sourceDocs)
            List<DocDw> destDocs = docRepoDw.findByIdsWithUsers(ids)
            this.migrate(sourceDocs, destDocs, results)

            page++
        }

        results
    }

    void deletePriorTo(Date date) {
        docRepo.deletePriorTo(date)
    }

    protected List<Long> getIds(List<Doc> sourceDocs) {
        List<Long> ids = []

        for (Doc doc : sourceDocs) {
            ids.add(doc.docId)
        }

        ids
    }

    protected void migrate(List<Doc> sourceDocs, List<DocDw> destDocs, Map<Long, String> results) {
        Map<Long, AuthUserDw> destMap = [:]
        for (DocDw doc : destDocs) {
            destMap[doc.docId] = doc
        }

        for (Doc source : sourceDocs) {
            DocDw dest = destMap[source.docId]

            boolean isNew = false
            if (dest == null) {
                dest = new DocDw()
                isNew = true
            }

            // only save if there has been a change

            String sourceJson = new CustomObjectMapper().writeValueAsString(source)
            String destJson = new CustomObjectMapper().writeValueAsString(dest)
            boolean different =  sourceJson != destJson

            if (different) {
                if (isNew) {
                    results[source.docId] = 'new'
                } else {
                    results[source.docId] = 'updated'
                }
                dest.with {
                    docId = source.docId
                    name = source.name
                    mimeType = source.mimeType
                    createdByUserId = source.createdByUserId
                    updatedByUserId = source.updatedByUserId
                    createdDateTime = source.createdDateTime
                    updatedDateTime = source.updatedDateTime
                }

                docRepoDw.save(dest)
            } else {
                results[source.docId] = 'unchanged'
            }

            log.info("Doc ${source.docId} ${results[source.docId]}")
        }
    }

}
