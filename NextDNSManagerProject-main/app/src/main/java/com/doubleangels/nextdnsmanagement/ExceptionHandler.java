package com.doubleangels.nextdnsmanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import io.sentry.ITransaction;
import io.sentry.Sentry;

public class ExceptionHandler {
    public void captureExceptionAndFeedback(Exception exception, Activity activity) {
        ITransaction capture_exception_and_feedback_transaction = Sentry.startTransaction("ExceptionHandler_captureExceptionAndFeedback()", "ExceptionHandler");
        try {
            // Generate our snackbar used to ask the user if they want to make feedback.
            Snackbar snackbar = Snackbar.make(activity.getWindow().getDecorView().getRootView(), "Error occurred! Share feedback?", Snackbar.LENGTH_LONG);

            // If user wants to provide feedback, send them to the feedback activity.
            snackbar.setAction("SHARE", view -> {
                int LAUNCH_SECOND_ACTIVITY = 1;
                Intent feedbackIntent = new Intent(activity, feedback.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("e", exception);
                feedbackIntent.putExtras(bundle);
                activity.startActivityForResult(feedbackIntent, LAUNCH_SECOND_ACTIVITY);
            });

            // If snackbar is dismissed on its own, proceed with normal error report.
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        Sentry.captureException(exception);
                    }
                }
            });
            snackbar.show();
        } catch (Exception e) {
            captureException(e);
        } finally {
            capture_exception_and_feedback_transaction.finish();
        }
    }

    public void captureException(Exception exception) {
        ITransaction capture_exception_transaction = Sentry.startTransaction("ExceptionHandler_captureException()", "ExceptionHandler");
        Sentry.captureException(exception);
        capture_exception_transaction.finish();
    }
}
