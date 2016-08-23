package com.appdynamics.extensions.bitbucket;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class DictionaryGenerator {

    public static List<Map> createIncludeDictionaryWithDefaults() {
        List<Map> dictionary = Lists.newArrayList();
        Map metric1 = Maps.newLinkedHashMap();
        metric1.put("ClusterLocks|TotalAcquiredCount","ClusterLocks|TotalAcquiredCount");
        dictionary.add(metric1);
        Map metric2 = Maps.newLinkedHashMap();
        metric2.put("CommandTickets|Available","CommandTickets|Available");
        dictionary.add(metric2);
        Map metric3 = Maps.newLinkedHashMap();
        metric3.put("EventStatistics|DispatchedCount","EventStatistics|DispatchedCount");
        dictionary.add(metric3);
        Map metric4 = Maps.newLinkedHashMap();
        metric4.put("HostingTickets|QueuedRequests","HostingTickets|QueuedRequests");
        dictionary.add(metric4);
        return dictionary;
    }

    public static List<Map> createIncludeDictionaryWithLocalOverrides() {
        List<Map> dictionary = Lists.newArrayList();
        Map metric1 = Maps.newLinkedHashMap();
        metric1.put("ClusterLocks|TotalAcquiredCount","ClusterLocks|TotalAcquiredCount");
        metric1.put(ConfigConstants.METRIC_TYPE,"SUM SUM COLLECTIVE");
        dictionary.add(metric1);
        Map metric2 = Maps.newLinkedHashMap();
        metric2.put("CommandTickets|Available","CommandTickets|Available");
        metric2.put(ConfigConstants.AGGREGATION,"true");
        dictionary.add(metric2);
        Map metric3 = Maps.newLinkedHashMap();
        metric3.put("EventStatistics|DispatchedCount","EventStatistics|DispatchedCount");
        dictionary.add(metric3);
        Map metric4 = Maps.newLinkedHashMap();
        metric4.put("HostingTickets|QueuedRequests","HostingTickets|QueuedRequests");
        dictionary.add(metric4);
        return dictionary;
    }



    public static List<String> createExcludeDictionary() {
        return Lists.newArrayList("ClusterLocks|TotalAcquiredCount","EventStatistics|DispatchedCount");
    }
}
