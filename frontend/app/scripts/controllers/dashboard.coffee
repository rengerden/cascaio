'use strict'

###*
 # @ngdoc function
 # @name frontendApp.controller:DashboardCtrl
 # @description
 # # DashboardCtrl
 # Controller of the frontendApp
###
angular.module('frontendApp')
  .controller 'DashboardCtrl', ($scope) ->
    $scope.awesomeThings = [
      'HTML5 Boilerplate'
      'AngularJS'
      'Karma'
    ]
