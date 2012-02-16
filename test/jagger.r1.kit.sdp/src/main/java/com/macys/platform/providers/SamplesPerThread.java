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

package com.macys.platform.providers;

import com.griddynamics.jagger.engine.e1.scenario.TerminateStrategyConfiguration;
import com.griddynamics.jagger.engine.e1.scenario.TerminationStrategy;
import com.griddynamics.jagger.engine.e1.scenario.WorkloadExecutionStatus;
import org.springframework.beans.factory.annotation.Required;

public class SamplesPerThread implements TerminateStrategyConfiguration {
    private int samples;

    @Override
    public TerminationStrategy getTerminateStrategy() {
        return new SamplesPerThreadStrategy(samples);
    }

    @Required
    public void setSamples(int samples) {
        this.samples = samples;
    }

    @Override
    public String toString() {
        return describe(samples);
    }

    private static String describe(int samples) {
        return "[" + samples + " samples per thread]";
    }

    private static class SamplesPerThreadStrategy implements TerminationStrategy {
        private final int samples;

        public SamplesPerThreadStrategy(int samples) {
            this.samples = samples;
        }

        @Override
        public boolean isTerminationRequired(WorkloadExecutionStatus status) {
            int totalThreads = status.getTotalThreads();
            return totalThreads != 0 && status.getTotalSamples() >= totalThreads * samples;
        }

        @Override
        public String toString() {
            return describe(samples);
        }
    }
}