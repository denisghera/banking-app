package ro.uvt.dp.services;

import ro.uvt.dp.support.Request;

public interface SupportHandler {
    boolean handleRequest(Request request);
    void setNextHandler(SupportHandler nextHandler);
}
