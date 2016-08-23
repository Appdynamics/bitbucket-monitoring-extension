package com.appdynamics.extensions.bitbucket;


import static com.appdynamics.extensions.bitbucket.ConfigConstants.DISPLAY_NAME;
import static com.appdynamics.extensions.bitbucket.ConfigConstants.OBJECT_NAME;
import static com.appdynamics.extensions.bitbucket.Util.convertToString;

import com.appdynamics.extensions.bitbucket.metrics.Metric;
import com.appdynamics.extensions.bitbucket.metrics.MetricPrinter;
import com.appdynamics.extensions.bitbucket.metrics.MetricProperties;
import com.appdynamics.extensions.bitbucket.metrics.MetricPropertiesBuilder;
import com.appdynamics.extensions.bitbucket.metrics.DomainMetricsProcessor;
import com.appdynamics.extensions.util.MetricWriteHelper;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import org.slf4j.LoggerFactory;

import javax.management.MalformedObjectNameException;
import javax.management.remote.JMXConnector;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

class BitbucketMonitorTask implements Runnable {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BitbucketMonitorTask.class);
    private static final String METRICS_COLLECTION_SUCCESSFUL = "Metrics Collection Successful";
    private static final BigDecimal ERROR_VALUE = BigDecimal.ZERO;
    private static final BigDecimal SUCCESS_VALUE = BigDecimal.ONE;

    private String displayName;
    /* metric prefix from the config.yaml to be applied to each metric path*/
    private String metricPrefix;

    /* server properties */
    private Map server;

    /* a facade to report metrics to the machine agent.*/
    private MetricWriteHelper metricWriter;

    /* a stateless JMX adapter that abstracts out all JMX methods.*/
    private JMXConnectionAdapter jmxAdapter;

    /* config mbeans from config.yaml. */
    private List<Map> configMBeans;


    private BitbucketMonitorTask() {
    }

    public void run() {
        displayName = convertToString(server.get(DISPLAY_NAME), "");
        long startTime = System.currentTimeMillis();
        MetricPrinter metricPrinter = new MetricPrinter(metricPrefix, displayName, metricWriter);
        try {
            logger.debug("Bitbucket monitor thread for server {} started.", displayName);
            BigDecimal status = extractAndReportMetrics(metricPrinter);
            metricPrinter.printMetric(metricPrinter.formMetricPath(METRICS_COLLECTION_SUCCESSFUL), status
                    , MetricWriter.METRIC_AGGREGATION_TYPE_OBSERVATION, MetricWriter.METRIC_TIME_ROLLUP_TYPE_CURRENT, MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_INDIVIDUAL);
        } catch (Exception e) {
            logger.error("Error in Bitbucket Monitor thread for server {}", displayName, e);
            metricPrinter.printMetric(metricPrinter.formMetricPath(METRICS_COLLECTION_SUCCESSFUL), ERROR_VALUE
                    , MetricWriter.METRIC_AGGREGATION_TYPE_OBSERVATION, MetricWriter.METRIC_TIME_ROLLUP_TYPE_CURRENT, MetricWriter.METRIC_CLUSTER_ROLLUP_TYPE_INDIVIDUAL);

        } finally {
            long endTime = System.currentTimeMillis() - startTime;
            logger.debug("Bitbucket monitor thread for server {} ended. Time taken = {} and Total metrics reported = {}", displayName, endTime, metricPrinter.getTotalMetricsReported());
        }
    }

    private BigDecimal extractAndReportMetrics(final MetricPrinter metricPrinter) throws Exception {
        JMXConnector jmxConnection = null;
        try {
            jmxConnection = jmxAdapter.open();
            logger.debug("JMX Connection is open");
            MetricPropertiesBuilder propertyBuilder = new MetricPropertiesBuilder();
            for (Map aConfigMBean : configMBeans) {
                String configObjectName = convertToString(aConfigMBean.get(OBJECT_NAME), "");
                logger.debug("Processing mbean %s from the config file", configObjectName);
                try {
                    Map<String, MetricProperties> metricPropsMap = propertyBuilder.build(aConfigMBean);
                    DomainMetricsProcessor nodeProcessor = new DomainMetricsProcessor(jmxAdapter, jmxConnection);
                    List<Metric> nodeMetrics = nodeProcessor.getNodeMetrics(aConfigMBean, metricPropsMap);
                    if (nodeMetrics.size() > 0) {
                        metricPrinter.reportNodeMetrics(nodeMetrics);
                    }

                } catch (MalformedObjectNameException e) {
                    logger.error("Illegal object name {}" + configObjectName, e);
                    throw e;
                } catch (Exception e) {
                    //System.out.print("" + e);
                    logger.error("Error fetching JMX metrics for {} and mbean={}", displayName, configObjectName, e);
                    throw e;
                }
            }
        } finally {
            try {
                jmxAdapter.close(jmxConnection);
                logger.debug("JMX connection is closed");
            } catch (IOException ioe) {
                logger.error("Unable to close the connection.");
                return ERROR_VALUE;
            }
        }
        return SUCCESS_VALUE;
    }

    static class Builder {
        private BitbucketMonitorTask task = new BitbucketMonitorTask();

        Builder metricPrefix(String metricPrefix) {
            task.metricPrefix = metricPrefix;
            return this;
        }

        Builder metricWriter(MetricWriteHelper metricWriter) {
            task.metricWriter = metricWriter;
            return this;
        }

        Builder server(Map server) {
            task.server = server;
            return this;
        }

        Builder jmxConnectionAdapter(JMXConnectionAdapter adapter) {
            task.jmxAdapter = adapter;
            return this;
        }

        Builder mbeans(List<Map> mBeans) {
            task.configMBeans = mBeans;
            return this;
        }

        BitbucketMonitorTask build() {
            return task;
        }
    }
}
