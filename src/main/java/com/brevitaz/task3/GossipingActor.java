package com.brevitaz.task3;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.util.Random;

class GossipingActor extends AbstractLoggingActor
{
    final private long id;
    final private Receive waitingForSecondNeighbour;
    final private Topology topology;

    private GossipingActor(final long id, final Topology topology)
    {
        this.id = id;

        waitingForSecondNeighbour = receiveBuilder().match(String.class, msg -> {
            log().info("my id: {}, message: {}", id, msg);
            getContext().getSystem().terminate();
        }).build();

        this.topology = topology;
    }

    static Props props(final int id, final Topology topology)
    {
        return Props.create(GossipingActor.class, () -> new GossipingActor(id, topology));
    }

    @Override public Receive createReceive()
    {
        return receiveBuilder().match(String.class, this::onMessage).build();
    }

    private void onMessage(final String msg)
    {
        final ActorSystem system = getContext().getSystem();
        final Random ran = new Random();
        final ActorSelection leftNeighbour = topology.getLeftNeighbour(id);
        final ActorSelection rightNeighbour = topology.getRightNeighbour(id);
        final ActorSelection sender = system.actorSelection(getSender().path());

        if (getSender().path().uid() != 0)
        {
            try { Thread.sleep(ran.nextInt(100)); } catch (InterruptedException ignored) { }
        }

        if (!sender.equals(leftNeighbour))
            leftNeighbour.tell(msg, getSelf());

        if (!sender.equals(rightNeighbour))
            rightNeighbour.tell(msg, getSelf());

        getContext().become(waitingForSecondNeighbour);
    }

}
