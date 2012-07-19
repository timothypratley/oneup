angular.module('oneup', [], function($provide) {
  $provide.factory('Propose', function() {
    return $resource('/propose', {}, {
      create: {method: 'POST'}
    });
  });
  $provide.factory('Vote', function() {
    return $resource('/vote', {}, {
      create: {method: 'POST'}
    });
  });
});