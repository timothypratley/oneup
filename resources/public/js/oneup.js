angular.module('oneup', ['http-auth-interceptor'])
  .config(function ($routeProvider, $httpProvider) {
      $routeProvider
          .when("/about", {templateUrl: "partials/about",
                           controller: AboutCtrl})
          .when("/login", {templateUrl: "partials/login",
                           controller: LoginCtrl})
          .when("/propose/:size", {templateUrl: "partials/propose",
                                   controller: ProposeCtrl})
          .when("/pirate/:name", {templateUrl: "partials/pirate",
                                  controller: PirateCtrl})
          .when("/vote", {templateUrl: "partials/vote",
                          controller: VoteCtrl})
          .when("/harbor", {templateUrl: "partials/harbor",
                            controller: HarborCtrl})
          .when("/leaderboard", {templateUrl: "partials/leaderboard",
                                 controller: LeaderboardCtrl})
          .otherwise({redirectTo: "/about"});
  })
  .directive('authenticate', function($http,$log,authService) {
    return function(scope, elem, attrs) {
        var login = elem.find('#login');
        scope.$on('event:auth-loginRequired', function() {
            login.modal('show');
        });
        scope.$on('event:auth-loginConfirmed', function() {
            login.modal('hide');
        });
        $http.get('/ping').success(authService.loginConfirmed);
    };
  })
  .run(function () {
    //console.log("Running");
  });

angular.bootstrap(document.body, ['oneup']);

