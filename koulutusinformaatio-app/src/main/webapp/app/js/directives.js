/* Directives */

angular.module('kiApp.directives', []).

/**
 * Render contact info block
 */
directive('kiRenderContactInfo', function() {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/contactInfo.html',
        scope: true,
        link: function(scope, element, attrs) {
            scope.anchor = attrs.anchor;

            scope.$watch('parentLO.provider', function(data) {
                if (data) {
                    scope.showContact = (data.visitingAddress ||
                        data.postalAddress ||
                        data.name ||
                        data.email ||
                        data.phone ||
                        data.fax ||
                        data.webPage) ? true : false;
                }

                scope.provider = data;
            });
        }
    }
}).

/**
 *  Render student benefits block
 */
directive('kiRenderStudentBenefits', function() {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/studentBenefits.html',
        scope: true,
        link: function(scope, element, attrs) {
            scope.anchor = attrs.anchor;

            scope.$watch('parentLO.provider', function(data) {
                if (data) {
                    scope.showStudentBenefits = (data.livingExpenses ||
                        data.dining ||
                        data.healthcare) ? true : false;
                }

                scope.provider = data;
            });
        }
    }
}).

/**
 *  Render general organization information block
 */
directive('kiRenderOrganization', function() {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/organization.html',
        link: function(scope, element, attrs) {
            scope.anchor = attrs.anchor;

            scope.$watch('parentLO.provider', function(data) {
                if (data) {
                    scope.showOrganization = (data.learningEnvironment ||
                        data.accessibility) ? true : false;

                    scope.provider = data;
                }
            });
        }
    }
}).

/**
 *  Render organization image
 */
directive('kiRenderOrganizationImage', function() {
    return function(scope, element, attrs) {
        scope.$watch('providerImage', function(data) {
            if (data && data.pictureEncoded) {
                var imgElem = $('<img>', {
                    src: 'data:image/jpeg;base64,' + data.pictureEncoded,
                    alt: 'Oppilaitoksen kuva'
                });

                $(element).empty();
                element.append(imgElem);
            }
        });
    }

}).

/**
 *  Render professional titles
 */
directive('kiRenderProfessionalTitles', function() {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/professionalTitles.html',
        scope: true,
        link: function(scope, element, attrs) {
            scope.anchor = attrs.anchor;

            scope.$watch('selectedLOI.professionalTitles', function(data) {
                scope.showProfessionalTitles = data ? true : false;

            });
        }
    }
}).

directive('kiRenderExams', function() {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/exams.html',
        //scope: true,
        link: function(scope, element, attrs) {
            scope.$watch('ao.exams', function(data) {
                scope.exams = data;
            });

            scope.rowClass = function(isFirst, isLast) {
                if (isFirst && isLast) {
                    return 'first last';
                } else if (isFirst) {
                    return 'first';
                } else if (isLast) {
                    return 'last';
                } else {
                    return '';
                }
            } 
        }
    }
}).

/**
 *  Render organization social links
 */
directive('kiSocialLinks', function() {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/socialLinks.html',
        link: function(scope, element, attrs) {
            scope.anchor = attrs.anchor;

            scope.$watch('parentLO', function(data) {
                if (data && data.provider) {
                    scope.showOrganization = (data.provider.learningEnvironment ||
                        data.provider.accessibility) ? true : false;

                    scope.provider = data.provider;
                }
            });
        }
    }
}).

/**
 *  Render email (@ replaced with (at))
 */
directive('kiEmail', function() {
    return {
        restrict: 'E,A',
        link: function(scope, element, attrs) {
            attrs.$observe('kiEmail', function(data) {
                if (data) {
                    element.text(data.replace('@', '(at)'));
                }
            });
        }
    }
}).

/**
 *  Change relative link to absolute link
 */
directive('kiAbsoluteLink', function() {
    return {
        restrict: 'E,A',
        link: function(scope, element, attrs) {
            attrs.$observe('kiAbsoluteLink', function(data) {
                if (data.search(':\/\/') > -1) {
                    element.attr('href', data);
                } else {
                    element.attr('href', 'http://' + data);
                }
            });
        }
    }
}).

/**
 *  Creates and controls the location filter element
 */
 directive('kiLocationFilter', function() {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/locationFilter.html',

        link: function(scope, element, attrs) {
            element = $(element);
            if (element.find('input')) {
                element.find('input').on('keyup', function(event) {
                    if (event.keyCode === 13) {
                        scope.add();
                    }
                });
            }

            scope.remove = function(element) {
                scope.locations.splice(scope.locations.indexOf(element), 1);
                scope.change();
                return false;
            }

            scope.add = function() {
                if (scope.location && scope.locations.indexOf(scope.location) < 0) {
                    scope.locations.push(scope.location);
                    scope.location = '';
                    scope.change();
                    return false;
                }
            }
        }
    };
 }).

/**
 *  Creates and controls language selector for description language
 */
 directive('kiLanguageRibbon', ['$routeParams', function($routeParams) {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/languageRibbon.html',

        link: function(scope, element, attrs) {
            scope.isChild = ($routeParams.childId) ? true : false;

            if (scope.isChild) {
                scope.$watch('childLO', function(data) {
                    scope.hasMultipleTranslations = (scope.childLO && scope.childLO.availableTranslationLanguages && scope.childLO.availableTranslationLanguages.length >= 1) ? true : false;
                });
            } else {
                scope.$watch('parentLO', function(data) {
                    scope.hasMultipleTranslations = (scope.parentLO && scope.parentLO.availableTranslationLanguages && scope.parentLO.availableTranslationLanguages.length >= 1) ? true : false;
                });
            }
        }
    };
 }]).

 /**
 *  Creates and controls prerequisite selection
 */
 directive('kiPrerequisiteSelectionRibbon', ['$routeParams', function($routeParams) {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/prerequisiteRibbon.html',
    };
 }]).

/**
 *  Creates and controls the link "ribbon" of sibling LOs in child view
 */
  directive('kiSiblingRibbon', ['$location', '$routeParams', function($location, $routeParams) {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/siblings.html',
        link: function(scope, element, attrs) {

            scope.$watch('selectedAs.children', function(data) {
                if (data && data.length <= 1) {
                    $(element).remove();
                }
            });

            scope.siblingClass = function(sibling) {
                if (sibling.losId == $routeParams.childId) {
                    return 'disabled';
                } else {
                    return '';
                }
            }
        }
    }
}]).

/**
 *  Creates and controls the link "ribbon" of sibling LOs in child view
 */
  directive('kiChildRibbon', ['$location', '$routeParams', function($location, $routeParams) {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/children.html',
        link: function(scope, element, attrs) {

            scope.$watch('selectedParentLOI', function(data) {
                if (data && !data.children) {
                    $(element).remove();
                }
            });

            scope.siblingClass = function(sibling) {
                if (sibling.childLOId == $routeParams.childId) {
                    return 'disabled';
                } else {
                    return '';
                }
            }
        }
    }
}]).


/**
 *  Creates and controls the breadcrumb
 */
 directive('kiBreadcrumb', ['$location', 'SearchService', 'kiAppConstants', 'LanguageService', function($location, SearchService, kiAppConstants, LanguageService) {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/breadcrumb.html',
        link: function(scope, element, attrs) {
            var home = 'home';
            var root = i18n.t('breadcrumb-search-results');
            var parent;
            var child;

            scope.$watch('parentLO.name', function(data) {
                parent = data;
                update();
            }, true);

            scope.$watch('childLO.name', function(data) {
                child = data;
                update();
            }, true);

            attrs.$observe('kiBreadcrumb', function(data) {
                root = i18n.t(data);
                update();
            });

            var update = function() {
                scope.breadcrumbItems = [];

                if (LanguageService.getLanguage() == LanguageService.getDefaultLanguage()) {
                    pushItem({name: home, linkHref: kiAppConstants.contextRoot });
                } else {
                    pushItem({name: home, linkHref: kiAppConstants.contextRoot + LanguageService.getLanguage() });
                }
                
                pushItem({name: root, linkHref: '#/haku/' + SearchService.getTerm() });

                if (scope.parentLO) {
                    pushItem({name: parent, linkHref: '#/tutkinto/' + scope.parentLO.id });
                }

                pushItem({name: child});
            };

            var pushItem = function(item) {
                if (item.name) {
                    scope.breadcrumbItems.push(item);
                }
            };
        }
    };
}]).

/**
 *  Renders a text block with title. If no content exists the whole text block gets removed. 
 */

directive('renderTextBlock', function() {
    return function(scope, element, attrs) {

            var title;
            var content;

            attrs.$observe('title', function(value) {
                if (value) {
                    title = i18n.t(value);
                }
                update();
            });

            attrs.$observe('content', function(value) {
                content = value;
                update();
            });

            var update = function() {
                $(element).empty();
                if (content) {
                    if (title) {
                        var titleElement = createTitleElement(title, attrs.anchor, attrs.level);
                        element.append(titleElement);
                    }

                    // replace line feed with <br>
                    //content = content.replace(/(\r\n|\n|\r)/g,"<br />");
                    element.append(content);
                }
            };

            var createTitleElement = function(text, anchortag, level) {
                var idAttr = anchortag ? 'id="' + anchortag + '"' : '';
                if (level) {
                    return $('<h' + level + ' ' + idAttr + '>' + text + '</h' + level + '>');
                } else {
                    return $('<h2 ' + idAttr + '>' + text + '</h2>');
                }
            };
        };
}).

/**
 *  Renders study plan block
 */
directive('renderStudyPlan', function() {
    return {
        restrict: 'E,A',
        templateUrl: 'templates/studyPlan.html',
        scope: false
    }
}).

/**
 *  Updates the title element of the page.
 */
directive('kiAppTitle', ['TitleService', function(TitleService) {
    return function(scope, element, attrs) {
        $(element).on('updatetitle', function(e, param) {
            element.text(param);
        });
    };
}]).

/**
 *  Creates a human readable date from timestamp
 */
directive('kiTimestamp', function() {
    var padWithZero = function(number) {
        number = number.toString();
        if (number.length <= 1) {
            return "0" + number;
        } else {
            return number;
        }
    }

    return function(scope, element, attrs) {
        attrs.$observe('kiTimestamp', function(value) {
            $(element).empty();
            value = parseInt(value);
            var date = new Date(value);
            element.append(date.getDate() + '.' + (date.getMonth() + 1) + '.' + date.getFullYear());
            if (attrs.precise) {
                element.append(' ' + padWithZero(date.getHours()) + ':' + padWithZero(date.getMinutes()));
            }
        });
    }


}).

/**
 *  Render application system state as label
 */
directive('kiAsStateLabel', function() {
    return function(scope, element, attrs) {
        scope.$watch('loi', function(data) {
            var asOngoing = false;

            if (data && data.applicationSystems) {
                for (var asIndex in data.applicationSystems) {
                    if (data.applicationSystems.hasOwnProperty(asIndex)) {
                        var as = data.applicationSystems[asIndex];
                        if (as.asOngoing) {
                            asOngoing = as.asOngoing;
                            break;
                        }
                    }
                }
            }

            if (asOngoing) {
                element.addClass('label vih');
                element.text(i18n.t('label-as-ongoing'));
            } else {
                element.addClass('label har');
                element.text(i18n.t('label-as-not-ongoing'));
            }
        })
    }
}).

/**
 *  Render applicayion system state
 */
directive('kiAsState', function() {
    return function(scope, element, attrs) {
        if (scope.lo.asOngoing) {
            element.text(i18n.t('search-as-ongoing'));
        } else if (scope.lo.nextApplicationPeriodStarts) {
            var ts = new Date(scope.lo.nextApplicationPeriodStarts);
            element.text(i18n.t('search-as-next') + ' ' + ts.getDate() + '.' + (ts.getMonth() + 1) + '.' + ts.getFullYear());
        }
    }


}).

/**
 *  Render application system status
 */
directive('kiRenderApplicationSystemActive', function() {
    return {
        restrict: 'E,A',
        template: '<span data-ng-switch="active">' +
                    '<span data-ng-switch-when="future"><span data-ki-i18n="application-system-active-future"></span> <span data-ki-timestamp="{{timestamp}}"></span></span>' +
                    '<span data-ng-switch-when="past" data-ki-i18n="application-system-active-past"></span>' +
                    '<span data-ng-switch-when="present"data-ki-i18n="application-system-active-present"></span>' +
                '</span>',
        link: function(scope, element, attrs) {
            var as;
            scope.$watch('as', function(data) {
                as = data;
                update();
            });

            var update = function() {
                if (as) {
                    if (as.asOngoing) {
                        scope.active = "present";
                    } else if (as.nextApplicationPeriodStarts) {
                        scope.active = "future";
                        scope.timestamp = as.nextApplicationPeriodStarts;
                    } else {
                        scope.active = "past";
                    }
                }
            };
        }
    }
}).

/**
 *  Render application option status
 */
directive('kiRenderApplicationOptionActive', function() {
    return {
        restrict: 'E,A',
        template: '<span data-ng-switch="active">' +
                    '<span data-ng-switch-when="future"><span data-ki-i18n="application-system-active-future"></span> <span data-ki-timestamp="{{timestamp}}"></span></span>' +
                    '<span data-ng-switch-when="past" data-ki-i18n="application-system-active-past"></span>' +
                    '<span data-ng-switch-when="present"data-ki-i18n="application-system-active-present"></span>' +
                '</span>',
        link: function(scope, element, attrs) {
            var ao;
            scope.$watch('ao', function(data) {
                ao = data;
                update();
            });

            var update = function() {
                if (ao) {
                    if (ao.canBeApplied) {
                        scope.active = "present";
                    } else if (ao.nextApplicationPeriodStarts) {
                        scope.active = "future";
                        scope.timestamp = ao.nextApplicationPeriodStarts;
                    } else {
                        scope.active = "past";
                    }
                }
            };
        }
    }
}).

/**
 *  Fetches a trasnlation with the given key and inserts it inside the element
 */
directive('kiI18n', ['TranslationService', function(TranslationService) {
    return function(scope, element, attrs) {
        attrs.$observe('kiI18n', function(value) {
            $(element).empty();
            var translation = TranslationService.getTranslation(value);
            if (attrs.showColon) {
                translation += ':';
            }

            element.append(translation);
        });
    }    
}]);
