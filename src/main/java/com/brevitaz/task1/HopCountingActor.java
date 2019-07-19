package com.brevitaz.task1;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorSelection;
import akka.actor.Props;

class HopCountingActor extends AbstractLoggingActor
{
    final private long id;
    final private ActorSelection nextActor;

    private HopCountingActor(long id)
    {
        this.id = id;

        final long nextActorId = id+1;
        nextActor = getContext().getSystem().actorSelection("user/" + nextActorId);
    }

    static Props props(int id)
    {
        return Props.create(HopCountingActor.class, () -> new HopCountingActor(id));
    }

    @Override public Receive createReceive()
    {
        return receiveBuilder().match(CountHopsMessage.class, this::onMessage).build();
    }

    private void onMessage(final CountHopsMessage msg)
    {
        // end of the actors chain. Print numberOfHopsTravelled. For 6 nodes, it should print 5.
        if(id == msg.getTotalNumberOfNodes()){
            log().info("Number of hops travelled is: {}", msg.numberOfHopsTravelled);
            getContext().getSystem().terminate();
        }

        // Create new message
        nextActor.tell(msg.nextHopMessage(), getSelf());
    }

    /**
     * Immutable message class
     */
    static class CountHopsMessage
    {

        final private int numberOfHopsTravelled;
        final private int totalNumberOfNodes;

        CountHopsMessage(int numberOfHopsTravelled, int totalNumberOfNodes)
        {
            this.numberOfHopsTravelled = numberOfHopsTravelled;
            this.totalNumberOfNodes = totalNumberOfNodes;
        }

        int getNumberOfHopsTravelled()
        {
            return numberOfHopsTravelled;
        }

        int getTotalNumberOfNodes()
        {
            return totalNumberOfNodes;
        }

        CountHopsMessage nextHopMessage()
        {
            final int numberOfHopsTravelled = this.numberOfHopsTravelled + 1;
            return new CountHopsMessage(numberOfHopsTravelled, totalNumberOfNodes);
        }
    }

}
