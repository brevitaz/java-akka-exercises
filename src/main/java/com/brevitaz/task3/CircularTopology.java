package com.brevitaz.task3;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;

/**
 *
 * Topology that describes arrangement of nodes and who can talk with which actor.
 *
 * Requirement mentioned: "(actor n can communicate with actor n‐1 and
 * actor 0)"
 * We have assumed that it is implicit to understand that "actor 0 can communicate with actor n and
 * actor 1"
 *
 */
public class CircularTopology implements Topology
{

    private final long totalNumberOfNodes;
    private final ActorSystem system;

    CircularTopology(final long totalNumberOfNodes, final ActorSystem system)
    {
        this.totalNumberOfNodes = totalNumberOfNodes;
        this.system = system;
    }

    @Override public ActorSelection getLeftNeighbour(long id)
    {
        long leftNeighbourId = id - 1;

        if (id == 1)
        {
            leftNeighbourId = totalNumberOfNodes;
        }

        return getActorSelection(leftNeighbourId);
    }

    @Override public ActorSelection getRightNeighbour(long id)
    {
        long rightNeighbourId = id + 1;

        if (id == totalNumberOfNodes)
        {
            rightNeighbourId = 1;
        }

        return getActorSelection(rightNeighbourId);
    }

    private ActorSelection getActorSelection(long id)
    {
        return system.actorSelection("user/" + id);
    }
}
