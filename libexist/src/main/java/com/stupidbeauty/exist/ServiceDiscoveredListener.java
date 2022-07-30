package com.stupidbeauty.exist;

import java.net.InetAddress;

public interface ServiceDiscoveredListener
{
    void onServiceDiscovered(ServicePublishMessage videoStreamQueryResponseMessage, InetAddress senderAddress); //!< 发现了服务。
}
