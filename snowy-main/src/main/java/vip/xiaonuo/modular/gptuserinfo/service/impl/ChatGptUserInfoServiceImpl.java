/*
Copyright [2020] [https://www.xiaonuo.vip]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：

1.请不要删除和修改根目录下的LICENSE文件。
2.请不要删除和修改Snowy源码头部的版权声明。
3.请保留源码和相关描述文件的项目出处，作者声明等。
4.分发源码时候，请注明软件出处 https://gitee.com/xiaonuobase/snowy-layui
5.在修改包名，模块名称，项目代码等时，请注明软件出处 https://gitee.com/xiaonuobase/snowy-layui
6.若您的项目无法满足以上几点，可申请商业授权，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
package vip.xiaonuo.modular.gptuserinfo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import vip.xiaonuo.core.consts.CommonConstant;
import vip.xiaonuo.core.enums.CommonStatusEnum;
import vip.xiaonuo.core.exception.ServiceException;
import vip.xiaonuo.core.factory.PageFactory;
import vip.xiaonuo.core.pojo.page.PageResult;
import vip.xiaonuo.modular.gptuserinfo.entity.ChatGptUserInfo;
import vip.xiaonuo.modular.gptuserinfo.entity.ChatGptUserInfoResp;
import vip.xiaonuo.modular.gptuserinfo.enums.ChatGptUserInfoExceptionEnum;
import vip.xiaonuo.modular.gptuserinfo.mapper.ChatGptUserInfoMapper;
import vip.xiaonuo.modular.gptuserinfo.param.ChatGptUserInfoParam;
import vip.xiaonuo.modular.gptuserinfo.service.ChatGptUserInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 会员信息service接口实现类
 *
 * @author 981743898@qq.com
 * @date 2023-08-21 21:49:44
 */
@Service
public class ChatGptUserInfoServiceImpl extends ServiceImpl<ChatGptUserInfoMapper, ChatGptUserInfo> implements ChatGptUserInfoService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public PageResult<ChatGptUserInfo> page(ChatGptUserInfoParam chatGptUserInfoParam) {
        QueryWrapper<ChatGptUserInfo> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotNull(chatGptUserInfoParam)) {

            // 根据邮箱 查询
            if (ObjectUtil.isNotEmpty(chatGptUserInfoParam.getEmail())) {
                queryWrapper.lambda().eq(ChatGptUserInfo::getEmail, chatGptUserInfoParam.getEmail());
            }
            // 根据登录名 查询
            if (ObjectUtil.isNotEmpty(chatGptUserInfoParam.getName())) {
                queryWrapper.lambda().eq(ChatGptUserInfo::getName, chatGptUserInfoParam.getName());
            }
            // 根据密码 查询
            if (ObjectUtil.isNotEmpty(chatGptUserInfoParam.getPassword())) {
                queryWrapper.lambda().eq(ChatGptUserInfo::getPassword, chatGptUserInfoParam.getPassword());
            }
            // 根据邀请码 查询
            if (ObjectUtil.isNotEmpty(chatGptUserInfoParam.getInviteCode())) {
                queryWrapper.lambda().eq(ChatGptUserInfo::getInviteCode, chatGptUserInfoParam.getInviteCode());
            }
            // 根据剩余聊天次数 查询
            if (ObjectUtil.isNotEmpty(chatGptUserInfoParam.getChatNum())) {
                queryWrapper.lambda().eq(ChatGptUserInfo::getChatNum, chatGptUserInfoParam.getChatNum());
            }
            // 根据剩余绘画次数 查询
            if (ObjectUtil.isNotEmpty(chatGptUserInfoParam.getDrawNum())) {
                queryWrapper.lambda().eq(ChatGptUserInfo::getDrawNum, chatGptUserInfoParam.getDrawNum());
            }
            // 根据状态 查询
            if (ObjectUtil.isNotEmpty(chatGptUserInfoParam.getState())) {
                queryWrapper.lambda().eq(ChatGptUserInfo::getState, chatGptUserInfoParam.getState());
            }
        }
        return new PageResult<>(this.page(PageFactory.defaultPage(), queryWrapper));
    }

    @Override
    public List<ChatGptUserInfo> list(ChatGptUserInfoParam chatGptUserInfoParam) {
        return this.list();
    }

    @Override
    public void add(ChatGptUserInfoParam chatGptUserInfoParam) {
        ChatGptUserInfo chatGptUserInfo = new ChatGptUserInfo();
        BeanUtil.copyProperties(chatGptUserInfoParam, chatGptUserInfo);
        this.save(chatGptUserInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<ChatGptUserInfoParam> chatGptUserInfoParamList) {
        chatGptUserInfoParamList.forEach(chatGptUserInfoParam -> {
        ChatGptUserInfo chatGptUserInfo = this.queryChatGptUserInfo(chatGptUserInfoParam);
            this.removeById(chatGptUserInfo.getId());
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void edit(ChatGptUserInfoParam chatGptUserInfoParam) {
        ChatGptUserInfo chatGptUserInfo = this.queryChatGptUserInfo(chatGptUserInfoParam);
        BeanUtil.copyProperties(chatGptUserInfoParam, chatGptUserInfo);
        this.updateById(chatGptUserInfo);
    }

    @Override
    public ChatGptUserInfo detail(ChatGptUserInfoParam chatGptUserInfoParam) {
        return this.queryChatGptUserInfo(chatGptUserInfoParam);
    }

    @Override
    public ChatGptUserInfoResp getUserInfo(String email) {
        LambdaQueryWrapper<ChatGptUserInfo> queryWrapper = new LambdaQueryWrapper<ChatGptUserInfo>()
                .eq(ChatGptUserInfo::getEmail, email);
        ChatGptUserInfo chatGptUserInfo = this.getOne(queryWrapper);
        ChatGptUserInfoResp resp = new ChatGptUserInfoResp();
        BeanUtils.copyProperties(chatGptUserInfo, resp);
        redisTemplate.opsForValue().set(CommonConstant.CHAT_USER_INFO + email, resp, 5 * 60, TimeUnit.SECONDS);
        return resp;
    }

    /**
     * 获取会员信息
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
    private ChatGptUserInfo queryChatGptUserInfo(ChatGptUserInfoParam chatGptUserInfoParam) {
        ChatGptUserInfo chatGptUserInfo = this.getById(chatGptUserInfoParam.getId());
        if (ObjectUtil.isNull(chatGptUserInfo)) {
            throw new ServiceException(ChatGptUserInfoExceptionEnum.NOT_EXIST);
        }
        return chatGptUserInfo;
    }
}
