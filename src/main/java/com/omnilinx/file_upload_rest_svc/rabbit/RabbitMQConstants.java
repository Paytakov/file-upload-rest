package com.omnilinx.file_upload_rest_svc.rabbit;

public class RabbitMQConstants {

    public static final String EXCHANGE_NAME = "file-processing-exchange";
    public static final String QUEUE_NAME = "file-processing-queue";
    public static final String ROUTING_KEY = "file-processing-key";
    public static final int MAX_ATTEMPTS_COUNT = 5;
    public static final long BACKOFF_INITIAL_INTERVAL = 1000;
    public static final double BACKOFF_MULTIPLIER = 2.0;
    public static final long BACKOFF_MAX_INTERVAL = 10000;
}
