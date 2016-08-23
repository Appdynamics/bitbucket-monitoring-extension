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
