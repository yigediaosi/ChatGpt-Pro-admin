package vip.xiaonuo.modular.chatauth;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.core.consts.CommonConstant;
import vip.xiaonuo.core.context.requestno.RequestNoContext;
import vip.xiaonuo.core.email.MailSender;
import vip.xiaonuo.core.email.modular.model.SendMailParam;
import vip.xiaonuo.core.exception.ServiceException;
import vip.xiaonuo.core.pojo.login.ChatAuth;
import vip.xiaonuo.core.pojo.login.ChatCheck;
import vip.xiaonuo.core.pojo.login.ChatRegister;
import vip.xiaonuo.core.pojo.response.ResponseData;
import vip.xiaonuo.core.pojo.response.SuccessResponseData;
import vip.xiaonuo.core.util.AESUtil;
import vip.xiaonuo.core.util.IdGen;
import vip.xiaonuo.modular.gptuserinfo.entity.ChatGptUserInfo;
import vip.xiaonuo.modular.gptuserinfo.param.ChatGptUserInfoParam;
import vip.xiaonuo.modular.gptuserinfo.service.ChatGptUserInfoService;
import vip.xiaonuo.sys.modular.email.enums.SysEmailExceptionEnum;
import javax.annotation.Resource;
import javax.mail.*;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 前端登陆验证
 *
 * @author 981743898@qq.com
 */
@Controller
@RequestMapping("/chatAuth")
@CrossOrigin
public class ChatAuthController {

    private static final Log log = Log.get();

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ChatGptUserInfoService chatGptUserInfoService;

    @Resource
    private MailSender mailSender;

    @PostMapping("/sendEmail")
    @ResponseBody
    public ResponseData sendEmail(@RequestBody ChatAuth chatAuthParam) {
        Integer code = (Integer) redisTemplate.opsForValue().get(CommonConstant.CHAT_AUTH + chatAuthParam.getEmail());
        if (null != code){
            return new SuccessResponseData(901, "发送过于频繁，请等待1分钟后再试", null);
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
        return new SuccessResponseData();
    }


    @PostMapping("/login")
    @ResponseBody
    public ResponseData login(@RequestBody ChatAuth chatAuthParam) {

        Boolean state = (Boolean) redisTemplate.opsForValue().get(CommonConstant.CHAT_AUTH_LOGIN_STATE + chatAuthParam.getEmail());
        if (null != state && state){
            return new SuccessResponseData(901, "账号被锁定", null);
        }
        LambdaQueryWrapper<ChatGptUserInfo> queryWrapper = new LambdaQueryWrapper<ChatGptUserInfo>()
                .eq(ChatGptUserInfo::getEmail, chatAuthParam.getEmail());
        ChatGptUserInfo chatGptUserInfo = chatGptUserInfoService.getOne(queryWrapper);
        if (null == chatGptUserInfo) {
            return new SuccessResponseData(900, "用户不存在，请先注册", null);
        }
        if (chatGptUserInfo.getState() == 1) {
            redisTemplate.opsForValue().set(CommonConstant.CHAT_AUTH_LOGIN_STATE + chatAuthParam.getEmail(), true, 72 * 60 * 60, TimeUnit.SECONDS);
            return new SuccessResponseData(901, "账号被锁定", null);
        }
        String passEnc = AESUtil.encrypt(chatAuthParam.getPassword());
        if (!chatGptUserInfo.getPassword().equals(passEnc)) {
            Integer num = (Integer) redisTemplate.opsForValue().get(CommonConstant.CHAT_AUTH_LOGIN_TIME + chatAuthParam.getEmail());
            num = null == num ? 1 : num + 1;
            if (num == 5) {
                chatGptUserInfo.setState(1);
                chatGptUserInfoService.updateById(chatGptUserInfo);
                redisTemplate.opsForValue().set(CommonConstant.CHAT_AUTH_LOGIN_STATE + chatAuthParam.getEmail(), true, 72 * 60 * 60, TimeUnit.SECONDS);
                return new SuccessResponseData(901, "账号被锁定", null);
            }
            redisTemplate.opsForValue().set(CommonConstant.CHAT_AUTH_LOGIN_TIME + chatAuthParam.getEmail(), num, 5 * 60, TimeUnit.SECONDS);
            return new SuccessResponseData(902, "账密或密码错误，剩余[" + (5 - num) + "]次机会", null);
        }
        chatGptUserInfo.setLastLoginTime(new Date());
        chatGptUserInfoService.updateById(chatGptUserInfo);
        return new SuccessResponseData();
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseData register(@RequestBody ChatRegister chatRegister) {
        Integer code = (Integer) redisTemplate.opsForValue().get(CommonConstant.CHAT_AUTH + chatRegister.getEmail());

        if (null == code || Integer.parseInt(chatRegister.getCode()) != code) {
            return new SuccessResponseData(900, "验证码已失效或发送失败，请重新获取验证码", null);
        }
        ChatGptUserInfoParam chatGptUserInfoParam = new ChatGptUserInfoParam();
        chatGptUserInfoParam.setChatNum(20);
        chatGptUserInfoParam.setDrawNum(3);
        chatGptUserInfoParam.setEmail(chatRegister.getEmail());
        String iCode = IdGen.getUUID(6);
        chatGptUserInfoParam.setInviteCode(iCode);
        //用户名密码暂时用不到，目前使用邮箱+验证码登陆
        chatGptUserInfoParam.setName("用户" + iCode);
        String passEnc = AESUtil.encrypt(chatRegister.getPassword());
        chatGptUserInfoParam.setPassword(passEnc);
        chatGptUserInfoParam.setState(0);
        chatGptUserInfoService.add(chatGptUserInfoParam);
        return new SuccessResponseData();
    }

    @PostMapping("/check")
    @ResponseBody
    public ResponseData check(@RequestBody ChatCheck chatCheck) {
        Boolean state = (Boolean) redisTemplate.opsForValue().get(CommonConstant.CHAT_AUTH_LOGIN_STATE + chatCheck.getEmail());
        if (null != state && state){
            return new SuccessResponseData(901, "账号被锁定", null);
        }

        LambdaQueryWrapper<ChatGptUserInfo> queryWrapper = new LambdaQueryWrapper<ChatGptUserInfo>()
                .eq(ChatGptUserInfo::getEmail, chatCheck.getEmail());
        ChatGptUserInfo chatGptUserInfo = chatGptUserInfoService.getOne(queryWrapper);
        if (null == chatGptUserInfo) {
            return new SuccessResponseData(900, "用户不存在，请先注册", null);
        }
        if (chatGptUserInfo.getState() == 1) {
            return new SuccessResponseData(901, "账号被锁定", null);
        }
        // TODO 后续加上聊天信息过滤
        // mj绘画
        if (chatCheck.getUserInput().startsWith("/mj")){
            if (chatGptUserInfo.getDrawNum() < 1){
                return new SuccessResponseData(903, "因为资源有限，本项目完全免费，所以绘画每天限制3次，请明天再来！", null);
            }
            chatGptUserInfo.setDrawNum(chatGptUserInfo.getDrawNum() - 1);
        }else {
            if (chatGptUserInfo.getChatNum() < 1){
                return new SuccessResponseData(903, "因为资源有限，本项目完全免费，所以对话每天限制20次，请明天再来！", null);
            }
            chatGptUserInfo.setChatNum(chatGptUserInfo.getChatNum() - 1);
        }
        chatGptUserInfoService.updateById(chatGptUserInfo);

        return new SuccessResponseData();
    }
}
