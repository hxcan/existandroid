package com.stupidbeauty.exist;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * @author Hxcan
 *
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class PrimeUnicastThread extends Thread
{

	private final DatagramSocket unicastSocket; //!<接收单播数据包用的UDP套接字。

	private final Context context; //!<无线网相关的上下文。

	private static final String TAG="PrimeUnicastThread"; //!<输出调试信息时使用的标记。
	private static final int BUFFER_LENGTH=1024; //!<缓冲区长度。

	private final ServiceDiscoveredListener serviceDiscoveredListener1; //!<上下文。
	private MulticastLock multicastLock; //!<多播锁。
	private  InetAddress group; //!<广播组地址。
	private MulticastSocket multiSocket; //!<多播套接字。
	private static final int PORT = 11500; //!<多播组的端口号。

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

		Log.d(TAG,"acquireMulticastLock, wifi: "+wifi); //Debug.

		if (wifi!=null)
		{
			multicastLock=wifi.createMulticastLock("PrimeUnicastThread");

			multicastLock.acquire();
		} //if (wifi!=null)
	} //private void acquireMulticastLock()

	@Override
	/**
	 * 线程运行。
	 */
	public void run()
	{
		byte[] b=new byte[BUFFER_LENGTH];
		DatagramPacket datagram =new DatagramPacket(b,b.length);

		while(true)
		{
			//接收数据：

			try
			{
				if (Thread.interrupted()) //被中断。
				{
					break; //跳出。
				} //if (Thread.interrupted()) //被中断。

				unicastSocket.setSoTimeout(5500); //超时时间。

				publishService(unicastSocket.getLocalPort()); //发布主动扫描数据包。

				Random random=new Random(); //随机数生成器。

				long randomMilliseconds =random.nextLong() % 1000; //取个随机的毫秒数。

				TimeUnit.MILLISECONDS.sleep(randomMilliseconds); //随机睡眠一段时间。

					unicastSocket.receive(datagram);

					Log.d(TAG,"run, received data gram, length: "+ datagram.getLength()+", sender ip: "+ datagram.getAddress().getHostAddress()); //Debug.

					if (datagram.getLength()==0) //长度为0,跳过。
					{
						continue; //跳过。
					} //if (datagram.getLength()==0) //长度为0,跳过。
				else //长度不为0,则尝试处理。
					{
						//解析数据：
						byte[] payloadData=new byte[datagram.getLength()]; //负载数据。
						System.arraycopy(datagram.getData(),0,payloadData,0, datagram.getLength());

						ExistMessage videoStreamQueryResponseMessage= ExistMessage.parseFrom(payloadData); //解析消息。

						byte[] servicePublishMessageByteArray=videoStreamQueryResponseMessage.getServicePublishMessage();

						ServicePublishMessage servicePublishMessage=ServicePublishMessage.parseFrom(servicePublishMessageByteArray); // Parse service publish message.

            Log.d(TAG,"run, parsed ExistMessage. Service name: "+servicePublishMessage.getName()+", sender address: "+ datagram.getAddress().toString()); //Debug.

						//通知监听器：
						serviceDiscoveredListener1.onServiceDiscovered(servicePublishMessage, datagram.getAddress()); //调用监听器的方法。

						TimeUnit.SECONDS.sleep(5); //睡5秒。
					} //else //长度不为0,则尝试处理。
			}
			catch (InterruptedException e) //被中断。
			{
				e.printStackTrace(); //报告错误。

				break; //跳出循环。
			} //catch (InterruptedException e) //被中断。
			catch (IOException e)
			{
				e.printStackTrace(); //报告错误。
			} //catch (IOException e)

			//重置数据包长度：
			datagram.setLength(b.length);
		}
	}

	/**
	 * 构造函数。
	 * @param serviceContext 服务上下文。
	 */
	public PrimeUnicastThread(ServiceDiscoveredListener serviceContext, MulticastSocket multicastSocket, Context wifiContext, DatagramSocket unicastSocket)
	{
		serviceDiscoveredListener1 =serviceContext; //记录服务发现之后的回调对象。
		multiSocket=multicastSocket; //记录套接字。
		context=wifiContext; //记录上下文。
		this.unicastSocket=unicastSocket; //记录单播用的套接字。
	} //public ServicePublisher()
	
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

			multiSocket=new MulticastSocket(PORT);
			multiSocket.joinGroup(group);
		} catch (IOException e) {
			e.printStackTrace();
		}
	} //private void joinMulticastGroup()

	private List<LanService> serviceList= new ArrayList<>(); //!<服务列表。

	/**
	 *  发布服务。
	 */
	private void publishService(int LanServicePort)
	{
		LanService currentService=new LanService(); //创建本地服务。
		
		currentService.setServicePort(LanServicePort); //设置服务端口号。

		multicastLanService(currentService); //向局域网广播此服务。

	} //public void publishService(LanServiceName,LanServicePort,LanServiceProtocolType)
	
	private class ProbeTask extends AsyncTask<LanService, Void, String>
	{
		@Override
		protected String doInBackground(LanService... params) 
		{
			String Result = ""; // 结果。

			LanService serviceObject=params[0]; //获取服务对象。

			byte[] requestData=buildMatchEvent(serviceObject); //发送的数据。
			try {
				group = InetAddress.getByName("239.173.40.5");
				DatagramPacket requestPacket=new DatagramPacket(requestData,requestData.length,group,PORT); //创建数据包。
				acquireMulticastLock();

				multiSocket.send(requestPacket);

				releaseMulticastLock(); //释放组播锁。

			} catch (IOException e) {
				e.printStackTrace();
			} //发送。

			return Result; // 返回结果。
		} // protected String doInBackground(String... params)


		/**
		 * 构造比赛事件数据体。
		 */
		private byte[] buildMatchEvent(LanService description)
		{
			byte[] byteArrayBody=null; //要发送的数据体。

			try //尝试构造请求对象，并且捕获可能的异常。
			{
				ExistMessageContainer.ExistMessage.Builder translateRequestBuilder = ExistMessageContainer.ExistMessage.newBuilder();


				ExistMessageContainer.ServiceProbeMessage.Builder servicePublishMessageBuilder= ExistMessageContainer.ServiceProbeMessage.newBuilder(); //服务发布消息构建器。

				servicePublishMessageBuilder
				.setPort(description.getServicePort()); //设置各个参数。

				ExistMessageContainer.ServiceProbeMessage servicePublishMessage=servicePublishMessageBuilder.build(); //构造服务发布消息。


				translateRequestBuilder
						.setMessageType(ExistMessageContainer.ExistMessage.MessageType.SERVICEPROBE)
				.setServiceProbeMessage(servicePublishMessage); //设置各个参数。



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
		ProbeTask GetCouponInfoListTask = new ProbeTask(); // 创建任务。
		GetCouponInfoListTask.execute(currentService); // 执行任务。
	} //private void broadcastLanService(LanService currentService)
}
