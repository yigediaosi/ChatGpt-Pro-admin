package vip.xiaonuo.modular.chatauth;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vip.xiaonuo.core.consts.CommonConstant;
import vip.xiaonuo.core.pojo.login.ChatAuth;
import vip.xiaonuo.core.pojo.login.ChatCheck;
import vip.xiaonuo.core.pojo.login.ChatRegister;
import vip.xiaonuo.core.pojo.response.ResponseData;
import vip.xiaonuo.core.pojo.response.SuccessResponseData;
import vip.xiaonuo.modular.gptuserinfo.entity.ChatGptUserInfoResp;
import vip.xiaonuo.modular.gptuserinfo.service.ChatGptUserInfoService;
import javax.annotation.Resource;

/**
 * 前端登陆验证
 *
 * @author 981743898@qq.com
 */
@Controller
@RequestMapping("/chatAuth")
@CrossOrigin
public class ChatAuthController {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ChatGptUserInfoService chatGptUserInfoService;

    @PostMapping("/sendEmail")
    @ResponseBody
    public ResponseData sendEmail(@RequestBody ChatAuth chatAuthParam) {
        chatGptUserInfoService.sendEmail(chatAuthParam);
        return new SuccessResponseData();
    }


    @PostMapping("/login")
    @ResponseBody
    public ResponseData login(@RequestBody ChatAuth chatAuthParam) {
        chatGptUserInfoService.login(chatAuthParam);
        return new SuccessResponseData();
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseData register(@RequestBody ChatRegister chatRegister) {
        chatGptUserInfoService.register(chatRegister);
        return new SuccessResponseData();
    }


    /**
     * 聊天校验
     * @param chatCheck
     * @return
     */
    @PostMapping("/check")
    @ResponseBody
    public ResponseData check(@RequestBody ChatCheck chatCheck) {
        chatGptUserInfoService.check(chatCheck);
        return new SuccessResponseData();
    }


    /**
     * 获取用户信息
     * @param chat
     * @return
     */
    @PostMapping("/getUserInfo")
    @ResponseBody
    public ResponseData userInfo(@RequestBody ChatAuth chat) {
        ChatGptUserInfoResp resp = (ChatGptUserInfoResp) redisTemplate.opsForValue().get(CommonConstant.CHAT_USER_INFO + chat.getEmail());
        if (null == resp){
            resp = chatGptUserInfoService.getUserInfo(chat.getEmail());
        }
        return new SuccessResponseData(resp);
    }

    /**
     * 签到
     * @param chat
     * @return
     */
    @PostMapping("/checkIn")
    @ResponseBody
    public ResponseData checkIn(@RequestBody ChatAuth chat) {
        return new SuccessResponseData(chatGptUserInfoService.checkIn(chat));
    }

    /**
     * 刷新积分
     * @param chat
     * @return
     */
    @PostMapping("/refresh")
    @ResponseBody
    public ResponseData refresh(@RequestBody ChatAuth chat) {
        return new SuccessResponseData(chatGptUserInfoService.refresh(chat));
    }

}
