package org.clebi.subscribers;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.clebi.subscribers.controllers.SubscriberController;
import org.clebi.subscribers.modules.ConfigModule;
import org.clebi.subscribers.modules.DaoModule;
import org.clebi.subscribers.modules.FilterModule;
import org.clebi.subscribers.modules.WsModule;

public class App {

  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new DaoModule(), new WsModule(), new FilterModule(), new ConfigModule());
    final SubscriberController controller = injector.getInstance(SubscriberController.class);
  }
}
