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
package vip.xiaonuo.modular.gptuserinfo.controller;

import vip.xiaonuo.core.annotion.BusinessLog;
import vip.xiaonuo.core.annotion.Permission;
import vip.xiaonuo.core.enums.LogAnnotionOpTypeEnum;
import vip.xiaonuo.core.pojo.page.PageResult;
import vip.xiaonuo.core.pojo.response.ResponseData;
import vip.xiaonuo.core.pojo.response.SuccessResponseData;
import vip.xiaonuo.modular.gptuserinfo.entity.ChatGptUserInfo;
import vip.xiaonuo.modular.gptuserinfo.param.ChatGptUserInfoParam;
import vip.xiaonuo.modular.gptuserinfo.service.ChatGptUserInfoService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import java.util.List;

/**
 * 会员信息控制器
 *
 * @author 981743898@qq.com
 * @date 2023-08-21 21:49:44
 */
@Controller
public class ChatGptUserInfoController {

    private String PATH_PREFIX = "chatGptUserInfo/";

    @Resource
    private ChatGptUserInfoService chatGptUserInfoService;

    /**
     * 会员信息页面
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
    @Permission
    @GetMapping("/chatGptUserInfo/index")
    public String index() {
        return PATH_PREFIX + "index.html";
    }

    /**
     * 会员信息表单页面
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
    @GetMapping("/chatGptUserInfo/form")
    public String form() {
        return PATH_PREFIX + "form.html";
    }

    /**
     * 查询会员信息
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
    @Permission
    @ResponseBody
    @GetMapping("/chatGptUserInfo/page")
    @BusinessLog(title = "会员信息_查询", opType = LogAnnotionOpTypeEnum.QUERY)
    public PageResult<ChatGptUserInfo> page(ChatGptUserInfoParam chatGptUserInfoParam) {
        return chatGptUserInfoService.page(chatGptUserInfoParam);
    }

    /**
     * 添加会员信息
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
    @Permission
    @ResponseBody
    @PostMapping("/chatGptUserInfo/add")
    @BusinessLog(title = "会员信息_增加", opType = LogAnnotionOpTypeEnum.ADD)
    public ResponseData add(@RequestBody @Validated(ChatGptUserInfoParam.add.class) ChatGptUserInfoParam chatGptUserInfoParam) {
        chatGptUserInfoService.add(chatGptUserInfoParam);
        return new SuccessResponseData();
    }

    /**
     * 删除会员信息
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
    @Permission
    @ResponseBody
    @PostMapping("/chatGptUserInfo/delete")
    @BusinessLog(title = "会员信息_删除", opType = LogAnnotionOpTypeEnum.DELETE)
    public ResponseData delete(@RequestBody @Validated(ChatGptUserInfoParam.delete.class) List<ChatGptUserInfoParam> chatGptUserInfoParamList) {
        chatGptUserInfoService.delete(chatGptUserInfoParamList);
        return new SuccessResponseData();
    }

    /**
     * 编辑会员信息
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
    @Permission
    @ResponseBody
    @PostMapping("/chatGptUserInfo/edit")
    @BusinessLog(title = "会员信息_编辑", opType = LogAnnotionOpTypeEnum.EDIT)
    public ResponseData edit(@RequestBody @Validated(ChatGptUserInfoParam.edit.class) ChatGptUserInfoParam chatGptUserInfoParam) {
        chatGptUserInfoService.edit(chatGptUserInfoParam);
        return new SuccessResponseData();
    }

    /**
     * 查看会员信息
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
    @Permission
    @ResponseBody
    @GetMapping("/chatGptUserInfo/detail")
    @BusinessLog(title = "会员信息_查看", opType = LogAnnotionOpTypeEnum.DETAIL)
    public ResponseData detail(@Validated(ChatGptUserInfoParam.detail.class) ChatGptUserInfoParam chatGptUserInfoParam) {
        return new SuccessResponseData(chatGptUserInfoService.detail(chatGptUserInfoParam));
    }

    /**
     * 会员信息列表
     *
     * @author 981743898@qq.com
     * @date 2023-08-21 21:49:44
     */
    @Permission
    @ResponseBody
    @GetMapping("/chatGptUserInfo/list")
    @BusinessLog(title = "会员信息_列表", opType = LogAnnotionOpTypeEnum.QUERY)
    public ResponseData list(ChatGptUserInfoParam chatGptUserInfoParam) {
        return new SuccessResponseData(chatGptUserInfoService.list(chatGptUserInfoParam));
    }

}
