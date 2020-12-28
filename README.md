## 分层思想: 分为三层,相互独立(接口,数据,用例)

一个用例对应多个接口 一个接口可以对应多个数据 一个数据只能对应一个接口

## 接口层: 实现方式为动态代理,spring容器启动后,生成代理接口注入容器

#### 编写方式:
注:如果不进行通用头部等rest-assured过滤器与通用BaseUrl设置,可直接注入com.ly.api.DefaultApiClient类进行接口调用

eg.

```java
@HttpServer(baseUrl = "${driver-api.url}")
@Filters({RestAssuredLogFilter.class, DriverHeadersFilter.class})
public interface DriverApiClient extends BaseHttpClient{
}
```

1. 定义一个接口类继承com.ly.api.BaseHttpClient,类上注解@HttpServer注解标明该接口是一个http接口类
2. 注解
   - @HttpServer: 接口类上注解，必填，可设置整个接口类的baseUrl,可用通配符${},读取对应的application.xml配置
   - @Filters: 用于传递rest-assured过滤器注解，接收一个io.restassured.filter.Filter类数组， 如果注释在类上，则整个类接口都会使用该注解里面的过滤器，如果注解在方法上，则作用域该方法,如: 需要设置该类下所有接口默认headers,可继承com.ly.core.listener.BaseHeadersFilter类,重写defHeaders()方法进行设置默认固定头部,如果与yaml文件中headers重复,优先headers中参数,两个地方都可用${}通配符调用

## 数据层: 通过yaml文件作为数据载体,其中headers,requests,validate中的值都支持${}通配符调用,validate中断言key支持jsonpath与xpath语法

#### 编写模板:

```
name: login
type: json
description: 登录成功
url: /login
method: POST
headers:
  x-request-client-imei: "222222222222"
requests:
  {
    "phone": "${phone, 13000000001}",
    "code": "0000"
  }
setup:
  - method: createTimestamp
  - method: setUptest
    args: ${request}
teardown:
  - method: teardowm
    args: args1111, args222
  - method: teardowm1
    args: ${response}, ${timestamp}
onFailure:
  - method: onFailure
    args: args1111, args222
validate:
  eq: ["result": 0, "error_code": "0"]
  notNull: ["data.token"]
  plugin: 
    - method: "teardowm"
      args: args1111, ${orderId}
saveGlobal: ["orderId": "response.data.orderId"]
saveMethod: ["orderId": "response.data.orderId"]
saveClass: ["orderId": "response.data.orderId"]
saveThread: ["orderId": "response.data.orderId"]
parameters:
  - name: login-phone-unregistered
    description: 手机号未注册
    headers:
    requests:
      "phone": "13000000009"
      "code": "0000"
    validate:
    eq: ["result": 1]
  - name: login-phone-not-found
    description: 手机号不存在
    requests:
      "phone": "10000000000"
      "code": "0000"
    validate:
      eq: ["result": 1]
  - name: login-code-length-error
    description: 验证码长度错误
    requests:
      "phone": "10000000000"
      "code": "000"
    validate:
      eq: ["result": 1]
  - name: login-code-error
    description: 验证码错误
    requests:
      code: "0001"
    validate:
      eq: ["result": 1]
 ```
- name:为该数据唯一标识,用例中引入需要

- type:标识该接口请求方式,目前支持(form,json,xml)

- description:简介,标明该数据作用

- url:接口url,可以是一个带http/https的全的url,也可是baseUrl + url

- method:http请求方法

- headers:接口头部,非必填
  如果有默认固定头部可继承com.ly.core.listener.HeadersFilterAdapter,重写defHeaders()方法进行设置默认固定头部,如果与yaml文件中headers重复,优先headers中参数,两个地方都可用${}通配符调用

- requests接口请求数据,非必填

- setup, teardown: 接口请求前后的两个hook函数,setup在接口执行前会执行,teardown在接口执行后且各种断言执行完后执行, 支持${}参数

- validate断言非必填
  断言方法内key为固定,比如eq,notNull,isNull,plugin等,value为一个数组,支持jsonpath取接口返回值与支持通配符${}取前面接口缓存,其中plugin为插件式断言,里面为com.ly.plugin.PluginSupport类中的一个方法,用法如hook函数

- onFailure接口失败调用
  如果接口调用失败或者validate校验不过会调用onFailure中的方法,用于进行一些有状态的数据回滚
  
- saveGlobal保存一个作用域在整个测试生命周期的值
- saveMethod保存一个作用域在该测试方法的值(多线程运行时候会进行线程隔离,线程安全)
- saveClass保存一个作用域在该测试类的值
- saveThread保存一个作用域在该线程的值(用于多线程执行用例时候进行线程隔离)

- parameters接口参数化设置,可为空
  parameters节点结构为一个list,list里面每一个值相当于一份完整的接口数据
  parameters除了定义下的5个节点数据与save节点(saveMethod,saveThread,saveGlobal)外,其他数据全取父case内容,组装成一份完整的case数据,运行模式为SINGLE时候,会把父case与parameters节点下的case都运行一遍
  parameters节点下包括:
  1. name节点: 单接口SINGLE模式下该值可省略, MULTIPLE串行时候如果需要调用该参数化数据需要填写唯一的name
  2. description节点: 简介,可省略,省略取父节点description值
  3. headers节点: 参数化节点与父case节点headers差异值,其本质为一个map,会和父case节点headers节点合并,key相同的参数用parameters中headers值覆盖,如无差异可省略
  4. requests节点: 参数化节点与父case节点requests差异值,其本质为一个map,会和父case节点requests节点合并,key相同的参数用parameters中requests值覆盖,key会递归替换,如无差异可省略
  5. validate节点:参数化数据自有的一份断言, 如果为空会取父case断言,如无差异可省略
  6. headers节点与requests节点会递归替换, 如果需要验证某个字段不存在,可将该字段设置为null

- 参数化说明
  yaml模板中支持参数化|jsonpath|xpath等写法,如${phone, 13000000001}, 该用法为该参数化值设置一个default值,如果缓存中无该值,那就取default值
  jsonpath一般用于validate与saveGlobal中,用于取返回值校验与保存
  setup支持${response}参数化,${response}会转换成com.ly.core.parse.BaseModel
  teardown支持${response}参数化,${response}会转换成com.ly.core.base.Response

## 用例层: 通过深度集成testng进行用例执行

#### 编写格式: 

定义一个类继承com.ly.core.base.BaseTestCase.class,然后通过spring @Autowired注入编写的接口层的类,再调用接口类中定义的接口,返回一个com.ly.core.base.Response对象

###### 测试用例方法注解说明:

1. allure测试报告注解: 

   - @Feature("driver-api")
     类上注解, 标注主要功能模块, 可以理解为testsuite

   - @Story("登录模块接口")
     类上注解,属于feature之下的结构,可以理解为testcase

   - @Severity(SeverityLevel.CRITICAL)
     标注测试用例的重要级别
     1. Blocker级别——中断缺陷
     2. Critical级别――临界缺陷
     3. Major级别——较严重缺陷
     4. Normal级别――普通缺陷
     5. Minor级别———次要缺陷
     6. Trivial级别——轻微缺陷
     7. Enhancement级别——测试建议、其他（非缺陷）

   - @Description("司机基础信息")
     用例说明

2. testng核心驱动注解@Test: 
   @Test(groups = "driver-api")

3. 测试数据引入核心注解@DataModel: 
   eg. 

   ```
   @DataModel(path = {"test-driver-api.yml", "test-ck.yml"}, format = DataModel.Format.MULTIPLE)
    ```

    eg.
    
    ```
    @DataModel(value = {"login1", "login2", "login3"}, format = DataModel.Format.SINGLE)
    ```

- value: 对应的数据层中的name, 做数据层引入, format = DataModel.Format.MULTIPLE可省略value值, 引入path所有值

- format:
  - DataModel.Format.MULTIPLE 串行, value中只做数据引入,测试方法入参为com.ly.core.parse.MultipleModel, 需要使用指定数据时候调用MultipleModel.getModel("name")
  - DataModel.Format.SINGLE 单接口, value中有多少份数据引入就会执行多少次,测试方法入参为com.ly.core.parse.BaseModel
  


4. 请求与响应
   com.ly.api.BaseHttpClient对象, 一个用于发起请求http的Client
   com.ly.core.base.Response对象, 链式调用方法处理接口返回值

   eg.

   ```
   driverApiClient.wait(TimeUnit.SECONDS, 1)
                  .saveAsk("key", "key")
                  .doHttp("login")
                  .auto();
   ```
   - BaseHttpClient对象方法
     wait(),wait(TimeUnit unit, long interval) 用于设置请求接口前的等待时间
     saveAsk(),saveGlobal(), saveTest(),saveSuite() 用于往不同生命周期保存一个缓存,saveAsk为该请求生命周期
     doHttp(BaseModel model)接口调用,入参为model,SINGLE模式时候直接传入方法入参BaseModel即可
     doHttp(String modelName)接口调用,入参为modelName, MULTIPLE模式时候传入modelName即可
   - Response对象方法
     then(): 语法糖,无特殊意义,只用作链式调用标明
     statusCode(): 用于断言接口返回code
     validate(): 断言方法
     eq(): 硬编码断言相等
     eqByPath(): 硬编码断言相等,值取jsonpath,xpath
     validatePlugin(): 硬编码断言,用于调用方法
     saveGlobal(), saveTest(),saveSuite() : 结果保存不同维度方法
     onFailure(BaseFailHandle failHandle): validate()断言失败后会执行的方法,所以必须在validate()方法后调用,入参为BaseFailHandle接口,需要实现该接口并且重写handle(T t)方法
     onFailure(Class clazz): 同上
     extract(): 用于取值
     processor(BaseProcessorHandle processorHandle)
     processor(Class clazz):用于该调用该接口后一些自定义处理,如订单行程需要一分钟,入参为BaseProcessorHandle接口,需要实现该接口并且重写processor(T t)方法
     wait(): 接口执行完后等待时间
     done(): 用于处理结束,抛出validate()异常,如没吊用extract()方法取值的话该方法为链式调用结尾必须调用
     auto(): 自动解析yaml文件所有内容
     auto(int httpStatusCode): 自动解析yaml文件所有内容,手动设置httpStatusCode
     autoExcludeDone(): 自动解析yml文件, 但是不会自动调用done()方法结束,需要手动调用done结束,主要用于给该http请求添加更多的自定义处理

5. 配置方法使用,定义一系列配置方法,不同测试生命周期运行
   @ApiBeforeMethod
   @ApiBeforeClass
   @ApiBeforeSuite
   @ApiAfterMethod
   @ApiAfterClass
   @ApiAfterSuite
   主要结合@DataModel, @DataFile, @DataParams 当配置方法入参使用

## 配置文件说明: src/main/resources

application-qa.yml,application-uat.yml 区分环境配置文件,最终会根据使用环境默认合并到application.yml

- application.yml

  - notification节点: 配置通知类型
  - retry节点: 配置用例失败重试

- application-qa.yml,application-uat.yml 

  - httpurl节点: 配置接口层接口类上面@HttpServer注解中baseurl代表值baseurl直接用${driverapi.url}调用即可

  - redis节点: 配置redis

    ```
    RedisService redisService = SpringContextUtil.getBean(RedisService.class);
    ```

  - mongodb节点: 配置mongodb

    ```
    MongoTemplate mongoTemplate = SpringContextUtil.getBean(MongoTemplate.class);
    ```

  - datasource节点: 配置多数据源

    ```
    MysqlServer mysqlServer = MysqlServer.create("carrier");
    ```

## 运行方式:

- testng.xml运行
- maven运行 : mvn clean test -P uat -Dtestng.xmlFilePath=testng.xml 执行测试用例, -P 参数后面跟着需要执行的环境,默认uat, -Dtestng.xmlFilePath 可选择执行的xml文件 默认testng.xml(支持Groups方式,详情查看pom.xml)
- 调用com.ly.core.actuator.TestNgRun运行
   - 根据groups运行测试用例
   - 根据类运行测试用例
   - 根据类中方法运行测试用例
   - 可设置运行并发
- 测试报告运行: allure serve target/allure-results

## 环境搭建:

- jdk1.8
- idea(需要装下maven插件与lombok插件)
- maven(一般来说idea会自带一个)
- allure2.13.2 (需要把allure目录加到ALLURE_HOME环境变量)

## 其他支持:

- 提供一个测试全程生命周期监听器com.ly.core.listener.BaseLifeCycleListener(不建议使用)
- 提供一个TestNg全程生命周期接口com.ly.core.support.TestNgLifeCyclePostProcessor(建议使用)
- 提供一个HTTP请求生命周期接口com.ly.core.support.HttpPostProcessor
- 提供一个给PostProcessor接口排序的Order接口

- 可设置测试用例执行失败或发生异常时,在多少个错误后跳过后续的其他测试用例

  pom.xml文件中设置

  ```
  <skipAfterFailureCount>5</skipAfterFailureCount> 
  ```

- 可设置通过设置groups运行,但是只能选择groups与 testng.xml其中一种方式运行,需要改pom.xml文件

- 提供另外两种数据驱动方式 @DataFile与@DataParams

- 提供com.ly.core.actuator.TestNgRun 编码方式运行

- 提供har与swagger格式转换为yaml用例数据(charles导出.har文件转换为yaml格式)

  ```
  com.ly.core.Utils.Source2Yaml
  ```

- redis

- mysql

- MongoDB

- 钉钉,邮件通知

- 用例失败重试

- 并发执行

- jenkins支持

## 资料

- <https://testng.org/doc/documentation-main.html> testng文档
- <http://allure.qatools.ru/> allure文档