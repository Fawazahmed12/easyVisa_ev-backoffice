import * as zxcvbn from 'zxcvbn';
import { ZXCVBNResult } from 'zxcvbn';

export function measurePasswordStrength(password: string): ZXCVBNResult {
  const result = zxcvbn(password);
  return {...result, feedback: translatePasswordWarnings(zxcvbn(password))};
}

// TODO don't use translatePasswordWarnings after updating zxcvbn lib https://github.com/dropbox/zxcvbn/pull/124
export function translatePasswordWarnings(measuredPassword) {
  const defaultFeedback = {
    warning: '',
    suggestions: ['FORM.METER.PASSWORD_RECOMMENDED.SUGGESTION_1', 'FORM.METER.PASSWORD_RECOMMENDED.SUGGESTION_2']
  };
  let i; let len; let match;
  let longest_match = measuredPassword.sequence[0];
  const ref = measuredPassword.sequence.slice(1);

  if (measuredPassword.sequence.length === 0) {
    return defaultFeedback;
  }
  if (measuredPassword.score > 2) {
    return {
      warning: '',
      suggestions: []
    };
  }
  for (i = 0, len = ref.length; i < len; i++) {
    match = ref[i];
    if (match.token.length > longest_match.token.length) {
      longest_match = match;
    }
  }
  let feedback = get_match_feedback(longest_match, measuredPassword.sequence.length === 1);
  const extra_feedback = 'FORM.METER.PASSWORD_RECOMMENDED.SUGGESTION_3';
  if (feedback != null) {
    feedback.suggestions.unshift(extra_feedback);
    if (feedback.warning == null) {
      feedback.warning = '';
    }
  } else {
    feedback = {
      warning: '',
      suggestions: [extra_feedback]
    };
  }
  return feedback;
}

export function get_match_feedback(match, is_sole_match) {
  let layout; let warning;
  switch (match.pattern) {
    case 'dictionary':
      return get_dictionary_match_feedback(match, is_sole_match);
    case 'spatial':
      layout = match.graph.toUpperCase();
      warning = match.turns === 1 ? 'FORM.METER.PASSWORD_RECOMMENDED.WARNING_1' : 'FORM.METER.PASSWORD_RECOMMENDED.WARNING_2';
      return {
        warning,
        suggestions: ['FORM.METER.PASSWORD_RECOMMENDED.SUGGESTION_4']
      };
    case 'repeat':
      warning = match.base_token.length === 1 ?
        'FORM.METER.PASSWORD_RECOMMENDED.WARNING_3'
        : 'FORM.METER.PASSWORD_RECOMMENDED.WARNING_4';
      return {
        warning,
        suggestions: ['FORM.METER.PASSWORD_RECOMMENDED.SUGGESTION_5']
      };
    case 'sequence':
      return {
        warning: 'FORM.METER.PASSWORD_RECOMMENDED.WARNING_5',
        suggestions: ['FORM.METER.PASSWORD_RECOMMENDED.SUGGESTION_6']
      };
    case 'regex':
      if (match.regex_name === 'recent_year') {
        return {
          warning: 'FORM.METER.PASSWORD_RECOMMENDED.WARNING_6',
          suggestions: ['FORM.METER.PASSWORD_RECOMMENDED.SUGGESTION_7', 'FORM.METER.PASSWORD_RECOMMENDED.SUGGESTION_8']
        };
      }
      break;
    case 'date':
      return {
        warning: 'FORM.METER.PASSWORD_RECOMMENDED.WARNING_7',
        suggestions: ['FORM.METER.PASSWORD_RECOMMENDED.SUGGESTION_9']
      };
  }
}

export function get_dictionary_match_feedback(match, is_sole_match) {
  let ref; let result;
  const warning = match.dictionary_name === 'passwords'
    ? is_sole_match && !match.l33t && !match.reversed
      ? match.rank <= 10 ? 'FORM.METER.PASSWORD_RECOMMENDED.WARNING_8' : match.rank <= 100
        ? 'FORM.METER.PASSWORD_RECOMMENDED.WARNING_9' : 'FORM.METER.PASSWORD_RECOMMENDED.WARNING_10' : match.guesses_log10 <= 4
        ? 'FORM.METER.PASSWORD_RECOMMENDED.WARNING_11' : void 0 : match.dictionary_name === 'english_wikipedia' ? is_sole_match
      ? 'FORM.METER.PASSWORD_RECOMMENDED.WARNING_12' : void 0 :
      ((ref = match.dictionary_name) === 'surnames' || ref === 'male_names' || ref === 'female_names')
        ? is_sole_match ? 'FORM.METER.PASSWORD_RECOMMENDED.WARNING_13' : 'FORM.METER.PASSWORD_RECOMMENDED.WARNING_14' : '';
  const suggestions = [];

  const START_UPPER = /^[A-Z][^A-Z]+$/;
  const ALL_UPPER = /^[^a-z]+$/;

  const word = match.token;
  if (word.match(START_UPPER)) {
    suggestions.push('FORM.METER.PASSWORD_RECOMMENDED.SUGGESTION_10');
  } else if (word.match(ALL_UPPER) && word.toLowerCase() !== word) {
    suggestions.push('FORM.METER.PASSWORD_RECOMMENDED.SUGGESTION_11');
  }
  if (match.reversed && match.token.length >= 4) {
    suggestions.push('FORM.METER.PASSWORD_RECOMMENDED.SUGGESTION_12');
  }
  if (match.l33t) {
    suggestions.push('FORM.METER.PASSWORD_RECOMMENDED.SUGGESTION_13');
  }
  result = {
    warning,
    suggestions
  };
  return result;
}
