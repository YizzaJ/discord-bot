package es.upm.bot.discordbot.bot;



import java.util.ArrayList;
import java.util.Random;

import org.reactivestreams.Publisher;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.ReactiveEventAdapter;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputAutoCompleteEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteraction;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
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

				switch (event.getCommandName()) {
				case "news":
					return event.deferReply().withEphemeral(true).then(newsDefered(event));

				case "change":
					return event.deferReply().withEphemeral(true).then(changeProviderDefered(event));

				case "topic":
					return event.deferReply().withEphemeral(true).then(changeTopicDefered(event));

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
				switch (event.getCustomId()) {
				case "next-news":
					return event.deferReply().withEphemeral(true).then(newsDeferedButton(event));	
				}

				return Mono.empty();
			}

			@Override
			public Publisher<?> onSelectMenuInteraction(SelectMenuInteractionEvent event) {

				switch (event.getCustomId()) {
				case "provider":
					String provider = event.getValues().toString();
					new CommandHandler(new String[]{"!menu",provider}).getCommandResponse();
					return event.reply("Proveedor de noticias cambiado.");

				case "topic":
					Button button = Button.success("next-news", "Siguientes");
					String topic = event.getValues().toString();
					return event.reply().withEmbeds(new CommandHandler(new String[]{"!topic",topic})
							.getCommandResponseEmbedList()).withEphemeral(true).withComponents(ActionRow.of(button));

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

	private static Mono<Message> newsDefered(ChatInputInteractionEvent event){
		Button button = Button.success("next-news", "Siguientes");
		return event.createFollowup().withEmbeds(new CommandHandler(new String[]{"!news",""})
				.getCommandResponseEmbedList()).withEphemeral(true).withComponents(ActionRow.of(button));
	}

	private static Mono<Message> newsDeferedButton(ButtonInteractionEvent event){
		Button button = Button.success("next-news", "Siguientes");
		return event.createFollowup().withEmbeds(new CommandHandler(new String[]{"!news",""})
				.getCommandResponseEmbedList()).withEphemeral(true).withComponents(ActionRow.of(button));
	}

	private static Mono<Message> changeProviderDefered(ChatInputInteractionEvent event){
		ArrayList<Topic> response = new CommandHandler(new String[]{"providers",""}).getTopicList();
		ArrayList<SelectMenu.Option> options = new ArrayList<>();

		for(Topic t : response) {
			options.add(SelectMenu.Option.of(t.getName(),t.getLink()));
		}

		SelectMenu selectProvider = SelectMenu.of("provider", options)
				.withMaxValues(1)
				.withMinValues(1);

		return event.createFollowup().withEphemeral(true).withComponents(ActionRow.of(selectProvider));
	}

	private static Mono<Message> changeTopicDefered(ChatInputInteractionEvent event){
		ArrayList<Topic> response = new CommandHandler(new String[]{"lista",""}).getTopicList();
		ArrayList<SelectMenu.Option> options = new ArrayList<>();

		for(Topic t : response) {
			options.add(SelectMenu.Option.of(t.getName(),t.getLink()));
		}
		SelectMenu selectTopic = SelectMenu.of("topic", options)
				.withMaxValues(1)
				.withMinValues(1);

		return event.createFollowup().withEphemeral(true).withComponents(ActionRow.of(selectTopic));
	}


}