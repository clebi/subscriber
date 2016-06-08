package org.clebi.subscribers.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.throwingproviders.CheckedProvides;
import com.google.inject.throwingproviders.ThrowingProviderBinder;

import org.clebi.subscribers.configuration.GlobalConfig;
import org.clebi.subscribers.configuration.YamlGlobalConfig;
import org.clebi.subscribers.modules.exceptions.ConfigurationException;
import org.clebi.subscribers.modules.providers.ConfigCheckedProvider;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;

public class ConfigModule extends AbstractModule {
  @Override
  protected void configure() {
    install(ThrowingProviderBinder.forModule(this));
  }

  @CheckedProvides(ConfigCheckedProvider.class)
  @Singleton
  private GlobalConfig globalConfig() throws ConfigurationException {
    String confPath = System.getProperty("global.config.path");
    if (confPath == null) {
      throw new ConfigurationException("missing config path");
    }
    try {
      YamlGlobalConfig config = (YamlGlobalConfig) new Yaml(
          new Constructor(YamlGlobalConfig.class)).load(new FileInputStream(new File(confPath))
      );
      return config;
    } catch (Exception exc) {
      throw new ConfigurationException("unable to read configuration file", exc);
    }
  }
}
