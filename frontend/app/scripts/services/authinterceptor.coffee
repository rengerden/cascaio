'use strict'

###*
 # @ngdoc service
 # @name frontendApp.AuthInterceptor
 # @description
 # # AuthInterceptor
 # Factory in the frontendApp.
###
angular.module('frontendApp').factory 'AuthInterceptor', ($q, toaster, config, Auth) ->
  {
  request: (request) ->
    if request.url.indexOf(config.apihost) == -1
      return request

    addBearer = ->
      Auth.updateToken(5)
      .success(->
        request.headers.Authorization = 'Bearer ' + Auth.token();
        deferred.notify()
        deferred.resolve(request)
      )

    deferred = $q.defer()
    if (Auth.isAuthenticated())
      addBearer()
    else
      Auth.onAuthSuccess = -> addBearer()

    return $q.when(deferred.promise)

  responseError: (rejection) ->
    if (rejection.status == 401)
      toaster.pop('error', 'Logged out', 'Your session has expired. Please, login again.')
      Auth.logout()

    return $q.reject(rejection)
  }


angular.module('frontendApp').config ($httpProvider) -> $httpProvider.interceptors.push('AuthInterceptor')
