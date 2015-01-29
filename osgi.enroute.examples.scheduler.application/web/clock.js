(function(){
	
	var MODULE = angular.module("enClock",[]);
	
	MODULE.directive( "enClock", function($interval) {
		return {
			scope: {},
			relplace: true,
			template: "<div style='color:white;background-color:#337ab7;padding:4px;border-radius:3px;'>{{date | date: 'ss mm hh d MMM EEE yyyy'}}</div>",
			link: function(scope,elem,attr) {
				var t = $interval(function() {
					scope.date = new Date().getTime();
				}, 100);
			}
		};
	});	
	
})();