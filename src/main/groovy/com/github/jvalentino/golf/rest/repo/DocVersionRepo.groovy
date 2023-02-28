package com.github.jvalentino.golf.rest.repo

import com.github.jvalentino.golf.rest.entity.DocVersion
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

/**
 * Repository interface for the DocVersion entity
 * @author john.valentino
 */
interface DocVersionRepo extends JpaRepository<DocVersion, Long> {

    @Query('''
        select distinct d from DocVersion d
        where d.createdDateTime >= ?1
    ''')
    Page<DocVersion> findUpdatedAfter(Date date, Pageable pageable)

    @Transactional
    @Modifying
    @Query('''
        delete from DocVersion d
        where d.createdDateTime < ?1
    ''')
    void deletePriorTo(Date date)

}
