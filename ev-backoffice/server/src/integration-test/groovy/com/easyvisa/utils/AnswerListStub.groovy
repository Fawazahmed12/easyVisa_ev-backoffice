package com.easyvisa.utils

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.util.DateUtil

import java.time.LocalDate
import java.time.Month

import static com.easyvisa.utils.TestUtils.getAnswerInstance
import static org.junit.Assert.assertEquals

class AnswerListStub {

    static List<Answer> answerList(Long packageId, Long applicantId) {
        //'Have you ever before filed a Petition for Alien Fiance or for any other beneficiary?''
        Answer q1 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_1", "yes")

        Answer q13 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_13", "yes")//goes to DocAction Node
        Answer q14 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_14", "no")//goes to Terminal Node

        Answer rqg1_0_q2 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_2/0", "John") //firstName
        Answer rqg1_0_q3 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_3/0", "Williams") //'middleName'
        Answer rqg1_0_q4 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_4/0", "Peer") //'familyName'
        Answer rqg1_0_q5 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_5/0", "Brother")
        //'relationshipToYou'
        Answer rqg1_0_q6 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_6/0", DateUtil.fromDate(LocalDate.of(1988, Month.MARCH, 11)))
        //'dob'
        Answer rqg1_0_q7 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_7/0", "Seatle") //'cityortown'
        Answer rqg1_0_q8 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_8/0", "Washington") //'state'
        Answer rqg1_0_q9 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_9/0", DateUtil.fromDate(DateUtil.today()))
        //'dof'
        Answer rqg1_0_q10 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_10/0", "Pending")
        //'resultOfPetition'
        Answer rqg1_0_q11 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_11/0", "no") //'issuedAlienNo'

        Answer rqg1_1_q2 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_2/1", "Stephen") //firstName
        Answer rqg1_1_q3 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_3/1", "Peterson") //'middleName'
        Answer rqg1_1_q4 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_4/1", "Adityan") //'familyName'
        Answer rqg1_1_q5 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_5/1", "Cousin")
        //'relationshipToYou'
        Answer rqg1_1_q6 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_6/1", DateUtil.fromDate(LocalDate.of(1985, Month.NOVEMBER, 22)))
        //'dob'
        Answer rqg1_1_q7 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_7/1", "Brisbane") //'cityortown'
        Answer rqg1_1_q8 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_8/1", "Queensland") //'state'
        Answer rqg1_1_q9 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_9/1", DateUtil.fromDate(DateUtil.today()))
        //'dof'
        Answer rqg1_1_q10 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_10/1", "Under Progress")
        //'resultOfPetition'
        Answer rqg1_1_q11 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_11/1", "yes") //'issuedAlienNo'
        Answer rqg1_1_q11_q12 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_1/Q_12/1", "A1008") //'alienNo'

        //'Other than this Beneficiary, are you filing separate petitions for other relatives?'
        Answer q15 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_2/Q_15", "yes")

        Answer rqg2_0_q16 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_2/Q_16/0", "Sachin") //firstName
        Answer rqg2_0_q17 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_2/Q_17/0", "Ramesh")// middleName
        Answer rqg2_0_q18 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_2/Q_18/0", "Tendulkar")// familyName
        Answer rqg2_0_q19 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_2/Q_19/0", "relationship")
// relationship

        Answer rqg2_1_q16 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_2/Q_16/1", "Shane") //firstName
        Answer rqg2_1_q17 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_2/Q_17/1", "Keith")// middleName
        Answer rqg2_1_q18 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_2/Q_18/1", "Warne")// familyName
        Answer rqg2_1_q19 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_2/Q_19/1", "relationship")
// relationship

        List<Answer> answerList = new ArrayList<>()
        answerList.add(q1)
        answerList.add(q13)
        answerList.add(q14)
        answerList.add(rqg1_0_q2)
        answerList.add(rqg1_0_q3)
        answerList.add(rqg1_0_q4)
        answerList.add(rqg1_0_q5)
        answerList.add(rqg1_0_q6)
        answerList.add(rqg1_0_q7)
        answerList.add(rqg1_0_q8)
        answerList.add(rqg1_0_q9)
        answerList.add(rqg1_0_q10)
        answerList.add(rqg1_0_q11)
        answerList.add(rqg1_1_q2)
        answerList.add(rqg1_1_q3)
        answerList.add(rqg1_1_q4)
        answerList.add(rqg1_1_q5)
        answerList.add(rqg1_1_q6)
        answerList.add(rqg1_1_q7)
        answerList.add(rqg1_1_q8)
        answerList.add(rqg1_1_q9)
        answerList.add(rqg1_1_q10)
        answerList.add(rqg1_1_q11)
        answerList.add(rqg1_1_q11_q12)

        answerList.add(q15)
        answerList.add(rqg2_0_q16)
        answerList.add(rqg2_0_q17)
        answerList.add(rqg2_0_q18)
        answerList.add(rqg2_0_q19)
        answerList.add(rqg2_1_q16)
        answerList.add(rqg2_1_q17)
        answerList.add(rqg2_1_q18)
        answerList.add(rqg2_1_q19)

        return answerList
    }


    static List<Answer> nameSectionPetitionerAnswerList(Long packageId, Long applicantId) {
        Answer q32 = getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_5/Q_32", "John")//Given Name (First name)
        Answer q33 = getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_5/Q_33", "Watson")
//Middle Name (Do Not Abbreviate)
        Answer q34 = getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_5/Q_34", "Alexa")
//Family Name/Last Name/Surname

        Answer q35 = getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_6/Q_35", "yes")
        //Have you ever used another name for your Given Name (First Name), Middle Name (including maiden names), or Family Name/Last Name/Surname?
        Answer rqg4_0_q37 = getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_6/Q_37/0", "Williams")
        //List any other Given Name used (e.g. Rich, Rick, Dick):
        Answer rqg4_0_q39 = getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_6/Q_39/0", "Maxx")
        //List any other Middle Name used:

        List<Answer> answerList = new ArrayList<>()
        answerList.add(q32)
        answerList.add(q33)
        answerList.add(q34)

        answerList.add(q35)
        answerList.add(rqg4_0_q37)
        answerList.add(rqg4_0_q39)
        return answerList
    }


    static List<Answer> nameSectionBeneficiaryAnswerList(Long packageId, Long applicantId) {
        Answer q1901 = getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_currentLegalNameForBeneficiary/Q_1901", "Peter")
//Given Name (First name)
        Answer q1902 = getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_currentLegalNameForBeneficiary/Q_1902", "Clark")
//Middle Name (Do Not Abbreviate)
        Answer q1903 = getAnswerInstance(packageId, applicantId, "Sec_nameForBeneficiary/SubSec_currentLegalNameForBeneficiary/Q_1903", "Johnson")
//Family Name/Last Name/Surname

        List<Answer> answerList = new ArrayList<>()
        answerList.add(q1901)
        answerList.add(q1902)
        answerList.add(q1903)
        return answerList
    }


    static List<Answer> employedStatusEmployeeHistoryAnswerList(Long packageId, Long applicantId) {
        Answer q1008_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1008/0", "Employed")
//What is your current employment status?
        Answer q1013_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1013/0", "No")
//Are you self-employed?
        Answer q1014_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1014/0", DateUtil.fromDate(LocalDate.of(2019, Month.JANUARY, 17)))
//What date did you begin working for this employer?
        Answer q1018_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1018/0", "United States")
//In what country is this employer located?
        Answer q1019_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1019/0", "Kevin Peterson")
//What is the full name of this employer?
        Answer q1020_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1020/0", "400 Godown Street")
//Street Number and Name
        Answer q1021_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1021/0", "Yes")
//Does your address have a secondary description (i.e. apartment, suite, or floor)?
        Answer q1024_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1024/0", "Aurora")
//City/Town/Village
        Answer q1029_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1029/0", "Senior Software Developer")
//What is your occupation at this employer?

        Answer q1022_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1022/0", "Floor")
//What is the secondary address description?
        Answer q1023_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1023/0", "21")
//Apartment/Suite/Floor

        Answer q1025_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1025/0", "Colorado")
//State
        Answer q1027_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1027/0", "80011")
//ZIP Code


        Answer q1008_1 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1008/1", "Unemployed")
//What was your previous employment status?
        Answer q1009_1 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1009/1", DateUtil.fromDate(LocalDate.of(2018, Month.MAY, 17)))
//What was the date you became unemployed?
        Answer q1010_1 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1010/1", DateUtil.fromDate(LocalDate.of(2019, Month.JANUARY, 5)))
//What was the last date of your unemployment?


        Answer q1008_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1008/2", "Employed")
//What was your previous employment status?
        Answer q1013_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1013/2", "No")
//Were you self-employed?
        Answer q1014_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1014/2", DateUtil.fromDate(LocalDate.of(2016, Month.JANUARY, 17)))
//What date did you begin working for this employer?
        Answer q1015_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1015/2", "Yes")
//Are you still working at this employer?
        //Answer q1017_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1017/2", "TO PRESENT")
//When did this employment end for this employer?  here default answer 'TO PRESENT'
        Answer q1018_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1018/2", "Australia")
//In what country is this employer located?
        Answer q1019_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1019/2", "Watson")
//What is the full name of this employer?
        Answer q1020_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1020/2", "250 Maiden Cross Lane")
//Street Number and Name
        Answer q1021_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1021/2", "No")
//Does your address have a secondary description (i.e. apartment, suite, or floor)?
        Answer q1024_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1024/2", "Sydney")
//City/Town/Village
        Answer q1029_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1029/2", "Software Developer")
//What was your occupation at this employer?

        Answer q1026_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1026/2", "Brisbane")
//Province/Territory/State/Prefecture/Parish
        Answer q1028_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1028/2", "12334")
//Postal Code


        Answer q1008_3 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1008/3", "Retired")
//What was your previous employment status?
        Answer q1011_3 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1011/3", DateUtil.fromDate(LocalDate.of(2010, Month.MAY, 17)))
//What was the date of your retirement?
        Answer q1012_3 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1012/3", DateUtil.fromDate(LocalDate.of(2014, Month.JANUARY, 5)))
//What was the last date of your retirement?


        List<Answer> answerList = new ArrayList<>()
        answerList.add(q1008_0)
        answerList.add(q1013_0)
        answerList.add(q1014_0)
        answerList.add(q1018_0)
        answerList.add(q1019_0)
        answerList.add(q1020_0)
        answerList.add(q1021_0)
        answerList.add(q1024_0)
        answerList.add(q1029_0)
        answerList.add(q1022_0)
        answerList.add(q1023_0)
        answerList.add(q1025_0)
        answerList.add(q1027_0)

        answerList.add(q1008_1)
        answerList.add(q1009_1)
        answerList.add(q1010_1)

        answerList.add(q1008_2)
        answerList.add(q1013_2)
        answerList.add(q1014_2)
        answerList.add(q1015_2)
        //answerList.add(q1017_2)
        answerList.add(q1018_2)
        answerList.add(q1019_2)
        answerList.add(q1020_2)
        answerList.add(q1021_2)
        answerList.add(q1024_2)
        answerList.add(q1029_2)
        answerList.add(q1026_2)
        answerList.add(q1028_2)

        answerList.add(q1008_3)
        answerList.add(q1011_3)
        answerList.add(q1012_3)

        return answerList
    }

    static List<Answer> employmentHistoryAnswerList(Long packageId, Long applicantId, LocalDate startDate, List dateRows) {
        // ***** Current Employment *****
        Answer q1008_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1008/0", "Employed")
        //What is your current employment status?
        Answer q1013_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1013/0", "No")
        //Are you self-employed?

        Answer q1014_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1014/0", DateUtil.fromDate(startDate))
        //What date did you begin working for this employer?

        Answer q1018_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1018/0", "United States")
        //In what country is this employer located?
        Answer q1019_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1019/0", "Kevin Peterson")
        //What is the full name of this employer?
        Answer q1020_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1020/0", "400 Godown Street")
        //Street Number and Name
        Answer q1021_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1021/0", "Yes")
        //Does your address have a secondary description (i.e. apartment, suite, or floor)?
        Answer q1024_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1024/0", "Aurora")
        //City/Town/Village
        Answer q1029_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1029/0", "Senior Software Developer")
        //What is your occupation at this employer?

        Answer q1022_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1022/0", "Floor")
        //What is the secondary address description?
        Answer q1023_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1023/0", "21")
        //Apartment/Suite/Floor

        Answer q1025_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1025/0", "Colorado")
        //State
        Answer q1027_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1027/0", "80011")
        //ZIP Code

        List<Answer> answerList = new ArrayList<>()
        answerList.add(q1008_0)
        answerList.add(q1013_0)
        answerList.add(q1014_0)
        answerList.add(q1018_0)
        answerList.add(q1019_0)
        answerList.add(q1020_0)
        answerList.add(q1021_0)
        answerList.add(q1024_0)
        answerList.add(q1029_0)
        answerList.add(q1022_0)
        answerList.add(q1023_0)
        answerList.add(q1025_0)
        answerList.add(q1027_0)

        // ***** Previous Employment *****

        dateRows.eachWithIndex { Map entry, int index ->
            entry.entrySet().each { en ->
                String tmpVal = en.value
                if (en.value instanceof LocalDate)
                    tmpVal = DateUtil.fromDate(en.value)

                Answer tmpAnswer = getAnswerInstance(packageId, applicantId, "${en.key}/${index + 1}", tmpVal)
                answerList.add(tmpAnswer)
            }
        }

        return answerList
    }


    static List<Answer> usCitizenLegalStatusAnswerList(Long packageId, Long applicantId) {
        Answer q108 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_109", "united_states_citizen")
//What is your Legal Status in the United States?
        Answer q6099 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_6099", "Yes")
//Have you ever had an Alien Registration Number (A-Number)?
        Answer q110 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_112", "978893170")
//Alien Registration Number (A-Number)
        Answer q6062 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_6062", "No")
// Does the U.S. Citizen or LPR who sponsored the Beneficiary for their visa have a Social Security Number?
        Answer q6097 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_6097", "No")
// Does this person have a Social Security Number?
        Answer q112 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_114", "No")
//Do you have a USCIS ELIS Account Number?
        //Answer q113 = getAnswerInstance(packageId, applicantId,"Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_115", "Unemployed")//What is your USCIS ELIS Account Number?

        Answer q115 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_usCitizens/Q_116", "Yes")
//Do you currently have a valid U.S. Passport?
        Answer q116 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_usCitizens/Q_117", "MGG5311089")
//Passport Number
        Answer q117 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_usCitizens/Q_118", DateUtil.fromDate(LocalDate.of(2011, Month.AUGUST, 15)))
//When was your passport issued?
        Answer q118 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_usCitizens/Q_119", DateUtil.fromDate(LocalDate.of(2021, Month.AUGUST, 15)))
//When does your passport expire?
        Answer q119 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_usCitizens/Q_120", "naturalization")
//How was your citizenship acquired?
        Answer q120 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_usCitizens/Q_121", "Yes")
//Have you obtained a Naturalization/Citizenship Certificate Number?
        Answer q121 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_usCitizens/Q_122", "3423123098992001")
//What is the Certificate of Naturalization/Citizenship Certificate Number?
        Answer q122 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_usCitizens/Q_123", "Arizona")
//Where was your Naturalization/Citizenship Issued?
        Answer q123 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_usCitizens/Q_124", DateUtil.fromDate(LocalDate.of(2016, Month.JANUARY, 17)))
//What date was your Certificate of Naturalization/Citizenship Issued?

        List<Answer> answerList = new ArrayList<>()
        answerList.add(q108)
        answerList.add(q6099)
        answerList.add(q110)
        answerList.add(q6062)
        answerList.add(q6097)
        answerList.add(q112)

        answerList.add(q115)
        answerList.add(q116)
        answerList.add(q117)
        answerList.add(q118)
        answerList.add(q119)
        answerList.add(q120)
        answerList.add(q121)
        answerList.add(q122)
        answerList.add(q123)
        return answerList
    }


    static List<Answer> greenCardHolderCitizenLegalStatusAnswerList(Long packageId, Long applicantId) {
        Answer q108 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_109", "lawful_permanent_resident")
//What is your Legal Status in the United States?
        Answer q6099 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_6099", "Yes")
//Have you ever had an Alien Registration Number (A-Number)?
        Answer q110 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_112", "944358001")
//Alien Registration Number (A-Number)

        Answer q6062 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_6062", "Yes")
        // Does the U.S. Citizen or LPR who sponsored the Beneficiary for their visa have a Social Security Number?

        Answer q6097 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_6097", "Yes")
        // Does this person have a Social Security Number?
        Answer q111 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_113", "944355965")
//Social Security Number (If any)
        Answer q112 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_114", "Yes")
//Do you have a USCIS ELIS Account Number?
        Answer q113 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_115", "MS8148938971")
//What is your USCIS ELIS Account Number?

        Answer q124 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_lawfulPermanentResident/Q_125", DateUtil.fromDate(LocalDate.of(2016, Month.JANUARY, 17)))
//Date of Admission for (or adjustment to) LPR (Lawful Permanent Resident, also called Green Card Holder) in the United States
        Answer q125 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_lawfulPermanentResident/Q_126", "Los Angeles")
//Place (City) of Admission for (or adjustment to) LPR (Lawful Permanent Resident, also called Green Card Holder) in the United States
        Answer q126 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_lawfulPermanentResident/Q_127", "California")
//Place of Admission for (or adjustment to) LPR (Lawful Permanent Resident, also called Green Card Holder) in the United States
        Answer q127 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_lawfulPermanentResident/Q_128", "Eureka")
//Class (Category) of Admission Help
        Answer q128 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_lawfulPermanentResident/Q_129", "Marriage to a United States Citizen")
//How did you gain your Permanent Residence?

        List<Answer> answerList = new ArrayList<>()
        answerList.add(q108)
        answerList.add(q6099)
        answerList.add(q110)
        answerList.add(q6062)
        answerList.add(q6097)
        answerList.add(q111)
        answerList.add(q112)
        answerList.add(q113)

        answerList.add(q124)
        answerList.add(q125)
        answerList.add(q126)
        answerList.add(q127)
        answerList.add(q128)
        return answerList
    }


    static List<Answer> biographicInformationWeightHeightAnswerList(Long packageId, Long applicantId) {
        Answer q101 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformation/SubSec_height/Q_101", "Metric")
//What units of measure do you use?
        Answer q102 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformation/SubSec_height/Q_102", "177")
//Centimeters

        Answer q105 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformation/SubSec_weight/Q_105", "Metric")
//What units of measure do you use?
        Answer q6009 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformation/SubSec_weight/Q_6009", "93")
//What is your weight?

        List<Answer> answerList = []
        answerList.add(q101)
        answerList.add(q102)

        answerList.add(q105)
        answerList.add(q6009)
        return answerList
    }


    static List<Answer> biographicInformationAnswerList(Long packageId, Long applicantId) {
        Answer q105 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformation/SubSec_weight/Q_105", "Metric")
//What units of measure do you use?
        Answer q106 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformation/SubSec_weight/Q_106", "95")
//What is your weight?

        List<Answer> answerList = []
        answerList.add(q105)
        answerList.add(q106)
        return answerList
    }


    static List<Answer> addressHistoryAnswerList(Long packageId, Long applicantId) {
        Integer currentYear = DateUtil.today().year
        Answer q42 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_42", "United States")
//In what country is your current physical address?
        Answer q43 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_43", "400 North Car Street")
//Street Number and Name
        Answer q44 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_44", "Yes")
//Does your address have a secondary description (i.e. apartment, suite, or floor)?
        Answer q45 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_45", "Apartment")
//What is the secondary address description?
        Answer q46 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_46", "Samuel Apartments")
//Apartment/Suite/Floor Help
        Answer q47 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_47", "Aurora")
//City/Town/Village
        Answer q48 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_48", "Colorado")
//State
        Answer q50 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_50", "80011")
//ZIP Code
        Answer q52 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_52", DateUtil.fromDate(LocalDate.of(currentYear - 3, Month.JANUARY, 17)))
//When did you move into this address?


        Answer q54 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_54/0", "Albania")
//In what country was this previous physical address?
        Answer q55 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_55/0", "23-A Mustafa Matohiti")
//Street Number and Name?
        Answer q56 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_56/0", "No")
//Does your address have a secondary description (i.e. apartment, suite, or floor)?2", "3423123098992001")//Apartment/Suite/Floor Help
        Answer q59 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_59/0", "Miladin")
//City/Town/Village
        Answer q61 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_61/0", "Tirana")
//Province/Territory/Prefecture/Parish
        Answer q63 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_63/0", "1031")
//Postal Code
        Answer q64 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_64/0", DateUtil.fromDate(LocalDate.of(currentYear - 4, Month.MAY, 1)))
//When did you move into this address?
        Answer q65 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_65/0", DateUtil.fromDate(LocalDate.of(currentYear - 3, Month.JANUARY, 15)))
//When did you move out of this address?


        Answer q68 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentMailingAddress/Q_68", "Edi Rama")
//In Care of Name
        Answer q69 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentMailingAddress/Q_69", "yes")
//Is your current mailing address the same as your current physical address?

        List<Answer> answerList = new ArrayList<>()
        answerList.add(q42)
        answerList.add(q43)
        answerList.add(q44)
        answerList.add(q45)
        answerList.add(q46)
        answerList.add(q47)
        answerList.add(q48)
        answerList.add(q50)
        answerList.add(q52)

        answerList.add(q54)
        answerList.add(q55)
        answerList.add(q56)
        answerList.add(q59)
        answerList.add(q61)
        answerList.add(q63)
        answerList.add(q64)
        answerList.add(q65)

        answerList.add(q68)
        answerList.add(q69)

        return answerList
    }


    static List<Answer> populateDependentChildNamesRuleAnswerList(Long packageId, Long applicantId) {
        Answer q275 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1201", "Yes")
//Have you ever been married?
        Answer q276 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1202", "2")
//How many times have you been married?
        Answer q277 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1203", "No")
//Was your most recent marriage annulled?
        Answer q278 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1204", "Married")
//What is your current marital status?


        Answer q279 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1206", "Trevor")
//Given Name (First name)
        Answer q280 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1207", "Bayliss")
//Middle Name (Do Not Abbreviate)
        Answer q281 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1208", "John")
//Family Name/Last Name/Surname
        Answer q282 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1209", DateUtil.fromDate(LocalDate.of(2016, Month.JANUARY, 17)))
//Date of Marriage
        Answer q283 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1210", "United States")
//In what country did this marriage take place?
        Answer q284 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1211", "240 Hayat Regency")
//On what Street Number and Name did the marriage take place?
        Answer q285 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1212", "No")
//Does your place of marriage address have a secondary description (i.e. apartment, suite, or floor)?
        Answer q288 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1215", "San Francisco Bay Area")
//In what City/Town/Village did this marriage take place?
        Answer q289 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1216", "California")
//In what State did your marriage take place?
        Answer q291 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1218", "629001")
//In what ZIP Code did this marriage take place?
        Answer q293 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1220", "Yes")
//Were you married previously?


        Answer q294 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1222/0", "Albert")
//Given Name (First name)
        Answer q295 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1223/0", "Thomson")
//Middle Name (Do Not Abbreviate)
        Answer q296 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1224/0", "Joel")
//Family Name/Last Name/Surname
        Answer q297 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1225/0", "Death")
//How did this marriage end?
        Answer q298 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1226/0", DateUtil.fromDate(LocalDate.of(2015, Month.FEBRUARY, 11)))
//Date of death of this spouse as listed on death certificate	date
        Answer q299 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1227/0", DateUtil.fromDate(LocalDate.of(2008, Month.MAY, 5)))
//Date of Marriage
        //Answer q300 = getAnswerInstance(packageId, applicantId,"Sec_familyInformation/SubSec_priorSpouses/Q_1228/0", DateUtil.fromDate(LocalDate.of(2015, Month.FEBRUARY, 11)))//Date Marriage Ended
        //TODO... rule will autostore the value of q298 to q300, if answer for the question q297 is  'Death'

        Answer q319 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1250", "Yes")
//Do you have any children under 18 years of age?
        Answer q320_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1251/0", "Kane")
//Given Name (First name)
        Answer q321_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1252/0", "Williamson")
//Middle Name (Do Not Abbreviate)
        Answer q322_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1253/0", "Anna")
//Family Name/Last Name/Surname
        Answer q323_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1254/0", "Male")
//Gender
        Answer q324_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1255/0", "United States")
//Country of Birth
        Answer q325_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1256/0", DateUtil.fromDate(LocalDate.of(2003, Month.FEBRUARY, 11)))
//Date of Birth of Child 1 (Children under 18 only)
        Answer q326_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1257/0", "United States")
//In what country is this child's physical address?
        Answer q327_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1258/0", "250 A Godown North Street")
//Street Number and Name
        Answer q328_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1259/0", "No")
//Does your address have a secondary description (i.e. apartment, suite, or floor)?
        Answer q331_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1262/0", "Chicago")
//City/Town/Village
        Answer q332_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1263/0", "Illinois")
//State
        Answer q334_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1265/0", "60612")
//ZIP Code


        Answer q320_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1251/1", "Gary")
//Given Name (First name)
        Answer q321_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1252/1", "Raymond")
//Middle Name (Do Not Abbreviate)
        Answer q322_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1253/1", "Stead")
//Family Name/Last Name/Surname
        Answer q323_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1254/1", "Male")
//Gender
        Answer q324_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1255/1", "United States")
//Country of Birth
        Answer q325_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1256/1", DateUtil.fromDate(LocalDate.of(2008, Month.NOVEMBER, 22)))
//Date of Birth of Child 1 (Children under 18 only)
        Answer q326_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1257/1", "United States")
//In what country is this child's physical address?
        Answer q327_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1258/1", "12 Arms Villa")
//Street Number and Name
        Answer q328_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1259/1", "Yes")
//Does your address have a secondary description (i.e. apartment, suite, or floor)?
        Answer q329_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1260/1", "Apartment")
//What is the secondary address description?
        Answer q330_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1261/1", "Samuel Apartments")
//Apartment/Suite/Floor Help
        Answer q331_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1262/1", "Los Angeles")
//City/Town/Village
        Answer q332_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1263/1", "California")
//State
        Answer q334_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1265/1", "90001")
//ZIP Code


        Answer q336 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1268", "Yes")
//Do you have any dependents who are children? Help
        Answer q337_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1269/0", "Gary")
//Select Child to Auto-Fill data (If child was listed in the previous subsection)
        Answer q338_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1270/0", "Gary")
//Given Name (First name)
        Answer q339_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1271/0", "Raymond")
//Middle Name (Do Not Abbreviate)
        Answer q340_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1272/0", "Stead")
//Family Name/Last Name/Surname
        Answer q341_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1273/0", "Male")
//Gender
        Answer q342_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1274/0", "United States")
//Country of Birth
        Answer q343_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1275/0", "United States")
//Country of Citizenship
        Answer q344_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1276/0", DateUtil.fromDate(LocalDate.of(2008, Month.NOVEMBER, 22)))
//Date of Birth
        Answer q345_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1277/0", "United States")
//In what country is this child's physical address?
        Answer q346_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1278/0", "12 Arms Villa")
//Street Number and Name
        Answer q347_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1279/0", "Yes")
//Does your address have a secondary description (i.e. apartment, suite, or floor)?
        Answer q348_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1280/0", "Apartment")
//What is the secondary address description?
        Answer q349_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1281/0", "Samuel Apartments")
//Apartment/Suite/Floor Help
        Answer q350_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1282/0", "Los Angeles")
//City/Town/Village
        Answer q351_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1283/0", "California")
//State
        Answer q353_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1285/0", "90001")
//ZIP Code
        Answer q355_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1287/0", "Child")
//What is this dependent's relationship to you?
        Answer q356_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1288/0", "Wholly Dependent")
//Degree of Dependency


        Answer q337_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1269/1", "")
//Select Child to Auto-Fill data (If child was listed in the previous subsection)
        Answer q338_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1270/1", "Joseph")
//Given Name (First name)
        Answer q339_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1271/1", "Edward")
//Middle Name (Do Not Abbreviate)
        Answer q340_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1272/1", "Root")
//Family Name/Last Name/Surname
        Answer q341_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1273/1", "Male")
//Gender
        Answer q342_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1274/1", "England")
//Country of Birth
        Answer q343_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1275/1", "England")
//Country of Citizenship
        Answer q344_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1276/1", DateUtil.fromDate(LocalDate.of(2005, Month.SEPTEMBER, 25)))
//Date of Birth
        Answer q345_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1277/1", "England")
//In what country is this child's physical address?
        Answer q346_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1278/1", "627 Mount Road")
//Street Number and Name
        Answer q347_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1279/1", "No")
//Does your address have a secondary description (i.e. apartment, suite, or floor)?
        Answer q350_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1282/1", "New York City")
//City/Town/Village
        Answer q351_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1283/1", "New York")
//State
        Answer q353_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1285/1", "10421")
//ZIP Code
        Answer q355_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1287/1", "Son-in-law")
//What is this dependent's relationship to you?
        Answer q356_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1288/1", "Partially Dependent")
//Degree of Dependency


        List<Answer> answerList = new ArrayList<>()
        answerList.add(q275)
        answerList.add(q276)
        answerList.add(q277)
        answerList.add(q278)

        answerList.add(q279)
        answerList.add(q280)
        answerList.add(q281)
        answerList.add(q282)
        answerList.add(q283)
        answerList.add(q284)
        answerList.add(q285)
        answerList.add(q288)
        answerList.add(q289)
        answerList.add(q291)
        answerList.add(q293)

        answerList.add(q294)
        answerList.add(q295)
        answerList.add(q296)
        answerList.add(q297)
        answerList.add(q298)
        answerList.add(q299)

        answerList.add(q319)
        answerList.add(q320_0)
        answerList.add(q321_0)
        answerList.add(q322_0)
        answerList.add(q323_0)
        answerList.add(q324_0)
        answerList.add(q325_0)
        answerList.add(q326_0)
        answerList.add(q327_0)
        answerList.add(q328_0)
        answerList.add(q331_0)
        answerList.add(q332_0)
        answerList.add(q334_0)

        answerList.add(q320_1)
        answerList.add(q321_1)
        answerList.add(q322_1)
        answerList.add(q323_1)
        answerList.add(q324_1)
        answerList.add(q325_1)
        answerList.add(q326_1)
        answerList.add(q327_1)
        answerList.add(q328_1)
        answerList.add(q329_1)
        answerList.add(q330_1)
        answerList.add(q331_1)
        answerList.add(q332_1)
        answerList.add(q334_1)


        answerList.add(q336)
        answerList.add(q337_0)
        answerList.add(q338_0)
        answerList.add(q339_0)
        answerList.add(q340_0)
        answerList.add(q341_0)
        answerList.add(q342_0)
        answerList.add(q343_0)
        answerList.add(q344_0)
        answerList.add(q345_0)
        answerList.add(q346_0)
        answerList.add(q347_0)
        answerList.add(q348_0)
        answerList.add(q349_0)
        answerList.add(q350_0)
        answerList.add(q351_0)
        answerList.add(q353_0)
        answerList.add(q355_0)
        answerList.add(q356_0)

        answerList.add(q337_1)
        answerList.add(q338_1)
        answerList.add(q339_1)
        answerList.add(q340_1)
        answerList.add(q341_1)
        answerList.add(q342_1)
        answerList.add(q343_1)
        answerList.add(q344_1)
        answerList.add(q345_1)
        answerList.add(q346_1)
        answerList.add(q347_1)
        answerList.add(q350_1)
        answerList.add(q351_1)
        answerList.add(q353_1)
        answerList.add(q355_1)
        answerList.add(q356_1)

        return answerList
    }


    static List<Answer> autoPopulateDependentChildNamesRuleAnswerList(Long packageId, Long applicantId, List child1, List child2, String selectDependant, List dependantData, Boolean addManual) {

        List<Answer> answerList = new ArrayList<>()
        Answer q275 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1201", "Yes")
//Have you ever been married?
        Answer q276 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1202", "1")
//How many times have you been married?
        Answer q277 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1203", "No")
//Was your most recent marriage annulled?
        Answer q278 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1204", "Married")
//What is your current marital status?

        Answer q319

        if (child1.isEmpty() && child2.isEmpty()) {

            q319 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1250", "No")
            //Do you have any children under 18 years of age?
        } else {

            q319 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1250", "Yes")
//Do you have any children under 18 years of age?

        }
        answerList.add(q319)

        if (!child1.isEmpty()) {
            Answer q320_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1251/0", child1[0])
//Given Name (First name)
            Answer q321_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1252/0", child1[1])
//Middle Name (Do Not Abbreviate)
            Answer q322_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1253/0", child1[2])
//Family Name/Last Name/Surname
            Answer q323_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1254/0", "Male")
//Gender
            Answer q324_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1255/0", "United States")
//Country of Birth
            Answer q325_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1256/0", child1[3])
//Date of Birth of Child 1 (Children under 18 only)
            Answer q326_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1257/0", "United States")
//In what country is this child's physical address?
            Answer q327_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1258/0", "250 A Godown North Street")
//Street Number and Name
            Answer q328_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1259/0", "No")
//Does your address have a secondary description (i.e. apartment, suite, or floor)?
            Answer q331_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1262/0", "Chicago")
//City/Town/Village
            Answer q332_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1263/0", "Illinois")
//State
            Answer q334_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1265/0", "60612")
//ZIP Code

            answerList.add(q320_0)
            answerList.add(q321_0)
            answerList.add(q322_0)
            answerList.add(q323_0)
            answerList.add(q324_0)
            answerList.add(q325_0)
            answerList.add(q326_0)
            answerList.add(q327_0)
            answerList.add(q328_0)
            answerList.add(q331_0)
            answerList.add(q332_0)
            answerList.add(q334_0)
        }

        if (!child2.isEmpty()) {
            Answer q320_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1251/1", child2[0])
//Given Name (First name)
            Answer q321_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1252/1", child2[1])
//Middle Name (Do Not Abbreviate)
            Answer q322_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1253/1", child2[2])
//Family Name/Last Name/Surname
            Answer q323_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1254/1", "Male")
//Gender
            Answer q324_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1255/1", "United States")
//Country of Birth
            Answer q325_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1256/1", child2[3])
//Date of Birth of Child 1 (Children under 18 only)
            Answer q326_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1257/1", "United States")
//In what country is this child's physical address?
            Answer q327_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1258/1", "12 Arms Villa")
//Street Number and Name
            Answer q328_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1259/1", "Yes")
//Does your address have a secondary description (i.e. apartment, suite, or floor)?
            Answer q329_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1260/1", "Apartment")
//What is the secondary address description?
            Answer q330_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1261/1", "Samuel Apartments")
//Apartment/Suite/Floor Help
            Answer q331_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1262/1", "Los Angeles")
//City/Town/Village
            Answer q332_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1263/1", "California")
//State
            Answer q334_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1265/1", "90001")
//ZIP Code
            answerList.add(q320_1)
            answerList.add(q321_1)
            answerList.add(q322_1)
            answerList.add(q323_1)
            answerList.add(q324_1)
            answerList.add(q325_1)
            answerList.add(q326_1)
            answerList.add(q327_1)
            answerList.add(q328_1)
            answerList.add(q329_1)
            answerList.add(q330_1)
            answerList.add(q331_1)
            answerList.add(q332_1)
            answerList.add(q334_1)
        }


        Answer q336 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1268", "Yes")
//Do you have any dependents who are children? Help

        Answer q337 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1269/0", selectDependant)
// Q_1269 Select Child to Auto-Fill data (If child was listed in the previous subsection)


        answerList.add(q275)
        answerList.add(q276)
        answerList.add(q277)
        answerList.add(q278)


        answerList.add(q336)
        answerList.add(q337)

        if (addManual) {
            // add data for dependents manually - ie. selection is --None-- and a new dependant is added
            Answer q338_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1270/0", dependantData[0])
//Given Name (First name)
            Answer q339_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1271/0", dependantData[1])
//Middle Name (Do Not Abbreviate)
            Answer q340_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1272/0", dependantData[2])
//Family Name/Last Name/Surname
            Answer q344_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1276/0", dependantData[3])
//Date of Birth

            answerList.add(q338_0)
            answerList.add(q339_0)
            answerList.add(q340_0)
            answerList.add(q344_0)


        }

        return answerList
    }

    //Radio (Imperial, Metric)
    // Question - 'Feet' under Height subsection , only appears if user selected 'Metric' to the question 'What units of measure do you use?'
    static List<Answer> metricUnitBiographicInformationAnswerList(Long packageId, Long applicantId) {
        Answer q2301 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_ethnicityForBeneficiary/Q_2301", "Hispanic or Latino")
//Please select your ethnicity (You race will be asked in a subsequent question)

        Answer q2303 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_raceForBeneficiary/Q_2303", "False")
//Are you White?
        Answer q2304 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_raceForBeneficiary/Q_2304", "False")
//Are you Asian?
        Answer q2305 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_raceForBeneficiary/Q_2305", "False")
//Are you Black or African American?
        Answer q2306 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_raceForBeneficiary/Q_2306", "True")
//Are you American Indian or Alaska Native?
        Answer q2307 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_raceForBeneficiary/Q_2307", "False")
//Are you Native Hawaiin or Other Pacific Islander?

        Answer q2309 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_heightForBeneficiary/Q_2309", "Metric")
//What units of measure do you use?
        Answer q2310 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_heightForBeneficiary/Q_2312", "172")
//Centimeters

        Answer q2314 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_weightForBeneficiary/Q_2314", "Metric")
//What units of measure do you use?
        Answer q2315 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_weightForBeneficiary/Q_6010", "84")
//What is your weight?

        Answer q2317 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_eyeColorForBeneficiary/Q_2317", "Hazel")
//What is your eye color?

        Answer q2319 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_hairColorForBeneficiary/Q_2319", "Gray")
//What is the color of your hair?

        List<Answer> answerList = new ArrayList<>()
        answerList.add(q2301)
        answerList.add(q2303)
        answerList.add(q2304)
        answerList.add(q2305)
        answerList.add(q2306)
        answerList.add(q2307)
        answerList.add(q2309)
        answerList.add(q2310)
        answerList.add(q2314)
        answerList.add(q2315)
        answerList.add(q2317)
        answerList.add(q2319)
        return answerList
    }

    //Radio (Imperial, Metric)
    // Question - 'Inches' and 'Centimeters' under Height subsection , only appears if user selected 'Imperial' to the question 'What units of measure do you use?'
    static List<Answer> imperialUnitBiographicInformationAnswerList(Long packageId, Long applicantId) {
        Answer q2301 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_ethnicityForBeneficiary/Q_2301", "Hispanic or Latino")
//Please select your ethnicity (You race will be asked in a subsequent question)

        Answer q2303 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_raceForBeneficiary/Q_2303", "False")
//Are you White?
        Answer q2304 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_raceForBeneficiary/Q_2304", "False")
//Are you Asian?
        Answer q2305 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_raceForBeneficiary/Q_2305", "False")
//Are you Black or African American?
        Answer q2306 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_raceForBeneficiary/Q_2306", "True")
//Are you American Indian or Alaska Native?
        Answer q2307 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_raceForBeneficiary/Q_2307", "False")
//Are you Native Hawaiin or Other Pacific Islander?

        Answer q2309 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_heightForBeneficiary/Q_2309", "Imperial")
//What units of measure do you use?
        Answer q2311 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_heightForBeneficiary/Q_2310", "5")
//Feet
        Answer q2312 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_heightForBeneficiary/Q_2311", "10")
//Inches

        Answer q2314 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_weightForBeneficiary/Q_2314", "Imperial")
//What units of measure do you use?
        Answer q2315 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_weightForBeneficiary/Q_2315", "236")
//What is your weight?

        Answer q2317 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_eyeColorForBeneficiary/Q_2317", "Hazel")
//What is your eye color?

        Answer q2319 = getAnswerInstance(packageId, applicantId, "Sec_biographicInformationForBeneficiary/SubSec_hairColorForBeneficiary/Q_2319", "Gray")
//What is the color of your hair?

        List<Answer> answerList = new ArrayList<>()
        answerList.add(q2301)
        answerList.add(q2303)
        answerList.add(q2304)
        answerList.add(q2305)
        answerList.add(q2306)
        answerList.add(q2307)
        answerList.add(q2309)
        answerList.add(q2311)
        answerList.add(q2312)
        answerList.add(q2314)
        answerList.add(q2315)
        answerList.add(q2317)
        answerList.add(q2319)
        return answerList
    }


    static List<Answer> personelInformationAnswerList(Long packageId, Long applicantId) {
        Answer q2401 = getAnswerInstance(packageId, applicantId, "Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2401/0", "Algeria")
//Country of Citizenship (or Nationality)

        List<Answer> answerList = new ArrayList<>()
        answerList.add(q2401)
        return answerList
    }


    static List<Answer> personelInformationBeneficiaryAnswerList(Long packageId, Long applicantId) {
        Answer q2401 = getAnswerInstance(packageId, applicantId, "Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2401/0", "Algeria")
//Country of Citizenship (or Nationality)
        Answer q2402 = getAnswerInstance(packageId, applicantId, "Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2402", "MGG5311089")
//Passport Number
        Answer q2403 = getAnswerInstance(packageId, applicantId, "Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2403", "No")
//Have you been issued a Travel Document Number?
        Answer q2405 = getAnswerInstance(packageId, applicantId, "Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2405", "Germany")
//Country of Issuance for Passport or Travel Document
        Answer q2406 = getAnswerInstance(packageId, applicantId, "Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2406", DateUtil.fromDate(LocalDate.of(2014, Month.JANUARY, 17)))
//Expiration Date for Passport or Travel Document
        Answer q2407 = getAnswerInstance(packageId, applicantId, "Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2407", "No")
//Has the Social Security Administration (SSA) ever officially issued a Social Security card to you?

        Answer q6098 = getAnswerInstance(packageId, applicantId, "Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_6098", "No")

        Answer q2408 = getAnswerInstance(packageId, applicantId, "Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2408", "No")
//Do you want the SSA to issue you a Social Security card?
        Answer q2410 = getAnswerInstance(packageId, applicantId, "Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2410", "No")
//Do you have a USCIS ELIS Account Number?
        Answer q2412 = getAnswerInstance(packageId, applicantId, "Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2412", "A12345678")
//Alien Registration Number (A-Number)
        Answer q2413 = getAnswerInstance(packageId, applicantId, "Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2413", DateUtil.fromDate(LocalDate.of(2008, Month.DECEMBER, 01)))
//Date of Admission for (or adjustment to) LPR in the United States
        Answer q2414 = getAnswerInstance(packageId, applicantId, "Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2414", "DC")
//Place of Admission for (or adjustment to) LPR in the United States
        Answer q2416 = getAnswerInstance(packageId, applicantId, "Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2416", "Male")
//Gender

        List<Answer> answerList = new ArrayList<>()
        answerList.add(q2401)
        answerList.add(q2402)
        answerList.add(q2403)
        answerList.add(q2405)
        answerList.add(q2406)
        answerList.add(q2407)
        answerList.add(q6098)
        answerList.add(q2408)
        answerList.add(q2410)
        answerList.add(q2412)
        answerList.add(q2413)
        answerList.add(q2414)
        answerList.add(q2416)
        return answerList
    }


    static List<Answer> employedStatusEmployeeHistoryForBeneficiaryAnswerList(Long packageId, Long applicantId) {
        Answer q2608_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2608/0", "Employed")
//What is your current employment status?
        Answer q2613_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2613/0", "No")
//Are you self-employed?
        Answer q2614_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2614/0", DateUtil.fromDate(LocalDate.of(2019, Month.JANUARY, 17)))
//What date did you begin working for this employer?
        Answer q2618_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2618/0", "United States")
//In what country is this employer located?
        Answer q2619_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2619/0", "Kevin Peterson")
//What is the full name of this employer?
        Answer q2620_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2620/0", "400 Godown Street")
//Street Number and Name
        Answer q2621_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2621/0", "Yes")
//Does your address have a secondary description (i.e. apartment, suite, or floor)?
        Answer q2624_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2624/0", "Aurora")
//City/Town/Village
        Answer q2629_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2629/0", "Senior Software Developer")
//What is your occupation at this employer?

        Answer q2622_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2622/0", "Floor")
//What is the secondary address description?
        Answer q2623_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2623/0", "21")
//Apartment/Suite/Floor

        Answer q2625_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2625/0", "Colorado")
//State
        Answer q2627_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2627/0", "80011")
//ZIP Code


        Answer q2608_1 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2608/1", "Unemployed")
//What was your previous employment status?
        Answer q2609_1 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2609/1", DateUtil.fromDate(LocalDate.of(2018, Month.MAY, 17)))
//What was the date you became unemployed?
        Answer q2610_1 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2610/1", DateUtil.fromDate(LocalDate.of(2019, Month.JANUARY, 5)))
//What was the last date of your unemployment?


        Answer q2608_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2608/2", "Employed")
//What was your previous employment status?
        Answer q2613_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2613/2", "No")
//Were you self-employed?
        Answer q2614_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2614/2", DateUtil.fromDate(LocalDate.of(2016, Month.JANUARY, 17)))
//What date did you begin working for this employer?
        Answer q2615_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2615/2", "Yes")
//Are you still working at this employer?
        //Answer q2617_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2617/2", "TO PRESENT")
//When did this employment end for this employer?  here default answer 'TO PRESENT'
        Answer q2618_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2618/2", "Australia")
//In what country is this employer located?
        Answer q2619_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2619/2", "Watson")
//What is the full name of this employer?
        Answer q2620_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2620/2", "250 Maiden Cross Lane")
//Street Number and Name
        Answer q2621_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2621/2", "No")
//Does your address have a secondary description (i.e. apartment, suite, or floor)?
        Answer q2624_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2624/2", "Sydney")
//City/Town/Village
        Answer q2629_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2629/2", "Software Developer")
//What was your occupation at this employer?

        Answer q2626_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2626/2", "Brisbane")
//Province/Territory/State/Prefecture/Parish
        Answer q2628_2 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2628/2", "12334")
//Postal Code

        List<Answer> answerList = new ArrayList<>()
        answerList.add(q2608_0)
        answerList.add(q2613_0)
        answerList.add(q2614_0)
        answerList.add(q2618_0)
        answerList.add(q2619_0)
        answerList.add(q2620_0)
        answerList.add(q2621_0)
        answerList.add(q2624_0)
        answerList.add(q2629_0)
        answerList.add(q2622_0)
        answerList.add(q2623_0)
        answerList.add(q2625_0)
        answerList.add(q2627_0)

        answerList.add(q2608_1)
        answerList.add(q2609_1)
        answerList.add(q2610_1)

        answerList.add(q2608_2)
        answerList.add(q2613_2)
        answerList.add(q2614_2)
        answerList.add(q2615_2)
        //answerList.add(q2617_2)
        answerList.add(q2618_2)
        answerList.add(q2619_2)
        answerList.add(q2620_2)
        answerList.add(q2621_2)
        answerList.add(q2624_2)
        answerList.add(q2629_2)
        answerList.add(q2626_2)
        answerList.add(q2628_2)

        return answerList
    }

    /**
     * @AG: Parameterized stub for creating dynamic answer data
     * @param packageId
     * @param applicantId
     * @return
     */
    static List<Answer> povertyGuideLineAnswerList(Long packageId, Long applicantId, String hhSize, String military, String state, String asset) {
        Answer q1201 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1201", "Yes")
        //Have you ever been married?
        Answer q1202 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1202", "1")
        //How many times have you been married?
        Answer q1204 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1204", "Married")
        //What is your current marital status?
        Answer q1203 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1203", "No")
        //Was your most recent marriage annulled?


        Answer q1206 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1206", "Angela")
        //Given Name (First name)
        Answer q1207 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1207", "Dorothea")
        //Middle Name (Do Not Abbreviate)
        Answer q1208 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1208", "Merkel")
        //Family Name/Last Name/Surname
        Answer q1209 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1209", DateUtil.fromDate(LocalDate.of(2016, Month.JANUARY, 17)))
        //Date of Marriage
        Answer q1210 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1210", "India")
        //In what country did this marriage take place?
        Answer q1211 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1211", "240 Hayat Regency")
        //On what Street Number and Name did the marriage take place?
        Answer q1212 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1212", "No")
        //Does your place of marriage address have a secondary description (i.e. apartment, suite, or floor)?
        Answer q1215 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1215", "Chennai")
        //In what City/Town/Village did this marriage take place?
        Answer q1220 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1220", "No")
        //Were you married previously?
        Answer q1217 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1217", "Tamilnadu")
        //In what Province/Territory/Prefecture/Parish did your marriage take place?
        Answer q1219 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1219", "629001")
        //In what Postal Code did this marriage take place?

        Answer q1230 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1230", "Hugh")
        //Given Name (First name)
        Answer q1231 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1231", "Jack")
        //Middle Name (Do Not Abbreviate)
        Answer q1232 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1232", "Man")
        //Family Name/Last Name/Surname
        Answer q1233 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1233", "Male")
        //Gender
        Answer q1234 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1234", "Australia")
        //Country of Birth
        Answer q1235 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1235", "Brisbane")
        //City/Town/Village of Birth
        Answer q1236 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1236", DateUtil.fromDate(LocalDate.of(1975, Month.JANUARY, 17)))
        //Date of Birth
        Answer q1237 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1237", "Brisbane")
        //Current City/Town/Village of Residence
        Answer q1238 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1238", "Australia")
        //Current Country of Residence


        Answer q1240 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1240", "William")
        //Given Name (First name)
        Answer q1241 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1241", "Bare")
        //Middle Name (Do Not Abbreviate)
        Answer q1242 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1242", "Gills")
        //Family Name/Last Name/Surname
        Answer q1243 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1243", "Female")
        //Gender
        Answer q1244 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1244", "Australia")
        //Country of Birth
        Answer q1245 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1245", "Melbourne")
        //City/Town/Village of Birth
        Answer q1246 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1246", DateUtil.fromDate(LocalDate.of(1980, Month.JANUARY, 17)))
        //Date of Birth
        Answer q1247 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1247", "Melbourne")
        //Current City/Town/Village of Residence
        Answer q1248 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1248", "Australia")
        //Current Country of Residence


        Answer q1250 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1250", "No")
        //Do you have any children under 18 years of age?


        Answer q1268 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1268", "No")
        //Do you have any dependents who are children? Help


        Answer q1290 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1290", "No")
        //Do you have any dependents who are NOT children? Help


        Answer q1311 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdSizeDependents/Q_1311", hhSize)
        //How many lawful permanent residents whom you are currently obligated to support based on your previous submission of Form I-864 as a petitioning, substitute, or joint sponsor, or Form I-864EZ, Affidavit of Support Under Section 213A of the INA, as a petitioning sponsor?

        Answer q1313 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdSizeDependents/Q_1313", "No")
        //Do you have another person who is willing to be a sponsor of the people immigrating to the United States under this(these) application(s)?

        Answer q1314 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdSizeDependents/Q_1314", "No")
        //Do you have any siblings, parents, or adult children living in your same principal residence who will be combing their income with yours to support the Beneficiaries in this application, when they come to the United States?


        Answer q1316 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1316", "No")
        //Do  you have any siblings, parents, or adult children (living in your same residence) who will be combing their income/assets to assist in supporting the beneficiary/beneficiaries in this application?

        List<Answer> answerList = []
        answerList.add(q1201)
        answerList.add(q1202)
        answerList.add(q1204)
        answerList.add(q1203)

        answerList.add(q1206)
        answerList.add(q1207)
        answerList.add(q1208)
        answerList.add(q1209)
        answerList.add(q1210)
        answerList.add(q1211)
        answerList.add(q1212)
        answerList.add(q1215)
        answerList.add(q1220)
        answerList.add(q1217)
        answerList.add(q1219)


        answerList.add(q1230)
        answerList.add(q1231)
        answerList.add(q1232)
        answerList.add(q1233)
        answerList.add(q1234)
        answerList.add(q1235)
        answerList.add(q1236)
        answerList.add(q1237)
        answerList.add(q1238)

        answerList.add(q1240)
        answerList.add(q1241)
        answerList.add(q1242)
        answerList.add(q1243)
        answerList.add(q1244)
        answerList.add(q1245)
        answerList.add(q1246)
        answerList.add(q1247)
        answerList.add(q1248)

        answerList.add(q1250)

        answerList.add(q1268)

        answerList.add(q1290)

        answerList.add(q1311)
        answerList.add(q1313)
        answerList.add(q1314)

        answerList.add(q1316)


        //PovertyThreshold Field Paths
        Answer stateAnswer = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentMailingAddress/Q_76", state)
        //State

        Answer militaryServiceAnswer = getAnswerInstance(packageId, applicantId, "Sec_criminalAndCivilHistory/SubSec_currentMilitaryService/Q_1102", military)
        //Are you currently serving in the Armed Forces of the United States?

        answerList.add(stateAnswer)
        answerList.add(militaryServiceAnswer)

        //PetitionerIncome Filed Paths
        /*Answer peitionerIncomeAnswer = getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_132", "18420")
        //What is your current individual annual income? Help
        answerList.add(peitionerIncomeAnswer)*/

        Answer doYouHaveMoneyInBanks = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_901", "Yes")
        //Do you have any money in banks or other financial institutions?
        answerList.add(doYouHaveMoneyInBanks)

        Answer financialInstitueName_0 = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_902/0", "Newyork Mellon")
        //Name of U.S. Financial Institution

        Answer accountType_0 = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_903/0", "Checking")
        //Account Type

        Answer accountLocation_0 = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_904/0", state)
        //Account Location

        Answer currentBalance_0 = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_907/0", asset)
        //Current Balance

        answerList.add(financialInstitueName_0)
        answerList.add(accountType_0)
        answerList.add(accountLocation_0)
        answerList.add(currentBalance_0)

        return answerList

    }

    static List<Answer> belowPovertyGuidelineAnswerList(Long packageId, Long applicantId) {
        Answer q1201 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1201", "Yes")
//Have you ever been married?
        Answer q1202 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1202", "1")
//How many times have you been married?
        Answer q1204 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1204", "Married")
//What is your current marital status?
        Answer q1203 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1203", "No")
//Was your most recent marriage annulled?


        Answer q1206 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1206", "Angela")
//Given Name (First name)
        Answer q1207 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1207", "Dorothea")
//Middle Name (Do Not Abbreviate)
        Answer q1208 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1208", "Merkel")
//Family Name/Last Name/Surname
        Answer q1209 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1209", DateUtil.fromDate(LocalDate.of(2016, Month.JANUARY, 17)))
//Date of Marriage
        Answer q1210 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1210", "India")
//In what country did this marriage take place?
        Answer q1211 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1211", "240 Hayat Regency")
//On what Street Number and Name did the marriage take place?
        Answer q1212 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1212", "No")
//Does your place of marriage address have a secondary description (i.e. apartment, suite, or floor)?
        Answer q1215 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1215", "Chennai")
//In what City/Town/Village did this marriage take place?
        Answer q1220 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1220", "No")
//Were you married previously?
        Answer q1217 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1217", "Tamilnadu")
//In what Province/Territory/Prefecture/Parish did your marriage take place?
        Answer q1219 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1219", "629001")
//In what Postal Code did this marriage take place?


        Answer q1230 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1230", "Hugh")
//Given Name (First name)
        Answer q1231 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1231", "Jack")
//Middle Name (Do Not Abbreviate)
        Answer q1232 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1232", "Man")
//Family Name/Last Name/Surname
        Answer q1233 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1233", "Male")
//Gender
        Answer q1234 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1234", "Australia")
//Country of Birth
        Answer q1235 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1235", "Brisbane")
//City/Town/Village of Birth
        Answer q1236 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1236", DateUtil.fromDate(LocalDate.of(1975, Month.JANUARY, 17)))
//Date of Birth
        Answer q1237 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1237", "Brisbane")
//Current City/Town/Village of Residence
        Answer q1238 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1238", "Australia")
//Current Country of Residence


        Answer q1240 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1240", "William")
//Given Name (First name)
        Answer q1241 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1241", "Bare")
//Middle Name (Do Not Abbreviate)
        Answer q1242 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1242", "Gills")
//Family Name/Last Name/Surname
        Answer q1243 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1243", "Female")
//Gender
        Answer q1244 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1244", "Australia")
//Country of Birth
        Answer q1245 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1245", "Melbourne")
//City/Town/Village of Birth
        Answer q1246 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1246", DateUtil.fromDate(LocalDate.of(1980, Month.JANUARY, 17)))
//Date of Birth
        Answer q1247 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1247", "Melbourne")
//Current City/Town/Village of Residence
        Answer q1248 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1248", "Australia")
//Current Country of Residence


        Answer q1250 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1250", "No")
//Do you have any children under 18 years of age?


        Answer q1268 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1268", "No")
//Do you have any dependents who are children? Help


        Answer q1290 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1290", "No")
//Do you have any dependents who are NOT children? Help


        Answer q1311 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdSizeDependents/Q_1311", "3")
//How many lawful permanent residents whom you are currently obligated to support based on your previous submission of Form I-864 as a petitioning, substitute, or joint sponsor, or Form I-864EZ, Affidavit of Support Under Section 213A of the INA, as a petitioning sponsor?
        Answer q1313 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdSizeDependents/Q_1313", "No")
//Do you have another person who is willing to be a sponsor of the people immigrating to the United States under this(these) application(s)?
        Answer q1314 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdSizeDependents/Q_1314", "Yes")
//Do you have any siblings, parents, or adult children living in your same principal residence who will be combing their income with yours to support the Beneficiaries in this application, when they come to the United States?


        Answer q1316 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1316", "Yes")
//Do  you have any siblings, parents, or adult children (living in your same residence) who will be combing their income/assets to assist in supporting the beneficiary/beneficiaries in this application?
        Answer q1317_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1317/0", "Stephen")
//Given Name (First name)
        Answer q1318_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1318/0", "Mark")
//Middle Name (Do Not Abbreviate)
        Answer q1319_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1319/0", "Waugh")
//Family Name/Last Name/Surname
        Answer q1320_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1320/0", "Sibling")
//Relationship to you
        Answer q1321_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1321/0", "60000")
//Annual Income

        List<Answer> answerList = []
        answerList.add(q1201)
        answerList.add(q1202)
        answerList.add(q1204)
        answerList.add(q1203)

        answerList.add(q1206)
        answerList.add(q1207)
        answerList.add(q1208)
        answerList.add(q1209)
        answerList.add(q1210)
        answerList.add(q1211)
        answerList.add(q1212)
        answerList.add(q1215)
        answerList.add(q1220)
        answerList.add(q1217)
        answerList.add(q1219)


        answerList.add(q1230)
        answerList.add(q1231)
        answerList.add(q1232)
        answerList.add(q1233)
        answerList.add(q1234)
        answerList.add(q1235)
        answerList.add(q1236)
        answerList.add(q1237)
        answerList.add(q1238)

        answerList.add(q1240)
        answerList.add(q1241)
        answerList.add(q1242)
        answerList.add(q1243)
        answerList.add(q1244)
        answerList.add(q1245)
        answerList.add(q1246)
        answerList.add(q1247)
        answerList.add(q1248)

        answerList.add(q1250)

        answerList.add(q1268)

        answerList.add(q1290)

        answerList.add(q1311)
        answerList.add(q1313)
        answerList.add(q1314)

        answerList.add(q1316)
        answerList.add(q1317_0)
        answerList.add(q1318_0)
        answerList.add(q1319_0)
        answerList.add(q1320_0)
        answerList.add(q1321_0)

        //PovertyThreshold Filed Paths
        Answer stateAnswer = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_48", "Alaska")
//State
        Answer houseHoldSizeAnswer = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdSizeDependents/Q_1311", "3")
//How many lawful permanent residents whom you are currently obligated to support based on your previous submission of Form I-864 as a petitioning, substitute, or joint sponsor, or Form I-864EZ, Affidavit of Support Under Section 213A of the INA, as a petitioning sponsor?
        Answer militaryServiceAnswer = getAnswerInstance(packageId, applicantId, "Sec_criminalAndCivilHistory/SubSec_currentMilitaryService/Q_1102", "Yes")
//Are you currently serving in the Armed Forces of the United States?
        answerList.add(stateAnswer)
        //answerList.add(houseHoldSizeAnswer)
        answerList.add(militaryServiceAnswer)

        //PetitionerIncome Filed Paths
        Answer peitionerIncomeAnswer = getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_132", "18420")
//What is your current individual annual income? Help
        answerList.add(peitionerIncomeAnswer)

        Answer doYouHaveMoneyInBanks = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_901", "Yes")
//Do you have any money in banks or other financial institutions?
        answerList.add(doYouHaveMoneyInBanks)

        Answer financialInstitueName_0 = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_902/0", "Newyork Mellon")
//Name of U.S. Financial Institution
        Answer accountType_0 = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_903/0", "Savings")
//Account Type
        Answer accountLocation_0 = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_904/0", "Alaska")
//Account Location
        Answer currentBalance_0 = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_907/0", "2000")
//Current Balance
        answerList.add(financialInstitueName_0)
        answerList.add(accountType_0)
        answerList.add(accountLocation_0)
        answerList.add(currentBalance_0)

        Answer financialInstitueName_1 = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_902/1", "Bank Of America")
//Name of U.S. Financial Institution
        Answer accountType_1 = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_903/1", "Salary")
//Account Type
        Answer accountLocation_1 = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_904/1", "Alaska")
//Account Location
        Answer currentBalance_1 = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_907/1", "4000")
//Current Balance
        answerList.add(financialInstitueName_1)
        answerList.add(accountType_1)
        answerList.add(accountLocation_1)
        answerList.add(currentBalance_1)

        return answerList
    }


    static List<Answer> inadmissibilityAndOtherLegalIssueAnswerList(Long packageId, Long applicantId) {
        Answer q3029 = getAnswerInstance(packageId, applicantId, "Sec_inadmissibilityAndOtherLegalIssues/SubSec_immigrationHistoryGeneral/Q_3029", "No")
//Have you EVER been a member of, involved in, or in any way associated with any organization, association, fund, foundation, party, club, society, or similar group in the United States or in any other location in the world including any military service?
        Answer q3038 = getAnswerInstance(packageId, applicantId, "Sec_inadmissibilityAndOtherLegalIssues/SubSec_immigrationHistoryGeneral/Q_3038", "Yes")
//Have you EVER been denied admission to the United States?
        Answer q3039 = getAnswerInstance(packageId, applicantId, "Sec_inadmissibilityAndOtherLegalIssues/SubSec_immigrationHistoryGeneral/Q_3039", "Yes, I have been denied admission to the US")
//Please explain the events and circumstances:
        Answer q3040 = getAnswerInstance(packageId, applicantId, "Sec_inadmissibilityAndOtherLegalIssues/SubSec_immigrationHistoryGeneral/Q_3040", "Yes")
//Have you EVER been denied a visa to the United States?
        Answer q3041 = getAnswerInstance(packageId, applicantId, "Sec_inadmissibilityAndOtherLegalIssues/SubSec_immigrationHistoryGeneral/Q_3041", "Please explain the events")
//Please explain the events and circumstances:

        List<Answer> answerList = []
        answerList.add(q3029)
        answerList.add(q3038)
        answerList.add(q3039)
        answerList.add(q3040)
        answerList.add(q3041)

        return answerList
    }


    static List<Answer> abovePovertyGuidelineAnswerList(Long packageId, Long applicantId) {
        Answer q1201 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1201", "Yes")
//Have you ever been married?
        Answer q1202 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1202", "1")
//How many times have you been married?
        Answer q1204 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1204", "Married")
//What is your current marital status?
        Answer q1203 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1203", "No")
//Was your most recent marriage annulled?


        Answer q1206 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1206", "Angela")
//Given Name (First name)
        Answer q1207 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1207", "Dorothea")
//Middle Name (Do Not Abbreviate)
        Answer q1208 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1208", "Merkel")
//Family Name/Last Name/Surname
        Answer q1209 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1209", DateUtil.fromDate(LocalDate.of(2016, Month.JANUARY, 17)))
//Date of Marriage
        Answer q1210 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1210", "India")
//In what country did this marriage take place?
        Answer q1211 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1211", "240 Hayat Regency")
//On what Street Number and Name did the marriage take place?
        Answer q1212 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1212", "No")
//Does your place of marriage address have a secondary description (i.e. apartment, suite, or floor)?
        Answer q1215 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1215", "Chennai")
//In what City/Town/Village did this marriage take place?
        Answer q1220 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1220", "No")
//Were you married previously?
        Answer q1217 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1217", "Tamilnadu")
//In what Province/Territory/Prefecture/Parish did your marriage take place?
        Answer q1219 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_currentSpouse/Q_1219", "629001")
//In what Postal Code did this marriage take place?


        Answer q1230 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1230", "Hugh")
//Given Name (First name)
        Answer q1231 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1231", "Jack")
//Middle Name (Do Not Abbreviate)
        Answer q1232 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1232", "Man")
//Family Name/Last Name/Surname
        Answer q1233 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1233", "Male")
//Gender
        Answer q1234 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1234", "Australia")
//Country of Birth
        Answer q1235 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1235", "Brisbane")
//City/Town/Village of Birth
        Answer q1236 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1236", DateUtil.fromDate(LocalDate.of(1975, Month.JANUARY, 17)))
//Date of Birth
        Answer q1237 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1237", "Brisbane")
//Current City/Town/Village of Residence
        Answer q1238 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent1/Q_1238", "Australia")
//Current Country of Residence


        Answer q1240 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1240", "William")
//Given Name (First name)
        Answer q1241 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1241", "Bare")
//Middle Name (Do Not Abbreviate)
        Answer q1242 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1242", "Gills")
//Family Name/Last Name/Surname
        Answer q1243 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1243", "Female")
//Gender
        Answer q1244 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1244", "Australia")
//Country of Birth
        Answer q1245 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1245", "Melbourne")
//City/Town/Village of Birth
        Answer q1246 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1246", DateUtil.fromDate(LocalDate.of(1980, Month.JANUARY, 17)))
//Date of Birth
        Answer q1247 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1247", "Melbourne")
//Current City/Town/Village of Residence
        Answer q1248 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_parent2/Q_1248", "Australia")
//Current Country of Residence


        Answer q1250 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1250", "No")
//Do you have any children under 18 years of age?


        Answer q1268 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsChildren/Q_1268", "No")
//Do you have any dependents who are children? Help


        Answer q1290 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_dependentsNonChildren/Q_1290", "No")
//Do you have any dependents who are NOT children? Help


        Answer q1311 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdSizeDependents/Q_1311", "2")
//How many lawful permanent residents whom you are currently obligated to support based on your previous submission of Form I-864 as a petitioning, substitute, or joint sponsor, or Form I-864EZ, Affidavit of Support Under Section 213A of the INA, as a petitioning sponsor?
        Answer q1313 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdSizeDependents/Q_1313", "No")
//Do you have another person who is willing to be a sponsor of the people immigrating to the United States under this(these) application(s)?
        Answer q1314 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdSizeDependents/Q_1314", "Yes")
//Do you have any siblings, parents, or adult children living in your same principal residence who will be combing their income with yours to support the Beneficiaries in this application, when they come to the United States?


        Answer q1316 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1316", "Yes")
//Do  you have any siblings, parents, or adult children (living in your same residence) who will be combing their income/assets to assist in supporting the beneficiary/beneficiaries in this application?
        Answer q1317_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1317/0", "Stephen")
//Given Name (First name)
        Answer q1318_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1318/0", "Mark")
//Middle Name (Do Not Abbreviate)
        Answer q1319_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1319/0", "Waugh")
//Family Name/Last Name/Surname
        Answer q1320_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1320/0", "Sibling")
//Relationship to you
        Answer q1321_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdIncome/Q_1321/0", "60000")
//Annual Income

        List<Answer> answerList = []
        answerList.add(q1201)
        answerList.add(q1202)
        answerList.add(q1204)
        answerList.add(q1203)

        answerList.add(q1206)
        answerList.add(q1207)
        answerList.add(q1208)
        answerList.add(q1209)
        answerList.add(q1210)
        answerList.add(q1211)
        answerList.add(q1212)
        answerList.add(q1215)
        answerList.add(q1220)
        answerList.add(q1217)
        answerList.add(q1219)


        answerList.add(q1230)
        answerList.add(q1231)
        answerList.add(q1232)
        answerList.add(q1233)
        answerList.add(q1234)
        answerList.add(q1235)
        answerList.add(q1236)
        answerList.add(q1237)
        answerList.add(q1238)

        answerList.add(q1240)
        answerList.add(q1241)
        answerList.add(q1242)
        answerList.add(q1243)
        answerList.add(q1244)
        answerList.add(q1245)
        answerList.add(q1246)
        answerList.add(q1247)
        answerList.add(q1248)

        answerList.add(q1250)

        answerList.add(q1268)

        answerList.add(q1290)

        answerList.add(q1311)
        answerList.add(q1313)
        answerList.add(q1314)

        answerList.add(q1316)
        answerList.add(q1317_0)
        answerList.add(q1318_0)
        answerList.add(q1319_0)
        answerList.add(q1320_0)
        answerList.add(q1321_0)

        //PovertyThreshold Filed Paths
        Answer stateAnswer = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_48", "Florida")
//State
        Answer houseHoldSizeAnswer = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_householdSizeDependents/Q_1311", "2")
//How many lawful permanent residents whom you are currently obligated to support based on your previous submission of Form I-864 as a petitioning, substitute, or joint sponsor, or Form I-864EZ, Affidavit of Support Under Section 213A of the INA, as a petitioning sponsor?
        Answer militaryServiceAnswer = getAnswerInstance(packageId, applicantId, "Sec_criminalAndCivilHistory/SubSec_currentMilitaryService/Q_1101", "No")
//Are you currently serving in the Armed Forces of the United States?
        answerList.add(stateAnswer)
        //answerList.add(houseHoldSizeAnswer)
        answerList.add(militaryServiceAnswer)

        //PetitionerIncome Filed Paths
        Answer peitionerIncomeAnswer = getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_132", "18420")
//What is your current individual annual income? Help
        answerList.add(peitionerIncomeAnswer)

        Answer doYouHaveMoneyInBanks = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_901", "Yes")
//Do you have any money in banks or other financial institutions?
        answerList.add(doYouHaveMoneyInBanks)

        Answer financialInstitueName_0 = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_902/0", "Newyork Mellon")
//Name of U.S. Financial Institution
        Answer accountType_0 = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_903/0", "Savings")
//Account Type
        Answer accountLocation_0 = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_904/0", "Alaska")
//Account Location
        Answer currentBalance_0 = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_907/0", "2000")
//Current Balance
        answerList.add(financialInstitueName_0)
        answerList.add(accountType_0)
        answerList.add(accountLocation_0)
        answerList.add(currentBalance_0)

        Answer financialInstitueName_1 = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_902/1", "Bank Of America")
//Name of U.S. Financial Institution
        Answer accountType_1 = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_903/1", "Salary")
//Account Type
        Answer accountLocation_1 = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_904/1", "Alaska")
//Account Location
        Answer currentBalance_1 = getAnswerInstance(packageId, applicantId, "Sec_assets/SubSec_bankDeposits/Q_907/1", "14000")
//Current Balance
        answerList.add(financialInstitueName_1)
        answerList.add(accountType_1)
        answerList.add(accountLocation_1)
        answerList.add(currentBalance_1)

        return answerList
    }

    static List<Answer> form134ExclusionIntroSectionAnswerList(Long packageId, Long applicantId) {
        Answer q27 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_4/Q_27", "spouse")
//How is the Beneficiary related to you?
        List<Answer> answerList = []
        answerList.add(q27)
        return answerList
    }


    static List<Answer> form134ExclusionAssetsSectionAnswerList(Long packageId, Long applicantId) {
        Answer q27 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_4/Q_27", "spouse")
//How is the Beneficiary related to you?
        List<Answer> answerList = []
        answerList.add(q27)
        return answerList
    }


    static List<Answer> form134ExclusionIncomeHistoryAnswerList(Long packageId, Long applicantId) {
        Answer q27 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_4/Q_27", "spouse")
//How is the Beneficiary related to you?

        Answer q131 = getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_131", "yes")
//Have you filed a Federal income tax return for each of the three most recent tax years?
        Answer q132 = getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_132", "2000")
//What is your current individual annual income?
        Answer q133 = getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_133", "2018")
//Select the most recent year that you filed federal income taxes
        Answer q134 = getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_134", "24000")
//What is your total annual income for the most recent tax year?

        List<Answer> answerList = []
        answerList.add(q27)
        answerList.add(q131)
        answerList.add(q132)
        answerList.add(q133)
        answerList.add(q134)
        return answerList
    }

    static List<Answer> form864ExclusionIncomeHistoryAnswerList(Long packageId, Long applicantId) {
        Answer q27 = getAnswerInstance(packageId, applicantId, "Sec_1/SubSec_4/Q_27", "fiance")
//How is the Beneficiary related to you?

        Answer q131 = getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_131", "yes")
//Have you filed a Federal income tax return for each of the three most recent tax years?
        Answer q132 = getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_132", "2000")
//What is your current individual annual income?
        Answer q133 = getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_133", "2018")
//Select the most recent year that you filed federal income taxes
        Answer q134 = getAnswerInstance(packageId, applicantId, "Sec_incomeHistory/SubSec_incomeHistory/Q_134", "24000")
//What is your total annual income for the most recent tax year?

        List<Answer> answerList = []
        answerList.add(q27)
        answerList.add(q131)
        answerList.add(q132)
        answerList.add(q133)
        answerList.add(q134)
        return answerList
    }

    static List<Answer> petitionerBirthInformationAnswerList(Long packageId, Long applicantId) {
        Answer q87 = getAnswerInstance(packageId, applicantId, "Sec_birthInformation/SubSec_birthInformation/Q_87", "Male")
//Gender
        Answer q88 = getAnswerInstance(packageId, applicantId, "Sec_birthInformation/SubSec_birthInformation/Q_88", DateUtil.fromDate(LocalDate.of(1988, Month.FEBRUARY, 11)))
//Date of Birth
        Answer q89 = getAnswerInstance(packageId, applicantId, "Sec_birthInformation/SubSec_birthInformation/Q_89", "United States")
//Country of Birth
        Answer q90 = getAnswerInstance(packageId, applicantId, "Sec_birthInformation/SubSec_birthInformation/Q_90", "FL")
//City/Town/Village
        Answer q91 = getAnswerInstance(packageId, applicantId, "Sec_birthInformation/SubSec_birthInformation/Q_91", "Florida")
//State
        Answer q93 = getAnswerInstance(packageId, applicantId, "Sec_birthInformation/SubSec_birthInformation/Q_93", "12345")
//ZIP Code

        List<Answer> answerList = []
        answerList.add(q87)
        answerList.add(q88)
        answerList.add(q89)
        answerList.add(q90)
        answerList.add(q91)
        answerList.add(q93)
        return answerList
    }


    static List<Answer> beneficiaryBirthInformationAnswerList(Long packageId, Long applicantId) {
        Answer q2201 = getAnswerInstance(packageId, applicantId, "Sec_birthInformationForBeneficiary/SubSec_birthInformationForBeneficiary/Q_2201", "Female")
//Gender
        Answer q2202 = getAnswerInstance(packageId, applicantId, "Sec_birthInformationForBeneficiary/SubSec_birthInformationForBeneficiary/Q_2202", DateUtil.fromDate(LocalDate.of(2000, Month.JANUARY, 17)))
//Date of Birth
        Answer q2203 = getAnswerInstance(packageId, applicantId, "Sec_birthInformationForBeneficiary/SubSec_birthInformationForBeneficiary/Q_2203", "Australia")
//Country of Birth
        Answer q2204 = getAnswerInstance(packageId, applicantId, "Sec_birthInformationForBeneficiary/SubSec_birthInformationForBeneficiary/Q_2204", "Queensland")
//Province/State/Territory/Prefecture/Parish
        Answer q2205 = getAnswerInstance(packageId, applicantId, "Sec_birthInformationForBeneficiary/SubSec_birthInformationForBeneficiary/Q_2205", "Brisbane")
//City/Town/Village
        Answer q2206 = getAnswerInstance(packageId, applicantId, "Sec_birthInformationForBeneficiary/SubSec_birthInformationForBeneficiary/Q_2206", "629001")
//Postal Code

        List<Answer> answerList = []
        answerList.add(q2201)
        answerList.add(q2202)
        answerList.add(q2203)
        answerList.add(q2204)
        answerList.add(q2205)
        answerList.add(q2206)
        return answerList
    }


    static List<Answer> travelToUSSectionPdfPrintTextRuleAnswerList(Long packageId, Long applicantId) {
        // Have you ever been in the U.S?
        Answer q2993 = getAnswerInstance(packageId, applicantId, "Sec_travelToTheUnitedStates/SubSec_previousVisitsToTheUnitedStates/Q_2993", "Yes")
        //Are you currently in the United States?
        Answer q2950 = getAnswerInstance(packageId, applicantId, "Sec_travelToTheUnitedStates/SubSec_currentStatusInTheUnitedStates/Q_2950", "Yes")
        //What was your legal status when you last entered the United States?
        Answer q2951 = getAnswerInstance(packageId, applicantId, "Sec_travelToTheUnitedStates/SubSec_currentStatusInTheUnitedStates/Q_2951", "without_inspection")

        List<Answer> answerList = []
        answerList.add(q2993)
        answerList.add(q2950)
        answerList.add(q2951)
        return answerList
    }

    static List<Answer> form601ExtremeHardshipSectionPdfPrintTextRuleAnswerList(Long packageId, Long applicantId) {
        Answer q3601 = getAnswerInstance(packageId, applicantId, "Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3602", "Yes")
//Do you have a spouse, parent, child, fiancé(e), or child of a fiancé(e) who either has or will experience "extreme hardship" in the future if you are not granted a waiver of your inadmissibility to the United States? This person MUST be either a U.S. citizen or  LPR (Lawful Permanent Resident).
        Answer q3651_0 = getAnswerInstance(packageId, applicantId, "Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3651/0", "uscitizen_parent")
//What is your relationship to the relative who will experience extreme hardship if you are refused admission into the United States?
        List<Answer> answerList = []
        answerList.add(q3601)
        answerList.add(q3651_0)
        return answerList
    }

    static List<Answer> form601AExtremeHardshipSectionPdfPrintTextRuleAnswerList(Long packageId, Long applicantId) {
        Answer q3601 = getAnswerInstance(packageId, applicantId, "Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3601", "Yes")
//Do you have a spouse or parent that would suffer extreme hardship, and therefore you would like him/her to be considered by the USCIS in deciding whether or not you should be admitted to the U.S.? This person MUST be either a U.S. citizen or  LPR (Lawful Permanent Resident).
        Answer q3652_0 = getAnswerInstance(packageId, applicantId, "Sec_extremeHardshipForRelatives/SubSec_extremeHardshipForRelatives/Q_3652/0", "uscitizen_spouse")
//What is your relationship to the relative who will experience extreme hardship if you are refused admission into the United States?
        List<Answer> answerList = []
        answerList.add(q3601)
        answerList.add(q3652_0)
        return answerList
    }


    static List<Answer> addressHistoryBeneficaryAnswerList(Long packageId, Long applicantId) {
        Integer currentYear = DateUtil.today().year
        Answer q2001 = getAnswerInstance(packageId, applicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2001", "Assisi")
//In Care of Name
        Answer q2002 = getAnswerInstance(packageId, applicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2002", "United States")
//In what country is your current physical address?
        Answer q2003 = getAnswerInstance(packageId, applicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2003", "400 North Car Street")
//Street Number and Name
        Answer q2004 = getAnswerInstance(packageId, applicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2004", "No")
//Does your address have a secondary description (i.e. apartment, suite, or floor)?
        Answer q2007 = getAnswerInstance(packageId, applicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2007", "Aurora")
//City/Town/Village
        Answer q2008 = getAnswerInstance(packageId, applicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2008", "Colorado")
//State
        Answer q2010 = getAnswerInstance(packageId, applicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2010", "80011")
//ZIP Code
        Answer q2012 = getAnswerInstance(packageId, applicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2012", DateUtil.fromDate(LocalDate.of(currentYear - 3, Month.JANUARY, 17)))
//When did you move into this address?


        Answer q2015 = getAnswerInstance(packageId, applicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentMailingAddressForBeneficiary/Q_2015", "No")
//Is your current mailing address the same as your current physical address?


        List<Answer> answerList = new ArrayList<>()
        answerList.add(q2001)
        answerList.add(q2002)
        answerList.add(q2003)
        answerList.add(q2004)
        answerList.add(q2007)
        answerList.add(q2008)
        answerList.add(q2010)
        answerList.add(q2012)

        answerList.add(q2015)

        return answerList
    }


    static List<Answer> addressHistoryBeneficaryAnswerListForForm751(Long packageId, Long applicantId, Boolean haveYouResidedAnyAddressSincePRBegan) {
        Integer currentYear = DateUtil.today().year
        Answer q2001 = getAnswerInstance(packageId, applicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2001", "Assisi")
//In Care of Name
        Answer q2002 = getAnswerInstance(packageId, applicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2002", "United States")
//In what country is your current physical address?
        Answer q2003 = getAnswerInstance(packageId, applicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2003", "400 North Car Street")
//Street Number and Name
        Answer q2004 = getAnswerInstance(packageId, applicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2004", "No")
//Does your address have a secondary description (i.e. apartment, suite, or floor)?
        Answer q2007 = getAnswerInstance(packageId, applicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2007", "Aurora")
//City/Town/Village
        Answer q2008 = getAnswerInstance(packageId, applicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2008", "Colorado")
//State
        Answer q2010 = getAnswerInstance(packageId, applicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2010", "80011")
//ZIP Code

        Answer q2015 = getAnswerInstance(packageId, applicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentMailingAddressForBeneficiary/Q_2015", "Yes")
//Is your current mailing address the same as your current physical address?

        LocalDate PRDate, moveinDate

        if (haveYouResidedAnyAddressSincePRBegan) {
            // Movein After PR
            PRDate = DateUtil.today().minusYears(2)
            moveinDate = DateUtil.today().minusYears(1)

        } else {
            // Movein Before PR
            PRDate = DateUtil.today().minusYears(1)
            moveinDate = DateUtil.today().minusYears(2)

        }
        Answer q2012 = getAnswerInstance(packageId, applicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2012", DateUtil.fromDate(moveinDate))

        // Date of Permanent Residence
        Answer q2028 = getAnswerInstance(packageId, applicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2028", DateUtil.fromDate(PRDate))


        List<Answer> answerList = new ArrayList<>()
        answerList.add(q2001)
        answerList.add(q2002)
        answerList.add(q2003)
        answerList.add(q2004)
        answerList.add(q2007)
        answerList.add(q2008)
        answerList.add(q2010)

        answerList.add(q2015)

        answerList.add(q2012)
        answerList.add(q2028)

        return answerList
    }

    static List<Answer> supportAndContributionsAnswerList(Long packageId, Long applicantId) {

        Answer q1402 = getAnswerInstance(packageId, applicantId, "Sec_supportAndContributions/SubSec_supportAndContributions/Q_1402", "Yes")
//Do you intend to make specific contributions to support of the beneficiary (and their derivative beneficiairies, if there are any) in this application?


        Answer q1404 = getAnswerInstance(packageId, applicantId, "Sec_supportAndContributions/SubSec_natureOfContributions/Q_1404", "Yes")
//Do you intend to make monetary contributions to the person you will be supporting?
        Answer q1405_0 = getAnswerInstance(packageId, applicantId, "Sec_supportAndContributions/SubSec_natureOfContributions/Q_1405/0", "United States")
//Describe the contribution
        Answer q1406_0 = getAnswerInstance(packageId, applicantId, "Sec_supportAndContributions/SubSec_natureOfContributions/Q_1406/0", "5000")
//If money, what is the U.S. dollar amount that you intend to give this person?
        Answer q1407_0 = getAnswerInstance(packageId, applicantId, "Sec_supportAndContributions/SubSec_natureOfContributions/Q_1407/0", "Annually")
//How frequently do you intend to make this contribution?
        Answer q1408_0 = getAnswerInstance(packageId, applicantId, "Sec_supportAndContributions/SubSec_natureOfContributions/Q_1408/0", "4")
//For how many [weeks, months, years] do you intend to continue making this contribution?
        Answer q1410_0 = getAnswerInstance(packageId, applicantId, "Sec_supportAndContributions/SubSec_natureOfContributions/Q_1410/0", "Colorado")
//Describe any other non-monetary contributions you intend to make to this person:


        List<Answer> answerList = new ArrayList<>()
        answerList.add(q1402)

        answerList.add(q1404)
        answerList.add(q1405_0)
        answerList.add(q1406_0)
        answerList.add(q1407_0)
        answerList.add(q1408_0)
        answerList.add(q1410_0)
        return answerList
    }


    static List<Answer> priorImmigrationProceedingsAnswerList(Long packageId, Long applicantId) {
        Answer q3081 = getAnswerInstance(packageId, applicantId, "Sec_inadmissibilityAndOtherLegalIssues/SubSec_priorImmigrationProceedings/Q_3081", "Yes")
        //Was the beneficiary EVER in immigration proceedings?
        Answer q3082_0 = getAnswerInstance(packageId, applicantId, "Sec_inadmissibilityAndOtherLegalIssues/SubSec_priorImmigrationProceedings/Q_3082/0", "Other Judicial Proceedings")
        //Select the type of proceedings
        Answer q3083_0 = getAnswerInstance(packageId, applicantId, "Sec_inadmissibilityAndOtherLegalIssues/SubSec_priorImmigrationProceedings/Q_3083/0", "New York City")
        //Provide the city of the proceedings
        Answer q3084_0 = getAnswerInstance(packageId, applicantId, "Sec_inadmissibilityAndOtherLegalIssues/SubSec_priorImmigrationProceedings/Q_3084/0", "New York")
        //Provide the state of the proceedings
        Answer q3085_0 = getAnswerInstance(packageId, applicantId, "Sec_inadmissibilityAndOtherLegalIssues/SubSec_priorImmigrationProceedings/Q_3085/0", DateUtil.fromDate(LocalDate.of(2016, Month.JANUARY, 17)))
        //Provide the date of the proceedings

        List<Answer> answerList = new ArrayList<>()
        answerList.add(q3081)
        answerList.add(q3082_0)
        answerList.add(q3083_0)
        answerList.add(q3084_0)
        answerList.add(q3085_0)
        return answerList
    }


    static List<Answer> populatePetitionerMarriedTimesVisibilityConstraintRuleAnswerList(Long packageId, Long applicantId, String everBeenMarriedValue) {
        Answer q1201 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1201", everBeenMarriedValue)
//Have you ever been married?
        Answer q1202 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1202", "2")
//How many times have you been married?
        Answer q1203 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1203", "No")
//Was your most recent marriage annulled?
        Answer q1204 = getAnswerInstance(packageId, applicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1204", "Married")
//What is your current marital status?

        List<Answer> answerList = new ArrayList<>()
        answerList.add(q1201)
        answerList.add(q1202)
        answerList.add(q1203)
        answerList.add(q1204)
        return answerList
    }


    static List<Answer> populateBeneficiaryMarriedTimesVisibilityConstraintRuleAnswerList(Long packageId, Long applicantId, String everBeenMarriedValue) {
        Answer q2778 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2778", everBeenMarriedValue)
//Have you ever been married?
        Answer q2779 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2779", "2")
//How many times have you been married?
        Answer q2780 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2780", "No")
//Was your most recent marriage annulled?
        Answer q2781 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2781", "Married")
//What is your current marital status?
        Answer q2783 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2783", "No")
//Do you have any previous marriages?

        List<Answer> answerList = new ArrayList<>()
        answerList.add(q2778)
        answerList.add(q2779)
        answerList.add(q2780)
        answerList.add(q2781)
        answerList.add(q2783)
        return answerList
    }


    static List<Answer> employmentHistoryPercentageAnswerList(Long packageId, Long applicantId) {
        Answer q1008_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1008/0", "Retired")
//What was your previous employment status?
        Answer q1011_0 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1011/0", DateUtil.fromDate(LocalDate.of(2019, Month.JANUARY, 17)))
//What was the date of your retirement?


        Answer q1008_1 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1008/1", "Unemployed")
//What was your previous employment status?
        Answer q1009_1 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1009/1", DateUtil.fromDate(LocalDate.of(2018, Month.MAY, 17)))
//What was the date you became unemployed?
        Answer q1010_1 = getAnswerInstance(packageId, applicantId, "Sec_employmentHistory/SubSec_employmentStatus/Q_1010/1", DateUtil.fromDate(LocalDate.of(2019, Month.JANUARY, 5)))
//What was the last date of your unemployment?


        List<Answer> answerList = new ArrayList<>()
        answerList.add(q1008_0)
        answerList.add(q1011_0)

        answerList.add(q1008_1)
        answerList.add(q1009_1)
        answerList.add(q1010_1)

        return answerList
    }

    static List<Answer> relationshipToPetitionerAnswerList1(Long packageId, Long applicantId) {

        Answer q1361 = getAnswerInstance(packageId, applicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1361", "United States")

        Answer q1367 = getAnswerInstance(packageId, applicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1367", "Colorado")

        Answer q1369 = getAnswerInstance(packageId, applicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1369", "80011")


        List<Answer> answerList = new ArrayList<>()
        answerList.add(q1361)
        answerList.add(q1367)
        answerList.add(q1369)

        return answerList
    }

    static List<Answer> relationshipToPetitionerAnswerList2(Long packageId, Long applicantId) {

        Answer q1361 = getAnswerInstance(packageId, applicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1361", "India")

        Answer q1368 = getAnswerInstance(packageId, applicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1368", "Tamilnadu")

        Answer q1370 = getAnswerInstance(packageId, applicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1370", "600005")


        List<Answer> answerList = new ArrayList<>()
        answerList.add(q1361)
        answerList.add(q1368)
        answerList.add(q1370)

        return answerList
    }


    static List<Answer> beneficiaryContactInformationAnswerList(Long packageId, Long applicantId) {
        Answer q2151 = getAnswerInstance(packageId, applicantId, "Sec_contactInformationForBeneficiary/SubSec_mobilePhoneNumberForBeneficiary/Q_2151", "Albania")
//Country
        Answer q2152 = getAnswerInstance(packageId, applicantId, "Sec_contactInformationForBeneficiary/SubSec_mobilePhoneNumberForBeneficiary/Q_2152", "9788931701")
//Phone Number (Including Area Code)
        Answer q2156 = getAnswerInstance(packageId, applicantId, "Sec_contactInformationForBeneficiary/SubSec_officePhoneNumberForBeneficiary/Q_2156", "Algeria")
//Country
        Answer q2157 = getAnswerInstance(packageId, applicantId, "Sec_contactInformationForBeneficiary/SubSec_officePhoneNumberForBeneficiary/Q_2157", "8148938971")
//Phone Number (Including Area Code)

        List<Answer> answerList = []
        answerList.add(q2151)
        answerList.add(q2152)
        answerList.add(q2156)
        answerList.add(q2157)

        return answerList
    }


    static List<Answer> populateDerivativeFamilyInformationAnswerList(Long packageId, Long applicantId, Boolean hasAddSpouseAsBeneficiary, Boolean hasAddChildAsBeneficiary) {
        // Do you have any children?
        Answer q2741 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2741", "yes")

        // Is [insert this child's name] applying with the Beneficiary, [insert Beneficiary Name]?
        Answer q2742_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2742/0", "no")
        // Child's Given Name (First name)
        Answer q2743_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2743/0", "Donald")
        // Child's Middle Name (Do Not Abbreviate)
        Answer q2746_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2746/0", "K")
        // Child's Family Name/Last Name/Surname
        Answer q2749_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2749/0", "Trumph")
        // Country of Birth
        Answer q2753_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2753/0", "Albania")
        // Date of Birth
        Answer q2754_0 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2754/0", DateUtil.fromDate(LocalDate.of(2019, Month.JANUARY, 5)))

        // Child's Given Name (First name)
        Answer q2743_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2743/1", "Sharfudeen")
        // Child's Middle Name (Do Not Abbreviate)
        Answer q2746_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2746/1", "A M")
        // Child's Family Name/Last Name/Surname
        Answer q2749_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2749/1", "Ashraf")
        // Country of Birth
        Answer q2753_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2753/1", "India")
        // Date of Birth
        Answer q2754_1 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2754/1", DateUtil.fromDate(LocalDate.of(1998, Month.FEBRUARY, 11)))

        // Is [insert this child's name] applying with the Beneficiary, [insert Beneficiary Name]?
        String addChildAsBeneficiaryAnsValue = hasAddChildAsBeneficiary ? "yes" : "no"
        Answer q2742_2 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2742/2", addChildAsBeneficiaryAnsValue)
        // Child's Given Name (First name)
        Answer q2743_2 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2743/2", "Mohamed")
        // Child's Middle Name (Do Not Abbreviate)
        Answer q2746_2 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2746/2", "A M")
        // Child's Family Name/Last Name/Surname
        Answer q2749_2 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2749/2", "Rizwan")
        // Country of Birth
        Answer q2753_2 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2753/2", "India")
        // Date of Birth
        Answer q2754_2 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2754/2", DateUtil.fromDate(LocalDate.of(1998, Month.FEBRUARY, 11)))

        // Is [insert this child's name] applying with the Beneficiary, [insert Beneficiary Name]?
        Answer q2742_3 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2742/3", "no")
        // Child's Given Name (First name)
        Answer q2743_3 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2743/3", "Bin")
        // Child's Middle Name (Do Not Abbreviate)
        Answer q2746_3 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2746/3", "K")
        // Child's Family Name/Last Name/Surname
        Answer q2749_3 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2749/3", "Joeden")
        // Country of Birth
        Answer q2753_3 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2753/3", "Algeria")
        // Date of Birth
        Answer q2754_3 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2754/3", DateUtil.fromDate(LocalDate.of(2010, Month.JANUARY, 5)))

        //Have you ever been married?
        String addSpouseAsBeneficiaryAnsValue = hasAddSpouseAsBeneficiary ? "yes" : "no"
        Answer q2778 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2778", addSpouseAsBeneficiaryAnsValue)
        //How many times have you been married?
        Answer q2779 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2779", "2")
        //Was your most recent marriage annulled?
        Answer q2780 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2780", "No")
        //What is your current marital status?
        Answer q2781 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2781", "Married")
        //Do you have any previous marriages?
        Answer q2783 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2783", "No")


        // Is [insert Beneficiary Name]'s spouse applying with the [insert Beneficiary Name]?
        Answer q2788 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_currentSpouseForBeneficiary/Q_2788", addSpouseAsBeneficiaryAnsValue)
        // Given Name (First name)
        Answer q2789 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_currentSpouseForBeneficiary/Q_2789", "Stephen")
        // Middle Name (Do Not Abbreviate)
        Answer q2790 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_currentSpouseForBeneficiary/Q_2790", "K")
        // Family Name/Last Name/Surname
        Answer q2791 = getAnswerInstance(packageId, applicantId, "Sec_familyInformationForBeneficiary/SubSec_currentSpouseForBeneficiary/Q_2791", "Peterson")


        List<Answer> answerList = new ArrayList<>()
        answerList.add(q2741)
        answerList.add(q2742_0)
        answerList.add(q2743_0)
        answerList.add(q2746_0)
        answerList.add(q2749_0)
        answerList.add(q2753_0)

        answerList.add(q2743_1)
        answerList.add(q2746_1)
        answerList.add(q2749_1)
        answerList.add(q2753_1)
        answerList.add(q2754_1)

        answerList.add(q2742_2)
        answerList.add(q2743_2)
        answerList.add(q2746_2)
        answerList.add(q2749_2)
        answerList.add(q2753_2)
        answerList.add(q2754_2)

        answerList.add(q2742_3)
        answerList.add(q2743_3)
        answerList.add(q2746_3)
        answerList.add(q2749_3)
        answerList.add(q2753_3)
        answerList.add(q2754_3)


        answerList.add(q2778)
        answerList.add(q2779)
        answerList.add(q2780)
        answerList.add(q2781)
        answerList.add(q2783)

        answerList.add(q2788)
        answerList.add(q2789)
        answerList.add(q2790)
        answerList.add(q2791)
        return answerList
    }


    static List<Answer> populatePetitionToRemoveConditionsOnResidenceAnswerList(Long packageId, Long applicantId, Boolean canAddRelationshipAnswer = true) {
        Answer q1615 = getAnswerInstance(packageId, applicantId, "Sec_introQuestionsForBeneficiary/SubSec_introQuestionsForBeneficiary/Q_1615", "Spouse")
//What is your relationship to the Petitioner (the U.S. Citizen or LPR (Lawful Permanent Resident)) who is filing to bring you to the United States? Help

        Answer q1701 = getAnswerInstance(packageId, applicantId, "Sec_basisPetitionToRemoveConditionsOnResidence/SubSec_basisPetitionToRemoveConditionsOnResidence/Q_1701", "Yes")
//Is the person you are currently married to, the same person who sponsored you for your conditional residence status (conditional 'green card')?
        Answer q1702 = getAnswerInstance(packageId, applicantId, "Sec_basisPetitionToRemoveConditionsOnResidence/SubSec_basisPetitionToRemoveConditionsOnResidence/Q_1702", "Individually")
//Are you filing to remove the conditions on your Temporary Residence with your spouse or parent's spouse OR are you filing on an individual basis?

        List<Answer> answerList = new ArrayList<>()
        if (canAddRelationshipAnswer) {
            answerList.add(q1615)
        }

        answerList.add(q1701)
        answerList.add(q1702)
        return answerList
    }


    static List<Answer> addressHistoryWith5YearsAnswerList(Long packageId, Long applicantId, LocalDate moveInDate) {
        Integer currentYear = DateUtil.today().year
        Answer q42 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_42", "United States")
//In what country is your current physical address?
        Answer q43 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_43", "400 North Car Street")
//Street Number and Name
        Answer q44 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_44", "Yes")
//Does your address have a secondary description (i.e. apartment, suite, or floor)?
        Answer q45 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_45", "Apartment")
//What is the secondary address description?
        Answer q46 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_46", "Samuel Apartments")
//Apartment/Suite/Floor Help
        Answer q47 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_47", "Aurora")
//City/Town/Village
        Answer q48 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_48", "Colorado")
//State
        Answer q50 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_50", "80011")
//ZIP Code
        Answer q52 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_52", DateUtil.fromDate(moveInDate))
//When did you move into this address?


        Answer q54 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_54/0", "Albania")
//In what country was this previous physical address?
        Answer q55 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_55/0", "23-A Mustafa Matohiti")
//Street Number and Name?
        Answer q56 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_56/0", "No")
//Does your address have a secondary description (i.e. apartment, suite, or floor)?2", "3423123098992001")//Apartment/Suite/Floor Help
        Answer q59 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_59/0", "Miladin")
//City/Town/Village
        Answer q61 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_61/0", "Tirana")
//Province/Territory/Prefecture/Parish
        Answer q63 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_63/0", "1031")
//Postal Code
        Answer q64 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_64/0", DateUtil.fromDate(LocalDate.of(currentYear - 4, Month.MAY, 1)))
//When did you move into this address?
        Answer q65 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_65/0", DateUtil.fromDate(LocalDate.of(currentYear - 3, Month.JANUARY, 15)))
//When did you move out of this address?


        Answer q68 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentMailingAddress/Q_68", "Edi Rama")
//In Care of Name
        Answer q69 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentMailingAddress/Q_69", "yes")
//Is your current mailing address the same as your current physical address?

        List<Answer> answerList = new ArrayList<>()
        answerList.add(q42)
        answerList.add(q43)
        answerList.add(q44)
        answerList.add(q45)
        answerList.add(q46)
        answerList.add(q47)
        answerList.add(q48)
        answerList.add(q50)
        answerList.add(q52)

        answerList.add(q54)
        answerList.add(q55)
        answerList.add(q56)
        answerList.add(q59)
        answerList.add(q61)
        answerList.add(q63)
        answerList.add(q64)
        answerList.add(q65)

        answerList.add(q68)
        answerList.add(q69)

        return answerList
    }


    static List<Answer> addressHistoryWith5YearsSplittedAnswerList(Long packageId, Long applicantId, LocalDate moveInDate,
                                                                   List<LocalDate> prevMoveInDates, List<LocalDate> prevMoveOutDates) {
        Answer q42 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_42", "United States")
//In what country is your current physical address?
        Answer q43 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_43", "400 North Car Street")
//Street Number and Name
        Answer q44 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_44", "Yes")
//Does your address have a secondary description (i.e. apartment, suite, or floor)?
        Answer q45 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_45", "Apartment")
//What is the secondary address description?
        Answer q46 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_46", "Samuel Apartments")
//Apartment/Suite/Floor Help
        Answer q47 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_47", "Aurora")
//City/Town/Village
        Answer q48 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_48", "Colorado")
//State
        Answer q50 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_50", "80011")
//ZIP Code
        Answer q52 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_52", DateUtil.fromDate(moveInDate))
//When did you move into this address?


        List<Answer> answerList = new ArrayList<>()
        answerList.add(q42)
        answerList.add(q43)
        answerList.add(q44)
        answerList.add(q45)
        answerList.add(q46)
        answerList.add(q47)
        answerList.add(q48)
        answerList.add(q50)
        answerList.add(q52)

        prevMoveInDates.eachWithIndex { LocalDate prevMoveInDate, int index ->
            Answer q64 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_64/${index}", DateUtil.fromDate(prevMoveInDate))
            //When did you move into this address?
            answerList.add(q64)
        }

        prevMoveOutDates.eachWithIndex { LocalDate prevMoveOutDate, int index ->
            Answer q65 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_65/${index}", DateUtil.fromDate(prevMoveOutDate))
            //When did you move out of this address?
            answerList.add(q65)
        }


        Answer q68 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentMailingAddress/Q_68", "Edi Rama")
//In Care of Name
        Answer q69 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentMailingAddress/Q_69", "yes")
//Is your current mailing address the same as your current physical address?

        answerList.add(q68)
        answerList.add(q69)

        return answerList
    }

    static List<Answer> addressHistoryForGapDays(Long packageId, Long applicantId, LocalDate moveInDate, List dateRows) {

        Answer q42 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_42", "United States")
//In what country is your current physical address?
        Answer q43 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_43", "400 North Car Street")
//Street Number and Name
        Answer q44 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_44", "Yes")
//Does your address have a secondary description (i.e. apartment, suite, or floor)?
        Answer q45 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_45", "Apartment")
//What is the secondary address description?
        Answer q46 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_46", "Samuel Apartments")
//Apartment/Suite/Floor Help
        Answer q47 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_47", "Aurora")
//City/Town/Village
        Answer q48 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_48", "Colorado")
//State
        Answer q50 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_50", "80011")
//ZIP Code
        Answer q52 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_52", DateUtil.fromDate(moveInDate))
//When did you move into this address?


        List<Answer> answerList = new ArrayList<>()
        answerList.add(q42)
        answerList.add(q43)
        answerList.add(q44)
        answerList.add(q45)
        answerList.add(q46)
        answerList.add(q47)
        answerList.add(q48)
        answerList.add(q50)
        answerList.add(q52)


        dateRows.eachWithIndex { def entry, int index ->
            if (entry.moveIn) {
                Answer q64 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_64/${index}", DateUtil.fromDate(entry.moveIn))
                answerList.add(q64)
            }
            if (entry.moveOut) {

                Answer q65 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_65/${index}", DateUtil.fromDate(entry.moveOut))
                answerList.add(q65)
            }
        }

        Answer q68 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentMailingAddress/Q_68", "Edi Rama")
//In Care of Name
        Answer q69 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentMailingAddress/Q_69", "yes")
//Is your current mailing address the same as your current physical address?

        answerList.add(q68)
        answerList.add(q69)

        return answerList
    }


    static List<Answer> addressHistoryForMailingAddressPovertyGuidelines(Long packageId, Long applicantId, String country, String state, String q69Answer, String domicile) {

        Answer q42 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_42", country)
//In what country is your current physical address?
        Answer q43 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_43", "400 North Car Street")
//Street Number and Name
        Answer q44 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_44", "Yes")
//Does your address have a secondary description (i.e. apartment, suite, or floor)?
        Answer q45 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_45", "Apartment")
//What is the secondary address description?
        Answer q46 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_46", "Samuel Apartments")
//Apartment/Suite/Floor Help
        Answer q47 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_47", "Aurora")
//City/Town/Village
        Answer q48 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_48", state)
//State
        Answer q50 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_50", "80011")
//ZIP Code

        Answer q53 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_53", domicile)
//Country of Domicile


        List<Answer> answerList = new ArrayList<>()
        answerList.add(q42)
        answerList.add(q43)
        answerList.add(q44)
        answerList.add(q45)
        answerList.add(q46)
        answerList.add(q47)
        answerList.add(q48)
        answerList.add(q50)
        answerList.add(q53)


        Answer q68 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentMailingAddress/Q_68", "Edi Rama")
        //In Care of Name
        answerList.add(q68)

        if (q69Answer) {
            Answer q69 = getAnswerInstance(packageId, applicantId, "Sec_addressHistory/SubSec_currentMailingAddress/Q_69", q69Answer)
            //Is your current mailing address the same as your current physical address?
            answerList.add(q69)
        }


        return answerList
    }

    static List<Answer> usCitizenLegalStatusRuleAnswerList(Long packageId, Long applicantId) {
        Answer q108 = getAnswerInstance(packageId, applicantId, "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_109", "united_states_citizen")
        //What is your Legal Status in the United States?

        List<Answer> answerList = []
        answerList.add(q108)
        return answerList;
    }

    static List<Answer> eligibilityCategoryForEAD(Long packageId, Long applicantId, String eligibilityCategory, String arrestQ) {

        Answer q6201 = getAnswerInstance(packageId, applicantId, "Sec_informationAboutEligibilityCategory/SubSec_eligibilityCategory/Q_6201", "Apply for Work Authorization")
        //Which of the following are you applying for?
        Answer q6202 = getAnswerInstance(packageId, applicantId, "Sec_informationAboutEligibilityCategory/SubSec_eligibilityCategory/Q_6202", eligibilityCategory)
        //Select the appropriate eligibility category that allows applicant to qualify for an EAD (Employment Authorization Document)

        List<Answer> answerList = new ArrayList<>()
        answerList.add(q6201)
        answerList.add(q6202)

        if (arrestQ) {
            Answer arrQ = getAnswerInstance(packageId, applicantId, "Sec_informationAboutEligibilityCategory/SubSec_eligibilityCategory/${arrestQ}", "yes")
            //Have you ever been arrested
            answerList.add(arrQ)
        }

        return answerList
    }

}
