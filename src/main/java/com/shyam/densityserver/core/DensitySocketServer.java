package com.shyam.densityserver.core;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DensitySocketServer extends WebSocketServer {

    private static List<WebSocket> clients = new ArrayList<>();

    private static final  int PORT = 8887;

    private Logger LOGGER = LoggerFactory.getLogger(DensitySocketServer.class);

    public DensitySocketServer() throws UnknownHostException {
        this(PORT);
    }

    public DensitySocketServer( int port ) throws UnknownHostException {
        super( new InetSocketAddress( port ) );
    }

    public DensitySocketServer( InetSocketAddress address ) {
        super( address );
    }

    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
        conn.send("Welcome to the server!"); //This method sends a message to the new client
        broadcast( "new connection: " + handshake.getResourceDescriptor() );
        clients.add(conn);//This method sends a message to all clients connected
        System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!" );
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
        broadcast( conn + " has left the room!" );
        System.out.println( conn + " has left the room!" );
    }

    @Override
    public void onMessage( WebSocket conn, String message ) {
        broadcast( message );
        System.out.println( conn + ": " + message );
    }
    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        broadcast( message.array() );
        System.out.println( conn + ": " + message );
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        ex.printStackTrace();
        if( conn != null ) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    @Override
    public void onStart() {
        System.out.println("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    @PostConstruct
    public void startServer() throws Exception {

        DensitySocketServer s = new DensitySocketServer( PORT );
        s.start();
        System.out.println( "ChatServer started on port: " + s.getPort() );


    }

    @PreDestroy
    public void preDestroy() throws IOException, InterruptedException {
        this.stop();
    }

    public void push(String message) {
        LOGGER.info("Received Message in push method: "+message);
        if(!clients.isEmpty()) {
            clients.forEach(e -> e.send(message));
        }

    }
}
