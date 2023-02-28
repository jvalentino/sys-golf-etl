package com.github.jvalentino.golf.dw.repo

import com.github.jvalentino.golf.dw.entity.DocVersionDw
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Repository interface for the DocVersion entity
 * @author john.valentino
 */
interface DocVersionRepoDw extends JpaRepository<DocVersionDw, Long> {

}
