package es.upm.bot.discordbot.bot;



import java.util.ArrayList;

import org.reactivestreams.Publisher;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.ReactiveEventAdapter;
import discord4j.core.event.domain.guild.GuildCreateEvent;
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

	private static CommandHandler handler = new CommandHandler();
	
	public static void main(String[] args) {
		GatewayDiscordClient client = DiscordClient.create(token)
				.login()
				.block();


		long applicationId = client.getRestClient().getApplicationId().block();

		Commands commands = new Commands(client, applicationId, guildId);
		commands.create();

		

		client.on(new ReactiveEventAdapter() {
			
			@Override
			public Publisher<?> onGuildCreate(GuildCreateEvent event) {
				handler.createBot(event.getGuild().getId().asLong(), event.getGuild().getName());
				return Mono.empty();
			}

			@Override
			public Publisher<?> onChatInputInteraction(ChatInputInteractionEvent event) {
				String userID = event.getInteraction().getUser().getId().asString();
				Long serverID = event.getInteraction().getGuildId().get().asLong();
				System.err.println( event.getInteraction().getUser().getId().asString());
				switch (event.getCommandName()) {
				case "news":
					return event.deferReply().withEphemeral(true).then(newsDefered(event, userID, serverID));

				case "change":
					return event.deferReply().withEphemeral(true).then(changeProviderDefered(event, userID, serverID));

				case "topic":
					return event.deferReply().withEphemeral(true).then(topicListDefered(event, userID, serverID));

				}

				return Mono.empty();
			}


			@Override
			public Publisher<?> onButtonInteraction(ButtonInteractionEvent event) {
				String userID = event.getInteraction().getUser().getId().asString();
				Long serverID = event.getInteraction().getGuildId().get().asLong();
				switch (event.getCustomId()) {
				case "next-news":
					return event.deferReply().withEphemeral(true).then(newsDeferedButton(event, userID, serverID));
				}

				return Mono.empty();
			}

			@Override
			public Publisher<?> onSelectMenuInteraction(SelectMenuInteractionEvent event) {
				String userID = event.getInteraction().getUser().getId().asString();
				Long serverID = event.getInteraction().getGuildId().get().asLong();
				switch (event.getCustomId()) {
				case "provider":
					String provider = event.getValues().toString();
					handler.changeProvider(userID, serverID, provider);
					return event.reply("Proveedor de noticias cambiado.").withEphemeral(true);

				case "topic":
					return event.deferReply().withEphemeral(true).then(changeTopicDefered(event, userID, serverID));
				}
				return Mono.empty();

			}

		}).blockLast();
	}

	private static Mono<Message> newsDefered(ChatInputInteractionEvent event, String userID, Long serverID){
		 return Mono.defer(() -> {
		        Button button = Button.success("next-news", "Siguientes");
		        return event.createFollowup()
		                .withEmbeds(handler.getNewsList(userID, serverID))
		                .withEphemeral(true)
		                .withComponents(ActionRow.of(button));
		    });
	}

	private static Mono<Message> newsDeferedButton(ButtonInteractionEvent event, String userID, Long serverID){
		Button button = Button.success("next-news", "Siguientes");
		return event.createFollowup().withEmbeds(handler.nextNews(userID, serverID))
				.withEphemeral(true).withComponents(ActionRow.of(button));
	}

	private static Mono<Message> changeProviderDefered(ChatInputInteractionEvent event, String userID, Long serverID){
		ArrayList<Topic> response = handler.getProviderList(userID, serverID);
		ArrayList<SelectMenu.Option> options = new ArrayList<>();

		for(Topic t : response) {
			options.add(SelectMenu.Option.of(t.getName(),t.getLink()));
		}

		SelectMenu selectProvider = SelectMenu.of("provider", options)
				.withMaxValues(1)
				.withMinValues(1);

		return event.createFollowup().withEphemeral(true).withComponents(ActionRow.of(selectProvider));
	}

	private static Mono<Message> topicListDefered(ChatInputInteractionEvent event, String userID, Long serverID){
		 return Mono.defer(() -> {
				ArrayList<Topic> response = handler.getTopicList(userID, serverID);
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
	private static Mono<Message> changeTopicDefered(SelectMenuInteractionEvent event, String userID, Long serverID){
		 return Mono.defer(() -> {
			 
			 Button button = Button.success("next-news", "Siguientes");
				String topic = event.getValues().toString();
				return event.createFollowup().withEmbeds(handler.changeTopic(userID, serverID, topic))
						.withEphemeral(true).withComponents(ActionRow.of(button));
				
		    });
	}


}