package com.shyam.densityserver.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component()
public class ProcessImage {

    private Logger LOGGER = LoggerFactory.getLogger(ProcessImage.class);

    public CameraDensity processImage(File file) throws Exception {
        String predictionCommand = "/Users/sumith/Workspace/playground/darknet/hello.sh";
        String[] commands = {"bash", predictionCommand, file.getAbsolutePath()};
        Process proc = Runtime.getRuntime().exec(commands);

        InputStream stdIn = proc.getInputStream();
        InputStreamReader isr = new InputStreamReader(stdIn);
        BufferedReader br = new BufferedReader(isr);
        BufferedReader errinput = new BufferedReader(new InputStreamReader(
                proc.getErrorStream()));

        CameraDensity cameraDensity= getCameraDensity(file.getName());
        int density = 0;

        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("person")) {
                density++;
            }
        }

        while ((line = errinput.readLine()) != null) {
            LOGGER.error(line);
        }
        LOGGER.info("Number of persons: " + density);
        try {
            int exitVal = proc.waitFor();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage());
        }
        cameraDensity.setDensity(density);
        return cameraDensity;
    }

    private CameraDensity getCameraDensity(final String name) {
        String[] names = name.split("-");
        final CameraDensity cameraDensity = new CameraDensity();
        cameraDensity.setCameraId(names[0]);
        cameraDensity.setLocationId(names[1]);
        return cameraDensity;
    }
}
