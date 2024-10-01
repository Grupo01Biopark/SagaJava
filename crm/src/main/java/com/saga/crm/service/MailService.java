package com.saga.crm.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

@Service
@CrossOrigin(origins = "*")
public class MailService {


    private final TemplateEngine templateEngine;

    public MailService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public void sendWelcomeEmail(String toEmail, String userName) throws IOException {
        Email from = new Email("bernardo.castellani@alunos.bpkedu.com.br");
        String subject = "Seja bem Vindo a Ambiente-se!";
        Email to = new Email(toEmail);


        Context context = new Context();
        context.setVariable("userName", userName);
        String htmlContent = templateEngine.process("emails/welcome", context);

        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid("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");
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            throw ex;
        }
    }

    public void sendForgotPasswordEmail(String toEmail, String userName, String temporaryPassword) throws IOException {
        Email from = new Email("bernardo.castellani@alunos.bpkedu.com.br");
        String subject = "Recuperação de Senha - Ambiente-se";
        Email to = new Email(toEmail);

        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("temporaryPassword", temporaryPassword);
        String htmlContent = templateEngine.process("emails/forgot-password", context);

        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid("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");
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            throw ex;
        }
    }
}