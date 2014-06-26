'use strict'

###*
 # @ngdoc service
 # @name adminApp.Mutualfundquote
 # @description
 # # Mutualfundquote
 # Service in the adminApp.
###
angular.module('adminApp').service 'MutualFundQuote', ($resource, apiUrl) ->
  $resource(apiUrl + '/reference/mutualFunds/:fundId/quotes/:id', {fundId:'@fundId', id:'@id'})
