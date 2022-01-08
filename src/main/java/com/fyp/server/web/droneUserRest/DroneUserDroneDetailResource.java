package com.fyp.server.web.droneUserRest;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static java.util.concurrent.TimeUnit.SECONDS;

@EnableScheduling
@Controller
public class DroneUserDroneDetailResource {

	
//	private final Logger log = LoggerFactory.getLogger(DroneUserDroneDetailResource.class);
//	
//	
//	@Autowired
//    SimpMessagingTemplate template;
//	
//	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
//	
//	@MessageMapping("/hello/{id}")
//	@SendTo("/topic/hello/{id}")
//    public void greeting(@DestinationVariable Long id) throws Exception {
//
//		log.debug("hello this is message: {}", id);
//
//       return "Hello, " + id  + "!";
//    }
//	
//	@Scheduled(fixedRate=5000)
//	public void schedule() {
//		for(Long id : list) {
//			template.convertAndSend("/topic/hello/"+id.toString(), "hello hohoho"+id);
//		}
//	}

}
