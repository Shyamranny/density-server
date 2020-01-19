package com.shyam.densityserver.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DensityIdentificationConfig extends RouteBuilder {

    @Override
    public void configure() throws Exception {
       // from("seda:density-request");
    }
}
