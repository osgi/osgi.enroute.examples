'use strict';

(function() {

	var MODULE = angular.module('osgi.enroute.iot.domotica',
			[ 'ngRoute', 'ngResource', 'jsplumb' ]);

	MODULE.config( function($routeProvider) {
		$routeProvider.when('/', { controller: mainProvider, templateUrl: '/osgi.enroute.iot.domotica/main/htm/home.htm'});
		$routeProvider.when('/about', { templateUrl: '/osgi.enroute.iot.domotica/main/htm/about.htm'});
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
		$scope.nodes = [
	                    { name: 'Node 1', position: [10, 10] }
	                    
//	                    ,
//	                    { name: 'Node 2', position: [100, 100] },
//	                    { name: 'Node 3', position: [200, 200] },
//	                    { name: 'Node 4', position: [300, 300] }
	                ];
		
		$scope.connections = [
//	                    { source: $scope.nodes[0], target: $scope.nodes[1], options: { paintStyle: { strokeStyle: '#666' } } }
	    ];

        $scope.removeConnection = function () {
            this.connections.splice(0, 1);
        };
        
        $scope.logConnection = function (receiver, $fromScope, $toScope) {
            console.log({ receiver: receiver, event: 'new connection', source: $fromScope.node.name, target: $toScope.node.name });
        };

	}
	
})();
