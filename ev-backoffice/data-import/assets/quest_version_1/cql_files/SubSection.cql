CREATE (SubSection_subsec_1:SubSection{easyVisaId:"SubSec_1",questVersion:"quest_version_1",name:"previousImmigrationFilledForAnotherPerson",displayText:"Previous Immigration (Visa) Petitions You Filed for Another Person",order:1});
CREATE (SubSection_subsec_2:SubSection{easyVisaId:"SubSec_2",questVersion:"quest_version_1",name:"petitionForOtherRelatives",displayText:"Petitions for Other Relatives",order:2});
CREATE (SubSection_subsec_3:SubSection{easyVisaId:"SubSec_3",questVersion:"quest_version_1",name:"submittedAffidavitOfSupport",displayText:"Previously Submitted Affidavit's of Support",order:3});
CREATE (SubSection_subsec_4:SubSection{easyVisaId:"SubSec_4",questVersion:"quest_version_1",name:"sponsorshipRelationship",displayText:"Sponsorship Relationship",order:4});
 
CREATE (SubSection_subsec_5:SubSection{easyVisaId:"SubSec_5",questVersion:"quest_version_1",name:"currentLegalName",displayText:"Current Legal Name",order:5});
CREATE (SubSection_subsec_6:SubSection{easyVisaId:"SubSec_6",questVersion:"quest_version_1",name:"otherNamesUsed",displayText:"Other Names Used",order:6});
 
CREATE (SubSection_subsec_currentphysicaladdress:SubSection{easyVisaId:"SubSec_currentPhysicalAddress",questVersion:"quest_version_1",name:"currentPhysicalAddress",displayText:"Current Physical Address",displayOrderChildren:"SubSec_currentPhysicalAddress,Q_42,Q_43,Q_44,Q_45,Q_46,Q_47,Q_48,Q_49,Q_50,Q_51,Q_52,Q_6000,Q_53",order:7});
CREATE (SubSection_subsec_previousphysicaladdress:SubSection{easyVisaId:"SubSec_previousPhysicalAddress",questVersion:"quest_version_1",name:"previousPhysicalAddress",displayText:"Previous Physical Address(es) within 5 Years",ruleClassName:"WithInNumberOfYears",ruleParam:"Sec_addressHistory/SubSec_currentPhysicalAddress/Q_52,5",order:8});
CREATE (SubSection_subsec_residedsince18:SubSection{easyVisaId:"SubSec_residedSince18",questVersion:"quest_version_1",name:"residedSince18",displayText:"U.S. States and Foreign Countries Resided in Since 18th Birthday",order:9});
CREATE (SubSection_subsec_currentmailingaddress:SubSection{easyVisaId:"SubSec_currentMailingAddress",questVersion:"quest_version_1",name:"currentMailingAddress",displayText:"Current Mailing Address",displayOrderChildren:"SubSec_currentMailingAddress,Q_68,Q_69,Q_70,Q_71,Q_72,Q_73,Q_74,Q_75,Q_76,Q_77,Q_78,Q_79",order:10});
 
CREATE (SubSection_subsec_mobilephonenumber:SubSection{easyVisaId:"SubSec_mobilePhoneNumber",questVersion:"quest_version_1",name:"mobilePhoneNumber",displayText:"Mobile Phone Number",order:11});
CREATE (SubSection_subsec_daytimeandhomephonenumber:SubSection{easyVisaId:"SubSec_daytimeAndHomePhoneNumber",questVersion:"quest_version_1",name:"daytimeAndHomePhoneNumber",displayText:"Daytime/Home Phone Number",order:12});
CREATE (SubSection_subsec_officephonenumber:SubSection{easyVisaId:"SubSec_officePhoneNumber",questVersion:"quest_version_1",name:"officePhoneNumber",displayText:"Office Phone Number",order:13});
CREATE (SubSection_subsec_email:SubSection{easyVisaId:"SubSec_email",questVersion:"quest_version_1",name:"email",displayText:"email",order:14});
 
CREATE (SubSection_subsec_birthinformation:SubSection{easyVisaId:"SubSec_birthInformation",questVersion:"quest_version_1",name:"birthInformation",displayText:"Birth Information",displayOrderChildren:"SubSec_birthInformation,Q_87,Q_88,Q_89,Q_90,Q_91,Q_92,Q_93,Q_94",order:15});
 
CREATE (SubSection_subsec_ethnicity:SubSection{easyVisaId:"SubSec_ethnicity",questVersion:"quest_version_1",name:"ethnicity",displayText:"Ethnicity (Select only one box)",order:16});
CREATE (SubSection_subsec_race:SubSection{easyVisaId:"SubSec_race",questVersion:"quest_version_1",name:"race",displayText:"Race (Select all applicable boxes)",order:17});
CREATE (SubSection_subsec_height:SubSection{easyVisaId:"SubSec_height",questVersion:"quest_version_1",name:"height",displayText:"Height",order:18});
CREATE (SubSection_subsec_weight:SubSection{easyVisaId:"SubSec_weight",questVersion:"quest_version_1",name:"weight",displayText:"Weight",order:19});
CREATE (SubSection_subsec_eyecolor:SubSection{easyVisaId:"SubSec_eyeColor",questVersion:"quest_version_1",name:"eyeColor",displayText:"Eye Color",order:20});
CREATE (SubSection_subsec_haircolor:SubSection{easyVisaId:"SubSec_hairColor",questVersion:"quest_version_1",name:"hairColor",displayText:"Hair Color",order:21});
 
CREATE (SubSection_subsec_legalstatusinusndgovtidnos:SubSection{easyVisaId:"SubSec_legalStatusInUSndGovtIDNos",questVersion:"quest_version_1",name:"legalStatusInUSndGovtIDNos",displayText:"Legal Status in U.S. and Government ID Numbers",order:22});
CREATE (SubSection_subsec_uscitizens:SubSection{easyVisaId:"SubSec_usCitizens",questVersion:"quest_version_1",name:"usCitizens",displayText:"U.S. Citizens",ruleClassName:"LegalStatusApplicableRule",ruleParam:"united_states_citizen",order:23});
CREATE (SubSection_subsec_lawfulpermanentresident:SubSection{easyVisaId:"SubSec_lawfulPermanentResident",questVersion:"quest_version_1",name:"lawfulPermanentResident",displayText:"LPR (Lawful Permanent Resident), also commonly referred to as a Green Card holder",ruleClassName:"LegalStatusApplicableRule",ruleParam:"lawful_permanent_resident",order:24});
 
CREATE (SubSection_subsec_incomehistory:SubSection{easyVisaId:"SubSec_incomeHistory",questVersion:"quest_version_1",name:"incomeHistory",displayText:"Income History",order:25});
 
CREATE (SubSection_subsec_bankdeposits:SubSection{easyVisaId:"SubSec_bankDeposits",questVersion:"quest_version_1",name:"bankDeposits",displayText:"Bank Deposits",order:26});
CREATE (SubSection_subsec_personalproperty:SubSection{easyVisaId:"SubSec_personalProperty",questVersion:"quest_version_1",name:"personalProperty",displayText:"Personal Property",order:27});
CREATE (SubSection_subsec_realestate:SubSection{easyVisaId:"SubSec_realEstate",questVersion:"quest_version_1",name:"realEstate",displayText:"Real Estate",order:28});
CREATE (SubSection_subsec_lifeinsurance:SubSection{easyVisaId:"SubSec_lifeInsurance",questVersion:"quest_version_1",name:"lifeInsurance",displayText:"Life Insurance",order:29});
CREATE (SubSection_subsec_financialinstruments:SubSection{easyVisaId:"SubSec_financialInstruments",questVersion:"quest_version_1",name:"financialInstruments",displayText:"Financial Instruments",order:30});
 
CREATE (SubSection_subsec_employmentstatus:SubSection{easyVisaId:"SubSec_employmentStatus",questVersion:"quest_version_1",name:"employmentStatus",displayText:"Employment Status",displayOrderChildren:"SubSec_employmentStatus,Q_1001,RQG_employmentStatus",order:31});
 
CREATE (SubSection_subsec_currentmilitaryservice:SubSection{easyVisaId:"SubSec_currentMilitaryService",questVersion:"quest_version_1",name:"currentMilitaryService",displayText:"Current Military Service",order:32});
CREATE (SubSection_subsec_criminalcivilconvictions:SubSection{easyVisaId:"SubSec_criminalCivilConvictions",questVersion:"quest_version_1",name:"criminalCivilConvictions",displayText:"Criminal/Civil Convictions or Military Courts Martial",displayOrderChildren:"SubSec_criminalCivilConvictions,Q_6005,Q_1115,RQG_6,Q_1119,Q_1120,RQG_7,Q_1124,Q_1125,Q_1126,Q_1127,Q_1128,Q_1129,Q_1130,RQG_8,Q_1134,Q_1135,Q_1136,Q_1137,Q_1138,Q_1139,RQG_9,Q_1143,Q_1144,Q_1145,Q_1146,Q_1147,Q_1148,Q_1149,Q_1150,Q_1151,Q_1152",order:33});
 
CREATE (SubSection_subsec_maritalstatus:SubSection{easyVisaId:"SubSec_maritalStatus",questVersion:"quest_version_1",name:"maritalStatus",displayText:"Marital Status",order:34});
CREATE (SubSection_subsec_currentspouse:SubSection{easyVisaId:"SubSec_currentSpouse",questVersion:"quest_version_1",name:"currentSpouse",displayText:"Current Spouse",ruleClassName:"CurrentSpouseApplicableRule",ruleParam:"Sec_familyInformation/SubSec_maritalStatus/Q_1204",order:35});
CREATE (SubSection_subsec_priorspouses:SubSection{easyVisaId:"SubSec_priorSpouses",questVersion:"quest_version_1",name:"priorSpouses",displayText:"Prior Spouses",ruleClassName:"PriorSpousesApplicableRule",ruleParam:"Sec_familyInformation/SubSec_maritalStatus/Q_1204,Sec_familyInformation/SubSec_currentSpouse/Q_1220",order:36});
CREATE (SubSection_subsec_parent1:SubSection{easyVisaId:"SubSec_parent1",questVersion:"quest_version_1",name:"parent1",displayText:"Parent 1",order:37});
CREATE (SubSection_subsec_parent2:SubSection{easyVisaId:"SubSec_parent2",questVersion:"quest_version_1",name:"parent2",displayText:"Parent 2",order:38});
CREATE (SubSection_subsec_childreninformation:SubSection{easyVisaId:"SubSec_childrenInformation",questVersion:"quest_version_1",name:"childrenInformation",displayText:"Children Information",order:39});
CREATE (SubSection_subsec_dependentschildren:SubSection{easyVisaId:"SubSec_dependentsChildren",questVersion:"quest_version_1",name:"dependentsChildren",displayText:"Dependents (Children)",order:40});
CREATE (SubSection_subsec_dependentsnonchildren:SubSection{easyVisaId:"SubSec_dependentsNonChildren",questVersion:"quest_version_1",name:"dependentsNonChildren",displayText:"Dependents (Non-Children)",order:41});
CREATE (SubSection_subsec_householdsizedependents:SubSection{easyVisaId:"SubSec_householdSizeDependents",questVersion:"quest_version_1",name:"householdSizeDependents",displayText:"Household Size/Dependents",order:42});
CREATE (SubSection_subsec_householdincome:SubSection{easyVisaId:"SubSec_householdIncome",questVersion:"quest_version_1",name:"householdIncome",displayText:"Household Income",ruleClassName:"HouseholdIncomeApplicableRule",order:43});
 
CREATE (SubSection_subsec_fiancequestions:SubSection{easyVisaId:"SubSec_fianceQuestions",questVersion:"quest_version_1",name:"fianceQuestions",displayText:"Fiancé(e) Questions",displayOrderChildren:"SubSec_fianceQuestions,Q_1351,Q_1352,Q_1353,Q_1354,Q_1355,Q_1356,Q_1357,Q_1358,Q_1359,Q_1360,Q_1361,Q_1362,Q_1363,Q_1364,Q_1365,Q_1366,Q_1367,Q_1368,Q_1369,Q_1370,Q_1371",order:44});
 
CREATE (SubSection_subsec_supportandcontributions:SubSection{easyVisaId:"SubSec_supportAndContributions",questVersion:"quest_version_1",name:"supportAndContributions",displayText:"Support & Contributions",order:45});
CREATE (SubSection_subsec_natureofcontributions:SubSection{easyVisaId:"SubSec_natureOfContributions",questVersion:"quest_version_1",name:"natureOfContributions",displayText:"Nature of Contributions",ruleClassName:"NatureOfContributionsApplicableRule",order:46});
 
CREATE (SubSection_subsec_jointsponsor1income:SubSection{easyVisaId:"SubSec_JointSponsor1Income",questVersion:"quest_version_1",name:"JointSponsor1Income",displayText:"Joint Sponsor 1 Income",order:47});
 
CREATE (SubSection_subsec_introquestionsforbeneficiary:SubSection{easyVisaId:"SubSec_introQuestionsForBeneficiary",questVersion:"quest_version_1",name:"introQuestionsForBeneficiary",displayText:"Intro Questions For Beneficiary (The person who is immigrating to the United States)",order:48});
 
CREATE (SubSection_subsec_basispetitiontoremoveconditionsonresidence:SubSection{easyVisaId:"SubSec_basisPetitionToRemoveConditionsOnResidence",questVersion:"quest_version_1",name:"basisPetitionToRemoveConditionsOnResidence",displayText:"Basis for Petition to Remove Conditions on Residence",order:49});
 
CREATE (SubSection_subsec_selectvisainterviewlocation:SubSection{easyVisaId:"SubSec_selectVisaInterViewLocation",questVersion:"quest_version_1",name:"selectVisaInterViewLocation",displayText:"Select Visa Interview Location",order:50});
CREATE (SubSection_subsec_selectadjustmentofstatuslocation:SubSection{easyVisaId:"SubSec_selectAdjustmentOfStatusLocation",questVersion:"quest_version_1",name:"selectAdjustmentOfStatusLocation",displayText:"Select Adjustment of Status Location",order:51});
 
CREATE (SubSection_subsec_currentlegalnameforbeneficiary:SubSection{easyVisaId:"SubSec_currentLegalNameForBeneficiary",questVersion:"quest_version_1",name:"currentLegalName",displayText:"Current Legal Name",order:52});
CREATE (SubSection_subsec_othernamesusedforbeneficiary:SubSection{easyVisaId:"SubSec_otherNamesUsedForBeneficiary",questVersion:"quest_version_1",name:"otherNamesUsed",displayText:"Other Names Used",order:53});
 
CREATE (SubSection_subsec_currentphysicaladdressforbeneficiary:SubSection{easyVisaId:"SubSec_currentPhysicalAddressForBeneficiary",questVersion:"quest_version_1",name:"currentPhysicalAddress",displayText:"Current Physical Address",order:54});
CREATE (SubSection_subsec_previousphysicaladdressforbeneficiary:SubSection{easyVisaId:"SubSec_previousPhysicalAddressForBeneficiary",questVersion:"quest_version_1",name:"previousPhysicalAddress",displayText:"Previous Physical Addresses",displayOrderChildren:"SubSec_previousPhysicalAddressForBeneficiary,Q_2028,Q_2029,RQG_previousPhysicalAddressForBeneficiary",ruleClassName:"WithInNumberOfYears",ruleParam:"Sec_addressHistoryForBeneficiary/SubSec_currentPhysicalAddressForBeneficiary/Q_2012,5",order:55});
CREATE (SubSection_subsec_currentmailingaddressforbeneficiary:SubSection{easyVisaId:"SubSec_currentMailingAddressForBeneficiary",questVersion:"quest_version_1",name:"currentMailingAddress",displayText:"Current Mailing Address",displayOrderChildren:"SubSec_currentMailingAddressForBeneficiary,Q_2015,Q_2016,Q_2017,Q_2018,Q_2019,Q_2020,Q_2021,Q_2022,Q_2023,Q_2024,Q_2025,Q_2026",order:56});
CREATE (SubSection_subsec_lastaddressoutsideofusforbeneficiary:SubSection{easyVisaId:"SubSec_lastAddressOutsideOfUSForBeneficiary",questVersion:"quest_version_1",name:"lastAddressOutsideOfUS",displayText:"Last Address Outside of the United States for more than 1 Year",order:57});
CREATE (SubSection_subsec_addresswhereyouintendtoliveinusforbeneficiary:SubSection{easyVisaId:"SubSec_addressWhereYouIntendToLiveInUSForBeneficiary",questVersion:"quest_version_1",name:"addressWhereYouIntendToLiveInUS",displayText:"Address Where You Intend to Live in U.S.",order:58});
CREATE (SubSection_subsec_physicaladdressabroadforbeneficiary:SubSection{easyVisaId:"SubSec_physicalAddressAbroadForBeneficiary",questVersion:"quest_version_1",name:"physicalAddressAbroad",displayText:"Physical Address Abroad",order:59});
CREATE (SubSection_subsec_lastaddresswhichyoulivedtogetherwithspouseforbeneficiary:SubSection{easyVisaId:"SubSec_lastAddressWhichYouLivedTogetherWithSpouseForBeneficiary",questVersion:"quest_version_1",name:"lastAddressWhichYouLivedTogetherWithSpouse",displayText:"If filing for your spouse, give last address at which you lived together",order:60});
 
CREATE (SubSection_subsec_mobilephonenumberforbeneficiary:SubSection{easyVisaId:"SubSec_mobilePhoneNumberForBeneficiary",questVersion:"quest_version_1",name:"mobilePhoneNumber",displayText:"Mobile Phone Number",styleClassName:"invisible-subsection-title",order:61});
CREATE (SubSection_subsec_intendeddaytimephonenumberinusforbeneficiary:SubSection{easyVisaId:"SubSec_intendedDaytimePhoneNumberInUSForBeneficiary",questVersion:"quest_version_1",name:"intendedDaytimePhoneNumberInUS",displayText:"Intended Daytime Phone Number in United States",styleClassName:"invisible-subsection-title",order:62});
CREATE (SubSection_subsec_homephonenumberforbeneficiary:SubSection{easyVisaId:"SubSec_homePhoneNumberForBeneficiary",questVersion:"quest_version_1",name:"homePhoneNumber",displayText:"Home Phone Number",styleClassName:"invisible-subsection-title",order:63});
CREATE (SubSection_subsec_officephonenumberforbeneficiary:SubSection{easyVisaId:"SubSec_officePhoneNumberForBeneficiary",questVersion:"quest_version_1",name:"officePhoneNumber",displayText:"Office Phone Number",styleClassName:"invisible-subsection-title",order:64});
CREATE (SubSection_subsec_emailforbeneficiary:SubSection{easyVisaId:"SubSec_emailForBeneficiary",questVersion:"quest_version_1",name:"email",displayText:"email",styleClassName:"invisible-subsection-title",order:65});
 
CREATE (SubSection_subsec_birthinformationforbeneficiary:SubSection{easyVisaId:"SubSec_birthInformationForBeneficiary",questVersion:"quest_version_1",name:"birthInformation",displayText:"Birth Information",order:66});
 
CREATE (SubSection_subsec_ethnicityforbeneficiary:SubSection{easyVisaId:"SubSec_ethnicityForBeneficiary",questVersion:"quest_version_1",name:"ethnicity",displayText:"Ethnicity (Select only one box)",order:67});
CREATE (SubSection_subsec_raceforbeneficiary:SubSection{easyVisaId:"SubSec_raceForBeneficiary",questVersion:"quest_version_1",name:"race",displayText:"Race (Select all applicable boxes)",order:68});
CREATE (SubSection_subsec_heightforbeneficiary:SubSection{easyVisaId:"SubSec_heightForBeneficiary",questVersion:"quest_version_1",name:"height",displayText:"Height",order:69});
CREATE (SubSection_subsec_weightforbeneficiary:SubSection{easyVisaId:"SubSec_weightForBeneficiary",questVersion:"quest_version_1",name:"weight",displayText:"Weight",order:70});
CREATE (SubSection_subsec_eyecolorforbeneficiary:SubSection{easyVisaId:"SubSec_eyeColorForBeneficiary",questVersion:"quest_version_1",name:"eyeColor",displayText:"Eye Color",order:71});
CREATE (SubSection_subsec_haircolorforbeneficiary:SubSection{easyVisaId:"SubSec_hairColorForBeneficiary",questVersion:"quest_version_1",name:"hairColor",displayText:"Hair Color",order:72});
 
CREATE (SubSection_subsec_personelinformationforbeneficiary:SubSection{easyVisaId:"SubSec_personelInformationForBeneficiary",questVersion:"quest_version_1",name:"personelInformation",displayText:"Personal Information",displayOrderChildren:"SubSec_personelInformationForBeneficiary,Q_2401,Q_2402,Q_2403,Q_2404,Q_2405,Q_2406,Q_2407,Q_2408,Q_2409,Q_2410,Q_2411,Q_2412,Q_2413,Q_2414,Q_2415,Q_2416",order:73});
 
CREATE (SubSection_subsec_incomehistoryforbeneficiary:SubSection{easyVisaId:"SubSec_incomeHistoryForBeneficiary",questVersion:"quest_version_1",name:"incomeHistory",displayText:"Income History",order:74});
CREATE (SubSection_subsec_feespaidforbeneficiary:SubSection{easyVisaId:"SubSec_feesPaidForBeneficiary",questVersion:"quest_version_1",name:"feesPaid",displayText:"Fees Paid",order:75});
 
CREATE (SubSection_subsec_employmentstatusforbeneficiary:SubSection{easyVisaId:"SubSec_employmentStatusForBeneficiary",questVersion:"quest_version_1",name:"employmentStatusForBeneficiary",displayText:"Employment Status",displayOrderChildren:"SubSec_employmentStatusForBeneficiary,Q_2601,RQG_employmentStatusForBeneficiary",order:76});
CREATE (SubSection_subsec_lastoccupationoutsideus:SubSection{easyVisaId:"SubSec_lastOccupationOutsideUS",questVersion:"quest_version_1",name:"lastOccupationOutsideUS",displayText:"Last Occupation Outside the United States",order:77});
CREATE (SubSection_subsec_sponsorscurrentemployment:SubSection{easyVisaId:"SubSec_sponsorsCurrentEmployment",questVersion:"quest_version_1",name:"sponsorsCurrentEmployment",displayText:"Sponsor's Current Employment",order:78});
CREATE (SubSection_subsec_employmentauthorization:SubSection{easyVisaId:"SubSec_employmentAuthorization",questVersion:"quest_version_1",name:"employmentAuthorization",displayText:"Employment Authorization",order:79});
 
CREATE (SubSection_subsec_childreninformationforbeneficiary:SubSection{easyVisaId:"SubSec_childrenInformationForBeneficiary",questVersion:"quest_version_1",name:"childrenInformationForBeneficiary",displayText:"Children Information",order:80});
CREATE (SubSection_subsec_maritalstatusforbeneficiary:SubSection{easyVisaId:"SubSec_maritalStatusForBeneficiary",questVersion:"quest_version_1",name:"maritalStatusForBeneficiary",displayText:"Marital Status",order:81});
CREATE (SubSection_subsec_currentspouseforbeneficiary:SubSection{easyVisaId:"SubSec_currentSpouseForBeneficiary",questVersion:"quest_version_1",name:"currentSpouseForBeneficiary",displayText:"Current Spouse",ruleClassName:"CurrentSpouseApplicableRule",ruleParam:"Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2781",order:82});
CREATE (SubSection_subsec_currentspousechildrenforbeneficiary:SubSection{easyVisaId:"SubSec_currentSpouseChildrenForBeneficiary",questVersion:"quest_version_1",name:"currentSpouseChildrenForBeneficiary",displayText:"Current Spouse's Children",ruleClassName:"CurrentSpouseApplicableRule",ruleParam:"Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2781",order:83});
CREATE (SubSection_subsec_priorspousesforbeneficiary:SubSection{easyVisaId:"SubSec_priorSpousesForBeneficiary",questVersion:"quest_version_1",name:"priorSpousesForBeneficiary",displayText:"Prior Spouses",ruleClassName:"PriorSpousesApplicableRule",ruleParam:"Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2781,Sec_familyInformationForBeneficiary/SubSec_maritalStatusForBeneficiary/Q_2783",order:84});
CREATE (SubSection_subsec_dependentsforbeneficiary:SubSection{easyVisaId:"SubSec_dependentsForBeneficiary",questVersion:"quest_version_1",name:"dependentsForBeneficiary",displayText:"Dependents",order:85});
CREATE (SubSection_subsec_parent1forbeneficiary:SubSection{easyVisaId:"SubSec_parent1ForBeneficiary",questVersion:"quest_version_1",name:"parent1ForBeneficiary",displayText:"Parent 1",order:86});
CREATE (SubSection_subsec_parent2forbeneficiary:SubSection{easyVisaId:"SubSec_parent2ForBeneficiary",questVersion:"quest_version_1",name:"parent2ForBeneficiary",displayText:"Parent 2",order:87});
 
CREATE (SubSection_subsec_currentstatusintheunitedstates:SubSection{easyVisaId:"SubSec_currentStatusInTheUnitedStates",questVersion:"quest_version_1",name:"currentStatusInTheUnitedStates",displayText:"Current status in the United States",order:88});
CREATE (SubSection_subsec_lastentryintotheunitedstates:SubSection{easyVisaId:"SubSec_lastEntryIntoTheUnitedStates",questVersion:"quest_version_1",name:"lastEntryIntoTheUnitedStates",displayText:"Last Entry into the United States",order:89});
CREATE (SubSection_subsec_previousvisitstotheunitedstates:SubSection{easyVisaId:"SubSec_previousVisitsToTheUnitedStates",questVersion:"quest_version_1",name:"previousVisitsToTheUnitedStates",displayText:"Prior Visits to the United States",displayOrderChildren:"SubSec_previousVisitsToTheUnitedStates,Q_2993,Q_2995,RQG_previousVisitsToTheUnitedStates",order:90});
 
CREATE (SubSection_subsec_inadmissibilityandotherlegalissues:SubSection{easyVisaId:"SubSec_inadmissibilityAndOtherLegalIssues",questVersion:"quest_version_1",name:"inadmissibilityAndOtherLegalIssues",displayText:"Inadmissibility and Other Legal  Issues",order:91});
CREATE (SubSection_subsec_immigrationhistorygeneral:SubSection{easyVisaId:"SubSec_immigrationHistoryGeneral",questVersion:"quest_version_1",name:"immigrationHistoryGeneral",displayText:"Immigration History General",order:92});
CREATE (SubSection_subsec_priorimmigrationproceedings:SubSection{easyVisaId:"SubSec_priorImmigrationProceedings",questVersion:"quest_version_1",name:"priorImmigrationProceedings",displayText:"Prior Immigration Proceedings",order:93});
CREATE (SubSection_subsec_criminalhistory:SubSection{easyVisaId:"SubSec_criminalHistory",questVersion:"quest_version_1",name:"criminalHistory",displayText:"Criminal History",order:94});
CREATE (SubSection_subsec_securitymilitaryparamilitarypolitical:SubSection{easyVisaId:"SubSec_securityMilitaryParamilitaryPolitical",questVersion:"quest_version_1",name:"securityMilitaryParamilitaryPolitical",displayText:"Security/Military/Paramilitary/Political",order:95});
CREATE (SubSection_subsec_health:SubSection{easyVisaId:"SubSec_health",questVersion:"quest_version_1",name:"health",displayText:"Health",order:96});
CREATE (SubSection_subsec_otherinadmissibilitygrounds:SubSection{easyVisaId:"SubSec_otherInadmissibilityGrounds",questVersion:"quest_version_1",name:"otherInadmissibilityGrounds",displayText:"Other Inadmissibility Grounds",order:97});
 
 
CREATE (SubSection_subsec_publicassistance:SubSection{easyVisaId:"SubSec_publicAssistance",questVersion:"quest_version_1",name:"publicAssistance",displayText:"Public Assistance ",order:98});
 
CREATE (SubSection_subsec_extremehardshipforrelatives:SubSection{easyVisaId:"SubSec_extremeHardshipForRelatives",questVersion:"quest_version_1",name:"relativesWhoWillExperienceExtremeHardshipIfYouAreInadmissibleToTheUnitedStates",displayText:"Extreme Hardship for Relatives",order:99});
 
CREATE (SubSection_subsec_otherpeoplewithtiestous:SubSection{easyVisaId:"SubSec_otherPeopleWithTiesToUS",questVersion:"quest_version_1",name:"otherPeopleWithTiesToUS",displayText:"Other People With Ties to U.S.",order:100});
 
CREATE (SubSection_subsec_extremehardshipstatement:SubSection{easyVisaId:"SubSec_extremeHardshipStatement",questVersion:"quest_version_1",name:"extremeHardshipStatement",displayText:"Extreme Hardship Statement",ruleClassName:"ExtremeHardshipStatementApplicableRule",order:101});
CREATE (SubSection_subsec_discretionaryapprovalstatement:SubSection{easyVisaId:"SubSec_discretionaryApprovalStatement",questVersion:"quest_version_1",name:"discretionaryApprovalStatement",displayText:"Discretionary Approval Statement",order:102});
 
CREATE (SubSection_subsec_attorneyprofile:SubSection{easyVisaId:"SubSec_attorneyProfile",questVersion:"quest_version_1",name:"attorneyProfile",displayText:"Attorney Profile",order:103});
 
 
 
 
 