package org.clebi.subscribers.daos;

import org.clebi.subscribers.daos.exceptions.ValidationException;
import org.clebi.subscribers.model.Subscriber;

public interface SubscriberDao {

    void addSubscriber(Subscriber subscriber) throws ValidationException;

}
