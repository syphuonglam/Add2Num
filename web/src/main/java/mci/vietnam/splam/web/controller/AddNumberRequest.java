package mci.vietnam.splam.web.controller;

import com.fasterxml.jackson.databind.JsonNode;

public class AddNumberRequest {
    private JsonNode a;
    private JsonNode b;
    private boolean aPresent;
    private boolean bPresent;

    public JsonNode getA() {
        return a;
    }

    public void setA(JsonNode a) {
        this.a = a;
        this.aPresent = true;
    }

    public JsonNode getB() {
        return b;
    }

    public void setB(JsonNode b) {
        this.b = b;
        this.bPresent = true;
    }

    public boolean isAPresent() {
        return aPresent;
    }

    public boolean isBPresent() {
        return bPresent;
    }
}