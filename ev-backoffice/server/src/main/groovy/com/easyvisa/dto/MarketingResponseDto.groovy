package com.easyvisa.dto

import groovy.transform.CompileStatic

@CompileStatic
class MarketingResponseDto {

    TimelineItemResponseDto activeClients
    TimelineItemResponseDto prospectiveClients
    TimelineItemResponseDto phoneNumberClients

}
