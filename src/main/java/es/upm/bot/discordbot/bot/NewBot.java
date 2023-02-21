package es.upm.bot.discordbot.bot;



import java.util.ArrayList;

import org.reactivestreams.Publisher;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.ReactiveEventAdapter;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Message;
import es.upm.bot.discordbot.commands.Commands;
import es.upm.bot.discordbot.elements.Topic;
import es.upm.bot.discordbot.handler.CommandHandler;
import reactor.core.publisher.Mono;

public class NewBot {

	private static final String token = "MTA1OTgyNDAwNTk1MzA5NzczOA.GUL_xT.eqwfcC42PhzrB1KDyf9cNGzT3FWp1EtVeTSqGg";
	private static final long guildId = 1059854853721030719L;

	public static void main(String[] args) {
		GatewayDiscordClient client = DiscordClient.create(token)
				.login()
				.block();

		// Get our application's ID
		long applicationId = client.getRestClient().getApplicationId().block();

		Commands commands = new Commands(client, applicationId, guildId);
		commands.create();
		//						commands.delete();

		client.on(new ReactiveEventAdapter() {


			@Override
			public Publisher<?> onChatInputInteraction(ChatInputInteractionEvent event) {
				String userID = event.getInteraction().getUser().getId().asString();
				System.err.println( event.getInteraction().getUser().getId().asString());
				switch (event.getCommandName()) {
				case "news":
					return event.deferReply().withEphemeral(true).then(newsDefered(event, userID));

				case "change":
					return event.deferReply().withEphemeral(true).then(changeProviderDefered(event, userID));

				case "topic":
					return event.deferReply().withEphemeral(true).then(topicListDefered(event, userID));

					//				case "provider":
					//					String provider = event.getOption("provider")
					//					.flatMap(ApplicationCommandInteractionOption::getValue)
					//					.map(ApplicationCommandInteractionOptionValue::asString)
					//					.get(); 
					//					System.err.println("CAMBIAMOS A " + provider);
					//					new CommandHandler(new String[]{"!menu",provider}).getCommandResponse();
					//					return event.reply("Proveedor de noticias cambiado.").withEphemeral(true);
				}

				return Mono.empty();
			}


			@Override
			public Publisher<?> onButtonInteraction(ButtonInteractionEvent event) {
				String userID = event.getInteraction().getUser().getId().asString();
				switch (event.getCustomId()) {
				case "next-news":
					Button button = Button.success("next-news", "Siguientes");
					return event.deferReply().withEphemeral(true).then(newsDeferedButton(event, userID));
				}

				return Mono.empty();
			}

			@Override
			public Publisher<?> onSelectMenuInteraction(SelectMenuInteractionEvent event) {
				String userID = event.getInteraction().getUser().getId().asString();
				switch (event.getCustomId()) {
				case "provider":
					String provider = event.getValues().toString();
					new CommandHandler(new String[]{"!menu",provider, userID}).getCommandResponse();
					return event.reply("Proveedor de noticias cambiado.").withEphemeral(true);

				case "topic":
					return event.deferReply().withEphemeral(true).then(changeTopicDefered(event, userID));

//				case "topic2":
//					String topic = event.getValues().toString();
//					ArrayList<EmbedCreateSpec> response = new CommandHandler(new String[]{"!topic",topic}).getCommandResponseEmbedList();
//
//
//					Mono<Void> result = Mono.empty();
//					for (EmbedCreateSpec messageText : response) {
//						result = result.then(event.getInteraction().getChannel()
//								.flatMap(channel -> channel.createMessage(messageText))
//								.flatMap(x -> x.getChannel()))
//								.then();
//					}
//					return result;
				}
				return Mono.empty();

			}

		}).blockLast();
		System.err.println("CERRAMOS BOT");
		commands.delete();
	}

	private static Mono<Message> newsDefered(ChatInputInteractionEvent event, String userID){
		 return Mono.defer(() -> {
		        Button button = Button.success("next-news", "Siguientes");
		        return event.createFollowup()
		                .withEmbeds(new CommandHandler(new String[]{"!news", "", userID})
		                        .getCommandResponseEmbedList())
		                .withEphemeral(true)
		                .withComponents(ActionRow.of(button));
		    });
	}

	private static Mono<Message> newsDeferedButton(ButtonInteractionEvent event, String userID){
		Button button = Button.success("next-news", "Siguientes");
		return event.createFollowup().withEmbeds(new CommandHandler(new String[]{"!nextNews","", userID})
				.getCommandResponseEmbedList()).withEphemeral(true).withComponents(ActionRow.of(button));
	}

	private static Mono<Message> changeProviderDefered(ChatInputInteractionEvent event, String userID){
		ArrayList<Topic> response = new CommandHandler(new String[]{"providers","", userID}).getTopicList();
		ArrayList<SelectMenu.Option> options = new ArrayList<>();

		for(Topic t : response) {
			options.add(SelectMenu.Option.of(t.getName(),t.getLink()));
		}

		SelectMenu selectProvider = SelectMenu.of("provider", options)
				.withMaxValues(1)
				.withMinValues(1);

		return event.createFollowup().withEphemeral(true).withComponents(ActionRow.of(selectProvider));
	}

	private static Mono<Message> topicListDefered(ChatInputInteractionEvent event, String userID){
		 return Mono.defer(() -> {
				ArrayList<Topic> response = new CommandHandler(new String[]{"lista","", userID}).getTopicList();
				ArrayList<SelectMenu.Option> options = new ArrayList<>();

				for(Topic t : response) {
					options.add(SelectMenu.Option.of(t.getName(),t.getLink()));
				}
				SelectMenu selectTopic = SelectMenu.of("topic", options)
						.withMaxValues(1)
						.withMinValues(1);

				return event.createFollowup().withEphemeral(true).withComponents(ActionRow.of(selectTopic));
		    });
	}
	private static Mono<Message> changeTopicDefered(SelectMenuInteractionEvent event, String userID){
		 return Mono.defer(() -> {
			 
			 Button button = Button.success("next-news", "Siguientes");
				String topic = event.getValues().toString();
				return event.createFollowup().withEmbeds(new CommandHandler(new String[]{"!topic",topic, userID})
						.getCommandResponseEmbedList()).withEphemeral(true).withComponents(ActionRow.of(button));
				
		    });
	}


}