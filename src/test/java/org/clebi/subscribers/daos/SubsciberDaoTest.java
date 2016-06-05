package org.clebi.subscribers.daos;

import org.clebi.subscribers.daos.elasticsearch.SubscriberDaoImpl;
import org.clebi.subscribers.daos.exceptions.ValidationException;
import org.clebi.subscribers.model.Email;
import org.clebi.subscribers.model.Subscriber;
import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;

public class SubsciberDaoTest {

  @Test(expected = ValidationException.class)
  public void testAddNotValidSubscriber() throws Exception {
    SubscriberDao dao = new SubscriberDaoImpl(() -> null);
    dao.addSubscriber(
        new Subscriber(
            true,
            true,
            new Email("bad_email"),
            ZonedDateTime.now(ZoneOffset.UTC),
            new HashMap<String, Object>()
        )
    );
  }
}
