package com.larsgeorge.valta.hbase.handler.authorizaton;

import org.apache.hadoop.conf.Configuration;

/**
 * Implements the default authorization handler.
 *
 * User: larsgeorge
 * Date: 3/18/12 12:39 PM
 */
public class DefaultAuthorizationHandler implements AuthorizationHandler {
  private Configuration _conf = null;

  public DefaultAuthorizationHandler(Configuration conf) {
    _conf = conf;
  }
}
