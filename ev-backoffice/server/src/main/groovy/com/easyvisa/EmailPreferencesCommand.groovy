package com.easyvisa

class EmailPreferencesCommand implements grails.validation.Validateable {

    List<EmailPreferenceCommand> taskQueue
    List<EmailPreferenceCommand> clientProgress

    List<EmailPreferenceCommand> getPreferences() {
        List<EmailPreferenceCommand> result = []
        !taskQueue ?: result.addAll(taskQueue)
        !clientProgress ?: result.addAll(clientProgress)
        result
    }
}
