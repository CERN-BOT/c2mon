package cern.c2mon.server.configuration.handler.transacted;

import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cern.c2mon.server.configuration.handler.AlarmConfigHandler;
import cern.c2mon.server.configuration.handler.RuleTagConfigHandler;
import cern.c2mon.server.configuration.handler.impl.TagConfigGateway;
import cern.c2mon.server.cache.RuleTagCache;
import cern.c2mon.server.cache.RuleTagFacade;
import cern.c2mon.server.cache.TagLocationService;
import cern.c2mon.server.cache.exception.CacheElementNotFoundException;
import cern.c2mon.server.cache.loading.RuleTagLoaderDAO;
import cern.c2mon.server.common.rule.RuleTag;
import cern.c2mon.shared.client.configuration.ConfigurationElement;
import cern.c2mon.shared.client.configuration.ConfigurationElementReport;
import cern.c2mon.shared.client.configuration.ConfigConstants.Action;
import cern.c2mon.shared.client.configuration.ConfigConstants.Entity;

/**
 * Implementation of transacted configuration methods.
 * @author Mark Brightwell
 *
 */
@Service
public class RuleTagConfigTransactedImpl extends TagConfigTransactedImpl<RuleTag> implements RuleTagConfigTransacted {

  /**
   * Class logger.
   */
  private static final Logger LOGGER = Logger.getLogger(RuleTagConfigTransactedImpl.class); 
  
  /**
   * Circular dependency between RuleTagConfigHandler
   * and TagConfigGateway, so autowire field.
   */
  @Autowired
  private TagConfigGateway tagConfigGateway;
  
  @Autowired
  private AlarmConfigHandler alarmConfigHandler;
  
  @Autowired
  private RuleTagConfigHandler ruleTagConfigHandler;
  
  private RuleTagFacade ruleTagFacade;

  @Autowired
  public RuleTagConfigTransactedImpl(RuleTagCache ruleTagCache,
      RuleTagFacade ruleTagFacade, RuleTagLoaderDAO ruleTagLoaderDAO, TagLocationService tagLocationService) {
    super(ruleTagLoaderDAO, ruleTagFacade, ruleTagCache, tagLocationService);
    this.ruleTagFacade = ruleTagFacade;
  }
  
  /**
   * Creates a rule on existing tags. 
   * 
   * <p>The DAQ does not need informing of this change (so no return
   * type as for DataTags.
   * 
   * @param element the details of the new object
   * @throws IllegalAccessException
   * @throws {@link UnexpectedRollbackException} if RuntimeException caught; DB transaction is rolled back and Rule & associated
   *                                                Tags are removed from cache 
   */
  @Override
  @Transactional(value = "cacheTransactionManager")
  public void doCreateRuleTag(ConfigurationElement element) throws IllegalAccessException {
    tagCache.acquireWriteLockOnKey(element.getEntityId());
    try {
      LOGGER.trace("Creating RuleTag with id " + element.getEntityId());
      checkId(element.getEntityId());
      RuleTag ruleTag = commonTagFacade.createCacheObject(element.getEntityId(), element.getElementProperties());
      Collection<Long> tagIds = ruleTag.getRuleInputTagIds();
      try {      
        configurableDAO.insert(ruleTag);
      } catch (Exception e) {
        LOGGER.error("Exception caught while inserting a new Rule into the DB - rolling back changes", e);
        throw new UnexpectedRollbackException("Unexpected exception while creating a Rule: rolling back the change", e);
      }
      try {
        for (Long tagId : tagIds) {      
          tagConfigGateway.addRuleToTag(tagId, ruleTag.getId()); 
        }
        tagCache.putQuiet(ruleTag); 
      } catch (RuntimeException e) {
        String errMessage = "Exception caught while adding a RuleTag - rolling back DB transaction.";
        LOGGER.error(errMessage, e);
        tagCache.remove(ruleTag.getId());
        for (Long tagId : tagIds) {      
          try {
            tagConfigGateway.removeRuleFromTag(tagId, ruleTag.getId());
          } catch (RuntimeException ex) {
            LOGGER.warn("Exception caught while attempting to role back rule creation in cache (removing references from input tags)", ex);
          }
        }
        throw new UnexpectedRollbackException(errMessage, e);
      }
    } finally {
      tagCache.releaseWriteLockOnKey(element.getEntityId());
    }
           
  }
  
  /**
   * Takes all the necessary actions when updating
   * the configuration of a rule tag (updating the cache
   * object and the database).
   * 
   * @param id the id of the rule that is being reconfigured
   * @param properties the properties of fields that have changed
   * @throws IllegalAccessException
   * @throw {@link UnexpectedRollbackException} if failure; 
   */
  @Override
  @Transactional(value = "cacheTransactionManager", propagation = Propagation.REQUIRES_NEW)
  public void doUpdateRuleTag(Long id, Properties properties) throws IllegalAccessException {
    LOGGER.trace("Updating RuleTag " + id);   
    tagCache.acquireWriteLockOnKey(id);
    try {
      RuleTag ruleTag = tagCache.get(id);
      Collection<Long> oldTagIds = null;
      //first record the old tag Ids before reconfiguring
      if (properties.containsKey("ruleText")) {
         oldTagIds = ruleTag.getRuleInputTagIds();
      }    
      try {            
        commonTagFacade.updateConfig(ruleTag, properties);      
        configurableDAO.updateConfig(ruleTag);      
      } catch (RuntimeException e) {
        String msg = "Exception caught while updating Rule";
        LOGGER.error(msg, e);
        throw new UnexpectedRollbackException(msg, e);      
      }
      try {
        //if successful so far, adjust associated Tags (remove all old, add all new)
        if (oldTagIds != null) {
          for (Long oldTagId : oldTagIds) {
            tagConfigGateway.removeRuleFromTag(oldTagId, ruleTag.getId());
          }
          for (Long newTagId : ruleTag.getRuleInputTagIds()) {
            tagConfigGateway.addRuleToTag(newTagId, ruleTag.getId());    
          }
        }
      } catch (RuntimeException e) {
        String errMessage = "Exception caught while updating a RuleTag in cache - rolling back DB transaction and removing from cache."; 
        LOGGER.error(errMessage, e); 
        //try to re-assign all references as they were
        if (oldTagIds != null) {
          for (Long oldTagId : oldTagIds) {
            try {
              tagConfigGateway.addRuleToTag(oldTagId, ruleTag.getId());           
            } catch (Exception ex) {
              LOGGER.warn("Exception caught while rolling back rule update", ex);
            }            
          }
          for (Long newTagId : ruleTag.getRuleInputTagIds()) {
            try {
              tagConfigGateway.removeRuleFromTag(newTagId, ruleTag.getId());              
            } catch (Exception ex) {
              LOGGER.warn("Exception caught while rolling back rule update", ex);
            }
          }
        } 
        throw new UnexpectedRollbackException(errMessage, e);
      }
      tagCache.releaseWriteLockOnKey(id);      
      //reset all parent DAQ/Equipment ids of rules higher up the pile - if fails, no rolling back possible, so rule cache may be left inconsistent
      try {
        LOGGER.trace("Resetting all relevant Rule parent Process/Equipment ids");
        for (Long parentRuleId : ruleTag.getRuleIds()) {
          ruleTagFacade.setParentSupervisionIds(parentRuleId);
        }
      } catch (Exception e) {
        String msg = "Exception while reloading rule parent ids: cache may be left in inconsistent state! - need to remove this rule to try and recover consistency";
        LOGGER.error(msg, e);
        throw new UnexpectedRollbackException(msg, e);
      }
    } finally {
      if (tagCache.isWriteLockedByCurrentThread(id)) {
        tagCache.releaseWriteLockOnKey(id);     
      }      
    }
  }
 
   
  @Override
  @Transactional(value = "cacheTransactionManager", propagation=Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
  public void doRemoveRuleTag(final Long id, final ConfigurationElementReport elementReport) {
    LOGGER.trace("Removing RuleTag " + id);
    try {
      RuleTag ruleTag = tagCache.get(id);
      Collection<Long> ruleIds = ruleTag.getCopyRuleIds();  
      if (!ruleIds.isEmpty()) {
        LOGGER.debug("Removing rules dependent on RuleTag " + id);
        for (Long ruleId : ruleIds) { //concurrent modifcation as a rule is removed from the list during the remove call!
          if (tagLocationService.isInTagCache(ruleId)) { //may already have been removed if a previous rule in the list was used in this rule!
            ConfigurationElementReport newReport = new ConfigurationElementReport(Action.REMOVE, Entity.RULETAG, ruleId);
            elementReport.addSubReport(newReport);
            ruleTagConfigHandler.removeRuleTag(ruleId, newReport); //call config handler bean so transaction annotation is noticed
          }         
        }                
      }
      tagCache.acquireWriteLockOnKey(id);      
      Collection<Long> ruleInputTagIds = Collections.EMPTY_LIST;
      try {
        ruleInputTagIds = ruleTag.getCopyRuleInputTagIds();                
        Collection<Long> alarmIds = ruleTag.getCopyAlarmIds();                  
        if (!alarmIds.isEmpty()) {
          LOGGER.debug("Removing Alarms dependent on RuleTag " + id);
          for (Long alarmId : alarmIds) { //need copy as modified concurrently by remove alarm
            ConfigurationElementReport alarmReport = new ConfigurationElementReport(Action.REMOVE, Entity.ALARM, alarmId);
            elementReport.addSubReport(alarmReport);
            alarmConfigHandler.removeAlarm(alarmId, alarmReport);
          }        
        }
        for (Long inputTagId : ruleInputTagIds) {
          tagConfigGateway.removeRuleFromTag(inputTagId, id); //allowed to lock tag below the rule...
        }
        configurableDAO.deleteItem(ruleTag.getId());                                           
      }
      catch (RuntimeException rEx) {
        String errMessage = "Exception caught when removing rule tag with id " + id;
        LOGGER.error(errMessage, rEx); 
        throw new UnexpectedRollbackException(errMessage, rEx);   
      } finally {
        if (tagCache.isWriteLockedByCurrentThread(id)) {
          tagCache.releaseWriteLockOnKey(id);
        }        
      }
    } catch (CacheElementNotFoundException e) {
      LOGGER.debug("Attempting to remove a non-existent RuleTag - no action taken.");
      elementReport.setWarning("Attempting to removed a non-existent RuleTag");      
    }       
  }
}
