export interface CategoryList {
  name: string;
  tid: string;
  parent_target_id: string;
}

export const categoryList: CategoryList[] = [
  {
    name: 'Visas',
    tid: '1',
    parent_target_id: ''
  },
  {
    name: 'Green Card (Permanent Residence)',
    tid: '2',
    parent_target_id: ''
  },
  {
    name: 'Citizenship (Naturalization)',
    tid: '3',
    parent_target_id: ''
  },
  {
    name: 'Admissibility Waiver  Issues',
    tid: '4',
    parent_target_id: ''
  },
  {
    name: 'Fiancee Visa or Marriage Visa?',
    tid: '5',
    parent_target_id: ''
  },
  {
    name: 'Visa Process',
    tid: '6',
    parent_target_id: ''
  },
  {
    name: 'Client is WITHIN the United States (Form I-601A)',
    tid: '7',
    parent_target_id: '4'
  },
  {
    name: 'Client is OUTSIDE the United States (Form I-601)',
    tid: '8',
    parent_target_id: '4'
  },
  {
    name: 'Petition For Removal of Conditions',
    tid: '9',
    parent_target_id: '2'
  },
  {
    name: 'Family Based Visas',
    tid: '10',
    parent_target_id: '1'
  },
  {
    name: 'Fiancee Visa - K1\/K2',
    tid: '14',
    parent_target_id: '10'
  }, {
    name: 'Marriage Visa K3\/CR1\/IR1',
    tid: '15',
    parent_target_id: '10'
  },
  {
    name: 'Additional Children with Accompanying Beneficiary',
    tid: '16',
    parent_target_id: '14'
  },
  {
    name: 'Additional Children with Accompanying Beneficiary',
    tid: '17',
    parent_target_id: '15'
  },
  {
    name: 'Parents',
    tid: '20',
    parent_target_id: '10'
  },
  {
    name: 'Siblings',
    tid: '21',
    parent_target_id: '10'
  },
  {
    name: 'Children',
    tid: '22',
    parent_target_id: '10'
  },
  {
    name: 'Orphans',
    tid: '23',
    parent_target_id: '10'
  },
  {
    name: 'Work Based Visas',
    tid: '24',
    parent_target_id: '1'
  },
  {
    name: 'Special Immigrant Visas',
    tid: '25',
    parent_target_id: '1'
  },
  {
    name: 'Diversity Visas',
    tid: '26',
    parent_target_id: '1'
  }
];
