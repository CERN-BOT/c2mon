package cern.c2mon.server.eslog.logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.LocalTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cern.c2mon.server.eslog.structure.mappings.Mapping;
import cern.c2mon.server.eslog.structure.queries.Query;
import cern.c2mon.server.eslog.structure.queries.QueryAliases;
import cern.c2mon.server.eslog.structure.queries.QueryIndexBuilder;
import cern.c2mon.server.eslog.structure.queries.QueryIndices;
import cern.c2mon.server.eslog.structure.queries.QueryTypes;
import cern.c2mon.server.eslog.structure.types.TagES;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Allows to connect to the cluster via a transport client. Handles all the
 * queries with a bulkProcessor to the ElasticSearch cluster. This is very light
 * for the cluster to be connected this way.
 * 
 * @author Alban Marguet.
 */
@Service
@Slf4j
@Data
public class TransportConnector implements Connector {
  /** Prefix used for every index in the ElasticSearch cluster, e.g., c2mon_2015-11 is a valid index. */
  private final String INDEX_PREFIX = "c2mon_";
  /** Every tag or alias must begin with the same prefix, e.g., tag_string is a good type and tag_207807 is a good alias. */
  private final String TAG_PREFIX = "tag_";
  /** The first index in the cluster is c2mon_1970-01 which corresponds to the Epoch time (ES stocks timestamps in milliseconds since Epoch). */
  private final String FIRST_INDEX = INDEX_PREFIX + "1970-01";

  /** Only used, if elasticsearch is started inside this JVM */
  private final int LOCAL_PORT = 1;
  /** Default port for elastic search transport node */
  public final static int DEFAULT_ES_PORT = 9300;

  private final HashMap<String, Integer> bulkSettings = new HashMap<>();
  private final Set<String> indices = new HashSet<>();
  private final Set<String> types = new HashSet<>();
  private final Set<String> aliases = new HashSet<>();

  /** The Client communicates with the Node inside the ElasticSearch cluster.*/
  private Client client;

  /** Port to which to connect when using a client that is not local. By default 9300 should be used. */
  @Value("${es.port:9300}")
  private int port;

  /** Name of the host holding the ElasticSearch cluster. */
  @Value("${es.host:localhost}")
  private String host;

  /** Name of the cluster. Must be set in order to connect to the right one in case there are several clusters running at the host. */
  @Value("${es.cluster:c2mon}")
  private String cluster;

  /** Name of the node in the cluster (more useful for debugging and to know which one is connected to the cluster). */
  @Value("${es.node:c2mon-indexing-transport-node}")
  private String node;

  /** Setting this to true will make the connector connect to a cluster inside the JVM. To false to a real cluster. */
  @Value("${es.local:true}")
  private boolean isLocal;

  /** Connection settings for the node according to the host, port, cluster, node and isLocal. */
  private Settings settings;

  /** Allows to send the data by batch. */
  private BulkProcessor bulkProcessor;

  /** Name of the BulkProcessor (more for debugging). */
  private final String bulkProcessorName = "ES-BulkProcessor";


  /*****************************************************************************
   * 
   * INITIALIZATION
   * 
   ****************************************************************************/


  /**
   * Instantiate the Client to communicate with the ElasticSearch cluster. If it
   * is well instantiated, retrieve the indices and and create a bulkProcessor
   * for batch writes.
   */
  @PostConstruct
  public void init() {
    
    log.info("init() - Connecting to ElasticSearch cluster " + cluster + " on host=" + host + ", port=" + port + ".");
    
    
    if (System.getProperty("es.host") == null && host.equalsIgnoreCase("localhost") && port == DEFAULT_ES_PORT) {
      //TODO: launch a local cluster.
      log.debug("init() - Connecting to local ElasticSearch instance (inside same JVM) is enabled.");
    }
    else if (System.getProperty("es.host") != null || !(port == DEFAULT_ES_PORT)) {
      setLocal(false);
      log.debug("init() - Connecting to local ElasticSearch instance (inside same JVM) is disabled.");
    }
    
    this.client = createClient();

    if (initTestPass()) {
      log.debug("init() - initial test passed: Transport client is connected to the cluster " + cluster + ".");
      updateLists();
      initBulkSettings();

      log.info("init() - Everything is initialized.");
      log.info("init() - Connected to cluster " + cluster + " with node " + node + ".");

      log.info("Indices in the cluster:");
      for (String index : indices) {
        log.debug(index);
      }

      log.info("Types in the cluster:");
      for (String type : types) {
        log.debug(type);
      }

      log.info("Aliases in the cluster:");
      for (String alias : aliases) {
        log.debug(alias);
      }

    }
    else {
      log.error("init() - Cluster is not initialized: cluster:" + cluster + ", host: " + host + ", port: " + port + ".");
    }
  }

  /**
   * Instantiate a BulkProcessor for batch writes. Settings are in the Enum
   * BulkSettings.
   */
  public void initBulkSettings() {
    bulkSettings.put("bulkActions", BulkSettings.BULK_ACTIONS.getSetting());
    bulkSettings.put("bulkSize", BulkSettings.BULK_SIZE.getSetting());
    bulkSettings.put("flushInterval", BulkSettings.FLUSH_INTERVAL.getSetting());
    bulkSettings.put("concurrent", BulkSettings.CONCURRENT.getSetting());

    this.bulkProcessor = BulkProcessor.builder(client, new BulkProcessor.Listener() {
      public void beforeBulk(long executionId, BulkRequest request) {
        log.info("Going to execute new bulk composed of {} actions", request.numberOfActions());
      }

      public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
        log.info("Executed bulk composed of {} actions", request.numberOfActions());
      }

      public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
        log.warn("Error executing bulk", failure);
      }
    })
        .setName(bulkProcessorName)
        .setBulkActions(bulkSettings.get("bulkActions"))
        .setBulkSize(new ByteSizeValue(bulkSettings.get("bulkSize"), ByteSizeUnit.GB))
        .setFlushInterval(TimeValue.timeValueSeconds(bulkSettings.get("flushInterval")))
        .setConcurrentRequests(bulkSettings.get("concurrent"))
        .build();

    log.debug("BulkProcessor created.");
  }

  /**
   * Need a transportClient to communicate with the ElasticSearch cluster.
   * @return Client to communicate with the ElasticSearch cluster.
   */
  public Client createClient() {
    if (isLocal) {
      this.settings = Settings.settingsBuilder().put("node.local", isLocal).put("node.name", node).put("cluster.name", cluster).build();

      setPort(LOCAL_PORT);

      Client builder = TransportClient.builder().settings(settings).build()
          .addTransportAddress(new LocalTransportAddress(String.valueOf(port)));

      log.info("Created local client on host " + host + " and port " + port + " with name " + node + ", in cluster " + cluster + ".");
      return builder;

    }
    else {

      this.settings = Settings.settingsBuilder().put("cluster.name", cluster).put("node.name", node).build();

      setPort(port);

      try {
        Client builder = TransportClient.builder().settings(settings).build()
            .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port));

        log.debug("Created client on host " + host + " and port " + port + " with name " + node + ", in cluster " + cluster + ".");
        return builder;

      }
      catch (UnknownHostException e) {
        log.error("createTransportClient() - Error whilst connecting to the ElasticSearch cluster (host=" + host + ", port=" + port + ").", e);
      }
    }

    return null;
  }

  /**
   * Method that query the cluster to list all the present indices. Allows to
   * check if the cluster is well initialized.
   * 
   * @return if the query has been acked or not.
   */
  public boolean initTestPass() {
    if (client != null) {
      client.admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
      return new QueryIndices(client).initTest();

    }
    else {
      log.warn("initTestPass() - client for the ElasticSearch cluster seems to have null value.");
      return false;
    }
  }

  /*****************************************************************************
   * 
   * INDEXING
   * 
   ****************************************************************************/

  /**
   * Add 1 TagES to index to the ElasticSearch cluster thanks to the
   * BulkProcessor.
   * 
   * @param tag to index.
   */
  public void indexTag(TagES tag) {
    String tagJson = tag.build();
    String indexMonth = generateIndex(tag.getTagServerTime());
    String type = generateType(tag.getDataType());
    log.debug("Index a new tag.");
    log.debug("Index = " + indexMonth);
    log.debug("Type = " + type);

    bulkAdd(indexMonth, type, tagJson, tag);
  }

  /**
   * Index several tags in the ElasticSearch cluster according to the
   * BulkProcessor parameters.
   * 
   * @param tags to index.
   */
  @Override
  public void indexTags(List<TagES> tags) {
    Map<String, TagES> aliases = new HashMap<>();
    if (tags == null) {
      log.debug("indexTags() - received a null List of tags to log to ElasticSearch.");

    }
    else {

      for (TagES tag : tags) {
        indexTag(tag);
        // 1 by 1 = long running
        aliases.put(generateAliasName(tag.getTagId()), tag);
      }

      // FLUSH
      log.debug("Close bulk.");
      closeBulk();

      for (String alias : aliases.keySet()) {
        bulkAddAlias(generateIndex(aliases.get(alias).getTagServerTime()), aliases.get(alias));
      }

      /** For quasi real time retrieval. */
      client.admin().indices().prepareRefresh().execute().actionGet();
      client.admin().cluster().prepareHealth().setWaitForYellowStatus().execute().actionGet();
    }
  }


  /*****************************************************************************
   * 
   * UTILITY FOR INDEXING
   * 
   ****************************************************************************/


  /**
   * Handles a query for the ElasticSearch cluster. This method only handles the
   * queries for listing admin values (indices, types and aliases)
   * 
   * @param query type of query.
   * @return List of retrieved responses according to the query.
   */
  @Override
  public List<String> handleListingQuery(Query query) {
    List<String> queryResponse = new ArrayList<>();

    if (client == null) {
      log.error("handleQuery() - Error: the client value is " + client + ".");
      return null;
    }

    if (query instanceof QueryIndices) {
      log.info("Handling queryIndices.");
      queryResponse.addAll(((QueryIndices) query).getListOfAnswer());
    }
    else if (query instanceof QueryAliases) {
      log.info("Handling queryAliases.");
      queryResponse.addAll(((QueryAliases) query).getListOfAnswer());
    }
    else if (query instanceof QueryTypes) {
      log.info("Handling queryTypes.");
      queryResponse.addAll(((QueryTypes) query).getListOfAnswer());
    }
    else {
      log.error("handleQuery() - Unhandled query type.");
      return null;
    }

    return queryResponse;
  }

  /**
   * Handles an indexing query for the ElasticSearch cluster. This method allows
   * to add a new index/type if needed directly to the cluster in addition to
   * adding the data itself.
   * 
   * @param query the query to handle.
   * @param indexName of the index to add the data to.
   * @param settings of the index in the cluster.
   * @param type of the document to add to the cluster.
   * @param mapping contains the information for the indexing: routing, fields,
   *          fields types...
   * @return true if the client has been acked.
   */
  @Override
  public boolean handleIndexQuery(Query query, String indexName, Settings settings, String type, String mapping) {
    boolean isAcked = false;

    if (client == null) {
      log.error("handleIndexQuery() - Error: the client value is " + client + ".");
      return isAcked;
    }

    if (query instanceof QueryIndexBuilder && query.isParametersSet()) {
      isAcked = ((QueryIndexBuilder) query).indexNew(indexName, settings, type, mapping);

      if (isAcked) {
        addType(type);
        addIndex(indexName);
      }

    }
    else {
      log.warn("handleIndexQuery() - Unhandled query type.");
    }

    return isAcked;
  }

  /**
   * Handles an alias addition query for the ElasticSearch cluster.
   * 
   * @param query query type.
   * @param indexMonth name of the index to which to add the alias.
   * @param aliasName alias to give to the name; must follow the format
   *          "tag_tagId".
   * @return if the query has been acked.
   */
  @Override
  public boolean handleAliasQuery(Query query, String indexMonth, String aliasName) {
    boolean isAcked = false;

    if (client == null || !checkIndex(indexMonth) || !checkAlias(aliasName)) {
      log.error("handleAliasQuery() - Error: required values are not set (client=" + client + ", indexname= " + indexMonth + ", aliasname= " + aliasName + ".");
      return isAcked;
    }

    if (query instanceof QueryAliases) {

      isAcked = ((QueryAliases) query).addAlias(indexMonth, aliasName);
      if (isAcked) {
        addAlias(aliasName);
      }

    }
    else {
      log.warn("handleAliasQuery() - Unhandled query type.");
    }

    return isAcked;
  }

  /**
   * Add an indexing request to the bulkProcessor to be added by batches.
   * 
   * @param index for the data.
   * @param type of the document.
   * @param json request for indexing.
   * @param tag TagES to be indexed.
   * @throws IOException
   */
  public boolean bulkAdd(String index, String type, String json, TagES tag) {
    log.debug("indices:");
    for (String i : indices) {
      log.debug(i);
    }

    if (tag == null || index == null || type == null || !checkIndex(index) || !checkType(type)) {
      log.warn("bulkAdd() - Error while indexing data. Bad index or type values: " + index + ", " + type + ".");
      return false;

    }
    else {

      if (!indices.contains(index)) {
        boolean isIndexed = instantiateIndex(tag, index, type);

        if (isIndexed) {
          addType(type);
          addIndex(index);
          log.debug("Indexed a new tag of type " + type + ".");
        }
      }

      IndexRequest indexNewTag = new IndexRequest(index, type).source(json).routing(String.valueOf(tag.getTagId()));
      bulkProcessor.add(indexNewTag);
      return true;
    }
  }

  /**
   * Add an alias query when the data is written by batches.
   * 
   * @param indexMonth to add the data into.
   * @param tag to be added to the cluster.
   * @return if the alias is in the aliases list.
   */
  public boolean bulkAddAlias(String indexMonth, TagES tag) {
    if (tag == null || !indices.contains(indexMonth) || !checkIndex(indexMonth)) {
      throw new IllegalArgumentException("bulkAddAlias() - IllegalArgument (tag = " + tag + ", index = " + indexMonth + ").");
    }

    long id = tag.getTagId();
    String aliasName = generateAliasName(id);

    if (!aliases.contains(aliasName)) {
      boolean isAcked = handleAliasQuery(new QueryAliases(client, Arrays.asList(indexMonth), false, null, null, -1, -1, -1, -1), indexMonth, aliasName);

      if (isAcked) {
        addAlias(aliasName);
        log.debug("Add alias: " + aliasName + " for index " + indexMonth);
      }
      return true;
    }
    else {
      return false;
    }
  }

  /**
   * If index does not exist already when adding data as bulk, we address a new
   * indexQuery to the ElasticSearch cluster.
   * 
   * @param tag TagES to add to the cluster.
   * @param index index to which add the TagES tag.
   * @param type the type of the TagES tag according to its dataType.
   * @return the boolean status of the Query.
   */
  public boolean instantiateIndex(TagES tag, String index, String type) {
    if (indices.contains(index)) {
      return false;
    }

    String mapping = null;

    if (!types.contains(type)) {
      mapping = tag.getMapping();
    }

    updateLists();

    return handleIndexQuery(new QueryIndexBuilder(client, Arrays.asList(index), true, Arrays.asList(type), null, -1, -1, -1, -1), index,
        getMonthIndexSettings(), type, mapping);
  }

  /**
   * Query the ElasticSearch cluster to retrieve all the indices, types and
   * aliases present already at startup. Store them in memory in the Sets:
   * indices, types and aliases.
   */
  public void updateLists() {
    indices.addAll(handleListingQuery(new QueryIndices(client)));
    types.addAll(handleListingQuery(new QueryTypes(client)));
    aliases.addAll(handleListingQuery(new QueryAliases(client)));
  }

  /**
   * Utility method. Aliases have the following format: "tag_tagId".
   * 
   * @param tagId
   * @return name of the alias for a given tagId.
   */
  public String generateAliasName(long tagId) {
    return TAG_PREFIX + tagId;
  }

  /**
   * Type in ElasticSearch.
   * 
   * @param dataType TagES's dataType.
   * @return String of the form "tag_type"
   */
  public String generateType(String dataType) {
    return TAG_PREFIX + dataType.toLowerCase();
  }

  /**
   * Index where a TagES is stored in the ElasticSearch cluster.
   * 
   * @param tagServerTime TagES's tagServerTime (milliseconds since Epoch).
   * @return name of the index of tag.
   */
  public String generateIndex(long tagServerTime) {
    return INDEX_PREFIX + millisecondsToYearMonth(tagServerTime);
  }

  /**
   * Utility method used by getIndex().
   * 
   * @param millis timestamp in ElasticSearch (milliseconds since Epoch)
   * @return String containing the corresponding "yyyy-MM".
   */
  public String millisecondsToYearMonth(long millis) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");

    Date date = new Date(millis);
    String timestamp = sdf.format(date);
    return timestamp.substring(0, 7);
  }

  /**
   * Close the bulk after it sent enough: reached bulkActions, bulkSize or
   * flushInterval. And create a new one for further requests.
   */
  public void closeBulk() {
    try {
      bulkProcessor.awaitClose(1, TimeUnit.SECONDS);
    }
    catch (InterruptedException e) {
      log.warn("closeBulk() - Error whilst awaitClose() the bulkProcessor.", e);
    }
    initBulkSettings();
  }

  /**
   * Method called to close the newly opened client.
   * 
   * @param client transportClient for the cluster.
   */
  @Override
  public void close(Client client) {
    if (client != null) {
      client.close();
      log.info("Closed client: " + client.settings().get("node.name"));
    }
  }

  /**
   * Add an index to the Set indices. Called by the writing of a new Index if it was successful.
   * @param indexName name of the index created in ElasticSearch.
   */
  public void addIndex(String indexName) {
    if (checkIndex(indexName)) {
      indices.add(indexName);
    }
    else {
      throw new IllegalArgumentException("Indices must follow the format \"c2mon_YYYY_MM\".");
    }
  }

  /**
   * Add an alias to the Set aliases. Called by the writing of a new alias if it was successful.
   * @param aliasName
   */
  public void addAlias(String aliasName) {
    if (checkAlias(aliasName)) {
      aliases.add(aliasName);
    }
    else {
      throw new IllegalArgumentException("Aliases must follow the format \"tag_tagId\".");
    }
  }

  /**
   * Add a type to the Set types. Called by the writing of a new Index if it was successful.
   * @param typeName type defined for the new document.
   */
  public void addType(String typeName) {
    if (checkType(typeName)) {
      types.add(typeName);
    }
    else {
      throw new IllegalArgumentException("Types must follow the format \"tag_dataType\".");
    }
  }

  /**
   * Check if the index has the right format: c2mon_YYYY-MM.
   * @param index the actual index name.
   * @return true if it has the right format, false otherwise.
   */
  public boolean checkIndex(String index) {
    return index.matches("^" + INDEX_PREFIX + "\\d\\d\\d\\d-\\d\\d$");
  }

  /**
   * Check if an alias has the right format: tag_tagId.
   * @param alias the acutal alias name.
   * @return true if it has the right format, false otherwise.
   */
  public boolean checkAlias(String alias) {
    return alias.matches("^" + TAG_PREFIX + "\\d+$");
  }

  /**
   * Check if a type has the right format: tag_(string||long||int||double||boolean)
   * @param type the type name.
   * @return true if it has the right format, false otherwise.
   */
  public boolean checkType(String type) {
    String dataType = type.substring(TAG_PREFIX.length());
    return type.matches("^" + TAG_PREFIX + ".+$") && (dataType.matches(Mapping.boolType) || dataType.matches(Mapping.doubleType)
        || dataType.matches(Mapping.intType) || dataType.matches(Mapping.longType) || dataType.matches(Mapping.stringType));
  }

  /**
   * Settings for the index/Month: 10 shards and 0 replica.
   * 
   * @return Settings.Builder to attach to an IndexRequest.
   */
  public Settings getMonthIndexSettings() {
    return Settings.settingsBuilder().put("number_of_shards", IndexMonthSettings.SHARDS.getSetting())
        .put("number_of_replicas", IndexMonthSettings.REPLICA.getSetting()).build();
  }
}