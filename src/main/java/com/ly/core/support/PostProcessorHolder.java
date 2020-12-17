package com.ly.core.support;

import com.ly.core.utils.SpringContextUtil;
import org.springframework.context.ApplicationContext;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: luoy
 * @Date: 2020/12/1 18:15.
 */
public class PostProcessorHolder {
    private List<PostProcessor> postProcessors ;

    private volatile static PostProcessorHolder instance;

    private PostProcessorHolder() {
        this.postProcessors = scanningAndSortPostProcessor();
    }

    public static PostProcessorHolder getInstance() {
        if (instance == null) {
            synchronized (PostProcessorHolder.class) {
                if (instance == null) {
                    instance = new PostProcessorHolder();
                }
            }
        }
        return instance;
    }

    public <T extends PostProcessor> List<T> getPostProcessor(Class<T> clazz) {
        List<PostProcessor> collect = postProcessors.stream().filter(postProcessor -> clazz.isAssignableFrom(postProcessor.getClass())).collect(Collectors.toList());
        return (List<T>) collect;
    }

    /**
     * 在第一次调用的时候就直接排序
     */
    private List<PostProcessor> scanningAndSortPostProcessor() {
        ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();
        Map<String, PostProcessor> beansOfType = applicationContext.getBeansOfType(PostProcessor.class);
        return beansOfType.values().stream().sorted(new OrderSort()).collect(Collectors.toList());
    }

    static class OrderSort implements Comparator<Object> {
        @Override
        public int compare(Object o1, Object o2) {
            boolean c1 = o1 instanceof Order;
            boolean c2 = o2 instanceof Order;
            if (c1 && c2) {
                return Integer.compare(((Order) o1).getOrder(), ((Order) o2).getOrder());
            }

            if (c1 && !c2) {
                return -1;
            }

            if (!c1 && c2) {
                return 1;
            }
            return 0;
        }
    }
}
