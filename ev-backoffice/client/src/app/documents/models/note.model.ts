import { NoteTypes } from './note-types.enum';
import { Profile } from '../../core/models/profile.model';

export class Note {
  id: number;
  packageId: number;
  creatorId?: number;
  creator: Profile;
  subject: string;
  documentNoteType: NoteTypes;
  createdDate: string;
  createdTime: string;
}
