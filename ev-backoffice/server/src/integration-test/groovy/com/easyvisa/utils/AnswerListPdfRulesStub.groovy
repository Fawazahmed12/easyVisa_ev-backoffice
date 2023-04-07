package com.easyvisa.utils

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.util.DateUtil

import java.time.LocalDate
import java.time.Month

import static TestUtils.getAnswerInstance

class AnswerListPdfRulesStub {

    static List<Answer> petitionerCriminalSingle(long packageId, long applicantId) {
        [
                //Have you ever been arrested cited charged indicted convicted fined or imprisoned for breaking or violating any law or ordinance in any country excluding traffic violations (unless a traffic violation was alcohol- or drug-related or involved a fine of $500 or more)?
                getAnswerInstance(packageId, applicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1115", "yes"),
                getAnswerInstance(packageId, applicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1116/0", "Speed limit"),
                getAnswerInstance(packageId, applicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1117/0", "2000/05/08"),
                getAnswerInstance(packageId, applicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1118/0", "payed")

        ]
    }

    static List<Answer> petitionerCriminalQuadro(long packageId, long applicantId) {
        List<Answer> result = petitionerCriminalSingle(packageId, applicantId)
        result.addAll([
                getAnswerInstance(packageId, applicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1116/1", "Speed limit 2016"),
                getAnswerInstance(packageId, applicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1117/1", "2016/01/01"),
                getAnswerInstance(packageId, applicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1118/1", "payed in 2016"),
                getAnswerInstance(packageId, applicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1116/2", "Speed limit 2017"),
                getAnswerInstance(packageId, applicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1117/2", "2017/02/02"),
                getAnswerInstance(packageId, applicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1118/2", "payed in 2017"),
                getAnswerInstance(packageId, applicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1116/3", "Speed limit 2018"),
                getAnswerInstance(packageId, applicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1117/3", "2018/03/03"),
                getAnswerInstance(packageId, applicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1118/3", "payed in 2018")
        ])
        result
    }

    static List<Answer> petitionerAddressHistoryAnswerList(Long packageId, Long applicantId) {
        Integer currentYear = DateUtil.today().year
        [
                //In what country is your current physical address?
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_42", "United States"),
                //Street Number and Name
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_43", "400 North Car Street"),
                //Does your address have a secondary description (i.e. apartment, suite, or floor)?
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_44", "yes"),
                //What is the secondary address description?
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_45", "apartment"),
                //Apartment/Suite/Floor Help
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_46", "123"),
                //City/Town/Village
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_47", "Aurora"),
                //State
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_48", "Colorado"),
                //ZIP Code
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_50", "80011"),
                //When did you move into this address?
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_52", DateUtil.fromDate(LocalDate.of(currentYear - 1, Month.JANUARY, 17))),

                //In what country was this previous physical address?
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_54/0", "Albania"),
                //Street Number and Name?
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_55/0", "23-A Mustafa Matohiti"),
                //Does your address have a secondary description (i.e. apartment, suite, or floor)?2", "3423123098992001");//Apartment/Suite/Floor Help
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_56/0", "no"),
                //City/Town/Village
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_59/0", "Miladin"),
                //Province/Territory/Prefecture/Parish
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_61/0", "Tirana"),
                //Postal Code
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_63/0", "1031"),
                //When did you move into this address?
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_64/0", DateUtil.fromDate(LocalDate.of(currentYear - 2, Month.MAY, 1))),
                //When did you move out of this address?
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_65/0", DateUtil.fromDate(LocalDate.of(currentYear - 1, Month.JANUARY, 15))),
                //In what country was this previous physical address?
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_54/1", "Belarus"),
                //Street Number and Name?
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_55/1", "10 Chkalova street"),
                //Does your address have a secondary description (i.e. apartment, suite, or floor)?2", "3423123098992001");
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_56/1", "no"),
                //City/Town/Village
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_59/1", "Minsk"),
                //Province/Territory/Prefecture/Parish
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_61/1", "Minks area"),
                //Postal Code
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_63/1", "220025"),
                //When did you move into this address?
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_64/1", DateUtil.fromDate(LocalDate.of(currentYear - 10, Month.MAY, 1))),
                //When did you move out of this address?
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_65/1", DateUtil.fromDate(LocalDate.of(currentYear - 2, Month.JANUARY, 15))),
        ]
    }

    static List<Answer> seeContinuationSheet129fLongExplanation(long packageId, long applicantId) {
        [
                //Have you ever been arrested cited charged indicted convicted fined or imprisoned for breaking or violating any law or ordinance in any country excluding traffic violations (unless a traffic violation was alcohol- or drug-related or involved a fine of $500 or more)?
                getAnswerInstance(packageId, applicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1115", "yes"),
                getAnswerInstance(packageId, applicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1116/0", "Very very long text 1 Very very long text 2 Very very long text 3 Very very long text 4 Very very long text 5 Very very long text 6 Very very long text 7 Very very long text 8 Very very long text 9 Very very long text 10 Very very long text 1 Very very long text 2 Very very long text 3 Very very long text 4 Very very long text 5 Very very long text 6 Very very long text 7 Very very long text 8 Very very long text 9 Very very long text 20 Very very long text 1 Very very long text 2 Very very long text 3 Very very long text 4 Very very long text 5 Very very long text 6 Very very long text 7 Very very long text 8 Very very long text 9 Very very long text 30 Very very long text 1 Very very long text 2 Very very long text 3 Very very long text 4 Very very long text 5 Very very long text 6 Very very long text 7 Very very long text 8 Very very long text 9 Very very long text 40"),
                getAnswerInstance(packageId, applicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1117/0", "2000/05/08"),
                getAnswerInstance(packageId, applicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1118/0", "payed")

        ]
    }

    static List<Answer> seeContinuationSheets129f(long packageId, long applicantId) {
        List<Answer> result = petitionerCriminalSingle(packageId, applicantId)
        result.addAll(otherNamesUsed(packageId, applicantId))
        result
    }

    static List<Answer> otherNamesUsed(long packageId, long applicantId) {
        [
                //Have you ever used another name for your Given Name (First Name), Middle Name (including maiden names), or Family Name/Last Name/Surname?
                getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_6/Q_35", "yes"),
                //List any other Family Name/Last Name/Surname used (e.g. Smith II, Smith Jr.):
                getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_6/Q_41/0", "Smith"),
                //List any other Given Name used (e.g. Rich, Rick, Dick):
                getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_6/Q_37/0", "Daniel"),
                //List any other Middle Name used:
                getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_6/Q_39/0", "James"),

                //List any other Family Name/Last Name/Surname used (e.g. Smith II, Smith Jr.):
                getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_6/Q_41/1", "Brown"),
                //List any other Given Name used (e.g. Rich, Rick, Dick):
                getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_6/Q_37/1", "Thomas"),
                //List any other Middle Name used:
                getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_6/Q_39/1", "Harry")
        ]
    }

    static List<Answer> beneficiaryOtherNamesTripleWithAnumber(long packageId, long applicantId) {
        List<Answer> result = [
                getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1906", "yes"),
                getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1908/0", "Peter"),
                getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1910/0", "Junior"),
                getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1912/0", "Brown"),

                getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1908/1", "Peter"),
                getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1910/1", "Tom"),
                getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1912/1", "Smith"),

                getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1908/2", "William"),
                getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1910/2", "Senior"),
                getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1912/2", "Green")
        ]
        result.addAll(beneficiaryAnumber(packageId, applicantId))
        result
    }

    static List<Answer> beneficiaryAnumber(long packageId, long applicantId) {
        [
                getAnswerInstance(packageId, applicantId, "Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2412", "012345678")
        ]
    }

    static List<Answer> beneficiaryOtherNamesQuadro(long packageId, long applicantId, Boolean withMiddleInLast = true) {
        List<Answer> result = beneficiaryOtherNamesTripleWithAnumber(packageId, applicantId)
        result.addAll(
                [
                        getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1908/3", "Tom"),
                        getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1912/3", "Small")
                ]
        )
        if (withMiddleInLast) {
            result.addAll([
                    getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1910/3", "Jerry")
            ])
        }
        result
    }

    static List<Answer> beneficiaryOtherNamesNine(long packageId, long applicantId, Boolean withMiddle = true) {
        List<Answer> result = beneficiaryOtherNamesQuadro(packageId, applicantId, withMiddle)
        result.addAll(
                [
                        getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1908/4", "Alexander"),
                        getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1912/4", "Cook"),
                        getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1908/5", "Aiden"),
                        getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1912/5", "Morgan"),
                        getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1908/6", "Daniel"),
                        getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1912/6", "Bell"),
                        getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1908/7", "Anthony"),
                        getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1912/7", "Murphy"),
                        getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1908/8", "Matthew"),
                        getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1912/8", "Bailey")
                ]
        )
        if (withMiddle) {
            result.addAll([
                    getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1910/4", "Elijah"),
                    getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1910/5", "Joshua"),
                    getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1910/6", "Liam"),
                    getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1910/7", "Andrew"),
                    getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1910/8", "James")
            ])
        }
        result
    }

    static List<Answer> commonHeaderPopulation(long packageId, long applicantId) {
        List<Answer> result = otherNamesUsed(packageId, applicantId)
        result.addAll(petitionerAlienNumber(packageId, applicantId))
        result
    }

    static List<Answer> residence18HeaderPopulation(long packageId, long applicantId) {
        [
                //Have you ever lived in another U.S. state since your 18th birthday?
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_residedSince18/Q_6007", "yes"),
                //State
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_residedSince18/Q_66/0", "Alabama"),
                //State
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_residedSince18/Q_66/1", "Colorado"),
                //State
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_residedSince18/Q_66/2", "New York"),
                //Have you ever lived in a foreign country since your 18th birthday?
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_residedSince18/Q_6008", "yes"),
                //Country
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_residedSince18/Q_67/0", "France"),
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_residedSince18/Q_67/1", "Germany")
        ]
    }

    static List<Answer> petitionerAlienNumber(long packageId, long applicantId) {
        [
                //Alien Registration Number (A-Number)
                getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_112", "654123087"),
        ]
    }

    static List<Answer> assetSingleValues(long packageId, long applicantId) {
        List<Answer> result = []
        result.addAll(bankDeposits(packageId, applicantId))
        result.addAll(personalValue(packageId, applicantId))
        result.addAll(realEstate(packageId, applicantId))
        result.addAll(lifeInsurance(packageId, applicantId))
        result.addAll(financialInstruments(packageId, applicantId))
        result
    }

    static List<Answer> assetDoubleValues(long packageId, long applicantId) {
        List<Answer> result = assetSingleValues(packageId, applicantId)
        result.addAll(bankDepositsSecond(packageId, applicantId))
        result.addAll(personalValueSecond(packageId, applicantId))
        result.addAll(realEstateSecond(packageId, applicantId))
        result.addAll(lifeInsurance(packageId, applicantId))
        result.addAll(financialInstrumentsSecond(packageId, applicantId))
        result
    }

    static List<Answer> bankDeposits(long packageId, long applicantId) {
        [
                //Do you have any money in banks or other financial institutions?
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_901", "yes"),
                //Name of U.S. Financial Institution
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_902/0", "Bank of America"),
                //Account Type
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_903/0", "Deposit"),
                //Account Location
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_904/0", "Inside U.S.A"),
                //Date Opened
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_905/0", "2018/01/09"),
                //Total Deposited (past 12 mo.)
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_906/0", "5555"),
                //Current Balance
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_907/0", "100000")
        ]
    }

    private static List<Answer> bankDepositsSecond(long packageId, long applicantId) {
        [
                //Name of U.S. Financial Institution
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_902/1", "Euro Bank"),
                //Account Type
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_903/1", "Euro Deposit"),
                //Account Location
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_904/1", "Outside U.S.A"),
                //Date Opened
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_905/1", "2017/03/09"),
                //Total Deposited (past 12 mo.)
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_906/1", "10000"),
                //Current Balance
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_907/1", "150000")
        ]
    }

    private static List<Answer> personalValue(long packageId, long applicantId) {
        [
                //Do you have any personal property?
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_personalProperty/Q_909", "yes"),
                //Description of Item
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_personalProperty/Q_910/0", "Diamond Ring"),
                //Date Acquired
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_personalProperty/Q_911/0", "2015/09/01"),
                //Market Value
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_personalProperty/Q_912/0", "59000")
        ]
    }

    private static List<Answer> personalValueSecond(long packageId, long applicantId) {
        [
                //Description of Item
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_personalProperty/Q_910/1", "Necklace"),
                //Date Acquired
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_personalProperty/Q_911/1", "2018/06/05"),
                //Market Value
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_personalProperty/Q_912/1", "151000")
        ]
    }

    static List<Answer> realEstate(long packageId, long applicantId) {
        [
                //Do you own any real estate?
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_realEstate/Q_914", "yes"),
                //Street Number and Name
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_realEstate/Q_915/0", "1 Main street"),
                //Does your address have a secondary description (i.e. apartment, suite, or floor)?
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_realEstate/Q_916/0", "no"),
                //City/Town/Village
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_realEstate/Q_919/0", "Boston"),
                //State
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_realEstate/Q_921/0", "Massachusetts"),
                //ZIP Code
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_realEstate/Q_923/0", "95863"),
                //Acquisition Date
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_realEstate/Q_925/0", "2018/05/09"),
                //Mortgage Balance(s)
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_realEstate/Q_926/0", "25000"),
                //Market Value
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_realEstate/Q_927/0", "75000")
        ]
    }

    private static List<Answer> realEstateSecond(long packageId, long applicantId, Boolean addMortgage = Boolean.TRUE) {
        List<Answer> result = [
                //Street Number and Name
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_realEstate/Q_915/1", "1 Drive bolivar"),
                //Does your address have a secondary description (i.e. apartment, suite, or floor)?
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_realEstate/Q_916/1", "no"),
                //City/Town/Village
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_realEstate/Q_919/1", "New York City"),
                //State
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_realEstate/Q_921/1", "New York"),
                //ZIP Code
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_realEstate/Q_923/1", "12345"),
                //Acquisition Date
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_realEstate/Q_925/1", "2019/01/20"),
                //Market Value
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_realEstate/Q_927/1", "130,000")
        ]
        if (addMortgage) {
            result.addAll([
                    //Mortgage Balance(s)
                    getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_realEstate/Q_926/1", "60,000")
            ])
        }
        result
    }

    static List<Answer> realEstateDouble(long packageId, long applicantId, Boolean addMortgage = Boolean.TRUE) {
        List<Answer> result = realEstate(packageId, applicantId)
        result.addAll(realEstateSecond(packageId, applicantId, addMortgage))
        result
    }

    static List<Answer> lifeInsurance(long packageId, long applicantId) {
        [
                //Do you own any life insurance policies?
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_lifeInsurance/Q_929", "yes"),
                //What is the total sum of your life insurance?
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_lifeInsurance/Q_930", "20000"),
                //What is the cash surrender value of all your policies?
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_lifeInsurance/Q_931", "2500")
        ]
    }

    private static List<Answer> financialInstruments(long packageId, long applicantId) {
        [
                //Do you own any stocks bonds, or CDs?
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_financialInstruments/Q_933", "yes"),
                //Name of U.S. Financial Institution
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_financialInstruments/Q_934/0", "Oil Company"),
                //Date Acquired
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_financialInstruments/Q_935/0", "2015/07/01"),
                //Name of Record Owner
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_financialInstruments/Q_936/0", "Mr Smith"),
                //Type of Account
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_financialInstruments/Q_937/0", "Deposit"),
                //Denomination
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_financialInstruments/Q_938/0", "US dollar"),
                //Serial Number(s)
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_financialInstruments/Q_939/0", "1234567"),
                //Current Value
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_financialInstruments/Q_940/0", "5000")
        ]
    }

    private static List<Answer> financialInstrumentsSecond(long packageId, long applicantId) {
        [
                //Name of U.S. Financial Institution
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_financialInstruments/Q_934/1", "MacDonald's"),
                //Date Acquired
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_financialInstruments/Q_935/1", "2018/06/03"),
                //Name of Record Owner
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_financialInstruments/Q_936/1", "Mr Brown"),
                //Type of Account
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_financialInstruments/Q_937/1", "Shares"),
                //Denomination
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_financialInstruments/Q_938/1", "US dollar"),
                //Serial Number(s)
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_financialInstruments/Q_939/1", "852369"),
                //Current Value
                getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_financialInstruments/Q_940/1", "15000")
        ]
    }

    static List<Answer> beneficiarySpouse(long packageId, long applicantId) {
        List<Answer> result = beneficiaryMarried(packageId, applicantId)
        result.addAll(
                [
                        //Is [insert Beneficiary Name]'s spouse applying with the [insert Beneficiary Name]?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_currentSpouseForBeneficiary/Q_2788", "yes"),
                        //Given Name (First name)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_currentSpouseForBeneficiary/Q_2789", "Matthew"),
                        //Middle Name (Do Not Abbreviate)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_currentSpouseForBeneficiary/Q_2790", "Liam"),
                        //Family Name/Last Name/Surname
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_currentSpouseForBeneficiary/Q_2791", "Jackson"),
                        //Country of Birth
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_currentSpouseForBeneficiary/Q_2794", "United Kingdom"),
                        //Date of Birth
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_currentSpouseForBeneficiary/Q_2795", "1970/01/01"),
                        //Does [insert this spouse's name] have a USCIS ELIS Account Number?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_currentSpouseForBeneficiary/Q_2797", "yes"),
                        //What is [insert this spouse's name]'s USCIS ELIS Account Number?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_currentSpouseForBeneficiary/Q_2798", "109876543210"),
                        //Does [insert this spouse's name] have an A-Number (Alien Registration Number)?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_currentSpouseForBeneficiary/Q_2799", "yes"),
                        //What is [insert this spouse's name]'s Alien Registration Number (A-number)?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_currentSpouseForBeneficiary/Q_2800", "876543210")
                ])
        result
    }

    static List<Answer> beneficiaryChildrenSingle(long packageId, long applicantId) {
        [
                //Do you have any children?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2741", "yes"),
                //Child's Given Name (First name)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2743/0", "James"),
                //Child's Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2746/0", "Ricky"),
                //Child's Family Name/Last Name/Surname
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2749/0", "Brown"),
                //Date of Birth
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2754/0", "2019/04/01"),
                //Does [insert this child's name] have a USCIS ELIS Account Number?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2757/0", "yes"),
                //What is [insert this child's name]'s USCIS ELIS Account Number?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2758/0", "123456789012"),
                //Does this child have an A-Number (Alien Registration Number)?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2759/0", "yes"),
                //What is [insert this child's name]'s Alien Registration Number (A-number)?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2760/0", "123456789")
        ]
    }

    static List<Answer> beneficiaryChildrenDouble(long packageId, long applicantId) {
        List<Answer> result = beneficiaryChildrenSingle(packageId, applicantId)
        result.addAll([
                //Child's Given Name (First name)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2743/1", "Indira"),
                //Child's Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2746/1", "Senior"),
                //Child's Family Name/Last Name/Surname
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2749/1", "Gandi"),
                //Date of Birth
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2754/1", "2018/06/01"),
                //Does [insert this child's name] have a USCIS ELIS Account Number?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2757/1", "yes"),
                //What is [insert this child's name]'s USCIS ELIS Account Number?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2758/1", "234567890123"),
                //Does this child have an A-Number (Alien Registration Number)?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2759/1", "yes"),
                //What is [insert this child's name]'s Alien Registration Number (A-number)?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2760/1", "234567890"),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2742/1", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2752/1", ""),
                //Country of Birth
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2753/1", "United States"),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2755/1", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2762/1", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2764/1", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2765/1", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2766/1", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2769/1", "")
        ])
        result
    }

    static List<Answer> beneficiaryChildrenTriple(long packageId, long applicantId) {
        List<Answer> result = beneficiaryChildrenDouble(packageId, applicantId)
        result.addAll([
                //Child's Given Name (First name)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2743/2", "Anthony"),
                //Child's Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2746/2", "Joshua"),
                //Child's Family Name/Last Name/Surname
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2749/2", "Cook"),
                //Date of Birth
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2754/2", "2018/03/10"),
                //Does [insert this child's name] have a USCIS ELIS Account Number?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2757/2", "yes"),
                //What is [insert this child's name]'s USCIS ELIS Account Number?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2758/2", "345678901234"),
                //Does this child have an A-Number (Alien Registration Number)?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2759/2", "yes"),
                //What is [insert this child's name]'s Alien Registration Number (A-number)?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2760/2", "345678901"),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2742/2", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2752/2", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2753/2", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2755/2", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2762/2", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2764/2", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2765/2", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2766/2", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2769/2", "")
        ])
        result
    }

    static List<Answer> beneficiaryChildrenQuadro(long packageId, long applicantId) {
        List<Answer> result = beneficiaryChildrenTriple(packageId, applicantId)
        result.addAll([
                //Child's Given Name (First name)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2743/3", "Jacob"),
                //Child's Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2746/3", "Noah"),
                //Child's Family Name/Last Name/Surname
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2749/3", "Williams"),
                //Date of Birth
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2754/3", "2018/01/01"),
                //Does [insert this child's name] have a USCIS ELIS Account Number?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2757/3", "yes"),
                //What is [insert this child's name]'s USCIS ELIS Account Number?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2758/3", "456789012345"),
                //Does this child have an A-Number (Alien Registration Number)?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2759/3", "yes"),
                //What is [insert this child's name]'s Alien Registration Number (A-number)?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2760/3", "456789012"),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2742/3", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2752/3", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2753/3", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2755/3", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2762/3", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2764/3", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2765/3", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2766/3", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2769/3", "")
        ])
        result
    }

    static List<Answer> beneficiaryChildrenFive(long packageId, long applicantId) {
        List<Answer> result = beneficiaryChildrenQuadro(packageId, applicantId)
        result.addAll([
                //Child's Given Name (First name)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2743/4", "Isabella"),
                //Child's Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2746/4", "Olivia"),
                //Child's Family Name/Last Name/Surname
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2749/4", "Hill"),
                //Date of Birth
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2754/4", "2017/07/15"),
                //Does [insert this child's name] have a USCIS ELIS Account Number?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2757/4", "yes"),
                //What is [insert this child's name]'s USCIS ELIS Account Number?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2758/4", "567890123456"),
                //Does this child have an A-Number (Alien Registration Number)?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2759/4", "yes"),
                //What is [insert this child's name]'s Alien Registration Number (A-number)?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2760/4", "567890123"),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2742/4", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2752/4", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2753/4", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2755/4", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2762/4", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2764/4", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2765/4", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2766/4", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2769/4", "")
        ])
        result
    }

    static List<Answer> beneficiaryChildrenSix(long packageId, long applicantId) {
        List<Answer> result = beneficiaryChildrenFive(packageId, applicantId)
        result.addAll([
                //Child's Given Name (First name)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2743/5", "Chloe"),
                //Child's Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2746/5", "Mia"),
                //Child's Family Name/Last Name/Surname
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2749/5", "Bailey"),
                //Date of Birth
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2754/5", "2017/02/17"),
                //Does [insert this child's name] have a USCIS ELIS Account Number?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2757/5", "yes"),
                //What is [insert this child's name]'s USCIS ELIS Account Number?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2758/5", "678901234567"),
                //Does this child have an A-Number (Alien Registration Number)?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2759/5", "yes"),
                //What is [insert this child's name]'s Alien Registration Number (A-number)?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2760/5", "678901234"),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2742/5", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2752/5", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2753/5", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2755/5", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2762/5", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2764/5", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2765/5", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2766/5", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2769/5", "")
        ])
        result
    }

    static List<Answer> beneficiaryDependentChildrenSingleSpouse(long packageId, long applicantId) {
        List<Answer> result = beneficiaryChildrenSingle(packageId, applicantId)
        result.addAll(beneficiarySpouse(packageId, applicantId))
        result
    }

    static List<Answer> beneficiaryDependentChildrenQuadroSpouse(long packageId, long applicantId) {
        List<Answer> result = beneficiaryChildrenQuadro(packageId, applicantId)
        result.addAll(beneficiarySpouse(packageId, applicantId))
        result
    }

    static List<Answer> beneficiaryDependentChildrenFiveSpouse(long packageId, long applicantId) {
        List<Answer> result = beneficiaryChildrenFive(packageId, applicantId)
        result.addAll(beneficiarySpouse(packageId, applicantId))
        result
    }

    static List<Answer> beneficiaryDependentChildrenSixSpouse(long packageId, long applicantId) {
        List<Answer> result = beneficiaryChildrenSix(packageId, applicantId)
        result.addAll(beneficiarySpouse(packageId, applicantId))
        result
    }

    static List<Answer> residence18StateSingle(long packageId, long applicantId) {
        [
                //Have you ever lived in another U.S. state since your 18th birthday?
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_residedSince18/Q_6007", "yes"),
                //State
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_residedSince18/Q_66/0", "Washington")
        ]
    }

    static List<Answer> residence18StateDouble(long packageId, long applicantId) {
        List<Answer> result = residence18StateSingle(packageId, applicantId)
        result.addAll(
                [
                        //State
                        getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_residedSince18/Q_66/1", "Virginia")
                ]
        )
        result
    }

    static List<Answer> residence18StateTriple(long packageId, long applicantId) {
        List<Answer> result = residence18StateDouble(packageId, applicantId)
        result.addAll(
                [
                        //State
                        getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_residedSince18/Q_66/2", "New Jersey")
                ]
        )
        result
    }

    static List<Answer> residence18CountrySingle(long packageId, long applicantId) {
        [
                //Have you ever lived in a foreign country since your 18th birthday?
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_residedSince18/Q_6008", "yes"),
                //Country
                getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_residedSince18/Q_67/0", "France")
        ]
    }

    static List<Answer> residence18CountryDouble(long packageId, long applicantId) {
        List<Answer> result = residence18CountrySingle(packageId, applicantId)
        result.addAll(
                [
                        //Country
                        getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_residedSince18/Q_67/1", "Germany")
                ]
        )
        result
    }

    static List<Answer> residence18CountryTriple(long packageId, long applicantId) {
        List<Answer> result = residence18CountryDouble(packageId, applicantId)
        result.addAll(
                [
                        //Country
                        getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_residedSince18/Q_67/2", "Italy")
                ]
        )
        result
    }

    static List<Answer> residence18StateCountrySingle(long packageId, long applicantId) {
        List<Answer> result = residence18StateSingle(packageId, applicantId)
        result.addAll(residence18CountrySingle(packageId, applicantId))
        result
    }

    static List<Answer> residence18StateCountryDouble(long packageId, long applicantId) {
        List<Answer> result = residence18StateDouble(packageId, applicantId)
        result.addAll(residence18CountryDouble(packageId, applicantId))
        result
    }

    static List<Answer> residence18StateCountryTriple(long packageId, long applicantId) {
        List<Answer> result = residence18StateTriple(packageId, applicantId)
        result.addAll(residence18CountryTriple(packageId, applicantId))
        result
    }

    static List<Answer> residence18SingleStateDoubleCountry(long packageId, long applicantId) {
        List<Answer> result = residence18StateSingle(packageId, applicantId)
        result.addAll(residence18CountryDouble(packageId, applicantId))
        result
    }

    static List<Answer> residence18DoubleStateSingleCountry(long packageId, long applicantId) {
        List<Answer> result = residence18StateDouble(packageId, applicantId)
        result.addAll(residence18CountrySingle(packageId, applicantId))
        result
    }

    static List<Answer> residence18StateCountryTripleCriminal(long packageId, long applicantId) {
        List<Answer> result = residence18StateTriple(packageId, applicantId)
        result.addAll(residence18CountryTriple(packageId, applicantId))
        result.addAll(petitionerCriminalSingle(packageId, applicantId))
        result
    }

    static List<Answer> petitionerDependentsChildrenSingle(long packageId, long applicantId) {
        [
                //Do you have any dependents who are children?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1268", "yes"),
                //Given Name (First name)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1270/0", "James"),
                //Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1271/0", "Junior"),
                //Family Name/Last Name/Surname
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1272/0", "Green"),
                //Date of Birth
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1276/0", "2018/05/07"),
                //What is this dependent's relationship to you?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1287/0", "child"),
                //Degree of Dependency
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1288/0", "wholly_dependent"),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1269/0", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1273/0", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1274/0", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1275/0", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1277/0", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1278/0", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1279/0", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1282/0", "")
        ]
    }

    static List<Answer> petitionerDependentsChildrenCount(long packageId, long applicantId, int childrenCount) {
        return [getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_6106", "${childrenCount}")]
    }


    static List<Answer> petitionerDependentsChildrenDouble(long packageId, long applicantId) {
        List<Answer> result = petitionerDependentsChildrenSingle(packageId, applicantId)
        result.addAll(
                [
                        //Given Name (First name)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1270/1", "Peter"),
                        //Middle Name (Do Not Abbreviate)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1271/1", "Jack"),
                        //Family Name/Last Name/Surname
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1272/1", "Pen"),
                        //Date of Birth
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1276/1", "2017/09/17"),
                        //What is this dependent's relationship to you?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1287/1", "child"),
                        //Degree of Dependency
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1288/1", "wholly_dependent"),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1269/1", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1273/1", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1274/1", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1275/1", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1277/1", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1278/1", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1279/1", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1282/1", "")
                ]
        )
        result
    }

    static List<Answer> petitionerDependentsChildrenTriple(long packageId, long applicantId) {
        List<Answer> result = petitionerDependentsChildrenDouble(packageId, applicantId)
        result.addAll(
                [
                        //Given Name (First name)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1270/2", "Deb"),
                        //Middle Name (Do Not Abbreviate)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1271/2", "Senior"),
                        //Family Name/Last Name/Surname
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1272/2", "White"),
                        //Date of Birth
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1276/2", "2015/03/12"),
                        //What is this dependent's relationship to you?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1287/2", "child"),
                        //Degree of Dependency
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1288/2", "wholly_dependent"),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1269/2", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1273/2", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1274/2", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1275/2", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1277/2", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1278/2", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1279/2", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1282/2", "")
                ]
        )
        result
    }

    static List<Answer> petitionerDependentsChildrenQuadro(long packageId, long applicantId) {
        List<Answer> result = petitionerDependentsChildrenTriple(packageId, applicantId)
        result.addAll(
                [
                        //Given Name (First name)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1270/3", "Mary"),
                        //Middle Name (Do Not Abbreviate)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1271/3", "Jacklin"),
                        //Family Name/Last Name/Surname
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1272/3", "Smith"),
                        //Date of Birth
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1276/3", "2016/12/15"),
                        //What is this dependent's relationship to you?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1287/3", "child"),
                        //Degree of Dependency
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1288/3", "wholly_dependent"),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1269/3", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1273/3", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1274/3", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1275/3", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1277/3", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1278/3", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1279/3", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1282/3", "")
                ]
        )
        result
    }

    static List<Answer> petitionerDependentsChildrenSeven(long packageId, long applicantId) {
        List<Answer> result = petitionerDependentsChildrenQuadro(packageId, applicantId)
        result.addAll(
                [
                        //Given Name (First name)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1270/4", "Alexis"),
                        //Middle Name (Do Not Abbreviate)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1271/4", "Zoey"),
                        //Family Name/Last Name/Surname
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1272/4", "Price"),
                        //Date of Birth
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1276/4", "2013/08/08"),
                        //What is this dependent's relationship to you?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1287/4", "child"),
                        //Degree of Dependency
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1288/4", "wholly_dependent"),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1269/4", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1273/4", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1274/4", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1275/4", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1277/4", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1278/4", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1279/4", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1282/4", ""),
                        //Given Name (First name)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1270/5", "Michael"),
                        //Middle Name (Do Not Abbreviate)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1271/5", "Ethan"),
                        //Family Name/Last Name/Surname
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1272/5", "Bennett"),
                        //Date of Birth
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1276/5", "2012/09/09"),
                        //What is this dependent's relationship to you?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1287/5", "child"),
                        //Degree of Dependency
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1288/5", "wholly_dependent"),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1269/5", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1273/5", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1274/5", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1275/5", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1277/5", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1278/5", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1279/5", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1282/5", ""),
                        //Given Name (First name)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1270/6", "Sofia"),
                        //Middle Name (Do Not Abbreviate)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1271/6", "Brooklyn"),
                        //Family Name/Last Name/Surname
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1272/6", "Wood"),
                        //Date of Birth
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1276/6", "2011/10/10"),
                        //What is this dependent's relationship to you?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1287/6", "child"),
                        //Degree of Dependency
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1288/6", "wholly_dependent"),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1269/6", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1273/6", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1274/6", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1275/6", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1277/6", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1278/6", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1279/6", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1282/6", "")
                ]
        )
        result
    }

    static List<Answer> petitionerDependentsNonChildrenSingle(long packageId, long applicantId) {
        [
                //Do you have any dependents who are children?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1290", "yes"),
                //Given Name (First name)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1292/0", "Andrew"),
                //Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1293/0", "New"),
                //Family Name/Last Name/Surname
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1294/0", "Black"),
                //Date of Birth
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1299/0", "2000/04/16"),
                //What is this dependent's relationship to you?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1295/0", "cousin"),
                //Degree of Dependency
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1291/0", "partially_dependent"),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1296/0", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1297/0", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1298/0", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1300/0", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1301/0", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1302/0", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1305/0", "")
        ]
    }

    static List<Answer> petitionerDependentsNonChildrenDouble(long packageId, long applicantId) {
        List<Answer> result = petitionerDependentsNonChildrenSingle(packageId, applicantId)
        result.addAll(
                [
                        //Given Name (First name)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1292/1", "Judith"),
                        //Middle Name (Do Not Abbreviate)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1293/1", "Sister"),
                        //Family Name/Last Name/Surname
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1294/1", "Rock"),
                        //Date of Birth
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1299/1", "2015/08/02"),
                        //What is this dependent's relationship to you?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1295/1", "niece"),
                        //Degree of Dependency
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1291/1", "partially_dependent"),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1296/1", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1297/1", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1298/1", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1300/1", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1301/1", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1302/1", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1305/1", "")
                ]
        )
        result
    }

    static List<Answer> petitionerDependentsNonChildrenTriple(long packageId, long applicantId) {
        List<Answer> result = petitionerDependentsNonChildrenDouble(packageId, applicantId)
        result.addAll(
                [
                        //Given Name (First name)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1292/2", "Bob"),
                        //Middle Name (Do Not Abbreviate)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1293/2", "Singer"),
                        //Family Name/Last Name/Surname
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1294/2", "Marley"),
                        //Date of Birth
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1299/2", "1965/09/17"),
                        //What is this dependent's relationship to you?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1295/2", "uncle"),
                        //Degree of Dependency
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1291/2", "partially_dependent"),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1296/2", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1297/2", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1298/2", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1300/2", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1301/2", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1302/2", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1305/2", "")
                ]
        )
        result
    }

    static List<Answer> petitionerDependentsNonChildrenQuadro(long packageId, long applicantId) {
        List<Answer> result = petitionerDependentsNonChildrenTriple(packageId, applicantId)
        result.addAll(
                [
                        //Given Name (First name)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1292/3", "Willy"),
                        //Middle Name (Do Not Abbreviate)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1293/3", "Grand"),
                        //Family Name/Last Name/Surname
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1294/3", "Bigfoot"),
                        //Date of Birth
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1299/3", "1933/04/22"),
                        //What is this dependent's relationship to you?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1295/3", "grandfather"),
                        //Degree of Dependency
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1291/3", "partially_dependent"),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1296/3", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1297/3", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1298/3", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1300/3", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1301/3", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1302/3", ""),
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1305/3", "")
                ]
        )
        result
    }

    static List<Answer> petitionerDependentsChildrenNonChildrenSingle(long packageId, long applicantId) {
        List<Answer> result = petitionerDependentsChildrenSingle(packageId, applicantId)
        result.addAll(petitionerDependentsNonChildrenSingle(packageId, applicantId))
        result
    }

    static List<Answer> petitionerDependentsChildrenTripleNonChildrenSingle(long packageId, long applicantId) {
        List<Answer> result = petitionerDependentsChildrenTriple(packageId, applicantId)
        result.addAll(petitionerDependentsNonChildrenSingle(packageId, applicantId))
        result
    }

    static List<Answer> petitionerDependentsChildrenQuadroNonChildrenSingle(long packageId, long applicantId) {
        List<Answer> result = petitionerDependentsChildrenQuadro(packageId, applicantId)
        result.addAll(petitionerDependentsNonChildrenSingle(packageId, applicantId))
        result
    }

    static List<Answer> petitionerDependentsChildrenSevenNonChildrenSingle(long packageId, long applicantId) {
        List<Answer> result = petitionerDependentsChildrenSeven(packageId, applicantId)
        result.addAll(petitionerDependentsNonChildrenSingle(packageId, applicantId))
        result
    }

    static List<Answer> petitionerCurrentSpouse(long packageId, long applicantId, String firstName = "Teresa",
                                                String middleName = "Ane", String lastName = "Pink") {
        List<Answer> result = petitionerMarried(packageId, applicantId)
        result.addAll(
                [
                        //Given Name (First name)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1206", firstName),
                        //Middle Name (Do Not Abbreviate)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1207", middleName),
                        //Family Name/Last Name/Surname
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1208", lastName),
                        //Were you married previously?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1220", "yes")
                ])
        result
    }

    static List<Answer> petitionerPreviousSpouse(long packageId, long applicantId) {
        [
                //Given Name (First name)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1222/0", "Addison"),
                //Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1223/0", "Ella"),
                //Family Name/Last Name/Surname
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1224/0", "Perez"),
                //Date Marriage Ended
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1228/0", "2018/01/01"),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1225/0", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1227/0", "")
        ]
    }

    static List<Answer> petitionerPreviousSpouseDouble(long packageId, long applicantId) {
        List<Answer> result = petitionerPreviousSpouse(packageId, applicantId)
        result.addAll([
                //Given Name (First name)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1222/1", "Lily"),
                //Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1223/1", "Avery"),
                //Family Name/Last Name/Surname
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1224/1", "Murphy"),
                //Date Marriage Ended
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1228/1", "2017/01/01"),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1225/1", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1227/1", "")
        ])
        result
    }

    static List<Answer> petitionerPreviousSpouseTriple(long packageId, long applicantId) {
        List<Answer> result = petitionerPreviousSpouseDouble(packageId, applicantId)
        result.addAll([
                //Given Name (First name)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1222/2", "Aubrey"),
                //Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1223/2", "Victoria"),
                //Family Name/Last Name/Surname
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1224/2", "Harris"),
                //Date Marriage Ended
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1228/2", "2016/01/01"),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1225/2", ""),
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1227/2", "")
        ])
        result
    }

    static List<Answer> petitionerDivorced(long packageId, long applicantId) {
        petitionerMaritalStatus(packageId, applicantId, 'divorced')
    }

    static List<Answer> petitionerMarried(long packageId, long applicantId) {
        petitionerMaritalStatus(packageId, applicantId, 'married')
    }

    static List<Answer> petitionerSingle(long packageId, long applicantId) {
        petitionerMaritalStatus(packageId, applicantId, 'single_never_married')
    }

    static List<Answer> petitionerWidowed(long packageId, long applicantId) {
        petitionerMaritalStatus(packageId, applicantId, 'widowed')
    }

    static List<Answer> petitionerMarriageAnnulled(long packageId, long applicantId) {
        petitionerMaritalStatus(packageId, applicantId, 'marriage_annulled')
    }

    static List<Answer> petitionerMaritalStatus(long packageId, long applicantId, String maritalStatus) {
        [
                //What is your current marital status?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1204", maritalStatus)
        ]
    }

    static List<Answer> beneficiaryDivorced(long packageId, long applicantId) {
        beneficiaryMaritalStatus(packageId, applicantId, 'divorced')
    }

    static List<Answer> beneficiaryMarried(long packageId, long applicantId) {
        beneficiaryMaritalStatus(packageId, applicantId, 'married')
    }

    static List<Answer> beneficiaryMaritalStatus(long packageId, long applicantId, String maritalStatus) {
        [
                //What is your current marital status?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2781", maritalStatus)
        ]
    }

    static List<Answer> petitionerDivorcedPreviousSpouse(long packageId, long applicantId) {
        List<Answer> result = petitionerDivorced(packageId, applicantId)
        result.addAll(petitionerPreviousSpouse(packageId, applicantId))
        result
    }

    static List<Answer> petitionerDivorcedDoublePreviousSpouse(long packageId, long applicantId) {
        List<Answer> result = petitionerDivorced(packageId, applicantId)
        result.addAll(petitionerPreviousSpouseDouble(packageId, applicantId))
        result
    }

    static List<Answer> petitionerDivorcedTriplePreviousSpouse(long packageId, long applicantId) {
        List<Answer> result = petitionerDivorced(packageId, applicantId)
        result.addAll(petitionerPreviousSpouseTriple(packageId, applicantId))
        result
    }

    static List<Answer> petitionerMarriedPreviousSpouse(long packageId, long applicantId) {
        List<Answer> result = petitionerCurrentSpouse(packageId, applicantId)
        result.addAll(petitionerPreviousSpouse(packageId, applicantId))
        result
    }

    static List<Answer> petitionerMarriedDoublePreviousSpouse(long packageId, long applicantId) {
        List<Answer> result = petitionerCurrentSpouse(packageId, applicantId)
        result.addAll(petitionerPreviousSpouseDouble(packageId, applicantId))
        result
    }

    static List<Answer> beneficiaryPreviousSpouse(long packageId, long applicantId) {
        [
                //Do you have any previous marriages?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2783", "yes"),
                //Given Name (First name)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_priorSpousesForBeneficiary/Q_2840/0", "Alexander"),
                //Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_priorSpousesForBeneficiary/Q_2841/0", "Daniel"),
                //Family Name/Last Name/Surname
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_priorSpousesForBeneficiary/Q_2842/0", "Young"),
                //Date Marriage Ended
                getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_priorSpousesForBeneficiary/Q_2851/0", "2018/05/01")
        ]
    }

    static List<Answer> beneficiaryDivorcedSingleChildrenPreviousSpouse(long packageId, long applicantId) {
        List<Answer> result = beneficiaryDivorced(packageId, applicantId)
        result.addAll(beneficiaryChildrenSingle(packageId, applicantId))
        result.addAll(beneficiaryPreviousSpouse(packageId, applicantId))
        result
    }

    static List<Answer> beneficiaryMarriedSingleChildren(long packageId, long applicantId) {
        List<Answer> result = beneficiarySpouse(packageId, applicantId)
        result.addAll(beneficiaryChildrenSingle(packageId, applicantId))
        result
    }

    static List<Answer> beneficiaryMarriedDoubleChildrenPreviousSpouse(long packageId, long applicantId) {
        List<Answer> result = beneficiarySpouse(packageId, applicantId)
        result.addAll(beneficiaryChildrenDouble(packageId, applicantId))
        result.addAll(beneficiaryPreviousSpouse(packageId, applicantId))
        result
    }

    static List<Answer> petitionerLegalNameLong(long packageId, long applicantId) {
        [
                //Given Name (First name)
                getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_5/Q_32", "Andrew Andrew Andrew Andrew Andrew"),
                //Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_5/Q_33", "James James James James James"),
                //Family Name/Last Name/Surname
                getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_5/Q_34", "White White White White White")
        ]
    }

    static List<Answer> petitionerLegalName(long packageId, long applicantId) {
        List<Answer> result = petitionerLegalNameNoMiddle(packageId, applicantId)
        result.addAll(
                [
                        //Middle Name (Do Not Abbreviate)
                        getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_5/Q_33", "James")
                ]
        )
        result
    }

    static List<Answer> petitionerLegalNameNoMiddle(long packageId, long applicantId) {
        [
                //Given Name (First name)
                getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_5/Q_32", "Andrew"),
                //Family Name/Last Name/Surname
                getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_5/Q_34", "White")
        ]
    }

    static List<Answer> petitionerBirth(long packageId, long applicantId) {
        [
                //Date of Birth
                getAnswerInstance(packageId, applicantId, "Sec_birthInformation/SubSec_birthInformation/Q_88", "1975-11-27"),
                //Country of Birth
                getAnswerInstance(packageId, applicantId, "Sec_birthInformation/SubSec_birthInformation/Q_89", "Barbados")
        ]
    }

    static List<Answer> petitionerLegalNameBirth(long packageId, long applicantId) {
        List<Answer> result = petitionerLegalName(packageId, applicantId)
        result.addAll(petitionerBirth(packageId, applicantId))
        result
    }

    private static List<Answer> petitionerRelationshipToBeneficiary(long packageId, long applicantId, String beneficiaryRelationship) {
        [
                //How is the Beneficiary related to you?
                getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_4/Q_27", beneficiaryRelationship),
        ]
    }

    static List<Answer> petitionerLegalNameFianceToBeneficiary(long packageId, long applicantId) {
        List<Answer> result = petitionerLegalNameBirth(packageId, applicantId)
        result.addAll(petitionerRelationshipToBeneficiary(packageId, applicantId, 'fiance'))
        result
    }

    static List<Answer> petitionerLegalNameSpouseToBeneficiary(long packageId, long applicantId) {
        List<Answer> result = petitionerLegalNameBirth(packageId, applicantId)
        result.addAll(petitionerRelationshipToBeneficiary(packageId, applicantId, 'spouse'))
        result
    }

    static List<Answer> petitionerLegalNameSiblingToBeneficiaryWithSpouse(long packageId, long petitionerApplicantId, long beneficiaryApplicantId) {
        List<Answer> result = petitionerLegalNameSiblingToBeneficiaryWithoutSpouse(packageId, petitionerApplicantId)
        result.addAll(beneficiarySpouse(packageId, beneficiaryApplicantId))
        result
    }

    static List<Answer> petitionerLegalNameSiblingToBeneficiaryWithoutSpouse(long packageId, long applicantId) {
        List<Answer> result = petitionerLegalNameBirth(packageId, applicantId)
        result.addAll(petitionerRelationshipToBeneficiary(packageId, applicantId, 'sibling'))
        result
    }

    static List<Answer> petitionerLegalNameParentToBeneficiaryWithSpouse(long packageId, long petitionerApplicantId, long beneficiaryApplicantId) {
        List<Answer> result = petitionerLegalNameParentToBeneficiaryWithoutSpouse(packageId, petitionerApplicantId)
        result.addAll(beneficiarySpouse(packageId, beneficiaryApplicantId))
        result
    }

    static List<Answer> petitionerLegalNameParentToBeneficiaryWithoutSpouse(long packageId, long applicantId) {
        List<Answer> result = petitionerLegalNameBirth(packageId, applicantId)
        result.addAll(petitionerRelationshipToBeneficiary(packageId, applicantId, 'parent'))
        result
    }

    static List<Answer> petitionerLegalNameMarried(long packageId, long applicantId) {
        List<Answer> result = petitionerLegalName(packageId, applicantId)
        result.addAll(petitionerMarried(packageId, applicantId))
        result
    }

    static List<Answer> beneficiaryLegalName(long packageId, long applicantId) {
        [
                //Given Name (First name)
                getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_currentLegalNameForBeneficiary/Q_1901", "Avery"),
                //Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_currentLegalNameForBeneficiary/Q_1902", "Victoria"),
                //Family Name/Last Name/Surname
                getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_currentLegalNameForBeneficiary/Q_1903", "Martin")
        ]
    }

    static List<Answer> beneficiaryBirth(long packageId, long applicantId) {
        [
                //Date of Birth
                getAnswerInstance(packageId, applicantId, "Sec_birthInformationForBeneficiary/SubSec_birthInformationForBeneficiary/Q_2202", "1985-07-01")
        ]
    }

    static List<Answer> beneficiaryPersonal(long packageId, long applicantId) {
        [
                //Alien Registration Number (A-Number)
                getAnswerInstance(packageId, applicantId, "Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2412", "569874123")
        ]
    }

    static List<Answer> beneficiaryInadmissibility(long packageId, long applicantId) {
        [
                //What is the USCIS Receipt Number for your Form I-130 (Petitioner for Alien Relative)?
                getAnswerInstance(packageId, applicantId, "Sec_inadmissibilityAndOtherLegalIssues/SubSec_inadmissibilityAndOtherLegalIssues/Q_3020", "8523697410"),
                //What is the priority date for your Form I-130 (Petitioner for Alien Relative)?
                getAnswerInstance(packageId, applicantId, "Sec_inadmissibilityAndOtherLegalIssues/SubSec_inadmissibilityAndOtherLegalIssues/Q_3021", "2015-06-25")
        ]
    }

    static List<Answer> beneficiaryDirectDerivative485(long packageId, long applicantId) {
        List<Answer> result = beneficiaryLegalName(packageId, applicantId)
        result.addAll(beneficiaryBirth(packageId, applicantId))
        result.addAll(beneficiaryPersonal(packageId, applicantId))
        result.addAll(beneficiaryInadmissibility(packageId, applicantId))
        result
    }

    static List<Answer> beneficiaryLegalNameMarried(long packageId, long applicantId) {
        List<Answer> result = beneficiaryLegalName(packageId, applicantId)
        result.addAll(beneficiaryMarried(packageId, applicantId))
        result
    }

    static List<Answer> beneficiaryLegalNameDivorced(long packageId, long applicantId) {
        List<Answer> result = beneficiaryLegalName(packageId, applicantId)
        result.addAll(beneficiaryDivorced(packageId, applicantId))
        result
    }

    static List<Answer> singleTax(long packageId, long applicantId) {
        [
                //Select the most recent year that you filed federal income taxes
                getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_133", "2018"),
                //What is your total annual income for the most recent tax year?
                getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_134", "150000")
        ]
    }

    private static List<Answer> secondThirdTaxes(long packageId, long applicantId) {
        [
                //Select the second most recent year that you filed federal income taxes
                getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_137", "2017"),
                //What is your total annual income for the 2nd most recent tax year?
                getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_138", "130000"),
                //Select the third most recent year that you filed federal income taxes
                getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_141", "2016"),
                //What is your total annual income for the 3rd most recent tax year?
                getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_142", "110000")
        ]
    }

    static List<Answer> tripleTax(long packageId, long applicantId) {
        List<Answer> result = singleTax(packageId, applicantId)
        result.addAll(secondThirdTaxes(packageId, applicantId))
        result
    }

    static List<Answer> singleTaxExplanation(long packageId, long applicantId) {
        List<Answer> result = singleTax(packageId, applicantId)
        result.addAll([
                //For this most recent tax year, were you exempted from filing a Federal income tax return because your income was below the IRS required level?
                getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_135", "yes"),
                //Please explain why you were not required to file a Federal Income Tax return for this most recent tax year:
                getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_136", "2018 was not required to fill in explanation")
        ])
        result
    }

    static List<Answer> tripleTaxExplanation(long packageId, long applicantId) {
        List<Answer> result = singleTaxExplanation(packageId, applicantId)
        result.addAll(secondThirdTaxes(packageId, applicantId))
        result.addAll([
                //For this second most recent tax year, were you exempted from filing a Federal income tax return because your income was below the IRS required
                getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_139", "yes"),
                //Please explain why you were not required to file a Federal Income Tax return for the second most recent tax year:
                getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_140", "2017 was not required to fill in explanation"),
                //For this third most recent tax year, were you exempted from filing a Federal income tax return because your income was below the IRS required
                getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_143", "yes"),
                //Please explain why you were not required to file a Federal Income Tax return for the third most recent tax year:
                getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_144", "2016 was not required to fill in explanation")
        ])
        result
    }

    static List<Answer> petitionerUnemploymentFirst(long packageId, long applicantId) {
        [
                //What is your current employment status?
                getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1008/0", "unemployed"),
                //What was the date you became unemployed?
                getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1009/0", DateUtil.fromDate(DateUtil.today().minusYears(1)))
        ]
    }

    static List<Answer> petitionerUnemploymentRetire(long packageId, long applicantId) {
        List<Answer> result = petitionerUnemploymentFirst(packageId, applicantId)
        result.addAll(petitionerRetiredSecond(packageId, applicantId))
        result
    }

    private static List<Answer> petitionerRetiredSecond(long packageId, long applicantId) {
        [
                //What is your current employment status?
                getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1008/1", "retired"),
                //What was the date of your retirement?
                getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1011/1", DateUtil.fromDate(DateUtil.today().minusYears(2))),
                //What was the last date of your retirement?
                getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1012/1", DateUtil.fromDate(DateUtil.today().minusYears(3)))
        ]
    }

    static List<Answer> petitionerUnemploymentFirstEmployedSecond(long packageId, long applicantId) {
        List<Answer> result = petitionerUnemploymentFirst(packageId, applicantId)
        result.addAll(petitionerEmployerSecond(packageId, applicantId, false))
        result
    }

    static List<Answer> petitionerEmploymentFirstAndRetired(long packageId, long applicantId) {
        List<Answer> result = petitionerEmploymentFirst(packageId, applicantId)
        result.addAll(petitionerRetiredSecond(packageId, applicantId))
        result
    }

    private static List<Answer> petitionerEmploymentFirst(long packageId, long applicantId, Boolean selfEmployed = false) {
        [
                //What is your current employment status?
                getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1008/0", "employed"),
                //Are you self-employed?
                getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1013/0", getSelfEmployedAnswer(selfEmployed)),
                //What date did you begin working for this employer?
                getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1014/0", DateUtil.fromDate(DateUtil.today().minusYears(1))),
                //What is the full name of this employer?
                getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1019/0", 'Google'),
                //What is your occupation at this employer?
                getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1029/0", "IT specialist")
        ]
    }

    private static String getSelfEmployedAnswer(boolean selfEmployed) {
        if (selfEmployed) {
            return 'yes'
        }
        'no'
    }

    static List<Answer> petitionerEmploymentDouble(long packageId, long applicantId, Boolean firstSelf = false, Boolean secondSelf = false) {
        List<Answer> result = petitionerEmploymentFirst(packageId, applicantId, firstSelf)
        result.addAll(petitionerEmployerSecond(packageId, applicantId, secondSelf))
        result
    }
    static List<Answer> petitionerEmploymentRetireAndEmployed(long packageId, long applicantId) {
        List<Answer> result = petitionerUnemploymentFirst(packageId, applicantId)
        result.addAll(petitionerEmployerSecond(packageId, applicantId, false))
        result
    }

    private static List<Answer> petitionerEmployerSecond(long packageId, long applicantId, boolean secondSelf) {
        [
                //What is your current employment status?
                getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1008/1", "employed"),
                //Are you self-employed?
                getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1013/1", getSelfEmployedAnswer(secondSelf)),
                //What date did you begin working for this employer?
                getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1014/1", DateUtil.fromDate(DateUtil.today().minusYears(2))),
                //What is the full name of this employer?
                getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1019/1", 'Amazon'),
                //What is your occupation at this employer?
                getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1029/1", "Software Engineer")
        ]
    }

    static List<Answer> beneficiaryIntro(long packageId, long applicantId) {
        [
                //What is your relationship to the Petitioner (the U.S. Citizen or LPR (Lawful Permanent Resident)) who is filing to bring you to the United States?
                getAnswerInstance(packageId, applicantId, "Sec_introQuestionsForBeneficiary/SubSec_introQuestionsForBeneficiary/Q_1615", "Spouse")
        ]
    }

    static List<Answer> petitionerFianceToBeneficiaryIntro(long packageId, long petitionerApplicantId, long beneficiaryApplicantId) {
        List<Answer> result = petitionerLegalNameFianceToBeneficiary(packageId, petitionerApplicantId)
        result.addAll(beneficiaryIntro(packageId, beneficiaryApplicantId))
        result
    }

    static List<Answer> petitionerSpouseToBeneficiaryIntro(long packageId, long petitionerApplicantId, long beneficiaryApplicantId) {
        List<Answer> result = petitionerLegalNameSpouseToBeneficiary(packageId, petitionerApplicantId)
        result.addAll(beneficiaryIntro(packageId, beneficiaryApplicantId))
        result
    }

    static List<Answer> petitionerSiblingToBeneficiaryIntro(long packageId, long petitionerApplicantId, long beneficiaryApplicantId) {
        List<Answer> result = petitionerLegalNameSiblingToBeneficiaryWithoutSpouse(packageId, petitionerApplicantId)
        result.addAll(beneficiaryIntro(packageId, beneficiaryApplicantId))
        result
    }

    static List<Answer> petitionerParentToBeneficiaryIntro(long packageId, long petitionerApplicantId, long beneficiaryApplicantId) {
        List<Answer> result = petitionerLegalNameParentToBeneficiaryWithoutSpouse(packageId, petitionerApplicantId)
        result.addAll(beneficiaryIntro(packageId, beneficiaryApplicantId))
        result
    }

    static List<Answer> petitionerSponsored(long packageId, long applicantId) {
        [
                //How many lawful permanent residents whom you are currently obligated to support based on your
                // previous submission of Form I-864 as a petitioning, substitute, or joint sponsor, or Form I-864EZ,
                // Affidavit of Support Under Section 213A of the INA, as a petitioning sponsor?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdSizeDependents/Q_1311", "2")
        ]
    }

    static List<Answer> petitionerHouseholdSingle(long packageId, long applicantId) {
        List<Answer> result = petitionerHouseholdSingleBankSavingsOnly(packageId, applicantId)
        result.addAll(petitionerHouseholdSingleRealEstate(packageId, applicantId))
        result.addAll(petitionerHouseholdSingleStocks(packageId, applicantId))
        result
    }

    static List<Answer> petitionerHouseholdSingleBankSavingsOnly(long packageId, long applicantId) {
        List<Answer> result = petitionerHouseholdSingleNoAssets(packageId, applicantId)
        result.addAll(petitionerHouseholdSingleBankSavings(packageId, applicantId))
        result
    }

    static List<Answer> petitionerHouseholdSingleRealEstateOnly(long packageId, long applicantId) {
        List<Answer> result = petitionerHouseholdSingleNoAssets(packageId, applicantId)
        result.addAll(petitionerHouseholdSingleRealEstate(packageId, applicantId))
        result
    }

    static List<Answer> petitionerHouseholdSingleStocksOnly(long packageId, long applicantId) {
        List<Answer> result = petitionerHouseholdSingleNoAssets(packageId, applicantId)
        result.addAll(petitionerHouseholdSingleStocks(packageId, applicantId))
        result
    }

    static List<Answer> petitionerHouseholdSingleWithOutDependents(long packageId, long applicantId) {
        List<Answer> result = petitionerHouseholdSingleBankSavingsOnlyWithOutDependents(packageId, applicantId)
        result.addAll(petitionerHouseholdSingleRealEstate(packageId, applicantId))
        result.addAll(petitionerHouseholdSingleStocks(packageId, applicantId))
        result
    }

    static List<Answer> petitionerHouseholdSingleBankSavingsOnlyWithOutDependents(long packageId, long applicantId) {
        List<Answer> result = petitionerHouseholdSingleNoAssets(packageId, applicantId, 'Intending Immigrant WITHOUT Dependents')
        result.addAll(petitionerHouseholdSingleBankSavings(packageId, applicantId))
        result
    }

    static List<Answer> petitionerHouseholdSingleRealEstateOnlyWithOutDependents(long packageId, long applicantId) {
        List<Answer> result = petitionerHouseholdSingleNoAssets(packageId, applicantId, 'Intending Immigrant WITHOUT Dependents')
        result.addAll(petitionerHouseholdSingleRealEstate(packageId, applicantId))
        result
    }

    static List<Answer> petitionerHouseholdSingleStocksOnlyWithOutDependents(long packageId, long applicantId) {
        List<Answer> result = petitionerHouseholdSingleNoAssets(packageId, applicantId, 'Intending Immigrant WITHOUT Dependents')
        result.addAll(petitionerHouseholdSingleStocks(packageId, applicantId))
        result
    }

    static List<Answer> petitionerHouseholdSingleNoMiddleNoAssets(long packageId, long applicantId) {
        petitionerHouseholdSingleNoAssets(packageId, applicantId, 'Parent', false)
    }

    static List<Answer> petitionerHouseholdSingleWithOutDependentsNoMiddleNoAssets(long packageId, long applicantId) {
        petitionerHouseholdSingleNoAssets(packageId, applicantId, 'Intending Immigrant WITHOUT Dependents', false)
    }

    private static List<Answer> petitionerHouseholdSingleNoAssets(long packageId, long applicantId, String relationship = 'Parent', Boolean middle = true) {
        List<Answer> result = [
                //Do you have any siblings, parents, or adult children living in your same principal residence who
                // will be combing their income with yours to support the Beneficiaries in this application, when they come to the United States?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdSizeDependents/Q_1314", "yes"),
                //Do  you have any siblings, parents, or adult children (living in your same residence) who will be
                // combing their income/assets to assist in supporting the beneficiary/beneficiaries in this application?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1316", "yes"),
                //Given Name (First name)
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1317/0", "Michael"),
                //Family Name/Last Name/Surname
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1319/0", "Davis"),
                //Relationship to you
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1320/0", relationship),
                //Annual Income
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1321/0", "125,000")
        ]
        if (middle) {
            result.addAll([
                    //Middle Name (Do Not Abbreviate)
                    getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1318/0", "Mason")
            ])
        }
        result
    }

    private static List<Answer> petitionerHouseholdSingleBankSavings(long packageId, long applicantId) {
        [
                //What is the total money being held  in banks or other financial institutions (i.e. checking and savings accounts) by this person?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_6016/0", "3,000")
        ]
    }

    private static List<Answer> petitionerHouseholdSingleRealEstate(long packageId, long applicantId) {
        [
                //What is the total value of all this person's real estate properties?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_6017/0", "50,000")
        ]
    }

    private static List<Answer> petitionerHouseholdSingleStocks(long packageId, long applicantId) {
        [
                //What is the total value of all of this person's own any stocks bonds, or CDs?
                getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_6018/0", "1,000")
        ]
    }

    static List<Answer> petitionerHouseholdDouble(long packageId, long applicantId) {
        List<Answer> result = petitionerHouseholdSingle(packageId, applicantId)
        result.addAll(
                [
                        //Given Name (First name)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1317/1", "Olivia"),
                        //Middle Name (Do Not Abbreviate)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1318/1", "Ella"),
                        //Family Name/Last Name/Surname
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1319/1", "Reed"),
                        //Relationship to you
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1320/1", "Brother/Sister"),
                        //Annual Income
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1321/1", "150,000"),
                        //What is the total money being held  in banks or other financial institutions (i.e. checking and savings accounts) by this person?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_6016/1", "5,000"),
                        //What is the total value of all this person's real estate properties?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_6017/1", "100,000"),
                        //What is the total value of all of this person's own any stocks bonds, or CDs?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_6018/1", "3,000")
                ])
        result
    }

    static List<Answer> petitionerHouseholdTriple(long packageId, long applicantId) {
        List<Answer> result = petitionerHouseholdDouble(packageId, applicantId)
        result.addAll(
                [
                        //Given Name (First name)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1317/2", "Alexander"),
                        //Middle Name (Do Not Abbreviate)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1318/2", "Daniel"),
                        //Family Name/Last Name/Surname
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1319/2", "Rogers"),
                        //Relationship to you
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1320/2", "Intending Immigrant WITHOUT Dependents"),
                        //Annual Income
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1321/2", "100,000"),
                        //What is the total money being held  in banks or other financial institutions (i.e. checking and savings accounts) by this person?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_6016/2", "1,000"),
                        //What is the total value of all this person's real estate properties?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_6017/2", "10,000"),
                        //What is the total value of all of this person's own any stocks bonds, or CDs?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_6018/2", "2,000")
                ])
        result
    }

    static List<Answer> petitionerHouseholdQuadro(long packageId, long applicantId) {
        List<Answer> result = petitionerHouseholdTriple(packageId, applicantId)
        result.addAll(
                [
                        //Given Name (First name)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1317/3", "Grace"),
                        //Middle Name (Do Not Abbreviate)
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1318/3", "Samantha"),
                        //Family Name/Last Name/Surname
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1319/3", "Butler "),
                        //Relationship to you
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1320/3", "Adult Child"),
                        //Annual Income
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1321/3", "70,000"),
                        //What is the total money being held  in banks or other financial institutions (i.e. checking and savings accounts) by this person?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_6016/3", "3,000"),
                        //What is the total value of all this person's real estate properties?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_6017/3", "15,000"),
                        //What is the total value of all of this person's own any stocks bonds, or CDs?
                        getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_6018/3", "1,500")
                ])
        result
    }

    static List<Answer> petitionerIncome(long packageId, long applicantId) {
        [
                //What is your current individual annual income?
                getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_132", "150,000"),
        ]
    }

    static List<Answer> peopleCalc864(long packageId, long petitionerApplicantId, long benApplicantId, String firstName = "Teresa",
                                      String middleName = "Ane", String lastName = "Pink") {
        List<Answer> result = peopleCalc864NotMarriedNoExtraSponsors(packageId, petitionerApplicantId, benApplicantId)
        result.addAll(petitionerMarried(packageId, petitionerApplicantId))
        result.addAll(petitionerCurrentSpouse(packageId, petitionerApplicantId, firstName, middleName, lastName))
        result.addAll(petitionerHouseholdQuadro(packageId, petitionerApplicantId))
        result.addAll(petitionerIncome(packageId, petitionerApplicantId))
        result.addAll(assetSingleValues(packageId, petitionerApplicantId))
        result
    }

    static List<Answer> peopleCalc864NotMarriedNoExtraSponsors(long packageId, long petitionerApplicantId, long benApplicantId) {
        List<Answer> result = beneficiaryChildrenSingle(packageId, benApplicantId)
        result.addAll(beneficiarySpouse(packageId, benApplicantId))
        result.addAll(petitionerLegalName(packageId, petitionerApplicantId))
        result.addAll(petitionerDependentsChildrenDouble(packageId, petitionerApplicantId))
        result.addAll(petitionerDependentsChildrenCount(packageId, petitionerApplicantId, 2))
        result.addAll(petitionerDependentsNonChildrenSingle(packageId, petitionerApplicantId))
        result.addAll(petitionerSponsored(packageId, petitionerApplicantId))
        result
    }

    static List<Answer> beneficiaryTravelToUs(long packageId, long applicantId, Boolean addCity = Boolean.TRUE) {
        List<Answer> result = [
                //Have you ever been in the U.S?
                getAnswerInstance(packageId, applicantId, "Sec_travelToTheUnitedStates/SubSec_previousVisitsToTheUnitedStates/Q_2993", "yes"),
                //On your most recent visit to the United States, through which State did you last enter the country?
                getAnswerInstance(packageId, applicantId, "Sec_travelToTheUnitedStates/SubSec_previousVisitsToTheUnitedStates/Q_2999/0", "California"),
        ]
        if (addCity) {
            result.addAll([
                    //On your most recent visit to the United States, through which City did you last enter the country?
                    getAnswerInstance(packageId, applicantId, "Sec_travelToTheUnitedStates/SubSec_previousVisitsToTheUnitedStates/Q_2998/0", "Las Vegas"),
            ])
        }
        result
    }

    static List<Answer> beneficiaryLastEntryAdmitted(long packageId, long applicantId) {
        beneficiaryLastEntry(packageId, applicantId, "inspected_and_admitted")
    }

    static List<Answer> beneficiaryLastEntryParoled(long packageId, long applicantId) {
        beneficiaryLastEntry(packageId, applicantId, "inspected_and_paroled")
    }

    static List<Answer> beneficiaryLastEntryNotAdmittedNotParoled(long packageId, long applicantId) {
        beneficiaryLastEntry(packageId, applicantId, "entered_without_admission_or_parole", Boolean.FALSE)
    }

    static List<Answer> beneficiaryLastEntryOther(long packageId, long applicantId) {
        beneficiaryLastEntry(packageId, applicantId, "other")
    }

    private static List<Answer> beneficiaryLastEntry(long packageId, long applicantId, String describeEntry, Boolean addDetails = Boolean.TRUE) {
        List<Answer> result = [
                //Have you ever been in the U.S?
                getAnswerInstance(packageId, applicantId, "Sec_travelToTheUnitedStates/SubSec_previousVisitsToTheUnitedStates/Q_2993", "yes"),
                //Please select the option that best describes your last entry into the United States:
                getAnswerInstance(packageId, applicantId, "Sec_travelToTheUnitedStates/SubSec_lastEntryIntoTheUnitedStates/Q_2984", describeEntry),
        ]
        if (addDetails) {
            result.addAll([
                    //Please describe the entry:
                    getAnswerInstance(packageId, applicantId, "Sec_travelToTheUnitedStates/SubSec_lastEntryIntoTheUnitedStates/Q_2985", "I was entered to the US successfully"),
            ])
        }
        result
    }


}
