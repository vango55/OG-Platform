/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.util.rest;

import javax.ws.rs.ext.Provider;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;

/**
 * An extension to {@code UniformInterfaceException} to represent a 404.
 * <p>
 * This allows catch clauses to be simpler.
 */
@Provider
public class UniformInterfaceException404NotFound extends UniformInterfaceException {

  /** Serialization version. */
  private static final long serialVersionUID = -8266318713789190845L;

  public UniformInterfaceException404NotFound(ClientResponse response, boolean bufferResponseEntity) {
    super(response, bufferResponseEntity);
  }

  public UniformInterfaceException404NotFound(ClientResponse response) {
    super(response);
  }

  public UniformInterfaceException404NotFound(String message, ClientResponse response, boolean bufferResponseEntity) {
    super(message, response, bufferResponseEntity);
  }

  public UniformInterfaceException404NotFound(String message, ClientResponse response) {
    super(message, response);
  }

}
