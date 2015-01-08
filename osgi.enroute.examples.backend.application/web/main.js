'use strict';

(function() {

	var MODULE = angular.module('osgi.enroute.examples.backend', [ 'ngRoute',
			'ngResource','enMarkdown' ]);

	MODULE.config(function($routeProvider) {
		$routeProvider.when('/', {
			controller : mainProvider,
			templateUrl : '/osgi.enroute.examples.backend/main/htm/home.htm'
		});
		$routeProvider.when('/about', {
			templateUrl : '/osgi.enroute.examples.backend/main/htm/about.htm'
		});
		$routeProvider.otherwise('/');
	});

	MODULE.run(function($rootScope, $location) {
		$rootScope.alerts = [];
		$rootScope.closeAlert = function(index) {
			$rootScope.alerts.splice(index, 1);
		};
		$rootScope.page = function() {
			return $location.path();
		}
	});

	//
	// The backend manager. Will provide the CRUD operations on the backend
	// files.
	//

	function BackendManager($resource, error)
	{
		var THIS = this;
		
		THIS.resources = [];
		THIS.name = "";
		THIS.text = "";
		THIS.types = [];
		THIS.type = null;
		
		var interceptor = {
			responseError : error
		};
		
		var backends = $resource("/rest/backend/:type/:name", {}, {
			list : {
				method : 'GET',
				interceptor : interceptor,
				isArray : true
			},
			types : {
				method : 'GET',
				interceptor : interceptor,
				isArray : true
			},
			store : {
				method : 'PUT',
				interceptor : interceptor
			},
			read : {
				method : 'GET',
				interceptor : interceptor
			}
		});
		
		
		THIS.refresh = function() {
			THIS.resources = backends.list({
				type : THIS.type
			});
			THIS.name = "";
			THIS.text = "";
		}

		THIS.types = backends.types({}, function(types) {
			if (THIS.types.length) {
				THIS.type = types[0];
				THIS.refresh();
			}
		});

		THIS.store = function() {
			backends.store({
				type : THIS.type,
				name : THIS.name
			}, JSON.stringify(THIS.text), THIS.refresh);
		}

		THIS.read = function(meta) {
			THIS.name = meta.name;
			backends.read({type:THIS.type, name: meta.name}, function(data) {
				THIS.text = data.data;
				THIS.name = data.name;
				THIS.typs = data.type;
			});
		}
		
		THIS.remove = function(r) {
			backends.remove({
				type : THIS.type,
				name : r.name
			}, THIS.refresh);
		}
	}

	var mainProvider = function($scope, $resource) {
		function error(e) {
			$scope.alerts.push({
				type : 'danger',
				msg : e.status
			});
		}

		$scope.bm = new BackendManager($resource, error);
	}

})();
