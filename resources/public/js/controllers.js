function ForumController($scope, $http) {
  $http.get('forum').success(function(data) {
    $scope.posts = data;
  });
}
