package com.navercorp.pinpoint.profiler.sender.grpc;

import com.navercorp.pinpoint.grpc.stream.ClientCallStateStreamObserver;
import com.navercorp.pinpoint.profiler.sender.grpc.stream.StreamJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.concurrent.Future;

public class DefaultStreamEventListener<ReqT> implements StreamEventListener<ReqT> {

    private final Logger logger = LogManager.getLogger(this.getClass());
    private final Reconnector reconnector;
    private final StreamJob<ReqT> streamJob;
    private volatile Future<?> handle;

    public DefaultStreamEventListener(Reconnector reconnector, StreamJob<ReqT> streamJob) {
        this.reconnector = Objects.requireNonNull(reconnector, "reconnector");
        this.streamJob = Objects.requireNonNull(streamJob, "streamTask");
    }

    @Override
    public void start(final ClientCallStateStreamObserver<ReqT> requestStream) {
        this.handle = streamJob.start(requestStream);
        reconnector.reset();
    }


    @Override
    public void onError(Throwable t) {
        cancel();
    }

    @Override
    public void onCompleted() {
        cancel();
    }

    private void cancel() {
        final Future<?> handle = this.handle;
        if (handle != null) {
            handle.cancel(true);
        }
        reconnector.reconnect();
    }

    @Override
    public String toString() {
        return "DefaultStreamEventListener{" +
                streamJob +
                '}';
    }
}
