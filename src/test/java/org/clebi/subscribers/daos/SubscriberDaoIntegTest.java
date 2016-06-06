package org.clebi.subscribers.daos;

import org.clebi.subscribers.daos.elasticsearch.SubscriberDaoImpl;
import org.clebi.subscribers.model.Email;
import org.clebi.subscribers.model.FilterOperand;
import org.clebi.subscribers.model.SearchFilter;
import org.clebi.subscribers.model.Subscriber;
import org.clebi.subscribers.model.serialize.JsonFactory;
import org.elasticsearch.action.get.GetResponse;
import org.junit.Assert;
import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SubscriberDaoIntegTest extends SubscribersIntegTestHelper {

  private static final String TEST_EMAIL = "test@test.com";

  @Test
  public void testGetUser() throws Exception {
    Map<String, Object> fields = generateTestFields();
    Subscriber subscriber = new Subscriber(
        true,
        true,
        new Email(TEST_EMAIL),
        ZonedDateTime.now(ZoneOffset.UTC),
        fields);
    indexSubsciber(subscriber);
    SubscriberDao dao = new SubscriberDaoImpl(SubscribersIntegTestHelper::getClient);
    Subscriber getSubscriber = dao.getSubscriber(subscriber.getEmail().toString());
    assertSubscriber(subscriber, getSubscriber);
  }

  @Test
  public void testAddUser() throws Exception {
    Map<String, Object> fields = generateTestFields();
    Subscriber subscriber = new Subscriber(
        true,
        true,
        new Email(TEST_EMAIL),
        ZonedDateTime.now(ZoneOffset.UTC),
        fields);
    SubscriberDao dao = new SubscriberDaoImpl(SubscribersIntegTestHelper::getClient);
    dao.addSubscriber(subscriber);
    GetResponse resp = getClient()
        .prepareGet(SubscriberDaoImpl.INDEX_NAME, SubscriberDaoImpl.DOCUMENT_NAME, TEST_EMAIL)
        .get();
    Subscriber getSubscriber = JsonFactory.getGson().fromJson(
        resp.getSourceAsString(),
        Subscriber.class);
    assertSubscriber(subscriber, getSubscriber);
  }

  @Test
  public void testSearchOptins() throws Exception {
    final SubscriberDao dao = new SubscriberDaoImpl(SubscribersIntegTestHelper::getClient);
    final Map<String, Subscriber> subscribers = indexTestSubscibers();
    refreshIndices();
    List<SearchFilter> filters = new LinkedList<>();
    List<Object> values = new ArrayList<>();
    values.add(true);
    filters.add(new SearchFilter("optin", FilterOperand.EQUAL, values));
    final List<Subscriber> listSubscribers = dao.search(5, 0, filters);
    Assert.assertEquals(2, listSubscribers.size());
    for (Subscriber subscriber : listSubscribers) {
      Subscriber expectedSubscriber = subscribers.get(subscriber.getEmail().toString());
      assertSubscriber(expectedSubscriber, subscriber);
    }
  }

  @Test
  public void testSearchActives() throws Exception {
    final SubscriberDao dao = new SubscriberDaoImpl(SubscribersIntegTestHelper::getClient);
    final Map<String, Subscriber> subscribers = indexTestSubscibers();
    refreshIndices();
    List<SearchFilter> filters = new LinkedList<>();
    List<Object> values = new ArrayList<>();
    values.add(true);
    filters.add(new SearchFilter("active", FilterOperand.EQUAL, values));
    final List<Subscriber> listSubscribers = dao.search(5, 0, filters);
    Assert.assertEquals(2, listSubscribers.size());
    for (Subscriber subscriber : listSubscribers) {
      Subscriber expectedSubscriber = subscribers.get(subscriber.getEmail().toString());
      assertSubscriber(expectedSubscriber, subscriber);
    }
  }

  @Test
  public void testSearchOptinsActives() throws Exception {
    final SubscriberDao dao = new SubscriberDaoImpl(SubscribersIntegTestHelper::getClient);
    final Map<String, Subscriber> subscribers = indexTestSubscibers();
    refreshIndices();
    List<SearchFilter> filters = new LinkedList<>();
    List<Object> values = new ArrayList<>();
    values.add(true);
    filters.add(new SearchFilter("active", FilterOperand.EQUAL, values));
    filters.add(new SearchFilter("optin", FilterOperand.EQUAL, values));
    final List<Subscriber> listSubscribers = dao.search(5, 0, filters);
    Assert.assertEquals(1, listSubscribers.size());
    for (Subscriber subscriber : listSubscribers) {
      Subscriber expectedSubscriber = subscribers.get(subscriber.getEmail().toString());
      assertSubscriber(expectedSubscriber, subscriber);
    }
  }

  @Test
  public void testListAll() throws Exception {
    final SubscriberDao dao = new SubscriberDaoImpl(SubscribersIntegTestHelper::getClient);
    final Map<String, Subscriber> subscribers = indexTestSubscibers();
    refreshIndices();
    final List<Subscriber> listSubscribers = dao.search(5, 0, new LinkedList<>());
    Assert.assertEquals(subscribers.size(), listSubscribers.size());
    for (Subscriber subscriber : listSubscribers) {
      Subscriber expectedSubscriber = subscribers.get(subscriber.getEmail().toString());
      assertSubscriber(expectedSubscriber, subscriber);
    }
  }
}
