package com.stackroute.transactionsmanagement.model;

public enum IsolationLevel {
    DEFAULT,
    READ_UNCOMMITTED,
    READ_COMMITTED,
    REPEATABLE_READ,
    SERIALIZABLE
}

