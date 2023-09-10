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
package vip.xiaonuo.core.email.modular;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.sun.mail.util.MailSSLSocketFactory;
import vip.xiaonuo.core.email.MailSender;
import vip.xiaonuo.core.email.modular.exception.MailSendException;
import vip.xiaonuo.core.email.modular.model.SendMailParam;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.Properties;

/**
 * 邮件发送器
 *
 * @author xuyuxiang
 * @date 2020/6/9 22:54
 */
public class SimpleMailSender implements MailSender {

    /**
     * 邮件配置
     */
    private final MailAccount mailAccount;

    public SimpleMailSender(MailAccount mailAccount) {
        this.mailAccount = mailAccount;
    }

    @Override
    public void sendMail(SendMailParam sendMailParam) {

        //校验发送邮件的参数
        assertSendMailParams(sendMailParam);

        //spring发送邮件
        MailUtil.send(mailAccount, CollUtil.newArrayList(sendMailParam.getTo()), sendMailParam.getTitle(), sendMailParam.getContent(), false);
    }

    @Override
    public void sendMail163(SendMailParam sendMailParam) throws MessagingException {
        //属性集
        Properties p = new Properties();
        p.setProperty("mail.smtp.host", mailAccount.getHost());
        p.setProperty("mail.smtp.port", mailAccount.getPort()+"");
        p.setProperty("mail.smtp.socketFactory.port", mailAccount.getPort()+"");
        p.setProperty("mail.smtp.auth", "true");
        p.setProperty("mail.smtp.socketFactory.class", "SSL_FACTORY");
        //获取连接
        Session session = Session.getInstance(p, new Authenticator() {
            // 设置认证账户信息
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailAccount.getUser(), mailAccount.getPass());
            }
        });
        session.setDebug(false);
        MimeMessage message = new MimeMessage(session);
        // 发件人
        message.setFrom(new InternetAddress(mailAccount.getFrom()));
        // 收件人和抄送人
        message.setRecipients(Message.RecipientType.TO, sendMailParam.getTo());
        // 内容(这个内容还不能乱写,有可能会被SMTP拒绝掉;多试几次吧)
        message.setSubject(sendMailParam.getTitle());
        message.setContent(sendMailParam.getContent(), "text/html;charset=UTF-8");
        message.setSentDate(new Date());
        message.saveChanges();//保存设置
        Transport.send(message);

    }

    @Override
    public void sendMailQQ(SendMailParam sendMailParam){
        //封装配置信息
        //实例化配置文件，也可以在外面写xxx.properties文件再引入配置
        Properties properties = new Properties();
        //设置QQ邮件服务器
        properties.setProperty("mail.host","smtp.qq.com");
        //设置邮件发送协议
        properties.setProperty("mail.transport.protocol","smtp");
        //需要验证用户名密码
        properties.setProperty("mail.smtp.auth","true");
        Transport ts = null;
        try {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            properties.put("mail.smtp.ssl.enable","true");
            properties.put("mail.smtp.ssl.socketFactory",sf);

            //1.创建定义整个应用程序所需的环境信息的Session对象
            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    //发件人邮件用户名、授权码
                    return new PasswordAuthentication(mailAccount.getUser(), mailAccount.getPass());
                }
            });
            //开启session的debug模式，这样就可以查看程序发送Email的运行状态
            //session.setDebug(true);
            //2.通过session得到transport对象
            ts = session.getTransport();
            //3.使用邮箱的用户名和授权码连上邮件服务器
            ts.connect(mailAccount.getHost(), mailAccount.getUser(), mailAccount.getPass());
            //4.创建邮件:写邮件
            //注意需要传递session
            MimeMessage message = new MimeMessage(session);
            //指明邮件发件人
            message.setFrom(new InternetAddress(mailAccount.getUser()));
            //指明邮件的收件人，现在的发件人和收件人是一样，就是给自己发邮件 收件人
            message.setRecipient(Message.RecipientType.TO,new InternetAddress(sendMailParam.getTo()));
            //邮件的标题
            message.setSubject(sendMailParam.getTitle());
            //邮件的文本内容,第二个参数代表前面文本支持html标签，字符编码为utf-8
            message.setContent(sendMailParam.getContent(),"text/html;charset=UTF-8");
            //5.发送邮件
            ts.sendMessage(message,message.getAllRecipients());

        } catch (GeneralSecurityException | NoSuchProviderException | AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }finally {
            //6.关闭连接
            if ( null != ts){
                try {
                    ts.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void sendMailHtml(SendMailParam sendMailParam) {

        //校验发送邮件的参数
        assertSendMailParams(sendMailParam);

        //spring发送邮件
        MailUtil.send(mailAccount, CollUtil.newArrayList(sendMailParam.getTo()), sendMailParam.getTitle(), sendMailParam.getContent(), true);
    }

    /**
     * 校验发送邮件的请求参数
     *
     * @author xuyuxiang
     * @date 2018/7/8 下午6:41
     */
    private void assertSendMailParams(SendMailParam sendMailParam) {
        if (sendMailParam == null) {
            throw new MailSendException(400, "请求参数为空");
        }

        if (ObjectUtil.isEmpty(sendMailParam.getTo())) {
            throw new MailSendException(400, "收件人邮箱为空");
        }

        if (ObjectUtil.isEmpty(sendMailParam.getTitle())) {
            throw new MailSendException(400, "邮件标题为空");
        }

        if (ObjectUtil.isEmpty(sendMailParam.getContent())) {
            throw new MailSendException(400, "邮件内容为空");
        }
    }

}
