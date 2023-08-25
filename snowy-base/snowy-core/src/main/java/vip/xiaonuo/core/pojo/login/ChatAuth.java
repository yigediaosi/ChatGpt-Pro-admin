package vip.xiaonuo.core.pojo.login;

import lombok.Data;
import java.io.Serializable;

/**
 * @author 981743898@qq.com
 */
@Data
public class ChatAuth implements Serializable {

    // 验证码
    private String code;

    private String email;

}
