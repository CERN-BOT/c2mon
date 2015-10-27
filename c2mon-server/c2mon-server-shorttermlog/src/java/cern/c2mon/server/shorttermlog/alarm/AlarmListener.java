package cern.c2mon.server.shorttermlog.alarm;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.c2mon.pmanager.IAlarmListener;

/**
 * Fallback alarm listener that send emails/SMS via log4j.
 * 
 * @author Mark Brightwell
 *
 */
public class AlarmListener implements IAlarmListener {

  /**
   * Mail logger.
   */
  private final static Logger EMAIL_LOGGER = LoggerFactory.getLogger("AdminMailLogger");
  
  /**
   * SMS logger.
   */
  private final static Logger SMS_LOGGER = LoggerFactory.getLogger("AdminSmsLogger");
  
  /**
   * Flags for not sending repeated error messages.
   */
  private volatile boolean dbAlarm = false;
  private volatile boolean diskAlarm = false;
  private volatile boolean fileAlarm = false;
  
  
  @Override
  public void dbUnavailable(boolean alarmUp, String exceptionMsg, String dbInfo) {
    if (alarmUp && !dbAlarm) {
      dbAlarm = true;
      EMAIL_LOGGER.error("Error in logging to Short-Term-Log: DB unavailable with error message " + exceptionMsg + ", for DB " + dbInfo);
      SMS_LOGGER.error("Error in STL logging: DB unavailable. See email for details.");
    } else if (!alarmUp && dbAlarm) {
      dbAlarm = false;
      EMAIL_LOGGER.error("DB unavailable error has resolved itself");
      SMS_LOGGER.error("DB unavailable error has resolved itself");
    }    
  }

  @Override
  public void diskFull(boolean alarmUp, String directoryName) {   
    if (alarmUp && !diskAlarm) {
      diskAlarm = true;
      EMAIL_LOGGER.error("Error in logging to Short-Term-Log fallback - the disk is nearly full, directory is " + directoryName);
      SMS_LOGGER.error("Error in STL fallback - the disk is nearly full.");
    } else if (!alarmUp && diskAlarm) {
      diskAlarm = false;
      EMAIL_LOGGER.error("Disk full error has resolved itself");
      SMS_LOGGER.error("Disk full error has resolved itself");
    }    
  }

  @Override
  public void fileNotReachable(boolean alarmUp, File file) {
    if (alarmUp && !fileAlarm) {
      fileAlarm = true;
      EMAIL_LOGGER.error("Error in logging to Short-Term-Log - the following file is not reachable: " + file.getName());
      SMS_LOGGER.error("Error in STL fallback - file not reachable: " + file.getName());
    } else if (!alarmUp && fileAlarm) {
      fileAlarm = false;
      EMAIL_LOGGER.error("File unreachable error has resolved itself");
      SMS_LOGGER.error("File unreachable error has resolved itself");
    }
  }

}
