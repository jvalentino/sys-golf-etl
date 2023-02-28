package com.github.jvalentino.golf.dw.entity

import groovy.transform.CompileDynamic

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table
import java.sql.Timestamp

/**
 * represents the document
 * @author john.valentino
 */
@CompileDynamic
@Entity
@Table(name = 'doc')
class DocDw {

    @Id
    @Column(name = 'doc_id')
    Long docId

    String name

    @Column(name = 'mime_type')
    String mimeType

    @Column(name = 'created_by_user_id', nullable=false)
    Long createdByUserId

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = 'created_by_user_id', referencedColumnName = 'auth_user_id',
            updatable = false, insertable = false)
    AuthUserDw createdByUser

    @Column(name = 'updated_by_user_id', nullable=false)
    Long updatedByUserId

    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = 'updated_by_user_id', referencedColumnName = 'auth_user_id',
            updatable = false, insertable = false)
    AuthUserDw updatedByUser

    @Column(name = 'created_datetime')
    Timestamp createdDateTime

    @Column(name = 'updated_datetime')
    Timestamp updatedDateTime

    @OneToMany(mappedBy='doc', fetch = FetchType.LAZY)
    Set<DocVersionDw> versions

    @OneToMany(mappedBy='doc', fetch = FetchType.LAZY)
    Set<DocTaskDw> tasks

}
