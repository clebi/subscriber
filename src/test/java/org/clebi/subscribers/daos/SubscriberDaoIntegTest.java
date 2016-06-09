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

  private static final String TEST_GET_USER_PROJECT = "test_integ_get_user_project";
  private static final String TEST_ADD_USER_PROJECT = "test_integ_add_user_project";
  private static final String TEST_SEARCH_OPTINS_PROJECT = "test_integ_search_optins_project";
  private static final String TEST_SEARCH_ACTIVES_PROJECT = "test_integ_search_actives_project";
  private static final String TEST_SEARCH_OPTINS_ACTIVES_PROJECT = "test_integ_optins_actives_project";
  private static final String TEST_LIST_ALL_PROJECT = "test_integ_list_all_project";
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
    indexSubsciber(TEST_GET_USER_PROJECT, subscriber);
    SubscriberDao dao = new SubscriberDaoImpl(SubscribersIntegTestHelper::getClient);
    Subscriber getSubscriber = dao.getSubscriber(TEST_GET_USER_PROJECT, subscriber.getEmail().toString());
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
    dao.addSubscriber(TEST_ADD_USER_PROJECT, subscriber);
    GetResponse resp = getClient()
        .prepareGet(TEST_ADD_USER_PROJECT, SubscriberDaoImpl.DOCUMENT_NAME, TEST_EMAIL)
        .get();
    Subscriber getSubscriber = JsonFactory.getGson().fromJson(
        resp.getSourceAsString(),
        Subscriber.class);
    assertSubscriber(subscriber, getSubscriber);
  }

  @Test
  public void testSearchOptins() throws Exception {
    final SubscriberDao dao = new SubscriberDaoImpl(SubscribersIntegTestHelper::getClient);
    final Map<String, Subscriber> subscribers = indexTestSubscibers(TEST_SEARCH_OPTINS_PROJECT);
    refreshIndices(TEST_SEARCH_OPTINS_PROJECT);
    List<SearchFilter> filters = new LinkedList<>();
    List<Object> values = new ArrayList<>();
    values.add(true);
    filters.add(new SearchFilter("optin", FilterOperand.EQUAL, values));
    final List<Subscriber> listSubscribers = dao.search(TEST_SEARCH_OPTINS_PROJECT, 5, 0, filters);
    Assert.assertEquals(2, listSubscribers.size());
    for (Subscriber subscriber : listSubscribers) {
      Subscriber expectedSubscriber = subscribers.get(subscriber.getEmail().toString());
      assertSubscriber(expectedSubscriber, subscriber);
    }
  }

  @Test
  public void testSearchActives() throws Exception {
    final SubscriberDao dao = new SubscriberDaoImpl(SubscribersIntegTestHelper::getClient);
    final Map<String, Subscriber> subscribers = indexTestSubscibers(TEST_SEARCH_ACTIVES_PROJECT);
    refreshIndices(TEST_SEARCH_ACTIVES_PROJECT);
    List<SearchFilter> filters = new LinkedList<>();
    List<Object> values = new ArrayList<>();
    values.add(true);
    filters.add(new SearchFilter("active", FilterOperand.EQUAL, values));
    final List<Subscriber> listSubscribers = dao.search(TEST_SEARCH_ACTIVES_PROJECT, 5, 0, filters);
    Assert.assertEquals(2, listSubscribers.size());
    for (Subscriber subscriber : listSubscribers) {
      Subscriber expectedSubscriber = subscribers.get(subscriber.getEmail().toString());
      assertSubscriber(expectedSubscriber, subscriber);
    }
  }

  @Test
  public void testSearchOptinsActives() throws Exception {
    final SubscriberDao dao = new SubscriberDaoImpl(SubscribersIntegTestHelper::getClient);
    final Map<String, Subscriber> subscribers = indexTestSubscibers(TEST_SEARCH_OPTINS_ACTIVES_PROJECT);
    refreshIndices(TEST_SEARCH_OPTINS_ACTIVES_PROJECT);
    List<SearchFilter> filters = new LinkedList<>();
    List<Object> values = new ArrayList<>();
    values.add(true);
    filters.add(new SearchFilter("active", FilterOperand.EQUAL, values));
    filters.add(new SearchFilter("optin", FilterOperand.EQUAL, values));
    final List<Subscriber> listSubscribers = dao.search(TEST_SEARCH_OPTINS_ACTIVES_PROJECT, 5, 0, filters);
    Assert.assertEquals(1, listSubscribers.size());
    for (Subscriber subscriber : listSubscribers) {
      Subscriber expectedSubscriber = subscribers.get(subscriber.getEmail().toString());
      assertSubscriber(expectedSubscriber, subscriber);
    }
  }

  @Test
  public void testListAll() throws Exception {
    final SubscriberDao dao = new SubscriberDaoImpl(SubscribersIntegTestHelper::getClient);
    final Map<String, Subscriber> subscribers = indexTestSubscibers(TEST_LIST_ALL_PROJECT);
    refreshIndices(TEST_LIST_ALL_PROJECT);
    final List<Subscriber> listSubscribers = dao.search(TEST_LIST_ALL_PROJECT, 5, 0, new LinkedList<>());
    Assert.assertEquals(subscribers.size(), listSubscribers.size());
    for (Subscriber subscriber : listSubscribers) {
      Subscriber expectedSubscriber = subscribers.get(subscriber.getEmail().toString());
      assertSubscriber(expectedSubscriber, subscriber);
    }
  }
}
