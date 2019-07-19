package com.brevitaz.task4;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;

import java.util.Random;

class AgentActor extends AbstractLoggingActor
{

    final private long id;
    private int receivedTriggers;

    private AgentActor(long id){
        this.id = id;
    }

    @Override
    public Receive createReceive()
    {

        return receiveBuilder()
                .match(TriggerMessage.class, this::onTriggerMessage)
                .build();
    }

    private void onTriggerMessage(TriggerMessage triggerMessage)
    {
        receivedTriggers++;
        log().info("id: {}, message: {}", id, triggerMessage.getTime());

        if(receivedTriggers < 10)
        {
            getSender().tell(triggerMessage.triggerMessageForScheduler(), getSelf());
        }
        getSender().tell(new SchedulerActor.AcknowledgementMessage(), getSelf());
    }

    static Props props(final int id) {
        return Props.create(AgentActor.class, () -> new AgentActor(id));
    }

    /**
     * Immutable message class
     */
    static class TriggerMessage
    {
        final private long time;

        TriggerMessage(long time)
        {
            this.time = time;
        }

        long getTime()
        {
            return time;
        }

        SchedulerActor.TriggerMessage triggerMessageForScheduler()
        {
            return new SchedulerActor.TriggerMessage(time + new Random().nextInt(100));
        }
    }
}
