package com.gao.spring.framework.aop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 20170707365(gaojinjin) on 2018/5/3.
 */
public class GAopConfig {

    //以目标对象需要增强的Method作为key，需要增强的代码内容作为value
    private Map<Method, GAspect> points = new HashMap<>();

    public void put(Method target, Object aspect, Method[] points) {
        this.points.put(target, new GAspect(aspect, points));
    }

    public GAspect get(Method method) {
        return this.points.get(method);
    }

    public boolean contain(Method method) {
        return this.points.containsKey(method);
    }



    //对增强的代码进行封装
    public class GAspect {
        private Object aspect; //待会将LogAspet这个对象赋值给它
        private Method[] points; //会将LogAspet的before方法和after方法赋值进来

        public GAspect(Object aspect, Method[] points) {
            this.aspect = aspect;
            this.points = points;
        }

        public Object getAspect() {
            return aspect;
        }

        public void setAspect(Object aspect) {
            this.aspect = aspect;
        }

        public Method[] getPoints() {
            return points;
        }

        public void setPoints(Method[] points) {
            this.points = points;
        }
    }
}
