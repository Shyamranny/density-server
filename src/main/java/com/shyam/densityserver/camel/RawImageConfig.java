package com.shyam.densityserver.camel;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;

import javax.activation.DataHandler;
import javax.mail.internet.MimeBodyPart;
import java.io.InputStream;

public class RawImageConfig extends RouteBuilder {

    @Value("${camera.rawImageLocation}")
    private String raeImageLocation;

    @Override
    public void configure() throws Exception {

        from("direct:rawImage")
                .process(exchange -> {
                    InputStream is = exchange.getIn().getBody(InputStream.class);
                    MimeBodyPart mimeMessage = new MimeBodyPart(is);
                    DataHandler dh = mimeMessage.getDataHandler();
                    exchange.getIn().setBody(dh.getInputStream());
                    exchange.getIn().setHeader(Exchange.FILE_NAME, dh.getName());
                })
        .to("file:/" + raeImageLocation);

    }
}
