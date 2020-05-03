package com.bsejawal.s3sync.model;

public class Location{
    private String source;
    private String destination;
    private String env;

    public Location(String source, String destination, String env) {
        this.source = source;
        this.destination = destination;
        this.env = env;
    }

    public static Location createLocation(String line){
        String attributes [] = line.split(",");
        String source = attributes[0];
        String destination = attributes[1];
        String env = attributes[2];
        return new Location(source, destination, env);

    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    @Override
    public String toString() {
        return "{" +
                "source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", env='" + env + '\'' +
                "}\n";
    }
}
