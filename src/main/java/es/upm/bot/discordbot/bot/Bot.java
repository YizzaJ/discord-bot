package es.upm.bot.discordbot.bot;

import java.util.ArrayList;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.SelectMenu;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateSpec;
import es.upm.bot.discordbot.handler.CommandHandler;
import reactor.core.publisher.Mono;

public class Bot {

	public static void main(String[] args) {
		DiscordClient client = DiscordClient.create("MTA1OTgyNDAwNTk1MzA5NzczOA.GUL_xT.eqwfcC42PhzrB1KDyf9cNGzT3FWp1EtVeTSqGg");

		SelectMenu select = SelectMenu.of("123445",
				SelectMenu.Option.of("El mundo", "mundo"),
				SelectMenu.Option.of("Antena 3", "antena"),
				SelectMenu.Option.of("El pais", "pais"),
				SelectMenu.Option.of("El universal", "universal")
				).withMaxValues(1).withMinValues(1);


		Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {

			Mono<Void> handleCommands = gateway.on(MessageCreateEvent.class, event -> {
				Message message = event.getMessage();
				if (message.getContent().equalsIgnoreCase("h")) {
					String response = new CommandHandler(new String[]{"news",""}).getCommandResponse();
					return message.getChannel()
							.flatMap(channel -> channel.createMessage(response));
				}
				else if (message.getContent().equalsIgnoreCase("a")) {
					EmbedCreateSpec response = new CommandHandler(new String[]{"new",""}).getCommandResponseEmbed();
					return message.getChannel()
							.flatMap(channel -> channel.createMessage(response));
				}
				else if (message.getContent().equalsIgnoreCase("b") || message.getContent().equalsIgnoreCase("!news")) {
					ArrayList<EmbedCreateSpec> response = new CommandHandler(new String[]{"!news",""}).getCommandResponseEmbedList();
					Mono<Object> result = Mono.empty();
					for (EmbedCreateSpec messageText : response) {
						result = result.then(message.getChannel()
								.flatMap(channel -> channel.createMessage(messageText))
								.flatMap(x -> x.getChannel()));
					}
					return result;  	  
				}
				else if (message.getContent().equalsIgnoreCase("menu")) {
					return message.getChannel()
							.flatMap(channel -> channel.createMessage(
									MessageCreateSpec.builder()
									.content("Selecciona tu proveedor de noticias.")
									.addComponent(ActionRow.of(select))
									.build()));
				}
				return Mono.empty();
			}).then();

			Mono<Void> changeProvider = gateway.on(SelectMenuInteractionEvent .class, event -> {
				String provider = event.getValues().toString();
				new CommandHandler(new String[]{"!news",provider}).getCommandResponse();
				select.disabled();
				return event.reply("Proveedor de noticias cambiado.");
			}).then();

			return handleCommands.and(changeProvider);
		});


		login.block();

	}
}
