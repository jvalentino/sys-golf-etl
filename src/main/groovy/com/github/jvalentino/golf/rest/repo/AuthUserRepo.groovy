package com.github.jvalentino.golf.rest.repo

import com.github.jvalentino.golf.rest.entity.AuthUser
import org.springframework.data.repository.PagingAndSortingRepository

/**
 * Repository interface for the AuthUser entity
 * @author john.valentino
 */
interface AuthUserRepo extends PagingAndSortingRepository<AuthUser, Long> {

}
