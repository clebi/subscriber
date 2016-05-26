package org.clebi.subscribers.providers;

import com.google.inject.throwingproviders.CheckedProvider;

import org.clebi.subscribers.modules.exceptions.ConfigurationException;

import java.io.FileNotFoundException;
import java.net.UnknownHostException;

public interface EsCheckedProvider<T> extends CheckedProvider<T> {
  @Override
  T get() throws UnknownHostException, ConfigurationException, FileNotFoundException;
}
