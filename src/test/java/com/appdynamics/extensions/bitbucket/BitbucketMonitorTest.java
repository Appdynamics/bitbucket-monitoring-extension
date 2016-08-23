package com.appdynamics.extensions.bitbucket;


import com.google.common.collect.Maps;
import com.singularity.ee.agent.systemagent.api.exception.TaskExecutionException;
import org.junit.Test;

import java.util.Map;

public class BitbucketMonitorTest {

    @Test
    public void test() throws TaskExecutionException {
        BitbucketMonitor monitor = new BitbucketMonitor();
        Map<String, String> taskArgs = Maps.newHashMap();
        taskArgs.put("config-file", "src/test/resources/conf/config.yml");
        monitor.execute(taskArgs, null);
    }
}
