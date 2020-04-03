package com.acl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

public class AppUtils {

	public static String getIncementdTimeStamp(int ms) throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
		Date d = sdf.parse(sdf.format(new Date()));
		System.out.println("date " + d + "time stamp " + d.getTime());
		d=DateUtils.addMinutes(d, ms);
		System.out.println("date " + d + "time stamp " + d.getTime());
		
		return d.getTime() + "";

	}
	
	public static String getCurrentTimeStamp() throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
		Date d = sdf.parse(sdf.format(new Date()));
		System.out.println("date " + d + "time stamp " + d.getTime());
		return d.getTime() + "";

	}
	
	public static String maskString(String strText, int start,  String maskChar) 
	        throws Exception{
			int end=6;
	        if(strText == null || strText.equals(""))
	            return "";
	        
	        if(start < 0)
	            start = 0;
	        
	        if( end > strText.length() )
	            end = strText.length();
	            
	        if(start > end)
	            throw new Exception("End index cannot be greater than start index");
	        
	        int maskLength = end - start;
	        
	        if(maskLength == 0)
	            return strText;
	        
	        StringBuilder sbMaskString = new StringBuilder(maskLength);
	        
	        for(int i = 0; i < maskLength; i++){
	            sbMaskString.append(maskChar);
	        }
	        
	        return strText.substring(0, start) 
	            + sbMaskString.toString() 
	            + strText.substring(start + maskLength);
	    }
	
	
	public static String maskInr(String strText, int start,  String maskChar) 
	        throws Exception{
			int end=strText.length()/2;
	        if(strText == null || strText.equals(""))
	            return "";
	        
	        if(start < 0)
	            start = 0;
	        
	        if( end > strText.length() )
	            end = strText.length();
	            
	        if(start > end)
	            throw new Exception("End index cannot be greater than start index");
	        
	        int maskLength = end - start;
	        
	        if(maskLength == 0)
	            return strText;
	        
	        StringBuilder sbMaskString = new StringBuilder(maskLength);
	        
	        for(int i = 0; i < maskLength; i++){
	            sbMaskString.append(maskChar);
	        }
	        
	        return strText.substring(0, start) 
	            + sbMaskString.toString() 
	            + strText.substring(start + maskLength);
	    }
	 
}
