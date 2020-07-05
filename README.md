# project

##秒杀，在spring-boot-service-impl模块spring-boot-service-impl-spike中
#spring-boot-common模块提供公共功能，工具类这些
#spring-boo-servicet-api接口层
#spring-boot-service-impl接口实现层

三级缓冲，提高并发性
1.秒杀售空的商品会存在本地缓存，由于读多写少（只有商品售空的时候会写一次）这里采用ReentrantReadWriteLock + HashMap, 请求进来先访问缓存发现已售空直接返回，没售空访问redis获取库存，售空的商品会put（Key，PRESENT）,PRESENT是一个懒加载的Object对象用来占位（在获取写锁的时候初始化）
2.商品库存会存到redis中来降低数据库的访问
3.秒杀成功的异步发送消息到rabbmitMQ（失败重试），返回秒杀成功，正在排队等候，消息消费者处理消息存入到数据库（失败的消息放入异常表以免数据丢失）
