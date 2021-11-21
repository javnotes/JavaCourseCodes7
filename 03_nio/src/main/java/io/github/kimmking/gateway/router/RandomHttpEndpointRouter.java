package io.github.kimmking.gateway.router;

import java.util.List;
import java.util.Random;
// Router：在多个后端业务服务中，找到确定的那个，调用真实的业务服务
public class RandomHttpEndpointRouter implements HttpEndpointRouter {
    @Override
    public String route(List<String> urls) {
        // 代理的后端地址个数
        int size = urls.size();
        Random random = new Random(System.currentTimeMillis());
        // 根据个数随机代理其中一个服务
        return urls.get(random.nextInt(size));
    }
}
