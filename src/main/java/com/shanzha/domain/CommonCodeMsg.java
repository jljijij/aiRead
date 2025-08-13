package com.shanzha.domain;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CommonCodeMsg extends CodeMsg {



    private CommonCodeMsg(Integer code, String msg){
        super(code,msg);
    }
    public static CommonCodeMsg NOT_FIND = new CommonCodeMsg(40400, "未找到小说");
    public static CommonCodeMsg NOT_SERIAL = new CommonCodeMsg(40401, "小说不处于连载状态");
    public static CommonCodeMsg IS_EMPTY = new CommonCodeMsg(40402, "文件分片为空");

    public static CommonCodeMsg IO_ERROR = new CommonCodeMsg(40403, "输入输出报错");
    public static CommonCodeMsg INSERT_ERROR = new CommonCodeMsg(40404, "插入失败");
    public static CommonCodeMsg DUPLICATE_NOVEL_TITLE = new CommonCodeMsg(40405,"小说名重复");
    public static final CodeMsg TOO_MANY_REQUESTS = new CommonCodeMsg(40406, "系统繁忙");
    public static final CodeMsg NOT_LOGIN = new CommonCodeMsg(40407, "未登录");
    public static final CodeMsg COUPON_NOT_EXIST = new CommonCodeMsg(40408, "优惠卷不存在");;
    public static final CodeMsg NOT_ENOUGH = new CommonCodeMsg(40409, "分片不足");
    public static final CodeMsg ALREADY_USE = new CommonCodeMsg(40410, "分片不足");
    public static final CodeMsg EXPIRE = new CommonCodeMsg(40411, "已过期");
    public static final CodeMsg PARAM_INVALID = new CommonCodeMsg(404012, "参数无效");
    public static final CodeMsg NOVEL_NOT_EXIST = new CommonCodeMsg(404013, "小说不存在");
    public static final CodeMsg COMMENT_NOT_EXIST = new CommonCodeMsg(404014, "评论不存在");
    public static final CodeMsg NOT_AUTH_DELETE =  new CommonCodeMsg(40415, "无权删除");
    public static final CodeMsg ID_IS_NULL = new CommonCodeMsg(40416, "ID为空");

}
