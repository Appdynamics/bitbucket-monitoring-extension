package com.appdynamics.extensions.bitbucket.filters;

import com.appdynamics.extensions.bitbucket.DictionaryGenerator;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

public class IncludeFilterTest {

    @Test
    public void whenAttribsMatch_thenIncludeMetrics(){
        List dictionary = DictionaryGenerator.createIncludeDictionaryWithDefaults();
        List<String> metrics = Lists.newArrayList("ClusterLocks|TotalAcquiredCount","CommandTickets|Available", "CommandTickets|Dummy");
        IncludeFilter filter = new IncludeFilter(dictionary);
        Set<String> filteredSet = Sets.newHashSet();
        filter.apply(filteredSet,metrics);
        Assert.assertTrue(filteredSet.contains("ClusterLocks|TotalAcquiredCount"));
        Assert.assertTrue(filteredSet.contains("CommandTickets|Available"));
        Assert.assertTrue(!filteredSet.contains("CommandTickets|Dummy"));
    }

    @Test
    public void whenNullDictionary_thenReturnUnchangedSet(){
        List<String> metrics = Lists.newArrayList("ClusterLocks|TotalAcquiredCount","CommandTickets|Available");
        IncludeFilter filter = new IncludeFilter(null);
        Set<String> filteredSet = Sets.newHashSet();
        filter.apply(filteredSet,metrics);
        Assert.assertTrue(filteredSet.size() == 0);
    }

    @Test
    public void whenEmptyDictionary_thenReturnUnchangedSet(){
        List dictionary = Lists.newArrayList();
        List<String> metrics = Lists.newArrayList("ClusterLocks|TotalAcquiredCount","CommandTickets|Available");
        IncludeFilter filter = new IncludeFilter(dictionary);
        Set<String> filteredSet = Sets.newHashSet("ClusterLocks|TotalAcquiredCount");
        filter.apply(filteredSet,metrics);
        Assert.assertTrue(filteredSet.size() == 1);
    }
}
