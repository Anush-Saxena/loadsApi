package com.TruckBooking.ContractRateUpload.EmailSender;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

// This class is helpful in sending Emails just needs email address, body and subject of the address
@Slf4j
@Component
@Data
public class SendEmail {

    @Autowired
    private JavaMailSender javaMailSender;
    private String address;
    private String body;
    private String subject;

    public boolean isSend(){
        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage, StandardCharsets.UTF_8.name());
        try{
            helper.setTo(address);
            helper.setSubject(subject);
            helper.setText(body,false);
            javaMailSender.send(mailMessage);
            log.info("Mail Sent to: " + address);
            return true;
        }
        catch (Exception e){
            log.error("Mail not sent to: " + address + "Error: " +e);
            return false;
        }
    }

}
