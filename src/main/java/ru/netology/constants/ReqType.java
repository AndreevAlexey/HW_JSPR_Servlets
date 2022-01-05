package ru.netology.constants;

public enum ReqType {
    GET,
    POST,
    DELETE,
    PUT;

    // получить ссылку на путь из списка возможных
    public static ReqType getByName(String name) {
        try {
            return ReqType.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
