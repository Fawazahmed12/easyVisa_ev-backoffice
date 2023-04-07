export interface State {
  label: string;
  value: string;
  code: string;
  checked?: boolean;
}

export const states: State[] = [
  {
    label: 'Alabama',
    value: 'ALABAMA',
    code: 'AL',
  },
  {
    label: 'Alaska',
    value: 'ALASKA',
    code: 'AK',
  },
  {
    label: 'Arizona',
    value: 'ARIZONA',
    code: 'AZ',
  },
  {
    label: 'Arkansas',
    value: 'ARKANSAS',
    code: 'AR',
  },
  {
    label: 'California',
    value: 'CALIFORNIA',
    code: 'CA',
  },
  {
    label: 'Colorado',
    value: 'COLORADO',
    code: 'CO',
  },
  {
    label: 'Connecticut',
    value: 'CONNECTICUT',
    code: 'CT',
  },
  {
    label: 'Delaware',
    value: 'DELAWARE',
    code: 'DE',
  },
  {
    label: 'D.C.',
    value: 'DC',
    code: 'DC',
  },
  {
    label: 'Florida',
    value: 'FLORIDA',
    code: 'FL',
  },
  {
    label: 'Georgia',
    value: 'GEORGIA',
    code: 'GA',
  },
  {
    label: 'Hawaii',
    value: 'HAWAII',
    code: 'HI',
  },
  {
    label: 'Idaho',
    value: 'IDAHO',
    code: 'ID',
  },
  {
    label: 'Illinois',
    value: 'ILLINOIS',
    code: 'IL',
  },
  {
    label: 'Indiana',
    value: 'INDIANA',
    code: 'IN',
  },
  {
    label: 'Iowa',
    value: 'IOWA',
    code: 'IA',
  },
  {
    label: 'Kansas',
    value: 'KANSAS',
    code: 'KS',
  },
  {
    label: 'Kentucky',
    value: 'KENTUCKY',
    code: 'KY',
  },
  {
    label: 'Louisiana',
    value: 'LOUISIANA',
    code: 'LA',
  },
  {
    label: 'Maine',
    value: 'MAINE',
    code: 'ME',
  },
  {
    label: 'Maryland',
    value: 'MARYLAND',
    code: 'MD',
  },
  {
    label: 'Massachusetts',
    value: 'MASSACHUSETTS',
    code: 'MA',
  },
  {
    label: 'Michigan',
    value: 'MICHIGAN',
    code: 'MI',
  },
  {
    label: 'Minnesota',
    value: 'MINNESOTA',
    code: 'MN',
  },
  {
    label: 'Mississippi',
    value: 'MISSISSIPPI',
    code: 'MS',
  },
  {
    label: 'Missouri',
    value: 'MISSOURI',
    code: 'MO',
  },
  {
    label: 'Montana',
    value: 'MONTANA',
    code: 'MT',
  },
  {
    label: 'Nebraska',
    value: 'NEBRASKA',
    code: 'NE',
  },
  {
    label: 'Nevada',
    value: 'NEVADA',
    code: 'NV',
  },
  {
    label: 'New Hampshire',
    value: 'NEW_HAMPSHIRE',
    code: 'NH',
  },
  {
    label: 'New Jersey',
    value: 'NEW_JERSEY',
    code: 'NJ',
  },
  {
    label: 'New Mexico',
    value: 'NEW_MEXICO',
    code: 'NM',
  },
  {
    label: 'New York',
    value: 'NEW_YORK',
    code: 'NY',
  },
  {
    label: 'North Carolina',
    value: 'NORTH_CAROLINA',
    code: 'NC',
  },
  {
    label: 'North Dakota',
    value: 'NORTH_DAKOTA',
    code: 'ND',
  },
  {
    label: 'Ohio',
    value: 'OHIO',
    code: 'OH',
  },
  {
    label: 'Oklahoma',
    value: 'OKLAHOMA',
    code: 'OK',
  },
  {
    label: 'Oregon',
    value: 'OREGON',
    code: 'OR',
  },
  {
    label: 'Pennsylvania',
    value: 'PENNSYLVANIA',
    code: 'PA',
  },
  {
    label: 'Rhode Island',
    value: 'RHODE_ISLAND',
    code: 'RI',
  },
  {
    label: 'South Carolina',
    value: 'SOUTH_CAROLINA',
    code: 'SC',
  },
  {
    label: 'South Dakota',
    value: 'SOUTH_DAKOTA',
    code: 'SD',
  },
  {
    label: 'Tennessee',
    value: 'TENNESSEE',
    code: 'TN',
  },
  {
    label: 'Texas',
    value: 'TEXAS',
    code: 'TX',
  },
  {
    label: 'Utah',
    value: 'UTAH',
    code: 'UT',
  },
  {
    label: 'Vermont',
    value: 'VERMONT',
    code: 'VT',
  },
  {
    label: 'Virginia',
    value: 'VIRGINIA',
    code: 'VA',
  },
  {
    label: 'Washington',
    value: 'WASHINGTON',
    code: 'WA',
  },
  {
    label: 'West Virginia',
    value: 'WEST_VIRGINIA',
    code: 'WV',
  },
  {
    label: 'Wisconsin',
    value: 'WISCONSIN',
    code: 'WI',
  },
  {
    label: 'Wyoming',
    value: 'WYOMING',
    code: 'WY',
  },
  {
    label: 'American Samoa',
    value: 'AMERICAN_SAMOA',
    code: 'AS',
  },
  {
    label: 'Guam',
    value: 'GUAM',
    code: 'GU',
  },
  {
    label: 'Northern Mariana Islands',
    value: 'NORTHERN_MARIANA_ISLANDS',
    code: 'MP',
  },
  {
    label: 'Puerto Rico',
    value: 'PUERTO_RICO',
    code: 'PR',
  },
  {
    label: 'U.S. Virgin Islands',
    value: 'US_VIRGIN_ISLANDS',
    code: 'VI',
  },
  {
    label: 'Federated States of Micronesia',
    value: 'FEDERATED_STATES_OF_MICRONESIA',
    code: 'FM',
  },
  {
    label: 'Marshall Islands',
    value: 'MARSHALL_ISLANDS',
    code: 'MH',
  },
  {
    label: 'Palau',
    value: 'PALAU',
    code: 'PW',
  },
  {
    label: 'Armed Forces Africa',
    value: 'ARMED_FORCES_AFRICA',
    code: 'AE',
  },
  {
    label: 'Armed Forces Americas',
    value: 'ARMED_FORCES_AMERICAS',
    code: 'AA',
  },
  {
    label: 'Armed Forces Canada',
    value: 'ARMED_FORCES_CANADA',
    code: 'AE',
  },
  {
    label: 'Armed Forces Europe',
    value: 'ARMED_FORCES_EUROPE',
    code: 'AE',
  },
  {
    label: 'Armed Forces Middle East',
    value: 'ARMED_FORCES_MIDDLE_EAST',
    code: 'AE',
  },
  {
    label: 'Armed Forces Pacific',
    value: 'ARMED_FORCES_PACIFIC',
    code: 'AP',
  },
];
