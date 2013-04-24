/*  Services */

angular.module('kiApp.services', ['ngResource']).

/**
 *  Resource for making string based search
 */
factory('LearningOpportunity', function($resource) {
    return $resource('../lo/search/:queryString', {}, {
        query: {method:'GET', isArray:true}
    });
}).

/**
 *  Resource for requesting LO data (parent and its children)
 */
factory('ParentLearningOpportunity', function($resource) {
    return $resource('../lo/:parentId', {}, {
        query: {method:'GET', isArray:false}
    });
}).

/**
 *  Resource for requesting AO data
 */
factory('ApplicationOption', function($resource) {
    return $resource('../ao/search/:asId/:lopId', {}, {
        query: {method:'GET', isArray:true}
    });
}).

/**
 *  Service taking care of search term saving
 *  TODO: this data should be persisted?
 */
service('SearchService', function() {
    var term;

    return {
        getTerm: function() {
            return term;
        },

        setTerm: function(newTerm) {
            term = newTerm;
        }
    };
});


/* Directives */

angular.module('kiApp.directives', ['ngResource']).

/**
 *  Creates and controls the link "ribbon" of sibling LOs in child view
 */
directive('kiSiblingRibbon', function() {
    return function(scope, element, attrs) {
        var result = "";
        scope.$watch('parentLO', function(parentData) {
            if (parentData.children && parentData.children.length <= 1) {
                return;
            } 

            for(var index in parentData.children) {
                var child = parentData.children[index];
                var isCurrentSelection = child.id == scope.childLO.id ? true : false;
                var clazz = isCurrentSelection ? 'disabled' : '';
                result += '<a href="#/info/' + parentData.id + '/' + child.id + '" class="' + clazz + '">' + child.degreeTitle + '</a>';

                if (index < parentData.children.length - 1) { 
                    result += '<span class="list-separator">|</span>';
                }
            }

            element.html(result);
        }, true);
        
    }
}).
directive('kiBreadcrumb', ['$location', function($location) {
    return {
        restrict: 'E,A',
        templateUrl: 'partials/breadcrumb.html',
        link: function(scope, element, attrs) {
            scope.goto = function(parentId, childId) {
                $location.path('/info/' + parentId);
            }
        }
    };
    /*
    return function(scope, element, attrs) {
        var home = "Hakutulokset";
        var parent;
        var child;

        scope.$watch('parentLO.name', function(data) {
            parent = data;
            update();
        }, true);

        scope.$watch('childLO.degreeTitle', function(data) {
            child = data;
            update();
        }, true);

        var update = function() {
            var crumbs = [home, parent, child];
            element.html( createBreadcrumb(crumbs) );
        };

        var createBreadcrumb = function(items) {
            //console.log(parent);
            //console.log(child);
            var result = '<ul>';
            for (var i = 0; i < items.length; i++) {
                var nextItemIsUndefined = items[i+1] ? false : true;
                var isLast = i == items.length - 1 ? true : false;
                var clazz =  (nextItemIsUndefined || isLast) ? 'breadcrumb-item current' : 'breadcrumb-item';
                if (items[i]) {
                    if (i == 0) {
                        result += '<li><a href="#" ng-click="alert(123)">' + items[i]+ '</a></li>';
                    } else {
                        result += '<li class="' + clazz + '"><a href="#">' + items[i] + '</a></li>';
                    }
                }
            }

            return result + '</ul>';
        }
    }
    */
}]);


/* Controllers */

/**
 *  Controller for index view
 */
function IndexCtrl($scope, $routeParams, LearningOpportunity, $location) {

    // route to search page
    $scope.search = function() {
        if ($scope.queryString) {
            $location.path('/haku/' + $scope.queryString);
        }
    };

    // launch navigation script
    $scope.initNavigation = function() {
        OPH.Common.initDropdownMenu();
    }
};

/**
 *  Controller for search functionality 
 */
function SearchCtrl($scope, $routeParams, LearningOpportunity, SearchService, $location) {
    $scope.queryString = SearchService.getTerm(); // TODO: persist this

    if ($routeParams.queryString) {
        $scope.loResult = LearningOpportunity.query({queryString: $routeParams.queryString});
        $scope.queryString = $routeParams.queryString;
        $scope.showFilters = $scope.queryString ? true : false;

        SearchService.setTerm($routeParams.queryString);
    }

    // Perform search using LearningOpportunity service
    $scope.search = function() {
        if ($scope.queryString) {
            SearchService.setTerm($scope.queryString);
            $location.path('/haku/' + $scope.queryString);
        }
    };

    // Forward to parent learning opportunity info page
    $scope.selectLO = function(parentLOId, LOId) {
        var path = parentLOId ? parentLOId + '/' + LOId : LOId;
        $location.path('/info/' + path);
    };

    // launch navigation script
    $scope.initNavigation = function() {
        OPH.Common.initDropdownMenu();
    };
};

/**
 *  Controller for info views (parent and child)
 */
function InfoCtrl($scope, $routeParams, ParentLearningOpportunity, SearchService, $location) {
    $scope.queryString = SearchService.getTerm(); // TODO: persist this

    // fetch data for parent and its children LOs
    if ($routeParams.parentId) {
        $scope.parentId = $routeParams.parentId;
        $scope.parentLO = ParentLearningOpportunity.query({parentId: $routeParams.parentId}, function(data) {
            for (var index in data.children) {
                if (data.children[index].id == $routeParams.childId) {
                    $scope.childLO = data.children[index];
                    break;
                }
            }

            // prepare breadcrumb data
            $scope.breadcrumbItems = [{value: 'Hakutulokset', cssClass: ''}];

            if ($scope.childLO && $scope.parentLO) {
                $scope.breadcrumbItems.push({value: $scope.parentLO.name, cssClass: 'breadcrumb-item'});
                $scope.breadcrumbItems.push({value: $scope.childLO.degreeTitle, cssClass: 'breadcrumb-item current'});
            } else if ($scope.parentLO) {
                $scope.breadcrumbItems.push({value: $scope.parentLO.name, cssClass: 'breadcrumb-item current'});
            }
        });  
    }

    

    // go back to search view
    $scope.back = function() {
        $location.path('/haku/' + SearchService.getTerm());
    }
/*
    $scope.test = function() {
        console.log('test');
    }
*/
    // trigger once content is loaded
    $scope.$on('$viewContentLoaded', tabsMenu.build);
};



/*  Application module */

angular.module('kiApp', ['kiApp.services', 'kiApp.directives']).
config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
    $routeProvider.when('/haku/:queryString', {templateUrl: 'partials/hakutulokset.html', controller: SearchCtrl});
    $routeProvider.when('/index/', {templateUrl: 'partials/etusivu.html', controller: IndexCtrl});
    $routeProvider.when('/info/:parentId', {templateUrl: 'partials/ylataso.html', controller: InfoCtrl});
    $routeProvider.when('/info/:parentId/:childId', {templateUrl: 'partials/alataso.html', controller: InfoCtrl});
    $routeProvider.otherwise({redirectTo: '/index/'});

    //$locationProvider.html5Mode(true);
}]);

var OPH = OPH || {};

OPH.Common = {
    initDropdownMenu: function() {
        dropDownMenu.build();
    }
};
