angular.module('kiApp.Navigation', []).

/**
 *  Updates the title element of the page.
 */
directive('kiNavigation', function() {
    return {
        restrict: 'A',
        template: '<ul data-tree data-val="navigation" data-level="1"></ul>',
        controller: function($scope, NavigationService) {
            NavigationService.query().then(function(result) {
                $scope.navigation = result;
            });
        }
    }
}).

directive('tree', function ($compile) {
return {
    restrict: 'A',
    terminal: true,
    replace: true,
    scope: { 
        val: '=',
        level: '='
    },
    link: function (scope, element, attrs) {

        scope.$watch('val', function(value) {
            if (value) {
                update();
            }
        });
        
        var update = function() {
            console.log(scope.level);
            var template = '';

            if (angular.isArray(scope.val)) {
                template += '<ul class="level-{{level}}"><li ng-repeat="item in val"><a data-ng-href="item.link">{{item.label}}</a><ul data-tree data-val="item.children" data-level="level + 1"></ul></li></ul>';
            }

            var newElement = angular.element(template);
            $compile(newElement)(scope);
            element.replaceWith(newElement);
        };
    }
}
});