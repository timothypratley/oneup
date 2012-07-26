angular.module('oneup', ['oneup.service'])
  .run(function() {
                   console.log("foo");
    //main
  });


function AppController($scope, $http) {
    /**
   * Holds all the requests which failed due to 401 response.
   */
  scope.requests401 = [];
 
  /**
   * On 'event:loginConfirmed', resend all the 401 requests.
   */
  scope.$on('event:loginConfirmed', function() {
    var i, requests = scope.requests401;
    for (i = 0; i < requests.length; i++) {
      retry(requests[i]);
    }
    scope.requests401 = [];
 
    function retry(req) {
      $http(req.config).then(function(response) {
        req.deferred.resolve(response);
      });
    }
  });
 
  /**
   * On 'event:loginRequest' send credentials to the server.
   */
  scope.$on('event:loginRequest', function(event, username, password) {
    var payload = $.param({j_username: username, j_password: password});
    var config = {
      headers: {'Content-Type':'application/x-www-form-urlencoded; charset=UTF-8'}
    }
    $http.post('j_spring_security_check', payload, config).success(function(data) {
      if (data === 'AUTHENTICATION_SUCCESS') {
        scope.$broadcast('event:loginConfirmed');
      }
    });
  });
 
  /**
   * On 'logoutRequest' invoke logout on the server and broadcast 'event:loginRequired'.
   */
  scope.$on('event:logoutRequest', function() {
    $http.put('j_spring_security_logout', {}).success(function() {
      ping();
    });
  });
 
  /**
   * Ping server to figure out if user is already logged in.
   */
  function ping() {
    $http.get('rest/ping').success(function() {
      scope.$broadcast('event:loginConfirmed');
    });
  }
  ping();
}
