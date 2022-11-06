package ec33nw.map.datahandler;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ec33nw.map.datahandler.controllers.iexcloud.IexCloudGrpcServer;

@SpringBootApplication
public class DatahandlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatahandlerApplication.class, args);
		
		IexCloudGrpcServer server;
		try {
			server = new IexCloudGrpcServer(8980);
		    server.start();
		    server.blockUntilShutdown();
		} catch (IOException e) {
			System.out.println(e.getClass().toString() + "______" + e.getMessage());
		} catch (InterruptedException e) {
			System.out.println(e.getClass().toString() + "______" + e.getMessage());
		}

	}

}
