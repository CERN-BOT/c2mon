/******************************************************************************
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
 *****************************************************************************/
package cern.c2mon.server.elasticsearch.listener;

import javax.annotation.PostConstruct;

import cern.c2mon.server.elasticsearch.structure.converter.EsSupervisionEventConverter;
import cern.c2mon.server.elasticsearch.structure.types.EsSupervisionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cern.c2mon.pmanager.persistence.IPersistenceManager;
import cern.c2mon.server.common.component.Lifecycle;
import cern.c2mon.server.common.config.ServerConstants;
import cern.c2mon.server.supervision.SupervisionListener;
import cern.c2mon.server.supervision.SupervisionNotifier;
import cern.c2mon.shared.client.supervision.SupervisionEvent;

/**
 * Listens for Supervision notifications and send the corresponding {@link EsSupervisionEvent} to Elasticsearch.
 *
 * @author Alban Marguet
 */
@Slf4j
@Service
public class EsSupervisionEventListener implements SupervisionListener, SmartLifecycle {
  /**
   * Notifier of supervision module.
   */
  private SupervisionNotifier supervisionNotifier;

  /**
   * Supervision Mapper
   */
  private IPersistenceManager<EsSupervisionEvent> persistenceManager;

  /**
   * Convert SupervisionEvent to EsSupervisionEvent for Elasticsearch.
   */
  private EsSupervisionEventConverter esSupervisionEventConverter;

  /**
   * Listener container lifecycle hook.
   */
  private Lifecycle listenerContainer;

  /**
   * Lifecycle flag.
   */
  private volatile boolean running = false;

  /**
   * Autowired constructor.
   *
   * @param supervisionNotifier the notifier to register to
   * @param persistenceManager  the mapper to write to the DB
   */
  @Autowired
  public EsSupervisionEventListener(final SupervisionNotifier supervisionNotifier,
                                    @Qualifier("esSupervisionEventPersistenceManager") final IPersistenceManager<EsSupervisionEvent> persistenceManager,
                                    final EsSupervisionEventConverter esSupervisionEventConverter) {
    this.supervisionNotifier = supervisionNotifier;
    this.persistenceManager = persistenceManager;
    this.esSupervisionEventConverter = esSupervisionEventConverter;
  }

  /**
   * Called at bean initialisation. Registers for notifications.
   */
  @PostConstruct
  public void init() {
    listenerContainer = supervisionNotifier.registerAsListener(this);
  }

  @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
  @Override
  public void notifySupervisionEvent(final SupervisionEvent supervisionEvent) {
    if(supervisionEvent == null) {
      log.warn("notifySupervisionEvent() - Warning: Received a null supervision event.");
      return;
    }

    log.debug("notifySupervisionEvent() - Logging supervision status " + supervisionEvent.getStatus()
        + " for " + supervisionEvent.getEntity()
        + " " + supervisionEvent.getEntityId() + " to Elasticsearch");

    try {
      EsSupervisionEvent esSupervisionEvent = esSupervisionEventConverter.convert(supervisionEvent);
      persistenceManager.storeData(esSupervisionEvent);
    } catch(Exception e) {
      log.error("Could not add SupervisionEvent to Elasticsearch: Event #{}", supervisionEvent.getEntityId(), e);
    }
  }

  @Override
  public boolean isAutoStartup() {
    return false;
  }

  @Override
  public void stop(Runnable runnable) {
    stop();
    runnable.run();
  }

  @Override
  public boolean isRunning() {
    return running;
  }

  @Override
  public void start() {
    log.debug("Starting ES supervision event logger.");
    running = true;
    listenerContainer.start();
  }

  @Override
  public void stop() {
    log.debug("Stopping ES supervision event logger.");
    listenerContainer.stop();
    running = false;
  }

  @Override
  public int getPhase() {
    return ServerConstants.PHASE_STOP_LAST - 1;
  }
}