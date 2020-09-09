### 简要描述：

#### 项目已经部署，演示地址：http://121.36.130.200

* 项目后端采用：SpringBoot
* 前端VUE：vue-element-admin
* 中间件：Mysql、redis
* API接口设计采用RESTful风格
* 热词模块实现概要：

1. 用户搜索的词汇实时存入redis的zset数据结构，通过维护score进行计数；
2. 后端通过定时任务，根据管理员配置的更新周期x小时，K热词个数，以及结合手动设置的热词排名进行组合推荐热词排行，热词列表生成存放redis，读取亦是从redis读取返回给用户；
3. 由于修改了更新周期，必须等到下个周期才能生效，故开放了手动生成热词推荐列表的API供管理员修改相关配置后进行实时操作；
4. 注：用户当前周期搜索词汇达不到配置个数K要求时，会用手动配置的热词进行替补（指定位置在K个数之前）。

## 一. 管理员模块

1.1获取参数列表：
获取“更新频率”、“热词个数”等配置参数
请求URL：
http://baseUrl/admin/params
请求方式：
GET
参数：

| 参数名 | 必选	| 类型 | 说明 |
| --- | --- | --- |
| 无 |	无 | 无 | 无 |
返回示例
{"code":200,"msg":"success","data":[{"id":"a31800e0f10d11eabb776c4f4a84b70b","pname":"hot_words_number","pvalue":"10","pdetail":"详细描述"},{"id":"b93a08d6f10e11eabb776c4f4a84b70b","pname":"cron","pvalue":"12""pdetail":"详细描述"}]}
返回参数说明
| 参数名 | 类型 |说明 |
| code | int |400：访问失败
401：权限不足
404：接口不存在
500：报错|

msg	String	信息提示
data	List<JOSN>	返回所需数据
id	Stirng	主键
pname	String	参数名称
pdetail	String	参数描述
pvalue	String	参数值
1.2获取单个参数：
获取“更新频率”、“热词个数”等单个参数
请求URL：
http://baseUrl/admin/params/{paramId}
请求方式：
GET
参数：
参数名	必选	类型	说明
paramId	是	String	参数主键
返回示例
{"code":200,"msg":"success","data":{"id":"b93a08d6f10e11eabb776c4f4a84b70b","pname":"cron","pvalue":"12"}}
返回参数说明
参数名	类型	说明
code	int	200：访问成功
400：访问失败
401：权限不足
404：接口不存在
500：报错
msg	String	信息提示
data	json	单条参数信息
id	Stirng	主键
pname	String	参数名称
pdetail	String	参数描述
pvalue	String	参数值
1.3修改相关参数：
修改“更新频率”、“热词个数”等配置参数
请求URL：
http://baseUrl/admin/params/{paramId}
请求方式：
PUT
参数：
参数名	必选	类型	说明
paramId	是	String	参数主键
pname	否	String	参数名称
pdetail	否	String	参数描述
pvalue	否	String	参数值
返回示例
{"code":200,"msg":"success","data":null}
返回参数说明
参数名	类型	说明
code	int	200：访问成功
400：访问失败
401：权限不足
404：接口不存在
500：报错
msg	String	信息提示
data	null	无需返回
1.4获取自定义热词：
可传status过滤
请求URL：
http://baseUrl/admin/hotwords/
http://baseUrl/admin/hotwords/{status}
请求方式：
GET
参数：
参数名	必选	类型	说明
status	否	Int	过滤0:停用/1:启用的热词
返回示例
{"code":200,"msg":"success","data":[{"id":"48157dfbf2d011eaba6200163e100331","seat":1,"word":"我一直排第一","status":0,"firsttime":"2020-9-10 03:11:36","lasttime":"2020-9-10 03:13:24"}]}
返回参数说明
参数名	类型	说明
code	int	200：访问成功
400：访问失败
401：权限不足
404：接口不存在
500：报错
msg	String	信息提示
data	List<JOSN>	返回所需数据
id	Stirng	主键
seat	Int	指定排位
word	String	自定义热词
status	int	0:停用/1:启用的热词
firsttime	date	记录设置时间
lasttime	date	记录最后修改时间

1.5新增一个自定义热词：
请求URL：
http://baseUrl/admin/hotwords
请求方式：
POST
参数：
参数名	必选	类型	说明
word	是	String	自定义热词
seat	是	Int	指定排位
返回示例
{"code":200,"msg":"success","data":null}
返回参数说明
参数名	类型	说明
code	int	200：访问成功
400：访问失败
401：权限不足
404：接口不存在
500：报错
msg	String	信息提示
data	Null	无需返回数据

1.6修改一个自定义热词：
请求URL：
http://baseUrl/admin/hotwords
请求方式：
PUT
参数：
参数名	必选	类型	说明
id	是	String	主键
word	否	String	自定义热词
seat	否	Int	指定排位
status	否	Int	0:停用/1:启用的热词
返回示例
{"code":200,"msg":"success","data":null}
返回参数说明
参数名	类型	说明
code	int	200：访问成功
400：访问失败
401：权限不足
404：接口不存在
500：报错
msg	String	信息提示
data	Null	无需返回数据

1.7手动触发生成热词缓存：
因为热词列表是由定时任务在生成，此接口用于修改参数，或者修改自定义热词后手动触发生成实时热词缓存。
请求URL：
http://baseUrl/admin/createHotwords
请求方式：
GET
参数：
参数名	必选	类型	说明
无	无	无	无
返回示例
{"code":200,"msg":"success","data":null}
返回参数说明
参数名	类型	说明
code	int	200：访问成功
400：访问失败
401：权限不足
404：接口不存在
500：报错
msg	String	信息提示
data	Null	无需返回数据

二：搜索模块
2.1手动触发生成热词缓存：
因为热词列表是由定时任务在生成，此接口用于修改参数，或者修改自定义热词后手动触发生成实时热词缓存。
请求URL：
http://baseUrl/search/{word}
请求方式：
GET
参数：
参数名	必选	类型	说明
word	是	String	记录用户的实时搜索词汇，供定时统计更新热词使用
返回示例
{"code":200,"msg":"success","data":null}
返回参数说明
参数名	类型	说明
code	int	200：访问成功
400：访问失败
401：权限不足
404：接口不存在
500：报错
msg	String	信息提示
data	Null	无需返回数据
2.2获取推荐热词列表：
因为热词列表是由定时任务在生成，此接口用于修改参数，或者修改自定义热词后手动触发生成实时热词缓存。
请求URL：
http://baseUrl/search
请求方式：
GET
参数：
参数名	必选	类型	说明
word	是	String	记录用户的实时搜索词汇，供定时统计更新热词使用
返回示例
{"code":200,"msg":"success","data":[["fff","修改","我是一个词1","fffff","1"]]}
返回参数说明
参数名	类型	说明
code	int	200：访问成功
400：访问失败
401：权限不足
404：接口不存在
500：报错
msg	String	信息提示
data	list	热词数组按排行返回