package vip.xiaonuo.core.util;

import java.util.UUID;

/**
 * @Author 981743898@qq.com
 * @Date 2023/8/25 19:50
 * @Describe
 */
public class IdGen {
    private long workerId;
    private long datacenterId;
    private long sequence = 0L;
    private long twepoch = 1288834974657L;                              //  Thu, 04 Nov 2010 01:42:54 GMT
    private long workerIdBits = 1L;                                     //  节点ID长度5
    private long datacenterIdBits = 1L;                                 //  数据中心ID长度5
    private long maxWorkerId = -1L ^ (-1L << workerIdBits);             //  最大支持机器节点数0~31，一共32个
    private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);     //  最大支持数据中心节点数0~31，一共32个
    private long sequenceBits = 4L;                                    //  序列号12位12
    private long workerIdShift = sequenceBits;                          //  机器节点左移12位
    private long datacenterIdShift = sequenceBits + workerIdBits;       //  数据中心节点左移17位
    private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits; //  时间毫秒数左移22位
    private long sequenceMask = -1L ^ (-1L << sequenceBits);                          //  4095
    private long lastTimestamp = -1L;

    private static class IdGenHolder {
        private static final IdGen instance = new IdGen();
    }

    public static IdGen get(){
        return IdGenHolder.instance;
    }

    public IdGen() {
        this(0L, 0L);

    }

    public IdGen(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized long nextId() {
        //获取当前毫秒数
        long timestamp = timeGen();
        //如果服务器时间有问题(时钟后退) 报错。
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format(
                    "Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        //如果上次生成时间和当前时间相同,在同一毫秒内
        if (lastTimestamp == timestamp) {
            //sequence自增，因为sequence只有12bit，所以和sequenceMask相与一下，去掉高位
            sequence = (sequence + 1) & sequenceMask;
            //判断是否溢出,也就是每毫秒内超过4095，当为4096时，与sequenceMask相与，sequence就等于0
            if (sequence == 0) {
                //自旋等待到下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            //如果和上次生成时间不同,重置sequence，就是下一毫秒开始，sequence计数重新从0开始累加
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        // 最后按照规则拼出ID。
        // 000000000000000000000000000000000000000000  00000            00000       000000000000
        // time                                       datacenterId   workerId    sequence
        return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift) | sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

    private static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };


    public static String getUUID(int num) {
        //调用Java提供的生成随机字符串的对象：32位，十六进制，中间包含-
        String uuid= UUID.randomUUID().toString().replace("-", "");
        StringBuffer shortBuffer = new StringBuffer();
        for (int i = 0; i < num; i++) { //分为8组
            String str = uuid.substring(i * 4, i * 4 + 4); //每组4位
            int x = Integer.parseInt(str, 16); //输出str在16进制下的表示
            shortBuffer.append(chars[x % 0x3E]); //用该16进制数取模62（十六进制表示为314（14即E）），结果作为索引取出字符
        }
        return shortBuffer.toString();//生成8位字符
    }

    public static String getUUID36() {
        //调用Java提供的生成随机字符串的对象：32位，十六进制，中间包含-
        String uuid= UUID.randomUUID().toString().replace("-", "");
        StringBuffer shortBuffer = new StringBuffer();
        for (int i = 0; i < 8; i++) { //分为8组
            String str = uuid.substring(i * 4, i * 4 + 4); //每组4位
            int x = Integer.parseInt(str, 16); //输出str在16进制下的表示
            shortBuffer.append(chars[x % 0x24]); //用该16进制数取模36，结果作为索引取出字符
        }
        return shortBuffer.toString();//生成8位字符
    }

    public static void main(String[] args) {

        System.out.println(getUUID(6));
    }
}
