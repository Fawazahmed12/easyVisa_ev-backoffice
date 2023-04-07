import { WarningsEffects } from './warnings/warnings.effects';
import { DispositionsEffects } from './dispositions/dispositions.effects';
import { TaskQueueModuleRequestEffects } from './requests/effects';

export const effects = [
  WarningsEffects,
  DispositionsEffects,
  ...TaskQueueModuleRequestEffects,
];
