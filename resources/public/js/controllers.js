function ForumController($scope, $http) {
  $http.get('forum').success(function(data) {
    $scope.posts = data;
  });
}

function ProposalController($scope) {
  $scope.gold1 = 2;                                   
  $scope.gold2 = 2;                                   
  $scope.gold3 = 2;                                   
  $scope.gold4 = 2;                                   
  $scope.gold5 = 2;         
  $scope.submit = function() {
    /*new Propose().$create({
      gold1: $scope.gold1,
      gold2: $scope.gold2,
      gold3: $scope.gold3,
      gold4: $scope.gold4,
      gold5: $scope.gold5});*/
     //$http.post('/propose', "hi");
     console.log("hi");
  };                          
}

function VoteController($scope) {
                                 
}
