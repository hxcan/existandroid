package com.stupidbeauty.exist;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Hxcan
 *
 */
public class PrimeThread extends Thread
{
	private final Context context; //!<无线网相关的上下文。
	private static final String TAG="PrimeThread"; //!<输出调试信息时使用的标记。
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
			multicastLock=wifi.createMulticastLock("PrimeThread");

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
				TimeUnit.SECONDS.sleep(2); //睡2秒。

				acquireMulticastLock(); //获取多播锁。

				multiSocket.setSoTimeout(2500);

				Log.d(TAG,"run, required multicast lock."); //Debug.

				multiSocket.receive(datagram);

				Log.d(TAG,"run, received data gram, length: "+ datagram.getLength()); //Debug.

				//解析数据：
				byte[] payloadData=new byte[datagram.getLength()]; //负载数据。
				System.arraycopy(datagram.getData(),0,payloadData,0, datagram.getLength());

				ExistMessage videoStreamQueryResponseMessage= ExistMessage.parseFrom(payloadData); // 解析消息。
				
				ServicePublishMessage servicePublishMessage=ServicePublishMessage.parseFrom(videoStreamQueryResponseMessage.getServicePublishMessage()); // Parse the service publish message.

				Log.d(TAG,"run, parsed ExistMessage. Service name: "+ servicePublishMessage.getName()+", sender address: "+ datagram.getAddress().toString()); //Debug.

				//通知监听器：
				serviceDiscoveredListener1.onServiceDiscovered(servicePublishMessage, datagram.getAddress()); //调用监听器的方法。

				releaseMulticastLock(); //释放组播锁。

				Log.d(TAG,"run, released multicast lock."); //Debug.

				TimeUnit.SECONDS.sleep(5); //睡5秒。
			}
			catch (SocketTimeoutException e) //接收超时。
			{
				e.printStackTrace(); //报告错误。

				releaseMulticastLock(); //释放多播锁。
			} //catch (SocketTimeoutException e) //接收超时。
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
	public PrimeThread(ServiceDiscoveredListener serviceContext,MulticastSocket multicastSocket,Context wifiContext)
	{
		serviceDiscoveredListener1 =serviceContext; //记录服务发现之后的回调对象。
		multiSocket=multicastSocket; //记录套接字。
		context=wifiContext; //记录上下文。
	} //public ServicePublisher()
	
	/**
	 * 加入多播组。
	 */
	private void joinMulticastGroup() 
	{
		try 
		{
			group = InetAddress.getByName("239.173.40.5");
			multiSocket=new MulticastSocket(PORT);
			multiSocket.joinGroup(group);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	} //private void joinMulticastGroup()
}
