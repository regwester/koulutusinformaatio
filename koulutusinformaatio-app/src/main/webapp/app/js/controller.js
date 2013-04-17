/*  Services */

angular.module('kiApp.services', ['ngResource']).
factory('LearningOpportunity', function($resource){
    return $resource('../lo/search/:queryString', {}, {
        query: {method:'GET', isArray:true}
    });
});


/* Controllers */

function SearchCtrl($scope, $routeParams, LearningOpportunity) {
    if ($routeParams.queryString) {
        $scope.loResult = LearningOpportunity.query({queryString: $routeParams.queryString});
        $scope.queryString = $routeParams.queryString;
        $scope.showFilters = $scope.queryString ? true : false;
    }

    // Perform search using LearningOpportunity service
    $scope.search = function() {
        if ($scope.queryString) {
            $scope.loResult = LearningOpportunity.query({queryString: $scope.queryString});
        } else {
            $scope.loResult = [];
        }

        $scope.showFilters = $scope.queryString ? true : false;
    };

    // TODO: how to make sure DOM is ready?
    //$scope.$on('$viewContentLoaded', ngReady);
};

function IndexCtrl($scope, $routeParams, LearningOpportunity, $location) {

    // route to search page
    $scope.search = function() {
        if ($scope.queryString) {
            $location.path('/haku/' + $scope.queryString);
        }
    };
};

/*  Application module */

angular.module('kiApp', ['kiApp.services']).
config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/haku/:queryString', {templateUrl: 'partials/hakutulokset.html', controller: SearchCtrl});
    $routeProvider.when('/index/', {templateUrl: 'partials/etusivu.html', controller: IndexCtrl});
    $routeProvider.otherwise({redirectTo: '/index/'});
}]);
