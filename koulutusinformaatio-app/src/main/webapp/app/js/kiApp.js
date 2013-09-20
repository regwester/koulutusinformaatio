/*  Application module */

var kiApp = angular.module('kiApp', ['kiApp.services', 'kiApp.directives', 'ui.bootstrap', 'angulartics', 'angulartics.piwik']);
kiApp.config(['$routeProvider', '$locationProvider', '$analyticsProvider', function($routeProvider, $locationProvider, $analyticsProvider, $rootScope) {
    $analyticsProvider.virtualPageviews(false);
    $analyticsProvider.firstPageview(false);
    $routeProvider.when('/haku/:queryString', {
    	templateUrl: 'partials/hakutulokset.html', 
    	controller: SearchCtrl
    });
    $routeProvider.when('/tutkinto/:parentId', {
    	templateUrl: 'partials/ylataso.html', 
    	controller: InfoCtrl
    });
    $routeProvider.when('/koulutusohjelma/:childId', {
    	templateUrl: 'partials/alataso.html', 
    	controller: InfoCtrl
    });
    $routeProvider.when('/muistilista', {
        templateUrl: 'partials/applicationbasket/applicationbasket.html',
        controller: ApplicationBasketCtrl
    });
    $routeProvider.otherwise({
    	redirectTo: '/haku/'
    });
}]);

kiApp.filter('escape', function() {
  return window.escape;
});

kiApp.filter('encodeURIComponent', function() {
    return window.encodeURIComponent;
});

// initialize i18n library
kiApp.run(['LanguageService', function(LanguageService) {
    i18n.init({
        resGetPath : 'locales/__ns__-__lng__.json',
        lng : LanguageService.getLanguage(),
        ns: 'language',
        getAsync : false,
        sendMissing : false,
        fallbackLng : 'fi',
        debug : false
    });

    // initialize piwik analytics tool
    OPH.Common.initPiwik();
}]);

kiApp.constant('kiAppConstants', {
    searchResultsPerPage: 30,
    searchResultsStartPage: 1,
    applicationBasketLimit: 5,
    contextRoot: '../static/'
});

var OPH = OPH || {};

OPH.Common = {
    initHeader: function() {},
    initPiwik: function() {
        var siteDomain = document.domain;
        var piwikSiteId = 2;
        if(siteDomain=='opintopolku.fi'){
            piwikSiteId = 4;
        }else if(siteDomain=='virkailija.opintopolku.fi'){
            piwikSiteId = 3;
        }else if(siteDomain=='testi.opintopolku.fi'){
            piwikSiteId = 1;
        }else if(siteDomain=='testi.virkailija.opintopolku.fi'){
            piwikSiteId = 5;
        }else{
            piwikSiteId = 2;
        }

        window._paq = window._paq || [];
        _paq.push(["setDocumentTitle", document.domain + "/" + document.title]);
        //_paq.push(["trackPageView"]);
        _paq.push(["enableLinkTracking"]);

        (function() {
            var u=(("https:" == document.location.protocol) ? "https" : "http") + "://nat-piwik-poc.oph.ware.fi/analytics/";
            _paq.push(["setTrackerUrl", u+"piwik.php"]);
            _paq.push(["setSiteId", piwikSiteId]);
            var d=document, g=d.createElement("script"), s=d.getElementsByTagName("script")[0]; g.type="text/javascript";
            g.defer=true; g.async=true; g.src=u+"piwik.js"; s.parentNode.insertBefore(g,s);
        })();
    }
};
