angular.module('oneup', [])
  .config(function ($routeProvider, $httpProvider) {
      $routeProvider
      .when("/about", { templateUrl: "partials/about", controller: AboutController })
      .when("/login", { templateUrl: "partials/login", controller: LoginController })
      .when("/propose/:size", { templateUrl: "partials/propose", controller: ProposeController })
      .when("/pirate/:name", { templateUrl: "partials/pirate", controller: PirateController })
      .when("/vote", { templateUrl: "partials/vote", controller: VoteController })
      .when("/harbor", { templateUrl: "partials/harbor", controller: HarborController })
      .when("/leaderboard", { templateUrl: "partials/leaderboard", controller: LeaderboardController })
      .otherwise({ redirectTo: "/about" });
	  $httpProvider.responseInterceptors.push(function($q) {
		function success(response) {
		  return response;
		}
	 
		function error(response) {
		  var deferred, req;
		  if (response.status == 401) {
			deferred = $q.defer();
			req = {
			  config: response.config,
			  deferred: deferred
			}
			$scope.requests401.push(req);
			$scope.$broadcast('event:loginRequired');
			return deferred.promise;
		  }
		  // otherwise
		  return $q.reject(response);
		}
	 
		return function(promise) {
		  return promise.then(success, error);
		}
	  })
  })
  .run(function () {
      //console.log("foo");
  });


function AppController($scope, $http) {
    /**
    * Holds all the requests which failed due to 401 response.
    */
    scope.requests401 = [];

    /**
    * On 'event:loginConfirmed', resend all the 401 requests.
    */
    scope.$on('event:loginConfirmed', function () {
        var i, requests = scope.requests401;
        for (i = 0; i < requests.length; i++) {
            retry(requests[i]);
        }
        scope.requests401 = [];

        function retry(req) {
            $http(req.config).then(function (response) {
                req.deferred.resolve(response);
            });
        }
    });

    /**
    * On 'event:loginRequest' send credentials to the server.
    */
    scope.$on('event:loginRequest', function (event, username, password) {
        var payload = $.param({ j_username: username, j_password: password });
        var config = {
            headers: { 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8' }
        }
        $http.post('j_spring_security_check', payload, config).success(function (data) {
            if (data === 'AUTHENTICATION_SUCCESS') {
                scope.$broadcast('event:loginConfirmed');
            }
        });
    });

    /**
    * On 'logoutRequest' invoke logout on the server and broadcast 'event:loginRequired'.
    */
    scope.$on('event:logoutRequest', function () {
        $http.put('j_spring_security_logout', {}).success(function () {
            ping();
        });
    });

    /**
    * Ping server to figure out if user is already logged in.
    */
    function ping() {
        $http.get('rest/ping').success(function () {
            scope.$broadcast('event:loginConfirmed');
        });
    }
    ping();
}
