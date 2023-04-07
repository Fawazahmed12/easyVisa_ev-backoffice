environments {
    production {
        frontEndAppURL = 'https://app.easyvisa.com'
        serverURL = 'https://api.easyvisa.com'
        easyvisa {
            inactiveTokenTimeout = 1 //1 hour
        }
    }
    development {
        frontEndAppURL = 'http://localhost:4200'
        serverURL = 'http://localhost:8080'
        easyvisa {
            inactiveTokenTimeout = 1344 //1344a hours = 8 weeks
        }
    }
    test {
        frontEndAppURL = 'http://localhost:4200'
        serverURL = 'http://localhost:8080'
    }
    aws_development {
        frontEndAppURL = 'https://dev-app.easyvisa.com'
        serverURL = 'https://dev-api.easyvisa.com'
        easyvisa {
            inactiveTokenTimeout = 336 //336 hours = 2 weeks
        }
    }
    qa {
        frontEndAppURL = 'https://qa-app.easyvisa.com'
        serverURL = 'https://qa-api.easyvisa.com'
        easyvisa {
            inactiveTokenTimeout = 1 // 1 hour
        }
    }
}

grails.gorm.failOnError = true

grails.spring.bean.packages = ['com.easyvisa.questionnaire']

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.easyvisa.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.easyvisa.UserRole'
grails.plugin.springsecurity.authority.className = 'com.easyvisa.Role'

grails.plugin.springsecurity.password.algorithm = 'bcrypt'
grails.plugin.springsecurity.password.bcrypt.logrounds = 12

grails.plugin.springsecurity.filterChain.chainMap = [
        [pattern: '/doc', filters: 'anonymousAuthenticationFilter,restTokenValidationFilter,restExceptionTranslationFilter,filterInvocationInterceptor'],
        [pattern: '/api/logout', filters: 'restLogoutFilter'],
        [pattern: '/api/public/**', filters: 'anonymousAuthenticationFilter,restTokenValidationFilter,restExceptionTranslationFilter,filterInvocationInterceptor'],
        [pattern: '/api/**', filters: 'JOINED_FILTERS,-anonymousAuthenticationFilter,-exceptionTranslationFilter,' +
                '-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter']
]
grails.plugin.springsecurity.roleHierarchy = '''
   ROLE_OWNER > ROLE_EV
   ROLE_ATTORNEY > ROLE_EMPLOYEE
'''
grails.plugin.springsecurity.rest.logout.endpointUrl = '/api/logout'
grails.plugin.springsecurity.rest.token.validation.headerName = 'Authorization'
grails.plugin.springsecurity.rest.token.validation.enableAnonymousAccess = true
grails.plugin.springsecurity.useSecurityEventListener = true

loginAttempts.time = 5
loginAttempts.allowedNumberOfAttempts = 3
forgotPassword.emailSubject = 'EasyVisa Password Reset'
forgotUsername.emailSubject = 'EasyVisa Username Retrieval'
registration.emailSubject = 'Welcome to EasyVisa'
emailChanged.emailSubject = 'EasyVisa email changed'
resetPasswordLink = '/auth/reset-password'
showUsernameLink = '/auth/show-username'
verifyRegistrationLink = '/auth/verify-registration'
nonLegalRegistrationLink = '/auth/sign-up'
loginUrl = '/auth/login'
registerRedir = '/api/public/register-redir'

easyvisa.maxFileSizeLimit = 20 * 1024 * 1024 //20 MB
easyvisa.emailFromName = 'EasyVisa'
easyvisa.emailFromEmail = 'DoNotReply@easyvisa.com'
easyvisa.inactiveTokenTimeout = 1// 1 hour

easyvisa.attorney.rank.base.pointsLifetime = 50
easyvisa.attorney.rank.base.points30 = 300
easyvisa.attorney.rank.base.points31To90 = 200
easyvisa.attorney.rank.base.points91To180 = 100
easyvisa.attorney.rank.recent.pointsLifetime = 10
easyvisa.attorney.rank.recent.points30 = 300
easyvisa.attorney.rank.recent.points31To90 = 100
easyvisa.attorney.rank.recent.points91To180 = 50
easyvisa.attorney.rank.top.pointsLifetime = 100
easyvisa.attorney.rank.top.points30 = 100
easyvisa.attorney.rank.top.points31To90 = 50
easyvisa.attorney.rank.top.points91To180 = 25

// hours shift due to server in UTC, but jobs should be run during Nevada state night
easyvisa.job.monthly.payment.cron='0 0 8 * * ?'
easyvisa.job.attorney.ranking.cron='0 0 9 * * ?'
easyvisa.job.questionnaire.inactive.cron='0 10 9 * * ?'
easyvisa.job.documents.inactive.cron='0 20 9 * * ?'
easyvisa.job.blocked.package.payment.cron='0 30 9 * * ?'
easyvisa.job.permanent.residence.card.cron='0 40 9 * * ?'
easyvisa.job.get.married90.days.cron='0 50 9 * * ?'
easyvisa.job.removal.conditions.residence.cron='0 0 10 * * ?'
easyvisa.job.check.inconsistent.data.cron='0 10 10 * * ?'
easyvisa.job.article.submit.cron='0 0/30 * * * ?'
easyvisa.job.article.check.cron='0 5/30 * * * ?'
easyvisa.job.article.check.views.cron='0 20 10 * * ?'
easyvisa.job.prospect.counts.check.cron='0 30 10 * * ?'
easyvisa.job.not.supported.benefit.cron='0 40 10 * * ?'
easyvisa.job.quarantine.files.check.cron='0 50 10 * * ?'
easyvisa.job.calculate.attorney.public.measures.cron='0 0 11 * * ?'
easyvisa.job.user.devices.clean.cron='0 10 11 * * ?'

payment.api.delay.message = 'Max attempts exceeded. A previous charge was recently successful. Please review and if necessary you can try again in five minutes.'

urls.globalExcludes = ['/api/public/**',
                       'api/users/**',
                       '/error/**',
                       '/doc/**',
                       '/errors/**',
                       '/console/**', //Console and static are for console plugin
                       '/static/**']

timeZonesList = ['Africa/Abidjan', 'Africa/Accra', 'Africa/Addis_Ababa', 'Africa/Algiers', 'Africa/Asmara', 'Africa/Asmera', 'Africa/Bamako', 'Africa/Bangui', 'Africa/Banjul', 'Africa/Bissau', 'Africa/Blantyre', 'Africa/Brazzaville',
                 'Africa/Bujumbura', 'Africa/Cairo', 'Africa/Casablanca', 'Africa/Ceuta', 'Africa/Conakry', 'Africa/Dakar', 'Africa/Dar_es_Salaam', 'Africa/Djibouti',
                 'Africa/Douala', 'Africa/El_Aaiun', 'Africa/Freetown', 'Africa/Gaborone', 'Africa/Harare', 'Africa/Johannesburg', 'Africa/Juba', 'Africa/Kampala',
                 'Africa/Khartoum', 'Africa/Kigali', 'Africa/Kinshasa', 'Africa/Lagos', 'Africa/Libreville', 'Africa/Lome', 'Africa/Luanda', 'Africa/Lubumbashi',
                 'Africa/Lusaka', 'Africa/Malabo', 'Africa/Maputo', 'Africa/Maseru', 'Africa/Mbabane', 'Africa/Mogadishu', 'Africa/Monrovia', 'Africa/Nairobi',
                 'Africa/Ndjamena', 'Africa/Niamey', 'Africa/Nouakchott', 'Africa/Ouagadougou', 'Africa/Porto-Novo', 'Africa/Sao_Tome', 'Africa/Timbuktu',
                 'Africa/Tripoli', 'Africa/Tunis', 'Africa/Windhoek', 'America/Adak', 'America/Anchorage', 'America/Anguilla', 'America/Antigua', 'America/Araguaina',
                 'America/Argentina/Buenos_Aires', 'America/Argentina/Catamarca', 'America/Argentina/ComodRivadavia', 'America/Argentina/Cordoba', 'America/Argentina/Jujuy',
                 'America/Argentina/La_Rioja', 'America/Argentina/Mendoza', 'America/Argentina/Rio_Gallegos', 'America/Argentina/Salta', 'America/Argentina/San_Juan',
                 'America/Argentina/San_Luis', 'America/Argentina/Tucuman', 'America/Argentina/Ushuaia', 'America/Aruba', 'America/Asuncion', 'America/Atikokan', 'America/Atka',
                 'America/Bahia', 'America/Bahia_Banderas', 'America/Barbados', 'America/Belem', 'America/Belize', 'America/Blanc-Sablon', 'America/Boa_Vista', 'America/Bogota',
                 'America/Boise', 'America/Buenos_Aires', 'America/Cambridge_Bay', 'America/Campo_Grande', 'America/Cancun', 'America/Caracas', 'America/Catamarca', 'America/Cayenne',
                 'America/Cayman', 'America/Chicago', 'America/Chihuahua', 'America/Coral_Harbour', 'America/Cordoba', 'America/Costa_Rica', 'America/Creston', 'America/Cuiaba',
                 'America/Curacao', 'America/Danmarkshavn', 'America/Dawson', 'America/Dawson_Creek', 'America/Denver', 'America/Detroit', 'America/Dominica', 'America/Edmonton',
                 'America/Eirunepe', 'America/El_Salvador', 'America/Ensenada', 'America/Fort_Nelson', 'America/Fort_Wayne', 'America/Fortaleza', 'America/Glace_Bay', 'America/Godthab', 'America/Goose_Bay',
                 'America/Grand_Turk', 'America/Grenada', 'America/Guadeloupe', 'America/Guatemala', 'America/Guayaquil', 'America/Guyana', 'America/Halifax', 'America/Havana', 'America/Hermosillo',
                 'America/Indiana/Indianapolis', 'America/Indiana/Knox', 'America/Indiana/Marengo', 'America/Indiana/Petersburg', 'America/Indiana/Tell_City', 'America/Indiana/Vevay', 'America/Indiana/Vincennes',
                 'America/Indiana/Winamac', 'America/Indianapolis', 'America/Inuvik', 'America/Iqaluit', 'America/Jamaica', 'America/Jujuy', 'America/Juneau', 'America/Kentucky/Louisville', 'America/Kentucky/Monticello',
                 'America/Knox_IN', 'America/Kralendijk', 'America/La_Paz', 'America/Lima', 'America/Los_Angeles', 'America/Louisville', 'America/Lower_Princes', 'America/Maceio', 'America/Managua', 'America/Manaus',
                 'America/Marigot', 'America/Martinique', 'America/Matamoros', 'America/Mazatlan', 'America/Mendoza', 'America/Menominee', 'America/Merida', 'America/Metlakatla', 'America/Mexico_City',
                 'America/Miquelon', 'America/Moncton', 'America/Monterrey', 'America/Montevideo', 'America/Montreal', 'America/Montserrat', 'America/Nassau', 'America/New_York', 'America/Nipigon',
                 'America/Nome', 'America/Noronha', 'America/North_Dakota/Beulah', 'America/North_Dakota/Center', 'America/North_Dakota/New_Salem', 'America/Ojinaga', 'America/Panama', 'America/Pangnirtung', 'America/Paramaribo',
                 'America/Phoenix', 'America/Port-au-Prince', 'America/Port_of_Spain', 'America/Porto_Acre', 'America/Porto_Velho', 'America/Puerto_Rico', 'America/Punta_Arenas', 'America/Rainy_River', 'America/Rankin_Inlet',
                 'America/Recife', 'America/Regina', 'America/Resolute', 'America/Rio_Branco', 'America/Rosario', 'America/Santa_Isabel', 'America/Santarem', 'America/Santiago', 'America/Santo_Domingo', 'America/Sao_Paulo', 'America/Scoresbysund',
                 'America/Shiprock', 'America/Sitka', 'America/St_Barthelemy', 'America/St_Johns', 'America/St_Kitts', 'America/St_Lucia', 'America/St_Thomas', 'America/St_Vincent', 'America/Swift_Current', 'America/Tegucigalpa', 'America/Thule',
                 'America/Thunder_Bay', 'America/Tijuana', 'America/Toronto', 'America/Tortola', 'America/Vancouver', 'America/Virgin', 'America/Whitehorse', 'America/Winnipeg', 'America/Yakutat', 'America/Yellowknife', 'Antarctica/Casey', 'Antarctica/Davis',
                 'Antarctica/DumontDUrville', 'Antarctica/Macquarie', 'Antarctica/Mawson', 'Antarctica/McMurdo', 'Antarctica/Palmer', 'Antarctica/Rothera', 'Antarctica/South_Pole', 'Antarctica/Syowa', 'Antarctica/Troll',
                 'Antarctica/Vostok', 'Arctic/Longyearbyen', 'Asia/Aden', 'Asia/Almaty', 'Asia/Amman', 'Asia/Anadyr', 'Asia/Aqtau', 'Asia/Aqtobe', 'Asia/Ashgabat', 'Asia/Ashkhabad', 'Asia/Atyrau',
                 'Asia/Baghdad', 'Asia/Bahrain', 'Asia/Baku', 'Asia/Bangkok', 'Asia/Barnaul', 'Asia/Beirut', 'Asia/Bishkek', 'Asia/Brunei', 'Asia/Calcutta', 'Asia/Chita', 'Asia/Choibalsan', 'Asia/Chongqing',
                 'Asia/Chungking', 'Asia/Colombo', 'Asia/Dacca', 'Asia/Damascus', 'Asia/Dhaka', 'Asia/Dili', 'Asia/Dubai', 'Asia/Dushanbe', 'Asia/Famagusta', 'Asia/Gaza', 'Asia/Harbin',
                 'Asia/Hebron', 'Asia/Ho_Chi_Minh', 'Asia/Hong_Kong', 'Asia/Hovd', 'Asia/Irkutsk', 'Asia/Istanbul', 'Asia/Jakarta', 'Asia/Jayapura', 'Asia/Jerusalem', 'Asia/Kabul', 'Asia/Kamchatka',
                 'Asia/Karachi', 'Asia/Kashgar', 'Asia/Kathmandu', 'Asia/Katmandu', 'Asia/Khandyga', 'Asia/Kolkata', 'Asia/Krasnoyarsk', 'Asia/Kuala_Lumpur', 'Asia/Kuching', 'Asia/Kuwait', 'Asia/Macao',
                 'Asia/Macau', 'Asia/Magadan', 'Asia/Makassar', 'Asia/Manila', 'Asia/Muscat', 'Asia/Nicosia', 'Asia/Novokuznetsk', 'Asia/Novosibirsk', 'Asia/Omsk', 'Asia/Oral', 'Asia/Phnom_Penh',
                 'Asia/Pontianak', 'Asia/Pyongyang', 'Asia/Qatar', 'Asia/Qyzylorda', 'Asia/Rangoon', 'Asia/Riyadh', 'Asia/Saigon', 'Asia/Sakhalin', 'Asia/Samarkand', 'Asia/Seoul', 'Asia/Shanghai',
                 'Asia/Singapore', 'Asia/Srednekolymsk', 'Asia/Taipei', 'Asia/Tashkent', 'Asia/Tbilisi', 'Asia/Tehran', 'Asia/Tel_Aviv', 'Asia/Thimbu', 'Asia/Thimphu', 'Asia/Tokyo', 'Asia/Tomsk', 'Asia/Ujung_Pandang',
                 'Asia/Ulaanbaatar', 'Asia/Ulan_Bator', 'Asia/Urumqi', 'Asia/Ust-Nera', 'Asia/Vientiane', 'Asia/Vladivostok', 'Asia/Yakutsk', 'Asia/Yangon', 'Asia/Yekaterinburg', 'Asia/Yerevan', 'Atlantic/Azores',
                 'Atlantic/Bermuda', 'Atlantic/Canary', 'Atlantic/Cape_Verde', 'Atlantic/Faeroe', 'Atlantic/Faroe', 'Atlantic/Jan_Mayen', 'Atlantic/Madeira', 'Atlantic/Reykjavik', 'Atlantic/South_Georgia', 'Atlantic/St_Helena',
                 'Atlantic/Stanley', 'Australia/ACT', 'Australia/Adelaide', 'Australia/Brisbane', 'Australia/Broken_Hill', 'Australia/Canberra', 'Australia/Currie', 'Australia/Darwin', 'Australia/Eucla', 'Australia/Hobart',
                 'Australia/LHI', 'Australia/Lindeman', 'Australia/Lord_Howe', 'Australia/Melbourne', 'Australia/NSW', 'Australia/North', 'Australia/Perth', 'Australia/Queensland', 'Australia/South', 'Australia/Sydney',
                 'Australia/Tasmania', 'Australia/Victoria', 'Australia/West', 'Australia/Yancowinna', 'Brazil/Acre', 'Brazil/DeNoronha', 'Brazil/East', 'Brazil/West', 'CET', 'CST6CDT', 'Canada/Atlantic', 'Canada/Central',
                 'Canada/Eastern', 'Canada/Mountain', 'Canada/Newfoundland', 'Canada/Pacific', 'Canada/Saskatchewan', 'Canada/Yukon', 'Chile/Continental', 'Chile/EasterIsland', 'Cuba', 'EET', 'EST5EDT', 'Egypt', 'Eire', 'Etc/GMT',
                 'Etc/GMT+0', 'Etc/GMT+1', 'Etc/GMT+10', 'Etc/GMT+11', 'Etc/GMT+12', 'Etc/GMT+2', 'Etc/GMT+3', 'Etc/GMT+4', 'Etc/GMT+5', 'Etc/GMT+6', 'Etc/GMT+7', 'Etc/GMT+8', 'Etc/GMT+9', 'Etc/GMT-0',
                 'Etc/GMT-1', 'Etc/GMT-10', 'Etc/GMT-11', 'Etc/GMT-12', 'Etc/GMT-13', 'Etc/GMT-14', 'Etc/GMT-2', 'Etc/GMT-3', 'Etc/GMT-4', 'Etc/GMT-5', 'Etc/GMT-6', 'Etc/GMT-7', 'Etc/GMT-8', 'Etc/GMT-9', 'Etc/GMT0', 'Etc/Greenwich',
                 'Etc/UCT', 'Etc/UTC', 'Etc/Universal', 'Etc/Zulu', 'Europe/Amsterdam', 'Europe/Andorra', 'Europe/Astrakhan', 'Europe/Athens', 'Europe/Belfast', 'Europe/Belgrade', 'Europe/Berlin', 'Europe/Bratislava', 'Europe/Brussels',
                 'Europe/Bucharest', 'Europe/Budapest', 'Europe/Busingen', 'Europe/Chisinau', 'Europe/Copenhagen', 'Europe/Dublin', 'Europe/Gibraltar', 'Europe/Guernsey', 'Europe/Helsinki', 'Europe/Isle_of_Man', 'Europe/Istanbul',
                 'Europe/Jersey', 'Europe/Kaliningrad', 'Europe/Kiev', 'Europe/Kirov', 'Europe/Lisbon', 'Europe/Ljubljana', 'Europe/London', 'Europe/Luxembourg', 'Europe/Madrid', 'Europe/Malta', 'Europe/Mariehamn', 'Europe/Minsk',
                 'Europe/Monaco', 'Europe/Moscow', 'Europe/Nicosia', 'Europe/Oslo', 'Europe/Paris', 'Europe/Podgorica', 'Europe/Prague', 'Europe/Riga', 'Europe/Rome', 'Europe/Samara', 'Europe/San_Marino', 'Europe/Sarajevo', 'Europe/Saratov',
                 'Europe/Simferopol', 'Europe/Skopje', 'Europe/Sofia', 'Europe/Stockholm', 'Europe/Tallinn', 'Europe/Tirane', 'Europe/Tiraspol', 'Europe/Ulyanovsk', 'Europe/Uzhgorod', 'Europe/Vaduz', 'Europe/Vatican', 'Europe/Vienna',
                 'Europe/Vilnius', 'Europe/Volgograd', 'Europe/Warsaw', 'Europe/Zagreb', 'Europe/Zaporozhye', 'Europe/Zurich', 'GB', 'GB-Eire', 'GMT', 'GMT0', 'Greenwich', 'Hongkong', 'Iceland',
                 'Indian/Antananarivo', 'Indian/Chagos', 'Indian/Christmas', 'Indian/Cocos', 'Indian/Comoro', 'Indian/Kerguelen', 'Indian/Mahe', 'Indian/Maldives', 'Indian/Mauritius', 'Indian/Mayotte', 'Indian/Reunion', 'Iran', 'Israel',
                 'Jamaica', 'Japan', 'Kwajalein', 'Libya', 'MET', 'MST7MDT', 'Mexico/BajaNorte', 'Mexico/BajaSur', 'Mexico/General', 'NZ', 'NZ-CHAT', 'Navajo', 'PRC', 'PST8PDT', 'Pacific/Apia', 'Pacific/Auckland',
                 'Pacific/Bougainville', 'Pacific/Chatham', 'Pacific/Chuuk', 'Pacific/Easter', 'Pacific/Efate', 'Pacific/Enderbury', 'Pacific/Fakaofo', 'Pacific/Fiji', 'Pacific/Funafuti', 'Pacific/Galapagos', 'Pacific/Gambier',
                 'Pacific/Guadalcanal', 'Pacific/Guam', 'Pacific/Honolulu', 'Pacific/Johnston', 'Pacific/Kiritimati', 'Pacific/Kosrae', 'Pacific/Kwajalein', 'Pacific/Majuro', 'Pacific/Marquesas', 'Pacific/Midway', 'Pacific/Nauru',
                 'Pacific/Niue', 'Pacific/Norfolk', 'Pacific/Noumea', 'Pacific/Pago_Pago', 'Pacific/Palau', 'Pacific/Pitcairn', 'Pacific/Pohnpei', 'Pacific/Ponape', 'Pacific/Port_Moresby', 'Pacific/Rarotonga', 'Pacific/Saipan',
                 'Pacific/Samoa', 'Pacific/Tahiti', 'Pacific/Tarawa', 'Pacific/Tongatapu', 'Pacific/Truk', 'Pacific/Wake', 'Pacific/Wallis', 'Pacific/Yap', 'Poland', 'Portugal', 'ROK', 'Singapore', 'SystemV/AST4', 'SystemV/AST4ADT',
                 'SystemV/CST6', 'SystemV/CST6CDT', 'SystemV/EST5', 'SystemV/EST5EDT', 'SystemV/HST10', 'SystemV/MST7', 'SystemV/MST7MDT', 'SystemV/PST8', 'SystemV/PST8PDT', 'SystemV/YST9', 'SystemV/YST9YDT', 'Turkey', 'UCT',
                 'US/Alaska', 'US/Aleutian', 'US/Arizona', 'US/Central', 'US/East-Indiana', 'US/Eastern', 'US/Hawaii', 'US/Indiana-Starke', 'US/Michigan', 'US/Mountain', 'US/Pacific', 'US/Pacific-New', 'US/Samoa', 'UTC',
                 'Universal', 'W-SU', 'WET', 'Zulu', 'EST', 'HST', 'MST', 'ACT', 'AET', 'AGT', 'ART', 'AST', 'BET', 'BST', 'CAT', 'CNT', 'CST', 'CTT', 'EAT', 'ECT', 'IET', 'IST', 'JST', 'MIT', 'NET', 'NST', 'PLT', 'PNT',
                 'PRT', 'PST', 'SST', 'VST']
