package me.linx.vchat.app.data.im.handler

import com.blankj.utilcode.util.LogUtils
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import me.linx.vchat.app.data.im.session.Attributes
import me.linx.vchat.core.codec.Decoder
import me.linx.vchat.core.codec.Encoder
import me.linx.vchat.core.packet.Packet
import me.linx.vchat.core.utils.SecurityUtils


class AESResponseHandler(val callback: () -> Unit) : SimpleChannelInboundHandler<Packet.AESResponsePacket>() {

    override fun messageReceived(ctx: ChannelHandlerContext?, msg: Packet.AESResponsePacket?) {
        msg?.let {
            //服务器回送的加密过的AES Key
            val aesKeyString = msg.aesKey
            val encryptKeyBytes = aesKeyString.toByteArray()

            //解密
            val privateKey = ctx?.channel()?.attr(Attributes.private_key)?.get()
            val aesKey = SecurityUtils.RSA.decryptByPrivateKey(encryptKeyBytes, privateKey?.encoded)

            // 替换解码器
            ctx?.pipeline()?.replace(
                Decoder.ProtobufDecoder::class.java,
                Decoder.ProtobufAESDecoder::class.java.name,
                Decoder.ProtobufAESDecoder(aesKey)
            )
            // 替换编码器
            ctx?.pipeline()?.replace(
                Encoder.ProtobufEncoder::class.java,
                Encoder.ProtobufAESEncoder::class.java.name,
                Encoder.ProtobufAESEncoder(aesKey)
            )

            // 移除自己
            ctx?.pipeline()?.remove(this)

            callback()
        }

    }

}
