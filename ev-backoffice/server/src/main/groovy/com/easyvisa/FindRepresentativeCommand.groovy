package com.easyvisa

import com.easyvisa.enums.Country
import com.easyvisa.enums.Language
import com.easyvisa.enums.State
import grails.validation.Validateable

class FindRepresentativeCommand extends PaginationCommand implements Validateable {

    List<State> states
    List<Country> countries
    String country_other // This will have value, only if user has selected 'Other Country' option in the country-list
    Language language

    Boolean hasCountryParam() {
        return (this.countries && this.countries.size()) || this.country_other?.trim();
    };

    Boolean hasStateParam() {
        return (this.states && this.states.size());
    };

    List<State> getStateParams() {
        return this.states ?: new ArrayList<State>();
    }

    String getCountriesQuery(Map params) {
        String query = '';
        if (this.countries) {
            query = 'addr.country in :countries'
            params['countries'] = this.getCountryParams();
        } else if (this.country_other) {
            query = 'addr.country not in :countries'
            params['countries'] = this.excludedCountryList();
        }
        return query;
    }

    private List<Country> excludedCountryList() {
        List<Country> excludeCountries = [
                Country.CHINA,
                Country.CHINA_TAIWAN,
                Country.HONG_KONG,
                Country.MEXICO,
                Country.PHILIPPINES,
                Country.THAILAND,
                Country.VIETNAM,
                Country.UNITED_STATES
        ];
        return excludeCountries;
    }

    private List<Country> getCountryParams() {
        return this.countries ?: new ArrayList<Country>();
    }
}
