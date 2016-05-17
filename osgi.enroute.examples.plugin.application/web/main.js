'use strict';

(function() {

	var MODULE = angular.module('osgi.enroute.examples.plugin',
			[ 'ngRoute', 'ngResource' ]);

	MODULE.config( function($routeProvider) {
		$routeProvider.when('/', { controller: mainProvider, templateUrl: '/osgi.enroute.examples.plugin/main/htm/home.htm'});
		$routeProvider.when('/about', { templateUrl: '/osgi.enroute.examples.plugin/main/htm/about.htm'});
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
		var fail = function(d) {
			$scope.alerts.push( { type: 'danger', msg: 'Failed with ['+ d.status + '] '+ d.statusText });
		}
		
		$scope.orders = {
			search: function(query) {
				$http.get('/rest/find/'+query).then(
						function(d) {
							$scope.orders.products = d.data;
						}, fail
				);
			},
			buy: function(supplier,id) {
				$http.get('/rest/buy/'+supplier +'/' + id).then(
						function(d) {
							$scope.alerts.push( { type: 'success', msg: 'bought ' + id });
						}, fail
				);
			}
		};	
	}
	
})();
