package org.clebi.subscribers.daos;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.clebi.subscribers.daos.elasticsearch.SubscriberDaoImpl;
import org.clebi.subscribers.model.Email;
import org.clebi.subscribers.model.Subscriber;
import org.clebi.subscribers.model.serialize.JsonFactory;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.File;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SubscribersIntegTestHelper {

  private static final String ES_DATA_PATH = "build/esdata";
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

  protected static Client getClient() {
    return node.client();
  }

  protected Map<String, Subscriber> indexTestSubscibers() {
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
    return subscribers;
  }

  protected static void refreshIndices() {
    node.client().admin().indices()
        .refresh(new RefreshRequest(SubscriberDaoImpl.INDEX_NAME))
        .actionGet();
  }

  protected void indexSubsciber(Subscriber subscriber) {
    node.client()
        .prepareIndex(SubscriberDaoImpl.INDEX_NAME, SubscriberDaoImpl.DOCUMENT_NAME)
        .setId(subscriber.getEmail().toString())
        .setSource(JsonFactory.getGson().toJson(subscriber))
        .get();
  }

  protected Map<String, Object> generateTestFields() {
    Map<String, Object> fields = new HashMap<>();
    fields.put("field1", "test");
    fields.put("field2", 12.0);
    return fields;
  }

  protected void assertSubscriber(
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
