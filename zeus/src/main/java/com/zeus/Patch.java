package com.zeus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by tianyang on 18/4/24.
 */
@Retention(RetentionPolicy.RUNTIME)

@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Patch {
    String value();
}
