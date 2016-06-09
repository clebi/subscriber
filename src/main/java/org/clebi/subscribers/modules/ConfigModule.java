// Copyright 2016 Clément Bizeau
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
