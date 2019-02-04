package me.linx.vchat.service;

import me.linx.vchat.bean.TokenRecord;
import me.linx.vchat.bean.User;
import me.linx.vchat.repository.TokenRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TokenRecordService {
    private final TokenRecordRepository tokenRecordRepository;

    @Autowired
    public TokenRecordService(TokenRecordRepository tokenRecordRepository) {
        this.tokenRecordRepository = tokenRecordRepository;
    }

    /**
     *  保存登录信息
     * @param user 用户
     * @param token 令牌
     * @param deviceId 设备标识
     */
    public void save(User user, String token, String deviceId) {
        TokenRecord record = new TokenRecord();
        record.setUser(user);
        record.setDevice(deviceId);
        record.setToken(token);
        tokenRecordRepository.save(record);
    }

    /**
     *  验证是否已经在其他设备登陆过
     * @param user 用户
     * @return yes or no
     */
    public Boolean verifyDevice(User user) {
        return tokenRecordRepository.countByUser(user) == 0;
    }

    /**
     *  是否存在token相同的记录
     * @param token token
     * @return yes or no
     */
    public Boolean  verifyToken(String token){
        return tokenRecordRepository.countByToken(token) > 0;
    }

    /**
     *  清除用户登录信息
     * @param user 用户
     */
    public void clearLoggedInfo(User user) {
        tokenRecordRepository.deleteByUser(user);
    }

    /**
     *  清除用户登录信息
     * @param userId 用户ID
     */
    public void clearLoggedInfo(Long userId) {
        tokenRecordRepository.deleteByUser_Id(userId);
    }
}
