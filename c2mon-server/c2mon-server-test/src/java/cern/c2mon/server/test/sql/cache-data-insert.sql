-- control tags needed for cache module tests
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1200, 'P_TEST_PROCESS:STATUS', 0,'String', 1, 1, 1, 'UNINITIALISED');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1205, 'P_TEST_EQUIPMENT:STATUS', 0,'String', 1, 1, 1, 'UNINITIALISED');
-- control tags for Process and Equipment TESTHANDLER03
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1220, 'P_TESTHANDLER03:STATUS', 0,'String', 1, 1, 1, 'UNINITIALISED');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1221, 'P_TESTHANDLER03:ALIVE', 0,'Integer', 1, 0, 1, 'UNINITIALISED');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1222, 'E_TESTHANDLER_TESTHANDLER03:STATUS', 0,'String', 1, 1, 1, 'UNINITIALISED');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1223, 'E_TESTHANDLER_TESTHANDLER03:COMM_FAULT', 0,'Integer', 1, 1, 1, 'UNINITIALISED');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1224, 'E_TESTHANDLER_TESTHANDLER03:ALIVE', 0,'Integer', 1, 0, 1, 'UNINITIALISED');
-- control tags for process P_TESTHANDLER04
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1260, 'P_TESTHANDLER04:STATUS', 0,'String', 1, 1, 1, 'UNINITIALISED');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1261, 'P_TESTHANDLER04:ALIVE', 0,'Integer', 1, 0, 1, 'UNINITIALISED');
-- control tags for equipment E_TESTHANDLER04
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1262, 'E_TESTHANDLER_TESTHANDLER04:STATUS', 0,'String', 1, 1, 1, 'UNINITIALISED');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1263, 'E_TESTHANDLER_TESTHANDLER04:COMM_FAULT', 0,'Integer', 1, 1, 1, 'UNINITIALISED');
-- control tags for equipment E_TEST_2
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1240, 'E_TEST_2:STATUS', 0,'String', 1, 1, 1, 'UNINITIALISED');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1241, 'E_TEST_2:COMM_FAULT', 0,'Integer', 1, 1, 1, 'UNINITIALISED');
-- control tags ready for subequipment
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1230, 'SUB_E_TESTHANDLER_TESTHANDLER03:STATUS', 0,'String', 1, 1, 1, 'UNINITIALISED');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1231, 'SUB_E_TESTHANDLER_TESTHANDLER03:ALIVE', 0,'Integer', 1, 0, 1, 'UNINITIALISED');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1232, 'SUB_E_TESTHANDLER_TESTHANDLER03:COMM_FAULT', 0,'Integer', 1, 1, 1, 'UNINITIALISED');
-- control tags not associated to Equipment but used to test config loading (& mapper tests)
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1250, 'E_CONFIG_TEST:STATUS', 0,'String', 1, 1, 1, 'UNINITIALISED');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1251, 'E_CONFIG_TEST:ALIVE', 0,'Integer', 1, 0, 1, 'UNINITIALISED');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (1252, 'E_CONFIG_TEST:COMM_FAULT', 0,'Integer', 1, 1, 1, 'UNINITIALISED');
-- process
INSERT INTO PROCESS (PROCID, PROCNAME, PROCDESC, PROCSTATE_TAGID, PROCALIVE_TAGID, PROCALIVEINTERVAL, PROCMAXMSGSIZE, PROCMAXMSGDELAY, PROCSTATE)
VALUES (50,'P_TESTHANDLER03','PROCESS TESTHANDLER03',1220,1221,60000,100,300,'DOWN');
INSERT INTO PROCESS (PROCID, PROCNAME, PROCDESC, PROCSTATE_TAGID, PROCALIVE_TAGID, PROCALIVEINTERVAL, PROCMAXMSGSIZE, PROCMAXMSGDELAY, PROCSTATE)
VALUES (51,'P_TESTHANDLER04','PROCESS TESTHANDLER04',1260,1261,60000,100,300,'DOWN');
-- equipment
INSERT INTO EQUIPMENT (EQID, EQNAME, EQDESC, EQHANDLERCLASS, EQSTATE_TAGID, EQALIVE_TAGID, EQALIVEINTERVAL, EQCOMMFAULT_TAGID, EQ_PROCID, EQADDRESS)
VALUES (150, 'E_TESTHANDLER_TESTHANDLER03', 'TESTHANDLER03 EQUIPMENT', 'cern.c2mon.daq.testhandler.TestMessageHandler', 1222, 1224, 60000, 1223, 50, 'interval=1000;eventProb=0.02;inRangeProb=1;outDeadBandProb=1;switchProb=1;startIn=0.2;aliveInterval=30000');
INSERT INTO EQUIPMENT (EQID, EQNAME, EQDESC, EQHANDLERCLASS, EQSTATE_TAGID, EQALIVEINTERVAL, EQCOMMFAULT_TAGID, EQ_PROCID, EQADDRESS)
VALUES (170, 'E_TESTHANDLER_TESTHANDLER04', 'TESTHANDLER04 EQUIPMENT', 'cern.c2mon.daq.testhandler.TestMessageHandler', 1262, 60000, 1263, 51, 'interval=1000;eventProb=0.02;inRangeProb=1;outDeadBandProb=1;switchProb=1;startIn=0.2;aliveInterval=30000');
INSERT INTO EQUIPMENT (EQID, EQNAME, EQDESC, EQHANDLERCLASS, EQSTATE_TAGID, EQALIVEINTERVAL, EQCOMMFAULT_TAGID, EQ_PROCID, EQADDRESS)
VALUES (160, 'E_TEST_2', 'E_TEST_2 EQUIPMENT', 'cern.c2mon.daq.testhandler.TestMessageHandler', 1240, 60000, 1241, 50, 'interval=1000;eventProb=0.02;inRangeProb=1;outDeadBandProb=1;switchProb=1;startIn=0.2;aliveInterval=30000');
-- subequipment
INSERT INTO EQUIPMENT (EQID, EQNAME, EQDESC, EQHANDLERCLASS, EQSTATE_TAGID, EQALIVE_TAGID, EQALIVEINTERVAL, EQCOMMFAULT_TAGID, EQ_PARENT_ID)
VALUES (250, 'SUB_E_TESTHANDLER_TESTHANDLER03', 'TESTHANDLER03 SUBEQUIPMENT', '-', 1230, 1231, 30000, 1232, 150);
-- tags
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAG_EQID, TAGRULEIDS, TAGADDRESS)
VALUES (200000, 'sys.proc.serverinit', 0, 'Integer', 0, 1, 1, 'UNINITIALISED',150,NULL,'<DataTagAddress><HardwareAddress class="cern.c2mon.shared.common.datatag.address.impl.JAPCHardwareAddressImpl"><protocol>yami</protocol><service>yami</service><device-name>TEST.CLIC.DIAMON.1</device-name><property-name>Acquisition</property-name><data-field-name>sys.proc.serverinit</data-field-name><column-index>-1</column-index><row-index>-1</row-index></HardwareAddress><time-to-live>3600000</time-to-live><priority>2</priority><guaranteed-delivery>false</guaranteed-delivery></DataTagAddress>');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAG_EQID, TAGRULEIDS, TAGADDRESS)
VALUES (200001, 'sys.mem.inactpct', 0, 'Integer', 0, 1, 1, 'UNINITIALISED',150,'60000,60001,60002,60003,60004,60005,60006','<DataTagAddress><HardwareAddress class="cern.c2mon.shared.common.datatag.address.impl.JAPCHardwareAddressImpl"><protocol>yami</protocol><service>yami</service><device-name>TEST.CLIC.DIAMON.1</device-name><property-name>Acquisition</property-name><data-field-name>sys.mem.inactpct</data-field-name><column-index>-1</column-index><row-index>-1</row-index></HardwareAddress><time-to-live>3600000</time-to-live><priority>2</priority><guaranteed-delivery>false</guaranteed-delivery></DataTagAddress>');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAG_EQID, TAGRULEIDS, TAGADDRESS)
VALUES (200002, 'sys.loadavg', 0, 'Integer', 0, 1, 1, 'UNINITIALISED',150,'60000,60001,60002,60003,60004,60005,60006,60007,60008,60009','<DataTagAddress><HardwareAddress class="cern.c2mon.shared.common.datatag.address.impl.JAPCHardwareAddressImpl"><protocol>yami</protocol><service>yami</service><device-name>TEST.CLIC.DIAMON.1</device-name><property-name>Acquisition</property-name><data-field-name>sys.loadavg</data-field-name><column-index>-1</column-index><row-index>-1</row-index></HardwareAddress><time-to-live>3600000</time-to-live><priority>2</priority><guaranteed-delivery>false</guaranteed-delivery></DataTagAddress>');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAG_EQID, TAGRULEIDS, TAGADDRESS)
VALUES (200003, 'tag_200003', 0, 'Integer', 0, 1, 1, 'UNINITIALISED',150,'60000,60001,60002,60003,60004,60005,60006,60007,60008,60009','<DataTagAddress><HardwareAddress class="cern.c2mon.shared.common.datatag.address.impl.JAPCHardwareAddressImpl"><protocol>yami</protocol><service>yami</service><device-name>TEST.CLIC.DIAMON.1</device-name><property-name>Acquisition</property-name><data-field-name>sys.mem.inactpct</data-field-name><column-index>-1</column-index><row-index>-1</row-index></HardwareAddress><time-to-live>3600000</time-to-live><priority>2</priority><guaranteed-delivery>false</guaranteed-delivery></DataTagAddress>');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAG_EQID, TAGRULEIDS, TAGADDRESS)
VALUES (200004, 'tag_200004', 0, 'Integer', 0, 1, 1, 'UNINITIALISED',150,NULL,'<DataTagAddress><HardwareAddress class="cern.c2mon.shared.common.datatag.address.impl.JAPCHardwareAddressImpl"><protocol>yami</protocol><service>yami</service><device-name>TEST.CLIC.DIAMON.1</device-name><property-name>Acquisition</property-name><data-field-name>sys.mem.inactpct</data-field-name><column-index>-1</column-index><row-index>-1</row-index></HardwareAddress><time-to-live>3600000</time-to-live><priority>2</priority><guaranteed-delivery>false</guaranteed-delivery></DataTagAddress>');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAG_EQID, TAGRULEIDS, TAGADDRESS)
VALUES (200005, 'tag_200005', 0, 'Integer', 0, 1, 1, 'UNINITIALISED',150,NULL,'<DataTagAddress><HardwareAddress class="cern.c2mon.shared.common.datatag.address.impl.JAPCHardwareAddressImpl"><protocol>yami</protocol><service>yami</service><device-name>TEST.CLIC.DIAMON.1</device-name><property-name>Acquisition</property-name><data-field-name>sys.mem.inactpct</data-field-name><column-index>-1</column-index><row-index>-1</row-index></HardwareAddress><time-to-live>3600000</time-to-live><priority>2</priority><guaranteed-delivery>false</guaranteed-delivery></DataTagAddress>');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAG_EQID, TAGRULEIDS, TAGADDRESS)
VALUES (200010, 'tag_200010', 0, 'Integer', 0, 1, 1, 'UNINITIALISED',170,'60011,60012','<DataTagAddress><HardwareAddress class="cern.c2mon.shared.common.datatag.address.impl.JAPCHardwareAddressImpl"><protocol>yami</protocol><service>yami</service><device-name>TEST.CLIC.DIAMON.1</device-name><property-name>Acquisition</property-name><data-field-name>sys.mem.inactpct</data-field-name><column-index>-1</column-index><row-index>-1</row-index></HardwareAddress><time-to-live>3600000</time-to-live><priority>2</priority><guaranteed-delivery>false</guaranteed-delivery></DataTagAddress>');
-- tags attached to subequipment
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAG_EQID, TAGRULEIDS, TAGADDRESS)
VALUES (200011, 'tag_200011', 0, 'Integer', 0, 1, 1, 'UNINITIALISED',250,NULL,'<DataTagAddress><HardwareAddress class="cern.c2mon.shared.common.datatag.address.impl.JAPCHardwareAddressImpl"><protocol>yami</protocol><service>yami</service><device-name>TEST.CLIC.DIAMON.1</device-name><property-name>Acquisition</property-name><data-field-name>sys.mem.inactpct</data-field-name><column-index>-1</column-index><row-index>-1</row-index></HardwareAddress><time-to-live>3600000</time-to-live><priority>2</priority><guaranteed-delivery>false</guaranteed-delivery></DataTagAddress>');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAG_EQID, TAGRULEIDS, TAGADDRESS)
VALUES (200012, 'tag_200012', 0, 'Integer', 0, 1, 1, 'UNINITIALISED',250,NULL,'<DataTagAddress><HardwareAddress class="cern.c2mon.shared.common.datatag.address.impl.JAPCHardwareAddressImpl"><protocol>yami</protocol><service>yami</service><device-name>TEST.CLIC.DIAMON.1</device-name><property-name>Acquisition</property-name><data-field-name>sys.mem.inactpct</data-field-name><column-index>-1</column-index><row-index>-1</row-index></HardwareAddress><time-to-live>3600000</time-to-live><priority>2</priority><guaranteed-delivery>false</guaranteed-delivery></DataTagAddress>');
-- rules
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE, TAGRULEIDS)
VALUES (60000, 'DIAMON_CLIC_CS-CCR-DEV3', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#200001 > 0)|(#200002 < 200)|(#200003 > 450)[2],(#200002 < 500)|(#200003 > 400)[1],true[0]', '59999');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE, TAGRULEIDS)
VALUES (60001, 'DIAMON_CLIC_CS-CCR-DEV4', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#200001 > 0)|(#200002 < 200)|(#200003 > 450)[2],(#200002 < 500)|(#200003 > 400)[1],true[0]', '60008,60009,60010,60011');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE)
VALUES (60002, 'DIAMON_CLIC_CS-CCR-DEV5', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#200001 > 0)|(#200002 < 200)|(#200003 > 450)[2],(#200002 < 500)|(#200003 > 400)[1],true[0]');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE, TAGRULEIDS)
VALUES (60003, 'DIAMON_CLIC_CS-CCR-DEV6', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#200001 > 0)|(#200002 < 200)|(#200003 > 450)[2],(#200002 < 500)|(#200003 > 400)[1],true[0]', '60007,60008,60009,60010');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE, TAGRULEIDS)
VALUES (60004, 'DIAMON_CLIC_CS-CCR-DEV7', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#200001 > 0)|(#200002 < 200)|(#200003 > 450)[2],(#200002 < 500)|(#200003 > 400)[1],true[0]', '60007,60008,60011');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE)
VALUES (60005, 'DIAMON_CLIC_CS-CCR-DEV8', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#200001 > 0)|(#200002 < 200)|(#200003 > 450)[2],(#200002 < 500)|(#200003 > 400)[1],true[0]');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE, TAGRULEIDS)
VALUES (60006, 'DIAMON_CLIC_CS-CCR-DEV9', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#200001 > 0)|(#200002 < 200)|(#200003 > 450)[2],(#200002 < 500)|(#200003 > 400)[1],true[0]', '60007');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE, TAGRULEIDS)
VALUES (60007, 'DIAMON_CLIC_CS-CCR-DEV10', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#60006 > 0)|(#200002 < 200)|(#60004 > 450)[2],(#60003 < 500)|(#200003 > 400)[1],true[0]', '60009,60010,60011');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE, TAGRULEIDS)
VALUES (60008, 'DIAMON_CLIC_CS-CCR-DEV11', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#60001 > 0)|(#200002 < 200)|(#60004 > 450)[2],(#60003 < 500)|(#200003 > 400)[1],true[0]', '60010,60011');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE)
VALUES (60009, 'DIAMON_CLIC_CS-CCR-DEV12', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#60001 > 0)|(#200002 < 200)|(#60004 > 450)[2],(#60007 < 500)|(#200003 > 400)[1],true[0]');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE)
VALUES (60010, 'DIAMON_CLIC_CS-CCR-DEV13', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#60008> 0)|(#60007 < 200)|(#60004 > 450)[2],(#60007 < 500)|(#60001 > 400)[1],true[0]');
--depends on 2 processes and 2 equipments
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE)
VALUES (60011, 'RULE_WITH_MULTIPLE_PARENTS', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#60008> 0)|(#60007 < 200)|(#60004 > 450)[2],(#60007 < 500)|(#60001 > 400)|(#200010 > 300)[1],true[0]');
-- new rules for parent id loading test
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE, TAGRULEIDS)
VALUES (60012, 'RULE_ON_EQUIP_170', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#200010 > 0)[0]', '59999');
-- use id to get to start in SQL query
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC, TAGRULE)
VALUES (59999, 'RULE_ON_EQUIP_150_170', 0, 'Integer', 0, 1, 1, 'UNINITIALISED','(#60000 > 0)|(#60012 < 200)[1]');
--alarms
insert into alarm (ALARMID, ALARM_TAGID, ALARMFFAMILY, ALARMFMEMBER, ALARMFCODE, ALARMSTATE, ALARMTIME, ALARMINFO, ALARMCONDITION)
values (350000, 60000, 'TEST_FAMILY', 'TEST_MEMBER', '20', 'TERMINATE', NULL, 'TEST_INFO', '<AlarmCondition class="cern.c2mon.server.common.alarm.ValueAlarmCondition"><alarm-value type="Integer">33</alarm-value></AlarmCondition>');
insert into alarm (ALARMID, ALARM_TAGID, ALARMFFAMILY, ALARMFMEMBER, ALARMFCODE, ALARMSTATE, ALARMTIME, ALARMINFO, ALARMCONDITION)
values (350001, 60000, 'TEST_FAMILY', 'TEST_MEMBER', '20', 'TERMINATE', NULL, 'TEST_INFO', '<AlarmCondition class="cern.c2mon.server.common.alarm.ValueAlarmCondition"><alarm-value type="Integer">33</alarm-value></AlarmCondition>');
INSERT INTO COMMANDTAG(CMDID, CMDNAME, CMDDESC, CMDMODE, CMDDATATYPE, CMDSOURCERETRIES, CMDSOURCETIMEOUT, CMDEXECTIMEOUT, CMDCLIENTTIMEOUT, CMDHARDWAREADDRESS, CMDMINVALUE, CMDMAXVALUE, CMD_EQID, CMDRBACCLASS, CMDRBACDEVICE, CMDRBACPROPERTY)
VALUES (11000, 'CMD_11000', 'CMD_11000_DESC', 0, 'Integer', 1, 1000, 1000, 5000, '<HardwareAddress class="cern.c2mon.shared.common.datatag.address.impl.OPCHardwareAddressImpl"><opc-item-name>CDE_N2_ARR_EJP_UAPE_X93X94</opc-item-name><command-pulse-length>100</command-pulse-length></HardwareAddress>',NULL,NULL,150,'TIM_COMMANDS','TIM_CMD_WATER_MEY','WRITE');
INSERT INTO COMMANDTAG(CMDID, CMDNAME, CMDDESC, CMDMODE, CMDDATATYPE, CMDSOURCERETRIES, CMDSOURCETIMEOUT, CMDEXECTIMEOUT, CMDCLIENTTIMEOUT, CMDHARDWAREADDRESS, CMDMINVALUE, CMDMAXVALUE, CMD_EQID, CMDRBACCLASS, CMDRBACDEVICE, CMDRBACPROPERTY)
VALUES (11001, 'CMD_11001', 'CMD_11001_DESC', 0, 'Integer', 1, 1000, 1000, 5000, '<HardwareAddress class="cern.c2mon.shared.common.datatag.address.impl.OPCHardwareAddressImpl"><opc-item-name>CDE_N2_ARR_EJP_UAPE_X93X94</opc-item-name><command-pulse-length>100</command-pulse-length></HardwareAddress>',NULL,NULL,150,'TIM_COMMANDS','TIM_CMD_TYPE2','WRITE');
-- tags for device properties
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (210000, 'D_PROPERTY_TEST_1', 0, 'Integer', 0, 1, 1, 'UNINITIALISED');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (210001, 'D_PROPERTY_TEST_2', 0, 'Integer', 0, 1, 1, 'UNINITIALISED');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (210002, 'D_PROPERTY_TEST_3', 0, 'Integer', 0, 1, 1, 'UNINITIALISED');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (210003, 'D_PROPERTY_TEST_4', 0, 'Integer', 0, 1, 1, 'UNINITIALISED');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (210008, 'D_PROPERTY_TEST_5', 0, 'Integer', 0, 1, 1, 'UNINITIALISED');
-- tags for device commands
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (210004, 'D_COMMAND_TEST_1', 0,'Integer', 1, 1, 1, 'UNINITIALISED');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (210005, 'D_COMMAND_TEST_2', 0,'Integer', 1, 1, 1, 'UNINITIALISED');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (210006, 'D_COMMAND_TEST_3', 0,'Integer', 1, 1, 1, 'UNINITIALISED');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (210007, 'D_COMMAND_TEST_4', 0,'Integer', 1, 1, 1, 'UNINITIALISED');
-- tags for device fields
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (210009, 'D_FIELD_TEST_1', 0, 'Integer', 0, 1, 1, 'UNINITIALISED');
INSERT INTO DATATAG (TAGID, TAGNAME, TAGMODE, TAGDATATYPE, TAGCONTROLTAG, TAGLOGGED, TAGQUALITYCODE, TAGQUALITYDESC)
VALUES (2100010, 'D_FIELD_TEST_2', 0, 'Integer', 0, 1, 1, 'UNINITIALISED');
-- device classes
INSERT INTO DEVICECLASS (DEVCLASSID, DEVCLASSNAME, DEVCLASSDESC)
VALUES (400, 'TEST_DEVICE_CLASS_1', 'Description of TEST_DEVICE_CLASS_1');
INSERT INTO DEVICECLASS (DEVCLASSID, DEVCLASSNAME, DEVCLASSDESC)
VALUES (401, 'TEST_DEVICE_CLASS_2', 'Description of TEST_DEVICE_CLASS_2');
-- properties
INSERT INTO PROPERTY (PROPID, PROPDEVCLASSID, PROPNAME, PROPDESC)
VALUES (1, 400, 'cpuLoadInPercent', 'The current CPU load in percent');
INSERT INTO PROPERTY (PROPID, PROPDEVCLASSID, PROPNAME, PROPDESC)
VALUES (2, 400, 'responsiblePerson', 'The person responsible for this device');
INSERT INTO PROPERTY (PROPID, PROPDEVCLASSID, PROPNAME, PROPDESC)
VALUES (3, 400, 'someCalculations', 'Some super awesome calculations');
INSERT INTO PROPERTY (PROPID, PROPDEVCLASSID, PROPNAME, PROPDESC)
VALUES (4, 400, 'numCores', 'The number of CPU cores on this device');
INSERT INTO PROPERTY (PROPID, PROPDEVCLASSID, PROPNAME, PROPDESC)
VALUES (5, 401, 'TEST_PROPERTY_1', 'Description of TEST_PROPERTY_1');
INSERT INTO PROPERTY (PROPID, PROPDEVCLASSID, PROPNAME, PROPDESC)
VALUES (6, 401, 'TEST_PROPERTY_2', 'Description of TEST_PROPERTY_2');
INSERT INTO PROPERTY (PROPID, PROPDEVCLASSID, PROPNAME, PROPDESC)
VALUES (7, 401, 'TEST_PROPERTY_3', 'Description of TEST_PROPERTY_3');
INSERT INTO PROPERTY (PROPID, PROPDEVCLASSID, PROPNAME, PROPDESC)
VALUES (8, 401, 'TEST_PROPERTY_4', 'Description of TEST_PROPERTY_4');
-- property with fields
INSERT INTO PROPERTY (PROPID, PROPDEVCLASSID, PROPNAME, PROPDESC)
VALUES (9, 400, 'TEST_PROPERTY_WITH_FIELDS', 'Description of TEST_PROPERTY_WITH_FIELDS');
-- commands
INSERT INTO COMMAND (CMDID, CMDDEVCLASSID, CMDNAME, CMDDESC)
VALUES (1, 400, 'TEST_COMMAND_1', 'Description of TEST_COMMAND_1');
INSERT INTO COMMAND (CMDID, CMDDEVCLASSID, CMDNAME, CMDDESC)
VALUES (2, 400, 'TEST_COMMAND_2', 'Description of TEST_COMMAND_2');
INSERT INTO COMMAND (CMDID, CMDDEVCLASSID, CMDNAME, CMDDESC)
VALUES (3, 401, 'TEST_COMMAND_3', 'Description of TEST_COMMAND_3');
INSERT INTO COMMAND (CMDID, CMDDEVCLASSID, CMDNAME, CMDDESC)
VALUES (4, 401, 'TEST_COMMAND_4', 'Description of TEST_COMMAND_4');
-- fields
INSERT INTO FIELD (FIELDID, FIELDNAME, FIELDPROPID)
VALUES (1, 'FIELD_CPULOAD', 9);
INSERT INTO FIELD (FIELDID, FIELDNAME, FIELDPROPID)
VALUES (2, 'FIELD_RESPONSIBLE_PERSON', 9);
INSERT INTO FIELD (FIELDID, FIELDNAME, FIELDPROPID)
VALUES (3, 'FIELD_SOME_CALCULATIONS', 9);
INSERT INTO FIELD (FIELDID, FIELDNAME, FIELDPROPID)
VALUES (4, 'FIELD_NUM_CORES', 9);
-- devices
INSERT INTO DEVICE (DEVID, DEVNAME, DEVCLASSID)
VALUES (300, 'TEST_DEVICE_1', 400);
INSERT INTO DEVICE (DEVID, DEVNAME, DEVCLASSID)
VALUES (301, 'TEST_DEVICE_2', 400);
INSERT INTO DEVICE (DEVID, DEVNAME, DEVCLASSID)
VALUES (302, 'TEST_DEVICE_3', 401);
INSERT INTO DEVICE (DEVID, DEVNAME, DEVCLASSID)
VALUES (303, 'TEST_DEVICE_4', 401);
-- device properties
INSERT INTO DEVICEPROPERTY (DVPPROPID, DVPDEVID, DVPNAME, DVPVALUE, DVPCATEGORY, DVPRESULTTYPE)
VALUES (1, 300, 'cpuLoadInPercent', '210000', 'tagId', NULL);
INSERT INTO DEVICEPROPERTY (DVPPROPID, DVPDEVID, DVPNAME, DVPVALUE, DVPCATEGORY, DVPRESULTTYPE)
VALUES (2, 300, 'responsiblePerson', 'Mr. Administrator', 'constantValue', NULL);
INSERT INTO DEVICEPROPERTY (DVPPROPID, DVPDEVID, DVPNAME, DVPVALUE, DVPCATEGORY, DVPRESULTTYPE)
VALUES (3, 300, 'someCalculations', '(#123 + #234) / 2', 'clientRule', 'Float');
INSERT INTO DEVICEPROPERTY (DVPPROPID, DVPDEVID, DVPNAME, DVPVALUE, DVPCATEGORY, DVPRESULTTYPE)
VALUES (4, 300, 'numCores', '4', 'constantValue', 'Integer');
INSERT INTO DEVICEPROPERTY (DVPPROPID, DVPDEVID, DVPNAME, DVPVALUE, DVPCATEGORY, DVPRESULTTYPE)
VALUES (5, 301, 'TEST_PROPERTY_1', '210001', 'tagId', NULL);
INSERT INTO DEVICEPROPERTY (DVPPROPID, DVPDEVID, DVPNAME, DVPVALUE, DVPCATEGORY, DVPRESULTTYPE)
VALUES (6, 302, 'TEST_PROPERTY_2', '210002', 'tagId', NULL);
INSERT INTO DEVICEPROPERTY (DVPPROPID, DVPDEVID, DVPNAME, DVPVALUE, DVPCATEGORY, DVPRESULTTYPE)
VALUES (7, 303, 'TEST_PROPERTY_3', '210003', 'tagId', NULL);
-- device property with fields
INSERT INTO DEVICEPROPERTY (DVPPROPID, DVPDEVID, DVPNAME, DVPVALUE, DVPCATEGORY, DVPRESULTTYPE)
VALUES (9, 301, 'TEST_PROPERTY_WITH_FIELDS', null, 'mappedProperty', null);
-- device commands
INSERT INTO DEVICECOMMAND (DVCCMDID, DVCDEVID, DVCNAME, DVCVALUE, DVCCATEGORY, DVCRESULTTYPE)
VALUES (1, 300, 'TEST_COMMAND_1', '210004', 'commandTagId', null);
INSERT INTO DEVICECOMMAND (DVCCMDID, DVCDEVID, DVCNAME, DVCVALUE, DVCCATEGORY, DVCRESULTTYPE)
VALUES (2, 301, 'TEST_COMMAND_2', '210005', 'commandTagId', null);
INSERT INTO DEVICECOMMAND (DVCCMDID, DVCDEVID, DVCNAME, DVCVALUE, DVCCATEGORY, DVCRESULTTYPE)
VALUES (3, 302, 'TEST_COMMAND_3', '210006', 'commandTagId', null);
INSERT INTO DEVICECOMMAND (DVCCMDID, DVCDEVID, DVCNAME, DVCVALUE, DVCCATEGORY, DVCRESULTTYPE)
VALUES (4, 303, 'TEST_COMMAND_4', '210007', 'commandTagId', null);
-- property fields
INSERT INTO PROPERTYFIELD (PRFFIELDID, PRFFIELDNAME, PRFVALUE, PRFCATEGORY, PRFRESULTTYPE, PRFPROPID, PRFDEVID)
VALUES (1, 'FIELD_CPULOAD', '210008', 'tagId', null, 9, 301);
INSERT INTO PROPERTYFIELD (PRFFIELDID, PRFFIELDNAME, PRFVALUE, PRFCATEGORY, PRFRESULTTYPE, PRFPROPID, PRFDEVID)
VALUES (2, 'FIELD_RESPONSIBLE_PERSON', 'Mr Administrator', 'constantValue', null, 9, 301);
INSERT INTO PROPERTYFIELD (PRFFIELDID, PRFFIELDNAME, PRFVALUE, PRFCATEGORY, PRFRESULTTYPE, PRFPROPID, PRFDEVID)
VALUES (3, 'FIELD_SOME_CALCULATIONS', '(#123 + #234) / 2', 'clientRule', 'Float', 9, 301);
INSERT INTO PROPERTYFIELD (PRFFIELDID, PRFFIELDNAME, PRFVALUE, PRFCATEGORY, PRFRESULTTYPE, PRFPROPID, PRFDEVID)
VALUES (4, 'FIELD_NUM_CORES', '', 'tagId', null, 9, 301);