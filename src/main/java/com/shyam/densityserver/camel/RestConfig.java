package com.shyam.densityserver.camel;

import com.shyam.densityserver.core.CameraDensity;
import com.shyam.densityserver.core.DensitySocketServer;
import com.shyam.densityserver.core.RawImage;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;

@Component
public class RestConfig extends RouteBuilder {

    @Value("${webservice.api.path}")
    private String contextPath;

    @Value("${server.port}")
    private String serverPort;

    @Override
    public void configure() throws Exception {
        CamelContext context = new DefaultCamelContext();

        restConfiguration()
                .contextPath(contextPath)
                .port(serverPort)
                .enableCORS(true)
                .corsAllowCredentials(true)
                .corsHeaderProperty("Access-Control-Allow-Origin","*")
                .corsHeaderProperty("Access-Control-Allow-Headers","Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization")
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "Test REST API")
                .apiProperty("api.version", "v1")
                .apiContextRouteId("doc-api")
                .component("servlet")
                .bindingMode(RestBindingMode.json);

        rest("/camera/")
                .id("api-density")
                .consumes("application/json")
                .post("/density")
                .bindingMode(RestBindingMode.json)
                .type(CameraDensity.class)
                .to("direct:remoteService");

        from("direct:remoteService")
                .wireTap("seda:density-request")
                .routeId("direct-route")
                .log(">>> ${body}")
                .setBody(simple(""))
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(200));

        rest("/camera/")
                .id("api-route")
                .post("/raw")
                .bindingMode(RestBindingMode.json)
                .type(RawImage.class)
                .to("direct:rawImage");

    }

    @Bean(name = "densitySocketServer")
    public DensitySocketServer densitySocketServer() throws UnknownHostException {
        return new DensitySocketServer();
    }

}
