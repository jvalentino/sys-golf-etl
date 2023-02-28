package com.github.jvalentino.golf.dw.dto

import com.github.jvalentino.golf.dw.entity.BatchRun
import groovy.transform.CompileDynamic

/**
 * General debug health
 * @author john.valentino
 */
@CompileDynamic
class HealthDto {

    boolean success = true
    Long sourceAuthUsers = 0L
    Long sourceDocs = 0L
    Long sourceDocVersions = 0L
    Long destAuthUsers = 0L
    Long destDocs = 0L
    Long destDocVersions = 0L

    List<BatchRun> runs = []

}
