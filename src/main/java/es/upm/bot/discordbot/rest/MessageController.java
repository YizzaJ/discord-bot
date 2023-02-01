package es.upm.bot.discordbot.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

	@PostMapping("/bot/send-message")
	public void receiveMessage(@RequestBody String message) {
		System.out.println("MANDO MENSAJE CON POST DESDE BOT");
	}
	

}
