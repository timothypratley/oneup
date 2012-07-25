function ForumController($scope, $http) {
  $http.get('forum').success(function(data) {
    $scope.posts = data;
  });
}

function ProposalController($scope, $http, $log) {
  $scope.gold = [2,2,2,2,2];
  $scope.total = function() {
    var tt=0;
    for(var ii in $scope.gold) { tt += $scope.gold[ii]; }
    return tt;
  };
  $scope.submit = function() {
    $http.post('/propose/' + $scope.gold.join('/'))
      .success($log.info)
      .error($log.error);
  };                          
}

function VoteController($scope, $http, $log) {
  $scope.submit = function(vote) {
    $http.post('/vote/' + vote)
      .success($log.info)
      .error($log.error);
  }
}

function LoginController($scope, $http, $log) {
  $scope.submit = function() {
    $http.post('/login', null,
      {params: {username:$scope.username, password:$scope.password}})
      .success($log.info)
      .error($log.error);
  }
}