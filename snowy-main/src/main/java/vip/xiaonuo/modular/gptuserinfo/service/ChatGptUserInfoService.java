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
package vip.xiaonuo.modular.gptuserinfo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import vip.xiaonuo.core.pojo.login.ChatAuth;
import vip.xiaonuo.core.pojo.login.ChatCheck;
import vip.xiaonuo.core.pojo.login.ChatRegister;
import vip.xiaonuo.core.pojo.page.PageResult;
import vip.xiaonuo.core.pojo.response.ResponseData;
import vip.xiaonuo.modular.gptuserinfo.entity.ChatGptUserInfo;
import vip.xiaonuo.modular.gptuserinfo.entity.ChatGptUserInfoResp;
import vip.xiaonuo.modular.gptuserinfo.param.ChatGptUserInfoParam;
import java.util.List;

/**
 * 会员信息service接口
 *
 * @author 981743898@qq.com
 * @date 2023-08-21 21:49:44
 */
public interface ChatGptUserInfoService extends IService<ChatGptUserInfo> {

    /**
     * 查询会员信息
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
    PageResult<ChatGptUserInfo> page(ChatGptUserInfoParam chatGptUserInfoParam);

    /**
     * 会员信息列表
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
    List<ChatGptUserInfo> list(ChatGptUserInfoParam chatGptUserInfoParam);

    /**
     * 添加会员信息
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
    void add(ChatGptUserInfoParam chatGptUserInfoParam);

    /**
     * 删除会员信息
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
    void delete(List<ChatGptUserInfoParam> chatGptUserInfoParamList);

    /**
     * 编辑会员信息
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
    void edit(ChatGptUserInfoParam chatGptUserInfoParam);

    /**
     * 查看会员信息
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
     ChatGptUserInfo detail(ChatGptUserInfoParam chatGptUserInfoParam);

    /**
     * 获取会员信息
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
     ChatGptUserInfoResp getUserInfo(String email);

    /**
     * 注册
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
    void register(ChatRegister chatRegister);

    /**
     * 签到
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
    Integer checkIn(ChatAuth chatAuth);

    /**
     * 签到
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
    Integer refresh(ChatAuth chatAuth);

    /**
     * 发送邮件
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
    void sendEmail(ChatAuth chatAuthParam);

    /**
     * 登陆
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
    void login(ChatAuth chatAuthParam);

    /**
     * 用户校验
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
    void check(ChatCheck chatCheck);
}
