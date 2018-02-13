/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 *
 */

package com.appdynamics.extensions.bitbucket.metrics;


import javax.management.ObjectInstance;
import javax.management.ObjectName;

public class MetricKeyFormatter {


    private ObjectName getObjectName(ObjectInstance instance) {
        return instance.getObjectName();
    }

    String getKeyProperty(ObjectInstance instance, String property) {
        if (instance == null) {
            return "";
        }
        return getObjectName(instance).getKeyProperty(property);
    }
}
