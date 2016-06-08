package org.clebi.subscribers.modules;

import com.google.inject.AbstractModule;

import org.clebi.subscribers.authorization.AuthorizationFilter;
import org.clebi.subscribers.modules.annotations.OauthFilter;
import spark.Filter;

public class FilterModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(Filter.class).annotatedWith(OauthFilter.class)
        .to(AuthorizationFilter.class);
  }
}
