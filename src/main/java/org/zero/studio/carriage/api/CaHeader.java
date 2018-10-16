package org.zero.studio.carriage.api;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * CaHeader for CaMessage
 * 
 * @author Administrator
 */
public class CaHeader
{
	// type: parent body type, under asynchronous case, get Object Type by this.
	private String type;
	// the date time String for client setting before sending or receiving.
	private String datetime;
	// maybe we will have many partners.
	private String channelId;
	// (Client Message Unique ID) : client set this Unique ID only belongs to themselves for every message.
	private String cmuId;
	// (Local Server Message Unique ID) : we will set a unique ID for every MSG.
	private String smuId;
	// Using whose language.
	private String language = Locale.ENGLISH.getLanguage();
	// error list for response or received MSG
	private List<CaError> errors = new LinkedList<>();
	// extra information for other situations
	private Map<String, Object> extraInfo = new HashMap<String, Object>();

	public String getChannelId()
	{
		return channelId;
	}

	public void setChannelId(String channelId)
	{
		this.channelId = channelId;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getCmuId()
	{
		return cmuId;
	}

	public void setCmuId(String cmuId)
	{
		this.cmuId = cmuId;
	}

	public String getSmuId()
	{
		return smuId;
	}

	public void setSmuId(String smuId)
	{
		this.smuId = smuId;
	}

	public String getDatetime()
	{
		return datetime;
	}

	public void setDatetime(String datetime)
	{
		this.datetime = datetime;
	}

	public String getLanguage()
	{
		return language;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	public Map<String, Object> getExtraInfo()
	{
		return extraInfo;
	}

	public void setExtraInfo(Map<String, Object> extraInfo)
	{
		this.extraInfo = extraInfo;
	}

	public List<CaError> getErrors()
	{
		return errors;
	}

	public void setErrors(List<CaError> errors)
	{
		this.errors = errors;
	}
}
