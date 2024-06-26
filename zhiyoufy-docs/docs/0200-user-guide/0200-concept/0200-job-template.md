
job template是具体运行job的描述信息，比如需要的config信息，超时配置等

## job path

worker从job path获取执行任务的地址信息

在Robot Framework的情况下，这个就是test suite的路径信息

具体配置取决于worker的job区分方式，逻辑上类似于rest endpoint

## worker labels

用于指定什么样的worker能执行，通过label来匹配比较灵活

## timeout seconds

指定超时时间，当超过这个时间未结束时worker应该终止执行，防止出现worker执行无法结束的情况

## base config path

代表基本配置的路径信息

## private config path

具体的job run会有一些私有的配置信息，比如被测系统的账户信息，这些配置信息会被组合后写入这个配置路径

## config singles

用到的config single集合名字

## config collections

用到的config collection集合名字

## extra args

传递给worker的额外的args，比如可以通过修改它来控制被测集的范围
