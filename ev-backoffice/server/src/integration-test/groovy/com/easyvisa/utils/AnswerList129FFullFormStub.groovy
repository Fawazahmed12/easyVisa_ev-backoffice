package com.easyvisa.utils

import com.easyvisa.questionnaire.Answer
import com.easyvisa.questionnaire.util.DateUtil

import java.time.LocalDate
import java.time.Month

import static TestUtils.getAnswerInstance

class AnswerList129FFullFormStub {

    static List<Answer> form129FullFianceNoContinuationSheet(Long packageId, Long petitionerApplicantId, Long beneficiaryApplicantId, Boolean continuations) {
        Integer currentYear = LocalDate.now().getYear()
        Integer currentMonth = LocalDate.now().getMonthValue()
        List<Answer> answers = [
                //Page 1 Part 1A
                //Information About You
                //What is your Legal Status in the United States?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_112", "978893170"),
                //Alien Registration Number (A-Number)
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_113", "989407628"),
                //Social Security Number (If any)
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_114", "yes"),
                //Do you have a USCIS ELIS Account Number?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_legalStatusInUS/SubSec_legalStatusInUSndGovtIDNos/Q_115", "236541258963"),
                //How is the Beneficiary related to you?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_1/SubSec_4/Q_27", "fiance"),

                //Your Full Name
                //Family Name/Last Name/Surname
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_2/SubSec_5/Q_34", "Smith"),
                //Given Name (First name)
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_2/SubSec_5/Q_32", "William"),
                //Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_2/SubSec_5/Q_33", "Senior"),

                //Other Names Used
                //Have you ever used another name for your Given Name (First Name), Middle Name (including maiden names), or Family Name/Last Name/Surname?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_2/SubSec_6/Q_35", "yes"),
                //List any other Family Name/Last Name/Surname used (e.g. Smith II, Smith Jr.):
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_2/SubSec_6/Q_41/0", "Gills"),
                //List any other Given Name used (e.g. Rich, Rick, Dick):
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_2/SubSec_6/Q_37/0", "Willy"),
                //List any other Middle Name used:
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_2/SubSec_6/Q_39/0", "Junior"),

                //Your Mailing Address
                //List any other Given Name used (e.g. Rich, Rick, Dick):
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_addressHistory/SubSec_currentMailingAddress/Q_68", "William Smith Sr"),
                //Is your current mailing address the same as your current physical address?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_addressHistory/SubSec_currentMailingAddress/Q_69", "yes"),


                //Page 2
                //Your Address History
                //Physical Address 1 - current address
                //Street Number and Name
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_43", "123 Main street"),
                //Does your address have a secondary description (i.e. apartment, suite, or floor)?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_44", "yes"),
                //What is the secondary address description?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_45", "apartment"),
                //Apartment/Suite/Floor Help
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_46", "245"),
                //City/Town/Village
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_47", "Boston"),
                //State
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_48", "Massachusetts"),
                //ZIP Code
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_50", "98675"),
                //In what country is this current mailing address?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_42", "United States"),
                //When did you move into this address?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_addressHistory/SubSec_currentPhysicalAddress/Q_52", DateUtil.fromDate(LocalDate.of(currentYear - 2, Month.JANUARY, 25))),
                //When did you move out of this address? set to PRESENT

                //Physical Address 2 - previous address
                //Street Number and Name
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_55/0", "23 Boulevard Jean Jaurès"),
                //Does your address have a secondary description (i.e. apartment, suite, or floor)?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_56/0", "no"),
                //City/Town/Village
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_59/0", "Orléans"),
                //Province/Territory/Prefecture/Parish
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_61/0", "Loiret"),
                //Postal Code
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_63/0", "45000"),
                //In what country is this current mailing address?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_54/0", "France"),
                //When did you move into this address?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_64/0", DateUtil.fromDate(LocalDate.of(currentYear - 20, Month.JANUARY, 1))),
                //When did you move out of this address?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_addressHistory/SubSec_previousPhysicalAddress/Q_65/0", DateUtil.fromDate(LocalDate.of(currentYear - 2, Month.DECEMBER, 31))),
//TODO: update employment data
/*

                //Your Employment History
                //What is your current employment status?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_30/Q_1001", "employed"),
                //Were you ever employed, at any time (for more than 30 days), during the previous 5 years?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_30/Q_1003", "yes"),
                //Are you self-employed?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_30/Q_1004", "no"),

                //Employer 1
                //What is the full name of your current employer?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_31/Q_1009", "Harvard University"),
                //Street Number and Name
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_31/Q_1010", "30 Dunster Street"),
                //Does your address have a secondary description (i.e. apartment, suite, or floor)?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_31/Q_1011", "yes"),
                //What is the secondary address description?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_31/Q_1012", "floor"),
                //Apartment/Suite/Floor Help
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_31/Q_1013", "2"),
                //City/Town/Village
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_31/Q_1014", "Boston"),
                //State
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_31/Q_1015", "MA"),
                //ZIP Code
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_31/Q_1017", "02138"),
                //In what country is this current mailing address?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_31/Q_1008", "United States"),
                //What is your occupation?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_31/Q_1019", "Computer Science Teacher"),
                //What date did you begin working for this employer?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_31/Q_1020", DateUtil.fromDate(LocalDate.of(currentYear - 2, Month.FEBRUARY, 1))),
                //When did this employment end for this employer?
                //getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_31/Q_1021", "TO PRESENT"),

                //Employer 2
                //Were you self-employed at this previous employer?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_32/Q_1026", "no"),
                //What is the full name of your current employer?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_32/Q_1028", "TAO Agency"),
                //Street Number and Name
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_32/Q_1029", "4 Rue de la Hallebarde"),
                //Does your address have a secondary description (i.e. apartment, suite, or floor)?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_32/Q_1030", "no"),
                //City/Town/Village
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_32/Q_1033", "Orléans"),
                //Province/Territory/Prefecture/Parish
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_32/Q_1035", "Loiret"),
                //Postal Code
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_32/Q_1037", "45000"),
                //In what country is this current mailing address?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_32/Q_1025", "France"),
                //What was your occupation?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_32/Q_1038", "IT consultant"),


                //Page 3
                //What date did you begin working for this employer?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_32/Q_1039", DateUtil.fromDate(LocalDate.of(currentYear - 6, Month.MARCH, 15))),
                //When did this employment end for this employer?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_employmentHistory/SubSec_32/Q_1040", DateUtil.fromDate(LocalDate.of(currentYear - 3, Month.DECEMBER, 1))),
*/

                //Other Information
                //Gender
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_birthInformation/SubSec_birthInformation/Q_87", "male"),
                //Date of Birth
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_birthInformation/SubSec_birthInformation/Q_88", DateUtil.fromDate(LocalDate.of(currentYear - 35, Month.JANUARY, 1))),
                //What is your current marital status?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1204", "married_annulted"),
                //City/Town/Village
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_birthInformation/SubSec_birthInformation/Q_90", "Orléans"),
                //Province/Territory/Prefecture/Parish
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_birthInformation/SubSec_birthInformation/Q_92", "Loiret"),
                //Country of Birth
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_birthInformation/SubSec_birthInformation/Q_89", "France"),

                //Information About Your Parents
                //Parent 1
                //Family Name/Last Name/Surname
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_parent1/Q_1232", "Dupont"),
                //Given Name (First name)
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_parent1/Q_1230", "Jean-Pierre"),
                //Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_parent1/Q_1231", "Senior"),
                //Date of Birth
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_parent1/Q_1236", DateUtil.fromDate(LocalDate.of(currentYear - 57, Month.MARCH, 17))),
                //Gender
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_parent1/Q_1233", "male"),
                //Country of Birth
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_parent1/Q_1234", "France"),
                //Current City/Town/Village of Residence
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_parent1/Q_1237", "Orléans"),
                //Current Country of Residence
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_parent1/Q_1238", "France"),

                //Parent 2
                //Family Name/Last Name/Surname
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_parent2/Q_1242", "Dupont"),
                //Given Name (First name)
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_parent2/Q_1240", "Marie-Louise"),
                //Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_parent2/Q_1241", "Chanel"),
                //Date of Birth
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_parent2/Q_1246", DateUtil.fromDate(LocalDate.of(currentYear - 53, Month.MARCH, 17))),
                //Gender
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_parent2/Q_1243", "female"),
                //Country of Birth
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_parent2/Q_1244", "France"),
                //Current City/Town/Village of Residence
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_parent2/Q_1247", "Boston"),
                //Current Country of Residence
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_parent2/Q_1248", "US"),

                //Have you ever been married?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_maritalStatus/Q_1201", "yes"),
                //Family Name/Last Name/Surname
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1224/0", "Jones"),
                //Given Name (First name)
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1222/0", "Emma"),
                //Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1223/0", "Junior"),
                //Date Marriage Ended
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_priorSpouses/Q_1228/0", DateUtil.fromDate(LocalDate.of(currentYear - 1, Month.JANUARY, 1))),

                //Your Citizenship Information
                //How was your citizenship acquired?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_legalStatusInUS/SubSec_usCitizens/Q_120", "naturalization"),
                //How was your citizenship acquired?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_legalStatusInUS/SubSec_usCitizens/Q_121", "yes"),


                //Page 4
                //What is the Certificate of Naturalization/Citizenship Certificate Number?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_legalStatusInUS/SubSec_usCitizens/Q_122", "32547890254"),
                //Where was your Naturalization/Citizenship Issued?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_legalStatusInUS/SubSec_usCitizens/Q_123", "Boston"),
                //What date was your Certificate of Naturalization/Citizenship Issued?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_legalStatusInUS/SubSec_usCitizens/Q_124", DateUtil.fromDate(LocalDate.of(currentYear - 2, Month.MARCH, 1))),

                //Additional Information
                //Have you ever before filed a Petition for Alien Fiance or for any other beneficiary?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_1/SubSec_1/Q_1", "yes"),
                //Was this person issued an Alien Registration Number (A-Number)?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_1/SubSec_1/Q_11/0", "yes"),
                //What was that person's Alien Registration Number (A-number)?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_1/SubSec_1/Q_12/0", "1234567890"),
                //Family Name/Last Name/Surname
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_1/SubSec_1/Q_4/0", "Dupont"),
                //Given Name (First name)
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_1/SubSec_1/Q_2/0", "Louise"),
                //Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_1/SubSec_1/Q_3/0", "Anne"),
                //Date of Filing
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_1/SubSec_1/Q_9/0", DateUtil.fromDate(LocalDate.of(currentYear - 2, Month.APRIL, 1))),
                //What was the result of the petition?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_1/SubSec_1/Q_10/0", "Approved"),

                //Do you have any children under 18 years of age?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1250", "yes"),
                //Date of Birth of Child 1 (Children under 18 only)
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_familyInformation/SubSec_childrenInformation/Q_1256/0", DateUtil.fromDate(LocalDate.now().minusYears(2))),

                //Part 2. Information About Your Beneficiary
                //Family Name/Last Name/Surname
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_nameForBeneficiary/SubSec_currentLegalNameForBeneficiary/Q_1903", "Dubois"),
                //Given Name (First name)
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_nameForBeneficiary/SubSec_currentLegalNameForBeneficiary/Q_1901", "Francoise"),
                //Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_nameForBeneficiary/SubSec_currentLegalNameForBeneficiary/Q_1902", "Valerie"),
                //Alien Registration Number (A-Number)
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2412", "569823140"),
                //Has the Social Security Administration (SSA) ever officially issued a Social Security card to you?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2407", "yes"),
                //Social Security Number (If any)
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2409", "123567352"),
                //Date of Birth
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_birthInformationForBeneficiary/SubSec_birthInformationForBeneficiary/Q_2202", DateUtil.fromDate(LocalDate.of(currentYear - 29, Month.JULY, 25))),
                //Gender
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_birthInformationForBeneficiary/SubSec_birthInformationForBeneficiary/Q_2201", "female"),
                //What is your current marital status?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2781", "divorced"),
                //City/Town/Village of birth
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_birthInformationForBeneficiary/SubSec_birthInformationForBeneficiary/Q_2205", "Rouen"),
                //Country of Birth
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_birthInformationForBeneficiary/SubSec_birthInformationForBeneficiary/Q_2203", "France"),
                //Country of Citizenship (or Nationality)
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_personelInformationForBeneficiary/SubSec_personelInformationForBeneficiary/Q_2401", "France"),

                //Other Names Used
                //Have you ever used another name for your Given Name (First Name), Middle Name (including maiden names), or Family Name/Last Name/Surname?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1906", "yes"),
                //List any other Family Name/Last Name/Surname used (e.g. Smith II, Smith Jr.):
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1912/0", "Lavigne"),
                //List any other Given Name used (e.g. Rich, Rick, Dick):
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1908/0", "Jacqueline"),
                //List any other Middle Name used:
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_nameForBeneficiary/SubSec_otherNamesUsedForBeneficiary/Q_1910/0", "Veronique"),


                //Page 5
                //Mailing Address for Your Beneficiary
                //Is your current mailing address the same as your current physical address?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentMailingAddressForBeneficiary/Q_2015", "no"),
                //In Care of Name
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentMailingAddressForBeneficiary/Q_2017", "Dubois Claude"),

                //Your Beneficiary's Address History
                //Beneficiary's Physical Address 1
                //Street Number and Name
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2003", "7 Rue du Dr Robert Rambert"),
                //Does your address have a secondary description (i.e. apartment, suite, or floor)?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2004", "yes"),
                //What is the secondary address description?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2005", "suite"),
                //Apartment/Suite/Floor
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2006", "135"),
                //City/Town/Village
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2007", "Rouen"),
                //Province/Territory/Prefecture/Parish
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2009", "Normandy"),
                //Postal Code
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2011", "76000"),
                //In what country is your current physical address?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2002", "France"),
                //When did you move into this address?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2012", DateUtil.fromDate(LocalDate.of(currentYear - 2, Month.AUGUST, 18))),

                //Beneficiary's Physical Address 2
                //Street Number and Name
//                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_previousPhysicalAddressForBeneficiary/Q_2041/0", "1 Rue du Dr Robert Rambert"),
//                //Does your address have a secondary description (i.e. apartment, suite, or floor)?
//                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_previousPhysicalAddressForBeneficiary/Q_2042/0", "yes"),
//                //What is the secondary address description?
//                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_previousPhysicalAddressForBeneficiary/Q_2043/0", "apartment"),
//                //Apartment/Suite/Floor
//                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_previousPhysicalAddressForBeneficiary/Q_2044/0", "698"),
//                //City/Town/Village
//                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_previousPhysicalAddressForBeneficiary/Q_2045/0", "Rouen"),
//                //Province/Territory/Prefecture/Parish
//                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_previousPhysicalAddressForBeneficiary/Q_2047/0", "Normandy"),
//                //Postal Code
//                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_previousPhysicalAddressForBeneficiary/Q_2049/0", "76000"),
//                //In what country is your current physical address?
//                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_previousPhysicalAddressForBeneficiary/Q_2040/0", "France"),
//                //When did you move into this address?
//                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_previousPhysicalAddressForBeneficiary/Q_2050/0", DateUtil.fromDate(LocalDate.of(currentYear - 9, Month.NOVEMBER, 1))),
//                //When did you move out of this address?
//                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_previousPhysicalAddressForBeneficiary/Q_2052/0", DateUtil.fromDate(LocalDate.of(currentYear - 2, Month.AUGUST, 17))),
//TODO: update employment section
/*

                //Your Beneficiary's Employment History
                //Beneficiary's Employer 1
                //What is your current employment status?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2601", "Employed"),
                //Were you ever employed, at any time, during the previous 5 years?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2602", "yes"),
                //Are you self-employed?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_employmentStatusForBeneficiary/Q_2603", "no"),
                //What is the full name of your current employer?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_currentEmployerForBeneficiary/Q_2608", "Museum of Fine Arts of Rouen"),
                //Street Number and Name
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_currentEmployerForBeneficiary/Q_2609", "Espl. Marcel Duchamp"),
                //Does your address have a secondary description (i.e. apartment, suite, or floor)?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_currentEmployerForBeneficiary/Q_2610", "yes"),
                //What is the secondary address description?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_currentEmployerForBeneficiary/Q_2611", "floor"),
                //Apartment/Suite/Floor
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_currentEmployerForBeneficiary/Q_2612", "2"),
                //City/Town/Village
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_currentEmployerForBeneficiary/Q_2613", "Rouen"),
                //Province/Territory/Prefecture/Parish
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_currentEmployerForBeneficiary/Q_2615", "Normandy"),
                //Postal Code
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_currentEmployerForBeneficiary/Q_2617", "76000"),
                //What country is your employer located in?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_currentEmployerForBeneficiary/Q_2607", "France"),
                //Occupation?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_currentEmployerForBeneficiary/Q_2618", "Lead accountant"),
                //What date did you begin working for this employer?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_currentEmployerForBeneficiary/Q_2619", DateUtil.fromDate(LocalDate.of(currentYear - 2, Month.MAY, 25))),
                //When did this employment end for this employer?
//                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_currentEmployerForBeneficiary/Q_2620", "PRESENT"),

                //Page 6
                //Beneficiary's Employer 2
                //Were you self-employed at this previous employer?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_previousEmployerByEmployedForBeneficiary/Q_2627", "yes"),
                //What was the full name of your previous employer?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_previousEmployerByEmployedForBeneficiary/Q_2628", "Historial Jeanne d’Arc"),
                //Street Number and Name
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_previousEmployerByEmployedForBeneficiary/Q_2630", "7 Rue Saint-Romain"),
                //Does your address have a secondary description (i.e. apartment, suite, or floor)?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_previousEmployerByEmployedForBeneficiary/Q_2631", "yes"),
                //What is the secondary address description?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_previousEmployerByEmployedForBeneficiary/Q_2632", "suite"),
                //Apartment/Suite/Floor
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_previousEmployerByEmployedForBeneficiary/Q_2633", "412"),
                //City/Town/Village
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_previousEmployerByEmployedForBeneficiary/Q_2634", "Rouen"),
                //Province/Territory/Prefecture/Parish
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_previousEmployerByEmployedForBeneficiary/Q_2636", "Normandy"),
                //Postal Code
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_previousEmployerByEmployedForBeneficiary/Q_2638", "76000"),
                //What country is your employer located in?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_previousEmployerByEmployedForBeneficiary/Q_2626", "France"),
                //Occupation
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_previousEmployerByEmployedForBeneficiary/Q_2639", "Accountant"),
                //What date did you begin working for this employer?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_previousEmployerByEmployedForBeneficiary/Q_2640", DateUtil.fromDate(LocalDate.of(currentYear - 8, Month.NOVEMBER, 15))),
                //When did this employment end for this employer?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_previousEmployerByEmployedForBeneficiary/Q_2641", DateUtil.fromDate(LocalDate.of(currentYear - 2, Month.MAY, 1))),
                //Were you unemployed for more than 30 days prior to when you began working for this previous employer?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_employmentHistoryForBeneficiary/SubSec_previousEmployerByEmployedForBeneficiary/Q_2642", "no"),
*/

                //Information About Your Beneficiary's Parents
                //Parent 1
                //Is this parent's current name the same as their birth name?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_parent1ForBeneficiary/Q_2882", "yes"),
                //Family Name/Last Name/Surname
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_parent1ForBeneficiary/Q_2885", "Dubois"),
                //Given Name (First name)
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_parent1ForBeneficiary/Q_2883", "Pierre"),
                //Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_parent1ForBeneficiary/Q_2884", "III"),
                //Date of Birth
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_parent1ForBeneficiary/Q_2892", DateUtil.fromDate(LocalDate.of(currentYear - 49, Month.JANUARY, 17))),
                //Gender
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_parent1ForBeneficiary/Q_2889", "male"),
                //Country of Birth
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_parent1ForBeneficiary/Q_2890", "France"),
                //Current City/Town/Village of Residence
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_parent1ForBeneficiary/Q_2893", "Rouen"),
                //Current Country of Residence
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_parent1ForBeneficiary/Q_2894", "France"),

                //Parent 2
                //Is this parent's current name the same as their birth name?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_parent2ForBeneficiary/Q_2900", "no"),
                //Family Name/Last Name/Surname
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_parent2ForBeneficiary/Q_2903", "Dubois"),
                //Given Name (First name)
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_parent2ForBeneficiary/Q_2901", "Louise"),
                //Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_parent2ForBeneficiary/Q_2902", "Juniour"),
                //Date of Birth
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_parent2ForBeneficiary/Q_2910", DateUtil.fromDate(LocalDate.of(currentYear - 48, Month.FEBRUARY, 17))),
                //Gender
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_parent2ForBeneficiary/Q_2907", "female"),
                //Country of Birth
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_parent2ForBeneficiary/Q_2908", "France"),
                //Current City/Town/Village of Residence
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_parent2ForBeneficiary/Q_2911", "Rouen"),
                //Current Country of Residence
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_parent2ForBeneficiary/Q_2912", "France"),

                //Other Information About Your Beneficiary
                //Have you ever been married?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2778", "yes"),
                //Does your current spouse have any children?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_priorSpousesForBeneficiary/Q_2819", "yes"),
                //Family Name/Last Name/Surname
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_priorSpousesForBeneficiary/Q_2842/0", "Dejean"),
                //Given Name (First name)
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_priorSpousesForBeneficiary/Q_2840/0", "Gerard"),
                //Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_priorSpousesForBeneficiary/Q_2841/0", "Patrick"),
                //Date Marriage Ended
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_priorSpousesForBeneficiary/Q_2851/0", DateUtil.fromDate(LocalDate.of(currentYear - 1, Month.JANUARY, 1))),
                //Have you ever been in the U.S?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_travelToTheUnitedStates/SubSec_previousVisitsToTheUnitedStates/Q_2993", "yes"),
                //Are you currently in the United States?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_travelToTheUnitedStates/SubSec_currentStatusInTheUnitedStates/Q_2950", "yes"),
                //What was your legal status when you last entered the United States?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_travelToTheUnitedStates/SubSec_currentStatusInTheUnitedStates/Q_2951", "without_inspection"),
                //What is the I-94 record number?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_travelToTheUnitedStates/SubSec_currentStatusInTheUnitedStates/Q_2958", "56210347895"),
                //Date of Arrival
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_travelToTheUnitedStates/SubSec_currentStatusInTheUnitedStates/Q_2954", DateUtil.fromDate(LocalDate.of(currentYear, currentMonth, 1))),


                //Page 7
                //Date of Arrival
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_travelToTheUnitedStates/SubSec_currentStatusInTheUnitedStates/Q_2957", "2019-08-01"),
                //Passport Number Used at Last Arrival
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_travelToTheUnitedStates/SubSec_lastEntryIntoTheUnitedStates/Q_2966", "AC356GB678"),
                //Travel Document Number Used at Last Arrival
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_travelToTheUnitedStates/SubSec_lastEntryIntoTheUnitedStates/Q_2967", "R456HJ123"),
                //Country that Issued this Passport or Travel Document
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_travelToTheUnitedStates/SubSec_lastEntryIntoTheUnitedStates/Q_2969", "France"),
                //Expiration Date of this Passport or Travel Document
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_travelToTheUnitedStates/SubSec_lastEntryIntoTheUnitedStates/Q_2968", DateUtil.fromDate(LocalDate.of(currentYear + 2, Month.DECEMBER, 31))),
                //Do you have any children?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2741", "yes"),
                //Child's Family Name/Last Name/Surname
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2749/0", "Dubois"),
                //Child's Given Name (First name)
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2743/0", "Jack"),
                //Child's Middle Name (Do Not Abbreviate)
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2746/0", "Senior"),
                //Country of Birth
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2753/0", "France"),
                //Date of Birth
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2754/0", DateUtil.fromDate(LocalDate.of(currentYear - 1, Month.JUNE, 1))),
                //Does [insert this child's name] live with the Beneficiary,  [insert Beneficiary Name]?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_familyInformationForBeneficiary/SubSec_childrenInformationForBeneficiary/Q_2762/0", "yes"),

                //Is the address where you intend to live in the United States the same as your current physical address?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_addressWhereYouIntendToLiveInUSForBeneficiary/Q_2065", "no"),
                //Street Number and Name
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_addressWhereYouIntendToLiveInUSForBeneficiary/Q_2066", "73 Thatcher Street"),
                //Does your address have a secondary description (i.e. apartment, suite, or floor)?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_addressWhereYouIntendToLiveInUSForBeneficiary/Q_2067", "yes"),
                //What is the secondary address description?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_addressWhereYouIntendToLiveInUSForBeneficiary/Q_2068", "apartment"),
                //Apartment/Suite/Floor Help
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_addressWhereYouIntendToLiveInUSForBeneficiary/Q_2069", "54"),
                //City/Town/Village
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_addressWhereYouIntendToLiveInUSForBeneficiary/Q_2070", "Brookline"),
                //State
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_addressWhereYouIntendToLiveInUSForBeneficiary/Q_2071", "Massachusetts"),
                //ZIP Code
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_addressWhereYouIntendToLiveInUSForBeneficiary/Q_2072", "02446"),
                //Phone Number (Including Area Code)
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_contactInformationForBeneficiary/SubSec_intendedDaytimePhoneNumberInUSForBeneficiary/Q_2153", "+12361334885"),

                //Your Beneficiary's Physical Address Abroad
                //Is your current Physical Address Abroad the same as the address you listed above in the 'Current Physical Address' section?
                //TODO: auto populate from address 1
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_addressHistoryForBeneficiary/SubSec_physicalAddressAbroadForBeneficiary/Q_2074", "yes"),

                //Page 8
                //Is your fiancé(e) related to you?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1351", "no"),
                //Have you and your fiancé(e) met in person during the two years immediately before filing this petition?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1353", "no"),

                //Did you meet your fiancé(e) or spouse through the services of an  International Marriage Broker (IMB)?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1355", "yes"),
                //What is the International Marriage Broker(s) (IMB) name (if any)?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1356", "RapidVisa"),
                //What is the International Marriage Broker's (IMB) family name (last name)?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1357", "Jackson"),
                //What is the International Marriage Broker's (IMB) given name first name)?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1358", "Tiffany"),
                //What is the International Marriage Broker's (IMB) organization's name?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1359", "RapidVisa Inc."),
                //What is the International Marriage Broker's (IMB) website URL?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1360", "https://rapidvisa.com"),
                //Street Number and Name
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1362", "6145 Spring Mountain Road"),
                //Does your address have a secondary description (i.e. apartment, suite, or floor)?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1363", "yes"),
                //What is the secondary address description?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1364", "suite"),
                //Apartment/Suite/Floor
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1365", "100"),
                //City/Town/Village
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1366", "Las Vegas"),
                //State
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1367", "Nevada"),
                //Zip Code
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1369", "89146"),
                //In what country is the IMB located?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1361", "US"),
                //Daytime telephone number
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_relationshipToPetitioner/SubSec_fianceQuestions/Q_1371", "800-872-1458"),

                //Consular Processing Information
                //In what city is this U.S. Embassy or Consulate located?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_uscisLocationInformation/SubSec_selectVisaInterViewLocation/Q_1802", "Paris"),
                //Select the country (where an U.S. Embassy or Consulate is located) where you will apply for your visa (and have your visa interview).
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_uscisLocationInformation/SubSec_selectVisaInterViewLocation/Q_1801", "France"),

                //Part 3. Other Information
                //Criminal Information
                //Have you EVER been subject to a temporary or permanent protection or restraining order (either civil or criminal)?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1124", "no"),
                //Have you ever been arrested or convicted for domestic violence, sexual assault, child abuse and neglect, dating violence, elder abuse or stalking or an attempt to commit any of these crimes?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1119", "no"),


                //Page 9
                //Have you ever been arrested or convicted of homicide, murder, manslaughter, rape, abusive sexual contact, sexual exploitation, incest, torture, trafficking, peonage, holding hostage, involuntary servitude, slave trade, kidnapping, abduction, unlawful criminal restraint, false imprisonment or an attempt to commit any of these crimes?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1130", "no"),
                //Have you either been arrested or convicted three or more times, not from a single act, for crimes relating to a controlled substance or alcohol?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1139", "no"),
                //Have you ever been arrested cited charged indicted convicted fined or imprisoned for breaking or violating any law or ordinance in any country excluding traffic violations (unless a traffic violation was alcohol- or drug-related or involved a fine of $500 or more)?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1115", "yes"),

                //Multiple Filer Waiver Request Information
                //The beneficiary is my spouse or I am not a multiple filer.
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_criminalAndCivilHistory/SubSec_criminalCivilConvictions/Q_1151", "yes"),

                //Part 4. Biographic Information
                //Please select your ethnicity (You race will be asked in a subsequent question):
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_biographicInformation/SubSec_ethnicity/Q_95", "not_hispanic_or_latino"),
                //Are you White?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_biographicInformation/SubSec_race/Q_96", "yes"),
                //What units of measure do you use?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_biographicInformation/SubSec_height/Q_101", "Metic"),
                //Centimeters
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_biographicInformation/SubSec_height/Q_102", "168"),

                //What units of measure do you use?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_biographicInformation/SubSec_weight/Q_105", "Metric"),
                //What is your weight?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_biographicInformation/SubSec_weight/Q_106", "80.5"),

                //What is your eye color?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_biographicInformation/SubSec_eyeColor/Q_107", "green"),
                //What is the color of your hair?
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_biographicInformation/SubSec_hairColor/Q_108", "sandy"),

                //Page 10
                //Part 5. Petitioner's Statement, Contact Information, Declaration, and Signature
                //Daytime Phone Number (Including Area Code)
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_contactInformation/SubSec_daytimeAndHomePhoneNumber/Q_83", "+1255755724"),
                //Phone Number (Including Area Code)
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_contactInformation/SubSec_mobilePhoneNumber/Q_81", "+123456789102"),
                //email address
                getAnswerInstance(packageId, petitionerApplicantId, "Sec_contactInformation/SubSec_email/Q_86", "petitioner@google.com"),


                //Page 11
                //Part 7. Contact Information, Declaration, and Signature of the Person Preparing this Petition, if Other Than the Petitioner
                //TODO: get data from attorney profile
                //Preparer's Full Name
                //Preparer's Family Name (Last name)
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_admin/SubSec_attorneyProfile/Q_3958", "Tsang"),
                //Preparer's Given Name (First name)
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_admin/SubSec_attorneyProfile/Q_3959", "Ka Yee"),
                //Preparer's Business or Organization Name
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_admin/SubSec_attorneyProfile/Q_3960", "Asian American Civic Association, Inc."),
                //Street Number and Name
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_admin/SubSec_attorneyProfile/Q_3963", "87 Tyler Street"),
                //Does your address have a secondary description (i.e. apartment, suite, or floor)?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_admin/SubSec_attorneyProfile/Q_3964", "yes"),
                //What is the secondary address description?
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_admin/SubSec_attorneyProfile/Q_3965", "floor"),
                //Apartment/Suite/Floor
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_admin/SubSec_attorneyProfile/Q_3966", "5th"),
                //City/Town/Village
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_admin/SubSec_attorneyProfile/Q_3967", "Boston"),
                //State
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_admin/SubSec_attorneyProfile/Q_3968", "MA"),
                //ZIP Code
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_admin/SubSec_attorneyProfile/Q_3970", "02111"),
                //Country
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_admin/SubSec_attorneyProfile/Q_3962", "United States"),
                //Daytime Phone Number
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_admin/SubSec_attorneyProfile/Q_3951", "(617) 426-9492"),
                //Mobile Phone Number
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_admin/SubSec_attorneyProfile/Q_3954", "(617) 548-2357"),
                //Preparer's E-mail Address (if any)
                getAnswerInstance(packageId, beneficiaryApplicantId, "Sec_admin/SubSec_attorneyProfile/Q_3961", "TsangKaYee@google.com")
        ]
        if (continuations) {
            answers.addAll(i129fContinuations(packageId, petitionerApplicantId))

        }
        answers
    }

    private static List<Answer> i129fContinuations(long packageId, long applicantId) {
        [
                //Other names
                //List any other Family Name/Last Name/Surname used (e.g. Smith II, Smith Jr.):
                getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_6/Q_41/1", "Smith"),
                //List any other Given Name used (e.g. Rich, Rick, Dick):
                getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_6/Q_37/1", "Daniel"),
                //List any other Middle Name used:
                getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_6/Q_39/1", "James"),

                //List any other Family Name/Last Name/Surname used (e.g. Smith II, Smith Jr.):
                getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_6/Q_41/2", "Brown"),
                //List any other Given Name used (e.g. Rich, Rick, Dick):
                getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_6/Q_37/2", "Thomas"),
                //List any other Middle Name used:
                getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_6/Q_39/2", "Harry"),

                //List any other Family Name/Last Name/Surname used (e.g. Smith II, Smith Jr.):
                getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_6/Q_41/3", "Davies"),
                //List any other Given Name used (e.g. Rich, Rick, Dick):
                getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_6/Q_37/3", "Jack"),
                //List any other Middle Name used:
                getAnswerInstance(packageId, applicantId, "Sec_2/SubSec_6/Q_39/3", "Oliver"),
        ]
    }

}
