package es.upm.bot.discordbot.commands;

import java.util.Map;

import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.discordjson.json.ApplicationCommandRequest;

public class Commands {

	private GatewayDiscordClient client;
	private long applicationId;
	private long guildId;

	public Commands(GatewayDiscordClient client, long applicationId, long guildId) {
		this.client = client;
		this.applicationId = applicationId;
		this.guildId = guildId;

	}

	public void create() {
		
		ApplicationCommandRequest showNews = ApplicationCommandRequest.builder()
				.name("noticias")
				.description("Muestra las noticias en el maximo especificado.")
				.build();

		client.getRestClient().getApplicationService()
		.createGlobalApplicationCommand(applicationId, showNews)
		.subscribe();

		ApplicationCommandRequest change = ApplicationCommandRequest.builder()
				.name("proveedor")
				.description("Cambia el proveedor de noticias.")
				.build();

		client.getRestClient().getApplicationService()
		.createGlobalApplicationCommand(applicationId, change)
		.subscribe();
		
		ApplicationCommandRequest topic = ApplicationCommandRequest.builder()
				.name("categorias")
				.description("Cambia la categoria de las noticias.")
				.build();

		client.getRestClient().getApplicationService()
		.createGlobalApplicationCommand(applicationId, topic)
		.subscribe();

		delete(); //TODO ELIMINAR AL ESTAR EN DESPLIEGUE
//
//		ApplicationCommandRequest showNews = ApplicationCommandRequest.builder()
//				.name("noticias")
//				.description("Muestra las noticias en el maximo especificado.")
//				.build();
//
//		client.getRestClient().getApplicationService()
//		.createGuildApplicationCommand(applicationId, guildId, showNews)
//		.subscribe();
//
//		ApplicationCommandRequest change = ApplicationCommandRequest.builder()
//				.name("proveedor")
//				.description("Cambia el proveedor de noticias.")
//				.build();
//
//		client.getRestClient().getApplicationService()
//		.createGuildApplicationCommand(applicationId, guildId, change)
//		.subscribe();
//		
//		ApplicationCommandRequest topic = ApplicationCommandRequest.builder()
//				.name("categorias")
//				.description("Cambia la categoria de las noticias.")
//				.build();
//
//		client.getRestClient().getApplicationService()
//		.createGuildApplicationCommand(applicationId, guildId, topic)
//		.subscribe();
	}

	public void delete() {

		Map<String, ApplicationCommandData> commands = client.getRestClient()
				.getApplicationService()
				.getGuildApplicationCommands(applicationId, guildId)
				.collectMap(ApplicationCommandData::name)
				.block();


		for(String commandName : commands.keySet()) {
			long commandId = Long.parseLong(commands.get(commandName).id().asString());
			client.getRestClient().getApplicationService()
			.deleteGuildApplicationCommand(applicationId, guildId, commandId)
			.subscribe();
		}

	}





}
