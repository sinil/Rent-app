package com.riwal.rentalapp.model

enum class Notification {
    MACHINE_ADDED,
    MACHINE_REMOVED,
    MACHINE_CHANGED,
    ORDER_SUBMITTED,
    ACCOUNT_REQUEST_SUCCEEDED,
    ACCOUNT_REQUEST_FAILED,
    OFF_RENT_REQUEST_SUCCEEDED,
    CANCEL_RENT_REQUEST_SUCCEEDED,
    CANCEL_RENT_REQUEST_FAILED,
    OFF_RENT_REQUEST_FAILED,
    CHANGE_REQUEST_SUCCEEDED,
    CHANGE_REQUEST_FAILED,
    SEND_FEEDBACK_SUCCEEDED,
    SEND_FEEDBACK_FAILED,
    REACHABILITY_CHANGED,
    SEND_BREAKDOWN_REPORT_SUCCEEDED,
    SEND_BREAKDOWN_REPORT_FAILED,
    SEND_TRANSFER_MACHINES_SUCCEEDED,
    SEND_TRANSFER_MACHINES_FAILED,
    SEND_REJECT_TRANSFER_REQUEST_SUCCEEDED,
    SEND_REJECT_TRANSFER_REQUEST_FAILED,
    SEND_ACCEPT_TRANSFER_REQUEST_SUCCEEDED,
    SEND_ACCEPT_TRANSFER_REQUEST_FAILED,
    TRAINING_REQUEST_SUCCEED,
    INVOICE_NOT_AVAILABLE,
    DOWNLOAD_INVOICE_FAILED,
    QUOTATION_NOT_AVAILABLE,
    DOWNLOAD_QUOTATION_FAILED
}