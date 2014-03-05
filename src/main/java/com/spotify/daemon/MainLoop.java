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

/**
 * A main loop class that just sits waiting for someone to terminate it.
**/
public abstract class MainLoop {
	private boolean running = true;
	private OnReload onReload = null;

	/**
	 * We need JVM-specific versions of the signal handling,
	 * so force use of #newInstance().
	**/
	protected MainLoop() {
	}

	public void run() {
		start();

		synchronized(this) {
			while (this.running) {
				try {
					this.wait();
				} catch (InterruptedException ex) {
                    // pass
				}
			}
		}
	}

	/**
	 * Internal function to provide a hook for implementations.
	**/
	protected abstract void start();

	/**
	 * Ask the main loop to stop.
	 *
	 * This can only be done once, so if this method is called before
	 * #run() is called, the loop will never even start.
	**/
	public void stop() {
		synchronized (this) {
			this.running = false;
			this.notify();
		}
	}

	/**
	 * Implement reload for handling reloading.
	**/
	public interface OnReload {
		void reload();
	}

	/**
	 * Install an implementation of OnReload to act as a reload handler.
	 *
	 * @param onReload: an implementation of OnReload.
        **/
	public void installReloadHandler(OnReload onReload) {
		this.onReload = onReload;
	}

	/**
	 * Ask the loop to reload. This is also used as a callback
	 * from signal handler, if available. (See SunMainloop for
	 * example).
	**/
	public void reload() {
		if (this.onReload != null) {
			this.onReload.reload();
		}
	}

	/**
	 * Create an environment specific instance of MainLoop.
	**/
	public static MainLoop newInstance() {
		String impl = "com.spotify.daemon.SunMainLoop";

		try {
			Class<?> cls = Class.forName(impl);

			return (MainLoop) cls.newInstance();
		} catch (ClassNotFoundException ex) {
			throw new TypeNotPresentException(impl, ex);
		} catch (IllegalAccessException ex) {
			throw new TypeNotPresentException(impl, ex);
		} catch (InstantiationException ex) {
			throw new TypeNotPresentException(impl, ex);
		}
	}
}
