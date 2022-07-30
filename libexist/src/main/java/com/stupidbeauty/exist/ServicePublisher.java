package com.stupidbeauty.exist;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;
import com.upokecenter.cbor.CBORObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hxcan
 */
public class ServicePublisher
{
	private PrimeThread pPassive; //!<被动扫描的线程。
	private PrimeUnicastThread pUnicast; //!<主动扫描的线程。
	private DatagramSocket unicastSocket; //!<接收单播数据包用的UDP套接字。
	private ServiceDiscoveredListener serviceDiscoveredListener; //!<服务发现之后的回调对象。
	private final Context context; //!<上下文。
	private MulticastLock multicastLock; //!<多播锁。
	private  InetAddress group; //!<广播组地址。
	private MulticastSocket multiSocket; //!<多播套接字。
	private static final int PORT = 11500; //!<多播组的端口号。

	/**
	 * 停止扫描。
	 */
	public void stopScanning()
	{
		pUnicast.interrupt(); //中断线程。
		pPassive.interrupt(); //中断线程。
	} //public void stopScanning()

	/**
	 * 开始扫描。
	 */
	public void startScanning()
	{
		pPassive =new PrimeThread(serviceDiscoveredListener,multiSocket,context); //创建一个用于扫描的线程。
		pPassive.start(); //启动线程。

		pUnicast=new PrimeUnicastThread(serviceDiscoveredListener,multiSocket,context,unicastSocket); //创建一个用于主动扫描的线程。
		pUnicast.start(); //启动线程。
	} //public void startScanning()

	/**
	 * 释放组播锁。
	 */
	private void releaseMulticastLock()
	{
		multicastLock.release(); //释放锁。
	} //private void releaseMulticastLock()

	/**
	 * 获取多播锁。
	 */
	private void acquireMulticastLock() 
	{
		WifiManager wifi=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		
		if (wifi!=null)
		{
			multicastLock=wifi.createMulticastLock("Log_Tag");
			
			multicastLock.acquire();
		} //if (wifi!=null)
	} //private void acquireMulticastLock()

	/**
	 * 设置服务发现事件监听器。
	 * @param serviceDiscoveredListener 要设置的监听器。
     */
	public void setServiceDiscoveredListener(ServiceDiscoveredListener serviceDiscoveredListener)
	{
		this.serviceDiscoveredListener=serviceDiscoveredListener; //记录。
	} //public void setServiceDiscoveredListener(ServiceDiscoveredListener serviceDiscoveredListener)
	
	/**
	 * 构造函数。
	 * @param serviceContext 服务上下文。
	 */
	public ServicePublisher(Context serviceContext) 
	{
		super();
		
		context=serviceContext; //记录上下文。
		
		acquireMulticastLock(); //获取多播锁。
		
		joinMulticastGroup(); //加入多播组。

		listenUnicastUdp(); //监听单播的端口。
	} //public ServicePublisher()

	/**
	 * 监听单播的端口。
	 */
	private void listenUnicastUdp()
	{
		try
		{
			unicastSocket = new DatagramSocket();
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
	} //private void listenUnicastUdp()
	
	/**
	 * 加入多播组。
	 */
	private void joinMulticastGroup() 
	{
		try {
			//224.0.0.0~239.255.255.255

//Table 1 Multicast Address Range Assignments
//
//Description
//Range
//Reserved Link Local Addresses
//
//224.0.0.0/24
//
//Globally Scoped Addresses
//
//224.0.1.0 to 238.255.255.255
//
//Source Specific Multicast
//
//232.0.0.0/8
//
//GLOP Addresses
//
//233.0.0.0/8
//
//Limited Scope Addresses
//
//239.0.0.0/8
//			
			group = InetAddress.getByName("239.173.40.5");
			multiSocket=new MulticastSocket(PORT);
			multiSocket.joinGroup(group);
		} catch (IOException e) {
			e.printStackTrace();
		}
	} //private void joinMulticastGroup()

	/**
	 *  发布服务。
	 */
	public void publishService(String LanServiceName,int LanServicePort, String LanServiceProtocolType)
	{
		LanService currentService=new LanService(); //创建本地服务。
		
		currentService.setServiceName(LanServiceName); //设置服务名字。
		currentService.setServicePort(LanServicePort); //设置服务端口号。
		currentService.setServiceProtocolType(LanServiceProtocolType); //设置服务协议类型。
		
		multicastLanService(currentService); //向局域网广播此服务。

		releaseMulticastLock(); //释放组播锁。
	} //public void publishService(LanServiceName,LanServicePort,LanServiceProtocolType)
	
	private class GetCouponInfoListTask extends AsyncTask<LanService, Void, String> 
	{
		@Override
		protected String doInBackground(LanService... params) 
		{
			String Result = ""; // 结果。

			LanService serviceObject=params[0]; //获取服务对象。

			byte[] requestData=buildExistMessageCbor(serviceObject); // build 发送的数据。 exist message.
// 			byte[] requestData=buildMatchEvent(serviceObject); // 发送的数据。
			DatagramPacket requestPacket=new DatagramPacket(requestData,requestData.length,group,PORT); //创建数据包。
			try 
			{
				multiSocket.send(requestPacket);
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			} //发送。

			return Result; // 返回结果。
		} // protected String doInBackground(String... params)

		/**
		* build 发送的数据。 exist message.
		*/
		private byte[] buildExistMessageCbor(LanService serviceObject) 
		{
			byte[] byteArrayBody=null; //要发送的数据体。

			try //尝试构造请求对象，并且捕获可能的异常。
			{
				ExistMessage translateRequestBuilder = new ExistMessage();

				ServicePublishMessage servicePublishMessageBuilder= new ServicePublishMessage(); //服务发布消息构建器。

				servicePublishMessageBuilder .setName(serviceObject.getServiceName()) .setPort(serviceObject.getServicePort()) .setProtocolType(serviceObject.getServiceProtocolType()); //设置各个参数。

								    CBORObject servicePublishCborObject= CBORObject.FromObject(servicePublishMessageBuilder); // 创建对象. Service publish message object.

				byte[] servicePublisMessageByteArray=servicePublishCborObject.EncodeToBytes(); // Encode service publish message.

// 				translateRequestBuilder .setMessageType(ExistMessage.MessageType.SERVICEPUBLISH) .setServicePublishMessage(servicePublishMessageBuilder); //设置各个参数。
				translateRequestBuilder .setMessageType(ExistMessage.MessageType.SERVICEPUBLISH) .setServicePublishMessage(servicePublisMessageByteArray); //设置各个参数。

				    CBORObject cborObject= CBORObject.FromObject(translateRequestBuilder); //创建对象

    byte[] array=cborObject.EncodeToBytes();

				byteArrayBody=array; //序列化成字节数组。
			} //try //尝试构造请求对象，并且捕获可能的异常。
			catch (Exception e)
			{
				e.printStackTrace();
			}

			return byteArrayBody;
		} // private byte[] buildExistMessageCbor(LanService serviceObject)
		
		/**
		 * 构造比赛事件数据体。
		 */
		private byte[] buildMatchEvent(LanService description)
		{
			byte[] byteArrayBody=null; //要发送的数据体。

			try //尝试构造请求对象，并且捕获可能的异常。
			{
				ExistMessageContainer.ExistMessage.Builder translateRequestBuilder = ExistMessageContainer.ExistMessage.newBuilder();

				ExistMessageContainer.ServicePublishMessage.Builder servicePublishMessageBuilder= ExistMessageContainer.ServicePublishMessage.newBuilder(); //服务发布消息构建器。

// 				servicePublishMessageBuilder .setName(description.getServiceName()) .setPort(description.getServicePort()) .setProtocolType(description.getServiceProtocolType()); //设置各个参数。

				ExistMessageContainer.ServicePublishMessage servicePublishMessage=servicePublishMessageBuilder.build(); //构造服务发布消息。

				translateRequestBuilder .setMessageType(ExistMessageContainer.ExistMessage.MessageType.SERVICEPUBLISH) .setServicePublishMessage(servicePublishMessage); //设置各个参数。

				byteArrayBody=translateRequestBuilder.build().toByteArray(); //序列化成字节数组。
			} //try //尝试构造请求对象，并且捕获可能的异常。
			catch (Exception e)
			{
				e.printStackTrace();
			}

			return byteArrayBody;
		} //private void buildVideoStreamQueryEvent()
	} // private class CommitOrderTask extends AsyncTask<String, Void, String>

	/**
	 * 向局域网发送广播，告知自己提供的服务。
	 * @param currentService 要广播的服务。
	 */
	private void multicastLanService(LanService currentService) 
	{
		GetCouponInfoListTask GetCouponInfoListtask = new GetCouponInfoListTask(); // 创建任务。
		GetCouponInfoListtask.execute(currentService); // 执行任务。
	} //private void broadcastLanService(LanService currentService)
}
