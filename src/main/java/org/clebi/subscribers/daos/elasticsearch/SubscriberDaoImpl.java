package org.clebi.subscribers.daos.elasticsearch;

import com.google.gson.Gson;
import com.google.inject.Inject;

import org.clebi.subscribers.daos.SubscriberDao;
import org.clebi.subscribers.daos.exceptions.NotFoundException;
import org.clebi.subscribers.daos.exceptions.ValidationException;
import org.clebi.subscribers.model.SearchFilter;
import org.clebi.subscribers.model.Subscriber;
import org.clebi.subscribers.model.serialize.JsonFactory;
import org.clebi.subscribers.providers.EsCheckedProvider;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.util.LinkedList;
import java.util.List;

public class SubscriberDaoImpl implements SubscriberDao {

  public static final String INDEX_NAME = "subscribers";
  public static final String DOCUMENT_NAME = "subscriber";

  private static final String ERROR_GET_NOT_FOUND = "unable to find subscriber with email: %s";

  private final Client client;
  private final Gson gson = JsonFactory.getGson();

  @Inject
  public SubscriberDaoImpl(EsCheckedProvider<Client> client) throws Exception {
    this.client = client.get();
  }

  @Override
  public void addSubscriber(Subscriber subscriber) throws ValidationException {
    if (!subscriber.isValid()) {
      throw new ValidationException("unable to validate subscriber model");
    }
    System.out.println(gson.toJson(subscriber));
    client.prepareIndex(INDEX_NAME, DOCUMENT_NAME)
        .setSource(gson.toJson(subscriber))
        .setId(subscriber.getEmail().toString())
        .get();
  }

  @Override
  public Subscriber getSubscriber(String email) throws NotFoundException {
    GetResponse resp = client.prepareGet("subscribers", "subscriber", email).get();
    Subscriber subscriber = gson.fromJson(resp.getSourceAsString(), Subscriber.class);
    if (subscriber == null || !subscriber.isValid()) {
      throw new NotFoundException(String.format(ERROR_GET_NOT_FOUND, email));
    }
    return subscriber;
  }

  @Override
  public List<Subscriber> search(int size, int offset, List<SearchFilter> filters) {
    BoolQueryBuilder builder = new BoolQueryBuilder();
    for (SearchFilter filter : filters) {
      System.out.println(filter);
      builder.must(QueryBuilders.termQuery(filter.getFieldName(), filter.getValues().get(0)));
    }
    SearchResponse resp = client.prepareSearch(INDEX_NAME)
        .setTypes(DOCUMENT_NAME)
        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
        .setQuery(builder)
        .execute()
        .actionGet();
    return parseSearchResponse(resp);
  }

  private List<Subscriber> parseSearchResponse(SearchResponse response) {
    List<Subscriber> subscribers = new LinkedList<>();
    for (SearchHit hit : response.getHits()) {
      subscribers.add(gson.fromJson(hit.getSourceAsString(), Subscriber.class));
    }
    return subscribers;
  }
}
