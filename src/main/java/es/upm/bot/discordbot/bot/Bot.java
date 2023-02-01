package es.upm.bot.discordbot.bot;

import java.util.ArrayList;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import es.upm.bot.discordbot.handler.CommandHandler;
import reactor.core.publisher.Mono;

public class Bot {

	public static void main(String[] args) {
		DiscordClient client = DiscordClient.create("MTA1OTgyNDAwNTk1MzA5NzczOA.GUL_xT.eqwfcC42PhzrB1KDyf9cNGzT3FWp1EtVeTSqGg");

		Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
			  // ReadyEvent example
			  Mono<Void> printOnLogin = gateway.on(ReadyEvent.class, event ->
			      Mono.fromRunnable(() -> {
			        final User self = event.getSelf();
			        System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
			      }))
			      .then();

			  
			  
			  
			  // MessageCreateEvent example
			  Mono<Void> handlePingCommand = gateway.on(MessageCreateEvent.class, event -> {
			    Message message = event.getMessage();
			    if (message.getContent().equalsIgnoreCase("h")) {
			    	String response = new CommandHandler(new String[]{"news",""}).getCommandResponse();
			      return message.getChannel()
			          .flatMap(channel -> channel.createMessage(response));
			    }
			    else if (message.getContent().equalsIgnoreCase("a")) {
			    	EmbedCreateSpec response = new CommandHandler(new String[]{"a",""}).getCommandResponseEmbed();
				      return message.getChannel()
				          .flatMap(channel -> channel.createMessage(response));
				    }
			    else if (message.getContent().equalsIgnoreCase("b") || message.getContent().equalsIgnoreCase("!news")) {
			    	ArrayList<EmbedCreateSpec> response = new CommandHandler(new String[]{"b",""}).getCommandResponseEmbedList();
			        Mono<Object> result = Mono.empty();
			        for (EmbedCreateSpec messageText : response) {
			            result = result.then(message.getChannel()
			                .flatMap(channel -> channel.createMessage(messageText))
			                .flatMap(x -> x.getChannel()));
			        }
			        return result;

				    	  
				    }

			    return Mono.empty();
			  }).then();

			  // combine them!
			  return printOnLogin.and(handlePingCommand);
			});
		
		
		login.block();

	}
}
