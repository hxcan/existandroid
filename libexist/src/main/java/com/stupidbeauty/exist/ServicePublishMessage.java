package com.stupidbeauty.exist;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.util.Log;
import com.upokecenter.cbor.CBORObject;
import com.upokecenter.cbor.CBORObject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import com.upokecenter.cbor.CBORException;
import com.google.protobuf.ByteString;
import android.Manifest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
* Protobuf type {@code com.stupidbeauty.exist.ServicePublishMessage}
*/
public final class ServicePublishMessage 
{
  private static final String TAG="ServicePublishMessage"; //!< 输出调试信息时使用的标记。

  /**
  *  Parse the service publish message.
  */
  public static ServicePublishMessage parseFrom(byte[] videoStreamQueryResponseMessagegetServicePublishMessage) 
  {
    ServicePublishMessage voicePackageNameMap=new ServicePublishMessage(); // he result.
    
    try
    {
      byte[] photoBytes= videoStreamQueryResponseMessagegetServicePublishMessage; //将照片文件内容全部读取。
      CBORObject videoStreamMessage= CBORObject.DecodeFromBytes(photoBytes); //解析消息。
            
      String json_debug=videoStreamMessage.ToJSONString(); // Convert to sjon.

      Log.d(TAG, "parseFrom, 45, json: " + json_debug); //Debug.

      CBORObject currentText=videoStreamMessage; // 获取 Single map.

      String packageName=currentText.get("name").AsString(); // 获取 package name.
      int score=currentText.get("port").AsInt32(); // 获取 score.
      String protocolType=currentText.get("protocolType").AsString(); // Get the protocol type.
                
      voicePackageNameMap.setName(packageName).setPort( score).setProtocolType(protocolType); // set attributes.
    }
    catch (CBORException e)
    {
      e.printStackTrace();
    }

    return voicePackageNameMap;
    } // public static ServicePublishMessage parseFrom(byte[] videoStreamQueryResponseMessagegetServicePublishMessage)
  
    public ServicePublishMessage()
    {
      this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); 
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;

    private int bitField0_;
    public static final int NAME_FIELD_NUMBER = 1;
    private java.lang.Object name_;
    /**
     * <code>required string name = 1;</code>
     */
    public boolean hasName() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    
    public ServicePublishMessage setName(String name)
    {
      name_=name;
      
      return this;
    }
    
    /**
     * <code>required string name = 1;</code>
     */
    public java.lang.String getName() {
      java.lang.Object ref = name_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          name_ = s;
        }
        return s;
      }
    }

    private int port_;
    
    public ServicePublishMessage setPort(int port)
    {
      port_=port;
      
      return this;
    }

    /**
     * <code>required int32 port = 2;</code>
     */
    public int getPort() {
      return port_;
    }

    private String protocolType_; //!< Protocol type
    
    public ServicePublishMessage setProtocolType(String protocolType)
    {
      protocolType_ = protocolType;
      
      return this;
    }

    /**
     * <code>required .com.stupidbeauty.exist.ServicePublishMessage.ServiceProtocolType protocolType = 3;</code>
     */
    public String getProtocolType() 
    {
      return protocolType_;
    }

    private int memoizedSerializedSize = -1;
  }
