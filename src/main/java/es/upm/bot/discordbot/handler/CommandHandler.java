package es.upm.bot.discordbot.handler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CommandHandler {

	private static final String newsEndpoint = "http://localhost:9999/";
	private String commandResponse;

	public CommandHandler(String[] message){	

		HttpClient httpClient = HttpClient.newHttpClient();	 
		String command = message[0];
		String content = message[0];

		switch(command) {
		case "news":{  
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:9999/news")).build();
			HttpResponse<String> response = null;
			try {
				response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			commandResponse = response.body();
		}


		}

	}

	public String getCommandResponse() {
		return commandResponse;
	}

}
