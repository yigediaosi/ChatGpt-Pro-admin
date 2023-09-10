package vip.xiaonuo.modular.gptuserinfo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import vip.xiaonuo.core.factory.PageFactory;
import vip.xiaonuo.core.pojo.page.PageResult;
import vip.xiaonuo.modular.gptuserinfo.entity.InviteRecord;
import vip.xiaonuo.modular.gptuserinfo.mapper.InviteRecordMapper;
import vip.xiaonuo.modular.gptuserinfo.param.InviteRecordParam;
import vip.xiaonuo.modular.gptuserinfo.service.InviteRecordService;
import java.util.List;

/**
 * @author 981743898@qq.com
 */
@Service
public class InviteRecordServiceImpl extends ServiceImpl<InviteRecordMapper, InviteRecord> implements InviteRecordService {


    @Override
    public PageResult<InviteRecord> page(InviteRecordParam inviteRecordParam) {
        QueryWrapper<InviteRecord> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotNull(inviteRecordParam)) {

            // 根据邮箱 查询
            if (ObjectUtil.isNotEmpty(inviteRecordParam.getEmail())) {
                queryWrapper.lambda().eq(InviteRecord::getEmail, inviteRecordParam.getEmail());
            }
            // 根据登录名 查询
            if (ObjectUtil.isNotEmpty(inviteRecordParam.getInvitedEmail())) {
                queryWrapper.lambda().eq(InviteRecord::getInvitedEmail, inviteRecordParam.getInvitedEmail());
            }

        }
        return new PageResult<>(this.page(PageFactory.defaultPage(), queryWrapper));
    }

    @Override
    public List<InviteRecord> list(InviteRecordParam inviteRecordParam) {
        return this.list();
    }

    @Override
    public void add(InviteRecordParam inviteRecordParam) {
        InviteRecord inviteRecord = new InviteRecord();
        BeanUtil.copyProperties(inviteRecordParam, inviteRecord);
        this.save(inviteRecord);
    }
}
