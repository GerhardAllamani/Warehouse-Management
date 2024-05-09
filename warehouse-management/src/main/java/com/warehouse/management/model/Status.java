package com.warehouse.management.model;

public enum Status {
        CREATED,
        AWAITING_APPROVAL,
        APPROVED,
        DECLINED,
        UNDER_DELIVERY,
        FULFILLED,
        CANCELED;

        public static Status fromString(String statusString) {
                for (Status status : Status.values()) {
                        if (status.name().equalsIgnoreCase(statusString)) {
                                return status;
                        }
                }
                throw new IllegalArgumentException("No constant with name " + statusString + " found in Status enum");
        }
}


