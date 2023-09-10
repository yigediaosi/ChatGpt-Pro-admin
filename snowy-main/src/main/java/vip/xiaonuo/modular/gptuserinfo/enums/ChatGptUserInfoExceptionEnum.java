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
package vip.xiaonuo.modular.gptuserinfo.enums;

import vip.xiaonuo.core.annotion.ExpEnumType;
import vip.xiaonuo.core.exception.enums.abs.AbstractBaseExceptionEnum;
import vip.xiaonuo.core.factory.ExpEnumCodeFactory;
import vip.xiaonuo.sys.core.consts.SysExpEnumConstant;

/**
 * 会员信息
 *
 * @author 981743898@qq.com
 * @date 2023-08-21 21:49:44
 */
@ExpEnumType(module = SysExpEnumConstant.SNOWY_SYS_MODULE_EXP_CODE, kind = SysExpEnumConstant.SYS_POS_EXCEPTION_ENUM)
public enum ChatGptUserInfoExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 数据不存在
     */
    NOT_EXIST(1, "此数据不存在"),

    CODE_IS_EXPIRE(900, "验证码已失效或发送失败，请重新获取验证码"),
    EMAIL_IS_EXIST(901, "邮箱已注册"),
    SEND_FREQUENTLY(902, "发送过于频繁，请等待1分钟后再试"),
    ACCOUNT_IS_LOCK(903, "账号被锁定"),
    USER_IS_EXIST(904, "用户不存在，请先注册"),
    PASS_ERROR_TIME(905, "账密或密码错误，剩余[{0}]次机会"),

    INVITATION_CODE_IS_NOT_EXIST(906, "邀请码不存在"),
    LIMITED_RESOURCES(907, "因为资源有限，本项目完全免费，所以{0}每天限制{1}次，请明天再来~"),
    SIGNED_IN(908, "今天已经签过到了~"),
    DRAW_ERROR(909, "积分不足{0}，请明日再来~"),

    ;

    private final Integer code;

    private final String message;

    ChatGptUserInfoExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return ExpEnumCodeFactory.getExpEnumCode(this.getClass(), code);
    }

    @Override
    public String getMessage() {
            return message;
    }

}
