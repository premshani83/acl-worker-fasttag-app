package com.acl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

/**
 * Applicaiton utils class which have the all util method
 * @author prem.sahani
 *
 */
public class AppUtils {

	private static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
	/**
	 * Get the incremented time in long format
	 * @param ms incremented time in minutes
	 * @return incremented time into long format
	 * @throws ParseException
	 */
	public static String getIncementdTimeStamp(int ms) throws ParseException {
		Date d = sdf.parse(sdf.format(new Date()));
		System.out.println("date " + d + "time stamp " + d.getTime());
		d=DateUtils.addMinutes(d, ms);
		System.out.println("date " + d + "time stamp " + d.getTime());
		
		return d.getTime() + "";

	}
	
	/**
	 * Get current time stamp in long format
	 * @return timeStamp time stamp in string
	 * @throws ParseException
	 */
	public static String getCurrentTimeStamp() throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
		Date d = sdf.parse(sdf.format(new Date()));
		System.out.println("date " + d + "time stamp " + d.getTime());
		return d.getTime() + "";

	}
	
	/**
	 * Masking the secure data like mobile no, account no etc.
	 * @param strText Plain text
	 * @param start masking from where character no
	 * @param maskChar how many character will masked 
	 * @return masked string
	 * @throws Exception
	 */
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
	
	
	/**
	 * Masking the dynamic value like which is not fixed 
	 * @param strText
	 * @param start
	 * @param maskChar
	 * @return masked string
	 * @throws Exception
	 */
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
