package com.brevitaz.task4;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

class SchedulerActor extends AbstractLoggingActor
{

    final private PriorityQueue<AgentReceivedTimeEntry> agentReceivedTimeQueue;

    /**
     * Adds one message per agent into sorted queue.
     */

    private SchedulerActor(List<ActorRef> allAgents)
    {
        agentReceivedTimeQueue = new PriorityQueue<>((AgentReceivedTimeEntry e1, AgentReceivedTimeEntry e2) -> new Long(e1.getTime() - e2.getTime()).intValue());
        allAgents.forEach(ref -> agentReceivedTimeQueue.add(new AgentReceivedTimeEntry(new Random().nextInt(100), ref)));
    }

    @Override public Receive createReceive()
    {
        return receiveBuilder().matchEquals("start", m -> onInitMessage()).match(TriggerMessage.class, this::receiveTriggerMessage).match(AcknowledgementMessage.class, m -> onAckMessage())
                               .build();
    }

    private void onInitMessage()
    {
        pollFromQueueAndSend();
    }

    private void receiveTriggerMessage(TriggerMessage triggerMessage)
    {
        agentReceivedTimeQueue.add(new AgentReceivedTimeEntry(triggerMessage.getTime(), getSender()));
    }

    private void onAckMessage()
    {
        pollFromQueueAndSend();
    }

    private void pollFromQueueAndSend()
    {
        final AgentReceivedTimeEntry agentReceivedTimeEntry = agentReceivedTimeQueue.poll();
        if (agentReceivedTimeEntry != null)
        {
            agentReceivedTimeEntry.getAgentRef().tell(new AgentActor.TriggerMessage(agentReceivedTimeEntry.getTime()), getSelf());
        }
        else
        {
            //System will be terminated when there is no message in queue.
            getContext().getSystem().terminate();
        }
    }

    static Props props(List<ActorRef> allActors)
    {
        return Props.create(SchedulerActor.class, () -> new SchedulerActor(allActors));
    }

    /**
     * Immutable TriggerMessage Message class
     */

    static class TriggerMessage
    {

        final private long time;

        TriggerMessage(final long time)
        {
            this.time = time;
        }

        long getTime()
        {
            return time;
        }
    }

    /**
     * Acknowledgement Message class
     */
    static class AcknowledgementMessage
    {
    }

    private class AgentReceivedTimeEntry
    {
        final private long time;
        final private ActorRef agentReference;

        AgentReceivedTimeEntry(final long time, final ActorRef agentReference)
        {
            this.time = time;
            this.agentReference = agentReference;
        }

        long getTime()
        {
            return time;
        }

        ActorRef getAgentRef()
        {
            return agentReference;
        }
    }

}
