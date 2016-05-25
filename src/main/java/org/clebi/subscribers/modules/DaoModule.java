package org.clebi.subscribers.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.throwingproviders.CheckedProvides;
import com.google.inject.throwingproviders.ThrowingProviderBinder;
import org.clebi.subscribers.daos.SubscriberDao;
import org.clebi.subscribers.daos.elasticsearch.SubscriberDaoImpl;
import org.clebi.subscribers.providers.EsCheckedProvider;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

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
    Client esProvider() throws UnknownHostException {
        Settings settings = Settings.settingsBuilder()
                .put("cluster.name", "mystash").build();
        return TransportClient.builder().settings(settings).build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.99.100"), 9300));
    }

}
