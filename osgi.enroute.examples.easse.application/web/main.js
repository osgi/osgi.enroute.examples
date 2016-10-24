'use strict';

(function() {
	var alerts = [];
	var events = [];

	function error(msg) {
		alerts.push({
			type : 'error',
			msg : e
		});
	}
	
	var MODULE = angular.module('osgi.enroute.examples.easse',
			[ 'ngRoute', "enEasse"]);

	MODULE.config( function($routeProvider) {
		$routeProvider.when('/', { controller: mainProvider, templateUrl: '/osgi.enroute.examples.easse/main/htm/home.htm'});
		$routeProvider.when('/about', { templateUrl: '/osgi.enroute.examples.easse/main/htm/about.htm'});
		$routeProvider.otherwise('/');
	});
	
	MODULE.run( function($rootScope, $location, en$easse) {
		$rootScope.alerts = alerts;
		$rootScope.events = events;
		
		en$easse.handle("*", function(e) {
			$rootScope.$applyAsync(function() {
				events.push(e);
			});
		}, error );
		
		$rootScope.page = function() {
			return $location.path();
		};
		
	});

	var mainProvider = function($scope, $http) {
		$scope.event = function(topic) {
			$http.put('/rest/topic', {topic: topic, time: new Date()}).then(
					undefined, function(d) {
						$scope.alerts.push( { type: 'danger', msg: 'Failed with ['+ d.status + '] '+ d.statusText });
					}
			);			
		}
	}
	
})();
