package site.wenshuo.srb.core.mapper;

import org.apache.ibatis.annotations.Param;
import site.wenshuo.srb.core.pojo.entity.UserAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.math.BigDecimal;

/**
 * <p>
 * 用户账户 Mapper 接口
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
public interface UserAccountMapper extends BaseMapper<UserAccount> {
    void updateAccount(@Param("bindCode") String bindCode, @Param("amount") BigDecimal amount, @Param("freezeAmount") BigDecimal freezeAmount);

}
