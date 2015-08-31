'use strict';

(function() {

	var MODULE = angular.module('osgi.enroute.examples.jsonrpc', [ 'ngRoute',
			'enJsonrpc', 'enMarkdown' ]);

	var resolveBefore = {};
	var alerts = [];

	function error(msg) {
		alerts.push({
			msg : msg,
			type : 'danger'
		});
	}

	MODULE.config(function($routeProvider, en$jsonrpcProvider) {
		en$jsonrpcProvider.setNotification({
			error : error
		})
		en$jsonrpcProvider.route();
		
		resolveBefore.exampleEndpoint = en$jsonrpcProvider.endpoint("exampleEndpoint");

		$routeProvider.when('/', {
			controller : MainController,
			templateUrl : '/osgi.enroute.examples.jsonrpc/main/htm/home.htm',
			resolve : resolveBefore
		});
		$routeProvider.when('/about', {
			templateUrl : '/osgi.enroute.examples.jsonrpc/main/htm/about.htm',
			resolve : resolveBefore
		});
		$routeProvider.otherwise('/');
		
	});

	MODULE.run(function($rootScope, en$jsonrpc, $location) {
		$rootScope.alerts = alerts;
		$rootScope.closeAlert = function(index) {
			alerts.splice(index, 1);
		};
		$rootScope.page = function() {
			return $location.path();
		}
		resolveBefore.exampleEndpoint().then(function(exampleEndpoint) {
			$rootScope.exampleEndpoint = exampleEndpoint;
		});
	});

	var MainController = function($scope, en$jsonrpc) {
		$scope.upper = function(s) {
			$scope.exampleEndpoint.toUpper(s).then(function(d) {
				alerts.push({msg: d, type:"info"});
			});
		}
		$scope.welcome = $scope.exampleEndpoint.descriptor
	}

})();
