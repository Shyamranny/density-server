package com.shyam.densityserver.camel;

import com.shyam.densityserver.core.ArrayListAggregationStrategy;
import com.shyam.densityserver.core.CameraDensity;
import com.shyam.densityserver.core.ProcessImage;
import com.shyam.densityserver.core.RawImage;
import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregateController;
import org.apache.camel.processor.aggregate.DefaultAggregateController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;

@Component()
public class RawImageConfig extends RouteBuilder {

    @Value("${camera.rawImageLocation}")
    private String raeImageLocation;

    private static final String FILE_NAME_SEPARATOR = "-";

    private Logger LOGGER = LoggerFactory.getLogger(RawImageConfig.class);

    @Autowired()
    private ProcessImage processImage;

    private AggregateController controller = new DefaultAggregateController();

    @Override()
    public void configure() throws Exception {

        from("direct:rawImage")
                .process(
                        exchange -> {
                            RawImage rawImage = exchange.getIn().getBody(RawImage.class);
                            byte[] decodedString = Base64.getDecoder().decode(rawImage.getRawImage());
                            InputStream inputStream = new ByteArrayInputStream(decodedString);
                            exchange.getIn().setBody(inputStream);
                            exchange.getIn().setHeader(Exchange.FILE_NAME, generateFileName(rawImage));
                        })
                .to("file://" + raeImageLocation)
                .setBody(simple("Done"));


        from("file://" + raeImageLocation + "?delay=10000&recursive=true&delete=true")
                .process(
                        exchange -> {
                            File file = exchange.getIn().getBody(File.class);
                            LOGGER.info("Processing file: " + file.getName());
                            final CameraDensity density = processImage.processImage(file);
                            exchange.getIn().setBody(density);
                            exchange.getIn().setHeader(Exchange.CORRELATION_ID, density.getLocationId());
                        }
                )
                .to("seda:density-result");



//        from("seda:density-result")
//                .log("++++++++++ ${body.cameraId}")
//                .log("++++++++++ ${body.density}")
//                .log("++++++++++ ${body.locationId}")
//                .aggregate(header(Exchange.CORRELATION_ID), new ArrayListAggregationStrategy())
//                .completionSize(5)
//                .completionTimeout(500L)
//                .id("myAggregator")
//                .aggregateController(controller)
//                .to("seda:density-aggregator");

        from("seda:density-result")
                .log("++++++++++ ${body.cameraId}")
                .log("++++++++++ ${body.density}")
                .log("++++++++++ ${body.locationId}")
                .aggregate(header(Exchange.CORRELATION_ID), new ArrayListAggregationStrategy())
                .completionSize(5)
                //.completionTimeout(500L)
                .id("myAggregator")
                .aggregateController(controller)
                .to("seda:density-aggregator");

        from("seda:density-aggregator")
                .process(exchange -> {

                    ArrayList<CameraDensity> list = exchange.getIn().getBody(ArrayList.class);

                    int total = 0;
                    for (CameraDensity cd : list) {
                        total += cd.getDensity();
                    }

                    CameraDensity out = list.get(0);
                    out.setDensity(total / list.size());
                    exchange.getIn().setBody(out);

                })
                .convertBodyTo(String.class)
                .bean("densitySocketServer", "push");


    }
    private String generateFileName(final RawImage rawImage) {
        StringBuilder sb = new StringBuilder(rawImage.getCameraId())
                .append(FILE_NAME_SEPARATOR)
                .append(rawImage.getLocationId())
                .append(FILE_NAME_SEPARATOR)
                .append(UUID.randomUUID().toString());
        return sb.toString();
    }
}
