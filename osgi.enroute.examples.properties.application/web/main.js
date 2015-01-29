'use strict';

(function() {

	var MODULE = angular.module('osgi.enroute.examples.properties',
			[ 'ngRoute', 'ngResource' ]);

	MODULE.config( function($routeProvider) {
		$routeProvider.when('/', { controller: mainProvider, templateUrl: '/osgi.enroute.examples.properties/main/htm/home.htm'});
		$routeProvider.when('/about', { templateUrl: '/osgi.enroute.examples.properties/main/htm/about.htm'});
		$routeProvider.otherwise('/');
	});
	
	MODULE.run( function($rootScope, $location) {
		$rootScope.alerts = [];
		$rootScope.closeAlert = function(index) {
			$rootScope.alerts.splice(index, 1);
		};
		$rootScope.page = function() {
			return $location.path();
		}
	});
	
	
	
	var mainProvider = function($scope, $http) {
		
		$http.get('/rest/property').then(
				function(d) {
					$scope.properties = d.data;
				}, function(d) {
					$scope.alerts.push( { type: 'danger', msg: 'Failed with ['+ d.status + '] '+ d.statusText });
				}
		);
		
		$scope.breaker = function(s) {
			if ( s.length < 50)
				return s;
			
			return s.replace(",",", ");
		}
		
		$scope.query = function(key) {
				$http.get('/rest/property/'+key).then(
						function(d) {
							$scope.value = d.data;
						}, function(d) {
							$scope.alerts.push( { type: 'danger', msg: 'Failed with ['+ d.status + '] '+ d.statusText });
						}
				);
		};
	
	}
	
})();
