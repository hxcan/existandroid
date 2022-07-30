package com.stupidbeauty.exist;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.util.Log;
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
* Protobuf type {@code com.stupidbeauty.exist.ExistMessage}
*/
public final class ExistMessage 
{
  private static final String TAG="ExistMessage"; //!< 输出调试信息时使用的标记。
  
  public ExistMessage() 
  { 
    this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); 
  }

  private final com.google.protobuf.UnknownFieldSet unknownFields;

    private int bitField0_;
    public static final int MESSAGETYPE_FIELD_NUMBER = 1;
    private int messageType_; //!< Message type.

    public ExistMessage setMessageType(int messageType)
    {
      messageType_=messageType;
      return this;
    }
    
    public static class MessageType
    {
      public static final Integer SERVICEPUBLISH=0; //!< Service publish.
    }
    
    /**
     * <code>required .com.stupidbeauty.exist.ExistMessage.MessageType messageType = 1;</code>
     */
    public int getMessageType() 
    {
      return messageType_;
    }

    public static final int SERVICEPUBLISHMESSAGE_FIELD_NUMBER = 2;
    private byte[] servicePublishMessage_;
    
    public ExistMessage setServicePublishMessage(byte[] servicePublishMessage)
    {
      servicePublishMessage_=servicePublishMessage;
      return this;
    }

    /**
     * <code>optional .com.stupidbeauty.exist.ServicePublishMessage servicePublishMessage = 2;</code>
     */
    public byte[] getServicePublishMessage() 
    {
      return servicePublishMessage_;
    }
    
    /**
    * 解析消息。
    */
    public static ExistMessage parseFrom(byte[] payloadData) 
    {
      ExistMessage voicePackageNameMap=new ExistMessage(); // The result;
      try
      {
        byte[] photoBytes= payloadData; //将照片文件内容全部读取。
        CBORObject videoStreamMessage= CBORObject.DecodeFromBytes(photoBytes); //解析消息。
            
        String json_debug=videoStreamMessage.ToJSONString(); // Convert to sjon.

        Log.d(TAG, "loadPackageScoreList, 421, json: " + json_debug); //Debug.

            CBORObject currentText=videoStreamMessage; // 获取 Single map.

            byte[] packageName=currentText.get("servicePublishMessage").GetByteString(); // 获取 service publish message.
            byte[] protocolType=currentText.get("serviceProbeMessage").GetByteString(); // 获取 service probe message.
            int score=currentText.get("messageType").AsInt32(); // 获取 message type.

            Log.d(TAG, "parseFrom, 102, publish message: " + packageName); //Debug.
            
            voicePackageNameMap.setServicePublishMessage(packageName).setMessageType(score).setServiceProbeMessage(protocolType); // Set attributes.
      }
      catch (CBORException e)
      {
        e.printStackTrace();
      }
      
      return voicePackageNameMap;
    } // public static ExistMessage parseFrom(byte[] payloadData)

    private byte[] serviceProbeMessage_;
    
    public ExistMessage setServiceProbeMessage(byte[] serviceProbeMessageByteArray)
    {
      serviceProbeMessage_=serviceProbeMessageByteArray;
    
      return this;
    } // voicePackageNameMap

    /**
     * <code>optional .com.stupidbeauty.exist.ServiceProbeMessage serviceProbeMessage = 3;</code>
     */
    public byte[] getServiceProbeMessage() 
    {
      return serviceProbeMessage_;
    }

    private byte memoizedIsInitialized = -1;

    private int memoizedSerializedSize = -1;
  }
