package site.wenshuo.srb.core.service;

import site.wenshuo.srb.core.pojo.entity.UserAccount;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 用户账户 服务类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
public interface UserAccountService extends IService<UserAccount> {

    String commitCharge(Long userId, BigDecimal chargeAmt);

    String notify(Map<String, Object> map);

    boolean isTransFlowExisted(String agentBillNo);

    BigDecimal getAccount(Long userId);

    String commitWithdraw(BigDecimal fetchAmt, Long userId);

    void notifyWithdraw(Map<String, Object> map);

    String getMobileByBindCode(String bindCode);
}
