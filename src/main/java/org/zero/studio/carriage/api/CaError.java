package org.zero.studio.carriage.api;

/**
 * Error information in callback, response, or receiver
 * 
 * @author Administrator
 */
public class CaError
{
	private String code;
	private String field;
	private String content;

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getField()
	{
		return field;
	}

	public void setField(String field)
	{
		this.field = field;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}
}
