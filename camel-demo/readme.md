
[TOC]

**代码内容以及本文内容摘自以下博文**
- [Apache Camel快速入门1](https://blog.csdn.net/yinwenjie/article/details/51692340)
- [Apache Camel快速入门2](https://yinwj.blog.csdn.net/article/details/51725807)
- [Apache Camel快速入门3](https://yinwj.blog.csdn.net/article/details/51769820)
- [Apache Camel快速入门4](https://yinwj.blog.csdn.net/article/details/51818352)
- [Apache Camel调研](https://www.jianshu.com/p/68aba8d09a89)
- [Apache Camel博客园](https://www.cnblogs.com/zengbiaobiao2016/p/5480992.html)
- [练习代码](https://github.com/cuiweiman/middleware-summary)

**包说明**
- Camel 简单案例：com.middleware.camel.module.basic
- Camel 基本使用原理：com.middleware.camel.module.quickstart
- Camel 将 mysql 数据传送到 Kafka ：Employee*

# Camel定义
> Apache Camel 作为集成项目的利器，针对应用集成场景，抽象出了一套消息交互模型，通过组件的方式进行第三方系统的接入，目前ApacheCamel已经提供了300多种组件能够接入HTTP，JMS，TCP，WS-*，WebSocket 等多种传输协议。Apache Camel结合企业应用集成模式（EIP）的特点提供了消息路由，消息转换等领域特定语言（DSL），极大降低了集成应用的开发难度。Apache Camel通过URI的方式来定义需要集成的应用节点信息，用户可以按照业务需求使用DSL快速编写消息路由规则，而无需关注集成协议的细节问题。与传统的企业集成服务总线（ESB）相比，Apache Camel的核心库非常小巧（是一个只有几M的jar包），可以方便地与其他系统进行集成。

Camel框架的核心是一个路由引擎，或者更确切地说是一个路由引擎构建器。它允许您定义自己的路由规则，决定从哪个源接收消息，并确定如何处理这些消息并将其发送到其他目标。

Camel提供更高层次的抽象，使您可以使用相同的API与各种系统进行交互，而不管系统使用的协议或数据类型如何。 Camel中的组件提供了针对不同协议和数据类型的API的特定实现。开箱即用。

# Camel 要素
## Endpoint 控制端点
**[Endpoint配置组件的官方文档](https://camel.apache.org/components/latest/index.html)**

Apache Camel中关于 Endpoint 最直白的解释就是，Camel作为系统集成的基础服务组件，在已经编排好的路由规则中，和其它系统进行通信的设定点。这个“其它系统”，可以是存在于本地或者远程的文件系统，可以是进行业务处理的订单系统，可以是消息队列服务，可以是提供了访问地址、访问ip、访问路径的任何服务。Apache Camel利用自身提供的广泛的通信协议支持，使这里的“通信”动作可以采用大多数已知的协议，例如各种RPC协议、JMS协议、FTP协议、HTTP协议等

不同的endpoint都是通过URI格式进行描述的，并且通过Camel中的org.apache.camel.Component（endpoint构建器）接口的响应实现进行endpoint实例的创建。需要注意的是，Camel通过plug方式提供对某种协议的endpoint支持，所以如果读者需要使用某种Camel的endpoint，就必须确定自己已经在工程中引入了相应的plug（即dependency依赖）。

Camel中的Endpoint控制端点使用URI的方式描述对目标系统的通信。例如以下URI描述了对外部MQ服务的通信，消息格式是Stomp：

```java
// 以下代码表示从名为test的MQ队列中接收消息，消息格式为 stomp
// 用户名为 username，监听本地端口 61613
from("stomp:queue:test?tcp://localhost:61613&login=username")

// 以下代码表示将消息发送到名为test的MQ队列中，消息格式为stomp
to("stomp:queue:test?tcp://localhost:61613&login=username");
```

消息格式是 Http：
```java
// 主动向http URI描述的路径发出请求
from("http://localhost:8080/dbk.manager.web/queryOrgDetailById")
// 将上一个路由元素上Message Out中消息作为请求内容，
// 向http URI描述的路径发出请求
// 注意，Message Out中的Body内容将作为数据流映射到Http Request Body中
to("http://localhost:8080/dbk.manager.web/queryOrgDetailById")
```

## Exchange 和 Message 消息格式
Camel为了完成消息传递、消息转换，过程中的消息必须使用统一的消息描述格式，并且保证路径上的控制端点都能存取消息。

Camel提供的Exchange要素帮助开发人员在控制端点到处理器、处理器到处理器的路由过程中完成消息的统一描述。一个Exchange元素结构如下：
```
|——Exchange
    |——Exception
    |——ExchangeID: 一个Exchange贯穿着整个编排的路由规则，ExchangeID就是它的唯一编号信息，同一个路由规则的不同实例（对路由规则分别独立的两次执行），ExchangeID不相同。
    |——FromEndpoint：Exchange实例初始来源的Endpoint控制端点（类的实例），设置路由时由“from”设置的Endpoint。
    |——Pattern：ExchangePattern(交换器工作模式)，枚举了Exchange中消息的传播方式。
    |——Properties：Exchange对象贯穿整个路由执行过程中的控制端点、处理器、表达式、路由条件判断。为了让这些元素能够共享一些自定义的参数配置信息，Exchange以K-V结构提供了信息存储方式。在 org.apache.camel.support.DefaultExchange 类中，对应 properties 的实现方法：org.apache.camel.support.AbstractExchange#getProperties。
    |——MessageIN
        |——MessageID：在系统开发阶段，提供给开发人员使用的标示消息对象唯一性的属性，这个属性可以没有值。
        |——Header：消息结构中的“头部”信息，在这个属性中的信息采用K-V的方式进行存储，并可以随着Message对象的传递将信息带到下一个参与路由的元素中。且key忽略大小写。
        |——Body：Message的业务消息内容数据存放在这里
        |——Attachment：attachment属性存储各种文件内容信息，以便这些文件内容在Camel路由的各个元素间进行流转。attachment同样使用K-V键值对形式进行文件内容的存储。但不同的是，这里的V是一个javax.activation.DataHandler类型的对象。
    |——MessageOUT
        |——MessageID
        |——Attachment
        |——Header
        |——Body
```
## [Endpoint Direct](http://camel.apache.org/direct.html)
> com.middleware.camel.module.quickstart.DirectDemo

Endpoint Direct 用于在两个编排好的路由之间实现 Exchange 消息的连接，在上一个路由的最后一个元素处理完Exchange对象后，Exchange对象将被发送至由Direct连接的下一个路由的起始位置。注意，两个被连接的路由一定要是可用的，并且存在于同一个Camel上下文服务中。

Endpoint Direct元素在实际使用Camel进行路由编排时，应用频度非常高。因为它可以把多个已编排好的路由按照业务要求连接起来，形成一个新的路由，保持原有路由的良好重用。

## Processor 处理器
Processor处理器用于接收从控制端点、路由选择条件，或者另一个处理器的Exchange中传来的消息信息，并进行消息处理。Camel核心包和各个Plugin组件都提供了很多Processor的实现，也可以通过实现org.apache.camel.Processor接口自定义处理器（后者是通常做法）。

Processor处理器是Camel编排的路由中，主要进行Exchange输入输出的消息交换，因此可以在Processor中进行数据处理或临时存储。

不过也可以在Processor处理器中连接数据库。例如开发人员需要根据上一个Endpoint中携带的“订单编号前缀”信息，在Processor中连接到一个独立的数据库中（或者缓存服务中）查找其对应的路由信息，以便动态决定下一个路由路径。由于Camel支持和JAVA语言的Spring框架无缝集成，所以要在Processor处理器中操作数据库只需要进行非常简单的配置。


## Routing 路由条件
在控制端点和处理器之间、处理器和处理器之间，Camel允许开发人员进行路由条件设置。
例如:当Exchange In Message的内容为A时将消息送入处理器A，内容为B时将消息送入处理器B的处理能力。
又例如，无论编排的路由中上一个元素的处理消息如何，都将携带消息的Exchange对象复制多份，分别送入下一处理器X、Y、Z。
开发人员甚至还可以通过路由规则完成Exchange到多个Endpoint的负载传输。

Camel中支持的路由规则非常丰富，包括：Message Filter、Based Router、Dynamic Router、Splitter、Aggregator、Resequencer等等。在[Camel官方文档](http://camel.apache.org/enterprise-integration-patterns.html)中使用了非常形象化的图形来表示这些路由功能。


### Content Based Router 基于内容的路由
> com.middleware.camel.module.quickstart.RoutingBasedContent

根据 传输的消息内容 进行判断，下发到相应的处理器中进行数据处理，在进行下一步传输

### Recipient List 接收者列表
根据判断条件，将 Exchange对象 发送给所有的 处理器或控制端点；分发给多个处理器时，Exchange对象会被复制成多分，彼此的ExchangeID不同，也因此不同的处理器之间数据信息互不干扰。

#### 使用multicast处理Static Recipient List
> com.middleware.camel.module.quickstart.RoutingBasedStatic

静态接收者列表：使用multicast方式时，Camel将会把上一处理元素输出的Exchange复制多份发送给这个列表中的所有接收者，并且按顺序逐一执行（可设置为并行处理）这些接收者。这些接收者可能是通过Direct连接的另一个路由，也可能是Processor或者某个单一的Endpoint。需要注意的是，Excahnge是在Endpoint控制端点和Processor处理器间或者两个Processor处理器间唯一能够有效携带Message的元素，所以将一条消息复制多份并且让其执行不相互受到影响，那么必然就会对Exchange对象进行复制（是复制，是复制，虽然主要属性内容相同，但是这些Exchange使用的内存区域都是不一样的，ExchangeId也不一样）
即 将一个 Exchange 对象复制多份，发送给 CamelContext 编排的多个接收者。

#### 处理 Dynamic Recipient List
> com.middleware.camel.module.quickstart.RoutingBasedDynamic

编排路由时，有时不能确定哪个接收者会成为下一个处理元素，而是需要根据 Exchange 对象携带的路由信息中解析出路由信息，并动态分发。
```json
// 携带有路由信息的 JSON 数据
{
    "data": {
        "routeName":["direct:directRouteB","direct:directRouteC"]
    }
}
```

#### [循环动态路由 Dynamic Router](https://camel.apache.org/components/latest/eips/dynamicRouter-eip.html)
> com.middleware.camel.module.quickstart.RoutingBasedCycle

动态循环路由的特点是开发人员可以通过条件表达式等方式，动态决定下一个路由位置。在下一路由位置处理完成后Exchanged对象将被重新返回到路由判断点，并由动态循环路由再次做出新路径的判断。如此循环执行直到动态循环路由不能再找到任何一条新的路由路径为止。

**整个过程中，只有一个 Exchange对象，ExchangeID都是相同的**

![DynamicRoute](https://camel.apache.org/components/latest/eips/_images/eip/DynamicRouter.gif)
动态循环路由（dynamicRouter）和之前介绍的动态路由（recipientList）在工作方式上的差异：dynamicRouter一次选择只能确定一条路由路径，而recipientList只进行一次判断并确定多条路由分支路径；dynamicRouter确定的下一路由在执行完成后，Exchange对象还会被返回到dynamicRouter中以便开始第二次循环判断，而recipientList会为各个分支路由复制一个独立的Exchange对象，并且各个分支路由执行完成后Exchange对象也不会返回到recipientList；

在DirectRouteA中我们使用“通过一个method方法返回信息”的方式确定dynamicRouter“动态循环路由”的下一个Endpoint（com.middleware.camel.module.quickstart.RoutingBasedCycle.RouteBuilderA.doDirect）。当然在实际使用中，开发人员还可以有很多方式向dynamicRouter“动态循环路由”返回指定的下一Endpoint。例如使用JsonPath指定JSON格式数据中的某个属性值，或者使用XPath指定XML数据中的某个属性值，又或者使用header方法指定Exchange中Header部分的某个属性。但是无论如何请开发人员确定一件事情：向dynamicRouter指定下一个Endpoint的方式中是会返回null进行循环终止的，否则整个dynamicRouter会无限的执行下去。

以上doDirect方法中，我们将一个计数器存储在了Exchange对象的properties区域，以便在同一个Exchange对象执行doDirect方法时进行计数操作。当同一个Exchange对象第一次执行动态循环路由判断时，选择directRouteB最为一下路由路径；当Exchange对象第二次执行动态循环路由判断时，选择DirectRouteC作为下一路由路径；当Exchange对象第三次执行时，选择一个Log4j-Endpoint作为下一个路由路径；当Exchange对象第四次执行时，作为路由路径判断的方法doDirect返回null，以便终止dynamicRouter的执行。

不能在DirectRouteA类中定义一个全局变量作为循环路由的计数器，因为由Jetty-HttpConsumer生成的线程池中，线程数量和线程对象是固定的，并且Camel也不是为每一个Exchange对象的运行创建新的DirectRouteA对象实例。


## Service与生命周期
> Camel uses a simple lifecycle interface called Service which has a single start() and stop() method.
> Various classes implement Service such as CamelContext along with a number of Component and Endpoint classes.
> When you use Camel you typically have to start the CamelContext which will start all the various components and endpoints and activate the routing rules until the context is stopped again.

包括Endpoint、Component、CamelContext等元素在内的大多数工作在Camel中的元素，都是一个一个的Service。例如，我们虽然定义了一个JettyHttpComponent（就是在代码中使用DSL定义的”jetty:http://0.0.0.0:8282/directCamel“头部所表示的Component），但是我们想要在Camel应用程序运行阶段使用这个Component，就需要利用start方法将这个Component启动起来。

实际上通过阅读org.apache.camel.component.jetty.JettyHttpComponent的源代码，读者可以发现JettyHttpComponent的启动过程起始大多数情况下什么都不会做，只是在org.apache.camel.support.ServiceSupport中更改了JettyHttpComponent对象的一些状态属性。倒是HttpConsumer这个Service，在启动的过程中启动了JettyHttpComponent对象的连接监听，并建立了若干个名为【qtp-*】的处理线程。

Service有且只有两个接口方法定义：start()和stop()，这两个方法的含义显而易见，启动服务和终止服务。另外继承自Service的另外两个子级接口SuspendableService、ShutdownableService分别还定义了另外几个方法：suspend()、resume()和shutdown()方法，分别用来暂停服务、恢复服务和彻底停止服务（彻底停止服务意味着在Camel应用程序运行的有生之年不能再次启动了）。

Camel应用程序中的每一个Service都是独立运行的，各个Service的关联衔接通过CamelContext上下文对象完成。每一个Service通过调用start()方法被激活并参与到Camel应用程序的工作中，直到它的stop()方法被调用。也就是说，每个Service都有独立的生命周期。（http://camel.apache.org/lifecycle.html）

既然每个Service都有独立的生命周期，在启动Camel应用程序时就要启动包括Route、Endpoint、Component、Producer、Consumer、LifecycleStrategy等概念元素在内的无数多个Service实现，但是我们不可能编写代码一个个地启动Service（大多数开发人员不了解Camel的内部结构，也根本不知道要启动哪些Service）。那么作为Camel应用程序肯定需要提供一个办法，在应用程序启动时分析应用程序所涉及到的所有的Service，并统一管理这些Service启动和停止的动作。这就是CamelContext所设计的另一个功能。

# [CamelContext 上下文](http://camel.apache.org/context.html)
> The context component allows you to create new Camel Components from a CamelContext with a number of routes which is then treated as a black box, allowing you to refer to the local endpoints within the component from other CamelContexts.
  First you need to create a CamelContext, add some routes in it, start it and then register the CamelContext into the Registry (JNDI, Spring, Guice or OSGi etc).

org.apache.camel.CamelContext

CamelContext横跨了Camel服务的整个生命周期，并且为Camel服务的工作环境提供支撑。

## SpringCamelContext
Camel可以和Spring框架进行无缝集成，例如可以将您的某个Processor处理器以Spring Bean的形式注入到Spring Ioc容器中，然后Camel服务就可以通过在Spring Ioc容器中定义的bean id（XML方式或者注解方式都行）取得这个Processor处理器的实例。

为了实现以上描述的功能，需要Camel服务能够从Spring的ApplicationContext取得Bean，而SpringCamelContext可以帮助Camel服务完成这个关键动作：通过SpringCamelContext中重写的createRegistry方法创建一个ApplicationContextRegistry实例，并通过后者从ApplicationContext的“getBean”方法中获取Spring Ioc容器中符合指定的Bean id的实例。这就是Camel服务和Spring进行无缝集成的一个关键点，如以下代码片段所示：
```java
public class SpringCamelContext extends DefaultCamelContext implements InitializingBean, DisposableBean, ApplicationContextAware {
    ......
     @Override
    protected Registry createRegistry() {
        return new ApplicationContextRegistry(getApplicationContext());
    }
    ......
}

public class ApplicationContextRegistry implements Registry {
    ......

    @Override
    public Object lookupByName(String name) {
        try {
            return applicationContext.getBean(name);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }
    ......
}
```

## DefaultCamelContext结构和启动过程
DefaultCamelContext的源代码中定义了许多全局变量，其中一些变量负责记录CamelContext的状态属性、一些负责引用辅助工具还有一些记录关联的顶层工作对象（例如Endpoint、Servcie、Routes、）Components等等）。部分变量作用如下所示：
```java
public class DefaultCamelContext extends ServiceSupport implements ModelCamelContext, SuspendableService {
    ......
    // java的基础概念：类加载器，一般进行线程操作时会用到它
    private ClassLoader applicationContextClassLoader;
    // 已定义的endpoint URI（完整的）和Endpoint对象的映射关系
    private Map<EndpointKey, Endpoint> endpoints;
    // 已使用的组件名称（即Endpoint URI头所代表的组件名称）和组件对象的对应关系
    private final Map<String, Component> components = new HashMap<String, Component>();
    // 针对原始路由编排所分析出的路由对象，路由对象是作为CamelContext从路由中的一个元素传递到下一个元素的依据
    //  路由对象中还包含了，将路由定义中各元素连接起来的其它Service。例如DefaultChannel
    private final Set<Route> routes = new LinkedHashSet<Route>();
    // 由DSL或者XML描述的原始路由编排。每一个RouteDefinition元素中都包含了参与这个路由的所有Service定义。
    private final List<RouteDefinition> routeDefinitions = new ArrayList<RouteDefinition>();
    // 生命周期策略，实际上是一组监听，文章后面的内容会重点讲到
    private List<LifecycleStrategy> lifecycleStrategies = new CopyOnWriteArrayList<LifecycleStrategy>();
    // 这是一个计数器，记录当前每一个不同的Routeid中正在运行的的Exchange数量
    private InflightRepository inflightRepository = new DefaultInflightRepository();
    // 服务停止策略
    private ShutdownStrategy shutdownStrategy = new DefaultShutdownStrategy(this);
    ......
}
```


org.apache.camel.CamelContextAware接口定义了两个方法：setCamelContext和getCamelContext。在Camel中大多数元素都实现了这个接口，DefaultCamelContext在一边启动各个Service的时候，顺便将自己所为参数赋给了正在启动的Service，最终实现了各个Service之间的共享上下文信息的效果：


```java
    // 这是CamelContextAware接口的定义
    public interface CamelContextAware {
        /**
         * Injects the {@link CamelContext}
         *
         * @param camelContext the Camel context
         */
        void setCamelContext(CamelContext camelContext);

        /**
         * Get the {@link CamelContext}
         *
         * @return camelContext the Camel context
         */
        CamelContext getCamelContext();
    }

     //............

    // 这是DefaultCamelContext的doAddService方法中
    // 对实现了CamelContextAware接口的Service
    // 进行CamelContext设置的代码
    private void doAddService(Object object, boolean closeOnShutdown) throws Exception {
        //......
        if (object instanceof CamelContextAware) {
            CamelContextAware aware = (CamelContextAware) object;
            aware.setCamelContext(this);
        }
        //......
    }

```


DefaultCamelContext的启动过程：DefaultCamelContext是如何帮助整个Camel应用程序中若干Service完成启动过程的？首先说明 DefaultCamelContext 也是一个Service，所以它必须实现Service接口的start()方法和stop()方法。而DefaultCamelContext对于start()方法的实现就是“启动其它已知的Service”。
更具体的来说，DefaultCamelContext将所有需要启动的Service按照它们的作用类型进行区分，例如负责策略管理的Service、负责Components组件描述的Service、负责注册管理的Service等等，然后再按照顺序启动这些Service。以下代码片段提取自DefaultCamelContext的doStartCamel()私有方法，并加入了笔者的中文注释（原有作者的注释依然保留），这个私有方法由DefaultCamelContext中的start()方法间接调用，用于完成上述各Service启动操作。

```java
// 为了调用该私有方法，之前的方法执行栈分别为：
// start()
// super.start()
// doStart()
......
private void doStartCamel() throws Exception {
    // 获取classloader是有必要的，这样保证了Camel服务中的classloader和环境中的其他组件（例如spring）一致
    if (applicationContextClassLoader == null) {
       // Using the TCCL as the default value of ApplicationClassLoader
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            // use the classloader that loaded this class
            cl = this.getClass().getClassLoader();
        }
        setApplicationContextClassLoader(cl);
    }

    ......

    // 首先启动的是ManagementStrategy策略管理器，它的默认实现是DefaultManagementStrategy。
    // 还记得我们在分析DUBBO时提到的Java spi机制吧，Camel-Core也使用了这个机制，并进行了二次封装。详见org.apache.camel.spi代码包。
    // 启动ManagementStrategy，可以帮助Camel实现第三方组件包（例如Camel-JMS）的动态加载
    // start management strategy before lifecycles are started
    ManagementStrategy managementStrategy = getManagementStrategy();
    // inject CamelContext if aware
    if (managementStrategy instanceof CamelContextAware) {
        ((CamelContextAware) managementStrategy).setCamelContext(this);
    }
    ServiceHelper.startService(managementStrategy);

    ......
    // 然后启动的是 生命周期管理策略
    // 这个lifecycleStrategies变量是一个LifecycleStrategy泛型的List集合。
    // 实际上LifecycleStrategy是指是一组监听，详见代码片段后续的描述
    ServiceHelper.startServices(lifecycleStrategies);

    ......
    // 接着做一系列的Service启动动作
    // 首先是Endpoint注册管理服务，要进行重点介绍的是org.apache.camel.util.LRUSoftCache
    // 它使用了java.lang.ref.SoftReference进行实现，这是Java提供的
    endpoints = new EndpointRegistry(this, endpoints);
        addService(endpoints);

    ......
    // 启动线程池管理策略和一些列其它服务
    // 基本上这些Service已经在上文中提到过
    doAddService(executorServiceManager, false);
    addService(producerServicePool);
    addService(inflightRepository);
    addService(shutdownStrategy);
    addService(packageScanClassResolver);
    addService(restRegistry);

    ......
    // start components
    startServices(components.values());
    // 启动路由定义，路由定义RouteDefinition本身并不是Service，但是其中包含了参与路由的各种元素，例如Endpoint。
    // start the route definitions before the routes is started
    startRouteDefinitions(routeDefinitions);

    ......
}
......
```

### LifecycleStrategy
> 关于Camel中元素生命周期的规则管理器，但实际上LifecycleStrategy接口的定义更确切的应该被描述成一个监听器：当Camel引用程序中发生诸如Route加载、Route移除、Service加载、Serivce移除、Context启动或者Context移除等事件时，DefaultCamelContext中已经被添加到集合“lifecycleStrategies”（java.util.List<LifecycleStrategy>）的LifecycleStrategy对象将会做相应的事件触发。“lifecycleStrategies”集合是一个CopyOnWriteArrayList。

以下代码展示了在DefaultCamelContext添加Service时，DefaultCamelContext内部是如何触发“lifecycleStrategies”集合中已添加的监听的：

```java
......
private void doAddService(Object object, boolean closeOnShutdown) throws Exception {
    ......

    // 只有以下条件成立，才需要将外部来源的Object作为一个Service处理
    if (object instanceof Service) {
        Service service = (Service) object;

        // 依次连续触发已注册的监听
        for (LifecycleStrategy strategy : lifecycleStrategies) {
            // 如果是一个Endpoint的实现，则触发onEndpointAdd方法
            if (service instanceof Endpoint) {
                // use specialized endpoint add
                strategy.onEndpointAdd((Endpoint) service);
            }
            // 其它情况下，促发onServiceAdd方法
            else {
               strategy.onServiceAdd(this, service, null);
            }
       }

       // 其它后续处理
       ......
    }
}
......
```

### CopyOnWriteArrayList与监听者模式
某个线程在对容器进行写操作的同时，还有另外的线程对容器进行读取操作。由于容器的各种读写操作都会加上锁（无论是悲观锁还是乐观锁），所以容器的读写性能又会收到影响。如果采用的是乐观锁，那么对性能的影响可能还不会太大，但是如果采用的是悲观锁，那么对性能的影响就有点具体了。


CopyOnWriteArrayList提供了另一种线程安全的容器操作方式：CopyOnWriteArrayList的工作效果类似于java.util.ArrayList，但是通过ReentrantLock实现了容器中写操作的线程安全性。CopyOnWriteArrayList最大的特点是：当进行容器中元素的修改操作时，它会首先将容器中的原有元素克隆到一个副本容器中，然后对副本容器中的元素进行修改操作。待这些操作完成后，再将副本中的元素集合重新会写到原有的容器中完成整个修改操作。这种工作机制称为Copy-On-Write（COW）。这样做的最主要目的是分离容器的读写操作。CopyOnWriteArrayList会对所有的写操作加锁，但是不会对任何容器的读操作加锁（因为写操作在一个副本中进行）。

另外CopyOnWriteArrayList还重新实现了一个新的迭代器：COWIterator。它是做什么的呢？举例说明：如果在ArrayList中进行迭代的同时进行容器的写操作，那么就可能会因为下标超界等原因出现程序异常：
```java
List<?> list = new ArrayList<?>();
// 省略了添加元素部分的代码
......

// ArrayList不支持这样的操作方式，会报错
for(Object item : list){
    list.remove(item);
}
```

但如果使用CopyOnWriteArrayList中重写的COWIterator迭代器，就不会出现的情况（开发人员还可以使用JDK 1.5+ 提供的另一个线程安全COW容器：CopyOnWriteArraySet）：
```java
List<?> list = new CopyOnWriteArrayList<?>();
// 省略了添加元素部分的代码
......

// COWIterator迭代器支持一边迭代一边进行容器的写操作
for(Object item : list){
    list.remove(item);
}
```


### SoftReference 软引用
JVM的内存是有上限的，JVM的垃圾回收线程进行工作时会将当前没有任何引用可达性的对象区域进行回收，以便保证JVM的内存空间能够被循环利用。

强引用：当JVM的可用内存达到上限，且垃圾回收线程根据引用可达性又无法找到任何可以回收的对象时，应用程序就会报错 OutOfMemoryError。

软引用（Soft Reference）：对象间接引用方式。在这种方式下，对象间的引用关系通过一个命名为 java.lang.ref.SoftReference 的工作类进行间接托管，目的是当JVM内存空间不足，垃圾回收策略被主动触发时 进行以下回收策略操作：扫面当前堆内存中只建立了“软引用”的内存区域，无论这些“软引用”是否依然存在引用可达性，都强制对这些建立了“软引用”的对象进行回收，以便腾出内存空间。


```java
package com.test;
import java.lang.ref.SoftReference;
public class A {
    // 软引用 B
    private SoftReference<B> paramB;
    // 软引用 C
    private SoftReference<C> paramC;
    /**
     * 构造函数中，建立和B、C的软引用
     */
    public A(B paramB , C paramC) {
        this.paramB = new SoftReference<B>(paramB);
        this.paramC = new SoftReference<C>(paramC);
    }
    public B getParamB() {
        return paramB.get();
    }
    public C getParamC() {
        return paramC.get();
    }
}
```
当出现“软引用”对象被垃圾回收线程回收时，例如B对象被回收时，A对象中的getB()方法将会返回 null。那么原来进行B对象间接引用动作的 SoftReference 对象该怎么处理呢？要知道如果B对象被回收了，那么承载这个“软引用”的 SoftReference 对象就没有什么用处了。还好JDK中帮我们准备了名叫 ReferenceQueue 的队列，当 SoftReference 对象所承载的“软引用”对象被回收后，这个 Reference 对象将被送入 ReferenceQueue 中（当然你也可以不指定，如果不指定的话 SoftReference 对象会以“强引用”的回收策略被回收，不过 SoftReference 对象所占用的内存空间不大），开发人员可以随时扫描 ReferenceQueue ，并对其中的 Reference 对象进行清除。

注意，一个对象同一时间并不一定只被另一个对象引用，而是可能被若干个对象同时引用。只要对这个对象的引用中有一个没有使用“软引用”特性，那么垃圾回收策略对它的回收就不会采用“软引用”的回收策略进行。



### Camel中的LRU算法
LRU（最近最少使用算法）“缓存淘汰算法”，在计算机技术实践中它被广泛用于缓存功能的开发，例如处理内存分页与虚拟内存的置换问题，或者又像 Camel 那样用于计算选择 Endpoint 对象将从缓存结构中被移除。

- 整个队列有一个阀值用于限制能够存放于队列容器中的最大元素个数，这个阀值暂且称为 maxCacheSize。
- 当队列中的元素还没有达到这个 maxCacheSize 时，进入队列的元素将被放置在队列的最前面，队列会保持这种处理策略直到队列中的元素达到 maxCacheSize 为止。
- 当队列中的某个元素被选择时（一般来说，队列允许开发人员在选择元素时传入一个Key，队列会依据这个Key进行元素选择），被命中的元素又会重新排列到队列的最前面。这样一来，队列最尾部的元素就是近期使用最少的一个元素。
- 一旦当队列中的元素达到 maxCacheSize 后（不可能超过），新进入队列中的元素将会把队列最尾部的元素挤出队列，而它自己会排列到队列的最顶部。



在 DefaultCamelContext 中，用来进行 Endpoint 注册存储管理的类称为 EndpointRegistry ，它就是依据 LRU 算法原则决定哪些 Endpoint 定义应该存放在缓存中。具体来说， EndpointRegistry 中使用“软引用”方式，通过 ConcurrentLinkedHashMap 提供的既有LRU技术支持实现了存在于内存中的高效缓存。

通过 LRUCache 保证已经注册并且最近使用频繁的 Endpoint 对象一定存在于缓存中，通过 LRUSoftCache 保证所有已保存在内存中 Endpoint 对象不会导致 JVM 内存溢出。

