package me.linx.vchat.core.session;

public class Session {

    private  byte[] aesKey;

    private  byte[] eccPrivateKey;

    public byte[] getAesKey() {
        return aesKey;
    }

    public void setAesKey(byte[] aesKey) {
        this.aesKey = aesKey;
    }

    public byte[] getEccPrivateKey() {
        return eccPrivateKey;
    }

    public void setEccPrivateKey(byte[] eccPrivateKey) {
        this.eccPrivateKey = eccPrivateKey;
    }
}
