package com.ly.core.support;

import com.ly.core.base.Response;
import com.ly.core.parse.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * http请求上下文
 * @Author: luoy
 * @Date: 2020/12/1 20:27.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HttpContext {
    /** 请求 */
    private BaseModel baseModel;

    /**响应 */
    private Response response;

    /** response操作中的throw Throwable*/
    private Throwable throwable;
}
