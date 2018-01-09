package edu.ciw.gl.hpcat.saverestore;

import java.io.*;
import java.util.*;
import jcifs.smb.*;

public class SaveFiles implements Runnable {
	  Calendar cal;
	private Date trialDate;
	private String saveYear;
	private String saveMonth;
	private String saveDay;
	private String saveHour;
	private String saveMinute;
	private String saveSec;
	private int formattedDate;
	private String[] ids = TimeZone.getAvailableIDs( -6 * 60 * 60 * 1000);
	private String[] monthName = {
	    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct","Nov", "Dec"};
	private String[] ampm = {"AM", "PM"};
	private SimpleTimeZone pdt = new SimpleTimeZone( -6 * 60 * 60 * 1000, ids[0]);
	private String[] iocDir = {
	    "ioc16ida", "ioc16idb", "ioc16idc", "ioc16idd", "ioc16bma","ioc16bmb", "ioc16bmd",
	    "ioc16lab"};
	private String autosave = "autosave";
	private String iocPath;
	private Set iocSet = new HashSet();
	private String[] files = {
	    "auto_settings.sav",
	    "auto_settings.savB",
	    "auto_positions.sav",
	    "auto_positions.savB"
	};

	  public SaveFiles() {
	    pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
	    pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
	    iocPath = "/home/epics/iocBoots/";

	  }

	  public void saveAll() {
	  String str;
	  cal = new GregorianCalendar(pdt);
	  trialDate = new Date();
	  cal.setTime(trialDate);
	  try {
	    String yearMonth = getYearMonth();
	    String day = getToDay();
	    String hourMinute = getHourMinute();

	    for (int i = 0; i < iocDir.length; i++) {
	      str = iocDir[i];
	      saveToServer(yearMonth, day, hourMinute, str);
	    }
	  }
	  catch (Exception e) {
	    System.out.println("PC Timer Exception   " + e.getMessage());
	  }

	}

	public String getYearMonth() {
	  String yearMonth;

	  saveYear = Integer.toString(cal.get(Calendar.YEAR));
	  saveMonth = monthName[cal.get(Calendar.MONTH)];
	  yearMonth = saveMonth + saveYear;
	  return yearMonth;
	}

	public String getToDay() {
	  String saveDay;
	  saveDay = Integer.toString(cal.get(Calendar.DATE));
	  return saveDay;
	}

	public String getHourMinute() {
	  String amORpm;

	  saveHour = Integer.toString(cal.get(Calendar.HOUR));
	  saveMinute = Integer.toString(cal.get(Calendar.MINUTE));
	  amORpm = ampm[cal.get(Calendar.AM_PM)];
	  return (saveHour + "_" + saveMinute + "_" + amORpm);

	}

	public void saveToServer(String yearMonthStr, String toDayStr,
	                         String hourMinuteStr, String ioc) throws Exception {

	  SmbFile toDir;
	  String toDirStr;
	  SmbFile toFile;

	  String in;
	  File inFile;
	  FileInputStream inStream;
	  SmbFileOutputStream outStream;

	  byte[] readBytes;
	  int availableBytes;
	  for (int j = 0; j < files.length; j++) {

	    toDirStr = "smb://epics:scipe1998@hpcat21" + "/" + "epics"
	        + "/"
	        + "saveRestoreBackups"
	        + "/"
	        + ioc
	        + "/"
	        + toDayStr
	        + yearMonthStr;
	    toDir = new SmbFile(toDirStr);

	    if (!toDir.exists()) {
	      toDir.mkdirs();
	    }

	    toDir.connect();

	    String toFileStr = toDirStr + File.separator + hourMinuteStr + "_" +
	        files[j];

	    toFile = new SmbFile(toFileStr);
	    in = iocPath + File.separator + ioc + File.separator + autosave +
	        File.separator + files[j];

	    inFile = new File(in);
	    inStream = new FileInputStream(inFile);
	    availableBytes = inStream.available();
	    readBytes = new byte[availableBytes];

	    inStream.read(readBytes);
	    outStream = new SmbFileOutputStream(toFile);
	    outStream.write(readBytes);
	    outStream.close();
	    inStream.close();
	  }
	}

	  public void run() {
	    saveAll();
	  }
	}
