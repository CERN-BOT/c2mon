/*******************************************************************************
 * This file is part of the Technical Infrastructure Monitoring (TIM) project.
 * See http://ts-project-tim.web.cern.ch
 *
 * Copyright (C) 2004 - 2014 CERN. This program is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with this program; if not,
 * write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * Author: TIM team, tim.support@cern.ch
 ******************************************************************************/
package cern.c2mon.client.ext.device;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.reset;
import static org.easymock.classextension.EasyMock.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;

import org.apache.log4j.Logger;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cern.c2mon.client.common.listener.DataTagListener;
import cern.c2mon.client.common.tag.ClientDataTagValue;
import cern.c2mon.client.core.C2monCommandManager;
import cern.c2mon.client.core.C2monTagManager;
import cern.c2mon.client.core.cache.BasicCacheHandler;
import cern.c2mon.client.core.tag.ClientCommandTagImpl;
import cern.c2mon.client.core.tag.ClientDataTagImpl;
import cern.c2mon.client.ext.device.cache.DeviceCache;
import cern.c2mon.client.ext.device.property.Category;
import cern.c2mon.client.ext.device.property.Field;
import cern.c2mon.client.ext.device.property.FieldImpl;
import cern.c2mon.client.ext.device.property.Property;
import cern.c2mon.client.ext.device.property.PropertyImpl;
import cern.c2mon.client.ext.device.property.PropertyInfo;
import cern.c2mon.client.ext.device.request.DeviceRequestHandler;
import cern.c2mon.client.ext.device.util.DeviceTestUtils;
import cern.c2mon.shared.client.device.DeviceClassNameResponse;
import cern.c2mon.shared.client.device.DeviceClassNameResponseImpl;
import cern.c2mon.shared.client.device.DeviceCommand;
import cern.c2mon.shared.client.device.DeviceInfo;
import cern.c2mon.shared.client.device.DeviceProperty;
import cern.c2mon.shared.client.device.TransferDevice;
import cern.c2mon.shared.client.device.TransferDeviceImpl;
import cern.c2mon.shared.client.tag.TagUpdate;
import cern.c2mon.shared.rule.RuleFormatException;

/**
 * @author Justin Lewis Salmon
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:cern/c2mon/client/ext/device/config/c2mon-devicemanager-test.xml" })
public class DeviceManagerTest {

  private Logger LOG = Logger.getLogger(DeviceManagerTest.class);

  /** Component to test */
  @Autowired
  private DeviceManager deviceManager;

  /** Mocked components */
  @Autowired
  private C2monTagManager tagManagerMock;

  @Autowired
  private C2monCommandManager commandManagerMock;

  @Autowired
  private DeviceCache deviceCacheMock;

  @Autowired
  private BasicCacheHandler dataTagCacheMock;

  @Autowired
  private DeviceRequestHandler requestHandlerMock;

  @Test
  public void testGetAllDeviceClassNames() throws JMSException {
    // Reset the mock
    reset(requestHandlerMock);

    List<DeviceClassNameResponse> deviceClassNamesReturnMap = new ArrayList<DeviceClassNameResponse>();
    deviceClassNamesReturnMap.add(new DeviceClassNameResponseImpl("test_device_class_1"));
    deviceClassNamesReturnMap.add(new DeviceClassNameResponseImpl("test_device_class_2"));

    // Expect the device manager to query the server
    expect(requestHandlerMock.getAllDeviceClassNames()).andReturn(deviceClassNamesReturnMap).once();

    // Setup is finished, need to activate the mock
    replay(requestHandlerMock);

    List<String> deviceClassNames = deviceManager.getAllDeviceClassNames();
    Assert.assertNotNull(deviceClassNames);
    Assert.assertTrue(deviceClassNames.get(0) == deviceClassNamesReturnMap.get(0).getDeviceClassName());
    Assert.assertTrue(deviceClassNames.get(1) == deviceClassNamesReturnMap.get(1).getDeviceClassName());

    // Verify that everything happened as expected
    verify(requestHandlerMock);
  }

  @Test
  public void testGetAllDevices() throws JMSException {
    // Reset the mock
    EasyMock.reset(tagManagerMock, deviceCacheMock, dataTagCacheMock, commandManagerMock);
    reset(requestHandlerMock);

    List<TransferDevice> devicesReturnList = new ArrayList<TransferDevice>();
    final TransferDeviceImpl device1 = new TransferDeviceImpl(1000L, "test_device_1", 1L, "test_device_class");
    final TransferDeviceImpl device2 = new TransferDeviceImpl(1000L, "test_device_2", 1L, "test_device_class");
    device1.addDeviceProperty(new DeviceProperty(1L, "TEST_PROPERTY_1", "100430", "tagId", null));
    device2.addDeviceProperty(new DeviceProperty(2L, "TEST_PROPERTY_2", "100431", "tagId", null));
    device1.addDeviceCommand(new DeviceCommand(1L, "TEST_COMMAND_1", "4287", "commandTagId", null));
    device2.addDeviceCommand(new DeviceCommand(2L, "TEST_COMMAND_2", "4288", "commandTagId", null));
    devicesReturnList.add(device1);
    devicesReturnList.add(device2);

    ClientCommandTagImpl cct1 = new ClientCommandTagImpl<>(-1L);
    ClientCommandTagImpl cct2 = new ClientCommandTagImpl<>(-2L);

    // Expect the device manager to check the cache
    EasyMock.expect(deviceCacheMock.getAllDevices("test_device_class")).andReturn(new ArrayList<Device>());
    // Expect the device manager to retrieve the devices
    expect(requestHandlerMock.getAllDevices(EasyMock.<String> anyObject())).andReturn(devicesReturnList);
    // Expect the device manager to get the command tags
    EasyMock.expect(commandManagerMock.getCommandTag(EasyMock.<Long> anyObject())).andReturn(cct1);
    EasyMock.expect(commandManagerMock.getCommandTag(EasyMock.<Long> anyObject())).andReturn(cct2);
    // Expect the device manager to add the devices to the cache
    deviceCacheMock.add(EasyMock.<Device> anyObject());
    EasyMock.expectLastCall().times(2);

    // Setup is finished, need to activate the mock
    EasyMock.replay(tagManagerMock, deviceCacheMock, dataTagCacheMock, commandManagerMock);
    replay(requestHandlerMock);

    List<Device> devices = deviceManager.getAllDevices("test_device_class");
    Assert.assertNotNull(devices);
    Assert.assertTrue(devices.size() == 2);

    for (Device device : devices) {
      Assert.assertTrue(device.getDeviceClassName().equals("test_device_class"));
    }

    // Verify that everything happened as expected
    EasyMock.verify(tagManagerMock, deviceCacheMock, dataTagCacheMock, commandManagerMock);
    verify(requestHandlerMock);
  }

  @Test
  public void testSubscribeDevice() {
    // Reset the mock
    EasyMock.reset(tagManagerMock, deviceCacheMock, dataTagCacheMock);

    ClientDataTagImpl cdt1 = new ClientDataTagImpl(100000L);
    ClientDataTagImpl cdt2 = new ClientDataTagImpl(200000L);

    Map<String, ClientDataTagValue> deviceProperties = new HashMap<String, ClientDataTagValue>();
    deviceProperties.put("test_property_name_1", cdt1);
    deviceProperties.put("test_property_name_2", cdt2);

    final DeviceImpl device1 = new DeviceImpl(1L, "test_device", 1L, "test_device_class", tagManagerMock, commandManagerMock);
    device1.setDeviceProperties(deviceProperties);

    final Map<Long, ClientDataTagValue> cacheReturnMap = getCacheReturnMap();

    // Expect the tag manager to subscribe to the tags
    tagManagerMock.subscribeDataTags(EasyMock.<Set<Long>> anyObject(), EasyMock.<DataTagListener> anyObject());
    EasyMock.expectLastCall().andAnswer(new IAnswer<Boolean>() {
      @Override
      public Boolean answer() throws Throwable {
        deviceManager.onInitialUpdate(cacheReturnMap.values());
        return true;
      }
    }).once();

    // Expect the device manager to get the devices from the cache - once to
    // call onInitialUpdate() and once to call onUpdate()
    EasyMock.expect(deviceCacheMock.getAllDevices()).andReturn(Arrays.asList((Device) device1)).times(2);

    // Setup is finished, need to activate the mock
    EasyMock.replay(tagManagerMock, deviceCacheMock, dataTagCacheMock);

    TestDeviceUpdateListener listener = new TestDeviceUpdateListener();
    deviceManager.subscribeDevice(device1, listener);

    // Simulate the tag update calls
    new Thread(new Runnable() {
      @Override
      public void run() {
        for (ClientDataTagValue tag : cacheReturnMap.values()) {
          deviceManager.onUpdate(tag);
        }
      }
    }).start();

    listener.await(5000L);
    Device device = listener.getDevice();
    Assert.assertNotNull(device);
    Assert.assertTrue(device.getId() == device1.getId());

    Assert.assertTrue(device.getProperty("test_property_name_1").getTag().getId().equals(100000L));
    Assert.assertTrue(device.getProperty("test_property_name_2").getTag().getId().equals(200000L));

    // Verify that everything happened as expected
    EasyMock.verify(tagManagerMock, deviceCacheMock, dataTagCacheMock);
  }

  @Test
  public void testSubscribeLazyDevice() throws RuleFormatException, ClassNotFoundException {
    // Reset the mock
    EasyMock.reset(tagManagerMock, deviceCacheMock, dataTagCacheMock);

    List<DeviceProperty> sparsePropertyMap = new ArrayList<>();
    sparsePropertyMap.add(new DeviceProperty(1L, "test_property_name_1", "100000", "tagId", null));
    sparsePropertyMap.add(new DeviceProperty(2L, "test_property_name_2", "200000", "tagId", null));

    final DeviceImpl device1 = new DeviceImpl(1L, "test_device", 1L, "test_device_class", tagManagerMock, commandManagerMock);
    device1.setDeviceProperties(sparsePropertyMap);

    final Map<Long, ClientDataTagValue> cacheReturnMap = getCacheReturnMap();

    // Expect the device to not call getDataTags() but instead to
    // get the tags from the cache
    // EasyMock.expect(dataTagCacheMock.get(EasyMock.<Set<Long>>
    // anyObject())).andReturn(cacheReturnMap).once();

    // Expect the tag manager to subscribe to the tags
    tagManagerMock.subscribeDataTags(EasyMock.<Set<Long>> anyObject(), EasyMock.<DataTagListener> anyObject());
    EasyMock.expectLastCall().andAnswer(new IAnswer<Boolean>() {
      @Override
      public Boolean answer() throws Throwable {
        deviceManager.onInitialUpdate(cacheReturnMap.values());
        return true;
      }
    }).once();

    // Expect the device manager to get the devices from the cache - once to
    // call onInitialUpdate() and once to call onUpdate()
    EasyMock.expect(deviceCacheMock.getAllDevices()).andReturn(Arrays.asList((Device) device1)).times(2);

    // Setup is finished, need to activate the mock
    EasyMock.replay(tagManagerMock, deviceCacheMock, dataTagCacheMock);

    TestDeviceUpdateListener listener = new TestDeviceUpdateListener();
    deviceManager.subscribeDevice(device1, listener);

    // Update a property
    deviceManager.onUpdate(cacheReturnMap.get(100000L));

    listener.await(5000L);
    Device device = listener.getDevice();
    Assert.assertNotNull(device);
    Assert.assertTrue(device.getId() == device1.getId());

    // Check all properties are subscribed to at this point
    Assert.assertTrue(areAllPropertiesAndFieldsSubscribed(listener.getDevices()));

    Assert.assertTrue(device.getProperty("test_property_name_1").getTag().getId().equals(100000L));
    Assert.assertTrue(device.getProperty("test_property_name_2").getTag().getId().equals(200000L));

    // Verify that everything happened as expected
    EasyMock.verify(tagManagerMock, deviceCacheMock, dataTagCacheMock);
  }

  @Test
  public void testSubscribeDeviceWithFields() throws RuleFormatException {
    // Reset the mock
    EasyMock.reset(tagManagerMock, deviceCacheMock, dataTagCacheMock);

    Map<String, Field> deviceFields = new HashMap<>();

    for (int i = 0; i < 1000; i++) {
      deviceFields.put("test_field_name_" + i, new FieldImpl("test_field_name_" + i, Category.TAG_ID, new Long(i)));
    }

    HashMap<String, Property> deviceProperties = new HashMap<>();
    deviceProperties.put("test_property_name", new PropertyImpl("test_property_name", Category.MAPPED_PROPERTY, deviceFields));

    final DeviceImpl device1 = new DeviceImpl(1L, "test_device", 1L, "test_device_class", tagManagerMock, commandManagerMock);
    device1.setDeviceProperties(deviceProperties);
    device1.setTagManager(tagManagerMock);

    final Map<Long, ClientDataTagValue> cacheReturnMap = new HashMap<Long, ClientDataTagValue>();

    for (int i = 0; i < 1000; i++) {
      ClientDataTagImpl cdt = new ClientDataTagImpl(new Long(i));
      cdt.update(DeviceTestUtils.createValidTransferTag(new Long(i), "test_tag_name_" + i, "test_value_" + i));
      cacheReturnMap.put(new Long(i), cdt);
    }

    // Expect the tag manager to subscribe to the tags
    tagManagerMock.subscribeDataTags(EasyMock.<Set<Long>> anyObject(), EasyMock.<DataTagListener> anyObject());
    EasyMock.expectLastCall().andAnswer(new IAnswer<Boolean>() {
      @Override
      public Boolean answer() throws Throwable {
        deviceManager.onInitialUpdate(cacheReturnMap.values());
        return true;
      }
    }).once();

    // Expect the device manager to get the devices from the cache - once to
    // call onInitialUpdate() and once to call onUpdate()
    EasyMock.expect(deviceCacheMock.getAllDevices()).andReturn(Arrays.asList((Device) device1)).times(2);

    // Setup is finished, need to activate the mock
    EasyMock.replay(tagManagerMock, deviceCacheMock, dataTagCacheMock);

    TestDeviceUpdateListener listener = new TestDeviceUpdateListener();
    deviceManager.subscribeDevices(new HashSet<Device>(Arrays.asList(device1)), listener);

    // Update a property
    deviceManager.onUpdate(cacheReturnMap.get(0L));

    listener.await(5000L);
    Device device = listener.getDevice();
    Assert.assertNotNull(device);
    Assert.assertTrue(device.getId() == device1.getId());

    Assert.assertTrue(areAllPropertiesAndFieldsSubscribed(listener.getDevices()));

    Property propertyWithFields = device.getProperty("test_property_name");
    Assert.assertTrue(propertyWithFields.getCategory().equals(Category.MAPPED_PROPERTY));
    for (int i = 0; i < 1000; i++) {
      ClientDataTagValue tag = propertyWithFields.getField("test_field_name_" + i).getTag();
      Assert.assertTrue(tag.getId().equals(new Long(i)));
      Assert.assertTrue(tag.getValue() != null);
      Assert.assertTrue(tag.getValue().equals("test_value_" + i));
    }

    // Verify that everything happened as expected
    EasyMock.verify(tagManagerMock, deviceCacheMock, dataTagCacheMock);
  }

  @Test
  public void testSubscribeDeviceByName() throws JMSException, ClassNotFoundException, RuleFormatException {
    // Reset the mock
    EasyMock.reset(tagManagerMock, deviceCacheMock, dataTagCacheMock, commandManagerMock);
    reset(requestHandlerMock);

    List<TransferDevice> devicesReturnList = new ArrayList<TransferDevice>();
    final TransferDeviceImpl transferDevice = new TransferDeviceImpl(1000L, "test_device_1", 1L, "test_device_class");
    transferDevice.addDeviceProperty(new DeviceProperty(1L, "TEST_PROPERTY_1", "100430", "tagId", null));
    transferDevice.addDeviceCommand(new DeviceCommand(1L, "TEST_COMMAND_1", "4287", "commandTagId", null));
    devicesReturnList.add(transferDevice);

    ClientCommandTagImpl cct1 = new ClientCommandTagImpl<>(-1L);

    // Expect the device manager to retrieve the devices
    expect(requestHandlerMock.getDevices(EasyMock.<Set<DeviceInfo>> anyObject())).andReturn(devicesReturnList);
    // Expect the device manager to get the command tag
    EasyMock.expect(commandManagerMock.getCommandTag(EasyMock.<Long> anyObject())).andReturn(cct1).times(1);
    // Expect the device manager to add the device to the cache
    final Capture<Device> capturedDevice = new Capture<>();
    deviceCacheMock.add(EasyMock.capture(capturedDevice));
    EasyMock.expectLastCall();

    final TestDeviceUpdateListener listener = new TestDeviceUpdateListener();

    // Expect the device manager to subscribe to the tag
    tagManagerMock.subscribeDataTags(EasyMock.<Set<Long>> anyObject(), EasyMock.<DataTagListener> anyObject());
    EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
      @Override
      public Boolean answer() throws Throwable {
        deviceManager.onInitialUpdate(Arrays.asList((ClientDataTagValue) new ClientDataTagImpl(100430L)));
        return true;
      }
    }).once();

    // Expect the device manager to get the devices from the cache - once to
    // call onInitialUpdate() and once to call onUpdate()
    EasyMock.expect(deviceCacheMock.getAllDevices()).andAnswer(new IAnswer<List<Device>>() {
      @Override
      public List<Device> answer() throws Throwable {
        return Arrays.asList(capturedDevice.getValue());
      }
    }).times(2);

    // Setup is finished, need to activate the mock
    EasyMock.replay(tagManagerMock, deviceCacheMock, dataTagCacheMock, commandManagerMock);
    replay(requestHandlerMock);

    // Run the actual code to be tested
    deviceManager.subscribeDevice(new DeviceInfo("test_device_class", "test_device_1"), listener);

    // Simulate the tag update calls
    new Thread(new Runnable() {
      @Override
      public void run() {
        deviceManager.onUpdate(new ClientDataTagImpl(100430L));
      }
    }).start();

    listener.await(5000L);
    Device device2 = listener.getDevice();
    Assert.assertNotNull(device2);
    Assert.assertTrue(device2.getId() == listener.getDevice().getId());

    Assert.assertTrue(listener.getDevice().getProperty("TEST_PROPERTY_1").getTag().getId().equals(100430L));

    // Verify that everything happened as expected
    EasyMock.verify(tagManagerMock, deviceCacheMock, dataTagCacheMock, commandManagerMock);
    verify(requestHandlerMock);
  }

  @Test
  public void testSubscribeNonexistentDeviceByName() throws JMSException, InterruptedException {
    // Reset the mock
    EasyMock.reset(tagManagerMock, deviceCacheMock, dataTagCacheMock, commandManagerMock);
    reset(requestHandlerMock);

    // Expect the device manager to retrieve the devices
    expect(requestHandlerMock.getDevices(EasyMock.<Set<DeviceInfo>> anyObject())).andReturn(new ArrayList<TransferDevice>());

    // Setup is finished, need to activate the mock
    EasyMock.replay(tagManagerMock, deviceCacheMock, dataTagCacheMock, commandManagerMock);
    replay(requestHandlerMock);

    // Attempt to get a nonexistent device
    final CountDownLatch latch = new CountDownLatch(1);
    deviceManager.subscribeDevice(new DeviceInfo("nonexistent", "nonexistent"), new DeviceInfoUpdateListener() {
      @Override
      public void onUpdate(Device device, PropertyInfo propertyInfo) {
      }

      @Override
      public void onInitialUpdate(List<Device> devices) {
      }

      @Override
      public void onDevicesNotFound(List<DeviceInfo> unknownDevices) {
        latch.countDown();
      }
    });

    latch.await(1000, TimeUnit.MILLISECONDS);
    Assert.assertTrue(latch.getCount() == 0);

    // Verify that everything happened as expected
    EasyMock.verify(tagManagerMock, deviceCacheMock, dataTagCacheMock, commandManagerMock);
    verify(requestHandlerMock);
  }

  @Test
  public void testSubscribeDevicesByName() throws JMSException {
 // Reset the mock
    EasyMock.reset(tagManagerMock, deviceCacheMock, dataTagCacheMock, commandManagerMock);
    reset(requestHandlerMock);

    List<TransferDevice> devicesReturnList = new ArrayList<TransferDevice>();
    final TransferDeviceImpl transferDevice = new TransferDeviceImpl(1000L, "test_device_1", 1L, "test_device_class");
    transferDevice.addDeviceProperty(new DeviceProperty(1L, "TEST_PROPERTY_1", "100430", "tagId", null));
    devicesReturnList.add(transferDevice);

    DeviceInfo known = new DeviceInfo("test_device_class", "test_device_1");
    DeviceInfo unknown = new DeviceInfo("test_device_class", "unknown_device");
    HashSet<DeviceInfo> infoList = new HashSet<>(Arrays.asList(known, unknown));

    // Expect the device manager to retrieve the devices
    expect(requestHandlerMock.getDevices(EasyMock.<Set<DeviceInfo>> anyObject())).andReturn(devicesReturnList);
    // Expect the device manager to add the device to the cache
    final Capture<Device> capturedDevice = new Capture<>();
    deviceCacheMock.add(EasyMock.capture(capturedDevice));
    EasyMock.expectLastCall();

    final TestDeviceUpdateListener listener = new TestDeviceUpdateListener();
    // This time we want to make sure that onDevicesNotFound() is also called
    listener.setLatch(2);

    // Expect the device manager to subscribe to the tags
    tagManagerMock.subscribeDataTags(EasyMock.<Set<Long>> anyObject(), EasyMock.<DataTagListener> anyObject());
    EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
      @Override
      public Boolean answer() throws Throwable {
        deviceManager.onInitialUpdate(Arrays.asList((ClientDataTagValue) new ClientDataTagImpl(100430L)));
        return true;
      }
    }).once();

    // Expect the device manager to get the devices from the cache - once to
    // call onInitialUpdate() and once to call onUpdate()
    EasyMock.expect(deviceCacheMock.getAllDevices()).andAnswer(new IAnswer<List<Device>>() {
      @Override
      public List<Device> answer() throws Throwable {
        return Arrays.asList(capturedDevice.getValue());
      }
    }).times(2);

    // Setup is finished, need to activate the mock
    EasyMock.replay(tagManagerMock, deviceCacheMock, dataTagCacheMock, commandManagerMock);
    replay(requestHandlerMock);

    // Run the actual code to be tested
    deviceManager.subscribeDevices(infoList, listener);

    // Simulate the tag update calls
    new Thread(new Runnable() {
      @Override
      public void run() {
        deviceManager.onUpdate(new ClientDataTagImpl(100430L));
      }
    }).start();

    listener.await(5000L);
    Device device2 = listener.getDevice();
    Assert.assertNotNull(device2);
    Assert.assertTrue(device2.getId() == listener.getDevice().getId());

    Assert.assertTrue(listener.getDevice().getProperty("TEST_PROPERTY_1").getTag().getId().equals(100430L));

    Assert.assertTrue(listener.getUnknownDevices().size() == 1);

    // Verify that everything happened as expected
    EasyMock.verify(tagManagerMock, deviceCacheMock, dataTagCacheMock, commandManagerMock);
    verify(requestHandlerMock);
  }

//  @Test
//  public void testPropertyUpdate() throws RuleFormatException {
//    // Reset the mock
//    EasyMock.reset(tagManagerMock, deviceCacheMock, dataTagCacheMock, commandManagerMock);
//    reset(requestHandlerMock);
//
//    DeviceImpl device = new DeviceImpl(1000L, "test_device", 1L, "test_device_class", tagManagerMock, commandManagerMock);
//    ClientDataTagImpl cdt1 = new ClientDataTagImpl(100000L);
//    cdt1.update(DeviceTestUtils.createValidTransferTag(cdt1.getId(), "test_tag_name", 1L));
//
//    Map<String, ClientDataTagValue> deviceProperties = new HashMap<String, ClientDataTagValue>();
//    deviceProperties.put("test_property_name", cdt1);
//    device.setDeviceProperties(deviceProperties);
//
//    // Update the tag value
//    ClientDataTagImpl cdt2 = new ClientDataTagImpl(100000L);
//    cdt2.update(DeviceTestUtils.createValidTransferTag(cdt1.getId(), cdt1.getName(), 2L));
//    deviceManager.onUpdate(cdt2);
//
//    // Check that the device stored the new update properly
//    Assert.assertTrue(((Long) device.getProperty("test_property_name").getTag().getValue()) == 2L);
//  }

  @Test
  public void testUnsubscribeDevices() {
    // Reset the mock
    EasyMock.reset(tagManagerMock, deviceCacheMock, dataTagCacheMock);

    ClientDataTagImpl cdt1 = new ClientDataTagImpl(100000L);
    ClientDataTagImpl cdt2 = new ClientDataTagImpl(200000L);

    final Map<String, ClientDataTagValue> deviceProperties = new HashMap<String, ClientDataTagValue>();
    deviceProperties.put("test_property_name_1", cdt1);
    deviceProperties.put("test_property_name_2", cdt2);

    final DeviceImpl device1 = new DeviceImpl(1L, "test_device", 1L, "test_device_class", tagManagerMock, commandManagerMock);
    device1.setDeviceProperties(deviceProperties);

    final Map<Long, ClientDataTagValue> cacheReturnMap = getCacheReturnMap();

    // Expect the tag manager to subscribe to the tags
    tagManagerMock.subscribeDataTags(EasyMock.<Set<Long>> anyObject(), EasyMock.<DataTagListener> anyObject());
    EasyMock.expectLastCall().andAnswer(new IAnswer<Boolean>() {
      @Override
      public Boolean answer() throws Throwable {
        deviceManager.onInitialUpdate(cacheReturnMap.values());
        return true;
      }
    }).times(2);

    // Expect the device manager to get the devices from the cache - once to
    // call onInitialUpdate() and once to call onUpdate()
    EasyMock.expect(deviceCacheMock.getAllDevices()).andReturn(Arrays.asList((Device) device1)).times(2);

    // Expect the device manager to unsubscribe the tags
    tagManagerMock.unsubscribeDataTags(EasyMock.<Set<Long>> anyObject(), EasyMock.<DataTagListener> anyObject());
    EasyMock.expectLastCall().times(2);
    // Expect the device to be removed from the cache
    deviceCacheMock.remove(EasyMock.<Device> anyObject());
    EasyMock.expectLastCall().once();

    // Setup is finished, need to activate the mock
    EasyMock.replay(tagManagerMock, deviceCacheMock, dataTagCacheMock);

    TestDeviceUpdateListener listener1 = new TestDeviceUpdateListener();
    TestDeviceUpdateListener listener2 = new TestDeviceUpdateListener();

    Set<Device> devices = new HashSet<Device>();
    devices.add(device1);

    // Subscribe multiple listeners
    deviceManager.subscribeDevices(devices, listener1);
    deviceManager.subscribeDevices(devices, listener2);

    // Remove a listener
    deviceManager.unsubscribeDevices(devices, listener1);
    Assert.assertFalse(deviceManager.getDeviceUpdateListeners().contains(listener1));
    Assert.assertTrue(deviceManager.getDeviceUpdateListeners().contains(listener2));
    Assert.assertTrue(deviceManager.isSubscribed(device1));

    // Remove another listener
    deviceManager.unsubscribeDevices(devices, listener2);
    Assert.assertFalse(deviceManager.getDeviceUpdateListeners().contains(listener2));
    Assert.assertFalse(deviceManager.isSubscribed(device1));

    // Verify that everything happened as expected
    EasyMock.verify(tagManagerMock, deviceCacheMock, dataTagCacheMock);
  }

  @Test
  public void testUnsubscribeAllDevices() {
    // Reset the mock
    EasyMock.reset(tagManagerMock, deviceCacheMock, dataTagCacheMock);
    System.out.println("testUnsubscribeAllDevices");

    final ClientDataTagImpl cdt1 = new ClientDataTagImpl(100000L);
    final ClientDataTagImpl cdt2 = new ClientDataTagImpl(200000L);
    Map<String, ClientDataTagValue> deviceProperties1 = new HashMap<String, ClientDataTagValue>();
    Map<String, ClientDataTagValue> deviceProperties2 = new HashMap<String, ClientDataTagValue>();
    deviceProperties1.put("test_property_name_1", cdt1);
    deviceProperties2.put("test_property_name_2", cdt2);

    final DeviceImpl device1 = new DeviceImpl(1L, "test_device_1", 1L, "test_device_class", tagManagerMock, commandManagerMock);
    final DeviceImpl device2 = new DeviceImpl(2L, "test_device_2", 1L, "test_device_class", tagManagerMock, commandManagerMock);
    device1.setDeviceProperties(deviceProperties1);
    device2.setDeviceProperties(deviceProperties2);

    Set<Device> devices = new HashSet<Device>();
    devices.add(device1);
    devices.add(device2);

    Map<Long, ClientDataTagValue> cacheReturnMap = getCacheReturnMap();

    // Expect the tag manager to subscribe to the tags
    tagManagerMock.subscribeDataTags(EasyMock.<Set<Long>> anyObject(), EasyMock.<DataTagListener> anyObject());
    EasyMock.expectLastCall().andAnswer(new IAnswer<Boolean>() {
      @Override
      public Boolean answer() throws Throwable {
        final Set<Long> tagIds = (Set<Long>) EasyMock.getCurrentArguments()[0];
        // Simulate the tag update calls
        if (tagIds.contains(cdt1.getId()))
          deviceManager.onUpdate(cdt1);
        else if (tagIds.contains(cdt2.getId()))
          deviceManager.onUpdate(cdt2);
        return true;
      }
    }).times(1);

    // Expect the device manager to get the devices from the cache - once to
    // call onInitialUpdate() and once to call onUpdate()
    EasyMock.expect(deviceCacheMock.getAllDevices()).andReturn(Arrays.asList((Device) device1)).times(3);

    // Expect the device manager to get all cached devices before unsubscribing
    EasyMock.expect(deviceCacheMock.getAllDevices()).andReturn(new ArrayList<Device>(devices)).once();
    // Expect the device manager to unsubscribe the tags
    tagManagerMock.unsubscribeDataTags(EasyMock.<Set<Long>> anyObject(), EasyMock.<DataTagListener> anyObject());
    EasyMock.expectLastCall().times(1);
    // Expect the devices to be removed from the cache
    deviceCacheMock.remove(EasyMock.<Device> anyObject());
    EasyMock.expectLastCall().times(2);

    // Setup is finished, need to activate the mock
    EasyMock.replay(tagManagerMock, deviceCacheMock, dataTagCacheMock);

    TestDeviceUpdateListener listener = new TestDeviceUpdateListener();
    deviceManager.subscribeDevices(devices, listener);

    // Update a property
    deviceManager.onUpdate(cacheReturnMap.get(100000L));
    deviceManager.onUpdate(cacheReturnMap.get(200000L));

    deviceManager.unsubscribeAllDevices(listener);

    Assert.assertFalse(deviceManager.getDeviceUpdateListeners().contains(listener));
    Assert.assertFalse(deviceManager.getDeviceUpdateListeners().contains(listener));

    // Verify that everything happened as expected
    EasyMock.verify(tagManagerMock, deviceCacheMock, dataTagCacheMock);
  }

  class TestDeviceUpdateListener implements DeviceInfoUpdateListener {

    CountDownLatch latch = new CountDownLatch(1);
    List<Device> devices;
    List<DeviceInfo> unknownDevices;
    PropertyInfo propertyInfo;

    @Override
    public void onInitialUpdate(List<Device> devices) {
      LOG.info("onInitialUpdate()");
      this.devices = devices;
      Assert.assertTrue(areAllPropertiesAndFieldsSubscribed(devices));
    }

    @Override
    public void onUpdate(Device device, PropertyInfo propertyInfo) {
      LOG.info("onUpdate()");
      this.propertyInfo = propertyInfo;
      latch.countDown();
    }

    @Override
    public void onDevicesNotFound(List<DeviceInfo> unknownDevices) {
      this.unknownDevices = unknownDevices;
      LOG.info("onDevicesNotFound()");
      latch.countDown();
    }

    public void await(Long timeout) {
      try {
        latch.await(timeout, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    public Device getDevice() {
      return devices.get(0);
    }

    public List<Device> getDevices() {
      return devices;
    }

    public List<DeviceInfo> getUnknownDevices() {
      return unknownDevices;
    }

    public void setLatch(int count) {
      latch = new CountDownLatch(count);
    }
  }

  private Map<Long, ClientDataTagValue> getCacheReturnMap() {
    Map<Long, ClientDataTagValue> cacheReturnMap = new HashMap<>();
    ClientDataTagImpl cdt1 = new ClientDataTagImpl(100000L);
    ClientDataTagImpl cdt2 = new ClientDataTagImpl(200000L);

    TagUpdate tu1 = DeviceTestUtils.createValidTransferTag(cdt1.getId(), "test_tag_name_1");
    TagUpdate tu2 = DeviceTestUtils.createValidTransferTag(cdt2.getId(), "test_tag_name_2");

    try {
      cdt1.update(tu1);
      cdt2.update(tu2);
    } catch (RuleFormatException e1) {
      e1.printStackTrace();
    }

    cacheReturnMap.put(cdt1.getId(), cdt1);
    cacheReturnMap.put(cdt2.getId(), cdt2);
    return cacheReturnMap;
  }

  private boolean areAllPropertiesAndFieldsSubscribed(List<Device> devices) {
    for (Device device : devices) {
      Map<String, Property> properties = ((DeviceImpl) device).getDeviceProperties();
      for (Property property : properties.values()) {
        if (((PropertyImpl) property).isDataTag()) {
          if (!((PropertyImpl) property).isValueLoaded()) {
            return false;
          }
        }

        if (((PropertyImpl) property).isMappedProperty()) {
          for (Field field : property.getFields()) {
            if (((FieldImpl) field).isDataTag()) {
              if (!((FieldImpl) field).isValueLoaded()) {
                return false;
              }
            }
          }
        }
      }
    }

    return true;
  }
}
