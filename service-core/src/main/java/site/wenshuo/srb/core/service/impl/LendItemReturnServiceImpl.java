package site.wenshuo.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import site.wenshuo.srb.core.mapper.*;
import site.wenshuo.srb.core.pojo.entity.*;
import site.wenshuo.srb.core.service.LendItemReturnService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借回款记录表 服务实现类
 * </p>
 *
 * @author Wenshuo
 * @since 2021-07-26
 */
@Service
public class LendItemReturnServiceImpl extends ServiceImpl<LendItemReturnMapper, LendItemReturn> implements LendItemReturnService {

    @Resource
    private LendItemReturnMapper lendItemReturnMapper;

    @Resource
    private LendMapper lendMapper;

    @Resource
    private LendReturnMapper lendReturnMapper;

    @Resource
    private LendItemMapper lendItemMapper;

    @Resource
    private UserInfoMapper userInfoMapper;
    @Override
    public List<LendItemReturn> selectByLendId(Long id, Long userId) {
        QueryWrapper<LendItemReturn> lendItemReturnQueryWrapper = new QueryWrapper<>();
        lendItemReturnQueryWrapper.eq("invest_user_id",userId).eq("lend_id",id).orderByAsc("current_period");
        return  baseMapper.selectList(lendItemReturnQueryWrapper);
    }

    @Override
    public List<Map<String, Object>> addReturnDetail(Long lendReturnId) {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        LendReturn lendReturn = lendReturnMapper.selectById(lendReturnId);
        Lend lend = lendMapper.selectById(lendReturn.getLendId());
        QueryWrapper<LendItemReturn> lendItemReturnQueryWrapper = new QueryWrapper<>();
        lendItemReturnQueryWrapper.eq("lend_return_id",lendReturnId);
        List<LendItemReturn> lendItemReturns = lendItemReturnMapper.selectList(lendItemReturnQueryWrapper);
        for (LendItemReturn lendItemReturn : lendItemReturns) {
            Long lendItemId = lendItemReturn.getLendItemId();
            LendItem lendItem = lendItemMapper.selectById(lendItemId);
            Long investUserId = lendItemReturn.getInvestUserId();
            UserInfo userInfo = userInfoMapper.selectById(investUserId);
            Map<String, Object> map = new HashMap<>();
            map.put("agentProjectCode",lend.getLendNo());
            map.put("voteBillNo",lendItem.getLendItemNo());
            map.put("toBindCode",userInfo.getBindCode());
            map.put("transitAmt",lendItemReturn.getTotal());
            map.put("baseAmt",lendItemReturn.getPrincipal());
            map.put("benifitAmt",lendItemReturn.getInterest());
            map.put("feeAmt",new BigDecimal(0));
            list.add(map);
        }
        return list;
    }
}
