package org.simpleframework.mvc;

import org.simpleframework.core.annotation.Component;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

//@WebServlet("/*")
@Component
public class DispatcherServlet extends HttpServlet {
//    List<RequestProcessor> PROCESSOR = new ArrayList<>();
//    @Override
//    public void init(){
//        //1.初始化容器
//        BeanContainer beanContainer = BeanContainer.getInstance();
//        beanContainer.loadBeans("com.imooc");
//        new AspectWeaver().doAop();
//        new DependencyInjector().doIoc();
//        //2.初始化请求处理器责任链
//        PROCESSOR.add(new PreRequestProcessor());
//        PROCESSOR.add(new StaticResourceRequestProcessor(getServletContext()));
//        PROCESSOR.add(new JspRequestProcessor(getServletContext()));
//        PROCESSOR.add(new ControllerRequestProcessor());
//    }
//    @Override
//    protected void service(HttpServletRequest req, HttpServletResponse resp) {
//        //1.创建责任链对象实例
//        RequestProcessorChain requestProcessorChain = new RequestProcessorChain(PROCESSOR.iterator(), req, resp);
//        //2.通过责任链模式来依次调用请求处理器对请求进行处理
//        requestProcessorChain.doRequestProcessorChain();
//        //3.对处理结果进行渲染
//        requestProcessorChain.doRender();
//    }
}
