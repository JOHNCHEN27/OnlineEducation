package com.xuecheng.content.feignclient;

import com.xuecheng.content.model.po.CourseIndex;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author LNC
 * @version 1.0
 * @description 熔断降级反馈机制
 * @date 2023/10/20 16:41
 */
@Slf4j
@Component
public class SearchServiceClientFallbackFactory implements FallbackFactory<SearchServiceClient> {
    @Override
    public SearchServiceClient create(Throwable throwable) {
        return new SearchServiceClient() {
            @Override
            public Boolean add(CourseIndex courseIndex) {
                throwable.printStackTrace();
                log.debug("调用ES全文检索发生熔断，异常信息:{}",throwable.getMessage());
                return false;
            }
        };
    }
}
