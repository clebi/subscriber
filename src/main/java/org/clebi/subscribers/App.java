package org.clebi.subscribers;

import com.google.inject.Guice;
import com.google.inject.Injector;

import org.clebi.subscribers.controllers.SubscriberController;
import org.clebi.subscribers.modules.DaoModule;

public class App {

  public static void main(String[] args) {
    Injector injector = Guice.createInjector(new DaoModule());
    final SubscriberController controller = injector.getInstance(SubscriberController.class);
  }
}
