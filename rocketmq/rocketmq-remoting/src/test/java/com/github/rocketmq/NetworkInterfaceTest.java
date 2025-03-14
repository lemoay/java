package com.github.rocketmq;

import lombok.SneakyThrows;

import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetworkInterfaceTest {
    @SneakyThrows
    public static void main(String[] args) {
        // RocketMQ 很慢？引出了一个未解之谜 https://developer.aliyun.com/article/779223
        // 在获取本机网卡接口时，出现耗时时间长。这其实也算是jdk跟操作系统层面的意思了，与中间件 RocketMQ 无关，一开始我是怀疑是不是持久化存储在加载时慢的可能（基本排除）。
        //本机 Windows 上有 Wlan、VPN、VMware 等网络适配器。
        // 最后事实就是跟他们有关，我把相应的适配器禁用之后，重新调用 NetworkInterface#getNetworkInterfaces，此时耗时从 3+秒降到几百毫秒。
        // 最后，很遗憾还是没能剖析出为什么 Windows 下调用网卡接口 native 方法会出现那么大耗时。并且肯定跟我的机器有关，因为其他机器验证没有问题。
        // 如果要剖析原因，就得需要有 c/c++更加底层的功底才能搞定吧？
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        System.out.println("Network interfaces:" + networkInterfaces);
    }
}
