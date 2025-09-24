package com.mainlylabs.mainlylabs_backend.Exceptions;
import org.springframework.http.HttpStatus;

public class HttpException extends RuntimeException {
  private final HttpStatus status;

  public HttpException(HttpStatus status, String message) {
    super(message);
    this.status = status;
  }

  public HttpStatus getStatus() {
    return status;
  }

  @Override
  public String toString() {
    return "HttpException{" +
            "status=" + status +
            ", message='" + getMessage() + '\'' +
            '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof HttpException)) return false;
    HttpException that = (HttpException) o;
    return status == that.status && getMessage().equals(that.getMessage());
  }

  @Override
  public int hashCode() {
    int result = status.hashCode();
    result = 31 * result + getMessage().hashCode();
    return result;
  }
}

