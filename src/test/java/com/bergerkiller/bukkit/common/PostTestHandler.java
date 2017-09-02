package com.bergerkiller.bukkit.common;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

public class PostTestHandler extends RunListener {

    @Override
    public void testRunStarted(Description description) throws Exception {
        // Called before any tests have been run.
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        // Called when all tests have finished
        com.bergerkiller.mountiplex.conversion.Conversion.debugExportConverterTree("misc/conversion.txt");
    }
}
