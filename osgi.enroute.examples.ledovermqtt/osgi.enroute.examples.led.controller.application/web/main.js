/*******************************************************************************
 * Copyright 2015 OSGi Alliance
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
'use strict';

(function() {

	var MODULE = angular.module('osgi.enroute.examples.led.controller',
			[ 'ngRoute', 'ngResource', 'enEasse' ]);
	var alerts = [];
	
	function error(msg) {
		alerts.push({
			type : 'error',
			msg : e
		});
	}

	MODULE.config( function($routeProvider) {
		$routeProvider.when('/', 
		{ 
			controller: mainProvider, 
			templateUrl: '/osgi.enroute.examples.led.controller/main/htm/home.htm'
		});
		$routeProvider.when('/settings', 
		{ 
			controller : mainProvider,
			templateUrl: '/osgi.enroute.examples.led.controller/main/htm/settings.htm'
		});
		$routeProvider.otherwise('/');
	});
	
	MODULE.run( function($rootScope, $location, en$easse) {
		$rootScope.alerts = alerts;
		$rootScope.closeAlert = function(index) {
			$rootScope.alerts.splice(index, 1);
		};
		$rootScope.page = function() {
			return $location.path();
		}
		
		/**
			Event Admin Server Sent Events for LED to be switched off
		*/
		en$easse.handle("osgi/enroute/led/off", function(e) {
			$rootScope.$applyAsync(function() {
				var path = "/osgi.enroute.examples.led.controller/images/";
				$rootScope.path = path + "bulb-off.png";
			});
		}, error );
		
		/**
			Event Admin Server Sent Events for LED to be switched on
		*/
		en$easse.handle("osgi/enroute/led/on", function(e) {
			$rootScope.$applyAsync(function() {
				var path = "/osgi.enroute.examples.led.controller/images/";
				$rootScope.path = path + "bulb-on.png";
			});
		}, error );
		
		/**
			Event Admin Server Sent Events for LED OFF Status
		*/
		en$easse.handle("osgi/enroute/led/status/off", function(e) {
			$rootScope.$applyAsync(function() {
				var path = "/osgi.enroute.examples.led.controller/images/";
				$rootScope.path = path + "bulb-off.png";
			});
		}, error );
		
		/**
			Event Admin Server Sent Events for LED ON Status
		*/
		en$easse.handle("osgi/enroute/led/status/on", function(e) {
			$rootScope.$applyAsync(function() {
				var path = "/osgi.enroute.examples.led.controller/images/";
				$rootScope.path = path + "bulb-on.png";
			});
		}, error );
	});
	
	/**
		Main LED Manager
	*/
	function LEDManager($resource, error)
	{
		var THIS = this;
		
		THIS.host = ""; 
		THIS.port = ""; 
		THIS.username = ""; 
		THIS.password = ""; 
		THIS.topic = ""; 
		THIS.pin = ""; 
		
		var interceptor = {
			responseError : error
		};
		
		/**
			Defining Web-service REST Resource to retrieve and update settings
		*/
		var settings = $resource("/rest/settings", {}, {
			retrieve : {
				method : 'GET',
				interceptor : interceptor
			},
			store : {
				method : 'PUT',
				interceptor : interceptor
			}
		});		
		
		/**
			Retrieves Configuration
		*/
		THIS.refresh = function() {
			settings.retrieve({}, function(data) {
				THIS.host = data.host;
				THIS.port = parseInt(data.port); 
				THIS.username = data.username; 
				THIS.password = data.password; 
				THIS.topic = data.topic; 
				THIS.pin = THIS.parsePin(data.pin);
			});
		}
		
		/**
			The UI is taking user input for the PIN as an integer but the backend enum accepts the PIN 
			as PINxx where xx is the GPIO PIN No.
			This function is used to parse the formatted PIN no in such a way that only the PIN No can 
			be shown in the UI
		*/
		THIS.parsePin = function(topic) {
			if (topic != null && topic.substring(3, 5) != null)
				return parseInt(topic.substring(3, 5));
			else
				return parseInt(topic);
		}
		
		/**
			Stores the new Configuration or Settings
		*/
		THIS.store = function() {
			settings.store({
				host : THIS.host,
				port : THIS.port,
				username : THIS.username,
				password : THIS.password,
				topic : THIS.topic,
				pin : THIS.confPin(THIS.pin),
			}, JSON.stringify({host: THIS.host, port: THIS.port, username: THIS.username, password: THIS.password, topic: THIS.topic, pin: THIS.confPin(THIS.pin)}), THIS.finalize);
		}
		
		/**
			Finalization Callback after updating Settings
		*/
		THIS.finalize = function() {
			THIS.refresh;
			alert("Configurations Updated");
			$rootScope.alerts.push( { type: 'success', msg: "Configurations Updated" });
		}
		
		/**
			Used to parse the user input of PIN to be preferred format the backend needs ie from 
			single digit or double digit pin no to PINxx format
		*/
		THIS.confPin = function(num) {
			var str = "";
			var no = str + num;
			if (no.length == 1)
				no = "0" + num;
				
			return "PIN" + no;
		}
	}

	/**
		The LED Controller
	*/
	var mainProvider = function($scope, $resource) {
		function error(e) {
			$scope.alerts.push({
				type : 'danger',
				msg : e.status
			});
		}
		$scope.bm = new LEDManager($resource, error);
	}
	
})();
