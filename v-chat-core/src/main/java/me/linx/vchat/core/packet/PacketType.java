package me.linx.vchat.core.packet;

import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;

public class PacketType {

    public static byte getType(MessageLite msg) {
        if (msg instanceof Packet.AESRequestPacket) {
            return 1;
        } else if (msg instanceof Packet.AESResponsePacket) {
            return 2;
        } else if (msg instanceof Packet.AuthRequestPacket) {
            return 3;
        } else if (msg instanceof Packet.AuthResponsePacket) {
            return 4;
        } else if (msg instanceof Packet.TextPacket) {
            return 5;
        } else if (msg instanceof Packet.HeartBeatPacket) {
            return 6;
        } else if (msg instanceof Packet.LoggedOtherPacket) {
            return 7;
        }

        throw new UnsupportedOperationException("unknown type:" + msg.getClass().getName());
    }

    public static Parser getParser(byte type) {
        switch (type) {
            case 1:
                return Packet.AESRequestPacket.getDefaultInstance().getParserForType();
            case 2:
                return Packet.AESResponsePacket.getDefaultInstance().getParserForType();
            case 3:
                return Packet.AuthRequestPacket.getDefaultInstance().getParserForType();
            case 4:
                return Packet.AuthResponsePacket.getDefaultInstance().getParserForType();
            case 5:
                return Packet.TextPacket.getDefaultInstance().getParserForType();
            case 6:
                return Packet.HeartBeatPacket.getDefaultInstance().getParserForType();
            case 7:
                return Packet.LoggedOtherPacket.getDefaultInstance().getParserForType();
        }

        throw new UnsupportedOperationException("unknown type:" + type);
    }
}
