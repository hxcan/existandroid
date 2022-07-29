package com.stupidbeauty.exist;

import java.net.InetAddress;

@SuppressWarnings("unused")
public interface ServiceDiscoveredListener
{
    void onServiceDiscovered(ExistMessageContainer.ExistMessage videoStreamQueryResponseMessage, InetAddress senderAddress); //发现了服务。
}
