/**
 * OSGI ENROUTE EXAMPLES CM
 * 
 * This module provides a demo of the capabilities of the OSGi Configuration
 * Admin service.
 */

(function() {

	'use strict';

	var MODULE = angular.module('osgi.enroute.examples.scheduler', [ 'ngRoute',
			'ngResource', 'enEasse', 'enMarkdown' ]);

	var alerts = [];

	function error(msg) {
		alerts.push({
			msg : msg,
			type : 'danger'
		});
	}

	//
	// CONFIG
	//

	MODULE.config(function($routeProvider) {

		$routeProvider.when('/about', {
			templateUrl : 'main/htm/about.htm'
		});

		$routeProvider.when('/', {
			controller : home,
			templateUrl : 'main/htm/home.htm'
		});

		$routeProvider.otherwise('/');

	});

	//
	// RUN
	//

	MODULE.run(function($rootScope, $location, en$easse, $window, $resource) {

		$rootScope.alerts = alerts;
		$rootScope.page = function() {
			return $location.path();
		}

		$rootScope.sm = new SchedulerManager($resource, $rootScope);
		en$easse.handle("osgi/enroute/examples/scheduler/*", $rootScope.sm.update, error);
	});

	function SchedulerManager($resource, $scope) {
		var THIS = this;
		$scope.trackers = {};
		var interceptor = {
				responseError : error
			};

	
		var scheduler = $resource("/rest/scheduler/:id", {}, {
			put: { method: "PUT", interceptor: interceptor },
			get: { method: "GET", interceptor: interceptor }
			
		});
		
		THIS.examples = scheduler.get({});
		
		THIS.toName = function(name) {
			var s = name.replace(/([A-Z])/g, " $1").substring(1);
			return name.substring(0,1).toUpperCase() + s;
		}
		
		THIS.execute = function(name, parameter) {
			if ( angular.isDate(parameter))
				parameter = parameter.toISOString();
			scheduler.put({id:name, parameter:parameter},{})
		}
		
		THIS.update = function(x) {
			if ( x.lastEvent === "DELETED")
				delete $scope.trackers[x.id];
			else {
				$scope.trackers[x.id]=x;;
			}
			$scope.$digest();
		}
		
		THIS.remove = function(r) {
			scheduler.remove({id:r.id},{})
			delete $scope.trackers[r.id];
		}
	}
	
	
	function home($scope, $resource, en$easse) {
	}
})();
