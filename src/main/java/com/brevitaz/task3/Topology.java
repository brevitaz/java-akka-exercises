/**
 * =========================================================================
 * Copyright 2018, Allego Corporation, MA USA
 *
 * This file and its contents are proprietary and confidential to and the
 * sole intellectual property of Allego Corporation.  Any use, reproduction,
 * redistribution or modification of this file is prohibited except as
 * explicitly defined by written license agreement with Allego Corporation.
 * =========================================================================
 */

package com.brevitaz.task3;

import akka.actor.ActorSelection;

public interface Topology
{
    ActorSelection getLeftNeighbour(long id);
    ActorSelection getRightNeighbour(long id);
}
