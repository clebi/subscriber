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
