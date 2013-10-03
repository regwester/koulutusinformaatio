/*  Services */

angular.module('kiApp.services', ['ngResource']).

service('SearchLearningOpportunityService', ['$http', '$timeout', '$q', '$analytics', 'FilterService', function($http, $timeout, $q, $analytics, FilterService) {
    var transformData = function(result) {
        for (var index in result.results) {
            if (result.results.hasOwnProperty(index)) {
                var resItem = result.results[index];
                if (resItem.parentId) {
                    resItem.linkHref = '#/koulutusohjelma/' + resItem.id;
                } else {
                    resItem.linkHref = '#/tutkinto/' + resItem.id;
                }

                var prerequisite = resItem.prerequisiteCode || FilterService.getPrerequisite();
                if (prerequisite) {
                    resItem.linkHref += '#' + prerequisite;
                }
            }
        }
    };

    return {
        query: function(params) {
            var deferred = $q.defer();
            var cities = '';
            
            if (params.locations) {
                for (var index = 0; index < params.locations.length; index++) {
                    if (params.locations.hasOwnProperty(index)) {
                        cities += '&city=' + params.locations[index];
                    }
                }

                cities = cities.substring(1, cities.length);
            }

            var qParams = '?';

            qParams += (params.start != undefined) ? ('start=' + params.start) : '';
            qParams += (params.rows != undefined) ? ('&rows=' + params.rows) : '';
            qParams += (params.prerequisite != undefined) ? ('&prerequisite=' + params.prerequisite) : '';
            qParams += (params.locations != undefined && params.locations.length > 0) ? ('&' + cities) : '';
            qParams += (params.ongoing) != undefined ? ('&ongoing=' + params.ongoing) : '';

            $http.get('../lo/search/' + encodeURI(params.queryString) + qParams, {}).
            success(function(result) {
                var category;
                if (params.locations && params.locations.length > 0) {
                    category = params.locations[0];
                } else if (params.prerequisite) {
                    category = params.prerequisite;
                } else {
                    category = false;
                }
                $analytics.siteSearchTrack(params.queryString, category, result.totalCount);
                transformData(result);
                deferred.resolve(result);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

/**
 *  Resource for requesting parent LO data
 */
service('ParentLearningOpportunityService', ['$http', '$timeout', '$q', '$filter', 'LanguageService', function($http, $timeout, $q, $filter, LanguageService) {
    var transformData = function(result) {
        var translationLanguageIndex = result.availableTranslationLanguages.indexOf(result.translationLanguage);
        result.availableTranslationLanguages.splice(translationLanguageIndex, 1);

        if (result && result.provider && result.provider.name) {
            result.provider.encodedName = $filter('encodeURIComponent')('"' + result.provider.name + '"');
        }

        var applicationSystems = [];

        for (var index in result.applicationOptions) {
            if (result.applicationOptions.hasOwnProperty(index)) {
                var ao = result.applicationOptions[index];
                if (ao.applicationSystem && ao.applicationSystem.applicationDates && ao.applicationSystem.applicationDates.length > 0) {
                    ao.applicationSystem.applicationDates = ao.applicationSystem.applicationDates[0];
                }
                result.applicationSystem = ao.applicationSystem;
            }
        }

        // set teaching languge as the first language in array
        for (var index in result.lois) {
            if (result.lois.hasOwnProperty(index)) {
                var loi = result.lois[index];
                for (var asIndex in loi.applicationSystems) {
                    if (loi.applicationSystems.hasOwnProperty(asIndex)) {
                        var as = loi.applicationSystems[asIndex];
                        for (var aoIndex in as.applicationOptions) {
                            if (as.applicationOptions.hasOwnProperty(aoIndex)) {
                                var ao = as.applicationOptions[aoIndex];

                                if (ao.teachingLanguages && ao.teachingLanguages.length > 0) {
                                    ao.teachLang = ao.teachingLanguages[0];
                                }
                            }
                        }
                    }
                }
            }
        }

        // sort exams based on start time
        for (var index in result.lois) {
            if (result.lois.hasOwnProperty(index)) {
                var loi = result.lois[index];
                for (var asIndex in loi.applicationSystems) {
                    if (loi.applicationSystems.hasOwnProperty(asIndex)) {
                        var as = loi.applicationSystems[asIndex];
                        for (var aoIndex in as.applicationOptions) {
                            if (as.applicationOptions.hasOwnProperty(aoIndex)) {
                                var ao = as.applicationOptions[aoIndex];
                                for (var exam in ao.exams) {
                                    if (ao.exams.hasOwnProperty(exam)) {
                                        if (ao.exams[exam].examEvents) {
                                            ao.exams[exam].examEvents.sort(function(a, b) {
                                                return a.start - b.start;
                                            });
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // check if application system is of type Lisähaku
        for (var loiIndex in result.lois) {
            if (result.lois.hasOwnProperty(loiIndex)) {
                var loi = result.lois[loiIndex];
                for (var asIndex in loi.applicationSystems) {
                    if (loi.applicationSystems.hasOwnProperty(asIndex)) {
                        var as = loi.applicationSystems[asIndex];
                        if (as.applicationOptions && as.applicationOptions.length > 0) {
                            var firstAo = as.applicationOptions[0];
                            as.aoSpecificApplicationDates = firstAo.specificApplicationDates;
                        }
                    }
                }
            }
        }

        // sort LOIs based on prerequisite
        if (result.lois) {
            result.lois.sort(function(a, b) {
                if (a.prerequisite.description > b.prerequisite.description) return 1;
                else if (a.prerequisite.description < b.prerequisite.description) return -1;
                else return a.id > b.id ? 1 : -1;
            });
        }
    };

    return {
        query: function(options) {
            var deferred = $q.defer();
            var queryParams = {
                uiLang: LanguageService.getLanguage()
            }

            if (options.language) {
                queryParams.lang = options.language
            }

            $http.get('../lo/parent/' + options.parentId, {
                params: queryParams
            }).
            success(function(result) {
                transformData(result);
                deferred.resolve(result);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

/**
 *  Resource for requesting child LO data
 */
service('ChildLearningOpportunityService', ['$http', '$timeout', '$q', 'LanguageService', function($http, $timeout, $q, LanguageService) {

    // TODO: could we automate data transformation somehow?
    var transformData = function(result) {
        var studyplanKey = "KOULUTUSOHJELMA";
        /*
        var translationLanguageIndex = result.availableTranslationLanguages.indexOf(result.translationLanguage);
        result.availableTranslationLanguages.splice(translationLanguageIndex, 1);
        */

        for (var loiIndex in result.lois) {
            if (result.lois.hasOwnProperty(loiIndex)) {
                var loi = result.lois[loiIndex];

                var startDate = new Date(loi.startDate);
                loi.startDate = startDate.getDate() + '.' + (startDate.getMonth() + 1) + '.' + startDate.getFullYear();
                loi.teachingLanguage = getFirstItemInList(loi.teachingLanguages);
                loi.formOfTeaching = getFirstItemInList(loi.formOfTeaching);

                if (loi.webLinks) {
                    loi.studyPlan = loi.webLinks[studyplanKey];
                }
            }
        }

        // set teaching languge as the first language in array
        for (var index in result.lois) {
            if (result.lois.hasOwnProperty(index)) {
                var loi = result.lois[index];
                for (var asIndex in loi.applicationSystems) {
                    if (loi.applicationSystems.hasOwnProperty(asIndex)) {
                        var as = loi.applicationSystems[asIndex];
                        for (var aoIndex in as.applicationOptions) {
                            if (as.applicationOptions.hasOwnProperty(aoIndex)) {
                                var ao = as.applicationOptions[aoIndex];

                                if (ao.teachingLanguages && ao.teachingLanguages.length > 0) {
                                    ao.teachLang = ao.teachingLanguages[0];
                                }
                            }
                        }
                    }
                }
            }
        }

        // sort exams based on start time
        for (var loiIndex in result.lois) {
            if (result.lois.hasOwnProperty(loiIndex)) {
                var loi = result.lois[loiIndex];
                for (var asIndex in loi.applicationSystems) {
                    if (loi.applicationSystems.hasOwnProperty(asIndex)) {
                        var as = loi.applicationSystems[asIndex];
                        for (var aoIndex in as.applicationOptions) {
                            if (as.applicationOptions.hasOwnProperty(aoIndex)) {
                                var ao = as.applicationOptions[aoIndex];
                                for (var exam in ao.exams) {
                                    if (ao.exams.hasOwnProperty(exam)) {
                                        if (ao.exams[exam].examEvents) {
                                            ao.exams[exam].examEvents.sort(function(a, b) {
                                                return a.start - b.start;
                                            });
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // group LOIs with prerequisite
        var lois = [];
        for (var loiIndex in result.lois) {
            if (result.lois.hasOwnProperty(loiIndex)) {
                var loi = result.lois[loiIndex];

                var loiFound = undefined;
                for (var i in lois) {
                    if (lois.hasOwnProperty(i)) {
                        if (lois[i].prerequisite.value == loi.prerequisite.value) {
                            loiFound = lois[i];
                            break;
                        } 
                    }
                }

                if (loiFound) {
                    for (var i in loi.applicationSystems) {
                        if (loi.applicationSystems.hasOwnProperty(i)) {
                            var as = loi.applicationSystems[i];

                            if (!loiFound.applicationSystems) {
                                loiFound.applicationSystems = [];
                            }
                            
                            // group application systems
                            var existingAs = undefined;
                            for (var asIndex in loiFound.applicationSystems) {
                                if (loiFound.applicationSystems.hasOwnProperty(asIndex)) {
                                    var loiFoundAs = loiFound.applicationSystems[asIndex];
                                    if (as.id == loiFoundAs.id) {
                                        existingAs = loiFoundAs;
                                    }
                                }
                            }

                            if (existingAs) {
                                for (var aoIndex in as.applicationOptions) {
                                    if (as.applicationOptions.hasOwnProperty(aoIndex)) {
                                        var ao = as.applicationOptions[aoIndex];

                                        // group application options
                                        var aoFound = false;
                                        for (var j in existingAs.applicationOptions) {
                                            if (existingAs.applicationOptions.hasOwnProperty(j)) {
                                                if (ao.id == existingAs.applicationOptions[j].id) {
                                                    aoFound = true
                                                    break;
                                                }
                                            }
                                        }

                                        if (!aoFound) {
                                            existingAs.applicationOptions.push(ao);
                                        }
                                    }
                                }
                            } else {
                                loiFound.applicationSystems.push(as);
                            }
                            
                        }
                    }
                } else {
                    lois.push(loi);
                }
            }
        }
        result.lois = lois;

        // check if application system is of type Lisähaku
        for (var loiIndex in result.lois) {
            if (result.lois.hasOwnProperty(loiIndex)) {
                var loi = result.lois[loiIndex];
                for (var asIndex in loi.applicationSystems) {
                    if (loi.applicationSystems.hasOwnProperty(asIndex)) {
                        var as = loi.applicationSystems[asIndex];
                        if (as.applicationOptions && as.applicationOptions.length > 0) {
                            var firstAo = as.applicationOptions[0];
                            as.aoSpecificApplicationDates = firstAo.specificApplicationDates;
                        }
                    }
                }
            }
        }

        // sort LOIs based on prerequisite
        if (result.lois) {
            result.lois.sort(function(a, b) {
                if (a.prerequisite.description > b.prerequisite.description) return 1;
                else if (a.prerequisite.description < b.prerequisite.description) return -1;
                else return a.id > b.id ? 1 : -1;
            });
        }

        // add current child to sibligs
        if (result.related) {
            result.related.push({
                childLOId: result.id, 
                name: result.name
            });

            // sort siblings alphabetically
            result.related = result.related.sort(function(a, b) {
                if (a.childLOId > b.childLOId) return 1;
                else if (a.childLOId < b.childLOId) return -1;
                else return a.childLOId > b.childLOId ? 1 : -1;
            });
        }
    };

    var getFirstItemInList = function(list) {
        if (list && list[0]) {
            return list[0];
        } else {
            return '';
        }
    };

    return {
        query: function(options) {
            var deferred = $q.defer();
            var queryParams = {
                uiLang: LanguageService.getLanguage()
            }

            if (options.language) {
                queryParams.lang = options.language
            }

            $http.get('../lo/child/' + options.childId, {
                params: queryParams
            }).
            success(function(result) {
                transformData(result);
                deferred.resolve(result);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

/**
 *  Resource for requesting LO provider picture
 */
service('LearningOpportunityProviderPictureService', ['$http', '$timeout', '$q', function($http, $timeout, $q) {
    return  {
        query: function(options) {
            var deferred = $q.defer();

            $http.get('../lop/' + options.providerId + '/picture').
            success(function(result) {
                deferred.resolve(result);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

/**
 *  Service taking care of search term saving
 */
 service('SearchService', function() {
    var key = 'searchTerm';
    return {
        getTerm: function() {
            var term = $.cookie(key);
            if (term) {
                return term;
            } else {
                return '';
            }
        },

        setTerm: function(newTerm) {
            if (newTerm) {
                $.cookie(key, newTerm, {useLocalStorage: false, path: '/'});
            }
        }
    };
}).

/**
 *  Service keeping track of the current language selection
 */
service('LanguageService', function() {
    var defaultLanguage = 'fi';
    var key = 'i18next';

    return {
        getLanguage: function() {
            return $.cookie(key) || defaultLanguage;
        },

        setLanguage: function(language) {
            $.cookie(key, language, {useLocalStorage: false, path: '/'});
        },

        getDefaultLanguage: function() {
            return defaultLanguage;
        }
    };
}).

/**
 *  Service for "caching" current parent selection
 */
 service('ParentLODataService', function() {
    var data;

    return {
        getParentLOData: function() {
            return data;
        },

        setParentLOData: function(newData) {
            data = newData;
        },

        dataExists: function(id) {
            return data && data.id == id; 
        }
    };
}).

/**
 *  Service for "caching" current child selection
 */
 service('ChildLODataService', function() {
    var data;

    return {
        getChildLOData: function() {
            return data;
        },

        setChildLOData: function(newData) {
            data = newData;
        },

        dataExists: function(id) {
            return data && data.id == id; 
        }
    };
}).

/**
 *  Service for retrieving translated values for text
 */
service('TranslationService', function() {
    return {
        getTranslation: function(key) {
            if (key) {
                return i18n.t(key);
            }
        }
    }
}).

/**
 *  Service for retrieving translated values for text
 */
service('TabService', function() {
    var currentTab;

    return {
        setCurrentTab: function(tab) {
            currentTab = tab;
        },

        getCurrentTab: function() {
            if (currentTab) {
                return currentTab;
            } else {
                return 'kuvaus';
            }
        }
    }
}).

/**
 *  Service for maintaining application basket state
 */
service('ApplicationBasketService', ['$http', '$q', 'LanguageService', function($http, $q, LanguageService) {
    var key = 'basket';
    var cookieConfig = {useLocalStorage: false, maxChunkSize: 2000, maxNumberOfCookies: 20, path: '/'};

    // used to update item count in basket
    var updateBasket = function(count) {
        var event = $.Event('basketupdate');
        event.count = count;
        $('#appbasket-link').trigger(event);
    };

    // TODO: could we automate data transformation somehow?
    var transformData = function(result) {
        for (var asIndex in result) {
            if (result.hasOwnProperty(asIndex)) {
                var applicationDates = result[asIndex].applicationDates;
                if (applicationDates.length > 0) {
                    result[asIndex].applicationDates = applicationDates[0];
                }

                var applicationOptions = result[asIndex].applicationOptions;
                for (var i in applicationOptions) {
                    if (applicationOptions.hasOwnProperty(i)) {
                        if (applicationOptions[i].children.length > 0) {
                            result[asIndex].applicationOptions[i].qualification = applicationOptions[i].children[0].qualification;
                            result[asIndex].applicationOptions[i].prerequisite = applicationOptions[i].children[0].prerequisite;
                        }

                        if (!result[asIndex].applicationOptions[i].deadlines) {
                            result[asIndex].applicationOptions[i].deadlines = [];
                        }

                        if (result[asIndex].applicationOptions[i].attachmentDeliveryDeadline) {
                            result[asIndex].applicationOptions[i].deadlines.push({
                                name: i18n.t('attachment-delivery-deadline'),
                                value: result[asIndex].applicationOptions[i].attachmentDeliveryDeadline
                            });
                        }

                        // set teaching languge as the first language in array

                        var ao = applicationOptions[i];
                        if (ao.teachingLanguages && ao.teachingLanguages.length > 0) {
                            ao.teachLang = ao.teachingLanguages[0];
                        }
                    }
                }
            }
        }

        return result;
    };

    return {
        addItem: function(aoId, itemType) {

            var current = $.cookie(key);

            if (current) {
                current = JSON.parse(current);

                // do not add same ao twice
                if (current.indexOf(aoId) < 0) {
                        current.push(aoId);
                }
            } else {
                current = [];
                current.push(itemType);
                current.push(aoId);
            }

            $.cookie(key, JSON.stringify(current), cookieConfig);

            updateBasket(this.getItemCount());
        },

        removeItem: function(aoId) {
            if (this.getItemCount() > 1) {
                var value = $.cookie(key);
                value = JSON.parse(value);

                var index = value.indexOf(aoId);
                value.splice(index, 1);

                $.cookie(key, JSON.stringify(value), cookieConfig);
            } else {
                this.empty();
            }

            updateBasket(this.getItemCount());
        },

        empty: function() {
            $.cookie(key, null, cookieConfig);
            updateBasket(this.getItemCount());
        },

        getItems: function() {
            return JSON.parse($.cookie(key));
        },

        getItemCount: function() {
            return $.cookie(key) ? JSON.parse($.cookie(key)).length - 1 : 0;
        },

        isEmpty: function() {
            return this.getItemCount() <= 0;
        },

        getType: function() {
            if (!this.isEmpty()) {
                var basket = this.getItems();
                return basket[0];
            }
        },

        query: function(params) {
            var deferred = $q.defer();
            var basketItems = this.getItems();

            var qParams = 'uiLang=' + LanguageService.getLanguage();

            
            for (var index = 1; index < basketItems.length; index++) {
                if (basketItems.hasOwnProperty(index)) {
                    qParams += '&aoId=' + basketItems[index];
                }
            }
            
            $http.get('../basket/items?' + qParams).
            success(function(result) {
                result = transformData(result);
                deferred.resolve(result);
            }).
            error(function(result) {
                deferred.reject(result);
            });

            return deferred.promise;
        }
    }
}]).

/**
 *  Service for maintaining search filter state
 */
service('FilterService', ['UtilityService', function(UtilityService) {
    var filters = {};
    var arrayFilters = ['locations'];

    var filterIsEmpty = function(filter) {
        if (filter == undefined || filter == null) return true;
        else if (typeof filter == 'boolean' && !filter) return true;
        else if (filter instanceof Array && filter.length <= 0 ) return true;
        else return false;
    }

    return {
        set: function(newFilters) {
            filters = {};
            for (var i in newFilters) {
                if (newFilters.hasOwnProperty(i)) {
                    var filter = newFilters[i];
                    if (arrayFilters.indexOf(i) >= 0 && typeof filter == 'string') {
                        filter = UtilityService.getStringAsArray(filter);
                    }

                    if (!filterIsEmpty(filter)) {
                        filters[i] = filter;
                    }
                }
            }

        },

        get: function() {
            return filters;
        },

        getPrerequisite: function() {
            return filters.prerequisite;
        },

        setPage: function(value) {
            if (value && !isNaN(value)) {
                filters.page = parseInt(value);
            } else {
                filters.page = 1;
            }
        },

        getParams: function() {
            var params = '';
            for (var i in filters) {
                if (filters.hasOwnProperty(i)) {
                    var filter = filters[i];
                    if (filter instanceof Array) {
                        params += '&' + i + '=' + filter.join(',');
                    } else if (typeof filter == 'boolean') {
                        params += (filter) ? '&' + i : '';
                    } else {
                        params += '&' + i + '=' + filter;
                    }
                }
            }

            params = params.length > 0 ? params.substring(1, params.length) : '';
            return params;
        }
    };
}]).
/*
service.('kiLocation', ['$location', function($location) {
    return {
        search: function() {

        }
    }
}]).
*/

/*
service('SearchCriteriaService', ['FilterService', 'kiAppConstants', function(FilterService, kiAppConstants) {
    var criterias = {};

    return {

        getPage: function() {
            return criterias.page || kiAppConstants.searchResultsStartPage;
        },

        setPage: function(value) {
            criterias.page = value;
        },

        get: function() {
            var filters = FilterService.get();

            for (var i in filters) {
                if (filters.hasOwnProperty(i)) {
                    criterias[i] = filters[i];
                }
            }

            return criterias;
        }
    }
}]).
*/

/**
 *  Service for retrieving translated values for text
 */
service('UtilityService', function() {
    return {
        getApplicationOptionById: function(aoId, aos) {
            if (aos && aos.length > 0) {
                for (var index in aos) {
                    if (aos.hasOwnProperty(index)) {
                        if (aos[index].id == aoId) {
                            return aos[index];
                        }
                    }
                }
            }
        },
        getStringAsArray: function(stringToArray) {
            var delimiter = ',';
            if (stringToArray) {
                return stringToArray.split(delimiter);
            }
        }
    };
});