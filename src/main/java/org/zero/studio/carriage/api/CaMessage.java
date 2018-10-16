package org.zero.studio.carriage.api;

public class CaMessage<T>
{
	private CaHeader header = new CaHeader();
	private Boolean isSuccess;
	private T body;

	public CaHeader getHeader()
	{
		return header;
	}

	public void setHeader(CaHeader header)
	{
		this.header = header;
	}

	public Boolean isSuccess()
	{
		isSuccess = this.getHeader().getErrors().isEmpty();
		return isSuccess;
	}

	public void resetSuccess()
	{
		isSuccess = this.getHeader().getErrors().isEmpty();
	}

	public void setIsSuccess(Boolean isSuccess)
	{
		this.isSuccess = isSuccess;
	}

	public T getBody()
	{
		return body;
	}

	public void setBody(T body)
	{
		this.body = body;
	}
}
