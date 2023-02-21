//package es.upm.bot.discordbot.bot;
//
//import java.util.ArrayList;
//
//import discord4j.core.DiscordClient;
//import discord4j.core.GatewayDiscordClient;
//import discord4j.core.event.domain.interaction.SelectMenuInteractionEvent;
//import discord4j.core.event.domain.message.MessageCreateEvent;
//import discord4j.core.object.component.ActionRow;
//import discord4j.core.object.component.SelectMenu;
//import discord4j.core.object.entity.Message;
//import discord4j.core.spec.EmbedCreateSpec;
//import discord4j.core.spec.MessageCreateSpec;
//import es.upm.bot.discordbot.elements.Topic;
//import es.upm.bot.discordbot.handler.CommandHandler;
//import reactor.core.publisher.Mono;
//
//public class Bot {
//
//	public static void main(String[] args) {
//		DiscordClient client = DiscordClient.create("MTA1OTgyNDAwNTk1MzA5NzczOA.GUL_xT.eqwfcC42PhzrB1KDyf9cNGzT3FWp1EtVeTSqGg");
//
//		SelectMenu select = SelectMenu.of("provider",
//				SelectMenu.Option.of("El mundo", "https://www.elmundo.es/"),
//				SelectMenu.Option.of("Antena 3", "https://www.antena3.com/noticias/"),
//				SelectMenu.Option.of("El pais", "https://elpais.com/"),
//				SelectMenu.Option.of("El universal", "https://www.eluniversal.com/")
//				).withMaxValues(1).withMinValues(1);
//
//
//
//
//		Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> {
//
//			Mono<Void> handleCommands = gateway.on(MessageCreateEvent.class, event -> {
//				Message message = event.getMessage();
//				if (message.getContent().equalsIgnoreCase("h")) {
//					String response = new CommandHandler(new String[]{"news",""}).getCommandResponse();
//					return message.getChannel()
//							.flatMap(channel -> channel.createMessage(response));
//				}
//				else if (message.getContent().equalsIgnoreCase("b") || message.getContent().equalsIgnoreCase("!news")) {
//					ArrayList<EmbedCreateSpec> response = new CommandHandler(new String[]{"!news",""}).getCommandResponseEmbedList();
//					Mono<Object> result = Mono.empty();
//					for (EmbedCreateSpec messageText : response) {
//						result = result.then(message.getChannel()
//								.flatMap(channel -> channel.createMessage(messageText))
//								.flatMap(x -> x.getChannel()));
//					}
//					return result;  	  
//				}
//				else if (message.getContent().equalsIgnoreCase("oldmenu") || message.getContent().equalsIgnoreCase("c")) {
//					return message.getChannel()
//							.flatMap(channel -> channel.createMessage(
//									MessageCreateSpec.builder()
//									.content("Selecciona tu proveedor de noticias.")
//									.addComponent(ActionRow.of(select))
//									.build()));
//				}
//				else if (message.getContent().equalsIgnoreCase("menu") || message.getContent().equalsIgnoreCase("c")) {
//					ArrayList<Topic> response = new CommandHandler(new String[]{"providers",""}).getTopicList();
//					ArrayList<SelectMenu.Option> options = new ArrayList<>();
//
//					for(Topic t : response) {
//						options.add(SelectMenu.Option.of(t.getName(),t.getLink()));
//					}
//
//					SelectMenu selectTopic = SelectMenu.of("provider", options)
//							.withMaxValues(1)
//							.withMinValues(1);
//
//					return message.getChannel()
//							.flatMap(channel -> channel.createMessage(
//									MessageCreateSpec.builder()
//									.content("Selecciona tu proveedor de noticias.")
//									.addComponent(ActionRow.of(selectTopic))
//									.build()));
//				}
//				else if (message.getContent().equalsIgnoreCase("topic") || message.getContent().equalsIgnoreCase("d")) {
//					ArrayList<Topic> response = new CommandHandler(new String[]{"lista",""}).getTopicList();
//					ArrayList<SelectMenu.Option> options = new ArrayList<>();
//
//					for(Topic t : response) {
//						options.add(SelectMenu.Option.of(t.getName(),t.getLink()));
//					}
//					SelectMenu selectTopic = SelectMenu.of("topic", options)
//							.withMaxValues(1)
//							.withMinValues(1);
//
//					return message.getChannel()
//							.flatMap(channel -> channel.createMessage(
//									MessageCreateSpec.builder()
//									.content("Selecciona la categoria")
//									.addComponent(ActionRow.of(selectTopic))
//									.build()));
//				}
//				return Mono.empty();
//			}).then();
//
//			Mono<Void> changeProvider = gateway.on(SelectMenuInteractionEvent .class, event -> {
//				if(event.getCustomId().equals("provider")){
//					String provider = event.getValues().toString();
//					new CommandHandler(new String[]{"!menu",provider}).getCommandResponse();
//					select.disabled();
//					return event.reply("Proveedor de noticias cambiado.");
//				}
//				else if(event.getCustomId().equals("topic")) {
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
//				}
//				else return Mono.empty();
//			}).then();
//
//			return handleCommands.and(changeProvider);
//		});
//
//
//		login.block();
//
//	}
//}
