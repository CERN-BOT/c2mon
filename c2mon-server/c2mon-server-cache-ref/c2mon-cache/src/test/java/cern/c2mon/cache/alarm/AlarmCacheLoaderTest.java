package cern.c2mon.cache.alarm;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import cern.c2mon.cache.api.C2monCache;
import cern.c2mon.cache.junit.CachePopulationRule;
import cern.c2mon.server.cache.CacheModuleRef;
import cern.c2mon.server.cache.dbaccess.AlarmMapper;
import cern.c2mon.server.cache.dbaccess.config.CacheDbAccessModule;
import cern.c2mon.server.cache.loader.config.CacheLoaderModuleRef;
import cern.c2mon.server.common.alarm.Alarm;
import cern.c2mon.server.common.config.CommonModule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Szymon Halastra
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        CacheModuleRef.class,
        CommonModule.class,
        CacheLoaderModuleRef.class,
        CacheDbAccessModule.class,
        CachePopulationRule.class
}, loader = AnnotationConfigContextLoader.class)
public class AlarmCacheLoaderTest {

  @Rule
  @Autowired
  public CachePopulationRule cachePopulationRule;

  @Autowired
  private C2monCache<Long, Alarm> alarmCacheRef;

  @Autowired
  private AlarmMapper alarmMapper;

  @Test
  public void preloadCache() {
    assertNotNull("Alarm Cache should not be null", alarmCacheRef);

    List<Alarm> alarmList = alarmMapper.getAll();

    assertTrue("List of alarms should not be empty", alarmList.size() > 0);
  }
}
