package vip.xiaonuo.core.pojo.login;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author 981743898@qq.com
 */
@Data
public class ChatAuth implements Serializable {


    private String password;

    @NotNull(message = "邮箱不能为空")
    private String email;

}
