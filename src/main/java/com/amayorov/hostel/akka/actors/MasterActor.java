package com.amayorov.hostel.akka.actors;

import akka.actor.AbstractActor;
import akka.routing.Routees;
import com.amayorov.hostel.akka.Scheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MasterActor extends AbstractActor {

	private final Scheduler scheduler;

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(Routees.class, this::onRoutees)
				.build();
	}

	private void onRoutees(Routees routees) throws InterruptedException { //just for learning purposes trying to change the amount of actors, no real need here
		int actorsCount = routees.getRoutees().size();
		if (actorsCount > 5) {
			scheduler.changeActorsCount(-3); // when receiver's actors number exceeds 5, it decreases by 3 each scheduled iteration
		} else {
            scheduler.changeActorsCount(1); // when receiver's actors number is lower than 5, it increases by 1 each scheduled iteration
		}
		Thread.sleep(250); //just to always have this message in the bottom
		log.info("Count of receiver`s active actors is {}", actorsCount);
	}

}
