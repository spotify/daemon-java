/*
 * Copyright (c) 2011-2014 Spotify AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.spotify.daemon;

import org.junit.Test;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Tests SunMainLoop
 */
public class SunMainLoopTest
{
    @Test
    public void testReload() throws Exception {
        // yup this is ugly, I just want something mutable.
        final CountDownLatch latch = new CountDownLatch(1);

        MainLoop ml = MainLoop.newInstance();

        ml.installReloadHandler(new MainLoop.OnReload() {
            @Override
            public void reload() {
              latch.countDown();
            }
        });

        ml.start();
        signalMyself(1);
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        latch.await(5000, TimeUnit.MILLISECONDS);
        assertEquals(0, latch.getCount());

        ml.stop();
    }

    // copied from
    // http://stackoverflow.com/questions/35842/how-can-a-java-program-get-its-own-process-id
    private static int getPid() {
        String s =  ManagementFactory.getRuntimeMXBean().getName();
        return Integer.parseInt(s.substring(0, s.indexOf('@')));
    }

    private static void signalMyself(int signal) {
        try {
            Runtime.getRuntime().exec(String.format("kill -%d %d", signal, getPid()));
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}
