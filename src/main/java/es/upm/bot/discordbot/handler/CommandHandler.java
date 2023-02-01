package es.upm.bot.discordbot.handler;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import es.upm.bot.discordbot.elements.Article;

public class CommandHandler {

	private static final String newsEndpoint = "http://localhost:9999/";
	private String commandResponse;
	private EmbedCreateSpec commandResponseEmbed;

	public CommandHandler(String[] message){	

		HttpClient httpClient = HttpClient.newHttpClient();	 
		String command = message[0];
		String content = message[0];

	
		switch(command) {
		case "news":{  
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(newsEndpoint + "news")).build();
			HttpResponse<String> response = null;
			try {
				response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String body = response.body();
			System.out.println(body);
			System.out.println();
			System.out.println(toArticle(body).toString());
			body = body.substring(0,(body.length() >= 500 ? 500 : body.length()));
			commandResponse = body;
		}
		case "a":{  
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(newsEndpoint + "news")).build();
			HttpResponse<String> response = null;
			try {
				response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			commandResponseEmbed = toEmbed(response.body());
		}


		}

	}
	
	private Article toArticle(String body) {
		StringReader sr = new StringReader(body);
        JsonReader reader = Json.createReader(sr);
		JsonObject obj = reader.readObject();
		Article article = new Article(obj.getString("title"), obj.getString("image"), 
				obj.getString("content"), obj.getString("authors"), obj.getString("link"));		
		return article;
	}
	
	private EmbedCreateSpec toEmbed(String body) {
		StringReader sr = new StringReader(body);
        JsonReader reader = Json.createReader(sr);
		JsonObject obj = reader.readObject();
		EmbedCreateSpec embed = EmbedCreateSpec.builder()
			    .color(Color.BLUE)
			    .title(obj.getString("title"))
			    .url(obj.getString("link"))
			    .author(obj.getString("authors"), null, null)
			    .description(obj.getString("content"))
			    .image("https://i.imgur.com/F9BhEoz.png")
			    .timestamp(Instant.now())
			    .footer("footer", "https://i.imgur.com/F9BhEoz.png")
			    .build();	
		return embed;
	}

	public String getCommandResponse() {
		return commandResponse;
	}

	public EmbedCreateSpec getCommandResponseEmbed() {
		return commandResponseEmbed;
	}
	

}
