package me.linx.vchat.netty.session;

import io.netty.util.AttributeKey;


public interface Attributes {
    AttributeKey<TokenRecordSession> SESSION = AttributeKey.newInstance(TokenRecordSession.class.getName());
}
