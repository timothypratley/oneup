var INTEGER_REGEXP = /^\-?\d*$/;

angular.module('oneup.service', ['ngResource'])
  .factory('interceptor', function($q, $log) {
    function success(response) {
      $log.info('Successful response: ' + response);
      return response;
    }
    function error(response) {
      var status = response.status;
      $log.error('Response status: ' + status + '. ' + response);
      if (status == 401) {
        var deferred = $q.defer();
        var req = {
          config: response.config,
          deferred: deferred
        }
        scope.requests401.push(req);
        scope.$broadcast('event:loginRequired');
        return deferred.promise;
      }
      // otherwise
      return $q.reject(response); //similar to throw response;
    }
    return function(promise) {
      return promise.then(success, error);
    }
  })
  .factory('Propose', function($resource) {
    return $resource('/propose', {}, {
      create: {method: 'POST'}
    });
  })
  .factory('Vote', function($resource) {
    return $resource('/vote', {}, {
      create: {method: 'POST'}
    });
  })
  .directive('integer', function() {
    return {
      require: 'ngModel',
      link: function(scope, elm, attrs, ctrl) {
        ctrl.$parsers.unshift(function(viewValue) {
          if (INTEGER_REGEXP.test(viewValue)) {
            // it is valid
            ctrl.$setValidity('integer', true);
            return viewValue;
          } else {
            // it is invalid, return undefined (no model update)
            ctrl.$setValidity('integer', false);
            return undefined;
          }
        });
      }
    };
  });
