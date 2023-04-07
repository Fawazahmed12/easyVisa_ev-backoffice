package com.easyvisa

import groovy.time.TimeCategory

class UserDevicesCleanJob {
    static concurrent = false

    protected static final int MAX_PAGE_DB_SIZE = 10000

    UserService userService

    void execute() {
        log.info('Job: Started job for clearing user devices')
        Integer offset = 0
        Integer total = 0
        Date date = new Date()
        use(TimeCategory) {
            date = date - 2.weeks
        }
        List<Long> users = getUsers(offset)
        //getting users in pagination way, due to there can be a lot of users in the db
        while (users) {
            total += users.size()
            users.each {
                userService.clearUserDevices(it, date)
            }
            offset += MAX_PAGE_DB_SIZE
            users = getUsers(offset)
        }
        log.info("Job: Finished job for clearing user devices. Touched users = ${total}")
    }

    protected List<Long> getUsers(Integer offset) {
        LegalRepresentative.withNewTransaction {
            User.createCriteria().list {
                projections {
                    property('id')
                }
                firstResult(offset)
                maxResults(MAX_PAGE_DB_SIZE)
                order('id')
            } as List<Long>
        }
    }

}
