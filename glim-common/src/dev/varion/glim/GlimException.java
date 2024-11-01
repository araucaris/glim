package dev.varion.glim;

public class GlimException extends RuntimeException {
  public GlimException(final String message) {
    super(message);
  }

  public GlimException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
