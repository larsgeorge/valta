package com.larsgeorge.valta.hbase.handler.accounting;

import org.apache.hadoop.conf.Configuration;

/**
 * Imlements the default accounting handler.
 *
 * User: larsgeorge
 * Date: 3/18/12 12:39 PM
 */
public class DefaultAccountingHandler implements AccountingHandler {
  private Configuration _conf = null;

  public DefaultAccountingHandler(Configuration conf) {
    _conf = conf;
  }
}
