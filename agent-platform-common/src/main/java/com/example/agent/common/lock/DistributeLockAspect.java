package com.example.agent.common.lock;


import cn.hutool.core.util.ArrayUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StopWatch;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Order
@Slf4j
@RequiredArgsConstructor
public class DistributeLockAspect {


    private final DistributeLockService distributeLockService;
    /**
     * 对象反射缓存 类class -> <参数名称,field>
     */
    private static final Map<Class<?>, Map<String, Optional<Field>>> cacheFieldMap = Maps.newConcurrentMap();


    @Around(value = "@annotation(com.example.agent.common.lock.DistributeLock)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        DistributeLock distributeLock = AnnotationUtils.findAnnotation(method, DistributeLock.class);
        if (distributeLock == null) {
            return point.proceed();
        }
        LockKey lockKey = resolveLockKey(point.getArgs(), distributeLock);
        long waitSeconds = distributeLock.waitSeconds();
        TaskCallable<Object> taskCallable = buildCallable(point);
        if (waitSeconds > 0) {
            return distributeLockService.tryLock(lockKey, taskCallable, waitSeconds, TimeUnit.SECONDS);
        }
        return distributeLockService.tryLock(lockKey, taskCallable);
    }

    private LockKey resolveLockKey(Object[] args, DistributeLock distributeLock) throws IllegalAccessException {
        LockEnum lockEnum = distributeLock.keyPattern();
        if (ArrayUtil.isEmpty(args)) {
            return lockEnum.getLockKey();
        }
        //解析参数
        String[] keyValues = distributeLock.keyValue();

        if (ArrayUtil.isEmpty(keyValues)) {
            return lockEnum.getLockKey();
        }

        String[] result = new String[keyValues.length];
        for (int i = 0; i < keyValues.length; i++) {
            String paramDesc = keyValues[i];
            String[] subParams = paramDesc.split("\\.");
            Assert.notEmpty(subParams, "lock value param desc list is null");
            String paramNumStr = subParams[0];
            String parmamNum = paramNumStr.substring(1, paramNumStr.length() - 1);
            int paramSeq = Integer.parseInt(parmamNum);
            Assert.isTrue(paramSeq < args.length, "lock value param seq is out of range");
            Object param = args[paramSeq];
            if (subParams.length == 1) {
                result[i]=param.toString();
                continue;
            }
            Object obj = param;
            for (int j = 1; j < subParams.length; j++) {
                String fieldName = subParams[j];
                Object object = resolveParamValue(obj, fieldName);
                Assert.isNull(object, "lock value param is null");
            }
           result[i]= obj.toString();
        }
        return LockEnum.getLockKey(lockEnum, result);
    }


    private Object resolveParamValue(Object object, String paramName) throws IllegalAccessException {
        Class<?> clazz = object.getClass();
        Map<String, Optional<Field>> methodMap = cacheFieldMap.computeIfAbsent(clazz, f -> Maps.newConcurrentMap());

        Optional<Field> paramFieldOp = methodMap.computeIfAbsent(paramName, name -> {
            Field field = ReflectionUtils.findField(clazz, name);
            if (field == null) {
                return Optional.empty();
            }
            field.setAccessible(true);
            return Optional.of(field);
        });
        Assert.isTrue(paramFieldOp.isPresent(), "lock value param is not exist");
        Field field = paramFieldOp.get();
        return field.get(object);
    }


    private TaskCallable<Object> buildCallable(ProceedingJoinPoint point) {
        return ()->{

            String className = point.getTarget().getClass().getName();
            String methodName = point.getSignature().getName();
            String timeCostKey = Joiner.on(".").skipNulls().join(className, methodName);

            StopWatch stopWatch = new StopWatch(timeCostKey);
            stopWatch.start();

            try {
                return point.proceed();
            } finally {
                stopWatch.stop();
            }
        };
    }
}
