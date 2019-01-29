package me.linx.vchat.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    //Token到期天数
//    private static final int EXPIRE_DAY = 30;

    //Token私钥
    private static final String TOKEN_SECRET = "linx.me.vchat.123212321";

    public synchronized static String sign(@NotNull long userId, @NotNull String deviceId) {
//        Calendar instance = Calendar.getInstance();
//        instance.add(Calendar.DAY_OF_YEAR, EXPIRE_DAY);

        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
        Map<String, Object> header = new HashMap<>(2);
        header.put("typ", "JWT");
        header.put("alg", "HMAC256");
        return JWT.create()
                .withHeader(header)
                .withClaim("userId", userId)
                .withClaim("deviceId", deviceId)
//                .withExpiresAt(instance.getTime())
                .sign(algorithm);
    }

    public static Long verify(@NotNull String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            Map<String, Claim> claims = jwt.getClaims();

            return claims.get("userId").asLong();
//            String dId = claims.get("deviceId").asString();
//            Date exp = claims.get("exp").asDate();

//            return uid == userId && dId.equals(deviceId);
//            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
