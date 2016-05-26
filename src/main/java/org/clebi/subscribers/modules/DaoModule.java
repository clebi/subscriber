package org.clebi.subscribers.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.throwingproviders.CheckedProvides;
import com.google.inject.throwingproviders.ThrowingProviderBinder;

import org.clebi.subscribers.daos.SubscriberDao;
import org.clebi.subscribers.daos.elasticsearch.SubscriberDaoImpl;
import org.clebi.subscribers.model.config.EsConfiguration;
import org.clebi.subscribers.modules.exceptions.ConfigurationException;
import org.clebi.subscribers.providers.EsCheckedProvider;
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
