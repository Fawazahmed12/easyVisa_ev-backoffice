import { Component, Input, OnDestroy, OnInit } from '@angular/core';

import { EMPTY, Observable, Subject } from 'rxjs';
import { fromPromise } from 'rxjs/internal-compatibility';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { catchError, filter, switchMap, withLatestFrom } from 'rxjs/operators';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { ModalService, OrganizationService, PackagesService, UserService } from '../../core/services';
import { Organization } from '../../core/models/organization.model';
import { Role } from '../../core/models/role.enum';
import { RequestState } from '../../core/ngrx/utils';


import { RepresentativeNotesService } from '../services/representative-notes.service';
import { Note } from '../models/note.model';
import { NoteTypes } from '../models/note-types.enum';

import { AddNewNoteComponent } from './add-new-note/add-new-note.component';
import { DeleteNoteFailure, PostNoteFailure } from '../ngrx/notes/notes.actions';


@Component({
  selector: 'app-representative-notes',
  templateUrl: './representative-notes.component.html',
})
@DestroySubscribers()
export class RepresentativeNotesComponent implements OnInit, AddSubscribers, OnDestroy {

  @Input() readOnlyAccess: boolean;

  activeOrganization$: Observable<Organization>;
  publicNotes$: Observable<Note[]>;
  representativeNotes$: Observable<Note[]>;
  currentRepresentativeId$: Observable<number>;
  isClient$: Observable<boolean>;
  deleteNoteRequestState$: Observable<RequestState<{id: number}>>;

  openNewNoteModal$: Subject<any> = new Subject<any>();
  removeNoteSubject$: Subject<any> = new Subject<any>();
  private subscribers: any = {};

  NoteTypes = NoteTypes;

  constructor(
    private organizationService: OrganizationService,
    private representativeNotesService: RepresentativeNotesService,
    private packagesService: PackagesService,
    private userService: UserService,
    private ngbModal: NgbModal,
    private modalService: ModalService
  ) {
  }

  ngOnInit() {
    this.isClient$ = this.userService.hasAccess([ Role.ROLE_USER ]);
    this.activeOrganization$ = this.organizationService.activeOrganization$;
    this.publicNotes$ = this.representativeNotesService.publicNotes$;
    this.representativeNotes$ = this.representativeNotesService.representativeNotes$;
    this.deleteNoteRequestState$ = this.representativeNotesService.deleteNoteRequestState$;

    this.currentRepresentativeId$ = this.organizationService.currentRepresentativeId$.pipe(
      filter((currentRepresentativeId) => !!currentRepresentativeId)
    );
  }

  addSubscribers() {
    this.subscribers.openNewModalSubscription = this.openNewNoteModal$.pipe(
      switchMap((type) => this.openNewNoteModal(type)),
      withLatestFrom(
        this.packagesService.activePackageId$,
        this.currentRepresentativeId$,
      )
    ).subscribe(([data, packageId, creatorId]) => this.representativeNotesService.postNote(
      {
        documentNoteType: data.type,
        subject: data.subject,
        creatorId,
        packageId,
      }
    ));

    this.subscribers.saveNoteFailSubscription = this.representativeNotesService.postNoteFailAction$
      .pipe(filter((action: PostNoteFailure) => this.representativeNotesService.documentAccessErrorFilter(action)))
      .subscribe((data) => this.representativeNotesService.documentAccessErrorHandler(data));

    this.subscribers.deleteNoteFailSubscription = this.representativeNotesService.deleteNoteFailAction$
      .pipe(filter((action: DeleteNoteFailure) => this.representativeNotesService.documentAccessErrorFilter(action)))
      .subscribe((data) => this.representativeNotesService.documentAccessErrorHandler(data));

    this.subscribers.openNewModalSubscription = this.removeNoteSubject$.pipe(
      withLatestFrom(this.packagesService.activePackageId$),
    ).subscribe(([documentNoteId, packageId]) => this.representativeNotesService.deleteNote(
      {
        documentNoteId,
        packageId,
      }
    ));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  openNewNoteModal(type) {
    const modalRef = this.ngbModal.open(AddNewNoteComponent, {
      centered: true,
      size: 'lg',
    });
    modalRef.componentInstance.type = type;
    return fromPromise(modalRef.result).pipe(
      catchError(() => EMPTY),
    );
  }

  newNote(type) {
    this.openNewNoteModal$.next(type);
  }

  removeNote(id) {
    this.removeNoteSubject$.next(id);
  }
}
