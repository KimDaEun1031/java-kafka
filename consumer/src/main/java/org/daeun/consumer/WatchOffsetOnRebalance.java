package org.daeun.consumer;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;

public class WatchOffsetOnRebalance implements ConsumerRebalanceListener {

    private String name;

    public WatchOffsetOnRebalance(String name) {
        this.name = name;
    }


    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        // save the offsets in an external store using some custom code not described here
        System.out.println(name + " Revoked " + partitions);
    }

    public void onPartitionsLost(Collection<TopicPartition> partitions) {
        // do not need to save the offsets since these partitions are probably owned by other consumers already
        System.out.println(name + " Lost " + partitions);
    }

    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        // read the offsets from an external store using some custom code not described here
        System.out.println(name + " Assigned " + partitions);
    }
}