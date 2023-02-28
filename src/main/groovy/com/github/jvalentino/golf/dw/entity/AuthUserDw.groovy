package com.github.jvalentino.golf.dw.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileDynamic

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

/**
 * Represents an authorized user
 * @author john.valentino
 */
@CompileDynamic
@Entity
@Table(name = 'auth_user')
class AuthUserDw {

    @Id
    @Column(name = 'auth_user_id')
    Long authUserId

    String email

    @JsonIgnore
    String password

    @JsonIgnore
    String salt

    @Column(name = 'first_name')
    String firstName

    @Column(name = 'last_name')
    String lastName

    @OneToMany(mappedBy='createdByUser', fetch = FetchType.LAZY)
    @JsonIgnore
    Set<DocDw> createdBys

    @OneToMany(mappedBy='updatedByUser', fetch = FetchType.LAZY)
    @JsonIgnore
    Set<DocDw> updatedBys

    @OneToMany(mappedBy='createdByUser', fetch = FetchType.LAZY)
    @JsonIgnore
    Set<DocVersionDw> versionCreatedBys

    @OneToMany(mappedBy='createdByUser', fetch = FetchType.LAZY)
    @JsonIgnore
    Set<DocTaskDw> taskCreatedBys

    @OneToMany(mappedBy='updatedByUser', fetch = FetchType.LAZY)
    @JsonIgnore
    Set<DocTaskDw> taskUpdatedBys

}
