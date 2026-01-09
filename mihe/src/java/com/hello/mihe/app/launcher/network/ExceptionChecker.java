package com.hello.mihe.app.launcher.network;

import okhttp3.Response;

public class ExceptionChecker {

  private static final String TAG = "ExceptionChecker";

  public ExceptionChecker() {}

  public static Exception check(Response response) {
    int code = response.code();
    if (code / 100 == 2) {
      return null;
    } else if (code == 400) {
      return new ApiExcep.Client.BadRequest(response);
    } else if (code == 401) {
      return new ApiExcep.Client.Unauthorized(response);
    } else if (code == 403) {
      return new ApiExcep.Client.Forbidden(response);
    } else if (code == 404) {
      return new ApiExcep.Client.NotFound(response);
    } else if (code == 405) {
      return new ApiExcep.Client.MethodNotAllowed(response);
    } else if (code == 409) {
      return new ApiExcep.Client.Conflict(response);
    } else if (code == 410) {
      return new ApiExcep.Client.Gone(response);
    } else if (code == 413) {
      return new ApiExcep.Client.RequestEntityTooLarge(response);
    } else if (code == 415) {
      return new ApiExcep.Client.UnsupportedMediaType(response);
    } else if (code == 418) {
      return new ApiExcep.Client.ImATeapot(response);
    } else if (code == 422) {
      return new ApiExcep.Client.UnprocessableEntity(response);
    } else if (code == 429) {
      String resetHeader = response.header("X-RateLimit-Reset");
      if (resetHeader != null) {
        int resetInSeconds = Math.min(Integer.parseInt(resetHeader), 10);
        return new ApiExcep.Client.TooManyRequests(response, resetInSeconds);
      } else {
        return new ApiExcep.Client.TooManyRequests(response, 5);
      }
    } else {
      return (Exception) (code / 100 == 5 ? new ApiExcep.Server(response) : new Exception("code: " + code));
    }
  }
}
