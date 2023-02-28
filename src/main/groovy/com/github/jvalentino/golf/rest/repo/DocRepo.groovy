package com.github.jvalentino.golf.rest.repo

import com.github.jvalentino.golf.rest.entity.Doc
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional

/**
 * Repository interface for the Doc entity
 * @author john.valentino
 */
interface DocRepo extends JpaRepository<Doc, Long> {

    @Query('''
        select distinct d from Doc d
        where d.updatedDateTime >= ?1
    ''')
    Page<Doc> findUpdatedAfter(Date date, Pageable pageable)

    @Transactional
    @Modifying
    @Query('''
        delete from Doc d
        where d.updatedDateTime < ?1
    ''')
    void deletePriorTo(Date date)

}
