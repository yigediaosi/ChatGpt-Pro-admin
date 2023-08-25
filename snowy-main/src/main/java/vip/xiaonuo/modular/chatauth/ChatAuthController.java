package vip.xiaonuo.modular.chatauth;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.log.Log;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.core.consts.CommonConstant;
import vip.xiaonuo.core.context.requestno.RequestNoContext;
import vip.xiaonuo.core.email.MailSender;
import vip.xiaonuo.core.email.modular.model.SendMailParam;
import vip.xiaonuo.core.exception.ServiceException;
import vip.xiaonuo.core.pojo.login.ChatAuth;
import vip.xiaonuo.core.pojo.response.ResponseData;
import vip.xiaonuo.core.pojo.response.SuccessResponseData;
import vip.xiaonuo.core.util.IdGen;
import vip.xiaonuo.modular.gptuserinfo.param.ChatGptUserInfoParam;
import vip.xiaonuo.modular.gptuserinfo.service.ChatGptUserInfoService;
import vip.xiaonuo.sys.modular.email.enums.SysEmailExceptionEnum;
import javax.annotation.Resource;
import javax.mail.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 *
 * 前端登陆验证
 * @author 981743898@qq.com
 */
@Controller
@RequestMapping("/chatAuth")
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
        if (ObjectUtil.isEmpty(chatAuthParam.getEmail())) {
            throw new ServiceException(SysEmailExceptionEnum.EMAIL_TO_EMPTY);
        }
        SendMailParam sendMailParam = new SendMailParam();
        sendMailParam.setTo(chatAuthParam.getEmail());
        String title = "chatGpt Pro 邮箱验证";
        sendMailParam.setTitle(title);
        Random random = new Random();
        int randomNumber = random.nextInt(9000) + 1000;
        sendMailParam.setContent(randomNumber+"");
        try {
            mailSender.sendMail163(sendMailParam);
        } catch (MessagingException e) {
            log.error(">>> 邮件发送异常，请求号为：{}，具体信息为：{}", RequestNoContext.get(), e.getMessage());
            throw new ServiceException(SysEmailExceptionEnum.EMAIL_SEND_ERROR);
        }
        redisTemplate.opsForValue().set(CommonConstant.CHAT_AUTH_EMAIL_CODE + chatAuthParam.getEmail(), randomNumber,60, TimeUnit.SECONDS);
        return new SuccessResponseData();
    }



    @PostMapping("/login")
    @ResponseBody
    public ResponseData chatLogin(@RequestBody ChatAuth chatAuthParam) {
        Integer code =  (Integer) redisTemplate.opsForValue().get(CommonConstant.CHAT_AUTH_EMAIL_CODE + chatAuthParam.getEmail());

        if (null == code || Integer.parseInt(chatAuthParam.getCode()) != code){
            return new SuccessResponseData(900, "验证码已失效或发送失败，请重新获取验证码", null);
        }
        ChatGptUserInfoParam chatGptUserInfoParam = new ChatGptUserInfoParam();
        chatGptUserInfoParam.setChatNum(20);
        chatGptUserInfoParam.setDrawNum(20);
        chatGptUserInfoParam.setEmail(chatAuthParam.getEmail());
        String iCode = IdGen.getUUID(6);
        chatGptUserInfoParam.setInviteCode(iCode);
        //用户名密码暂时用不到，目前使用邮箱+验证码登陆
        chatGptUserInfoParam.setName("用户"+iCode);
        chatGptUserInfoParam.setPassword(iCode+123);
        chatGptUserInfoParam.setState(0);
        chatGptUserInfoService.add(chatGptUserInfoParam);
        return new SuccessResponseData();
    }

}
