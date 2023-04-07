package com.easyvisa

import com.easyvisa.enums.ArticleStatus
import groovy.transform.ToString

@ToString(includes = 'id', includeNames = true, includePackage = false)
class Article {

    LegalRepresentative author
    Organization organization
    String categoryId
    String categoryName
    ArticleStatus status
    String title
    String body
    Long views = 0
    Long wordsCount
    Date dateSubmitted
    Date dispositioned
    Boolean isApproved
    String rejectedMessage
    String url

    Date dateCreated
    Date lastUpdated

    static mapping = {
        body sqlType:'text'
        rejectedMessage sqlType:'text'
        id generator: 'native', params: [sequence: 'article_id_seq']
    }

    static constraints = {
        wordsCount min:600L
        dateSubmitted nullable:true
        dispositioned nullable:true
        isApproved nullable:true
        url url:true, nullable:true
        rejectedMessage nullable:true
    }

}
