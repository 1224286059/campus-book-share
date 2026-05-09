package com.campus.bookshare.utils;

import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class BeanCopyUtils {

    private BeanCopyUtils() {
    }

    public static <T> T copy(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetClass.newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new IllegalStateException("对象复制失败", e);
        }
    }

    public static <S, T> List<T> copyList(List<S> source, Class<T> targetClass) {
        List<T> list = new ArrayList<T>();
        if (source == null) {
            return list;
        }
        for (S item : source) {
            list.add(copy(item, targetClass));
        }
        return list;
    }
}
