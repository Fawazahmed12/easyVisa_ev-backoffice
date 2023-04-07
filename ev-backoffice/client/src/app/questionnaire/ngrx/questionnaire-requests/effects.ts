import { QuestionnaireAccessGetRequestEffects } from './questionnaireaccess-get/state';
import { SectionsGetRequestEffects } from './sections-get/state';
import { QuestionsGetRequestEffects } from './questions-get/state';
import { AnswersGetRequestEffects } from './answers-get/state';
import { AnswerPostRequestEffects } from './answer-post/state';
import { RepeatGroupPostRequestEffects } from './repeatgroup-post/state';
import { RepeatGroupDeleteRequestEffects } from './repeatgroup-delete/state';
import { SectionWarningGetRequestEffects } from './sectionwarning-get/state';
import { AnswerValidationGetRequestEffects } from './answervalidation-get/state';

export const QuestionnaireRequestEffects = [
  QuestionnaireAccessGetRequestEffects,
  SectionsGetRequestEffects,
  QuestionsGetRequestEffects,
  AnswersGetRequestEffects,
  AnswerPostRequestEffects,
  RepeatGroupPostRequestEffects,
  RepeatGroupDeleteRequestEffects,
  SectionWarningGetRequestEffects,
  AnswerValidationGetRequestEffects
];
