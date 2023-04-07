import { Warning } from '../models/warning.model';

export const mockWarnings: Warning[] = [
  {
    id: 1,
    answerId: 1,
    subject: 'Potential Admissibility Issue',
    clientName: 'Smith, Michael',
    content: '1 warning',
    createdOn: '02/06/2017 9:38 AM',
    read: false,
    representativeName: 'Rogers, Jeffery',
    starred: true,
    source: '',
    packageId: 4,
    representativeId: 31,
    questionId: 2,
  },
  {
    id: 2,
    answerId: 2,
    subject: 'Potential Admissibility Issue',
    clientName: 'Johnson, Christopher + Gonzalez, Maria',
    content: '2 warning',
    createdOn: '02/06/2017 9:38 AM',
    read: true,
    representativeName: 'Cook, Rodney',
    starred: false,
    source: '',
    packageId: 4,
    representativeId: 31,
    questionId: 2,
  },
];
