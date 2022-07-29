package com.stupidbeauty.exist;

/**
 * 要向局域网发布的服务。
 * @author root 蔡火胜
 *
 */
public class LanService
{

	private String serviceName; //!<服务名字。

	public String getServiceProtocolType() 
	{
		return serviceProtocolType;
	}

	public void setServiceProtocolType(String serviceProtocolType) 
	{
		this.serviceProtocolType = serviceProtocolType;
	}

	public int getServicePort() {
		return servicePort;
	}

	private int servicePort; //!<服务端口号。

	private String serviceProtocolType; //!<服务协议类型。

	/**
	 * 设置服务名字。
	 * @param lanServiceName 服务名字。
	 */
	public void setServiceName(String lanServiceName) 
	{
		serviceName=lanServiceName; //记录。
		
	
	} //public void setServiceName(String lanServiceName)

	/**
	 * 设置服务端口号。
	 * @param lanServicePort 服务端口号。
	 */
	public void setServicePort(int lanServicePort) 
	{
		
		servicePort=lanServicePort; //记录。


	} //public void setServicePort(int lanServicePort)

	/**
	 * 获取服务名字。
	 * @return 服务名字。
	 */
	public String getServiceName() 
	{
	
		return serviceName;
	} //public String getServiceName()
}
