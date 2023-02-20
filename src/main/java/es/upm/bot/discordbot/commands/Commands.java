package es.upm.bot.discordbot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import es.upm.bot.discordbot.elements.Topic;
import es.upm.bot.discordbot.handler.CommandHandler;

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

		delete(); //TODO ELIMINAR AL ESTAR EN DESPLIEGUE

		ApplicationCommandRequest showNews = ApplicationCommandRequest.builder()
				.name("news")
				.description("Muestra las noticias en el maximo especificado.")
				.build();

		client.getRestClient().getApplicationService()
		.createGuildApplicationCommand(applicationId, guildId, showNews)
		.subscribe();

		ApplicationCommandRequest change = ApplicationCommandRequest.builder()
				.name("change")
				.description("Cambia el proveedor de noticias.")
				.build();

		client.getRestClient().getApplicationService()
		.createGuildApplicationCommand(applicationId, guildId, change)
		.subscribe();
		
		ApplicationCommandRequest topic = ApplicationCommandRequest.builder()
				.name("topic")
				.description("Cambia la categoria de las noticias.")
				.build();

		client.getRestClient().getApplicationService()
		.createGuildApplicationCommand(applicationId, guildId, topic)
		.subscribe();

//		ApplicationCommandRequest changeProvider = ApplicationCommandRequest.builder()
//				.name("provider")
//				.description("Cambia el proveedor de noticias.")
//				.addOption(ApplicationCommandOptionData.builder()
//						.name("provider")
//						.description("Proveedor de noticias al que quieres cambiar.")
//						.type(ApplicationCommandOption.Type.STRING.getValue())
//						.addAllChoices(loadProviders())
//						.required(true)
//						.build()
//						).build();
//
//		client.getRestClient().getApplicationService()
//		.createGuildApplicationCommand(applicationId, guildId, changeProvider)
//		.subscribe();
	}

	public List<ApplicationCommandOptionChoiceData> loadProviders(){
		List<ApplicationCommandOptionChoiceData> providers = new ArrayList<>();
		ArrayList<Topic> response = new CommandHandler(new String[]{"providers",""}).getTopicList();

		for(Topic t : response)
			providers.add(ApplicationCommandOptionChoiceData.builder().name(t.getName()).value(t.getLink()).build());
		return providers;
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
