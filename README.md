# Java classes that implements reload on SIGHUP

This minimal library is essentially a way to encapsulate the somewhat
non-portable way in which a handler for the SIGHUP Unix signal can be
installed. The following code

```
MainLoop ml = MainLoop.newInstance();
ml.installReloadHandler(onReload);
ml.run();
```

will install the onReload handler and then wait around until the process
is told to exit by sending SIGTERM.

This library uses non-standard functionality, i.e. sun.misc.Signal, that
might not be available in Java everywhere. Additionally the unit test
assumes that there is a kill command that can be executed on the host system
to signal the current process.

## License

This software is released under the Apache License 2.0. More information in
the file LICENSE distributed with this project.
