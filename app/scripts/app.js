(function ()
{
    'use strict';

    var app = angular.module('app', ['ngRoute']);

    app.config(function ($routeProvider)
    {
        $routeProvider.when('/', {
            templateUrl: 'views/view.html'
        });
    });

    app.controller('AppCtrl', function ()
    {
        this.message = 'Hello Angular App!';
    });
})();








