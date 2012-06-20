/*
*    Copyright [2011] [wisemapping]
*
*   Licensed under WiseMapping Public License, Version 1.0 (the "License").
*   It is basically the Apache License, Version 2.0 (the "License") plus the
*   "powered by wisemapping" text requirement on every single page;
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the license at
*
*       http://www.wisemapping.org/license
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/

package com.wisemapping.mail;

import com.wisemapping.model.Collaboration;
import com.wisemapping.model.MindMap;
import com.wisemapping.model.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

final public class NotificationService {


    @Autowired
    private Mailer mailer;
    private String baseUrl;

    public NotificationService() {

    }

    public void newCollaboration(@NotNull Collaboration collaboration, @NotNull MindMap mindmap, @NotNull User user, @Nullable String message) {

        try {
            // Sent collaboration email ...
            final String formMail = mailer.getServerSenderEmail();

            // Is the user already registered user ?.
            final String collabEmail = collaboration.getCollaborator().getEmail();

            // Build the subject ...
            final String subject = "[WiseMapping] " + user.getFullName() + " has shared a mindmap with you";

            // Fill template properties ...
            final Map<String, Object> model = new HashMap<String, Object>();
            model.put("mindmap", mindmap);
            model.put("message", "message");
            model.put("ownerName", user.getFirstname());
            model.put("mapEditUrl", baseUrl + "/c/maps/" + mindmap.getId() + "/edit");
            model.put("baseUrl", formMail);
            model.put("senderMail", user.getEmail());
            model.put("message", message);
            model.put("supportEmail", mailer.getSupportEmail());

            mailer.sendEmail(formMail, collabEmail, subject, model, "newCollaboration.vm");
        } catch (Exception e) {
            handleException(e);
        }

    }

    public void resetPassword(@NotNull User user, @NotNull String temporalPassword) {
        final String mailSubject = "[WiseMapping] Your new password";
        final String messageTitle = "Your new password has been generated";
        final String messageBody =
                "<p>Someone, most likely you, requested a new password for your WiseMapping account. </p>\n" +
                        "<p><strong>Here is your new password: : " + temporalPassword + "</strong></p>\n" +
                        "<p>You can login clicking <a href=\"" + this.baseUrl + "/c/login\">here</a>. We strongly encourage you to change the password as soon as possible.</p>";

        sendTemplateMail(user, mailSubject, messageTitle, messageBody);
    }

    public void passwordChanged(@NotNull User user) {
        final String mailSubject = "[WiseMapping] Your password has been changed";
        final String messageTitle = "Your password has been changed successfully";
        final String messageBody =
                "<p>This is only an notification that your password has been changed. No further action is required.</p>";

        sendTemplateMail(user, mailSubject, messageTitle, messageBody);
    }

    public void newAccountCreated(@NotNull User user) {
        final String mailSubject = "Welcome to WiseMapping !";
        final String messageTitle = "Your account has been created successfully";
        final String messageBody =
                "<p> Thank you for your interest in WiseMapping.  If have any feedback or idea, send us an email to <a href=\"mailto:feedback@wisemapping.com\">feedback@wisemapping.com</a> .We'd love to hear from  you.</p>";
        sendTemplateMail(user, mailSubject, messageTitle, messageBody);
    }

    private void sendTemplateMail(@NotNull User user, @NotNull String mailSubject, @NotNull String messageTitle, @NotNull String messageBody) {

        try {
            final Map<String, Object> model = new HashMap<String, Object>();
            model.put("firstName", user.getFirstname());
            model.put("messageTitle", messageTitle);
            model.put("messageBody", messageBody);
            model.put("baseUrl", this.baseUrl);
            model.put("supportEmail", mailer.getSupportEmail());

            mailer.sendEmail(mailer.getServerSenderEmail(), user.getEmail(), mailSubject, model, "baseLayout.vm");
        } catch (Exception e) {
            handleException(e);
        }
    }

    private void handleException(Exception e) {
        e.printStackTrace();
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setMailer(Mailer mailer) {
        this.mailer = mailer;
    }


    public void activateAccount(@NotNull User user) {
        final Map<String, User> model = new HashMap<String, User>();
        model.put("user", user);
        mailer.sendEmail(mailer.getServerSenderEmail(), user.getEmail(), "[WiseMapping] Active account", model, "activationAccountMail.vm");
    }

    public void sendRegistrationEmail(@NotNull User user) {
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("user", user);
        final String activationUrl = "http://wisemapping.com/c/activation?code=" + user.getActivationCode();
        model.put("emailcheck", activationUrl);
        mailer.sendEmail(mailer.getServerSenderEmail(), user.getEmail(), "Welcome to Wisemapping!", model,
                "confirmationMail.vm");
    }
}


