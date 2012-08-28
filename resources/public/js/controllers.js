function ForumCtrl($scope, $http) {
    $http.get("forum")
    .success(function (data) {
        $scope.posts = data;
    });
}

function AboutCtrl($scope) {

}

// TODO: embed pirate in harbor instead
function HarborCtrl($scope, $http, $log, $routeParams, user) {
    $http.get("/pirate/" + user.username)
    .success(function (data) {
        $scope.pirate = angular.fromJson(data);
    })
    .error($log.error);
}

function PirateCtrl($scope, $http, $log, $routeParams) {
    $http.get("/pirate/" + $routeParams.name)
    .success(function (data) {
        $scope.pirate = angular.fromJson(data);
    })
    .error($log.error);
}

function ProposeCtrl($scope, $http, $log, $routeParams, $location) {
    var ii,
        share = Math.floor(10 / $routeParams.size);

    // initialize the gold to split among party of 'size' pirates
    $scope.gold = [];
    for (ii = 0; ii < $routeParams.size; ii++) {
        $scope.gold[ii] = { gold: share };
    }

    // sum all the gold allocated
    $scope.total = function () {
        var ii,
            tt = 0;
        for (ii in $scope.gold) { tt += $scope.gold[ii].gold; }
        return tt;
    };

    // send the proposal to the server
    $scope.submit = function () {
        var a = [];
        for (x in $scope.gold) { a[x] = $scope.gold[x].gold; }
        $http.post("/propose/" + a.join("/"))
        .success($log.info(data))
        .error($log.error);
        $location.path("/harbor");
    };
}

function VoteCtrl($scope, $http, $log, $location) {
    $scope.submit = function (vote) {
        $http.post("/vote/" + vote)
        .success($log.info)
        .error($log.error);
        $location.path("/harbor");
    }
}

function LoginCtrl($scope, $http, $log, $location, authService) {
    $scope.username = "";
    $scope.password = "";
    $scope.submit = function () {
        // capture the current username
        var username = $scope.username;
        $http.post("/login", null,
            {params: {username: username,
                      password: $scope.password}})
        .success(authService.loginConfirmed)
        .error($log.error);
    }
}

function TopCtrl($scope, $http, $log, $location, user, authService) {
    $scope.user = user;
    $scope.$on('event:auth-loginConfirmed', function() {
        //TODO: how to bind directly to user?
        $scope.user = user;
        $log.info(user);
    });
    $scope.$on('event:auth-loginRequired', function() {
        $scope.user = {};
    });
    $scope.logout = function () {
        $log.info("logout called");
        user.username = null;
        $http.post("/logout")
            .success($log.info)
            .error($log.error);
        $location.path("/");
    }
    $scope.login = function () {
        authService.loginRequest();
    }
}

function LeaderboardCtrl($scope, $http, $log) {
    $http.get("/leaderboard")
    .success(function (data) {
        $scope.leaderboard = angular.fromJson(data);
    })
    .error($log.error);
}

