package com.amayorov.hostel.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.routing.AdjustPoolSize;
import akka.routing.GetRoutees;
import akka.routing.RoundRobinPool;
import com.amayorov.hostel.HostelBusinessLogic;
import com.amayorov.hostel.akka.extension.SpringExtension;
import com.amayorov.hostel.domain.dto.ValidateQuartersDTO;
import com.amayorov.hostel.exception.CustomException;
import com.amayorov.hostel.repository.security.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Component
@Slf4j
public class Scheduler {
	private static final long DELAY = 120_000L;
	private static final long INITIAL_DELAY = 5_000L;
	private static final int DEFAULT_ACTOR_NUMBER = 5;

	private final ApplicationContext applicationContext;
	private final UserRepo userRepo;
	private final HostelBusinessLogic hostelBusinessLogic;

	ActorRef receiveActor;
	ActorRef masterActor;

	@Scheduled(initialDelay = INITIAL_DELAY, fixedDelay = DELAY)
	public void performRegularAction() throws InterruptedException {
		log.info("---------------------------------------------------");
		log.info("Actors working...");

		receiveActor.tell(GetRoutees.getInstance(), masterActor);
		receiveActor.tell(new UserList(userRepo.findAll()), ActorRef.noSender());

		try {
			Integer freeAmount = hostelBusinessLogic.checkQuarters(new ValidateQuartersDTO
					(new Date(), getSevenDaysAfterDate(), Set.of("Business", "Standard", "Apartment", "Deluxe",
							"Duplex", "Superior"), 1)).size();
			receiveActor.tell(freeAmount, ActorRef.noSender());
		} catch (CustomException e) {
			log.info("No free quarters or no quarters added at all, look for the next update in 120 sec.");
		}
		Thread.sleep(50);
		log.info("---------------------------------------------------");
	}

	@NonNull
	private Date getSevenDaysAfterDate() {
		Instant now = Instant.now(); //current date
		Instant after = now.plus(Duration.ofDays(7));
		return Date.from(after);
	}

	@PostConstruct
	public void postConstructMethod() {
		ActorSystem system = applicationContext.getBean(ActorSystem.class);
		SpringExtension springExtension = applicationContext.getBean(SpringExtension.class);
		// using Spring Extension to create props for named actors bean
		receiveActor = system.actorOf(
				springExtension.props("receiverActor")
						.withRouter(new RoundRobinPool(DEFAULT_ACTOR_NUMBER)), "receiverActor");
		masterActor = system.actorOf(springExtension.props("masterActor"), "masterActor");
	}

	public void changeActorsCount(int change) {
		receiveActor.tell(new AdjustPoolSize(change), ActorRef.noSender());
	}
}
