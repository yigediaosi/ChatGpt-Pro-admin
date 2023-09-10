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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import vip.xiaonuo.core.consts.CommonConstant;
import vip.xiaonuo.core.email.MailSender;
import vip.xiaonuo.core.email.modular.model.SendMailParam;
import vip.xiaonuo.core.exception.ServiceException;
import vip.xiaonuo.core.factory.PageFactory;
import vip.xiaonuo.core.pojo.login.ChatAuth;
import vip.xiaonuo.core.pojo.login.ChatCheck;
import vip.xiaonuo.core.pojo.login.ChatRegister;
import vip.xiaonuo.core.pojo.page.PageResult;
import vip.xiaonuo.core.util.AESUtil;
import vip.xiaonuo.core.util.IdGen;
import vip.xiaonuo.modular.gptuserinfo.entity.ChatGptUserInfo;
import vip.xiaonuo.modular.gptuserinfo.entity.ChatGptUserInfoResp;
import vip.xiaonuo.modular.gptuserinfo.entity.InviteRecord;
import vip.xiaonuo.modular.gptuserinfo.enums.ChatGptUserInfoExceptionEnum;
import vip.xiaonuo.modular.gptuserinfo.mapper.ChatGptUserInfoMapper;
import vip.xiaonuo.modular.gptuserinfo.param.ChatGptUserInfoParam;
import vip.xiaonuo.modular.gptuserinfo.param.InviteRecordParam;
import vip.xiaonuo.modular.gptuserinfo.service.ChatGptUserInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vip.xiaonuo.modular.gptuserinfo.service.InviteRecordService;
import vip.xiaonuo.sys.modular.email.enums.SysEmailExceptionEnum;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Resource
    private MailSender mailSender;

    @Resource
    private InviteRecordService inviteRecordService;

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
        LambdaQueryWrapper<InviteRecord> inviteRecordLambdaQueryWrapper = new LambdaQueryWrapper<InviteRecord>()
                .eq(InviteRecord::getEmail, email);
        resp.setInviteRecordCount(inviteRecordService.count(inviteRecordLambdaQueryWrapper));
        redisTemplate.opsForValue().set(CommonConstant.CHAT_USER_INFO + email, resp, 5 * 60, TimeUnit.SECONDS);
        return resp;
    }


    // 脱敏邮箱
    private String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return email;
        }

        String[] parts = email.split("@");
        if (parts.length != 2) {
            // 不是有效的邮箱地址，不进行脱敏
            return email;
        }

        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 2) {
            // 用户名太短，不进行脱敏
            return email;
        }

        // 脱敏处理用户名部分
        StringBuilder maskedUsername = new StringBuilder();
        maskedUsername.append(username.charAt(0));
        // 保留第一个字符
        for (int i = 1; i < username.length() - 1; i++) {
            // 用星号替代中间字符
            maskedUsername.append('*');
        }
        // 保留最后一个字符
        maskedUsername.append(username.charAt(username.length() - 1));

        return maskedUsername.toString() + "@" + domain;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(ChatRegister chatRegister) {
        Integer code = (Integer) redisTemplate.opsForValue().get(CommonConstant.CHAT_AUTH + chatRegister.getEmail());

        if (null == code || Integer.parseInt(chatRegister.getCode()) != code) {
            throw new ServiceException(ChatGptUserInfoExceptionEnum.CODE_IS_EXPIRE);
        }
        LambdaQueryWrapper<ChatGptUserInfo> queryWrapper = new LambdaQueryWrapper<ChatGptUserInfo>()
                .eq(ChatGptUserInfo::getEmail, chatRegister.getEmail());
        ChatGptUserInfo chatGptUserInfo = this.getOne(queryWrapper);
        if (null != chatGptUserInfo) {
            throw new ServiceException(ChatGptUserInfoExceptionEnum.EMAIL_IS_EXIST);
        }

        Integer integral = 0;
        // 邀请码不为空 邀请人+10 被邀请人+5
        if (StringUtils.isNotBlank(chatRegister.getInviteCode())) {
            integral = 10;
            LambdaQueryWrapper<ChatGptUserInfo> queryWrapper2 = new LambdaQueryWrapper<ChatGptUserInfo>()
                    .eq(ChatGptUserInfo::getInviteCode, chatRegister.getInviteCode());
            ChatGptUserInfo chatGptUserInfo2 = this.getOne(queryWrapper2);
            if (null == chatGptUserInfo2) {
                throw new ServiceException(ChatGptUserInfoExceptionEnum.INVITATION_CODE_IS_NOT_EXIST);
            }
            ChatGptUserInfoParam chatGptUserInfoParam = new ChatGptUserInfoParam();
            chatGptUserInfoParam.setIntegral(chatGptUserInfoParam.getIntegral() + integral);
            this.updateById(chatGptUserInfo2);
            // 新增邀请记录
            InviteRecordParam inviteRecordParam = new InviteRecordParam();
            inviteRecordParam.setEmail(chatGptUserInfo2.getEmail());
            inviteRecordParam.setInvitedEmail(chatRegister.getEmail());
            inviteRecordService.add(inviteRecordParam);
        }
        ChatGptUserInfoParam chatGptUserInfoParam = new ChatGptUserInfoParam();
        chatGptUserInfoParam.setEmail(chatRegister.getEmail());
        String iCode = IdGen.getUUID(6);
        chatGptUserInfoParam.setInviteCode(iCode);

        chatGptUserInfoParam.setIntegral(integral > 0 ? 5 : 0 + 10);
        //用户名密码暂时用不到，目前使用邮箱+验证码登陆
        chatGptUserInfoParam.setName("用户" + iCode);
        String passEnc = AESUtil.encrypt(chatRegister.getPassword());
        chatGptUserInfoParam.setPassword(passEnc);
        chatGptUserInfoParam.setState(0);
        this.add(chatGptUserInfoParam);
    }

    @Override
    public Integer checkIn(ChatAuth chatAuth) {
        String code = (String) redisTemplate.opsForValue().get(CommonConstant.CHAT_SIGN_IN + chatAuth.getEmail());
        if (StringUtils.isNotBlank(code)) {
            throw new ServiceException(ChatGptUserInfoExceptionEnum.SIGNED_IN);
        }
        LambdaQueryWrapper<ChatGptUserInfo> queryWrapper = new LambdaQueryWrapper<ChatGptUserInfo>()
                .eq(ChatGptUserInfo::getEmail, chatAuth.getEmail());
        ChatGptUserInfo chatGptUserInfo = this.getOne(queryWrapper);
        if (null == chatGptUserInfo) {
            throw new ServiceException(ChatGptUserInfoExceptionEnum.USER_IS_EXIST);
        }
        if (null != chatGptUserInfo.getCheckinTime()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            // 获取当前日期
            Date currentDate = new Date();
            Date dateToCompare = chatGptUserInfo.getCheckinTime();
            String currentDateString = dateFormat.format(currentDate);
            String dateToCompareString = dateFormat.format(dateToCompare);
            if (currentDateString.equals(dateToCompareString)) {
                throw new ServiceException(ChatGptUserInfoExceptionEnum.SIGNED_IN);
            }
        }
        chatGptUserInfo.setCheckinTime(new Date());
        // 每次签到+5
        Integer currentIntegral = chatGptUserInfo.getIntegral() + 5;
        chatGptUserInfo.setIntegral(chatGptUserInfo.getIntegral() + 5);
        this.updateById(chatGptUserInfo);

        long midnightSeconds = calculateSecondsUntilMidnight();
        redisTemplate.opsForValue().set(CommonConstant.CHAT_SIGN_IN + chatAuth.getEmail(), "signed", midnightSeconds, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(CommonConstant.CHAT_USER_INFO + chatAuth.getEmail(), chatGptUserInfo, 5 * 60, TimeUnit.SECONDS);
        return currentIntegral;
    }

    /**
     * 计算距离凌晨12点的剩余时间（以秒为单位）
     *
     * @return
     */
    private long calculateSecondsUntilMidnight() {
        Date now = new Date();
        Date midnight = new Date();
        midnight.setHours(23);
        midnight.setMinutes(59);
        midnight.setSeconds(59);

        long secondsUntilMidnight = (midnight.getTime() - now.getTime()) / 1000;
        return secondsUntilMidnight;
    }

    @Override
    public Integer refresh(ChatAuth chatAuth) {
        LambdaQueryWrapper<ChatGptUserInfo> queryWrapper = new LambdaQueryWrapper<ChatGptUserInfo>()
                .eq(ChatGptUserInfo::getEmail, chatAuth.getEmail());
        ChatGptUserInfo chatGptUserInfo = this.getOne(queryWrapper);
        return null != chatGptUserInfo ? chatGptUserInfo.getIntegral() : 0;
    }

    @Override
    public void sendEmail(ChatAuth chatAuthParam) {
        Integer code = (Integer) redisTemplate.opsForValue().get(CommonConstant.CHAT_AUTH + chatAuthParam.getEmail());
        if (null != code) {
            throw new ServiceException(ChatGptUserInfoExceptionEnum.SEND_FREQUENTLY);
        }
        if (ObjectUtil.isEmpty(chatAuthParam.getEmail())) {
            throw new ServiceException(SysEmailExceptionEnum.EMAIL_TO_EMPTY);
        }
        SendMailParam sendMailParam = new SendMailParam();
        sendMailParam.setTo(chatAuthParam.getEmail());
        String title = "chatGpt Pro 邮箱验证";
        sendMailParam.setTitle(title);
        Random random = new Random();
        int randomNumber = random.nextInt(9000) + 1000;
        sendMailParam.setContent(randomNumber + "");
        mailSender.sendMailQQ(sendMailParam);
        redisTemplate.opsForValue().set(CommonConstant.CHAT_AUTH + chatAuthParam.getEmail(), randomNumber, 60, TimeUnit.SECONDS);

    }

    @Override
    public void login(ChatAuth chatAuthParam) {
        Boolean state = (Boolean) redisTemplate.opsForValue().get(CommonConstant.CHAT_AUTH_LOGIN_STATE + chatAuthParam.getEmail());
        if (null != state && state) {
            throw new ServiceException(ChatGptUserInfoExceptionEnum.ACCOUNT_IS_LOCK);
        }
        LambdaQueryWrapper<ChatGptUserInfo> queryWrapper = new LambdaQueryWrapper<ChatGptUserInfo>()
                .eq(ChatGptUserInfo::getEmail, chatAuthParam.getEmail());
        ChatGptUserInfo chatGptUserInfo = this.getOne(queryWrapper);
        if (null == chatGptUserInfo) {
            throw new ServiceException(ChatGptUserInfoExceptionEnum.USER_IS_EXIST);
        }
        if (chatGptUserInfo.getState() == 1) {
            redisTemplate.opsForValue().set(CommonConstant.CHAT_AUTH_LOGIN_STATE + chatAuthParam.getEmail(), true, 5 * 60, TimeUnit.SECONDS);
            throw new ServiceException(ChatGptUserInfoExceptionEnum.ACCOUNT_IS_LOCK);
        }
        String passEnc = AESUtil.encrypt(chatAuthParam.getPassword());
        if (!chatGptUserInfo.getPassword().equals(passEnc)) {
            Integer num = (Integer) redisTemplate.opsForValue().get(CommonConstant.CHAT_AUTH_LOGIN_TIME + chatAuthParam.getEmail());
            num = null == num ? 1 : num + 1;
            if (num == 5) {
                chatGptUserInfo.setState(1);
                this.updateById(chatGptUserInfo);
                redisTemplate.opsForValue().set(CommonConstant.CHAT_AUTH_LOGIN_STATE + chatAuthParam.getEmail(), true, 5 * 60, TimeUnit.SECONDS);
                throw new ServiceException(ChatGptUserInfoExceptionEnum.ACCOUNT_IS_LOCK);
            }
            redisTemplate.opsForValue().set(CommonConstant.CHAT_AUTH_LOGIN_TIME + chatAuthParam.getEmail(), num, 5 * 60, TimeUnit.SECONDS);
            throw new ServiceException(ChatGptUserInfoExceptionEnum.PASS_ERROR_TIME, 5 - num);
        }
        chatGptUserInfo.setLastLoginTime(new Date());
        this.updateById(chatGptUserInfo);
    }

    @Override
    public void check(ChatCheck chatCheck) {

        LambdaQueryWrapper<ChatGptUserInfo> queryWrapper = new LambdaQueryWrapper<ChatGptUserInfo>()
                .eq(ChatGptUserInfo::getEmail, chatCheck.getEmail());
        ChatGptUserInfo chatGptUserInfo = this.getOne(queryWrapper);
        if (null == chatGptUserInfo) {
            throw new ServiceException(ChatGptUserInfoExceptionEnum.USER_IS_EXIST);
        }
        if (chatGptUserInfo.getState() == 1) {
            throw new ServiceException(ChatGptUserInfoExceptionEnum.ACCOUNT_IS_LOCK);
        }
        // 校验绘画/对话次数
        check(chatGptUserInfo, chatCheck.getUserInput().startsWith("/mj") ? true : false);

        // TODO 后续加上聊天信息过滤
        if (chatCheck.getUserInput().startsWith("/mj")) {
            // 每次绘画扣5分
            chatGptUserInfo.setIntegral(chatGptUserInfo.getIntegral() - 5);
        } else {
            // 每次聊天扣1分
            chatGptUserInfo.setIntegral(chatGptUserInfo.getIntegral() - 1);
        }

        this.updateById(chatGptUserInfo);
    }

    private void check(ChatGptUserInfo chatGptUserInfo, boolean isDraw) {
        if (isDraw && chatGptUserInfo.getIntegral() < 5) {
            throw new ServiceException(ChatGptUserInfoExceptionEnum.LIMITED_RESOURCES, "绘画", 2);
        }
        if (chatGptUserInfo.getIntegral() < 1) {
            throw new ServiceException(ChatGptUserInfoExceptionEnum.LIMITED_RESOURCES, "对话", 10);
        }
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
