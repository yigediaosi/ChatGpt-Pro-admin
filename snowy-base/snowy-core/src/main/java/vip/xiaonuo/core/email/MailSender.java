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
package vip.xiaonuo.core.email;


import cn.hutool.extra.mail.MailException;
import vip.xiaonuo.core.email.modular.model.SendMailParam;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import java.security.GeneralSecurityException;

/**
 * 邮件收发统一接口
 *
 * @author xuyuxiang
 * @date 2018-07-08-下午3:26
 */
public interface MailSender {

    /**
     * 发送普通邮件
     *
     * @author xuyuxiang
     * @date 2018/7/8 下午3:34
     */
    void sendMail(SendMailParam sendMailParam);

    /**
     * 发送普通163邮件
     *
     * @author 981743898@qq.com
     * @date 2023-8-24 17:26
     */
    void sendMail163(SendMailParam sendMailParam) throws MessagingException;

    /**
     * 发送普通QQ邮件
     *
     * @author 981743898@qq.com
     * @date 2023-8-24 17:26
     */
    void sendMailQQ(SendMailParam sendMailParam);

    /**
     * 发送html的邮件
     *
     * @author xuyuxiang
     * @date 2020/6/9 22:58
     */
    void sendMailHtml(SendMailParam sendMailParam);

}
