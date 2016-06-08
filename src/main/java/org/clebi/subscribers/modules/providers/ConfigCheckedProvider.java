package org.clebi.subscribers.modules.providers;

import com.google.inject.throwingproviders.CheckedProvider;

import org.clebi.subscribers.modules.exceptions.ConfigurationException;

import java.io.FileNotFoundException;
import java.net.UnknownHostException;

public interface ConfigCheckedProvider<T> extends CheckedProvider<T> {
  @Override
  T get() throws ConfigurationException;
}
