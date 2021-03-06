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
package cern.c2mon.shared.util.threadhandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.extern.slf4j.Slf4j;


/**
 * Class used to feed objects to a given method on a separate thread.
 * The thread is notified of incoming objects as soon as they arrive,
 * and pauses when no objects are incoming.
 *
 * The same handler can be started and shutdown once only. When
 * shutdown, it throws an exception if one attempts to pass some parameters
 * to it using the put method. It also logs a warning if parameters are
 * being passed to the thread and it has not been started. If fed the
 * wrong type of parameters errors are logged and the parameters are
 * ignored.
 *
 * This implementation uses reflection, so the calls to the registered method
 * are relatively slow. Use the {@link ThreadHandler} class for improved
 * performance (although a specific interface then needs implementing).
 *
 * All exceptions are caught by the ThreadHandler's thread when calling the
 * passed method (the events are simply logged and dropped, so the ThreadHandler
 * provides no guarantee they will be executed).
 * @author mbrightw
 *
 */
@Slf4j
public class ThreadHandler extends Thread {

  /**
   * Object used to synchronize access to the list of incoming objects.
   */
  private final Object threadMonitorObject = new Object();

  /**
   * The object that must be fed the incoming objects.
   */
  private Object objectToCall;

  /**
   * The method that should be called on the objectToCall object
   * with the incoming object as parameter list.
   */
  private Method methodToCall;

  /**
   * The list containing the incoming parameters to be processed by the thread.
   */
  private LinkedBlockingQueue<Object[]> incomingQueue;

  /**
   * Copy of the list.
   */
  private LinkedList<Object[]> copyList;

  /**
   * Flag which indicates that the ThreadHandler is shutting down,
   * so no new values should be accepted and the list should be emptied.
   */
  private volatile boolean handlerEnabled;

  /**
   * Create a new thread to consume the objects. Started with the run method.
   * @param processingObject
   * @param method
   */
  public ThreadHandler(Object processingObject, Method processingMethod) {
    super();
    this.methodToCall = processingMethod;
    this.objectToCall = processingObject;
    this.handlerEnabled = true;

    incomingQueue = new LinkedBlockingQueue<>();
    copyList = new LinkedList<>();
  }
  /**
   * Add the parameter list to the thread (constitutes a "single"
   * incoming object, and should be the arguments taken by the method).
   * Called by external thread.
   * @param parameters
   */
  public void put(Object[] parameters) {
    if (!this.isAlive()) {
      synchronized (threadMonitorObject) {
        log.warn("HandlerThread has not being started yet and objects are being added!");
        incomingQueue.offer(parameters);
      }
    } else {
      if (handlerEnabled) {
        synchronized (threadMonitorObject) {
          incomingQueue.offer(parameters);
          threadMonitorObject.notify();
        }
      } else {
        throw new IllegalStateException("The ThreadHandler is no longer enabled and cannot accept any new objects.");
      }
    }
  }

  /**
   * Start the thread. Expires once shutdown has been called.
   */
  @Override
  public void run() {
    //wait to be notified of incoming objects for the first time
    synchronized (threadMonitorObject) {
      if (incomingQueue.isEmpty()) {
        try {
          threadMonitorObject.wait();
        } catch (InterruptedException e) {
          log.error("ThreadHandler interrupted while waiting on monitor object");
        }
      }
    }

    //while the handler is enabled, or if there are still some objects in the list, so not expire the thread
    while (handlerEnabled || !incomingQueue.isEmpty()) {

      //while the list is not empty, make synchronized copies are treat the objects
      while (!incomingQueue.isEmpty()) {
        //make a copy of the list of incoming objects
        synchronized (threadMonitorObject) {
          incomingQueue.drainTo(copyList); //get a shallow copy
        }

        //process all objects in copied list (other threads can add to the linkedList).
        while (!copyList.isEmpty()) {
          try {
            methodToCall.invoke(objectToCall, copyList.pollFirst());
          } catch (IllegalArgumentException e) {
            log.error("ThreadHandler passed incorrect parameters - unable to process them: {}", e.getMessage());
          } catch (IllegalAccessException e) {
            log.error("IllegalAccessException caught while invoking method on ThreadHandler: {}", e.getMessage());
          } catch (InvocationTargetException e) {
            log.error("InvocationTargetException caught while invoking method on ThreadHandler: {}", e.getMessage());
          } catch (Exception e) {
            log.error("Unidentified exception caught in the ThreadHandler thread: {}", e.getMessage());
          }
        }
      }

      //if the list is empty, wait for a notification, otherwise start processing again
      synchronized (threadMonitorObject) {
        if (incomingQueue.isEmpty()) {
          try {
            threadMonitorObject.wait();
          } catch (InterruptedException e) {
            log.error("Interrupted while waiting.");
            e.printStackTrace();
          }
        }
      }

    }


  }


  /**
   * Returns the number of calls queued by the ThreadHandler.
   * @return number of method calls queue by the ThreadHandler
   */
  public int getTaskQueueSize() {
    synchronized (threadMonitorObject) {
     return incomingQueue.size();
    }
  }

  /**
   * Wait for the all incoming values to be dealt with, then expire the thread.
   * Does nothing if already shutdown.
   */
  public void shutdown() {
    this.handlerEnabled = false;
    synchronized (threadMonitorObject) {
      threadMonitorObject.notify();
    }
    //give some time to empty the list
    while (!incomingQueue.isEmpty()) {
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        log.error("Interrupted while waiting.");
        e.printStackTrace();
      }
    }
  }

}
