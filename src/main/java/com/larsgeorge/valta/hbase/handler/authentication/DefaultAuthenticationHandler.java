package com.larsgeorge.valta.hbase.handler.authentication;

import org.apache.hadoop.conf.Configuration;

/**
 * Implements the default authentication handler.
 *
 * User: larsgeorge
 * Date: 3/18/12 12:38 PM
 */
public class DefaultAuthenticationHandler implements AuthenticationHandler {
  private Configuration _conf = null;

  public DefaultAuthenticationHandler(Configuration conf) {
    _conf = conf;
  }
}
