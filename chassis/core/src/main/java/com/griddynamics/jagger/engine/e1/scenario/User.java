/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.engine.e1.scenario;

import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.util.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;

/**
 * User: dkotlyarov
 */
public class User {
    private static final Logger log = LoggerFactory.getLogger(User.class);

    private final int id;
    private final UserGroup group;
    private final long startTime;
    private final long finishTime;
    private final NodeId nodeId;
    private boolean deleted = false;

    public User(UserGroup group, long time, NodeId nodeId, LinkedHashMap<NodeId, WorkloadConfiguration> workloadConfigurations) {
        this.id = group.users.size();
        this.group = group;
        this.startTime = time;
        this.finishTime = startTime + Parser.parseTime(group.getConfig().life, group.getWorkload().getClock().getRandom());
        this.nodeId = nodeId;

        group.users.add(this);
        group.activeUserCount++;
        group.getWorkload().getClock().setUserStarted(true);

        WorkloadConfiguration workloadConfiguration = workloadConfigurations.get(nodeId);
        workloadConfigurations.put(nodeId, WorkloadConfiguration.with(workloadConfiguration.getThreads() + 1, workloadConfiguration.getDelay()));

        log.info(String.format("User %d from group %d is created at %dms of test on node %s", id, group.getId(), startTime - group.getWorkload().getClock().getStartTime(), nodeId));
    }

    public void delete(long time) {
        if (!deleted) {
            deleted = true;
            group.activeUserCount--;

            log.info(String.format("User %d from group %d is deleted at %dms of test on node %s", id, group.getId(), time - group.getWorkload().getClock().getStartTime(), nodeId));
        } else {
            throw new IllegalStateException();
        }
    }

    public void tick(long time, LinkedHashMap<NodeId, WorkloadConfiguration> workloadConfigurations) {
        if (!deleted && (time >= finishTime)) {
            delete(time);

            WorkloadConfiguration workloadConfiguration = workloadConfigurations.get(nodeId);
            workloadConfigurations.put(nodeId, WorkloadConfiguration.with(workloadConfiguration.getThreads() - 1, workloadConfiguration.getDelay()));
        }
    }
}
