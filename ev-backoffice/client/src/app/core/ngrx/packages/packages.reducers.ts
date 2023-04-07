import {QuestionnaireActionTypes} from '../../../questionnaire/ngrx/questionnaire/questionnaire.actions';

import {
  DeletePackagesSuccess,
  GetActivePackageSuccess,
  GetPackageFailure,
  GetPackagesSuccess,
  GetPackageSuccess,
  PackagesActionsUnion,
  PackagesActionTypes,
  PatchPackageSuccess, PostPackageSuccess,
  SelectPackageId, SetActivePackageId,
  UpdatePackage,
} from './packages.actions';
import { adapter } from './packages.state';

export const initialState = adapter.getInitialState({
  activePackage: null,
  activePackageId: null,
  currentPackageId: null,
  total: null,
});

export function reducer(state = initialState, action: PackagesActionsUnion) {
  switch (action.type) {

    case PackagesActionTypes.GetPackageSuccess: {
      return {
        ...adapter.setAll([(action as GetPackageSuccess).payload], state),
        currentPackageId: (action as GetPackageSuccess).payload.id,
      };
    }

    case PackagesActionTypes.PostPackageSuccess: {
      return {
        ...adapter.setAll([(action as PostPackageSuccess).payload], state),
        currentPackageId: (action as PostPackageSuccess).payload.id,
      };
    }

    case PackagesActionTypes.GetPackageFailure:
    case  PackagesActionTypes.RemovePackage: {
      return {
        ...adapter.removeOne((action as GetPackageFailure).payload, state),
        currentPackageId: null,
      };
    }

    case PackagesActionTypes.GetPackagesSuccess: {
      const total = (action as GetPackagesSuccess).payload.headers.get('X-total-count');
      const packages = (action as GetPackagesSuccess).payload.body;
      const {entities, ids} = createEntities(packages);

      return {
        ...state,
        ids,
        entities: {...state.entities, ...entities},
        currentPackageId: null,
        total,
      };
    }

    case PackagesActionTypes.PatchPackageSuccess: {
      return {
        ...adapter.upsertOne((action as PatchPackageSuccess).payload, state),
      };
    }

    case PackagesActionTypes.RemovePackages: {
      return {
        ...adapter.removeAll(state),
        currentPackageId: null,
      };
    }

    case PackagesActionTypes.SelectPackageId: {
      return {
        ...state,
        currentPackageId: (action as SelectPackageId).payload,
      };
    }

    case PackagesActionTypes.UpdatePackage: {
      return {
        ...adapter.upsertOne((action as UpdatePackage).payload, state),
      };
    }

    case PackagesActionTypes.GetActivePackageSuccess: {
      return {
        ...state,
        activePackage: (action as GetActivePackageSuccess).payload,
        activePackageId: (action as GetActivePackageSuccess).payload.id,
      };
    }

    case PackagesActionTypes.SetActivePackageId: {
      return {
        ...state,
        activePackageId: (action as SetActivePackageId).payload,
      };
    }

    case PackagesActionTypes.GetActivePackageFailure:
    case PackagesActionTypes.ClearActivePackage: {
      return {
        ...state,
        activePackage: null,
        activePackageId: null,
      };
    }

    case QuestionnaireActionTypes.PostAnswerSuccess: {
      const payloadData = action['payload'];
      return {
        ...state,
        activePackage: payloadData.activePackage,
      };
    }

    case PackagesActionTypes.DeletePackagesSuccess: {
      const isActivePackage = (action as DeletePackagesSuccess).payload.deletedPackageIds.find((id) => id === state.activePackageId);

      return {
        ...adapter.removeMany((action as DeletePackagesSuccess).payload.deletedPackageIds, state),
        activePackage: isActivePackage ? null : state.activePackage,
        activePackageId: isActivePackage ? null : state.activePackageId,
      };
    }


    default: {
      return state;
    }
  }
}

export function createEntities(
  payload: any[],
): { entities: {}; ids: number[] | string[] } {
  let initialValue;

  const { entities, ids } = initialState;
  initialValue = { entities, ids };

  return payload.reduce(
    (acc, entity) => ({
      entities: { ...acc.entities, [entity.id]: entity },
      ids: acc.ids.indexOf(entity.id) === -1 ? [...acc.ids, entity.id] : acc.ids,
    }),
    initialValue,
  );
}
