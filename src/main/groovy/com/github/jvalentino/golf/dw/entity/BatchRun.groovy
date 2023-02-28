package com.github.jvalentino.golf.dw.entity

import groovy.transform.CompileDynamic

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table
import java.sql.Timestamp

/**
 * A batch run
 * @author john.valentino
 */
@CompileDynamic
@Entity
@Table(name = 'batch_run')
class BatchRun {

    @Id @GeneratedValue
    @Column(name = 'batch_run_id')
    Long batchRunId

    @Column(name = 'created_datetime')
    Timestamp createdDateTime

    Boolean success

    @Column(name = 'error_text')
    String errorText

    @Column(name = 'runtime_ms')
    Long runtimeMs

}
