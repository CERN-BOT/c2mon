/*******************************************************************************
 * Copyright (C) 2010-2016 CERN. All rights not expressly granted are reserved.
 *
 * This file is part of the CERN Control and Monitoring Platform 'C2MON'.
 * C2MON is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the license.
 *
 * C2MON is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with C2MON. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package cern.c2mon.server.elasticsearch.indexer;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import cern.c2mon.pmanager.persistence.exception.IDBPersistenceException;
import cern.c2mon.server.elasticsearch.config.ElasticsearchProperties;
import cern.c2mon.server.elasticsearch.connector.TransportConnector;
import cern.c2mon.server.elasticsearch.structure.types.tag.EsTagConfig;

/**
 * @author Szymon Halastra
 */
@Slf4j
@Component
public class EsTagConfigIndexer<T extends EsTagConfig> {

  @Autowired
  @Setter
  private ElasticsearchProperties properties;

  @Autowired
  private TransportConnector connector;

  @Autowired
  public EsTagConfigIndexer(final TransportConnector connector, final ElasticsearchProperties properties) {
    this.connector = connector;
    this.properties = properties;
  }

  @PostConstruct
  public void init() throws IDBPersistenceException {
    connector.createIndex(properties.getIndexTagConfig());
  }

  public void storeData(T object) throws IDBPersistenceException {
    if (object == null) {
      return;
    }

    boolean logged = false;
    try {
      logged = indexTagConfig(object);
    }
    catch (Exception e) {
      throw new IDBPersistenceException(e);
    }
  }

  protected boolean indexTagConfig(EsTagConfig tag) {
    String type = generateTagType(tag.getC2mon().getDataType());

    log.trace("Indexing a new tag (#{}) with type={}", tag.getId(), type);

    if (type == null /*|| !checkIndex(index)*/) {
      log.warn("Error while indexing tag #{}. Bad index {}  -> Tag will not be sent to elasticsearch!",
              tag.getId(), properties.getIndexTagConfig());
      return false;
    }

    String tagJson = tag.toString();
    log.debug("sendTagToBatch() - New 'IndexRequest' for index {} and source {}", properties.getIndexTagConfig(), tagJson);
    IndexRequest indexNewTag = new IndexRequest(properties.getIndexTagConfig(), type, String.valueOf(tag.getId())).source(tagJson);
    return connector.logTagConfig(properties.getIndexTagConfig(), indexNewTag);
  }

  public void storeData(List<T> data) throws IDBPersistenceException {
    if (data == null) {
      return;
    }

    try {
      log.debug("storeData() - Try to send data by batch of size " + data.size());

      this.indexTags(data);
    }
    catch (ElasticsearchException e) {
      throw new IDBPersistenceException(e);
    } finally {

    }
  }

  public synchronized void indexTags(Collection<T> tags) throws IDBPersistenceException {
    if (tags == null) {
      return;
    }

    log.debug("indexTags() - Received a collection of " + tags.size() + " EsTagConfig tags to send by batch.");

    if (CollectionUtils.isEmpty(tags)) {
      return;
    }

    for (EsTagConfig tagConfig : tags) {
      if (indexTagConfig(tagConfig)) {
        log.debug(tagConfig.toString());
      }
    }

    connector.getBulkProcessor().flush();

    connector.refreshIndices();
  }

  private String generateTagType(String dataType) {
    return "type_" + getSimpleTypeName(dataType);
  }

  public static String getSimpleTypeName(String dataType) {
    String type = dataType.toLowerCase();

    if (dataType.contains(".")) {
      type = type.substring(type.lastIndexOf('.') + 1);
    }

    return type;
  }
}