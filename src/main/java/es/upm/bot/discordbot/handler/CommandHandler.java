package es.upm.bot.discordbot.handler;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import es.upm.bot.discordbot.elements.Article;

public class CommandHandler {

	private static final String newsEndpoint = "http://localhost:9999/";
	private ArrayList<EmbedCreateSpec> commandResponseEmbedList;
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
		
		case "b":{  
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(newsEndpoint + "newslist")).build();
			HttpResponse<String> response = null;
			try {
				response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			commandResponseEmbedList = toEmbedList(response.body());
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
	
	private ArrayList<EmbedCreateSpec> toEmbedList(String body) {
		ArrayList<EmbedCreateSpec> embedList = new ArrayList<>();
		StringReader sr = new StringReader(body);
        JsonReader reader = Json.createReader(sr);
		
		JsonArray array = reader.readArray();
		
		for(JsonValue jo : array) {
			JsonObject obj = jo.asJsonObject();
			Article article = new Article(obj.getString("title"), obj.getString("image"), 
					obj.getString("content"), obj.getString("authors"), obj.getString("link"));	
			System.err.println(article.toString());
			EmbedCreateSpec embed = EmbedCreateSpec.builder()
				    .color(Color.BLUE)
				    .title(obj.getString("title"))
				    .url(obj.getString("link"))
				    .author(obj.getString("authors"), null, null)
				    .image(obj.getString("image"))
				    .description(obj.getString("content"))
				    .timestamp(Instant.now())
				    .footer("NotiBot", obj.getString("favicon"))
				    .build();	
			embedList.add(embed);
		}
		
		return embedList;
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
			    .image(obj.getString("image"))
			    .description(obj.getString("content"))
			    .timestamp(Instant.now())
			    .footer("NotiBot", obj.getString("favicon"))
			    .build();	
		return embed;
	}

	public String getCommandResponse() {
		return commandResponse;
	}

	public EmbedCreateSpec getCommandResponseEmbed() {
		return commandResponseEmbed;
	}
	
	public ArrayList<EmbedCreateSpec> getCommandResponseEmbedList() {
		return commandResponseEmbedList;
	}
	

}
