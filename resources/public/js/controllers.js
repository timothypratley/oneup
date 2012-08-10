function ForumController($scope, $http) {
    $http.get("forum")
    .success(function (data) {
        $scope.posts = data;
    });
}

function AboutController($scope) {

}

// TODO: embed pirate in harbor instead
function HarborController($scope, $http, $log, $routeParams) {
	$log.info("username: " + $scope.username);
    $http.get("/pirate/" + $scope.username)
    .success(function (data, status) {
        $log.info(data, status);
        // TODO: Why is null taken as a string? must be a nicer way... (maybe return nothing from request)
        if (data !== "null") {
            $scope.pirate = data;
        }
    })
    .error($log.error);
}

function PirateController($scope, $http, $log, $routeParams) {
    $scope.username = $routeParams.name;
    $http.get("/pirate/" + $routeParams.name)
    .success(function (data, status) {
        $log.info(data, status);
        // TODO: Why is null taken as a string? must be a nicer way... (maybe return nothing from request)
        if (data !== "null") {
            $scope.pirate = data;
        }
    })
    .error($log.error);
}

function ProposeController($scope, $http, $log, $routeParams, $location) {
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
        .success(function (data, status) {
            $log.info(data, status);
            $location.path("/harbor");
        })
        .error($log.error);
    };
}

function VoteController($scope, $http, $log, $location) {
    $scope.submit = function (vote) {
        $http.post("/vote/" + vote)
        .success(function (data, status) {
            $log.info(data, status);
            $location.path("/harbor");
        })
        .error($log.error);
    }
}

function LoginController($scope, $http, $log, $location) {
    $scope.submit = function () {
        $http.post("/login", null,
            { params: { username: $scope.username, password: $scope.password} })
        .success(function (data, status) {
            $log.info(data, status);
            $scope.$emit("LoginSuccessEvent", $scope.username);
            $location.path("/harbor");
        })
        .error($log.error);
    }
}

function TopController($scope, $http, $log, $location) {
    $scope.$on("LoginSuccessEvent", function (e, username) {
        $scope.username = username;
    });
    $scope.logout = function () {
        $log.info("logout called");
        $scope.username = null;
        $http.post("/logout")
        .success(function (data, status) {
            $log.info(data, status);
            $location.path("/");
        })
        .error($log.error);
    }
}

function LeaderboardController($scope) {
	$http.post("/leaderboard")
	.success(function (data, status) {
		$log.info(data,status);
		$scope.leaderboard = data;
	})
	.error($log.error);
}
