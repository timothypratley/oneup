function ForumController($scope, $http) {
    $http.get("forum")
    .success(function (data) {
        $scope.posts = data;
    });
}

function AboutController($scope) {

}

function HarborController($scope) {

}

function ProposeController($scope, $http, $log) {
    $scope.gold = [2, 2, 2, 2, 2];
    $scope.total = function () {
        var tt = 0;
        for (var ii in $scope.gold) { tt += $scope.gold[ii]; }
        return tt;
    };
    $scope.submit = function () {
        $http.post("/propose/" + $scope.gold.join("/"))
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
