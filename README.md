bitbucket-monitoring-extension
==============================

An AppDynamics extension to be used with a stand alone Java machine agent to provide metrics for Atlassian Bitbucket


## Use Case ##

Atlassian Bitbucket is a distributed version control system that makes it easy for you to collaborate with your team. The only collaborative Git solution that massively scales.

## Prerequisites ##

This extension extracts the metrics from Bitbucket using the JMX protocol.
By default, Bitbucket starts with local or remote JMX disabled. Please follow the below link to enable JMX

https://confluence.atlassian.com/bitbucketserver/enabling-jmx-counters-for-performance-monitoring-776640189.html

To know more about JMX, please follow the below link

 http://docs.oracle.com/javase/6/docs/technotes/guides/management/agent.html


## Troubleshooting steps ##
Before configuring the extension, please make sure to run the below steps to check if the set up is correct.

1. Telnet into your Bitbucket server from the box where the extension is deployed.
       telnet <hostname> <port>

       <port> - It is the jmxremote.port specified.
        <hostname> - IP address

    If telnet works, it confirm the access to the Bitbucket server.


2. Start jconsole. Jconsole comes as a utility with installed jdk. After giving the correct host and port , check if Bitbucket
mbean shows up.

3. It is a good idea to match the mbean configuration in the config.yml against the jconsole. JMX is case sensitive so make
sure the config matches exact.

## Metrics Provided ##

In addition to the metrics exposed by Bitbucket, we also add a metric called "Metrics Collection Successful" with a value 0 when an error occurs and 1 when the metrics collection is successful.

Note : By default, a Machine agent or a AppServer agent can send a fixed number of metrics to the controller. To change this limit, please follow the instructions mentioned [here](http://docs.appdynamics.com/display/PRO14S/Metrics+Limits).
For eg.
```
    java -Dappdynamics.agent.maxMetrics=2500 -jar machineagent.jar
```


## Installation ##

1. Run "mvn clean install" and find the BitbucketMonitor.zip file in the "target" folder. You can also download the BitbucketMonitor.zip from [AppDynamics Exchange][].
2. Unzip as "BitbucketMonitor" and copy the "BitbucketMonitor" directory to `<MACHINE_AGENT_HOME>/monitors`


# Configuration ##

Note : Please make sure to not use tab (\t) while editing yaml files. You may want to validate the yaml file using a [yaml validator](http://yamllint.com/)

1. Configure the Bitbucket instances by editing the config.yml file in `<MACHINE_AGENT_HOME>/monitors/BitbucketMonitor/`.
2. Below is the default config.yml which has metrics configured already
   For eg.

   ```
      ### ANY CHANGES TO THIS FILE DOES NOT REQUIRE A RESTART ###

      #This will create this metric in all the tiers, under this path
      metricPrefix: Custom Metrics|Bitbucket

      #This will create it in specific Tier/Component. Make sure to replace <COMPONENT_ID> with the appropriate one from your environment.
      #To find the <COMPONENT_ID> in your environment, please follow the screenshot https://docs.appdynamics.com/display/PRO42/Build+a+Monitoring+Extension+Using+Java
      #metricPrefix: Server|Component:<COMPONENT_ID>|Custom Metrics|Bitbucket

      # List of Bitbucket Instances
      instances:
        - host: "localhost"
          port: 9987
          username: "monitorRole"
          password: "password123"
          #encryptedPassword:
          #encryptionKey:
          displayName: "LocalBitbucket"  #displayName is a REQUIRED field for  level metrics.


      # number of concurrent tasks.
      # This doesn't need to be changed unless many instances are configured
      numberOfThreads: 10


      # The configuration of different metrics from various mbeans of bitbucket server
      # For most cases, the mbean configuration does not need to be changed.
      # For detailed description on stats please have a look at https://confluence.atlassian.com/bitbucketserver/enabling-jmx-counters-for-performance-monitoring-776640189.html
      mbeans:
        # This mbean is to get ClusterLocks related metrics.
        - objectName: "com.atlassian.bitbucket:name=ClusterLocks"
          metrics:
            include:
              - LockedCount : "LockedCount"
              - TotalAcquiredCount : "TotalAcquiredCount"
              - TotalAcquireErrorCount : "TotalAcquireErrorCount"
              - TotalReleasedCount : "TotalReleasedCount"
              - TotalReleaseErrorCount : "TotalReleaseErrorCount"
              - TotalAcquireTimeMillis : "TotalAcquireTimeMillis"
              - QueuedThreadCount : "QueuedThreadCount"
            #exclude:
            #  - LockedCount : "LockedCount"

        - objectName: "com.atlassian.bitbucket:name=CommandTickets"
          #aggregation: true #uncomment this only if you want the extension to do aggregation for all the metrics in this mbean for a cluster
          metrics:
            include:
              - Available : "Available"
              - Used : "Used"
              - Total : "Total"
              - QueuedRequests : "QueuedRequests"

        - objectName: "com.atlassian.bitbucket:name=EventStatistics"
          metrics:
            include:
              - DispatchedCount : "DispatchedCount"
              - PublishedCount : "PublishedCount"
              - QueueCapacity : "QueueCapacity"
              - QueueLength : "QueueLength"
              - RejectedCount : "RejectedCount"
              - RemainingQueueCapacity : "RemainingQueueCapacity"

        - objectName: "com.atlassian.bitbucket:name=HostingTickets"
          #aggregation: true #uncomment this only if you want the extension to do aggregation for all the metrics in this mbean for a cluster
          metrics:
            include:
              - Available : "Available"
              - Used : "Used"
              - Total : "Total"
              - QueuedRequests : "QueuedRequests"

        - objectName: "com.atlassian.bitbucket:name=Projects"
          #aggregation: true #uncomment this only if you want the extension to do aggregation for all the metrics in this mbean for a cluster
          metrics:
            include:
              - Count : "Count"

        - objectName: "com.atlassian.bitbucket:name=Repositories"
          metrics:
            include:
              - Count : "Count"

        - objectName: "com.atlassian.bitbucket:name=ScmStatistics"
          metrics:
            include:
              - Pulls : "Pulls"
              - Pushes : "Pushes"

        - objectName: "com.atlassian.bitbucket:name=SshSessions"
          metrics:
            include:
              - ActiveSessionCount : "ActiveSessionCount"
              - SessionCreatedCount : "SessionCreatedCount"
              - SessionExceptionCount : "SessionExceptionCount"
              - MaxActiveSessionCount : "MaxActiveSessionCount"
              - SessionClosedCount : "SessionClosedCount"

        - objectName: "com.atlassian.bitbucket.thread-pools:name=EventThreadPool"
          metrics:
            include:
              - QueueLength : "QueueLength"
              - MaximumPoolSize : "MaximumPoolSize"
              - PoolSize : "PoolSize"
              - ActiveCount : "ActiveCount"
              - LargestPoolSize : "LargestPoolSize"
              - TaskCount : "TaskCount"
              - CompletedTaskCount : "CompletedTaskCount"

        - objectName: "com.atlassian.bitbucket.thread-pools:name=IoPumpThreadPool"
          metrics:
            include:
              - QueueLength : "QueueLength"
              - MaximumPoolSize : "MaximumPoolSize"
              - PoolSize : "PoolSize"
              - ActiveCount : "ActiveCount"
              - LargestPoolSize : "LargestPoolSize"
              - TaskCount : "TaskCount"
              - CompletedTaskCount : "CompletedTaskCount"

        - objectName: "com.atlassian.bitbucket.thread-pools:name=ScheduledThreadPool"
          metrics:
            include:
              - QueueLength : "QueueLength"
              - MaximumPoolSize : "MaximumPoolSize"
              - PoolSize : "PoolSize"
              - ActiveCount : "ActiveCount"
              - LargestPoolSize : "LargestPoolSize"
              - TaskCount : "TaskCount"
              - CompletedTaskCount : "CompletedTaskCount"

   ```

3. Configure the path to the config.yml file by editing the <task-arguments> in the monitor.xml file in the `<MACHINE_AGENT_HOME>/monitors/BitbucketMonitor/` directory. Below is the sample
   For Windows, make sure you enter the right path.
     ```
     <task-arguments>
         <!-- config file-->
         <argument name="config-file" is-required="true" default-value="monitors/BitbucketMonitor/config.yml" />
          ....
     </task-arguments>
    ```


## Contributing ##

Always feel free to fork and contribute any changes directly via [GitHub][].

## Community ##

Find out more in the [AppDynamics Exchange][].

## Support ##

For any questions or feature request, please contact [AppDynamics Center of Excellence][].

**Version:** 1.0.0
**Controller Compatibility:** 3.7+
**Bitbucket Versions Tested On:** 4.8.3

[Github]: https://github.com/Appdynamics/bitbucket-monitoring-extension
[AppDynamics Exchange]: http://community.appdynamics.com/t5/AppDynamics-eXchange/idb-p/extensions
[AppDynamics Center of Excellence]: mailto:help@appdynamics.com
