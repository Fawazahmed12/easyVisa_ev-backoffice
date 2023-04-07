package com.easyvisa.dto

import groovy.transform.CompileStatic

@CompileStatic
class FinancialResponseDto {

    TimelineDecimalItemResponseDto clientRevenue
    TimelineDecimalItemResponseDto articleBonuses
    TimelineDecimalItemResponseDto referralBonuses

}
