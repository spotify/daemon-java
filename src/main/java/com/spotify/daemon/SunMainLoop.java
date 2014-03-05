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

import sun.misc.Signal;
import sun.misc.SignalHandler;


/**
 * Sun JVM specific main loop.
 *
 * Based on
 *
 *  http://www.ibm.com/developerworks/java/library/i-signalhandling/
 *
 * Registers POSIX signal handlers using the internal Sun API.
**/
@SuppressWarnings({"restriction", "UnusedDeclaration"})
public class SunMainLoop extends MainLoop {
	@Override
	protected void start() {
		Terminator terminator = new Terminator();
		Reloader reloader = new Reloader();

		install("INT", terminator);
		// XXX(tommie): SIGQUIT is already registered by the JVM, so we can't have it. :(
		//install("QUIT", terminator);
		install("TERM", terminator);

		install("HUP", reloader);
	}

	/**
	 * Installs a new signal handler for the given signal.
	 *
	 * @param name the name of the signal, without the "SIG" prefix.
	 * @param handler the new handler.
	 * @return the old handler.
	**/
	private SignalHandler install(String name, SignalHandler handler) {
		return Signal.handle(new Signal(name), handler);
	}

	class Terminator implements SignalHandler {
		@Override
		public void handle(Signal sig) {
			stop();
		}
	}

	class Reloader implements SignalHandler {
		@Override
		public void handle(Signal sig) {
			reload();
		}
	}
}

