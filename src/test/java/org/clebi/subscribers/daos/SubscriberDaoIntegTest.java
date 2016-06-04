package org.clebi.subscribers.daos;

import org.clebi.subscribers.daos.elasticsearch.SubscriberDaoImpl;
import org.clebi.subscribers.model.Email;
import org.clebi.subscribers.model.Subscriber;
import org.clebi.subscribers.model.serialize.JsonFactory;
import org.elasticsearch.action.get.GetResponse;
import org.junit.Assert;
import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class SubscriberDaoIntegTest extends SubscribersIntegTestHelper {

  private static final String TEST_EMAIL = "test@test.com";

  @Test
  public void testGetUser() throws Exception {
    Map<String, Object> fields = generateTestFields();
    Subscriber subscriber = new Subscriber(
        true,
        new Email(TEST_EMAIL),
        ZonedDateTime.now(ZoneOffset.UTC),
        fields);
    indexSubsciber(subscriber);
    refreshIndices();
    SubscriberDao dao = new SubscriberDaoImpl(SubscribersIntegTestHelper::getClient);
    Subscriber getSubscriber = dao.getSubscriber(subscriber.getEmail().toString());
    assertSubscriber(subscriber, getSubscriber);
  }

  @Test
  public void testAddUser() throws Exception {
    Map<String, Object> fields = generateTestFields();
    Subscriber subscriber = new Subscriber(
        true,
        new Email(TEST_EMAIL),
        ZonedDateTime.now(ZoneOffset.UTC),
        fields);
    SubscriberDao dao = new SubscriberDaoImpl(SubscribersIntegTestHelper::getClient);
    dao.addSubscriber(subscriber);
    refreshIndices();
    GetResponse resp = getClient()
        .prepareGet(SubscriberDaoImpl.INDEX_NAME, SubscriberDaoImpl.DOCUMENT_NAME, TEST_EMAIL)
        .get();
    Subscriber getSubscriber = JsonFactory.getGson().fromJson(
        resp.getSourceAsString(),
        Subscriber.class);
    assertSubscriber(subscriber, getSubscriber);
  }

  @Test
  public void testListOptins() throws Exception {
    final SubscriberDao dao = new SubscriberDaoImpl(SubscribersIntegTestHelper::getClient);
    final Map<String, Subscriber> subscribers = indexTestSubscibers();
    refreshIndices();
    final List<Subscriber> listSubscribers = dao.listOptins(5, 0);
    Assert.assertEquals(2, listSubscribers.size());
    for (Subscriber subscriber : listSubscribers) {
      Subscriber expectedSubscriber = subscribers.get(subscriber.getEmail().toString());
      assertSubscriber(expectedSubscriber, subscriber);
    }
  }

  @Test
  public void testList() throws Exception {
    final SubscriberDao dao = new SubscriberDaoImpl(SubscribersIntegTestHelper::getClient);
    final Map<String, Subscriber> subscribers = indexTestSubscibers();
    refreshIndices();
    final List<Subscriber> listSubscribers = dao.list(5, 0);
    Assert.assertEquals(subscribers.size(), listSubscribers.size());
    for (Subscriber subscriber : listSubscribers) {
      Subscriber expectedSubscriber = subscribers.get(subscriber.getEmail().toString());
      assertSubscriber(expectedSubscriber, subscriber);
    }
  }
}
