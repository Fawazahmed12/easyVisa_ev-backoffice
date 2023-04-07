export const mockBenefitCatogories = [
  {
    group: {
      value: 'FIRST',
      label: 'Immediate Relative Visa & Green Card on Arrival (AOS)',
    },
    categories: [
      {
        value: 'IR-1',
        label: 'IR-1',
        description: 'Spouse of Citizen',
        price: '1800',
      },
      {
        value: 'IR-2',
        label: 'IR-2',
        description: 'Unmarried Children (under 21) of a Nat. Citizen',
        price: '1400',
      },
      {
        value: 'IR-5',
        label: 'IR-5',
        description: 'Parent of Citizen (age 21 and over)',
        price: '1800',
      },
    ]
  },
  {
    group: {
      value: 'SECOND',
      label: 'Family Preference Visa & Green Card on Arrival (AOS)',
    },
    categories: [
      {
        value: 'F-1',
        label: 'F-1',
        description: 'Unmarried (age 21 and over) Sons and Daughters of Citizens and Their Minor Children',
        price: '1800',
      },
      {
        value: 'F-2',
        label: 'F-2',
        description: 'Spouse, Minor Children, and Unmarried Sons and Daughters (age 21 and over) of LPRs',
        price: '1800',
      },
      {
        value: 'F-3',
        label: 'F-3',
        description: 'Married Sons and Daughters of Citizens, and Their Spouses and Their Minor Children',
        price: '1800',
      },
      {
        value: 'F-4',
        label: 'F-4',
        description: 'Brothers & Sisters of Citizens (age 21 and over), & Their Spouses & Minor Children',
        price: '1800',
      },
    ]
  },
  {
    group: {
      value: 'THIRD',
      label: 'Visa - Fiancé(e)/Spouse of U.S. Citizen',
    },
    categories: [
      {
        value: 'K-1/K-3',
        label: 'K-1/K-3',
        description: 'Fiancé(e)/Spouse',
        price: '1800',
      },
      {
        value: 'K-2/K-4',
        label: 'K-2/K-4',
        description: 'Fiancé(e) Children/Spouse Children',
        price: '1500',
      },
    ]
  },
  {
    group: {
      value: 'FOURTH',
      label: 'Naturalization',
    },
    categories: [
      {
        value: 'CITIZENSHIP',
        label: '',
        description: 'Citizenship (Green Card Holder to U.S. Citizenship)',
        price: '',
      },
    ]
  },
  {
    group: {
      value: 'FIFTH',
      label: 'Permanent Residence',
    },
    categories: [
      {
        value: 'LPR1',
        label: 'LPR1',
        description: 'Spouse to LPR',
        price: '1800',
      },
      {
        value: 'LPR2',
        label: 'LPR2',
        description: 'Spouse’s Children to LPR',
        price: '1400',
      },
    ]
  },
  {
    group: {
      value: 'SIXTH',
      label: 'Remove Conditions on Permanent Residence',
    },
    categories: [
      {
        value: 'REMOVE',
        label: 'Remove Conditions',
        description: '2-Year to 10-Year LPR',
        price: '1200',
      },
    ]
  },
  {
    group: {
      value: 'SEVENTH',
      label: 'Miscellaneous',
    },
    categories: [
      {
        value: '601',
        label: '601',
        description: 'Application for Waiver of Grounds on Inadmissibility (Client is OUTSIDE U.S.)',
        price: '3800',
      },
      {
        value: '601A',
        label: '601A',
        description: 'Application for Provisional Unlawful Presence Waiver (Client is INSIDE U.S.)',
        price: '3900',
      },
      {
        value: '765',
        label: '765',
        description: 'EAD (Employment Authorization Document)',
        price: '1800',
      },
      {
        value: '648',
        label: '648',
        description: 'Medical Certification for Disability Exception',
        price: '1800',
      },
    ]
  },
];
