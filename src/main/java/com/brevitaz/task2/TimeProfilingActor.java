package com.brevitaz.task2;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorSelection;
import akka.actor.Props;

import java.util.LinkedHashMap;
import java.util.Map;

class TimeProfilingActor extends AbstractLoggingActor {

    final private long id;
    final private ActorSelection nextActor;

    private TimeProfilingActor(final long id){
        this.id = id;

        final long nextActorId = id + 1;
        nextActor = getContext().getSystem().actorSelection("user/" + nextActorId);
    }

    static Props props(int id)
    {
        return Props.create(TimeProfilingActor.class, () -> new TimeProfilingActor(id));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ProfileMessage.class, this::onMessage)
                .build();
    }

    private void onMessage(final ProfileMessage msg){

        final Map<Long, Long> actorReceiveTimes = msg.getActorReceivedTimes();
        actorReceiveTimes.put(id, System.currentTimeMillis());

        final ProfileMessage nextMsg = new ProfileMessage(actorReceiveTimes, msg.getTotalNodes());

        if(id == nextMsg.getTotalNodes())
        {
            log().info(nextMsg.toString());
            getContext().getSystem().terminate();
        }

        nextActor.tell(nextMsg, getSelf());
    }

    /**
     * Immutable message class
     */
    static class ProfileMessage
    {
        // tracks actorId with received time
        final private Map<Long, Long> actorReceivedTimes;
        final private int totalNodes;

        ProfileMessage(final Map<Long, Long> actorReceivedTimes, final int totalNodes)
        {
            this.actorReceivedTimes = actorReceivedTimes;
            this.totalNodes = totalNodes;
        }

        Map<Long, Long> getActorReceivedTimes()
        {
            // Return clone so immutability isn't compromised
            return new LinkedHashMap<>(actorReceivedTimes);
        }

        int getTotalNodes()
        {
            return totalNodes;
        }

        @Override public String toString()
        {
            final StringBuilder outBuilder = new StringBuilder();

            for (final Map.Entry<Long, Long> entry: actorReceivedTimes.entrySet())
            {
                outBuilder.append("\n").append("actor ").append(entry.getKey()).append(", message received at ").append(entry.getValue());
            }

            return outBuilder.toString();
        }
    }

}
