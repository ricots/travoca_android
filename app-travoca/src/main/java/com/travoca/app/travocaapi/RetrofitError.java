package com.travoca.app.travocaapi;

import retrofit.Response;

/**
 * @author ortal
 * @date 2015-11-04
 */
public class RetrofitError extends Throwable {

    public RetrofitError(Response response) {
        super(convertToMessage(response));
    }

    private static String convertToMessage(Response response) {
        StringBuilder message = new StringBuilder();

        message.append(String.valueOf(response.code()))
                .append(" ")
                .append(response.message())
        ;

        return message.toString();
    }

}
