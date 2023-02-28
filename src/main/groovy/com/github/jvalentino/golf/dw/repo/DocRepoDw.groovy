package com.github.jvalentino.golf.dw.repo

import com.github.jvalentino.golf.dw.entity.DocDw
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

/**
 * Repository interface for the Doc entity
 * @author john.valentino
 */
interface DocRepoDw extends JpaRepository<DocDw, Long> {

    @Query('''
        select distinct d from DocDw d
        left join fetch d.createdByUser
        left join fetch d.updatedByUser
        where d.docId in ?1
    ''')
    List<DocDw> findByIdsWithUsers(List<Long> ids)

}
