package org.clebi.subscribers.daos;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.clebi.subscribers.daos.elasticsearch.SubscriberDaoImpl;
import org.clebi.subscribers.model.Email;
import org.clebi.subscribers.model.Subscriber;
import org.clebi.subscribers.model.serialize.JsonFactory;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubscriberDaoIntegTest {

  private static final String ES_DATA_PATH = "build/esdata";
  private static final String TEST_EMAIL = "test@test.com";
  private static final String EXCLUDE_FROM_EQUALS = "fields";

  private static Settings settings = Settings.settingsBuilder().put("path.home", ES_DATA_PATH).build();
  private static Node node;

  @BeforeClass
  public static void setUp() throws IOException {
    FileUtils.deleteDirectory(new File(ES_DATA_PATH));
    node = NodeBuilder.nodeBuilder().settings(settings).local(true).node();
  }

  /**
   * Initialize elasticsearch index for tests.
   */
  @Before
  public void before() {
    if (node.client().admin().indices().prepareExists().setIndices(SubscriberDaoImpl.INDEX_NAME)
        .get()
        .isExists()) {
      DeleteIndexRequest indexRequest = new DeleteIndexRequest(SubscriberDaoImpl.INDEX_NAME);
      node.client().admin().indices().delete(indexRequest).actionGet();
    }
  }

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
    SubscriberDao dao = new SubscriberDaoImpl(() -> node.client());
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
    SubscriberDao dao = new SubscriberDaoImpl(() -> node.client());
    dao.addSubscriber(subscriber);
    refreshIndices();
    GetResponse resp = node.client()
        .prepareGet(SubscriberDaoImpl.INDEX_NAME, SubscriberDaoImpl.DOCUMENT_NAME, TEST_EMAIL)
        .get();
    Subscriber getSubscriber = JsonFactory.getGson().fromJson(
        resp.getSourceAsString(),
        Subscriber.class);
    assertSubscriber(subscriber, getSubscriber);
  }

  @Test
  public void testListOptins() throws Exception {
    final Map<String, Subscriber> subscribers = new HashMap<>();
    final String emailOptin1 = "test_optin_1@test.com";
    final String emailOptin2 = "test_optin_2@test.com";
    final String emailNonOptin = "test_non_optin_1@test.com";
    subscribers.put(
        emailOptin1,
        new Subscriber(
            true,
            new Email(emailOptin1),
            ZonedDateTime.now(ZoneOffset.UTC),
            generateTestFields()));
    subscribers.put(
        emailOptin2,
        new Subscriber(
            true,
            new Email(emailOptin2),
            ZonedDateTime.now(ZoneOffset.UTC),
            generateTestFields()));
    subscribers.put(
        emailNonOptin,
        new Subscriber(
            false,
            new Email(emailNonOptin),
            ZonedDateTime.now(ZoneOffset.UTC),
            generateTestFields()));
    for (Map.Entry<String, Subscriber> entry : subscribers.entrySet()) {
      indexSubsciber(entry.getValue());
    }
    final SubscriberDao dao = new SubscriberDaoImpl(() -> node.client());
    refreshIndices();
    final List<Subscriber> listSubscribers = dao.listOptins(5, 0);
    Assert.assertEquals(2, listSubscribers.size());
    for (Subscriber subscriber : listSubscribers) {
      Subscriber expectedSubscriber = subscribers.get(subscriber.getEmail().toString());
      assertSubscriber(expectedSubscriber, subscriber);
    }
  }

  private void refreshIndices() {
    node.client().admin().indices()
        .refresh(new RefreshRequest(SubscriberDaoImpl.INDEX_NAME))
        .actionGet();
  }

  private void indexSubsciber(Subscriber subscriber) {
    node.client()
        .prepareIndex(SubscriberDaoImpl.INDEX_NAME, SubscriberDaoImpl.DOCUMENT_NAME)
        .setId(subscriber.getEmail().toString())
        .setSource(JsonFactory.getGson().toJson(subscriber))
        .get();
  }

  private Map<String, Object> generateTestFields() {
    Map<String, Object> fields = new HashMap<>();
    fields.put("field1", "test");
    fields.put("field2", 12.0);
    return fields;
  }

  private void assertSubscriber(
      Subscriber expected,
      Subscriber actual) {
    Collection<String> excludes = new ArrayList<String>();
    excludes.add(EXCLUDE_FROM_EQUALS);
    Assert.assertTrue(EqualsBuilder.reflectionEquals(expected, actual, excludes));
    Assert.assertEquals(expected.getFields().size(), actual.getFields().size());
    for (Map.Entry<String, Object> entry : expected.getFields().entrySet()) {
      Assert.assertEquals(entry.getValue(), actual.getFields().get(entry.getKey()));
    }
  }

}
