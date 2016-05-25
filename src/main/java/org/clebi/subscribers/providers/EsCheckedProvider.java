package org.clebi.subscribers.providers;

import com.google.inject.throwingproviders.CheckedProvider;

public interface EsCheckedProvider<T> extends CheckedProvider<T> {
    @Override
    T get() throws Exception;
}
