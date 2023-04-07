package com.easyvisa

import com.easyvisa.enums.ImmigrationBenefitCategory
import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class PackageSpec extends Specification implements DomainUnitTest<Package> {

    def setup() {
    }

    def cleanup() {
    }

    void "test package benefit constraints"(benefits, isValid) {
        when:
        Package aPackage = new Package()
        aPackage.benefits = benefits
        then:
        aPackage.validate(['benefits']) == isValid
        where:
        benefits                                                                  | isValid
        null                                                                      | false
        []                                                                        | false
        [new ImmigrationBenefit(category: ImmigrationBenefitCategory.IR1, id: 1)] | true
        [new ImmigrationBenefit(category: ImmigrationBenefitCategory.IR1, id: 1),
         new ImmigrationBenefit(category: ImmigrationBenefitCategory.IR2, id: 2)] | true
        [new ImmigrationBenefit(category: ImmigrationBenefitCategory.IR1, id: 1),
         new ImmigrationBenefit(category: ImmigrationBenefitCategory.IR2, id: 2),
         new ImmigrationBenefit(category: ImmigrationBenefitCategory.F1_A, id: 3)]  | false
        [new ImmigrationBenefit(category: ImmigrationBenefitCategory.IR1, id: 1),
         new ImmigrationBenefit(category: ImmigrationBenefitCategory.F1_A, id: 2)]  | false
        [new ImmigrationBenefit(category: ImmigrationBenefitCategory.IR1, id: 1),
         new ImmigrationBenefit(category: ImmigrationBenefitCategory.IR2, id: 2),
         new ImmigrationBenefit(category: ImmigrationBenefitCategory.F1_A, id: 3),
         new ImmigrationBenefit(category: ImmigrationBenefitCategory.F2_A, id: 4)]  | false

    }
}
