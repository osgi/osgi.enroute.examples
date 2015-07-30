'use strict';

(function() {

	var MODULE = angular.module('osgi.enroute.trains', [ 'ngRoute',
			'ngResource', 'enJsonrpc', 'enEasse' ]);

	var resolveBefore;
	var alerts = [];
	var trains = {
		events : [],
		segments : {},
		locations: {},
		ep : null
	};

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
		resolveBefore = {
			trainsEndpoint : en$jsonrpcProvider.endpoint("trains")
		};

		$routeProvider.when('/', {
			controller : mainProvider,
			templateUrl : '/osgi.enroute.trains/main/htm/home.htm',
			resolve : resolveBefore
		});
		$routeProvider.when('/about', {
			templateUrl : '/osgi.enroute.trains/main/htm/about.htm',
			resolve : resolveBefore
		});
		$routeProvider.otherwise('/');

	});

	MODULE.run(function($rootScope, $location, en$easse, en$jsonrpc) {

		$rootScope.alerts = alerts;
		$rootScope.trains = trains;

		en$easse.handle("osgi/trains/*", function(e) {
			$rootScope.$applyAsync(function() {
				trains.events.push(e);
				if ( trains.events.length > 10)
					trains.events.splice(0,1);
				if ( e.type == "LOCATED") {
					trains.locations[e.train]=e.segment;
				}
			});
		}, function(e) {
			alerts.push({
				type : 'error',
				msg : e
			});
		});

		resolveBefore.trainsEndpoint().then(function(ep) {
			trains.ep = ep;
			ep.getSegments().then( function(d) {
				for ( var k in d ) {
					var seg = d[k];
					seg.trains = [];
					switch(seg.type) {
					case "SIGNAL":
						seg.signal = "red";
						break;
						
					case "SWITCH":
						seg.alternative = false;
						break;
					}
				}
				trains.segments = d;
			});
		});

		$rootScope.closeAlert = function(index) {
			alerts.splice(index, 1);
		};
		$rootScope.page = function() {
			return $location.path();
		}

	});

	var mainProvider = function($scope) {
	}

})();
