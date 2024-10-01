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

import java.io.IOException;

@Service
@CrossOrigin(origins = "*")
public class MailService {


    public void sendWelcomeEmail(String toEmail, String userName) throws IOException {
        Email from = new Email("bernardo.castellani@alunos.bpkedu.com.br");
        String subject = "Seja bem Vindo a Ambiente-se!";
        Email to = new Email(toEmail);
        Content content = new Content("text/plain", "Olá " + userName + ",\n\nSeja bem vindo ao nosso sistema!");
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
