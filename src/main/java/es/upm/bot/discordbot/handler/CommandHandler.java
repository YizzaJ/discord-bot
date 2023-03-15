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
import es.upm.bot.discordbot.elements.Topic;

public class CommandHandler {

	private static final String newsEndpoint = "http://localhost:9999/";

	private HttpClient httpClient;
	public CommandHandler(){
		httpClient = HttpClient.newHttpClient();
	}
	
	public void createBot(Long serverID, String name){ 
		String API = newsEndpoint + "guilds/" + serverID + "/";
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API)).
				POST(BodyPublishers.ofString("[" + name + "]")).build();
		try {
			httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<EmbedCreateSpec> getNewsList(String user, Long serverID){ 
		String API = newsEndpoint + "news/" + serverID + "/" + user + "/";
		
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API)).build();
		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toEmbedArticleList(response.body());
	}

	public ArrayList<Topic> getProviderList(String user, Long serverID){ 
		String API = newsEndpoint + "providers/" + serverID + "/";

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API)).build();
		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return toProviderList(response.body());
	}

	public ArrayList<Topic> getTopicList(String user, Long serverID){  
		String API = newsEndpoint + "providers/" + serverID + "/" + user + "/topics/";
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API)).build();
		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toTopicList(response.body());

	}
	public ArrayList<EmbedCreateSpec> changeTopic(String user, Long serverID, String topic){  
		String API = newsEndpoint + "providers/" + serverID + "/" + user + "/topics/";
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API)).
				POST(BodyPublishers.ofString(topic)).build();
		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toEmbedArticleList(response.body());
	}

	public void changeProvider(String user, Long serverID, String provider){ 
		String API = newsEndpoint + "providers/" + serverID + "/" + user + "/";
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API)).
				POST(BodyPublishers.ofString(provider)).build();
		try {
			httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<EmbedCreateSpec> nextNews(String user, Long serverID){ 
		String API = newsEndpoint + "news/"+ serverID+ "/" + user + "/next/";
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API)).build();
		HttpResponse<String> response = null;
		try {
			response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toEmbedArticleList(response.body());
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
		JsonArray array = reader.readArray();

		for(JsonValue jo : array) {
			JsonObject obj = jo.asJsonObject();
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
}
