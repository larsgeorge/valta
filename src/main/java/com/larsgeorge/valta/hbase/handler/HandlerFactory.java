package com.larsgeorge.valta.hbase.handler;

import com.larsgeorge.valta.hbase.Constants;
import com.larsgeorge.valta.hbase.handler.accounting.AccountingHandler;
import com.larsgeorge.valta.hbase.handler.authentication.AuthenticationHandler;
import com.larsgeorge.valta.hbase.handler.authorizaton.AuthorizationHandler;
import org.apache.hadoop.conf.Configuration;

/**
 * Provides access to all handler implementations.
 *
 * User: larsgeorge
 * Date: 3/18/12 12:33 PM
 */
public abstract class HandlerFactory {
  protected Configuration _conf = null;

  public void init(Configuration conf) {
    _conf = conf;
  }

  public abstract AuthenticationHandler getAuthenticationHandler();
  public abstract AuthorizationHandler getAuthorizationHandler();
  public abstract AccountingHandler getAccountingHandler();

  public static synchronized HandlerFactory GetHandlerFactory(Configuration conf) {
    String clazzName = conf.get(Constants.FACTORY_CLASS, Constants.DEFAULT_FACTORY_CLASS);
    HandlerFactory factory;
    try {
      Class clazz = Class.forName(clazzName);
      factory = (HandlerFactory) clazz.newInstance();
      factory.init(conf);
    } catch (Exception e) {
      throw new UnsupportedOperationException("Can not find handler class: " + clazzName, e);
    }
    return factory;
  }
}
