package es.upm.bot.discordbot.handler;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
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
import es.upm.bot.discordbot.elements.Topic;

public class CommandHandler {

	private static final String newsEndpoint = "http://localhost:9999/";
	private ArrayList<EmbedCreateSpec> commandResponseEmbedList;
	private ArrayList<Topic> topicList;
	private String commandResponse;
	private EmbedCreateSpec commandResponseEmbed;

	public CommandHandler(String[] message){	

		HttpClient httpClient = HttpClient.newHttpClient();	 
		String command = message[0];
		String content = message[1];
		String user = message[2];

		String API = newsEndpoint + ""+ user + "/";
		switch(command) {
		case "news":{  
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API + "news")).build();
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
			
			break;
		}
		case "new":{  
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API + "news")).build();
			HttpResponse<String> response = null;
			try {
				response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			commandResponseEmbed = toEmbed(response.body());
			break;
		}

		case "!news":{  
			System.err.println("MANDO GET POR: " +  API + "newslist");
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API + "newslist")).build();
			HttpResponse<String> response = null;
			try {
				response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			commandResponseEmbedList = toEmbedArticleList(response.body());
			break;
		}
		
		case "!nextNews":{  
			System.err.println("MANDO GET POR: " +  API + "nextnews");
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API + "nextnews")).build();
			HttpResponse<String> response = null;
			try {
				response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			commandResponseEmbedList = toEmbedArticleList(response.body());
			break;
		}

		case "!menu":{  
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API + "change")).
					POST(BodyPublishers.ofString(content)).build();
			HttpResponse<String> response = null;
			try {
				response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}

		case "lista":{  
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API + "topiclist")).build();
			HttpResponse<String> response = null;
			try {
				response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.err.println("BODY " + response.body());
			topicList = toTopicList(response.body());
			break;
		}
		
		case "providers":{  
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API + "providerlist")).build();
			HttpResponse<String> response = null;
			try {
				response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.err.println("BODY " + response.body());
			topicList = toProviderList(response.body());
			break;
		}

		case "!topic":{  
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API + "topic")).
					POST(BodyPublishers.ofString(content)).build();
			HttpResponse<String> response = null;
			try {
				response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			commandResponseEmbedList = toEmbedArticleList(response.body());
			break;
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

	private ArrayList<Topic> toTopicList(String body) {
		ArrayList<Topic> topicList = new ArrayList<>();
		StringReader sr = new StringReader(body);
		JsonReader reader = Json.createReader(sr);

		JsonArray array = reader.readArray();
		System.err.println("ARRAY " + array.toString());
		for(JsonValue jo : array) {
			JsonObject obj = jo.asJsonObject();
			topicList.add(new Topic(obj.getString("name"), obj.getString("link")));	
		}
		return topicList;
	}
	
	private ArrayList<Topic> toProviderList(String body) {
		ArrayList<Topic> topicList = new ArrayList<>();
		StringReader sr = new StringReader(body);
		JsonReader reader = Json.createReader(sr);

		JsonArray array = reader.readArray();
		System.err.println("ARRAY " + array.toString());
		for(JsonValue jo : array) {
			JsonObject obj = jo.asJsonObject();
			topicList.add(new Topic(obj.getString("name"), obj.getString("webSite")));	
		}
		return topicList;
	}

	private ArrayList<EmbedCreateSpec> toEmbedArticleList(String body) {
		ArrayList<EmbedCreateSpec> embedList = new ArrayList<>();
		StringReader sr = new StringReader(body);
		JsonReader reader = Json.createReader(sr);
		System.err.println("BODYTOPICOOOOOO " + body);
		JsonArray array = reader.readArray();

		for(JsonValue jo : array) {
			JsonObject obj = jo.asJsonObject();
			Article article = new Article(obj.getString("title"), obj.getString("image"), 
					obj.getString("content"), obj.getString("authors"), obj.getString("link"));	
			EmbedCreateSpec embed = EmbedCreateSpec.builder()
					.color(Color.BLUE)
					.title(obj.getString("title"))
					.url(obj.getString("link"))
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

	public ArrayList<Topic> getTopicList() {
		return topicList;
	}


}
