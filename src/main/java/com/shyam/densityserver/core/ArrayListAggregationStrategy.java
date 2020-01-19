package com.shyam.densityserver.core;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

import java.util.ArrayList;

public class ArrayListAggregationStrategy implements AggregationStrategy {

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        CameraDensity newBody = newExchange.getIn().getBody(CameraDensity.class);
        ArrayList<CameraDensity> list;
        if (oldExchange == null) {
            list = new ArrayList<CameraDensity>();
            list.add(newBody);
            newExchange.getIn().setBody(list);
            return newExchange;
        } else {
            list = oldExchange.getIn().getBody(ArrayList.class);
            list.add(newBody);
            return oldExchange;
        }
    }
}