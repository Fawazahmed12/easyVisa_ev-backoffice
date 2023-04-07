package com.easyvisa.dto

import org.hibernate.SQLQuery
import org.hibernate.Session

class QueryDto {

    String query
    Map params

    List<String> andFragments

    Long max
    Long offset
    String sortFragment
    String direction = 'ASC'
    String extraSortFragment
    String extraSortDirection = 'ASC'

    QueryDto(String q) {
        query = q
        params = [:]
        andFragments = []
    }

    SQLQuery toSQLQuery(String q, Session session) {
        SQLQuery nativeQuery = session.createSQLQuery(q)
        params.each { k, v ->
            if (v instanceof List) {
                nativeQuery.setParameterList(k, v)
            } else {
                nativeQuery.setParameter(k, v)
            }
        }
        nativeQuery
    }

    void addAndFragment(def cond, String queryFragment, Map queryParams) {
        if (cond as Boolean) {
            andFragments << queryFragment
            params += queryParams
        }
    }

    String getCompleteQuery() {
        query + ' WHERE ' + andFragments.join(" AND ")
    }

    String getOrderClause() {
        String result = ''
        if (sortFragment || extraSortFragment) {
            result = getOrderClausePart(sortFragment, direction)
            result = getOrderClausePart(extraSortFragment, extraSortDirection, result)
        }
        result
    }

    private String getOrderClausePart(String sortFragment, String sortDirection, String orderClause = null) {
        if (sortFragment) {
            return [(orderClause ? orderClause + ',' : 'ORDER BY'), sortFragment, sortDirection].join(' ')
        }
        return null
    }

    String getLimitClause() {
        if (max != null && offset != null) {
            return ['OFFSET', offset, 'LIMIT', max].join(' ')
        }
        return ''
    }

    String withCount(String selector) {
        ['SELECT', (selector ?: 'count(*)'), completeQuery].join(' ')
    }

    String withSelect(String selector) {
        ['SELECT', (selector ?: '*'), completeQuery, orderClause, limitClause].join(' ')
    }

    static List<String> listToListOfString(List l) {
        l*.toString()
    }
}