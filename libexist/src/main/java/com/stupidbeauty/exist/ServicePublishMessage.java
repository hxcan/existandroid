package com.stupidbeauty.exist;

  /**
   * Protobuf type {@code com.stupidbeauty.exist.ServicePublishMessage}
   */
  public final class ServicePublishMessage 
  {
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
