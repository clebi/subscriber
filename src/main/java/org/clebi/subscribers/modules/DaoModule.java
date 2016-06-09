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

import org.clebi.subscribers.daos.SubscriberDao;
import org.clebi.subscribers.daos.elasticsearch.SubscriberDaoImpl;
import org.clebi.subscribers.model.config.EsConfiguration;
import org.clebi.subscribers.modules.exceptions.ConfigurationException;
import org.clebi.subscribers.modules.providers.EsCheckedProvider;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DaoModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(SubscriberDao.class).to(SubscriberDaoImpl.class);
    install(ThrowingProviderBinder.forModule(this));
  }

  @CheckedProvides(EsCheckedProvider.class)
  @Singleton
  Client esProvider() throws UnknownHostException, ConfigurationException, FileNotFoundException {
    String confPath = System.getProperty("es.config_path");
    if (confPath == null) {
      throw new ConfigurationException("missing config path");
    }
    EsConfiguration config = (EsConfiguration) new Yaml(
        new Constructor(EsConfiguration.class)).load(new FileInputStream(new File(confPath))
    );
    System.out.println(config);
    Settings settings = Settings.settingsBuilder()
        .put("cluster.name", config.getClusterName()).build();
    return TransportClient.builder().settings(settings).build().addTransportAddress(
        new InetSocketTransportAddress(InetAddress.getByName(config.getHost()), config.getPort())
    );
  }

}
