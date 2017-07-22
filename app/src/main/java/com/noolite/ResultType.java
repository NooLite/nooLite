package com.noolite;

/**
 * Created by urix on 7/22/2017.
 */

public enum ResultType {

    SUCCESS_RESULT(0, "Успешно завершено"),

    INTERNAL_ERROR(1, "Внутренняя ошибка"),

    CONNECTION_ERROR(10, "Ошибка подключения, проверьте соединение с Wi-Fi или IP адрес"),
    URL_ERROR(11, "Ошибка в адресе"),
    READ_ERROR(12, "Ошибка чтения данных"),
    PARSE_ERROR(13, "Ошибка обработки полученных данных"),
    UNSUPPORTED_ENCODING(14, "Ошибка установки параметров аутентификации соединения"),

    DB_ERROR(15, "Ошибка при работе с базой данных");



    int code;
    String description;

    ResultType(int code, String description){
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
