package me.linx.vchat.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import me.linx.vchat.bean.TokenRecord;
import me.linx.vchat.bean.User;
import me.linx.vchat.netty.session.IMDispatcher;
import me.linx.vchat.repository.TokenRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class TokenRecordService {
    //Token到期天数
    private static final int EXPIRE_DAY = 365;
    //Token私钥
    private static final String TOKEN_SECRET = "linx.me.vchat.123212321";

    private TokenRecordRepository tokenRecordRepository;

    @Autowired
    public void setTokenRecordRepository(TokenRecordRepository tokenRecordRepository) {
        this.tokenRecordRepository = tokenRecordRepository;
    }

    public static TokenRecordService instance ;
    public TokenRecordService(){
        instance = this;
    }

    /**
     * 验证token
     *
     * @param token token
     * @return {@link TokenRecord}
     */
    public TokenRecord verify(@NotNull String token) {
       try{
           Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
           JWTVerifier verifier = JWT.require(algorithm).build();
           DecodedJWT jwt = verifier.verify(token);
           Map<String, Claim> claims = jwt.getClaims();
           Long userId = claims.get("userId").asLong();
           String deviceId = claims.get("deviceId").asString();
           return tokenRecordRepository.findByUser_IdAndTokenAndDevice(userId, token, deviceId);
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }
    }

    /**
     * 签名，生成TokenRecord
     *
     * @param user     用户
     * @param deviceId 设备
     * @return {@link TokenRecord}
     */
    public synchronized static TokenRecord sign(@NotNull User user, @NotNull String deviceId) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DAY_OF_YEAR, EXPIRE_DAY);
        Date extTime = instance.getTime();

        // 根据userId， 设备标识生成token
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
        Map<String, Object> header = new HashMap<>(2);
        header.put("typ", "JWT");
        header.put("alg", "HMAC256");
        String token = JWT.create()
                .withHeader(header)
                .withClaim("userId", user.getId())
                .withClaim("deviceId", deviceId)
                .withExpiresAt(extTime)
                .sign(algorithm);


        TokenRecord tokenRecord = new TokenRecord();
        tokenRecord.setUser(user);
        tokenRecord.setDevice(deviceId);
        tokenRecord.setToken(token);
        tokenRecord.setExpTime(extTime);
        return tokenRecord;
    }

    /**
     * 保存登录信息
     *
     * @param user     用户
     * @param deviceId 设备标识
     */
    public TokenRecord save(User user, String deviceId) {
        return tokenRecordRepository.save(sign(user, deviceId));
    }

    /**
     * 验证是否已经在其他设备登陆过
     *
     * @param user 用户
     * @return yes or no
     */
    public Boolean verifyDevice(User user) {
        return tokenRecordRepository.countByUser(user) == 0;
    }

    /**
     * 是否存在token相同的记录
     *
     * @param token token
     * @return yes or no
     */
    public Boolean verifySameToken(String token) {
        return tokenRecordRepository.countByToken(token) > 0;
    }

    /**
     * 清除用户登录信息
     *
     * @param user 用户
     */
    public void clearLoggedInfo(User user) {
        tokenRecordRepository.deleteByUser(user);
    }

    /**
     * 清除用户登录信息
     *
     * @param userId 用户ID
     */
    public void clearLoggedInfo(Long userId) {
        tokenRecordRepository.deleteByUser_Id(userId);
    }
}
