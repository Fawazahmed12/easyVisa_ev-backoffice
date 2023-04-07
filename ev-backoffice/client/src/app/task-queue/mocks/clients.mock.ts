import { ClientModel } from '../models/client.model';
import { PackageStatus } from '../../core/models/package/package-status.enum';

export const mockClients: { header: any; body: ClientModel[] } = {
  header: {
    'X-Total-Count': 234
  },
  body: [
    {
      id: '1',
      status: PackageStatus.OPEN,
      clients: 'Jones, David',
      repType: 'Simmons, Barry',
      legalStatus: 'US Citizen',
      state: 'NV',
      benefit: 'K-1, K-2',
      ques: '64%',
      docs: '78%',
      lastActive: '12/10/2017',
      owed: '$1,200',
      active: false,
    },
    {
      id: '2',
      status: PackageStatus.BLOCKED,
      clients: 'Williams, Jason',
      repType: 'Foster, Bobby',
      legalStatus: 'LPR',
      state: 'CA',
      benefit: 'IR-1, IR-2',
      ques: '0%',
      docs: '0%',
      lastActive: '04/06/2017',
      owed: '',
      active: true,
    },
    {
      id: '3',
      status: PackageStatus.CLOSED,
      clients: 'Brown, James',
      repType: 'Alexander, Jon',
      legalStatus: 'US Citizen',
      state: 'TX',
      benefit: 'US Cit.',
      ques: '37%',
      docs: '84%',
      lastActive: '04/06/2017',
      owed: '$1,500',
      active: false,
    },
    {
      id: '4',
      status: PackageStatus.LEAD,
      clients: 'Davis, John',
      repType: 'Russell, Philip',
      legalStatus: 'US Citizen',
      state: 'WI',
      benefit: 'IR-1, IR-2',
      ques: '17%',
      docs: '91%',
      lastActive: '04/06/2017',
      owed: '',
      active: false,
    },
  ]
};
